package com.ritesh.zstreamingquote.control;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.ritesh.zstreamingquote.config.ZStreamingConfig;
import com.ritesh.zstreamingquote.db.IStreamingQuoteStorage;
import com.ritesh.zstreamingquote.db.StreamingQuoteStorageFactory;
import com.ritesh.zstreamingquote.quote.OHLCquote;
import com.ritesh.zstreamingquote.quote.StreamingQuote;
import com.ritesh.zstreamingquote.websocket.WebsocketThread;

public class ZStreamingQuoteControl {
	private DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	private TimeZone timeZone = TimeZone.getTimeZone("IST");
	private String todaysDate = null; 	// In yyyy-MM-dd format
	
	//singleton instance
	private static ZStreamingQuoteControl zStreamingQuoteControl = null;
	
	//Quote Streaming - WebSocket
	private boolean streamingQuoteStarted = false;
	private WebsocketThread websocketThread = null;
	private static final String STREAMING_QUOTE_WS_URL_TEMPLATE = "wss://websocket.kite.trade/?";
	private static final String QUOTE_STREAMING_TIME = ZStreamingConfig.getStreamingQuoteStartTime();
	
	//Quote Storage
	private IStreamingQuoteStorage streamingQuoteStorage = null;

	/**
	 * constructor
	 */
	private ZStreamingQuoteControl() {
		//update todays date - STEP 1
		updateTodaysDate();
		
		//Get Quote storage - STEP 2
		if(ZStreamingConfig.isStreamingQuoteStoringRequired()){
			streamingQuoteStorage = StreamingQuoteStorageFactory.getStreamingQuoteStorage();
			if(streamingQuoteStorage == null){
				System.out.println("ZStreamingQuoteControl.ZStreamingQuoteControl(): ERROR: Streaming Quote storage is null... !!!");
			}
		} else{
			System.out.println("ZStreamingQuoteControl.ZStreamingQuoteControl(): WARNING: Streaming Quote Storage Config disabled");
		}
	}

	/**
	 * getInstance - singleton instance provider
	 * @return ZStreamingQuoteControl singleton instance
	 */
	public static ZStreamingQuoteControl getInstance() {
		if (zStreamingQuoteControl == null) {
			zStreamingQuoteControl = new ZStreamingQuoteControl();
		}

		return zStreamingQuoteControl;
	}
	
	/**
	 * start - public method to start Web Socket For Streaming Quote
	 * @param apiKey
	 * @param userId
	 * @param publicToken
	 */
	public void start(String apiKey, String userId, String publicToken){
		if(ZStreamingConfig.isStreamingQuoteStartAtBootup()){
			//start Streaming Quote WebSocket - immediately
			System.out.println("ZStreamingQuoteControl.start(): Starting Streaming Quote WS");
			startStreamingQuote(apiKey, userId, publicToken);
		} else{
			//start Streaming Quote WebSocket - at market open
			Thread t = new Thread(new Runnable(){
				private boolean runnable = true;
				private DateFormat dtFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				private TimeZone timeZone = TimeZone.getTimeZone("IST");
				private String refTime = todaysDate + " " + QUOTE_STREAMING_TIME;
				
				@Override
				public void run() {
					dtFmt.setTimeZone(timeZone);
					try {
						Date timeRef = dtFmt.parse(refTime);
						while(runnable){
							Date timeNow = Calendar.getInstance(timeZone).getTime();
							if(timeNow.compareTo(timeRef) >= 0){
								System.out.println("ZStreamingQuoteControl.start().StreamingQuoteStartThread.run(): Starting Streaming Quote WS at: " + timeNow);
								runnable = false;
								startStreamingQuote(apiKey, userId, publicToken);
							}
							Thread.sleep(1000);
						}
					} catch (ParseException e1) {
						System.out.println("ZStreamingQuoteControl.start(): ParseException while parsing time: [" + refTime + "], cause: " + e1.getMessage());
					} catch (InterruptedException e) {
						System.out.println("ZStreamingQuoteControl.start(): InterruptedException for Thread sleep");
					}						
				}			
			});
			t.start();
		}
	}
	
	/**
	 * stop - public method to stop Web Socket For Streaming Quote
	 */
	public void stop(){
		stopStreamingQuote();
	}
	
	/**
	 * getOHLCDataByTimeRange - public method to get OHLC Data between a time range
	 * @param instrumentToken
	 * @param fromTime
	 * @param toTime
	 * @return OHLCquote
	 */
	public OHLCquote getOHLCDataByTimeRange(String instrumentToken, String fromTime, String toTime){
		if (ZStreamingConfig.isStreamingQuoteStoringRequired() && (streamingQuoteStorage != null)) {
			//Get OHLC Data
			return streamingQuoteStorage.getOHLCDataByTimeRange(instrumentToken, fromTime, toTime);
		} else{
			return null;
		}
	}
	
	/**
	 * getQuoteListByTimeRange - public method to get complete Streamed Data between a time range
	 * @param instrumentToken
	 * @param fromTime
	 * @param toTime
	 * @return StreamingQuote List
	 */
	public List<StreamingQuote> getQuoteListByTimeRange(String instrumentToken, String fromTime, String toTime){
		if (ZStreamingConfig.isStreamingQuoteStoringRequired() && (streamingQuoteStorage != null)) {
			//Get Complete Streamed Data
			return streamingQuoteStorage.getQuoteListByTimeRange(instrumentToken, fromTime, toTime);
		} else{
			return null;
		}
	}
	
	/**
	 * updateTodaysDate - private method to update todays date
	 */
	private void updateTodaysDate(){
		DateFormat dtFmt = new SimpleDateFormat("yyyy-MM-dd");
		dtFmt.setTimeZone(timeZone);
		todaysDate = dtFmt.format(Calendar.getInstance(timeZone).getTime());
	}
	
	/**
	 * startStreamingQuote - private method to start streaming Quote
	 * @param apiKey
	 * @param userId
	 * @param publicToken
	 */
	private void startStreamingQuote(String apiKey, String userId, String publicToken){
		String URIstring = 
				STREAMING_QUOTE_WS_URL_TEMPLATE + "api_key="+ apiKey + "&user_id="+ userId + "&public_token=" + publicToken;
		
		DateFormat quoteTableDtFmt = new SimpleDateFormat("ddMMyyyy");
		quoteTableDtFmt.setTimeZone(timeZone);
		String date = quoteTableDtFmt.format(Calendar.getInstance(timeZone).getTime());
		
		if (ZStreamingConfig.isStreamingQuoteStoringRequired() && (streamingQuoteStorage != null)) {
			// Initialize streaming Quote Storage
			streamingQuoteStorage.initializeJDBCConn();
			streamingQuoteStorage.createDaysStreamingQuoteTable(date);
		}
		
		//Instrument Tokens List for subscribing
		List<String> instrumentList = getInstrumentTokensList();
		
		//Start Web Socket for streaming data
		websocketThread = new WebsocketThread(URIstring, instrumentList, streamingQuoteStorage);
		streamingQuoteStarted = websocketThread.startWS();
		if(streamingQuoteStarted){
			Thread t = new Thread(websocketThread);
			t.start();
		} else{
			System.out.println("ZStreamingQuoteControl.startStreamingQuote(): ERROR: WebSocket Streaming Quote not started !!!");
		}
	}
	
	/**
	 * stopStreamingQuote - private method to stop streaming Quote WS
	 */
	private void stopStreamingQuote(){
		//stop web socket for streaming quote URL
		if(streamingQuoteStarted && websocketThread != null){
			websocketThread.stopWS();
			//Allow WS to get closed
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				System.out.println("ZStreamingQuoteControl.stopStreamingQuote(): ERROR: InterruptedException while sleeping");
			}
		}
		
		if(ZStreamingConfig.isStreamingQuoteStoringRequired() && (streamingQuoteStorage != null)){
			//Close streaming quote storage
			streamingQuoteStorage.closeJDBCConn();
		}
	}
	
	/**
	 * getInstrumentsList - private method to get Instruments List for streaming quotes
	 * @return Instruments List
	 */
	private List<String> getInstrumentTokensList(){
		String[] instrumentsArr = ZStreamingConfig.getInstrumentTokenArr();
		List<String> instrumentList = Arrays.asList(instrumentsArr);
		System.out.println("ZStreamingQuoteControl.getInstrumentTokensList(): Subscribe Instrument Tokens: " + instrumentList);
		
		return instrumentList;
	}
}
