package com.ap.greenpole.usermodule.config;

import com.ap.greenpole.usermodule.model.GreenPoleUserDetails;
import com.ap.greenpole.usermodule.model.User;
import com.ap.greenpole.usermodule.service.UserService;
import com.ap.greenpole.usermodule.util.Helpers;
import com.ap.greenpole.usermodule.util.JwtTokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.thecarisma.InvalidEntryException;
import io.github.thecarisma.Konfiger;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import redis.clients.jedis.Jedis;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

/**
 * @author Adewale Azeez <azeezadewale98@gmail.com>
 * @date 03-Aug-20 11:56 PM
 */
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    Logger logger = LoggerFactory.getLogger(JwtTokenUtil.class);

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    UserService userService;

    @Autowired
    private Environment env;

    private static String redisIp = null;

    private static int redisPort = 0;

    Jedis jedis;

    public String getRedisIp() {
        redisIp = env.getProperty("spring.redis.ip");
        if (redisIp == null && GreenPoleConfig.userManagementProperties != null) {
            redisIp = GreenPoleConfig.resolveOptionalEnvFromSystem(
                    GreenPoleConfig.userManagementProperties.getString("spring.redis.ip"));
        }
        return redisIp;
    }

    public void setRedisIp(String redisIp) {
        JwtRequestFilter.redisIp = redisIp;
    }

    public int getRedisPort() {
        if (redisPort != 0) {
            return redisPort;
        }
        redisPort = Integer.parseInt(
                env.getProperty("spring.redis.port", "0")
        );
        if (redisPort == 0 && GreenPoleConfig.userManagementProperties != null) {
            String value = GreenPoleConfig.resolveOptionalEnvFromSystem(
                    GreenPoleConfig.userManagementProperties.getString("spring.redis.port", "0"));
            redisPort = Integer.parseInt(value);
        }
        return redisPort;
    }

    public void setRedisPort(int redisPort) {
        JwtRequestFilter.redisPort = redisPort;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        final String requestTokenHeader = httpServletRequest.getHeader("Authorization");
        String jwtToken = null;
        GreenPoleUserDetails userDetails;

        if (!WebSecurityConfig.orRequestMatcher.matches(httpServletRequest)) {
            if (requestTokenHeader != null && (requestTokenHeader.startsWith("Bearer ") || requestTokenHeader.startsWith("bearer "))) {
                jwtToken = requestTokenHeader.substring(7);
                try {
                    userDetails = jwtTokenUtil.getGreenPoleUserDetailsFromToken(jwtToken);

                    // fetch permission and cache in redis
                    User user = null;
                    try {
                        if (jedis == null || !jedis.isConnected()) {
                            jedis = new Jedis(getRedisIp(), getRedisPort());
                        }
                        boolean notExist = false;
                        try {
                            notExist = (jedis.get(jwtToken) == null || jedis.get(jwtToken).isEmpty());
                        } catch (Exception exception) {
                            exception.printStackTrace();
                            jedis = new Jedis(getRedisIp(), getRedisPort());
                        }
                        if (notExist) {
                            Optional<User> userRes = userService.getUserWithPermissionsAndRolesByEmail(userDetails.getEmail());
                            if (!userRes.isPresent()) {
                                Helpers.WriteError(httpServletRequest, httpServletResponse, HttpStatus.UNAUTHORIZED,
                                        "An error occur when trying to validate the access token, Could not get the roles and permissions");
                                return;
                            }
                            user = userRes.get();
                            jedis.set(jwtToken, new ObjectMapper().writeValueAsString(user));
                        }
                        user = new ObjectMapper().readValue(jedis.get(jwtToken), User.class);
                        userDetails = new GreenPoleUserDetails(userDetails.getEmail(), user.getRoles(), user.getPermissions());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        if (user == null) {
                            Optional<User> userRes = userService.getUserWithPermissionsAndRolesByEmail(userDetails.getEmail());
                            if (userRes.isPresent()) {
                                user = userRes.get();
                            }
                        }
                        if (user != null) {
                            userDetails = new GreenPoleUserDetails(userDetails.getEmail(), user.getRoles(), user.getPermissions());
                        }
                    }
                    // end

                    httpServletRequest.setAttribute("ROLES", userDetails.getRoles());
                    httpServletRequest.setAttribute("PERMISSIONS", userDetails.getPermissions());
                    httpServletRequest.setAttribute("AUTHORIZATION_HEADER", requestTokenHeader);

                } catch (IllegalArgumentException | MalformedJwtException ex) {
                    logger.error(ex.getMessage(), ex);
                    Helpers.WriteError(httpServletRequest, httpServletResponse, HttpStatus.UNAUTHORIZED, "Invalid Authentication token");
                    return;
                } catch (ExpiredJwtException ex) {
                    logger.error(ex.getMessage(), ex);
                    Helpers.WriteError(httpServletRequest, httpServletResponse, HttpStatus.UNAUTHORIZED, "The Authentication token has expired");
                    return;
                } catch (Exception ex) {
                    logger.error(ex.getMessage(), ex);
                    Helpers.WriteError(httpServletRequest, httpServletResponse, HttpStatus.UNAUTHORIZED, "An error occur when trying to validate the access token");
                    return;
                }
            } else {
                Helpers.WriteError(httpServletRequest, httpServletResponse, HttpStatus.UNAUTHORIZED, "The Authentication token is missing");
                return;
            }

            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                try {
                    if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
                        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        usernamePasswordAuthenticationToken
                                .setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                    }
                } catch (Exception ex) {
                    logger.error(ex.getMessage(), ex);
                    Helpers.WriteError(httpServletRequest, httpServletResponse, HttpStatus.UNAUTHORIZED,
                            "An error occur when trying to validate the access token, Could not get the roles and permissions");
                    return;
                }
            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

}
