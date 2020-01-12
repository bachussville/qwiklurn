/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bville.qwiklurn.repository.flora;

import com.bville.qwiklurn.repository.flora.type.TreeClass;
import com.mongodb.DBCollection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
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
    public void testConnect() {
        MongoDatabase db = dbMgr.connect();
        assertNotNull(db);
    }

    @Test
    public void collectionsExist() {
        MongoCollection floraCollection = dbMgr.getFloraCollection();
        assertNotNull(floraCollection);
        MongoCollection speciesCollection = dbMgr.getSpeciesCollection();
        assertNotNull(speciesCollection);
    }

    @Test
    public void validateUniqueIndexFlora() {
        MongoCollection floraCollection = dbMgr.getFloraCollection();
        Document doc = dbMgr.getDummyTree("u").toBson();
        floraCollection.insertOne(doc);
        try {
            floraCollection.insertOne(doc);
            fail("Duplicate shouldn't be inserted");
        } catch (Exception e) {
        }
        
   }

    @Test
    @Ignore
    public void testListAlphabetically() {
        Document doc1 = dbMgr.persistTree("b_comesLast");
        Document doc2 = dbMgr.persistTree("a_ComesFirst");

        List<IFloraSubType> result = dbMgr.listFloraTypesAlphabetically();
        assertEquals(2, result.size());
        assertEquals(doc2, result.get(0));
        assertEquals(doc1, result.get(1));
    }

    @Test
    @Ignore
    public void testListLeastedTested() {
        System.out.println("count");
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
