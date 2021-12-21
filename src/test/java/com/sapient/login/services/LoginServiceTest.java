package com.sapient.login.services;

import com.sapient.login.builder.JWTBuilder;
import com.sapient.login.builder.UserProfileEntityBuilder;
import com.sapient.login.domain.Status;
import com.sapient.login.domain.UserCredentials;
import com.sapient.login.repository.UserProfileRepository;
import com.sapient.login.repository.entity.UserProfileEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoginServiceTest {

    @Mock
    private UserProfileRepository repository;
    @Mock
    private UserProfileEntityBuilder userProfileEntityBuilder;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JWTBuilder jwtBuilder;
    @InjectMocks
    private LoginService loginService;

    @Test
    public void test_authenticate_WithInValidEmailId() {
        when(repository.findByEmailId("testUser@ps.com")).thenReturn(Optional.empty());
        ResponseStatusException thrown = assertThrows(ResponseStatusException.class,
                () -> loginService.authenticate(new UserCredentials("testUser@ps.com", "password")));
        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
        assertEquals("404 NOT_FOUND \"Email Id doesn't exist!\"", thrown.getMessage());
    }

    @Test
    public void test_authenticate_WithInactiveAccount() {
        UserProfileEntity entity = mock(UserProfileEntity.class);
        when(repository.findByEmailId("testUser@ps.com")).thenReturn(Optional.of(entity));
        when(entity.getStatus()).thenReturn(Status.CONFIRM_PENDING);
        ResponseStatusException thrown = assertThrows(ResponseStatusException.class,
                () -> loginService.authenticate(new UserCredentials("testUser@ps.com", "password")));
        assertEquals(HttpStatus.FORBIDDEN, thrown.getStatus());
        assertEquals("403 FORBIDDEN \"Verify registered email to access the account.\"", thrown.getMessage());
    }

    @Test
    public void test_authenticate_WithLockedAccount() {
        UserProfileEntity entity = mock(UserProfileEntity.class);
        when(repository.findByEmailId("testUser@ps.com")).thenReturn(Optional.of(entity));
        when(entity.getStatus()).thenReturn(Status.LOCKED);
        ResponseStatusException thrown = assertThrows(ResponseStatusException.class,
                () -> loginService.authenticate(new UserCredentials("testUser@ps.com", "password")));
        assertEquals(HttpStatus.FORBIDDEN, thrown.getStatus());
        assertEquals("403 FORBIDDEN \"Account is locked due to 3 consecutive failure attempts, please contact admin!\"", thrown.getMessage());
    }

    @Test
    public void test_authenticate_WithInvalidPassword() {
        UserProfileEntity entity = mock(UserProfileEntity.class);
        when(repository.findByEmailId("testUser@ps.com")).thenReturn(Optional.of(entity));
        when(entity.getStatus()).thenReturn(Status.ACTIVE);
        when(entity.getFailedLoginAttempts()).thenReturn(0);
        when(entity.getPassword()).thenReturn("bjhgjhjgh");
        ResponseStatusException thrown = assertThrows(ResponseStatusException.class,
                () -> loginService.authenticate(new UserCredentials("testUser@ps.com", "password")));

        assertEquals(HttpStatus.UNAUTHORIZED, thrown.getStatus());
        assertEquals("401 UNAUTHORIZED \"Incorrect EmailId or password!\"", thrown.getMessage());

        verify(entity).setFailedLoginAttempts(1);
        verify(repository, times(1)).save(entity);
    }

    @Test
    public void test_authenticate_WithInvalidPassword_3FailedAttempts_LocksAccount() {
        UserProfileEntity entity = mock(UserProfileEntity.class);
        when(repository.findByEmailId("testUser@ps.com")).thenReturn(Optional.of(entity));
        when(entity.getStatus()).thenReturn(Status.ACTIVE);
        when(entity.getFailedLoginAttempts()).thenReturn(2);
        when(entity.getPassword()).thenReturn("bnnbbnbnv");
        ResponseStatusException thrown = assertThrows(ResponseStatusException.class,
                () -> loginService.authenticate(new UserCredentials("testUser@ps.com", "password")));

        assertEquals(HttpStatus.UNAUTHORIZED, thrown.getStatus());
        assertEquals("401 UNAUTHORIZED \"Incorrect EmailId or password!\"", thrown.getMessage());

        verify(entity).setFailedLoginAttempts(3);
        verify(entity).setStatus(Status.LOCKED);
        verify(repository, times(1)).save(entity);
    }

    @Test
    public void test_authenticate_WithValidUserCredentials() {
        UserProfileEntity entity = mock(UserProfileEntity.class);
        when(repository.findByEmailId("testUser@ps.com")).thenReturn(Optional.of(entity));
        when(entity.getStatus()).thenReturn(Status.ACTIVE);
        when(entity.getFailedLoginAttempts()).thenReturn(0);
        when(entity.getPassword()).thenReturn("password");
        when(passwordEncoder.matches("password", "password")).thenReturn(true);
        loginService.authenticate(new UserCredentials("testUser@ps.com", "password"));

        verify(repository, times(0)).save(any(UserProfileEntity.class));
        verify(jwtBuilder).getSignedJWT("testUser@ps.com");
    }

    @Test
    public void test_authenticateSuccessful_FailedLoginAttemptsResets() {
        UserProfileEntity entity = mock(UserProfileEntity.class);
        when(repository.findByEmailId("testUser@ps.com")).thenReturn(Optional.of(entity));
        when(entity.getStatus()).thenReturn(Status.ACTIVE);
        when(entity.getFailedLoginAttempts()).thenReturn(2);
        when(entity.getPassword()).thenReturn("password");
        when(passwordEncoder.matches("password", "password")).thenReturn(true);
        loginService.authenticate(new UserCredentials("testUser@ps.com", "password"));

        verify(entity).setFailedLoginAttempts(0);
        verify(entity).setStatus(Status.ACTIVE);
        verify(repository, times(1)).save(entity);
        verify(jwtBuilder).getSignedJWT("testUser@ps.com");
    }
}
