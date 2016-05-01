package com.ritesh.zstreamingquote.quoteparser;

import java.nio.ByteBuffer;

import com.ritesh.zstreamingquote.quote.StreamingQuote;

public interface IStreamingQuoteParser {

	public StreamingQuote parse(ByteBuffer pktBuffer, String time);
}
