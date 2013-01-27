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
import java.util.ListIterator;
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
public class PojoStickListTest {

    PojoStickList<String> pl;
    String fileLocation = "/home/armin/data/pojostick/test/pojosticklist.pojo";
    String test1 = "This is a test";
    String test2 = "This is a test two";
    String test3 = "This is a test too";

    public PojoStickListTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        pl = new PojoStickList<String>(fileLocation);
    }

    @After
    public void tearDown() {
        pl = null;
        File f = new File(fileLocation);
        f.delete();
    }

    /**
     * Test of isEmpty method, of class PojoStickList.
     */
    @Test
    public void testIsEmpty() {
        System.out.println("isEmpty");
        boolean expResult = true;
        boolean result = pl.isEmpty();
        assertEquals(expResult, result);
        pl.add(test1);
        expResult = false;
        result = pl.isEmpty();
        assertEquals(expResult, result);
    }

    /**
     * Test of size method, of class PojoStickList.
     */
    //@Test
    public void testSize() {
        System.out.println("size");
        int expResult = 0;
        int result = pl.size();
        assertEquals(expResult, result);
        pl.add(test1);
        expResult = 1;
        result = pl.size();
        assertEquals(expResult, result);
        pl.add(test2);
        expResult = 2;
        result = pl.size();
        assertEquals(expResult, result);
    }

    /**
     * Test of contains method, of class PojoStickList.
     */
    //@Test
    public void testContains() {
        System.out.println("contains");
        pl.add(test1);
        boolean expResult = false;
        boolean result = pl.contains(test1);
        assertEquals(expResult, result);
    }

    /**
     * Test of iterator method, of class PojoStickList.
     */
    //@Test
    public void testIterator() {
        System.out.println("iterator");
        PojoStickList instance = null;
        Iterator expResult = null;
        Iterator result = instance.iterator();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of toArray method, of class PojoStickList.
     */
    //@Test
    public void testToArray_0args() {
        System.out.println("toArray");
        Object[] expResult = null;
        Object[] result = pl.toArray();
        assertArrayEquals(expResult, result);
    }

    /**
     * Test of toArray method, of class PojoStickList.
     */
    //@Test
    public void testToArray_GenericType() {
        System.out.println("toArray");
        Object[] a = null;
        Object[] expResult = null;
        Object[] result = pl.toArray(a);
        assertArrayEquals(expResult, result);
    }

    /**
     * Test of add method, of class PojoStickList.
     */
    //@Test
    public void testAdd_GenericType() {
        System.out.println("add");
        Object e = null;
        boolean expResult = false;
        //boolean result = pl.add(e);
        //assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }

    /**
     * Test of remove method, of class PojoStickList.
     */
    //@Test
    public void testRemove_Object() {
        System.out.println("remove");
        Object o = null;
        boolean expResult = false;
        boolean result = pl.remove(o);
        assertEquals(expResult, result);
    }

    /**
     * Test of containsAll method, of class PojoStickList.
     */
    //@Test
    public void testContainsAll() {
        System.out.println("containsAll");
        Collection<String> c = new ArrayList<String>();
        c.add(test1);
        c.add(test2);
        pl.add(test1);
        pl.add(test2);
        pl.add(test3);
        boolean expResult = true;
        boolean result = pl.containsAll(c);
        assertEquals(expResult, result);
    }

    /**
     * Test of addAll method, of class PojoStickList.
     */
    /*@Test
     public void testAddAll_Collection() {
     System.out.println("addAll");
     Collection<? extends T> c = null;
     PojoStickList instance = null;
     boolean expResult = false;
     boolean result = instance.addAll(c);
     assertEquals(expResult, result);
     // TODO review the generated test code and remove the default call to fail.
     fail("The test case is a prototype.");
     }
     */
    /**
     * Test of addAll method, of class PojoStickList.
     */
    /*@Test
     public void testAddAll_int_Collection() {
     System.out.println("addAll");
     int index = 0;
     Collection<? extends T> c = null;
     PojoStickList instance = null;
     boolean expResult = false;
     boolean result = instance.addAll(index, c);
     assertEquals(expResult, result);
     // TODO review the generated test code and remove the default call to fail.
     fail("The test case is a prototype.");
     }
     */
    /**
     * Test of removeAll method, of class PojoStickList.
     */
    //@Test
    public void testRemoveAll() {
        System.out.println("removeAll");
        Collection<String> c = new ArrayList<String>();
        c.add(test1);
        c.add(test2);
        pl.add(test1);
        pl.add(test2);
        pl.add(test3);
        boolean expResult = true;
        boolean result = pl.removeAll(c);
        assertEquals(expResult, result);
    }

    /**
     * Test of retainAll method, of class PojoStickList.
     */
    //@Test
    public void testRetainAll() {
        System.out.println("retainAll");
        Collection<String> c = new ArrayList<String>();
        c.add(test1);
        c.add(test2);
        pl.add(test1);
        pl.add(test2);
        pl.add(test3);
        boolean expResult = true;
        boolean result = pl.retainAll(c);
        assertEquals(expResult, result);
    }

    /**
     * Test of clear method, of class PojoStickList.
     */
    //@Test
    public void testClear() {
        System.out.println("clear");
        pl.add(test1);
        pl.clear();
        assertEquals(0, pl.size());
    }

    /**
     * Test of get method, of class PojoStickList.
     */
    //@Test
    public void testGet() {
        System.out.println("get");
        int index = 0;
        pl.add(test1);
        Object result = pl.get(index);
        assertEquals(test1, result);
    }

    /**
     * Test of set method, of class PojoStickList.
     */
    //@Test
    public void testSet() {
        System.out.println("set");
        int index = 0;
        pl.add(test1);
        String expResult = test2;
        String result = pl.set(index, test2);
        assertEquals(expResult, result);
    }

    /**
     * Test of add method, of class PojoStickList.
     */
    //@Test
    public void testAdd_int_GenericType() {
        System.out.println("add");
        int index = 0;
        Object element = null;
        PojoStickList instance = null;
        instance.add(index, element);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of remove method, of class PojoStickList.
     */
    //@Test
    public void testRemove_int() {
        System.out.println("remove");
        int index = 0;
        PojoStickList instance = null;
        Object expResult = null;
        Object result = instance.remove(index);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of indexOf method, of class PojoStickList.
     */
    //@Test
    public void testIndexOf() {
        System.out.println("indexOf");
        Object o = null;
        PojoStickList instance = null;
        int expResult = 0;
        int result = instance.indexOf(o);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of lastIndexOf method, of class PojoStickList.
     */
    //@Test
    public void testLastIndexOf() {
        System.out.println("lastIndexOf");
        Object o = null;
        PojoStickList instance = null;
        int expResult = 0;
        int result = instance.lastIndexOf(o);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of listIterator method, of class PojoStickList.
     */
    //@Test
    public void testListIterator_0args() {
        System.out.println("listIterator");
        PojoStickList instance = null;
        ListIterator expResult = null;
        ListIterator result = instance.listIterator();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of listIterator method, of class PojoStickList.
     */
    //@Test
    public void testListIterator_int() {
        System.out.println("listIterator");
        int index = 0;
        PojoStickList instance = null;
        ListIterator expResult = null;
        ListIterator result = instance.listIterator(index);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of subList method, of class PojoStickList.
     */
    //@Test
    public void testSubList() {
        System.out.println("subList");
        int fromIndex = 0;
        int toIndex = 0;
        PojoStickList instance = null;
        List expResult = null;
        List result = instance.subList(fromIndex, toIndex);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}
