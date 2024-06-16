package com.example.mongoTriples;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class MongoTriplesApplication {

	public static void main(String[] args) {
		SpringApplication.run(MongoTriplesApplication.class, args);
	}

}

