/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bville.qwiklurn.repository.flora;

import com.bville.qwiklurn.repository.flora.DbManager;
import com.bville.qwiklurn.repository.flora.type.TreeClass;
import com.mongodb.DBCollection;
import org.bson.Document;

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
        getFloraCollection().insertOne(tree.toBson());
        return treeAsDoc;
    }

    public TreeClass getDummyTree(String latinName) {
        TreeClass dummy = new TreeClass();
        dummy.setLatinName(latinName);
        dummy.setCommonName("cName");

        return dummy;
    }
}
