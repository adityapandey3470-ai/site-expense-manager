package com.aditya.siteexpensemanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class SiteExpenseManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SiteExpenseManagerApplication.class, args);
	}

}
