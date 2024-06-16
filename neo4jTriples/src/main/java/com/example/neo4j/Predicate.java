package com.example.neo4j;

import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.Node;


public class Predicate {
    @Id
    private Long id;
    private String name;
    private static Long count = 0L;
    // Additional properties (e.g., description)

    public Predicate(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Predicate(String name)
    {
        this.id = count;
        count += 1;
        this.name = name;
    }


    public Predicate(){
        this.id = count;
        count += 1;
        this.name = "";
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public static void setCount(Long count) {
        Predicate.count = count;
    }

    public void setName(String name) {
        this.name = name;
    }
}