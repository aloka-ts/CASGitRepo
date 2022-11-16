/*
 * RemoveException.java
 *
 * Created on August 19, 2004, 5:12 PM
 */
package com.baypackets.ase.container.exceptions;


/**
 * This exception is thrown by a data access object if an error occurs while
 * removing data from a backing store.
 *
 * @author Zoltan Medveczky
 */
public final class RemoveException extends Exception {

    /**
     *
     *
     */
    public RemoveException() {
        super();
    }

    /**
     *
     *
     */
    public RemoveException(String message) {
        super(message);
    }

    /**
     *
     *
     */
    public RemoveException(Exception e) {
        super(e.toString());
    }

}

       