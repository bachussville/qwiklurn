/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bville.qwiklurn.database.changelogs;

import com.bville.qwiklurn.repository.flora.DBManagerForTest;
import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import junit.framework.Assert;
import static junit.framework.Assert.assertNotNull;
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
        dbMgr = new DBManagerForTest();
        MongoDatabase db = dbMgr.connect();
        db.drop();

        dbMgr.connect();
        dbMgr.setUpDatabase();
        
    }
    @Test
    public void uniqueIndexOnLatinNameIsActive() {
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

}
