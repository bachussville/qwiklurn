/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bville.qwiklurn.repository.flora;

import com.bville.qwiklurn.repository.flora.type.Loofboom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import org.bson.Document;

/**
 *
 * @author Bart
 */
public class DBManagerForTest extends DbManager {

    public DBManagerForTest(String databaseName) {
        super("QwiklurnUnitTest");
    }

    /*
    @Override
    public String getDbName() {
        return super.getDbName() + "UnitTest"; //To change body of generated methods, choose Tools | Templates.
    }
     */
    public void clearAllCollections() {
        clearCollection(DbManager.COLL_FLORA);
        clearCollection(DbManager.COLL_SPECIES);
        clearCollection(DbManager.COLL_PROJECTS);
        clearCollection(DbManager.COLL_FLORA + "Meda.chunks");
        clearCollection(DbManager.COLL_FLORA + "Media.files");
    }

    public void clearCollection(String name) {
        connect().getCollection(name).deleteMany(new Document());
    }

    public Document persistTree(String latinName) {
        return persistTreeClass(getDummyTree(latinName));

    }

    public Document persistTreeClass(Loofboom tree) {
        Document treeAsDoc = tree.toBson();
        getFloraCollection().insertOne(treeAsDoc);
        return treeAsDoc;
    }

    public Loofboom getDummyTree(String latinName) {
        Loofboom dummy = new Loofboom();
        dummy.setSubType(FloraSubTypeEnum.LOOFBOOM);
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
        dummy.setSpecialProperties(new EnumMap<>(OpvallendeEigenschapEnum.class));
        dummy.setBlossomMonths(Arrays.asList(4));
        dummy.setHarvestMonths(Arrays.asList(8));

        return dummy;
    }

    public Species getDummySpecies(String latinName) {
        Species dummy = new Species(latinName);
        return dummy;
    }

    public Project getDummyProject(String name) {
        Project dummy = new Project(name);
        return dummy;
    }
}
