package com.example.neo4j;

import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.Optional;

public interface TripleRelationshipRepository extends Neo4jRepository<TripleRelationship, Long> {
    Iterable<TripleRelationship> findBySubjectName(String name);
    Optional<TripleRelationship> findBySubjectNameAndPredicateName(String subject, String predicate);
}