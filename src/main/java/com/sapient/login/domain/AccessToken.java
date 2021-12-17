package com.sapient.login.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AccessToken {

    private String accessToken;
    private String tokenType;

}
