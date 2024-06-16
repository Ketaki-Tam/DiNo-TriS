package com.example.neo4j;

import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

@Node
public class TripleRelationship {

    @Id
    @GeneratedValue
    private Long id;

    @Relationship(type = "Subject")
    private Subject subject;

    @Relationship(type = "Predicate")
    private Predicate predicate;  // Use String or Predicate entity based on your choice

    @Relationship(type = "Object")
    private Object object;

    public TripleRelationship(Subject subject, Predicate predicate, Object object) {
        this.id = null;  // ID is auto-generated
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
    }

    public TripleRelationship(Long id, Subject subject, Predicate predicate, Object object) {
        this.id = id;
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
    }

    public TripleRelationship(){}

    public Long getId() {
        return id;
    }

    public Subject getSubject()
    {
        return this.subject;
    }

    public Predicate getPredicate() {
        return this.predicate;
    }

    public Object getObject() {
        return this.object;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public void setPredicate(Predicate predicate) {
        this.predicate = predicate;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}