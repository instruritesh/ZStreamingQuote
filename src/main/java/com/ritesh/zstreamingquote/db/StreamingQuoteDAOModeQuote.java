package com.ritesh.zstreamingquote.db;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.ritesh.zstreamingquote.config.ZStreamingConfig;
import com.ritesh.zstreamingquote.quote.OHLCquote;
import com.ritesh.zstreamingquote.quote.StreamingQuote;
import com.ritesh.zstreamingquote.quote.mode.StreamingQuoteModeQuote;

public class StreamingQuoteDAOModeQuote implements IStreamingQuoteStorage{
	// JDBC driver name and database URL
	private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	private static final String DB_URL = ZStreamingConfig.getStreamingQuoteDbUrl();

	// Database credentials
	private static final String USER = ZStreamingConfig.getStreamingQuoteDbUser();
	private static final String PASS = ZStreamingConfig.getStreamingQuoteDbPwd();

	// DB connection
	private Connection conn = null;
	
	//Quote Table Name
	private static String quoteTable = null;
	
	/**
	 * constructor
	 */
	public StreamingQuoteDAOModeQuote(){
		//
	}
	
	/**
	 * initializeJDBCConn - method to create and initialize JDBC connection
	 */
	@Override
	public void initializeJDBCConn() {
		try {
			System.out.println("StreamingQuoteDAOModeQuote.initializeJDBCConn(): creating JDBC connection for Streaming Quote...");
			// Register JDBC driver
			Class.forName(JDBC_DRIVER);
			// Open the connection
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
		} catch (ClassNotFoundException e) {
			System.out.println("StreamingQuoteDAOModeQuote.initializeJDBCConn(): ClassNotFoundException: " + JDBC_DRIVER);
			e.printStackTrace();
		} catch (SQLException e) {
			System.out.println("StreamingQuoteDAOModeQuote.initializeJDBCConn(): SQLException on getConnection");
			e.printStackTrace();
		}
	}

	/**
	 * closeJDBCConn - method to close JDBC connection
	 */
	@Override
	public void closeJDBCConn() {
		if (conn != null) {
			try {
				System.out.println("StreamingQuoteDAOModeQuote.closeJDBCConn(): Closing JDBC connection for Streaming Quote...");
				conn.close();
			} catch (SQLException e) {
				System.out.println("StreamingQuoteDAOModeQuote.closeJDBCConn(): SQLException on conn close");
				e.printStackTrace();
			}
		} else{
			System.out.println("StreamingQuoteDAOModeQuote.closeJDBCConn(): WARNING: DB connection already null");
		}
	}
	
	/**
	 * createDaysStreamingQuoteTable - method to create streaming quote table for the day
	 * @param date
	 */
	@Override
	public void createDaysStreamingQuoteTable(String date){
		if (conn != null) {
			Statement stmt;
			try {
				stmt = conn.createStatement();
				quoteTable = ZStreamingConfig.getStreamingQuoteTbNameAppendFormat(date);
				String sql = "CREATE TABLE " + quoteTable + " " + 
		                "(Time time NOT NULL, " +
		                " InstrumentToken varchar(32) NOT NULL, " + 
		                " LastTradedPrice DECIMAL(20,4) NOT NULL, " + 
		                " LastTradedQty BIGINT NOT NULL, " + 
		                " AvgTradedPrice DECIMAL(20,4) NOT NULL, " + 
		                " Volume BIGINT NOT NULL, " + 
		                " BuyQty BIGINT NOT NULL, " + 
		                " SellQty BIGINT NOT NULL, " + 
		                " OpenPrice DECIMAL(20,4) NOT NULL, " + 
		                " HighPrice DECIMAL(20,4) NOT NULL, " + 
		                " LowPrice DECIMAL(20,4) NOT NULL, " + 
		                " ClosePrice DECIMAL(20,4) NOT NULL, " + 
		                " PRIMARY KEY (InstrumentToken, Time)) " + 
		                " ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;";
				stmt.executeUpdate(sql);
				System.out.println("StreamingQuoteDAOModeQuote.createDaysStreamingQuoteTable(): SQL table for Streaming quote created, table name: [" + quoteTable + "]");
			} catch (SQLException e) {
				System.out.println("StreamingQuoteDAOModeQuote.createDaysStreamingQuoteTable(): ERROR: SQLException on creating Table, cause: " + e.getMessage());
			}
		} else{
			System.out.println("StreamingQuoteDAOModeQuote.createDaysStreamingQuoteTable(): ERROR: DB conn is null !!!");
		}
	}
	
	/**
	 * storeData - method to update the quote data
	 * @param quote
	 */
	@Override
	public void storeData(StreamingQuote quote){
		if (conn != null && quote instanceof StreamingQuoteModeQuote) {
			StreamingQuoteModeQuote quoteModeQuote = (StreamingQuoteModeQuote)quote;
			
			try {
				//SQL query
				String sql = "INSERT INTO " + quoteTable + " " + 
							"(Time, InstrumentToken, LastTradedPrice, LastTradedQty, AvgTradedPrice, " + 
							"Volume, BuyQty, SellQty, OpenPrice, HighPrice, LowPrice, ClosePrice) " + 
							"values(?,?,?,?,?,?,?,?,?,?,?,?)";
				PreparedStatement prepStmt = conn.prepareStatement(sql);
				
				//prepare statement
				prepStmt.setString(1, quoteModeQuote.getTime());
				prepStmt.setString(2, quoteModeQuote.getInstrumentToken());
				prepStmt.setBigDecimal(3, quoteModeQuote.getLtp());
				prepStmt.setLong(4, quoteModeQuote.getLastTradedQty());
				prepStmt.setBigDecimal(5, quoteModeQuote.getAvgTradedPrice());
				prepStmt.setLong(6, quoteModeQuote.getVol());
				prepStmt.setLong(7, quoteModeQuote.getBuyQty());
				prepStmt.setLong(8, quoteModeQuote.getSellQty());
				prepStmt.setBigDecimal(9, quoteModeQuote.getOpenPrice());
				prepStmt.setBigDecimal(10, quoteModeQuote.getHighPrice());
				prepStmt.setBigDecimal(11, quoteModeQuote.getLowPrice());
				prepStmt.setBigDecimal(12, quoteModeQuote.getClosePrice());
				
				prepStmt.executeUpdate();
				prepStmt.close();
			} catch (SQLException e) {
				System.out.println("StreamingQuoteDAOModeQuote.storeData(): ERROR: SQLException on Storing data to Table: " + quote);
				System.out.println("StreamingQuoteDAOModeQuote.storeData(): [SQLException Cause]: " + e.getMessage());
				//e.printStackTrace();
			}
		} else{
			if(conn != null){
				System.out.println("StreamingQuoteDAOModeQuote.storeData(): ERROR: DB conn is null !!!");
			} else{
				System.out.println("StreamingQuoteDAOModeQuote.storeData(): ERROR: quote is not of type StreamingQuoteModeQuote !!!");
			}
		}
	}
	
	/**
	 * getOHLCDataByTimeRange - OHLC values between two time
	 * @param instrumentToken
	 * @param prevTime
	 * @param currTime
	 * @return OHLC quote
	 */
	@Override
	public OHLCquote getOHLCDataByTimeRange(String instrumentToken, String prevTime, String currTime){
		OHLCquote ohlcMap = null;
		
		if (conn != null) {
			try {
				Statement stmt = conn.createStatement();
				
				//SQL query OPEN
				String openSql = "SELECT LastTradedPrice FROM " + quoteTable + 
						" WHERE Time >= '" + prevTime +
						"' AND Time <= '" + currTime + 
						"' AND InstrumentToken = '" + instrumentToken + 
						"' ORDER BY Time ASC LIMIT 1";
				ResultSet openRs = stmt.executeQuery(openSql);
				openRs.next();
				BigDecimal openQuote = openRs.getBigDecimal("LastTradedPrice");
				//System.out.println("OPEN: " + openQuote);
				
				//SQL query HIGH
				String highSql = "SELECT MAX(LastTradedPrice) FROM " + quoteTable + 
						" WHERE Time >= '" + prevTime +
						"' AND Time <= '" + currTime + 
						"' AND InstrumentToken = '" + instrumentToken + "'";
				ResultSet highRs = stmt.executeQuery(highSql);
				highRs.next();
				BigDecimal highQuote = highRs.getBigDecimal(1);
				//System.out.println("HIGH: " + highQuote);
				
				//SQL query LOW
				String lowSql = "SELECT MIN(LastTradedPrice) FROM " + quoteTable + 
						" WHERE Time >= '" + prevTime +
						"' AND Time <= '" + currTime + 
						"' AND InstrumentToken = '" + instrumentToken + "'";
				ResultSet lowRs = stmt.executeQuery(lowSql);
				lowRs.next();
				BigDecimal lowQuote = lowRs.getBigDecimal(1);
				//System.out.println("LOW: " + lowQuote);
				
				//SQL query CLOSE
				String closeSql = "SELECT LastTradedPrice FROM " + quoteTable + 
						" WHERE Time >= '" + prevTime +
						"' AND Time <= '" + currTime + 
						"' AND InstrumentToken = '" + instrumentToken + 
						"' ORDER BY Time DESC LIMIT 1";
				ResultSet closeRs = stmt.executeQuery(closeSql);
				closeRs.next();
				BigDecimal closeQuote = closeRs.getBigDecimal("LastTradedPrice");
				//System.out.println("CLOSE: " + closeQuote);
				
				//SQL query VOL
				String volSql = "SELECT Volume FROM " + quoteTable + 
						" WHERE Time >= '" + prevTime +
						"' AND Time <= '" + currTime + 
						"' AND InstrumentToken = '" + instrumentToken + 
						"' ORDER BY Time DESC LIMIT 1";
				ResultSet volRs = stmt.executeQuery(volSql);
				volRs.next();
				Long volQuote = volRs.getLong(1);
				//System.out.println("VOL: " + volQuote);
				
				ohlcMap = new OHLCquote(openQuote, highQuote, lowQuote, closeQuote, volQuote);
				
				stmt.close();
			} catch (SQLException e) {
				ohlcMap = null;
				System.out.println("StreamingQuoteDAOModeQuote.getOHLCDataByTimeRange(): ERROR: SQLException on fetching data from Table, cause: " + e.getMessage());
				// e.printStackTrace();
			}
		} else{
			ohlcMap = null;
			System.out.println("StreamingQuoteDAOModeQuote.getOHLCDataByTimeRange(): ERROR: DB conn is null !!!");
		}
		
		return ohlcMap;
	}
	
	/**
	 * getQuoteByTimeRange - Quote Mode Quote data between two time
	 * @param instrumentToken
	 * @param prevTime
	 * @param currTime
	 * @return StreamingQuote list
	 */
	@Override
	public List<StreamingQuote> getQuoteListByTimeRange(String instrumentToken, String prevTime, String currTime){
		List<StreamingQuote> streamingQuoteList = new ArrayList<StreamingQuote>();
		
		if (conn != null) {
			try {
				Statement stmt = conn.createStatement();
				
				//SQL query LTP Quote Data
				String openSql = "SELECT * FROM " + quoteTable + 
						" WHERE Time >= '" + prevTime +
						"' AND Time <= '" + currTime + 
						"' AND InstrumentToken = '" + instrumentToken + "'";
				ResultSet openRs = stmt.executeQuery(openSql);
				while(openRs.next()){
					String time = openRs.getString("Time");
					String instrument_Token = openRs.getString("InstrumentToken");
					BigDecimal lastTradedPrice = openRs.getBigDecimal("LastTradedPrice");					
					Long lastTradedQty = openRs.getLong("LastTradedQty");
					BigDecimal avgTradedPrice = openRs.getBigDecimal("AvgTradedPrice");
					Long volume = openRs.getLong("Volume");
					Long buyQty = openRs.getLong("BuyQty");
					Long sellQty = openRs.getLong("SellQty");
					BigDecimal openPrice = openRs.getBigDecimal("OpenPrice");
					BigDecimal highPrice = openRs.getBigDecimal("HighPrice");
					BigDecimal lowPrice = openRs.getBigDecimal("LowPrice");
					BigDecimal closePrice = openRs.getBigDecimal("ClosePrice");
									
					StreamingQuote streamingQuote = new StreamingQuoteModeQuote(time, instrument_Token, lastTradedPrice,
							lastTradedQty, avgTradedPrice, volume, buyQty, sellQty, openPrice, highPrice, lowPrice, closePrice);
					//System.out.println("Quote: " + streamingQuote);
					streamingQuoteList.add(streamingQuote);
				}
				
				stmt.close();
			} catch (SQLException e) {
				streamingQuoteList = null;
				System.out.println("StreamingQuoteDAOModeQuote.getQuoteByTimeRange(): ERROR: SQLException on fetching data from Table, cause: " + e.getMessage());
				// e.printStackTrace();
			}
		} else{
			streamingQuoteList = null;
			System.out.println("StreamingQuoteDAOModeQuote.getQuoteByTimeRange(): ERROR: DB conn is null !!!");
		}
		
		return streamingQuoteList;
	}
}
