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
public enum BodemEigenschapEnum implements CodeDescrEnum{
    KALK_RIJK("zuurteGraad", "laag", "kalkrijk"),
    KALK_ARM("zuurteGraad", "gemiddeld", "gemiddeld"),
    KALK_ZUUR("zuurteGraad", "hoog", "zuur"),
    VOCHT_DROOG("vochtigheid", "weinig", "droog"),
    VOCHT_GEMIDDELD("vochtigheid", "gemiddeld", "gemiddeld"),
    VOCHT_VOCHTIG("vochtigheid", "veel", "vochtig"),
    HUMUS_ARM("voeding", "arm", "arm"),
    HUMUS_GEMIDDELD("voeding", "gemiddeld", "gemiddeld"),
    HUMUS_RIJK("voeding", "rijk", "voedingrijk");

    public static BodemEigenschapEnum parse(String code) {
        return (BodemEigenschapEnum) EnumUtil.parse(code, BodemEigenschapEnum.values());
   }

    private String groupCode;
    private String code;
    private String description;

    BodemEigenschapEnum(String groupCode, String code, String desc) {
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
        BodemEigenschapEnum[] all = BodemEigenschapEnum.values();
        for (int i = 0; i < all.length; i++) {
            if (!groups.contains(all[i].getGroupCode())) {
                groups.add(all[i].getGroupCode());
            }
        }
        return groups;
    }

}
