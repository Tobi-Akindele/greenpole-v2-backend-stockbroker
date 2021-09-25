package com.ap.greenpole.usermodule.config;

import com.ap.greenpole.usermodule.annotation.PreAuthorizePermission;
import com.ap.greenpole.usermodule.model.Permission;
import com.ap.greenpole.usermodule.model.Role;
import com.ap.greenpole.usermodule.model.UserPermission;
import com.ap.greenpole.usermodule.model.UserRole;
import com.ap.greenpole.usermodule.util.Helpers;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Adewale Azeez <azeezadewale98@gmail.com>
 * @date 12-Aug-20 01:18 AM
 */
@Component
public class GreenPoleMethodInterceptor implements HandlerInterceptor {

    // JVM Type inference issue here too
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        HandlerMethod handlerMethod;
        try {
            handlerMethod = (HandlerMethod) handler;
        } catch (ClassCastException e) {
            return true;
        }
        Method method = handlerMethod.getMethod();
        PreAuthorizePermission preAuthorizePermission = null;
        if (method.isAnnotationPresent(PreAuthorizePermission.class)) {
            preAuthorizePermission = method.getDeclaredAnnotation(PreAuthorizePermission.class);
        } else if (method.getDeclaringClass().isAnnotationPresent(PreAuthorizePermission.class)) {
            preAuthorizePermission = method.getDeclaringClass().getDeclaredAnnotation(PreAuthorizePermission.class);
        }
        if (preAuthorizePermission != null) {
            List<String> values = Arrays.asList(preAuthorizePermission.value());
            ArrayList<Object> userRoles = (ArrayList<Object>) request.getAttribute("ROLES"); // get from method and repo
            ArrayList<Object> userPermissions = (ArrayList<Object>) request.getAttribute("PERMISSIONS"); // get from method and repo
            if (userRoles == null || userPermissions == null) {
                return false;
            }
            for (Object role_ : userRoles) {
                UserRole role = (role_ instanceof UserRole) ? (UserRole) role_ :
                        new ObjectMapper().convertValue(role_, UserRole.class);
                if (role.getValue().equals("ADMIN")) {
                    return true;
                }
            }
            for (Object permission_ : userPermissions) {
                UserPermission permission = (permission_ instanceof UserPermission) ? (UserPermission) permission_ :
                        new ObjectMapper().convertValue(permission_, UserPermission.class);
                if (values.contains(Helpers.PermissionPrefix + permission.getValue())) {
                    return true;
                }
            }
            Helpers.WriteError(request, response, HttpStatus.UNAUTHORIZED,
                    "You don't have the required permission to perform this operation");
            return false;
        }
        return true;
    }

}
