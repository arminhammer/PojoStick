/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arminhammer.pojostick;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
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
public class PojoStickTest<T extends Object> {

    public PojoStickTest() {
    }

    //Test object that contains a single primitive integer
    private class TestPojoIntegerObject {

        private int testInteger = 0;

        public TestPojoIntegerObject(int initial) {
            this.testInteger = initial;
        }

        public int getTestInteger() {
            return testInteger;
        }

        public void setTestInteger(int testInteger) {
            this.testInteger = testInteger;
        }
    }

    // Test object that contains several strings and Integer objects
    private class TestPojoComplexObject {

        private String testString1;
        private String testString2;
        private String testString3;
        private Integer testInteger1;
        private Integer testInteger2;

        public TestPojoComplexObject(String testString1, String testString2, String testString3, Integer testInteger1, Integer testInteger2) {
            this.testString1 = testString1;
            this.testString2 = testString2;
            this.testString3 = testString3;
            this.testInteger1 = testInteger1;
            this.testInteger2 = testInteger2;
        }

        public String getTestString1() {
            return testString1;
        }

        public void setTestString1(String testString1) {
            this.testString1 = testString1;
        }

        public String getTestString2() {
            return testString2;
        }

        public void setTestString2(String testString2) {
            this.testString2 = testString2;
        }

        public String getTestString3() {
            return testString3;
        }

        public void setTestString3(String testString3) {
            this.testString3 = testString3;
        }

        public Integer getTestInteger1() {
            return testInteger1;
        }

        public void setTestInteger1(Integer testInteger1) {
            this.testInteger1 = testInteger1;
        }

        public Integer getTestInteger2() {
            return testInteger2;
        }

        public void setTestInteger2(Integer testInteger2) {
            this.testInteger2 = testInteger2;
        }
    }

    private class TestPojoCollectionsObject {

        private String name;
        private List<String> list;
        private Map<Integer, String> map;
        private Set<String> set;

        public TestPojoCollectionsObject(String name, List<String> list, Map<Integer, String> map, Set<String> set) {
            this.name = name;
            this.list = list;
            this.map = map;
            this.set = set;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<String> getList() {
            return list;
        }

        public void setList(List<String> list) {
            this.list = list;
        }

        public Map<Integer, String> getMap() {
            return map;
        }

        public void setMap(Map<Integer, String> map) {
            this.map = map;
        }

        public Set<String> getSet() {
            return set;
        }

        public void setSet(Set<String> set) {
            this.set = set;
        }
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void stringTest() {
        //Set the filename
        String filename = "/home/armin/data/pojostick/stringtest.pojo";
        //Initialize pojostick db
        PojoStick pj = new PojoStick(filename);
        TestPojoStringObject test1 = new TestPojoStringObject("Test String");
        pj.save(test1);
        List<TestPojoStringObject> resultString = pj.findType(TestPojoStringObject.class, "Test String");
        assertEquals(test1.getTestString(), resultString.get(0).getTestString());
        TestPojoStringObject test2 = new TestPojoStringObject("Test String 2");
        pj.save(test2);
        List<TestPojoStringObject> resultList = pj.findType(TestPojoStringObject.class, "Test String");
        List<TestPojoStringObject> compareList = new ArrayList<TestPojoStringObject>();
        compareList.add(test1);
        compareList.add(test2);
        assertEquals(resultList.size(), compareList.size());
        pj.close();
        //File db = new File("/home/armin/data/pojostick/stringtest.pojo");
        //db.delete();
    }

    /*
    //@Test
    public void integerTest() {
        //Set the filename
        String filename = "/home/armin/data/pojostick/integertest.pojo";
        //Initialize pojostick db
        PojoStick pj = new PojoStick(filename);
        TestPojoIntegerObject test1 = new TestPojoIntegerObject(1);
        pj.save(test1);
        TestPojoIntegerObject resultObject = (TestPojoIntegerObject)pj.createQuery(TestPojoIntegerObject.class).find("1").get();
        assertEquals(test1, resultObject);
        TestPojoIntegerObject test2 = new TestPojoIntegerObject(2);
        pj.save(test2);
        List<TestPojoIntegerObject> resultList = (List<TestPojoIntegerObject>)pj.findAll();
        List<TestPojoIntegerObject> compareList = new ArrayList<TestPojoIntegerObject>();
        compareList.add(test1);
        compareList.add(test2);
        assertEquals(resultList, compareList);
    }

    //@Test
    public void testComplexObject() {
        //Set the filename
        String filename = "/home/armin/data/pojostick/complextest.pojo";
        //Initialize pojostick db
        PojoStick pj = new PojoStick(filename);
        TestPojoComplexObject test1 = new TestPojoComplexObject("One", "Two", "Three", 1, 2);
        TestPojoComplexObject test2 = new TestPojoComplexObject("One", "Three", "Four", 1, 3);
        TestPojoComplexObject test3 = new TestPojoComplexObject("One", "Four", "Five", 1, 4);
        TestPojoComplexObject test4 = new TestPojoComplexObject("One", "Five", "Six", 1, 5);
        pj.add(test1);
        pj.add(test2);
        pj.add(test3);
        pj.add(test4);
        pj.persist();
        //Make sure everything is there
        List<TestPojoComplexObject> resultList1 = (List<TestPojoComplexObject>)pj.findAll();
        List<TestPojoComplexObject> compareList1 = new ArrayList<TestPojoComplexObject>();
        compareList1.add(test1);
        compareList1.add(test2);
        compareList1.add(test3);
        compareList1.add(test4);
        assertEquals(resultList1, compareList1);
        //Do a more limited string search
        List<TestPojoComplexObject> resultList2 = (List<TestPojoComplexObject>)pj.findAll();
        List<TestPojoComplexObject> compareList2 = new ArrayList<TestPojoComplexObject>();
        compareList2.add(test1);
        compareList2.add(test2);
        compareList2.add(test3);
        compareList2.add(test4);
        assertEquals(resultList2, compareList2);
        //Do a more limited integer search
        List<TestPojoComplexObject> resultList3 = (List<TestPojoComplexObject>)pj.findAll();
        List<TestPojoComplexObject> compareList3 = new ArrayList<TestPojoComplexObject>();
        compareList3.add(test1);
        compareList3.add(test2);
        compareList3.add(test3);
        compareList3.add(test4);
        assertEquals(resultList3, compareList3);
        // Delete test
        pj.delete(test2);
        pj.delete(test3);
        pj.persist();
        List<TestPojoComplexObject> compareList4 = new ArrayList<TestPojoComplexObject>();
        compareList3.add(test1);
        compareList3.add(test4);
        assertEquals(resultList3, compareList3);
    }
    
    //@Test
    public void testCollections() {
        //Set the filename
        String filename = "/home/armin/data/pojostick/collectiontest.pojo";
        //Initialize pojostick db
        PojoStick pj = new PojoStick(filename);
        List<String> list1 = new ArrayList<String>();
        list1.add("One");
        list1.add("Two");
        Map<Integer, String> map1 = new HashMap<Integer, String>();
        map1.put(1, "One");
        map1.put(2, "Two");
        map1.put(3, "Three");
        Set<String> set1 = new CopyOnWriteArraySet<String>();
        set1.add("Four");
        set1.add("Five");
        set1.add("Six");
        TestPojoCollectionsObject test1 = new TestPojoCollectionsObject("Onez", list1, map1, set1);
        List<String> list2 = new ArrayList<String>();
        list2.add("Two");
        list2.add("Three");
        Map<Integer, String> map2 = new HashMap<Integer, String>();
        map2.put(1, "Two");
        map2.put(2, "Three");
        map2.put(3, "Four");
        Set<String> set2 = new CopyOnWriteArraySet<String>();
        set2.add("Five");
        set2.add("Six");
        set2.add("Seven");
        TestPojoCollectionsObject test2 = new TestPojoCollectionsObject("Twoz", list2, map2, set2);
        pj.add(test1);
        pj.add(test2);
        pj.persist();
    }
    */
}
