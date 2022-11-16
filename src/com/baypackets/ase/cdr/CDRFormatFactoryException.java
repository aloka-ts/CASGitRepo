/*
 * CDRFormatFactoryException.java
 *
 * Created on June 29, 2005, 4:16 PM
 */
package com.baypackets.ase.cdr;

/**
 * Exception thrown by the CDRFormatFactory class.
 *
 * @see com.baypackets.ase.cdr.CDRFormatFactory
 * @author Baypackets
 */
public class CDRFormatFactoryException extends Exception {
    
    public CDRFormatFactoryException() {
        super();
    }

    public CDRFormatFactoryException(String msg) {
        super(msg);        
    }
    
}
