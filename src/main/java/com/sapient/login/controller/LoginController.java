package com.sapient.login.controller;

import com.sapient.login.domain.LoginResponse;
import com.sapient.login.domain.UserCredentials;
import com.sapient.login.services.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@RestController
@RequestMapping("login")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid UserCredentials userCredentials) {
        return ResponseEntity.ok(loginService.authenticate(userCredentials));
    }

    @ExceptionHandler(value = ResponseStatusException.class)
    public ResponseEntity<Object> handleResponseStatusException(ResponseStatusException ex) {
        return new ResponseEntity<Object>(ex.getMessage(), ex.getStatus());
    }
}
