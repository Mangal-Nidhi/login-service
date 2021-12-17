package com.sapient.login.builder;

import com.sapient.login.domain.Status;
import com.sapient.login.domain.UserProfile;
import com.sapient.login.repository.entity.UserProfileEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserProfileEntityBuilder {

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserProfileEntity build(UserProfile userProfile) {
        return new UserProfileEntity(
                userProfile.getEmailId(),
                userProfile.getAuthType(),
                Status.CONFIRM_PENDING,
                0,
                userProfile.getUserName(),
                passwordEncoder.encode(userProfile.getPassword())
        );
    }
}
