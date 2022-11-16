/*
 * FinderException.java
 *
 * Created on August 19, 2004, 5:12 PM
 */
package com.baypackets.ase.container.exceptions;


/**
 * This exception is thrown by a data access object if an error occurs while
 * retrieving data from a backing store.
 *
 * @author Zoltan Medveczky
 */
public final class FinderException extends Exception {

    /**
     *
     *
     */
    public FinderException() {
        super();
    }

    /**
     *
     *
     */
    public FinderException(String message) {
        super(message);
    }

    /**
     *
     *
     */
    public FinderException(Exception e) {
        super(e.toString());
    }

}

