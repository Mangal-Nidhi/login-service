package com.sapient.login.services;

import com.sapient.login.domain.AccessToken;
import com.sapient.login.domain.UserCredentials;
import com.sapient.login.repository.UserProfileRepository;
import com.sapient.login.repository.entity.UserProfileEntity;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class LoginService {

    @Autowired
    private UserProfileRepository userProfileRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public AccessToken authenticate(UserCredentials userCredentials) {
        Optional<UserProfileEntity> userProfileEntity = userProfileRepository.findByEmailId(userCredentials.getEmailId());
        if (userProfileEntity.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        if (!passwordEncoder.matches(userCredentials.getPassword(), userProfileEntity.get().getPassword())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        return AccessToken.builder()
                .accessToken(getSignedJWT(userCredentials.getEmailId()))
                .tokenType("Bearer")
                .build();
    }

    public String getSignedJWT(String emailId) {
        JwtBuilder jwtBuilder = Jwts.builder();
        setHeaders(jwtBuilder);
        setClaims(jwtBuilder, emailId);
        return jwtBuilder
                //.signWith(getPrivateKey(), SignatureAlgorithm.RS256)
                .compact();
    }

    private void setHeaders(JwtBuilder jwtBuilder) {
        jwtBuilder
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", SignatureAlgorithm.RS256);
    }

    private void setClaims(JwtBuilder jwtBuilder, String emailId) {
        jwtBuilder
                .setIssuer("PSCode")
                .setSubject(emailId)
                .setAudience("PSClient")
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plus(24, ChronoUnit.HOURS)))
                .setId(UUID.randomUUID().toString());
    }

    private Key getPrivateKey() {
        return null;
    }
}
