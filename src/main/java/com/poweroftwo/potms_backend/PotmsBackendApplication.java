package com.poweroftwo.potms_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PotmsBackendApplication {
	public static void main(String[] args) {
		SpringApplication.run(PotmsBackendApplication.class, args);
	}

}