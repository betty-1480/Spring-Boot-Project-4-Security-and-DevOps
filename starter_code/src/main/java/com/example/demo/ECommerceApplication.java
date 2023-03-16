package com.example.demo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@EnableJpaRepositories("com.example.demo.model.persistence.repositories") //Why? - because repositories are not in the direct subpackage of main application
@EntityScan("com.example.demo.model.persistence") //why? - because @Entity classes are not in the direct subpackage of main application
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class ECommerceApplication {

	private static final Logger logger=LogManager.getLogger(ECommerceApplication.class);
	
	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder(){
		return new BCryptPasswordEncoder();
	}

	public static void main(String[] args) {
		logger.info("Application started");
		SpringApplication.run(ECommerceApplication.class, args);
	}

}
