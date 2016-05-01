package com.ritesh.zstreamingquote.quote.mode;

import java.math.BigDecimal;

import com.ritesh.zstreamingquote.quote.MarketDepth;

public class StreamingQuoteModeFull extends StreamingQuoteModeQuote{
	public MarketDepth bidEntry1;
	public MarketDepth bidEntry2;
	public MarketDepth bidEntry3;
	public MarketDepth bidEntry4;
	public MarketDepth bidEntry5;
	public MarketDepth offerEntry1;
	public MarketDepth offerEntry2;
	public MarketDepth offerEntry3;
	public MarketDepth offerEntry4;
	public MarketDepth offerEntry5;
	
	/**
	 * Constructor
	 * @param time
	 * @param instrumentToken
	 * @param ltp
	 * @param lastTradedQty
	 * @param avgTradedPrice
	 * @param vol
	 * @param buyQty
	 * @param sellQty
	 * @param openPrice
	 * @param highPrice
	 * @param lowPrice
	 * @param closePrice
	 * @param bidEntry1
	 * @param bidEntry2
	 * @param bidEntry3
	 * @param bidEntry4
	 * @param bidEntry5
	 * @param offerEntry1
	 * @param offerEntry2
	 * @param offerEntry3
	 * @param offerEntry4
	 * @param offerEntry5
	 */
	public StreamingQuoteModeFull(String time, String instrumentToken, BigDecimal ltp, Long lastTradedQty,
			BigDecimal avgTradedPrice, Long vol, Long buyQty, Long sellQty, BigDecimal openPrice, BigDecimal highPrice,
			BigDecimal lowPrice, BigDecimal closePrice, MarketDepth bidEntry1, MarketDepth bidEntry2, MarketDepth bidEntry3,
			MarketDepth bidEntry4, MarketDepth bidEntry5, MarketDepth offerEntry1, MarketDepth offerEntry2,
			MarketDepth offerEntry3, MarketDepth offerEntry4, MarketDepth offerEntry5) {
		super(time, instrumentToken, ltp, lastTradedQty, avgTradedPrice, vol, buyQty, sellQty, openPrice, highPrice,
				lowPrice, closePrice);
		this.bidEntry1 = bidEntry1;
		this.bidEntry2 = bidEntry2;
		this.bidEntry3 = bidEntry3;
		this.bidEntry4 = bidEntry4;
		this.bidEntry5 = bidEntry5;
		this.offerEntry1 = offerEntry1;
		this.offerEntry2 = offerEntry2;
		this.offerEntry3 = offerEntry3;
		this.offerEntry4 = offerEntry4;
		this.offerEntry5 = offerEntry5;
	}

	public MarketDepth getBidEntry1() {
		return bidEntry1;
	}
	public void setBidEntry1(MarketDepth bidEntry1) {
		this.bidEntry1 = bidEntry1;
	}
	public MarketDepth getBidEntry2() {
		return bidEntry2;
	}
	public void setBidEntry2(MarketDepth bidEntry2) {
		this.bidEntry2 = bidEntry2;
	}
	public MarketDepth getBidEntry3() {
		return bidEntry3;
	}
	public void setBidEntry3(MarketDepth bidEntry3) {
		this.bidEntry3 = bidEntry3;
	}
	public MarketDepth getBidEntry4() {
		return bidEntry4;
	}
	public void setBidEntry4(MarketDepth bidEntry4) {
		this.bidEntry4 = bidEntry4;
	}
	public MarketDepth getBidEntry5() {
		return bidEntry5;
	}
	public void setBidEntry5(MarketDepth bidEntry5) {
		this.bidEntry5 = bidEntry5;
	}
	public MarketDepth getOfferEntry1() {
		return offerEntry1;
	}
	public void setOfferEntry1(MarketDepth offerEntry1) {
		this.offerEntry1 = offerEntry1;
	}
	public MarketDepth getOfferEntry2() {
		return offerEntry2;
	}
	public void setOfferEntry2(MarketDepth offerEntry2) {
		this.offerEntry2 = offerEntry2;
	}
	public MarketDepth getOfferEntry3() {
		return offerEntry3;
	}
	public void setOfferEntry3(MarketDepth offerEntry3) {
		this.offerEntry3 = offerEntry3;
	}
	public MarketDepth getOfferEntry4() {
		return offerEntry4;
	}
	public void setOfferEntry4(MarketDepth offerEntry4) {
		this.offerEntry4 = offerEntry4;
	}
	public MarketDepth getOfferEntry5() {
		return offerEntry5;
	}
	public void setOfferEntry5(MarketDepth offerEntry5) {
		this.offerEntry5 = offerEntry5;
	}

	@Override
	public String toString() {
		return "StreamingQuoteModeFull [bidEntry1=" + bidEntry1 + ", bidEntry2=" + bidEntry2 + ", bidEntry3="
				+ bidEntry3 + ", bidEntry4=" + bidEntry4 + ", bidEntry5=" + bidEntry5 + ", offerEntry1=" + offerEntry1
				+ ", offerEntry2=" + offerEntry2 + ", offerEntry3=" + offerEntry3 + ", offerEntry4=" + offerEntry4
				+ ", offerEntry5=" + offerEntry5 + ", lastTradedQty=" + lastTradedQty + ", avgTradedPrice="
				+ avgTradedPrice + ", vol=" + vol + ", buyQty=" + buyQty + ", sellQty=" + sellQty + ", openPrice="
				+ openPrice + ", highPrice=" + highPrice + ", lowPrice=" + lowPrice + ", closePrice=" + closePrice
				+ ", ltp=" + ltp + ", time=" + time + ", instrumentToken=" + instrumentToken + "]";
	}
}
