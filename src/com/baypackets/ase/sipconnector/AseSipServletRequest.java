/*
 * AseSipServletRequest.java
 *
 * @author Vishal Sharma
 */

package com.baypackets.ase.sipconnector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.HashMap;
import java.util.Set;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.net.Inet6Address;
import java.net.URLDecoder;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.sip.Address;
import javax.servlet.sip.AuthInfo;
import javax.servlet.sip.B2buaHelper;
import javax.servlet.sip.Parameterable;
import javax.servlet.sip.Proxy;
import javax.servlet.sip.Rel100Exception;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.ServletParseException;
import javax.servlet.sip.SipSession;
import javax.servlet.sip.SipURI;
import javax.servlet.sip.TooManyHopsException;
import javax.servlet.sip.URI;
import javax.servlet.sip.ar.SipApplicationRoutingDirective;
import javax.servlet.sip.ar.SipApplicationRoutingRegion;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;
import org.apache.log4j.Logger;
import com.baypackets.ase.security.SasAuthInfoBean;
import com.baypackets.ase.security.SasAuthInfoImpl;
import com.baypackets.ase.security.SasAuthenticationInfo;
import com.baypackets.ase.security.SasSecurityException;
import com.baypackets.ase.security.SasSecurityManager;
import com.baypackets.ase.sipconnector.AseNsepMessageHandler;
import com.baypackets.ase.container.AseApplicationSession;
import com.baypackets.ase.container.AseBaseRequest;
import com.baypackets.ase.container.AseContext;
import com.baypackets.ase.container.AseEngine;
import com.baypackets.ase.container.AseProtocolSession;
import com.baypackets.ase.container.AseThreadData;
import com.baypackets.ase.latency.AseLatencyData;
import com.baypackets.ase.latency.AseLatencyData.ComponentTimes;
import com.baypackets.ase.measurement.AseMeasurementUtil;
import com.baypackets.ase.ocm.TimeMeasurement;
import com.baypackets.ase.replication.ReplicatedMessageHolder;
import com.baypackets.ase.spi.container.SasMessage;
import com.baypackets.ase.spi.container.SasProtocolSession;
import com.baypackets.ase.common.Registry;
import com.baypackets.bayprocessor.slee.common.BaseContext;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;

import com.baypackets.ase.util.AseStrings;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.util.AseUtils;
import com.dynamicsoft.DsLibs.DsSipLlApi.DsSipClientTransaction;
import com.dynamicsoft.DsLibs.DsSipLlApi.DsSipClientTransactionInterface;
import com.dynamicsoft.DsLibs.DsSipLlApi.DsSipServerTransaction;
import com.dynamicsoft.DsLibs.DsSipLlApi.DsSipServerTransactionIImpl;
import com.dynamicsoft.DsLibs.DsSipLlApi.DsSipTransactionParams;
import com.dynamicsoft.DsLibs.DsSipObject.DsByteString;
import com.dynamicsoft.DsLibs.DsSipObject.DsParameter;
import com.dynamicsoft.DsLibs.DsSipObject.DsParameters;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipAuthenticateHeaderBase;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipAuthorizationHeaderBase;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipChallengeInfo;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipConstants;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipContactHeader;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipContentTypeHeader;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipCredentialsInfo;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipEventHeader;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipFromHeader;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipHeader;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipHeaderInterface;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipHeaderList;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipMessage;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipProxyAuthenticateHeader;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipRequest;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipResponse;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipRouteHeader;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipSubscriptionStateHeader;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipTag;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipToHeader;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipURL;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipWWWAuthenticateHeader;
import com.dynamicsoft.DsLibs.DsSipObject.DsTelURL;
import com.dynamicsoft.DsLibs.DsSipObject.DsURI;
import com.dynamicsoft.DsLibs.DsSipParser.DsSipParserException;
import com.dynamicsoft.DsLibs.DsSipParser.DsSipParserListenerException;
import com.dynamicsoft.DsLibs.DsSipParser.DsSipFrameStream;
import com.dynamicsoft.DsLibs.DsUtil.DsEvent;
import com.dynamicsoft.DsLibs.DsUtil.DsException;
import com.dynamicsoft.DsLibs.DsUtil.DsBindingInfo;
import com.dynamicsoft.DsLibs.DsSipParser.DsSipParserException;
import com.dynamicsoft.DsLibs.DsSipParser.DsSipParserListenerException;
import com.dynamicsoft.DsLibs.DsSipLlApi.DsSipServerTransaction;
import com.dynamicsoft.DsLibs.DsUtil.DsSSLBindingInfo;

import com.baypackets.ase.ari.AriSipServletRequest;

/**
 * This class represents SIP requests.
 */

@DefaultSerializer(ExternalizableSerializer.class)
public class AseSipServletRequest extends AseSipServletMessage 
implements AriSipServletRequest, AseBaseRequest, TimeMeasurement, Cloneable
{
	private static final long serialVersionUID = -384885847114974604L;
	private static final String ATTR_REQUEST_TLS_CERTIFICATE = "javax.servlet.request.X509Certificate";

	// this field stores last route popped by the container if Route header
	// points to container's own IP or fqdn
	private Address poppedRoute;

	private transient Object msResult;
	
	public Object getMsResult() {
		return msResult;
	}

	public void setMsResult(Object msResult) {
		this.msResult = msResult;
	}

	// this field stores initial route popped by the container if Route header
	// points to container's own IP or fqdn
	private Address initialPoppedRoute;

	private SipApplicationRoutingDirective routingDirective = SipApplicationRoutingDirective.NEW;
	
	private static ConfigRepository config = (ConfigRepository) BaseContext.getConfigRepository();
	
	private static String transReplication = (String) config.getValue(Constants.TRANS_REPLICATION);
	
	private boolean transReplicated = false;
	
	private boolean readTransaction = false;
	
	private int messageId = 0;

	// -- Interface SipServletRequest methods --

	/**
	 * Returns the request URI of this request.
	 *
	 * @return request URI of this request.
	 */
	public URI getRequestURI()
	{

		if (m_l.isDebugEnabled()) {
			m_l.debug("getRequestURI() called.");
		}
		// DsSipRequest.getURI() would be invoked.
		DsURI dsUri = m_request.getURI();
		URI uri = null;

		if (dsUri.isSipURL())
			uri = new AseSipURIImpl((DsSipURL)(dsUri));
		else
		{
			DsByteString dStr= new DsByteString(dsUri.getScheme());
			if((dStr.toString()).equals("tel"))
				uri = new AseTelURLImpl((DsTelURL)(dsUri));
			else
			{
				//throw new ServletParseException( " Unsupported URL type ");
				uri = new AseURIImpl(dsUri);
			}
		}

		return uri;
	}

	/**
	 * Sets the request URI of the request. This becomes the destination
	 * used in a subsequent invocation of send().
	 *
	 * @param uri request URI.
	 */
	public void setRequestURI (URI uri)
	{
		if (m_l.isDebugEnabled()) {
			m_l.debug("setRequestURI (URI) called.");
		}
		// DsSipRequest.setURI(DsURI) would be invoked.

		// create DsURI from supplied URI
		DsURI dsUri = ((AseURIImpl) uri).getDsURI();

		m_request.setURI(dsUri);
	}

	/**
	 * Adds a Route header field value to the request. This value is added
	 * ahead of any existing Route header field values.
	 *
	 * @param uri route URI.
	 */

	public void pushRoute (SipURI uri){
		this.pushRoute(uri, true);
	}

	protected void pushRoute (SipURI uri, boolean checkMutable)
	{
		if (m_l.isDebugEnabled()) {
			m_l.debug("pushRoute (SipURI) called.");
		}
		// SipURI.toString() would be used to obtain uriString. Then,
		// DsSipMessage.addHeader(DsSipContants.ROUTE, uriString) would be
		// invoked.

		if (m_l.isInfoEnabled())
			m_l.info("Logging Call-ID .. " + getCallId());

		// this method should throw exception in case the ROUTE header cannot
		// be mutated
		if (checkMutable && !isMutable())
		{
			if (m_l.isInfoEnabled())
				m_l.info("Message is immutable.");

			return;
		}

		// null check
		if (null == uri)
		{
			m_l.error("Null URI specified.");
			return;
		}

		// This needs to be enclosed in angle brackets,
		// otherwise lr is treated as header parameter
		// rather than URI parameter
		String    strUri = "<" + ((AseURIImpl) uri).toString() + ";lr>";

		addHeaderWithoutCheck("ROUTE", strUri, true, checkMutable);
	}

	/**
	 * Adds a Route header field value to the request. This value is added
	 * ahead of any existing Route header field values. This is used by the
	 * SIP connector for adding a route header for application chaining, or
	 * Out-bound SIP entity configured
	 *
	 * @param address IP Address to be added in the URI
	 * @param port Port number to be added in the URI
	 * @param cookie Cookie string for application chaining
	 */
	protected void pushRoute(String address, int port, String cookie) {

		if (m_l.isDebugEnabled()) {
			m_l.debug( "Entering pushRoute(String, int, String)");
		}
		URI uri = getRequestURI();

		if(true == uri.isSipURI()) {
			SipURI route = (SipURI)uri.clone();

			Iterator it = route.getParameterNames();
			if(null != it) {
				while(it.hasNext()) {
					route.removeParameter((String)it.next());
				}
			}

			route.setHost(address);
			route.setPort(port);
			route.setLrParam(true);

			pushRoute(route, true);
		}
		else {
			if (m_l.isDebugEnabled()) {
				m_l.debug("Request URI is not a SIP URI !! \n Leaving pushRoute(String, int, String)");
				//m_l.debug( "Leaving pushRoute(String, int, String)");
			}
		}
	}

	public void pushRouteInternal (SipURI uri){
		this.pushRoute(uri, false);
	}


	/**
	 * Returns the value of Max-Forwards header.
	 *
	 * @return the value of MAX-FORWARDS header.
	 */
	public int getMaxForwards()
	{
		if (m_l.isDebugEnabled()) {
			m_l.debug("getMaxForwards() called.");
		}
		// Max-Forwards is mandatory - no null string check required here
		return Integer.parseInt(getHeader(DsSipConstants.MAX_FORWARDS));
	}

	/**
	 * Sets the value of Max-Forwards header.
	 *
	 * @param n MAX-FORWARDS value.
	 *
	 * @throws IllegalArgumentException if the specified value is not in
	 *   (0,255) range.
	 */
	public void setMaxForwards (int n)
	throws IllegalArgumentException
	{
		if (m_l.isDebugEnabled()) {
			m_l.debug("setMaxForwards (int) called.");
		}
		// java.lang.IllegalArgumentException would be thrown in case the
		// argument is not in (0,255) range.
		// DsSipMessage.setHeader(DsSipContants.MAX_FORWARDS, String.valueOf(n))
		// would be used.

		if ((n < 0) || (n > 255))
		{
			m_l.error("Value out of permitted range.");

			throw new IllegalArgumentException
			("setMaxForwards - Absurd value:" + n);
		}

		// need to throw IllegalStateException too

		try
		{
			setHeader("MAX-FORWARDS", String.valueOf(n));
		}
		catch (IllegalStateException illex)
		{
			m_l.error("Not in a state to set MAX-FORWARDS header.", illex);
		}
		catch (Exception ex)
		{
			m_l.error("Could not set MAX-FORWARDS header.", ex);
		}
	}

	/**
	 * Sends out the request. This would be used by UACs only.
	 *
	 * @throws IOException if an IO error occurs.
	 * @throws IllegalStateException if the underlying SIP state does not
	 *   allow sending of the message.
	 */
	public void send()
	throws IOException, IllegalStateException
	{
		if (m_l.isDebugEnabled()) {
			m_l.debug("send() enter.");
		}
		//ipv6 start 
		if(m_connector.checkConnectorIPType()){
			if(this.getSession0().getOutboundInterface() == null){

				if(m_connector.getIpv6Address() != null){
					AseAddressImpl addressImpl = (AseAddressImpl)this.getTo();
					AseSipURIImpl aseSipURIImpl = (AseSipURIImpl)addressImpl.getURI();
					InetAddress inetAddress = InetAddress.getByName(aseSipURIImpl.getHost());

					if(inetAddress !=null){
						if(inetAddress instanceof Inet6Address){
							if (m_l.isDebugEnabled()) {
								m_l.debug("In send() method of SipServletRequest : Before setting outbound for IPv6");
							}
							this.getSession0().setOutboundInterface(InetAddress.getByName(m_connector.getIpv6Address()));

						}
					}
				}
			}
		}
        // end
		AseLatencyData.ThreadLocalLatencyContainer.handleBeginOut(ComponentTimes.RETURN, this);

		boolean changeLock = AseThreadData.setIcLock(m_sipSession);

		try {
			// CHECK: Proxies cannot invoke this method.
			// Transport errors would result in java.io.IOException being thrown.
			// (dummy if/else to be used.)
			// java.lang.IllegalStateException is returned if the message can not
			// be legally sent in the current state of the underlying SIP
			// transaction.
			// It marks/updates the message as pending for transmission till it is
			// actually sent on the network or chained it to another session so as
			// to handle the race conditions and validations e.g. servlet invoking
			// send() multiple times. (isMutable would be set to FALSE to restrict
			// modifications to the sent message - this would be used in
			// conjunction with isProxy flag of the SipSession to determine whether
			// any changes can be made to the message at all after send() is
			// invoked). SipSession.handleRequest() would be invoked. In
			// SipSession, all validations pertaining to dialog state and SIP
			// session state are performed. CSeq is given (otherwise a servlet may
			// create a SipServletMessage and then discard it, resulting in the
			// pre-alloted CSeq to be lost). SIP session state-validations check
			// whether this message can be send in the current state of SIP session.
			// This is detailed in another section. isCommitted flag is also
			// updated (this is done is SipSession).

			// not taking care of Proxy right now, so no isProxy() related logic
			// in here; just going ahead with isMutable check to prevent multiple
			// sends

			//Throw IllegalStateException if the associated Session Object is NULL.
			super.checkSessionState();

			DsSipRequest dsRequest = getDsRequest();

			if (m_l.isDebugEnabled()) {
				m_l.debug("CSEQ = " + dsRequest.getCSeqNumber());
			}

			if(m_sipSession.getRole() == AseSipSession.ROLE_PROXY) {
				throw new IllegalStateException("Cannot invoke send on proxy request");
			}

			if (isCommitted()) {
				m_l.error("Request already committed");
				throw new IllegalStateException("Committed Request");
			}
			if (!isMutable()) {
				m_l.error("Message is immutable.");

				throw new IllegalStateException("send - Already sent or a received message.");
			}

			boolean alwaysTrue = true;

			// If this is a PRACK validate the presence of RACK header
			if (DsSipConstants.PRACK == getDsRequest().getMethodID()) {
				DsSipHeader rAckHeader = null;
				try {
					rAckHeader = getDsRequest().
					getHeaderValidate(DsSipConstants.RACK);
				} catch (Exception e) {
					m_l.error("Cannot parse RAck Header " + e.toString());
					throw new IllegalStateException("Cannot parse RACH header");
				}

				if (null == rAckHeader) {
					m_l.error("No RACK header in PRACK");
					throw new IllegalStateException("No RACK header in PRACK");
				}
			}

			// If request is a SUBSCRIBE validate the presence of Event header
			if (DsSipConstants.SUBSCRIBE == getDsRequest().getMethodID()) {
				DsSipHeader eventHeader = null;
				try {
					eventHeader = getDsRequest(). getHeaderValidate(DsSipConstants.EVENT);
				} catch (Exception e) {
					m_l.error("Cannot parse Event Header " + e.toString());
					throw new IllegalStateException("Cannot parse EVENT header");
				}

				if (null == eventHeader) {
					m_l.error("No Event Header in SUBSCRIBE ");
					throw new IllegalStateException("No EVENT header in SUBSCRIBE");
				}
			}

			// If request is a REFER validate the presence of Refer-To header
			if (DsSipConstants.REFER == getDsRequest().getMethodID()) {
				DsSipHeader referHeader = null;
				try {
					referHeader = getDsRequest().getHeaderValidate(DsSipConstants.REFER_TO);
				} catch (Exception e) {
					m_l.error("Cannot parse Refer-To Header " + e.toString());
					throw new IllegalStateException("Cannot parse Refer-To header");
				}
				if (null == referHeader) {
					m_l.error("No Refer-To Header in REFER ");
					throw new IllegalStateException("No Refer-To header in REFER");
				}
			}

			// If request is a NOTIFY validate the presence of Event header
			// And a subscription-state header
			if(DsSipConstants.NOTIFY == getDsRequest().getMethodID()) {
				// EVENT header
				DsSipHeader eventHeader = null;
				try {
					eventHeader = getDsRequest().getHeaderValidate(DsSipConstants.EVENT);
				} catch (Exception e) {
					m_l.error("Cannot parse Event Header " + e.toString());
					throw new IllegalStateException("Cannot parse EVENT header");
				}

				if(null == eventHeader) {
					m_l.error("No Event Header in NOTIFY");
					throw new IllegalStateException("No EVENT header in NOTIFY");
				}

				DsSipHeader sStateHeader = null;
				try {
					sStateHeader = getDsRequest().getHeaderValidate(DsSipConstants.SUBSCRIPTION_STATE);
				} catch(Exception e) {
					m_l.error("Cannot parse Subscription-State Header " + e.toString());
					throw new IllegalStateException("Cannot parse Subscription-State header");
				}

				if(null == sStateHeader) {
					m_l.error("No Subscription-State Header in NOTIFY");
					throw new IllegalStateException("No Subscription=State header in NOTIFY");
				}
			}

			if (alwaysTrue)
			{
				// Set the 100rel status
				set100relStatus();
				/*
				 * check for presence of Resource-Priority header
				 * and set priority flag of DsSipMessage
				 * Update priority flag of application session
				 */
				boolean pMsg = false;
				if(AseUtils.getCallPrioritySupport() == 1)  {
					AseApplicationSession appSession = (AseApplicationSession)getApplicationSession();
					DsSipMessage dsSipMessage = getDsMessage();
					pMsg = AseNsepMessageHandler.getMessagePriority(this);
					if(pMsg)    {
						if(m_l.isDebugEnabled())
							m_l.debug("Resource-Priority header present in outgoing Request"
									+" treat it as priority message");
						//set message priority to true
						dsSipMessage.setMessagePriority(true);
						appSession.setPriorityStatus(true);

					}else   {
						if(m_l.isDebugEnabled())
							m_l.debug("No Resource-Priority header in outgoing Request"
									+" treat it as normal message");
						//set message priority to false
						dsSipMessage.setMessagePriority(false);
						appSession.setPriorityStatus(false);


					}
				}

				//
				// Diagnostic Logging
				//
				AseSipDiagnosticsLogger diag = AseSipDiagnosticsLogger.getInstance();
				if (diag.isAppMsgLoggingEnabled()) {
					if (diag.dumpRequest(this.getMethod())) {
						diag.log("REQ2SAS:" + this.getMethod() + AseStrings.COLON + this.getCallId() + AseStrings.COLON + m_sipSession.getId());
					}
				}

				try {
					m_sipSession.sendRequest(this);
				} catch (AseSipSessionException e) {
					m_l.error("Exception in sendRequest", e);
					throw new IllegalStateException(e.toString());
				}

				setImmutable();
			} else {
				// eyewash!
				throw new IOException ("send - Beware Harry Potter!");
			}
		} finally {
			AseThreadData.resetIcLock(m_sipSession, changeLock);
		}

		AseLatencyData.ThreadLocalLatencyContainer.handleEndOut(ComponentTimes.RETURN, this);

		if (m_l.isDebugEnabled()) {
			m_l.debug("send() exit.");
		}
	}

	/**
	 * Determines if dialog can be initiated with this request.
	 *
	 * @return <code>true</code> if dialog can be initiated with this request.
	 */
	public boolean canInitiateDialog()
	{
		if (m_l.isDebugEnabled()) {
			m_l.debug("canInitiateDialog() called.");
		}

		if (DsSipConstants.INVITE == getDsRequest().getMethodID() ||
				DsSipConstants.REFER == getDsRequest().getMethodID() ||
				DsSipConstants.SUBSCRIBE == getDsRequest().getMethodID()) {

			if (m_l.isDebugEnabled()) {
				m_l.debug("canInitiateDialog(): return TRUE");
			}

			return true;
		}

		if (m_l.isDebugEnabled()) {
			m_l.debug("canInitiateDialog(): return FALSE");
		}

		return false;
	}

	/**
	 * Determines if a dialog can be created by this request
	 */
	public boolean canCreateDialog() {
		if (m_l.isDebugEnabled()) {
			m_l.debug("canCreateDialog() called.");
		}
		if (DsSipConstants.NOTIFY == getDsRequest().getMethodID()) {
			// Get subscription state
			// Return true if it is "active"
			DsSipSubscriptionStateHeader ssHdr = null;
			try {
				ssHdr = (DsSipSubscriptionStateHeader)getDsRequest().
				getHeaderValidate(DsSipConstants.SUBSCRIPTION_STATE);
			} catch(DsSipParserException exp) {
				m_l.error("Parsing Subscription-State header in NOTIFY", exp);
			} catch(DsSipParserListenerException exp) {
				m_l.error("Parsing Subscription-State header in NOTIFY", exp);
			}

			if(ssHdr.getState().toString().equals("active")) {
				if (m_l.isDebugEnabled()) {
					m_l.debug("NOTIFY request with Subscription-State as active. Return TRUE");
				}
				return true;
			} else {
				if(m_l.isDebugEnabled())
					m_l.debug("NOTIFY request with Subscription-State as " +
							ssHdr.getState().toString() + ". Return FALSE");
				return false;
			}
		}

		if (m_l.isDebugEnabled())
			m_l.debug("Not a NOTIFY request. Return FALSE");
		return false;
	}

	/**
	 * Determines if a dialog can be terminated by this request
	 */
	public boolean canTerminateDialog() {
		if (m_l.isDebugEnabled())
			m_l.debug("canTerminateDialog called.");

		if(DsSipConstants.NOTIFY == getDsRequest().getMethodID()) {
			// Get subscription state
			// Return true if it is "terminated"
			DsSipSubscriptionStateHeader ssHdr = null;
			try {
				ssHdr = (DsSipSubscriptionStateHeader)getDsRequest().
				getHeaderValidate(DsSipConstants.SUBSCRIPTION_STATE);
			} catch(DsSipParserException exp) {
				m_l.error("Parsing Subscription-State header in NOTIFY", exp);
			} catch(DsSipParserListenerException exp) {
				m_l.error("Parsing Subscription-State header in NOTIFY", exp);
			}

			if(ssHdr.getState().toString().equals("terminated"))  {
				if (m_l.isDebugEnabled())
					m_l.debug("NOTIFY request with Subscription-State as terminated. Return TRUE");
				return true;
			} else {
				if(m_l.isDebugEnabled())
					m_l.debug("NOTIFY request with Subscription-State as " +
							ssHdr.getState().toString() + ". Return FALSE");
				return false;
			}
		} else if(DsSipConstants.BYE == getDsRequest().getMethodID()) {
			if (m_l.isDebugEnabled())
				m_l.debug("BYE request. Return TRUE");
			return true;
		}

		if (m_l.isDebugEnabled())
			m_l.debug("Not a NOTIFY or BYE request. Return FALSE");
		return false;
	}

	/**
	 * Determines if this request can reset the remote Target
	 */
	public boolean canResetRemoteTarget() {
		if (m_l.isDebugEnabled())
			m_l.debug("canResetRemoteTarget called.");

		if (DsSipConstants.NOTIFY == getDsRequest().getMethodID() ||
				DsSipConstants.INVITE == getDsRequest().getMethodID() ||
				DsSipConstants.SUBSCRIBE == getDsRequest().getMethodID() ||
				DsSipConstants.REFER == getDsRequest().getMethodID() ||
				DsSipConstants.UPDATE == getDsRequest().getMethodID()) {
			if (m_l.isDebugEnabled())
				m_l.debug("canResetRemoteTarget Return TRUE");
			return true;
		}

		if (m_l.isDebugEnabled())
			m_l.debug("canResetRemoteTarget Return FALSE");
		return false;
	}

	/**
	 * Determines if this request can modify CSEQ number
	 */
	public boolean canModifySequenceNumber() {
		if (m_l.isDebugEnabled())
			m_l.debug("canModifySequenceNumber called.");

		if (DsSipConstants.CANCEL == getDsRequest().getMethodID() ||
				DsSipConstants.ACK == getDsRequest().getMethodID()) {
			m_l.debug("canModifySequenceNumber Return FALSE");
			return false;
		}

		if (m_l.isDebugEnabled())
			m_l.debug("canModifySequenceNumber Return TRUE");
		return true;
	}

	/**
	 * Set the fact that this is a ACK for a non-2XX final response
	 *
	 */
	public void setNon2XXAck() {
		m_isNon2XXAck = true;
	}

	/**
	 * Is this a non 2XX ACK
	 */
	public boolean isNon2XXAck() {
		return m_isNon2XXAck;
	}

	/**
	 * Sets initial request flag to true.
	 * This flag would be set by SIL for received SIP requests. For
	 * requests created by Servlets, SipFactory would set this flag to
	 * <code>true</code>, depending upon the type of request.
	 */
	public void setInitial()
	{
		if (m_l.isDebugEnabled())
			m_l.debug("setInitial() called.");

		m_isInitial = true;
		super.setInitial(this.m_isInitial);
	}

	/**
	 * Returns <code>true</code> if the message is an initial message.
	 *
	 * @return true if message is an initial message.
	 */
	public boolean isInitial()
	{
		if (m_l.isDebugEnabled())
			m_l.debug("isInitial() called.");

		// Would return the value of isInitial flag. This flag would be set
		// by SIL for received SIP requests. For messages created by
		// Servlets, SipFactory would set this flag to TRUE, depending upon
		// the type of request.

		return m_isInitial;
	}

	/**
	 * Returns ServletInputStream. Returns <code>null</code> for SIP requests.
	 *
	 * @return the ServletInputStream; null if it is SIP request.
	 */
	public ServletInputStream getInputStream()
	{
		if (m_l.isDebugEnabled())
			m_l.debug("GetInputStream() called.");

		// Always returns NULL.
		return null;
	}

	/**
	 * Returns BufferedReader. Returns <code>null</code> for SIP requests.
	 *
	 * @return BufferedReader; null if it is SIP request.
	 */
	public BufferedReader getReader()
	{
		if (m_l.isDebugEnabled())
			m_l.debug("getReader() called.");

		// Always returns NULL.
		return null;
	}

	/**
	 * Returns the Proxy objects associated with the request. A Proxy instance
	 * would be created if it did not already exist.
	 *
	 * @return proxy object associated with the request.
	 *
	 * @throws TooManyHopsException if MAX-FORWARDS is 0
	 * @throws IllegalStateException if Proxy object does not exist, or the
	 * underlying transaction state doesn't allow proxying.
	 */
	public Proxy getProxy()
	throws TooManyHopsException, IllegalStateException {

		if (m_l.isDebugEnabled())
			m_l.debug("getProxy() called.");

		if (m_proxy == null) {
			getProxy(true);
		}

		return m_proxy;
	}

	/**
	 * Returns the Proxy object associated with the request. A Proxy instance
	 * would be created if it did not already exist and if the <code>create
	 * </code> argument is <code>true</code>. <code>null</code> would be
	 * returned for the case where the <code>create</code> flag is
	 * <code>false</code> and
	 * there is no existing Proxy object for the request.
	 *
	 * @param create boolean value indicating whether proxy should be created
	 * if not present already.
	 *
	 * @return proxy object associated with the request.
	 *
	 * @throws TooManyHopsException if MAX-FORWARDS is 0
	 * @throws IllegalStateException if Proxy object does not exist, or the
	 * underlying transaction state doesn't allow proxying.
	 */
	public Proxy getProxy (boolean create)
	throws TooManyHopsException, IllegalStateException {

		if (m_l.isDebugEnabled())
			m_l.debug("getProxy (boolean) enter.");

		// if create flag is false or m_proxy is not null, return here.
		if (!create || (m_proxy != null)) {
			if (m_l.isDebugEnabled())
				m_l.debug("getProxy (boolean) exit.");
			return m_proxy;
		}

		// change by PRASHANT
		// Throw exception if this is not an initial request
		if (!m_isInitial) {
			throw new IllegalStateException("No Proxy creation allowed on " +
			"non-initial request");
		}

		//BpInd17838
		if(m_request.getMethodID()==DsSipConstants.NOTIFY)
		{
			throw new IllegalStateException("Proxy not supported for Initial Notify");
		}
		// TooManyHopsException would be thrown in case the Max-Forwards
		// header field value is 0.
		// IllegalStateException would be thrown in case Proxy object did not
		// exist already and the underlying transaction state doesn't allow
		// proxying.

		// If this is servlet generated request, Proxy cannot be created for it.
		if(AseSipConstants.SRC_NETWORK != m_source) {
			throw new IllegalStateException(
					"Cannot create Proxy for servlet created request");
		}

		// If transaction is in COMPLETED or TERMINATED state, Proxy object
		// cannot be created
		if(null != m_serverTxn) {
			if(m_serverTxn.getState() == DsSipClientTransaction.DS_COMPLETED
					|| m_serverTxn.getState() == DsSipClientTransaction.DS_TERMINATED
					|| m_serverTxn.getState() == DsSipClientTransaction.DS_CONFIRMED
					|| m_serverTxn.getState() == DsSipClientTransaction.DS_XCOMPLETED
					|| m_serverTxn.getState() == DsSipClientTransaction.DS_XTERMINATED
					|| m_serverTxn.getState() == DsSipClientTransaction.DS_XCONFIRMED) {
				throw new IllegalStateException(
						"Illegal transaction state for Proxy creation");
			}
		}

		// If final response for this request is already generated (i.e. if
		// request is committed, Proxy cannnot be created
		if(m_isCommitted == true) {
			throw new IllegalStateException(
					"Cannot create Proxy as final response already created.");
		}


		//Throw IllegalStateException if the associated Session Object is NULL.
		super.checkSessionState();

		// Validate the Max-Forwards Header
		try {
			AseSipMaxForwardsHeaderHandler.validateMaxForwards(this);
		}
		catch (Exception e) {
			m_l.error("Exception during Max-Forwards validation", e);
			throw new javax.servlet.sip.TooManyHopsException();
		}

		// Set session's role to PROXY
		m_sipSession.setRole(AseSipSession.ROLE_PROXY);

		// Remove top Route header, if present and indicates SAS
		AseSipRouteHeaderHandler.stripTopSelfRoute(this);

		// Create the PROXY object
		m_proxy = new AseProxyImpl(m_sipSession.getOrigRequest(),
				this, m_connector);

		// Use the "sequential-search-timeout" value configured
		// for this application if one is specified.
		AseApplicationSession appSession =
			(AseApplicationSession)m_sipSession.getApplicationSession();
		AseContext context = appSession.getContext();
		int timeout = context.getSequentialSearchTimeout();
		if (timeout > 0) {
			if (m_l.isDebugEnabled()) {
				m_l.debug("getProxy(): Using application configured " +
						"value for sequential search timeout of proxy: " +
						timeout + " seconds");
			}

			m_proxy.setSequentialSearchTimeout(timeout);
		}
		else {

			if (m_l.isDebugEnabled()) {
				m_l.debug("getProxy(): No sequential search timeout value for proxy specified by application.");
			}
		}

		// Set proxy reference into SIP session
		m_sipSession.setProxy(m_proxy);
		// set the local party and remote party addresses
		m_sipSession.setLocalParty( getFrom() );
		m_sipSession.setRemoteParty( getTo() );
		m_sipSession.resetPrCount();

		// Set server transaction's proxy server mode to true
		if (m_serverTxn != null) {
			m_serverTxn.setProxyServerMode(true);
		} else if(m_pServerTxn != null) {
			m_pServerTxn.setProxyServerMode(true);
		} else {
			throw new IllegalStateException("Server transaction is null");
		}

		// If INVITE, send 100 Trying, if not already sent
		if((m_request.getMethodID() == DsSipConstants.INVITE) &&
				!m_responded ) {
			m_responded = true;
			try {
				createResponse(100).send();
			} catch(Exception exp) {
				m_l.error("Exception Sending 100 in getProxy()", exp);
				throw new IllegalStateException("Exception sending 100 Trying");
			}
		}

		// Implicit CONTINUE on Proxy.
		routingDirective = SipApplicationRoutingDirective.CONTINUE;

		if (m_l.isDebugEnabled())
			m_l.debug("getProxy (true) exit.");

		return m_proxy;
	}

	/**
	 * Returns a CANCEL request object. This is to be used by UACs only;
	 * Proxies need to call Proxy.cancel() to cancel outstanding transactions.
	 *
	 * @return CANCEL request.
	 *
	 * @throws IllegalStateException if the state of the underlying SIP
	 * transaction does not allow CANCEL at this stage.
	 */
	public SipServletRequest createCancel()
	throws IllegalStateException
	{
		if (m_l.isDebugEnabled())
			m_l.debug("createCancel() called.");

		// java.lang.IllegalStateException is thrown in case the state of the
		// underlying SIP transaction does not allow CANCEL at this stage.
		// Constructor DsSipCancelMessage(DsSipRequest) would be used.
		// Cancelled SIP request would also be stored in the resulting
		// AseSipServletMessage.

		// First need to generate DsSipCancelMessage and then invoke one
		// of constructors of AseSipServletRequest specially designed for
		// CANCEL (which takes in client transaction in addition to connector
		// and CANCEL message). This would be followed by setCancelledMessage
		// to set the cancelled SIP request (this).

		//Throw IllegalStateException if the associated Session Object is NULL.
		super.checkSessionState();

		if(m_sipSession.getRole() == AseSipSession.ROLE_PROXY) {
			throw new IllegalStateException("Cannot create CANCEL for proxy request");
		}

		// If this is an incoming request, CANCEL cannot be created for it.
		if(AseSipConstants.SRC_NETWORK == m_source) {
			throw new IllegalStateException("Cannot create CANCEL for incoming request");
		}

		// If client transaction is in COMPLETED or TERMINATED state, CANCEL
		// cannot be created
		if(null != m_clientTxn) {
			if(m_clientTxn.getState() == DsSipClientTransaction.DS_COMPLETED
					|| m_clientTxn.getState() == DsSipClientTransaction.DS_TERMINATED
					|| m_clientTxn.getState() == DsSipClientTransaction.DS_XCOMPLETED
					|| m_clientTxn.getState() == DsSipClientTransaction.DS_XTERMINATED) {
				throw new IllegalStateException(
						"Illegal transaction state for CANCEL creation");
			}
		}

		AseSipServletRequest cancel =
			( (AseConnectorSipFactory)m_connector.getFactory() ).createCancel( this);

		return (SipServletRequest) cancel;
	}

	/**
	 * Creates a response for this request with the specified status code.
	 *
	 * @param statusCode status code.
	 *
	 * @return SIP response message.
	 *
	 * @throws IllegalArgumentException if specified status code is invalid.
	 * @throws IllegalStateException if the request has already been responded
	 * to with a final status code.
	 */
	public SipServletResponse createResponse (int statusCode)
	throws IllegalArgumentException, IllegalStateException
	{
		if (m_l.isDebugEnabled())
			m_l.debug("createResponse (int) called." );

		// java.lang.IllegalArgumentException is returned for invalid
		// statusCode values.
		// java.lang.IllegalStateException would be thrown in case the request
		// has already been responded to with a final status code.
		// (isCommited ??)
		// Constructor DsSipResponse(statusCode, DsSipRequest) would be used.

		if ((100 > statusCode) || (700 <= statusCode))
		{
			m_l.error("Status code (" + statusCode + ") out of range.");

			throw new IllegalArgumentException ("Status code out of range.");
		}

		if (m_isCommitted == true)
		{
			m_l.error("Cannot create response for a committed request.");

			throw new IllegalStateException("No new responses can be created.");
		}

		//Throw IllegalStateException if the associated Session Object is NULL.
		super.checkSessionState();

		AseSipServletResponse response = ((AseConnectorSipFactory)m_connector.
				getFactory()).createResponse(this, statusCode, null);

		// Commit the request if created response is final
		if(statusCode >= 200) {
			m_isCommitted = true;
		}

		return response;
	}

	/**
	 * Creates a response for this request with the specified status code and
	 * the specified reason phrase.
	 *
	 * @param statusCode status code.
	 * @param reasonPhrase reason phrase.
	 *
	 * @return SIP response message.
	 *
	 * @throws IllegalArgumentException if specified status code is invalid.
	 * @throws IllegalStateException if the request has already been responded
	 * to with a final status code.
	 */
	public SipServletResponse createResponse (int  statusCode,
			String reasonPhrase)
	throws IllegalArgumentException, IllegalStateException
	{
		if (m_l.isDebugEnabled())
			m_l.debug("createResponse (int, String) called." );

		// java.lang.IllegalArgumentException is returned for invalid
		// statusCode values.
		// java.lang.IllegalStateException would be thrown in case the request
		// has already been responded to with a final status code.
		// (isCommited ??)
		// Contructor DsSipResponse(statusCode, DsSipRequest) would be used,
		// followed by DsSipResponse.setReasonPhrase(reasonPhrase).

		if ((100 > statusCode) || (700 <= statusCode))
		{
			m_l.error("Status code (" + statusCode + ") out of range");

			throw new IllegalArgumentException ("Status code out of range.");
		}

		if (m_isCommitted == true)
		{
			m_l.error("Cannot create response for a committed request");

			throw new IllegalStateException ("No new responses can be created");
		}

		//Throw IllegalStateException if the associated Session Object is NULL.
		super.checkSessionState();

		AseSipServletResponse response = ((AseConnectorSipFactory)m_connector.
				getFactory()).createResponse(this, statusCode, reasonPhrase);

		if(reasonPhrase != null && (reasonPhrase.compareToIgnoreCase ( "Request not handled by app" ) == 0 ) &&  AseUtils.getCallPrioritySupport() == 1)      {
			if(((AseApplicationSession)this.getApplicationSession()).getPriorityStatus())    {
				String rphValue = this.getHeader(Constants.RPH);
				if(rphValue != null){
					response.setHeader(Constants.RPH,rphValue);
				}
			}
		}

		// Commit the request if created response is final
		if(statusCode >= 200) {
			m_isCommitted = true;
		}

		return response;
	}


	// -- Implementations of abstract method of javax.servlet.ServletRequest --

	public String getRealPath (String path)
	{
		if (m_l.isDebugEnabled())
			m_l.debug("getRealPath(String path) called.");
		SipSession sipSession  = this.getSession(false);
		if(sipSession != null){
			if(sipSession.getServletContext() != null){
				return sipSession.getServletContext().getRealPath(path);
			}
		}
		return null;
	}

	public RequestDispatcher getRequestDispatcher (String path)
	{
		// returning null as per JSR 289.This is same as ServletContext#getRequestDispatcher method 
		// except this method can take relative path.
		return null;
	}

	public Locale getLocale()
	{
		if (m_l.isDebugEnabled())
			m_l.debug("getLocale() called.");
		Locale locale = getAcceptLanguage();
		if(locale == null){
			return locale = Locale.getDefault();
		}
		return locale ;
	}

	public Enumeration getLocales()
	{
		if (m_l.isDebugEnabled())
			m_l.debug("getLocales()called.");
		Iterator iterator = getAcceptLanguages();
		ArrayList arrayList = new ArrayList();
		if(iterator.hasNext() == false){
			arrayList.add(Locale.getDefault());
			return Collections.enumeration(arrayList);
		}

		while(iterator.hasNext()){
			arrayList.add(iterator.next());
		}
		return Collections.enumeration(arrayList);
	}

	public String getLocalName() {
		if (m_l.isDebugEnabled())
			m_l.debug("getLocalName()called.");

		if (AseSipConstants.SRC_NETWORK == m_source) 
		{
			DsBindingInfo info = m_message.getBindingInfo();
			return info.getLocalAddress().getHostName();
		}
		else
		{
			// locally generated
			if (m_l.isInfoEnabled()) m_l.info("Locally generated message.");
			String hostName = null;
			try{
				hostName = InetAddress.getByName(m_connector.getIPAddress()).getHostName();
			}catch (UnknownHostException e) {
				m_l.error("IP address of the host could not be determined"); 
			}

			return m_connector != null ? hostName : null;
		}
	}

	/**
	 * Returns the value of the specified parameter. <code>null</code> is
	 * returned if there is no such parameter.
	 *
	 * @param name parameter name.
	 *
	 * @return parameter value.
	 */
	public String getParameter (String name)
	{
		if (m_l.isDebugEnabled())
			m_l.debug("getParameter(String) called.");
		DsSipURL  dsSipURL = null;
		DsURI sipUri = null ;
		DsParameters parameter = null ;
		if(!this.isInitial())
		{
			try{
				sipUri = m_request.lrFix(null);
				if(sipUri == null )
					return null ;
				if(sipUri.isSipURL())
					dsSipURL = (DsSipURL)sipUri ;
				else
					return null;
			}catch(DsException e) {
				m_l.error("DsException ", e ) ;
			}

			parameter = dsSipURL.getParameters();
		}


		String strVal = null;
		if(this.isInitial())
		{
			if((this.getRequestURI()).isSipURI())
				strVal = ((SipURI)this.getRequestURI()).getParameter(name);
			else
			{
				if (m_l.isDebugEnabled())
					m_l.debug("Not a SipURI so returning nothing");
			}
			return strVal;
		}


		else if((!this.isInitial())&&parameter!=null )
		{

			DsByteString value = parameter.get(name);
			if(value != null)
				strVal=value.toString();

		}
		return strVal;
	}

	/**
	 * Returns the keys of parameter map as an <code>Enumeration</code>.
	 *
	 * @return <code>Enumeration</code> of parameter names.
	 */
	public Enumeration getParameterNames()
	{
		if (m_l.isDebugEnabled())
			m_l.debug("getParameterNames() called.");

		Vector vec=new Vector();
		Iterator itr = null ;
		if(this.isInitial())
		{
			if((this.getRequestURI()).isSipURI())
			{
				itr = ((SipURI)this.getRequestURI()).getParameterNames();
				if (itr  != null)
				{
					while(itr.hasNext())
					{
						vec.add(itr.next());
					}
				}
				else
					m_l.debug("no parameters in requsest");
				return vec.elements();
			}
			else
			{
				if (m_l.isDebugEnabled())
					m_l.debug("Not a SipURI so returning nothing");
			}


		}
		else if(!this.isInitial() )
		{
			DsURI sipUri = null ;
			DsSipURL  dsSipURL = null;

			try{
				sipUri = m_request.lrFix(null);
				if(sipUri == null)
					return null;
				if(sipUri.isSipURL())
					dsSipURL = (DsSipURL)sipUri ;
				else
					return null;
			}catch(DsException e ){
				m_l.error(" DsException ", e ) ;
			}

			DsParameters parameter = dsSipURL.getParameters();
			if(parameter == null )
				return null;

			byte delimiter = parameter.getDelimiter();

			byte[] delimit = {delimiter};
			String delim = new String(delimit);
			String parametersAsString = parameter.getValue().toString();
			StringTokenizer t =new  StringTokenizer(parametersAsString, delim) ;
			if (t  != null)
			{
				while(t.hasMoreTokens())
				{
					String token = t.nextToken();
					String param=null;
					int index = token.indexOf(AseStrings.EQUALS);
					if(index >0)
					{
						param = token.substring(0,index).trim();
						vec.add(param);
					}
					else
					{
						param = token.trim();
						if( !( (param.equals(AseStrings.PARAM_LR)) || (param.equals(AseSipConstants.RR_URI_PARAM )) ) )
							vec.add(param);
					}
				}
				return vec.elements();
			}
		}


		return vec.elements();
		//return m_paramMap.keys();
	}

	/**
	 * Returns all the values of the specified parameter. Since only one
	 * value is supported for one parameter either in Request URI or Route
	 * header, the implementation uses <code>getParameter(String)</code>.
	 *
	 * @param name parameter name.
	 *
	 * @return array of parameter values.
	 */
	public String[] getParameterValues (String name)
	{
		if (m_l.isDebugEnabled())
			m_l.debug("getParameterValues(String) called.");

		String[] retVal = { this.getParameter(name) };

		return retVal;
	}

	/**
	 * Returns the parameter map.
	 *
	 * @return parameter map.
	 */
	public Map getParameterMap()
	{
		if (m_l.isDebugEnabled())
			m_l.debug("getParameterMap() called.");

		return m_paramMap;
	}

	public String getRemoteHost()
	{
		String remoteHost = null;
		try{
			DsURI dsURI = this.m_request.getFromHeaderValidate().getURI();
			if(dsURI.isSipURL()){
				remoteHost =((DsSipURL)dsURI).getHost().toString();
			}
		}catch(Exception e){
			m_l.error(e.getMessage(), e);
		}
		return remoteHost;
	}

	public String getScheme()
	{
		return "sip";
	}

	public String getServerName()
	{
		if (m_l.isDebugEnabled())
			m_l.debug("getServerName called.");
		SipURI uri =  (SipURI)this.getRequestURI();
		if(uri != null){
			String uriHost = uri.getHost();
			if(uriHost.contains(AseStrings.COLON)){
				uriHost =  uriHost.substring(0, uriHost.indexOf(AseStrings.COLON));
				return uriHost;
			}
			return uriHost;
		}
		return null;
	}

	public int getServerPort()
	{
		//return -1;
		SipURI uri =  (SipURI)this.getRequestURI();
		if(uri != null){
			String uriHost = uri.getHost();
			if(uriHost.contains(AseStrings.COLON)){
				uriHost =  uriHost.substring(uriHost.indexOf(AseStrings.COLON)+1);
				return Integer.parseInt(uriHost);
			}
		}
		return -1;
	}

	public void removeAttribute (String name)
	{
		if (m_l.isDebugEnabled())
			m_l.debug("removeAttribute (String) called.");

		// method not specified in SipServlet APIs ...
		// however implementation may be provided using Hashtable.remove(key)
		m_attributeMap.remove(name);
	}

	// -- interface SipServletMessage: overidden method --

	/**
	 * Returns the SipSession to which the message belongs. The session is
	 * created if it did not exist already.
	 *
	 * @return the SIP session to which message belongs.
	 */
	public SipSession getSession()
	{
		if (m_l.isDebugEnabled())
			m_l.debug("getSession() called.");

		// sipSession would be returned.

		if (null == getSession0() && !isReplicated() && m_isInitial)
		{
			m_sipSession =
				((AseConnectorSipFactory)m_connector.getFactory()).createSession();
			
			
			//for tcap calls Active Sip session count will not be mantained
			if(getHeader(Constants.DIALOGUE_ID)!=null){
				AseMeasurementUtil.counterActiveSIPSessions.decrement();
			}
			
			// Set the stored router state in the new session
			m_sipSession.setRouterStateInfo(m_routerState);
			m_sipSession.setRegion(m_routeRegion);
			m_sipSession.setSubscriberURI(m_subscriberURI);

			if(this.getInitialPriorityStatus()) {
				m_ocmManager.increaseNSEP(m_sessionOcmId);
			} else {
				m_ocmManager.increase(m_sessionOcmId);
			}

			if(null != m_serverTxn)
				((AseSipTransaction)m_serverTxn).setSipSession(m_sipSession);

			if(null != m_clientTxn)
				((AseSipTransaction)m_clientTxn).setSipSession(m_sipSession);
		}

		return m_sipSession;
	}

	// -- local methods --
	//added as due to no valid cunstructor error
	public AseSipServletRequest() {
	}


	/**
	 * Constructor used by the AseSipFactory. This method is used to created
	 * new requests which is associated with newly created SIP session. Soucre
	 * is SERVLET for such messages. initialFlag is <code>true</code>. request
	 * (DsRequest) is created and populated using the specified information.
	 *
	 * @param session SIP session.
	 * @param connector SIP connector.
	 * @param method method name.
	 * @param from FROM address
	 * @param to TO address
	 * @param cseq CSEQ number.
	 * @param callID call Id.
	 * @param routeHeaders route set.
	 * @param contactHeader CONTACT header.
	 * @param isInitial boolean value indicating whether the message is a dialog
	 * initiating message.
	 */
	AseSipServletRequest ( AseSipSession           session,
			AseSipConnector         connector,
			DsByteString            method,
			Address                 from,
			Address                 to,
			long                    cseq,
			String                  callID,
			DsSipHeaderList         routeHeaders,
			DsSipHeaderInterface    contactHeader,
			boolean                 isInitial)
			{
		if (m_l.isDebugEnabled())
			m_l.debug("AseSipServletRequest (AseSipSession, AseSipConnector, DsByteString, Address, Address, long, String, DsSipHeaderList, DsSipHeaderInterface, boolean) called.");

		// Added by Vimal to be used in AseConnectorSipFactory...
		// Please see createRequest methods...Aug 26th..04

		// NOTE: not using the passed CSeq value - this should be set when
		// the request is handed over to SipSession for sending.
		// Adding a method setCSeqNumber in the class for this purpose.

		// assuming that Factory would check for null From, To

		// servlet generated SIP request
		m_source = AseSipConstants.SRC_SERVLET;

		// default values
		m_isCommitted = false;
		m_isMutable = true;

		m_cancelledRequest = null;
		m_clientTxn = null;
		m_serverTxn = null;

		m_isInitial = isInitial;
		m_isTargeted = false;

		// supplied values
		m_sipSession = session;
		m_connector = connector;

		// create DsSipRequest ..

		// a. get DsSipFromHeader
		DsSipFromHeader dsFrom = new DsSipFromHeader
		(((AseAddressImpl)from).getDsNameAddressHeader().getNameAddress(),
				((AseAddressImpl)from).getDsNameAddressHeader().getParameters());

		// OR SHOULD WE JUST REMOVE FROM-TAG, IF PRESENT??
		dsFrom = addNewFromTag(dsFrom);

		// b. get DsSipToHeader
		DsSipToHeader dsTo = new DsSipToHeader
		(((AseAddressImpl)to).getDsNameAddressHeader().getNameAddress(),
				((AseAddressImpl)to).getDsNameAddressHeader().getParameters());

		// remove To-tag, if any
		dsTo = removeToTag(dsTo);

		//bug# BPInd09232
		DsByteString dsMethod = null;
		if(null != method){
			dsMethod = new DsByteString(method);
		}else{
			m_l.error("AseSipServletRequest():method is null");
		}
		DsByteString dsCallID = null;
		if(null != callID){
			dsCallID = new DsByteString(callID);
		}else{
			m_l.error("AseSipServletRequest():callID is null");
		}

		DsSipContactHeader ch = null;

		// Do not add Contact header on MESSAGE  and PUBLISH requests
		//BYE,PRACK,OPTIONS and INFO are also exempted as per 
		//RFC 3261 and SBTM UAT 1181
		if(!method.equalsIgnoreCase(AseStrings.MESSAGE)&&
				!method.equalsIgnoreCase(AseStrings.PUBLISH) &&
				!method.equalsIgnoreCase(AseStrings.BYE)&&
				!method.equalsIgnoreCase(AseStrings.PRACK)&&
				!method.equalsIgnoreCase(AseStrings.OPTIONS)&&
				!method.equalsIgnoreCase(AseStrings.INFO)) {
			if(m_l.isDebugEnabled()) m_l.debug("GOT IT");
			ch = (DsSipContactHeader)contactHeader;
		}

		// CSeq is provisionally set as 0
		// not using the specified cseq here
		long tmpCseq = 0;
		m_request = new DsSipRequest (dsMethod, dsFrom, dsTo, ch,
				dsCallID, tmpCseq, null, null);

		// add specified ROUTE headers
		if (null != routeHeaders)
		{
			// adding the specified headers as the first headers
			m_request.addHeaders(routeHeaders, true, true);
		}

		// store the request as m_message too
		m_message = (DsSipMessage) m_request;
			}

	/**
	 * Constructor used by the AseSipFactory. This method is used to created
	 * new requests which is associated with an existing SIP session. Soucre
	 * is SERVLET for such messages. initialFlag is <code>true</code>. request
	 * (DsRequest) is created and populated using the specified information.
	 *
	 * @param session SIP session.
	 * @param connector SIP connector.
	 * @param method method name.
	 * @param from FROM value.
	 * @param to TO value.
	 * @param cseq CSEQ number.
	 * @param callID call Id.
	 * @param routeHeaders route set.
	 * @param contactHeader CONTACT header.
	 * @param isInitial Whether this is a dialog initiating message
	 */
	AseSipServletRequest (  AseSipSession          session,
			AseSipConnector            connector,
			DsByteString           method,
			DsSipFromHeader            from,
			DsSipToHeader          to,
			long                   cseq,
			String                 callID,
			DsSipHeaderList            routeHeaders,
			DsSipHeaderInterface   contactHeader,
			boolean                    isInitial)
			{
		if (m_l.isDebugEnabled())
			m_l.debug("AseSipServletRequest (AseSipSession, AseSipConnector, DsByteString, DsSipFromHeader, DsSipToHeader, long, String, DsSipHeaderList, DsSipHeaderInterface) called.");

		// Added by Vimal to be used in AseConnectorSipFactory...
		// Please see createRequest methods...Aug 26th..04

		// NOTE: not using the passed CSeq value - this should be set when
		// the request is handed over to SipSession for sending.
		// Adding a method setCSeqNumber in the class for this purpose.

		// assuming that Factory would check for null From, To

		// servlet generated SIP request
		m_source = AseSipConstants.SRC_SERVLET;

		// default values
		m_attributeMap = new Hashtable();
		m_isCommitted = false;
		m_isMutable = true;

		m_cancelledRequest = null;
		m_clientTxn = null;
		m_serverTxn = null;

		m_isInitial = isInitial;
		m_isTargeted = false;

		// supplied values
		m_sipSession = session;
		m_connector = connector;

		// create DsSipRequest ..

		//bug# BPInd09232
		DsByteString dsMethod = null;
		if(null != method){
			dsMethod = new DsByteString(method);
		}else{
			m_l.error("AseSipServletRequest():method is null");
		}
		DsByteString dsCallID = null;
		if(null != callID){
			dsCallID = new DsByteString(callID);
		}else{
			m_l.error("AseSipServletRequest():callID is null");
		}

		DsSipContactHeader ch = null;

		// Do not add Contact header on MESSAGE and PUBLISH requests
		if(!method.equalsIgnoreCase(AseStrings.MESSAGE)
				&& !method.equalsIgnoreCase(AseStrings.PUBLISH)&&
				!method.equalsIgnoreCase(AseStrings.BYE)&&
				!method.equalsIgnoreCase(AseStrings.PRACK)&&
				!method.equalsIgnoreCase(AseStrings.OPTIONS)&&
				!method.equalsIgnoreCase(AseStrings.INFO)) {
			if (m_l.isDebugEnabled()) m_l.debug("GOT IT1");
			ch = (DsSipContactHeader)contactHeader;
		}

		// CSeq is provisionally set as 0
		// not using the specified cseq here
		m_request = new DsSipRequest (dsMethod, from, to, ch, dsCallID, cseq, null, null);

		// add specified ROUTE headers
		if (null != routeHeaders)
		{
			// adding the specified ROUTE headers as the first ROUTE headers
			m_request.addHeaders(routeHeaders, true, true);
		}

		// store the request as m_message too
		m_message = (DsSipMessage) m_request;
			}

	/**
	 * Constructor used by the AseSipFactory. This method is used to created
	 * new requests which is associated with an existing SIP session. Soucre
	 * is SERVLET for such messages. initialFlag is <code>true</code>. request
	 * (DsRequest) is created and populated using the specified information.
	 *
	 * @param session SIP session.
	 * @param connector SIP connector.
	 * @param request stack request object.
	 * @param from FROM value.
	 * @param to TO value.
	 * @param cseq CSEQ number.
	 * @param callID call Id.
	 * @param contactHeader CONTACT header.
	 * @param isInitial Whether this is a dialog initiating message
	 */
	AseSipServletRequest (  AseSipSession          session,
			AseSipConnector        connector,
			DsSipRequest           request,
			DsSipFromHeader        from,
			DsSipToHeader          to,
			long                   cseq,
			String                 callID,
			DsSipHeaderInterface   contactHeader,
			boolean                isInitial)
			{
		if (m_l.isDebugEnabled())
			m_l.debug("AseSipServletRequest (AseSipSession, AseSipConnector, DsSipRequest, DsSipFromHeader, DsSipToHeader, long, String, DsSipHeaderInterface, boolean) called.");

		// servlet generated SIP request
		m_source = AseSipConstants.SRC_SERVLET;

		// default values
		m_attributeMap = new Hashtable();
		m_isCommitted = false;
		m_isMutable = true;

		m_cancelledRequest = null;
		m_clientTxn = null;
		m_serverTxn = null;

		m_isInitial = isInitial;
		m_isTargeted = false;

		// supplied values
		m_sipSession = session;
		m_connector = connector;

		// clone DsSipRequest and clear binding info
		m_request = (DsSipRequest)request.clone();
		try {
			m_request.lrUnescape();
		} catch ( DsSipParserException parseEx ) {
			m_l.error( "exception in parsing" , parseEx ) ;
		} catch ( DsSipParserListenerException ex ) {
			m_l.error( "exception in parsing" , ex ) ;
		}

		m_request.setBindingInfo(new DsBindingInfo());

		if (m_l.isDebugEnabled()) m_l.debug( "In AseSipRequest :  "  +  m_request.getBindingInfo().toString() ) ;

		// Set new From and To headers
		m_request.updateHeader(from, false);
		m_request.updateHeader(to, false);

		// Set new CSeq and Call-Id
		m_request.setCSeqNumber(cseq);
		m_request.setCallId(new DsByteString(callID));

		// Set new Contact header for non-REGISTER request
		if(m_request.getMethodID() != DsSipConstants.REGISTER
				&& contactHeader != null && m_request.getMethodID() != DsSipConstants.PRACK 
				&& m_request.getMethodID() != DsSipConstants.BYE
				&& m_request.getMethodID() != DsSipConstants.OPTIONS 
				&& m_request.getMethodID() != DsSipConstants.INFO) {
			m_request.updateHeader(contactHeader, false);
		}

		// Remove Contact header if copied from orig MESSAGE request
		if(m_request.getMethodID() == DsSipConstants.MESSAGE ||
				m_request.getMethodID() == DsSipConstants.PUBLISH || 
				m_request.getMethodID() == DsSipConstants.PRACK ||
				m_request.getMethodID() == DsSipConstants.BYE ||
				m_request.getMethodID() == DsSipConstants.OPTIONS ||
				m_request.getMethodID() == DsSipConstants.INFO) {
			if (m_l.isDebugEnabled()) m_l.debug("GOT IT2");
			m_request.removeHeader(DsSipConstants.CONTACT);
		}

		// Remove Record-Route and Via headers
		m_request.removeHeaders(DsSipConstants.VIA);
		m_request.removeHeaders(DsSipConstants.RECORD_ROUTE);

		// store the request as m_message too
		m_message = m_request;
			}

	/**
	 * Constructor used by the AseSipFactory. This method is used to created
	 * new requests which is associated with newly created SIP session. Soucre
	 * is SERVLET for such messages. initialFlag is <code>true</code>. request
	 * (DsRequest) is created and populated using the specified information.
	 *
	 * @param session SIP session.
	 * @param connector SIP connector.
	 * @param method method name.
	 * @param from FROM address.
	 * @param to TO address.
	 * @param callID call Id.
	 */
	AseSipServletRequest ( AseSipSession       session,
			AseSipConnector     connector,
			String              method,
			Address             from,
			Address             to,
			String              callID)
			{
		if (m_l.isDebugEnabled())
			m_l.debug("AseSipServletRequest (AseSipSession, AseSipConnector, String, Address, Address, String) called.");

		// assuming that Factory would check for null From, To

		// servlet generated SIP request
		m_source = AseSipConstants.SRC_SERVLET;

		// default values
		m_attributeMap = new Hashtable();
		m_isCommitted = false;
		m_isMutable = true;

		m_cancelledRequest = null;
		m_clientTxn = null;
		m_serverTxn = null;

		m_isInitial = true; // should we check here whether the method is
		// dialog initiating or not !?? SipFactory is
		// the right place to check it before calling
		// the constructor.

		m_isTargeted = false;
		// supplied values
		m_sipSession = session;
		m_connector = connector;

		// create DsSipRequest ..

		// a. get DsSipFromHeader
		DsSipFromHeader dsFrom = new DsSipFromHeader
		(((AseAddressImpl)from).getDsNameAddressHeader().getNameAddress(),
				((AseAddressImpl)from).getDsNameAddressHeader().getParameters());


		// OR SHOULD WE JUST REMOVE FROM-TAG, IF PRESENT??
		dsFrom = addNewFromTag(dsFrom);

		// b. get DsSipToHeader
		DsSipToHeader dsTo = new DsSipToHeader
		(((AseAddressImpl)to).getDsNameAddressHeader().getNameAddress(),
				((AseAddressImpl)to).getDsNameAddressHeader().getParameters());

		// remove To-tag, if any
		dsTo = removeToTag(dsTo);

		// c. create Contact Header
		// get the pre-created String
		DsSipContactHeader dsContact = null;

		if (!method.equalsIgnoreCase(AseStrings.REGISTER) &&
				!method.equalsIgnoreCase(AseStrings.MESSAGE)&&
				!method.equalsIgnoreCase(AseStrings.PUBLISH)&&
				!method.equalsIgnoreCase(AseStrings.BYE)&&
				!method.equalsIgnoreCase(AseStrings.PRACK)&&
				!method.equalsIgnoreCase(AseStrings.OPTIONS)&&
				!method.equalsIgnoreCase(AseStrings.INFO))
		{
			if (m_l.isDebugEnabled()) m_l.debug("GOT IT3");
			dsContact = createContactHeader(dsFrom);
		}

		//bug# BPInd09232
		DsByteString dsMethod = null;
		if(null != method){
			dsMethod = new DsByteString(method);
		}else{
			m_l.error("AseSipServletRequest():method is null");
		}
		DsByteString dsCallID = null;
		if(null != callID){
			dsCallID = new DsByteString(callID);
		}else{
			m_l.error("AseSipServletRequest():callID is null");
		}

		// CSeq is provisionally set as 0
		long cseq = 0;
		m_request = new DsSipRequest (dsMethod, dsFrom, dsTo, dsContact,
				dsCallID, cseq, null, null);

		// store the request as m_message too
		m_message = (DsSipMessage) m_request;
			}

	/**
	 * Constructor used by the AseSipFactory. This method is used to created
	 * new requests which is associated with newly created SIP session. Soucre
	 * is SERVLET for such messages. initialFlag is <code>true</code>. request
	 * (DsRequest) is created and populated using the specified information.
	 *
	 * @param session SIP session.
	 * @param connector SIP connector.
	 * @param method method name.
	 * @param from FROM address.
	 * @param to TO address.
	 * @param callID call Id.
	 */
	AseSipServletRequest ( AseSipSession           session,
			AseSipConnector         connector,
			String                  method,
			URI                     from,
			URI                     to,
			String                  callID)
			{
		if (m_l.isDebugEnabled())
			m_l.debug("AseSipServletRequest (AseSipSession, AseSipConnector, String, URI, URI, String) called.");

		// assuming Factory is taking care of null To, From

		// servlet generated SIP request
		m_source = AseSipConstants.SRC_SERVLET;

		// default values
		m_attributeMap = new Hashtable();
		m_isCommitted = false;
		m_isMutable = true;

		m_cancelledRequest = null;
		m_clientTxn = null;
		m_serverTxn = null;

		m_isInitial = true; // should we check here whether the method is
		// dialog initiating or not !?? SipFactory is
		// the right place to check it before calling
		// the constructor.
		m_isTargeted = false;

		// supplied values
		m_sipSession = session;
		m_connector = connector;

		// create DsSipRequest ..

		// a. get DsSipFromHeader
		DsSipFromHeader dsFrom =
			new DsSipFromHeader(((AseURIImpl)from).getDsURI());

		// OR SHOULD WE JUST REMOVE FROM-TAG, IF PRESENT??
		dsFrom = addNewFromTag(dsFrom);

		// b. get DsSipToHeader
		DsSipToHeader dsTo = new DsSipToHeader(((AseURIImpl)to).getDsURI());

		// remove To-tag, if any
		dsTo = removeToTag(dsTo);

		// c. create Contact Header
		// get the pre-created String
		DsSipContactHeader dsContact = null;

		if (!method.equalsIgnoreCase(AseStrings.REGISTER) &&
				!method.equalsIgnoreCase(AseStrings.MESSAGE)&&
				!method.equalsIgnoreCase(AseStrings.PUBLISH)&&
				!method.equalsIgnoreCase(AseStrings.BYE)&&
				!method.equalsIgnoreCase(AseStrings.PRACK)&&
				!method.equalsIgnoreCase(AseStrings.OPTIONS)&&
				!method.equalsIgnoreCase(AseStrings.INFO))
		{
			if (m_l.isDebugEnabled()) m_l.debug("GOT IT4");
			dsContact = createContactHeader(dsFrom);
		}

		//bug# BPInd09232
		DsByteString dsMethod = null;
		if(null != method){
			dsMethod = new DsByteString(method);
		}else{
			m_l.error("AseSipServletRequest():method is null");
		}
		DsByteString dsCallID = null;
		if(null != callID){
			dsCallID = new DsByteString(callID);
		}else{
			m_l.error("AseSipServletRequest():callID is null");
		}

		// CSeq is provisionally set as 0
		long cseq = 0;
		m_request = new DsSipRequest (dsMethod, dsFrom, dsTo, dsContact,
				dsCallID, cseq, null, null);

		// store the request as m_message too
		m_message = (DsSipMessage) m_request;
			}

	/**
	 * Constructor used by AseSipFactory. This method is used to created new
	 * requests which is associated with newly created SIP session. Soucre is
	 * SERVLET for such messages initialFlag is <code>true</code>. request
	 * (DsRequest) is created and populated using the specified information.
	 *
	 * @param session SIP session.
	 * @param connector SIP connector.
	 * @param method method name.
	 * @param from FROM address.
	 * @param to TO address.
	 * @param callID call Id.
	 */
	AseSipServletRequest ( AseSipSession           session,
			AseSipConnector         connector,
			String                  method,
			String                  from,
			String                  to,
			String                  callID)
			{
		if (m_l.isDebugEnabled())
			m_l.debug("AseSipServletRequest (AseSipSession, AseSipConnector, String, String, String, String) called.");

		// assuming Factory is taking care of null To, From

		// servlet generated SIP request
		m_source = AseSipConstants.SRC_SERVLET;

		// default values
		m_attributeMap = new Hashtable();
		m_isCommitted = false;
		m_isMutable = true;

		m_cancelledRequest = null;
		m_clientTxn = null;
		m_serverTxn = null;

		m_isInitial = true; // should we check here whether the method is
		// dialog initiating or not !?? SipFactory is
		// the right place to check it before calling
		// the constructor.
		m_isTargeted = false;

		// supplied values
		m_sipSession = session;
		m_connector = connector;

		// create DsSipRequest ..


		// a. get DsSipFromHeader
		DsSipFromHeader dsFrom = null;

		try
		{
			//bug# BPInd09232
			if(null != from){
				dsFrom = new DsSipFromHeader(new DsByteString(from));
			}

		}
		catch (Exception ex)
		{
			m_l.error("Can not create FROM header from " + from, ex);
			return;
		}

		// OR SHOULD WE JUST REMOVE FROM-TAG, IF PRESENT??
		dsFrom = addNewFromTag(dsFrom);

		// b. get DsSipToHeader
		DsSipToHeader dsTo = null;

		try
		{
			//bug# BPInd09232
			if(null != to){
				dsTo = new DsSipToHeader(new DsByteString(to));
			}
		}
		catch (Exception ex)
		{
			m_l.error("Can not create TO header from " + to, ex);
			return;
		}

		// remove To-tag, if any
		dsTo = removeToTag(dsTo);

		// get the pre-created String
		DsSipContactHeader dsContact = null;

		if(!method.equalsIgnoreCase(AseStrings.REGISTER) &&
				!method.equalsIgnoreCase(AseStrings.MESSAGE)&&
				!method.equalsIgnoreCase(AseStrings.PUBLISH)&&
				!method.equalsIgnoreCase(AseStrings.BYE)&&
				!method.equalsIgnoreCase(AseStrings.PRACK)&&
				!method.equalsIgnoreCase(AseStrings.OPTIONS)&&
				!method.equalsIgnoreCase(AseStrings.INFO))
		{
			if (m_l.isDebugEnabled()) m_l.debug("GOT IT5");
			dsContact = createContactHeader(dsFrom);
		}

		//bug# BPInd09232
		DsByteString dsMethod = null;
		if(null != method){
			dsMethod = new DsByteString(method);
		}else{
			m_l.error("AseSipServletRequest():method is null");
		}
		DsByteString dsCallID = null;
		if(null != callID){
			dsCallID = new DsByteString(callID);
		}else{
			m_l.error("AseSipServletRequest():callID is null");
		}

		// CSeq is provisionally set as 0
		m_request = new DsSipRequest (dsMethod, dsFrom, dsTo, dsContact,
				dsCallID, 0, null, null);

		// store the request as m_message too
		m_message = (DsSipMessage) m_request;
			}

	/**
	 * Constructor used by AseSipFactory. This method is used to created new
	 * requests which is associated with newly created SIP session. This would
	 * be used for B2BUA applications. Soucre is SERVLET for such messages and
	 * initialFlag is <code>true</code>. request (DsRequest) is created and
	 * populated using the specified information. Same call-ID as the template
	 * request message is used in case the supplied callID argument is
	 * <code>null</code>.
	 *
	 * @param session SIP session.
	 * @param connector SIP connector.
	 * @param origRequest original request.
	 * @param callID call Id.
	 */
	AseSipServletRequest ( AseSipSession           session,
			AseSipConnector         connector,
			AseSipServletRequest    origRequest,
			String                  callID)
			{
		if (m_l.isDebugEnabled())
			m_l.debug("AseSipServletRequest (AseSipSession, AseSipConnector, AseSipServletRequest, String) called.");

		// This method creates a request identical to origRequest with
		//    following exceptions:
		// -  From header field of the new request has a new tag chosen by
		//        the container.
		// -  To header field of the request has no tag.
		// -  Record-Route and Via header fields are not copied. (Via header
		//        field is added when the request is actually sent)
		// -  For non-REGISTER requests, the Contact header field is not
		//        copied but is populated by ASE.
		// -  All other header fields including Route header and unknown
		//        headers are copied from origRequest to new request.

		// servlet generated SIP request
		m_source = AseSipConstants.SRC_SERVLET;

		// default values
		m_attributeMap = new Hashtable();
		m_isCommitted = false;
		m_isMutable = true;

		m_cancelledRequest = null;
		m_clientTxn = null;
		m_serverTxn = null;

		m_isInitial = origRequest.isInitial();
		m_isTargeted = false;
		// supplied values
		m_sipSession = session;
		m_connector = connector;

		// create DsSipRequest ..

		DsSipRequest request =
			(DsSipRequest) origRequest.getDsRequest().clone();
		try {
			m_request.lrUnescape();
		} catch ( DsSipParserException parseEx ) {
			m_l.error( "exception in parsing" , parseEx ) ;
		} catch ( DsSipParserListenerException ex ) {
			m_l.error( "exception in parsing" , ex ) ;
		}
		// add new From-Tag
		DsSipFromHeader fromHeadObj = null;

		try
		{
			fromHeadObj = request.getFromHeaderValidate();
		}
		catch (Exception ex)
		{
			m_l.error("No FROM header!!", ex);
			return;
		}

		fromHeadObj = addNewFromTag(fromHeadObj);

		// delete To-tag
		DsSipToHeader toHeadObj = null;

		try
		{
			toHeadObj = request.getToHeaderValidate();
		}
		catch (Exception ex)
		{
			m_l.error("No TO header!!", ex);
			return;
		}

		toHeadObj = removeToTag(toHeadObj);

		// modify the Contact Header
		if(!origRequest.getMethod().equalsIgnoreCase(AseStrings.REGISTER)
				&& !origRequest.getMethod().equalsIgnoreCase(AseStrings.MESSAGE)
				&& !origRequest.getMethod().equalsIgnoreCase(AseStrings.PUBLISH)&&
				!origRequest.getMethod().equalsIgnoreCase(AseStrings.BYE)&&
				!origRequest.getMethod().equalsIgnoreCase(AseStrings.PRACK)&&
				!origRequest.getMethod().equalsIgnoreCase(AseStrings.OPTIONS)&&
				!origRequest.getMethod().equalsIgnoreCase(AseStrings.INFO))
		{
			if (m_l.isDebugEnabled()) m_l.debug("GOT IT6");
			// create new Contact header
			DsSipContactHeader newContactHdr = createContactHeader(fromHeadObj);
			DsSipHeaderInterface updtHdr = request.updateHeader(newContactHdr);
		}

		if((origRequest.getMethod().equalsIgnoreCase(AseStrings.MESSAGE))||
				(origRequest.getMethod().equalsIgnoreCase(AseStrings.PUBLISH)) || 
				(origRequest.getMethod().equalsIgnoreCase(AseStrings.PRACK)) ||
				(origRequest.getMethod().equalsIgnoreCase(AseStrings.BYE)) ||
				(origRequest.getMethod().equalsIgnoreCase(AseStrings.OPTIONS)) ||
				(origRequest.getMethod().equalsIgnoreCase(AseStrings.INFO))) {
			if (m_l.isDebugEnabled()) m_l.debug("GOT IT7");
			request.removeHeader(DsSipConstants.CONTACT);
		}

		// remove the Record-Route Header
		DsByteString recrouteHdr = new DsByteString(AseStrings.HDR_RECORD_ROUTE);
		if(true == request.hasHeaders(recrouteHdr))
		{
			DsSipHeaderInterface removeHdr = request.removeHeader(recrouteHdr);
		}

		// remove the Via Header
		DsByteString viaHdr = new DsByteString(AseStrings.HDR_VIA);
		if(true == request.hasHeaders(viaHdr))
		{
			DsSipHeaderInterface removeHdr = request.removeHeader(viaHdr);
		}

		m_request = request;

		// store the request as m_message too
		m_message = (DsSipMessage) m_request;
			}

	/**
	 * Constructor used by AseSipFactory. This method is used to create
	 * subsequent requests for an already established SIP dialog. From and To
	 * headers are used from the original SIP request according to the
	 * specified role of the SIP session w.r.t. the original request. (If the
	 * role is UAS, From and To headers are used as To and From headers,
	 * respectively, for creating the new request. For UAC, From and To are
	 * used as new request's From and To, respectively.) This role would be
	 * inferenced from the source of the request (NETWORK/ASE mean incoming
	 * requests i.e. UAS; SERVLET means outgoing requests i.e. UAC). Soucre is
	 * SERVLET for such messages. initialFlag is <code>false</code>.
	 *
	 * @param session SIP session.
	 * @param connector SIP connector.
	 * @param method method name.
	 * @param origRequest original request.
	 */
	AseSipServletRequest ( AseSipSession           session,
			AseSipConnector         connector,
			String                  method,
			AseSipServletRequest    origRequest)
			{
		if (m_l.isDebugEnabled())
			m_l.debug("AseSipServletRequest (AseSipSession, AseSipConnector, String, AseSipServletRequest) called.");

		// need to take care about the use of getValue (and not toByteString)
		// since a COPY of the original header is needed for making the
		// From-To switch.

		// servlet generated SIP request
		m_source = AseSipConstants.SRC_SERVLET;

		// default values
		m_attributeMap = new Hashtable();
		m_isCommitted = false;
		m_isMutable = true;

		m_cancelledRequest = null;
		m_clientTxn = null;
		m_serverTxn = null;

		m_isInitial = false;
		m_isTargeted = false;

		// supplied values
		m_sipSession = session;
		m_connector = connector;

		// create DsSipRequest ..

		// a. From and To headers

		DsByteString origFrom =
			origRequest.getDsMessage().getFromHeader().getValue();

		DsByteString origTo =
			origRequest.getDsMessage().getToHeader().getValue();

		DsSipFromHeader fromHdr = null;
		DsSipToHeader toHdr = null;

		if(AseSipConstants.SRC_SERVLET == origRequest.getSource()) {
			try {
				// From and To in same order as original request
				fromHdr = new DsSipFromHeader(origFrom);
				toHdr = new DsSipToHeader(origTo);
			} catch (Exception ex) {
				m_l.error("Creating From/To header", ex);
			}
		} else {
			try {
				// From and To in opposite order as original request
				fromHdr = new DsSipFromHeader(origTo);
				toHdr = new DsSipToHeader(origFrom);
			} catch (Exception ex) {
				m_l.error("Creating From/To header", ex);
			}
		}

		// b. Contact header
		// hoping that SIP session has the proper object
		DsSipContactHeader contactHdr = null;
		if((!method.equalsIgnoreCase(AseStrings.MESSAGE))&&
				(!method.equalsIgnoreCase(AseStrings.PUBLISH))&&
				(!method.equalsIgnoreCase(AseStrings.BYE))&&
				(!method.equalsIgnoreCase(AseStrings.PRACK))&&
				(!method.equalsIgnoreCase(AseStrings.OPTIONS))&&
				(!method.equalsIgnoreCase(AseStrings.INFO))) {
			if (m_l.isDebugEnabled()) m_l.debug("GOT IT8");
			contactHdr = (DsSipContactHeader) m_sipSession.getLocalTarget();
		}

		// c. Call-ID
		DsByteString callId = origRequest.getDsMessage().getCallId();

		// d. C-Seq : temporarily 0 (ZERO)
		int cseq = 0;
		//bug# BPInd09232
		if(null != method){
			m_request = new DsSipRequest( new DsByteString(method), fromHdr,
					toHdr, contactHdr, callId, cseq, null, null);
		}else{
			m_l.error("AseSipServletRequest():method is null");
		}

		// e. set ROUTE headers
		DsSipHeaderList routeSet = m_sipSession.getRouteSet();
		if (null != routeSet)
		{
			// adding the specified ROUTE headers as the first ROUTE headers
			m_request.addHeaders(routeSet, true, true);
		}

		// store the request as m_message too
		m_message = (DsSipMessage) m_request;
			}

	/**
	 * Constructor used by AseSipFactory. This method is used to create ASE
	 * SIP request objects for SIP requests received by the stack over the
	 * network. Source is NETWORK. initialFlag is <code>false</code>.
	 * (initialFlag would be updated by SIL, if needed.). m_serverTxn is also
	 * set.
	 *
	 * @param connector SIP connector.
	 * @param request DS SIP request object.
	 * @param serverTxn DS SIP server transaction.
	 */
	AseSipServletRequest ( AseSipConnector         connector,
			DsSipRequest            request,
			DsSipServerTransaction  serverTxn)
			{
		if (m_l.isDebugEnabled())
			m_l.debug("AseSipServletRequest (AseSipConnector, DsSipRequest, DsSipServerTransaction)");

		m_source  = AseSipConstants.SRC_NETWORK;

		// BPInd10481
		try {
			DsSipContentTypeHeader contentType = request.getContentTypeHeaderValidate();
			if (contentType != null) {
				DsByteString encoding = contentType.getParameter(AseStrings.PARAM_CHARSET);
				if (encoding != null) {
					m_enc = encoding.toString();
				}
			}

		}
		catch(DsSipParserException exp) {
			m_l.info("Exception caught " + exp);
		}
		catch(DsSipParserListenerException exp) {
			if (m_l.isInfoEnabled()) m_l.info("Exception caught " + exp);
		}
		// default values
		m_attributeMap = new Hashtable();
		m_isCommitted = false;
		m_isMutable = false;
		m_cancelledRequest = null;
		m_clientTxn = null;
		m_sipSession = null;  // don't know yet

		m_isInitial = false;
		m_isTargeted = false;

		// supplied values
		m_serverTxn = serverTxn;
		m_connector = connector;

		m_message = (DsSipMessage)request;
		m_request = request;

		//m_l.info("The message is: \n"+m_request.getRequestURI().toString());

		// Sets the 100rel status flag
		set100relStatus();

		if(m_message.getBindingInfo() instanceof DsSSLBindingInfo) {
			m_isSecure = true;

			DsSSLBindingInfo sslBInfo = (DsSSLBindingInfo)m_message.getBindingInfo();
			if(sslBInfo != null) {
				try {
					this.setAttribute(ATTR_REQUEST_TLS_CERTIFICATE, sslBInfo.getPeerCertificateChain());
				} catch(javax.net.ssl.SSLPeerUnverifiedException exp) {
					m_l.error("AseSipServletRequest(): getting TLS certificate", exp);
				}
			}
			else
				if (m_l.isInfoEnabled()) m_l.info("Binding Info is null");
		}
		
	}

	/**
	 * Constructor used for creating CANCEL and ACK requests. Source is SERVLET.
	 * initialFlag is <code>false</code>. m_clientTxn is also set.
	 *
	 * @param session SIP session.
	 * @param connector SIP connector.
	 * @param request DS SIP request.
	 * @param clientTxn DS SIP client transaction.
	 */
	AseSipServletRequest ( AseSipSession           session,
			AseSipConnector         connector,
			DsSipRequest            request,
			DsSipClientTransaction  clientTxn)
			{
		if (m_l.isDebugEnabled())
			m_l.debug("AseSipServletRequest (AseSipSession, AseSipConnector, DsSipRequest, DsSipClientTransaction) called.");

		m_source  = AseSipConstants.SRC_SERVLET;

		// default values
		m_attributeMap = new Hashtable();
		m_isCommitted = false;
		m_isMutable = true;
		m_cancelledRequest = null;
		m_serverTxn = null;

		m_isInitial = false;
		m_isTargeted = false;

		// supplied values
		m_clientTxn = clientTxn;
		m_sipSession = session;
		m_connector = connector;

		m_message = (DsSipMessage)request;
		m_request = request;
		// BPInd10481
		try {
			DsSipContentTypeHeader contentType = request.getContentTypeHeaderValidate();
			if (contentType != null) {
				DsByteString encoding = contentType.getParameter(AseStrings.PARAM_CHARSET);
				if (encoding != null) {
					m_enc = encoding.toString();
				}
			}

		}
		catch(DsSipParserException exp) {
			if (m_l.isInfoEnabled()) m_l.info("Exception caught " + exp);
		}
		catch(DsSipParserListenerException exp) {
			if (m_l.isInfoEnabled()) m_l.info("Exception caught " + exp);
		}
			}

	/**
	 * Copy constructor. A deep copy of the request would be made which is
	 * then modified according to specified requirements.
	 *
	 * @param template template request.
	 */
	AseSipServletRequest ( AseSipServletRequest    template)
	{
		if (m_l.isDebugEnabled())
			m_l.debug("AseSipServletRequest (AseSipServletRequest) called.");

		// servlet generated SIP request
		m_source = AseSipConstants.SRC_SERVLET;

		// default values

		// DO WE NEED TO COPY THE HASHTABLE ENTRIES TOO??
		// IF SO A NEW API WOULD BE REQUIRED TO GET ATTRIBUTE MAP REF.
		m_attributeMap = new Hashtable();

		// copying rest of attributes. HOW CORRECT IS THIS??
		// the new request might be born untouchable!??
		m_isCommitted = template.isCommitted();
		m_isMutable = template.isMutable();

		m_cancelledRequest = template.getCancelledRequest();

		m_clientTxn = template.getClientTxn();
		m_serverTxn = template.getServerTxn();

		m_isInitial = template.isInitial();
		m_isTargeted = template.isTargeted();

		// supplied values
		m_sipSession = template.getAseSipSession();

		// a new method getSipConnector has been added to AseSipServletMessage
		// only because of this
		m_connector = template.getSipConnector();

		// create DsSipRequest ..

		m_request = (DsSipRequest) template.getDsRequest().clone();

		// Copy the 100rel status flag
		m_relStatus = template.m_relStatus;

		// store the request as m_message too
		m_message = (DsSipMessage) m_request;

		m_enc = getCharacterEncoding();
	}

	/**
	 * Constructor used by AseSipFactory. This method is used to create ASP SIP
	 * request objects for SIP requests received by the stack over the stray
	 * interface from the network. Source is NETWORK. initialFlag is
	 * <code>false</code>.
	 *
	 * @param connector SIP connector.
	 * @param request DS SIP request.
	 */
	AseSipServletRequest ( AseSipConnector         connector,
			DsSipRequest            request)
			{
		if (m_l.isDebugEnabled())
			m_l.debug("AseSipServletRequest (AseSipConnector, DsSipRequest) called.");

		m_source  = AseSipConstants.SRC_NETWORK;

		// default values
		m_attributeMap = new Hashtable();
		m_isCommitted = false;
		m_isMutable = false;
		m_cancelledRequest = null;
		m_clientTxn = null;
		m_serverTxn = null;
		m_sipSession = null;

		m_isInitial = false;
		m_isTargeted = false;

		// supplied values
		m_connector = connector;
		// BPInd10481
		try {
			DsSipContentTypeHeader contentType = request.getContentTypeHeaderValidate();
			if (contentType != null) {
				DsByteString encoding = contentType.getParameter(AseStrings.PARAM_CHARSET);
				if (encoding != null) {
					m_enc = encoding.toString();
				}
			}

		}
		catch(DsSipParserException exp) {
			if (m_l.isInfoEnabled()) m_l.info("Exception caught " + exp);
		}
		catch(DsSipParserListenerException exp) {
			if (m_l.isInfoEnabled()) m_l.info("Exception caught " + exp);
		}


		m_message = (DsSipMessage)request;
		m_request = request;

		// Set the status of the m_relStatus
		set100relStatus();

		if(m_message.getBindingInfo() instanceof DsSSLBindingInfo) {
			m_isSecure = true;

			DsSSLBindingInfo sslBInfo = (DsSSLBindingInfo)m_message.getBindingInfo();
			if(sslBInfo != null) {
				try {
					this.setAttribute(ATTR_REQUEST_TLS_CERTIFICATE, sslBInfo.getPeerCertificateChain());
				} catch(javax.net.ssl.SSLPeerUnverifiedException exp) {
					m_l.error("AseSipServletRequest(): getting TLS certificate", exp);
				}
			}
		}
			}

	//FT Handling strategy Update: Replication will be done for the provisional
	//responses as well, so in order to activate requests on the stand by server
	//storing the requests in the application session. This is done to replicate 
	//the server transaction and its associated connection object.
	//Server transaction will be present in the INVITE request coming from the 
	//network while client transaction gets created at SIL while sending the 
	//INVITE request to the network
	/**
	 * Adds the request to the message map maintained by the AseApplication Session
	 */
	public void addToApplicationSession (){
		messageId = this.assignMessageId();
		((AseApplicationSession)this.getApplicationSession()).addSipServletMessage(messageId,this);
	}
	
	/**
	 * Checks the request for 100rel in Require/Supported header, and sets the
	 * <code>relStatus</code> accordingly. <code>relStatus</code> is
	 * REL_REQUIRED
	 * in case there is Require header with 100rel option; it is REL_SUPPORETD
	 * in case 100rel option is present in Supported header.
	 */
	void set100relStatus() {

		if (m_l.isDebugEnabled())
			m_l.debug( "Entering AseSipServletRequest set100relStatus");

		// If not an INVITE request do nothing
		if (DsSipConstants.INVITE != getDsRequest().getMethodID()) {
			if (m_l.isDebugEnabled())
				m_l.debug( "NON INVITE request. Nothing to do");
		}

		DsSipRequest request = m_request;

		// first check REQUIRE ...
		DsSipHeaderInterface reqHdr =
			request.getHeader(DsSipConstants.REQUIRE);

		if (null != reqHdr) {
			int indx = reqHdr.getValue().toLowerCase().
			indexOf(AseSipServletRequest.DS_100REL);

			if (m_l.isInfoEnabled())
				m_l.info("100rel's index in Require header is: " + indx);

			if (-1 != indx) { //100rel is present
				m_relStatus = REL_REQUIRED;
				if (m_l.isDebugEnabled()){
					m_l.debug("100rel = REL_REQUIRED, Leaving set100relStatus.");
				}
				return;
			}
		}

		// Now check supported
		DsSipHeaderInterface suppHdr =
			request.getHeader(DsSipConstants.SUPPORTED);

		if (null != suppHdr && suppHdr.getValue()!=null) {
			int indx = suppHdr.getValue().toLowerCase().
			indexOf(AseSipServletRequest.DS_100REL);

			if (m_l.isInfoEnabled())
				m_l.info("100rel's index in Supported header is: " + indx);

			if (-1 != indx) { //100rel is present
				m_relStatus = REL_SUPPORTED;

				if (m_l.isDebugEnabled()){
					m_l.debug("100rel = REL_SUPPORTED, Leaving set100relStatus.");
				}
			}
		}

		if (m_l.isDebugEnabled()){
			m_l.debug("100rel = REL_NOT_SUPPORTED");
			m_l.debug( "Leaving set100relStatus.");
		}
	}

	/**
	 * Sets the cancelled request. This would be invoked inside the invocation
	 * of SipServletRequest.createCancel().
	 *
	 * @param request cancelled request.
	 */
	void setCancelledRequest (AseSipServletRequest request)
	{
		if (m_l.isDebugEnabled())
			m_l.debug("setCancelledRequest (AseSipServletRequest) called.");

		m_cancelledRequest = request;
	}

	/**
	 * Gets the cancelled request, or <code>null</code> is there is none
	 * associated.
	 *
	 * @return the cancelled request; <code>null</code> if there is none.
	 */
	AseSipServletRequest getCancelledRequest()
	{
		if (m_l.isDebugEnabled())
			m_l.debug("getCancelledRequest () called.");

		return m_cancelledRequest;
	}

	/**
	 * Mark this request as cancelled
	 */
	void cancelled() {
		m_cancelled = true;
	}

	/**
	 * Is this request cancelled
	 */
	boolean isCancelled() {
		return m_cancelled;
	}


	/**
	 * Returns the DsSipRequest associated with the ASE SipServlet Request.
	 *
	 * @return the associated DS SIP request.
	 */
	DsSipRequest getDsRequest()
	{
		if (m_l.isDebugEnabled())
			m_l.debug("getDsRequest() called.");

		return m_request;
	}

	/**
	 * Sets the client transaction associated with the request.
	 *
	 * @param txn DS SIP client transaction.
	 */
	void setClientTxn(DsSipClientTransaction txn)
	{
		if (m_l.isDebugEnabled())
			m_l.debug("setClientTxn(DsSipClientTransaction) called.");

		m_clientTxn = txn;
	}

	/**
	 * Returns the client transaction associated with the request.
	 *
	 * @return the client transaction associated with the request.
	 */
	DsSipClientTransaction getClientTxn()
	{
		if (m_l.isDebugEnabled())
			m_l.debug("getClientTxn() called.");

		if (m_clientTxn == null) {
			//bug# BPInd09232
			if(DsSipConstants.CANCEL == getDsRequest().getMethodID()){
				if (m_l.isInfoEnabled())
					m_l.info("Taking transaction for CANCEL from orig request");

				if(m_cancelledRequest != null) { //bug# BPInd18392
					m_clientTxn = m_cancelledRequest.m_clientTxn;
				}

				if (m_clientTxn == null) {
					m_l.error("No associated transaction with this request");
				}
			}
		}

		return m_clientTxn;
	}

	/**
	 * Returns the server transaction associated with the request.
	 *
	 * @return the server transaction associated with the request.
	 */
	DsSipServerTransaction getServerTxn()
	{
		if (m_l.isDebugEnabled())
			m_l.debug("getServerTxn() called.");

		return m_serverTxn;
	}

	void clearStackTxn()
	{
		if (m_l.isDebugEnabled())
			m_l.debug("clearStackTxn() called");

		super.clearStackTxn();

		m_cancelledRequest = null;
		m_cancelled = false;

		m_serverTxn = null;
		m_clientTxn = null;
		m_pClientTxn = null;
		m_pServerTxn = null;

		m_subscription = null;
		m_referencedSubscription = null;
		m_paramMap = new Hashtable();

		m_responded = false;
		m_isInProgress = false;
		timestamp = -1;
	}

	void setPseudoClientTxn(AsePseudoSipClientTxn txn)
	{
		if (m_l.isDebugEnabled())
			m_l.debug("setPseudoClientTxn(AsePseudoSipClientTxn) called.");
		m_pClientTxn = txn;
	}

	AsePseudoSipClientTxn getPseudoClientTxn()
	{
		if (m_l.isDebugEnabled())
			m_l.debug("getPseudoClientTxn() called.");
		return m_pClientTxn;
	}

	void setPseudoServerTxn(AsePseudoSipServerTxn txn)
	{
		if (m_l.isDebugEnabled())
			m_l.debug("setPseudoServerTxn(AsePseudoSipServerTxn) called.");
		m_pServerTxn = txn;
	}

	AsePseudoSipServerTxn getPseudoServerTxn()
	{
		if (m_l.isDebugEnabled())
			m_l.debug("getPseudoServerTxn() called.");
		return m_pServerTxn;
	}

	/**
	 * Returns whether the CONTACT header is mutable; implementation of abstract
	 * method of base class.
	 *
	 * @return <code>true</code> if the CONTACT header is mutable;
	 *   <code>false</code> otherwise.
	 */
	boolean canMutateContactHeader()
	{
		if (m_l.isDebugEnabled())
			m_l.debug("canMutateContactHeader() called.");
		//bug# BPInd09232
		if(DsSipConstants.REGISTER == getDsRequest().getMethodID()){
			return true;
		}
		else
			return false;
	}

	/**
	 * Sets the CSeq number in the request.
	 *
	 * @param cseq CSEQ number.
	 */
	void setCSeqNumber (long cseq)
	{
		if (m_l.isDebugEnabled())
			m_l.debug("setCSeqNumber (long) called.");

		// CSeq number cannot be set in immutable messages.

		if (isMutable())
		{
			m_request.setCSeqNumber(cseq);
		}
		else
		{
			m_l.error("Message is immutable.");
		}
	}

	/**
	 * Adds new From-tag in supplied DsSipFromHeader.
	 *
	 * @param dsFrom DS FROM header.
	 *
	 * @return the new From Header after tag addition
	 */
	DsSipFromHeader addNewFromTag (DsSipFromHeader dsFrom)
	{
		if (m_l.isDebugEnabled())
			m_l.debug("addNewFromTag (DsSipFromHeader) called.");

		if(true == dsFrom.isTagPresent())
		{
			// Generate a new tag
			DsSipTag tag = new DsSipTag();
			DsByteString newFromTag = tag.generateTag();

			if (m_l.isInfoEnabled())
				m_l.info("New From-tag is :" + newFromTag);

			// set this tag in the From Header
			dsFrom.setTag(newFromTag);
		}

		return dsFrom;
	}

	/**
	 * Removes From-tag from supplied DsSipFromHeader.
	 *
	 * @param dsFrom DS FROM header.
	 *
	 * @return the new From Header after tag removal
	 */
	DsSipFromHeader removeFromTag (DsSipFromHeader dsFrom)
	{
		if (m_l.isDebugEnabled())
			m_l.debug("removeFromTag (DsSipFromHeader) called.");

		if(true == dsFrom.isTagPresent())
		{
			dsFrom.removeTag();
		}

		return dsFrom;
	}

	/**
	 * Removes To-tag from supplied DsSipToHeader.
	 *
	 * @param dsTo DS TO header.
	 *
	 * @return the new to header after tag removal
	 */
	DsSipToHeader removeToTag (DsSipToHeader dsTo)
	{
		if (m_l.isDebugEnabled())
			m_l.debug("removeToTag (DsSipToHeader) called.");

		if(true == dsTo.isTagPresent())
		{
			dsTo.removeTag();
		}

		return dsTo;
	}

	/**
	 * Creates CONTACT header.
	 *
	 * @param from DS FROM header.
	 *
	 * @return the created CONTACT header.
	 */
	DsSipContactHeader createContactHeader(DsSipFromHeader from)
	{
		if (m_l.isDebugEnabled())
			m_l.debug("createContactHeader (DsSipFromHeader) called.");

		DsByteString dsDisplayName = from.getNameAddress().getDisplayName();

		String displayName = null;

		if (null != dsDisplayName)
		{
			displayName = dsDisplayName.toString();
		}
		else
		{
			displayName = new String("unknown");
		}

		String hostName = m_connector.getIPAddress();

		int port = m_connector.getPort();

		DsByteString contactStr = new DsByteString("sip:" + displayName +
				AseStrings.AT + hostName + AseStrings.COLON + port);

		DsSipContactHeader contact = null;
		try
		{
			DsSipURL contactURL = new DsSipURL(contactStr);
			contact = new DsSipContactHeader(contactURL);
		}
		catch (Exception ex)
		{
			m_l.error("Unable to generate ASE CONTACT header.", ex);
			return null;
		}

		return contact;
	}

	/**
	 * Get the 100rel support status of this Request
	 */
	int getRelStatus() {
		return m_relStatus;
	}

	/**
	 * Get the subscription for this request
	 * If the request is other than SUBSCRIBE/REFER/NOTIFY then this
	 * returns null
	 * The boolean singleInDialog is used only in the case of REFER
	 */
	AseSipSubscription getSubscription(boolean singleInDialog) {
		if (m_l.isDebugEnabled()) m_l.debug("Entering AseSipServletRequest getSubscription");

		if(m_l.isDebugEnabled())
			m_l.debug("singleInDialog = [" + singleInDialog + "]");

		// If a subscription exists return it
		if (null != m_subscription) {
			if (m_l.isDebugEnabled())
				m_l.debug("Leaving AseSipServletRequest getSubscription: exists");
			return m_subscription;
		}

		// If message is of type SUBSCRIBE
		if (DsSipConstants.SUBSCRIBE == getDsRequest().getMethodID()) {
			if (m_l.isDebugEnabled())
				m_l.debug("SUBSCRIBE request");

			// Retrieve the DsSipEventHeader
			DsSipHeader eventHeader = null;
			try {
				eventHeader = getDsRequest().
				getHeaderValidate(DsSipConstants.EVENT);
			}
			catch (Exception e) {
				m_l.error("Cannot parse Event Header " + e.toString());
				if (m_l.isDebugEnabled()) m_l.debug("Leaving AseSipServletRequest getSubscription: null");
				return null;
			}
			if (null == eventHeader) {
				m_l.error("No Event Header in SUBSCRIBE ");
				if (m_l.isDebugEnabled()) m_l.debug("Leaving AseSipServletRequest getSubscription: null");
				return null;
			}

			// Work with the ID parameter
			DsByteString id = ((DsSipEventHeader)eventHeader).getID();
			if (null == id)
				id = AseSipSubscription.DS_ZERO;

			m_subscription =
				new AseSipSubscription(DsSipConstants.SUBSCRIBE,
						getDsRequest().getCallId(),
						getDsRequest().getFromTag(),
						((DsSipEventHeader)eventHeader).
						getFullPackageName(),
						id, null);
			if (m_l.isDebugEnabled()) {
				m_l.debug("Leaving AseSipServletRequest getSubscription" +
						m_subscription.toString() );
			}
			return m_subscription;
		}

		// If message is of type REFER
		if (DsSipConstants.REFER == getDsRequest().getMethodID()) {
			if (m_l.isDebugEnabled())
				m_l.debug("REFER request");

			// Work with the ID parameter


			DsByteString id =
				new DsByteString("" + getDsRequest().getCSeqNumber());
			DsByteString referencedId = null;

			if (true == singleInDialog)
				referencedId = AseSipSubscription.DS_ZERO;
			else
				referencedId = null;

			m_subscription =
				new AseSipSubscription(DsSipConstants.REFER,
						getDsRequest().getCallId(),
						getDsRequest().getFromTag(),
						AseSipSubscription.DS_REFER,
						id, referencedId);
			if (m_l.isDebugEnabled()) {
				m_l.debug("Leaving AseSipServletRequest getSubscription" +
						m_subscription.toString() );
			}
			return m_subscription;
		}

		// If message is of type NOTIFY
		if (DsSipConstants.NOTIFY == getDsRequest().getMethodID()) {
			if (m_l.isDebugEnabled()) {
				m_l.debug("NOTIFY request");
			}
			// Retrieve the DsSipEventHeader
			DsSipHeader eventHeader = null;
			try {
				eventHeader = getDsRequest().
				getHeaderValidate(DsSipConstants.EVENT);
			}
			catch (Exception e) {
				m_l.error("Cannot parse Event Header " + e.toString());
				if (m_l.isDebugEnabled()) m_l.debug("Leaving AseSipServletRequest getSubscription");
				return null;
			}
			if (null == eventHeader) {
				m_l.error("No Event Header in NOTIFY ");

				if (m_l.isDebugEnabled())
					m_l.debug("Leaving AseSipServletRequest getSubscription");
				return null;
			}

			// The type of subscription depends on the event package name
			DsByteString eventPkgName =
				((DsSipEventHeader)eventHeader).getFullPackageName();
			int manner = 0;
			if (true == DsByteString.equals(eventPkgName,
					AseSipSubscription.DS_REFER))
				manner = DsSipConstants.REFER;
			else
				manner = DsSipConstants.SUBSCRIBE;

			// Work with the ID parameter
			DsByteString id = ((DsSipEventHeader)eventHeader).getID();
			if (null == id)
				id = AseSipSubscription.DS_ZERO;

			m_subscription =
				new AseSipSubscription(manner,
						getDsRequest().getCallId(),
						getDsRequest().getToTag(),
						eventPkgName, id, null);
			if (m_l.isDebugEnabled()) {
				m_l.debug("Leaving AseSipServletRequest getSubscription" +
						m_subscription.toString());
			}
			return m_subscription;
		}

		// If we come here then this is an illegal call
		m_l.error("Error: Request netiher of SUBSCRIBE/NOTIFY/REFER");

		if (m_l.isDebugEnabled())
			m_l.debug("Leaving AseSipServletRequest getSubscription");
		return null;
	}

	/**
	 * Get the referenced subscription
	 * Should be called only for REFER methods
	 */
	AseSipSubscription getReferencedSubscription() {
		if (m_l.isDebugEnabled())
			m_l.debug("Entering AseSipServletRequest getReferencedSubscription");

		// If a subscription exists return it
		if (null != m_referencedSubscription) {
			if (m_l.isDebugEnabled())
				m_l.debug("Leaving AseSipServletRequest getReferencedSubscription");
			return m_referencedSubscription;
		}

		// If message is of type REFER
		if (DsSipConstants.REFER == getDsRequest().getMethodID()) {
			if (m_l.isDebugEnabled())
				m_l.debug("REFER request");

			// Work with the ID parameter
			DsByteString referencedId =
				new DsByteString("" + getDsRequest().getCSeqNumber());
			DsByteString id = AseSipSubscription.DS_ZERO;

			m_referencedSubscription =
				new AseSipSubscription(DsSipConstants.REFER,
						getDsRequest().getCallId(),
						getDsRequest().getFromTag(),
						AseSipSubscription.DS_REFER,
						id, referencedId);
			if (m_l.isDebugEnabled())
				m_l.debug("Leaving AseSipServletRequest getReferencedSubscription");
			return m_referencedSubscription;
		}

		// If we come here then this is an illegal call
		m_l.error("Request not a REFER");

		if (m_l.isDebugEnabled())
			m_l.debug("Leaving AseSipServletRequest getReferencedSubscription");
		return null;
	}

	/**
	 * Fills the parameter map with parameters from Request URI. No parameters
	 * are extracted in case the Request URI is not a SIP/SIPS URI.
	 */
	void extractParamsFromRequestURI()
	{
		if (m_l.isDebugEnabled())
			m_l.debug("extractParamsFromRequestURI() called.");

		DsURI uri = m_request.getURI();

		if ( !( uri instanceof DsSipURL ))
		{
			if (m_l.isDebugEnabled())
				m_l.info("Not a SIP URI - not populating parameter map.");

			return;
		}

		populateParamMap(REQ_URI_PREFIX,((DsSipURL) uri).getParameters());
	}

	/**
	 * Fills the parameter map with parameters from Route header.
	 */
	void extractParamsFromRouteHeader()
	{
		if (m_l.isDebugEnabled())
			m_l.debug("extractParamsFromRouteHeader() called.");

		DsSipRouteHeader route = null;
		try
		{
			route = (DsSipRouteHeader)
			m_request.getHeaderValidate(DsSipConstants.ROUTE);
		}
		catch (Exception ex)
		{
			m_l.error("Unable to correctly parse Route header.", ex);
			return;
		}

		// return if no Route header present
		if ( null == route )
		{
			m_l.error("No Route header in message - returning.");
			return;
		}

		populateParamMap(ROUTE_PREFIX, route.getParameters());
	}

	/**
	 * Populates the parameter map. The supplied prefix is added to the name
	 * of the key. Both key and value are added as <code>String</code> objects.
	 */
	void populateParamMap (String prefix, DsParameters params)
	{
		if (m_l.isDebugEnabled())
			m_l.debug("populateParamMap (String, DsParameters) called.");

		if (m_l.isInfoEnabled())
			m_l.info("Prefix for prameter names is: " + prefix);

		if (null == params)
		{
			if (m_l.isDebugEnabled()) {
				m_l.debug("List of parameters is null - returning.");
			}
			return;
		}

		ListIterator iter = params.listIterator(0);

		while ( iter.hasNext() )
		{
			try
			{
				DsParameter thisParam = (DsParameter) iter.next();
				String key = prefix + thisParam.getKey().toString().toLowerCase();
				String value = thisParam.getValue().toString();

				if (m_l.isInfoEnabled())
					m_l.info("Adding param: " + key + " [" + value + "]");

				m_paramMap.put(key, value);
			}
			catch (Exception ex)
			{
				m_l.error("Cannot typecast to DsParameter.", ex);
			}
		}
	}

	boolean isInProgress() {
		return m_isInProgress;
	}

	void setInProgress() {
		m_isInProgress = true;
	}

	void resetInProgress() {
		m_isInProgress = false;
	}

	public void setChainedDownstream() {
		if (m_l.isDebugEnabled())
			m_l.debug("setChainedDownstream(): called");
		m_chainedDownstream = true;
	}

	public boolean chainedDownstream() {
		if(m_l.isDebugEnabled())
			m_l.debug("chainedDownstream(): called. Returning "
					+ m_chainedDownstream);
		return m_chainedDownstream;
	}

	public AseProtocolSession getPrevSession() {
		return m_prevSession;
	}

	public void setPrevSession(AseProtocolSession session) {
		m_prevSession = (AseSipSession)session;
	}

	public java.lang.Object clone()
	throws CloneNotSupportedException {
		if (m_l.isDebugEnabled())
			m_l.debug("clone(): enter");

		AseSipServletRequest copy = (AseSipServletRequest)super.clone();

		// Request is already clone as DsSipMessage in super class
		copy.m_request              = (DsSipRequest)copy.m_message;
		copy.m_paramMap             = (Hashtable)m_paramMap.clone();
		copy.m_chainedDownstream    = false;
		copy.m_pServerTxn           = m_pServerTxn;
		if (this.isInitial()) {
			// Only call if initial request.
			copy.setRoutingDirective(SipApplicationRoutingDirective.CONTINUE, this);
		}

		if( (m_request.getMethodID() == DsSipConstants.SUBSCRIBE)
				|| (m_request.getMethodID() == DsSipConstants.REFER)
				|| (m_request.getMethodID() == DsSipConstants.NOTIFY) ) {
			if(m_subscription != null) {
				copy.m_subscription = new AseSipSubscription(
						m_subscription.getManner(),
						m_subscription.getCallId(),
						m_subscription.getTag(),
						m_subscription.getEvent(),
						m_subscription.getEventId(),
						m_subscription.getReferencedId());
			}
		}

		if(m_request.getMethodID() == DsSipConstants.REFER) {
			if(m_referencedSubscription != null) {
				copy.m_referencedSubscription = new AseSipSubscription(
						m_referencedSubscription.getManner(),
						m_referencedSubscription.getCallId(),
						m_referencedSubscription.getTag(),
						m_referencedSubscription.getEvent(),
						m_referencedSubscription.getEventId(),
						m_referencedSubscription.getReferencedId());
			}
		}

		if (m_l.isDebugEnabled())
			m_l.debug("clone(): exit");
		return copy;
	}

	/**
	 * This method called in response.send() to mark that this request
	 * has been responded to.
	 */
	public void setResponded() {
		m_responded = true;
	}

	/**
	 * Returns m_responded.
	 */
	public boolean isResponded() {
		return m_responded;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public long getTimestamp() {
		return this.timestamp;
	}

	public boolean hasTimestamp() {
		return this.timestamp == -1? false : true;
	}

	// -- data members --

	/**
	 * Indicates whether the request is "initial". Default value is "false".
	 */
	private boolean m_isInitial;


	/**
	 * Cancelled request.
	 */
	private transient AseSipServletRequest m_cancelledRequest = null;

	/**
	 * Boolean to indicate if this request has been cancelled
	 */
	private boolean m_cancelled = false;

	private ReplicatedMessageHolder m_cancelReqHolder;

	/**
	 * Associated DS stack request.
	 */
	private transient DsSipRequest m_request = null;

	/**
	 * ACK for a non 2xx final response
	 */
	private boolean m_isNon2XXAck = false;

	/**
	 * Client transaction in case the request is being sent out.
	 */
	private transient DsSipClientTransaction m_clientTxn = null;


	/**
	 * Server transaction in case the request is being received.
	 */
	private DsSipServerTransaction m_serverTxn = null;

	private transient AsePseudoSipClientTxn m_pClientTxn = null;

	private transient AsePseudoSipServerTxn m_pServerTxn = null;

	// Data members added for 100rel support

	/**
	 * 100rel is not supported
	 */
	final static int REL_NOT_SUPPORTED = 0;

	/**
	 * 100rel is supported
	 */
	final static int REL_SUPPORTED = 1;

	/**
	 * 100rel is required
	 */
	final static int REL_REQUIRED = 2;

	/**
	 * Indicates the 100rel status of this request
	 */
	private int m_relStatus = REL_NOT_SUPPORTED;

	/**
	 * DsByteString token for testing presence of 100rel
	 */
	private final static DsByteString DS_100REL = new DsByteString("100rel");

	// Data members for SUBSCRIBE/REFER/NOTIFY
	/**
	 * The subscription associated with this request
	 * This will have a value only if the request is of type
	 * SUBSCRIBE or REFER or NOTIFY
	 */
	transient AseSipSubscription m_subscription = null;

	/**
	 * The referenced subscription
	 * Only used in the case of REFER requests
	 */
	transient AseSipSubscription m_referencedSubscription = null;

	/**
	 * Parameter map. This <code>Hashtable</code> is filled with parameters
	 * present either in Request URI or in Route header.
	 */
	transient Hashtable m_paramMap = new Hashtable();

	private boolean m_chainedDownstream = false;

	/**
	 * Flag to be used in getProxy() to indicate if this request has already
	 * been responded or not.
	 */
	private boolean m_responded = false;

	/**
	 * The marker for keeping track of the in progress requests for Overload
	 * Control Manager
	 */
	private boolean m_isInProgress = false;

	/**
	 * In-coming or out-going timestamp
	 */
	private long timestamp = -1;

	private Serializable m_routerState = null;
	private SipApplicationRoutingRegion m_routeRegion = null;
	private String m_subscriberURI = null;

	//Data members added for B2bua handler
	private String linkedRequestId;

	private static final String REQ_URI_PREFIX = "request.uri.param.";
	private static final String ROUTE_PREFIX = "request.route.param.";
	private static final String APP_CHAIN_COOKIE_ID1 = "AseIcId";
	private static final String APP_CHAIN_COOKIE_ID2 = "AseSSId";

	// logger instance for the class
	private static Logger m_l = Logger.getLogger(AseSipServletRequest.class.getName());

	/**
	 * Indicates whether the request is "targeted". Default value is "false".
	 */
	private boolean m_isTargeted;
	private boolean isContactBrackets;

	//
	// UT code
	//

	public static void main(String[] args)
	{

		boolean isInfoEnabled = m_l.isInfoEnabled();

		m_l.info("Creating container");
		AseEngine container = new AseEngine();


		if (isInfoEnabled)
			m_l.info("Creating sipconn");
		AseSipConnector sipconn = new AseSipConnector(container);

		if (isInfoEnabled)
			m_l.info("Creating sipsession");
		AseSipSession sipsess = new AseSipSession(sipconn);

		if (isInfoEnabled)
			m_l.info("Creating request 1");
		AseSipServletRequest request1 = new AseSipServletRequest(sipsess,
				sipconn, AseStrings.INVITE, "one@sender.com:5060",
				"someone@receiver.com:5060", "dummyCall");

		if (isInfoEnabled)
			m_l.info("Creating request 2");
		Address fromAddress = null;
		Address toAddress = null;
		DsSipHeaderList routeHeaderList = null;
		DsSipContactHeader contactHeader = null;
		try
		{
			DsByteString fromUrlStr = new DsByteString("one@sender.com:5060");
			DsByteString toUrlStr = new DsByteString("someone@receiver.com:5060");
			DsSipFromHeader fromHeader = new DsSipFromHeader(fromUrlStr);
			DsSipFromHeader toHeader = new DsSipFromHeader(toUrlStr);
			AseAddressImpl fromAddressImpl = new AseAddressImpl(fromHeader);
			AseAddressImpl toAddressImpl = new AseAddressImpl(toHeader);
			fromAddress = fromAddressImpl;
			toAddress = toAddressImpl;
			routeHeaderList = new DsSipHeaderList(DsSipConstants.ROUTE);
			DsByteString contactUrlStr = new DsByteString("sip:contact@bpsun90:5060");
			contactHeader = new DsSipContactHeader(contactUrlStr);
		}
		catch (Exception ex)
		{
			ex.getMessage();
			ex.printStackTrace();
		}
		DsByteString dsmethod = new DsByteString(AseStrings.INVITE);
		AseSipServletRequest request2 = new AseSipServletRequest(sipsess,
				sipconn, dsmethod, fromAddress, toAddress,
				1, "2001-02-03", routeHeaderList,
				contactHeader, true);

		if (isInfoEnabled)
			m_l.info("Creating request 3");
		AseSipServletRequest request3 =  new AseSipServletRequest(sipsess,
				sipconn, AseStrings.INVITE, fromAddress,
				toAddress, "dummyCall");

		if (isInfoEnabled)
			m_l.info("Creating request 4");
		AseURIImpl fromUri = null;
		AseURIImpl toUri = null;
		try
		{
			DsByteString fromUrlStr = new DsByteString("sip:one@sender.com:5060");
			DsSipURL fromdsUrl = new DsSipURL(fromUrlStr);
			DsByteString toUrlStr = new DsByteString("sip:someone@receiver.com:5060");
			DsSipURL todsUrl = new DsSipURL(toUrlStr);
			fromUri = new AseURIImpl(fromdsUrl);
			toUri = new AseURIImpl(todsUrl);
		}
		catch (Exception ex)
		{
			ex.getMessage();
			ex.printStackTrace();
		}
		AseSipServletRequest request =  new AseSipServletRequest(sipsess,
				sipconn, AseStrings.INVITE, fromUri,
				toUri, "dummyCall");

		try
		{
			if (isInfoEnabled)
				m_l.info("testing setRequestURI() ..");
			DsByteString testUrlStr = new DsByteString("sip:amit@test.com:5060");
			DsSipURL testdsUrl = new DsSipURL(testUrlStr);
			AseURIImpl testURI = new AseURIImpl(testdsUrl);
			request.setRequestURI(testURI);
		}
		catch (Exception ex)
		{
			ex.getMessage();
			ex.printStackTrace();
		}

		if (isInfoEnabled)
			m_l.info("testing getRequestURI() ..");
		URI uri = request.getRequestURI();
		if (isInfoEnabled)
			m_l.info("URI is: " + uri);

		if (isInfoEnabled)
			m_l.info("testing setMaxForwards(int) ..");
		try
		{
			request.setMaxForwards(2);
		}
		catch (Exception ex)
		{
			ex.getMessage();
			ex.printStackTrace();
		}

		if (isInfoEnabled)
			m_l.info("testing getMaxForwards() ..");
		int maxForwards = request.getMaxForwards();
		if (isInfoEnabled)
			m_l.info("MAXFORWARDS is: " + maxForwards);

		/*
        if (isInfoEnabled)
            m_l.info("testing send() method ..");
        try
        {
            request.send();
        }
        catch (Exception ex)
        {
            ex.getMessage();
            ex.printStackTrace();
        }
		 */

		if (isInfoEnabled)
			m_l.info("testing canInitiateDialog() method ..");
		boolean initDialogStatus = request.canInitiateDialog();
		if (isInfoEnabled)
			m_l.info("INITIATE DIALOG STATUS is: " + initDialogStatus);

		if (isInfoEnabled)
			m_l.info("testing setInitial() method ..");
		try
		{
			request.setInitial();
		}
		catch (Exception ex)
		{
			ex.getMessage();
			ex.printStackTrace();
		}

		if (isInfoEnabled)
			m_l.info("testing isInitial() method ..");
		boolean initialStatus = request.isInitial();
		if (isInfoEnabled)
			m_l.info("INITIAL STATUS is: " + initialStatus);

		if (isInfoEnabled)
			m_l.info("testing createCancel() method ..");
		try
		{
			SipServletRequest cancelRequest = request.createCancel();
			if (isInfoEnabled)
				m_l.info("CANCEL REQUEST OBJECT is: " + cancelRequest);
		}
		catch (Exception ex)
		{
			ex.getMessage();
			ex.printStackTrace();
		}

		if (isInfoEnabled)
			m_l.info("testing createResponse(int) method ..");
		try
		{
			SipServletResponse createResponse = request.createResponse(180);
			if (isInfoEnabled)
				m_l.info("CREATE RESPONSE OBJECT is: " + createResponse);
		}
		catch (Exception ex)
		{
			ex.getMessage();
			ex.printStackTrace();
		}

		if (isInfoEnabled)
			m_l.info("testing createResponse(int, String) method ..");
		try
		{
			SipServletResponse createResponse = request.createResponse(200, "OK");
			if (isInfoEnabled)
				m_l.info("CREATE RESPONSE OBJECT (Overloaded) is: " + createResponse);
		}
		catch (Exception ex)
		{
			ex.getMessage();
			ex.printStackTrace();
		}

		if (isInfoEnabled)
			m_l.info("testing getProtocolSession() method ..");
		SasProtocolSession protocolSession = request.getProtocolSession();
		if (isInfoEnabled)
			m_l.info("PROTOCOLSESSION is: " + protocolSession);

		if (isInfoEnabled)
			m_l.info("testing getDsRequest() method ..");
		DsSipRequest sipRequest = request.getDsRequest();
		if (isInfoEnabled)
			m_l.info("SIPREQUEST is: " + sipRequest);

		if (isInfoEnabled)
			m_l.info("testing canMutateContactHeader() method ..");
		boolean iscontactHeadMutable = request.canMutateContactHeader();

		if (isInfoEnabled)
			m_l.info("IS CONTACT HEADER MUTABLE is: " + iscontactHeadMutable);

		if (isInfoEnabled)
			m_l.info("testing setCSeqNumber() method ..");
		request.setCSeqNumber(2);

		if (isInfoEnabled)
			m_l.info("testing addNewFromTag(DsSipFromHeader) method ..");
		try
		{
			DsByteString urlStr = new DsByteString("one@sender.com:5060;tag=1234");
			DsSipFromHeader fromHdr = new DsSipFromHeader(urlStr);
			DsSipFromHeader newFromHdr = request.addNewFromTag(fromHdr);
			m_l.info("NEW FROM HDR is: " + newFromHdr);
		}
		catch (Exception ex)
		{
			ex.getMessage();
			ex.printStackTrace();
		}

		if (isInfoEnabled)
			m_l.info("testing removeFromTag(DsSipFromHeader) method ..");
		try
		{
			DsByteString urlStr = new DsByteString("one@sender.com:5060;tag=1234");
			DsSipFromHeader fromHdr = new DsSipFromHeader(urlStr);
			DsSipFromHeader noTagHdr = request.removeFromTag(fromHdr);
			m_l.info("NO TAG HEADER is: " + noTagHdr);
		}
		catch (Exception ex)
		{
			ex.getMessage();
			ex.printStackTrace();
		}

		if (isInfoEnabled)
			m_l.info("testing createContactHeader(DsSipFromHeader) method ..");
		try
		{
			DsByteString urlStr = new DsByteString("sip:contact@bpsun90:5060;tag=1234");
			DsSipFromHeader fromHdr = new DsSipFromHeader(urlStr);
			DsSipContactHeader contactHdr = request.createContactHeader(fromHdr);
			if (m_l.isInfoEnabled()) m_l.info("CONTACT HEADER is: " + contactHdr);
		}
		catch (Exception ex)
		{
			ex.getMessage();
			ex.printStackTrace();
		}


		class LlExampleClientTransactionInterface implements DsSipClientTransactionInterface
		{
			private AseSipServletRequest m_app;


			public LlExampleClientTransactionInterface(AseSipServletRequest app)
			{
				m_app = app;
			}

			public void provisionalResponse(DsSipClientTransaction clientTransaction, DsSipResponse response)
			{
				if (m_l.isInfoEnabled()) m_l.info("Received a provisional response\n.." + response);
			}

			public void finalResponse(DsSipClientTransaction clientTransaction, DsSipResponse response)
			{
				if (m_l.isInfoEnabled()) m_l.info("\nReceived a final response:\n\n.." + response);
			}

			public void timeOut(DsSipClientTransaction clientTransaction)
			{
				if (m_l.isInfoEnabled()) m_l.info("TimeOut on client side..");
			}
			public void icmpError(DsSipClientTransaction clientTransaction)
			{
				if (m_l.isInfoEnabled()) m_l.info("icmpError on client side..");
			}
		}

		DsSipTransactionParams transactionParams = new DsSipTransactionParams();
		LlExampleClientTransactionInterface  m_clientTransactionInterface;
		m_clientTransactionInterface = new LlExampleClientTransactionInterface(request);

		DsSipClientTransaction m_clientTransaction = null;
		try
		{
			m_clientTransaction = new AseSipClientTransactionImpl(sipRequest,
					m_clientTransactionInterface,
					transactionParams);
		}
		catch (Exception ex)
		{
			m_l.error(ex.getMessage(), ex);
		}

		if (m_l.isInfoEnabled()) m_l.info("testing setClientTxn(DsSipClientTransaction) method ..");
		request.setClientTxn(m_clientTransaction);

		if (m_l.isInfoEnabled()) m_l.info("testing getClientTxn() method ..");
		DsSipClientTransaction clientTxn = request.getClientTxn();
		if (m_l.isInfoEnabled()) m_l.info("CLIENT TRANSACTION is: " + clientTxn);

		if (m_l.isInfoEnabled()) m_l.info("testing getServerTxn() method ..");
		DsSipServerTransaction serverTxn = request.getServerTxn();
		if (m_l.isInfoEnabled()) m_l.info("SERVER TRANSACTION is: " + serverTxn);

		if (m_l.isInfoEnabled()) m_l.info("testing pushRoute(SipURI uri) method ..");
		AseDefaultSipCallIdGenerator callIdGenerator = new AseDefaultSipCallIdGenerator(null, 0);
		AseConnectorSipFactory connFactory = new AseConnectorSipFactory(sipconn, callIdGenerator);
		SipURI sipUri = connFactory.createSipURI("testuri", "bpsun80.com");
		request.pushRoute(sipUri);
		if (m_l.isInfoEnabled()) m_l.info("REQUEST AFTER PUSHING ROUTE is: " + request.getDsRequest());

		if (m_l.isInfoEnabled()) m_l.info("testing getInputStream() method ..");
		ServletInputStream inputStream = request.getInputStream();
		if (m_l.isInfoEnabled()) m_l.info("INPUT STREAM IS" + inputStream);

		if (m_l.isInfoEnabled()) m_l.info("testing getReader() method ..");
		BufferedReader buffReader = request.getReader();
		if (m_l.isInfoEnabled()) if (m_l.isInfoEnabled())
m_l.info("BUFFERED READER IS" + buffReader);

		try
		{
			if (m_l.isInfoEnabled()) m_l.info("testing getProxy() method ..");
			Proxy proxy = request.getProxy();
			if (m_l.isInfoEnabled()) m_l.info("PROXY IS" + proxy);
		}
		catch (Exception ex)
		{
			m_l.error(ex.getMessage(), ex);
		}

		try
		{
			if (m_l.isInfoEnabled()) m_l.info("testing overloaded getProxy() method ..");
			Proxy proxy1 = request.getProxy(true);
			if (m_l.isInfoEnabled()) m_l.info("PROXY IS" + proxy1);
		}
		catch (Exception ex)
		{
			m_l.error(ex.getMessage(), ex);
		}

		if (m_l.isInfoEnabled()) m_l.info("exiting from main()");
	} // main(): UT code
	/* (non-Javadoc)
	 * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
	 */
	public void readExternal(ObjectInput in)
	throws IOException, ClassNotFoundException {

		if(m_l.isDebugEnabled())
			m_l.debug("Entering readExternal ");

		super.readExternal(in);

		m_isInitial = in.readBoolean();
		m_cancelled = in.readBoolean();
		m_isNon2XXAck = in.readBoolean();
		m_responded = in.readBoolean();
		m_isInProgress = in.readBoolean();

		m_relStatus = in.readInt();
		timestamp = in.readLong();

		m_subscription = (AseSipSubscription)in.readObject();
		m_referencedSubscription = (AseSipSubscription)in.readObject();
		m_paramMap = (Hashtable)in.readObject();

		m_cancelReqHolder = (ReplicatedMessageHolder) in.readObject();
		//m_request = (DsSipRequest)in.readObject();
		//m_message = m_request;

		/////////////////////// stack obj de-serialization ////////////////////
		int msgSize = in.readInt();
		byte[] msgBytes = new byte[msgSize];
		in.readFully(msgBytes);

		try {
			m_request = (DsSipRequest) DsSipMessage.createMessage(msgBytes);
			m_message = m_request;
		} catch(DsSipParserListenerException exp) {
			m_l.error("Exception in De-serializing stack request", exp);
		} catch(DsSipParserException exp) {
			m_l.error("Exception in De-serializing stack request", exp);
		}
		//Setting the reference as null for easy GC
		msgBytes = null;
		/////////////////////// stack obj de-serialization ////////////////////


		//In case of sending the response to the INVITE request from
		//standby SAS server transaction needs to be replicated.
		this.readTransaction = in.readBoolean();
		if(transReplication.equals("true") && readTransaction){
			this.m_serverTxn = (DsSipServerTransaction) in.readObject();
		}

		//This is done to handle the stack overflow error as request in the transaction is now transient
		if (m_serverTxn != null && m_serverTxn instanceof AseSipServerTransactionIImpl || m_serverTxn instanceof AseSipServerTransactionImpl){
			((AseSipTransaction) m_serverTxn).setAseSipRequest(this);
		}

		if(m_l.isDebugEnabled())
			m_l.debug("Exiting readExternal : Message = "+this.getMethod());
	}

	/* (non-Javadoc)
	 * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
	 */
	public void writeExternal(ObjectOutput out) throws IOException {

		if(m_l.isDebugEnabled())
			m_l.debug("Entering writeExternal : Message = "+this.getMethod());

		super.writeExternal(out);

		out.writeBoolean(m_isInitial);
		out.writeBoolean(m_cancelled);
		out.writeBoolean(m_isNon2XXAck);
		out.writeBoolean(m_responded);
		out.writeBoolean(m_isInProgress);

		out.writeInt(m_relStatus);
		out.writeLong(timestamp);

		out.writeObject(m_subscription);
		out.writeObject(m_referencedSubscription);
		out.writeObject(m_paramMap);


		out.writeObject(m_cancelReqHolder);
		//out.writeObject(m_request);


		//////////////////////// stack obj serialization ////////////////////
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		m_request.write(baos);
		byte[] msgBytes = baos.toByteArray();
		out.writeInt(msgBytes.length);
		out.write(msgBytes);
		//Setting the reference as null for easy GC
		msgBytes = null;
		//////////////////////// stack obj serialization ////////////////////
		
		//In case of sending the response to the INVITE request from
		//standby SAS server transaction needs to be replicated.
		if(transReplication.equals("true") && !transReplicated && m_serverTxn != null){
			if(m_l.isDebugEnabled())
				m_l.debug("Replicating Transaction");
			transReplicated = true;
			readTransaction = true;	
			out.writeBoolean(readTransaction);
			out.writeObject(m_serverTxn);	
		}else{
			readTransaction = false;	
			out.writeBoolean(readTransaction);
		}
		
		
		if(m_l.isDebugEnabled())
			m_l.debug("Exiting writeExternal");
	}


	@SuppressWarnings("deprecation")
	public String decode(){
		String appSessionId = null;
		URI uri = this.getRequestURI();
		if(uri instanceof SipURI){
			if(m_l.isDebugEnabled()){
				m_l.debug("URI is a SipURI. Will check if the URI is Encoded ....");
			}
			appSessionId = ((SipURI)uri).getParameter(AseApplicationSession.ENCODE_KEY);
		}
		//JSR289.36
		if(appSessionId == null) {
			DsSipRouteHeader routeHdr = null;
			DsByteString sessId = null;
			routeHdr = AseSipRouteHeaderHandler.getTopRoute(this);
			if(null != routeHdr) {
				sessId = routeHdr.getParameter(AseApplicationSession.ENCODE_KEY);
				if(null != sessId){
					try{
						appSessionId = URLDecoder.decode(sessId.toString(),"UTF-8");
						m_l.debug("Decoded appsession id is with encoding scheme UTF-8 :"+appSessionId);
					}catch(UnsupportedEncodingException e){
						m_l.error("Exception while decoding appSessionId",e);
						appSessionId=sessId.toString();
					}
				}
			}
		}
		return appSessionId;
	}

	public void storeMessageAttr() {
		super.storeMessageAttr();
		if(m_cancelledRequest != null){
			if(m_cancelledRequest.m_attrStored == true)
				return;
			//m_cancelledRequest.m_attrStored = true;
			m_cancelReqHolder = new ReplicatedMessageHolder(m_cancelledRequest);
			m_cancelledRequest.storeMessageAttr();
			//((AseApplicationSession)m_cancelledRequest.getApplicationSession()).
			//          addSipServletMessage(m_cancelledRequest.assignMessageId(), m_cancelledRequest);
		}
	}

	/**
	 * Called on standby while it is activating.
	 */
	public void activate() {
		super.activate();
		if(this.m_cancelReqHolder != null) {
			this.m_cancelledRequest = (AseSipServletRequest)m_cancelReqHolder.resolve();
		}
		//FT Handling strategy Update: Replication will be done for the provisional
		//responses as well, so server transaction gets replicated along with INVITE 
		//Request. Underlying connection on which INVITE responses needs to be sent 
		//is transient at Ds Stack. So in order to associate the underlying connection 
		//with server transaction Server transaction needs to be started at stand by
		//node after failover
		
		if(this.m_serverTxn != null && this.m_serverTxn instanceof AseSipServerTransactionIImpl) {
			if (m_l.isDebugEnabled()) m_l.debug("Server Transaction instance of AseSipServerTransactionIImpl");
			try {
				if (m_l.isDebugEnabled()) m_l.debug("Associating Server Transaction with the Via Connection");
				try {
					this.m_serverTxn.startAfterFailover();
				}catch(Exception e){
					m_l.error("Unable to execute startAfterFailover on server txn!"+e);
				}
				if (m_l.isDebugEnabled()) m_l.debug("Associating Timer Task with Server Transaction as a DsEvent");
				DsEvent event = (DsEvent)m_serverTxn;
				((DsSipServerTransactionIImpl)m_serverTxn).setEvent(event);
				//This is done to ensure that Service should get this attribute in case of server transaction 
				//i.e., when invite is received. Now in case of FT Service needs to send the 4XX response.
				//Before this change service was getting the request from attribute in the app session
				//that object doesn't have the server transaction. The same thing is being done for INVITE
				//going out.
				
				if (m_sipSession != null)
					((AseSipServletRequest) m_sipSession.getAttribute(Constants.ORIG_REQUEST)).setServerTxn(this.m_serverTxn);
				//m_sipSession.setAttribute(Constants.ORIG_REQUEST, this);
				if (m_l.isDebugEnabled()) m_l.debug("Server Transaction associated with Via Connection and as a DsEvent to the timer task");
			}catch (Exception e) {
				m_l.error("Exception in reincarnating server transaction.:::"+e.getMessage());
				//m_l.error("Exception in reincarnating server transaction. ",e);
			}
		}
		//FT Handling strategy Update: Replication will be done for the provisional
		//responses as well, so need to replicate the client transaction on 
		//standby SAS only in case on INVITE request
		//The reincarnation of INVITE client transaction needs to be done only once when 
		//INVITE request is getting activated. This code is getting invoked for other requests 
		//like INFO for the same application session
		if (this.getMethod().equalsIgnoreCase(AseStrings.INVITE) && m_sipSession != null 
				&& m_sipSession.getTxn() != null 
				&& m_sipSession.getTxn() instanceof AseSipClientTransactionIImpl){
			if (m_l.isDebugEnabled()) m_l.debug("Client Transaction instance of AseSipClientTransactionIImpl");
			try {
				this.setClientTxn(m_sipSession.getTxn());
			if (m_l.isDebugEnabled())	m_l.debug("Starting Client Transaction");
				this.getClientTxn().startAfterFailover();
				//THis is done as when we receive 487 for CANCEL sent, we don't get 
				//reference of sip session in client transaction as it was tramsient
				((AseSipClientTransactionIImpl)this.getClientTxn()).setSipSession(m_sipSession);
			}catch (Exception e) {
				m_l.error("Exception in Starting the client transaction.:::"+ e.getMessage());
				m_l.warn("Exception in Starting the client transaction." , e);
			}
			//DsSipTransactionManager.addTransaction(this.getTxn(),true,true); // client and useVia				
			// Need to recreate the AseSipClientTransportInfo.instance() as this is the transient 
			// attribute of the client transaction.
			// Need to recreate the AseSipClientTransactionListener as this is also transient attribute 
			((AseSipClientTransactionIImpl)this.getClientTxn()).setClientTransportInfo(AseSipClientTransportInfo.instance());
			((AseSipClientTransactionIImpl) this.getClientTxn())
					.setClientInterface(new AseSipClientTransactionListener(
							AseStackInterfaceLayer.getInstance(),
							AseStackInterfaceLayer.getInstance().getM_factory()));
			//This is done to ensure that Service should get this attribute in case of client transaction 
			//i.e., when invite is sent. Now in case of FT Service needs to send the CANCEL.
			//Before this change service was getting the request from attribute in the app session
			//that object doesn't have the client transaction. The same thing is being done for INVITE
			//coming in.
			((AseSipServletRequest)m_sipSession.getAttribute(Constants.ORIG_REQUEST)).setClientTxn(this.getClientTxn());
			m_sipSession.getOrigRequest().setClientTxn(this.getClientTxn());
			m_sipSession.setTxn(this.getClientTxn());
			if (m_l.isDebugEnabled()) m_l.debug("Client Transaction Reincarnated and Started");
		}
		//This is done to send 487 from standby in case Cancel is received. Now what happens,
		//when Cancel is received then container tries to send 487 to the original request 
		//Original Request is retrieved from outstanding request list of AseSipInvitation Handler
		//And the one reincarnated here (message_map of app session) is different from that so 
		//need to update the list with this reference.
		if (m_sipSession != null){
			List outstandingRequests = m_sipSession.getInvitationHandler().m_outstandingRequests;
			Iterator iter = outstandingRequests.iterator();
			AseSipServletRequest req = null;
			while (iter.hasNext()){
				req = (AseSipServletRequest)(iter.next());
	            if (req.getDsRequest().getCSeqNumber() == this.getDsRequest().getCSeqNumber())
	                break;
				req = null;
			}
			if (req != null){
				m_l.debug("Got a matching outstanding request");
				outstandingRequests.remove(req);
				outstandingRequests.add(this);
			}
		}
	}


	/**
	 * Called on active when this message is set as attribute.
	 */
	public int assignMessageId() {
		if(this.m_cancelledRequest != null) {
			this.m_cancelledRequest.assignMessageId();
		}

		return super.assignMessageId();
	}
	public void setRoutingDirective(SipApplicationRoutingDirective directive,
			SipServletRequest origRequest)
	throws IllegalStateException {

		String uri = null;

		// - if the directive is CONTINUE/REVERSE the origRequest must be initial and not sent
		// Session may not be created, and we don't create here as the session is set
		// null in someplaces.
		if(m_l.isDebugEnabled()){
			m_l.debug("Setting routing directive: " + directive);
		}

		if (!isMutable() || !isInitial()) {
			throw new IllegalStateException("Illegal state (sent, or not initial) in setRoutingDirective");
		}

		routingDirective = directive;
		if (directive != SipApplicationRoutingDirective.NEW) {
			AseSipSession mysess = (AseSipSession)this.getSession(false);
			AseSipSession sess = (AseSipSession)origRequest.getSession();
			Serializable state = sess.getRouterStateInfo();
			SipApplicationRoutingRegion r = sess.getRegion();
			URI subURI = sess.getSubscriberURI();

			if(subURI!=null){
				uri = subURI.toString();
			}

			if(m_l.isDebugEnabled()){
				m_l.debug("Setting routing state: " + state);
			}
			if (mysess != null) {
				mysess.setRouterStateInfo(state);
				mysess.setRegion(r);
				mysess.setSubscriberURI(uri);
			}

			m_routerState = state;
			m_routeRegion = r;
			m_subscriberURI = uri;
		}
	}

	public SipApplicationRoutingDirective getRoutingDirective() {
		return routingDirective;
	}

	/**
	 * Method to add Auth Header with AuthInfo
	 * Bug ID : 5638
	 */
	public void addAuthHeader(SipServletResponse challengeResponse, AuthInfo authInfo) throws IllegalStateException {
		// TODO Auto-generated method stub
		if(m_l.isDebugEnabled())
			m_l.debug("Entering addAuthHeader(SipServletResponse challengeResponse, AuthInfo authInfo) method.. ");

		try{
			if(m_isCommitted == true) {
				throw new IllegalStateException(
						"Can not modify the message because of this message is for committed");
			}

			AseSipAuthenticationHandler handler = new AseSipAuthenticationHandler() {

				@Override
				public void prepareRequestByAuthInfo(
						SipServletRequest request,
						SipServletResponse challenge, AuthInfo authInfo)
				throws SasSecurityException {
					// TODO Auto-generated method stub
					super.prepareRequestByAuthInfo(request, challenge, authInfo);
				}

				@Override
				public boolean validateCredentials(SasMessage req,
						SasAuthenticationInfo authInfo) throws SasSecurityException {
					// TODO Auto-generated method stub
					return false;
				}

				@Override
				public void sendChallenge(SasMessage req, int respCode, String realm)
				throws SasSecurityException {
					// TODO Auto-generated method stub

				}

				@Override
				public Short getHandlerType() {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public SasAuthenticationInfo getCredentials(SasMessage req, String realm, String IdAssertSch, String IdAssertSupp)
				throws SasSecurityException {
					// TODO Auto-generated method stub
					return null;
				}
			};
			handler.prepareRequestByAuthInfo(this,challengeResponse,authInfo);

		}catch (Exception e) {
			// TODO: handle exception
			String msg = "Error occurred in addAuthHeader method of AseSipServletRequest  " + e.toString();
			m_l.error(msg, e);
		}
		if(m_l.isDebugEnabled())
			m_l.debug("Leaving addAuthHeader(SipServletResponse challengeResponse, AuthInfo authInfo) method.. ");

	}

	/**
	 * Method to add Auth Header with UserName & Password.
	 * Bug ID : 5638
	 */
	public void addAuthHeader(SipServletResponse challengeResponse, String userName,String password) throws IllegalStateException {
		// TODO Auto-generated method stub
		if(m_l.isDebugEnabled())
			m_l.debug("Entering addAuthHeader(SipServletResponse challengeResponse, String userName,String password) method.. ");

		try {
			if(m_isCommitted == true) {
				throw new IllegalStateException(
						"Can not modify the message because of this message is for committed");
			}
			AseSipAuthenticationHandler handler = new AseSipAuthenticationHandler() {

				@Override
				public void prepareRequest(SipServletRequest request,
						SipServletResponse challenge, String user,
						String password) throws SasSecurityException {
					// TODO Auto-generated method stub
					super.prepareRequest(request, challenge, user, password);
				}

				@Override
				public boolean validateCredentials(SasMessage req,
						SasAuthenticationInfo authInfo) throws SasSecurityException {
					// TODO Auto-generated method stub
					return false;
				}

				@Override
				public void sendChallenge(SasMessage req, int respCode, String realm)
				throws SasSecurityException {
					// TODO Auto-generated method stub

				}

				@Override
				public Short getHandlerType() {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public SasAuthenticationInfo getCredentials(SasMessage req, String realm, String IdAssertSch, String IdAssertSupp)
				throws SasSecurityException {
					// TODO Auto-generated method stub
					return null;
				}
			};
			handler.prepareRequest(this,challengeResponse,userName,password);

		} catch (Exception e) {
			// TODO: handle exception
			String msg = "Error occurred in addAuthHeader with userName,password  method of AseSipServletRequest  " + e.toString();
			m_l.error(msg, e);
		}
		if(m_l.isDebugEnabled())
			m_l.debug("Leaving addAuthHeader(SipServletResponse challengeResponse, String userName,String password) method.. ");
	}


	public B2buaHelper getB2buaHelper() throws IllegalStateException{

		try {
			if(this.getProxy(false)!=null)
				throw new IllegalStateException(" An Application can not be both Proxy and B2bua");

		} catch (TooManyHopsException e) {
			m_l.error("Too many hops  " + e);
		}
		return ((AseSipSession)this.getSession()).getB2buaHelper();
	}

	//get initial popped route
	public Address getInitialPoppedRoute() {
		return initialPoppedRoute;
	}

	//set initial popped router
	public Address getPoppedRoute() {
		return poppedRoute;
	}

	public SipApplicationRoutingRegion getRegion() {
		if(!m_isInitial){
			throw new IllegalStateException("Method only available for initial requests");
		}
		return this.getSession().getRegion();
	}

	public URI getSubscriberURI() {
		if(!m_isInitial){
			throw new IllegalStateException("Method only available for initial requests");
		}
		return this.getSession().getSubscriberURI();
	}

	public void pushPath(Address adr) {

		if(DsSipConstants.REGISTER != getDsRequest().getMethodID()){
			m_l.error("Path can't be added for non-REGISTER Request:");
			throw new IllegalStateException ("pushPath(Address)invoked on non-REGISTER Request.");
		}

		String supportedHeader  =  this.getHeader(DsSipConstants.SUPPORTED);
		String requireHeader    =  this.getHeader(DsSipConstants.REQUIRE);
		boolean isPathSupported = (supportedHeader!=null)? ((supportedHeader.contains("path")? true : false)) : false;
		boolean isPathRequired  = (requireHeader!=null)? ((requireHeader.contains("path")? true : false)) : false;

		if( ! (isPathSupported || isPathRequired)){
			m_l.error("Path cannot be pushed without path Support");
			throw new IllegalStateException ("Support not indicated by User Agent");
		}

		this.pushPath(adr, true);

	}

	protected void pushPath (Address adr , boolean checkMutable)
	{
		if (m_l.isDebugEnabled()) {
			m_l.debug("pushPath (Address) called.");
		}

		if (m_l.isInfoEnabled())
			m_l.info("Logging Call-ID .. " + getCallId());

		// this method should throw exception in case the Path header cannot
		// be mutated
		if (checkMutable && !isMutable())
		{
			if (m_l.isInfoEnabled())
				m_l.info("Message is immutable.");

			return;
		}

		// null check
		if (null == adr)
		{
			m_l.error("Null Address specified.");
			return;
		}

		String    str_adr = ((Address)adr).toString()  ;

		addHeaderWithoutCheck("Path", str_adr, true, checkMutable);
	}

	public void pushRoute(Address uri) {
		this.pushRoute(uri, true);

	}

	public void pushRoute(Address uri, boolean checkMutable) {
		if (m_l.isDebugEnabled()) {
			m_l.debug("pushRoute (Address) called.");
		}

		// this method should throw exception in case the ROUTE header cannot
		// be mutated
		if (checkMutable && !isMutable())
		{
			if (m_l.isInfoEnabled())
				m_l.info("Message is immutable.");

			return;
		}

		// null check
		if (null == uri)
		{
			m_l.error("Null Address specified.");
			return;
		}

		String    str_adr = ((Address)uri).toString();

		addHeaderWithoutCheck("ROUTE", str_adr, true, checkMutable);
	}



	// set popped route in request
	public void setPoppedRoute(Address poppedRoute) {
		this.poppedRoute = poppedRoute;
	}

	// set initial popped route in request
	public void setInitialPoppedRoute(Address initialPoppedRoute) {
		this.initialPoppedRoute = initialPoppedRoute;
	}

	protected boolean isIncoming() {
		return m_source != AseSipConstants.SRC_SERVLET;
		//return (m_serverTxn != null || m_pServerTxn != null);
	}

	public String getLinkedRequestId() {
		return linkedRequestId;
	}

	public void setLinkedRequestId(String linkedRequestId) {
		this.linkedRequestId = linkedRequestId;
		if(linkedRequestId != null){
			((AseSipSession)this.getSession()).getB2bSessionHandler().storeLinkedRequest(this);
		}else {
			((AseSipSession)this.getSession()).getB2bSessionHandler().storeLinkedRequest(this);
		}
	}

	/**
	 * Sets targeted request flag to true.
	 */
	public void setTargeted()
	{
		if (m_l.isDebugEnabled())
			m_l.debug("setTargeted() called.");

		m_isTargeted = true;
	}

	/**
	 * Returns <code>true</code> if the request is a targeted request.
	 *
	 * @return true if request is a targeted request.
	 */
	public boolean isTargeted()
	{
		if (m_l.isDebugEnabled())
			m_l.debug("isTargeted() called.");

		// Would return the value of m_isTargeted flag. 

		return m_isTargeted;
	}
	
	public void setServerTxn(DsSipServerTransaction serverTxn){
		this.m_serverTxn = serverTxn;
	}

	@Override
	public AsyncContext getAsyncContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DispatcherType getDispatcherType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServletContext getServletContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isAsyncStarted() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isAsyncSupported() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public AsyncContext startAsync() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AsyncContext startAsync(ServletRequest arg0, ServletResponse arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setContactBrackets(boolean b) {
		// TODO Auto-generated method stub
		
	     if( m_l.isDebugEnabled()){
	    	  m_l.debug("setContactBrackets ..."+ b );
	      }
		isContactBrackets=b;
	}
	
	public boolean isContactHasBrackets() {
		return isContactBrackets;
	}



}
