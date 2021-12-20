package com.sapient.login;

import com.sapient.login.controller.PingController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest()
class LoginApplicationTests {

	@Autowired
	PingController helloWorldController;

	@Test
	void contextLoads() {
		assertThat(helloWorldController).isNotNull();
	}

}
