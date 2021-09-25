package com.ap.greenpole.usermodule.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author Adewale Azeez <azeezadewale98@gmail.com>
 * @date 17-Aug-20 12:14 PM
 */
public class ChangePassword {
    @NotNull @NotEmpty
    @NotBlank(message = "The old password value cannot be empty")
    @JsonProperty("old_password")
    String oldPassword;

    @NotNull @NotEmpty
    @NotBlank(message = "The new password value cannot be empty")
    @JsonProperty("new_password")
    String newPassword;

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
