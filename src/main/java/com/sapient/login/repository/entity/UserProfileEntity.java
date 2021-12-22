package com.sapient.login.repository.entity;

import com.sapient.login.domain.AuthenticationType;
import com.sapient.login.domain.Status;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Getter
@Setter
@NoArgsConstructor
public class UserProfileEntity {

    @Id
    private String objectId;
    @Indexed(unique = true)
    private String emailId;
    private AuthenticationType authType;
    private Status status;
    private int failedLoginAttempts;
    private String userName;
    private String password;

    public UserProfileEntity(String emailId, AuthenticationType authType, Status status,
                             int failedLoginAttempts, String userName, String password) {
        this.emailId = emailId;
        this.authType = authType;
        this.status = status;
        this.failedLoginAttempts = failedLoginAttempts;
        this.userName = userName;
        this.password = password;
    }
}
