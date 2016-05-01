package com.ritesh.zstreamingquote.config;

public class ZStreamingConfig {
	public static final String QUOTE_STREAMING_START_TIME = "09:15:01";
	public static final Integer QUOTE_STREAMING_WS_HEARTBIT_CHECK_TIME = 3000;
	public static final Integer QUOTE_STREAMING_WS_DATA_CHECK_TIME_ON_SUBSCRIBE = 5000;
	public static final Integer QUOTE_STREAMING_WS_SUBSCRIBE_DELAY_ON_INITIATE = 500;
	public static final String QUOTE_STREAMING_DB_URL = "jdbc:mysql://localhost/ZStreamingQuotesDB";
	public static final String QUOTE_STREAMING_DB_USER = "root";
	public static final String QUOTE_STREAMING_DB_PWD = "";
	public static final Integer QUOTE_STREAMING_REINITIATE_RETRY_LIMIT = 5;
	
	public static final String QUOTE_STREAMING_MODE_LTP = "ltp";
	public static final String QUOTE_STREAMING_MODE_QUOTE = "quote";
	public static final String QUOTE_STREAMING_MODE_FULL = "full";
	
	public static final Integer JETTY_SERVER_PORT_NUM = 8080;
	public static final String JETTY_SERVER_PROCESS_START_URL = "/zstreamingquote/start";
	public static final String JETTY_SERVER_PROCESS_STOP_URL = "/zstreamingquote/stop";
	public static final String JETTY_SERVER_TIMERANGE_OHLC_URL = "/zstreamingquote/timerangeohlc";
	public static final String JETTY_SERVER_TIMERANGE_STREAMING_QUOTE_URL = "/zstreamingquote/timerangestreamingquote";
	public static final String JETTY_SERVER_PROCESS_START_APIKEY_REQ_PARAM = "apikey";
	public static final String JETTY_SERVER_PROCESS_START_USERID_REQ_PARAM = "userid";
	public static final String JETTY_SERVER_PROCESS_START_PUBTOKEN_REQ_PARAM = "publictoken";
	public static final String JETTY_SERVER_TIMERANGE_FORMAT_REQ_PARAM = "format";
	public static final String JETTY_SERVER_TIMERANGE_FROM_TIME_REQ_PARAM = "from";
	public static final String JETTY_SERVER_TIMERANGE_TO_TIME_REQ_PARAM = "to";
	public static final String JETTY_SERVER_TIMERANGE_INSTRUMENT_REQ_PARAM = "instrument";
	
	/**
	 *  Common Configurations 
	 */
	public static Boolean isStreamingQuoteStartAtBootup(){
		//Whether To start WebSocket streaming at the time of application launch or delay
		return false;
	}
	
	public static String getStreamingQuoteStartTime(){
		//If WebSocket streaming is delayed, configure the start time when streaming should be started
		return QUOTE_STREAMING_START_TIME;
	}
	
	public static Integer getStreamingQuoteHeartBitCheckTime(){
		//Time gap to consider as Heart Bit stopped
		return QUOTE_STREAMING_WS_HEARTBIT_CHECK_TIME;
	}
	
	public static Integer getStreamingQuoteDataCheckTimeOnSubscribe(){
		//Time gap to consider as Data not received in socket even though subscribed
		return QUOTE_STREAMING_WS_DATA_CHECK_TIME_ON_SUBSCRIBE;
	}
	
	public static Integer getStreamingQuoteSubscribeDelayAfterInitiate(){
		//Time delay between WebSocket initiate and subscribe command
		return QUOTE_STREAMING_WS_SUBSCRIBE_DELAY_ON_INITIATE;
	}
	
	public static Integer getStreamingQuoteMaxInitiateRetryCount(){
		//Max num of retry to initiate WS session if Open Fails
		return QUOTE_STREAMING_REINITIATE_RETRY_LIMIT;
	}
	
	/**
	 * Instrument Tokens for Subscription
	 */
	public static String[] getInstrumentTokenArr(){
		//Instrument Tokens for streaming data subscribe
		String[] instrumentsArr = {
				"121345", "1793"
		};
		
		return instrumentsArr;
	}
	
	/**
	 * Streaming Data Modes
	 */
	public static String getStreamingQuoteMode(){
		//Streaming Data Mode: 'ltp' / 'quote' / 'full'
		return QUOTE_STREAMING_MODE_QUOTE;
	}
	
	/**
	 * DB specific config
	 */
	public static Boolean isStreamingQuoteStoringRequired(){
		//Whether to store streaming data in DB
		return true;
	}
	
	public static String getStreamingQuoteDbUrl(){
		//DB URL
		return QUOTE_STREAMING_DB_URL;
	}
	
	public static String getStreamingQuoteDbUser(){
		//DB User
		return QUOTE_STREAMING_DB_USER;
	}
	
	public static String getStreamingQuoteDbPwd(){
		//DB Password
		return QUOTE_STREAMING_DB_PWD;
	}
	
	public static String getStreamingQuoteTbNameAppendFormat(String date){
		//Streaming Quote table name append format "StreamingQuoteMode_Mode<modetype>_Date<DDMMYYYY>"
		//e.g. table name will be "StreamingQuoteMode_modeltp_29042016"
		return "StreamingQuoteMode_Mode" + getStreamingQuoteMode() + "_Date" + date;
	}
	
	/**
	 * Trading Holidays
	 */
	public static String[] getTradingHolidays(){
		//Dates when streaming quotes wont be active
		String[] tradingHolidays = {
				"26-01-2016", "07-03-2016", "24-03-2016", "25-03-2016", 
				"14-04-2016", "15-04-2016", "19-04-2016", "06-07-2016", 
				"15-08-2016", "05-09-2016", "13-09-2016", "11-10-2016", 
				"12-10-2016", "31-10-2016", "14-11-2016"
				};
		
		return tradingHolidays;
	}
	
	/**
	 * Print config
	 */
	public static Boolean isHeartBitMsgPrintable(){
		//Whether to print Heart Bit Message in Console
		return true;
	}
	
	public static Boolean isQuoteMsgPrintable(){
		//Whether to print Streaming Quote Message in console
		return true;
	}
	
	public static Boolean isWebServiceLogsPrintable(){
		//Whether to print WebService Log Message in console
		return true;
	}
	
	/**
	 * WebApp config
	 */
	public static Integer getJettyServerPortNum(){
		return JETTY_SERVER_PORT_NUM;
	}
	
	public static final String getJettyServerProcessStartURL(){
		return JETTY_SERVER_PROCESS_START_URL;
	}
	
	public static final String getJettyServerProcessStopURL(){
		return JETTY_SERVER_PROCESS_STOP_URL;
	}
	
	public static final String getJettyServerTimeRangeOHLCURL(){
		return JETTY_SERVER_TIMERANGE_OHLC_URL;
	}
	
	public static final String getJettyServerTimeRangeStreamingQuoteURL(){
		return JETTY_SERVER_TIMERANGE_STREAMING_QUOTE_URL;
	}
	
	public static final String getJettyServerProcessStartApiKeyReqParam(){
		return JETTY_SERVER_PROCESS_START_APIKEY_REQ_PARAM;
	}
	
	public static final String getJettyServerProcessStartUserIdReqParam(){
		return JETTY_SERVER_PROCESS_START_USERID_REQ_PARAM;
	}
	
	public static final String getJettyServerProcessStartPubTokenReqParam(){
		return JETTY_SERVER_PROCESS_START_PUBTOKEN_REQ_PARAM;
	}
	
	public static final String getJettyServerTimeRangeOHLCformatReqParam(){
		return JETTY_SERVER_TIMERANGE_FORMAT_REQ_PARAM;
	}
	
	public static final String getJettyServerTimeRangeOHLCfromTimeReqParam(){
		return JETTY_SERVER_TIMERANGE_FROM_TIME_REQ_PARAM;
	}
	
	public static final String getJettyServerTimeRangeOHLCtoTimeReqParam(){
		return JETTY_SERVER_TIMERANGE_TO_TIME_REQ_PARAM;
	}
	
	public static final String getJettyServerTimeRangeOHLCinstrumentReqParam(){
		return JETTY_SERVER_TIMERANGE_INSTRUMENT_REQ_PARAM;
	}
	
	public static final String getJettyServerTimeRangeStreamingQuoteformatReqParam(){
		return JETTY_SERVER_TIMERANGE_FORMAT_REQ_PARAM;
	}
	
	public static final String getJettyServerTimeRangeStreamingQuotefromTimeReqParam(){
		return JETTY_SERVER_TIMERANGE_FROM_TIME_REQ_PARAM;
	}
	
	public static final String getJettyServerTimeRangeStreamingQuotetoTimeReqParam(){
		return JETTY_SERVER_TIMERANGE_TO_TIME_REQ_PARAM;
	}
	
	public static final String getJettyServerTimeRangeStreamingQuoteinstrumentReqParam(){
		return JETTY_SERVER_TIMERANGE_INSTRUMENT_REQ_PARAM;
	}
}
