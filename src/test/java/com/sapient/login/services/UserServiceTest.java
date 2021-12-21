package com.sapient.login.services;

import com.sapient.login.builder.UserProfileEntityBuilder;
import com.sapient.login.domain.AuthenticationType;
import com.sapient.login.domain.Status;
import com.sapient.login.domain.UserProfile;
import com.sapient.login.repository.UserProfileRepository;
import com.sapient.login.repository.entity.UserProfileEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserProfileRepository repository;
    @Mock
    private UserProfileEntityBuilder userProfileEntityBuilder;
    @Mock
    private EmailService emailService;
    @InjectMocks
    private UserService serviceUnderTest;

    @Test
    void verify_CreateUserProfile() throws Exception {
        UserProfile userProfile = mock(UserProfile.class);
        UserProfileEntity entity = mock(UserProfileEntity.class);
        when(userProfileEntityBuilder.build(userProfile)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(entity.getId()).thenReturn(123);
        when(userProfile.getEmailId()).thenReturn("testUser@gmail.com");
        when(emailService.getConfirmationEmailTemplate(123)).thenReturn("message");

        Integer userId = serviceUnderTest.createUserProfile(userProfile);

        assertEquals(123, userId);
        verify(userProfileEntityBuilder).build(userProfile);
        verify(repository).save(any(UserProfileEntity.class));
        verify(entity).getId();
        verify(emailService).sendEmail(eq("testUser@gmail.com"), eq("message"), eq("Verify Email"));

    }

    @Test
    void verify_GetUserProfile_WIthInvalidUserId() throws Exception {
        ResponseStatusException thrown = assertThrows(ResponseStatusException.class, () -> serviceUnderTest.getUserProfile(345));

        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
        assertEquals("404 NOT_FOUND", thrown.getMessage());
    }

    @Test
    void verify_GetUserProfile_WIthValidUserId() throws Exception {
        UserProfileEntity entity = mock(UserProfileEntity.class);
        when(repository.findById(345)).thenReturn(Optional.of(entity));
        when(entity.getId()).thenReturn(345);
        when(entity.getEmailId()).thenReturn("testUser@gmail.com");
        when(entity.getAuthType()).thenReturn(AuthenticationType.DATABASE);
        when(entity.getUserName()).thenReturn("Test User");

        UserProfile userProfile = serviceUnderTest.getUserProfile(345);

        assertEquals(345, userProfile.getUserId());
        assertEquals("testUser@gmail.com", userProfile.getEmailId());
        assertEquals("Test User", userProfile.getUserName());
        assertEquals(AuthenticationType.DATABASE, userProfile.getAuthType());
        verify(repository).findById(345);
    }

    @Test
    void verify_DeleteUserProfile() throws Exception {
        serviceUnderTest.deleteUserProfile(123);
        verify(repository).deleteById(345);
    }

    @Test
    void verify_ConfirmUserProfile() throws Exception {
        UserProfileEntity entity = mock(UserProfileEntity.class);
        when(repository.findById(123)).thenReturn(Optional.of(entity));

        serviceUnderTest.confirmEmailId(123);

        verify(repository).findById(123);
        verify(entity).setStatus(Status.ACTIVE);
        verify(repository).save(entity);
    }
}
