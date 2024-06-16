package com.example.neo4j;

import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Node;

@Node("Subject")
public class Subject {
    @Id
    private Long id;
    private String name;
    private static Long count = 0L;

    public Subject(Long id, String name) {
        this.id = id;
        this.name = name;
    }
    public Subject(String name)
    {
        this.id = count;
        count = count+1;
        this.name = name;
    }

    public Subject(){
        this.id = count;
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
        Subject.count = count;
    }

    public void setName(String name) {
        this.name = name;
    }
}