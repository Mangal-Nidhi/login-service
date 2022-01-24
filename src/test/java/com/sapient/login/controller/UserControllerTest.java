package com.sapient.login.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sapient.login.domain.AuthenticationType;
import com.sapient.login.domain.UserProfile;
import com.sapient.login.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = UserController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
@ImportAutoConfiguration(RefreshAutoConfiguration.class)
class UserControllerTest {

    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    private MockMvc mvc;
    @MockBean
    private UserService service;

    @BeforeEach
    void setUp() {
        when(service.createUserProfile(any(UserProfile.class))).thenReturn("123");
    }

    @Test
    void verify_createUserProfile_WithValidDetails_returns200() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .content(objectMapper.writeValueAsString(new UserProfile.Builder()
                                .withEmailId("test_user@ps.com")
                                .withPassword("Sapient@123")
                                .withUserName("newUser")
                                .withAuthType(AuthenticationType.DATABASE)
                                .build()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().stringValues("Location", "/users/123"));
    }

    @Test
    void verify_createUserProfile_WithInValidEmail_returns400() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .content(new ObjectMapper().writeValueAsBytes(new UserProfile.Builder()
                                .withEmailId("@ps.com")
                                .withPassword("Sapient@123")
                                .withUserName("newUser")
                                .withAuthType(AuthenticationType.DATABASE)
                                .build()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void verify_createUserProfile_WithInValidPassword_returns400() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .content(new ObjectMapper().writeValueAsBytes(new UserProfile.Builder()
                                .withEmailId("test_user@ps.com")
                                .withPassword("sapient@123")
                                .withUserName("newUser")
                                .withAuthType(AuthenticationType.DATABASE)
                                .build()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void verify_createUserProfile_WithInValidUsername_returns400() throws Exception {
        String error = mvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .content(new ObjectMapper().writeValueAsBytes(new UserProfile.Builder()
                                .withEmailId("test_user@ps.com")
                                .withPassword("Sapient@123")
                                .withUserName("newUsergrgrtgrtgtrgrtgrtgtgtgt")
                                .withAuthType(AuthenticationType.DATABASE)
                                .build()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn().getResolvedException().getMessage();

        assertEquals(true, error.contains("userName length should be between 2 to 20"));
    }

    @Test
    void verify_createUserProfile_WithEmptyPayload_returns400() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .content("")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void verify_getUserProfile_returns200() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .get("/users/123"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void verify_getUserProfile_WithInvalidUserId_returns404() throws Exception {
        when(service.getUserProfile("123")).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));
        mvc.perform(MockMvcRequestBuilders
                        .get("/users/123"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void verify_deleteUserProfile_returns200() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .delete("/users/123"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void verify_ConfirmUserProfile_returns200() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .get("/users/123/confirm"))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
