package com.ap.greenpole.usermodule.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;

/**
 * @author Adewale Azeez <azeezadewale98@gmail.com>
 * @date 07-Jun-20 09:34 PM
 */
public class ResetPassword {

    @NotBlank(message = "The new password cannot be empty")
    String password;

    @NotBlank(message = "The token cannot be empty")
    String token;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
