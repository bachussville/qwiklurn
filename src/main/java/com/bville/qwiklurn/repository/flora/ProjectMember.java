/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bville.qwiklurn.repository.flora;

import com.bville.qwiklurn.repository.flora.type.interfaces.IFloraSubType;
import java.util.List;
import org.bson.Document;
import org.bson.types.ObjectId;

/**
 *
 * @author Bart
 */
public class ProjectMember {

    private ObjectId memberId;
    private String name;
    private int areaSize;

    public ProjectMember(ObjectId memberId, String name, int areaSize) {
        this.memberId = memberId;
        this.name = name;
        this.areaSize = areaSize;
    }

    public ProjectMember(IFloraSubType floraElement) {
        if (floraElement.getId() == null) {
            throw new IllegalArgumentException("id shouldn't be null");
        }

        this.memberId = floraElement.getId();
        this.name = floraElement.getLatinName();
        this.areaSize = -1;
    }

    public ObjectId getMemberId() {
        return memberId;
    }

    public void setMemberId(ObjectId memberId) {
        this.memberId = memberId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAreaSize() {
        return areaSize;
    }

    public void setAreaSize(int areaSize) {
        this.areaSize = areaSize;
    }

    public Document toBson() {
        Document doc = new Document();
        doc.put("memberId", getMemberId());
        doc.put("name", getName());
        doc.put("areaSize", getAreaSize());
        return doc;
    }

    public static ProjectMember fromBson(Document doc) {
        return new ProjectMember(doc.getObjectId("memberId"),
                doc.getString("name"),
                doc.getInteger("areaSize"));
    }

}
