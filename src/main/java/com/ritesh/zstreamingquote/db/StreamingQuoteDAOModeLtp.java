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
import com.ritesh.zstreamingquote.quote.mode.StreamingQuoteModeLtp;

public class StreamingQuoteDAOModeLtp implements IStreamingQuoteStorage{
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
	public StreamingQuoteDAOModeLtp(){
		//
	}
	
	/**
	 * initializeJDBCConn - method to create and initialize JDBC connection
	 */
	@Override
	public void initializeJDBCConn() {
		try {
			System.out.println("StreamingQuoteDAOModeLtp.initializeJDBCConn(): creating JDBC connection for Streaming Quote...");
			// Register JDBC driver
			Class.forName(JDBC_DRIVER);
			// Open the connection
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
		} catch (ClassNotFoundException e) {
			System.out.println("StreamingQuoteDAOModeLtp.initializeJDBCConn(): ClassNotFoundException: " + JDBC_DRIVER);
			e.printStackTrace();
		} catch (SQLException e) {
			System.out.println("StreamingQuoteDAOModeLtp.initializeJDBCConn(): SQLException on getConnection");
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
				System.out.println("StreamingQuoteDAOModeLtp.closeJDBCConn(): Closing JDBC connection for Streaming Quote...");
				conn.close();
			} catch (SQLException e) {
				System.out.println("StreamingQuoteDAOModeLtp.closeJDBCConn(): SQLException on conn close");
				e.printStackTrace();
			}
		} else{
			System.out.println("StreamingQuoteDAOModeLtp.closeJDBCConn(): WARNING: DB connection already null");
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
		                " PRIMARY KEY (InstrumentToken, Time)) " + 
		                " ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;";
				stmt.executeUpdate(sql);
				System.out.println("StreamingQuoteDAOModeLtp.createDaysStreamingQuoteTable(): SQL table for Streaming quote created, table name: [" + quoteTable + "]");
			} catch (SQLException e) {
				System.out.println("StreamingQuoteDAOModeLtp.createDaysStreamingQuoteTable(): ERROR: SQLException on creating Table, cause: " + e.getMessage());
			}
		} else{
			System.out.println("StreamingQuoteDAOModeLtp.createDaysStreamingQuoteTable(): ERROR: DB conn is null !!!");
		}
	}
	
	/**
	 * storeData - method to update the quote data
	 * @param quote
	 */
	@Override
	public void storeData(StreamingQuote quote){
		if (conn != null && quote instanceof StreamingQuoteModeLtp) {
			StreamingQuoteModeLtp quoteModeLtp = (StreamingQuoteModeLtp)quote;
			
			try {
				//SQL query
				String sql = "INSERT INTO " + quoteTable + " " + 
							"(Time, InstrumentToken, LastTradedPrice) " + 
							"values(?,?,?)";
				PreparedStatement prepStmt = conn.prepareStatement(sql);
				
				//prepare statement
				prepStmt.setString(1, quoteModeLtp.getTime());
				prepStmt.setString(2, quoteModeLtp.getInstrumentToken());
				prepStmt.setBigDecimal(3, quoteModeLtp.getLtp());
				
				prepStmt.executeUpdate();
				prepStmt.close();
			} catch (SQLException e) {
				System.out.println("StreamingQuoteDAOModeLtp.storeData(): ERROR: SQLException on Storing data to Table: " + quote);
				System.out.println("StreamingQuoteDAOModeLtp.storeData(): [SQLException Cause]: " + e.getMessage());
				//e.printStackTrace();
			}
		} else{
			if(conn != null){
				System.out.println("StreamingQuoteDAOModeLtp.storeData(): ERROR: DB conn is null !!!");
			} else{
				System.out.println("StreamingQuoteDAOModeLtp.storeData(): ERROR: quote is not of type StreamingQuoteModeLtp !!!");
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
				
				ohlcMap = new OHLCquote(openQuote, highQuote, lowQuote, closeQuote, null);
				
				stmt.close();
			} catch (SQLException e) {
				ohlcMap = null;
				System.out.println("StreamingQuoteDAOModeLtp.getOHLCDataByTimeRange(): ERROR: SQLException on fetching data from Table, cause: " + e.getMessage());
				//e.printStackTrace();
			}
		} else{
			ohlcMap = null;
			System.out.println("StreamingQuoteDAOModeLtp.getOHLCDataByTimeRange(): ERROR: DB conn is null !!!");
		}
		
		return ohlcMap;
	}
	
	/**
	 * getQuoteByTimeRange - LTP Mode Quote data between two time
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
									
					StreamingQuote streamingQuote = new StreamingQuoteModeLtp(time, instrument_Token, lastTradedPrice);
					//System.out.println("Quote: " + streamingQuote);
					streamingQuoteList.add(streamingQuote);
				}
				
				stmt.close();
			} catch (SQLException e) {
				streamingQuoteList = null;
				System.out.println("StreamingQuoteDAOModeLtp.getQuoteByTimeRange(): ERROR: SQLException on fetching data from Table, cause: " + e.getMessage());
				//e.printStackTrace();
			}
		} else{
			streamingQuoteList = null;
			System.out.println("StreamingQuoteDAOModeLtp.getQuoteByTimeRange(): ERROR: DB conn is null !!!");
		}
		
		return streamingQuoteList;
	}
}
