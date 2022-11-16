/*
 * CompileFormatException.java
 *
 * Created on June 29, 2005, 4:16 PM
 */
package com.baypackets.ase.cdr;

/**
 * Exception thrown by the CDRFormat.compile() method if an error occurs
 * while compiling the given format string.
 *
 * @see com.baypackets.ase.cdr.CDRFormat#compile(String)
 * @author Baypackets
 */
public class CompileFormatException extends Exception {
    
    public CompileFormatException() {
        super();
    }

    public CompileFormatException(String msg) {
        super(msg);        
    }
    
}
