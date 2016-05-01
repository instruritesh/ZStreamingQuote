package com.ritesh.zstreamingquote.app;

import com.ritesh.zstreamingquote.config.ZStreamingConfig;
import com.ritesh.zstreamingquote.control.TradingHolidays;
import com.ritesh.zstreamingquote.control.ZStreamingQuoteControl;

public class TestApp {
	public static void main(String[] args) {
//		 String apiKey = "zac2flvsbza9cge4";
//		 String userId = "DR2062";
//		 String publicToken = "bb20b54f2d9a0b6efa8d879e80a024dd";

		String apiKey = "abcd51hdgns";
		String userId = "DR1234";
		String publicToken = "asljfldlncnl093nnnzc4";

		if (!TradingHolidays.isHoliday()) {
			// Start Only when Its not a trading holiday
			ZStreamingQuoteControl.getInstance().start(apiKey, userId, publicToken);

			// Time Range and Instrument Tokens
			String fromTime = "17:00:00";
			String toTime = "19:00:00";
			String instrumentToken1 = ZStreamingConfig.getInstrumentTokenArr()[0];
			String instrumentToken2 = ZStreamingConfig.getInstrumentTokenArr()[1];

			// Sleep for 10 seconds for testing
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// OHLC Data Print
			System.out.println("\nTestApp.main(): Instrument [" + instrumentToken1 + "] OHLC Data between Time Range ["
					+ fromTime + "] : [" + toTime + "] ::");
			System.out.println(
					ZStreamingQuoteControl.getInstance().getOHLCDataByTimeRange(instrumentToken1, fromTime, toTime)
							+ "\n");
			System.out.println("\nTestApp.main(): Instrument [" + instrumentToken2 + "] OHLC Data between Time Range ["
					+ fromTime + "] : [" + toTime + "] ::");
			System.out.println(
					ZStreamingQuoteControl.getInstance().getOHLCDataByTimeRange(instrumentToken2, fromTime, toTime)
							+ "\n");

			// Streamed Data Print
			System.out.println("\nTestApp.main(): Instrument [" + instrumentToken1
					+ "] Streamed Data between Time Range [" + fromTime + "] : [" + toTime + "] ::");
			System.out.println(
					ZStreamingQuoteControl.getInstance().getQuoteListByTimeRange(instrumentToken1, fromTime, toTime)
							+ "\n");
			System.out.println("\nTestApp.main(): Instrument [" + instrumentToken2
					+ "] Streamed Data between Time Range [" + fromTime + "] : [" + toTime + "] ::");
			System.out.println(
					ZStreamingQuoteControl.getInstance().getQuoteListByTimeRange(instrumentToken2, fromTime, toTime)
							+ "\n");

			// Sleep for 10 seconds for testing
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// Stop
			ZStreamingQuoteControl.getInstance().stop();
		}
	}
}
