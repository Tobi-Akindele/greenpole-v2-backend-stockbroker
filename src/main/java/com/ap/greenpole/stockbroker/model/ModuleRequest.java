package com.ap.greenpole.stockbroker.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.format.annotation.DateTimeFormat;

import com.ap.greenpole.stockbroker.holderModule.util.Const;
import com.ap.greenpole.usermodule.model.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;

@Entity
@Table(name = "tbl_request_approval")
public class ModuleRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("request_id")
    @Column(name = "request_id")
    private long requestId;

    @JsonProperty("requester_id")
    @Column(name = "requester_id")
    long requesterId;

    @JsonProperty("resource_id")
    @Column(name = "resource_id")
    long resourceId;

    @JsonProperty("approver_id")
    @Column(name = "approver_id")
    long approverId;

    int status;

    private String reason, modules;

    @JsonProperty("request_code")
    @Column(name = "request_code")
    String requestCode;

    @DateTimeFormat(pattern= Const.DATE_FORMATE)
    @JsonProperty("created_on")
    @Column(name = "created_on")
    private Date createdOn;

    @DateTimeFormat(pattern=Const.DATE_FORMATE)
    @JsonProperty("approved_on")
    @Column(name = "approved_on")
    Date approvedOn;

    @JsonProperty("old_record")
    @Column(name = "old_record")
    private String oldRecord;

    @JsonProperty("new_record")
    @Column(name = "new_record")
    private String newRecord;

    @JsonProperty("action_required")
    @Column(name = "action_required")
    private String actionRequired;
    
    @Transient
    private User requester;
    
    public ModuleRequest(String oldRecord, String newRecord) {
    	this.oldRecord = oldRecord;
    	this.newRecord = newRecord;
    }

    public ModuleRequest(Long userId, String action, int status, String oldData, String holder){
        actionRequired = action;
        this.status = status;
        newRecord = holder;
        this.requesterId = userId;
        oldRecord = oldData;
    }

    public ModuleRequest(Long userId, String action, int status, String holder){
        actionRequired = action;
        this.status = status;
        newRecord = holder;
        this.requesterId = userId;

    }

    public String getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(String requestCode) {
        this.requestCode = requestCode;
    }

    public long getResourceId() {
        return resourceId;
    }

    public void setResourceId(long resourceId) {
        this.resourceId = resourceId;
    }

    public long getApproverId() {
        return approverId;
    }

    public void setApproverId(long approverId) {
        this.approverId = approverId;
    }

    public String getModules() {
        return modules;
    }

    public void setModules(String modules) {
        this.modules = modules;
    }
    public ModuleRequest(){}

    public long getRequestId() {
        return requestId;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }

    public long getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(long requesterId) {
        this.requesterId = requesterId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getOldRecord() {
        return oldRecord;
    }

    public void setOldRecord(String oldRecord) {
        this.oldRecord = oldRecord;
    }

    public String getNewRecord() {
        return newRecord;
    }

    public void setNewRecord(String newRecord) {
        this.newRecord = newRecord;
    }

    public String getActionRequired() {
        return actionRequired;
    }

    public void setActionRequired(String actionRequired) {
        this.actionRequired = actionRequired;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public Date getApprovedOn() {
        return approvedOn;
    }

    public void setApprovedOn(Date approvedOn) {
        this.approvedOn = approvedOn;
    }

    public static class ReasonForRejection {
        String reason;

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }

	public User getRequester() {
		return requester;
	}

	public void setRequester(User approvalRequesterUser) {
		this.requester = approvalRequesterUser;
	}

	@Override
	public String toString() {
		try {
			return new Gson().toJson(this);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
}
