package com.example.neo4j;

import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface PredicateRepository extends Neo4jRepository<Predicate, Long> {
    public Predicate findByName(String name);
}