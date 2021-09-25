package com.ap.greenpole.stockbroker.holderModule.Entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
public class Shareholder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long shareholder_id;

    private String email, phone, gender, occupation, address, city, country, relationship, rin, nuban, bvn, status;

    @JsonProperty("first_name")
    @Column(name = "first_name")
    String firstName;

    @JsonProperty("middle_name")
    @Column(name = "middle_name")
    String middleName;

    @JsonProperty("last_name")
    @Column(name = "last_name")
    String lastName;

    @JsonProperty("marital_status")
    @Column(name = "marital_status")
    String maritalStatus;

    @JsonProperty("state_of_origin")
    @Column(name = "state_of_origin")
    String stateOfOrigin;

    @JsonProperty("marriage_certificate_number")
    @Column(name = "marriage_certificate_number")
    String marriageCertificateNumber;

    @JsonProperty("postal_code")
    @Column(name = "postal_code")
    String postalCode;

    @JsonProperty("lga_of_origin")
    @Column(name = "lga_of_origin")
    String lgaOfOrigin;

    @JsonProperty("kin_email")
    @Column(name = "kin_email")
    String kinEmail;

    @JsonProperty("kin_name")
    @Column(name = "kin_name")
    String kinName;

    @JsonProperty("kin_phone")
    @Column(name = "kin_phone")
    String kinPhone;

    @JsonProperty("kin_address")
    @Column(name = "kin_address")
    String kinAddress;

    @JsonProperty("kin_country")
    @Column(name = "kin_country")
    String kinCountry;

    @JsonProperty("kin_state")
    @Column(name = "kin_state")
    String kinState;

    @JsonProperty("kin_lga")
    @Column(name = "kin_lga")
    String kinLga;

    @JsonProperty("shareholder_type")
    @Column(name = "shareholder_type")
    String shareholderType;

    @JsonProperty("client_company")
    @Column(name = "client_company")
    long clientCompany;

    @JsonProperty("stock_broker")
    @Column(name = "stock_broker")
    long stockBroker;

    @JsonProperty("bank_name")
    @Column(name = "bank_name")
    String bankName;

    @JsonProperty("bank_account")
    @Column(name = "bank_account")
    String bankAccount;

    @JsonProperty("clearing_housing_number")
    @Column(name = "clearing_housing_number")
    String clearingHousingNumber;

    @JsonProperty("esop_status")
    @Column(name = "esop_status")
    String esopStatus;

    @JsonProperty("share_unit")
    @Column(name = "share_unit")
    int shareUnit;

    @DateTimeFormat(pattern="dd-MM-yyyy hh:mm:ss")
    @JsonProperty("created_on")
    @Column(name = "created_on")
    private Date createdOn;

    @DateTimeFormat(pattern="dd-MM-yyyy")
    private Date dob;

    @JsonProperty("tax_exemption")
    @Column(name = "tax_exemption")
    boolean taxExemption;

    @JsonProperty("registrar_mandated")
    @Column(name = "registrar_mandated")
    boolean registrarMandated;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public long getShareholder_id() {
        return shareholder_id;
    }

    public void setShareholder_id(long shareholder_id) {
        this.shareholder_id = shareholder_id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getStateOfOrigin() {
        return stateOfOrigin;
    }

    public void setStateOfOrigin(String stateOfOrigin) {
        this.stateOfOrigin = stateOfOrigin;
    }

    public String getLgaOfOrigin() {
        return lgaOfOrigin;
    }

    public void setLgaOfOrigin(String lgaOfOrigin) {
        this.lgaOfOrigin = lgaOfOrigin;
    }

    public String getMarriageCertificateNumber() {
        return marriageCertificateNumber;
    }

    public void setMarriageCertificateNumber(String marriageCertificateNumber) {
        this.marriageCertificateNumber = marriageCertificateNumber;
    }

    public String getKinName() {
        return kinName;
    }

    public void setKinName(String kinName) {
        this.kinName = kinName;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getKinEmail() {
        return kinEmail;
    }

    public void setKinEmail(String kinEmail) {
        this.kinEmail = kinEmail;
    }

    public String getKinPhone() {
        return kinPhone;
    }

    public void setKinPhone(String kinPhone) {
        this.kinPhone = kinPhone;
    }

    public String getKinAddress() {
        return kinAddress;
    }

    public void setKinAddress(String kinAddress) {
        this.kinAddress = kinAddress;
    }

    public String getKinCountry() {
        return kinCountry;
    }

    public void setKinCountry(String kinCountry) {
        this.kinCountry = kinCountry;
    }

    public String getKinState() {
        return kinState;
    }

    public void setKinState(String kinState) {
        this.kinState = kinState;
    }

    public String getKinLga() {
        return kinLga;
    }

    public void setKinLga(String kinLga) {
        this.kinLga = kinLga;
    }

    public String getShareholderType() {
        return shareholderType;
    }

    public void setShareholderType(String shareholderType) {
        this.shareholderType = shareholderType;
    }

    public long getClientCompany() {
        return clientCompany;
    }

    public void setClientCompany(long clientCompany) {
        this.clientCompany = clientCompany;
    }

    public long getStockBroker() {
        return stockBroker;
    }

    public void setStockBroker(long stockBroker) {
        this.stockBroker = stockBroker;
    }

    public String getRin() {
        return rin;
    }

    public void setRin(String rin) {
        this.rin = rin;
    }

    public String getNuban() {
        return nuban;
    }

    public void setNuban(String nuban) {
        this.nuban = nuban;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getBvn() {
        return bvn;
    }

    public void setBvn(String bvn) {
        this.bvn = bvn;
    }

    public String getClearingHousingNumber() {
        return clearingHousingNumber;
    }

    public void setClearingHousingNumber(String clearingHousingNumber) {
        this.clearingHousingNumber = clearingHousingNumber;
    }

    public String getEsopStatus() {
        return esopStatus;
    }

    public void setEsopStatus(String esopStatus) {
        this.esopStatus = esopStatus;
    }

    public int getShareUnit() {
        return shareUnit;
    }

    public void setShareUnit(int shareUnit) {
        this.shareUnit = shareUnit;
    }

    public Date getCreatedOn() {
        return this.createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(String dob) {

        try{
            this.dob=new SimpleDateFormat("dd-MM-yyyy").parse(dob);

        }
        catch (Exception ex){
            this.dob = null;
        }

    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean getTaxExemption() {
        return taxExemption;
    }

    public void setTaxExemption(boolean taxExemption) {
        this.taxExemption = taxExemption;
    }

    public boolean getRegistrarMandated() {
        return registrarMandated;
    }

    public void setRegistrarMandated(boolean registrarMandated) {
        this.registrarMandated = registrarMandated;
    }

}
