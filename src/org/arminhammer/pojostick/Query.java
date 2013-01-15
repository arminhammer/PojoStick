/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arminhammer.pojostick;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.slf4j.Logger;

import org.slf4j.LoggerFactory;

/**
 *
 * @author armin
 */
class Query<T extends Object> {

    private Class<T> classType;
    private List<T> results;
    private Logger LOGGER;

    public Query(Class<T> kind) {
        this.classType = kind;
        this.results = new ArrayList<>(0);
        this.LOGGER = LoggerFactory.getLogger(Query.class);
    }

    /*public Query(Class<T> clazz) {
        this.classType = clazz;
        this.results = new ArrayList<>(0);
        this.LOGGER = LoggerFactory.getLogger(Query.class);
    }*/
    /*public Query<T> filter(T criteria) {
        
     }*/
    public <T> T get() {
        if (this.results.isEmpty()) {
            //return (T) new Object();
            return null;
        } else {
            return (T) this.results.get(0);
        }
    }

    public Query<T> find(String criteria) {
        Query<T> newResults = new Query<T>(this.classType);
        for (T item : this.results) {
            Field[] fields = item.getClass().getFields();
            for (int i = 0; i < fields.length; i++) {
                Class<?> fType = fields[i].getType();
                if (fType.getName().equals("String")) {
                    String val = "";
                    try {
                        val = (String) fields[i].get(i);
                    } catch (IllegalArgumentException ex) {
                        LOGGER.error(null, ex);
                    } catch (IllegalAccessException ex) {
                        LOGGER.error(null, ex);
                    }
                    if (criteria.equals(val)) {
                        newResults.results.add(item);
                    }
                }
            }
        }
        return newResults;
    }
}
