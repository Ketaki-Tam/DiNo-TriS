package com.example.neo4j;

import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface ObjectRepository extends Neo4jRepository<Object, Long> {
    public Object findByName(String name);
}