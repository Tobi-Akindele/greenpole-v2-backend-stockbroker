package com.ap.greenpole.usermodule.model;

import com.ap.greenpole.usermodule.util.BooleanToIntConverter;

import javax.persistence.*;

/**
 * @author Adewale Azeez <azeezadewale98@gmail.com>
 * @date 22-Aug-20 03:11 AM
 */

@Entity
@Table(name = "email_verifications")
public class EmailVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    long userId;

    String emailAddress;

    String token;

    long dateIssued;

    long expiryDate;

    @Convert(converter = BooleanToIntConverter.class)
    boolean tokenUsed;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getDateIssued() {
        return dateIssued;
    }

    public void setDateIssued(long dateIssued) {
        this.dateIssued = dateIssued;
    }

    public long getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(long expiryDate) {
        this.expiryDate = expiryDate;
    }

    public boolean isTokenUsed() {
        return tokenUsed;
    }

    public void setTokenUsed(boolean tokenUsed) {
        this.tokenUsed = tokenUsed;
    }
}
