package com.ecapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ProyectoTesisApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProyectoTesisApplication.class, args);
	}

}
