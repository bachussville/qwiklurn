/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bville.qwiklurn.repository.flora.type;

import com.bville.qwiklurn.repository.flora.type.interfaces.IFloraSubType;
import com.bville.qwiklurn.repository.flora.type.interfaces.IFlora;
import com.bville.qwiklurn.repository.flora.DbManager;
import com.bville.qwiklurn.repository.flora.FloraSubTypeEnum;
import com.bville.qwiklurn.repository.flora.FunctionType;
import com.bville.qwiklurn.repository.flora.SeasonType;
import com.bville.qwiklurn.repository.flora.SoilType;
import com.bville.qwiklurn.repository.flora.SolarType;
import com.bville.qwiklurn.repository.flora.SpecialsType;
import com.bville.qwiklurn.repository.flora.Species;
import static com.mongodb.client.model.Updates.set;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import javax.swing.JPanel;
import javax.swing.JTextArea;
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
public abstract class FloraClass implements IFlora, Comparable<FloraClass> {

    static final Color COLOR_VALIDATION_FAILED = Color.YELLOW;
    static final Color COLOR_VALIDATION_SUCCES = Color.WHITE;
    static final Color COLOR_READY_FOR_VALIDATION = Color.WHITE;

    DbManager dbMgr;
    private ObjectId id;
    private FloraSubTypeEnum subType;
    private Species species;
    private List<Object> mediaReferences;
    private List<FunctionType> functionTypes;
    private List<SoilType> soilTypes;
    private List<SolarType> solarTypes;
    private Map<SpecialsType, String> specialProperties;
    private SeasonType season;
    private String latinName, commonName, maintenance, color;
    private Integer maxHeight;
    private Integer maxWidth;
    private Boolean winterLeaves;
    private Date modificationDate;
    private Date interrogationDate;
    
    

    public void setAttributes(Document doc) {
        this.setId(doc.getObjectId("_id"));
        this.setSubType(FloraSubTypeEnum.parse(doc.get("subType", String.class)));
        this.setSpecies(doc.getObjectId("species"));
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
                this.addFunctionType(FunctionType.parse(functionTypesAsString.get(i)));
            }
        }
        ArrayList<String> soilTypesAsString = (ArrayList) doc.get("soilTypes");
        if (soilTypesAsString != null && !soilTypesAsString.isEmpty()) {
            for (int i = 0; i < soilTypesAsString.size(); i++) {
                this.addSoilType(SoilType.parse(soilTypesAsString.get(i)));
            }
        }
        ArrayList<String> solarTypesAsString = (ArrayList) doc.get("solarTypes");
        if (solarTypesAsString != null && !solarTypesAsString.isEmpty()) {
            for (int i = 0; i < solarTypesAsString.size(); i++) {
                this.addSolarType(SolarType.parse(solarTypesAsString.get(i)));
            }
        }
        ArrayList<Document> specialProps = (ArrayList) doc.get("specials");
        if (specialProps != null && !specialProps.isEmpty()) {
            for (int i = 0; i < specialProps.size(); i++) {
                this.addSpecialProperty(SpecialsType.parse(
                        specialProps.get(i).get("code").toString()),
                        specialProps.get(i).get("value").toString()
                );
            }
        }
        if (doc.getString("season") != null) {
            this.setSeason(SeasonType.parse(doc.getString("season")));
        }

        if (doc.getInteger("maxHeight") != null) {
            this.setMaxHeight(doc.getInteger("maxHeight"));
        }

        if (doc.getInteger("maxWidth") != null) {
            this.setMaxWidth(doc.getInteger("maxWidth"));
        }
        if (doc.getString("maintenance") != null) {
            this.setMaintenance(doc.getString("maintenance"));
        }

        if (doc.getString("color") != null) {
            this.setColor(doc.getString("color"));
        }

        if (doc.getBoolean("winterLeaf") != null) {
            this.setWinterLeaves(doc.getBoolean("winterLeaf"));
        }

        this.setModificationDate(doc.getDate("modificationDate"));
        this.setInterrogationDate(doc.getDate("interrogationDate"));

    }

    public Document toBson() {
        Document a = new Document();
        a.put("_id", (getId() == null ? ObjectId.get() : getId()));
        if (getSubType() != null) {
            a.put("subType", new BsonString(getSubType().getCode()));
        }
        if (getLatinName() != null) {
            a.put("latinName", new BsonString(getLatinName()));
        }
        if (getSpecies() != null) {
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
                return t.getCode();
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

        if (getSeason() != null) {
            a.put("season", getSeason().getCode());
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

        if (getColor() != null) {
            a.put("color", new BsonString("" + getColor()));
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
    public String getColor() {
        return color;
    }

    @Override
    public void setColor(String color) {
        this.color = color;
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

    public void setSpecies(ObjectId speciesId) {
        this.species = dbMgr.getSpeciesById(speciesId);
    }

    @Override
    public List<SoilType> getSoilTypes() {
        return soilTypes;
    }

    @Override
    public void setSoilTypes(List<SoilType> soilTypes) {
        this.soilTypes = soilTypes;
    }

    @Override
    public void addSoilType(SoilType soilType) {
        if (!soilTypes.contains(soilType)) {
            this.soilTypes.add(soilType);
        }
    }

    @Override
    public void removeSoilType(SoilType soilType) {
        if (soilTypes.contains(soilType)) {
            this.soilTypes.remove(soilType);
        }
    }

    @Override
    public List<FunctionType> getFunctionTypes() {
        return functionTypes;
    }

    @Override
    public void setFunctionTypes(List<FunctionType> functionTypes) {
        this.functionTypes = functionTypes;
    }

    @Override
    public void addFunctionType(FunctionType functionType) {
        if (!functionTypes.contains(functionType)) {
            this.functionTypes.add(functionType);
        }
    }

    @Override
    public void removeFunctionType(FunctionType functionType) {
        if (functionTypes.contains(functionType)) {
            this.functionTypes.remove(functionType);
        }
    }

    @Override
    public List<SolarType> getSolarTypes() {
        return solarTypes;
    }

    @Override
    public void setSolarTypes(List<SolarType> solarTypes) {
        this.solarTypes = solarTypes;
    }

    @Override
    public void addSolarType(SolarType solarType) {
        if (!solarTypes.contains(solarType)) {
            this.solarTypes.add(solarType);
        }
    }

    @Override
    public void removeSolarType(SolarType solarType) {
        if (solarTypes.contains(solarType)) {
            this.solarTypes.remove(solarType);
        }
    }

    @Override
    public Map<SpecialsType, String> getSpecialProperties() {
        return specialProperties;
    }

    @Override
    public void setSpecialProperties(Map<SpecialsType, String> specialProperties) {
        this.specialProperties = specialProperties;
    }

    @Override
    public void addSpecialProperty(SpecialsType type, String value) {
        this.specialProperties.put(type, value);
    }

    @Override
    public void removeSpecialProperty(SpecialsType type) {
        this.specialProperties.forEach((c, v) -> {
            if (c == type) {
                this.specialProperties.remove(c);
            }
        });
    }

    @Override
    public SeasonType getSeason() {
        return season;
    }

    @Override
    public void setSeason(SeasonType season) {
        this.season = season;
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

    public FloraClass() {
        this.id = ObjectId.get();
        this.dbMgr = new DbManager();
        this.subType = getSubType();

        this.mediaReferences = new ArrayList<>();
        this.functionTypes = new ArrayList<>();
        this.soilTypes = new ArrayList<>();
        this.solarTypes = new ArrayList<>();
        this.specialProperties = new HashMap<>();

    }

    public void setAttributes(IFloraSubType defaultContent, boolean preserveSubType) {
        this.id = defaultContent.getId();
        this.mediaReferences = defaultContent.getMediaReferences();
        if (!preserveSubType) {
            this.subType = defaultContent.getSubType();
        }
        this.species = defaultContent.getSpecies();
        this.functionTypes = defaultContent.getFunctionTypes();
        this.soilTypes = defaultContent.getSoilTypes();
        this.solarTypes = defaultContent.getSolarTypes();
        this.specialProperties = defaultContent.getSpecialProperties();
        this.season = defaultContent.getSeason();
        this.latinName = defaultContent.getLatinName();
        this.commonName = defaultContent.getCommonName();
        this.maxHeight = defaultContent.getMaxHeight();
        this.maxWidth = defaultContent.getMaxWidth();
        this.maintenance = defaultContent.getMaintenance();
        this.color = defaultContent.getColor();
        this.winterLeaves = defaultContent.getWinterLeaves();

    }

    @Override
    public String toString() {
        return getId().toHexString();
    }

    @Override
    public int compareTo(FloraClass o) {
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

}
