package com.ap.greenpole.stockbroker.dto;

import java.util.List;

import com.google.gson.Gson;

public class RequestAuthorization {

	private List<Long> requestIds;
	private String action;
	private String comment;
	private Long approverId;
	
	public List<Long> getRequestIds() {
		return requestIds;
	}
	public void setRequestIds(List<Long> requestIds) {
		this.requestIds = requestIds;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public Long getApproverId() {
		return approverId;
	}
	public void setApproverId(Long approverId) {
		this.approverId = approverId;
	}
	@Override
	public String toString() {
		try {
			return new Gson().toJson(this);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return "";
	}
}
