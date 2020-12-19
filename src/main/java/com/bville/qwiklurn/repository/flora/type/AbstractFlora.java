/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bville.qwiklurn.repository.flora.type;

import com.bville.qwiklurn.repository.flora.*;
import com.bville.qwiklurn.repository.flora.type.interfaces.IFloraSubType;
import com.bville.qwiklurn.repository.flora.type.interfaces.IFlora;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.bson.BsonArray;
import org.bson.BsonBoolean;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.BsonObjectId;
import org.bson.BsonString;
import org.bson.Document;
import org.bson.types.ObjectId;

/**
 *
 * @author Bart
 */
public abstract class AbstractFlora implements IFloraSubType, Comparable<AbstractFlora>, Cloneable {

    static final Color COLOR_VALIDATION_FAILED = Color.YELLOW;
    static final Color COLOR_VALIDATION_SUCCES = Color.WHITE;
    static final Color COLOR_READY_FOR_VALIDATION = Color.WHITE;

    private ObjectId id;
    private FloraSubTypeEnum subType;
    private Species species;
    private List<Object> mediaReferences;
    private List<FunctieEnum> functieEnums;
    private List<BodemEigenschapEnum> bodemEigenschapEnums;
    private List<ZonlichtEnum> zonlichtEnums;
    private Map<OpvallendeEigenschapEnum, String> specialProperties;
    private List<Integer> blossomMonths, harvestMonths;
    private String latinName, commonName, maintenance;
    private Integer maxHeight;
    private Integer maxWidth;
    private Boolean winterLeaves;
    private Date modificationDate;
    private Date interrogationDate;

    public AbstractFlora(Boolean winterLeaves) {
        this.winterLeaves = winterLeaves;
        this.mediaReferences = new ArrayList<>();
        this.functieEnums = new ArrayList<>();
        this.bodemEigenschapEnums = new ArrayList<>();
        this.zonlichtEnums = new ArrayList<>();
        this.specialProperties = new HashMap<>();
        this.blossomMonths = new ArrayList<>();
        this.harvestMonths = new ArrayList<>();
    }

    
    public void setAttributes(Document doc, DbManager dbMgr) {
        this.setId(doc.getObjectId("_id"));
        this.setSubType(FloraSubTypeEnum.parse(doc.get("subType", String.class)));
        this.setSpecies(dbMgr.getSpeciesById(doc.getObjectId("species")));
        this.setLatinName(doc.get("latinName", String.class));
        this.setCommonName(doc.get("commonName", String.class));
        ArrayList medRefs = (ArrayList) doc.get("mediaReferences");
        if (medRefs != null && !medRefs.isEmpty()) {

            BsonArray arr = new BsonArray();

            medRefs.forEach(t -> {
                Document mediaRefDoc = ((Document) t);
                this.getMediaReferences().add(t);
            });

        }
        ArrayList<String> functionTypesAsString = (ArrayList) doc.get("functions");
        if (functionTypesAsString != null && !functionTypesAsString.isEmpty()) {
            for (int i = 0; i < functionTypesAsString.size(); i++) {
                this.addFunctionType(FunctieEnum.parse(functionTypesAsString.get(i)));
            }
        }
        ArrayList<String> soilTypesAsString = (ArrayList) doc.get("soilTypes");
        if (soilTypesAsString != null && !soilTypesAsString.isEmpty()) {
            for (int i = 0; i < soilTypesAsString.size(); i++) {
                this.addSoilType(BodemEigenschapEnum.parse(soilTypesAsString.get(i)));
            }
        }
        ArrayList<String> solarTypesAsString = (ArrayList) doc.get("solarTypes");
        if (solarTypesAsString != null && !solarTypesAsString.isEmpty()) {
            for (int i = 0; i < solarTypesAsString.size(); i++) {
                this.addSolarType(ZonlichtEnum.parse(solarTypesAsString.get(i)));
            }
        }
        ArrayList<Document> specialProps = (ArrayList) doc.get("specials");
        if (specialProps != null && !specialProps.isEmpty()) {
            for (int i = 0; i < specialProps.size(); i++) {
                this.addSpecialProperty(OpvallendeEigenschapEnum.parse(
                        specialProps.get(i).get("code").toString()),
                        specialProps.get(i).get("value").toString()
                );
            }
        }
        this.blossomMonths = (ArrayList) doc.get("blossomMonths");
        this.harvestMonths = (ArrayList) doc.get("harvestMonths");

        if (doc.getInteger("maxHeight") != null) {
            this.setMaxHeight(doc.getInteger("maxHeight"));
        }

        if (doc.getInteger("maxWidth") != null) {
            this.setMaxWidth(doc.getInteger("maxWidth"));
        }
        if (doc.getString("maintenance") != null) {
            this.setMaintenance(doc.getString("maintenance"));
        }

        if (doc.getBoolean("winterLeaf") != null) {
            this.setWinterLeaves(doc.getBoolean("winterLeaf"));
        }

        this.setModificationDate(doc.getDate("modificationDate"));
        this.setInterrogationDate(doc.getDate("interrogationDate"));

    }

    public Document toBson() {
        Document a = new Document();
        if (getId() != null) {
            a.put("_id", getId());
        }
        if (getSubType() != null) {
            a.put("subType", new BsonString(getSubType().getCode()));
        }
        if (getLatinName() != null) {
            a.put("latinName", new BsonString(getLatinName()));
        }
        if (getSpecies() != null && getSpecies().getId() != null) {
            a.put("species", new BsonObjectId(getSpecies().getId()));
        }
        if (commonName != null) {
            a.put("commonName", new BsonString(getCommonName()));
        }
        if (mediaReferences != null) {
            BsonArray mediaRefs = new BsonArray();
            mediaReferences.forEach(t -> {
                BsonDocument child = new BsonDocument();
                child.append("type", new BsonString(DbManager.MEDIA_GRIDFS));
                BsonObjectId tgt = ((BsonDocument) t).get(DbManager.MEDIA_GRIDFS_ID).asObjectId();
                child.append("gridFsId", tgt.asObjectId());
                mediaRefs.add(child);
            });
            a.put("mediaReferences", mediaRefs);
        }
        if (getFunctionTypes() != null) {
            a.put("functions", getFunctionTypes().stream().map((t) -> {
                return t.getCode();
            }).collect(Collectors.toList()));
        }

        if (getSoilTypes() != null) {
            a.put("soilTypes", getSoilTypes().stream().map((t) -> {
                return t.getUniqueid();
            }).collect(Collectors.toList()));
        }

        if (getSolarTypes() != null) {
            a.put("solarTypes", getSolarTypes().stream().map((t) -> {
                return t.getCode();
            }).collect(Collectors.toList()));
        }

        if (getFunctionTypes() != null) {
            a.put("specials", getSpecialProperties().keySet().stream().map((t) -> {
                BsonDocument elem = new BsonDocument();
                elem.append("code", new BsonString(t.getCode()));
                elem.append("value", new BsonString(getSpecialProperties().get(t)));
                return elem;
            }).collect(Collectors.toList()));
        }

        if (getBlossomMonths() != null) {
            a.put("blossomMonths", getBlossomMonths());
        }
        if (getHarvestMonths() != null) {
            a.put("harvestMonths", getHarvestMonths());
        }

        if (getMaxHeight() != null) {
            a.put("maxHeight", new BsonInt32(getMaxHeight()));
        }
        if (getMaxWidth() != null) {
            a.put("maxWidth", new BsonInt32(getMaxWidth()));
        }

        if (getMaintenance() != null) {
            a.put("maintenance", new BsonString("" + getMaintenance()));
        }

        if (getWinterLeaves() != null) {
            a.put("winterLeaf", new BsonBoolean(getWinterLeaves()));
        }

        a.put("modificationDate", new Date());
        a.put("interrogationDate", null);
        return a;
    }

    @Override
    public List<Object> getMediaReferences() {
        return mediaReferences;
    }

    @Override
    public void setMediaReferences(List<Object> mediaReferences) {
        this.mediaReferences = mediaReferences;
    }

    @Override
    public Integer getMaxHeight() {
        return maxHeight;
    }

    @Override
    public void setMaxHeight(Integer maxHeight) {
        this.maxHeight = maxHeight;
    }

    @Override
    public Integer getMaxWidth() {
        return maxWidth;
    }

    @Override
    public void setMaxWidth(Integer maxWidth) {
        this.maxWidth = maxWidth;
    }

    @Override
    public String getMaintenance() {
        return maintenance;
    }

    @Override
    public void setMaintenance(String maintenance) {
        this.maintenance = maintenance;
    }

    @Override
    public Boolean getWinterLeaves() {
        return winterLeaves;
    }

    @Override
    public void setWinterLeaves(Boolean winterLeaves) {
        this.winterLeaves = winterLeaves;
    }

    @Override
    public ObjectId getId() {
        return id;
    }

    @Override
    public void setId(ObjectId id) {
        this.id = id;
    }

    @Override
    public abstract FloraSubTypeEnum getSubType();

    @Override
    public void setSubType(FloraSubTypeEnum subType) {
        this.subType = subType;
    }

    @Override
    public Species getSpecies() {
        return species;
    }

    @Override
    public void setSpecies(Species species) {
        this.species = species;
    }

    @Override
    public List<BodemEigenschapEnum> getSoilTypes() {
        return bodemEigenschapEnums;
    }

    @Override
    public void setSoilTypes(List<BodemEigenschapEnum> bodemEigenschapEnums) {
        this.bodemEigenschapEnums = bodemEigenschapEnums;
    }

    @Override
    public void addSoilType(BodemEigenschapEnum bodemEigenschapEnum) {
        if (!bodemEigenschapEnums.contains(bodemEigenschapEnum)) {
            this.bodemEigenschapEnums.add(bodemEigenschapEnum);
        }
    }

    @Override
    public void removeSoilType(BodemEigenschapEnum bodemEigenschapEnum) {
        if (bodemEigenschapEnums.contains(bodemEigenschapEnum)) {
            this.bodemEigenschapEnums.remove(bodemEigenschapEnum);
        }
    }

    @Override
    public List<FunctieEnum> getFunctionTypes() {
        return functieEnums;
    }

    @Override
    public void setFunctionTypes(List<FunctieEnum> functieEnums) {
        this.functieEnums = functieEnums;
    }

    @Override
    public void addFunctionType(FunctieEnum functieEnum) {
        if (!functieEnums.contains(functieEnum)) {
            this.functieEnums.add(functieEnum);
        }
    }

    @Override
    public void removeFunctionType(FunctieEnum functieEnum) {
        if (functieEnums.contains(functieEnum)) {
            this.functieEnums.remove(functieEnum);
        }
    }

    @Override
    public List<ZonlichtEnum> getSolarTypes() {
        return zonlichtEnums;
    }

    @Override
    public void setSolarTypes(List<ZonlichtEnum> zonlichtEnums) {
        this.zonlichtEnums = zonlichtEnums;
    }

    @Override
    public void addSolarType(ZonlichtEnum zonlichtEnum) {
        if (!zonlichtEnums.contains(zonlichtEnum)) {
            this.zonlichtEnums.add(zonlichtEnum);
        }
    }

    @Override
    public void removeSolarType(ZonlichtEnum zonlichtEnum) {
        if (zonlichtEnums.contains(zonlichtEnum)) {
            this.zonlichtEnums.remove(zonlichtEnum);
        }
    }

    @Override
    public Map<OpvallendeEigenschapEnum, String> getSpecialProperties() {
        return specialProperties;
    }

    @Override
    public void setSpecialProperties(Map<OpvallendeEigenschapEnum, String> specialProperties) {
        this.specialProperties = specialProperties;
    }

    @Override
    public void addSpecialProperty(OpvallendeEigenschapEnum type, String value) {
        this.specialProperties.put(type, value);
    }

    @Override
    public void removeSpecialProperty(OpvallendeEigenschapEnum type) {
        this.specialProperties.forEach((c, v) -> {
            if (c == type) {
                this.specialProperties.remove(c);
            }
        });
    }

    @Override
    public List<Integer> getBlossomMonths() {
        return blossomMonths;
    }

    @Override
    public List<Integer> getHarvestMonths() {
        return harvestMonths;
    }

    @Override
    public void setBlossomMonths(List<Integer> months) {
        blossomMonths = months;
    }

    @Override
    public void setHarvestMonths(List<Integer> months) {
        harvestMonths = months;
    }

    @Override
    public String getLatinName() {
        return latinName;
    }

    @Override
    public void setLatinName(String latinName) {
        this.latinName = latinName;
    }

    @Override
    public String getCommonName() {
        return commonName;
    }

    @Override
    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    @Override
    public Date getModificationDate() {
        return modificationDate;
    }

    @Override
    public void setModificationDate(Date modificationDate) {
        this.modificationDate = modificationDate;
    }

    @Override
    public Date getInterrogationDate() {
        return interrogationDate;
    }

    @Override
    public void setInterrogationDate(Date interrogationDate) {
        this.interrogationDate = interrogationDate;
    }

    public AbstractFlora() {
        this.subType = getSubType();

        this.mediaReferences = new ArrayList<>();
        this.functieEnums = new ArrayList<>();
        this.bodemEigenschapEnums = new ArrayList<>();
        this.zonlichtEnums = new ArrayList<>();
        this.specialProperties = new HashMap<>();
        this.blossomMonths = new ArrayList<>();
        this.harvestMonths = new ArrayList<>();

    }

    public void setAttributes(IFloraSubType defaultContent, boolean preserveSubType) {
        this.id = defaultContent.getId();
        this.mediaReferences = defaultContent.getMediaReferences();
        if (!preserveSubType) {
            this.subType = defaultContent.getSubType();
        }
        this.species = defaultContent.getSpecies();
        this.functieEnums = defaultContent.getFunctionTypes();
        this.bodemEigenschapEnums = defaultContent.getSoilTypes();
        this.zonlichtEnums = defaultContent.getSolarTypes();
        this.specialProperties = defaultContent.getSpecialProperties();
        this.blossomMonths = defaultContent.getBlossomMonths();
        this.harvestMonths = defaultContent.getHarvestMonths();
        this.latinName = defaultContent.getLatinName();
        this.commonName = defaultContent.getCommonName();
        this.maxHeight = defaultContent.getMaxHeight();
        this.maxWidth = defaultContent.getMaxWidth();
        this.maintenance = defaultContent.getMaintenance();
        this.winterLeaves = defaultContent.getWinterLeaves();

    }

    @Override
    public String toString() {
        return getId().toHexString();
    }

    @Override
    public int compareTo(AbstractFlora o) {
        if (o == null) {
            return 1;
        }

        return (getLatinName() + getId()).compareTo(
                (o.getLatinName() + o.getId())
        );
    }

    public IFloraSubType asIFloraSubType() {
        try {
            return (IFloraSubType) this;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public IFloraSubType getCopy(String newName) {
        IFloraSubType clone = null;
        try {
            clone = (IFloraSubType) this.clone();
            clone.setId(null);
            clone.setLatinName(newName);
            clone.setMediaReferences(new ArrayList<>());
            return clone;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return  null;
    }
}
