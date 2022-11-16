/*CallTraceServiceImpl
 * CallTraceServiceImpl.java
 *
 * Created on October 5, 2004, 2:57 PM
 */
package com.baypackets.ase.container.sip;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import javax.mail.BodyPart;
import javax.mail.internet.MimeMultipart;
import javax.servlet.sip.Address;
import javax.servlet.sip.ServletParseException;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipServletMessage;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipURI;
import javax.servlet.sip.TelURL;
import javax.servlet.sip.URI;

import org.apache.log4j.Logger;

import java.util.ListIterator;

import com.baypackets.ase.common.AgentDelegate;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.sipconnector.AseSipServletMessage;
import com.baypackets.ase.util.AseStrings;
import com.baypackets.ase.util.CallTraceService;
import com.baypackets.ase.util.Constants;
import com.baypackets.bayprocessor.agent.MComponent;
import com.baypackets.bayprocessor.agent.MComponentState;
import com.baypackets.bayprocessor.agent.OperationType;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.baypackets.bayprocessor.slee.common.Pair;
import com.baypackets.bayprocessor.slee.common.UnableToChangeStateException;
import com.baypackets.bayprocessor.slee.common.UnableToUpdateConfigException;
import com.baypackets.utils.calltracing.CallConstraint;
import com.baypackets.utils.calltracing.CallTraceManager;
import com.baypackets.utils.calltracing.CallConstraintChangeListener;


/**
 * This class provides a utility for selectively logging SIP messages.
 */
public class CallTraceServiceImpl implements CallTraceService,MComponent,CallConstraintChangeListener {

	private static Logger _logger = Logger.getLogger(CallTraceServiceImpl.class);

	private static final String NOTIFY_METHOD = "notify";
	private static final String TCAP_USER_IN = "tcaplistener";
	private static final String TCAP_USER_OUT = "tcapprovider";
	
	private static final String MULTIPART_MIXED = "multipart/mixed";
	private static final String APP_ISUP = "application/isup";
	private static final String CALL_ID = "call-id";
	
	private ConfigRepository _configRep = (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
	private AgentDelegate _agentDelegate = (AgentDelegate)Registry.lookup(Constants.NAME_AGENT_DELEGATE);
	private String _contTracingEnabled;
	private int maxCallCount = -1;
	private static boolean isCriteriaUpdated = false;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

	private final String CALLER="CALLER";

	private final String CALLED="CALLED";

	public CallTraceServiceImpl() {
		_contTracingEnabled = _configRep.getValue(Constants.PROP_CONTAINER_CALL_TRACING).trim();
		CallTraceManager.getInstance().registerConstraintChangeListener(this);
		//		String maxCount = _configRep.getValue(Constants.OID_CALLTRACING_MAX_COUNT).trim();
		//		if(maxCount!= null && maxCount.length()>0) {
		//			maxCallCount = Integer.parseInt(maxCount);
		//		}
	}

	/**
	 * Compares the attributes of the given SipServletMessage with this 
	 * object's constraints on SIP messages and returns a value of "true" if
	 * it is a match or returns "false" otherwise.
	 */
	public boolean matchesCriteria(SipServletMessage message) {
		if (_logger.isDebugEnabled()) {
			_logger.debug("matchesCriteria(): Comparing the given SIP message with the current call tracing constraints...");
		}

		if(!CallTraceServiceImpl.isCriteriaUpdated) {
			String maxCount = _configRep.getValue(Constants.OID_CALLTRACING_MAX_COUNT).trim();
			if(maxCount!= null && maxCount.length()>0) {
				maxCallCount = Integer.parseInt(maxCount);
			}
			if (_logger.isDebugEnabled()) {

				_logger.debug("updating with max count " +maxCallCount);
			}
			if (updateCriteria())
				CallTraceServiceImpl.isCriteriaUpdated=true;
			else
				CallTraceServiceImpl.isCriteriaUpdated=false;
		}

		Object matches = message.getAttribute(Constants.MATCHES_CALL_CRITERIA);

		if (matches instanceof Boolean) {
			if (_logger.isDebugEnabled()) {
				_logger.debug("matchesCriteria(): SIP message was already compared to call constraints.");
			}            
			return ((Boolean)matches).booleanValue();
		}

		SipApplicationSession appSession = message.getApplicationSession();
		if(appSession != null && !appSession.isValid()){
		//	_logger.error("Folowing Appsession is already invalidated .may throw exception in further processing.::"+appSession);
			// commenting log as it is creating so much logging in error
			return false;
		}

		if (appSession != null) {
			if (appSession.getAttribute(Constants.TRACE_KEY) == null) {
				appSession.setAttribute(Constants.TRACE_KEY, message.getCallId());
			}
			matches = appSession.getAttribute(Constants.MATCHES_CALL_CRITERIA);
			if (_logger.isDebugEnabled()) {
				_logger.debug("matchesCriteria(): appSession is not null with matched attribute= "
								+ matches);
			}
		}

		if (matches instanceof Boolean) {
			if (_logger.isDebugEnabled()) {
				_logger.debug("matchesCriteria(): SIP message was already compared to call constraints.");
			}      
			if (_logger.isDebugEnabled()) {
				_logger.debug("matchesCriteria(): appSession returning "+((Boolean)matches).booleanValue());
			}
			return ((Boolean)matches).booleanValue();
		}

		// Returning in case of Notify as not required to increment the logging counter in 
		// CallConstraints.isCallAllowed.
		
		if (message.getMethod().equalsIgnoreCase(NOTIFY_METHOD)) {
			SipURI sipUri = null;
			if (message instanceof SipServletRequest) {
				sipUri = (SipURI) ((SipServletRequest) message).getRequestURI();
			} else {
				// if message is not req than it should be a instance of SipServletResponse
				if(((SipServletResponse) message).getRequest()!=null){
					sipUri = (SipURI) ((SipServletResponse) message).getRequest().getRequestURI();
				}else{
					sipUri=null;
					return false;
				}
			}

			if (sipUri != null
					&& (TCAP_USER_IN.equalsIgnoreCase(sipUri.getUser()) || TCAP_USER_OUT
							.equalsIgnoreCase(sipUri.getUser()))) {
				return false;
			}
		}
		
		Collection constraints = this.getCallConstraints();
		ArrayList list = null;
		
		if (constraints == null || constraints.isEmpty()) {
			if (_logger.isDebugEnabled()) {
				_logger.debug("matchesCriteria(): No call tracing constraints are currently specified.");
			}
			matches = Boolean.valueOf(false);
		} else {
			list = new ArrayList();
			Iterator iterator = constraints.iterator();

			while (iterator.hasNext()) {
				CallConstraint constraint = (CallConstraint)iterator.next();

				if (!constraint.isCallAllowed()) {
						if (_logger.isDebugEnabled()) {

							_logger.debug("max active call count reached for constraint "
								+ constraint.getConstraintID());
						}
					continue;
				}
				if (matches(message, constraint)) {
					list.add(constraint);
				}
			}
			if(!list.isEmpty()){
				message.setAttribute(Constants.MATCHING_CONSTRAINT, list);	
				matches = Boolean.valueOf(true);
				//Setting the value in appSession as well, so that for subsequent message in 
				// any application session, no need to perform call matching criteria.
				if(appSession !=null) {
					appSession.setAttribute(Constants.MATCHING_CONSTRAINT, list);	
				}
			}
		}

		if (!(matches instanceof Boolean)) {
			matches = Boolean.valueOf(false);
		}

		// Tag the message so that subsequent criteria checks aren't necessary.
		message.setAttribute(Constants.MATCHES_CALL_CRITERIA, matches);
		if(appSession !=null) {
			appSession.setAttribute(Constants.MATCHES_CALL_CRITERIA, matches);
		}

		if (_logger.isDebugEnabled()) {
			_logger.debug("matchesCriteria(): incrementCurrentCall ");
		}
		boolean value = ((Boolean)matches).booleanValue();

		if (value) {
			if (_logger.isDebugEnabled()) {
				_logger.debug("matchesCriteria(): Increment the Call Counter.");
			}
			for (int i = 0; i < list.size(); i++) {
				CallConstraint constraint = (CallConstraint) list.get(i);
				constraint.incrementCurrentCall();
			}
		}
		
		if (_logger.isDebugEnabled()) {
			if (value) {
				_logger.debug("matchesCriteria(): SIP message matches the current call tracing criteria.  Returning true...");
			} else {
				_logger.debug("matchesCriteria(): SIP message does not match the current call tracing criteria.  Returning false...");
			}
		}

		return value;
	}


	/**
	 * Compares the attributes of the given SipServletMessage with this 
	 * object's constraints on SIP messages and returns a value of "true" if
	 * it is a match or returns "false" otherwise.
	 */
	public List<Integer> matchesCriteria(String origAddr,String termAddr,String dialedAddr,String ipAddressPort,String opc,String serviceKey) {

		if (_logger.isDebugEnabled()) {
			_logger.debug("matchesCriteria(6 args): Comparing the app given parameters with the current call tracing constraints...");
		}
		// This is done here as well because it may be possible
		// that application specific tracing is disabled and
		// matchesCriteria(SipServletMessage message) is never been called.
		if(!CallTraceServiceImpl.isCriteriaUpdated) {
			String maxCount = _configRep.getValue(Constants.OID_CALLTRACING_MAX_COUNT).trim();
			if(maxCount!= null && maxCount.length()>0) {
				maxCallCount = Integer.parseInt(maxCount);
			}
			if (_logger.isDebugEnabled()) {

				_logger.debug("updating with max count " +maxCallCount);
			}
			if (updateCriteria())
				CallTraceServiceImpl.isCriteriaUpdated=true;
			else
				CallTraceServiceImpl.isCriteriaUpdated=false;
		}

		int matchedConstraint = -1;
		// Combined OPC and Service key as one criteria
		String opcServiceKey=(opc!=null&&serviceKey!=null)?opc+":"+serviceKey:null;
		
		AppSpecificTraceData appData = new AppSpecificTraceData(origAddr, termAddr, dialedAddr,ipAddressPort,opcServiceKey);
		Collection constraints = this.getCallConstraints();
		List<Integer> list = new ArrayList<Integer>();

		if (constraints == null || constraints.isEmpty()) {
			if (_logger.isDebugEnabled()) {
				_logger.debug("matchesCriteria(): No call tracing constraints are currently specified.");
			}
		} else {
			Iterator iterator = constraints.iterator();

			while (iterator.hasNext()) {
				CallConstraint constraint = (CallConstraint)iterator.next();
				
				//Changes Start
				if (!constraint.isCallAllowed()) {
						if (_logger.isDebugEnabled()) {

							_logger.debug("max active call count reached for constraint " + constraint.getConstraintID());
						}	
				continue;
				}
				//Changes End
				
				if (matches(appData, constraint)) {
					matchedConstraint = Integer.parseInt(constraint.getConstraintID());
					list.add(matchedConstraint);
					//Changes Start
					constraint.incrementCurrentCall();
					//Changes End
				}
			}
		}

		if(!list.isEmpty()){
			if (_logger.isDebugEnabled()) {

				_logger.debug("parameters match the current call tracing criteria.");
			}
			//			appSession.setAttribute(Constants.MATCHES_CALL_CRITERIA, Boolean.valueOf(true));
			//			appSession.setAttribute(Constants.MATCHING_CONSTRAINT, list);	
		}else {
			if (_logger.isDebugEnabled()) {

				_logger.debug("parameters do not match the current call tracing criteria.Returning null");
			}
		}

		return list;
	}

	/**
	 * Compares the attributes of the given SipServletMessage with this 
	 * object's constraints on SIP messages and returns a value of "true" if
	 * it is a match or returns "false" otherwise.
	 */
	public List<Integer> matchesCriteria(String origAddr,String termAddr,String dialedAddr) {

		if (_logger.isDebugEnabled()) {
			_logger.debug("matchesCriteria(String,String,String): Comparing the app given parameters with the current call tracing constraints...");
		}
		return this.matchesCriteria(origAddr, termAddr, dialedAddr, null, null, null);
	}

	/**
	 * Returns "true" if the given SipServletMessage is a match for the
	 * specified call constraint.
	 */
	private boolean matches(SipServletMessage message, CallConstraint constraint) {
		if (constraint.getMatchAll()) {
			return matchAll(message, constraint);
		}
		return matchOne(message, constraint);
	}

	/**
	 * Returns "true" if the given SipServletMessage is a match for the
	 * specified call constraint.
	 */
	private boolean matches(AppSpecificTraceData data, CallConstraint constraint) {
		if (constraint.getMatchAll()) {
			return matchAll(data, constraint);
		}
		return matchOne(data, constraint);
	}

	/**
	 * Returns "true" if the given SipServletMessage matches all non-null
	 * parameters contained in the specified call constraint.
	 */
	private boolean matchAll(SipServletMessage message, CallConstraint constraint) {
		if (_logger.isDebugEnabled()) {
			_logger.debug("Trying to match SIP message against all parameters of call constraint with ID:--> " + constraint.getConstraintID());
		}

		if (_logger.isDebugEnabled()) {
			_logger.debug("Request URI in SIP message: " + getRequestURI(message));

			if (constraint.getRequestURI() != null) {
				_logger.debug("Request URI pattern specified in call constraint: " + constraint.getRequestURI().pattern());
			} else {
				_logger.debug("No request URI pattern specified in call constraint.");
			}
		}        

		if (constraint.getRequestURI()!=null &&!matchAll(getRequestURI(message), constraint.getRequestURI())) {
			if (_logger.isDebugEnabled()) {
				_logger.debug("Request URI pattern does not match SIP message.");
			}
			return false;
		}

		if (_logger.isDebugEnabled()) {
			_logger.debug("'To' header in SIP message: " + message.getTo());            

			if (constraint.getToURI() != null) {
				_logger.debug("'To' header pattern specified in call constraint: " + constraint.getToURI().pattern());
			} else {
				_logger.debug("No 'To' header pattern specified in call constraint.");
			}
		}        

			
		if (constraint.getToURI()!=null &&!matchAll(message.getTo(), constraint.getToURI())) {
			if (_logger.isDebugEnabled()) {				
				_logger.debug("'To' header pattern does not match SIP message.");
			}
			return false;
		}

		
		if (_logger.isDebugEnabled()) {
			_logger.debug("Contractor Number in SIP message: " + getContractorNumber(message));
			_logger.debug("'From' header in SIP message: " + getOrigAddress(message));

			if (constraint.getFromURI() != null) {
				_logger.debug("'From' header pattern specified in call constraint: " + constraint.getFromURI().pattern());
			} else {
				_logger.debug("No 'From' header pattern specified in call constraint.");
			}
		}

		if (getContractorNumber(message) != null){
			if(constraint.getFromURI()!=null &&!matchAll(getContractorNumber(message), constraint.getFromURI())){
				if (_logger.isDebugEnabled()) {
					_logger.debug("Contractor Number does not match SIP message.");
				}
				return false;
			}
		}else if (constraint.getFromURI()!=null &&!matchOrigAll(getOrigAddress(message), constraint.getFromURI())) {
			if (_logger.isDebugEnabled()) {
				_logger.debug("'From' header pattern does not match SIP message.");
			}
			return false;
		}

		if (_logger.isDebugEnabled()) {
			_logger.debug("Remote address in SIP message: " + ((AseSipServletMessage)message).getPeerAddress());

			if (constraint.getOriginGateway() != null) {
				_logger.debug("Remote address pattern specified in call constraint: " + constraint.getOriginGateway().pattern());
			} else {
				_logger.debug("No remote address pattern specified in call constraint.");
			}
		}        

		if (constraint.getOriginGateway()!=null &&!matchAll(((AseSipServletMessage)message).getPeerAddress(), fix(constraint.getOriginGateway()))) {
			if (_logger.isDebugEnabled()) {
				_logger.debug("Remote address pattern does not match SIP message.");
			}
			return false;
		}
		
		if (constraint.getIPAddressPort()!=null && !matchAll(((AseSipServletMessage)message).getPeerAddress() + AseStrings.COLON+ ((AseSipServletMessage)message).getRemotePort(),constraint.getIPAddressPort())){
			if (_logger.isDebugEnabled()) {
				_logger.debug("IP:Port does not match SIP message.");
			}
			return false;
		}
		
		return true;
	}


	/**
	 * Returns "true" if at least one of the parameters in the given call
	 * constraint matches the specified SipServletMessage.
	 */
	private boolean matchOne(SipServletMessage message, CallConstraint constraint) {
		if (_logger.isDebugEnabled()) {
			_logger.debug("Looking for at least one attribute of SIP message to match against call constraint with ID:--> " + constraint.getConstraintID());
		}

		if (_logger.isDebugEnabled()) {
			_logger.debug("Request URI in SIP message: " + getRequestURI(message));

			if (constraint.getRequestURI() != null) {
				_logger.debug("Request URI pattern specified in call constraint: " + constraint.getRequestURI().pattern());
			} else {
				_logger.debug("No request URI pattern specified in call constraint.");
			}
		}

		if (matchOne(getRequestURI(message), constraint.getRequestURI())) {
			if (_logger.isDebugEnabled()) {
				_logger.debug("Request URI pattern matches SIP message.");
			}
			return true;
		}

		if (_logger.isDebugEnabled()) {
			_logger.debug("'To' header in SIP message: " + message.getTo());            

			if (constraint.getToURI() != null) {
				_logger.debug("'To' header pattern specified in call constraint: " + constraint.getToURI().pattern());
			} else {
				_logger.debug("No 'To' header pattern specified in call constraint.");
			}
		}

		if (matchOne(message.getTo(), constraint.getToURI())) {
			if (_logger.isDebugEnabled()) {
				_logger.debug("'To' header pattern matches SIP message.");
			}
			return true;
		}

		if (_logger.isDebugEnabled()) {
			//_logger.debug("Contractor Number in SIP message: " + getContractorNumber(message));
			
			_logger.debug("'OrigAddress header in SIP message: " + getOrigAddress(message));

			if (constraint.getFromURI() != null) {
				_logger.debug("'From' header pattern specified in call constraint: " + constraint.getFromURI().pattern());
			} else {
				_logger.debug("No 'From' header pattern specified in call constraint.");
			}
		}

//		if (getContractorNumber(message) != null){
//			if(matchOne(getContractorNumber(message), constraint.getFromURI())){
//				if (_logger.isDebugEnabled()) {
//					_logger.debug("Contractor Number matches SIP message.");
//				}
//				return true;
//			}
//		}else 
		if (matchOrigOne(getOrigAddress(message), constraint.getFromURI())) {
			if (_logger.isDebugEnabled()) {
				_logger.debug("'From' header pattern matches SIP message.");
			}
			return true;
		}

		if (_logger.isDebugEnabled()) {
			_logger.debug("Remote address in SIP message: " + ((AseSipServletMessage)message).getPeerAddress());

			if (constraint.getOriginGateway() != null) {
				_logger.debug("Remote address pattern specified in call constraint: " + constraint.getOriginGateway().pattern());
			} else {
				_logger.debug("No remote address pattern specified in call constraint.");
			}
		}

		if (matchOne(((AseSipServletMessage)message).getPeerAddress(), fix(constraint.getOriginGateway()))) {
			if (_logger.isDebugEnabled()) {
				_logger.debug("Remote address pattern matches SIP message.");
			}
			return true;
		}
		
		if (matchOne(((AseSipServletMessage)message).getPeerAddress() + AseStrings.COLON + ((AseSipServletMessage)message).getRemotePort(),constraint.getIPAddressPort())){
			if (_logger.isDebugEnabled()) {
				_logger.debug("IP:Port does match SIP message.");
			}
			return true;
		}

		return false;
	}

	/**
	 * Returns "true" if the given SipServletMessage matches all non-null
	 * parameters contained in the specified call constraint.
	 */
	private boolean matchAll(AppSpecificTraceData data, CallConstraint constraint) {
		if (_logger.isDebugEnabled()) {
			_logger.debug("Trying to match AppSpecificTraceData against all parameters of call constraint with ID: " + constraint.getConstraintID());
		}

		if (_logger.isDebugEnabled()) {
			_logger.debug("Request URI in SIP message: " + data.getTerminatingAddress());

			if (constraint.getRequestURI() != null) {
				_logger.debug("Request URI pattern specified in call constraint: " + constraint.getRequestURI().pattern());
			} else {
				_logger.debug("No request URI pattern specified in call constraint.");
			}
		}        

		if (constraint.getRequestURI()!=null &&!matchAll(data.getTerminatingAddress(), constraint.getRequestURI())) {
			if (_logger.isDebugEnabled()) {
				_logger.debug("Request URI pattern does not match SIP message.");
			}
			return false;
		}

		if (_logger.isDebugEnabled()) {
			_logger.debug("'To' header in SIP message: " + data.getDialedAddress());            

			if (constraint.getToURI() != null) {
				_logger.debug("'To' header pattern specified in call constraint: " + constraint.getToURI().pattern());
			} else {
				_logger.debug("No 'To' header pattern specified in call constraint.");
			}
		}        

		if (constraint.getToURI()!=null &&!matchAll(data.getDialedAddress(), constraint.getToURI())) {
			if (_logger.isDebugEnabled()) {
				_logger.debug("'To' header pattern does not match SIP message.");
			}
			return false;
		}

		if (_logger.isDebugEnabled()) {
			_logger.debug("'From' header in SIP message: " + data.getOriginatingAddress());

			if (constraint.getFromURI() != null) {
				_logger.debug("'From' header pattern specified in call constraint: " + constraint.getFromURI().pattern());
			} else {
				_logger.debug("No 'From' header pattern specified in call constraint.");
			}
		}

		if (constraint.getFromURI()!=null &&!matchAll(data.getOriginatingAddress(), constraint.getFromURI())) {
			if (_logger.isDebugEnabled()) {
				_logger.debug("'From' header pattern does not match SIP message.");
			}
			return false;
		}
		
		if (constraint.getIPAddressPort()!=null &&!matchAll(data.getIPAddressPort(), constraint.getIPAddressPort())) {
			if (_logger.isDebugEnabled()) {
				_logger.debug("'IPAddress:Port not matches with message.");
			}
			return false;
		}
		
		if (constraint.getOpcServiceKey() != null
				&& !constraint.getOpcServiceKey().equals("null:null")
				&& !matchAll(data.getOpcServiceKey(),
						constraint.getOpcServiceKey())) {
			if (_logger.isDebugEnabled()) {
				_logger.debug("'ServiceKey:OPC not matches with message."
						+ constraint.getOpcServiceKey());
			}
			return false;
		}

		//		if (_logger.isDebugEnabled()) {
		//			_logger.debug("Remote address in SIP message: " + (data.getPeerAddress()));
		//
		//			if (constraint.getOriginGateway() != null) {
		//				_logger.debug("Remote address pattern specified in call constraint: " + constraint.getOriginGateway().pattern());
		//			} else {
		//				_logger.debug("No remote address pattern specified in call constraint.");
		//			}
		//		}        
		//
		//		if (!matchAll(data.getPeerAddress(), fix(constraint.getOriginGateway()))) {
		//			if (_logger.isDebugEnabled()) {
		//				_logger.debug("Remote address pattern does not match SIP message.");
		//			}
		//			return false;
		//		}

		return true;
	}


	/**
	 * Returns "true" if at least one of the parameters in the given call
	 * constraint matches the specified SipServletMessage.
	 */
	private boolean matchOne(AppSpecificTraceData data, CallConstraint constraint) {
		if (_logger.isDebugEnabled()) {
			_logger.debug("Looking for at least one parameter to match against call constraint with ID: " + constraint.getConstraintID());
		}

		if (_logger.isDebugEnabled()) {
			_logger.debug("Request URI in SIP message: " + data.getTerminatingAddress());

			if (constraint.getRequestURI() != null) {
				_logger.debug("Request URI pattern specified in call constraint: " + constraint.getRequestURI().pattern());
			} else {
				_logger.debug("No request URI pattern specified in call constraint.");
			}
		}

		if (matchOne(data.getTerminatingAddress(), constraint.getRequestURI())) {
			if (_logger.isDebugEnabled()) {
				_logger.debug("Request URI pattern matches SIP message.");
			}
			return true;
		}

		if (_logger.isDebugEnabled()) {
			_logger.debug("'To' header in SIP message: " + data.getDialedAddress());            

			if (constraint.getToURI() != null) {
				_logger.debug("'To' header pattern specified in call constraint: " + constraint.getToURI().pattern());
			} else {
				_logger.debug("No 'To' header pattern specified in call constraint.");
			}
		}

		if (matchOne(data.getDialedAddress(), constraint.getToURI())) {
			if (_logger.isDebugEnabled()) {
				_logger.debug("'To' header pattern matches SIP message.");
			}
			return true;
		}

		if (_logger.isDebugEnabled()) {
			_logger.debug("'From' header in SIP message: " + data.getOriginatingAddress());

			if (constraint.getFromURI() != null) {
				_logger.debug("'From' header pattern specified in call constraint: " + constraint.getFromURI().pattern());
			} else {
				_logger.debug("No 'From' header pattern specified in call constraint.");
			}
		}

		if (matchOne(data.getOriginatingAddress(), constraint.getFromURI())) {
			if (_logger.isDebugEnabled()) {
				_logger.debug("'From' header pattern matches SIP message.");
			}
			return true;
		}
		
		if (matchOne(data.getIPAddressPort(), constraint.getIPAddressPort())) {
			if (_logger.isDebugEnabled()) {
				_logger.debug("'IPAddress:Port matches with message.");
			}
			return true;
		}
		
		if (matchOne(data.getOpcServiceKey(), constraint.getOpcServiceKey())) {
			if (_logger.isDebugEnabled()) {
				_logger.debug("'ServiceKey:OPC matches with message.");
			}
			return true;
		}

		//		if (_logger.isDebugEnabled()) {
		//			_logger.debug("Remote address in SIP message: " + data.getPeerAddress());
		//
		//			if (constraint.getOriginGateway() != null) {
		//				_logger.debug("Remote address pattern specified in call constraint: " + constraint.getOriginGateway().pattern());
		//			} else {
		//				_logger.debug("No remote address pattern specified in call constraint.");
		//			}
		//		}
		//
		//		if (matchOne(data.getPeerAddress(), fix(constraint.getOriginGateway()))) {
		//			if (_logger.isDebugEnabled()) {
		//				_logger.debug("Remote address pattern matches SIP message.");
		//			}
		//			return true;
		//		}

		return false;
	}

	/**
	 * This is a kludge to fix an erroneous regular expression in the call
	 * constraints.  This method should be removed at some point and fixed
	 * in the EmsAgent class.
	 */
	private Pattern fix(Pattern pattern) {
		if (pattern == null) {
			return null;
		}
		String s = pattern.pattern();
		StringBuffer buff = new StringBuffer(s);
		 if(s.indexOf(AseStrings.AT)!=-1)
		 buff.deleteCharAt(s.lastIndexOf(AseStrings.AT));
		return Pattern.compile(buff.toString());
	}


	/**
	 * Returns "true" if the given String is a match for the specified
	 * regular expression.
	 */
	private boolean matchAll(Object value, Pattern pattern) {
		if (pattern == null) {
			return true;
		}
		if (value == null) {
			return false;
		}
		
		  String Pluspattern = pattern.pattern().replaceAll("\\+","");
          Pattern secondaryPattern = Pattern.compile(Pluspattern);

          if (pattern.matcher(value.toString().trim()).matches()
        		  || secondaryPattern.matcher(value.toString().replaceAll("\\+" ,"" ).trim()).matches()) {
                 if (_logger.isDebugEnabled()) {
                      _logger.debug("matchAll "+value.toString().trim());
                 } 
              return true;
          }
		return pattern.matcher(value.toString().trim()).matches();
	}

	/**
	 * Returns "true" if the given String is equal to the specified
	 * String.
	 */
	private boolean matchAll(String value, String pattern) {
		if (pattern == null) {
			return true;
		}
		if (value == null) {
			return false;
		}
		return pattern.equals(value);
	}
	
	/**
	 * Returns "true" if the given String is a match for the specified
	 * regular expression.
	 */
	private boolean matchOne(Object value, Pattern pattern) {
		if (pattern == null || value == null) {
			return false;
		}


		
		 String Pluspattern = pattern.pattern().replaceAll("\\+","");
         Pattern secondaryPattern = Pattern.compile(Pluspattern);

         if (pattern.matcher(value.toString().trim()).matches()
        		 || secondaryPattern.matcher(value.toString().replaceAll("\\+" ,"" ).trim()).matches()) {
                if (_logger.isDebugEnabled()) {
                     _logger.debug("matchOne "+value.toString().trim());
                } 
             return true;
         }
		return pattern.matcher(value.toString().trim()).matches();
	}

	/**
	 * Returns "true" if the given String is a match for the specified
	 * regular expression.
	 */
	private boolean matchOne(String value, String pattern) {
		if (pattern == null || value == null) {
			return false;
		}
		return pattern.equals(value);
	}


         /**
	 * Returns true if the any of Adresss matches specified regular Exp.
	 * @param value
	 * @param pattern
	 * @return
	 */
    private boolean matchOrigOne(ListIterator<Address> value, Pattern pattern) {

    	 if (_logger.isDebugEnabled()) {
             _logger.debug("matchOrigOne -->");
        } 
        if (pattern == null || value == null) {
            return false;
         }

        while (value.hasNext()) {
            Address orig = value.next();


            String Pluspattern = pattern.pattern().replaceAll("\\+","");
            Pattern secondaryPattern = Pattern.compile(Pluspattern);


            if (pattern.matcher(orig.toString().trim()).matches()
					|| secondaryPattern.matcher(orig.toString().replaceAll("\\+" ,"" ).trim()).matches()) {
                   if (_logger.isDebugEnabled()) {
                        _logger.debug("Orig Criteria Mathes with "+orig.toString());
                   } 
                return true;
            }
        }
       
          if (_logger.isDebugEnabled()) {
                        _logger.debug("Orig Criteria Donot Mathes pattern" );
          } 
       
        return false;
    } 
    
    

    /**
     * Returns true if the any of Adresss matches specified regular Exp.
     * @param value
     * @param pattern
     * @return
     */
    private boolean matchOrigAll(ListIterator<Address> value, Pattern pattern) {

    	if (pattern == null){
    		return true;
    	}
    	
    	if( value == null) {
    		return false;
    	}
    	
    	while (value.hasNext()) {
    		 Address orig = value.next();

             String Pluspattern = pattern.pattern().replaceAll("\\+","");
             Pattern secondaryPattern = Pattern.compile(Pluspattern);


             if (pattern.matcher(orig.toString().trim()).matches()
            		 || secondaryPattern.matcher(orig.toString().replaceAll("\\+" ,"" ).trim()).matches()) {
                    if (_logger.isDebugEnabled()) {
                         _logger.debug("Orig Criteria Mathes with "+orig.toString());
                    } 
                 return true;
             }
    	}

    	if (_logger.isDebugEnabled()) {
    		_logger.debug("Orig Criteria Donot Mathes" );
    	} 

    	return false;
    } 

    
       
	/**
	 * Extracts the request URI from the given SipServletMessage.
	 */
	private URI getRequestURI(SipServletMessage message) {
		//		SipServletRequest request = null;
		//
		//		if (message instanceof SipServletRequest) {
		//			request = (SipServletRequest)message;
		//		} else if (message instanceof SipServletResponse) {
		//			request = ((SipServletResponse)message).getRequest();
		//		}
		//
		//		if (request != null) {
		//			return request.getRequestURI();
		//		}
		URI uri = null;
		try {
			if(message.getAddressHeader("To") != null) {
				uri = message.getAddressHeader("To").getURI();
			}
		} catch (ServletParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return uri;
	}

	private String getContractorNumber(SipServletMessage message){
		if (_logger.isDebugEnabled()) {
			_logger.debug("Getting Contractor Number from 'P-Sig-Info' header");
		}
		String originatingNumber = message.getHeader("P-Sig-Info");
		if (originatingNumber == null){
			return null;
		}else{
			originatingNumber = originatingNumber.substring(originatingNumber.indexOf(AseStrings.EQUALS)+1);	
			return originatingNumber;
		}
	}
	
	/**
	 * Extracts the request URI from the given SipServletMessage.
	 */

       private ListIterator<Address> getOrigAddress(SipServletMessage message) {
		if (_logger.isDebugEnabled()) {
			_logger.debug("getOrigAddress() called...");
		}
		ListIterator<Address> addr = null;
		try {
//			if (_logger.isDebugEnabled()) {
//				_logger.debug("Getting orig address from 'P-Sig-Info' header");
//			}
//			addr = message.getAddressHeader("P-Sig-Info");
//			if(addr == null) {
//				if (_logger.isDebugEnabled()) {
//					_logger.debug("Getting orig address from 'P-Asserted-Identity' header");
//				}
//				addr = message.getAddressHeader("P-Asserted-Identity");
//			}
			addr = message.getAddressHeaders("P-Asserted-Identity");
			
                    if (!addr.hasNext()) {
                        if (_logger.isDebugEnabled()) {
                            _logger.debug("Getting orig address from 'from' header");
                        }
                        ArrayList<Address> list = new ArrayList<Address>();
                        list.add(message.getFrom());
                        addr = list.listIterator();
                    }
                   
		} catch (ServletParseException e) {
			_logger.error("Exception in getOrigAddress() ",e);
		}
		 return addr;
	} 

	/**
	 * Logs the given SIP message.
	 *
	 * @param message - The SIP message to be logged.
	 * @param logMessage - Additional info to be logged with the message.
	 */
	public void trace(SipServletMessage message, String logMessage) {
		if (_logger.isDebugEnabled()) {
			_logger.debug("trace() called...");
		}

		if (!isContainerTracingEnabled()) {
			if (_logger.isDebugEnabled()) {
				_logger.debug("trace(): Call tracing is currently disabled.");
			}
			return;
		}

		if (!matchesCriteria(message)) {
			if (_logger.isDebugEnabled()) {
				_logger.debug("trace(): SIP message does not match the current call tracing criteria.");
			}
			return;
		}

		SipApplicationSession appSession = message.getApplicationSession();
		String traceKey = null;
		if(appSession != null) {
			traceKey = (String) appSession.getAttribute(Constants.TRACE_KEY);	
		}
		
		int callState = CALL_IN_PROGRESS;
		if ((message.getMethod().equals(AseStrings.BYE) || message.getMethod().equals(AseStrings.CANCEL)) 
				&& traceKey != null && traceKey.equals(message.getCallId())) {
			if (_logger.isDebugEnabled()) {
				_logger.debug("CALL is terminated " + message.getMethod() + " is received for call " + message.getCallId());
			}
			callState = CALL_TERMINATED;
		}
		
		String caller=null;
		String called=null;
		
if(appSession==null){
			
			if (message.getFrom().getURI().isSipURI()) {
				SipURI fromURI = (SipURI) message.getFrom().getURI();
				caller = fromURI.getUser();

			} else {
				TelURL fromURI = (TelURL) message.getFrom().getURI();
				caller = fromURI.getPhoneNumber();

			}

			if (message.getTo().getURI().isSipURI()) {
				SipURI toURI = (SipURI) message.getTo().getURI();
				called = toURI.getUser();
			} else {
				TelURL toURI = (TelURL) message.getTo().getURI();
				called = toURI.getPhoneNumber();
			}
			if (_logger.isDebugEnabled()) {
				_logger.debug("fetched caller" + caller +" and called "+called);
			}
			
		}else if ((appSession.getAttribute(CALLER) == null || appSession.getAttribute(CALLED) == null)) {

			if (message.getFrom().getURI().isSipURI()) {
				SipURI fromURI = (SipURI) message.getFrom().getURI();
				caller = fromURI.getUser();

			} else {
				TelURL fromURI = (TelURL) message.getFrom().getURI();
				caller = fromURI.getPhoneNumber();

			}

			appSession.setAttribute(CALLER, caller);

			if (message.getTo().getURI().isSipURI()) {
				SipURI toURI = (SipURI) message.getTo().getURI();
				called = toURI.getUser();
			} else {
				TelURL toURI = (TelURL) message.getTo().getURI();
				called = toURI.getPhoneNumber();
			}

			appSession.setAttribute(CALLED, called);
			
			if (_logger.isDebugEnabled()) {
				_logger.debug("fetched  and set in appsession caller" + caller +" and called "+called);
			}
		} else{
			caller = (String) appSession.getAttribute(CALLER);
			called = (String) appSession.getAttribute(CALLED);
			
			if (_logger.isDebugEnabled()) {
				_logger.debug("use existing caller" + caller +" and  called "+called);
			}
		}
		
		if (message instanceof SipServletResponse){	
			
			int responseCode = ((SipServletResponse)message).getStatus();
			if(responseCode >= 300 && responseCode <=700 && traceKey != null 
					&& traceKey.equals(message.getCallId())) {
				if (_logger.isDebugEnabled()) {
					_logger.debug("CALL is terminated " + message.getMethod() + " is received for call " + message.getCallId());
				}
				callState = CALL_TERMINATED;
			}
		}

		ArrayList list = (ArrayList) message.getAttribute(Constants.MATCHING_CONSTRAINT);

		
		if(list == null) {
			if (_logger.isDebugEnabled()) {
				_logger.debug("Reading value from Application session");
			}
			if(appSession != null) {
				list = (ArrayList) appSession.getAttribute(Constants.MATCHING_CONSTRAINT);
			}
		}
		
		for(int i=0; list != null && i < list.size(); i++ ){
			// Get the CallConstraint object that matched this SIP message.
			CallConstraint constraint = (CallConstraint) list.get(i);                

			int isTestCall = constraint.isTest() ? 1 : 0;

			//String traceKey = null;
			if (appSession != null && appSession.getAttribute(Constants.TRACE_KEY) != null){
				traceKey = (String) appSession.getAttribute(Constants.TRACE_KEY);
			} else{
				traceKey = message.getCallId();
			}
			
			if (_logger.isDebugEnabled()) {
				_logger.debug("trace(): Invoking EMS agent to log the SIP message with trace key..." + traceKey);
			}        

			String dateForMessage = sdf.format(new Date());
			String finalLogMessage = dateForMessage+"\n"+logMessage;
			if (_logger.isDebugEnabled()) {
				_logger.debug("In trace() method : dateForMessage " + dateForMessage);
			}
			String isupContent = getMultipartIsupContent(message);
			String beforeIsup=null;
			String finalConvertedMessage=null;
			try{	
				finalConvertedMessage=new String(finalLogMessage.getBytes("ISO-8859-1"),"ISO-8859-1");
			}catch(UnsupportedEncodingException e){
				_logger.error("UnsupportedEncodingException in trace message while converting in ISO-8859-1 format",e);
				finalConvertedMessage=finalLogMessage;
				
			}
			_agentDelegate.reportCallHistoryInfo(traceKey, finalConvertedMessage, Integer.parseInt(constraint.getConstraintID()), isTestCall, callState,caller,called);
			
			// call-id should be there for all msg..no need to send to ems for service msg
			if(isupContent != null && logMessage.toLowerCase().contains((CALL_ID))){
				
				if (message instanceof SipServletRequest){
					beforeIsup = message.getMethod();
				}
				
				if (message instanceof SipServletResponse){
					beforeIsup = ((SipServletResponse)message).getStatus()+"";
					 
				}
				
				isupContent = dateForMessage+"\n"+beforeIsup+ " : " +isupContent;
				
				String finalConvertedIsupContent=null;
				try{	
					finalConvertedIsupContent=new String(isupContent.getBytes("ISO-8859-1"),"ISO-8859-1");
				}catch(UnsupportedEncodingException e){
					_logger.error("UnsupportedEncodingException in trace message while converting in ISO-8859-1 format",e);
					finalConvertedIsupContent=isupContent;
					
				}
				
				_agentDelegate.reportCallHistoryInfo(traceKey, finalConvertedIsupContent, Integer.parseInt(constraint.getConstraintID()), isTestCall, callState,caller,called);

			}
		}
		if (_logger.isDebugEnabled()) {
			_logger.debug("Returning from trace() method.");
		}
	}
	
	
	public static String getMultipartIsupContent(SipServletMessage msg){
		BodyPart bodyPart = null;
		byte [] contentBytes = null;
		String msgIsupContent=null;

		try {
			if ((msg.getContentType() != null)
					&& (msg.getContentType().startsWith(MULTIPART_MIXED))) {
				_logger.debug("Inside getMultipartIsupContent if..");

				MimeMultipart mimeMultipart = (MimeMultipart) msg.getContent();
				for (int indx = 0; indx < mimeMultipart.getCount(); indx++) {
					_logger.debug("Inside getMultipartIsupContent for..");
					bodyPart = mimeMultipart.getBodyPart(indx);

					if (bodyPart.getContentType().startsWith(APP_ISUP)) {
						contentBytes = getByteArrayFromStream((ByteArrayInputStream) bodyPart.getContent());
						_logger.debug("Inside getMultipartIsupContent app/isup..");
						break;
					}
				}
			}


			if(contentBytes != null){

				msgIsupContent = formatBytes(contentBytes);
				msgIsupContent = msgIsupContent.replace("0x", "");
				msgIsupContent = msgIsupContent.replaceAll("\\s+","");

			}

		} catch (Exception e) {
			_logger.error("Inside getMultipartIsupContent Exception : " + e);
			msgIsupContent = null;
		}
		_logger.debug("Inside getMultipartIsupContent isup contents :=> " + msgIsupContent);

		return msgIsupContent;
	}
	
	public static final char hexcodes[] = { '0', '1', '2', '3', '4', '5', '6',
		'7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
	
	public  static String formatBytes(byte data[]) {
		if(data != null){
			char output[] = new char[5 * (data.length)];
			int top = 0;
	
			for (int i = 0; i < data.length; i++) {
				output[top++] = '0';
				output[top++] = 'x' ;
				output[top++] = hexcodes[(data[i] >> 4) & 0xf];
				output[top++] = hexcodes[data[i] & 0xf];
				output[top++] = ' ';
			}
	
			return (new String(output).trim());
		}
		else
			return null;
	}

	public static byte[] getByteArrayFromStream(ByteArrayInputStream byteArrInpStream)
	throws Exception {
		if (_logger.isDebugEnabled()) {
			_logger.debug(":: Inside getByteArrayFromStream");
		}
		byte[] byteArr = null;
		int byteArrLen = byteArrInpStream.available();
		byteArr = new byte[byteArrLen];
		byteArrInpStream.read(byteArr, 0, byteArrLen);
		return byteArr;
	}
	

	public int trace(int constraintId, String traceKey , String logMessage, int callState) {

		if (_logger.isDebugEnabled()) {
			_logger.debug("trace(constraintId,traceKey,logMessage) called...");
		}

		if (!isEnabled()) {
			if (_logger.isDebugEnabled()) {
				_logger.debug("trace(): Call tracing is currently disabled.");
			}
			return CallTraceService.CALL_TRACING_NOT_ENABLE;
		}

		CallConstraint constraint = getCallConstraints(constraintId);

		// This is done to handle the situation when an calltracing criteria
		// is added on EMS when SAS is up and running. As there is no callback
		// from SLEE code to SAS about the newly added call tracing criteria,
		// SAS has to check the maxCallCount in the callTracing criteria and
		// set the value.
		// This has been removed as call back is now implemented
		/*if(constraint.getMaxCallCount() < 0){
			_logger.error("max call count is negative. Setting it.");
			constraint.setMaxCallCount(maxCallCount);
		}*/

		//This has been moved to the matches criteria
		/*if (!constraint.isCallAllowed()) {
			if (_logger.isDebugEnabled()) {
				_logger.error("max active call count reached");
			}
			return CallTraceService.MAX_TRACED_CALL_COUNT_REACHED;
		}

		constraint.incrementCurrentCall();*/

		int isTestCall = constraint.isTest() ? 1 : 0;

		if (_logger.isDebugEnabled()) {
			_logger.debug("trace(): Invoking EMS agent to log the SIP message...");
		}        

		String dateForMessage = sdf.format(new Date());
		String finalLogMessage = dateForMessage+"\n"+logMessage;
		if (_logger.isDebugEnabled()) {
			_logger.debug("In trace() method : dateForMessage " + dateForMessage);
		}
		String finalConvertedMessage=null;
		try{	
			finalConvertedMessage=new String(finalLogMessage.getBytes("ISO-8859-1"),"ISO-8859-1");
		}catch(UnsupportedEncodingException e){
			_logger.error("UnsupportedEncodingException in trace message while converting in ISO-8859-1 format",e);
			finalConvertedMessage=finalLogMessage;
			
		}
		_agentDelegate.reportCallHistoryInfo(traceKey, finalConvertedMessage,constraintId , isTestCall, callState,null,null);

		if (_logger.isDebugEnabled()) {
			_logger.debug("Returning from trace() method.");
		}
		return 0;
	}
	
	@Override
	public int trace(int constraintId, String traceKey , String logMessage, int callState,String caller,String called) {

		if (_logger.isDebugEnabled()) {
			_logger.debug("trace(constraintId,traceKey,logMessage, int callState,String caller,String called) called...");
		}

		if (!isEnabled()) {
			if (_logger.isDebugEnabled()) {
				_logger.debug("trace(): Call tracing is currently disabled.");
			}
			return CallTraceService.CALL_TRACING_NOT_ENABLE;
		}

		CallConstraint constraint = getCallConstraints(constraintId);

		// This is done to handle the situation when an calltracing criteria
		// is added on EMS when SAS is up and running. As there is no callback
		// from SLEE code to SAS about the newly added call tracing criteria,
		// SAS has to check the maxCallCount in the callTracing criteria and
		// set the value.
		// This has been removed as call back is now implemented
		/*if(constraint.getMaxCallCount() < 0){
			_logger.error("max call count is negative. Setting it.");
			constraint.setMaxCallCount(maxCallCount);
		}*/

		//This has been moved to the matches criteria
		/*if (!constraint.isCallAllowed()) {
			if (_logger.isDebugEnabled()) {
				_logger.error("max active call count reached");
			}
			return CallTraceService.MAX_TRACED_CALL_COUNT_REACHED;
		}

		constraint.incrementCurrentCall();*/

		int isTestCall = constraint.isTest() ? 1 : 0;

		if (_logger.isDebugEnabled()) {
			_logger.debug("trace(): Invoking EMS agent to log the SIP message...");
		}        

		String dateForMessage = sdf.format(new Date());
		String finalLogMessage = dateForMessage+"\n"+logMessage;
		if (_logger.isDebugEnabled()) {
			_logger.debug("In trace() method : dateForMessage " + dateForMessage);
		}
		String finalConvertedMessage=null;
		try{	
			finalConvertedMessage=new String(finalLogMessage.getBytes("ISO-8859-1"),"ISO-8859-1");
		}catch(UnsupportedEncodingException e){
			_logger.error("UnsupportedEncodingException in trace message while converting in ISO-8859-1 format",e);
			finalConvertedMessage=finalLogMessage;
			
		}
		_agentDelegate.reportCallHistoryInfo(traceKey, finalConvertedMessage,constraintId , isTestCall, callState,caller,called);

		if (_logger.isDebugEnabled()) {
			_logger.debug("Returning from trace() method.");
		}
		return 0;
	}

	/**
	 * This method is used to update the CallConstraints with the 
	 * value of max call count. max call count is run time changable 
	 * OID and its value is not sent by EMS to SLEE along with the
	 * CallConstraint object but rather as an OID to SAS and its
	 * SAS's responsibility to update all the CallConstraints. 
	 */
	private boolean updateCriteria() {
		Collection constraints = this.getCallConstraints();

		if (constraints == null || constraints.isEmpty()) {
			if (_logger.isDebugEnabled()) {
				_logger.debug("No call tracing constraints are currently specified.");
			}
			return false;
		} else {
			ArrayList list = new ArrayList();
			Iterator iterator = constraints.iterator();

			while (iterator.hasNext()) {
				CallConstraint constraint = (CallConstraint)iterator.next();
				if(constraint.getMaxCallCount() < 0){
					constraint.setMaxCallCount(maxCallCount);
				}
			}
			return true;
		}
	}

	/**
	 * This method is used to update a particular CallConstraints with the 
	 * value of max call count. max call count is run time changable 
	 * OID and its value is not sent by EMS to SLEE along with the
	 * CallConstraint object but rather as an OID to SAS and its
	 * SAS's responsibility to update all the CallConstraints. 
	 */
	private void updateCriteria(int constrantId) {
		Collection constraints = this.getCallConstraints();

		if (constraints == null || constraints.isEmpty()) {
			if (_logger.isDebugEnabled()) {
				_logger.debug("No call tracing constraints are currently specified.");
			}
		} else {
			Iterator iterator = constraints.iterator();

			while (iterator.hasNext()) {
				CallConstraint constraint = (CallConstraint)iterator.next();
				if (Integer.parseInt(constraint.getConstraintID())==constrantId) {
					constraint.setMaxCallCount(maxCallCount);
				}
			}
		}
	}

	/**
	 * Returns the constraint currently registered with this object.
	 *
	 * @return  CallConstraint object corresponding to the constraintId.
	 * @see com.baypackets.utils.calltracing.CallConstraint
	 */
	public CallConstraint getCallConstraints(int constrantId) {
		Collection constraints = this.getCallConstraints();

		if (constraints == null || constraints.isEmpty()) {
			if (_logger.isDebugEnabled()) {
				_logger.debug("No call tracing constraints are currently specified.");
			}
		} else {

			Iterator iterator = constraints.iterator();

			while (iterator.hasNext()) {
				CallConstraint constraint = (CallConstraint)iterator.next();
				if (Integer.parseInt(constraint.getConstraintID())==constrantId) {
					return constraint;
				}
			}
		}
		return null;
	}

	/**
	 * Returns the set of all SIP message constraints currently registered
	 * with this object.
	 *
	 * @return  A Collection of CallConstraint objects.
	 * @see com.baypackets.utils.calltracing.CallConstraint
	 */
	public Collection getCallConstraints() {
		return CallTraceManager.getInstance().getCallConstraints();
	}


	/**
	 * Registers a new SIP message constraint with this object.
	 */
	public void addCallConstraint(CallConstraint constraint) {
		if (constraint.getMaxCallCount() == -1) {
			constraint.setMaxCallCount(maxCallCount);
		}
		if (_logger.isDebugEnabled()) {
			_logger.debug("addCallConstraint with max count "+constraint.getMaxCallCount());
		}
	
		CallTraceManager.getInstance().addCallConstraint(constraint);
	}


	/**
	 * Removes the specified SIP message constraint.
	 */
	public void removeCallConstraint(String constraintID) {
		CallTraceManager.getInstance().removeCallConstraint(constraintID);
	}

	/**
	 * Indicates whether SIP message logging is currently enabled for container.
	 * If this is 'false' then container will not trace any SIP message.
	 */
	public boolean isContainerTracingEnabled() {
		if(this._contTracingEnabled.equalsIgnoreCase(AseStrings.TRUE_CAPS)) {
			return true;
		}
		return false;
	}

	/**
	 * Indicates whether SIP message logging is currently enabled.
	 */
	public boolean isEnabled() {
		return CallTraceManager.getInstance().isTracingEnabled();
	}


	/**
	 * Enables SIP message logging.
	 */
	public void enableCallTracing() {
		CallTraceManager.getInstance().enableCallTracing();
	}


	/**
	 * Disables SIP message logging.
	 */
	public void disableCallTracing() {
		CallTraceManager.getInstance().disableCallTracing();
	}

	/**
	 * Decrements the active call count for any constraint ID.
	 */
	public void decrementActiveCallCount(int constraintId) {
		if (_logger.isDebugEnabled()) {
			_logger.debug("Inside decrementActiveCallCount for constraint "+constraintId);
		}
		CallConstraint constraint = getCallConstraints(constraintId);
		constraint.decrementCurrentCall();
	}


	/**
	 * Wrapper class for application specific trace data parameters.
	 * 
	 */
	class AppSpecificTraceData {


		private String originatingAddress;
		private String terminatingAddress;
		private String dialedAddress;
		private String iPAddressPort;
		private String opcServiceKey;
		
		AppSpecificTraceData(){

		}

		AppSpecificTraceData(String origAddr,String termAddr,String dialedAddr) {
			this.originatingAddress="sip:" +origAddr;
			this.terminatingAddress="sip:" +termAddr;
			this.dialedAddress="sip:" +dialedAddr;
		}
		
		AppSpecificTraceData(String origAddr,String termAddr,String dialedAddr,String iPAddressPort,String opcServiceKey) {
			this.originatingAddress="sip:" +origAddr;
			this.terminatingAddress="sip:" +termAddr;
			this.dialedAddress="sip:" +dialedAddr;
			this.iPAddressPort=iPAddressPort;
			this.opcServiceKey=opcServiceKey;
		}

		public String getOriginatingAddress() {
			return originatingAddress;
		}

		public void setOriginatingAddress(String originatingAddress) {
			this.originatingAddress = originatingAddress;
		}

		public String getTerminatingAddress() {
			return terminatingAddress;
		}

		public void setTerminatingAddress(String terminatingAddress) {
			this.terminatingAddress = terminatingAddress;
		}

		public String getDialedAddress() {
			return dialedAddress;
		}

		public void setDialedAddress(String dialedAddress) {
			this.dialedAddress = dialedAddress;
		}

		public String getIPAddressPort() {
			return iPAddressPort;
		}

		public void setIPAddressPort(String iPAddressPort) {
			this.iPAddressPort = iPAddressPort;
		}

		public String getOpcServiceKey() {
			return opcServiceKey;
		}

		public void setOpcServiceKey(String opcServiceKey) {
			this.opcServiceKey = opcServiceKey;
		}

		
	}


	@Override
	public void changeState(MComponentState arg0)
	throws UnableToChangeStateException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateConfiguration(Pair[] configData, OperationType opType)
	throws UnableToUpdateConfigException {
		if (OperationType.MODIFY != opType.getValue()) {
			return;
		}

		for (int i = 0; i < configData.length; i++) {
			// Extract the parameter name and value.
			String name = (String)configData[i].getFirst();
			String value = (String)configData[i].getSecond();
			value = (value == null) ? "" :value.trim();
			if (name.equals(Constants.OID_CALLTRACING_MAX_COUNT)) {
				int intValue = Integer.parseInt(value);
				CallTraceManager.getInstance().updateMaxCounter(intValue);
				resetActiveCallCounterForConstraints();
				maxCallCount = intValue;
			} 
		}
	}
	public void callConstraintAdded(CallConstraint callConstraint){
		if (_logger.isDebugEnabled()) {

			_logger.debug("Call Constraint Added wit maxcount " + callConstraint.getMaxCallCount());
		}
		if (callConstraint != null && callConstraint.getMaxCallCount()==-1){
			if (maxCallCount == -1) {
				String maxCount = _configRep.getValue(
						Constants.OID_CALLTRACING_MAX_COUNT).trim();
				if (maxCount != null && maxCount.length() > 0) {
					maxCallCount = Integer.parseInt(maxCount);
				}
				if (_logger.isDebugEnabled()) {

					_logger.debug("updating with max count " + maxCallCount);
				}
			}
			callConstraint.setMaxCallCount(maxCallCount);
		}
	}
	public void callConstraintRemoved(CallConstraint callConstraint){
		if (_logger.isDebugEnabled()) {

			_logger.debug("Call Constraint Removed");
		}
	}
	
	private void resetActiveCallCounterForConstraints(){
		Collection constraints = this.getCallConstraints();

		if (constraints == null || constraints.isEmpty()) {
			if (_logger.isDebugEnabled()) {
				_logger.debug("No call tracing constraints are currently specified.");
			}
		} else {
			Iterator iterator = constraints.iterator();

			while (iterator.hasNext()) {
				CallConstraint constraint = (CallConstraint)iterator.next();
				constraint.resetCurrentActiveCall();
			}
		}
	}

}
