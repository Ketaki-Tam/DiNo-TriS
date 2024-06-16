//package com.example.neo4j;
//
//import org.springframework.data.annotation.Id;
//import org.springframework.data.neo4j.core.schema.Node;
//import org.springframework.data.neo4j.core.schema.Relationship;
//
//import java.util.Set;
//
//@Node("Triple")
//public class TripleEntity {
//
//    @Id
//    private Long id;
//    private String name;
//
//    @Relationship(type = "Predicate") // Generic relationship type
//    private Set<RelatedEntity> relatedEntities;
//
//    public static class RelatedEntity {
//        private TripleEntity object;
//        private String predicate;
//
//        public RelatedEntity(TripleEntity object, String predicate) {
//            this.object = object;
//            this.predicate = predicate;
//        }
//
//        public TripleEntity getObject() {
//            return object;
//        }
//
//        public void setObject(TripleEntity object) {
//            this.object = object;
//        }
//
//        public String getPredicate() {
//            return predicate;
//        }
//
//        public void setPredicate(String predicate) {
//            this.predicate = predicate;
//        }
//    }
//
//    public TripleEntity(String name, Set<RelatedEntity> relatedEntities) {
//        this.id = id;
//        this.name = name;
//        this.relatedEntities = relatedEntities;
//    }
//
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public Set<RelatedEntity> getRelatedEntities() {
//        return relatedEntities;
//    }
//
//    public void setRelatedEntities(Set<RelatedEntity> relatedEntities) {
//        this.relatedEntities = relatedEntities;
//    }
//}
