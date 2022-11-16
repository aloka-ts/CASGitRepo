/*
 * ObjectCallback.java
 *
 * Created on 03/02/2004
 */
package com.baypackets.ase.security;

import javax.security.auth.callback.Callback;


/**
 * Provides an implementation of the Callback interface that can be used to
 * propagate an arbitrary object to the login modules during a JAAS 
 * authentication.
 */
public class ObjectCallback implements Callback {

    private Object obj;
    
    public ObjectCallback() {        
    }
    
    public ObjectCallback(Object obj) {
        this.setObject(obj);
    }
    
    public Object getObject() {
        return obj;
    }
    
    public void setObject(Object obj) {
        this.obj = obj;
    }
    
}


	
