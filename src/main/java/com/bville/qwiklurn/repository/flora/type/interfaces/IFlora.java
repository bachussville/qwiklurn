/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bville.qwiklurn.repository.flora.type.interfaces;

import com.bville.qwiklurn.repository.flora.FloraSubTypeEnum;
import com.bville.qwiklurn.repository.flora.FunctionType;
import com.bville.qwiklurn.repository.flora.SeasonType;
import com.bville.qwiklurn.repository.flora.SoilType;
import com.bville.qwiklurn.repository.flora.SolarType;
import com.bville.qwiklurn.repository.flora.SpecialsType;
import com.bville.qwiklurn.repository.flora.Species;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.bson.types.ObjectId;

/**
 *
 * @author Bart
 */
public interface IFlora {

    //Getter
    public ObjectId getId();

    public FloraSubTypeEnum getSubType();

    public List<FunctionType> getFunctionTypes();

    public Species getSpecies();

    public String getLatinName();

    public String getCommonName();

    public List<Object> getMediaReferences();

    public Integer getMaxHeight();

    public Integer getMaxWidth();

    public String getMaintenance();

    public String getColor();

    public Boolean getWinterLeaves();

    public List<SoilType> getSoilTypes();

    public List<SolarType> getSolarTypes();

    public Map<SpecialsType, String> getSpecialProperties();

    public SeasonType getSeason();

    //Setter
    public Date getModificationDate();

    public Date getInterrogationDate();

    public void setMediaReferences(List<Object> mediaReferences);

    public void setMaxHeight(Integer maxHeight);

    public void setMaxWidth(Integer maxWidth);

    public void setMaintenance(String maintenance);

    public void setColor(String color);

    public void setWinterLeaves(Boolean winterLeaves);

    public void setId(ObjectId id);

    public void setSubType(FloraSubTypeEnum subType);

    public void setSpecies(Species species);

    public void setFunctionTypes(List<FunctionType> functionTypes);

    public void addFunctionType(FunctionType functionType);

    public void removeFunctionType(FunctionType functionType);

    public void setSoilTypes(List<SoilType> soilTypes);

    public void addSoilType(SoilType soilType);

    public void removeSoilType(SoilType soilType);

    public void setSolarTypes(List<SolarType> solarTypes);

    public void addSolarType(SolarType solarType);

    public void removeSolarType(SolarType solarType);

    public void setSpecialProperties(Map<SpecialsType, String> specialProperties);

    public void addSpecialProperty(SpecialsType type, String value);

    public void removeSpecialProperty(SpecialsType type);

    public void setSeason(SeasonType season);

    public void setLatinName(String latinName);

    public void setCommonName(String commonName);

    public void setModificationDate(Date modificationDate);

    public void setInterrogationDate(Date interrogationDate);

}
