package com.ap.greenpole.stockbroker.model;

import java.util.Date;
import java.util.List;

import javax.persistence.*;

import com.ap.greenpole.stockbroker.holderModule.Entity.Bondholder;
import com.ap.greenpole.stockbroker.holderModule.Entity.Shareholder;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;

import io.swagger.annotations.ApiModelProperty;

/**
 * Created By: Oyindamola Akindele
 * Date: 8/11/2020 2:32 AM
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class StockBroker {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String stockBrokerName;
	@Column(name = "cscsAccountNumber")
	private String cscsAccountNumber;
	private String address;
	private String emails;
	private String phoneNumbers;
	private boolean active;
	private String validationState;
	private String suspensionState;
	private String signature;
	private Date dateCreated;
	private Date dateModified;
	private String signatureDownloadLink;

	@Transient
	private List<String> emailAddresses;
	@Transient
	private List<String> phones;
	@Transient
	@ApiModelProperty(hidden=true)
	private List<Shareholder> shareholders;
	@Transient
	@ApiModelProperty(hidden=true)
	private List<Bondholder> bondholders;
	@Transient
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private transient long shLowerRange;
	@Transient
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private transient long shUpperRange;
	@Transient
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private transient long bhLowerRange;
	@Transient
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private transient long bhUpperRange;

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getStockBrokerName() {
		return stockBrokerName;
	}
	public void setStockBrokerName(String stockBrokerName) {
		this.stockBrokerName = stockBrokerName;
	}
	
	public String getCscsAccountNumber() {
		return cscsAccountNumber;
	}
	public void setCscsAccountNumber(String cscsAccountNumber) {
		this.cscsAccountNumber = cscsAccountNumber;
	}
	
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getEmails() {
		return emails;
	}
	public void setEmails(String emails) {
		this.emails = emails;
	}
	public String getPhoneNumbers() {
		return phoneNumbers;
	}
	public void setPhoneNumbers(String phoneNumbers) {
		this.phoneNumbers = phoneNumbers;
	}
	public List<String> getEmailAddresses() {
		return emailAddresses;
	}
	public void setEmailAddresses(List<String> emailAddresses) {
		this.emailAddresses = emailAddresses;
	}
	public List<String> getPhones() {
		return phones;
	}
	public void setPhones(List<String> phones) {
		this.phones = phones;
	}
	
    public boolean getActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public String getValidationState() {
		return validationState;
	}
	public void setValidationState(String validationState) {
		this.validationState = validationState;
	}
	public String getSuspensionState() {
		return suspensionState;
	}
	public void setSuspensionState(String suspensionState) {
		this.suspensionState = suspensionState;
	}
	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}
	public Date getDateModified() {
		return dateModified;
	}

	public void setDateModified(Date dateModified) {
		this.dateModified = dateModified;
	}

	public List<Shareholder> getShareholders() {
		return shareholders;
	}

	public void setShareholders(List<Shareholder> shareholders) {
		this.shareholders = shareholders;
	}

	public List<Bondholder> getBondholders() {
		return bondholders;
	}

	public void setBondholders(List<Bondholder> bondholders) {
		this.bondholders = bondholders;
	}

	public String getSignatureDownloadLink() {
		return signatureDownloadLink;
	}

	public void setSignatureDownloadLink(String signatureDownloadLink) {
		this.signatureDownloadLink = signatureDownloadLink;
	}

	public long getShLowerRange() {
		return shLowerRange;
	}

	public void setShLowerRange(long shLowerRange) {
		this.shLowerRange = shLowerRange;
	}

	public long getShUpperRange() {
		return shUpperRange;
	}

	public void setShUpperRange(long shUpperRange) {
		this.shUpperRange = shUpperRange;
	}

	public long getBhLowerRange() {
		return bhLowerRange;
	}

	public void setBhLowerRange(long bhLowerRange) {
		this.bhLowerRange = bhLowerRange;
	}

	public long getBhUpperRange() {
		return bhUpperRange;
	}

	public void setBhUpperRange(long bhUpperRange) {
		this.bhUpperRange = bhUpperRange;
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
