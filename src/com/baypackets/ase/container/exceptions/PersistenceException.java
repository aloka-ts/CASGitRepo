/*
 * PersistenceException.java
 *
 * Created on August 19, 2004, 5:12 PM
 */
package com.baypackets.ase.container.exceptions;


/**
 * This exception is thrown by a data access object if an error occurs while
 * persisting data to a backing store.
 *
 * @author Zoltan Medveczky
 */
public final class PersistenceException extends Exception {

    /**
     *
     *
     */
    public PersistenceException() {
        super();
    }

    /**
     *
     *
     */
    public PersistenceException(String message) {
        super(message);
    }

    /**
     *
     *
     */
    public PersistenceException(Exception e) {
        super(e.toString());
    }

}


