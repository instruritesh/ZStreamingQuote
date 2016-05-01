package com.ritesh.zstreamingquote.quote;

import java.math.BigDecimal;

public class MarketDepth {
	public Long qty;
	public BigDecimal price;
	public Integer orders;
	
	/**
	 * Constructor
	 * @param qty
	 * @param price
	 * @param orders
	 */
	public MarketDepth(Long qty, BigDecimal price, Integer orders) {
		super();
		this.qty = qty;
		this.price = price;
		this.orders = orders;
	}
	
	public Long getQty() {
		return qty;
	}
	public void setQty(Long qty) {
		this.qty = qty;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public Integer getOrders() {
		return orders;
	}
	public void setOrders(Integer orders) {
		this.orders = orders;
	}

	@Override
	public String toString() {
		return "MarketDepth [qty=" + qty + ", price=" + price + ", orders=" + orders + "]";
	}
}
