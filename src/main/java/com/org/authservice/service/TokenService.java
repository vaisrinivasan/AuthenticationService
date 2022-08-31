package com.org.authservice.service;

import com.org.authservice.exceptions.InvalidInputException;
import io.jsonwebtoken.*;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;

public class TokenService {

    private final String tokenSecret;

    public TokenService(final String tokenSecret) {
        this.tokenSecret = tokenSecret;
    }

    public String generateToken(final String id) {
        try {
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
        } catch (JwtException jwtException) {
            throw new InvalidInputException(jwtException);
        }
    }

    public Jws<Claims> parseToken(final String token) {
        try {
            final Key hmacKey = new SecretKeySpec(Base64.getDecoder().decode(tokenSecret),
                    SignatureAlgorithm.HS256.getJcaName());
            Jws<Claims> jwt = Jwts.parserBuilder()
                    .setSigningKey(hmacKey)
                    .build()
                    .parseClaimsJws(token);
            return jwt;
        } catch (JwtException jwtException) {
            throw new InvalidInputException(jwtException);
        }
    }
}
