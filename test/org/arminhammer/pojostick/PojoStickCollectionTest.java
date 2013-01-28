/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arminhammer.pojostick;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author armin
 */
public class PojoStickCollectionTest {

    PojoStickCollection<String> pc;
    String fileLocation = "/home/armin/data/pojostick/test/pojostickstringcollection.pojo";
    String test1 = "This is a test";
    String test2 = "This is a test two";
    String test3 = "This is a test too";

    public PojoStickCollectionTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        pc = new PojoStickCollection<String>(fileLocation);
    }

    @After
    public void tearDown() {
        pc = null;
        File f = new File(fileLocation);
        f.delete();
    }

    /**
     * Test of size method, of class PojoStickCollection.
     */
    @Test
    public void testSize() {
        System.out.println("size");
        int expResult = 0;
        int result = pc.size();
        assertEquals(expResult, result);
        pc.add(test1);
        expResult = 1;
        result = pc.size();
        assertEquals(expResult, result);
        pc.add(test2);
        expResult = 2;
        result = pc.size();
        assertEquals(expResult, result);
    }

    /**
     * Test of isEmpty method, of class PojoStickCollection.
     */
    @Test
    public void testIsEmpty() {
        System.out.println("isEmpty");
        boolean expResult = true;
        boolean result = pc.isEmpty();
        assertEquals(expResult, result);
        pc.add(test1);
        expResult = false;
        result = pc.isEmpty();
        assertEquals(expResult, result);
    }

    /**
     * Test of contains method, of class PojoStickCollection.
     */
    @Test
    public void testContains() {
        System.out.println("contains");
        boolean expResult = false;
        boolean result = pc.contains(test1);
        assertEquals(expResult, result);
        pc.add(test1);
        expResult = true;
        result = pc.contains(test1);
        assertEquals(expResult, result);
    }

    /**
     * Test of iterator method, of class PojoStickCollection.
     */
    @Test
    public void testIterator() {
        System.out.println("iterator");
        pc.add(test1);
        pc.add(test2);
        List<String> testList = new ArrayList<String>();
        testList.add(test1);
        testList.add(test2);
        Iterator expResult = testList.iterator();
        Iterator result = pc.iterator();
        assertEquals(expResult.hasNext(), result.hasNext());
        assertEquals(expResult.next(), result.next());
    }

    /**
     * Test of toArray method, of class PojoStickCollection.
     */
    @Test
    public void testToArray_0args() {
        System.out.println("toArray");
        Object[] expResult = new Object[0];
        Object[] result = pc.toArray();
        assertArrayEquals(expResult, result);
        pc.add(test1);
        expResult = new Object[1];
        expResult[0] = test1;
        result = pc.toArray();
        assertArrayEquals(expResult, result);
    }

    /**
     * Test of toArray method, of class PojoStickCollection.
     */
    @Test
    public void testToArray_GenericType() {
        System.out.println("toArray");
        pc.add(test1);
        String[] expResult = new String[1];
        expResult[0] = test1;
        String[] result = (String[]) pc.toArray(new String[0]);
        assertArrayEquals(expResult, result);
    }

    /**
     * Test of add method, of class PojoStickCollection.
     */
    @Test
    public void testAdd() {
        System.out.println("add");
        boolean expResult = true;
        boolean result = pc.add(test1);
        assertEquals(expResult, result);
    }

    /**
     * Test of remove method, of class PojoStickCollection.
     */
    @Test
    public void testRemove() {
        System.out.println("remove");
        boolean expResult = true;
        pc.add(test1);
        boolean result = pc.remove(test1);
        assertEquals(expResult, result);
        assertEquals(0, pc.size());
    }

    /**
     * Test of containsAll method, of class PojoStickCollection.
     */
    @Test
    public void testContainsAll() {
        System.out.println("containsAll");
        Collection<String> c = new ArrayList<String>();
        c.add(test1);
        c.add(test2);
        pc.add(test1);
        pc.add(test2);
        pc.add(test3);
        boolean expResult = true;
        boolean result = pc.containsAll(c);
        assertEquals(expResult, result);
    }

    /**
     * Test of addAll method, of class PojoStickCollection.
     */
    @Test
    public void testAddAll() {
        System.out.println("addAll");
        Collection<String> c = new ArrayList<String>();
        c.add(test1);
        c.add(test2);
        boolean expResult = true;
        boolean result = pc.addAll(c);
        assertEquals(expResult, result);
        assertEquals(expResult, pc.contains(test1));
        assertEquals(expResult, pc.contains(test2));
    }

    /**
     * Test of removeAll method, of class PojoStickCollection.
     */
    @Test
    public void testRemoveAll() {
        System.out.println("removeAll");
        Collection<String> c = new ArrayList<String>();
        c.add(test2);
        c.add(test3);
        pc.add(test1);
        pc.add(test2);
        pc.add(test3);
        boolean expResult = true;
        boolean result = pc.removeAll(c);
        assertEquals(expResult, result);
        assertEquals(expResult, pc.contains(test1));
        assertEquals(false, pc.contains(test2));
        assertEquals(false, pc.contains(test3));
    }

    /**
     * Test of retainAll method, of class PojoStickCollection.
     */
    @Test
    public void testRetainAll() {
        System.out.println("retainAll");
        System.out.println("removeAll");
        Collection<String> c = new ArrayList<String>();
        c.add(test2);
        c.add(test3);
        pc.add(test1);
        pc.add(test2);
        pc.add(test3);
        boolean expResult = true;
        boolean result = pc.retainAll(c);
        assertEquals(expResult, result);
        assertEquals(false, pc.contains(test1));
        assertEquals(true, pc.contains(test2));
        assertEquals(true, pc.contains(test3));
    }

    /**
     * Test of clear method, of class PojoStickCollection.
     */
    @Test
    public void testClear() {
        System.out.println("clear");
        pc.add(test1);
        pc.clear();
        assertEquals(0, pc.size());
    }
}
