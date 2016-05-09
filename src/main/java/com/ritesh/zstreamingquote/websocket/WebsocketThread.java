package com.ritesh.zstreamingquote.websocket;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.ritesh.zstreamingquote.config.ZStreamingConfig;
import com.ritesh.zstreamingquote.db.IStreamingQuoteStorage;
import com.ritesh.zstreamingquote.quoteparser.StreamingQuoteParserThread;

public class WebsocketThread implements Runnable, IwsSessionNotifier{
	private String URIstring = null;
	private List<String> instrumentList = null;
	private static WebsocketClientEndpoint clientEndPoint = null;
	private BlockingQueue<Object> quoteBufferQ = null;
	private StreamingQuoteParserThread quoteParserThread = null;
	
	private static Timer dataTimer = null;
    private static final int dataTimeDelay = ZStreamingConfig.getStreamingQuoteDataCheckTimeOnSubscribe();
    
    private static int wsSessionRetry = 0;
    
    //Quote Storage
  	private IStreamingQuoteStorage streamingQuoteStorage = null;
	
	private enum WSstate{
		WS_INITIATED,
		WS_OPENED,
		WS_SUBSCRIBED,
		WS_MODE_SWITCHED,
		WS_DATA_MISSED,
		WS_MSG_RECEIVED,
		WS_UNSUBSCRIBED,
		WS_HEARTBIT_EXPIRED,
		WS_CLOSED
	}
	//current WS state
	WSstate currWSstate = null;
	//WS state maintaining lock
	private Lock currWSstateLock = null;

	// running status
	private boolean runStatus = false;
	
	/**
	 * Constructor
	 * @param URIstring
	 * @param instrumentList
	 * @param streamingQuoteStorage
	 */
	public WebsocketThread(String URIstring, List<String> instrumentList,
							IStreamingQuoteStorage streamingQuoteStorage){
		this.URIstring = URIstring;
		this.instrumentList = instrumentList;
		
		if(ZStreamingConfig.isStreamingQuoteStoringRequired()){
			this.streamingQuoteStorage = streamingQuoteStorage;
		}
		
		this.currWSstateLock = new ReentrantLock();
	}
	
	/**
	 * startWS - public method to initiate the websocket
	 * @return websocket creation status
	 */
	public boolean startWS(){
		boolean status = false;
		
		//Establish the websocket
		try {
			System.out.println("WebsocketThread.startWS(): creating WebsocketClientEndpoint with URI: <" + 
																									URIstring + ">....");
			//save the state of web socket before initiating
			//open is an async call, notification may come before setting state
			currWSstateLock.lock();
	        currWSstate = WSstate.WS_INITIATED;
	        currWSstateLock.unlock();
	        
	        //initiate WS
			clientEndPoint = new WebsocketClientEndpoint(new URI(URIstring), this);
			status = true;
			
			//set the running status
			runStatus = true;
		} catch (URISyntaxException e) {
			System.out.println("WebsocketThread.startWS(): ERROR: URISyntaxException on WebsocketClientEndpoint");
			e.printStackTrace();
		}
		return status;
	}
	
	@Override
	public void run() {
		//create Q for sending quote buffer
		quoteBufferQ = new ArrayBlockingQueue<Object>(300);
		
		//spawn quote parse thread
		System.out.println("WebsocketThread.run(): starting StreamingQuoteParserThread...");
		quoteParserThread = new StreamingQuoteParserThread(quoteBufferQ, streamingQuoteStorage);
		Thread parserTh = new Thread(quoteParserThread);
		parserTh.start();
		
		//send websocket subscribe message
		System.out.println("WebsocketThread.run(): Sending Suscribe message with Handler to Streaming Quote WS server");
		subscribeWSwithMsgHandler();
		
		//loop
		while(runStatus){
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				System.out.println("WebsocketThread.run(): ERROR: InterruptedException on sleep");
			}
		}
	}
	
	/**
	 * stopWS - method to stop the websocket thread
	 */
	public void stopWS(){
		System.out.println("WebsocketThread.stopWS(): Sending UnSuscribe message to Streaming Quote WS server");
		sendUnSubscribeMessage();
		
		System.out.println("WebsocketThread.stopWS(): Terminate Force closing previous WS session");
		clientEndPoint.forceClose(true);
		clientEndPoint = null;
		
		currWSstateLock.lock();
		currWSstate = null;
		currWSstateLock.unlock();
		
		System.out.println("WebsocketThread.stopWS(): method called to stop Websocket data parser thread...");
		//Stop quote parser thread first
		quoteParserThread.stopThread();
		
		//flag this thread to stop
		runStatus = false;
	}
	
	/**
	 * addMessageHandler - private method to add message handler
	 */
	private void addMessageHandler() {
		//add listener
		clientEndPoint.addMessageHandler(new WebsocketClientEndpoint.MessageHandler() {
			public void handleMessage(ByteBuffer buffer) {
				//save the state of web socket
				currWSstateLock.lock();
		        currWSstate = WSstate.WS_MSG_RECEIVED;
		        currWSstateLock.unlock();
		        
				//send the buffer to Q
				try {
					quoteBufferQ.put(buffer);
				} catch (InterruptedException e) {
					System.out.println("WebsocketThread.addMessageHandler().new MessageHandler(): " +
											"ERROR: InterruptedException on putting to quoteBufferQ");
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * sendSubscribeMessage - private method to send subscribe message for the instruments
	 */
	private void sendSubscribeMessage(){
		//save the state of web socket before subscribing, due to async call
        currWSstateLock.lock();
        currWSstate = WSstate.WS_SUBSCRIBED;
        currWSstateLock.unlock();
        
		String instrumentString = getInstrumentString(instrumentList);
		//send message to websocket e.g. INFY (408065) and TATAMOTORS (884737)
        String msg = "{\"a\": \"subscribe\", \"v\": [" + instrumentString + "]}";
        System.out.println("WebsocketThread.sendSubscribeMessage(): WS Subscribe msg: " + msg);
        clientEndPoint.sendMessage(msg);
	}
	
	/**
	 * sendModeMessage - private method to send mode message for the instruments
	 */
	private void sendModeMessage(){
		//save the state of web socket before switching mode, due to async call
        currWSstateLock.lock();
        currWSstate = WSstate.WS_MODE_SWITCHED;
        currWSstateLock.unlock();
        
		String instrumentString = getInstrumentString(instrumentList);
		//send message to websocket e.g. INFY (408065) and TATAMOTORS (884737)
        String msg = "{\"a\": \"mode\", \"v\": [\"" + ZStreamingConfig.getStreamingQuoteMode() + "\", [" + instrumentString + "]]}";
        System.out.println("WebsocketThread.sendModeMessage(): WS mode msg: " + msg);
        clientEndPoint.sendMessage(msg);
        
        //Dirty Hack: At Market Open, WS does not respond with data after Open and Subscribe
        fireDataMissTimerOnWSsubscribe();
	}
	
	/**
	 * sendUnSubscribeMessage - private method to send unsubscribe message for the instruments
	 */
	private void sendUnSubscribeMessage(){
		//save the state of web socket before unsubscribing, due to async call
        currWSstateLock.lock();
        currWSstate = WSstate.WS_UNSUBSCRIBED;
        currWSstateLock.unlock();
        
		String instrumentString = getInstrumentString(instrumentList);
        String msg = "{\"a\": \"unsubscribe\", \"v\": [" + instrumentString + "]}";
        System.out.println("WebsocketThread.sendUnSubscribeMessage(): WS UnSubscribe msg: " + msg);
        clientEndPoint.sendMessage(msg);
	}
	
	/**
	 * getInstrumentString - private method to create instrument string from instrument list
	 * @param instrumentList
	 * @return instrumentString
	 */
	private String getInstrumentString(List<String> instrumentList){
		StringBuilder stringBuilder = new StringBuilder();
		for(int i = 0; i < instrumentList.size(); i++){
			stringBuilder.append(instrumentList.get(i));
			if(i < (instrumentList.size() - 1)){
				stringBuilder.append(",");
			}
		}
		
		String instrumentString = stringBuilder.toString();
		System.out.println("WebsocketThread.getInstrumentString(): instrumentString: [" + instrumentString + "]");
		return instrumentString;
	}
	
	@Override
	public void notifyWsInitiateFailed(){
		try {
			//delay before re initiating
			Thread.sleep(ZStreamingConfig.getStreamingQuoteReinitiateDelayOnInitiateFail());
		} catch (InterruptedException e1) {
			System.out.println("WebsocketThread.notifyWsInitiateFailure(): ERROR: InterruptedException on sleep !!!");
		}
		
		// Establish the websocket again
		try {
			System.out.println(
					"WebsocketThread.notifyWsInitiateFailure(): Previous WS initiate Failed, "
							+ "creating new WebsocketClientEndpoint with URI: <" + URIstring + ">....");
			clientEndPoint = new WebsocketClientEndpoint(new URI(URIstring), this);
			// save the state of web socket
			currWSstateLock.lock();
			currWSstate = WSstate.WS_INITIATED;
			currWSstateLock.unlock();
			
			// Subscribe again with message Handler
			System.out.println(
					"WebsocketThread.notifyWsInitiateFailure(): ReSending Suscribe message with handler to Streaming Quote WS server");
			subscribeWSwithMsgHandler();
		} catch (URISyntaxException e) {
			System.out.println("WebsocketThread.notifyWsInitiateFailure(): ERROR: URISyntaxException on WebsocketClientEndpoint");
			e.printStackTrace();
		}
	}

	
	@Override
	public void notifyWsSessionOpened(){
		//save the state of web socket
		currWSstateLock.lock();
        currWSstate = WSstate.WS_OPENED;
        currWSstateLock.unlock();
	}

	@Override
	public void notifyWsSessionClosed(boolean toTerminate) {
		//save the state of web socket
		currWSstateLock.lock();
        currWSstate = WSstate.WS_CLOSED;
        currWSstateLock.unlock();
        
		if(toTerminate){
			//Nothing to do, WS session is to be terminated completely
			System.out.println("WebsocketThread.notifyWsSessionClosed(): Previous WS session closed on Termination");
		} else{
			//Abrupt close of WS session, restart the WS session
			System.out.println("WebsocketThread.notifyWsSessionClosed(): ERROR: Previous WS session closed, reStarting new WS session !!!");
			try {
				if(clientEndPoint != null){
					//save the state of web socket before initiating
					//open is an async call, notification may come before setting state
					currWSstateLock.lock();
			        currWSstate = WSstate.WS_INITIATED;
			        currWSstateLock.unlock();
			        
			        //initiate
					clientEndPoint = new WebsocketClientEndpoint(new URI(URIstring), this);
								        
			        //Subscribe again with message Handler
			        System.out.println("WebsocketThread.notifyWsSessionClosed(): ReSending Suscribe message with handler to Streaming Quote WS server");
			        subscribeWSwithMsgHandler();
				} else{
					System.out.println("WebsocketThread.notifyWsSessionClosed(): ERROR: clientEndPoint is null");
				}
			} catch (URISyntaxException e) {
				System.out.println("WebsocketThread.notifyWsSessionClosed(): ERROR: URISyntaxException on reopening of WS session");
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void notifyWsDataMissedAfterSubscribe() {
		//save the state of web socket
		currWSstateLock.lock();
        currWSstate = WSstate.WS_DATA_MISSED;
        currWSstateLock.unlock();
        
		System.out.println("WebsocketThread.notifyWsDataMissedAfterSubscribe(): ERROR: WS session Data Missed notified after Subscribe!!!");
		
		if(clientEndPoint != null){
			//first unsubscribe the previous messages
			System.out.println("WebsocketThread.notifyWsDataMissedAfterSubscribe(): Sending UnSubscribe message to Streaming Quote WS server");
			sendUnSubscribeMessage();
			
			//force close the session
			System.out.println("WebsocketThread.notifyWsDataMissedAfterSubscribe(): Force closing previous WS session");
			clientEndPoint.forceClose(false);
		} else{
			System.out.println("WebsocketThread.notifyWsDataMissedAfterSubscribe(): ERROR: clientEndPoint is null");
		}
	}
	
	@Override
	public void notifyWsHeartBitExpired() {
		//save the state of web socket
		currWSstateLock.lock();
        currWSstate = WSstate.WS_HEARTBIT_EXPIRED;
        currWSstateLock.unlock();
        
		System.out.println("WebsocketThread.notifyWsHeartBitExpired(): ERROR: Previous WS session heart bit expired notified!!!");
		
		if(clientEndPoint != null){
			//first unsubscribe the previous messages
			System.out.println("WebsocketThread.notifyWsHeartBitExpired(): Sending UnSubscribe message to Streaming Quote WS server");
			sendUnSubscribeMessage();
			
			//force close the session
			System.out.println("WebsocketThread.notifyWsHeartBitExpired(): Force closing previous WS session");
			clientEndPoint.forceClose(false);
		} else{
			System.out.println("WebsocketThread.notifyWsHeartBitExpired(): ERROR: clientEndPoint is null");
		}
	}
	
	/**
	 * subscribeWSwithMsgHandler - private method to subscribe with Msg Handler
	 */
	private void subscribeWSwithMsgHandler(){
		try {
			Thread.sleep(ZStreamingConfig.getStreamingQuoteSubscribeDelayAfterInitiate());
		} catch (InterruptedException e) {
			System.out.println("WebsocketThread.subscribeWSwithMsgHandler(): ERROR: InterruptedException on sleep before subscribe !!!");
		}
		
		if(currWSstate == WSstate.WS_OPENED){
			//add message handler
			addMessageHandler();
			
			//send websocket subscribe message		
			sendSubscribeMessage();
			
			//send websocket mode message
			sendModeMessage();
		} else{
			//WebSocket Did not get Opened even on delay after initiation
			if(wsSessionRetry < ZStreamingConfig.getStreamingQuoteMaxInitiateRetryCount()){
				//Reinitiate WS session
				System.out.println("WebsocketThread.subscribeWSwithMsgHandler(): WARNING: WS Open FAILED On Initiation, Retrying !!!");
				wsSessionRetry++;
				reInitiateOnWSOpenFailure();
			} else{
				//Max limit reached, No initiation again
				System.out.println("WebsocketThread.subscribeWSwithMsgHandler(): ERROR: WS reinitiation max limit reached, no retry !!!");
			}
		}
	}
	
	/**
	 * reInitiateOnWSOpenFailure - private method to reinitiate websocket session on Open Failure
	 */
	private void reInitiateOnWSOpenFailure(){
		if(clientEndPoint != null){
			//force close the session without terminating
			System.out.println("WebsocketThread.reInitiateOnWSOpenFailure(): Force closing previous WS session");
			clientEndPoint.forceClose(false);
		} else{
			System.out.println("WebsocketThread.reInitiateOnWSOpenFailure(): ERROR: clientEndPoint is null");
		}
	}
	
	/**
	 * fireDataMissTimerOnWSsubscribe - private method to fire data miss timer on subscribe
	 */
	private void fireDataMissTimerOnWSsubscribe(){
        //start Timer for Web Socket Data miss Check after subscribe
        if(dataTimer != null){
        	dataTimer.cancel();
		}
        dataTimer = new Timer("WS Data Miss Timer");
        dataTimer.schedule(new TimerTask() {			
			@Override
			public void run() {
				if(currWSstate == WSstate.WS_MODE_SWITCHED){
					System.out.println("WebsocketThread.fireDataMissTimerOnWSsubscribe().new TimerTask().run(): ERROR: " +
							"Streaming Quote WS Data Miss Timer Fired after subscribe, notifying session notifier !!!");
					//Notify Data Missed after Subscribe
					notifyWsDataMissedAfterSubscribe();
				} else{
					//Data started, let the timer expire
					System.out.println("WebsocketThread.fireDataMissTimerOnWSsubscribe()new TimerTask().run(): " + 
														"WS data getting pushed in, curr state[" + currWSstate + "]");
				}
			}
		}, dataTimeDelay);
	}
}
