package com.sapient.login;

import com.sapient.login.controller.LoginController;
import com.sapient.login.controller.PingController;
import com.sapient.login.controller.UserController;
import com.sapient.login.services.LoginService;
import com.sapient.login.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class LoginApplicationTests {

	@Autowired
	PingController pingController;
	@Autowired
	UserController userController;
	@Autowired
	UserService userService;
	@Autowired
	LoginController loginController;
	@Autowired
	LoginService loginService;

	@Test
	void contextLoads() {
		assertThat(pingController).isNotNull();
		assertThat(userController).isNotNull();
		assertThat(loginController).isNotNull();
		assertThat(userService).isNotNull();
		assertThat(loginService).isNotNull();
	}

}
