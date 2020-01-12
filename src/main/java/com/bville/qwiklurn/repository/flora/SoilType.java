/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bville.qwiklurn.repository.flora;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Bart
 */
public enum SoilType  implements CodeDescrEnum{
    KALK_RIJK("k", "kr", "kalkrijk"),
    KALK_ARM("k", "ka", "kalkarm"),
    KALK_ZUUR("k", "kz", "zuur"),
    VOCHT_DROOG("v", "vd", "droog"),
    VOCHT_GEMIDDELD("v", "vg", "gemiddeld"),
    VOCHT_VOCHTIG("v", "vv", "vochtig"),
    HUMUS_ARM("h", "ha", "humusarm"),
    HUMUS_GEMIDDELD("h", "hg", "gemiddeld"),
    HUMUS_RIJK("h", "hr", "humusrijk");

    public static SoilType parse(String code) {
        if(code.equalsIgnoreCase(KALK_RIJK.getCode())) return KALK_RIJK;
        if(code.equalsIgnoreCase(KALK_ARM.getCode())) return KALK_ARM;
        if(code.equalsIgnoreCase(KALK_ZUUR.getCode())) return KALK_ZUUR;
        if(code.equalsIgnoreCase(VOCHT_DROOG.getCode())) return VOCHT_DROOG;
        if(code.equalsIgnoreCase(VOCHT_GEMIDDELD.getCode())) return VOCHT_GEMIDDELD;
        if(code.equalsIgnoreCase(VOCHT_VOCHTIG.getCode())) return VOCHT_VOCHTIG;
        if(code.equalsIgnoreCase(HUMUS_ARM.getCode())) return HUMUS_ARM;
        if(code.equalsIgnoreCase(HUMUS_GEMIDDELD.getCode())) return HUMUS_GEMIDDELD;
        if(code.equalsIgnoreCase(HUMUS_RIJK.getCode())) return HUMUS_RIJK;

        throw new RuntimeException("Unsupported code for SoilType: " + code);

    }

    private String groupCode;
    private String code;
    private String description;

    SoilType(String groupCode, String code, String desc) {
        this.groupCode = groupCode;
        this.code = code;
        this.description = desc;
    }

    public String getGroupCode() {
        return groupCode;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDescription() {
        return description;
    }

    

    public static List<String> getGroupCodes() {
        ArrayList<String> groups = new ArrayList<>();
        SoilType[] all = SoilType.values();
        for (int i = 0; i < all.length; i++) {
            if (!groups.contains(all[i].getGroupCode())) {
                groups.add(all[i].getGroupCode());
            }
        }
        return groups;
    }
    
    /*
    public boolean equals(SoilType o){
        return code.equalsIgnoreCase(o.getCode());
    }
    
    */
}
