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
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import org.bson.Document;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Bart
 */
public class DBScriptsTest {

    private static DBManagerForTest dbMgr;

    @Before
    public void init(){
        dbMgr = new DBManagerForTest(null);
        MongoDatabase db = dbMgr.connect();
        db.drop();

        dbMgr.connect();
        dbMgr.setUpDatabase();
        
    }
    @Test
    public void uniqueIndexForFloraIsActive() {
        MongoCollection f = dbMgr.getFloraCollection();
        Document doc = dbMgr.getDummyTree("u").toBson();
        Document doc2 = dbMgr.getDummyTree("u").toBson();
        f.insertOne(doc);
        try {
            f.insertOne(doc2);
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
}
