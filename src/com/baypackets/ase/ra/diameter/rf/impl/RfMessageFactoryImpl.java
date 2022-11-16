package com.baypackets.ase.ra.diameter.rf.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.rf.RfAccountingRequest;
import com.baypackets.ase.ra.diameter.rf.RfMessage;
import com.baypackets.ase.ra.diameter.rf.stackif.RfAbstractRequest;
import com.baypackets.ase.ra.diameter.rf.stackif.RfAccountingRequestImpl;
import com.baypackets.ase.ra.diameter.rf.stackif.RfStackInterfaceImpl;
import com.baypackets.ase.ra.diameter.rf.utils.Constants;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.spi.container.SasMessage;
import com.baypackets.ase.spi.container.SasProtocolSession;
import com.baypackets.ase.spi.resource.MessageFactory;
import com.baypackets.ase.spi.resource.ResourceContext;
import com.traffix.openblox.core.enums.AvpFormat;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.coding.DiameterVendorSpecificAvpSet;
import com.traffix.openblox.diameter.rf.generated.avp.AvpSMSInformation;
import com.traffix.openblox.diameter.rf.generated.avp.AvpServiceInformation;
import com.traffix.openblox.diameter.rf.generated.enums.EnumAccountingRecordType;
import com.traffix.openblox.diameter.rf.generated.enums.EnumSMSNode;
import com.traffix.openblox.diameter.rf.generated.event.MessageACR;
import com.traffix.openblox.diameter.rf.generated.session.SessionRfClient;

public class RfMessageFactoryImpl implements RfMessageFactory, Constants {

	private static Logger logger = Logger.getLogger(RfMessageFactoryImpl.class);

	private ResourceContext context;
	private MessageFactory msgFactory;
	private static RfMessageFactoryImpl rfMessageFactory;


	/**
	 *	Default constructor for creating SmppMessageFactory object.
	 *
	 */
	public RfMessageFactoryImpl(){
		logger.debug("creating RfMessageFactory object");
		rfMessageFactory=this;
	}

	/**
	 *	This  method returns the instance of SmppMessageFactory.
	 *
	 *	@return SmppMessageFactory object.
	 */
	public static RfMessageFactory getInstance(){
		if(rfMessageFactory==null){
			rfMessageFactory = new RfMessageFactoryImpl();
		}
		return rfMessageFactory;
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
		RfResourceAdaptorFactory.setMessageFactory(this);
	}

	public SasMessage createRequest(SasProtocolSession session, int type)
	throws ResourceException {
		logger.debug("Inside createRequest(session,type)");
		RfAbstractRequest message = null;
		message = new RfAccountingRequestImpl((RfSession)session, type);
		message.setProtocolSession(session);
		//message.setResourceContext(this.context);
		logger.debug("leaving createRequest():");
		return message;
	}

	public SasMessage createResponse(SasMessage request, int type)
	throws ResourceException {
		//		ShRequest shRequest = (ShRequest)request;
		//				ShAbstractResponse message = null;
		//				switch (type) {
		//				case UDA:
		//					message = new ShUserDataResponseImpl(shRequest);
		//					break;
		//				case PUR:
		//				case PUA:
		//					message = new ShProfileUpdateResponse(shRequest);
		//					break;
		//				case SNR:
		//				case SNA:
		//					message = new ShSubscribeNotificationResponse(shRequest);
		//					break;
		//				case PNR:
		//				case PNA:
		//					message = new ShPushNotificationResponse(shRequest);
		//					break;
		//				default:
		//					logger.error("Wrong/Unkown response type.");
		//					throw new ResourceException("Wrong/Unkown response type.");
		//				}
		//				message.setProtocolSession(request.getProtocolSession());
		//				//message.setResourceContext(this.context);
		//				//logger.debug("createResponse(): ResourceContext is set to " + this.context);
		//				logger.debug("leaving createRequest():");
		//				return message;
		return null;
	}


	public SasMessage createResponse(SasMessage request) {
		// TODO Auto-generated method stub
		return null;
	}

	public RfAccountingRequest createAccountingRequest (SasProtocolSession session, 
			int type) throws ResourceException {
		logger.debug("Inside RfAccountingRequest():");
		RfAccountingRequest request = (RfAccountingRequest)this.msgFactory.createRequest(session,type);
		((RfMessage) request).setProtocolSession(session);
		//message.setResourceContext(this.context);
		logger.debug("leaving createRequest():");
		return request;
	}

	///////////////////////////////////////////////////////////////////////////////
	/////////////// stack object request creation methods /////////////////////////
	///////////////////////////////////////////////////////////////////////////////

	//	public static MessageUserDataRequest createUDR(SessionShClient session) throws ValidationException {
	//		//Creating User-Data-Request within the current session
	//		MessageUserDataRequest udr = session.createUserDataRequest(RfStackInterfaceImpl.serverRealm, RfStackInterfaceImpl.serverHost);  
	//		/** adding mandatory avps of request**/
	//		//Adding Vendor-Specific-Application-Id avp
	//		AvpVendorSpecificApplicationId gAvpVSI =udr.addGroupedVendorSpecificApplicationId();
	//		gAvpVSI.addVendorId(RfStackInterfaceImpl.applicationId.getVendorId());
	//		gAvpVSI.addAuthApplicationId(RfStackInterfaceImpl.applicationId.getAppId());
	//		//Adding Auth-Session-State avp
	//		//This indicates whether an STR will be sent when authorization is expired.
	//		udr.addAuthSessionState(EnumAuthSessionState.NO_STATE_MAINTAINED);
	//		//Adding User-Identity Avp
	//		//This is the user we want to be notified for each data change at server.
	//		AvpUserIdentity gAvpUI = udr.addGroupedUserIdentity();
	//		//Adding the user MSISDN number
	//		gAvpUI.addMSISDN("MSISDN value");
	//		return udr;
	//	}

	public static MessageACR createACR(SessionRfClient session,
			EnumAccountingRecordType recordType, long requestNumber) throws ValidationException, UnknownHostException {

		// Use the SessionClient to create a Accounting-Request
		// destination realm and destination host are given as parameters.
		MessageACR acr = session.createACR(RfStackInterfaceImpl.serverRealm, null);

		/** adding mandatory avps first **/
		// Add to the ACR an Accounting-Application-Id avp.
		acr.addAccountingApplicationId(RfStackInterfaceImpl.applicationId.getAppId());

		// Add to the ACR an Accounting-Record-Type enumerated avp.
		acr.addAccountingRecordType(recordType);

		// Add to the ACR a Accounting-Record-Number avp. 
		// This request number helps identify each request within a session.( Session-ID AVP identifies which session)
		acr.addAccountingRecordNumber(requestNumber);

		/** adding optional avps **/

//		//Adding User-Name AVP.
//		acr.addUserName("User-Name");
//		//Adding Service-Context-ID Avp.
//		//Adds the id of a requested service and the domain name of who allocated that id.
//		acr.addServiceContextId("serviceContext@domain");
//
//		//Adding Service-Information grouped AVP.
//		AvpServiceInformation gAvpGSI = acr.addGroupedServiceInformation();
//
//		//Add to it SMS-Information grouped AVP.
//		AvpSMSInformation gsi_gAvpSMSI =gAvpGSI.addGroupedSMSInformation();
//		//Add AVPs with the information.
//		gsi_gAvpSMSI.addClientAddress(InetAddress.getLocalHost());
//		gsi_gAvpSMSI.addNumberOfMessagesSent(2L);
//		gsi_gAvpSMSI.addSMSNode(EnumSMSNode.SMSRouter);
//
//		/**Examples of adding Vendor-Specific-Avps.**/
//
//		long vendorId = 27611; //vendor id
//		boolean mFlag=false; //mandatory flag of avp.
//		//Getting the VendorSpecificAvpSet
//		DiameterVendorSpecificAvpSet avpSet = acr.getVendorSpecificAvpSet();
//		avpSet.addAvp(1000, mFlag, vendorId, new String("MY_DIAMETER_ID").getBytes(), AvpFormat.DiameterIdentity);
//		avpSet.addAvp(1007, mFlag, vendorId, 56L, AvpFormat.Unsigned64);
//		avpSet.addAvp(1011, mFlag, vendorId, "Some Octet String", AvpFormat.OctetString);
//
//		//Adding Vendor-Specific Grouped Avp.
//		DiameterVendorSpecificAvpSet groupedAvpSet = avpSet.addGroupedAvp(1013, vendorId, mFlag);
//		//Adding avps to the grouped avp.
//		groupedAvpSet.addAvp(1014, false, vendorId, 14.4f);
//		groupedAvpSet.addAvp(1017, mFlag, vendorId, "Some UTF8 String", AvpFormat.UTF8String);

		return acr;
	}
}

