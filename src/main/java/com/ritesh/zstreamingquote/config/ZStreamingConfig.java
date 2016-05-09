package com.ritesh.zstreamingquote.config;

public class ZStreamingConfig {
	/**
	 * Configuration Constants
	 */
	public static final String QUOTE_STREAMING_START_TIME = "09:15:01";
	public static final Integer QUOTE_STREAMING_REINITIATE_DELAY_ON_INITIATE_FAIL = 500;
	public static final Integer QUOTE_STREAMING_WS_HEARTBIT_CHECK_TIME = 3000;
	public static final Integer QUOTE_STREAMING_WS_DATA_CHECK_TIME_ON_SUBSCRIBE = 5000;
	public static final Integer QUOTE_STREAMING_WS_SUBSCRIBE_DELAY_ON_INITIATE = 500;
	public static final Integer QUOTE_STREAMING_REINITIATE_RETRY_LIMIT = 5;
	public static final Boolean QUOTE_STREAMING_START_AT_BOOTUP = false;
	
	public static final String QUOTE_STREAMING_DB_URL = "jdbc:mysql://localhost/ZStreamingQuotesDB";
	public static final String QUOTE_STREAMING_DB_USER = "root";
	public static final String QUOTE_STREAMING_DB_PWD = "";
	public static final String QUOTE_STREAMING_DB_TABLE_NAME_PRE_APPENDER = "StreamingQuoteMode_Mode";
	public static final String QUOTE_STREAMING_DB_TABLE_NAME_POST_APPENDER = "_Date";
	public static final Boolean QUOTE_STREAMING_DB_STORE_REQD = true;
	
	public static final Boolean QUOTE_STREAMING_HEART_BIT_MSG_PRINT = true;
	public static final Boolean QUOTE_STREAMING_QUOTE_FLOW_MSG_PRINT = true;
	public static final Boolean QUOTE_STREAMING_WEB_SERVICE_MSG_PRINT = true;

	public static final String[] QUOTE_STREAMING_INSTRUMENTS_ARR = { "121345", "1793" };
	public static final String[] QUOTE_STREAMING_TRADING_HOLIDAYS = { "26-01-2016", "07-03-2016", "24-03-2016",
			"25-03-2016", "14-04-2016", "15-04-2016", "19-04-2016", "06-07-2016", "15-08-2016", "05-09-2016",
			"13-09-2016", "11-10-2016", "12-10-2016", "31-10-2016", "14-11-2016" };

	public static final String QUOTE_STREAMING_MODE_LTP = "ltp";
	public static final String QUOTE_STREAMING_MODE_QUOTE = "quote";
	public static final String QUOTE_STREAMING_MODE_FULL = "full";
	public static final String QUOTE_STREAMING_DEFAULT_MODE = QUOTE_STREAMING_MODE_QUOTE;

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
	 * Common Configurations
	 */
	public static Boolean isStreamingQuoteStartAtBootup() {
		// Whether To start WebSocket streaming at the time of application
		// launch or delay
		return QUOTE_STREAMING_START_AT_BOOTUP;
	}

	public static String getStreamingQuoteStartTime() {
		// If WebSocket streaming is delayed, configure the start time when
		// streaming should be started
		return QUOTE_STREAMING_START_TIME;
	}
	
	public static Integer getStreamingQuoteReinitiateDelayOnInitiateFail(){
		//If websocket initiation fails due to exception, reiinitiate with after this delay
		return QUOTE_STREAMING_REINITIATE_DELAY_ON_INITIATE_FAIL;
	}

	public static Integer getStreamingQuoteHeartBitCheckTime() {
		// Time gap to consider as Heart Bit stopped
		return QUOTE_STREAMING_WS_HEARTBIT_CHECK_TIME;
	}

	public static Integer getStreamingQuoteDataCheckTimeOnSubscribe() {
		// Time gap to consider as Data not received in socket even though
		// subscribed
		return QUOTE_STREAMING_WS_DATA_CHECK_TIME_ON_SUBSCRIBE;
	}

	public static Integer getStreamingQuoteSubscribeDelayAfterInitiate() {
		// Time delay between WebSocket initiate and subscribe command
		return QUOTE_STREAMING_WS_SUBSCRIBE_DELAY_ON_INITIATE;
	}

	public static Integer getStreamingQuoteMaxInitiateRetryCount() {
		// Max num of retry to initiate WS session if Open Fails
		return QUOTE_STREAMING_REINITIATE_RETRY_LIMIT;
	}

	/**
	 * Instrument Tokens for Subscription
	 */
	public static String[] getInstrumentTokenArr() {
		// Instrument Tokens for streaming data subscribe
		return QUOTE_STREAMING_INSTRUMENTS_ARR;
	}

	/**
	 * Streaming Data Modes
	 */
	public static String getStreamingQuoteMode() {
		// Streaming Data Mode: 'ltp' / 'quote' / 'full'
		return QUOTE_STREAMING_DEFAULT_MODE;
	}

	/**
	 * DB specific config
	 */
	public static Boolean isStreamingQuoteStoringRequired() {
		// Whether to store streaming data in DB
		return QUOTE_STREAMING_DB_STORE_REQD;
	}

	public static String getStreamingQuoteDbUrl() {
		// DB URL
		return QUOTE_STREAMING_DB_URL;
	}

	public static String getStreamingQuoteDbUser() {
		// DB User
		return QUOTE_STREAMING_DB_USER;
	}

	public static String getStreamingQuoteDbPwd() {
		// DB Password
		return QUOTE_STREAMING_DB_PWD;
	}

	public static String getStreamingQuoteTbNameAppendFormat(String date) {
		// Streaming Quote table name append format
		// "StreamingQuoteMode_Mode<modetype>_Date<DDMMYYYY>"
		// e.g. table name will be "StreamingQuoteMode_modeltp_29042016"
		return QUOTE_STREAMING_DB_TABLE_NAME_PRE_APPENDER + getStreamingQuoteMode()
				+ QUOTE_STREAMING_DB_TABLE_NAME_POST_APPENDER + date;
	}

	/**
	 * Trading Holidays
	 */
	public static String[] getTradingHolidays() {
		// Dates when streaming quotes wont be active
		return QUOTE_STREAMING_TRADING_HOLIDAYS;
	}

	/**
	 * Print config
	 */
	public static Boolean isHeartBitMsgPrintable() {
		// Whether to print Heart Bit Message in Console
		return QUOTE_STREAMING_HEART_BIT_MSG_PRINT;
	}

	public static Boolean isQuoteMsgPrintable() {
		// Whether to print Streaming Quote Message in console
		return QUOTE_STREAMING_QUOTE_FLOW_MSG_PRINT;
	}

	public static Boolean isWebServiceLogsPrintable() {
		// Whether to print WebService Log Message in console
		return QUOTE_STREAMING_WEB_SERVICE_MSG_PRINT;
	}

	/**
	 * WebApp config
	 */
	public static Integer getJettyServerPortNum() {
		// Port Num for Jetty Server
		return JETTY_SERVER_PORT_NUM;
	}

	public static final String getJettyServerProcessStartURL() {
		// Streaming Quote process Start URL
		return JETTY_SERVER_PROCESS_START_URL;
	}

	public static final String getJettyServerProcessStopURL() {
		// Streaming Quote process Stop URL
		return JETTY_SERVER_PROCESS_STOP_URL;
	}

	public static final String getJettyServerTimeRangeOHLCURL() {
		// Streaming Quote OHLC Data Fetch URL
		return JETTY_SERVER_TIMERANGE_OHLC_URL;
	}

	public static final String getJettyServerTimeRangeStreamingQuoteURL() {
		// Streaming Quote Streamed Data Fetch URL
		return JETTY_SERVER_TIMERANGE_STREAMING_QUOTE_URL;
	}

	public static final String getJettyServerProcessStartApiKeyReqParam() {
		// Streaming Quote process Start Request Param Api Key
		return JETTY_SERVER_PROCESS_START_APIKEY_REQ_PARAM;
	}

	public static final String getJettyServerProcessStartUserIdReqParam() {
		// Streaming Quote process Start Request Param User ID
		return JETTY_SERVER_PROCESS_START_USERID_REQ_PARAM;
	}

	public static final String getJettyServerProcessStartPubTokenReqParam() {
		// Streaming Quote process Start Request Param Public Token
		return JETTY_SERVER_PROCESS_START_PUBTOKEN_REQ_PARAM;
	}

	public static final String getJettyServerTimeRangeOHLCformatReqParam() {
		// OHLC Data Request Param Data Format type
		return JETTY_SERVER_TIMERANGE_FORMAT_REQ_PARAM;
	}

	public static final String getJettyServerTimeRangeOHLCfromTimeReqParam() {
		// OHLC Data Request Param From Time
		return JETTY_SERVER_TIMERANGE_FROM_TIME_REQ_PARAM;
	}

	public static final String getJettyServerTimeRangeOHLCtoTimeReqParam() {
		// OHLC Data Request Param To Time
		return JETTY_SERVER_TIMERANGE_TO_TIME_REQ_PARAM;
	}

	public static final String getJettyServerTimeRangeOHLCinstrumentReqParam() {
		// OHLC Data Request Param Instrument Token
		return JETTY_SERVER_TIMERANGE_INSTRUMENT_REQ_PARAM;
	}

	public static final String getJettyServerTimeRangeStreamingQuoteformatReqParam() {
		// Streaming Data Request Param Data Format Type
		return JETTY_SERVER_TIMERANGE_FORMAT_REQ_PARAM;
	}

	public static final String getJettyServerTimeRangeStreamingQuotefromTimeReqParam() {
		// Streaming Data Request Param From Time
		return JETTY_SERVER_TIMERANGE_FROM_TIME_REQ_PARAM;
	}

	public static final String getJettyServerTimeRangeStreamingQuotetoTimeReqParam() {
		// Streaming Data Request Param To Time
		return JETTY_SERVER_TIMERANGE_TO_TIME_REQ_PARAM;
	}

	public static final String getJettyServerTimeRangeStreamingQuoteinstrumentReqParam() {
		// Streaming Data Request Param Instrument Token
		return JETTY_SERVER_TIMERANGE_INSTRUMENT_REQ_PARAM;
	}
}
