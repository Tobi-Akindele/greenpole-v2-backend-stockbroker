/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ap.greenpole.stockbroker.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result<T> {

	private List<T> list; /*list may be set in get operations that return a resultset*/

    private T data; /*data may be set in "get by" operations that return single result */

    private Integer resultCode; /*resultCode is set for all operations */

    private Long identityValue; /*identity value may be set in create operations*/

    private Long noOfRecords;

    private Integer currentPageSize;

    private Integer currentPageNumber;
    
    
    public Result(){}

    public Result(Integer resultCode,
            List<T> list,
            Long noOfRecords, Integer pageNumber ,Integer pageSize) {
        this.list = list;
        this.resultCode = resultCode;
        this.noOfRecords = noOfRecords;
        this.currentPageNumber=pageNumber;
        this.currentPageSize=pageSize;
    }

    public Result(Integer resultCode) {
        this.resultCode = resultCode;
    }

    public Result(Integer resultCode,
            T data) {
        this.resultCode = resultCode;
        this.data = data;
    }

    public Result(Integer resultCode,
            Long identityValue) {
        this.resultCode = resultCode;
        this.identityValue = identityValue;
    }
    
    public List<T> getList() {
        return list;
    }

    public Integer getResultCode() {
        return resultCode;
    }

    public T getData() {
        return data;
    }

    public Long getIdentityValue() {
        return identityValue;
    }

    public Long getNoOfRecords() {
        return noOfRecords;
    }
    
    public void setNoOfRecords(Long noOfRecords) {
		this.noOfRecords = noOfRecords;
	}

    public Integer getCurrentPageSize() {
        return currentPageSize;
    }

    public void setCurrentPageSize(Integer currentPageSize) {
        this.currentPageSize = currentPageSize;
    }

    public Integer getCurrentPageNumber() {
        return currentPageNumber;
    }

    public void setCurrentPageNumber(Integer currentPageNumber) {
        this.currentPageNumber = currentPageNumber;
    }
    
	public void setList(List<T> list) {
		this.list = list;
	}

	public void setData(T data) {
		this.data = data;
	}

	public void setIdentityValue(Long identityValue) {
		this.identityValue = identityValue;
	}
}
