package com.example.neo4j;

import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface SubjectRepository extends Neo4jRepository<Subject, Long> {
    public Subject findByName(String name);
}