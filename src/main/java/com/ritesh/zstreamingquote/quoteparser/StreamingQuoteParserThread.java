package com.ritesh.zstreamingquote.quoteparser;

import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.BlockingQueue;

import com.ritesh.zstreamingquote.config.ZStreamingConfig;
import com.ritesh.zstreamingquote.db.IStreamingQuoteStorage;
import com.ritesh.zstreamingquote.quote.StreamingQuote;

public class StreamingQuoteParserThread implements Runnable{
	//Quote Buffer Queue
	private BlockingQueue<Object> quoteBufferQ = null;
	
	// running status
	private boolean runStatus = false;
	
	//Time formats
	private DateFormat timeFormat = null;
	private TimeZone timeZone = null;
	
	//Quote Parser
	private IStreamingQuoteParser streamingQuoteParser = null;
	
	//Quote Storage
  	private IStreamingQuoteStorage streamingQuoteStorage = null;
	
	/**
	 * constructor
	 * @param quoteBufferQ
	 * @param streamingQuoteStorage
	 */
	public StreamingQuoteParserThread(BlockingQueue<Object> quoteBufferQ, 
							IStreamingQuoteStorage streamingQuoteStorage){
		this.quoteBufferQ = quoteBufferQ;
		this.runStatus = true;
		
		//get quote parser
		streamingQuoteParser = StreamingQuoteParserFactory.getStreamingQuoteParser();
		if(streamingQuoteParser == null){
			System.out.println("StreamingQuoteParserThread.StreamingQuoteParserThread(): ERROR: Quote Parser is null... !!!");
		}
		
		if(ZStreamingConfig.isStreamingQuoteStoringRequired()){
			this.streamingQuoteStorage = streamingQuoteStorage;
		}
		
		timeFormat = new SimpleDateFormat("HH:mm:ss");
		timeZone = TimeZone.getTimeZone("IST");
		timeFormat.setTimeZone(timeZone);
	}
	
	@Override
	public void run() {
		while(runStatus){
			//listen for any quote buffer in Q
			try {
				Object bufferObj = quoteBufferQ.take();
				if(bufferObj instanceof ByteBuffer){
					String time = timeFormat.format(Calendar.getInstance(timeZone).getTime());
					ByteBuffer buffer = (ByteBuffer)bufferObj;
					try {
						parseBuffer(buffer, time);
					} catch (Exception e) {
						//If its an exception while parsing the data, we dont require this data
						System.out.println(
								"StreamingQuoteParserThread.run(): ERROR: Exception in parsing the Buffer, reason: "
										+ e.getMessage());
					}
				}
			} catch (InterruptedException e) {
				System.out.println("StreamingQuoteParserThread.run(): ERROR: InterruptedException on take from quoteBufferQ");
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * stopThread - method to stop the quote parser thread
	 */
	public void stopThread(){
		runStatus = false;
	}
	
	/**
	 * parseBuffer - private method to parse the bytebuffer recieved from WS
	 * @param buffer
	 * @param time
	 */
	private void parseBuffer(ByteBuffer buffer, String time){
		// start parse Buffer array
		int start = buffer.position();
		int buffLen = buffer.capacity();
		if (buffLen == 1) {
			// HeartBeat
			if(ZStreamingConfig.isHeartBitMsgPrintable()){
				System.out.println("StreamingQuoteParserThread.parseBuffer(): WS HEARTBIT Byte");
			}
		} else {
			// num of Packets
			int numPackets = buffer.getShort();
			if (numPackets == 0) {
				// Invalid Case: Zero Num of Packets - ignore
				System.out.println(
						"StreamingQuoteParserThread.parseBuffer(): ERROR: WS Byte numPackets is 0 in WS message, Ignoring !!!");
			} else {
				start += 2;
				//System.out.println("numPackets: " + numPackets);
				for (int i = 0; i < numPackets; i++) {
					// each packet
					//System.out.println("packet num: " + i);
					int numBytes = buffer.getShort();
					if (numBytes != 0) {
						// Valid Number of Bytes
						start += 2;
		
						// packet structure
						byte[] pkt = new byte[numBytes];
						buffer.get(pkt, 0, numBytes);
						ByteBuffer pktBuffer = ByteBuffer.wrap(pkt);
						if (pktBuffer != null) {
							//parse quote packet buffer
							parseQuotePktBuffer(pktBuffer, time);
							start += numBytes;
						} else {
							// Invalid Case: ByteBuffer could not wrap the bytes
							// - ignore
							System.out.println(
									"StreamingQuoteParserThread.parseBuffer(): ERROR: pktBuffer is null in WS message, Ignoring !!!");
						}
					} else {
						// Invalid Case: Zero Num of Bytes in packet - ignore
						System.out.println("StreamingQuoteParserThread.parseBuffer(): ERROR: numBytes is 0 in WS message packet[" + i + "], Ignoring !!!");
					}
				}
			}
		}
	}
	
	/**
	 * parseQuotePktBuffer - private method to parse the Quote packet buffer
	 * @param pktBuffer
	 * @param time
	 */
	private void parseQuotePktBuffer(ByteBuffer pktBuffer, String time){
		StreamingQuote streamingQuote = null;
		
		if(streamingQuoteParser != null){
			streamingQuote = streamingQuoteParser.parse(pktBuffer, time);
		}
		
		if (ZStreamingConfig.isStreamingQuoteStoringRequired() && (streamingQuoteStorage != null)
				&& streamingQuote != null) {
			// store streaming quote data
			streamingQuoteStorage.storeData(streamingQuote);
		}
		
		if(ZStreamingConfig.isQuoteMsgPrintable()){
			System.out.println("Quote: " + streamingQuote);
		}
	}
}
