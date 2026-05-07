package com.brenluz.order_service;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OrderServiceApplication {
	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.configure()
				.directory("order-service")
				.ignoreIfMissing()
				.load();
		System.setProperty("JWT_SECRET", dotenv.get("JWT_SECRET"));
		SpringApplication.run(OrderServiceApplication.class, args);
	}
}
