package com.sapient.login.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
@Slf4j
public class EmailService {

    public void sendEmail(String toEmailId, String message, String subject) {
        String recipient = toEmailId;
        String sender = "no-reply@pscode.com";

        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.socketFactory.port", "465");
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("", "");
            }

        });
        try {
            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(new InternetAddress(sender));
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            mimeMessage.setSubject(subject);
            mimeMessage.setContent("<h1>" + message + "</h1>", "text/html");
            Transport.send(mimeMessage);
        } catch (MessagingException ex) {
            log.warn("Something went wrong while sending email. {}", ex.getMessage());
        }
    }

    public String getConfirmationEmailTemplate(Integer userId) {
        return "<a href=\"http://localhost:8083/users/" + userId + "/confirm\">Verify Email</a>";
    }
}


