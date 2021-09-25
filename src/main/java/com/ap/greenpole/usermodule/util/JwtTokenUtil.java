package com.ap.greenpole.usermodule.util;

import com.ap.greenpole.usermodule.model.*;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;
import java.util.function.Function;

/**
 * @author Adewale Azeez <azeezadewale98@gmail.com>
 * @date 03-Aug-20 11:09 PM
 */
@Component
public class JwtTokenUtil {

    Logger logger = LoggerFactory.getLogger(JwtTokenUtil.class);

    //720 hours (30 days)
    public static final long JWT_TOKEN_VALIDITY = 720 * 60 * 60;

    @Value("${jwt.secret}")
    private String secret;

    public String getEmailFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public ArrayList<String> getPermissionsFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return (ArrayList<String>) claims.get("PERMISSIONS");
    }

    public ArrayList<String> getRolesFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return (ArrayList<String>) claims.get("ROLES");
    }

    public GreenPoleUserDetails getGreenPoleUserDetailsFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return new GreenPoleUserDetails(getEmailFromToken(token),
                (ArrayList<UserRole>)claims.get("ROLES"),
                (ArrayList<UserPermission>)claims.get("PERMISSIONS"));
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public String generateToken(GreenPoleUserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        Date expiryDate = new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000);
        //claims.put("ROLES", userDetails.getRoles());
        //claims.put("PERMISSIONS", userDetails.getPermissions());
        return doGenerateToken(claims, userDetails.getEmail(), expiryDate);
    }

    public String doGenerateToken(Map<String, Object> claims, String subject, Date expiryDate) {
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, secret).compact();
    }

    public Boolean validateToken(String token, GreenPoleUserDetails userDetails) {
        final String email = getEmailFromToken(token);
        return (email.equals(userDetails.getEmail()) && !isTokenExpired(token));
    }

    public Optional<RSAPublicKey> getParsedPublicKey() {
        String publicKey = "";
        try {
            X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(publicKey.getBytes());
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            RSAPublicKey pubKey = (RSAPublicKey) keyFactory.generatePublic(keySpecX509);
            return Optional.of(pubKey);

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            logger.error("Exception block | Public key parsing error ", e);
            return Optional.empty();
        }
    }

}
