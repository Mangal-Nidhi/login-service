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
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
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

    public Integer createUserProfile(UserProfile userProfile) {
        Integer userId = saveUser(userProfile);
        log.info("Added new user with email={}", userProfile.getEmailId());
        emailService.sendEmail(userProfile.getEmailId(), emailService.getConfirmationEmailTemplate(userId), "Verify Email");
        return userId;
    }

    @Transactional
    private Integer saveUser(UserProfile userProfile) {
        UserProfileEntity userProfileEntity = userProfileRepository.save(
                userProfileEntityBuilder.build(userProfile));
        return userProfileEntity.getId();
    }

    public UserProfile getUserProfile(Integer userId) {
        Optional<UserProfileEntity> userEntity = userProfileRepository.findById(userId);
        if (userEntity.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        log.info("Returning profile for user with id={}", userId);
        return new UserProfile.Builder()
                .withUserId(userEntity.get().getId())
                .withEmailId(userEntity.get().getEmailId())
                .withAuthType(userEntity.get().getAuthType())
                .withUserName(userEntity.get().getUserName())
                .build();

    }

    @Transactional
    public void deleteUserProfile(Integer userId) {
        userProfileRepository.deleteById(userId);
        log.info("Deleted profile for user with id={}", userId);
    }

    @Transactional
    public void confirmEmailId(Integer userId) {
        Optional<UserProfileEntity> entity = userProfileRepository.findById(userId);
        entity.ifPresent(userProfileEntity -> {
            userProfileEntity.setStatus(Status.ACTIVE);
            userProfileRepository.save(userProfileEntity);
            log.info("Confirmed account for user with id={}", userId);
        });
    }
}
