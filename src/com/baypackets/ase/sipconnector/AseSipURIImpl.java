/*
 * @(#)AseSipURIImpl.java        1.0 2004/08/10
 *
 */

package com.baypackets.ase.sipconnector;

import java.util.LinkedList;
import java.util.ListIterator;
import java.io.Serializable;
import java.io.Externalizable;

import javax.servlet.sip.SipURI;
import javax.servlet.sip.ServletParseException;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;

import com.baypackets.ase.util.AseStrings;
import com.dynamicsoft.DsLibs.DsSipObject.DsURI;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipConstants;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipURL;
import com.dynamicsoft.DsLibs.DsSipParser.DsSipParserException;
import com.dynamicsoft.DsLibs.DsSipObject.DsByteString;
import com.dynamicsoft.DsLibs.DsSipObject.DsParameter;
import com.dynamicsoft.DsLibs.DsSipObject.DsParameters;
import com.dynamicsoft.DsLibs.DsSipObject.DsHeaderParameters;
import com.dynamicsoft.DsLibs.DsSipObject.DsHeaderParameter;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipTransportType;

/**
 * Class AseSipURIImpl implements the interface javax.servlet.sip.SipURI.
 *
 * @version 	1.0 10 Aug 2004 
 * @author 	Vimal Mishra
 *
 */

public class AseSipURIImpl extends AseURIImpl implements SipURI, Cloneable,
																			Serializable {

	/**
	 * This class is a wrapper on the DS Stack class 
	 * com.dynamicsoft.DsLibs.DsSipObject.DsSipURL. 
	 * All the methods in this class delegate to corresponding DsSipURL 
	 * methods.
	 *
	 */

	private static final long serialVersionUID = -30848850788251L;
	/** Reference to the DsSipURL object. */
	private DsSipURL m_dsSipUrl;
	

	/** Iterator for SIP URI parameter names. */
	transient private java.util.Iterator m_parameterIterator;

	/** Iterator for SIP URI Header names. */
	transient private java.util.Iterator m_headerIterator;

	/** Logger element */
	private static Logger logger = Logger.getLogger(AseSipURIImpl.class.getName());
	
	private boolean isRecurse = false;
	
	/**
	 * Creates a new AseSipURIImpl object from a valid DsSipURL object.
	 *
	 * @param dsSipUrl	A valid DsSipURL object
	 *
	 * @throws IllegalArgumentException	if dsSipUrl is null.
	 *
	 */
	public AseSipURIImpl( DsSipURL dsSipUrl) {
		super( dsSipUrl);
		m_dsSipUrl = dsSipUrl;
	}

	/**
	 * Creates a new AseSipURIImpl object from a valid DsSipURL object.
	 *
	 * @param dsSipUrl	A valid DsSipURL object
	 * @param bImmutable	True if the new object should be immutable
	 *
	 * @throws IllegalArgumentException	if dsSipUrl is null.
	 *
	 */
	public AseSipURIImpl( DsSipURL dsSipUrl, boolean bImmutable) {
		super( dsSipUrl, bImmutable);
		m_dsSipUrl = dsSipUrl;

	}

	/**
	 * Creates a new AseSipURIImpl object from a valid sip URL string.
	 *
	 * @param sipUrl	A valid sip URL String object.
	 *
	 * @throws ServletParseException	if sipUrl is not a valid sip URL.
	 *
	 */
	public AseSipURIImpl( String sipUrl) throws ServletParseException {

		super(sipUrl);

		if ( !(m_dsUri instanceof DsSipURL) ){
			logger.error(STR_INVALID_SIP_URL + sipUrl);

			throw new ServletParseException( STR_INVALID_SIP_URL);
		}

		m_dsSipUrl = (DsSipURL)m_dsUri;
	}

	/**
	 * Returns the user part of this SipURI.
	 *
	 */
	public String getUser(){

		DsByteString bsTemp = m_dsSipUrl.getUser();
		if( bsTemp != null)
			return bsTemp.toString();

		return null;
	}

	/**
	 * Sets the user part of this SipURI.
	 * @param user	New value for user part
	 *
	 */
	public void setUser(String user){

		if( m_bImmutable){
			warnImmutable();
			throw new IllegalArgumentException();
		}
        //bug# BPInd09232
        if(null == user){
            logger.error("setUser():user is null");
            throw new IllegalArgumentException();
        }
		m_dsSipUrl.setUser( 
				new DsByteString( user));
	}

	/**
	 * Returns the password of this SipURI, or null if this is not set.
	 */
	public String getUserPassword(){

		DsByteString bsTemp = m_dsSipUrl.getUserPassword();
		if( bsTemp != null)
			return bsTemp.toString();

		return null;

	}

	/**
	 * Sets the password of this SipURI. 
	 * The use of passwords in SIP or SIPS URIs is discouraged 
	 *	as sending passwords in clear text is a security risk.
	 */
	public void setUserPassword(String password){
		// Need to throw some exception but the Interafce 
		//	does not permit the same.
		if( m_bImmutable){
			warnImmutable();
			throw new IllegalArgumentException();
		}

	}

	/**
	 * Returns the host part of this SipURI.
	 */
	public String getHost(){

		DsByteString bsTemp = m_dsSipUrl.getHost();
		if( bsTemp != null)
			return bsTemp.toString();

		return null;
	}

	/**
	 * Sets the host part of this SipURI. 
	 * This should be a fully qualified domain name or a numeric IP address.
	 */
	public void setHost(String host){
		if( m_bImmutable){
			warnImmutable();
			throw new IllegalArgumentException();
		}
        //bug# BPInd09232
        if(null == host){
            logger.error("setHost():host or value is null");
            throw new IllegalArgumentException();
        }
		m_dsSipUrl.setHost(
				new DsByteString( host) );
	}

	/**
	 * Returns the port number of this SipURI, or -1 if this is not set
	 */
	public int getPort(){
		if(m_dsSipUrl.hasPort()) {
			// Port is set. return the same
			return m_dsSipUrl.getPort();
		} else {
			// Port is not set, return -1
			return -1;
		}
	}

	/**
	 * Sets the port number of this SipURI.
	 */
	public void setPort(int port){
		if( m_bImmutable){
			warnImmutable();
			throw new IllegalArgumentException();
		}

		if(port >= 0) {
			// 'port' is positive, set port no.
			m_dsSipUrl.setPort(port);
		} else {
			// 'port' is negative, remove port no.
			m_dsSipUrl.removePort();
		}
	}

	/**
	 * Returns true if this SipURI is secure, that is, 
	 * if this it represents a sips URI. For "ordinary" sip URIs, 
	 * false is returned.
	 */
	 //BpInd 18588
	public boolean isSecure(){

		return m_dsSipUrl.isSecure();
	}

	/**
	 * Sets the scheme of this URI to sip or sips depending on 
	 * whether the argument is true or not.
	 */
	public void setSecure(boolean b){

		if( m_bImmutable){
			warnImmutable();
			throw new IllegalArgumentException();
		}
		else
		{
			m_dsSipUrl.setSecure(b);
		}
	}

	/**
	 * Returns the value of the named parameter, 
	 * or null if it is not set. 
	 * A zero-length String indicates flag parameter.
	 */
	public String getParameter(String name){
		DsByteString bsTemp = 
			m_dsSipUrl.getParameter( name);
		if( bsTemp != null)
			return bsTemp.toString();

		return null;
	}

	/**
	 * Sets the value of the specified parameter. 
	 * If the parameter already had a value it will be 
	 * overwritten. A zero-length String indicates flag parameter.
	 *
	 */
	public void setParameter(String name, String value){

		// We need to check, if it is one of the known 
		//	boolean parameter and then call appropriate method .

		if( m_bImmutable){
			warnImmutable();
			throw new IllegalArgumentException();
		}
		
		 //BugID:5103 Contact Header JSR289
		/* Checking if URI is of Contact Header type then the parameters
		 * method,ttl,lr,maddr are not allowed to set
		 */
		if(addressHeaderName !=null && addressHeaderName.equals("Contact")){
			if(logger.isInfoEnabled()) logger.info(addressHeaderName);
			if((name.equals(AseStrings.PARAM_MADDR))||(name.equals(AseStrings.PARAM_METHOD))||(name.equals(AseStrings.PARAM_LR))||(name.equals(AseStrings.PARAM_TTL)))
				throw new IllegalArgumentException();
		}

		if(name.equals(AseStrings.PARAM_LR)) {
            //throw new IllegalArgumentException("Invalid param name: lr");
			logger.error("Invalid param name: please use setLrParam(boolean)!");
			return;
		} else if(name.equals(AseStrings.PARAM_MADDR)) {
			setMAddrParam(value);
			return;
		} else if(name.equals(AseStrings.PARAM_METHOD)) {
			setMethodParam(value);
			return;
		} else if(name.equals("transport")) {
			setTransportParam(value);
			return;
		} else if(name.equals(AseStrings.PARAM_TTL)) {
			setTTLParam(Integer.parseInt(value));
			return;
		} else if(name.equals("user")) {
			setUserParam(value);
			return;
		}

        //bug# BPInd09232
        if(null == name ){
			// BPInd10493
            if(logger.isInfoEnabled())  logger.info("setParameter():name is null");
            //throw new IllegalArgumentException();
			return;
        }
        if(null == value ){
			if (name.equalsIgnoreCase(AseStrings.PARAM_Q) || name.equalsIgnoreCase(AseStrings.PARAM_EXPIRES) 
						  || name.equalsIgnoreCase(AseStrings.PARAM_ACTION)) {
				if(logger.isInfoEnabled())  logger.info("setParameter():value is null for parameter name ::"+name);	
			}
			else {
            	logger.error("setParameter():value is null for parameter name ::"+name);
			}
            //throw new IllegalArgumentException();
			return;
        }

		m_dsSipUrl.setParameter( new DsByteString( name),
				new DsByteString( value) );
	}

	/**
	 * Removes the specified parameter.
	 */
	public void removeParameter(String name){
        //bug# BPInd09232
        if(null != name){
            m_dsSipUrl.removeParameter( new DsByteString( name) );    
        }else{
            logger.error("removeParameter(): name is null");
        }
		
	}

	/**
	 * Returns an Iterator over the names (Strings) of all 
	 * parameters present in this SipURI.
	 */
	public java.util.Iterator getParameterNames(){
		/**
		 * We are relying on lazy instantiation here.
		 * This list shall not be prepared unless it is 
		 * asked for. Additionally, we shall invalidate our 
		 * cache, the moment any 'related' mutator is called.
		 *
		 * Try the cached iterator first, if available.
		 * If NOT then generate a new one and cache the same.
		 */

		DsParameters dsParams = m_dsSipUrl.getParameters();
		if(dsParams == null){
			return null;
		}

		ListIterator listIter = dsParams.listIterator(0);
		LinkedList list = new LinkedList();
		DsParameter dsParam = null;
		while( listIter.hasNext() ){
			dsParam = (DsParameter) listIter.next();
			if( null == dsParam)
				continue;

			list.add( dsParam.getKey().toString() );
		}

		m_parameterIterator = list.iterator();

		return m_parameterIterator;

	}

	/**
	 * Returns the value of the "transport" parameter, 
	 * or null if this is not set. 
	 * This is equivalent to getParameter("transport").
	 */
	public String getTransportParam(){

		int transport = m_dsSipUrl.getTransportParam();

		return DsSipTransportType.intern( transport).toString();
	}

	/**
	 * Sets the value of the "transport" parameter. 
	 * This parameter specifies which transport protocol 
	 * to use for sending requests and responses to this entity. 
	 * The following values are defined: "udp", "tcp", "sctp", "tls", 
	 * but other values may be used also. [Use of "tls" as transport
	 * param has been deprecated in RFC3261.]
	 * This method is equivalent to setParameter("transport", transport).
	 */
	public void setTransportParam(String transport){
		if( m_bImmutable){
			warnImmutable();
			throw new IllegalArgumentException();
		}

        //bug# BPInd09232
        if(null == transport){
            logger.error("setTransportParam():transport is null");
            throw new IllegalArgumentException();
        }
		if( validateTransport(transport) == -1 )
		{
			logger.error(" Illegal/Unsupported transport(=>"+transport+"<=) value given, Taking default(=>udp<=) ");
			transport = "udp";
		}
		m_dsSipUrl.setTransportParam( 
				new DsByteString( transport) );
	}

	/**
	*	This method validates the input against the supported transports, currently only UDP is supported
	*/
	private int validateTransport( String transport )
	{
		if(transport.equalsIgnoreCase("udp"))
			return 0;
		if(transport.equalsIgnoreCase("tcp"))
			return 0;
		if(transport.equalsIgnoreCase("tls"))
			return 0;
		return -1;
	}

	/**
	 * Returns the value of the "maddr" parameter, or null 
	 * if this is not set. 
	 * This is equivalent to getParameter("maddr").
	 */
	public String getMAddrParam(){
		DsByteString bsTemp = m_dsSipUrl.getMAddrParam();

		if( bsTemp != null)
			return bsTemp.toString();

		return null;
	}

	/**
	 * Sets the value of the "maddr" parameter. 
	 * This is equivalent to setParameter("maddr", maddr).
	 */
	public void setMAddrParam(String maddr){
		if( m_bImmutable){
			warnImmutable();
			throw new IllegalArgumentException();
		}

        //bug# BPInd09232
        if(null == maddr){
            logger.error("setMAddrParam():maddr is null");
            throw new IllegalArgumentException();
        }
		m_dsSipUrl.setMAddrParam( new DsByteString( maddr) );
	}

	/**
	 * Returns the value of the "method" parameter, 
	 * or null if this is not set. 
	 * This is equivalent to getParameter("method").
	 */
	public String getMethodParam(){

		try{
			DsByteString bsTemp = m_dsSipUrl.getMethodParam();

			if( bsTemp != null)
				return bsTemp.toString();
		} catch( DsSipParserException ex){
			if( logger.isEnabledFor(Level.ERROR) ){
				logger.error( "getMethodParam()", ex);
			}
		}

		return null;
	}

	/**
	 * Sets the value of the "method" parameter. 
	 * This specifies which SIP method to use in requests 
	 * directed at this SIP/SIPS URI.
	 * This method is equivalent to setParameter("method", method).
	 *
	 */
	public void setMethodParam(String method){

		if( m_bImmutable){
			warnImmutable();
			throw new IllegalArgumentException();
		}

        //bug# BPInd09232
        if(null == method){
            logger.error("setMethodParam():method is null");
            throw new IllegalArgumentException();
        }
		m_dsSipUrl.setMethodParam( 
				new DsByteString( method) );
	}

	/**
	 * Returns the value of the "ttl" parameter, 
	 * or -1 if this is not set. 
	 * This method is equivalent to getParameter("ttl").
	 */
	public int getTTLParam(){
		return m_dsSipUrl.getTTL();
	}

	/**
	 * Sets the value of the "ttl" parameter. 
	 * The ttl parameter specifies the time-to-live 
	 * value when packets are sent using UDP multicast.
	 * This is equivalent to setParameter("ttl", ttl).
	 */
	public void setTTLParam(int ttl){

		if( m_bImmutable){
			warnImmutable();
			throw new IllegalStateException();
		}
			

		if (ttl >= 0 &&  ttl <= 255) {
			m_dsSipUrl.setTTL( ttl);
		}
		else {
		if(logger.isInfoEnabled()) 	logger.info("Invalid TTL value specified ");
		}	
	}

	/**
	 * Returns the value of the "user" parameter, 
	 * or null if this is not set. 
	 * This is equivalent to getParameter("user").
	 */
	public String getUserParam(){
		/*int userParam = m_dsSipUrl.getUserParam();

		if( userParam == DsSipURL.USER_IP)
			return STR_IP;
		else if( userParam == DsSipURL.USER_PHONE)
			return STR_PHONE;
		else*/
		DsByteString parStr = m_dsSipUrl.getParameter( new DsByteString("user") );
		if( parStr == null )
			return STR_IP;
		return parStr.toString();
	}

	/**
	 * Sets the value of the "user" parameter. 
	 * This is equivalent to setParameter("user", user).
	 */
	public void setUserParam(String user){
		if( m_bImmutable){
			warnImmutable();
			throw new IllegalArgumentException();
		}


		if( 0 == user.compareToIgnoreCase( STR_IP) )
			m_dsSipUrl.setUserParam( DsSipURL.USER_IP);
		else if ( 0 == user.compareToIgnoreCase( STR_PHONE) )
			m_dsSipUrl.setUserParam( DsSipURL.USER_PHONE);
		else {
			// LOG: unknown user type...
		if( logger.isDebugEnabled() ) {
            logger.debug(" setUserParamCalled with user="+user); }
			m_dsSipUrl.setParameter(new DsByteString("user"), new DsByteString(user) );
		}

	}

	/**
	 * Returns true if the "lr" flag parameter is set, 
	 * and false otherwise. 
	 * This is equivalent to "".equals(getParameter("lr")).
	 */
	public boolean getLrParam(){

		return m_dsSipUrl.hasLRParam();
	}

	/**
	 * Sets or removes the "lr" parameter depending on the value of the flag.
	 */
	public void setLrParam(boolean flag){

		if( m_bImmutable){
			warnImmutable();
			throw new IllegalArgumentException();
		}

		if( flag){
			m_dsSipUrl.setLRParam();
		} else {
			m_dsSipUrl.removeLRParam();
		}
	}

	/**
	 * Returns the value of the specified header. 
	 * SIP/SIPS URIs may specify headers. As an example, 
	 * the URI sip:joe@example.com?Priority=emergency 
	 * has a header "Priority" whose value is "emergency".
	 *
	 */
	public String getHeader(String name){
        //bug# BPInd09232
        DsByteString bsTemp = null;
        if(null != name){
            bsTemp = m_dsSipUrl.getHeader(
                    new DsByteString( name) );
        }else{
            logger.error("getHeader(): name is null");
            return null;
        }

		if( bsTemp != null)
			return bsTemp.toString();

		return null;
	}

	/**
	 * Sets the value of the specified header in this SipURI.
	 */
	public void setHeader(String name,
			String value){

		if( m_bImmutable){
			warnImmutable();
			throw new IllegalArgumentException();
		}

        //bug# BPInd09232
        if(null == name){
          if(logger.isInfoEnabled())   logger.info("setHeader():name is null");
            //throw new IllegalArgumentException();
			return;
        }
        if( null == value){
          if(logger.isInfoEnabled())   logger.info("setHeader():value is null for header name = "+name);
            //throw new IllegalArgumentException();
			return;
        }
       		m_dsSipUrl.removeHeader(new DsByteString(name) ); 
		m_dsSipUrl.setHeader( new DsByteString( name),
				new DsByteString( value) );
	}

	/**
	 * Returns an Iterator over the names of all headers 
	 * present in this SipURI.
	 */
	public java.util.Iterator getHeaderNames(){

		/**
		 * Try the cached iterator first, if available.
		 * If NOT then generate a new one and cache the same.
		 */

		DsHeaderParameters dsParams = m_dsSipUrl.getHeaders();
		if(dsParams == null){
			return null;
		}

		ListIterator listIter = dsParams.listIterator(0);
		LinkedList list = new LinkedList();
		DsHeaderParameter dsHeaderParam = null;
		while( listIter.hasNext() ){
			dsHeaderParam = (DsHeaderParameter) listIter.next();
			if( null == dsHeaderParam)
				continue;

			list.add( dsHeaderParam.getKey().toString() );
		}

		m_headerIterator = list.iterator();

		return m_headerIterator;
	}

	/**
	 * Returns the cloned object of this SipURI. The copy should be mutable.
	 */
    public SipURI clone(){
            return new AseSipURIImpl( (DsSipURL)m_dsUri.clone(), false);
    }


	/**
	 * Returns the String representation of this SipURI.
	 */
	public String toString(){
		return m_dsSipUrl.toString();
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
			 public void setImmutable()
			 public boolean isSipURI()
			 public String getScheme()
			 public Object clone()
			 public String toString()

			 *
			 public String getUser()
			 public String getUserPassword()
			 public String getHost()
			 public int getPort()
			 public boolean isSecure()
			 public String getParameter(String name)
			 public void removeParameter(String name)
			 public java.util.Iterator getParameterNames()
			 public String getTransportParam()
			 public String getMAddrParam()
			 public String getMethodParam()
			 public int getTTLParam()
			 public String getUserParam()
			 public boolean getLrParam()
			 public String getHeader(String name)
			 public java.util.Iterator getHeaderNames()

			 *
			 */

			if(args.length >3){
				logResults("\tArguments: " + args[0] + " , " + args[1] +" , " + args[2] +" , " + args[3] );
				logResults("");
			}

			AseSipURIImpl uri = new AseSipURIImpl( args[0]);

			logResults("\t[ Method ]\t\t[ Return Value ]");
			logResults("\tboolean isSipURI() : " + uri.isSipURI());
			logResults("\tString getScheme() : " + uri.getScheme() );
			logResults("\tObject clone() : " + uri.clone().toString() );
			logResults("\tString toString() : " + uri );

			logResults("\tString getUser() : " + uri.getUser() );
			logResults("\tString getUserPassword() : " + uri.getUserPassword() );
			logResults("\tString getHost() : " + uri.getHost() );
			logResults("\tint getPort() : " + uri.getPort() );
			logResults("\tboolean isSecure() : " + uri.isSecure() );
			logResults("\tString getTransportParam() : " + uri.getTransportParam() );
			logResults("\tString getMAddrParam() : " + uri.getMAddrParam() );
			logResults("\tString getMethodParam() : " + uri.getMethodParam() );
			logResults("\tint getTTLParam() : " + uri.getTTLParam() );
			logResults("\tString getUserParam() : " + uri.getUserParam() );
			logResults("\tboolean getLrParam() : " + uri.getLrParam() );
			logResults("\tString getHeader(String name) : " + uri.getHeader("some-header") );
			logResults("\tString getParameter(String name) : " + uri.getParameter("some-parameter") );
			logResults("\tjava.util.Iterator getParameterNames() : " + 
					printIterator( uri.getParameterNames() ) );
			logResults("\tjava.util.Iterator getHeaderNames() : " + 
					printIterator( uri.getHeaderNames() ) );

			uri.setUser(args[1]) ;
			uri.setUserPassword(args[1]) ;
			uri.setHost(args[2]) ;
			uri.setPort(Integer.parseInt(args[3]) ) ;
			uri.setSecure(true) ;
			uri.setParameter(args[1], args[2]) ;
			uri.setTransportParam("TCP") ;
			uri.setMAddrParam("192.168.9.128") ;
			uri.setMethodParam("INVITE") ;
			uri.setTTLParam(555) ;
			uri.setUserParam(args[1]) ;
			uri.setLrParam(!uri.getLrParam() );
			uri.setHeader("D_HEADER", args[1]) ;

			logResults("\tString toString() : " + uri );

			uri.removeParameter(args[1]) ;

			logResults("\tString toString() : " + uri );
			logResults("\tjava.util.Iterator getParameterNames() : " + 
					printIterator( uri.getParameterNames() ) );
			logResults("\tjava.util.Iterator getHeaderNames() : " + 
					printIterator( uri.getHeaderNames() ) );

			uri.setImmutable();
			uri.setParameter(args[1], args[2]) ;

		}// main()

	/**
	 * Removes the named header from this SipURI
	 */
	public void removeHeader(String name) {
		if(m_bImmutable){
			warnImmutable();
			throw new IllegalArgumentException();
		}

		if(null == name){
			if(logger.isInfoEnabled())  logger.info("removeHeader():name is null");
			return;
		}

		m_dsSipUrl.removeHeader(new DsByteString(name)); 
	}
	
	// BugID  6859
	
	/*
	 *Compares the given SipURI with this SipURI.
	 */
	public boolean equals(Object uri){

		if(uri == null || m_dsSipUrl == null){
			return false;
		}

		if(!(uri instanceof AseSipURIImpl) ){
			return false;
		}

		return m_dsSipUrl.equals( ((AseSipURIImpl)uri).m_dsSipUrl );
	}

	/*
	 * The hashCode() needs to be override otherwise it will call 
	 * hashCode() implementation of the Object class which will always
	 * return unequal match for different objects
	 */
	public int hashCode() {

		if(m_dsSipUrl == null){
			return -1;
		}
		return m_dsSipUrl.toString().hashCode();
	}

	
	public void setRecursed(boolean recurse){
		isRecurse = recurse;
	}
	
	public boolean isRecursed(){
		return isRecurse;
	}
	//

}// class AseSipURIImpl

