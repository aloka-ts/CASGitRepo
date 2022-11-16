/*
 * SimpleCDRFormat.java
 *
 * Created on Jun 20, 2005
 */
package com.baypackets.ase.cdr;

import java.util.Iterator;

import com.baypackets.ase.sbb.CDR;
import java.util.ArrayList;
import java.util.Collection;
import java.util.StringTokenizer;
import org.apache.log4j.Logger;

/**
 * This implementation of the CDRFormat interface compiles CDR format strings
 * of the following form:
 * <p>
 * <pre>
 *  delim:[delim string]:[field 1][delim string][field 2][delim string]...[field n]
 * </pre>
 * </p>
 * Example:
 * <p>
 * <pre>
 * <code>
 *  // Populate the following fields in a CDR object...
 *  CDR cdr = ...;
 *  cdr.set(CDR.CORRELATION_ID, "1");
 *  cdr.set(CDR.SESSION_ID, "2");
 *  cdr.set("CUSTOM2", "3");
 *
 *  // Create a formatter for the CDR that will return a comma delimitted
 *  // list of the following CDR fields: CORRELATION_ID, SESSION_ID, 
 *  // CUSTOM1 and CUSTOM2.
 *  CDRFormat formatter = CDRFormatFactory.getInstance().createCDRFormat();
 *  formatter.compile("delim=,:CORRELATION_ID,SESSION_ID,CUSTOM1,CUSTOM2");
 *
 *  String cdrString = formatter.format(cdr);
 *
 *  // "cdrString" will look like the following: 1,2,,3
 *  // Since no CUSTOM1 field value was set in the CDR, an empty string 
 *  // is substituted in it's place.
 * </code>
 * </pre>
 * </p>
 * The default behavior if no format string was specified is to return a 
 * comma separated list of all fields set in the CDR.
 */
public class SimpleCDRFormat implements CDRFormat {

		private static Logger _logger = Logger.getLogger(SimpleCDRFormat.class);
        private Collection fieldNames;
        private String delimiter = ",";  // default delimiter value
    
	/**
	 * The Default Constructor. 
	 */
	public SimpleCDRFormat(){
	}
	
	/**
	 * Creates a CDRFormat object and compiles the format using the string 
	 * passed. 
	 */
	public SimpleCDRFormat(String format) throws CompileFormatException {
		this();
		this.compile(format);
	}
	
        
	/**
	 * This method parses the CDR format string passed and identifies the fields 
	 * to be written and their position and creates an internal 
         * data-structure that helps formatting the data quickly and 
         * efficiently.
	 * 
	 * The main purpose of this method is to avoid parsing the CDR format 
         * string each time the CDR is to be written. 
	 * 
	 * This method would be implemented as follows.
	 * <pre>
	 * 1. Parse the CDR format string and get the delimitor specified 
         * in the format.
	 * 2. Identify all the CDR fields mentioned in the format and Create a
         * list of CDR fields with their corresponding position.
	 * </pre>
         *
	 * @param format Output format for the CDR.
         * @throws CompileFormatException if an error occurs while compiling 
         * the given format string.
	 */
	public void compile(String format) throws CompileFormatException {
            if (_logger.isDebugEnabled()) {
							_logger.debug("compile(): Compiling the following format string: " + format);
						}
						
						if (format == null) {
                throw new CompileFormatException("The CDR format string cannot be null.");
            }
            
            format = format.trim();
            int index = format.indexOf("delim:");
            
            if (index != 0) {
                throw new CompileFormatException("The CDR format string must begin by specifying a field delimiter token (ex. delim:,:SESSION_ID,CORRELATION_ID )");
            }
            
            index = format.lastIndexOf(':');
            
            if(index < 6) {
            	throw new CompileFormatException("CDR format string must contain 2 colon characters (ex. delim:,:SESSION_ID,CORRELATION_ID ) ");
            }
            
            this.delimiter = format.substring(6, index);
            format = format.substring(index + 1, format.length());
            
            StringTokenizer tokens = new StringTokenizer(format, delimiter);
                
            this.fieldNames = new ArrayList(tokens.countTokens());
            
            while (tokens.hasMoreTokens()) {
                this.fieldNames.add(tokens.nextToken().trim());
            }
	}
	
        
	/**
	 * Generates the String representation of the CDR as specified in
         * the format.
	 * 
	 * This method would be implemented as follows.
	 * <pre>
	 * 1. Create a StringBuffer obejct.
	 * 2. Get the compiled CDR Fields list. If it is NULL or empty, Get the fields from CDR using cdr.getFields() method
	 * 3. For each CDR field in the list, 
	 * 		a. call the get on the CDR object.
	 * 		b. Append the delimitor if (index != 0)
	 * 		c. Append the toString() value of the CDR field value object if (value != NULL).
	 * 4. return the toString from the String buffer.
	 * </pre>
	 * 
	 * @param cdr CDR to be formatted.
	 * @return the formatterd CDR String
	 */
	public String format (CDR cdr) {
		boolean loggerEnabled = _logger.isDebugEnabled();

		if (loggerEnabled) {
			_logger.debug("format(): Formatting CDR into string...");
		}
		
            StringBuffer buffer = new StringBuffer();
            
            Iterator fieldNamesIter = null;
            
            if (this.fieldNames != null) {
                fieldNamesIter = this.fieldNames.iterator();
            } else {
                fieldNamesIter = cdr.getFields();
            }
             
            if (fieldNamesIter != null) {
                while (fieldNamesIter.hasNext()) {
                    String fieldName = (String)fieldNamesIter.next();
										
										if (loggerEnabled) {
											_logger.debug("format(): Writing the following field to CDR string: " + fieldName);
										}
                    
										Object fieldValue = cdr.get(fieldName);                    
                    buffer.append(fieldValue != null ? fieldValue.toString() : "");
                    
                    if (fieldNamesIter.hasNext()) {
                        buffer.append(this.delimiter);
                    }
                }
            }
           
					 String string = buffer.toString();

					 if (loggerEnabled) {
					 	_logger.debug("format(): Returning the following string: " + string);
					}
            return string;
	}
	
        
	/**
	 * Returns the list of CDR fields, as mentioned in the input format.
	 * @return iterator of all CDR Fields as specified in the format or
         * an empty Iterator if none were specified.
	 */
	public Iterator getCDRFields(){
            return this.fieldNames != null ? this.fieldNames.iterator() : new ArrayList(0).iterator();
	}
        
}
