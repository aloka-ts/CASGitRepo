/*
 * Created on Jun 20, 2005
 *
 */
package com.baypackets.ase.cdr;

import java.util.Iterator;

import com.baypackets.ase.sbb.CDR;

/**
 * This interface defines an object used to return a string representation
 * of a given CDR object.  The returned representation is determined
 * by a format string passed to this object's "compile" method.  The type
 * of format string accepted is determined by the specific CDRFormat 
 * implementation.
 */
public interface CDRFormat {
		
	/**
	 * This method parses the CDR format passed and identifies the fields
	 * to be written and the position and creates an internal 
         * data-structure that helps formatting the data quickly and 
         * efficiently.
	 * 
	 * The main purpose of this method is to avoid parsing the CDR format 
         * string each time the CDR is to be written. 
         *
	 * @param format Output format for the CDR.
         * @throws CompileFormatException if an error occurs while compiling 
         * the given format string.
	 */
	public void compile(String format) throws CompileFormatException;
	
	/**
	 * Generates the String representation of the given CDR object as 
         * specified by the format.
	 * 
	 * @param cdr CDR to be formatted.
	 * @return The formatted CDR String
	 */
	public String format(CDR cdr);
	
	/**
	 * Returns the list of CDR field names as specified in the format.
         *
	 * @return - An iterator over all CDR field names specified in the 
         * format or an empty iterator if no field names were specified.
	 */
	public Iterator getCDRFields();
        
}
