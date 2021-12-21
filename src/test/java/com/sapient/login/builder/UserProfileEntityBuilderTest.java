package com.sapient.login.builder;

import com.sapient.login.domain.AuthenticationType;
import com.sapient.login.domain.Status;
import com.sapient.login.domain.UserProfile;
import com.sapient.login.repository.entity.UserProfileEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserProfileEntityBuilderTest {

    @Mock
    private PasswordEncoder encoder;
    @InjectMocks
    private UserProfileEntityBuilder builderUnderTest;

    @Test
    void verify_buildUserProfileEntity() throws Exception {
        when(encoder.encode("Password")).thenReturn("hashedPassword");
        UserProfileEntity entity = builderUnderTest.build(new UserProfile.Builder()
                .withUserName("Test User")
                .withAuthType(AuthenticationType.DATABASE)
                .withEmailId("testUser@gmail.com")
                .withPassword("Password")
                .build());

        assertEquals("Test User", entity.getUserName());
        assertEquals("hashedPassword", entity.getPassword());
        assertEquals(AuthenticationType.DATABASE, entity.getAuthType());
        assertEquals("testUser@gmail.com", entity.getEmailId());
        assertEquals(0, entity.getFailedLoginAttempts());
        assertEquals(Status.CONFIRM_PENDING, entity.getStatus());
        verify(encoder).encode("Password");
    }


}
