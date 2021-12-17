package com.sapient.login.services;

import com.sapient.login.builder.UserProfileEntityBuilder;
import com.sapient.login.domain.LoginResponse;
import com.sapient.login.domain.Status;
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

import javax.transaction.Transactional;
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
    private UserProfileEntityBuilder userProfileEntityBuilder;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public LoginResponse authenticate(UserCredentials userCredentials) {
        Optional<UserProfileEntity> userProfileEntity = userProfileRepository.findByEmailId(userCredentials.getEmailId());
        if (userProfileEntity.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Email Id doesn't exists!");
        }

        int loginAttemptCount = userProfileEntity.get().getFailedLoginAttempts();
        verifyAccountActive(userProfileEntity.get());

        boolean authenticated = verifyCredentials(userProfileEntity.get(), userCredentials, loginAttemptCount);
        if (!authenticated) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Incorrect EmailId or password!");
        }

        resetFailedAttemptCountOnSuccess(userProfileEntity.get(), loginAttemptCount);

        return LoginResponse.builder()
                .accessToken(getSignedJWT(userCredentials.getEmailId()))
                .tokenType("Bearer")
                .build();
    }

    private void verifyAccountActive(UserProfileEntity userProfileEntity) {
        if (Status.LOCKED.equals(userProfileEntity.getStatus())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Account is locked due to 3 consecutive failure attempts, please contact admin!");
        }
        if (Status.CONFIRM_PENDING.equals(userProfileEntity.getStatus())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Verify registered email to access the account.");
        }
    }

    @Transactional
    private boolean verifyCredentials(UserProfileEntity userProfileEntity, UserCredentials userCredentials, int loginAttemptCount) {
        if (!passwordEncoder.matches(userCredentials.getPassword(), userProfileEntity.getPassword())) {
            userProfileEntity.setFailedLoginAttempts(loginAttemptCount + 1);
            if (loginAttemptCount == 2) {
                userProfileEntity.setStatus(Status.LOCKED);
            }
            userProfileRepository.save(userProfileEntity);
            return false;
        }
        return true;
    }

    @Transactional
    private void resetFailedAttemptCountOnSuccess(UserProfileEntity userProfileEntity, int loginAttemptCount) {
        if (loginAttemptCount > 0) {
            userProfileEntity.setFailedLoginAttempts(0);
            userProfileEntity.setStatus(Status.ACTIVE);
            userProfileRepository.save(userProfileEntity);
        }
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
