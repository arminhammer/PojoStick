/*
 * PojoStick, a small single-file Java object store.
 * By Armin Graf, copyright 2012-
 * Licensed under the Apache License 2.0.
 */
package org.arminhammer.pojostick;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
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
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author armin
 */
public class PojoStick {

    // This is the file that will be used as the datastore
    private File pojofile;
    // Logger
    private Logger LOGGER;
    // This list stores the objects that are currently in the store.  This is currently
    // only filled when adding new objects, then cleared.
    private List<Object> objects;
    // A list for new objects that still need to be changed.
    private List<Object> addObjects;
    // A list to hold the objects that are to be deleted.
    private List<Object> delObjects;
    // The JSON converter
    private Gson gson;
    //private GsonBuilder queueGson;
    private QueueProcessor queueThread;
    private String queueSeparator = "----QUEUE----";
    private Queue<PojoAction> actionQueue;
    private boolean dirty;

    private enum Action {

        SAVE, DELETE
    };
    /* flag to tell persist() whether this.objects is current or needs to be
     * refreshed.
     */
    private boolean ready;

    /**
     * Constructor for PojoStick. Parameter is a string for the file path that
     * will be used as the datastore.
     *
     * @param filename
     */
    PojoStick(String filename) {
        // Initialize variables.
        this.LOGGER = LoggerFactory.getLogger(PojoStick.class);
        GsonBuilder queueGson = new GsonBuilder();
        queueGson.registerTypeAdapter(PojoAction.class, new PojoActionDeSerializer());
        this.gson = queueGson.create();
        this.pojofile = new File(filename);
        objects = new ArrayList<>(0);
        addObjects = new ArrayList<>(0);
        delObjects = new ArrayList<>(0);
        actionQueue = new ConcurrentLinkedQueue<PojoAction>();
        this.ready = false;
        this.dirty = false;
        /* Check to see if the file exists.  Verify that it is a valid
         * PojoStick object store, and then do some further validations
         * to make sure that it is useable.  If it doesn't exist, create
         * it.
         */
        if (this.pojofile.exists()) {
            try {
                this.verifyFile();
            }
            catch (FileNotVerifiableException e) {
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
        }
        else {
            try {
                File parent = this.pojofile.getParentFile();
                System.out.println("Parent: " + parent);
                if (!this.pojofile.getParentFile().exists()) {
                    this.pojofile.getParentFile().mkdirs();
                }
                FileWriter writer;
                try {
                    writer = new FileWriter(pojofile, true);
                    writer.write(queueSeparator + "\n");
                    writer.close();
                }
                catch (IOException ex) {
                    LOGGER.error("IOError: " + ex);
                }
                System.out.println("pojofile is " + this.pojofile);
                boolean created = this.pojofile.createNewFile();
                System.out.println(created);

            }
            catch (IOException ex) {
                LOGGER.error("IOException " + ex);
            }
        }
        queueThread = new QueueProcessor(this);
    }

    /**
     * Function to verify that the file can be used as a valid Pojostick object
     * store.
     *
     * @return true if verified, false if not verified.
     * @throws org.arminhammer.pojostick.PojoStick.FileNotVerifiableException
     */
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
        }
        catch (Exception e) {//Catch exception if any
            LOGGER.error("Error: File verification failed.  File is corrupt: " + e.getMessage());
        }
        return true;
    }

    /**
     * Internal class to store actions on the database.
     */
    private class PojoAction {

        private Action action;
        private String type;
        private Object target;

        public PojoAction() {
        }

        PojoAction(Action action, Object target) {
            this.action = action;
            this.type = target.getClass().getName();
            this.target = target;
        }
    }

    private class PojoActionDeSerializer implements JsonDeserializer<PojoAction> {

        @Override
        public PojoAction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jobject = (JsonObject) json;
            PojoAction newPA = new PojoAction();
            newPA.action = Action.valueOf(jobject.get("action").getAsString());
            newPA.type = jobject.get("type").getAsString();
            try {
                newPA.target = context.deserialize(jobject.get("target"), Class.forName(newPA.type));
            }
            catch (ClassNotFoundException ex) {
                java.util.logging.Logger.getLogger(PojoStick.class.getName()).log(Level.SEVERE, null, ex);
            }
            //newPA.target = newPA.type.getClass().cast(jobject.get("target"));
            //Object asObject = (Object)jobject.get("target");
            return newPA;
        }
    }

    /**
     * Internal method that adds an action to the end of the file.
     *
     * @param action SAVE or DELETE
     * @param target the Object to store.
     */
    private void addAction(Action action, Object target) {
        PojoAction pa = new PojoAction(action, target);
        synchronized (this) {
            FileWriter writer;
            try {
                writer = new FileWriter(pojofile, true);
                writer.write(gson.toJson(pa) + "\n");
                writer.close();
            }
            catch (IOException ex) {
                LOGGER.error("IOError: " + ex);
            }
            this.dirty = true;
        }
    }

    /**
     * Method to allow user to save object if added with add()
     *
     * @return void
     */
    public void save() {
        this.persist();
        this.addObjects.clear();
        this.delObjects.clear();
    }

    /**
     * Method to allow user to save a specified object. Saves Object t.
     *
     * @param t
     */
    public void save(Object t) {
        this.addAction(Action.SAVE, t);
        //this.add(t);
        //this.persist();
        //this.addObjects.clear();
        //this.delObjects.clear();
    }

    /*public <T> Query<T> createQuery(Class<T> kind) {

     return new Query<T>(kind);
     }*/
    /**
     * Adds an object to the object store. save() must be called before the
     * object will be persisted!
     *
     * @param t
     */
    public void add(Object t) {
        this.addAction(Action.SAVE, t);
        //this.addObjects.add(t);
    }

    /**
     * Internal method that saves new objects, and removes deleted ones, and
     * writes to the file.
     */
    private void persist() {
        synchronized (this) {
            if (this.ready == false) {
                this.objects = find();
                Queue<PojoAction> currentQueue = this.readQueue();
            }
            Queue<PojoAction> currentQueue = this.actionQueue;
            //this.objects.addAll(this.addObjects);
            //this.objects.removeAll(delObjects);
            int fileLength = this.objects.size();
            String[] lines = new String[fileLength];
            for (int i = 0; i < this.objects.size(); i++) {
                String jsonString = gson.toJson(this.objects.get(i));
                String className = this.objects.get(i).getClass().getName();
                lines[i] = className + "\t" + jsonString + "\n";
            }
            StringBuilder writeContents = new StringBuilder();
            for (int i = 0; i < lines.length; i++) {
                writeContents.append(lines[i]);
            }
            StringBuilder writeQueue = new StringBuilder();
            for (PojoAction action : currentQueue) {
                String line = gson.toJson(action) + "\n";
                writeQueue.append(line);
            }
            FileWriter writer;
            try {
                writer = new FileWriter(pojofile, false);
                writer.write(writeContents.toString());
                writer.write(queueSeparator + "\n");
                writer.write(writeQueue.toString());
                writer.close();
            }
            catch (IOException ex) {
                LOGGER.error("IOError: " + ex);
            }
            this.objects.clear();
            this.ready = false;
        }
    }

    /**
     * Deletes object t from the object store.
     *
     * @param t
     */
    public void delete(Object t) {
        this.addAction(Action.DELETE, t);
        //this.delObjects.add(t);
    }

    /**
     * Method that allows user to search the object store using both an object
     * type and a string query.
     *
     * @param <T>
     * @param className
     * @param query
     * @return a List of objects of type className
     */
    public <T extends Object> List<T> findType(Class<T> className, String query) {
        List<Object> rawObjects = readContents(query);
        // Add new objects in case new objects have been added but not saved.
        rawObjects.addAll(addObjects);
        List<T> returnObjects = new ArrayList<T>();
        for (Object obj : rawObjects) {
            if (className.getName().equals(obj.getClass().getName())) {
                returnObjects.add(className.cast(obj));
            }
        }
        return returnObjects;
    }

    /**
     * Method to search the object store.
     *
     * @param query
     * @return a List<Object> of all objects that match the criteria.
     */
    public List<Object> find(String query) {
        return readContents(query);
    }

    /**
     * Returns all objects currently in the object store.
     *
     * @return a List<Object> of all objects in the store.
     */
    public List<Object> find() {
        return readContents(null);
    }

    /**
     * Method to read the file and return a List<Object> of what it contains. If
     * a query is given, it restricts the list to objects that match the
     * criteria.
     *
     * @param query
     * @return
     */
    private List<Object> readContents(String query) {
        if (this.dirty) {
            this.queueThread.goAhead();
        }
        synchronized (this) {
            ArrayList<Object> returnList = new ArrayList<Object>();
            try {
                FileInputStream fstream = new FileInputStream(pojofile);
                DataInputStream in = new DataInputStream(fstream);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String line;
                if (query == null) {
                    while (!(line = br.readLine()).equals(queueSeparator)) {
                        returnList.add(reAnimate(line));
                    }
                }
                else {
                    while (!(line = br.readLine()).equals(queueSeparator)) {
                        if (line.contains(query)) {
                            returnList.add(reAnimate(line));
                        }
                    }
                }
                in.close();
            }
            catch (Exception e) {//Catch exception if any
                LOGGER.error("Error: " + e.getMessage());
            }
            return returnList;
        }
    }

    /**
     * Private helper method that reads a string and inflates it into an object.
     *
     * @param input
     * @return
     */
    private Object reAnimate(String input) {
        String[] split = input.split("\t");
        Object classType = null;
        //Object newInstance = null;
        try {
            try {
                classType = Class.forName(split[0]).newInstance();
                //newInstance = classType.newInstance();
            }
            catch (InstantiationException ex) {
                LOGGER.error("Instantiation failed " + ex);
            }
            catch (IllegalAccessException ex) {
                LOGGER.error("Illegal access " + ex);
            }
        }
        catch (ClassNotFoundException e) {
            LOGGER.error("ClassNotFoundException " + e);
            //java.util.logging.Logger.getLogger(PojoStick.class.getName()).log(Level.SEVERE, null, ex);
        }
        //out = gson.fromJson(input, classType)
        Object out = gson.fromJson(split[1], classType.getClass());
        return classType.getClass().cast(out);
    }

    /**
     * Internal method to read the queue at the end of the file.
     */
    private Queue<PojoAction> readQueue() {
        synchronized (this) {
            this.actionQueue.clear();
            Queue<PojoAction> tempQueue = new ConcurrentLinkedQueue<PojoAction>();
            try {
                FileInputStream fstream = new FileInputStream(pojofile);
                DataInputStream in = new DataInputStream(fstream);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.equals(queueSeparator)) {
                        while ((line = br.readLine()) != null) {
                            PojoAction action = gson.fromJson(line, PojoAction.class);
                            tempQueue.add(action);
                        }
                    }
                }
                in.close();
            }
            catch (Exception e) {//Catch exception if any
                LOGGER.error("Error: " + e.getMessage());
            }
            this.actionQueue = tempQueue;
            return this.actionQueue;
        }
    }

    /**
     * Internal method that processes the queue at the end of the file.
     */
    private void processQueue() {
        this.readQueue();
        if (!this.actionQueue.isEmpty()) {
            this.objects = find();
            while (!this.actionQueue.isEmpty()) {
                PojoAction next = this.actionQueue.remove();
                if (next.action == Action.SAVE) {
                    Object toSave;
                    try {
                        toSave = Class.forName(next.type).cast(next.target);
                        this.objects.add(toSave);
                    }
                    catch (ClassNotFoundException ex) {
                        this.LOGGER.error("Class not found: " + ex);
                    }
                }
                else if (next.action == Action.DELETE) {
                    Object toDelete;
                    try {
                        toDelete = Class.forName(next.type).cast(next.target);
                        this.objects.remove(toDelete);
                    }
                    catch (ClassNotFoundException ex) {
                        this.LOGGER.error("Class not found: " + ex);
                    }
                }
            }
            this.ready = true;
            this.persist();
            this.dirty = false;
        }
    }

    private static class QueueProcessor extends Thread {

        boolean keepGoing;
        PojoStick pojostick;

        public QueueProcessor(PojoStick pojostick) {
            this.pojostick = pojostick;
            this.keepGoing = true;
            start();
        }

        public void windDown() {
            this.keepGoing = false;
        }

        public void goAhead() {
            if (this.pojostick.dirty) {
                pojostick.processQueue();
            }
        }

        @Override
        public void run() {
            while (keepGoing) {
                if (this.pojostick.dirty) {
                    pojostick.processQueue();
                }
                /*try {
                 Thread.sleep(1000);
                 }
                 catch (InterruptedException ex) {
                 this.pojostick.LOGGER.error("Interruption exception " + ex);
                 }*/
            }
        }
    }

    /**
     * Close the PojoStick file after you are done.
     */
    void close() {
        objects = null;
        addObjects = null;
        this.queueThread.windDown();
    }

    /**
     * Custom exception to throw if the file cannot be validated as a proper
     * PojoStick file.
     */
    private static class FileNotVerifiableException extends Exception {

        public FileNotVerifiableException() {
        }
    }
}
