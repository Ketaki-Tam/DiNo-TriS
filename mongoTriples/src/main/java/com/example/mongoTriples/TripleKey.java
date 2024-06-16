package com.example.mongoTriples;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class TripleKey implements Serializable {
    private String firstKey;
    private String secondKey;

    public TripleKey(){}

    public TripleKey(String firstKey, String secondKey) {
        this.firstKey = firstKey;
        this.secondKey = secondKey;
    }

    public String getFirstKey() {
        return firstKey;
    }

    public void setFirstKey(String firstKey) {
        this.firstKey = firstKey;
    }

    public String getSecondKey() {
        return secondKey;
    }

    public void setSecondKey(String secondKey) {
        this.secondKey = secondKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TripleKey tripleKey = (TripleKey) o;
        return Objects.equals(firstKey, tripleKey.firstKey) && Objects.equals(secondKey, tripleKey.secondKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstKey, secondKey);
    }

}