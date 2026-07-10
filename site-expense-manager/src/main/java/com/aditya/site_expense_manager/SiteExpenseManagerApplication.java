package com.aditya.site_expense_manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

import java.sql.DatabaseMetaData;

@SpringBootApplication
public class SiteExpenseManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SiteExpenseManagerApplication.class, args);
	}

}
