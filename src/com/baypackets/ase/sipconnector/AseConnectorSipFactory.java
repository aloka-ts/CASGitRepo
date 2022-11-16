/*
 * @(#)AseConnectorSipFactory.java        1.0 2004/08/10
 *
 */

package com.baypackets.ase.sipconnector;

import javax.servlet.sip.Address;
import javax.servlet.sip.AuthInfo;
import javax.servlet.sip.Parameterable;
import javax.servlet.sip.ServletParseException;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipURI;
import javax.servlet.sip.URI;
import javax.servlet.sip.ar.SipApplicationRoutingDirective;

import org.apache.log4j.Logger;

import com.baypackets.ase.common.Registry;
import com.baypackets.ase.container.AseApplicationSession;
import com.baypackets.ase.container.AseContext;
import com.baypackets.ase.container.AseHost;
import com.baypackets.ase.measurement.AseMeasurementUtil;
import com.baypackets.ase.ocm.OverloadControlManager;
import com.baypackets.ase.util.AseStrings;
import com.baypackets.ase.util.Constants;
import com.dynamicsoft.DsLibs.DsSipLlApi.DsSipClientTransaction;
import com.dynamicsoft.DsLibs.DsSipLlApi.DsSipServerTransaction;
import com.dynamicsoft.DsLibs.DsSipObject.DsByteString;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipAckMessage;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipCancelMessage;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipConstants;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipContactHeader;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipFromHeader;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipHeader;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipHeaderList;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipMessage;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipNameAddressHeader;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipRequest;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipResponse;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipTag;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipToHeader;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipURL;
import com.dynamicsoft.DsLibs.DsSipObject.DsTelURL;
import com.dynamicsoft.DsLibs.DsSipObject.DsURI;
import com.dynamicsoft.DsLibs.DsSipParser.DsSipParserException;
import com.dynamicsoft.DsLibs.DsUtil.DsException;

import com.baypackets.bayprocessor.slee.common.ConfigRepository;

/**
 * Class AseConnectorSipFactory implements the interface javax.servlet.sip.SipFactory.
 *
 * @version 	1.0 10 Aug 2004 
 * @author 	Baypackets Inc
 *
 */

public class AseConnectorSipFactory implements SipFactory{
	/**
	 * This class implements menthods to create other 
	 *	Sip Servlet objects by 
	 *	- calling their constructors, 
	 *	- copying and modifying the required parameters 
	 *	- and optionaly making them immutable.
	 */

	/** Reference to the AseSipConnector object. */
	private AseSipConnector m_connector = null;

	/** Reference to the AseSipCallIdGenerator object. */
	private AseSipCallIdGenerator m_callIdGenerator = null;

	/** A Dummy DsSipURL object to be used as Contact URI for generating new Requests. */
	private static DsSipURL m_DummyContactUri = null;

    /** Name of Dummy user for creating CONTACT URI */
    private static final String STR_DUMMY_USER = "ase-dummy";

	/** Logger element */
	private static Logger logger = Logger.getLogger(AseConnectorSipFactory.class.getName());

	private OverloadControlManager m_ocmManager = null; 

	/**
	 * Creates a new AseConnectorSipFactory object
	 *
	 */
	public AseConnectorSipFactory(AseSipConnector connector, 
		AseSipCallIdGenerator callIdGenerator) {
		m_connector = connector;
		m_DummyContactUri = new DsSipURL(
				new DsByteString(AseConnectorSipFactory.STR_DUMMY_USER),
				new DsByteString("127.0.0.1"));
		m_callIdGenerator = callIdGenerator;
		m_ocmManager = (OverloadControlManager) Registry.lookup(Constants.NAME_OC_MANAGER);
	}

	/**
	 * Initializes the AseConnectorSipFactory object from a valid AseSipConnector object.
	 *
	 * @param connector	A valid AseSipConnector object
	 *
	 */
	public void initialize() {
		
		ConfigRepository m_configRepository    = (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
		String contact = m_configRepository.getValue(Constants.CAS_PUBLIC_CONTACT_ADDRESS);
		
		if(contact != null && !contact.isEmpty()) {
			this.setIPAddress(contact);
		} else {
			this.setIPAddress(m_connector.getIPAddress());
		}
		
		this.setPort(m_connector.getPort());
	}

	public void setCallIdGenerator(AseSipCallIdGenerator callIdGenerator) {
		if(null != callIdGenerator) {
			m_callIdGenerator = callIdGenerator;
		}
	}
	
	
	public AseSipCallIdGenerator getCallIdGenerator() {
	
			return m_callIdGenerator ;
		
	}
	

	/**
	 * This method creates deep copy of the given SIP session.
	 *
	 * @param session session which is to be cloned
	 * @return clone object of the given session
	 */
	public AseSipSession createSession(AseSipSession session) {
		if(logger.isDebugEnabled()) {
			logger.debug("createSession(AseSipSession) called");
		}

		try {
			return (AseSipSession)session.clone();
		} catch(CloneNotSupportedException e) {
			logger.error("createSession(): Exception in cloning SipSession: "
						+ session.getId(), e);
		}

		return null;
	}

	/**
	 * Returns a AseApplicationSession. 
	 * @return NULL
	 */
	public SipApplicationSession createApplicationSession() {
		if(logger.isDebugEnabled()) logger.debug("createApplicationSession() called");
		return null;
	}

	/**
	 * Creates a new AseSipSession object.
	 * @return a new AseSipSession object
	 */
	public AseSipSession createSession() {
		if(logger.isDebugEnabled()) logger.debug("createSession() called");
		return new AseSipSession(m_connector);
	}

	/**
	 * Returns a URI object corresponding to the specified string. 
	 * The URI may then be used as request URI in SIP requests or 
	 * as the URI component of Address objects.
	 *	Implementations must be able to represent URIs of any scheme. 
	 * This method returns a SipURI object if the specified string 
	 * is a sip or a sips URI, and a TelURL object if it's a tel URL.
	 *
	 * @param uri the URI string to parse
	 * @return a parsed URI object
	 * @throws ServletParseException if the URI scheme is unknown or parsing failed
	 */
	public URI createURI(String uri) throws ServletParseException {
		if(logger.isDebugEnabled() ){
			logger.debug("createURI(String uri) : [" + uri + "] called");
		}

		try{
			DsURI dsUri = DsURI.constructFrom(uri);

			if(dsUri instanceof DsSipURL){
				return new AseSipURIImpl( (DsSipURL) dsUri);
			} else if(dsUri instanceof DsTelURL){
				return new AseTelURLImpl( (DsTelURL) dsUri);
			} else 
				{
					return new AseURIImpl( dsUri);
					//throw new ServletParseException( " Unsupported URL type ");
				}

		} catch(DsSipParserException e) {
			logger.error("createURI(): Exception in creating URI", e);
			throw new ServletParseException(e.getMessage() );
		}

	}

	/**
	 *	Constructs a SipURI with the specified user and host components. The scheme will 
	 *  initially be sip but the application may change it to sips by calling setSecure(true) 
	 *  on the returned SipURI. Likewise, the port number of the new URI is left unspecified 
	 *  but may subsequently be set by calling setPort on the returned SipURI.
	 * @param user user part of the new SipURI
	 * @param host - host part of the new SipURI
	 * @return new insecure SipURI with the specified user and host parts
	 *
	 */
	public SipURI createSipURI(String user, String host) {
		if(logger.isDebugEnabled() ){
			logger.debug("createSipURI(String user, String host) : [" 
				+ user + ", " + host + "] called");
		}

		try{ 
            DsSipURL dsSipUrl = null;
            //bug# BPInd09232
            if(null == user ) {
                logger.error("createSipURI(): null user");
                return null;    
            }
            if( null == host) {
                logger.error("createSipURI(): null host");
                return null;    
            }
            
            dsSipUrl = new DsSipURL( 
                    new DsByteString(user),
                    new DsByteString(host) );

			return new AseSipURIImpl(dsSipUrl);
		} catch(Exception e) {
			logger.error("createSipURI(): Exception in creating SIP URL", e);
			return null;
		}

	}


	/**
	 * Returns a Address corresponding to the specified string. 
	 * The resulting object can be used, for example, as the value 
	 * of From or To headers of locally initiated SIP requests.
	 * The special argument "*" results in a wildcard Address being 
	 * returned, that is, an Address for which isWildcard returns true. 
	 * Such addresses are for use in Contact headers only.
	 *
	 * @param addr valid value of SIP From or To header
	 * @return a parsed Address
	 * @throws ServletParseException - if parsing failed
	 *
	 */
	public Address createAddress(String address) throws ServletParseException {
		//bug# BPInd09232
        if(null == address) {
            logger.error("createAddress(String address):null address");
			return null;
		}

		if(logger.isDebugEnabled()) {
			logger.debug("createAddress(String): [" + address + "] called");
		}

		try{
			DsSipNameAddressHeader dsNAHeader = (DsSipNameAddressHeader)
				DsSipHeader.createHeader(DsSipHeader.CONTACT, 
						address.getBytes());
			return new AseAddressImpl(dsNAHeader);
		} catch(DsException e) {
			logger.error("createAddress(String): Exception in creating header", e);
			throw new ServletParseException(e.getMessage());
		}

	}

	/**
	 * Returns an Address with the specified URI and no display name.
	 *
	 * @param uri - the URI of the returned Address
	 * @return an Address whose URI component is the argument
	 *
	 */
	public Address createAddress(URI uri) {
        //bug# BPInd09232
		if(null == uri) {
            logger.error("createAddress(URI uri):null uri");
			return null;
		}

		if(logger.isDebugEnabled()) {
			logger.debug("createAddress(URI): [" + uri.toString() + "] called");
		}

		try{ 
			AseURIImpl newUri = (AseURIImpl) uri.clone();
			DsSipNameAddressHeader dsNAHeader = 
				new DsSipContactHeader(newUri.getDsURI());
			return new AseAddressImpl(dsNAHeader);
		} catch(Exception e) {
			logger.error("createAddress(URI): Exception in creating header", e);
			return null;
		}

	}

	/**
	 * Returns a new Address with the specified URI and display name.
	 *
	 * @param uri - URI of the new Address
	 * @param displayName - display name of the new Address
	 * @return an Address whose Display name and URI component are the arguments 
	 *
	 */
	public Address createAddress(URI uri, String displayName) {
        //bug# BPInd09232
		if(null == uri) {
            logger.error("createAddress(URI, String): uri is null");
			return null;
		}

		if(logger.isDebugEnabled()) {
			logger.debug("createAddress(URI, String): [" + uri.toString() 
				+ ", " + displayName + "] called");
		}

		AseAddressImpl address = (AseAddressImpl) createAddress(uri);
        //bug# BPInd09232
        if(null != displayName) {
            address.getDsNameAddressHeader().getNameAddress().
                setDisplayName(new DsByteString(displayName));
        }else{
            logger.error("createAddress(URI, String): Display Name is null");
            return null;
        }
		return address;
	}

	/**
	 * Returns a new request object with the specified request 
	 * method, From, and To headers. The returned request object 
	 * exists in a new SipSession which belongs to the specified 
	 * SipApplicationSession.
	 *
	 * This method is used by servlets acting as SIP clients in 
	 * order to send a request in a new call leg. The container is 
	 * responsible for assigning the request appropriate Call-ID and 
	 * CSeq headers, as well as Contact header if the method is not 
	 * REGISTER.
	 *
	 * This method makes a copy of the from and to arguments and 
	 * associates them with the new SipSession. Any component of the 
	 * from and to URIs not allowed in the context of SIP From and To 
	 * headers are removed from the copies. This includes, headers and 
	 * various parameters. Also, a "tag" parameter in either of the 
	 * copied from or to is also removed, as it is illegal in an 
	 * initial To header and the container will choose it's own tag 
	 * for the From header. The copied from and to addresses can be 
	 * obtained from the SipSession but must not be modified by 
	 * applications.
	 *
	 * @param appSession the application session to which the new 
	 * 		SipSession and SipServletRequest belongs
	 * @param method - the method of the new request, e.g. "INVITE"
	 * @param from - value of the From header
	 * @param to - value of the To header
	 * @return the request object with method, request URI, and From, To, 
	 * 		Call-ID, CSeq, Route headers filled in.
	 * @throws IllegalArgumentException - if the method is "ACK" or "CANCEL"
	 *
	 */
	 public SipServletRequest createRequest(SipApplicationSession appSession,
						 String method, Address from, Address to) {
		if(logger.isDebugEnabled())
			logger.debug("Entering createRequest(SipAppSession, String, Address, Address)");

		if(null == appSession) {
			logger.error("createRequest(): ApplicationSession agrument is NULL");
			throw new IllegalArgumentException();
		}

		//Bug 7070
		if(method != null && method.equalsIgnoreCase(AseStrings.NOTIFY)) {
			AseContext context = ((AseApplicationSession)appSession).getContext();
			if(context != null && context.getMaxNotifyPerSec() > -1){
				if(context.getNotifyCount() >= context.getMaxNotifyPerSec()) {
					logger.error("createRequest(): NOTIFY creation not allowed. It is exceeding the specified rate.");
					return null;
				} else {
					context.incrementNotifyCount();
				}
			}
		}

		DsSipNameAddressHeader dsFromHdr = null;
		DsSipNameAddressHeader dsToHdr = null;
		if(true == validateMethodForReqCreation(method)) {
			if(null != from) {
                                
				dsFromHdr = ((AseAddressImpl)from).getDsNameAddressHeader();
			}
			else {
				logger.error("createRequest(): FROM parameter is NULL");
				throw new IllegalArgumentException();
			}

			if(null != to) {

                                dsToHdr = ((AseAddressImpl)to).getDsNameAddressHeader();
			}
			else {
				logger.error("createRequest(): TO parameter is NULL");
				throw new IllegalArgumentException();
			}
		}
		else {
			logger.error("createRequest(): SIP METHOD parameter validation failed");
			throw new IllegalArgumentException();
		}

		String origCallId = (String)appSession.getAttribute(Constants.ORIG_CALL_ID);
		SipServletRequest request = this.createRequest(appSession, method, 
			dsFromHdr, dsToHdr, null, m_callIdGenerator.generateCallId(origCallId));
		if(logger.isDebugEnabled()) logger.debug("Exiting createRequest(SipAppSession, String, Address, Address)");
		return request;
	}

	/**
	 * Returns a new request object with the specified request method, 
	 *	From, and To headers. The returned request object exists in a 
	 * 	new SipSession which belongs to the specified SipApplicationSession.
	 * 
	 * 	This method is used by servlets acting as SIP clients in order to 
	 *	send a request in a new call leg. The container is responsible for 
	 *	assigning the request appropriate Call-ID and CSeq headers, as well 
	 *	as Contact header if the method is not REGISTER.
	 * 
	 * This method makes a copy of the from and to arguments and associates 
	 *	them with the new SipSession. Any component of the from and to URIs 
	 *	not allowed in the context of SIP From and To headers are removed 
	 *	from the copies. This includes, headers and various parameters. 
	 *	The from and to addresses can subsequently be obtained from the 
	 *	SipSession or the returned request object but must not be modified 
	 *	by applications.
	 * 
	 * @param appSession - the application session to which the new SipSession 
	 * 		and SipServletRequest belongs
	 * @param method - the method of the new request, e.g. "INVITE"
	 * @param from - value of the From header
	 * @param to - value of the To header
	 * @return the request object with method, request URI, and From, To, 
	 * 		Call-ID, CSeq, Route headers filled in.
	 * @throws IllegalArgumentException - if the method is "ACK" or "CANCEL"
	 *
	 */
	public SipServletRequest createRequest(SipApplicationSession appSession,
			String method, URI from, URI to) {
		if(logger.isDebugEnabled()) logger.debug("Entering createRequest(SipAppSession, String, URI, URI)");

		if(null == appSession) {
			logger.error("createRequest(): ApplicationSession parameter is NULL");
			throw new IllegalArgumentException();
		}

		//Bug 7070
		if(method != null && method.equalsIgnoreCase(AseStrings.NOTIFY)) {
			AseContext context = ((AseApplicationSession)appSession).getContext();
			if(context != null && context.getMaxNotifyPerSec() > -1){
				if(context.getNotifyCount() >= context.getMaxNotifyPerSec()) {
					logger.error("createRequest(): NOTIFY creation not allowed. It is exceeding the specified rate.");
					return null;
				} else {
					context.incrementNotifyCount();
				}
			}
		}
		
		DsSipNameAddressHeader dsFromHdr = null;
		DsSipNameAddressHeader dsToHdr = null;
		if(true == validateMethodForReqCreation(method)) {
			if(null != from) {
				dsFromHdr = new DsSipFromHeader(((AseURIImpl)from).getDsURI());
			}
			else {
				logger.error("createRequest(): FROM parameter is NULL");
				throw new IllegalArgumentException();
			}

			if(null != to) {
				dsToHdr = new DsSipToHeader(((AseURIImpl)to).getDsURI());
			}
			else {
				logger.error("createRequest(): TO parameter is NULL");
				throw new IllegalArgumentException();
			}
		}
		else {
			logger.error("createRequest(): SIP METHOD parameter validation failed");
			throw new IllegalArgumentException();
		}

		String origCallId = (String)appSession.getAttribute(Constants.ORIG_CALL_ID);
		SipServletRequest request = this.createRequest(appSession, method, 
			dsFromHdr, dsToHdr, null, m_callIdGenerator.generateCallId(origCallId));
		if(logger.isDebugEnabled()) logger.debug("Exiting createRequest(SipAppSession, String, URI, URI)");
		return request;
	}

	/**
	 * Returns a new request object with the specified request method, From, 
	 * and To headers. The returned request object exists in a new SipSession 
	 * which belongs to the specified SipApplicationSession.
	 * This method is used by servlets acting as SIP clients in order to send 
	 * a request in a new call leg. The container is responsible for assigning 
	 * the request appropriate Call-ID and CSeq headers, as well as Contact
	 * header if the method is not REGISTER.
	 *
	 * This method is functionally equivalent to:
	 * 		createRequest(method, f.createAddress(from), f.createAddress(to));
	 *
	 * Note that this implies that if either of the from or to argument is a SIP 
	 * URI containing parameters, the URI must be enclosed in angle brackets. 
	 * Otherwise the address will be parsed as if the parameter belongs to the 
	 * address and not the URI.
	 *
	 * @param appSession - the application session to which the new SipSession 
	 * 		and SipServletRequest belongs
	 * @param method - the method of the new request, e.g. "INVITE" from - value 
	 * 		of the From header -- this must be a valid Address
	 * @param to - value of the To header -- this must be a valid Address
	 * @return the request object with method, request URI, and From, To, Call-ID, 
	 * 		CSeq, Route headers filled in.
	 * @throws ServletParseException - if the URI scheme of the from or to argument 
	 * 		is unknown or if parsing failed IllegalArgumentException - if the method 
	 * 		is "ACK" or "CANCEL"
	 */
	public SipServletRequest createRequest(SipApplicationSession appSession,
			String method, String from, String to) throws ServletParseException {
		logger.debug("Entering createRequest(SipAppSession, String, String, String)");

		if(null == appSession) {
			logger.error("createRequest(): ApplicationSession parameter is NULL");
			throw new IllegalArgumentException();
		}

		//Bug 7070
		if(method != null && method.equalsIgnoreCase(AseStrings.NOTIFY)) {
			AseContext context = ((AseApplicationSession)appSession).getContext();
			if(context != null && context.getMaxNotifyPerSec() > -1){
				if(context.getNotifyCount() >= context.getMaxNotifyPerSec()) {
					logger.error("createRequest(): NOTIFY creation not allowed. It is exceeding the specified rate.");
					return null;
				} else {
					context.incrementNotifyCount();
				}
			}
		}
		
		DsSipNameAddressHeader dsFromHdr = null;
		DsSipNameAddressHeader dsToHdr = null;
		if(true == validateMethodForReqCreation(method)) {
			try {
                //bug# BPInd09232
                if(null == from) {
                    logger.error("createRequest() : From Header is null");
                    throw new ServletParseException("From Header is null");
                }
                if(null == to) {
                    logger.error("createRequest() : To Header is null");
                    throw new ServletParseException("To Header is null");
                }
                
			 	dsFromHdr = new DsSipFromHeader(new DsByteString(from));
			 	dsToHdr = new DsSipToHeader(new DsByteString(to));
			}
			catch(DsException e) {
			 	logger.error("createRequest() : ", e);
			 	throw new ServletParseException(e.getMessage());
			}
		}
		else {
			logger.error("createRequest(): SIP METHOD parameter validation failed");
			throw new IllegalArgumentException();
		}

		String origCallId = (String)appSession.getAttribute(Constants.ORIG_CALL_ID);
		SipServletRequest request = this.createRequest(appSession, method, 
			dsFromHdr, dsToHdr, null, m_callIdGenerator.generateCallId(origCallId));

		if(logger.isDebugEnabled()) logger.debug("Exiting createRequest(SipAppSession, String, String, String)");
		return request;
	}

	/**
	 * Creates a new request object belonging to a new SipSession. The new request 
	 * is similar to the specified origRequest in that the method and the majority 
	 * of header fields are copied from origRequest to the new request.
	 *
	 * This method satisfies the following rules:
	 * The From header field of the new request has a new tag chosen by the container.
	 * The To header field of the new request has no tag.
	 * If the sameCallId argument is false, the new request (and the corresponding 
	 * SipSession)is assigned a new Call-ID.
	 * Record-Route and Via header fields are not copied. As usual, the container will 
	 * add its own Via header field to the request when it!Gs actually sent outside 
	 * the application server.
	 * For non-REGISTER requests, the Contact header field is not copied but is 
	 * populated by the container as usual. 
	 * This method provides a convenient and efficient way of constructing the second 
	 * "leg" of a B2BUA application. It is used only for the initial request. Subsequent 
	 * requests in either leg must be created using SipSession.createRequest(String) as 
	 * usual.
	 *
	 * @param origRequest - request to be "copied"
	 * @param sameCallId - whether or not to use same Call-ID for the new dialog
	 * @return the "copied" request object
	 *
	 */
	public SipServletRequest createRequest(SipServletRequest origRequest,
			boolean sameCallId) {
		if(logger.isDebugEnabled()) logger.debug("Entering createRequest(SipServletRequest, boolean)");

		SipApplicationSession appSession = ((AseSipServletMessage)origRequest).
		getAseSipSession().getApplicationSession();		
		
		DsSipNameAddressHeader dsFromHdr = null;
		DsSipNameAddressHeader dsToHdr = null;
		String method = origRequest.getMethod();

		//Bug 7070
		if(method != null && method.equalsIgnoreCase(AseStrings.NOTIFY)) {
			AseContext context = ((AseApplicationSession)appSession).getContext();
			if(context != null && context.getMaxNotifyPerSec() > -1){
				if(context.getNotifyCount() >= context.getMaxNotifyPerSec()) {
					logger.error("createRequest(): NOTIFY creation not allowed. It is exceeding the specified rate.");
					return null;
				} else {
					context.incrementNotifyCount();
				}
			}
		}
		
		if(true == validateMethodForReqCreation(method)) {
		 	dsFromHdr = ((AseAddressImpl)origRequest.getFrom()).getDsNameAddressHeader();
		 	dsToHdr = ((AseAddressImpl)origRequest.getTo()).getDsNameAddressHeader();
		}
		else {
			logger.error("createRequest(): SIP METHOD name validation failed");
			throw new IllegalArgumentException();
		}

		DsSipMessage lDsMessage = ((AseSipServletMessage)origRequest).getDsMessage();
		String callId = null;
		if(true == sameCallId) {
			callId = lDsMessage.getCallId().toString();
		} else {
			// BPInd19271 callId = m_callIdGenerator.generateCallId(lDsMessage.getCallId().toString());
			String origCallId = (String)appSession.getAttribute(Constants.ORIG_CALL_ID);
			callId = m_callIdGenerator.generateCallId(origCallId);
		}

		SipServletRequest request = this.createRequest(appSession, (DsSipRequest)lDsMessage,
												dsFromHdr, dsToHdr, callId);

		
		((AseSipServletRequest)request).setRoutingDirective(SipApplicationRoutingDirective.CONTINUE, origRequest);

		if(logger.isDebugEnabled()) logger.debug("Exiting createRequest(SipServletRequest, boolean)");
		return request;
	}
	
	
	  /**
     * Creates a new request object belonging to a new SipSession. The new request 
     * is similar to the specified origRequest in that the method and the majority 
     * of header fields are copied from origRequest to the new request.
     *
     * This method satisfies the following rules:
     * The From header field of the new request has a new tag chosen by the container.
     * The To header field of the new request has no tag.
     * If the sameCallId argument is false, the new request (and the corresponding 
     * SipSession)is assigned a new Call-ID.
     * Record-Route and Via header fields are not copied. As usual, the container will 
     * add its own Via header field to the request when it!Gs actually sent outside 
     * the application server.
     * For non-REGISTER requests, the Contact header field is not copied but is 
     * populated by the container as usual. 
     * This method provides a convenient and efficient way of constructing the second 
     * "leg" of a B2BUA application. It is used only for the initial request. Subsequent 
     * requests in either leg must be created using SipSession.createRequest(String) as 
     * usual.
     * The contact header will be created using the fromheader if provided to b2bhelper in
     * header map while creating the request 
     *
     * @param origRequest - request to be "copied"
     * @param fromHeaderToCreateContact even the new request is created from orig request but
     * contact will be created from form header if provided through headermap by b2buahelper
     * @param sameCallId - whether or not to use same Call-ID for the new dialog
     * @return the "copied" request object
     *
     */
	  public SipServletRequest createRequest(SipServletRequest origRequest,Address fromHeaderToCreateContact,
              boolean sameCallId) {
      if(logger.isDebugEnabled()) logger.debug("Entering createRequest(SipServletRequest,fromHeaderToCreateContact, boolean)");

      SipApplicationSession appSession = ((AseSipServletMessage)origRequest).
      getAseSipSession().getApplicationSession();

      DsSipNameAddressHeader dsFromHdr = null;
      DsSipNameAddressHeader dsToHdr = null;
      String method = origRequest.getMethod();

      //Bug 7070
      if(method != null && method.equalsIgnoreCase(AseStrings.NOTIFY)) {
              AseContext context = ((AseApplicationSession)appSession).getContext();
              if(context != null && context.getMaxNotifyPerSec() > -1){
                      if(context.getNotifyCount() >= context.getMaxNotifyPerSec()) {
                              logger.error("createRequest(): NOTIFY creation not allowed. It is exceeding the specified rate.");
                              return null;
                      } else {
                              context.incrementNotifyCount();
                      }
              }
      }

      if(true == validateMethodForReqCreation(method)) {
              dsFromHdr = ((AseAddressImpl)fromHeaderToCreateContact).getDsNameAddressHeader();
              dsToHdr = ((AseAddressImpl)origRequest.getTo()).getDsNameAddressHeader();
      }
      else {
              logger.error("createRequest(): SIP METHOD name validation failed");
              throw new IllegalArgumentException();
      }

      DsSipMessage lDsMessage = ((AseSipServletMessage)origRequest).getDsMessage();
      String callId = null;
      if(true == sameCallId) {
              callId = lDsMessage.getCallId().toString();
      } else {
              // BPInd19271 callId = m_callIdGenerator.generateCallId(lDsMessage.getCallId().toString());
    	      String origCallId = (String)appSession.getAttribute(Constants.ORIG_CALL_ID);
              callId = m_callIdGenerator.generateCallId(origCallId);
      }

      SipServletRequest request = this.createRequest(appSession, (DsSipRequest)lDsMessage,
                                                                                      dsFromHdr, dsToHdr, callId);

      ((AseSipServletRequest)request).setRoutingDirective(SipApplicationRoutingDirective.CONTINUE, origRequest);

      if(logger.isDebugEnabled()) logger.debug("Exiting createRequest(SipServletRequest,fromHeaderToCreateContact, boolean)");
      return request;
    }

	/**
	 * This method creates deep copy of the given request.
	 * @param request request which is to be cloned
	 * @return clone object of the given request
	 */
	AseSipServletRequest createRequest(AseSipServletRequest request) {
		if(logger.isDebugEnabled()) {
			logger.debug("createRequest(AseSipServletRequest) called");
		}

		try {
			return (AseSipServletRequest)request.clone();
		} catch(CloneNotSupportedException e) {
			logger.error("createRequest(): Exception in cloning request", e);
		}

		return null;
	}

	/**
	 * Invoked from AseSipRequestListener and AseSipServerTransactionListener
	 */
	AseSipServletRequest createRequest(DsSipRequest request,
			DsSipServerTransaction serverTxn) {
		if(logger.isDebugEnabled()) logger.debug("createRequest(DsSipRequest, DsSipServerTransaction) called");
		return new AseSipServletRequest(m_connector, request, serverTxn);
	}

	/**
	 * Invoked by AseSipSession.
	 */
	AseSipServletRequest createRequest(AseSipSession sipSession,
												  String method) {

		 if(logger.isDebugEnabled()) logger.debug("Entering createRequest(AseSipSession, String)");

		 DsByteString lDsMethod = AseSipConstants.getDsByteString(method);
		 if (null == lDsMethod) {
			  logger.error("IllegalArgument - SIP method not supported - [" +
								method + "]");
            throw new IllegalArgumentException();
		 }
		 
		 // If the session state is STATE_INITIAL then we are creating
		 // an initial request, else we are creating a subsequent request
		 boolean isInitial = false;
		 if (AseSipSessionState.STATE_INITIAL == sipSession.getSessionState()) {
			 if(logger.isDebugEnabled()) logger.debug("Dialog state INITIAL");
			  isInitial = true;
		 }
		 
		 AseSipServletRequest request = new
			  AseSipServletRequest(sipSession, m_connector, lDsMethod, 
										  sipSession.getFromHeader(),
										  sipSession.getToHeader(),
										  sipSession.getLocalCSeqNumber(),
										  sipSession.getCallId(), 
										  sipSession.getRouteSet(),
										  sipSession.getLocalTarget(), 
										  isInitial);
		 request.setRequestURI(new AseURIImpl(sipSession.getRemoteTarget()));
		 
		 if(logger.isDebugEnabled()) logger.debug("Exiting createRequest(AseSipSession, String)");
		 return request;
	}

	/**
	 * Called by AseSipServletResponse.createAck()
	 * @param request Original AseSipServletResponse object to be Acknowledged.
	 * @return AseSipServletRequest object for ACK method
	 */
	AseSipServletRequest createAck(AseSipServletResponse response) {
		 if(logger.isDebugEnabled()) logger.debug("Entering createAck(AseSipServletResponse)");

		DsSipAckMessage lDsAck = new DsSipAckMessage(response.getDsResponse(), null, null);

		AseSipSession sipSession = (AseSipSession)response.getSession();
		DsSipContactHeader lDsContactHdr = (DsSipContactHeader)sipSession.
														getLocalTarget();
		if(lDsContactHdr == null) {
			lDsContactHdr = this.createContactHdr(((AseAddressImpl)response.getTo())
				.getDsNameAddressHeader());
		}
		lDsAck.updateHeader(lDsContactHdr);

		AseSipServletRequest ackRequest = new AseSipServletRequest(
				sipSession, m_connector, (DsSipRequest)lDsAck, 
				response.getClientTxn());
		// Set pseudo client txn
		ackRequest.setPseudoClientTxn(response.getPseudoClientTxn());

		if(response.getDsResponse().getResponseClass() > 2) {
			ackRequest.setNon2XXAck();
		}

		// Add any routeSet to the ACK
		if(null != sipSession.getRouteSet()) {
			lDsAck.addHeaders(sipSession.getRouteSet(), false);
		}

		 if(logger.isDebugEnabled()) logger.debug("Exiting createAck(AseSipServletResponse)");
		return ackRequest;
	}
	
	
	// Bug ID : 5325 JSR 289.8 PRACK Support
	/**
	 * Called by AseSipServletResponse.createPrack()
	 * @param response Reliable Provisional Response object to be Acknowledged.
	 * @return AseSipServletRequest object for PRACK method
	 */
	AseSipServletRequest createPrack(AseSipServletResponse response) {
		 if(logger.isDebugEnabled()) logger.debug("Entering createPrack(AseConnectorSipFactory");
		String RSeq = response.getHeader(AseStrings.HDR_RSEQ);
		Long cseq = new Long(response.getDsResponse().getCSeqNumber());
		AseSipServletRequest PRACKRequest = (AseSipServletRequest)response.getSession().createRequest("PRACK");
		String rack = RSeq +AseStrings.SPACE+ cseq + AseStrings.SPACE + AseStrings.INVITE;
		PRACKRequest.addHeaderWithoutCheck(AseStrings.HDR_RACK, rack , true, true);
		 if(logger.isDebugEnabled()) logger.debug("Exiting createPrack(AseSipServletResponse)");
		return PRACKRequest;
		
	}
	

	/**
	 * Called by AseSipServletRequest.createCancel()
	 * @param request Original AseSipServletRequest object to be cancelled.
	 * @return AseSipServletRequest object for CANCEL method
	 */
	AseSipServletRequest createCancel(AseSipServletRequest request) {
		 if(logger.isDebugEnabled()) logger.debug("Entering createCancel(AseSipServletRequest)");

		DsSipCancelMessage lDsCancel = new DsSipCancelMessage(request.getDsRequest());

		AseSipServletRequest cancelRequest = 
			new AseSipServletRequest(request.getAseSipSession(), m_connector, 
				(DsSipRequest)lDsCancel, request.getClientTxn());

		// store self reference in CANCEL request
		cancelRequest.setCancelledRequest(request);
		// Store pseudo client txn in CANCEL request
		cancelRequest.setPseudoClientTxn(request.getPseudoClientTxn());
		// Set chain info
		if(request.chainedDownstream()) {
			cancelRequest.setChainedDownstream();
		}


			//BpInd 18444
			 if(logger.isDebugEnabled()) logger.debug("Setting the special headers if any");
			if(request.getHeader(AseStrings.HDR_ACCEPT_CONTACT)!=null)
			{
				cancelRequest.addHeader(AseStrings.HDR_ACCEPT_CONTACT,request.getHeader(AseStrings.HDR_ACCEPT_CONTACT));
			}

			if(request.getHeader(AseStrings.HDR_REJECT_CONTACT)!=null)
			{
				cancelRequest.addHeader(AseStrings.HDR_REJECT_CONTACT,request.getHeader(AseStrings.HDR_REJECT_CONTACT));
			}
			if(request.getHeader("Request-Disposition")!=null)
			{
				cancelRequest.addHeader("Request-Disposition",request.getHeader("Request-Disposition"));
			}

		 if(logger.isDebugEnabled()) logger.debug("Exiting createCancel(AseSipServletRequest)");
		return cancelRequest;
	}

	/**
	 * 
	 */
	AseSipServletResponse createResponse(DsSipResponse dsResponse,
			DsSipClientTransaction clientTxn) {
		 if(logger.isDebugEnabled()) logger.debug("createResponse(DsSipResponse, DsSipClientTransaction) called");
		return new AseSipServletResponse(m_connector, dsResponse, clientTxn);
	}

	/**
	 * Used to create stray response objects
	 */
	AseSipServletResponse createResponse(DsSipResponse dsResponse) {
		 if(logger.isDebugEnabled()) logger.debug("createResponse(DsSipResponse) called");
		return new AseSipServletResponse(m_connector, dsResponse);
	}

	/**
	 * 
	 */
	AseSipServletResponse createResponse(AseSipServletRequest request,
			int respCode, String msg) {

		AseSipServletResponse response = null;
		if(msg == null) {
			response = new AseSipServletResponse(request, respCode);
		} else {
			response = new AseSipServletResponse(request, respCode, msg);
		}

		if(request.getDsRequest().getMethodID() == DsSipConstants.CANCEL) {
			return response;
		}

		// If the response is not redirect and the CONTACT header is not present,
		// create it using the TO header and add in the response
		//BYE,PRACK,OPTIONS and INFO are also exempted as per 
		//RFC 3261 and SBTM UAT 1181
		AseSipSession sipSession = (AseSipSession)(request.getSession());
		if(3 != response.getResponseClass()
		&& sipSession != null
		&& request.getDsRequest().getMethodID() != DsSipConstants.REGISTER
		&& request.getDsRequest().getMethodID() != DsSipConstants.MESSAGE 
		&& request.getDsRequest().getMethodID() != DsSipConstants.PUBLISH
		&& request.getDsRequest().getMethodID() != DsSipConstants.PRACK 
		&& request.getDsRequest().getMethodID() != DsSipConstants.BYE
		&& request.getDsRequest().getMethodID() != DsSipConstants.OPTIONS 
		&& request.getDsRequest().getMethodID() != DsSipConstants.INFO) {
			DsSipContactHeader lDsContactHdr = (DsSipContactHeader)sipSession.
				getLocalTarget();
			if(lDsContactHdr == null) {
				lDsContactHdr = this.createContactHdr(((AseAddressImpl)request.getTo())
					.getDsNameAddressHeader());
			}

			 if(logger.isDebugEnabled()) logger.debug("Updating CONTACT header in response");
			response.getDsResponse().updateHeader(lDsContactHdr);
		}

		// See if we need to add a TAG to the TO header. If dialog state is DLG_INITIAL 
		// then nothing to do, else the TO header already has a tag or we get it from 
		// the session

		if(sipSession == null
		|| AseSipSessionState.STATE_INITIAL != sipSession.getSessionState()) {
				  
			try {
				DsSipToHeader toHeader = response.getDsResponse().getToHeaderValidate();
				if (true != toHeader.isTagPresent()) {
					toHeader.setTag(sipSession.getLocalTag());
				}
			}
			catch (Exception e) {
				// Cannot happen as the TO header is from the stack object
				logger.error("Unable to set the TAG in the TO header");
				logger.error("createResponse(): Exception" + e);
			}
		}
		
		return response;
	}

	/**
	 * Used to clone response objects
	 */
	AseSipServletResponse createResponse(AseSipServletResponse response) {
		 if(logger.isDebugEnabled()) logger.debug("createResponse(AseSipServletResponse) called");

		try {
			return (AseSipServletResponse)response.clone();
		} catch(CloneNotSupportedException e) {
			logger.error("createResponse(): Exception in cloning response", e);
		}

		return null;
	}

	/**
	 * This method is used by other public methods to create a new request object.
	 * This method is used for creating out-of-dialog requests and should not be 
	 * used for creating subsequent requests within an existing dialog - as it
	 * performs the tag manipulation in the FROM & TO headers accordingly.
	 */
	private SipServletRequest createRequest(SipApplicationSession appSession,
			String method, DsSipNameAddressHeader from, DsSipNameAddressHeader to, 
			DsSipHeaderList dsRouteHeaderList, String callId) {

		 if(logger.isDebugEnabled()) logger.debug("Entering private createRequest()");

		// Create the sip session and perform the binding with application session
		AseSipSession lSipSession = this.createSession();
		//decraese sipsession count as it is not maintained for tcap appsessions
		if (appSession.getAttribute(Constants.DIALOGUE_ID) != null) {
			AseMeasurementUtil.counterActiveSIPSessions.decrement();

		}
		// incrementing session count at OCM
		int m_ocmId = m_ocmManager.getParameterId(OverloadControlManager.PROTOCOL_SESSION_COUNT);
		if(((AseApplicationSession)appSession).getInitialPriorityStatus()) {
			m_ocmManager.increaseNSEP(m_ocmId);
		} else {
			m_ocmManager.increase(m_ocmId);
		}
        ((AseApplicationSession)appSession).addProtocolSession(lSipSession);

		// Perform the tag manipulation for TO & FROM headers
		// - remove the tag from TO header (if any)
		// - remove the tag from FROM header (if any) and add a new tag
		// Update the information in the sip session dialog state as well
		DsSipNameAddressHeader lDsNameAddrFromHdr = (DsSipNameAddressHeader)from.clone();
		
		// Now lDsNameAddrFromHdr can be instance of DsSipNameAndAddress class or
		// any of its derived classs. 
		// So we need to convert DsSipNameAddressHeader tp DsSipFromHeader
		DsSipFromHeader lDsFromHdr = null;
		if (! (lDsNameAddrFromHdr instanceof DsSipFromHeader))  {
		 	lDsFromHdr = new DsSipFromHeader(
							lDsNameAddrFromHdr.getNameAddress(),
							lDsNameAddrFromHdr.getParameters());
		}
		else {
			lDsFromHdr = (DsSipFromHeader)lDsNameAddrFromHdr;
		}
		
        lDsFromHdr.removeTag();
    //BPInd18885
     lDsFromHdr.removeParameter(new DsByteString(AseStrings.PARAM_TAG));
        
        DsByteString tag = DsSipTag.generateTag();
        lDsFromHdr.setTag(tag);
        //lSipSession.getDialogState().setLocalTag(tag);
        //lSipSession.getDialogState().setFromHeader(lDsFromHdr);

		DsSipNameAddressHeader lDsNameAddrToHdr = (DsSipNameAddressHeader)to.clone();
		// Now lDsNameAddrToHdr can be instance of DsSipNameAndAddress class or
        // any of its derived classs.
        // So we need to convert DsSipNameAddressHeader tp DsSipFromHeader
		DsSipToHeader lDsToHdr = null;
        if (! (lDsNameAddrToHdr instanceof DsSipToHeader))  {
            lDsToHdr = new DsSipToHeader(
                            lDsNameAddrToHdr.getNameAddress(),
                            lDsNameAddrToHdr.getParameters());
        }
		else {
			lDsToHdr = (DsSipToHeader)lDsNameAddrToHdr;
		}
        //BPInd18885
          lDsToHdr.removeTag();
         lDsToHdr.removeParameter(new DsByteString(AseStrings.PARAM_TAG));

		// Create the CONTACT header for non-REGISTER requests
        DsSipContactHeader lDsContactHeader = null;
        if(!method.equalsIgnoreCase(AseSipConstants.STR_REGISTER)
		&& !method.equalsIgnoreCase(AseSipConstants.STR_MESSAGE) 
		&& !method.equalsIgnoreCase(AseSipConstants.STR_PUBLISH)
		&& !method.equalsIgnoreCase(AseStrings.PRACK)
		&& !method.equalsIgnoreCase(AseStrings.BYE)
		&& !method.equalsIgnoreCase(AseStrings.OPTIONS) 
		&& !method.equalsIgnoreCase(AseStrings.INFO)) {
        	
            lDsContactHeader = this.createContactHdr(lDsFromHdr);
        }

		AseSipServletRequest request = new AseSipServletRequest(
				lSipSession, m_connector, 
				AseSipConstants.getDsByteString(method), 
				lDsFromHdr, lDsToHdr, 
				lSipSession.getLocalCSeqNumber(), 
				callId, dsRouteHeaderList, lDsContactHeader, true);

		 if(logger.isDebugEnabled()) logger.debug("Exiting private createRequest()");
		return request;
	}

	/**
	 * This method is used by other public methods to create a new request object.
	 * This method is used for creating out-of-dialog requests and should not be 
	 * used for creating subsequent requests within an existing dialog - as it
	 * performs the tag manipulation in the FROM & TO headers accordingly.
	 */
	private SipServletRequest createRequest(SipApplicationSession appSession,
						DsSipRequest dsRequest, DsSipNameAddressHeader from,
						DsSipNameAddressHeader to, String callId) {

		 if(logger.isDebugEnabled()) logger.debug("Entering private createRequest()");

		// Create the sip session and perform the binding with application session
		AseSipSession lSipSession = this.createSession();
		//decraese sipsession count as it is not maintained for tcap appsessions
		if (appSession.getAttribute(Constants.DIALOGUE_ID) != null) {
			AseMeasurementUtil.counterActiveSIPSessions.decrement();

		}
        ((AseApplicationSession)appSession).addProtocolSession(lSipSession);
		// incrementing session count at OCM
		int m_ocmId = m_ocmManager.getParameterId(OverloadControlManager.PROTOCOL_SESSION_COUNT);
		if(((AseApplicationSession)appSession).getInitialPriorityStatus()) {
			m_ocmManager.increaseNSEP(m_ocmId);
		} else {
			m_ocmManager.increase(m_ocmId);
		}

		// Perform the tag manipulation for TO & FROM headers
		// - remove the tag from TO header (if any)
		// - remove the tag from FROM header (if any) and add a new tag
		// Update the information in the sip session dialog state as well
		DsSipNameAddressHeader lDsNameAddrFromHdr = (DsSipNameAddressHeader)from.clone();
		
		// Now lDsNameAddrFromHdr can be instance of DsSipNameAndAddress class or
		// any of its derived classs. 
		// So we need to convert DsSipNameAddressHeader tp DsSipFromHeader
		DsSipFromHeader lDsFromHdr = null;
		if (! (lDsNameAddrFromHdr instanceof DsSipFromHeader))  {
		 	lDsFromHdr = new DsSipFromHeader(
							lDsNameAddrFromHdr.getNameAddress(),
							lDsNameAddrFromHdr.getParameters());
		}
		else {
			lDsFromHdr = (DsSipFromHeader)lDsNameAddrFromHdr;
		}
		
       lDsFromHdr.removeTag();
       lDsFromHdr.removeParameter(new DsByteString(AseStrings.PARAM_TAG));
        DsByteString tag = DsSipTag.generateTag();
        lDsFromHdr.setTag(tag);
        //lSipSession.getDialogState().setLocalTag(tag);
        //lSipSession.getDialogState().setFromHeader(lDsFromHdr);

		DsSipNameAddressHeader lDsNameAddrToHdr = (DsSipNameAddressHeader)to.clone();
		// Now lDsNameAddrToHdr can be instance of DsSipNameAndAddress class or
        // any of its derived classs.
        // So we need to convert DsSipNameAddressHeader tp DsSipFromHeader
		DsSipToHeader lDsToHdr = null;
        if (! (lDsNameAddrToHdr instanceof DsSipToHeader))  {
            lDsToHdr = new DsSipToHeader(
                            lDsNameAddrToHdr.getNameAddress(),
                            lDsNameAddrToHdr.getParameters());
        }
		else {
			lDsToHdr = (DsSipToHeader)lDsNameAddrToHdr;
		}

             //BPInd18885
              lDsToHdr.removeTag();
              lDsToHdr.removeParameter(new DsByteString(AseStrings.PARAM_TAG));

		// Create the CONTACT header for non-REGISTER requests
        DsSipContactHeader lDsContactHeader = null;
        if(dsRequest.getMethodID() != DsSipConstants.REGISTER
		&& dsRequest.getMethodID() != DsSipConstants.MESSAGE 
		&& dsRequest.getMethodID() != DsSipConstants.PUBLISH
		&& dsRequest.getMethodID() != DsSipConstants.PRACK 
		&& dsRequest.getMethodID() != DsSipConstants.BYE
		&& dsRequest.getMethodID() != DsSipConstants.OPTIONS 
		&& dsRequest.getMethodID() != DsSipConstants.INFO) {
        	
            lDsContactHeader = this.createContactHdr(lDsFromHdr);
            
            DsSipNameAddressHeader lDsNameAddrContactHdr = (DsSipNameAddressHeader) dsRequest.getContactHeader().clone(); 
            lDsContactHeader.setParameters(lDsNameAddrContactHdr.getParameters());
            
        }

		AseSipServletRequest request = new AseSipServletRequest(
								lSipSession, m_connector, 
								dsRequest, lDsFromHdr, lDsToHdr, 
								lSipSession.getLocalCSeqNumber(), 
								callId, lDsContactHeader, true);

		 if(logger.isDebugEnabled()) logger.debug("Exiting private createRequest()");
		return request;
	}

	/**
	 * 
	 */
	private boolean validateMethodForReqCreation(String method) {

        if(method.equalsIgnoreCase(AseSipConstants.STR_ACK) ||
                method.equalsIgnoreCase(AseSipConstants.STR_CANCEL) ) {
            logger.error("IllegalArgument - Cannot be used for ACK or CANCEL methods");
            return false;
        }

        DsByteString dsMethod = AseSipConstants.getDsByteString(method);
        if(null == dsMethod) {
            logger.error("IllegalArgument - SIP method not supported - [" + method + "]");
            return false;
        }

		return true;
	}

    /**
     * Called by AseSipServletRequest.createRequest()
     * @param nameAddrHdr To uesd for getting the user part
     * @return DsSipContactHeader Created CONTACT header
     */
	public DsSipContactHeader createContactHdr(DsSipNameAddressHeader nameAddrHdr) {
		DsURI uri = nameAddrHdr.getURI();
		DsByteString fromUser = null;
		if (true == uri.isSipURL()) {
			fromUser = ((DsSipURL)uri).getUser();
		}
		else {
			if(uri instanceof DsTelURL) {
				fromUser = ((DsTelURL)uri).getTelephoneSubscriber().
					getPhoneNumber();
			}
			else {
				fromUser = uri.getValue();
			}
		}

		DsSipURL lDsUri = (DsSipURL) m_DummyContactUri.clone();
		lDsUri.setUser(fromUser);

		try{
			return new DsSipContactHeader(lDsUri);
		} catch(DsException e){
			logger.error("createContactHdr(): Exception in creating header", e);
			return null;
		}
	}

	/**
	 * 
	 */
	private void setIPAddress(String address) {
        //bug# BPInd09232
        if(null == address){
            logger.error("setIPAddress(String address): address is null");
            return;
        }
		m_DummyContactUri.setHost(new DsByteString(address));
	}

	/**
	 * 
	 */
	private void setPort(int port) {
		m_DummyContactUri.setPort(port);
	}

	public AseSipSession getSession(String applicationName, String sessionId){
		
		AseHost host = (AseHost)Registry.lookup(Constants.NAME_HOST);
		if(host == null){
			logger.error("Not able to get the host reference");
			return null;
		}
		AseContext context = (AseContext) host.findChild(applicationName);
		if(context == null){
			logger.error("Not able to get the context named :" +applicationName);
			return null;
		}
		
		return (AseSipSession)context.getProtocolSession(sessionId);
	}

	public SipApplicationSession createApplicationSessionByKey(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public AuthInfo createAuthInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	public Parameterable createParameterable(String value)
			throws ServletParseException {
		return this.createAddress(value);
	}
}

