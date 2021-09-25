package com.ap.greenpole.usermodule.model;

import com.ap.greenpole.usermodule.util.Helpers;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.List;

/**
 * @author Adewale Azeez <azeezadewale98@gmail.com>
 * @date 03-Jun-20 11:18 PM
 */
public class GreenPoleUserDetails implements Serializable {

    String email;

    List<UserRole> roles;

    List<UserPermission> permissions;

    public GreenPoleUserDetails() {
        email = "";
        roles = new ArrayList<>();
        permissions = new ArrayList<>();
    }

    public GreenPoleUserDetails(String email, List<UserRole> roles, List<UserPermission> permissions) {
        this.email = email;
        this.roles = roles;
        this.permissions = permissions;
    }

    public String getPassword() {
        return null;
    }

    public String getEmail() {
        return email;
    }

    public List<UserRole> getRoles() {
        return roles;
    }

    public List<UserPermission> getPermissions() {
        return permissions;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRoles(List<UserRole> roles) {
        this.roles = roles;
    }

    public void setPermissions(List<UserPermission> permissions) {
        this.permissions = permissions;
    }

    public boolean isAccountNonExpired() {
        return false;
    }

    public boolean isAccountNonLocked() {
        return false;
    }

    public boolean isCredentialsNonExpired() {
        return false;
    }

    public boolean isEnabled() {
        return false;
    }

    // JavaVM type inference failed woefully here. Even though the object
    // is initialized with the List<Role> the content is always
    // List<LinkedHashmap>. JVM was suppose to catch that
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> list = new ArrayList<>();
        Object[] array = roles.toArray();
        for (Object role_ : array) {
            UserRole role = (role_ instanceof UserRole) ? (UserRole) role_ : new ObjectMapper().convertValue(role_, UserRole.class);
            list.add(new SimpleGrantedAuthority(Helpers.RolePrefix + role.getValue().toUpperCase()));
        }
        return list;
    }

    @JsonIgnore
    public Collection<? extends GrantedAuthority> getPermissionAuthorities() {
        List<GrantedAuthority> list = new ArrayList<>();
        Object[] array = permissions.toArray();
        for (Object permission_ : array) {
            UserPermission permission = (permission_ instanceof UserPermission) ? (UserPermission) permission_ :
                    new ObjectMapper().convertValue(permission_, UserPermission.class);
            list.add(new SimpleGrantedAuthority(Helpers.PermissionPrefix + permission.getValue().toUpperCase()));
        }
        return list;
    }

}
