package com.ap.greenpole.stockbroker.dto;

/**
 * Created By: Ilesanmi Omoniyi
 * Date: 8/11/2020 11:13 PM
 */
public enum StockBrokerAPIResponseCode {


    SUCCESSFUL("00"), FAILED("01");

    private String status;


    StockBrokerAPIResponseCode(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
