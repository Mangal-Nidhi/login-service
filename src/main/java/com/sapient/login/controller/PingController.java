package com.sapient.login.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@Configuration
public class PingController {

    @Value("${login.message:hello}")
    private String message;

    @GetMapping("/ping")
    public String helloWorld() {
        return message;
    }
}
