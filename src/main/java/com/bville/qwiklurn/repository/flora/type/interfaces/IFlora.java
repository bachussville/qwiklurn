/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bville.qwiklurn.repository.flora.type.interfaces;

import com.bville.qwiklurn.repository.flora.FloraSubTypeEnum;
import com.bville.qwiklurn.repository.flora.FunctieEnum;
import com.bville.qwiklurn.repository.flora.BodemEigenschapEnum;
import com.bville.qwiklurn.repository.flora.ZonlichtEnum;
import com.bville.qwiklurn.repository.flora.OpvallendeEigenschapEnum;
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

    public List<FunctieEnum> getFunctionTypes();

    public Species getSpecies();

    public String getLatinName();

    public String getCommonName();

    public List<Object> getMediaReferences();

    public Integer getMaxHeight();

    public Integer getMaxWidth();

    public String getMaintenance();

    public Boolean getWinterLeaves();

    public List<BodemEigenschapEnum> getSoilTypes();

    public List<ZonlichtEnum> getSolarTypes();

    public Map<OpvallendeEigenschapEnum, String> getSpecialProperties();

    public List<Integer> getBlossomMonths();

    public List<Integer> getHarvestMonths();
    
    //Setter
    public Date getModificationDate();

    public Date getInterrogationDate();

    public void setMediaReferences(List<Object> mediaReferences);

    public void setMaxHeight(Integer maxHeight);

    public void setMaxWidth(Integer maxWidth);

    public void setMaintenance(String maintenance);

    public void setWinterLeaves(Boolean winterLeaves);

    public void setId(ObjectId id);

    public void setSubType(FloraSubTypeEnum subType);

    public void setSpecies(Species species);

    public void setFunctionTypes(List<FunctieEnum> functieEnums);

    public void addFunctionType(FunctieEnum functieEnum);

    public void removeFunctionType(FunctieEnum functieEnum);

    public void setSoilTypes(List<BodemEigenschapEnum> bodemEigenschapEnums);

    public void addSoilType(BodemEigenschapEnum bodemEigenschapEnum);

    public void removeSoilType(BodemEigenschapEnum bodemEigenschapEnum);

    public void setSolarTypes(List<ZonlichtEnum> zonlichtEnums);

    public void addSolarType(ZonlichtEnum zonlichtEnum);

    public void removeSolarType(ZonlichtEnum zonlichtEnum);

    public void setSpecialProperties(Map<OpvallendeEigenschapEnum, String> specialProperties);

    public void addSpecialProperty(OpvallendeEigenschapEnum type, String value);

    public void removeSpecialProperty(OpvallendeEigenschapEnum type);

    public void setBlossomMonths(List<Integer> months);

    public void setHarvestMonths(List<Integer> months);

    public void setLatinName(String latinName);

    public void setCommonName(String commonName);

    public void setModificationDate(Date modificationDate);

    public void setInterrogationDate(Date interrogationDate);

}
