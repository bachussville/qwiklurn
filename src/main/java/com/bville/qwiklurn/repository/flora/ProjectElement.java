/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bville.qwiklurn.repository.flora;

import com.bville.qwiklurn.repository.flora.type.interfaces.IFloraSubType;
import org.bson.Document;
import org.bson.types.ObjectId;

/**
 *
 * @author Bart
 */
public class ProjectElement {

    private ObjectId elementId;
    private String name;
    private int areaSize;

    public ProjectElement(ObjectId elementId, String name, int areaSize) {
        this.elementId = elementId;
        this.name = name;
        this.areaSize = areaSize;
    }

    public ProjectElement(IFloraSubType floraElement) {
        if (floraElement.getId() == null) {
            throw new IllegalArgumentException("id shouldn't be null");
        }

        this.elementId = floraElement.getId();
        this.name = floraElement.getLatinName();
        this.areaSize = -1;
    }

    public ObjectId getElementId() {
        return elementId;
    }

    public void setElementId(ObjectId elementId) {
        this.elementId = elementId;
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
        doc.put("memberId", getElementId());
        doc.put("name", getName());
        doc.put("areaSize", getAreaSize());
        return doc;
    }

    public static ProjectElement fromBson(Document doc) {
        return new ProjectElement(doc.getObjectId("memberId"),
                doc.getString("name"),
                doc.getInteger("areaSize"));
    }

}
