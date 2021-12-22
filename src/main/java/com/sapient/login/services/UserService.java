package com.sapient.login.services;

import com.sapient.login.builder.UserProfileEntityBuilder;
import com.sapient.login.domain.Status;
import com.sapient.login.domain.UserProfile;
import com.sapient.login.repository.UserProfileRepository;
import com.sapient.login.repository.entity.UserProfileEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserProfileRepository userProfileRepository;
    @Autowired
    private UserProfileEntityBuilder userProfileEntityBuilder;
    @Autowired
    private EmailService emailService;

    public String createUserProfile(UserProfile userProfile) {
        String userId = saveUser(userProfile);
        log.info("Added new user with email={}", userProfile.getEmailId());
        emailService.sendEmail(userProfile.getEmailId(), emailService.getConfirmationEmailTemplate(userId), "Verify Email");
        return userId;
    }

    private String saveUser(UserProfile userProfile) {
        UserProfileEntity userProfileEntity = userProfileRepository.save(
                userProfileEntityBuilder.build(userProfile));
        return userProfileEntity.getObjectId();
    }

    public UserProfile getUserProfile(String userId) {
        Optional<UserProfileEntity> userEntity = userProfileRepository.findById(userId);
        if (userEntity.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        log.info("Returning profile for user with id={}", userId);
        return new UserProfile.Builder()
                .withUserId(userEntity.get().getObjectId())
                .withEmailId(userEntity.get().getEmailId())
                .withAuthType(userEntity.get().getAuthType())
                .withUserName(userEntity.get().getUserName())
                .build();

    }

    @Transactional
    public void deleteUserProfile(String userId) {
        userProfileRepository.deleteById(userId);
        log.info("Deleted profile for user with id={}", userId);
    }

    @Transactional
    public void confirmEmailId(String userId) {
        Optional<UserProfileEntity> entity = userProfileRepository.findById(userId);
        entity.ifPresent(userProfileEntity -> {
            userProfileEntity.setStatus(Status.ACTIVE);
            userProfileRepository.save(userProfileEntity);
            log.info("Confirmed account for user with id={}", userId);
        });
    }
}
