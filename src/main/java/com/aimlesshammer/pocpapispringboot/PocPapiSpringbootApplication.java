package com.aimlesshammer.pocpapispringboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PocPapiSpringbootApplication {

	public static final String LOG_ID = "ah-poc-papi-logger";

	public static void main(String[] args) {
		SpringApplication.run(PocPapiSpringbootApplication.class, args);
	}

}
