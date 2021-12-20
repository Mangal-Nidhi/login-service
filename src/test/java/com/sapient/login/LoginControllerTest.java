package com.sapient.login;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sapient.login.controller.LoginController;
import com.sapient.login.domain.LoginResponse;
import com.sapient.login.domain.UserCredentials;
import com.sapient.login.services.LoginService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = LoginController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
class LoginControllerTest {

    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    private MockMvc mvc;
    @MockBean
    private LoginService service;

    @Test
    void verify_Login_ForInvalidPasswordCredentials_returns401() throws Exception {
        when(service.authenticate(any(UserCredentials.class))).thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        mvc.perform(MockMvcRequestBuilders
                        .post("/login")
                        .content(objectMapper.writeValueAsBytes(new UserCredentials("testUser", "abc@123")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void verify_Login_WithInvalidEmailId_returns404() throws Exception {
        when(service.authenticate(any(UserCredentials.class))).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));
        mvc.perform(MockMvcRequestBuilders
                        .post("/login")
                        .content(objectMapper.writeValueAsBytes(new UserCredentials("testUser", "abc@123")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void verify_Login_WithValidCredentials_returns200() throws Exception {
        when(service.authenticate(any(UserCredentials.class))).thenReturn(new LoginResponse("gregre", "bearer"));
        mvc.perform(MockMvcRequestBuilders
                        .post("/login")
                        .content(objectMapper.writeValueAsBytes(new UserCredentials("testUser", "abc@123")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
