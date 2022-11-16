package com.baypackets.ase.ra.diameter.rf.stackif;


import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.rf.RfAccountingRequest;
import com.baypackets.ase.ra.diameter.rf.RfAccountingResponse;
import com.baypackets.ase.ra.diameter.rf.RfResourceException;
import com.baypackets.ase.ra.diameter.rf.avp.AccountingApplicationIdAvp;
import com.baypackets.ase.ra.diameter.rf.avp.AccountingInterimIntervalAvp;
import com.baypackets.ase.ra.diameter.rf.avp.AccountingRecordNumberAvp;
import com.baypackets.ase.ra.diameter.rf.avp.AccountingRecordTypeAvp;
import com.baypackets.ase.ra.diameter.rf.avp.ErrorReportingHostAvp;
import com.baypackets.ase.ra.diameter.rf.avp.EventTimestampAvp;
import com.baypackets.ase.ra.diameter.rf.avp.OriginStateIdAvp;
import com.baypackets.ase.ra.diameter.rf.avp.ProxyInfoAvp;
import com.baypackets.ase.ra.diameter.rf.avp.UserNameAvp;
import com.baypackets.ase.ra.diameter.rf.enums.AccountingRecordTypeEnum;
import com.baypackets.ase.ra.diameter.rf.impl.RfSession;
import com.baypackets.ase.resource.Request;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.rf.generated.avp.AvpErrorReportingHost;
import com.traffix.openblox.diameter.rf.generated.avp.AvpProxyInfo;
import com.traffix.openblox.diameter.rf.generated.enums.EnumAccountingRecordType;
import com.traffix.openblox.diameter.rf.generated.event.MessageACA;

public class RfAccountingResponseImpl extends RfAbstractResponse implements RfAccountingResponse {

	private static Logger logger = Logger.getLogger(RfAccountingResponseImpl.class);

	/**
	 *	This attribute contains the stack object which is to be used 
	 *	for all the set/get method
	 *
	 */
	private MessageACA stackObj;
	private RfSession m_shSession;
	private int retryCounter=0;

	private RfAccountingRequest rfRequest;

	public RfAccountingResponseImpl(int type){
		super(type);
		System.out.println("Inside RfAccountingResponseImpl constructor ");
	}

	public RfAccountingResponseImpl(RfSession shSession){
		super(shSession);
		//stackObj = new MessageProfileUpdateAnswer();
		this.m_shSession=shSession;
	}

	public RfAccountingResponseImpl(MessageACA stkObj){
		super(stkObj);
		this.stackObj=stkObj;
	}


	public void incrementRetryCounter() {
		this.retryCounter++;
	}

	public int getRetryCounter() {
		return retryCounter;
	}

	public void setStackObj(MessageACA stkObj){
		super.setStackObject(stkObj);
		this.stackObj=stkObj;
	}

	public MessageACA getStackObj(){
		return stackObj;
	}

	////////////////////////////////////////////////////////////////
	//// RF Method implementation starts ///////////////////////////
	////////////////////////////////////////////////////////////////

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


	@Override
	public ErrorReportingHostAvp addErrorReportingHost(String s)throws RfResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addErrorReportingHost()");
			} 
			return new ErrorReportingHostAvp(stackObj.addErrorReportingHost(s));
		}
		catch (ValidationException e) {
			throw new RfResourceException("Exception in addAccountingRecordNumber",e);
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

	@Override
	public String getErrorReportingHost() throws RfResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getErrorReportingHos()");
			}
			return this.stackObj.getErrorReportingHost();
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

	@Override
	public long getResultCode() throws RfResourceException {
		try {
			return this.getStackObj().getResultCode();
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in getResultCode",e);
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
	
	public void setRequest(RfAccountingRequest request) {

		if (logger.isDebugEnabled()) {
			logger.debug("Inside setRequest()");

		}
		this.rfRequest = request;
	}

	@Override
	public Request getRequest() {

		if (logger.isDebugEnabled()) {
			logger.debug("Inside getRequest()");

		}
		return this.rfRequest;
	}

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

}
