/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bville.qwiklurn.repository.flora;

import java.util.Date;
import java.util.List;
import java.util.Map;
import org.bson.types.ObjectId;

/**
 *
 * @author Bart
 */
public interface IFlora {

    public List<Object> getMediaReferences();

    public void setMediaReferences(List<Object> mediaReferences);

    public Integer getMaxHeight();

    public void setMaxHeight(Integer maxHeight);

    public Integer getMaxWidth();

    public void setMaxWidth(Integer maxWidth);

    public String getMaintenance();

    public void setMaintenance(String maintenance);

    public String getColor();

    public void setColor(String color);

    public Boolean getWinterLeaves();

    public void setWinterLeaves(Boolean winterLeaves);

    public ObjectId getId();

    public void setId(ObjectId id);

    public FloraSubTypeEnum getSubType();

    public void setSubType(FloraSubTypeEnum subType);

    public Species getSpecies();

    public void setSpecies(Species species);

    public List<SoilType> getSoilTypes();

    public List<FunctionType> getFunctionTypes();

    public void setFunctionTypes(List<FunctionType> functionTypes);

    public void addFunctionType(FunctionType functionType);

    public void removeFunctionType(FunctionType functionType);

    public void setSoilTypes(List<SoilType> soilTypes);

    public void addSoilType(SoilType soilType);

    public void removeSoilType(SoilType soilType);

    public List<SolarType> getSolarTypes();

    public void setSolarTypes(List<SolarType> solarTypes);

    public void addSolarType(SolarType solarType);

    public void removeSolarType(SolarType solarType);

    public Map<SpecialsType, String> getSpecialProperties();

    public void setSpecialProperties(Map<SpecialsType, String> specialProperties);

    public void addSpecialProperty(SpecialsType type, String value);

    public void removeSpecialProperty(SpecialsType type);

    public SeasonType getSeason();

    public void setSeason(SeasonType season);

    public String getLatinName();

    public void setLatinName(String latinName);

    public String getCommonName();

    public void setCommonName(String commonName);

    public Date getModificationDate();

    public void setModificationDate(Date modificationDate);

    public Date getInterrogationDate();

    public void setInterrogationDate(Date interrogationDate);

}
