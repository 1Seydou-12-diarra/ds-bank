package com.said.dsbank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;


@SpringBootApplication
@EnableAsync
public class DsbankApplication {

	public static void main(String[] args) {
		SpringApplication.run(DsbankApplication.class, args);
	}

}
