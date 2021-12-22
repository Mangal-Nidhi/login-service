package com.sapient.login.services;

import org.springframework.stereotype.Service;

@Service
public class EmailService {

    public void sendEmail(String toEmailId, String message, String subject) {

    }

    public String getConfirmationEmailTemplate(String userId) {
        return "<a href=\"http://localhost:8083/users/" + userId + "/confirm\">Verify Email</a>";
    }
}


