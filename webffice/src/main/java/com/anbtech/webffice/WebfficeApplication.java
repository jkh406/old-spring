package com.anbtech.webffice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
public class WebfficeApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebfficeApplication.class, args);
	}

}
