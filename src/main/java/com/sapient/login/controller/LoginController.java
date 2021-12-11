package com.sapient.login.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @Value("${name}")
    private String name;

    @GetMapping("/")
    public String helloWorld(){
        return "Hello " + name + "!";
    }
}
