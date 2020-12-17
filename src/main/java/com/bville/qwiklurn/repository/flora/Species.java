/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bville.qwiklurn.repository.flora;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bson.Document;
import org.bson.types.ObjectId;

/**
 * @author Bart
 */
public class Species {

    private ObjectId id;
    private String name;
    private List<ObjectId> members;
    private List<VoortplantingEnum> reproductionTechniques;
    private PricingCategory price;


    public Species(String name) {
        this.name = name;
        this.members = new ArrayList<>();
        this.reproductionTechniques = new ArrayList<>();
    }


    public Species(ObjectId id, String name, List<ObjectId> members, List<VoortplantingEnum> reproductionTechniques, PricingCategory price) {
        this.id = id;
        this.name = name;
        this.members = members;
        this.reproductionTechniques = reproductionTechniques;
        this.price = price;
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

    public List<VoortplantingEnum> getReproductionTechniques() {
        return reproductionTechniques;
    }

    public void setReproductionTechniques(List<VoortplantingEnum> reproductionTechniques) {
        this.reproductionTechniques = reproductionTechniques;
    }

    public PricingCategory getPrice() {
        return price;
    }

    public void setPrice(PricingCategory price) {
        this.price = price;
    }

    public Document toBson() {
        Document doc = new Document();

        if (getId() != null) {
            doc.put("_id", getId());
        }

        doc.put("name", getName());
        doc.put("members", getMembers());
        doc.put("reproduction", getReproductionTechniques().stream().map(VoortplantingEnum::getCode).collect(Collectors.toList()));
        doc.put("price", getPrice().name());

        return doc;
    }

    public static Species fromBson(Document doc) {
        return new Species(doc.getObjectId("_id"),
                doc.getString("name"),
                doc.getList("members", ObjectId.class),
                doc.getList("reproduction", String.class).stream().map(VoortplantingEnum::parse).collect(Collectors.toList()),
                PricingCategory.valueOf(doc.getString("price"))
        );
    }

    @Override
    public String toString() {
        return name;
    }

}
