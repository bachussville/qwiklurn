/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bville.qwiklurn.repository.flora;

import java.util.List;
import java.util.stream.Collectors;
import org.bson.Document;
import org.bson.types.ObjectId;

/**
 *
 * @author Bart
 */
public class Species {

    private ObjectId id;
    private String name;
    private List<ObjectId> members;

    public Species(ObjectId id, String name, List<ObjectId> members) {
        if (id == null){
            this.id = ObjectId.get();
        }else{
            this.id = id;
        }
        this.name = name;
        this.members = members;
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

    public List<ObjectId> getMembers() {
        return members;
    }

    public void setMembers(List<ObjectId> members) {
        this.members = members;
    }

    public Document toBson() {
        Document doc = new Document();
        
        doc.put("name", getName());
        
        doc.put("members", getMembers());        
        
        return doc;
    }

    public static Species fromBson(Document doc) {
        return new Species(doc.getObjectId("_id")
                , doc.getString("name")
                , doc.getList("members", ObjectId.class));
    }    

    @Override
    public String toString() {
        return name;
    }

    
}
