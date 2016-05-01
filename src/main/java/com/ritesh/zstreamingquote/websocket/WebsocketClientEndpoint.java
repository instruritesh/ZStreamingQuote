package com.ritesh.zstreamingquote.websocket;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Timer;
import java.util.TimerTask;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import com.ritesh.zstreamingquote.config.ZStreamingConfig;

@ClientEndpoint
public class WebsocketClientEndpoint {
	Session userSession = null;
	private MessageHandler messageHandler;
	private IwsSessionNotifier sessionNotifier = null;
	private static Timer hbTimer = null;
	private static final int hbTimeDelay = ZStreamingConfig.getStreamingQuoteHeartBitCheckTime();
	private boolean terminate = false;

	/**
	 * constructor
	 * 
	 * @param endpointURI
	 */
	public WebsocketClientEndpoint(URI endpointURI, IwsSessionNotifier sessionNotifier) {
		try {
			this.sessionNotifier = sessionNotifier;
			System.out.println("WebsocketClientEndpoint.WebsocketClientEndpoint(): creating WebSocketContainer...");
			WebSocketContainer container = ContainerProvider.getWebSocketContainer();
			container.connectToServer(this, endpointURI);
		} catch (Exception e) {
			System.out.println(
					"WebsocketClientEndpoint.WebsocketClientEndpoint(): ERROR: Exception on container connectToServer, reason: "
							+ e.getMessage());
			//throw new RuntimeException(e);
		}
	}

	/**
	 * Callback hook for Connection open events.
	 * 
	 * @param userSession
	 *            the userSession which is opened.
	 */
	@OnOpen
	public void onOpen(Session userSession) {
		System.out.println("WebsocketClientEndpoint.onOpen(): Opening WebSocket...");
		this.userSession = userSession;

		// Notify session opened
		sessionNotifier.notifyWsSessionOpened();
	}

	/**
	 * Callback hook for Connection close events.
	 * 
	 * @param userSession
	 *            the userSession which is getting closed.
	 * @param reason
	 *            the reason for connection close
	 */
	@OnClose
	public void onClose(Session userSession, CloseReason reason) {
		System.out.println(
				"WebsocketClientEndpoint.onClose(): Closing Websocket.... Reason[" + reason.getReasonPhrase() + "]");
		try {
			this.userSession.close();
		} catch (IOException e) {
			System.out.println("WebsocketClientEndpoint.onClose(): ERROR: IOException on userSession close!!!");
			e.printStackTrace();
		}
		this.userSession = null;

		// stop timer if running
		if (hbTimer != null) {
			hbTimer.cancel();
			hbTimer = null;
		}

		// Notify session closed
		sessionNotifier.notifyWsSessionClosed(terminate);
	}

	/**
	 * Callback hook for Message Events. This method will be invoked when a
	 * server send a binary message.
	 * 
	 * @param message
	 *            The binary message
	 */
	@OnMessage
	public void onMessage(ByteBuffer buffer) {
		// System.out.println("WebsocketClientEndpoint.onMessage(): buffer
		// recieved");
		if (messageHandler != null) {
			messageHandler.handleMessage(buffer);
		}

		// Start timer for WS heart bit monitoring
		fireWSHeartBitMonitorTimer();
	}

	/**
	 * Callback hook for Message Events. This method will be invoked when a
	 * server send a text message.
	 * 
	 * @param message
	 *            The text message
	 */
	@OnMessage
	public void onMessage(String message) {
		System.out.println("WebsocketClientEndpoint.onMessage(): [String Message]: \n" + message);
	}

	/**
	 * register message handler
	 * 
	 * @param msgHandler
	 */
	public void addMessageHandler(MessageHandler msgHandler) {
		System.out.println("WebsocketClientEndpoint.addMessageHandler(): Adding MessageHandler...");
		this.messageHandler = msgHandler;
	}

	/**
	 * Send a message.
	 * 
	 * @param message
	 */
	public void sendMessage(String message) {
		System.out.println("WebsocketClientEndpoint.sendMessage(): sending message");
		this.userSession.getAsyncRemote().sendText(message);
	}

	/**
	 * forceClose - Force closing a websocket
	 */
	public void forceClose(boolean terminate) {
		System.out.println("WebsocketClientEndpoint.forceClose(): Force Closing Websocket....");
		try {
			this.terminate = terminate;
			this.userSession.close();
		} catch (IOException e) {
			System.out
					.println("WebsocketClientEndpoint.forceClose(): ERROR: IOException on userSession force close!!!");
			e.printStackTrace();
		}
		this.userSession = null;
	}

	/**
	 * Message handler Interface.
	 */
	public static interface MessageHandler {
		public void handleMessage(ByteBuffer buffer);
	}

	/**
	 * fireWSHeartBitMonitorTimer - private method to start WS heart bit monitor
	 * timer
	 */
	private void fireWSHeartBitMonitorTimer() {
		// start Timer for Web Socket Heart Bit Check
		if (hbTimer != null) {
			hbTimer.cancel();
		}
		hbTimer = new Timer("WS HeartBit Timer");
		hbTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				System.out.println(
						"WebsocketClientEndpoint.onMessage().new TimerTask().run(): ERROR: Streaming Quote WS HeartBit Timer Fired, notifying session notifier !!!");
				// Notify Heart bit Expired
				sessionNotifier.notifyWsHeartBitExpired();
			}
		}, hbTimeDelay);
	}
}
