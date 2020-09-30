package com.bville.qwiklurn.repository.flora;

import java.util.Arrays;
import java.util.Optional;

public enum VoortplantingEnum implements CodeDescrEnum {
    VERSTUIVING("verstuiving", "Verstuiving"),
    UITLOPERS_B("uitlopersBoven", "Uitlopers (bovengronds)"),
    UITLOPERS_O("uitlopersOnder", "Uitlopers (ondergronds)"),
    ZADEN("zaad", "Zaden"),
    ENTEN("entbaar", "Enten"),
    STEKKEN("stekbaar", "Stekken"),
    SPLITSEN("splitsbaar", "Splitsen"),

    ;

    private String code;
    private String descr;

    VoortplantingEnum(String code, String descr) {
        this.code = code;
        this.descr = descr;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String getDescription() {
        return descr;
    }

    public static VoortplantingEnum parse(String code) {
        return (VoortplantingEnum) EnumUtil.parse(code, values());
    }

    @Override
    public String toString() {
        return getDescription();
    }

}
