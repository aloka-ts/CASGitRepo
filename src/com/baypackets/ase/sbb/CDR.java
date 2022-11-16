/*
 * Created on Jun 16, 2005
 */
package com.baypackets.ase.sbb;

import java.util.Iterator;

/**
 * The Call Detail Record interface.
 * An application can use this interface to get and set values for the CDR fields and 
 * write to the CDR.
 * <p>
 * The CDR object is available to the application as an attribute in the SipSession
 * with the name <code>"com.baypackets.ase.sbb.CDR"</code>
 * <p>
 * The application can override any default value by calling the set method on this interface. 
 * <p>
 * The following code example provides of how to use the CDR object for writing the CDR.
 *  <pre>
 * 	<code>		
 * 	CDR cdrObject = (CDR)request.getSession().getAttribute(CDR.class.getName());
 * 	cdrObject.set(FIELD_CALL_END_TIMESTAMP, ""+System.currentTimeMillis());
 * 	cdrObject.set(FIELD_CALL_DURATION_MSECS, ""+12300);
 * 	...	...	...
 * 	cdrObject.write();
 * </code>
 * </pre>
 * 
 * <p>
 * 	The CDR is written to based on the CDR configuration parameters, including:
 *  <pre>
 * 	1. CDR Primary Directory
 * 	2. CDR Secondary Directory
 * 	3. Maximum Number of CDRs per file.
 * 	4. Maximum Time for CDR file rollover
 * 	5. CDR Format, etc.
 * </pre>
 */
public interface CDR {
	
	/**
	 * Identifier for the Correlation ID field.
	 * <p> 
	 * This Identifier would be used in a CDR to correlate the various SIP Dialogs that are part of the same call.
	 * By default this value would be populated by the container with the Application Session ID.  
	 * 
	 */
	public static final String CORRELATION_ID = "CORRELATION_ID".intern();

	/**
	 * Identifier for the DIALOG ID field.
	 * <p> 
	 * By default this value will be populated by the container with the SIP Session ID.
	 */
	public static final String SESSION_ID 	= "SESSION_ID".intern();

	/**
	 * Identifier for the ORIGINATING NUMBER field. 
	 * <p>
	 * By default this value will be populated with the value of request.from.uri.user attribute.
	 */
	public static final String ORIGINATING_NUMBER 	= "ORIGINATING_NUMBER".intern();

	/**
	 * Identifier for the TERMINATING_NUMBER field.
	 * <p>
	 * By default this value will be populated with the value of request.to.uri.user attribute.
	 */
	public static final String TERMINATING_NUMBER	= "TERMINATING_NUMBER".intern();
	
	/**
	 * Identifier for the CALL_START_TIMESTAMP field.
	 * <p> 
	 * By default this value will be populated with the creation time of the SipSession.
	 */
	public static final String CALL_START_TIMESTAMP	= "START_TIMESTAMP".intern();
	
	/**
	 * Identifier for the CALL_END_TIMESTAMP field.
	 * <p> 
	 * If not set, this field will be set to the Session invalidation timestamp or the 
	 * time at which this CDR was written for the first time.
	 */
	public static final String CALL_END_TIMESTAMP	= "CALL_END_TIMESTAMP";

	/**
	 * The number of the party to bill the call to.  By default, this is the
	 * calling party.
	 */
	public static final String BILL_TO_NUMBER = "BILL_TO_NUMBER";

	/**
	 * Identifier for the CALL_DURATION_MSECS field.
	 * <p>
	 * If not set, this value will be set to CALL_END_TIMESTAMP - CALL_START_TIMESTAMP.
	 */
	public static final String CALL_DURATION_MSECS	= "CALL_DURATION_MSECS".intern();

	/**
	 * The status code of the call.  Default value is the response code for the 
	 * initial INVITE of the call dialog.
	 */
	public static final String CALL_COMPLETION_STATUS_CODE = "CALL_COMPLETION_STATUS_CODE";

	/**
	 * Identifier for the CUSTOM1 field.
	 */
	public static final String CUSTOM1	= "CUSTOM1".intern();

	/**
	 * Identifier for the CUSTOM2 field.
	 */	
	public static final String CUSTOM2 = "CUSTOM2".intern();

	/**
	 * Identifier for the CUSTOM3 field.
	 */	
	public static final String CUSTOM3	= "CUSTOM3".intern();

	/**
	 * Identifier for the CUSTOM4 field.
	 */	
	public static final String CUSTOM4	= "CUSTOM4".intern();

	/**
	 * Identifier for the CUSTOM5 field.
	 */	
	public static final String CUSTOM5	= "CUSTOM5".intern();
	
	/**
	 * Identifier for the CDR written by SAS in case service fails to write the CDR.
	 */
	public static final String DEFAULT_CDR = "DEFAULT_CDR".intern();
	
	/**
	 * Gets the current value of the field specified.
	 * Returns NULL if the field is not set yet or if it is an invalid field identifier.
	 * @param field	- The specified field 
	 */
	public Object get(String field);
	
	/**
	 * Sets the specified value as the current value for the field specified.
	 * If the application using this CDR is declared as "distributable" but
	 * the field value specified is non-serializable, then an 
	 * IllegalArgumentException will be thrown.
	 *
	 * @param field - The specified field
	 * @param value - Value for the specified field.
	 * @throws IllegalArgumentException if the value is not implementing the serializable interface 
	 * but the application is a distributable application.  
	 */
	public void set(String field, Object value);
	
	/**
	 * Writes this CDR as specified in the CDR Configuration.
	 * 
	 * @throws CDRWriteFailedException if the write operation fails.
	 */
	public void write() throws CDRWriteFailedException;
	
	/**
	 * Returns the number of times this CDR object is written to the CDR File.
	 * @return Number of times this CDR is written to the file. 0 if this CDR is not written to the file. 
	 */
	public int getWriteCount();
	
	/**
	 * Returns an iterator of all the fields specified in the CDR.
	 * @return Iterator of all the fields.
	 */
	public Iterator getFields();
	
	//sumit@sbtm new method for CDR writing [
	/**
	 * Writes CDRs passed in string array.
	 * 
	 * @throws CDRWriteFailedException if the write operation fails.
	 * 
	 * @author Sumit
	 */
	public void write(String[] cdr) throws CDRWriteFailedException;
	//]sumit@sbtm new method for CDR writing
}
