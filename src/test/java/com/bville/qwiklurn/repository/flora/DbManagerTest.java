/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bville.qwiklurn.repository.flora;

import com.bville.qwiklurn.repository.flora.type.interfaces.IFloraSubType;
import com.bville.qwiklurn.repository.flora.type.Tree;
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
import static org.junit.Assert.assertNull;
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
        dbMgr = new DBManagerForTest(null);
        //dbMgr.setUpDatabase();
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
    
    private Tree getCompleteTreeClass(boolean forInsert) {
        Tree newTree = dbMgr.getDummyTree("new");
        
        //SubType for Tree is TREE
        //newTree.setSubType(FloraSubTypeEnum.TREE);
        List<FunctionType> fTypes = new ArrayList<>();
        fTypes.add(forInsert ? FunctionType.DECO : FunctionType.BIO);
        newTree.setFunctionTypes(fTypes);
        Species species = new Species(forInsert ? "species" : "anotherSpecies");
        newTree.setSpecies(species);
        newTree.setLatinName(forInsert ? "lName" : "anotherLatinName");
        newTree.setCommonName(forInsert ? "cName" :"anotherCommonName");
        List<Object> mediaRefs = new ArrayList<>();
        File mediaRefAsFile = null;
        try {
            mediaRefAsFile = File.createTempFile("Qwiklurn", "unitTest");
            FileWriter fw = new FileWriter(mediaRefAsFile);
            fw.append(forInsert ? "abc" : "xyz");
            fw.close();
        } catch (Exception e) {
            fail("Error writign temp file");
        }
        mediaRefs.add(mediaRefAsFile);
        newTree.setMediaReferences(mediaRefs);
        newTree.setMaxHeight(forInsert ? 200 : 400);
        newTree.setMaxWidth(forInsert ? 150 : 300);
        newTree.setMaintenance(forInsert ? "maintenance" : "moreMaintenance");
        newTree.setColor(forInsert ? "color" : "anotherColor");
        newTree.setWinterLeaves(forInsert ? Boolean.FALSE : Boolean.TRUE);
        List<SoilType> soiltypes = new ArrayList();
        soiltypes.add(forInsert ? SoilType.KALK_ARM : SoilType.HUMUS_ARM );
        newTree.setSoilTypes(soiltypes);
        Map<SpecialsType, String> specialProperties = new EnumMap<SpecialsType, String>(SpecialsType.class);
        specialProperties.put(SpecialsType.GEUR, forInsert ? "lekker" : "nogLekkerder");
        newTree.setSpecialProperties(specialProperties);
        newTree.setSeason(forInsert ? SeasonType.HERFST:SeasonType.VOORJAAR);
        return newTree;
    }

    private void validateTree(boolean forInsert, Tree readTreeClass, Tree newTree, Species readSpecies) {
        assertNotNull(readTreeClass.getId().toHexString());
        assertEquals(FloraSubTypeEnum.TREE, readTreeClass.getSubType());
        assertEquals(forInsert ? FunctionType.DECO : FunctionType.BIO, readTreeClass.getFunctionTypes().get(0));
        assertNotNull(readTreeClass.getSpecies());
        assertEquals(forInsert ? "lName":"anotherLatinName", newTree.getLatinName());
        assertEquals(forInsert ? "cName":"anotherCommonName", newTree.getCommonName());
        assertEquals(newTree.getMediaReferences().size(), readTreeClass.getMediaReferences().size());
        assertEquals(((BsonDocument)newTree.getMediaReferences().get(0)).getObjectId("gridFsId").getValue().toHexString()
                , ((Document)readTreeClass.getMediaReferences().get(0)).getObjectId("gridFsId").toHexString());
        assertEquals(forInsert ? 200:400, readTreeClass.getMaxHeight().longValue());
        assertEquals(forInsert ? 150:300,  readTreeClass.getMaxWidth().longValue());
        assertEquals(forInsert ? "maintenance":"moreMaintenance", readTreeClass.getMaintenance());
        assertEquals(forInsert ? "color":"anotherColor", readTreeClass.getColor());
        assertEquals(forInsert ? Boolean.FALSE: Boolean.TRUE, readTreeClass.getWinterLeaves());
        assertEquals(forInsert ? SoilType.KALK_ARM:SoilType.HUMUS_ARM, readTreeClass.getSoilTypes().get(0));
        assertEquals(forInsert ? "lekker":"nogLekkerder", readTreeClass.getSpecialProperties().get(SpecialsType.GEUR));
        assertEquals(forInsert ? SeasonType.HERFST:SeasonType.VOORJAAR, readTreeClass.getSeason());
        
        assertEquals(newTree.getSpecies().getName(), readTreeClass.getSpecies().getName());
        assertEquals(readTreeClass.getSpecies().getId().toHexString(), readSpecies.getId().toHexString());
        assertEquals(readTreeClass.getId().toHexString(), readSpecies.getMembers().get(0).toHexString());
    }
    
    @Test
    public void saveTreeClass_newWithNewSpecies() {
        dbMgr.clearAllCollections();
        Tree newTree = getCompleteTreeClass(true);
        assertNull(newTree.getId());
        assertNull(newTree.getSpecies().getId());

        dbMgr.saveFlora(newTree);

        Tree readTreeClass = (Tree) dbMgr.getFloraById(newTree.getId());
        Species readSpecies = dbMgr.getSpeciesById(readTreeClass.getSpecies().getId());

        
        validateTree(true, readTreeClass, newTree, readSpecies);
        assertEquals(1, dbMgr.getFloraCollection().countDocuments());
        assertEquals(1, dbMgr.getSpeciesCollection().countDocuments());
        
    }

    
    @Test
    public void saveTreeClass_newWithExistingSpecies() {
        dbMgr.clearAllCollections();
        
        dbMgr.saveSpecies(new Species("species"));
        Document dbSpecies = (Document)dbMgr.getSpeciesCollection().find().first();
        
        Tree newTree = getCompleteTreeClass(true);
        newTree.setSpecies(dbMgr.getSpeciesById(dbSpecies.getObjectId("_id")));
        
        assertNull(newTree.getId());
        assertNotNull(newTree.getSpecies().getId());
        
        dbMgr.saveFlora(newTree);

        Tree readTreeClass = (Tree) dbMgr.getFloraById(newTree.getId());
        Species readSpecies = dbMgr.getSpeciesById(readTreeClass.getSpecies().getId());

        validateTree(true, readTreeClass, newTree, readSpecies);
        assertEquals(1, dbMgr.getFloraCollection().countDocuments());
        assertEquals(1, dbMgr.getSpeciesCollection().countDocuments());
        
    }
    
    @Test
    public void saveTreeClass_existingWithNewSpecies() {
        dbMgr.clearAllCollections();
        Tree oldTree = getCompleteTreeClass(true);
        assertNull(oldTree.getId());
        assertNull(oldTree.getSpecies().getId());
        dbMgr.saveFlora(oldTree);
        ObjectId dbTreeId = ((Document)dbMgr.getFloraCollection().find().first()).getObjectId("_id");
        
        Tree updatedTree = getCompleteTreeClass(false);
        updatedTree.setId(new ObjectId(dbTreeId.toHexString()));
        
        dbMgr.saveFlora(updatedTree);
        
        Tree readTreeClass = (Tree) dbMgr.getFloraById(updatedTree.getId());
        Species readSpecies = dbMgr.getSpeciesById(readTreeClass.getSpecies().getId());

        
        validateTree(false, readTreeClass, updatedTree, readSpecies);
        assertEquals(1, dbMgr.getFloraCollection().countDocuments());
        assertEquals(2, dbMgr.getSpeciesCollection().countDocuments());
    }
    @Test
    public void saveTreeClass_existingWithExistingSpecies() {
        dbMgr.clearAllCollections();
        dbMgr.saveSpecies(new Species("species"));
        Document dbSpecies = (Document)dbMgr.getSpeciesCollection().find().first();
        
        Tree oldTree = getCompleteTreeClass(true);
        oldTree.setSpecies(dbMgr.getSpeciesById(dbSpecies.getObjectId("_id")));
        
        assertNull(oldTree.getId());
        assertNotNull(oldTree.getSpecies().getId());

        dbMgr.saveFlora(oldTree);
        ObjectId dbTreeId = ((Document)dbMgr.getFloraCollection().find().first()).getObjectId("_id");
        
        Tree updatedTree = getCompleteTreeClass(false);
        updatedTree.setSpecies(dbMgr.getSpeciesById(dbSpecies.getObjectId("_id")));
        updatedTree.setId(new ObjectId(dbTreeId.toHexString()));
        
        dbMgr.saveFlora(updatedTree);
        
        Tree readTreeClass = (Tree) dbMgr.getFloraById(updatedTree.getId());
        Species readSpecies = dbMgr.getSpeciesById(readTreeClass.getSpecies().getId());

        
        validateTree(false, readTreeClass, updatedTree, readSpecies);
        assertEquals(1, dbMgr.getFloraCollection().countDocuments());
        assertEquals(1, dbMgr.getSpeciesCollection().countDocuments());
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
