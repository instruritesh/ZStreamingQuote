package com.ritesh.zstreamingquote.quote.mode;

import java.math.BigDecimal;

public class StreamingQuoteModeQuote extends StreamingQuoteModeLtp{
	public Long lastTradedQty;
	public BigDecimal avgTradedPrice;
	public Long vol;
	public Long buyQty;
	public Long sellQty;
	public BigDecimal openPrice;
	public BigDecimal highPrice;
	public BigDecimal lowPrice;
	public BigDecimal closePrice;
	
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
	 */
	public StreamingQuoteModeQuote(String time, String instrumentToken, BigDecimal ltp, Long lastTradedQty,
			BigDecimal avgTradedPrice, Long vol, Long buyQty, Long sellQty, BigDecimal openPrice, BigDecimal highPrice,
			BigDecimal lowPrice, BigDecimal closePrice) {
		super(time, instrumentToken, ltp);
		this.lastTradedQty = lastTradedQty;
		this.avgTradedPrice = avgTradedPrice;
		this.vol = vol;
		this.buyQty = buyQty;
		this.sellQty = sellQty;
		this.openPrice = openPrice;
		this.highPrice = highPrice;
		this.lowPrice = lowPrice;
		this.closePrice = closePrice;
	}

	public Long getLastTradedQty() {
		return lastTradedQty;
	}
	public void setLastTradedQty(Long lastTradedQty) {
		this.lastTradedQty = lastTradedQty;
	}
	public BigDecimal getAvgTradedPrice() {
		return avgTradedPrice;
	}
	public void setAvgTradedPrice(BigDecimal avgTradedPrice) {
		this.avgTradedPrice = avgTradedPrice;
	}
	public Long getVol() {
		return vol;
	}
	public void setVol(Long vol) {
		this.vol = vol;
	}
	public Long getBuyQty() {
		return buyQty;
	}
	public void setBuyQty(Long buyQty) {
		this.buyQty = buyQty;
	}
	public Long getSellQty() {
		return sellQty;
	}
	public void setSellQty(Long sellQty) {
		this.sellQty = sellQty;
	}
	public BigDecimal getOpenPrice() {
		return openPrice;
	}
	public void setOpenPrice(BigDecimal openPrice) {
		this.openPrice = openPrice;
	}
	public BigDecimal getHighPrice() {
		return highPrice;
	}
	public void setHighPrice(BigDecimal highPrice) {
		this.highPrice = highPrice;
	}
	public BigDecimal getLowPrice() {
		return lowPrice;
	}
	public void setLowPrice(BigDecimal lowPrice) {
		this.lowPrice = lowPrice;
	}
	public BigDecimal getClosePrice() {
		return closePrice;
	}
	public void setClosePrice(BigDecimal closePrice) {
		this.closePrice = closePrice;
	}

	@Override
	public String toString() {
		return "StreamingQuoteModeQuote [lastTradedQty=" + lastTradedQty + ", avgTradedPrice=" + avgTradedPrice
				+ ", vol=" + vol + ", buyQty=" + buyQty + ", sellQty=" + sellQty + ", openPrice=" + openPrice
				+ ", highPrice=" + highPrice + ", lowPrice=" + lowPrice + ", closePrice=" + closePrice + ", ltp=" + ltp
				+ ", time=" + time + ", instrumentToken=" + instrumentToken + "]";
	}
}
