package com.flottevehicule.flottevehicule;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FlottevehiculeApplication {

	public static void main(String[] args) {
		SpringApplication.run(FlottevehiculeApplication.class, args);
	}

}