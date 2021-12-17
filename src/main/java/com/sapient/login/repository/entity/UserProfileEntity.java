package com.sapient.login.repository.entity;

import com.sapient.login.domain.AuthenticationType;
import com.sapient.login.domain.Status;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class UserProfileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @Column(nullable = false, unique = true)
    private String emailId;
    @Column(nullable = false)
    private AuthenticationType authType;
    @Column(nullable = false)
    private Status status;
    @Column(nullable = false)
    private int failedLoginAttempts;
    @Column(nullable = false)
    private String userName;
    @Column
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
