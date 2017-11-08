package com.sumscope.optimus.moneymarket.model.dto;

import com.sumscope.optimus.commons.log.LogManager;

import java.util.List;

public class QuoteRequestDto implements Cloneable{

	private List<MmQuoteDto> offer_data;

	public List<MmQuoteDto> getOffer_data() {
		return offer_data;
	}

	public void setOffer_data(List<MmQuoteDto> offer_data) {
		this.offer_data = offer_data;
	}

	public Object clone(){
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			LogManager.error(e.toString()+"this is CloneNotSupportedException");
		}
		return null;
	}

}
