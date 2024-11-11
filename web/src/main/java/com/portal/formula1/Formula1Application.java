package com.portal.formula1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class Formula1Application {

	public static void main(String[] args) {
		SpringApplication.run(Formula1Application.class, args);
	}

}
