/*
 * CallDAO.java
 *
 * Created on September 16, 2004, 12:32 PM
 */
package com.baypackets.clicktodial.util;



/**
 * This interface defines an object that is used to persist and retrieve
 * Call objects to and from the backing store.
 */
public interface CallDAO {      
    
    /**
     * Persists the given Call object to the backing store.
     */
    public void persist(Call call);
    
    /**
     * Retrieves the specified Call object from the backing store.
     */
    public Call findByID(String callID);
    
}
