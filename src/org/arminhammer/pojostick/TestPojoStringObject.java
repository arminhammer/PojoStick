/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arminhammer.pojostick;

import java.util.Objects;

/**
 *
 * @author armin
 */
public class TestPojoStringObject {

    private String testString;

    public TestPojoStringObject() {
        
    }
    
    public TestPojoStringObject(String testString) {
        this.testString = testString;
    }

    public String getTestString() {
        return testString;
    }

    public void setTestString(String testString) {
        this.testString = testString;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + Objects.hashCode(this.testString);
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
        final TestPojoStringObject other = (TestPojoStringObject) obj;
        if (!Objects.equals(this.testString, other.testString)) {
            return false;
        }
        return true;
    }
    
    
}
