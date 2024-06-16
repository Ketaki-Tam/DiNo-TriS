package com.example.demo;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

@Entity
@Table(name="triple")
@Builder
@Data
public class TripleEntity {
    @EmbeddedId
    @AttributeOverrides({
            @AttributeOverride(name = "firstKey", column = @Column(name = "subject")),
            @AttributeOverride(name = "secondKey", column = @Column(name = "predicate"))
    })
    private TripleKey id;
    @Column(name="object")
    private String object;

    public TripleEntity() {}

    public TripleEntity(TripleKey id, String object) {
        this.id = id;
        this.object = object;
    }

    public TripleKey getId() {
        return id;
    }

    public void setId(TripleKey id) {
        this.id = id;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }
}
