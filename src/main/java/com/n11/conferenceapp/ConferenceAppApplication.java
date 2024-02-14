package com.n11.conferenceapp;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
	info = @Info(
		description = "Conference App API Documentation",
		title = "Conference App API Documentation",
		version = "v1"
	),
	servers = {
		@Server(url = "http://localhost:8081/conference-app/api")
	}
)
@SpringBootApplication(scanBasePackages = "com.n11.conferenceapp.*")
public class ConferenceAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConferenceAppApplication.class, args);
	}
}
