/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arminhammer.pojostick;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author armin
 */
public class PojoStickold {

    // This is the file that will be used as the datastore
    private File pojofile;
    // Logger
    private Logger LOGGER;
    private List<Object> objects;
    private ObjectMapper mapper;
    //private Gson gson;
    private List<Object> newObjects;

    /**
     * Constructor for PojoStick. Parameter is a string for the file path that
     * will be used as the datastore.
     *
     * @param filename
     */
    PojoStickold(String filename) {
        this.LOGGER = LoggerFactory.getLogger(PojoStickold.class);
        //this.gson = new Gson();
        this.mapper = new ObjectMapper();
        this.pojofile = new File(filename);

        objects = new ArrayList<>(0);
        newObjects = new ArrayList<>(0);

        if (this.pojofile.exists()) {
            try {
                this.verifyFile();
            } catch (FileNotVerifiableException e) {
                LOGGER.error("File " + e + " exists, but was not verifiable as a valid PojoStick file.");
            }
            if (this.pojofile.isDirectory()) {
                LOGGER.error("File is a directory and cannot be used.");
                System.exit(0);
            }
            if (!this.pojofile.canRead() || !this.pojofile.canWrite()) {
                LOGGER.error("PojoStick does not have full permissions to use this file.");
                System.exit(0);
            }
        } else {
            try {
                File parent = this.pojofile.getParentFile();
                System.out.println("Parent: " + parent);
                if (!this.pojofile.getParentFile().exists()) {
                    this.pojofile.getParentFile().mkdirs();
                }
                System.out.println("pojofile is " + this.pojofile);
                boolean created = this.pojofile.createNewFile();
                System.out.println(created);

            } catch (IOException ex) {
                LOGGER.error("IOException " + ex);
            }
        }
    }

    private boolean verifyFile() throws FileNotVerifiableException {
        try {
            FileInputStream fstream = new FileInputStream(pojofile);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null) {
                Object test = (reAnimate(line));
                if (test == null) {
                    return false;
                }
            }
            in.close();
        } catch (Exception e) {//Catch exception if any
            LOGGER.error("Error: File verification failed.  File is corrupt: " + e.getMessage());
        }
        return true;
    }

    public void save(Object t) {
        this.add(t);
        this.persist();
    }

    public <T> Query<T> createQuery(Class<T> kind) {

        return new Query<T>(kind);
    }

    public void add(Object t) {
        this.newObjects.add(t);
    }

    public void persist() {
        this.objects = find();
        this.objects.addAll(this.newObjects);
        int fileLength = this.objects.size();
        String[] lines = new String[fileLength];
        for (int i = 0; i < this.objects.size(); i++) {
            String jsonString = null;
            try {
                jsonString = mapper.writeValueAsString(this.objects.get(i));
            } catch (JsonProcessingException ex) {
                java.util.logging.Logger.getLogger(PojoStickold.class.getName()).log(Level.SEVERE, null, ex);
            }
            //String jsonString = gson.toJson(this.objects.get(i));
            String className = this.objects.get(i).getClass().getName();
            lines[i] = className + "\t" + jsonString + "\n";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            sb.append(lines[i]);
        }
        FileWriter writer;
        try {
            writer = new FileWriter(pojofile, false);
            writer.write(sb.toString());
            writer.close();
        } catch (IOException ex) {
            LOGGER.error("IOError: " + ex);
        }
    }

    /*    
     private int hashMaxSize() {
     Set<Integer> keys = this.objects.keySet();
     int max = 0;
     for (Integer key : keys) {
     if (key >= max) {
     max = key;
     }
     }
     return max;
     }
     */
    public void delete(Object t) {
    }

    /*public List<Object> find(String kind, String criterion) {
     List<Object> objList = new ArrayList<Object>();
     List<Object> returnList = new ArrayList<Object>();
     for (Object obj : this.objects.values()) {
     if (obj.getClass().getSimpleName().equals(kind)) {
     objList.add(obj);
     }
     }
     for (Object found : objList) {
     Field[] attr = found.getClass().getDeclaredFields();
     for (Field field : attr) {
     if (field.getType().equals(found)) {
     }
     }
     if (found) {
     returnListList.add(found);
     }
     }
     return returnList;
     }*/
    public <T extends Object> List<T> findType(Class<T> className, String criterion) {
        List<Object> rawObjects = readFile(criterion);
        List<T> returnObjects = new ArrayList<T>();
        for (Object obj : rawObjects) {
            if (className.getName().equals(obj.getClass().getName())) {
                returnObjects.add(className.cast(obj));
            }
        }
        return returnObjects;
    }

    public List<Object> find(String criterion) {
        return readFile(criterion);
    }

    public List<Object> find() {
        return readFile(null);
    }

    private List<Object> readFile(String criteria) {
        ArrayList<Object> returnList = new ArrayList<Object>();
        try {
            FileInputStream fstream = new FileInputStream(pojofile);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            if (criteria == null) {
                while ((line = br.readLine()) != null) {
                    returnList.add(reAnimate(line));
                }
            } else {
                while ((line = br.readLine()) != null) {
                    if (line.contains(criteria)) {
                        returnList.add(reAnimate(line));
                    }
                }
            }
            in.close();
        } catch (Exception e) {//Catch exception if any
            LOGGER.error("Error: " + e.getMessage());
        }
        return returnList;
    }

    /*
     private int getHash(Object t) {
     int current = this.hashMaxSize();
     int newSize = 0;
     if (current < 25) {
     newSize = 25;
     } else {
     newSize = current + 1;
     }
     String asJson = gson.toJson(t);
     int returnHash = asJson.hashCode() % (newSize);
     boolean findSlot = false;
     while (findSlot == false) {
     if (this.objects.get(returnHash) != null) {
     returnHash++;
     } else {
     findSlot = true;
     }
     }
     return returnHash;
     }

     */
    private Object reAnimate(String input) {
        String[] split = input.split("\t");
        Class classType = null;
        Object newInstance = null;
        try {
            try {
                classType = Class.forName(split[0]);
                newInstance = classType.newInstance();
            } catch (InstantiationException ex) {
                LOGGER.error("Instantiation failed " + ex);
            } catch (IllegalAccessException ex) {
                LOGGER.error("Illegal access " + ex);
            }
        } catch (ClassNotFoundException e) {
            LOGGER.error("ClassNotFoundException " + e);
            //java.util.logging.Logger.getLogger(PojoStick.class.getName()).log(Level.SEVERE, null, ex);
        }
        //out = gson.fromJson(input, classType)
        Object out = null;
        try {
            out = mapper.readValue(split[1], classType);
        } catch (JsonParseException ex) {
            java.util.logging.Logger.getLogger(PojoStickold.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JsonMappingException ex) {
            java.util.logging.Logger.getLogger(PojoStickold.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(PojoStickold.class.getName()).log(Level.SEVERE, null, ex);
        } 
        //Object out = gson.fromJson(split[1], classType.getClass());
        return classType.getClass().cast(out);
    }

    private static class FileNotVerifiableException extends Exception {

        public FileNotVerifiableException() {
        }
    }
}
