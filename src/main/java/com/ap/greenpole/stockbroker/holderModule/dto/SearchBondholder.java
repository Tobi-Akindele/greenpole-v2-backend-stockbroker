package com.ap.greenpole.stockbroker.holderModule.dto;

import io.swagger.annotations.ApiModelProperty;

public class SearchBondholder {
    String element, value;
    @ApiModelProperty(hidden=true)
    private long stockBroker;

    public String getElement() {
        return element;
    }

    public void setElement(String element) {
        this.element = element;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

	public long getStockBroker() {
		return stockBroker;
	}

	public void setStockBroker(long stockBroker) {
		this.stockBroker = stockBroker;
	}
}
