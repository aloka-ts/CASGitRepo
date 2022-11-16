/*
 * @(#)AseAddressImpl.java       1.0 2004/08/10
 *
 */

package com.baypackets.ase.sipconnector;

import java.util.LinkedList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.ListIterator;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map.Entry;
import java.io.Serializable;

import javax.servlet.sip.URI;
import javax.servlet.sip.Address;
import javax.servlet.sip.ServletParseException;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;

import com.baypackets.ase.util.AseStrings;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipConstants;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipHeader;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipNameAddress;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipToFromHeader;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipContactHeader;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipNameAddressHeader;
import com.dynamicsoft.DsLibs.DsSipObject.DsURI;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipURL;
import com.dynamicsoft.DsLibs.DsSipObject.DsTelURL;
import com.dynamicsoft.DsLibs.DsUtil.DsException;
import com.dynamicsoft.DsLibs.DsSipParser.DsSipParserException;
import com.dynamicsoft.DsLibs.DsSipObject.DsByteString;
import com.dynamicsoft.DsLibs.DsSipObject.DsParameters;
import com.dynamicsoft.DsLibs.DsSipObject.DsParameters;
import com.dynamicsoft.DsLibs.DsSipObject.DsParameter;
import com.dynamicsoft.DsLibs.DsSipObject.DsHeaderParameters;
import com.dynamicsoft.DsLibs.DsSipObject.DsHeaderParameter;

/**
 * Class AseAddressImpl implements the interface javax.servlet.sip.SipURI.
 *
 * @version 	1.0 10 Aug 2004 
 * @author 	BayPackets Inc.
 *
 */

public class AseAddressImpl implements Address, Cloneable, Serializable { 

	private static final long serialVersionUID = -38403374888251L;
	
	/**
	 * This class is a wrapper on the DS Stack class 
	 * com.dynamicsoft.DsLibs.DsSipObject.DsSipNameAddressHeader
	 * All the methods in this class delegate to corresponding 
	 * DsSipNameAddressHeader methods.
	 *
	 */

	/** Reference to the DsSipNameAddressHeader object. */
	private DsSipNameAddressHeader m_dsNAHeader;

	/** Whether this object is immutable. */
	private boolean m_bImmutable;

	/** The contained AseURIImpl object. */
	transient private AseURIImpl m_Uri;

	/** Display name after removing the DQOUT \" */
	transient private String m_displayName;

	/** Iterator for SIP URI parameter names. */
	transient private java.util.Iterator m_parameterIterator;

	/** Map for SIP URI parameter values. */
	transient private Map<String,String> m_paramValuesMap;

	private String m_HeaderName = null;

	/** Logger element */
	transient private static Logger logger = Logger.getLogger(AseAddressImpl.class.getName());

	/**
	 * Creates a new AseAddressImpl object from a valid DsSipNameAddressHeader object.
	 *
	 * @param dsNAHeader	A valid DsSipNameAddressHeader object
	 * @param isImmutable whether this object should be immutable
	 *
	 * @throws IllegalArgumentException	if dsNAHeader is null.
	 *
	 */
	public AseAddressImpl( DsSipNameAddressHeader dsNAHeader, 
			boolean isImmutable,String name) {

		if( dsNAHeader == null){
			logger.error( AseSipConstants.STR_ERR_NULL_PARAM);
			throw new IllegalArgumentException();
		}
		m_dsNAHeader = dsNAHeader;
		m_bImmutable = isImmutable;
		m_HeaderName=name;
	}

	/**
	 * Creates a new AseAddressImpl object from a valid DsSipNameAddressHeader object.
	 *
	 * @param dsNAHeader	A valid DsSipNameAddressHeader object
	 *
	 * @throws IllegalArgumentException	if dsNAHeader is null.
	 *
	 */
	public AseAddressImpl( DsSipNameAddressHeader dsNAHeader) {

		if( dsNAHeader == null){
			logger.error( AseSipConstants.STR_ERR_NULL_PARAM);
			throw new IllegalArgumentException();
		}
		m_dsNAHeader = dsNAHeader;
		m_bImmutable = false;
	}

	/**
	 * Creates a new AseAddressImpl object from a valid sip URL string.
	 *
	 * @param naHeader	A valid sip URL String object.
	 *
	 * @throws ServletParseException	if naHeader is not a valid sip URL.
	 *
	 */
	 public AseAddressImpl( String sipAddress) 
		 throws ServletParseException {

			 try{
				 m_dsNAHeader = (DsSipNameAddressHeader)
					 DsSipHeader.createHeader( DsSipHeader.CONTACT, 
							 sipAddress.getBytes() );
				 m_bImmutable = false;

			 } catch( DsException dsPEx){
				 logger.error("createAddress( String): ", dsPEx);

				 throw new ServletParseException( dsPEx.getMessage() );
			 }
			 
		 }

	DsSipNameAddressHeader getDsNameAddressHeader(){
		/**
		 * This method doesn't care about immutability as it 
		 *	should be visible only inside SipConnector package.
		 */

		return m_dsNAHeader;
	}

	public void setImmutable(){

		m_bImmutable = true;

		if( m_Uri != null){
			m_Uri.setImmutable();
		}
	}

	/**
	 * logs a warning message if any mutator method is called on an Immutable object.
	 *
	 */
	protected void warnImmutable(){

		if(logger.isEnabledFor(Level.WARN)) {
			logger.warn(AseSipConstants.STR_WARN_IMMUTABLE + this.toString());
		}
	}


	/**
	 * Returns the display name of this Address. 
	 *	This is typically a caller or callees real name 
	 *	and may be rendered by a user agent, for example when alerting.
	 *
	 * @return display name of this Address, or null if one doesn't exist
	 */
	public String getDisplayName(){

		if(null == m_displayName) {
			DsByteString bStr = m_dsNAHeader.getNameAddress().getDisplayName();
			if( null != bStr) {
				m_displayName = bStr.unquoted().toString();
			}
		}

		return m_displayName;
	}

	/**
	 * Sets the URI of this Address.
	 *
	 * @param uri new URI of this Address
	 * @throws IllegalStateException if this Address is used in a context where it cannot be modified
	 */	
	public void setURI(URI uri){
		if( m_bImmutable){
			warnImmutable();
			throw new IllegalStateException();
		}

		m_dsNAHeader.getNameAddress().setURI( 
				( (AseURIImpl) uri).getDsURI() );

		// Invalidate the local cache if any...
		m_Uri = null;
	}

	/**
	 * @param name - the name of the parameter
	 * @return the value of the specified parameter
	 */

	public String getParameter( String name){
        DsByteString bStr = null;

		if(name.equals(AseStrings.PARAM_TAG)) {
			if(m_dsNAHeader instanceof DsSipToFromHeader) {
				// "tag" parameter for From/To header needs to be handled
				// specially
				if(((DsSipToFromHeader)m_dsNAHeader).isTagPresent()) {
					return ((DsSipToFromHeader)m_dsNAHeader).getTag().toString();
				}
			}
		}
		
		//BugID:5103 Contact Header JSR289
		float param_q = 0.0F;
		if(name.equalsIgnoreCase(AseStrings.PARAM_Q)){
		   param_q = getQ();
		   String q = new Float(param_q).toString();
		   return q;
		}
     
		int param_exp = 0 ;
		if(name.equalsIgnoreCase(AseStrings.PARAM_EXPIRES)){
		   param_exp = getExpires();
		   String exp = new Integer(param_exp).toString();
		   return exp;
		}

        if(null != name){
            bStr = m_dsNAHeader.getParameter(name);
        }else{
            logger.error("getParameter(): name is null");
            return null;
        }

		if( null!= bStr){
			return bStr.toString();
		} else {
			return null;
		}
	}

	/**
	 *
	 * Sets the display name of this Address.
	 * @param name display name
	 * @throws IllegalStateException if this Address is used in a context where it cannot be modified
	 *
	 */
	public void setDisplayName(String name){
		if( m_bImmutable){
			warnImmutable();
			throw new IllegalStateException();
		}

		if(null != name) {
			m_dsNAHeader.getNameAddress().setDisplayName( 
				new DsByteString( name) );
		}
		else {
			if(true == m_dsNAHeader.getNameAddress().hasDisplayName()) {
				m_dsNAHeader.getNameAddress().removeDisplayName();
			}
		}
		m_displayName = null;
	}

	/**
	 * Returns the URI component of this Address. 
	 *	This method will return null for wildcard addresses 
	 *	(see isWildcard(). For non-wildcard addresses the result 
	 *	will always be non-null.
	 * @return the URI of this Address
	 */
	public URI getURI(){

		// Try the cached object if available
		if( null== m_Uri){
            DsURI uri = m_dsNAHeader.getNameAddress().getURI();
            if (uri.isSipURL()) {
				   m_Uri = new AseSipURIImpl((DsSipURL)uri,m_bImmutable); 
				   //BugID:5103 Contact Header JSR289
				   m_Uri.setAddressHeaderName(m_HeaderName);
				  
            }
            else {
				   m_Uri = new AseTelURLImpl((DsTelURL)uri,m_bImmutable); 
				   //BugID:5103 Contact Header JSR289
				   m_Uri.setAddressHeaderName(m_HeaderName);
				  
            }
		}

		return m_Uri;
	}

	/**
	 *
	 * Sets the value of the specified parameter. A zero-length String indicates flag parameter.
	 * @param name parameter name
	 * @param value parameter value
	 * @throws IllegalStateException if this Address is used in a context where it cannot be modified
	 *
	 */
	public void setParameter(DsByteString name, DsByteString value){
		if( m_bImmutable){
			warnImmutable();
			throw new IllegalStateException();
		}
             //somesh
      if(name.toString(name).equals(AseStrings.PARAM_TAG))
         {
            if(m_dsNAHeader instanceof DsSipToFromHeader)
              ((DsSipToFromHeader)m_dsNAHeader).setTag(name);
         }
      
     
		// Invalidate the parameter name list iterator..
		m_parameterIterator = null;
		m_paramValuesMap = null;

		m_dsNAHeader.setParameter( name, value );

	}


	/**
	 *
	 * Sets the value of the specified parameter. A zero-length String indicates flag parameter.
	 * @param name parameter name
	 * @param value parameter value
	 * @throws IllegalStateException if this Address is used in a context where it cannot be modified
	 *
	 */
	public void setParameter(String name, String value){
		if( m_bImmutable){
			warnImmutable();
			throw new IllegalStateException();
		}
 
                     //somesh
      if(name.equals(AseStrings.PARAM_TAG))
         {
            if(m_dsNAHeader instanceof DsSipToFromHeader)
              ((DsSipToFromHeader)m_dsNAHeader).setTag(new DsByteString( name));
         }

      //BugID:5103 Contact Header JSR289
       if(name.equals(AseStrings.PARAM_Q)){
    	  float q_param = new Float(value);
    	  setQ(q_param);
    	  return;
    	  }
    	
      if(name.equals(AseStrings.PARAM_EXPIRES)){
    	  int exp_param = Integer.parseInt(value);
    	  setExpires(exp_param);
    	  return;
      }
      
		// Invalidate the parameter name list iterator..
		m_parameterIterator = null;
		m_paramValuesMap = null;

        if(null == name || null == value){
            throw new IllegalArgumentException();
        }

        m_dsNAHeader.setParameter( 
                new DsByteString( name),
                new DsByteString( value) );
	}

	/**
	 *
	 * Removes the parameter with the specified name.
	 * @param name - parameter name
	 * @throws IllegalStateException - if this Address is used in a context where it cannot be modified
	 *
	 */
	void removeParameter(DsByteString name){
		if( m_bImmutable){
			warnImmutable();
			throw new IllegalStateException();
		}
             //somesh
           if(name.toString(name).equals(AseStrings.PARAM_TAG))
         {
            if(m_dsNAHeader instanceof DsSipToFromHeader)   
              ((DsSipToFromHeader)m_dsNAHeader).removeTag();  
         }    
		// Invalidate the parameter name list iterator..
		m_parameterIterator = null;
		m_paramValuesMap = null;
		m_dsNAHeader.removeParameter( name);

	}


	/**
	 *
	 * Removes the parameter with the specified name.
	 * @param name - parameter name
	 * @throws IllegalStateException - if this Address is used in a context where it cannot be modified
	 *
	 */
	public void removeParameter(String name){
		if( m_bImmutable){
			warnImmutable();
			throw new IllegalStateException();
		}
                  //somesh
           if(name.equals(AseStrings.PARAM_TAG))
         {
            if(m_dsNAHeader instanceof DsSipToFromHeader)
              ((DsSipToFromHeader)m_dsNAHeader).removeTag();
         }

         //BugID:5103 Contact Header JSR289
           if(name.equalsIgnoreCase(AseStrings.PARAM_Q)){
        		   ((DsSipContactHeader)m_dsNAHeader).removeQValue();
        		    return;
           }
        		   
           if(name.equalsIgnoreCase(AseStrings.PARAM_EXPIRES)){
        		   ((DsSipContactHeader)m_dsNAHeader).removeExpires();
           			return;
           }
           
           
		// Invalidate the parameter name list iterator..
		m_parameterIterator = null;
		m_paramValuesMap = null;
        //bug# BPInd09232
        if(null == name) {
            throw new IllegalArgumentException();
        }
        m_dsNAHeader.removeParameter( 
                new DsByteString( name) );


	}

	/**
	 *
	 * Returns an Iterator over the set of all parameters of this address.  
	 * @return an Iterator over the set of String objects that are the names of parameters of this Address
	 *
	 */
	public java.util.Iterator getParameterNames() {

		boolean hasTag = false;
		if(m_dsNAHeader instanceof DsSipToFromHeader) {
			// "tag" parameter for From/To header needs to be handled
			// specially
			hasTag = ((DsSipToFromHeader)m_dsNAHeader).isTagPresent();
		}

		/**
		 * Try the cached iterator first, if available.
		 * If NOT then generate a new one and cache the same.
		 */

		LinkedList list = null;

		if (m_parameterIterator != null){
			return m_parameterIterator;
		}

		DsParameters dsParams = m_dsNAHeader.getParameters();

		if(null == dsParams){
			if(hasTag == true) {
				list = new LinkedList();
				list.add(new String("tag"));
				m_parameterIterator = list.listIterator();
			}
			return m_parameterIterator;
		}

		ListIterator listIter = dsParams.listIterator(0);
		if(list == null) {
			list = new LinkedList();
		}
		DsParameter dsParam = null;
		while( listIter.hasNext() ){
			dsParam = (DsParameter) listIter.next();
			if( null == dsParam)
				continue;

			list.add( dsParam.getKey().toString() );
		}

		m_parameterIterator = list.listIterator();

		return m_parameterIterator;
	}

	/**
	 *
	 * Returns true if this Address represents the "wildcard" contact address. This is the case if it represents a Contact header whose string value is "*". Likewise, SipFactory.createAddress("*") always returns a wildcard Address instance.
	 * @return true if this Address represents the "wildcard" contact address, and false otherwise
	 *
	 */
	public boolean isWildcard(){

		if( !( m_dsNAHeader instanceof DsSipContactHeader) ) 
			return false;

		return ( (DsSipContactHeader)m_dsNAHeader).isWildCard();
	}

	/**
	 *
	 * Returns the value of the "q" parameter of this Address. The "qvalue" indicates the relative preference amongst a set of locations. "qvalue" values are decimal numbers from 0 to 1, with higher values indicating higher preference.
	 * @return this Address' qvalue or -1.0 if this is not set
	 *
	 */ 
	public float getQ(){

		if( !( m_dsNAHeader instanceof DsSipContactHeader) ) 
			return (float)-1.0;

		return ( (DsSipContactHeader)m_dsNAHeader).getQvalue();
	}

	/**
	 *
	 * Sets this Addresss qvalue.
	 * @param q new qvalue for this Address or -1 to remove the qvalue
	 * @throws IllegalArgumentException if the new qvalue isn't between 0.0 and 1.0 (inclusive) and isn't -1.0.
	 *
	 */
	public void setQ(float q){

		if( m_bImmutable){
			warnImmutable();
			throw new IllegalStateException();
		}
		
		if( !( m_dsNAHeader instanceof DsSipContactHeader) ) 
			return;

		// validation for illegal q value
		if(0.0 <= q && q <= 1.0 ) {
			// Invalidate the parameter name list iterator..
			m_parameterIterator = null;
			m_paramValuesMap = null;

			((DsSipContactHeader)m_dsNAHeader).setQvalue(q);
		} else if( q == -1.0 ) {
			// Invalidate the parameter name list iterator..
			m_parameterIterator = null;
			m_paramValuesMap = null;

			((DsSipContactHeader)m_dsNAHeader).removeQValue();
		} else {
			throw new IllegalArgumentException();
		}
	}

	/**
	 *
	 * Returns the value of the "expires" parameter as delta-seconds.
	 * @return value of "expires" parameter measured in delta-seconds, or -1 if the parameter does not exist
	 *
	 */
	public int getExpires(){
		
		int expires = -1;
		if(m_dsNAHeader instanceof DsSipContactHeader){
			expires =  (int) ((DsSipContactHeader)m_dsNAHeader).getExpires();
		}
		return expires;
	}

	/**
	 *
	 * Sets the value of the "expires" parameter.
	 * @param seconds new relative value of the "expires" parameter. A negative value causes the "expires" parameter to be removed.
	 *
	 */
	public void setExpires(int seconds){
		if( m_bImmutable){
			warnImmutable();
			throw new IllegalStateException();
		}
		
		// BPInd10467
        // if specified input in -ve, remove the expire from address	
		if (seconds >= 0) {	
			if(m_dsNAHeader instanceof DsSipContactHeader){
				((DsSipContactHeader)m_dsNAHeader).setExpires(seconds);
			}
		}
		else {
			if(m_dsNAHeader instanceof DsSipContactHeader){
				((DsSipContactHeader)m_dsNAHeader).removeExpires();
			}
		}
	}

	/**
	 * Removes all the Header parameters from the underlying NameAddressHeader.
	 *
	 */
	void removeParameters(){

		if( m_bImmutable){
			warnImmutable();
			throw new IllegalStateException();
		}

		// Invalidate the parameter name list iterator..
		m_parameterIterator = null;
		m_paramValuesMap = null;
        	m_dsNAHeader.removeParameters();
	}

	/**
	 *
	 * Returns the value of this address as a String. The resulting string must be a valid value of a SIP From or To header.
	 * @overrides toString in class Object
	 * @return value of this Address as a String
	 */
	public String toString(){
		if (this.isWildcard())
			return "*";
		String value = null;
		DsSipNameAddress nameAddr=m_dsNAHeader.getNameAddress();
		if ( nameAddr!= null) {
			value = nameAddr.toString();
			Set<Entry<String, String>> retval = getParameters();
			if (retval == null) {
				return value;
			}
			Iterator itr = retval.iterator();
			while (itr.hasNext()) {
				value = value + ";" + itr.next();
			}
		}
		return value;
	}

	/** 
	 *
	 * Returns a clone of this Address. The cloned Address has identical display name, URI, and parameters, except that it has no tag parameter. This means the cloned address can be used as an argument to SipFactory.createRequest.
	 * @overrides clone in class Object
	 * @return a clone of this Address
	 */
	public Object clone(){

		return new AseAddressImpl( 
				(DsSipNameAddressHeader)m_dsNAHeader.clone());
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
			 public String getDisplayName()
			 public String getParameter( String name)
			 public URI getURI()
			 public void removeParameter(DsByteString name)
			 public void removeParameter(String name)
			 public java.util.Iterator getParameterNames() 
			 public boolean isWildcard()
			 public float getQ()
			 public int getExpires()
			 public String toString()
			 public Object clone()

			 public void setURI(URI uri)
			 public void setDisplayName(String name)
			 public void setParameter(DsByteString name, DsByteString value)
			 public void setParameter(String name, String value)
			 public void setQ(float q)
			 public void setExpires(int seconds)

			 public void setImmutable()                                     

			 public void setURI(URI uri)

			 *
			 */

			if(args.length >3){
				logResults("\tArguments: " + args[0] + " , " + args[1] +" , " + args[2] +" , " + args[3] );
				logResults("");
			}else{
				return;
			}

			// Insert single white space where find "&WS;"
			String addrStr = args[0].replaceAll("&WS;"," ");

			AseAddressImpl address = new AseAddressImpl(addrStr);

			logResults("\t[ Method ]\t\t[ Return Value ]");

			logResults("	String getDisplayName() : " + address.getDisplayName() );
			logResults("	String getParameter( String name) : " + address.getParameter(args[1]) );
			logResults("	URI getURI() : " + address.getURI() );
			logResults("	java.util.Iterator getParameterNames()  : " + 
					printIterator(address.getParameterNames() ) );
			logResults("	boolean isWildcard() : " + address.isWildcard() );
			logResults("	float getQ() : " + address.getQ() );
			logResults("	int getExpires() : " + address.getExpires() );
			logResults("	String toString() : " + address.toString() );
			logResults("	Object clone() : " + address.clone() );

			address.removeParameter(AseStrings.PARAM_TAG);
			address.setURI( new AseURIImpl( args[3] ) );
			address.setDisplayName(args[1]);
			address.setParameter(args[1], args[2] );
			address.setQ( (float)0.3 );
			address.setExpires( 300 );

			logResults("	String toString() : " + address.toString() );

			address.setImmutable();

			try{ 
				address.setExpires( 300 );
			} catch (Exception ex){
				logger.error(ex.getMessage(), ex);
			}

			try{ 
				( (AseSipURIImpl) address.getURI() ).setUser(args[1] );
			} catch (Exception ex){
				logger.error(ex.getMessage(), ex);
			}

			logResults("	String toString() : " + address.toString() );

		}// main()

	protected static void logResults( String line){
		logger.info(line);
	}

	protected static String printIterator(java.util.Iterator iter){

		if( null == iter)
			return null;

		StringBuffer strBuf = new StringBuffer("Iterator: [");

		while (iter.hasNext() ){
			strBuf.append( iter.next() ).append(AseStrings.COMMA);
		}

		strBuf.append(AseStrings.SQUARE_BRACKET_CLOSE);

		return strBuf.toString();
	}

	/**
	 *
	 * Returns a map over the set of all parameters name-value of this address.  
	 * @return a map over the set of <String,String> objects that are the name-value 
	 * pair of all the parameters of this Address.
	 *
	 */
	public Set<Entry<String, String>> getParameters() {

		boolean hasTag = false;
		if(m_dsNAHeader instanceof DsSipToFromHeader) {
			// "tag" parameter for From/To header needs to be handled
			// specially
			hasTag = ((DsSipToFromHeader)m_dsNAHeader).isTagPresent();
		}

		/**
		 * Try the cached iterator first, if available.
		 * If NOT then generate a new one and cache the same.
		 */

		Map<String,String> retval = new HashMap<String,String> ();

		if (m_paramValuesMap != null){
			return m_paramValuesMap.entrySet();
		}

		DsParameters dsParams = m_dsNAHeader.getParameters();
		if(null == dsParams){
			return null;
		}

//		if(null == dsParams){
//			if(hasTag == true) {
//				list = new LinkedList();
//				list.add(new String("tag"));
//				m_parameterIterator = list.listIterator();
//			}
//			return m_parameterIterator;
//		}

		ListIterator listIter = dsParams.listIterator(0);
//		if(list == null) {
//			list = new LinkedList();
//		}
		DsParameter dsParam = null;
		while( listIter.hasNext() ){
			dsParam = (DsParameter) listIter.next();
			if( null == dsParam)
				continue;

			retval.put(dsParam.getKey().toString(), dsParam.getValue().toString());
		}

		m_paramValuesMap = retval;

		return m_paramValuesMap.entrySet();
	}

	public String getValue() {
		return toString();
	}

	public void setValue(String sipAddress) {
		if(sipAddress==null){
			throw new NullPointerException("header field can not be null");
		}
		if( m_bImmutable){
			warnImmutable();
			throw new IllegalStateException();
		}
		try{
		// saving all the parameters.
//		Map<String,String> oldParam = new HashMap<String,String> ();
//		oldParam=(Map<String, String>) getParameters();
		Set<Entry<String, String>> oldParam = getParameters();;
		m_dsNAHeader = (DsSipNameAddressHeader)DsSipHeader.createHeader(DsSipHeader.CONTACT,
				sipAddress.getBytes() );
		m_bImmutable = false;
		if(oldParam ==null){
			return;
		}
		//for(Entry<String, String> nameValue : oldParam.entrySet()) {
		for(Entry<String, String> nameValue : oldParam) {
			setParameter(nameValue.getKey(), nameValue.getValue());
		}
	
		} catch( DsException dsPEx){
			logger.error("setValue(String): ", dsPEx);
			throw new IllegalStateException(dsPEx.getMessage());
		}
	}

}

