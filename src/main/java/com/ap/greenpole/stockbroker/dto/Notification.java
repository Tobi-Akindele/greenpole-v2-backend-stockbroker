package com.ap.greenpole.stockbroker.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "moduleName", "modulePermission", "dataOwnerName", "dataRequesterName", "dataOwnerPhoneNumbers",
		"dataOwnerEmails", "dataRequesterPhoneNumbers", "dataRequesterEmails" })
public class Notification {

	@JsonProperty("moduleName")
	private String moduleName;
	@JsonProperty("modulePermission")
	private String modulePermission;
	@JsonProperty("dataOwnerName")
	private String dataOwnerName;
	@JsonProperty("dataRequesterName")
	private String dataRequesterName;
	@JsonProperty("dataOwnerPhoneNumbers")
	private List<String> dataOwnerPhoneNumbers = null;
	@JsonProperty("dataOwnerEmails")
	private List<String> dataOwnerEmails = null;
	@JsonProperty("dataRequesterPhoneNumbers")
	private List<String> dataRequesterPhoneNumbers = null;
	@JsonProperty("dataRequesterEmails")
	private List<String> dataRequesterEmails = null;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<>();

	public Notification(String dataOwnerName, String dataRequesterName,
			List<String> dataOwnerPhoneNumbers, List<String> dataOwnerEmails, List<String> dataRequesterPhoneNumbers,
			List<String> dataRequesterEmails) {
		super();
		this.dataOwnerName = dataOwnerName;
		this.dataRequesterName = dataRequesterName;
		this.dataOwnerPhoneNumbers = dataOwnerPhoneNumbers;
		this.dataOwnerEmails = dataOwnerEmails;
		this.dataRequesterPhoneNumbers = dataRequesterPhoneNumbers;
		this.dataRequesterEmails = dataRequesterEmails;
	}

	@JsonProperty("moduleName")
	public String getModuleName() {
		return moduleName;
	}

	@JsonProperty("moduleName")
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	@JsonProperty("modulePermission")
	public String getModulePermission() {
		return modulePermission;
	}

	@JsonProperty("modulePermission")
	public void setModulePermission(String modulePermission) {
		this.modulePermission = modulePermission;
	}

	@JsonProperty("dataOwnerName")
	public String getDataOwnerName() {
		return dataOwnerName;
	}

	@JsonProperty("dataOwnerName")
	public void setDataOwnerName(String dataOwnerName) {
		this.dataOwnerName = dataOwnerName;
	}

	@JsonProperty("dataRequesterName")
	public String getDataRequesterName() {
		return dataRequesterName;
	}

	@JsonProperty("dataRequesterName")
	public void setDataRequesterName(String dataRequesterName) {
		this.dataRequesterName = dataRequesterName;
	}

	@JsonProperty("dataOwnerPhoneNumbers")
	public List<String> getDataOwnerPhoneNumbers() {
		return dataOwnerPhoneNumbers;
	}

	@JsonProperty("dataOwnerPhoneNumbers")
	public void setDataOwnerPhoneNumbers(List<String> dataOwnerPhoneNumbers) {
		this.dataOwnerPhoneNumbers = dataOwnerPhoneNumbers;
	}

	@JsonProperty("dataOwnerEmails")
	public List<String> getDataOwnerEmails() {
		return dataOwnerEmails;
	}

	@JsonProperty("dataOwnerEmails")
	public void setDataOwnerEmails(List<String> dataOwnerEmails) {
		this.dataOwnerEmails = dataOwnerEmails;
	}

	@JsonProperty("dataRequesterPhoneNumbers")
	public List<String> getDataRequesterPhoneNumbers() {
		return dataRequesterPhoneNumbers;
	}

	@JsonProperty("dataRequesterPhoneNumbers")
	public void setDataRequesterPhoneNumbers(List<String> dataRequesterPhoneNumbers) {
		this.dataRequesterPhoneNumbers = dataRequesterPhoneNumbers;
	}

	@JsonProperty("dataRequesterEmails")
	public List<String> getDataRequesterEmails() {
		return dataRequesterEmails;
	}

	@JsonProperty("dataRequesterEmails")
	public void setDataRequesterEmails(List<String> dataRequesterEmails) {
		this.dataRequesterEmails = dataRequesterEmails;
	}

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
	}

}