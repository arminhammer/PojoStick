/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arminhammer.pojostick;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

/**
 *
 * @author armin
 */
public class PojoStickList<T> implements List<T> {

    Node head;
    PojoStick pj;

    public PojoStickList(String filename) {
        pj = new PojoStick(filename);
        head = null;
        pj.add(head);
    }
    
    private Node getHead() {
        return (Node) pj.find(head);
    }
    
    private void setHead(Node newHead) {
        Node oldHead = (Node) pj.find(head);
        pj.update(oldHead, newHead);
    }

    private class Node {

        T item;
        Node next;

        public Node(T item) {
            this.item = item;
            this.next = null;
            pj.add(this);
        }

        public T getItem() {
            Node it = (Node) pj.find(this);
            return it.item;
        }

        public void setItem(T item) {
            Node oldNode = (Node) pj.find(this);
            Node newNode = new Node(oldNode.item);
            newNode.item = item;
            newNode.next = oldNode.next;
            pj.update(oldNode, newNode);
        }

        public Node getNext() {
            Node node = (Node) pj.find(this);
            return node.next;
        }

        public void setNext(Node next) {
            Node oldNode = (Node) pj.find(this);
            Node newNode = new Node(oldNode.item);
            newNode.item = item;
            newNode.next = next;
            pj.update(oldNode, newNode);
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 97 * hash + Objects.hashCode(this.item);
            hash = 97 * hash + Objects.hashCode(this.next);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Node other = (Node) obj;
            if (!Objects.equals(this.item, other.item)) {
                return false;
            }
            if (!Objects.equals(this.next, other.next)) {
                return false;
            }
            return true;
        }
    }

    private Node getLastNode() {
        if (getHead() == null) {
            return null;
        }
        else if (getHead().getNext() == null) {
            return getHead();
        }
        else {
            Node next = getHead().getNext();
            while (next.getNext() != null) {
                next = next.getNext();
            }
            return next;
        }
    }

    //Add support for Integer.MAX_VALUE
    @Override
    public int size() {
        if (getHead() == null) {
            return 0;
        }
        else {
            int count = 1;
            Node next = getHead();
            while (next.getNext() != null) {
                count++;
                next = next.getNext();
            }
            return count;
        }
    }

    @Override
    public boolean isEmpty() {
        List<Object> contents = pj.find();
        if (contents.size() == 0) {
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public boolean contains(Object o) {
        return pj.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean add(T e) {
        if(pj.contains(e)) {
            return false;
        }
        Node newNode = new Node(e);
        newNode.setItem(e);
        if (getHead() == null) {
            setHead(newNode);
        }
        else {
            Node last = getLastNode();
            last.setNext(newNode);
        }
        return true;
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void clear() {
        setHead(null);
    }

    @Override
    public T get(int index) {
        if(getHead() == null) {
            //throw IndexOutOfBoundsException(e);
        }
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public T set(int index, T element) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void add(int index, T element) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public T remove(int index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int indexOf(Object o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int lastIndexOf(Object o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ListIterator<T> listIterator() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
