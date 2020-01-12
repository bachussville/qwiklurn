/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bville.qwiklurn.repository.flora;

import com.bville.qwiklurn.repository.flora.type.interfaces.IFloraSubType;
import com.bville.qwiklurn.repository.flora.type.TreeClass;
import com.mongodb.DBCollection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author Bart
 */
public class DbManagerTest {

    private static DBManagerForTest dbMgr;

    public DbManagerTest() {
    }

    @Before
    public void before() {
        dbMgr = new DBManagerForTest();
        dbMgr.setUpDatabase();
    }

    @Test
    public void testListAlphabetically() {
        dbMgr.clearAllCollections();
        Document doc1 = dbMgr.persistTree("b_comesLast");
        Document doc2 = dbMgr.persistTree("a_ComesFirst");

        List<IFloraSubType> result = dbMgr.listFloraTypesAlphabetically();
        assertEquals(2, result.size());
        assertEquals("doc2 should be first", result.get(0).getLatinName(), "a_ComesFirst");
        assertEquals("doc1 should be last", result.get(1).getLatinName(), "b_comesLast");

    }

    @Test
    public void findFloraByFilter() {
        dbMgr.clearAllCollections();
        Document doc1 = dbMgr.persistTree("b_comesLast");
        Document doc2 = dbMgr.persistTree("a_ComesFirst");

        BsonDocument filter = new BsonDocument("latinName", new BsonString("a_ComesFirst"));
        List<IFloraSubType> result = dbMgr.findFloraByFilter(filter);
        assertEquals(1, result.size());
        assertEquals(result.get(0).getLatinName(), "a_ComesFirst");
        assertEquals(doc2.getObjectId("_id").toHexString(), result.get(0).getId().toHexString());
    }

    @Test
    public void findFloraByFilter_nullValue() {
        dbMgr.clearAllCollections();
        Document doc1 = dbMgr.persistTree("b_comesLast");
        Document doc2 = dbMgr.persistTree("a_ComesFirst");

        List<IFloraSubType> result = dbMgr.findFloraByFilter(null);
        assertEquals(2, result.size());
    }

    @Test
    public void getDbName() {
        assertEquals("QwiklurnUnitTest", dbMgr.getDbName());
    }

    @Test
    public void saveTreeClass_newWithNewSpecies() {
        dbMgr.clearAllCollections();
        TreeClass newTree = dbMgr.getDummyTree("new");
        newTree.setId(null);

        newTree.setId(null);
        //SubType for TreeClass is TREE
        //newTree.setSubType(FloraSubTypeEnum.TREE);
        List<FunctionType> fTypes = new ArrayList<>();
        fTypes.add(FunctionType.DECO);
        newTree.setFunctionTypes(fTypes);
        Species species = new Species(null, "species", new ArrayList<>());
        newTree.setSpecies(species);
        newTree.setLatinName("lName");
        newTree.setCommonName("cName");
        List<Object> mediaRefs = new ArrayList<>();
        File mediaRefAsFile = null;
        try {
            mediaRefAsFile = File.createTempFile("Qwiklurn", "unitTest");
            FileWriter fw = new FileWriter(mediaRefAsFile);
            fw.append("abc");
            fw.close();
        } catch (Exception e) {
            fail("Error writign temp file");
        }

        mediaRefs.add(mediaRefAsFile);
        newTree.setMediaReferences(mediaRefs);

        newTree.setMaxHeight(200);
        newTree.setMaxWidth(150);
        newTree.setMaintenance("maintenance");
        newTree.setColor("color");
        newTree.setWinterLeaves(Boolean.FALSE);

        List<SoilType> soiltypes = new ArrayList();
        soiltypes.add(SoilType.KALK_ARM);
        newTree.setSoilTypes(soiltypes);

        Map<SpecialsType, String> specialProperties = new EnumMap<SpecialsType, String>(SpecialsType.class);
        specialProperties.put(SpecialsType.GEUR, "lekker");

        newTree.setSpecialProperties(specialProperties);

        newTree.setSeason(SeasonType.HERFST);

        dbMgr.saveFlora(newTree);

        TreeClass readValue = (TreeClass) dbMgr.getFloraById(newTree.getId());

        assertNotNull(readValue.getId().toHexString());
        assertEquals(FloraSubTypeEnum.TREE, readValue.getSubType());
        assertEquals(FunctionType.DECO, readValue.getFunctionTypes().get(0));
        assertEquals(null, readValue.getSpecies());
        assertEquals("lName", newTree.getLatinName());
        assertEquals("cName", newTree.getCommonName());
        assertEquals(newTree.getMediaReferences().size(), readValue.getMediaReferences().size());
        assertEquals(((BsonDocument)newTree.getMediaReferences().get(0)).getObjectId("gridFsId").getValue().toHexString()
                , ((Document)readValue.getMediaReferences().get(0)).getObjectId("gridFsId").toHexString());
        assertEquals(200, readValue.getMaxHeight().longValue());
        assertEquals(150,  readValue.getMaxWidth().longValue());
        assertEquals("maintenance", readValue.getMaintenance());
        assertEquals("color", readValue.getColor());
        assertEquals(Boolean.FALSE, readValue.getWinterLeaves());
        assertEquals(SoilType.KALK_ARM, readValue.getSoilTypes().get(0));
        assertEquals("lekker", readValue.getSpecialProperties().get(SpecialsType.GEUR));
        assertEquals(SeasonType.HERFST, readValue.getSeason());
    }

    @Test
    @Ignore
    public void saveFlora_update() {
        assertEquals("QwiklurnUnisTest", dbMgr.getDbName());
    }

    @Test
    @Ignore
    public void testListLeastedTested() {
        List<IFloraSubType> aaa = dbMgr.listFloraTypesByLeastTested();
        System.out.println("Number of xy elements in database: " + aaa.size());
        assertTrue(aaa.size() > 0);
    }

    @Test
    @Ignore
    public void test22WriteBigFile() {

        MongoDatabase db = dbMgr.connect();
        GridFSBucket grid = GridFSBuckets.create(db, "best88");

        try {
            File sourceFile = new File("D:\\JavaDev\\NetBeansProjects\\qwiklurn\\testResources\\Sample.MOV");
            ObjectId id = grid.uploadFromStream("TestJEEEE", new FileInputStream(sourceFile));
            FileOutputStream out = new FileOutputStream(new File(sourceFile.getParent(), "copy.mov"));
            grid.downloadToStream(id, out);

        } catch (FileNotFoundException e) {
            fail("Unexpected Exception: " + e.getClass().getName() + "\n" + e.getMessage());
        }
    }

}
