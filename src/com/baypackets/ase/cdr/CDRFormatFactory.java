/*
 * CDRFormatFactory.java
 *
 * Created on June 29, 2005, 4:12 PM
 */
package com.baypackets.ase.cdr;

import com.baypackets.ase.common.Registry;
import com.baypackets.ase.util.Constants;


/**
 * This class provides a factory for creating CDRFormat objects.
 *
 * @see com.baypackets.ase.cdr.CDRFormat
 * @author Baypackets
 */
public abstract class CDRFormatFactory {
        
    /**
     * Returns a singleton instance of this abstract factory class.
     *
     * @throws CDRFormatException if an error occurs while instantiating the 
     * factory class.
     */
    public static CDRFormatFactory getInstance() throws CDRFormatFactoryException {
        return (CDRFormatFactory)Registry.lookup(Constants.NAME_CDR_FORMAT_FACTORY);
    }
    
    /**
     * Creates and returns an implementation of the CDRFormat interface.
     *
     * @throws CDRFormatFactoryException if an error occurs while instantiating
     * the CDRFormat impl class.
     */
    public abstract CDRFormat createCDRFormat() throws CDRFormatFactoryException;
    
}
