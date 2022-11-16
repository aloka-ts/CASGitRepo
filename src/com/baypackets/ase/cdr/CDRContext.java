/*
 * CDRContext.java
 *
 * Created on Jun 20, 2005
 *
 */
package com.baypackets.ase.cdr;

import java.util.Properties;

import com.baypackets.ase.sbb.CDR;
import com.baypackets.ase.sbb.CDRWriteFailedException;

/**
 * The CDRContext interface defines the container's contract with the CDR Module.
 * The application will always work with the <code>CDR</code> interface. 
 * The implementation of the CDR interface would internally make calls to this interface. 
 * 
 * <p>
 * This interface defines the methods for creating the CDR and formatting and writing it.
 *
 * @see com.baypackets.ase.sbb.CDR
 */
public interface CDRContext {
	
	/**
	 * This method initializes this CDRContext using the parameters specified
	 * in the given Properties object.
	 *
	 * @param props - Properties of the CDR Context
	 * @throws InitializationFailedException if an error occurrs during 
	 * initialization.
	 */
	public void initialize(Properties props) throws InitializationFailedException;
	
	/**
	 *  This method returns id of this CDR Context.
	 */
	int getId();
	
	/**
	 * Creates and returns an implementation of the CDR interface.
	 *
	 * @return CDR object
	 */
	public CDR createCDR();
	
	/**
	 * Creates and returns a CDR object of the class specified.
	 *
	 * @param clazz - The Class specifying the type of the CDR object to create.
	 * @return the CDR Object or NULL if the CDR object creation failed.
	 */
	public CDR createCDR(Class clazz) ;
	
	/**
	 * Formats the CDR to the format specified by this object's configuration 
	 * and returns it.
	 *
	 * @param cdr  The CDR object to be formatted.
	 * @return a string representation of the given CDR object.
	 */
	public String formatCDR(CDR cdr);
	
	/**
	 * Formats the given CDR object and writes the formatted string to the 
	 * destination (eg. File, etc.)
	 *
	 * @param cdr  The CDR object to be written
	 * @throws CDRWriteFailedException if the write operation fails.
	 */
	public void writeCDR(CDR cdr) throws CDRWriteFailedException;
	
	/**
	 * Callback method for the CDRWriter to notify this object that the write
	 * location is unavailable.
	 *
	 * @param writer The CDRWriter that detected the failure.
	 */
	public void failureDetected(CDRWriter writer);
	
	/**
	 * Callback method for the CDRWriter to notify this object that the write
	 * location has been restored.
	 *
	 * @param writer  The CDRWriter that detected that the write location is
	 * available.
	 */
	public void failureCorrected(CDRWriter writer);

	/**
	 * Returns any CDRWriter objects associated with this CDRContext or an empty
	 * array if none are currently associated.
	 */
	public CDRWriter[] getWriters();
	
	//sumit@sbtm new method for CDR writing [
	/**
	 * Iterates the string array of cdrs and writes the string cdr to the 
	 * destination (eg. File, etc.)
	 *
	 * @param cdr  The String array containing cdrs to be written
	 * @throws CDRWriteFailedException if the write operation fails.
	 */
	public void writeCDR(String[] strCdr, CDR cdr ) throws CDRWriteFailedException;
	//]sumit@sbtm new method for CDR writing	
}
