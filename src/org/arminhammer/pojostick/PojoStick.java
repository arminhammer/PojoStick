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
    // The JSON converter
    private Gson gson;
    // Thread that processes the queue.
    private QueueProcessor queueThread;
    // Constant that sets the divider between the contents and the queue.
    private static final String queueSeparator = "----QUEUE----";
    // The queue that holds the actions that need to be performed.
    private Queue<PojoAction> actionQueue;
    // Flag to tell the system if there are items in the queue that need
    // to be processed before the file can be read.
    private boolean dirty;

    // List of actions that can be performed on objects in the store.
    public enum Action {

        SAVE, DELETE, UPDATE
    };
    /* flag to tell persist() whether this.getObjects() is current or needs to be
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
        queueGson.registerTypeAdapter(PojoItem.class, new PojoItemDeSerializer());
        this.gson = queueGson.create();
        this.pojofile = new File(filename);
        objects = new ArrayList<>(0);
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
                //System.out.println("Parent: " + parent);
                if (!this.pojofile.getParentFile().exists()) {
                    this.pojofile.getParentFile().mkdirs();
                }
                FileWriter writer;
                try {
                    writer = new FileWriter(pojofile, true);
                    writer.write(queueSeparator + "\n");
                    writer.close();
                } catch (IOException ex) {
                    LOGGER.error("IOError: " + ex);
                }
                //System.out.println("pojofile is " + this.pojofile);
                boolean created = this.pojofile.createNewFile();
                //System.out.println(created);

            } catch (IOException ex) {
                LOGGER.error("IOException " + ex);
            }
        }
        queueThread = new QueueProcessor(this);
    }

    public synchronized boolean isDirty() {
        return dirty;
    }

    public synchronized void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public synchronized File getPojofile() {
        return pojofile;
    }

    public synchronized void setPojofile(File pojofile) {
        this.pojofile = pojofile;
    }

    public synchronized List<Object> getObjects() {
        return objects;
    }

    public synchronized void setObjects(List<Object> objects) {
        this.objects = objects;
    }

    public synchronized Queue<PojoAction> getActionQueue() {
        return actionQueue;
    }

    public synchronized void setActionQueue(Queue<PojoAction> actionQueue) {
        this.actionQueue = actionQueue;
    }

    public synchronized boolean isReady() {
        return ready;
    }

    public synchronized void setReady(boolean ready) {
        this.ready = ready;
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
            while (!(line = br.readLine()).equals(queueSeparator)) {
                PojoItem test = (gson.fromJson(line, PojoItem.class));
                if (test == null) {
                    return false;
                }
            }
            while ((line = br.readLine()) != null) {
                System.out.println("Looked at queue.");
                PojoAction temp = gson.fromJson(line, PojoAction.class);
            }
            in.close();
        } catch (Exception e) {//Catch exception if any
            LOGGER.error("Error: File verification failed.  File is corrupt: " + e.getMessage());
        }
        return true;
    }

    /**
     * Method to allow user to save object if added with add()
     *
     * @return void
     */
    public void save() {
        this.persist();
    }

    /**
     * Method to allow user to save a specified object. Saves Object t.
     *
     * @param t
     */
    public void save(Object t) {
        this.addAction(Action.SAVE, t);
    }

    /**
     * Adds an object to the object store. save() must be called before the
     * object will be persisted!
     *
     * @param t
     */
    public void add(Object t) {
        this.addAction(Action.SAVE, t);
    }

    public void update(Object t, Object newT) {
        this.addAction(Action.UPDATE, t, newT);
    }

    /**
     * Internal method that saves new objects, and removes deleted ones, and
     * writes to the file.
     */
    private void persist() {
        System.out.println("Persist Starting...");
        if (this.ready == false) {
            this.setObjects(readContents(null));
            Queue<PojoAction> currentQueue = this.readQueue();
        }
        Queue<PojoAction> currentQueue = this.getActionQueue();
        int fileLength = this.getObjects().size();
        String[] lines = new String[fileLength];
        for (int i = 0; i < this.getObjects().size(); i++) {
            lines[i] = gson.toJson(new PojoItem(this.getObjects().get(i))) + "\n";
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
            writer = new FileWriter(this.getPojofile(), false);
            writer.write(writeContents.toString());
            writer.write(queueSeparator + "\n");
            writer.write(writeQueue.toString());
            writer.close();
        } catch (IOException ex) {
            LOGGER.error("IOError: " + ex);
        }
        this.getObjects().clear();
        this.ready = false;
        System.out.println("Persist done.");
    }

    /**
     * Deletes object t from the object store.
     *
     * @param t
     */
    public void delete(Object t) {
        this.addAction(Action.DELETE, t);
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
        if (isDirty()) {
            System.out.println("It's dirty, waiting to read.");
            this.processQueue();
        }
        List<Object> rawObjects = readContents(query);
        // Add new objects in case new objects have been added but not saved.
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
        if (isDirty()) {
            System.out.println("It's dirty, waiting to read.");
            this.processQueue();
        }
        return readContents(query);
    }

    /**
     * Returns all objects currently in the object store.
     *
     * @return a List<Object> of all objects in the store.
     */
    public List<Object> find() {
        if (isDirty()) {
            System.out.println("It's dirty, waiting to read.");
            this.processQueue();
        }
        return readContents(null);
    }

    /**
     * Returns the number of objects currently in the object store.
     *
     * @return a List<Object> of all objects in the store.
     */
    public int size() {
        if (isDirty()) {
            System.out.println("It's dirty, waiting to read.");
            this.processQueue();
        }
        return readContents(null).size();
    }
    
    /**
     * Returns a specific object if it is found in the store.
     *
     * @param o
     * @return
     */
    public boolean contains(Object o) {
        if (isDirty()) {
            System.out.println("It's dirty, waiting to read.");
            this.processQueue();
            //this.queueThread.process();
        }
        List<Object> list = readContents(null);
        for (Object listO : list) {
            if (o.equals(listO)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a specific object if it is found in the store.
     *
     * @param o
     * @return
     */
    public Object find(Object o) {
        if (isDirty()) {
            System.out.println("It's dirty, waiting to read.");
            this.processQueue();
            //this.queueThread.process();
        }
        List<Object> list = readContents(null);
        for (Object listO : list) {
            if (o.equals(listO)) {
                return listO;
            }
        }
        return null;
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
        System.out.println("readcontents wants to read...");
        System.out.println("readcontents is reading.");
        ArrayList<Object> returnList = new ArrayList<Object>();
        try {
            FileInputStream fstream = new FileInputStream(this.getPojofile());
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            if (query == null) {
                while (!(line = br.readLine()).equals(queueSeparator)) {
                    PojoItem nextItem = gson.fromJson(line, PojoItem.class);
                    returnList.add(nextItem.getItem());
                    //returnList.add(gson.fromJson(null, null));
                    //returnList.add(reAnimate(line));
                }
            } else {
                while (!(line = br.readLine()).equals(queueSeparator)) {
                    if (line.contains(query)) {
                        PojoItem nextItem = gson.fromJson(line, PojoItem.class);
                        returnList.add(nextItem.getItem());
                        //returnList.add(reAnimate(line));
                    }
                }
            }
            in.close();
        } catch (Exception e) {//Catch exception if any
            LOGGER.error("Error: " + e.getMessage());
        }
        System.out.println("Done reading.");
        return returnList;
    }

    /**
     * Close the PojoStick file after you are done.
     */
    void close() {
        objects = null;
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

    /**
     * Internal method that adds an action to the end of the file. Allows for
     * update.
     *
     * @param action SAVE or DELETE
     * @param target the Object to store.
     */
    private void addAction(Action action, Object target, Object newVersion) {
        PojoAction pa = new PojoAction(action, target, newVersion);
        writeAction(pa);
    }

    /**
     * Internal method that adds an action to the end of the file.
     *
     * @param action SAVE or DELETE
     * @param target the Object to store.
     */
    private void addAction(Action action, Object target) {
        PojoAction pa = new PojoAction(action, target);
        writeAction(pa);
    }

    private void writeAction(PojoAction pa) {
        FileWriter writer;
        System.out.println("addaction wants to add...");
        System.out.println("addaction took control.");
        try {
            writer = new FileWriter(this.getPojofile(), true);
            writer.write(gson.toJson(pa) + "\n");
            writer.close();
        } catch (IOException ex) {
            LOGGER.error("IOError: " + ex);
        }
        setDirty(true);
        System.out.println("Addaction done, dirty set to " + isDirty());
    }

    /**
     * Class for the internal queue processing thread.
     */
    private static class QueueProcessor extends Thread {

        // Flag to whether to keep going
        boolean keepGoing;
        // Reference to the parent object
        PojoStick pojostick;

        public QueueProcessor(PojoStick pojostick) {
            this.pojostick = pojostick;
            this.keepGoing = true;
            start();
            //System.out.println("Queue Processor Starting!");
        }

        /**
         * Method to stop the thread.
         */
        public void windDown() {
            this.keepGoing = false;
        }

        /**
         * Method to process the queue.
         */
        public void process() {
            if (this.pojostick.isDirty()) {
                //System.out.println("Time to process the queue!");
                pojostick.processQueue();
            }
        }

        @Override
        public void run() {
            while (keepGoing) {
            }
        }
    }

    /**
     * Internal method to read the queue at the end of the file.
     */
    private Queue<PojoAction> readQueue() {
        System.out.println("readQueue wants to read...");
        System.out.println("readQueue is reading.");
        this.getActionQueue().clear();
        Queue<PojoAction> tempQueue = new ConcurrentLinkedQueue<PojoAction>();
        try {
            FileInputStream fstream = new FileInputStream(this.getPojofile());
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
        } catch (Exception e) {//Catch exception if any
            LOGGER.error("Error: " + e.getMessage());
        }
        this.setActionQueue(tempQueue);
        return this.getActionQueue();
    }

    /**
     * Internal method that processes the queue at the end of the file.
     */
    private void processQueue() {
        this.readQueue();
        if (!this.getActionQueue().isEmpty()) {
            this.setObjects(readContents(null));
            while (!this.getActionQueue().isEmpty()) {
                PojoAction next = this.getActionQueue().remove();
                if (next.getAction() == Action.SAVE) {
                    Object toSave;
                    try {
                        toSave = Class.forName(next.getType()).cast(next.getTarget());
                        if (!this.getObjects().contains(toSave)) {
                            this.getObjects().add(toSave);
                        }
                    } catch (ClassNotFoundException ex) {
                        this.LOGGER.error("Class not found: " + ex);
                    }
                } else if (next.getAction() == Action.DELETE) {
                    Object toDelete;
                    try {
                        toDelete = Class.forName(next.getType()).cast(next.getTarget());
                        this.getObjects().remove(toDelete);
                    } catch (ClassNotFoundException ex) {
                        this.LOGGER.error("Class not found: " + ex);
                    }
                }
            }
            this.ready = true;
            this.persist();
            setDirty(false);
        }
    }
}
