package com.sapient.login.controller;

import com.sapient.login.domain.AccessToken;
import com.sapient.login.domain.UserCredentials;
import com.sapient.login.services.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("login")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping
    public ResponseEntity<AccessToken> login(@RequestBody @Valid UserCredentials userCredentials) {
        return ResponseEntity.ok(loginService.authenticate(userCredentials));
    }
}
