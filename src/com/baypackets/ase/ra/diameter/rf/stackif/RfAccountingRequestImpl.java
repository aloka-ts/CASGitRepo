package com.baypackets.ase.ra.diameter.rf.stackif;


import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.rf.RfAccountingRequest;
import com.baypackets.ase.ra.diameter.rf.RfAccountingResponse;
import com.baypackets.ase.ra.diameter.rf.RfResourceException;
import com.baypackets.ase.ra.diameter.rf.avp.AccountingApplicationIdAvp;
import com.baypackets.ase.ra.diameter.rf.avp.AccountingInterimIntervalAvp;
import com.baypackets.ase.ra.diameter.rf.avp.AccountingRecordNumberAvp;
import com.baypackets.ase.ra.diameter.rf.avp.AccountingRecordTypeAvp;
import com.baypackets.ase.ra.diameter.rf.avp.EventTimestampAvp;
import com.baypackets.ase.ra.diameter.rf.avp.OriginStateIdAvp;
import com.baypackets.ase.ra.diameter.rf.avp.ProxyInfoAvp;
import com.baypackets.ase.ra.diameter.rf.avp.RouteRecordAvp;
import com.baypackets.ase.ra.diameter.rf.avp.ServiceContextIdAvp;
import com.baypackets.ase.ra.diameter.rf.avp.ServiceInformationAvp;
import com.baypackets.ase.ra.diameter.rf.avp.UserNameAvp;
import com.baypackets.ase.ra.diameter.rf.enums.AccountingRecordTypeEnum;
import com.baypackets.ase.ra.diameter.rf.impl.RfMessageFactoryImpl;
import com.baypackets.ase.ra.diameter.rf.impl.RfSession;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.resource.Response;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.rf.generated.avp.AvpProxyInfo;
import com.traffix.openblox.diameter.rf.generated.enums.EnumAccountingRecordType;
import com.traffix.openblox.diameter.rf.generated.event.MessageACA;
import com.traffix.openblox.diameter.rf.generated.event.MessageACR;

public class RfAccountingRequestImpl extends RfAbstractRequest implements RfAccountingRequest {

	private static Logger logger = Logger.getLogger(RfAccountingRequestImpl.class.getName());

	/**
	 *	This attribute contains the stack object which is to be used 
	 *	for all the set/get method
	 *
	 */
	private MessageACR stackObj;
	private RfSession m_rfSession;
	private int retryCounter=0;

	public RfAccountingRequestImpl(int type){
		super(type);
		System.out.println("Inside ShUserDataRequestImpl constructor ");
	}

	public void setStackObj(MessageACR stkObj){
		super.setStackObject(stkObj);
		this.stackObj=stkObj;
	}

	public MessageACR getStackObj(){
		return stackObj;
	}

	public void incrementRetryCounter() {
		this.retryCounter++;
	}

	public int getRetryCounter() {
		return retryCounter;
	}

	public RfAccountingRequestImpl(RfSession shSession, int type){
		super(shSession);
		try {
			logger.debug("Inside RfAccountingRequestImpl(RfSession) constructor ");
			switch (type) {
			case EVENT_RECORD:
				logger.debug("Creating EVENT_RECORD request");
				this.stackObj = RfMessageFactoryImpl.createACR(RfStackInterfaceImpl.stackSession, 
						EnumAccountingRecordType.EVENT_RECORD, 
						RfStackInterfaceImpl.getNextHandle());
				break;
			case START_RECORD:
				logger.debug("Creating START_RECORD request");
				this.stackObj = RfMessageFactoryImpl.createACR(RfStackInterfaceImpl.stackSession, 
						EnumAccountingRecordType.START_RECORD, 
						RfStackInterfaceImpl.getNextHandle());
				break;
			case INTERIM_RECORD:
				logger.debug("Creating INTERIM_RECORD request");
				this.stackObj = RfMessageFactoryImpl.createACR(RfStackInterfaceImpl.stackSession, 
						EnumAccountingRecordType.INTERIM_RECORD, 
						RfStackInterfaceImpl.getNextHandle());
				break;
			case STOP_RECORD:
				logger.debug("Creating STOP_RECORD request");
				this.stackObj = RfMessageFactoryImpl.createACR(RfStackInterfaceImpl.stackSession, 
						EnumAccountingRecordType.STOP_RECORD, 
						RfStackInterfaceImpl.getNextHandle());
				break;
			default:
				logger.error("Wrong/Unkown request type.");
				throw new ResourceException("Wrong/Unkown request type.");
			}
			logger.debug("Stack object created ="+this.stackObj);
			super.setStackObject(this.stackObj);
			this.m_rfSession=shSession;
		} catch (ValidationException e) {
			logger.debug("ValidationException in creating request ",e);
			//throw new RfResourceException("ValidationException in creating request ",e);
		} catch (UnknownHostException e) {
			logger.debug("UnknownHostException in creating request ",e);
			//throw new RfResourceException("UnknownHostException in creating request ",e);
		} catch (ResourceException e) {
			logger.debug("Wrong/Unkown request type. ",e);
			//throw new RfResourceException("UWrong/Unkown request type. ",e);
		}
	}

	public void addRequest(){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside addRequest()");
		}
		this.m_rfSession.addRequest(this);
	}
	////////////////////////////////////////////////////////////////////
	///////////////////////// OPENBLOX COMMON API STARTS ///////////////
	////////////////////////////////////////////////////////////////////


	//	public RfAccountingResponse createAnswer(long l) throws ValidationException;
	//
	//	public RfAccountingResponse createAnswer(long l, long l1) throws ValidationException;


	public AccountingRecordTypeAvp addAccountingRecordType(
			AccountingRecordTypeEnum enumaccountingrecordtype)
	throws RfResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addAccountingRecordType()");
			}
			EnumAccountingRecordType stackEnum = AccountingRecordTypeEnum.getStackObj(enumaccountingrecordtype);
			return new AccountingRecordTypeAvp(stackObj.addAccountingRecordType(stackEnum));
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in addAccountingRecordType",e);
		}
	}


	public String getUserName() throws RfResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getUserName()");
			}
			return stackObj.getUserName();
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in getUserName",e);
		}
	}





	/**
	 *  Adding AccountingApplicationId AVP of type Unsigned32 to the message.
	 */
	public AccountingApplicationIdAvp addAccountingApplicationId(long value) throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addAccountingApplicationId()");
			}
			return new AccountingApplicationIdAvp(stackObj.addAccountingApplicationId(value));
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in addAccountingApplicationId",e);
		}
	}

	/**
	 *  Adding AccountingInterimInterval AVP of type Unsigned32 to the message.
	 */
	public AccountingInterimIntervalAvp addAccountingInterimInterval(long value) throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addAccountingInterimInterval()");
			}
			return new AccountingInterimIntervalAvp(stackObj.addAccountingInterimInterval(value));
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in addAccountingInterimInterval",e);
		}
	}

	/**
	 *  Adding AccountingRecordNumber AVP of type Unsigned32 to the message.
	 */
	public AccountingRecordNumberAvp addAccountingRecordNumber(long value) throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addAccountingRecordNumber()");
			}
			return new AccountingRecordNumberAvp(stackObj.addAccountingRecordNumber(value));
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in addAccountingRecordNumber",e);
		}
	}

	/**
	 *  Adding AccountingRecordType AVP of type Enumerated to the message.
	 */
	public AccountingRecordTypeAvp addAccountingRecordType(EnumAccountingRecordType value) throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addAccountingRecordType()");
			}
			return new AccountingRecordTypeAvp(stackObj.addAccountingRecordType(value));
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in addAccountingRecordType",e);
		}
	}

	/**
	 *  Adding EventTimestamp AVP of type Time to the message.
	 */
	public EventTimestampAvp addEventTimestamp(java.util.Date value) throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addEventTimestamp()");
			}
			return new EventTimestampAvp(stackObj.addEventTimestamp(value));
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in addEventTimestamp",e);
		}
	}

	/**
	 *  Adding ProxyInfo AVP of type Grouped to the message.
	 */
	public ProxyInfoAvp addGroupedProxyInfo() throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedProxyInfo()");
			}
			return new ProxyInfoAvp(stackObj.addGroupedProxyInfo());
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in addGroupedProxyInfo",e);
		}
	}

	/**
	 *  Adding ServiceInformation AVP of type Grouped to the message.
	 */
	public ServiceInformationAvp addGroupedServiceInformation() throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedServiceInformation()");
			}
			return new ServiceInformationAvp(stackObj.addGroupedServiceInformation());
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in addGroupedServiceInformation",e);
		}
	}

	/**
	 *  Adding OriginStateId AVP of type Unsigned32 to the message.
	 */
	public OriginStateIdAvp addOriginStateId(long value) throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addOriginStateId()");
			}
			return new OriginStateIdAvp(stackObj.addOriginStateId(value));
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in addOriginStateId",e);
		}
	}

	/**
	 *  Adding RouteRecord AVP of type DiameterIdentity to the message.
	 */
	public RouteRecordAvp addRouteRecord(java.lang.String value) throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addRouteRecord()");
			}
			return new RouteRecordAvp(stackObj.addRouteRecord(value));
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in addRouteRecord",e);
		}
	}

	/**
	 *  Adding ServiceContextId AVP of type UTF8String to the message.
	 */
	public ServiceContextIdAvp addServiceContextId(java.lang.String value) throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addServiceContextId()");
			}
			return new ServiceContextIdAvp(stackObj.addServiceContextId(value));
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in addServiceContextId",e);
		}
	}

	/**
	 *  Adding UserName AVP of type UTF8String to the message.
	 */
	public UserNameAvp addUserName(java.lang.String value) throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addUserName()");
			}
			return new UserNameAvp(stackObj.addUserName(value));
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in addUserName",e);
		}
	}

	/**
	 *  Retrieving a single Unsigned32 value from AccountingApplicationId AVPs.
	 */
	public long getAccountingApplicationId() throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getAccountingApplicationId()");
			}
			return stackObj.getAccountingApplicationId();
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in getAccountingApplicationId",e);
		}
	}

	/**
	 *  Retrieving a single Unsigned32 value from AccountingInterimInterval AVPs.
	 */
	public long getAccountingInterimInterval() throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getAccountingInterimInterval()");
			}
			return stackObj.getAccountingInterimInterval();
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in getAccountingInterimInterval",e);
		}
	}

	/**
	 *  Retrieving a single Unsigned32 value from AccountingRecordNumber AVPs.
	 */
	public long getAccountingRecordNumber() throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getAccountingRecordNumber()");
			}
			return stackObj.getAccountingRecordNumber();
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in getAccountingRecordNumber",e);
		}
	}

	/**
	 *  Retrieving a single Enumerated value from AccountingRecordType AVPs.
	 */
	public int getAccountingRecordType() throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getAccountingRecordType()");
			}
			return stackObj.getAccountingRecordType();
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in getAccountingRecordType",e);
		}
	}

	/**
	 *  This method returns the Enum value for AccourintRecordType AVP for this request.
	 */
	public AccountingRecordTypeEnum getEnumAccountingRecordType() throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getEnumAccountingRecordType()");
			}
			return AccountingRecordTypeEnum.getContainerObj(stackObj.getEnumAccountingRecordType());
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in getEnumAccountingRecordType",e);
		}
	}


	/**
	 *  Retrieving a single Time value from EventTimestamp AVPs.
	 */
	public java.util.Date getEventTimestamp() throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getEventTimestamp()");
			}
			return stackObj.getEventTimestamp();
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in getEventTimestamp",e);
		}
	}

	/**
	 *  Retrieving multiple Grouped values from ProxyInfo AVPs.
	 */
	public ProxyInfoAvp[] getGroupedProxyInfos() throws RfResourceException { 

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
			throw new RfResourceException("Exception in getGroupedProxyInfos",e);
		}
	}

	/**
	 *  Retrieving a single Grouped value from ServiceInformation AVPs.
	 */
	public ServiceInformationAvp getGroupedServiceInformation() throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedServiceInformation()");
			}
			return new ServiceInformationAvp(stackObj.getGroupedServiceInformation());
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in getGroupedServiceInformation",e);
		}
	}

	/**
	 *  Retrieving a single Unsigned32 value from OriginStateId AVPs.
	 */
	public long getOriginStateId() throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getOriginStateId()");
			}
			return stackObj.getOriginStateId();
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in getOriginStateId",e);
		}
	}

	/**
	 *  Retrieving multiple DiameterIdentity values from RouteRecord AVPs.
	 */
	public java.lang.String[] getRouteRecords() throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getRouteRecords()");
			}
			return stackObj.getRouteRecords();
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in getRouteRecords",e);
		}
	}

	/**
	 *  Retrieving a single UTF8String value from ServiceContextId AVPs.
	 */
	public java.lang.String getServiceContextId() throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getServiceContextId()");
			}
			return stackObj.getServiceContextId();
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in getServiceContextId",e);
		}
	}

	@Override
	public RfAccountingResponse createAnswer(long l) throws RfResourceException {
		try {
		
			if(logger.isDebugEnabled()){
				logger.debug("Inside createAnswer(long)");
			}
			
			MessageACA answer=null;

			answer = stackObj.createAnswer(l);
			//Add Accounting-Application-ID
			answer.addAccountingApplicationId(stackObj.getAccountingApplicationId());

			//Adding request type and number, so client will know on what we answered.
			answer.addAccountingRecordNumber(stackObj.getAccountingRecordNumber());
			answer.addAccountingRecordType(stackObj.getEnumAccountingRecordType());

			//Adding the requested User-Name AVP.
			answer.addUserName(stackObj.getUserName());

			RfAccountingResponseImpl response = new RfAccountingResponseImpl(answer);
			response.setRequest(this);
			return response;

		} catch (ValidationException e) {
			throw new RfResourceException("Exception in createAnswer(long) ",e);
		}
	}

	@Override
	public RfAccountingResponse createAnswer(long l, long l1)
	throws RfResourceException {
		try {
			
			if(logger.isDebugEnabled()){
				logger.debug("Inside createAnswer(long,long)");
			}
			
			MessageACA answer=null;

			answer = stackObj.createAnswer(l,l);
			//Add Accounting-Application-ID
			answer.addAccountingApplicationId(stackObj.getAccountingApplicationId());

			//Adding request type and number, so client will know on what we answered.
			answer.addAccountingRecordNumber(stackObj.getAccountingRecordNumber());
			answer.addAccountingRecordType(stackObj.getEnumAccountingRecordType());

			//Adding the requested User-Name AVP.
			answer.addUserName(stackObj.getUserName());

			RfAccountingResponseImpl response = new RfAccountingResponseImpl(answer);
			response.setRequest(this);
			return response;

		} catch (ValidationException e) {
			throw new RfResourceException("Exception in createAnswer(long) ",e);
		}
	}

	@Override
	public Response createResponse(int arg0) throws ResourceException {
		// TODO Auto-generated method stub
		return null;
	}
}
