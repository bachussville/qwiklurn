/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bville.qwiklurn;

import com.bville.qwiklurn.repository.flora.DbManager;
import com.bville.qwiklurn.repository.flora.IFloraSubType;
import com.bville.qwiklurn.repository.flora.type.TreeClass;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import static junit.framework.TestCase.assertNotNull;
import org.bson.types.ObjectId;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 *
 * @author Bart
 */
public class DbManagerTest  {

    public DbManagerTest() {
    }

    @BeforeAll
    public static void setUpClass() {
    }

    @AfterAll
    public static void tearDownClass() {
    }

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    public void testConnect() {
        System.out.println("connect");
        DbManager instance = new DbManager();
        MongoDatabase db = instance.connect();
        assertNotNull(db);
    }

    @Test
    public void testCount() {
        System.out.println("count");
        DbManager instance = new DbManager();
        long size = instance.count("xy");
        System.out.println("Number of xy elements in database: " + size);
        assertTrue(size>0);
    }
 
    @Test
    public void testListAlphabetically() {
        System.out.println("count");
        DbManager instance = new DbManager();
        List<IFloraSubType> aaa = instance.listFloraTypesAlphabetically();
        System.out.println("Number of xy elements in database: " + aaa.size());
        assertTrue(aaa.size()>0);
    }
    @Test
    public void testListLeastedTested() {
        System.out.println("count");
        DbManager instance = new DbManager();
        List<IFloraSubType> aaa = instance.listFloraTypesByLeastTested();
        System.out.println("Number of xy elements in database: " + aaa.size());
        assertTrue(aaa.size()>0);
    }
    
    @Test
    public void testListHardcodedFilter() {
        System.out.println("count");
        DbManager instance = new DbManager();
        List<IFloraSubType> aaa = instance.hardcodedQuery();
        System.out.println("Number of xy elements in database: " + aaa.size());
        for (IFloraSubType FloraType : aaa) {
            System.out.println( FloraType.getLatinName() + ": " + ((TreeClass)FloraType).getLeafage().getCode());
        }
        assertTrue(aaa.size()>0);
    }
    
    @Test
    public void testListHardcodedFilter2() {
        System.out.println("count");
        DbManager instance = new DbManager();
        List<IFloraSubType> aaa = instance.hardcodedQuery();
        List<IFloraSubType> bbb = instance.hardcodedQuery2();
        
        SortedSet s1  = new TreeSet(aaa);
        SortedSet s2  = new TreeSet(bbb);
        
        System.out.println("Number of xy elements in database: " + aaa.size());
        for (IFloraSubType FloraType : aaa) {
            System.out.println( FloraType.getLatinName() + ": " + ((TreeClass)FloraType).getLeafage().getCode());
        }
        
        assertEquals(aaa.size(),bbb.size(), "different number of results in new query");
        for (int i = 0; i < s1.size(); i++) {
            IFloraSubType get = (IFloraSubType)s1.toArray()[i];
            assertEquals(get.getId().toHexString(), ((IFloraSubType)s2.toArray()[i]).getId().toHexString());
            
        }

        
    }
    
    @Test
    @Ignore
    public void test22WriteBigFile() {
        
        DbManager instance = new DbManager();
        MongoDatabase db = instance.connect();
        GridFSBucket grid = GridFSBuckets.create(db, "best88");

        try {
            File sourceFile  = new File("D:\\JavaDev\\NetBeansProjects\\qwiklurn\\testResources\\Sample.MOV");
            ObjectId id = grid.uploadFromStream("TestJEEEE", new FileInputStream(sourceFile));
            FileOutputStream out = new FileOutputStream(new File(sourceFile.getParent(), "copy.mov"));
            grid.downloadToStream(id, out);
            
        } catch (FileNotFoundException e) {
            fail("Unexpected Exception: " + e.getClass().getName() + "\n" + e.getMessage());
        }
    }

}
