package com.ritesh.zstreamingquote.db;

import com.ritesh.zstreamingquote.config.ZStreamingConfig;

public class StreamingQuoteStorageFactory {
	
	/**
	 * getStreamingQuoteStorage - StreamingQuoteStorage Instance provider factory
	 * @return StreamingQuoteStorage Instance
	 */
	public static IStreamingQuoteStorage getStreamingQuoteStorage(){
		IStreamingQuoteStorage streamingQuoteStorage = null;
		
		if(ZStreamingConfig.getStreamingQuoteMode().equals(ZStreamingConfig.QUOTE_STREAMING_MODE_LTP)){
			streamingQuoteStorage = new StreamingQuoteDAOModeLtp();
		} else if(ZStreamingConfig.getStreamingQuoteMode().equals(ZStreamingConfig.QUOTE_STREAMING_MODE_QUOTE)){
			streamingQuoteStorage = new StreamingQuoteDAOModeQuote();
		} else if(ZStreamingConfig.getStreamingQuoteMode().equals(ZStreamingConfig.QUOTE_STREAMING_MODE_FULL)){
			streamingQuoteStorage = new StreamingQuoteDAOModeFull();
		} else{
			System.out.println("StreamingQuoteStorageFactory.getStreamingQuoteStorage(): ERROR: " + 
					"Current DB storage type not supported for Quote type [" + ZStreamingConfig.getStreamingQuoteMode() + "]");
		}
		
		return streamingQuoteStorage;
	}
}
