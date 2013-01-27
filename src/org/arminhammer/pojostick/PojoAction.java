/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arminhammer.pojostick;

import org.arminhammer.pojostick.PojoStick.Action;

/**
 * Internal class to store actions on the database.
 */
public class PojoAction {

    private PojoStick.Action action;
    private String type;
    private Object target;
    private Object newVersion;

    public PojoAction() {
    }

    PojoAction(PojoStick.Action action, Object target) {
        this.action = action;
        this.type = target.getClass().getName();
        this.target = target;
        this.newVersion = null;
    }

    PojoAction(PojoStick.Action action, Object target, Object newVersion) {
        this.action = action;
        this.type = target.getClass().getName();
        this.target = target;
        this.newVersion = newVersion;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public Object getNewVersion() {
        return newVersion;
    }

    public void setNewVersion(Object newVersion) {
        this.newVersion = newVersion;
    }
    
}
