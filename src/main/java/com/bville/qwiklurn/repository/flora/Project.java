/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bville.qwiklurn.repository.flora;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.bson.Document;
import org.bson.types.ObjectId;

/**
 *
 * @author Bart
 */
public class Project {

    private ObjectId id;
    private String name;
    private List<ProjectMember> members;
    private Long creationStamp;

    public Project(String name) {
        this.name = name;
        this.members = new ArrayList<>();
    }

    public Project(ObjectId id, String name, List<ProjectMember> members, Long creationStamp) {
        this.id = id;
        this.name = name;
        this.members = members;
        this.creationStamp = creationStamp;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ProjectMember> getMembers() {
        return members;
    }

    public void setMembers(List<ProjectMember> members) {
        this.members = members;
    }

    public Long getCreationStamp() {
        return creationStamp;
    }

    public void setCreationStamp(Long creationStamp) {
        this.creationStamp = creationStamp;
    }

    public Document toBson() {
        Document doc = new Document();

        if (getId() != null) {
            doc.put("_id", getId());
        }

        doc.put("name", getName());

        doc.put("members", getMembers().stream().map(ProjectMember::toBson).collect(Collectors.toList()));
        doc.put("creationStamp", getCreationStamp());

        return doc;
    }

    public static Project fromBson(Document doc) {
        List<Document> members = (List<Document>) doc.get("members");
        if(members==null){
            members = new ArrayList<>();
        }
        
        return new Project(doc.getObjectId("_id"),
                doc.getString("name"),
                members.stream().map(ProjectMember::fromBson).collect(Collectors.toList()),
                doc.getLong("creationStamp")
        );
    }

    @Override
    public String toString() {
        return name;
    }

    public boolean addMember(ProjectMember projectMember) {
        if(members.stream().noneMatch(m -> {return m.getMemberId() == projectMember.getMemberId();})){
            members.add(projectMember);
            return true;
        }
        return false;
    }

}
