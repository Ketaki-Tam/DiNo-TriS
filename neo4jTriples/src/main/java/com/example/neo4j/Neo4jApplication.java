package com.example.neo4j;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;


@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
public class Neo4jApplication {

	public static void main(String[] args) {
		SpringApplication.run(Neo4jApplication.class, args);
	}
//to delete all triples in the database
//	@Bean
//	CommandLineRunner demo(TripleRelationshipRepository tripleRepository, SubjectRepository subjectRepository, ObjectRepository objectRepository,PredicateRepository predicateRepository, TripleService tripleService) {
//		return args -> {
//
//			System.out.println("Deleting");
//			tripleRepository.deleteAll();
//			subjectRepository.deleteAll();
//			objectRepository.deleteAll();
//			predicateRepository.deleteAll();
//
//		};
//	}

}
