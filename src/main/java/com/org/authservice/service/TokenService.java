package com.org.authservice.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;

public class TokenService {

    private final String tokenSecret;

    public TokenService(String tokenSecret) {
        this.tokenSecret = tokenSecret;
    }

    public String generateToken(String id) {
        final Key hmacKey = new SecretKeySpec(Base64.getDecoder().decode(tokenSecret),
                SignatureAlgorithm.HS256.getJcaName());
        final Instant now = Instant.now();
        final String jwtToken = Jwts.builder()
                .setId(id)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(10l, ChronoUnit.MINUTES)))
                .signWith(hmacKey)
                .compact();
        return jwtToken;
    }

    public Jws<Claims> parseToken(String token) {
        final Key hmacKey = new SecretKeySpec(Base64.getDecoder().decode(tokenSecret),
                SignatureAlgorithm.HS256.getJcaName());
        Jws<Claims> jwt = Jwts.parserBuilder()
                .setSigningKey(hmacKey)
                .build()
                .parseClaimsJws(token);
        return jwt;
    }
}
