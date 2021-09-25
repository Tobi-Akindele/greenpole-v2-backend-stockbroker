package com.ap.greenpole.usermodule.model;

import com.ap.greenpole.usermodule.util.BooleanToIntConverter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Adewale Azeez <azeezadewale98@gmail.com>
 * @date 13-Aug-20 11:20 AM
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @Email
    @NotBlank(message = "The email cannot be empty")
    String email;

    String firstName;

    String middleName;

    String lastName;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    Date dob;

    @NotBlank(message = "The password cannot be empty")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    String password;

    String phone;

    String gender;

    String occupation;

    String maritalStatus;

    String address;

    String country;

    String city;

    String postalCode;

    String stateOfOrigin;

    String lgaOfOrigin;

    String jobDescription;

    String department;

    String unit;

    String position;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    Date lastChangePasswordDate;

    @Transient
    List<UserRole> roles;

    @Transient
    List<UserPermission> permissions;

    @JsonIgnore
    @Convert(converter = BooleanToIntConverter.class)
    boolean firstTimeLogin;

    @Transient
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    String adminRegisterKey;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
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

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Date getLastChangePasswordDate() {
        return lastChangePasswordDate;
    }

    public void setLastChangePasswordDate(Date lastChangePasswordDate) {
        this.lastChangePasswordDate = lastChangePasswordDate;
    }

    public List<UserRole> getRoles() {
        return roles;
    }

    public void setRoles(List<UserRole> roles) {
        this.roles = roles;
    }

    public List<UserPermission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<UserPermission> permissions) {
        this.permissions = permissions;
    }

    public boolean isFirstTimeLogin() {
        return firstTimeLogin;
    }

    public void setFirstTimeLogin(boolean firstTimeLogin) {
        this.firstTimeLogin = firstTimeLogin;
    }

    public String getAdminRegisterKey() {
        return adminRegisterKey;
    }

    public void setAdminRegisterKey(String adminRegisterKey) {
        this.adminRegisterKey = adminRegisterKey;
    }

    public static class Request {
        @NotNull @NotEmpty
        @NotBlank(message = "The username value cannot be empty")
        String email;

        @NotNull @NotEmpty
        @NotBlank(message = "The password value cannot be empty")
        String password;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class Response {
        String accessToken;

        String message;

        User user;

        boolean firstTimeLogin = true;

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public boolean isFirstTimeLogin() {
            return firstTimeLogin;
        }

        public void setFirstTimeLogin(boolean firstTimeLogin) {
            this.firstTimeLogin = firstTimeLogin;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }
    }

}
