package com.sapient.login.services;

import com.sapient.login.domain.Status;
import com.sapient.login.domain.UserProfile;
import com.sapient.login.repository.UserProfileRepository;
import com.sapient.login.repository.entity.UserProfileEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserProfileRepository userProfileRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public Integer createUserProfile(UserProfile userProfile) {
        UserProfileEntity userProfileEntity = userProfileRepository.save(new UserProfileEntity(
                userProfile.getEmailId(),
                userProfile.getAuthType(),
                Status.UNCONFIRMED,
                0,
                userProfile.getUserName(),
                passwordEncoder.encode(userProfile.getPassword())
        ));

        return userProfileEntity.getId();
    }

    public UserProfile getUserProfile(Integer userId) {
        Optional<UserProfileEntity> userEntity = userProfileRepository.findById(userId);
        if (userEntity.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return new UserProfile.Builder()
                .withUserId(userEntity.get().getId())
                .withEmailId(userEntity.get().getEmailId())
                .withAuthType(userEntity.get().getAuthType())
                .withUserName(userEntity.get().getUserName())
                .build();

    }

    public void deleteUserProfile(Integer userId) {
        userProfileRepository.deleteById(userId);
    }
}
