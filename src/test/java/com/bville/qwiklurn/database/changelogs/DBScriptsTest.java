/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bville.qwiklurn.database.changelogs;

import com.bville.qwiklurn.repository.flora.DBManagerForTest;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Bart
 */
public class DBScriptsTest {

    private static DBManagerForTest dbMgr;

    @BeforeClass
    public static void setup() {
        dbMgr = new DBManagerForTest(null);
        MongoDatabase db = dbMgr.connect();
        db.drop();

        dbMgr.connect();
        dbMgr.setUpDatabase();
    }

    @Before
    public void initTest() {
        MongoDatabase db = dbMgr.connect();
        dbMgr.clearAllCollections();
    }


    @Test
    public void uniqueIndexForFloraIsActive() {
        MongoCollection f = dbMgr.getFloraCollection();
        Document doc = dbMgr.getDummyTree("u").toBson();
        Document doc2 = dbMgr.getDummyTree("u").toBson();
        f.insertOne(doc);
        long count = f.countDocuments();
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            f.insertOne(doc2);
            count = f.countDocuments();
            System.out.println("Long = " + count);
            System.out.println("key1 = " + doc.toJson());
            System.out.println("key1 = " + doc2.toJson());
            fail("Duplicate shouldn't be inserted");
        } catch (MongoException e) {
            assertEquals(11000, e.getCode());
            assertTrue("Wrong duplicate key", e.getMessage().indexOf("latinName_Unq") > 0);
        }

    }

    @Test
    public void uniqueIndexForSpeciesIsActive() {
        MongoCollection f = dbMgr.getSpeciesCollection();
        Document doc = dbMgr.getDummySpecies("u").toBson();
        Document doc2 = dbMgr.getDummySpecies("u").toBson();
        f.insertOne(doc);
        try {
            f.insertOne(doc2);
            fail("Duplicate shouldn't be inserted");
        } catch (MongoException e) {
            assertEquals(11000, e.getCode());
            assertTrue("Wrong duplicate key", e.getMessage().indexOf("name_Unq") > 0);
        }
    }

    @Test
    public void uniqueIndexForProjectsIsActive() {
        MongoCollection f = dbMgr.getProjectCollection();
        Document doc = dbMgr.getDummyProject("p").toBson();
        Document doc2 = dbMgr.getDummySpecies("p").toBson();
        f.insertOne(doc);
        try {
            f.insertOne(doc2);
            fail("Duplicate shouldn't be inserted");
        } catch (MongoException e) {
            assertEquals(11000, e.getCode());
            assertTrue("Wrong duplicate key", e.getMessage().indexOf("name_Unq") > 0);
        }
    }
}
