package com.example.demo.config;


import com.example.demo.model.XUserDetails;
import io.jsonwebtoken.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;


/**
 *  is responsible for performing JWT operations like creation and validation.
 */

@Log4j2
@Component
@PropertySource("classpath:jwt.properties")
public class JwtTokenUtil implements Serializable {
    private static final long serialVersionUID = -2550185165626007488L;

    @Value("${jwt.secret}")
    private String secret;

    @Value("86400000")
    private int jwtExpirationTime;

    //get username from jwt token
    public String getUsernameFromToken(String token){
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
    }

    //generate token for user
    public String generateToken(Authentication authentication) {
        XUserDetails xUserPrincipal = (XUserDetails) authentication.getPrincipal();
        return Jwts.builder()
                .setSubject(xUserPrincipal.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date().getTime()+jwtExpirationTime)))
                .signWith(SignatureAlgorithm.HS512,secret)
                .compact();
    }

    //validate token
    public boolean validateToken(String token) {
        try{
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            return true;
        }catch (SignatureException ex){
            log.info(String.format("Invalid JWT signature: [%s]",ex.getMessage()));
        }catch (MalformedJwtException ex) {
            log.info(String.format("Invalid JWT token: [%s]",ex.getMessage()));
        } catch (ExpiredJwtException ex) {
            log.info(String.format("JWT Token is expired: [%s]",ex.getMessage()));
        } catch (UnsupportedJwtException ex) {
            log.info(String.format("JWT Token is unsupported: [%s]",ex.getMessage()));
        } catch (IllegalArgumentException ex) {
            log.info(String.format("JWT claims string is empty: [%s]",ex.getMessage()));
        }

        return false;
    }

    //for getting any information from token we will need the secret key
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

}
