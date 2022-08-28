package com.org.authservice.service;

import com.google.common.base.Throwables;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.keys.HmacKey;
import org.jose4j.lang.JoseException;

import java.util.Map;
import java.util.UUID;

import static java.util.Collections.singletonMap;
import static org.jose4j.jws.AlgorithmIdentifiers.HMAC_SHA256;

public class TokenService {

    private final byte[] tokenSecret;

    public TokenService(byte[] tokenSecret) {
        this.tokenSecret = tokenSecret;
    }

    public Map<String, String> generateToken(String id) {
        final JwtClaims claims = new JwtClaims();
        claims.setSubject(id);
        claims.setExpirationTimeMinutesInTheFuture(30);
        final JsonWebSignature jws = new JsonWebSignature();
        jws.setPayload(claims.toJson());
        jws.setAlgorithmHeaderValue(HMAC_SHA256);
        jws.setKey(new HmacKey(tokenSecret));
        try {
            return singletonMap("token", jws.getCompactSerialization());
        }
        catch (JoseException e) { throw Throwables.propagate(e); }
    }
}
