/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arminhammer.pojostick;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author armin
 */
public class PojoStickCollection<T> implements Collection<T> {

    PojoStick pj;

    public PojoStickCollection(String filename) {
        pj = new PojoStick(filename);
    }

    public PojoStickCollection(Collection<T> collection, String filename) {
        pj = new PojoStick(filename);
        for (T item : collection) {
            pj.add(item);
        }
    }

    @Override
    public int size() {
        return pj.size();
    }

    @Override
    public boolean isEmpty() {
        int size = pj.size();
        if (size == 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean contains(Object o) {
        return pj.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        List<T> current = (List<T>) pj.find();
        return current.iterator();
    }

    @Override
    public Object[] toArray() {
        int size = pj.size();
        Object[] returnArray = new Object[size];
        List<Object> contents = pj.find();
        for (int i = 0; i < size; i++) {
            returnArray[i] = contents.get(i);
        }
        return returnArray;
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return pj.find().toArray(a);
    }

    @Override
    public boolean add(T e) {
        pj.add(e);
        return true;
    }

    @Override
    public boolean remove(Object o) {
        pj.delete(o);
        return true;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object obj : c) {
            if (!pj.contains(obj)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        for (Object obj : c) {
            pj.add(obj);
        }
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        for (Object obj : c) {
            pj.delete(obj);
        }
        return true;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        List<T> toDelete = new ArrayList<T>();
        List<T> current = (List<T>) pj.find();
        for (T obj : current) {
            if (!c.contains(obj)) {
                toDelete.add(obj);
            }
        }
        for (T t : toDelete) {
            pj.delete(t);
        }
        return true;
    }

    @Override
    public void clear() {
        List<T> current = (List<T>) pj.find();
        for (T t : current) {
            pj.delete(t);
        }
    }
}

