package com.ecom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.github.javafaker.Faker;

@SpringBootApplication
public class employee {

	public static void main(String[] args) {
		SpringApplication.run(employee.class, args);
	}
	
	@Bean
    public Faker faker() {
        return new Faker();
    }
	

}
