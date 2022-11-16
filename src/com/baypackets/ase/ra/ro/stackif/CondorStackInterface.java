package com.baypackets.ase.ra.ro.stackif;

import java.util.Map;
import java.util.Hashtable;
import java.io.File;

import javax.servlet.sip.SipApplicationSession;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.ro.impl.*;
import com.baypackets.ase.ra.ro.*;
import com.baypackets.ase.resource.ResourceEvent;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.spi.resource.ResourceContext;
import com.baypackets.ase.spi.util.TimerService;
import com.baypackets.bayprocessor.slee.internalservices.AlarmService; // TODO - only spi package should be used

import com.condor.ro.roclient.*;
import com.condor.ro.roclient.request.*;
import com.condor.ro.roclient.response.*;
import com.condor.ro.rocommon.*;
import com.condor.diaCommon.UnExpectedStateInfo;
import com.condor.ro.rocommon.RoAcctRecForStorage;
import com.condor.ro.rocommon.ErrorInfo;
import com.condor.ro.rocommon.RoStateInfo;

/**
 * This class handles all stack specific operations in either direction. It implements
 * <code>RoStackInterface</code> and <code>RoClientAPIRegister</code> interfaces.
 * <p/>
 * The first interface represents operations in RoRA-to-Stack direction whereas second
 * interface represents operations in Stack-to-RoRA direction.
 * <p/>
 * There will be only one instance of this class in a program.
 *
 * @author Neeraj Jain
 */

public class CondorStackInterface implements RoStackInterface, RoClientAPIRegister, Constants {

	private static Logger logger = Logger.getLogger(CondorStackInterface.class);

	private static RoResourceFactory raFactory;
	private static TimerService timerService;
	private AlarmService alarmService;
	private RoResourceAdaptor ra;
	private RoClientAPI roClient;

	private Map requestMap;
	private String configFile; //Condor Stack initializes by reading this file

	private int handleCount = 0;
		
	// Following is used for generating log messages only
	private static String[] methods = new String[7]; // Total request types = 7

	public CondorStackInterface(RoResourceAdaptor ra) {
		logger.info("In the Constructor of CondorStackInterface(RoResourceAdaptor )");
		this.ra = ra;
		this.roClient = new RoClientAPI();
		this.requestMap = new Hashtable(64*1024); // initialize with 64 K entries
	}

	public void init(ResourceContext context) throws RoStackException {

		this.alarmService = context.getAlarmService();
		this.raFactory = (RoResourceFactory)context.getResourceFactory();
		this.configFile = (String)context.getConfigProperty("ase.home") + File.separator + "conf"
							+ File.separator + "roClient.cfg";
		logger.debug("Using [" + this.configFile + "]");

		// Following is only for log messages
		methods[0] = new String("roClient.roStartSessionReq ");
		methods[1] = new String("roClient.roIntermSessionReq ");
		methods[2] = new String("roClient.roStopSessionReq ");
		methods[3] = new String("roClient.roDirectDebitingReq ");
		methods[4] = new String("roClient.roRefundServiceReq ");
		methods[5] = new String("roClient.roBalanceCheckReq ");
		methods[6] = new String("roClient.roServicePriceEnquiry ");
	}

	public void start() throws RoStackException {
		logger.debug("start(): initialize Condor Ro stack.");
		int noOfTrial = 1;

		for(int i=1; i<= noOfTrial; ++i) {
			if(logger.isDebugEnabled()) {
				logger.debug("start(): " + i + "th trial...");
			}

			int retCode = this.roClient.initializeRoInterface(this.configFile);

			if(retCode == RoResultType.RO_PASS) {
				logger.debug("start(): Condor statck is initialized.");
				this.roClient.roRegisterAppAPIs(this);
				logger.debug("start(): Registration Of Call Back methods is done");
				return;
			} else {
				String alarmMsg = "initializeROInterface() failed with error code [" + retCode + "]";
				logger.error(alarmMsg);
				try {
					this.alarmService.sendAlarm(
						com.baypackets.ase.util.Constants.ALARM_RO_STACK_INITIALIZATION_FAILED,
							 alarmMsg); // TODO - shift
				} catch(Exception e) {
					logger.error("Unable to send alarm exception", e);
				}

				throw new RoStackException(alarmMsg);
			}
		} // for
	}

	public void stop() throws RoStackException {
		logger.info("stop(): closing Diameter stack...");

		this.roClient.roCloseDiameterStack((byte)3);

		logger.info("stop(): Diameter stack is closed");
	}

	public void handleResponse(RoResponse response) throws RoStackException {
		throw new RoStackException("No response should be sent by Ro RA");
	}

	public void handleRequest(RoRequest request) throws RoStackException {
		if (request == null) {
			logger.error("handleRequest(): null request");
			throw new RoStackException("Null request argument passed");
		}

		// Send Condor Request
		int errorCode = 0;
		String reqStr = null;
		int handle = -1;
		RoSession session = (RoSession)request.getSession();
		CreditControlRequestImpl ccr = (CreditControlRequestImpl)request;

		try {
			switch(request.getType()) {
				case RO_FIRST_INTERROGATION:
					handle = this.getNextHandle();
					session.setHandle(handle);

					if(logger.isDebugEnabled()) {
						logger.debug("First Interogation Request handle is " + handle);
					}
					reqStr = methods[0];

					this.requestMap.put(new Integer(handle), request);
					errorCode = this.roClient.roStartSessionReq(
											(RoFirstInterogationReq)ccr.getStackImpl(),
											ccr.getDestinationInfo(),
											handle,
											new RoStackHandle());
				break;

				case RO_INTERMEDIATE_INTERROGATION:
					handle = session.getHandle();
					if(logger.isDebugEnabled()) {
						logger.debug("Update Interogation Request handle is " + handle);
					}
					reqStr = methods[1];

					this.requestMap.put(new Integer(handle), request);
					errorCode = this.roClient.roIntermSessionReq(
											(RoUpdateInterogationReq)ccr.getStackImpl(),
											handle);
				break;

				case RO_FINAL_INTERROGATION:
					handle = session.getHandle();
					if(logger.isDebugEnabled()) {
						logger.debug("Final Interogation Request handle is " + handle);
					}
					reqStr = methods[2];

					this.requestMap.put(new Integer(handle), request);
					errorCode = this.roClient.roStopSessionReq(
											(RoFinalInterogationReq)ccr.getStackImpl(),
											handle);
				break;

				case RO_DIRECT_DEBITING:
					handle = this.getNextHandle();
					if(logger.isDebugEnabled()) {
						logger.debug("Direct Debiting Request handle is " + handle);
					}
					reqStr = methods[3];

					this.requestMap.put(new Integer(handle), request);
					errorCode = this.roClient.roDirectDebitingReq(
											(RoDirectDebitingReq)ccr.getStackImpl(),
											ccr.getDestinationInfo(),
											handle);
				break;

				case RO_REFUND_ACCOUNT:
					handle = this.getNextHandle();
					if(logger.isDebugEnabled()) {
						logger.debug("Refund Account Request handle is " + handle);
					}
					reqStr = methods[4];

					this.requestMap.put(new Integer(handle), request);
					errorCode = this.roClient.roRefundServiceReq(
											(RoRefundServiceReq)ccr.getStackImpl(),
											ccr.getDestinationInfo(),
											handle);
				break;

				case RO_CHECK_BALANCE:
					handle = this.getNextHandle();
					if(logger.isDebugEnabled()) {
						logger.debug("Check Balance Request handle is " + handle);
					}
					reqStr = methods[5];

					this.requestMap.put(new Integer(handle), request);
					errorCode = this.roClient.roBalanceCheckReq(
											(RoBalanceCheckReq)ccr.getStackImpl(),
											ccr.getDestinationInfo(),
											handle);
				break;

				case RO_PRICE_ENQUERY:
					handle = this.getNextHandle();
					if(logger.isDebugEnabled()) {
						logger.debug("Price Enquiry Request handle is " + handle);
					}
					reqStr = methods[6];

					this.requestMap.put(new Integer(handle), request);
					errorCode = this.roClient.roServicePriceEnquiry(
											(RoServicePriceEnqRequest)ccr.getStackImpl(),
											ccr.getDestinationInfo(),
											handle);
				break;

				default:
					throw new RoStackException("Unknown RoRequest type");
			} // switch

			if(errorCode == RoResultType.RO_PASS ) {
				if(logger.isDebugEnabled()) {
					logger.debug(reqStr + "was successful");
				}

				session.setRoState(RoSession.RO_ACTIVE);
			} else {
				this.requestMap.remove(new Integer(handle));
				logger.error(reqStr + "failed with error code [" + errorCode + "]" + " for handle :" + session.getHandle());

				if(errorCode == RoResultType.RO_NO_FREE_SESSION ) {
					logger.error(reqStr + "failure cause [no free session available]");
				} else if(errorCode == RoResultType.RO_NULL_OBJ ) {
					logger.error(reqStr + "failure cause [request object is null]");
				}

				throw new RoStackException("Request could not be sent");
			}
		} catch(Throwable t) {
			logger.error("handleRequest failed", t );
			throw new RoStackException(t);
		}
	}

	/////////////////////////////////////////////////////////////

	///////////// processing direct debiting response ///////////

	public int roDirectDebitingResponse(	RoDirectDebitingResponse debitingResponse,
											int handle ) {
		if(logger.isDebugEnabled()) {
			logger.debug("roDirectDebitingResponse(): for request handle:" + handle);
		}
		logger.debug("Response code is: "+ debitingResponse.getResultCode() ); 
		if(debitingResponse.getResultCode() > 2999 ) {
			logger.debug("error response received ");
		}
		RoRequest request = (RoRequest)this.requestMap.remove(new Integer(handle));

		if(request == null) {
			logger.error("In roDirectDebitingResponse(): No corresponding request found");
			return 0;
		}
		
		try {
			RoResponse toResponse = RoMessageFactoryImpl.createResponse(
														debitingResponse,
														(RoSession)request.getSession(),
														request);
			this.ra.deliverResponse(toResponse);
			return 1;
		} catch (Exception ex) {
			logger.error("roDirectDebitingResponse() failed: ", ex);
			return 0;
		}
	}


	////////  processing service enquiery requset //////////////
	
	public int roServicePriceEnquiryResponse(	RoServicePriceEnqResponse servPriceEnqResponse,
												int handle ) {
		if(logger.isDebugEnabled()) {
			logger.debug(" roServicePriceEnquiryResponse(): for request handle:" + handle);
		}
		logger.debug("Response code is: "+ servPriceEnqResponse.getResultCode());
		if(servPriceEnqResponse.getResultCode() > 2999 ) {
			logger.debug("error response received ");
		}

		RoRequest request = (RoRequest)this.requestMap.remove(new Integer(handle));

		if(request == null) {
			logger.error("In roServicePriceEnquiryResponse(): No corresponding request found");
			return 0;
		}
		
		try {
			RoResponse toResponse = RoMessageFactoryImpl.createResponse(
														servPriceEnqResponse,
														(RoSession)request.getSession(),
														request);
			this.ra.deliverResponse(toResponse);
			return 1;
		} catch (Exception ex) {
			logger.error("roServicePriceEnquiryResponse() failed: ", ex);
			return 0;
		}
	}

	public int roBalanceCheckResponse(RoBalanceCheckResponse balCheckResponse, int handle) {
		if(logger.isDebugEnabled()) {
			logger.debug("roBalanceCheckResponse(): for request handle:" + handle);
		}
		logger.debug("Response code is: "+ balCheckResponse.getResultCode());
		if(balCheckResponse.getResultCode() > 2999 ) {
			logger.debug("error response received ");
		}

		RoRequest request = (RoRequest)this.requestMap.remove(new Integer(handle));
	
		if(request == null) {
			logger.error("In roBalanceCheckResponse(): No corresponding request found");
			return 0;
		}

		try {
			RoResponse toResponse = RoMessageFactoryImpl.createResponse(
														balCheckResponse,
														(RoSession)request.getSession(),
														request);
			this.ra.deliverResponse(toResponse);
			return 1;
		} catch (Exception ex) {
			logger.error("roBalanceCheckResponse() failed: ", ex);
			return 0;
		}
	}

	public int roRefundServiceResponse(RoRefundServiceResponse roRefServResponse, int handle) {
		if(logger.isDebugEnabled()) {
			logger.debug("roRefundServiceResponse(): for request handle: " + handle);
		}
		logger.debug("Response code is: "+ roRefServResponse.getResultCode());
		if(roRefServResponse.getResultCode() > 2999 ) {
			logger.debug("error response received ");
		}

		RoRequest request = (RoRequest)this.requestMap.remove(new Integer(handle));
	
		if(request == null) {
			logger.error("In roRefundServiceResponse(): No corresponding request found");
			return 0;
		}
	
		try {
			RoResponse toResponse = RoMessageFactoryImpl.createResponse(
														roRefServResponse,
														(RoSession)request.getSession(),
														request);
			this.ra.deliverResponse(toResponse);
			return 1;
		} catch (Exception ex) {
			logger.error("roBalanceCheckResponse() failed: ", ex);
			return 0;
		}
	}

	public int roSessionFirstInterogationRes(RoFirstInterogationRes firstIntResponse, int handle) {
		if(logger.isDebugEnabled()) {
			logger.debug("roSessionFirstInterogationRes(): for request handle: " + handle);
		}

		RoRequest request;
		logger.debug("response code is "+firstIntResponse.getResultCode());				
		if(firstIntResponse.getResultCode() > 2999 ) {
			logger.debug("error response received removing from map");
			request = (RoRequest)this.requestMap.remove(new Integer(handle));
		}
		else {
			request = (RoRequest)this.requestMap.get(new Integer(handle));
		}
	
		if(request == null) {
			logger.error("In roSessionFirstInterogationRes(): No corresponding request found");
			return 1;
		}

		try {
			RoResponse toResponse = RoMessageFactoryImpl.createResponse(
														firstIntResponse,
														(RoSession)request.getSession(),
														request);
			this.ra.deliverResponse(toResponse);
		} catch (Exception ex) {
			logger.error("roSessionFirstInterogationRes() failed: ", ex);
			return 1;
		}

		return 0;
	}

	public int roSessionUpdateInterogationRes(RoUpdateInterogationRes updateIntResponse, int handle) {
		if(logger.isDebugEnabled()) {
			logger.debug("roSessionUpdateInterogationRes(): for request handle:" + handle);
		}
		RoRequest request;
		if(updateIntResponse.getResultCode() > 2999 ) {
			logger.debug("error response received removing from map");
			request = (RoRequest)this.requestMap.remove(new Integer(handle));
		}
		else {
			request = (RoRequest)this.requestMap.get(new Integer(handle));
		}
	
		if(request == null) {
			logger.error("In roSessionUpdateInterogationRes(): No corresponding request found");
			return 0;
		}

		try {
			RoResponse toResponse = RoMessageFactoryImpl.createResponse(
															updateIntResponse,
															(RoSession)request.getSession(),
															request);
			this.ra.deliverResponse(toResponse);
			return 1;
		} catch (Exception ex) {
			logger.error("roSessionUpdateInterogationRes() failed: ", ex);
			return 0;
		}
	}


	public int roSessionFinalInterogationRes(RoFinalInterogationRes finalIntResponse, int handle) {
		if(logger.isInfoEnabled()) {
			logger.debug(" roSessionFinalInterogationRes() :called for request handle:" + handle);
		}
		if(finalIntResponse.getResultCode() > 2999 ) {
			logger.debug("error response received .");
		}
		RoRequest request = (RoRequest)this.requestMap.remove(new Integer(handle));
	
		if(request == null) {
			logger.error("In roSessionFinalInterogationRes(): No corresponding request found");
			return 0;
		}

		try {
			RoResponse toResponse = RoMessageFactoryImpl.createResponse(
														finalIntResponse,
														(RoSession)request.getSession(),
														request);
			this.ra.deliverResponse(toResponse);
			return 1;
		} catch (Exception ex) {
			logger.error("roSessionFinalInterogationRes() failed: ", ex);
			return 0;
		}
	}


	public int roProvideInterimRecord(RoUpdateInterogationReq req, int appHdl) {
		if(logger.isInfoEnabled()) {
			logger.info("roProvideInterimRecord: for handle " + appHdl + " called");
		}

		try {
			RoRequest request = (RoRequest)this.requestMap.get(new Integer(appHdl));
			SipApplicationSession appSession = null;
			if(request == null) {
				logger.error("In roProvideInterimRecord: No corresponding request found");
				return 1;
			} else {
				appSession = request.getApplicationSession();
			}
		
			logger.error("roProvideInterimRecord: for app session :" + appSession + " and handle: " + appHdl);
			RoResourceEvent event = new RoResourceEvent(
											this,
											RoResourceEvent.RO_INTERIM_TIMEOUT_EVENT,
											appSession);
			event.setMessage(request);
			this.ra.deliverEvent(event);
		} catch (Exception ex) {
			logger.error("roProvideInterimRecord() failed: ", ex);
		}

		return 1; // Indicating stack to not send given 'req'
	}

	public int roGrantService(int roGrantServReason, int appHdl) {
		if(logger.isDebugEnabled()) {
			logger.debug("roGrantService: reason is " + roGrantServReason);
		}

		try {
			RoRequest request = (RoRequest)this.requestMap.get(new Integer(appHdl));
			SipApplicationSession appSession = null;
			if(request == null) {
				logger.error("In roGrantService: No corresponding request found");
				return 1;
			} else {
				appSession = request.getApplicationSession();
			}

			RoResourceEvent event = new RoResourceEvent(
											this,
											RoResourceEvent.RO_GRANT_ENDUSER_SERVICE,
											appSession);
			event.setErrorCode(roGrantServReason); // Not an error-code though!
			event.setMessage(request);
			this.ra.deliverEvent(event);
		} catch(ResourceException e) {
			logger.error("In roGrantService", e);
			return 1;
		}

		return 0;
	}

	public int roTerminateEndUserService(int cause, int appHdl) {
		if(logger.isDebugEnabled()) {
			logger.debug("End user service to be terminated. The cause is : " + cause);
		}

		try {
			RoRequest request = (RoRequest)this.requestMap.remove(new Integer(appHdl));
			SipApplicationSession appSession = null;
			if(request == null) {
				logger.error("In roTerminateEndUserService: No corresponding request found");
				return 1;
			} else {
				appSession = request.getApplicationSession();
			}

			RoResourceEvent event = new RoResourceEvent(
											this,
											RoResourceEvent.RO_TERMINATE_ENDUSER_SERVICE,
											appSession);
			event.setErrorCode(cause);
			event.setMessage(request);
			this.ra.deliverEvent(event);
		} catch(ResourceException e) {
			logger.error("In roTerminateEndUserService", e);
			return 1;
		}

		return 0;	
	}

	//////////////////////// Implementation of Management APIs //////////////////////////

	public int notifyTxTimerOutEvent(int appHandle) {
		logger.debug("TX Timer out occurred");

		try {
			RoRequest request = (RoRequest)this.requestMap.remove(new Integer(appHandle));
			SipApplicationSession appSession = null;
			if(request == null) {
				logger.error("In notifyTxTimerOutEvent: No corresponding request found");
			} else {
				appSession = request.getApplicationSession();
			}

			RoResourceEvent event = new RoResourceEvent(
										this,
										RoResourceEvent.RO_TX_TIMEROUT_EVENT,
										appSession);
			event.setMessage(request);
			this.ra.deliverEvent(event);
		} catch(Exception e) {
			logger.error("In notifyTxTimerOutEvent", e);
			return 1;
		}

		return 0;
	}

	public int roNotifyDisconnectPeerRequest(int cause) {
		if(logger.isDebugEnabled()) {
			logger.debug("roNotifyDisconnectPeerRequest() is called with cause :" + cause);
			logger.debug("PEER DIAMETER NODE (CDF) wants to disconnect with the local DFN");
		}

		try {
			RoResourceEvent event = new RoResourceEvent(
										this,
										RoResourceEvent.RO_NOTIFY_DISCONNECT_PEER_REQUEST,
										null);
			event.setErrorCode(cause);
			this.ra.deliverEvent(event);
		} catch(ResourceException ee) {
			logger.error("In roNotifyDisconnectPeerRequest", ee);
			return 1;
		}

		return 0;
	}
	
	public int roDisconnectPeerResponse(int resultCode) {
		if(logger.isDebugEnabled()) {
			logger.debug("roDisconnectPeerResponse() is called");
		}

		try {
			RoResourceEvent event = new RoResourceEvent(
										this,
										RoResourceEvent.RO_DISCONNECT_PEER_RESPONSE,
										null);
			event.setErrorCode(resultCode);
			this.ra.deliverEvent(event);
		} catch(ResourceException e) {
			logger.error("In roDisconnectPeerResponse", e);
			return 1;
		}

		return 0;
	}
	
	public int roNotifyPeerDown(String peerName) {
		if(logger.isDebugEnabled()) {
			logger.debug("roNotifyPeerDown() is called");
		}

		logger.error("The peer down is : " + peerName);

		try {
			RoResourceEvent event = new RoResourceEvent(this, RoResourceEvent.RO_NOTIFY_PEER_DOWN, null);
			event.setData(peerName);
			this.ra.deliverEvent(event);
		} catch(ResourceException e) {
			logger.error("In roNotifyPeerDown",e);
			return 1;
		}

		return 0;
	}

	public int roNotifyPeerUp(String peerName) {
		if(logger.isDebugEnabled()) {
			logger.debug("roNotifyPeerUp() is called");
		}
		logger.error("The peer up is : " + peerName);

		try {
			RoResourceEvent event = new RoResourceEvent(this, RoResourceEvent.RO_NOTIFY_PEER_UP, null);
			event.setData(peerName);
			this.ra.deliverEvent(event);
		} catch(ResourceException e) {
			logger.error("In roNotifyPeerUp", e);
			return 1;
		}

		return 0;
	}
	
	public int roNotifyDfnUp(short dfnId) {
		String alarmMsg = "DFN with dfnId [" + dfnId + "] is up";
		logger.error(alarmMsg);

		//TODO this alarm to be added 	
		/*
		try {
				this.alarmService.sendAlarm(
					com.baypackets.ase.util.Constants.ALARM_DFN_UP,
					alarmMsg ); // TODO - shift
		} catch(Exception e) {
			logger.error("Unable to send alarm exception", e);
		}
		*/
		try {
			RoResourceEvent event = new RoResourceEvent(this, RoResourceEvent.RO_NOTIFY_DFN_UP, null);
			event.setData(new Short(dfnId));
			this.ra.deliverEvent(event);
		} catch(ResourceException ee) {
			logger.error("In roNotifyDfnUp", ee);
			return 1;
		}

		return 0;
	}

	public int roNotifyDfnDown(short dfnId) {
		String alarmMsg = "DFN with dfnId [" + dfnId + "] is down";
		logger.error(alarmMsg);

		try {
			this.alarmService.sendAlarm(
				com.baypackets.ase.util.Constants.ALARM_DFN_DOWN,
				alarmMsg ); // TODO - shift
		} catch(Exception e) {
			logger.error("Unable to send alarm exception", e);
		}

		try {
			RoResourceEvent event = new RoResourceEvent(this, RoResourceEvent.RO_NOTIFY_DFN_DOWN, null);
			event.setData(new Short(dfnId));
			this.ra.deliverEvent(event);
		} catch(ResourceException ee) {
			logger.error("In roNotifyDfnDown", ee);
			return 1;
		}

		return 0;
	}

	public int roNotifyDfnDownEvent(short dfnId) {
		if(logger.isDebugEnabled()) {
			logger.debug("roNotifyDfnDownEvent called for dfnId : " + dfnId);
		}

		return this.roNotifyDfnDown(dfnId);
	}

	public int roNotifyUnexpectedMsgEvent(RoStateInfo stateInfo, int appHdl) {
		if(logger.isDebugEnabled()) {
			logger.debug("the current state information is " + stateInfo.getCurrentStateInfo());
			logger.debug("Stack expects ["+ stateInfo.getExpectedMsg() + "]");
			logger.debug("Stack received [" + stateInfo.getReceivedMsg() + "]");
		}
		
		try {
			RoRequest request = (RoRequest)this.requestMap.get(new Integer(appHdl));
			SipApplicationSession appSession = null;
			if(request == null) {
				logger.error("In roNotifyUnexpectedMsgEvent: No corresponding request found");
			} else {
				appSession = request.getApplicationSession();
			}

			RoResourceEvent event = new RoResourceEvent(
											this,
											RoResourceEvent.RO_NOTIFY_UNEXPECTED_MESSAGE,
											appSession);
			event.setData(stateInfo.getCurrentStateInfo());
			this.ra.deliverEvent(event);
			// set state to inactive. TODO - shift
		} catch(ResourceException e) {
			logger.error("In roNotifyUnexpectedMsgEvent", e);
			return 1;
		}

		return 0;
	}

	public int roNotifyErrorEvent(ErrorInfo errorInfo) {
		if(logger.isDebugEnabled()) {
			logger.debug("The application handle in ErrorInfo is " + errorInfo.getAppHdl());
			logger.debug("The error code in ErrorInfo is " + errorInfo.getErrorCode());
			logger.debug("The message type in ErrorInfo is " + errorInfo.getMsgType());
			logger.debug("The state in ErrorInfo is " + errorInfo.getState());
		}
	
		try {
			RoRequest request = (RoRequest)this.requestMap.get(new Integer(errorInfo.getAppHdl()));
			SipApplicationSession appSession = null;
			if(request == null) {
				logger.error("In roNotifyErrorEvent: No corresponding request found");
				return 1;
			} else {
				appSession = request.getApplicationSession();
			}

			String eventType;
			switch(errorInfo.getErrorCode()) {
				case 0:
					eventType = RoResourceEvent.RO_HB_HID_ERROR;
				break;

				case 1:
					eventType = RoResourceEvent.RO_ETE_ID_ERROR;
				break;

				case 2:
					eventType = RoResourceEvent.RO_SESSION_STR_ERRROR;
				break;

				case 3:
					eventType = RoResourceEvent.RO_AAA_XML_VALIDATION_ERROR;
				break;

				case 4:
					eventType = RoResourceEvent.RO_AAA_INVALID_AVP_VALUE;
				break;

				default:
					logger.error("Invalid error code from stack!");
					return 1;
			}
			RoResourceEvent event = new RoResourceEvent(
											this,
											eventType,
											appSession);
			event.setMessage(request);
			event.setData(errorInfo);
			this.ra.deliverEvent(event);
		} catch(ResourceException e) {
			logger.error("In roNotifyErrorEvent", e);
			return 1;
		}

		return 0;
	}

	public int roNotifyUnknownReqType(String stateName, int appHdl) {
		logger.debug("inside roNotifyUnknownReqType.");

		try {
			SipApplicationSession appSession = null;
			RoRequest request = (RoRequest)this.requestMap.remove(new Integer(appHdl));
			if(request == null) {
				logger.error("In roNotifyUnknownReqType: No corresponding request found");
			} else {
				appSession = request.getApplicationSession();
			}

			RoResourceEvent event = new RoResourceEvent(
											this,
											RoResourceEvent.RO_UNKNOWN_REQ_TYPE,
											appSession);

			event.setMessage(request);
			event.setData(stateName);
			this.ra.deliverEvent(event);
		} catch(ResourceException e) {
			logger.error("In roNotifyUnknownReqType", e);
			return 1;
		}

		return 0;
	}
	
	public int roNotifyUnexpectedReqAction(int appHdl) {
		logger.debug("Inside roNotifyUnexpectedReqAction.");

		try {
			SipApplicationSession appSession = null;
			RoRequest request = (RoRequest)this.requestMap.remove(new Integer(appHdl));
			if(request == null) {
				logger.error("In roNotifyUnexpectedReqAction: No corresponding request found");
				return 1;
			} else {
				appSession = request.getApplicationSession();
			}

			RoResourceEvent event = new RoResourceEvent(
											this,
											RoResourceEvent.RO_UNEXPECTED_REQUESTED_ACTION,
											appSession);

			event.setMessage(request);
			this.ra.deliverEvent(event);
		} catch(ResourceException e) {
			logger.error("In roNotifyUnexpectedReqAction", e );
			return 1;
		}

		return 0;
	}

	public int roDelAcctRecFromNonVolatileStorage(int hdl) {
		logger.error("In roDelAcctRecFromNonVolatileStorage() with handle: " + hdl + " [NOOP]");
		// Note :- Pending FT implementation
		return 0;
	}	

	public int roStoreAcctRecInNonVolatileStorage(RoAcctRecForStorage roStorage, int hdl) {
		logger.error("In roStoreAcctRecInNonVolatileStorage() with handle: " + hdl + " [NOOP]");
		// Note :- Pending FT implementation
		return 0;
	}

	public String roRedirectNotification(RoRedirectInfo redirInfo) {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside roRedirectNotification.");
			logger.debug("Redirect host cache time: " + redirInfo.getRedirectHostCacheTime());
			logger.debug("Redirect usage: " + redirInfo.getRedirectUsage());
		}

		try {
			RoResourceEvent event = new RoResourceEvent(
											this,
											RoResourceEvent.RO_REDIRECT_NOTIFICATION,
											null);

			event.setData(redirInfo);
			this.ra.deliverEvent(event);
		} catch(ResourceException e) {
			logger.error("In roRedirectNotification", e );
		}

		return null;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////

	private synchronized int getNextHandle() {
		return this.handleCount++;
	}

	public void addRequestToMap(int handle , RoRequest request ) {
		this.requestMap.put(new Integer(handle) , request);
	}
}
