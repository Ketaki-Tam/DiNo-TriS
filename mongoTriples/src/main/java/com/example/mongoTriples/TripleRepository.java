package com.example.mongoTriples;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;


import java.util.List;

@EnableMongoRepositories
public interface TripleRepository extends MongoRepository<TripleEntity, ObjectId> {

    @Query("{ 'subject' : ?0 }")
    List<TripleEntity> findBySubject(String subject);

}
