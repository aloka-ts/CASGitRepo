/*
 * Created on Nov 22, 2004
 *
 */
package com.baypackets.ase.cdr;

import java.net.URI;

import com.baypackets.ase.sbb.CDRWriteFailedException;

/**
 * This interface defines an object used to write CDR objects to the backing
 * store (ex. file system, database, etc.).
 */
public interface CDRWriter {
	
	/**
	 * Will be called to initialize this CDRWriter.
	 * Before calling this method, the attributes of the CDR writter should be
	 * set using the corresponding mutator methods.
	 * 
	 * @throws CDRWriterException If CDR location is not set (OR) 
	 * if there is any problem accessing the CDR location. 
	 */
	public void initialize() throws InitializationFailedException;

	/**
	 * Will be called to re-initialize the CDR Writer.
	 * Before calling this method, the attributes of the CDR writter should be
	 * set using the corresponding mutator methods.
	 * 
	 * This method would be called to re-initialize the writer after a write 
	 * failure has been corrected.
	 *  
	 * @throws CDRWriterException If CDR location is not set (OR) 
	 * if there is any problem accessing the CDR location. 
	 */
	
	public void reInitialize() throws InitializationFailedException;
	
	/**
	 * Writes the CDR to the underlying storage.
	 * @param cdr CDR to be written
	 * @return true if the CDR was written successfully otherwise false.
	 * @throws CDRWriterException
	 */
	public boolean write(String cdr) throws CDRWriteFailedException;

	/**
	 * Gets the CDR location where the CDRs are being currently written.
	 * @return The location where the CDRs are being written
	 */
	public URI getCDRLocation();
	
	/**
	 * Gets the maximum number of CDRs to be written to this file.
	 * @return Max CDR Count.
	 */
	public int getMaxCDRCount();
	
	/**
	 * Gets the rollover interval.
	 * @return Rollover interval in msecs.
	 */
	public int getRolloverInterval();
	
	/**
	 * Sets the location where the CDRs will be written. 
	 * @param location Location to write the CDRs
	 */
	public void setCDRLocation(URI location);
	
	/**
	 * Sets the Maximum CDR count per file. 
	 * @param count Maximum CDR COunt per CDR file.
	 */
	public void setMaxCDRCount(int count);

	/**
	 * Sets the Rollover interval in msecs.
	 * @param interval Rollover interval in msecs
	 */
	public void setRolloverInterval(int interval);
	
	/**
	 * @author Sharat
	 * Sets the CDR File Name Prefix
	 */
	public void setCdrFileNamePrefix(String string);
	
	/**
	 * @author Sharat
	 * Sets the CDR File Name Suffix to be used foe File name extension
	 */
	public void setCdrFileNameSuffix(String string);
	
	/**
	 * @author Sharat
	 * Sets the CDR Header which will be inserted every time a new CDR File is written
	 */
	public void setCdrHeader(String string);
	
	/**
	 * Returns whether the Writer is in the WRITABLE state or not
	 * @return true if writable false otherwise
	 */
	public boolean isWritable();
        
        /**
         * Closes any resources held by this object (ex. file handles,
         * database connections, etc.).
         */
        public void close();

	/**
	 * Returns "true" if this CDRWriter is a primary writer or returns "false"
	 * otherwise.
	 */
	public boolean isPrimary();

	/**
	 * Sets this CDRWriter object as a primary or secondary writer based on the
	 * given boolean parameter.
	 */
	public void setPrimary(boolean isPrimary);
	
	//sumit@sbtm new method for CDR writing [
	/**
	 * Writes the array of CDRs to the underlying storage.
	 * @param cdr String array of CDRs to be written
	 * @return true if the CDR was written successfully otherwise false.
	 * @throws CDRWriterException
	 */
	public boolean write(String[] cdr) throws CDRWriteFailedException;
	//]sumit@sbtm new method for CDR writing
	
	//saneja@bug7797 method added only for rollover without initialization[
	public void partialInitialize() throws InitializationFailedException;
	//]closed saneja@bug7797

	/**
	 * Flag to check whether to write CDRs in datewise directories or not
	 */
	public void setCdrDateDirFlag(boolean b);
	
}
