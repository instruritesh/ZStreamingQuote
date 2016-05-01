package com.ritesh.zstreamingquote.quoteparser;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.nio.ByteBuffer;

import com.ritesh.zstreamingquote.quote.StreamingQuote;
import com.ritesh.zstreamingquote.quote.mode.StreamingQuoteModeLtp;

public class StreamingQuoteParserModeLtp implements IStreamingQuoteParser {

	@Override
	public StreamingQuote parse(ByteBuffer pktBuffer, String time) {
		int instrumentToken = pktBuffer.getInt();
		int ltp = pktBuffer.getInt();

		StreamingQuote streamingQuote = new StreamingQuoteModeLtp(time, convertIntToIntString(instrumentToken),
				convertIntToBigDecimal(ltp));

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
}
