package com.sapient.login.controller;

import com.sapient.login.domain.UserProfile;
import com.sapient.login.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;

@RestController
@RequestMapping("users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity createUserProfile(@RequestBody @NotNull @Valid UserProfile userProfile,
                                            HttpServletRequest request) {
        Integer userId = userService.createUserProfile(userProfile);
        return ResponseEntity
                .created(URI.create(request.getRequestURI().concat("/").concat(userId.toString())))
                .build();
    }

    @GetMapping("{userId}")
    public ResponseEntity<UserProfile> getUserProfile(@PathVariable Integer userId) {
        return ResponseEntity.ok(userService.getUserProfile(userId));
    }

    @DeleteMapping("{userId}")
    public void deleteUserProfile(@PathVariable Integer userId) {
        userService.deleteUserProfile(userId);
    }

    @ExceptionHandler(value = {EntityNotFoundException.class, EmptyResultDataAccessException.class})
    public ResponseEntity<Object> handleNoResultException(Exception ex) {
        return new ResponseEntity<Object>("User Id doesn't exists!", HttpStatus.NOT_FOUND);
    }
}
