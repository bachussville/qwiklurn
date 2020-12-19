/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bville.qwiklurn.repository.flora;

import com.bville.qwiklurn.repository.flora.type.interfaces.IFloraSubType;
import com.bville.qwiklurn.repository.flora.type.Loofboom;
import com.google.common.collect.Lists;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.Document;
import org.bson.types.ObjectId;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author Bart
 */
public class DbManagerTest {

    private static DBManagerForTest dbMgr;

    public DbManagerTest() {
    }

    @BeforeClass
    public static void beforeClass(){
        dbMgr = new DBManagerForTest(null);
        dbMgr.setUpDatabase();
   }

    @Before
    public void before() {
        dbMgr.clearAllCollections();
    }

    @Test
    public void testListAlphabetically() {
        
        Document doc1 = dbMgr.persistTree("b_comesLast");
        Document doc2 = dbMgr.persistTree("a_ComesFirst");

        List<IFloraSubType> result = dbMgr.listFloraTypesAlphabetically();
        assertEquals(2, result.size());
        assertEquals("doc2 should be first", result.get(0).getLatinName(), "a_ComesFirst");
        assertEquals("doc1 should be last", result.get(1).getLatinName(), "b_comesLast");

    }


    @Test
    public void findFloraById() {
        Loofboom l1 = dbMgr.getDummyTree("1");

        dbMgr.saveFlora(l1);

        IFloraSubType r = dbMgr.getFloraById(l1.getId());
        assertEquals(l1.getId(), r.getId());
    }

    @Test
    public void deleteFloraById() {
        
        Loofboom l1 = dbMgr.getDummyTree("1");

        dbMgr.saveFlora(l1);

        IFloraSubType r = dbMgr.getFloraById(l1.getId());
        assertEquals(l1.getId(), r.getId());

        boolean result  = dbMgr.deleteFloraById(l1.getId());
        assertTrue(result);
        r = dbMgr.getFloraById(l1.getId());
        assertNull(r);
    }


    @Test
    public void findFloraByFilter() {
        
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
        
        Document doc1 = dbMgr.persistTree("b_comesLast");
        Document doc2 = dbMgr.persistTree("a_ComesFirst");

        List<IFloraSubType> result = dbMgr.findFloraByFilter(null);
        assertEquals(2, result.size());
    }

    @Test
    public void getDbName() {
        assertEquals("QwiklurnUnitTest", dbMgr.getDbName());
    }

    private Loofboom getCompleteTreeClass(boolean forInsert) {
        Loofboom newTree = dbMgr.getDummyTree("new");

        //SubType for Loofboom is TREE
        //newTree.setSubType(FloraSubTypeEnum.TREE);
        List<FunctieEnum> fTypes = new ArrayList<>();
        fTypes.add(forInsert ? FunctieEnum.DECO : FunctieEnum.BIO);
        newTree.setFunctionTypes(fTypes);
        Species species = new Species(forInsert ? "species" : "anotherSpecies");
        species.setReproductionTechniques(Lists.newArrayList(VoortplantingEnum.STEKKEN, VoortplantingEnum.SPLITSEN));
        species.setPrice(PricingCategory.B);
        newTree.setSpecies(species);
        newTree.setLatinName(forInsert ? "lName" : "anotherLatinName");
        newTree.setCommonName(forInsert ? "cName" : "anotherCommonName");
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
        newTree.setWinterLeaves(forInsert ? Boolean.FALSE : Boolean.TRUE);
        List<BodemEigenschapEnum> soiltypes = new ArrayList();
        soiltypes.add(forInsert ? BodemEigenschapEnum.KALK_ARM : BodemEigenschapEnum.HUMUS_ARM);
        newTree.setSoilTypes(soiltypes);
        Map<OpvallendeEigenschapEnum, String> specialProperties = new EnumMap<OpvallendeEigenschapEnum, String>(OpvallendeEigenschapEnum.class);
        specialProperties.put(OpvallendeEigenschapEnum.GEUR, forInsert ? "lekker" : "nogLekkerder");
        newTree.setSpecialProperties(specialProperties);
        newTree.setBlossomMonths(forInsert ? Arrays.asList(4) : Arrays.asList(5));
        newTree.setHarvestMonths(forInsert ? Arrays.asList(8) : Arrays.asList(9));
        return newTree;
    }

    private void validateTree(boolean forInsert, Loofboom readTreeClass, Loofboom newTree, Species readSpecies) {
        assertNotNull(readTreeClass.getId().toHexString());
        assertEquals(FloraSubTypeEnum.LOOFBOOM, readTreeClass.getSubType());
        assertEquals(forInsert ? FunctieEnum.DECO : FunctieEnum.BIO, readTreeClass.getFunctionTypes().get(0));
        assertNotNull(readTreeClass.getSpecies());
        assertEquals(forInsert ? "lName" : "anotherLatinName", newTree.getLatinName());
        assertEquals(forInsert ? "cName" : "anotherCommonName", newTree.getCommonName());
        assertEquals(newTree.getMediaReferences().size(), readTreeClass.getMediaReferences().size());
        assertEquals(((BsonDocument) newTree.getMediaReferences().get(0)).getObjectId("gridFsId").getValue().toHexString(),
                ((Document) readTreeClass.getMediaReferences().get(0)).getObjectId("gridFsId").toHexString());
        assertEquals(forInsert ? 200 : 400, readTreeClass.getMaxHeight().longValue());
        assertEquals(forInsert ? 150 : 300, readTreeClass.getMaxWidth().longValue());
        assertEquals(forInsert ? "maintenance" : "moreMaintenance", readTreeClass.getMaintenance());
        assertEquals(forInsert ? Boolean.FALSE : Boolean.TRUE, readTreeClass.getWinterLeaves());
        assertEquals(forInsert ? BodemEigenschapEnum.KALK_ARM : BodemEigenschapEnum.HUMUS_ARM, readTreeClass.getSoilTypes().get(0));
        assertEquals(forInsert ? "lekker" : "nogLekkerder", readTreeClass.getSpecialProperties().get(OpvallendeEigenschapEnum.GEUR));
        assertEquals(forInsert ? "4" : "5", "" + readTreeClass.getBlossomMonths().get(0));
        assertEquals(forInsert ? "8" : "9", "" + readTreeClass.getHarvestMonths().get(0));

        assertEquals(newTree.getSpecies().getName(), readTreeClass.getSpecies().getName());
        assertEquals(readTreeClass.getSpecies().getId().toHexString(), readSpecies.getId().toHexString());
        assertEquals(readTreeClass.getId().toHexString(), readSpecies.getMembers().get(0).toHexString());
    }

    @Test
    public void saveTreeClass_newWithNewSpecies() {
        
        Loofboom newTree = getCompleteTreeClass(true);
        assertNull(newTree.getId());
        assertNull(newTree.getSpecies().getId());

        dbMgr.saveFlora(newTree);

        Loofboom readTreeClass = (Loofboom) dbMgr.getFloraById(newTree.getId());
        Species readSpecies = dbMgr.getSpeciesById(readTreeClass.getSpecies().getId());

        validateTree(true, readTreeClass, newTree, readSpecies);
        assertEquals(1, dbMgr.getFloraCollection().countDocuments());
        assertEquals(1, dbMgr.getSpeciesCollection().countDocuments());

    }

    @Test
    public void saveTreeClass_newWithExistingSpecies() {
        

        dbMgr.saveSpecies(dbMgr.getDummySpecies("species"));
        Document dbSpecies = (Document) dbMgr.getSpeciesCollection().find().first();

        Loofboom newTree = getCompleteTreeClass(true);
        newTree.setSpecies(dbMgr.getSpeciesById(dbSpecies.getObjectId("_id")));

        assertNull(newTree.getId());
        assertNotNull(newTree.getSpecies().getId());

        dbMgr.saveFlora(newTree);

        Loofboom readTreeClass = (Loofboom) dbMgr.getFloraById(newTree.getId());
        Species readSpecies = dbMgr.getSpeciesById(readTreeClass.getSpecies().getId());

        validateTree(true, readTreeClass, newTree, readSpecies);
        assertEquals(1, dbMgr.getFloraCollection().countDocuments());
        assertEquals(1, dbMgr.getSpeciesCollection().countDocuments());

    }

    @Test
    public void saveTreeClass_existingWithNewSpecies() {
        
        Loofboom oldTree = getCompleteTreeClass(true);
        assertNull(oldTree.getId());
        assertNull(oldTree.getSpecies().getId());
        dbMgr.saveFlora(oldTree);
        ObjectId dbTreeId = ((Document) dbMgr.getFloraCollection().find().first()).getObjectId("_id");

        Loofboom updatedTree = getCompleteTreeClass(false);
        updatedTree.setId(new ObjectId(dbTreeId.toHexString()));

        dbMgr.saveFlora(updatedTree);

        Loofboom readTreeClass = (Loofboom) dbMgr.getFloraById(updatedTree.getId());
        Species readSpecies = dbMgr.getSpeciesById(readTreeClass.getSpecies().getId());

        validateTree(false, readTreeClass, updatedTree, readSpecies);
        assertEquals(1, dbMgr.getFloraCollection().countDocuments());
        assertEquals(2, dbMgr.getSpeciesCollection().countDocuments());
    }

    @Test
    public void saveTreeClass_existingWithExistingSpecies() {
        
        dbMgr.saveSpecies(dbMgr.getDummySpecies("species"));
        Document dbSpecies = (Document) dbMgr.getSpeciesCollection().find().first();

        Loofboom oldTree = getCompleteTreeClass(true);
        oldTree.setSpecies(dbMgr.getSpeciesById(dbSpecies.getObjectId("_id")));

        assertNull(oldTree.getId());
        assertNotNull(oldTree.getSpecies().getId());

        dbMgr.saveFlora(oldTree);
        ObjectId dbTreeId = ((Document) dbMgr.getFloraCollection().find().first()).getObjectId("_id");

        Loofboom updatedTree = getCompleteTreeClass(false);
        updatedTree.setSpecies(dbMgr.getSpeciesById(dbSpecies.getObjectId("_id")));
        updatedTree.setId(new ObjectId(dbTreeId.toHexString()));

        dbMgr.saveFlora(updatedTree);

        Loofboom readTreeClass = (Loofboom) dbMgr.getFloraById(updatedTree.getId());
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

    @Test
    public void addTreeToProject() {
        
        Loofboom floraElement = dbMgr.getDummyTree("tree1");
        dbMgr.saveFlora(floraElement);

        assertNotNull(floraElement.getId());

        Project projectElement = dbMgr.getDummyProject("EersteKlant");
        projectElement.getMembers().add(new ProjectElement(floraElement.getId(), "cactus", -1));
        dbMgr.saveProject(projectElement);

        Project a = dbMgr.getProjectById(projectElement.getId());
        assertNotNull(a.getMembers());
        assertEquals("cactus", a.getMembers().get(0).getName());

        assertEquals(1, dbMgr.listProjects().size());
        assertEquals(1, dbMgr.listProjectsFor(floraElement).size());

        Loofboom xx = dbMgr.getDummyTree("tree1");
        dbMgr.saveFlora(floraElement);
        assertEquals(0, dbMgr.listProjectsFor(xx).size());
    }
}
