package com.ritesh.zstreamingquote.quoteparser;

import com.ritesh.zstreamingquote.config.ZStreamingConfig;

public class StreamingQuoteParserFactory {

	/**
	 * getStreamingQuoteParser - StreamingQuoteParser Instance provider factory
	 * @return StreamingQuoteParser Instance
	 */
	public static IStreamingQuoteParser getStreamingQuoteParser(){
		IStreamingQuoteParser streamingQuoteParser = null;
		
		if(ZStreamingConfig.getStreamingQuoteMode().equals(ZStreamingConfig.QUOTE_STREAMING_MODE_LTP)){
			streamingQuoteParser = new StreamingQuoteParserModeLtp();
		} else if(ZStreamingConfig.getStreamingQuoteMode().equals(ZStreamingConfig.QUOTE_STREAMING_MODE_QUOTE)){
			streamingQuoteParser = new StreamingQuoteParserModeQuote();
		} else if(ZStreamingConfig.getStreamingQuoteMode().equals(ZStreamingConfig.QUOTE_STREAMING_MODE_FULL)){
			streamingQuoteParser = new StreamingQuoteParserModeFull();
		} else{
			System.out.println("StreamingQuoteParserFactory.getStreamingQuoteParser(): ERROR: " + 
					"Current Parsing Strategy not supported for Quote type [" + ZStreamingConfig.getStreamingQuoteMode() + "]");
		}
		
		return streamingQuoteParser;
	}
}
