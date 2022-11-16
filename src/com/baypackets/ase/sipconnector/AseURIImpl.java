/*
 * @(#)AseURIImpl.java        1.0 2004/08/10
 *
 */

package com.baypackets.ase.sipconnector;

import java.io.Serializable;
import java.util.Iterator;

import javax.servlet.sip.URI;
import javax.servlet.sip.ServletParseException;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;

import com.baypackets.ase.util.AseStrings;
import com.dynamicsoft.DsLibs.DsSipObject.DsURI;
import com.dynamicsoft.DsLibs.DsSipObject.DsTelURL;
import com.dynamicsoft.DsLibs.DsSipParser.DsSipParserException;
import com.dynamicsoft.DsLibs.DsSipObject.DsByteString;

/**
 * Class AseURIImpl implements the interface javax.servlet.sip.URI
 *
 * @version 1.0 10 Aug 2004 
 * @author 	Vimal Mishra
 *
 */

public class AseURIImpl implements URI, Cloneable, Serializable{

	/**
	 * This class is a wrapper on the DS Stack class 
	 * com.dynamicsoft.DsLibs.DsSipObject.DsURI. 
	 * All the methods in this class delegate to corresponding DsURI 
	 * methods.
	 *
	 */

	/** These final String objects shall be used while throwing exceptions. */
	public final static String STR_INVALID_SIP_URL = "Not a valid sip URL : ";
	public final static String STR_INVALID_TEL_URL = "Not a valid tel URL : ";

	/** Represents "phone" string of 'user=phone' parameter. */
	public final static String STR_PHONE = "phone";

	/** Represents "ip" string of 'user=ip' parameter. */
	public final static String STR_IP = "ip";

	/** Reference to the DsURI object. */
	protected DsURI m_dsUri;

	/** Whether this object is immutable. */
	protected boolean m_bImmutable;

	/** String representation of this object, once it has been made Immutable. */
	//protected String m_strForm;

	/** Logger element */
	private static Logger logger = Logger.getLogger(AseURIImpl.class.getName());
	
	// BugID:5103 Contact Header JSR289
	public String addressHeaderName = null; 

	public void setAddressHeaderName(String name) {
		
		 addressHeaderName = name;
	}

	/**
	 * Creates a new AseURIImpl object from a valid DsURI object.
	 *
	 * @param dsURI	A valid DsURI object
	 *
	 * @throws IllegalArgumentException	if dsURI is null.
	 *
	 */
	public AseURIImpl( DsURI dsURI) {
		if( null == dsURI){
			logger.error( AseSipConstants.STR_ERR_NULL_PARAM);
			throw new IllegalArgumentException();
		}

		m_dsUri = dsURI;
		m_bImmutable = false;
	}

	

	/**
	 * Creates a new AseURIImpl object from a valid DsURI object.
	 *
	 * @param dsURI	A valid DsURI object
	 * @param bImmutable	True if the new object should be immutable
	 *
	 * @throws IllegalArgumentException	if dsURI is null.
	 *
	 */
	public AseURIImpl( DsURI dsURI, boolean bImmutable) {
		if( null == dsURI){
			logger.error( AseSipConstants.STR_ERR_NULL_PARAM);
			throw new IllegalArgumentException();
		}

		m_dsUri = dsURI;
		m_bImmutable = bImmutable;

		/*if( bImmutable){
			m_strForm = m_dsUri.toString();
		}*/
	}

	/**
	 * Creates a new AseURIImpl object from a valid URL string.
	 *
	 * @param url	A valid URL String object.
	 *
	 * @throws ServletParseException	if url is not a valid URL.
	 *
	 */
	public AseURIImpl( String url) throws ServletParseException {

		if( null == url){
			logger.error( AseSipConstants.STR_ERR_NULL_PARAM);
			throw new IllegalArgumentException();
		}

		try {
			m_dsUri = DsURI.constructFrom(url);
		} catch( DsSipParserException dsspe){
			logger.error("Not a valid URI.", dsspe);
			throw new ServletParseException( dsspe.getMessage() );
		}

	}
	
	/**
	 * To be used by Derived classes only..
	 */
	protected AseURIImpl( ) {
		m_dsUri = null;
		m_bImmutable = false;
	}


	public void setImmutable(){

		//m_strForm = m_dsUri.toString();

		if(logger.isDebugEnabled() ){
			logger.debug("Made Immutable: ");// + m_strForm);
		}

		m_bImmutable = true;
	}
	
	public boolean isImmutable(){
		return this.m_bImmutable;
	}

	/**
	 * logs a warning message if any mutator method is called on an Immutable object.
	 *
	 */
	protected void warnImmutable(){

		/*if(logger.isEnabledFor(Level.WARN) ){
			logger.warn(
					AseSipConstants.STR_WARN_IMMUTABLE + m_strForm);
		}*/
	}

	/**
	 * Returns true if the actual URI associated with this object is a 
	 * "sip" or "sips" URI.
	 *
	 */
	public boolean isSipURI(){
		return m_dsUri.isSipURL();
	}

	DsURI getDsURI() {
		return m_dsUri;
	}

	public java.lang.String getScheme(){
		return m_dsUri.getScheme().toString();
	}

	public URI clone(){
			return new AseURIImpl( (DsURI)m_dsUri.clone() );
	}

	public java.lang.String toString(){
		/*if( null!= m_strForm)
			return m_strForm;
		else*/
			return m_dsUri.toString();
	}

	/**
	 * Used for Unit Testing only..
	 */
	
	public static void main(String[] args) 
		throws Exception {
		
		/**
		 * The idea is to operate on the passed command line arguments.
		 * Each argument shall represent one particular Unit test case.
		 * Following methods shall be tested...
		 *
		 public AseURIImpl( DsURI dsURI) 
		 public AseURIImpl( DsURI dsURI, boolean bImmutable) 
		 public AseURIImpl( String url) throws ServletParseException 
		 public void setImmutable()
		 public boolean isSipURI()
		 public java.lang.String getScheme()
		 public java.lang.Object clone()
		 public java.lang.String toString()

		 *
		 */

		String strArg1 = null;

		if(args.length >0){
			strArg1 = args[0];
		}

		AseURIImpl uri = new AseURIImpl( strArg1);

		logResults("\t[ Method ]\t\t[ Return Value ]");
		logResults("\tboolean isSipURI() : " + uri.isSipURI());
		logResults("\tString getScheme() : " + uri.getScheme() );
		logResults("\tObject clone() : " + uri.clone().toString() );
		logResults("\tString toString() : " + uri );

	}// main()

	protected static void logResults( String line){
		if(logger.isInfoEnabled()) logger.info(line);
	}

	protected static String printIterator(java.util.Iterator iter){

		if( null == iter)
			return null;

		StringBuffer strBuf = new StringBuffer("Iterator: [");

		while (iter.hasNext() ){
			strBuf.append( iter.next() ).append(AseStrings.CHAR_COMMA);
		}

		strBuf.append(AseStrings.SQUARE_BRACKET_CHAR_CLOSE);

		return strBuf.toString();
	}
	
	// BugID  6859
	
	/*
	 *Compares the given URI with this URI.
	 */
	
	public boolean equals(Object uri){

		if(uri == null || m_dsUri == null){
			return false;
		}

		if(!(uri instanceof AseURIImpl) ){
			return false;
		}

		return m_dsUri.equals(((AseURIImpl)uri).m_dsUri);
	}


	/*
	 * The hashCode() needs to be overrided otherwise it will call 
	 * hashCode() implementation of the Object class which will always
	 * return unequal match for different objects.
	 */
	
	public int hashCode() {

		if(m_dsUri == null){
			return -1;
		}

		return m_dsUri.toString().hashCode();
	}

	//
	
	public String getParameter(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}


	public Iterator<String> getParameterNames() {
		// TODO Auto-generated method stub
		return null;
	}


	public void removeParameter(String arg0) {
		// TODO Auto-generated method stub
		
	}


	public void setParameter(String arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

}// class AseURIImpl

