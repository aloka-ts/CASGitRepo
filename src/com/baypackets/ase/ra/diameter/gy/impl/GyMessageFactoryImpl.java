package com.baypackets.ase.ra.diameter.gy.impl;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.gy.CreditControlRequest;
import com.baypackets.ase.ra.diameter.gy.GyMessage;
import com.baypackets.ase.ra.diameter.gy.stackif.CreditControlRequestImpl;
import com.baypackets.ase.ra.diameter.gy.stackif.GyAbstractRequest;
import com.baypackets.ase.ra.diameter.gy.stackif.GyStackInterfaceImpl;
import com.baypackets.ase.ra.diameter.gy.utils.Constants;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.spi.container.SasMessage;
import com.baypackets.ase.spi.container.SasProtocolSession;
import com.baypackets.ase.spi.resource.MessageFactory;
import com.baypackets.ase.spi.resource.ResourceContext;
import com.traffix.openblox.core.enums.AvpFormat;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.coding.DiameterVendorSpecificAvpSet;
import com.traffix.openblox.diameter.gy.generated.avp.AvpMultipleServicesCreditControl;
import com.traffix.openblox.diameter.gy.generated.avp.AvpRequestedServiceUnit;
import com.traffix.openblox.diameter.gy.generated.avp.AvpSubscriptionId;
import com.traffix.openblox.diameter.gy.generated.avp.AvpUsedServiceUnit;
import com.traffix.openblox.diameter.gy.generated.enums.EnumCCRequestType;
import com.traffix.openblox.diameter.gy.generated.enums.EnumMultipleServicesIndicator;
import com.traffix.openblox.diameter.gy.generated.enums.EnumSubscriptionIdType;
import com.traffix.openblox.diameter.gy.generated.enums.EnumTerminationCause;
import com.traffix.openblox.diameter.gy.generated.event.MessageCCR;
import com.traffix.openblox.diameter.gy.generated.session.SessionGyClient;

public class GyMessageFactoryImpl implements GyMessageFactory, Constants {

	private static Logger logger = Logger.getLogger(GyMessageFactoryImpl.class);

	private ResourceContext context;
	private MessageFactory msgFactory;
	private static GyMessageFactoryImpl GyMessageFactory;


	/**
	 *	Default constructor for creating SmppMessageFactory object.
	 *
	 */
	public GyMessageFactoryImpl(){
		logger.debug("creating GyMessageFactory object");
		GyMessageFactory=this;
	}

	/**
	 *	This  method returns the instance of SmppMessageFactory.
	 *
	 *	@return SmppMessageFactory object.
	 */
	public static GyMessageFactory getInstance(){
		if(GyMessageFactory==null){
			GyMessageFactory = new GyMessageFactoryImpl();
		}
		return GyMessageFactory;
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
		logger.debug("Setting ShMessageFactory in  RfResourceAdaptorFactory");
		GyResourceAdaptorFactory.setMessageFactory(this);
	}

	public SasMessage createRequest(SasProtocolSession session, int type)
	throws ResourceException {
		logger.debug("Inside createRequest(session,type)");
		GyAbstractRequest message = null;
		message = new CreditControlRequestImpl((GySession)session, type);
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
			int type) throws ResourceException {
		logger.debug("Inside createCCR():");
		CreditControlRequest request = (CreditControlRequest)this.msgFactory.createRequest(session,type);
		((GyMessage) request).setProtocolSession(session);
		//message.setResourceContext(this.context);
		logger.debug("leaving createRequest():");
		return request;
	}

	///////////////////////////////////////////////////////////////////////////////
	/////////////// stack object request creation methods /////////////////////////
	///////////////////////////////////////////////////////////////////////////////

	public static MessageCCR createCCR(SessionGyClient session,
			EnumCCRequestType requestType, long requestNumber) throws ValidationException {
		MessageCCR ccr=null;
		logger.debug("Inside createCCR with " );
		switch (requestType.getCode()){

		case EVENT_REQUEST :
			ccr=createEventRequest(session,requestNumber);
			break;
		case INITIAL_REQUEST :
			ccr=createInitialRequest(session,requestNumber);
			break;
		case UPDATE_REQUEST :
			ccr=createUpdateRequest(session, requestNumber);	
			break;
		case TERMINATION_REQUEST :
			ccr=createTerminationRequest(session, requestNumber);

		}
		return ccr;
	}

	private static MessageCCR createEventRequest(SessionGyClient session, long requestNumber) throws ValidationException{
		// Use the SessionCCClient to create a Credit-Control-Request
		// destination realm and destination host are given as parameters.
		MessageCCR ccr = session.createCCR(GyStackInterfaceImpl.serverRealm, null);
		/** adding mandatory avps first **/
		// Add to the CCR an Auth-Application-Id avp.
		ccr.addAuthApplicationId(GyStackInterfaceImpl.applicationId.getAppId());
		// Add to the CCR an CCRequestType enumerated avp.
		ccr.addCCRequestType(EnumCCRequestType.EVENT_REQUEST);
		// Add to the CCR a CC-Request-Number avp. 
		// This request number helps identify each request within a session.( Session-ID AVP identifies which session)
		ccr.addCCRequestNumber(requestNumber);
		//Adds the id of a requested service and the domain name of who allocated that id.
		//ccr.addServiceContextId("serviceContext@domain");
		return ccr;
	}

	private static MessageCCR createInitialRequest(SessionGyClient session, long requestNumber) throws ValidationException{
		// Use the SessionCCClient to create a Credit-Control-Request
		// destination realm and destination host are given as parameters.
		MessageCCR ccr = session.createCCR(GyStackInterfaceImpl.serverRealm, null);
		/** adding mandatory avps first **/
		// Add to the CCR an Auth-Application-Id avp.
		ccr.addAuthApplicationId(GyStackInterfaceImpl.applicationId.getAppId());
		// Add to the CCR an CCRequestType enumerated avp.
		ccr.addCCRequestType(EnumCCRequestType.INITIAL_REQUEST);
		// Add to the CCR a CC-Request-Number avp. 
		// This request number helps identify each request within a session.( Session-ID AVP identifies which session)
		ccr.addCCRequestNumber(requestNumber);
		//Adds the id of a requested service and the domain name of who allocated that id.
		//ccr.addServiceContextId("serviceContext@domain");
		return ccr;
	}

	private static MessageCCR createUpdateRequest(SessionGyClient session, long requestNumber) throws ValidationException{
		//Sending to same realm and host of the initial request.
		MessageCCR ccr = session.createCCR(GyStackInterfaceImpl.serverRealm, null);
		/** adding mandatory avps first **/ 
		// Add to the CCR an Auth-Application-Id avp.
		ccr.addAuthApplicationId(GyStackInterfaceImpl.applicationId.getAppId());
		// Add to the CCR an CCRequestType enumerated avp.
		//This is an Update Request.
		ccr.addCCRequestType(EnumCCRequestType.UPDATE_REQUEST);
		//Add CC-Request-Number
		ccr.addCCRequestNumber(requestNumber);
		return ccr;
	}

	private static MessageCCR createTerminationRequest(SessionGyClient session, long requestNumber) throws ValidationException{
		//Sending to same realm and host of the initial request.
		MessageCCR ccr = session.createCCR(GyStackInterfaceImpl.serverRealm, null);
		/** adding mandatory avps first **/ 
		// Add to the CCR an Auth-Application-Id avp.
		ccr.addAuthApplicationId(GyStackInterfaceImpl.applicationId.getAppId());
		// Add to the CCR an CCRequestType enumerated avp.
		//This is a Termination Request.
		ccr.addCCRequestType(EnumCCRequestType.TERMINATION_REQUEST);
		//Add CC-Request-Number
		ccr.addCCRequestNumber(requestNumber);
		return ccr;
	}
}

