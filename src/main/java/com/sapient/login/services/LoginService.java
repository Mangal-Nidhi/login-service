package com.sapient.login.services;

import com.sapient.login.builder.JWTBuilder;
import com.sapient.login.builder.UserProfileEntityBuilder;
import com.sapient.login.domain.LoginResponse;
import com.sapient.login.domain.Status;
import com.sapient.login.domain.UserCredentials;
import com.sapient.login.repository.UserProfileRepository;
import com.sapient.login.repository.entity.UserProfileEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Slf4j
public class LoginService {

    @Autowired
    private UserProfileRepository userProfileRepository;
    @Autowired
    private UserProfileEntityBuilder userProfileEntityBuilder;
    @Autowired
    private JWTBuilder jwtBuilder;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public LoginResponse authenticate(UserCredentials userCredentials) {
        Optional<UserProfileEntity> userProfileEntity = userProfileRepository.findByEmailId(userCredentials.getEmailId());
        if (userProfileEntity.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Email Id doesn't exist!");
        }

        verifyAccountActive(userProfileEntity.get());

        int loginAttemptCount = userProfileEntity.get().getFailedLoginAttempts();
        boolean authenticated = verifyCredentials(userProfileEntity.get(), userCredentials, loginAttemptCount);
        if (!authenticated) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Incorrect EmailId or password!");
        }

        resetFailedAttemptCountOnSuccess(userProfileEntity.get(), loginAttemptCount);

        return LoginResponse.builder()
                .accessToken(jwtBuilder.getSignedJWT(userCredentials.getEmailId()))
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
                log.warn("Locked user with id={}", userProfileEntity.getId());
            }
            userProfileRepository.save(userProfileEntity);
            log.info("User credentials not valid for id={}", userProfileEntity.getId());
            return false;
        }
        log.info("User authenticated with id={}", userProfileEntity.getId());
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
}
