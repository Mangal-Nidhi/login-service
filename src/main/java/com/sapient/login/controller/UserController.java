package com.sapient.login.controller;

import com.sapient.login.domain.UserProfile;
import com.sapient.login.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;

@RestController
@RequestMapping("users")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<Object> createUserProfile(@RequestBody @NotNull @Valid UserProfile userProfile,
                                                    HttpServletRequest request) {
        String userId = userService.createUserProfile(userProfile);
        return ResponseEntity
                .created(URI.create(request.getRequestURI().concat("/").concat(userId.toString())))
                .build();
    }

    @GetMapping("{userId}")
    @RolesAllowed("PS_USER")
    public ResponseEntity<UserProfile> getUserProfile(@PathVariable String userId) {
        return ResponseEntity.ok(userService.getUserProfile(userId));
    }

    @DeleteMapping("{userId}")
    @RolesAllowed("PS_USER")
    public void deleteUserProfile(@PathVariable String userId) {
        userService.deleteUserProfile(userId);
    }

    @GetMapping("{userId}/confirm")
    @RolesAllowed("PS_USER")
    public ResponseEntity<Object> confirmEmail(@PathVariable String userId) {
        userService.confirmEmailId(userId);
        return ResponseEntity.ok().build();
    }


    @ExceptionHandler(value = {EmptyResultDataAccessException.class})
    public ResponseEntity<Object> handleNoResultException(Exception ex) {
        return new ResponseEntity<>("User Id doesn't exists!", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDuplicateIdException(DataIntegrityViolationException ex) {
        return new ResponseEntity<>("Email Id is already registered!", HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = ResponseStatusException.class)
    public ResponseEntity<Object> handleResponseStatusException(ResponseStatusException ex) {
        log.error(ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), ex.getStatus());
    }

}
