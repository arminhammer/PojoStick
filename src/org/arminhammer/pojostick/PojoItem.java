/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arminhammer.pojostick;

/**
 * Internal class to store items in the file.
 */
public class PojoItem {

    private String type;
    private Object item;

    public PojoItem() {
    }

    public PojoItem(Object item) {
        this.item = item;
        this.type = item.getClass().getName();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getItem() {
        return item;
    }

    public void setItem(Object item) {
        this.item = item;
    }
    
}
