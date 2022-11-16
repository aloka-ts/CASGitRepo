package com.baypackets.ase.ra.diameter.ro.impl;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.ro.CreditControlRequest;
import com.baypackets.ase.ra.diameter.ro.RoMessage;
import com.baypackets.ase.ra.diameter.ro.enums.CCRequestTypeEnum;
import com.baypackets.ase.ra.diameter.ro.stackif.CreditControlRequestImpl;
import com.baypackets.ase.ra.diameter.ro.stackif.RoAbstractRequest;
import com.baypackets.ase.ra.diameter.ro.stackif.RoStackClientInterfaceImpl;
import com.baypackets.ase.ra.diameter.ro.stackif.RoStackInterfaceImpl;
import com.baypackets.ase.ra.diameter.ro.utils.Constants;
import com.baypackets.ase.ra.diameter.ro.utils.RoStackConfig;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.spi.container.SasMessage;
import com.baypackets.ase.spi.container.SasProtocolSession;
import com.baypackets.ase.spi.resource.MessageFactory;
import com.baypackets.ase.spi.resource.ResourceContext;

import fr.marben.diameter.DiameterException;
import fr.marben.diameter.DiameterInvalidArgumentException;
import fr.marben.diameter.DiameterMessage;
import fr.marben.diameter.DiameterSession;
import fr.marben.diameter.DiameterStack;
import fr.marben.diameter._3gpp.ro.DiameterRoMessageFactory;

public class RoMessageFactoryImpl implements RoMessageFactory, Constants {

	private static Logger logger = Logger.getLogger(RoMessageFactoryImpl.class);

	private ResourceContext context;
	private MessageFactory msgFactory;
	private static RoMessageFactoryImpl roMessageFactory;
//	private static DiameterMessageFactory diameterMsgFactory=null;
	private static DiameterRoMessageFactory diameterRoMsgFactory=null;
	
	private static DiameterStack stack=null;

	/**
	 *	Default constructor for creating SmppMessageFactory object.
	 *
	 */
	public RoMessageFactoryImpl(){
		logger.debug("creating RoMessageFactory object");
		roMessageFactory=this;
	}

	/**
	 *	This  method returns the instance of SmppMessageFactory.
	 *
	 *	@return SmppMessageFactory object.
	 */
	public static RoMessageFactory getInstance(){
		if(roMessageFactory==null){
			roMessageFactory = new RoMessageFactoryImpl();
		}
		return roMessageFactory;
	}

	public void init(ResourceContext context) throws ResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside init(ResourceContext)");
		}
		this.context = context;
		this.msgFactory=context.getMessageFactory();
		if (this.msgFactory == null) {
			logger.error("init(): null message factory.");
		}
		logger.debug("Setting RoMessageFactory in  RoMessageFactoryImpl and diameter marben stack Rel13 and factories");
		RoResourceAdaptorFactory.setMessageFactory(this);
		//stack=((RoStackInterfaceImpl)RoResourceAdaptorImpl.stackInterface).getClientInterfaceDiameterStack();//.getDiameterStack();
		
		//diameterMsgFactory= stack.getDiameterMessageFactory();
	}

	public SasMessage createRequest(SasProtocolSession session, int type,String remoteRealm)
	throws ResourceException {
		logger.debug("Inside createRequest(session,type)");
		RoAbstractRequest message = null;
		message = new CreditControlRequestImpl((RoSession)session, type, remoteRealm);
		message.setProtocolSession(session);
		//message.setResourceContext(this.context);
		logger.debug("leaving createRequest():");
		return message;
	}

	public SasMessage createResponse(SasMessage request, int type)
	throws ResourceException {
		return null;
	}


	public SasMessage createResponse(SasMessage request) {
		// TODO Auto-generated method stub
		return null;
	}

	public CreditControlRequest createCreditControlRequest (SasProtocolSession session, 
			int type, String remoteRealm) throws ResourceException {
		logger.debug("Inside createCCR():");
		CreditControlRequest request = (CreditControlRequest)this.msgFactory.createRequest(session,type,remoteRealm);
		((RoMessage) request).setProtocolSession(session);
		//message.setResourceContext(this.context);
		logger.debug("leaving createRequest():");
		return request;
	}

	///////////////////////////////////////////////////////////////////////////////
	/////////////// stack object request creation methods /////////////////////////
	///////////////////////////////////////////////////////////////////////////////

	public static DiameterMessage createCCR(DiameterSession session,
			CCRequestTypeEnum requestType, long requestNumber, String remoteRealm) throws DiameterException,DiameterInvalidArgumentException {
		DiameterMessage ccr=null;
		logger.debug("Inside createCCR with " +requestType );
		
		String sessionId = null;
//		if (session == null && RoStackConfig.isStateless()) {
//			logger.debug("create session id for request for stateless request" + requestType);
//			sessionId = RoStackClientInterfaceImpl.roProvider
//					.getNextSessionIdValue();
//		} else {
//			sessionId = session.getSessionId();
//		}
		switch (CCRequestTypeEnum.getCode(requestType)){

		case EVENT_REQUEST :
			ccr=createEventRequest(requestNumber,sessionId,remoteRealm);
			break;
		case INITIAL_REQUEST :
			ccr=createInitialRequest(requestNumber,sessionId,remoteRealm);
			break;
		case UPDATE_REQUEST :
			ccr=createUpdateRequest( requestNumber,sessionId,remoteRealm);	
			break;
		case TERMINATION_REQUEST :
			ccr=createTerminationRequest(requestNumber,sessionId,remoteRealm);

		}
		return ccr;
	}

	private static DiameterMessage createEventRequest(long requestNumber, String sessionId, String remoteRealm) throws DiameterException,DiameterInvalidArgumentException{
		// Use the SessionCCClient to create a Credit-Control-Request
		// destination realm and destination host are given as parameters.
		//java.lang.String destinationRealm, int authApplicationId, java.lang.String serviceContextId, int CCRequestNumber)
		
		if(logger.isDebugEnabled()){
			logger.debug("Entering createEventRequest(): with requestNumber : "+requestNumber +
					" serverRealm "+ remoteRealm +" servicecontext "+RoStackClientInterfaceImpl.serverContextId );
		}
		
		if(logger.isDebugEnabled()){
			logger.debug("Entering createEventRequest() use diameterRoClientMsgFactory as : "+diameterRoMsgFactory);
		}
		
		DiameterMessage ccr = diameterRoMsgFactory.createCreditControlRequest(remoteRealm, 
				RoStackClientInterfaceImpl.serverContextId,DiameterRoMessageFactory.EVENT_REQUEST, requestNumber);//.session.createCCR(RoStackInterfaceImpl.serverRealm, RoStackInterfaceImpl.serverHost);
		
		if(logger.isDebugEnabled()){
			logger.debug("Entering createEventRequest() request created is  : "+ccr);
		}
		
		// Add Mandatory Requested-Action AVP
//		DiameterAVP avp = stack.getDiameterMessageFactory()
//				.createInteger32AVP(
//						"Requested-Action",
//						"base",
//						stack.findEnumCode("base", "Requested-Action",
//								DiameterRoMessageFactory.CHECK_BALANCE));

//		ccr.add(avp);
		
		/** adding mandatory avps first **/
		// Add to the CCR an Auth-Application-Id avp.
	//	ccr.addAuthApplicationId(RoStackInterfaceImpl.applicationId.getAppId());
//		DiameterAVP avp = diameterMsgFactory.createInteger32AVP(
//				"CC-Request-Type", "3GPP", CCRequestTypeEnum.getCode(CCRequestTypeEnum.EVENT_REQUEST));
//		ccr.add(avp);
		
		//avp= diameterMsgFactory.createInteger32AVP("CC-Request-Number", "3GPP", (int) requestNumber);
		
	//	ccr.add(avp);
		//String sessionId = RoStackInterfaceImpl.roProvider.getNextSessionIdValue();
		//ccr.setSessionIdAVPValue(sessionId);
		
		// Set End-To-End Id in the request
		ccr.setEndtoEndId(stack.getNextEndToEndId());
		
		// Add to the CCR an CCRequestType enumerated avp.
		//ccr.adaddCCRequestType(CCRequestTypeEnum.EVENT_REQUEST);
		// Add to the CCR a CC-Request-Number avp. 
		// This request number helps identify each request within a session.( Session-ID AVP identifies which session)
	//	ccr.addCCRequestNumber(requestNumber);
		//Adds the id of a requested service and the domain name of who allocated that id.
		//ccr.addServiceContextId("serviceContext@domain");
		return ccr;
	}

	private static DiameterMessage createInitialRequest(long requestNumber, String sessionId, String remoteRealm) throws DiameterException,DiameterInvalidArgumentException{
		// Use the SessionCCClient to create a Credit-Control-Request
		// destination realm and destination host are given as parameters.
		
		if(logger.isDebugEnabled()){
			logger.debug("Entering createInitialRequest(): with requestNumber as "+requestNumber+ "  Remote Realm "+remoteRealm);
		}
		
		DiameterMessage ccr = diameterRoMsgFactory.createCreditControlRequest(
				remoteRealm, RoStackClientInterfaceImpl.serverContextId,
				DiameterRoMessageFactory.INITIAL_REQUEST, (long) requestNumber);
	//	String sessionId = RoStackInterfaceImpl.roProvider.getNextSessionIdValue();
	//	ccr.setSessionIdAVPValue(sessionId);
		
		// Set End-To-End Id in the request
		ccr.setEndtoEndId(stack.getNextEndToEndId());
	//	MessageCCR ccr = session.createCCR(RoStackInterfaceImpl.serverRealm, RoStackInterfaceImpl.serverHost);
		/** adding mandatory avps first **/
		// Add to the CCR an Auth-Application-Id avp.
//		ccr.addAuthApplicationId(RoStackInterfaceImpl.applicationId.getAppId());
//		// Add to the CCR an CCRequestType enumerated avp.
//		ccr.addCCRequestType(EnumCCRequestType.INITIAL_REQUEST);
//		// Add to the CCR a CC-Request-Number avp. 
//		// This request number helps identify each request within a session.( Session-ID AVP identifies which session)
//		ccr.addCCRequestNumber(requestNumber);
		//Adds the id of a requested service and the domain name of who allocated that id.
		//ccr.addServiceContextId("serviceContext@domain");
		return ccr;
	}

	private static DiameterMessage createUpdateRequest(long requestNumber,String sessionId, String remoteRealm) throws DiameterException,DiameterInvalidArgumentException{
		//Sending to same realm and host of the initial request.
		
		if(logger.isDebugEnabled()){
			logger.debug("Entering createUpdateRequest(): with requestNumber as "+requestNumber+ "  Remote Realm "+remoteRealm);
		}
		DiameterMessage ccr = diameterRoMsgFactory.createCreditControlRequest(
				remoteRealm, RoStackClientInterfaceImpl.serverContextId,
				DiameterRoMessageFactory.UPDATE_REQUEST, (long) requestNumber);
		//String sessionId = RoStackInterfaceImpl.roProvider.getNextSessionIdValue();
	//	ccr.setSessionIdAVPValue(sessionId);
		
		// Set End-To-End Id in the request
		ccr.setEndtoEndId(stack.getNextEndToEndId());
//		MessageCCR ccr = session.createCCR(RoStackInterfaceImpl.serverRealm, RoStackInterfaceImpl.serverHost);
//		/** adding mandatory avps first **/ 
//		// Add to the CCR an Auth-Application-Id avp.
//		ccr.addAuthApplicationId(RoStackInterfaceImpl.applicationId.getAppId());
//		// Add to the CCR an CCRequestType enumerated avp.
//		//This is an Update Request.
//		ccr.addCCRequestType(EnumCCRequestType.UPDATE_REQUEST);
//		//Add CC-Request-Number
//		ccr.addCCRequestNumber(requestNumber);
		return ccr;
	}

	private static DiameterMessage createTerminationRequest(long requestNumber,String sessionId, String remoteRealm) throws DiameterException,DiameterInvalidArgumentException{
		//Sending to same realm and host of the initial request.
		
		if(logger.isDebugEnabled()){
			logger.debug("Entering createTerminationRequest(): with requestNumber as "+requestNumber + "  Remote Realm "+remoteRealm);
		}
		DiameterMessage ccr = diameterRoMsgFactory.createCreditControlRequest(
				remoteRealm, RoStackClientInterfaceImpl.serverContextId,
				DiameterRoMessageFactory.TERMINATION_REQUEST, (long) requestNumber);
	//	String sessionId = RoStackInterfaceImpl.roProvider.getNextSessionIdValue();
	//	ccr.setSessionIdAVPValue(sessionId);
		
		// Set End-To-End Id in the request
		ccr.setEndtoEndId(stack.getNextEndToEndId());
//		MessageCCR ccr = session.createCCR(RoStackInterfaceImpl.serverRealm, RoStackInterfaceImpl.serverHost);
//		/** adding mandatory avps first **/ 
//		// Add to the CCR an Auth-Application-Id avp.
//		ccr.addAuthApplicationId(RoStackInterfaceImpl.applicationId.getAppId());
//		// Add to the CCR an CCRequestType enumerated avp.
//		//This is a Termination Request.
//		ccr.addCCRequestType(EnumCCRequestType.TERMINATION_REQUEST);
//		//Add CC-Request-Number
//		ccr.addCCRequestNumber(requestNumber);
		return ccr;
	}
	
	public DiameterRoMessageFactory getDiameterRoMessageFactory(){
		return diameterRoMsgFactory;
		
	}
	
	public static void setDiameterRoMsgFactory(DiameterRoMessageFactory roMsgFactory) {
		
		if(logger.isDebugEnabled()){
			logger.debug("setDiameterRoClientMsgFactory as : "+roMsgFactory);
		}
		diameterRoMsgFactory = roMsgFactory;
	}
	
  public static void setDiameterRoClientStack(DiameterStack clientStack) {
		
		if(logger.isDebugEnabled()){
			logger.debug("setDiameterRoClientStack as : "+clientStack);
		}
	    stack=clientStack;
	}

	@Override
	public SasMessage createRequest(SasProtocolSession session, int type)
			throws ResourceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CreditControlRequest createCreditControlRequest(
			SasProtocolSession session, int type) throws ResourceException {
		// TODO Auto-generated method stub
		
		logger.error("createCreditControlRequepst create method by passing realm without realm not supported   as : ");
		return null;
	}

	@Override
	public SasMessage createRequest(SasProtocolSession session, int type,
			String remoteRealm, String msisdn) throws ResourceException {
		// TODO Auto-generated method stub
		return null;
	}

}


