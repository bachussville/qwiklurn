/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bville.qwiklurn.repository.flora;

import com.bville.qwiklurn.repository.flora.type.TreeClass;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import static junit.framework.Assert.assertEquals;
import org.bson.Document;
import org.bson.types.ObjectId;

/**
 *
 * @author Bart
 */
public class DBManagerForTest extends DbManager {

    @Override
    public String getDbName() {
        return super.getDbName() + "UnitTest"; //To change body of generated methods, choose Tools | Templates.
    }

    public void clearAllCollections() {
        clearCollection(DbManager.COLL_FLORA);
        clearCollection(DbManager.COLL_SPECIES);
        clearCollection(DbManager.COLL_FLORA + "Meda.chunks");
        clearCollection(DbManager.COLL_FLORA + "Media.files");
    }

    public void clearCollection(String name) {
        connect().getCollection(name).deleteMany(new Document());
    }

    public Document persistTree(String latinName) {
        return persistTreeClass(getDummyTree(latinName));

    }

    public Document persistTreeClass(TreeClass tree) {
        Document treeAsDoc = tree.toBson();
        getFloraCollection().insertOne(treeAsDoc);
        return treeAsDoc;
    }

    public TreeClass getDummyTree(String latinName) {
        TreeClass dummy = new TreeClass();
        dummy.setSubType(FloraSubTypeEnum.TREE);
        dummy.setFunctionTypes(new ArrayList<>());
        dummy.setSpecies(new Species("someSpecie"));
        dummy.setLatinName(latinName);
        dummy.setCommonName("cName");
        dummy.setMediaReferences(new ArrayList<>());
        dummy.setMaxHeight(1000);
        dummy.setMaxWidth(250);
        dummy.setMaintenance("Maintenance: do it!");
        dummy.setColor("kakigroen");
        dummy.setWinterLeaves(Boolean.TRUE);
        dummy.setSoilTypes(new ArrayList<>());
        dummy.setSpecialProperties(new EnumMap<>(SpecialsType.class));
        dummy.setSeason(SeasonType.HERFST);
        
        return dummy;
    }
}
