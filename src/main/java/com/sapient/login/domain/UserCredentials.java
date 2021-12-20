package com.sapient.login.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
public class UserCredentials {

    @NotNull
    @NotEmpty
    private String emailId;
    @NotNull
    @NotEmpty
    private String password;
}
