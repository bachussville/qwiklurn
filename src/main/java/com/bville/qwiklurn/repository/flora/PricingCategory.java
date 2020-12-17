package com.bville.qwiklurn.repository.flora;

public enum PricingCategory {
    A("Laag",0,2),
    B("Laag",2,4),
    C("Laag",4,6),
    D("Midden",8,10),
    E("Midden",10,15),
    F("Hoog",15,30),
    G("Hoog",30,50),
    H("Exclusief",50,75),
    I("Exclusief",75,100),
    J("Exclusief",100,1000),
    K("Exclusief",1000,10000);

    public final String description;
    public final int min;
    public final int max;

    PricingCategory(String d, int min, int max) {
        this.description = d;
        this.min = min;
        this.max= max;
    }

    @Override
    public String toString() {
        return String.format("%s, %s [%d - %d]", name(), description, min, max);
    }
}
