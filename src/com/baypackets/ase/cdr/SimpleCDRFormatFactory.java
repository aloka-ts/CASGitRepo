/*
 * SimpleCDRFormatFactory.java
 *
 * Created on June 29, 2005, 5:52 PM
 */
package com.baypackets.ase.cdr;

/**
 * This implementation of the CDRFormatFactory abstract class creates
 * and returns SimpleCDRFormat objects.
 *
 * @see com.baypackets.ase.cdr.SimpleCDRFormat
 * @author Baypackets
 */
public class SimpleCDRFormatFactory extends CDRFormatFactory {
    
    /**
     * Instantiates and returns a SimpleCDRFormat object.
		 * @throws CDRFormatFactoryException if an error occurs creating the
		 * CDRFormat object.
     */
    public CDRFormat createCDRFormat() throws CDRFormatFactoryException {
        return new SimpleCDRFormat();
    }
    
}
