package com.ritesh.zstreamingquote.quoteparser;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.nio.ByteBuffer;

import com.ritesh.zstreamingquote.quote.StreamingQuote;
import com.ritesh.zstreamingquote.quote.mode.StreamingQuoteModeQuote;

public class StreamingQuoteParserModeQuote implements IStreamingQuoteParser{

	@Override
	public StreamingQuote parse(ByteBuffer pktBuffer, String time) {
		int instrumentToken = pktBuffer.getInt();
		int ltp = pktBuffer.getInt();
		int lastTradedQty = pktBuffer.getInt();
		int avgTradedPrice = pktBuffer.getInt();
		int vol = pktBuffer.getInt();
		int buyQty = pktBuffer.getInt();
		int sellQty = pktBuffer.getInt();
		int openPrice = pktBuffer.getInt();
		int highPrice = pktBuffer.getInt();
		int lowPrice = pktBuffer.getInt();
		int closePrice = pktBuffer.getInt();
		
		StreamingQuote streamingQuote = new StreamingQuoteModeQuote(time, convertIntToIntString(instrumentToken), 
				convertIntToBigDecimal(ltp), convertIntToLong(lastTradedQty),
				convertIntToBigDecimal(avgTradedPrice), convertIntToLong(vol),
				convertIntToLong(buyQty), convertIntToLong(sellQty),
				convertIntToBigDecimal(openPrice), convertIntToBigDecimal(highPrice),
				convertIntToBigDecimal(lowPrice), convertIntToBigDecimal(closePrice));
		
		return streamingQuote;
	}
	
	/**
	 * convertIntToIntString - private method to convert Integer to Integer
	 * String
	 * 
	 * @param quoteParam
	 * @return Integer String
	 */
	private String convertIntToIntString(int quoteParam) {
		String quoteParamString = (new Integer(quoteParam)).toString();
		return quoteParamString;
	}

	/**
	 * convertIntToBigDecimal - private method to convert int to BigDecimal
	 * 
	 * @param quoteParam
	 * @return BigDecimal value
	 */
	private BigDecimal convertIntToBigDecimal(int quoteParam) {
		BigDecimal quoteParamBigDecimal = new BigDecimal(quoteParam).divide(new BigDecimal(100),
				new MathContext(8, RoundingMode.HALF_DOWN));
		return quoteParamBigDecimal;
	}
	
	/**
	 * convertIntToLong - private method to convert int to Long
	 * 
	 * @param quoteParam
	 * @return Long value
	 */
	private Long convertIntToLong(int quoteParam) {
		Long quoteParamLong = new Long(convertIntToIntString(quoteParam));
		return quoteParamLong;
	}
}
