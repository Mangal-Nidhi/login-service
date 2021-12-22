package com.sapient.login.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserProfile {

    private String userId;
    @NotNull(message = "EmailId can't be null")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@(.+)$", message = "EmailId format is not valid.")
    private String emailId;
    @NotNull(message = "userName can't be null")
    @Size(min = 2, max = 20, message = "userName length should be between 2 to 20")
    private String userName;
    @NotNull(message = "password can't be null")
    @Size(min = 8, max = 20, message = "password length should be between 2 to 20")
    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,20}$", message = "password should contain atleast 1 upper case, 1 lower case and 1 digit")
    private String password;
    @NotNull
    private AuthenticationType authType;

    public static class Builder {
        private String userId;
        private String emailId;
        private String userName;
        private String password;
        private AuthenticationType authType;

        public Builder withUserId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder withEmailId(String emailId) {
            this.emailId = emailId;
            return this;
        }

        public Builder withUserName(String userName) {
            this.userName = userName;
            return this;
        }

        public Builder withPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder withAuthType(AuthenticationType authType) {
            this.authType = authType;
            return this;
        }

        public UserProfile build() {
            UserProfile profile = new UserProfile();
            profile.userId = this.userId;
            profile.emailId = this.emailId;
            profile.userName = this.userName;
            profile.password = this.password;
            profile.authType = this.authType;
            return profile;
        }
    }
}
