package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface TripleRepository extends JpaRepository<TripleEntity, TripleKey> {

    @Query(value = "SELECT * FROM triple t where t.subject = :subject",nativeQuery = true)
    List<TripleEntity> findBySubject(@Param("subject")String subject);
}
