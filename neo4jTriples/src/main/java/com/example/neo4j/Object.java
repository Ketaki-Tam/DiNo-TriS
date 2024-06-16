package com.example.neo4j;


import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("Object")
public class Object {
    @Id
    private Long id;
    private String name;
    private static Long count = 0L;

    public Object(Long id, String value) {
        this.id = id;
        count = id+1;
        this.name = value;
    }

    public Object(String name)
    {
        this.id = count;
        count += 1;
        this.name = name;
    }


    public Object(){
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

    public static Long getCount() {
        return count;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public static void setCount(Long count) {
        Object.count = count;
    }

    public void setName(String name) {
        this.name = name;
    }
}