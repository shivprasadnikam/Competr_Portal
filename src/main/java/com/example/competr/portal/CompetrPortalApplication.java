package com.example.competr.portal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CompetrPortalApplication {

	public static void main(String[] args) {
		SpringApplication.run(CompetrPortalApplication.class, args);
	}

}
