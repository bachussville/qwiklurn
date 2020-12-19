package com.bville.qwiklurn.repository.flora.type;

import com.bville.qwiklurn.repository.flora.DBManagerForTest;
import com.bville.qwiklurn.repository.flora.type.interfaces.IFloraSubType;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class AbstractFloraTest {

    private static DBManagerForTest dbMgr;

    @BeforeClass
    public static void beforeClass(){
        dbMgr = new DBManagerForTest(null);
        dbMgr.setUpDatabase();
    }


    @Test
    public void copy() {
        Loofboom l = dbMgr.getDummyTree("name");
        IFloraSubType l2 = l.getCopy("new");

        assertEquals(Loofboom.class, l2.getClass());
        assertEquals(null, l2.getId());
        assertEquals("new", l2.getLatinName());

    }
}
