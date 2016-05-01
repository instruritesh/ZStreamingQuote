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
import com.ritesh.zstreamingquote.quote.MarketDepth;
import com.ritesh.zstreamingquote.quote.OHLCquote;
import com.ritesh.zstreamingquote.quote.StreamingQuote;
import com.ritesh.zstreamingquote.quote.mode.StreamingQuoteModeFull;

public class StreamingQuoteDAOModeFull implements IStreamingQuoteStorage{
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
	public StreamingQuoteDAOModeFull(){
		//
	}
	
	/**
	 * initializeJDBCConn - method to create and initialize JDBC connection
	 */
	@Override
	public void initializeJDBCConn() {
		try {
			System.out.println("StreamingQuoteDAOModeFull.initializeJDBCConn(): creating JDBC connection for Streaming Quote...");
			// Register JDBC driver
			Class.forName(JDBC_DRIVER);
			// Open the connection
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
		} catch (ClassNotFoundException e) {
			System.out.println("StreamingQuoteDAOModeFull.initializeJDBCConn(): ClassNotFoundException: " + JDBC_DRIVER);
			e.printStackTrace();
		} catch (SQLException e) {
			System.out.println("StreamingQuoteDAOModeFull.initializeJDBCConn(): SQLException on getConnection");
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
				System.out.println("StreamingQuoteDAOModeFull.closeJDBCConn(): Closing JDBC connection for Streaming Quote...");
				conn.close();
			} catch (SQLException e) {
				System.out.println("StreamingQuoteDAOModeFull.closeJDBCConn(): SQLException on conn close");
				e.printStackTrace();
			}
		} else{
			System.out.println("StreamingQuoteDAOModeFull.closeJDBCConn(): WARNING: DB connection already null");
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
		                " MarketDepthBid1Qty BIGINT NOT NULL, " +
		                " MarketDepthBid1Price DECIMAL(20,4) NOT NULL, " +
		                " MarketDepthBid1Orders INTEGER NOT NULL, " +
		                " MarketDepthBid2Qty BIGINT NOT NULL, " +
		                " MarketDepthBid2Price DECIMAL(20,4) NOT NULL, " +
		                " MarketDepthBid2Orders INTEGER NOT NULL, " +
		                " MarketDepthBid3Qty BIGINT NOT NULL, " +
		                " MarketDepthBid3Price DECIMAL(20,4) NOT NULL, " +
		                " MarketDepthBid3Orders INTEGER NOT NULL, " +
		                " MarketDepthBid4Qty BIGINT NOT NULL, " +
		                " MarketDepthBid4Price DECIMAL(20,4) NOT NULL, " +
		                " MarketDepthBid4Orders INTEGER NOT NULL, " +
		                " MarketDepthBid5Qty BIGINT NOT NULL, " +
		                " MarketDepthBid5Price DECIMAL(20,4) NOT NULL, " +
		                " MarketDepthBid5Orders INTEGER NOT NULL, " +		                
		                " MarketDepthOffer1Qty BIGINT NOT NULL, " +
		                " MarketDepthOffer1Price DECIMAL(20,4) NOT NULL, " +
		                " MarketDepthOffer1Orders INTEGER NOT NULL, " +
		                " MarketDepthOffer2Qty BIGINT NOT NULL, " +
		                " MarketDepthOffer2Price DECIMAL(20,4) NOT NULL, " +
		                " MarketDepthOffer2Orders INTEGER NOT NULL, " +
		                " MarketDepthOffer3Qty BIGINT NOT NULL, " +
		                " MarketDepthOffer3Price DECIMAL(20,4) NOT NULL, " +
		                " MarketDepthOffer3Orders INTEGER NOT NULL, " +
		                " MarketDepthOffer4Qty BIGINT NOT NULL, " +
		                " MarketDepthOffer4Price DECIMAL(20,4) NOT NULL, " +
		                " MarketDepthOffer4Orders INTEGER NOT NULL, " +
		                " MarketDepthOffer5Qty BIGINT NOT NULL, " +
		                " MarketDepthOffer5Price DECIMAL(20,4) NOT NULL, " +
		                " MarketDepthOffer5Orders INTEGER NOT NULL, " +		                
		                " PRIMARY KEY (InstrumentToken, Time)) " + 
		                " ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;";
				stmt.executeUpdate(sql);
				System.out.println("StreamingQuoteDAOModeFull.createDaysStreamingQuoteTable(): SQL table for Streaming quote created, table name: [" + quoteTable + "]");
			} catch (SQLException e) {
				System.out.println("StreamingQuoteDAOModeFull.createDaysStreamingQuoteTable(): ERROR: SQLException on creating Table, cause: " + e.getMessage());
			}
		} else{
			System.out.println("StreamingQuoteDAOModeFull.createDaysStreamingQuoteTable(): ERROR: DB conn is null !!!");
		}
	}
	
	/**
	 * storeData - method to update the quote data
	 * @param quote
	 */
	@Override
	public void storeData(StreamingQuote quote){
		if (conn != null && quote instanceof StreamingQuoteModeFull) {
			StreamingQuoteModeFull quoteModeFull = (StreamingQuoteModeFull)quote;
			
			try {
				//SQL query
				String sql = "INSERT INTO " + quoteTable + " " + 
							"(Time, InstrumentToken, LastTradedPrice, LastTradedQty, AvgTradedPrice, " + 
							"Volume, BuyQty, SellQty, OpenPrice, HighPrice, LowPrice, ClosePrice, " + 
							"MarketDepthBid1Qty, MarketDepthBid1Price, MarketDepthBid1Orders, " +
							"MarketDepthBid2Qty, MarketDepthBid2Price, MarketDepthBid2Orders, " +
							"MarketDepthBid3Qty, MarketDepthBid3Price, MarketDepthBid3Orders, " +
							"MarketDepthBid4Qty, MarketDepthBid4Price, MarketDepthBid4Orders, " +
							"MarketDepthBid5Qty, MarketDepthBid5Price, MarketDepthBid5Orders, " +
							"MarketDepthOffer1Qty, MarketDepthOffer1Price, MarketDepthOffer1Orders, " +
							"MarketDepthOffer2Qty, MarketDepthOffer2Price, MarketDepthOffer2Orders, " +
							"MarketDepthOffer3Qty, MarketDepthOffer3Price, MarketDepthOffer3Orders, " +
							"MarketDepthOffer4Qty, MarketDepthOffer4Price, MarketDepthOffer4Orders, " +
							"MarketDepthOffer5Qty, MarketDepthOffer5Price, MarketDepthOffer5Orders) " + 
							"values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				PreparedStatement prepStmt = conn.prepareStatement(sql);
				
				//prepare statement
				prepStmt.setString(1, quoteModeFull.getTime());
				prepStmt.setString(2, quoteModeFull.getInstrumentToken());
				prepStmt.setBigDecimal(3, quoteModeFull.getLtp());
				prepStmt.setLong(4, quoteModeFull.getLastTradedQty());
				prepStmt.setBigDecimal(5, quoteModeFull.getAvgTradedPrice());
				prepStmt.setLong(6, quoteModeFull.getVol());
				prepStmt.setLong(7, quoteModeFull.getBuyQty());
				prepStmt.setLong(8, quoteModeFull.getSellQty());
				prepStmt.setBigDecimal(9, quoteModeFull.getOpenPrice());
				prepStmt.setBigDecimal(10, quoteModeFull.getHighPrice());
				prepStmt.setBigDecimal(11, quoteModeFull.getLowPrice());
				prepStmt.setBigDecimal(12, quoteModeFull.getClosePrice());
				prepStmt.setLong(13, quoteModeFull.getBidEntry1().getQty());
				prepStmt.setBigDecimal(14, quoteModeFull.getBidEntry1().getPrice());
				prepStmt.setInt(15, quoteModeFull.getBidEntry1().getOrders());				
				prepStmt.setLong(16, quoteModeFull.getBidEntry2().getQty());
				prepStmt.setBigDecimal(17, quoteModeFull.getBidEntry2().getPrice());
				prepStmt.setInt(18, quoteModeFull.getBidEntry2().getOrders());				
				prepStmt.setLong(19, quoteModeFull.getBidEntry3().getQty());
				prepStmt.setBigDecimal(20, quoteModeFull.getBidEntry3().getPrice());
				prepStmt.setInt(21, quoteModeFull.getBidEntry3().getOrders());				
				prepStmt.setLong(22, quoteModeFull.getBidEntry4().getQty());
				prepStmt.setBigDecimal(23, quoteModeFull.getBidEntry4().getPrice());
				prepStmt.setInt(24, quoteModeFull.getBidEntry4().getOrders());				
				prepStmt.setLong(25, quoteModeFull.getBidEntry5().getQty());
				prepStmt.setBigDecimal(26, quoteModeFull.getBidEntry5().getPrice());
				prepStmt.setInt(27, quoteModeFull.getBidEntry5().getOrders());
				prepStmt.setLong(28, quoteModeFull.getOfferEntry1().getQty());
				prepStmt.setBigDecimal(29, quoteModeFull.getOfferEntry1().getPrice());
				prepStmt.setInt(30, quoteModeFull.getOfferEntry1().getOrders());				
				prepStmt.setLong(31, quoteModeFull.getOfferEntry2().getQty());
				prepStmt.setBigDecimal(32, quoteModeFull.getOfferEntry2().getPrice());
				prepStmt.setInt(33, quoteModeFull.getOfferEntry2().getOrders());				
				prepStmt.setLong(34, quoteModeFull.getOfferEntry3().getQty());
				prepStmt.setBigDecimal(35, quoteModeFull.getOfferEntry3().getPrice());
				prepStmt.setInt(36, quoteModeFull.getOfferEntry3().getOrders());				
				prepStmt.setLong(37, quoteModeFull.getOfferEntry4().getQty());
				prepStmt.setBigDecimal(38, quoteModeFull.getOfferEntry4().getPrice());
				prepStmt.setInt(39, quoteModeFull.getOfferEntry4().getOrders());				
				prepStmt.setLong(40, quoteModeFull.getOfferEntry5().getQty());
				prepStmt.setBigDecimal(41, quoteModeFull.getOfferEntry5().getPrice());
				prepStmt.setInt(42, quoteModeFull.getOfferEntry5().getOrders());				
				prepStmt.executeUpdate();
				prepStmt.close();
			} catch (SQLException e) {
				System.out.println("StreamingQuoteDAOModeFull.storeData(): ERROR: SQLException on Storing data to Table: " + quote);
				System.out.println("StreamingQuoteDAOModeFull.storeData(): [SQLException Cause]: " + e.getMessage());
				//e.printStackTrace();
			}
		} else{
			if(conn != null){
				System.out.println("StreamingQuoteDAOModeFull.storeData(): ERROR: DB conn is null !!!");
			} else{
				System.out.println("StreamingQuoteDAOModeFull.storeData(): ERROR: quote is not of type StreamingQuoteModeQuote !!!");
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
				System.out.println("StreamingQuoteDAOModeFull.getOHLCDataByTimeRange(): ERROR: SQLException on fetching data from Table, cause: " + e.getMessage());
				// e.printStackTrace();
			}
		} else{
			ohlcMap = null;
			System.out.println("StreamingQuoteDAOModeFull.getOHLCDataByTimeRange(): ERROR: DB conn is null !!!");
		}
		
		return ohlcMap;
	}
	
	/**
	 * getQuoteByTimeRange - Full Mode Quote data between two time
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
					
					//Market Depthh - Bid
					Long marketDepthBid1Qty = openRs.getLong("MarketDepthBid1Qty");
					BigDecimal marketDepthBid1Price = openRs.getBigDecimal("MarketDepthBid1Price");
					Integer marketDepthBid1Orders = openRs.getInt("MarketDepthBid1Orders");					
					Long marketDepthBid2Qty = openRs.getLong("MarketDepthBid2Qty");
					BigDecimal marketDepthBid2Price = openRs.getBigDecimal("MarketDepthBid2Price");
					Integer marketDepthBid2Orders = openRs.getInt("MarketDepthBid2Orders");					
					Long marketDepthBid3Qty = openRs.getLong("MarketDepthBid3Qty");
					BigDecimal marketDepthBid3Price = openRs.getBigDecimal("MarketDepthBid3Price");
					Integer marketDepthBid3Orders = openRs.getInt("MarketDepthBid3Orders");					
					Long marketDepthBid4Qty = openRs.getLong("MarketDepthBid4Qty");
					BigDecimal marketDepthBid4Price = openRs.getBigDecimal("MarketDepthBid4Price");
					Integer marketDepthBid4Orders = openRs.getInt("MarketDepthBid4Orders");					
					Long marketDepthBid5Qty = openRs.getLong("MarketDepthBid5Qty");
					BigDecimal marketDepthBid5Price = openRs.getBigDecimal("MarketDepthBid5Price");
					Integer marketDepthBid5Orders = openRs.getInt("MarketDepthBid5Orders");
					
					MarketDepth marketDepthBid1 = new MarketDepth(marketDepthBid1Qty, marketDepthBid1Price, marketDepthBid1Orders);
					MarketDepth marketDepthBid2 = new MarketDepth(marketDepthBid2Qty, marketDepthBid2Price, marketDepthBid2Orders);
					MarketDepth marketDepthBid3 = new MarketDepth(marketDepthBid3Qty, marketDepthBid3Price, marketDepthBid3Orders);
					MarketDepth marketDepthBid4 = new MarketDepth(marketDepthBid4Qty, marketDepthBid4Price, marketDepthBid4Orders);
					MarketDepth marketDepthBid5 = new MarketDepth(marketDepthBid5Qty, marketDepthBid5Price, marketDepthBid5Orders);
					
					//Market Depthh - Offer
					Long marketDepthOffer1Qty = openRs.getLong("MarketDepthOffer1Qty");
					BigDecimal marketDepthOffer1Price = openRs.getBigDecimal("MarketDepthOffer1Price");
					Integer marketDepthOffer1Orders = openRs.getInt("MarketDepthOffer1Orders");					
					Long marketDepthOffer2Qty = openRs.getLong("MarketDepthOffer2Qty");
					BigDecimal marketDepthOffer2Price = openRs.getBigDecimal("MarketDepthOffer2Price");
					Integer marketDepthOffer2Orders = openRs.getInt("MarketDepthOffer2Orders");					
					Long marketDepthOffer3Qty = openRs.getLong("MarketDepthOffer3Qty");
					BigDecimal marketDepthOffer3Price = openRs.getBigDecimal("MarketDepthOffer3Price");
					Integer marketDepthOffer3Orders = openRs.getInt("MarketDepthOffer3Orders");					
					Long marketDepthOffer4Qty = openRs.getLong("MarketDepthOffer4Qty");
					BigDecimal marketDepthOffer4Price = openRs.getBigDecimal("MarketDepthOffer4Price");
					Integer marketDepthOffer4Orders = openRs.getInt("MarketDepthOffer4Orders");					
					Long marketDepthOffer5Qty = openRs.getLong("MarketDepthOffer5Qty");
					BigDecimal marketDepthOffer5Price = openRs.getBigDecimal("MarketDepthOffer5Price");
					Integer marketDepthOffer5Orders = openRs.getInt("MarketDepthOffer5Orders");
					
					MarketDepth marketDepthOffer1 = new MarketDepth(marketDepthOffer1Qty, marketDepthOffer1Price, marketDepthOffer1Orders);
					MarketDepth marketDepthOffer2 = new MarketDepth(marketDepthOffer2Qty, marketDepthOffer2Price, marketDepthOffer2Orders);
					MarketDepth marketDepthOffer3 = new MarketDepth(marketDepthOffer3Qty, marketDepthOffer3Price, marketDepthOffer3Orders);
					MarketDepth marketDepthOffer4 = new MarketDepth(marketDepthOffer4Qty, marketDepthOffer4Price, marketDepthOffer4Orders);
					MarketDepth marketDepthOffer5 = new MarketDepth(marketDepthOffer5Qty, marketDepthOffer5Price, marketDepthOffer5Orders);
					
					StreamingQuote streamingQuote = new StreamingQuoteModeFull(time, instrument_Token, lastTradedPrice,
							lastTradedQty, avgTradedPrice, volume, buyQty, sellQty, openPrice, highPrice, lowPrice, closePrice,							
							marketDepthBid1, marketDepthBid2, marketDepthBid3, marketDepthBid4, marketDepthBid5,							
							marketDepthOffer1, marketDepthOffer2, marketDepthOffer3, marketDepthOffer4, marketDepthOffer5);
					//System.out.println("Quote: " + streamingQuote);
					streamingQuoteList.add(streamingQuote);
				}
				
				stmt.close();
			} catch (SQLException e) {
				streamingQuoteList = null;
				System.out.println("StreamingQuoteDAOModeFull.getQuoteByTimeRange(): ERROR: SQLException on fetching data from Table, cause: " + e.getMessage());
				// e.printStackTrace();
			}
		} else{
			streamingQuoteList = null;
			System.out.println("StreamingQuoteDAOModeFull.getQuoteByTimeRange(): ERROR: DB conn is null !!!");
		}
		
		return streamingQuoteList;
	}
}
