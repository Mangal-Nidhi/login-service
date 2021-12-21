package com.sapient.login.builder;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;

@Component
public class JWTBuilder {

    @Value("${signing.private.key}")
    private String privateKey;

    public String getSignedJWT(String subject) {
        JwtBuilder jwtBuilder = Jwts.builder();
        setHeaders(jwtBuilder);
        setClaims(jwtBuilder, subject);
        return jwtBuilder
                .signWith(getPrivateKey(), SignatureAlgorithm.RS256)
                .compact();
    }

    private void setHeaders(JwtBuilder jwtBuilder) {
        jwtBuilder
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", SignatureAlgorithm.RS256);
    }

    private void setClaims(JwtBuilder jwtBuilder, String subject) {
        Claims claims = Jwts.claims();
        claims.put("permissions", Collections.singleton("PS_USER"));
        jwtBuilder
                .setClaims(claims)
                .setIssuer("PSCode")
                .setSubject(subject)
                .setAudience("PSClient")
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plus(24, ChronoUnit.HOURS)))
                .setId(UUID.randomUUID().toString());
    }

    private Key getPrivateKey() {
        try {
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey));
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePrivate(keySpec);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }
}
