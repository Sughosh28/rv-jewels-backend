package com.rv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class RvApplication {

	public static void main(String[] args) {
		SpringApplication.run(RvApplication.class, args);
	}

}