package com.ap.greenpole.stockbroker.dto;

import java.io.Serializable;

/**
 * Created By: Ilesanmi Omoniyi
 * Date: 8/22/2020 1:10 PM
 */
public class StockBrokerBaseResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private String status;

    private String statusMessage;

    private  T data;
    
    private Long count;


    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}
}
