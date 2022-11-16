package com.baypackets.ase.ra.diameter.gy.stackif;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.gy.GyResourceException;
import com.baypackets.ase.ra.diameter.gy.avp.AcctMultiSessionIdAvp;
import com.baypackets.ase.ra.diameter.gy.avp.AoCRequestTypeAvp;
import com.baypackets.ase.ra.diameter.gy.avp.AuthApplicationIdAvp;
import com.baypackets.ase.ra.diameter.gy.avp.CCCorrelationIdAvp;
import com.baypackets.ase.ra.diameter.gy.avp.CCRequestNumberAvp;
import com.baypackets.ase.ra.diameter.gy.avp.CCRequestTypeAvp;
import com.baypackets.ase.ra.diameter.gy.avp.CCSubSessionIdAvp;
import com.baypackets.ase.ra.diameter.gy.avp.EventTimestampAvp;
import com.baypackets.ase.ra.diameter.gy.avp.MultipleServicesCreditControlAvp;
import com.baypackets.ase.ra.diameter.gy.avp.MultipleServicesIndicatorAvp;
import com.baypackets.ase.ra.diameter.gy.avp.OriginStateIdAvp;
import com.baypackets.ase.ra.diameter.gy.avp.ProxyInfoAvp;
import com.baypackets.ase.ra.diameter.gy.avp.RequestedActionAvp;
import com.baypackets.ase.ra.diameter.gy.avp.RouteRecordAvp;
import com.baypackets.ase.ra.diameter.gy.avp.ServiceContextIdAvp;
import com.baypackets.ase.ra.diameter.gy.avp.ServiceIdentifierAvp;
import com.baypackets.ase.ra.diameter.gy.avp.ServiceInformationAvp;
import com.baypackets.ase.ra.diameter.gy.avp.ServiceParameterInfoAvp;
import com.baypackets.ase.ra.diameter.gy.avp.SubscriptionIdAvp;
import com.baypackets.ase.ra.diameter.gy.avp.TerminationCauseAvp;
import com.baypackets.ase.ra.diameter.gy.avp.UsedServiceUnitAvp;
import com.baypackets.ase.ra.diameter.gy.avp.UserEquipmentInfoAvp;
import com.baypackets.ase.ra.diameter.gy.avp.UserNameAvp;

import com.baypackets.ase.ra.diameter.gy.enums.AoCRequestTypeEnum;
import com.baypackets.ase.ra.diameter.gy.enums.CCRequestTypeEnum;
import com.baypackets.ase.ra.diameter.gy.enums.MultipleServicesIndicatorEnum;
import com.baypackets.ase.ra.diameter.gy.enums.RequestedActionEnum;
import com.baypackets.ase.ra.diameter.gy.enums.TerminationCauseEnum;

import com.baypackets.ase.ra.diameter.gy.impl.GyMessageFactoryImpl;
import com.baypackets.ase.ra.diameter.gy.impl.GySession;
import com.baypackets.ase.ra.diameter.gy.utils.Constants;
import com.baypackets.ase.ra.diameter.gy.CreditControlAnswer;
import com.baypackets.ase.ra.diameter.gy.CreditControlRequest;

import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.resource.Response;

import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.enums.ResultCode;
import com.traffix.openblox.diameter.gy.generated.avp.AvpAcctMultiSessionId;
import com.traffix.openblox.diameter.gy.generated.avp.AvpCCSubSessionId;
import com.traffix.openblox.diameter.gy.generated.avp.AvpGrantedServiceUnit;
import com.traffix.openblox.diameter.gy.generated.avp.AvpMultipleServicesCreditControl;
import com.traffix.openblox.diameter.gy.generated.avp.AvpProxyInfo;
import com.traffix.openblox.diameter.gy.generated.avp.AvpRequestedServiceUnit;
import com.traffix.openblox.diameter.gy.generated.avp.AvpServiceIdentifier;
import com.traffix.openblox.diameter.gy.generated.avp.AvpServiceParameterInfo;
import com.traffix.openblox.diameter.gy.generated.avp.AvpSubscriptionId;
import com.traffix.openblox.diameter.gy.generated.avp.AvpUsedServiceUnit;
import com.traffix.openblox.diameter.gy.generated.enums.EnumCCRequestType;
import com.traffix.openblox.diameter.gy.generated.enums.EnumMultipleServicesIndicator;
import com.traffix.openblox.diameter.gy.generated.enums.EnumRequestedAction;
import com.traffix.openblox.diameter.gy.generated.event.MessageCCA;
import com.traffix.openblox.diameter.gy.generated.event.MessageCCR;

public class CreditControlRequestImpl extends GyAbstractRequest implements CreditControlRequest , Constants{

	private static Logger logger = Logger.getLogger(CreditControlRequestImpl.class.getName());

	/**
	 *	This attribute contains the stack object which is to be used 
	 *	for all the set/get method
	 *
	 */
	private MessageCCR stackObj;
	private GySession m_GySession;
	private int retryCounter=0;

	public CreditControlRequestImpl(int type){
		super(type);
		System.out.println("Inside CreditControlRequestImpl constructor ");
	}

	public void setStackObj(MessageCCR stkObj){
		super.setStackObject(stkObj);
		this.stackObj=stkObj;
	}

	public MessageCCR getStackObj(){
		return stackObj;
	}

	public void incrementRetryCounter() {
		this.retryCounter++;
	}

	public int getRetryCounter() {
		return retryCounter;
	}

	public CreditControlRequestImpl(GySession GySession, int type){
		super(GySession);
		try {
			logger.debug("Inside RoAccountingRequestImpl(GySession) constructor ");
			switch (type) {
			case EVENT_REQUEST:
				logger.debug("Creating EVENT_REQUEST request");
				this.stackObj = GyMessageFactoryImpl.createCCR(GyStackInterfaceImpl.stackSession, 
						EnumCCRequestType.EVENT_REQUEST, 
						GyStackInterfaceImpl.getNextHandle());
				break;
			case INITIAL_REQUEST:
				logger.debug("Creating INITIAL_REQUEST request");
				this.stackObj = GyMessageFactoryImpl.createCCR(GyStackInterfaceImpl.stackSession, 
						EnumCCRequestType.INITIAL_REQUEST, 
						GyStackInterfaceImpl.getNextHandle());
				break;
			case UPDATE_REQUEST:
				logger.debug("Creating UPDATE_REQUEST request");
				this.stackObj = GyMessageFactoryImpl.createCCR(GyStackInterfaceImpl.stackSession, 
						EnumCCRequestType.UPDATE_REQUEST, 
						GyStackInterfaceImpl.getNextHandle());
				break;
			case TERMINATION_REQUEST:
				logger.debug("Creating TERMINATION_REQUEST request");
				this.stackObj = GyMessageFactoryImpl.createCCR(GyStackInterfaceImpl.stackSession, 
						EnumCCRequestType.TERMINATION_REQUEST, 
						GyStackInterfaceImpl.getNextHandle());
				break;
			default:
				logger.error("Wrong/Unkown request type.");
				throw new ResourceException("Wrong/Unkown request type.");
			}
			logger.debug("Stack object created ="+this.stackObj);
			super.setStackObject(this.stackObj);
			this.m_GySession=GySession;
		} catch (ValidationException e) {
			logger.debug("ValidationException in creating request ",e);
			//throw new RoResourceException("ValidationException in creating request ",e);
		} catch (ResourceException e) {
			logger.debug("Wrong/Unkown request type. ",e);
			//throw new RoResourceException("UWrong/Unknown request type. ",e);
		}
	}

	public void addRequest(){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside addRequest()");
		}
		this.m_GySession.addRequest(this);
	}
	////////////////////////////////////////////////////////////////////
	///////////// OPENBLOX Credit Control Request API STARTS ///////////
	////////////////////////////////////////////////////////////////////



	/**
	 *  Adding AoCRequestType AVP of type Enumerated to the message.
	 */
	public AoCRequestTypeAvp addAoCRequestType(AoCRequestTypeEnum value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addAoCRequestType()");
			}
			return new AoCRequestTypeAvp(stackObj.addAoCRequestType(AoCRequestTypeEnum.getStackObj(value)));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addAoCRequestType",e);
		}
	}

	/**
	 *  Adding AuthApplicationId AVP of type Unsigned32 to the message.
	 */
	public AuthApplicationIdAvp addAuthApplicationId(long value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addAuthApplicationId()");
			}
			return new AuthApplicationIdAvp(stackObj.addAuthApplicationId(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addAuthApplicationId",e);
		}
	}

	/**
	 *  Adding CCCorrelationId AVP of type OctetString to the message.
	 */
	public CCCorrelationIdAvp addCCCorrelationId(byte[] value, boolean mFlag) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addCCCorrelationId()");
			}
			return new CCCorrelationIdAvp(stackObj.addCCCorrelationId(value,mFlag));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addCCCorrelationId",e);
		}
	}

	/**
	 *  Adding CCCorrelationId AVP of type OctetString to the message.
	 */
	public CCCorrelationIdAvp addCCCorrelationId(java.lang.String value, boolean mFlag) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addCCCorrelationId()");
			}
			return new CCCorrelationIdAvp(stackObj.addCCCorrelationId(value,mFlag));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addCCCorrelationId",e);
		}
	}

	/**
	 *  Adding CCRequestNumber AVP of type Unsigned32 to the message.
	 */
	public CCRequestNumberAvp addCCRequestNumber(long value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addCCRequestNumber()");
			}
			return new CCRequestNumberAvp(stackObj.addCCRequestNumber(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addCCRequestNumber",e);
		}
	}

	/**
	 *  Adding CCRequestType AVP of type Enumerated to the message.
	 */
	public CCRequestTypeAvp addCCRequestType(CCRequestTypeEnum value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addCCRequestType()");
			}
			return new CCRequestTypeAvp(stackObj.addCCRequestType(CCRequestTypeEnum.getStackObj(value)));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addCCRequestType",e);
		}
	}

	/**
	 *  Adding EventTimestamp AVP of type Time to the message.
	 */
	public EventTimestampAvp addEventTimestamp(java.util.Date value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addEventTimestamp()");
			}
			return new EventTimestampAvp(stackObj.addEventTimestamp(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addEventTimestamp",e);
		}
	}

	/**
	 *  Adding MultipleServicesCreditControl AVP of type Grouped to the message.
	 */
	public MultipleServicesCreditControlAvp addGroupedMultipleServicesCreditControl() throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedMultipleServicesCreditControl()");
			}
			return new MultipleServicesCreditControlAvp(stackObj.addGroupedMultipleServicesCreditControl());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGroupedMultipleServicesCreditControl",e);
		}
	}

	/**
	 *  Adding ProxyInfo AVP of type Grouped to the message.
	 */
	public ProxyInfoAvp addGroupedProxyInfo() throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedProxyInfo()");
			}
			return new ProxyInfoAvp(stackObj.addGroupedProxyInfo());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGroupedProxyInfo",e);
		}
	}

	/**
	 *  Adding ServiceInformation AVP of type Grouped to the message.
	 */
	public ServiceInformationAvp addGroupedServiceInformation() throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedServiceInformation()");
			}
			return new ServiceInformationAvp(stackObj.addGroupedServiceInformation());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGroupedServiceInformation",e);
		}
	}

	/**
	 *  Adding SubscriptionId AVP of type Grouped to the message.
	 */
	public SubscriptionIdAvp addGroupedSubscriptionId() throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedSubscriptionId()");
			}
			return new SubscriptionIdAvp(stackObj.addGroupedSubscriptionId());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGroupedSubscriptionId",e);
		}
	}

	/**
	 *  Adding UserEquipmentInfo AVP of type Grouped to the message.
	 */
	public UserEquipmentInfoAvp addGroupedUserEquipmentInfo(boolean mFlag) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedUserEquipmentInfo()");
			}
			return new UserEquipmentInfoAvp(stackObj.addGroupedUserEquipmentInfo(mFlag));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGroupedUserEquipmentInfo",e);
		}
	}

	/**
	 *  Adding MultipleServicesIndicator AVP of type Enumerated to the message.
	 */
	public MultipleServicesIndicatorAvp addMultipleServicesIndicator(MultipleServicesIndicatorEnum value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addMultipleServicesIndicator() with "+value);
			}
			logger.debug("TODO STACK CCR OBJECT IS  "+stackObj);
			EnumMultipleServicesIndicator stkVal = MultipleServicesIndicatorEnum.getStackObj(value); 
			if(stkVal instanceof EnumMultipleServicesIndicator) {
				logger.debug("TRUE INSTANCE  "+MultipleServicesIndicatorEnum.getStackObj(value));
			}else{
				logger.debug("FALSE NOT AN INSTANCE ");
			}
			if(stkVal==EnumMultipleServicesIndicator.MULTIPLE_SERVICES_SUPPORTED){
				logger.debug(" NOW WHAT TO DO NEXT ");
			}else{
				logger.debug("OH SHIT.......................");
			}
			logger.debug("TODO STACK OBJECT IS  "+MultipleServicesIndicatorEnum.getStackObj(value));
			//logger.debug("TODO STACK OBJECT AVP IS  "+stackObj.addMultipleServicesIndicator(MultipleServicesIndicatorEnum.getStackObj(value)));
			return new MultipleServicesIndicatorAvp(stackObj.addMultipleServicesIndicator(stkVal));
			//return new MultipleServicesIndicatorAvp(stackObj.addMultipleServicesIndicator(MultipleServicesIndicatorEnum.getStackObj(value)));
			//return new MultipleServicesIndicatorAvp(stackObj.addMultipleServicesIndicator(EnumMultipleServicesIndicator.MULTIPLE_SERVICES_SUPPORTED));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addMultipleServicesIndicator",e);
		}
	}

	/**
	 *  Adding OriginStateId AVP of type Unsigned32 to the message.
	 */
	public OriginStateIdAvp addOriginStateId(long value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addOriginStateId()");
			}
			return new OriginStateIdAvp(stackObj.addOriginStateId(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addOriginStateId",e);
		}
	}

	/**
	 *  Adding RequestedAction AVP of type Enumerated to the message.
	 */
	public RequestedActionAvp addRequestedAction(RequestedActionEnum value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addRequestedAction()");
			}
			return new RequestedActionAvp(stackObj.addRequestedAction(RequestedActionEnum.getStackObj(value)));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addRequestedAction",e);
		}
	}

	/**
	 *  Adding RouteRecord AVP of type DiameterIdentity to the message.
	 */
	public RouteRecordAvp addRouteRecord(java.lang.String value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addRouteRecord()");
			}
			return new RouteRecordAvp(stackObj.addRouteRecord(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addRouteRecord",e);
		}
	}

	/**
	 *  Adding ServiceContextId AVP of type UTF8String to the message.
	 */
	public ServiceContextIdAvp addServiceContextId(java.lang.String value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addServiceContextId() "+this.stackObj);
			}
			return new ServiceContextIdAvp(this.stackObj.addServiceContextId(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addServiceContextId",e);
		}
	}

	/**
	 *  Adding TerminationCause AVP of type Enumerated to the message.
	 */
	public TerminationCauseAvp addTerminationCause(TerminationCauseEnum value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addTerminationCause()");
			}
			return new TerminationCauseAvp(stackObj.addTerminationCause(TerminationCauseEnum.getStackObj(value)));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addTerminationCause",e);
		}
	}

	/**
	 *  Adding UserName AVP of type UTF8String to the message.
	 */
	public UserNameAvp addUserName(java.lang.String value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addUserName()");
			}
			return new UserNameAvp(stackObj.addUserName(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addUserName",e);
		}
	}

	/**
	 *  Retrieving a single Enumerated value from AoCRequestType AVPs.
	 */
	public int getAoCRequestType() throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getAoCRequestType()");
			}
			return stackObj.getAoCRequestType();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getAoCRequestType",e);
		}
	}

	//	/**
	//	 *  Retrieving application id associated with this request.
	//	 * @throws GyResourceException 
	//	 */
	//	public long getApplicationId() throws GyResourceException {
	//		try {
	//			if(logger.isDebugEnabled()){
	//				logger.debug("Inside getApplicationId()");
	//			}
	//			return stackObj.getApplicationId();
	//		} catch (ValidationException e) {
	//			throw new GyResourceException("Exception in getApplicationId",e);
	//		}
	//	}


	/**
	 *  Retrieving a single Unsigned32 value from AuthApplicationId AVPs.
	 */
	public long getAuthApplicationId() throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getAuthApplicationId()");
			}
			return stackObj.getAuthApplicationId();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getAuthApplicationId",e);
		}
	}

	/**
	 *  Retrieving a single OctetString value from CCCorrelationId AVPs.
	 */
	public java.lang.String getCCCorrelationId() throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getCCCorrelationId()");
			}
			return stackObj.getCCCorrelationId();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getCCCorrelationId",e);
		}
	}

	/**
	 *  Retrieving a single Unsigned32 value from CCRequestNumber AVPs.
	 */
	public long getCCRequestNumber() throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getCCRequestNumber()");
			}
			return stackObj.getCCRequestNumber();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getCCRequestNumber",e);
		}
	}

	/**
	 *  Retrieving a single Enumerated value from CCRequestType AVPs.
	 */
	public int getCCRequestType() throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getCCRequestType()");
			}
			return stackObj.getCCRequestType();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getCCRequestType",e);
		}
	}

	//	/**
	//	 *  This method returns the command code associated with this message.
	//	 */
	//	public int getCommandCode() {
	//		try {
	//			if(logger.isDebugEnabled()){
	//				logger.debug("Inside getCommandCode()");
	//			}
	//			return stackObj.getCommandCode();
	//		} catch (ValidationException e) {
	//			throw new GyResourceException("Exception in getCommandCode",e);
	//		}
	//	}
	//
	//	/**
	//	 *  Retrieving a single DiameterIdentity value from DestinationHost AVPs.
	//	 */
	//	public java.lang.String getDestinationHost() {
	//		try {
	//			if(logger.isDebugEnabled()){
	//				logger.debug("Inside getDestinationHost()");
	//			}
	//			return stackObj.getDestinationHost();
	//		} catch (ValidationException e) {
	//			throw new GyResourceException("Exception in getDestinationHost",e);
	//		}
	//	}
	//
	//	/**
	//	 *  Retrieving a single DiameterIdentity value from DestinationRealm AVPs.
	//	 */
	//	public java.lang.String getDestinationRealm() {
	//		try {
	//			if(logger.isDebugEnabled()){
	//				logger.debug("Inside getDestinationRealm()");
	//			}
	//			return stackObj.getDestinationRealm();
	//		} catch (ValidationException e) {
	//			throw new GyResourceException("Exception in getDestinationRealm",e);
	//		}
	//	}

	/**
	 *  This mehtod returns the enum value associated with AoCRequestTypeAvp.
	 */
	public AoCRequestTypeEnum getEnumAoCRequestType() throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getEnumAoCRequestType()");
			}
			return AoCRequestTypeEnum.getContainerObj(stackObj.getEnumAoCRequestType());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getEnumAoCRequestType",e);
		}
	}


	/**
	 *  This mehtod returns the enum value associated with CCRequestTypeAvp.
	 */
	public CCRequestTypeEnum getEnumCCRequestType() throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getEnumCCRequestType()");
			}
			return CCRequestTypeEnum.getContainerObj(stackObj.getEnumCCRequestType());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getEnumCCRequestType",e);
		}
	}


	/**
	 *  This mehtod returns the enum value associated with MultipleServicesIndicatorAvp.
	 */
	public MultipleServicesIndicatorEnum getEnumMultipleServicesIndicator() throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getEnumMultipleServicesIndicator()");
			}
			return MultipleServicesIndicatorEnum.getContainerObj(stackObj.getEnumMultipleServicesIndicator());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getEnumMultipleServicesIndicator",e);
		}
	}


	/**
	 *  This mehtod returns the enum value associated with RequestedActionAvp.
	 */
	public RequestedActionEnum getEnumRequestedAction() throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getEnumRequestedAction()");
			}
			return RequestedActionEnum.getContainerObj(stackObj.getEnumRequestedAction());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getEnumRequestedAction",e);
		}
	}


	/**
	 *  This mehtod returns the enum value associated with TerminationCauseAvp.
	 */
	public TerminationCauseEnum getEnumTerminationCause() throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getEnumTerminationCause()");
			}
			return TerminationCauseEnum.getContainerObj(stackObj.getEnumTerminationCause());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getEnumTerminationCause",e);
		}
	}


	/**
	 *  Retrieving a single Time value from EventTimestamp AVPs.
	 */
	public java.util.Date getEventTimestamp() throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getEventTimestamp()");
			}
			return stackObj.getEventTimestamp();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getEventTimestamp",e);
		}
	}

	/**
	 *  Retrieving multiple Grouped values from MultipleServicesCreditControl AVPs.
	 */
	public MultipleServicesCreditControlAvp[] getGroupedMultipleServicesCreditControls() throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedMultipleServicesCreditControls()");
			}
			AvpMultipleServicesCreditControl[] stackAv= stackObj.getGroupedMultipleServicesCreditControls();
			MultipleServicesCreditControlAvp[] contAvp= new MultipleServicesCreditControlAvp[stackAv.length];
			for(int i=0;i<stackAv.length;i++){
				contAvp[i]=new MultipleServicesCreditControlAvp(stackAv[i]);
			}
			return contAvp;
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGroupedMultipleServicesCreditControls",e);
		}
	}

	/**
	 *  Retrieving multiple Grouped values from ProxyInfo AVPs.
	 */
	public ProxyInfoAvp[] getGroupedProxyInfos() throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedProxyInfos()");
			}
			AvpProxyInfo[] stackAv= stackObj.getGroupedProxyInfos();
			ProxyInfoAvp[] contAvp= new ProxyInfoAvp[stackAv.length];
			for(int i=0;i<stackAv.length;i++){
				contAvp[i]=new ProxyInfoAvp(stackAv[i]);
			}
			return contAvp;
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGroupedProxyInfos",e);
		}
	}

	/**
	 *  Retrieving a single Grouped value from ServiceInformation AVPs.
	 */
	public ServiceInformationAvp getGroupedServiceInformation() throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedServiceInformation()");
			}
			return new ServiceInformationAvp(stackObj.getGroupedServiceInformation());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGroupedServiceInformation",e);
		}
	}

	/**
	 *  Retrieving multiple Grouped values from SubscriptionId AVPs.
	 */
	public SubscriptionIdAvp[] getGroupedSubscriptionIds() throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedSubscriptionIds()");
			}
			AvpSubscriptionId[] stackAv= stackObj.getGroupedSubscriptionIds();
			SubscriptionIdAvp[] contAvp= new SubscriptionIdAvp[stackAv.length];
			for(int i=0;i<stackAv.length;i++){
				contAvp[i]=new SubscriptionIdAvp(stackAv[i]);
			}
			return contAvp;
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGroupedSubscriptionIds",e);
		}
	}

	/**
	 *  Retrieving a single Grouped value from UserEquipmentInfo AVPs.
	 */
	public UserEquipmentInfoAvp getGroupedUserEquipmentInfo() throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedUserEquipmentInfo()");
			}
			return new UserEquipmentInfoAvp(stackObj.getGroupedUserEquipmentInfo());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGroupedUserEquipmentInfo",e);
		}
	}

	/**
	 *  Retrieving a single Enumerated value from MultipleServicesIndicator AVPs.
	 */
	public int getMultipleServicesIndicator() throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getMultipleServicesIndicator()");
			}
			return stackObj.getMultipleServicesIndicator();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getMultipleServicesIndicator",e);
		}
	}

	//	/**
	//	 *  Retrieving a single DiameterIdentity value from OriginHost AVPs.
	//	 * @throws ResourceException 
	//	 */
	//	public java.lang.String getOriginHost() throws ResourceException {
	//		try {
	//			if(logger.isDebugEnabled()){
	//				logger.debug("Inside getOriginHost()");
	//			}
	//			return stackObj.getOriginHost();
	//		} catch (ValidationException e) {
	//			throw new GyResourceException("Exception in getOriginHost",e);
	//		}
	//	}
	//
	//	/**
	//	 *  Retrieving a single DiameterIdentity value from OriginRealm AVPs.
	//	 */
	//	public java.lang.String getOriginRealm() {
	//		try {
	//			if(logger.isDebugEnabled()){
	//				logger.debug("Inside getOriginRealm()");
	//			}
	//			return stackObj.getOriginRealm();
	//		} catch (ValidationException e) {
	//			throw new GyResourceException("Exception in getOriginRealm",e);
	//		}
	//	}

	/**
	 *  Retrieving a single Unsigned32 value from OriginStateId AVPs.
	 */
	public long getOriginStateId() throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getOriginStateId()");
			}
			return stackObj.getOriginStateId();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getOriginStateId",e);
		}
	}

	/**
	 *  Retrieving a single OctetString value from CCCorrelationId AVPs.
	 */
	public byte[] getRawCCCorrelationId() throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getRawCCCorrelationId()");
			}
			return stackObj.getRawCCCorrelationId();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getRawCCCorrelationId",e);
		}
	}

	/**
	 *  Retrieving a single Enumerated value from RequestedAction AVPs.
	 */
	public int getRequestedAction() throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getRequestedAction()");
			}
			return stackObj.getRequestedAction();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getRequestedAction",e);
		}
	}

	/**
	 *  Retrieving multiple DiameterIdentity values from RouteRecord AVPs.
	 */
	public java.lang.String[] getRouteRecords() throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getRouteRecords()");
			}
			return stackObj.getRouteRecords();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getRouteRecords",e);
		}
	}

	/**
	 *  Retrieving a single UTF8String value from ServiceContextId AVPs.
	 */
	public java.lang.String getServiceContextId() throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getServiceContextId()");
			}
			return stackObj.getServiceContextId();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getServiceContextId",e);
		}
	}

	//	/**
	//	 *  Retrieving a single UTF8String value from SessionId AVPs.
	//	 */
	//	public java.lang.String getSessionId() {
	//		try {
	//			if(logger.isDebugEnabled()){
	//				logger.debug("Inside getSessionId()");
	//			}
	//			return stackObj.getSessionId();
	//		} catch (ValidationException e) {
	//			throw new GyResourceException("Exception in getSessionId",e);
	//		}
	//	}

	/**
	 *  Retrieving a single Enumerated value from TerminationCause AVPs.
	 */
	public int getTerminationCause() throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getTerminationCause()");
			}
			return stackObj.getTerminationCause();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getTerminationCause",e);
		}
	}

	/**
	 *  Retrieving a single UTF8String value from UserName AVPs.
	 */
	public java.lang.String getUserName() throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getUserName()");
			}
			return stackObj.getUserName();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getUserName",e);
		}
	}

	

	@Override
	public AcctMultiSessionIdAvp addAcctMultiSessionId(long value, boolean mFlag)
			throws GyResourceException {
		// TODO Auto-generated method stub
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addCCSubSessionId()");
			}
			return new AcctMultiSessionIdAvp(stackObj.addAcctMultiSessionId(value,mFlag));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addCCSubSessionId",e);
		}
	}

	@Override
	public CCSubSessionIdAvp addCCSubSessionId(long value, boolean mFlag)
			throws GyResourceException {
		// TODO Auto-generated method stub
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addCCSubSessionId()");
			}
			return new CCSubSessionIdAvp(stackObj.addCCSubSessionId(value,mFlag));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addCCSubSessionId",e);
		}
	}

	@Override
	public ServiceParameterInfoAvp addGroupedServiceParameterInfo(boolean mFlag)
			throws GyResourceException {
		// TODO Auto-generated method stub
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedServiceParameterInfo()");
			}
			return new ServiceParameterInfoAvp(stackObj.addGroupedServiceParameterInfo(mFlag));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGroupedServiceParameterInfo",e);
		}
	}

	@Override
	public UsedServiceUnitAvp addGroupedUsedServiceUnit()
			throws GyResourceException {
		// TODO Auto-generated method stub
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedUsedServiceUnit()");
			}
			return new UsedServiceUnitAvp(stackObj.addGroupedUsedServiceUnit());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGroupedUsedServiceUnit",e);
		}
	}

	@Override
	public ServiceIdentifierAvp addServiceIdentifier(long value)
			throws GyResourceException {
		// TODO Auto-generated method stub
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addServiceIdentifier()");
			}
			return new ServiceIdentifierAvp(stackObj.addServiceIdentifier(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addServiceIdentifier",e);
		}
	}

	@Override
	public long getAcctMultiSessionId() throws GyResourceException {
		// TODO Auto-generated method stub
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getAcctMultiSessionId()");
			}
			return stackObj.getAcctMultiSessionId();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getAcctMultiSessionId",e);
		}
	}

	@Override
	public long getCCSubSessionId() throws GyResourceException {
		// TODO Auto-generated method stub
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getCCSubSessionId()");
			}
			return stackObj.getCCSubSessionId();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getCCSubSessionId",e);
		}
	}

	@Override
	public ServiceParameterInfoAvp[] getGroupedServiceParameterInfos()
			throws GyResourceException {
		// TODO Auto-generated method stub
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedServiceParameterInfos()");
			}
			AvpServiceParameterInfo[] stackAv= stackObj.getGroupedServiceParameterInfos();
			ServiceParameterInfoAvp[] contAvp= new ServiceParameterInfoAvp[stackAv.length];
			for(int i=0;i<stackAv.length;i++){
				contAvp[i]=new ServiceParameterInfoAvp(stackAv[i]);
			}
			return contAvp;
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGroupedServiceParameterInfos",e);
		}
	}

	@Override
	public UsedServiceUnitAvp[] getGroupedUsedServiceUnits()
			throws GyResourceException {
		// TODO Auto-generated method stub
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedUsedServiceUnits()");
			}
			AvpUsedServiceUnit[] stackAv= stackObj.getGroupedUsedServiceUnits();
			UsedServiceUnitAvp[] contAvp= new UsedServiceUnitAvp[stackAv.length];
			for(int i=0;i<stackAv.length;i++){
				contAvp[i]=new UsedServiceUnitAvp(stackAv[i]);
			}
			return contAvp;
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGroupedUsedServiceUnits",e);
		}
	}

	@Override
	public long getServiceIdentifier() throws GyResourceException {
		// TODO Auto-generated method stub
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getServiceIdentifier()");
			}
			return stackObj.getServiceIdentifier();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getServiceIdentifier",e);
		}
	}

	public CreditControlAnswer createAnswer(long l) throws GyResourceException {
		try {

			if(logger.isDebugEnabled()){
				logger.debug("Inside createAnswer(long)");
			}

			MessageCCA answer=null;

			answer = stackObj.createAnswer(l);

			answer.addAuthApplicationId(stackObj.getApplicationId());

			//Adding request type and number, so client will know on what we answered.
			answer.addCCRequestNumber(stackObj.getCCRequestNumber());
			answer.addCCRequestType(stackObj.getEnumCCRequestType());

			if(stackObj.getEnumCCRequestType() != EnumCCRequestType.TERMINATION_REQUEST) {
				//Adding Multiple-Service-Credit-Control
				//Here a response of granted units will be sent for each MSSC in the request.
				//We will determine granted units per Rating Grouped MSCC request, in reality it does not have to be so.
				//Checking for optional MULTIPLE SERVICES INDICATOR flag if present and set as MULTIPLE_SERVICES_SUPPORTED then perform this operation of adding MSCC in answer.
				int indicator=EnumMultipleServicesIndicator.MULTIPLE_SERVICES_NOT_SUPPORTED.getCode();
				try{
					indicator=stackObj.getMultipleServicesIndicator();
				}catch (ValidationException e) {
					logger.error("ValidationException in getMultipleServicesIndicator using default as MULTIPLE_SERVICES_NOT_SUPPORTED ");
				}
				if(EnumMultipleServicesIndicator.isValid(indicator)&& EnumMultipleServicesIndicator.fromCode(indicator).equals(EnumMultipleServicesIndicator.MULTIPLE_SERVICES_SUPPORTED)){
					AvpMultipleServicesCreditControl[] aMSCC = stackObj.getGroupedMultipleServicesCreditControls();
					for(int i=0; i<aMSCC.length; i++){
						fillMSCCAnswerPerRatingGroup(answer,aMSCC[i]);
					}
				}				
			}
			CreditControlAnswerImpl response=new CreditControlAnswerImpl(answer);
			response.setRequest(this);
			return response;

		} catch (ValidationException e) {
			throw new GyResourceException("Exception in createAnswer(long) ",e);
		}
	}



	public CreditControlAnswer createAnswer(long vendorId, long experimentalResultCode)
	throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside createAnswer(long)");
			}

			MessageCCA answer=null;

			answer = stackObj.createAnswer(vendorId,experimentalResultCode);

			answer.addAuthApplicationId(stackObj.getApplicationId());

			//Adding request type and number, so client will know on what we answered.
			answer.addCCRequestNumber(stackObj.getCCRequestNumber());
			answer.addCCRequestType(stackObj.getEnumCCRequestType());

			//Adding Multiple-Service-Credit-Control
			//Here a response of granted units will be sent for each MSSC in the request.
			//We will determine granted units per Rating Grouped MSCC request, in reality it does not have to be so.
			//Checking for optional MULTIPLE SERVICES INDICATOR flag if present and set as MULTIPLE_SERVICES_SUPPORTED then perform this operation of adding MSCC in answer.
			int indicator=EnumMultipleServicesIndicator.MULTIPLE_SERVICES_NOT_SUPPORTED.getCode();
			try{
				indicator=stackObj.getMultipleServicesIndicator();
			}catch (ValidationException e) {
				logger.error("ValidationException in getMultipleServicesIndicator using default as MULTIPLE_SERVICES_NOT_SUPPORTED");
			}
			if(EnumMultipleServicesIndicator.isValid(indicator)&& EnumMultipleServicesIndicator.fromCode(indicator).equals(EnumMultipleServicesIndicator.MULTIPLE_SERVICES_SUPPORTED)){
				AvpMultipleServicesCreditControl[] aMSCC = stackObj.getGroupedMultipleServicesCreditControls();
				for(int i=0; i<aMSCC.length; i++){
					fillMSCCAnswerPerRatingGroup(answer,aMSCC[i]);
				}
			}				
			CreditControlAnswerImpl response=new CreditControlAnswerImpl(answer);
			response.setRequest(this);
			return response;
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in createAnswer(long) ",e);
		}

	}

	@Override
	public Response createResponse(int arg0) throws ResourceException {
		// TODO Auto-generated method stub
		return null;
	}

	//Handle answer for each service this server supports.
	//
	private void fillMSCCAnswerPerRatingGroup(MessageCCA answer, AvpMultipleServicesCreditControl gRequestMSCC) throws ValidationException
	{
		long[] serviceIdentifers =gRequestMSCC.getServiceIdentifiers();
		//Adding Multiple-Service-Credit-Control to answer
		/** adding Avps from request, so answer could be identified by client properly **/
		AvpMultipleServicesCreditControl gAvpMSCC = answer.addGroupedMultipleServicesCreditControl();
		//Identifying the requested service
		for(int i=0;i<serviceIdentifers.length;i++){
			gAvpMSCC.addServiceIdentifier(serviceIdentifers[i]);
		}

		//Adding RequestedServiceUnit to it. 
		AvpRequestedServiceUnit mscc_gAvpRSU = gAvpMSCC
		.addGroupedRequestedServiceUnit();
		mscc_gAvpRSU.addCCTotalOctets(gRequestMSCC
				.getGroupedRequestedServiceUnit().getCCTotalOctets());
		gAvpMSCC.addResultCode(ResultCode.SUCCESS);

		if(gRequestMSCC.getRatingGroup()==1) {
			//Adding GrantedServiceUnit
			AvpGrantedServiceUnit gAvpGSU = gAvpMSCC.addGroupedGrantedServiceUnit();
			//Request Granted, after the usage of the following granted resource another CCR MUST be sent by client.
			gAvpGSU.addCCTotalOctets(gRequestMSCC.getGroupedRequestedServiceUnit()
					.getCCTotalOctets());
		}
		else if(gRequestMSCC.getRatingGroup()==2){
			//Adding GrantedServiceUnit
			AvpGrantedServiceUnit gAvpGSU = gAvpMSCC.addGroupedGrantedServiceUnit();
			//Request Granted, after the usage of the following granted resource another CCR MUST be sent by client.
			gAvpGSU.addCCTotalOctets(gRequestMSCC.getGroupedRequestedServiceUnit()
					.getCCTotalOctets());
			//In this case, we also add a validation time.
			//After the validation time expires the client MUST send an update request.
			gAvpMSCC.addValidityTime(3000L);


		}
	}
}
