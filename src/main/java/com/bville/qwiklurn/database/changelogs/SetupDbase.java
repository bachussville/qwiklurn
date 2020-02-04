/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bville.qwiklurn.database.changelogs;

import com.github.mongobee.changeset.ChangeLog;
import com.github.mongobee.changeset.ChangeSet;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Collation;
import org.bson.Document;

/**
 *
 * @author Bart
 */
@ChangeLog(order = "001")
public class SetupDbase {

    private DBCollection getFloraCollection(DB db){
        return db.getCollection("Flora");
    }
    
    @ChangeSet(order = "001", id = "changeId#1", author = "mongoBee")
    public void addCollectionFlora(DB db) {
        DBCollection flora = db.createCollection("Flora", null);
        DBObject idxDef = new BasicDBObject();
        idxDef.put("latinName", Integer.parseInt("1"));
        DBObject idxOptions = new BasicDBObject();
        idxOptions.put("name", "latinName_Unq");
        idxOptions.put("unique", true);
        flora.createIndex(idxDef, idxOptions);
    }
    
    @ChangeSet(order = "002", id = "changeId#2", author = "mongoBee")
    public void addCollectionSpecies(DB db) {
        DBCollection flora = db.createCollection("Species", null);
        DBObject idxDef = new BasicDBObject();
        idxDef.put("name", Integer.parseInt("1"));
        DBObject idxOptions = new BasicDBObject();
        idxOptions.put("name", "name_Unq");
        idxOptions.put("unique", true);
        flora.createIndex(idxDef, idxOptions);
        
    }
}
