package com.ap.greenpole.usermodule.model;

import org.springframework.http.HttpStatus;
import java.sql.Timestamp;
import java.util.Date;

/**
 * @author Adewale Azeez <azeezadewale98@gmail.com>
 * @date 26-May-20 07:50 AM
 */
public class GenericResponse<T> {

    private String status, statusMessage;
    private T data;
    private Date date = new Date();

    public GenericResponse() { }

    public GenericResponse(String status, String statusMessage, T data) {
        this.status = status;
        this.statusMessage = statusMessage;
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
