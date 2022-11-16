package com.baypackets.ase.ra.diameter.gy.stackif;

import java.util.Date;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.gy.CreditControlAnswer;
import com.baypackets.ase.ra.diameter.gy.CreditControlRequest;
import com.baypackets.ase.ra.diameter.gy.GyResourceException;
import com.baypackets.ase.ra.diameter.gy.avp.AcctMultiSessionIdAvp;
import com.baypackets.ase.ra.diameter.gy.avp.AuthApplicationIdAvp;
import com.baypackets.ase.ra.diameter.gy.avp.CCRequestNumberAvp;
import com.baypackets.ase.ra.diameter.gy.avp.CCRequestTypeAvp;
import com.baypackets.ase.ra.diameter.gy.avp.CCSessionFailoverAvp;
import com.baypackets.ase.ra.diameter.gy.avp.CCSubSessionIdAvp;
import com.baypackets.ase.ra.diameter.gy.avp.CheckBalanceResultAvp;
import com.baypackets.ase.ra.diameter.gy.avp.CostInformationAvp;
import com.baypackets.ase.ra.diameter.gy.avp.CreditControlFailureHandlingAvp;
import com.baypackets.ase.ra.diameter.gy.avp.DirectDebitingFailureHandlingAvp;
import com.baypackets.ase.ra.diameter.gy.avp.EventTimestampAvp;
import com.baypackets.ase.ra.diameter.gy.avp.FailedAVPAvp;
import com.baypackets.ase.ra.diameter.gy.avp.FinalUnitIndicationAvp;
import com.baypackets.ase.ra.diameter.gy.avp.GrantedServiceUnitAvp;
import com.baypackets.ase.ra.diameter.gy.avp.LowBalanceIndicationAvp;
import com.baypackets.ase.ra.diameter.gy.avp.MultipleServicesCreditControlAvp;
import com.baypackets.ase.ra.diameter.gy.avp.OriginStateIdAvp;
import com.baypackets.ase.ra.diameter.gy.avp.ProxyInfoAvp;
import com.baypackets.ase.ra.diameter.gy.avp.RedirectHostUsageAvp;
import com.baypackets.ase.ra.diameter.gy.avp.RedirectMaxCacheTimeAvp;
import com.baypackets.ase.ra.diameter.gy.avp.RemainingBalanceAvp;
import com.baypackets.ase.ra.diameter.gy.avp.RouteRecordAvp;
import com.baypackets.ase.ra.diameter.gy.avp.ServiceInformationAvp;
import com.baypackets.ase.ra.diameter.gy.avp.UserNameAvp;
import com.baypackets.ase.ra.diameter.gy.avp.ValidityTimeAvp;
import com.baypackets.ase.ra.diameter.gy.enums.CCRequestTypeEnum;
import com.baypackets.ase.ra.diameter.gy.enums.CCSessionFailoverEnum;
import com.baypackets.ase.ra.diameter.gy.enums.CreditControlFailureHandlingEnum;
import com.baypackets.ase.ra.diameter.gy.enums.DirectDebitingFailureHandlingEnum;
import com.baypackets.ase.ra.diameter.gy.enums.LowBalanceIndicationEnum;
import com.baypackets.ase.ra.diameter.gy.enums.RedirectHostUsageEnum;
import com.baypackets.ase.ra.diameter.gy.impl.GySession;
import com.baypackets.ase.resource.Request;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.gy.generated.avp.AvpFailedAVP;
import com.traffix.openblox.diameter.gy.generated.avp.AvpMultipleServicesCreditControl;
import com.traffix.openblox.diameter.gy.generated.avp.AvpProxyInfo;
import com.traffix.openblox.diameter.gy.generated.enums.EnumCheckBalanceResult;
import com.traffix.openblox.diameter.gy.generated.event.MessageCCA;

public class CreditControlAnswerImpl extends GyAbstractResponse implements CreditControlAnswer {

	private static Logger logger = Logger.getLogger(CreditControlAnswerImpl.class);

	/**
	 *	This attribute contains the stack object which is to be used 
	 *	for all the set/get method
	 *
	 */
	private MessageCCA stackObj;
	private GySession m_GySession;
	private int retryCounter=0;
    private CreditControlRequest gyRequest;
	public CreditControlAnswerImpl(int type){
		super(type);
		System.out.println("Inside CreditControlAnswerImpl constructor ");
	}

	public CreditControlAnswerImpl(GySession shSession){
		super(shSession);
		//stackObj = new MessageProfileUpdateAnswer();
		this.m_GySession=shSession;
	}

	public CreditControlAnswerImpl(MessageCCA stkObj){
		super(stkObj);
		this.stackObj=stkObj;
	}


	public void incrementRetryCounter() {
		this.retryCounter++;
	}

	public int getRetryCounter() {
		return retryCounter;
	}

	public void setStackObj(MessageCCA stkObj){
		super.setStackObject(stkObj);
		this.stackObj=stkObj;
	}

	public MessageCCA getStackObj(){
		return stackObj;
	}

	////////////////////////////////////////////////////////////////
	//// RO CCA method implementation starts ///////////////////////////
	////////////////////////////////////////////////////////////////

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
	 *  Adding CCSessionFailover AVP of type Enumerated to the message.
	 */
	public CCSessionFailoverAvp addCCSessionFailover(CCSessionFailoverEnum value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addCCSessionFailover()");
			}
			return new CCSessionFailoverAvp(stackObj.addCCSessionFailover(CCSessionFailoverEnum.getStackObj(value)));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addCCSessionFailover",e);
		}
	}

	/**
	 *  Adding CreditControlFailureHandling AVP of type Enumerated to the message.
	 */
	public CreditControlFailureHandlingAvp addCreditControlFailureHandling(CreditControlFailureHandlingEnum value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addCreditControlFailureHandling()");
			}
			return new CreditControlFailureHandlingAvp(stackObj.addCreditControlFailureHandling(CreditControlFailureHandlingEnum.getStackObj(value)));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addCreditControlFailureHandling",e);
		}
	}

	/**
	 *  Adding DirectDebitingFailureHandling AVP of type Enumerated to the message.
	 */
	public DirectDebitingFailureHandlingAvp addDirectDebitingFailureHandling(DirectDebitingFailureHandlingEnum value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addDirectDebitingFailureHandling()");
			}
			return new DirectDebitingFailureHandlingAvp(stackObj.addDirectDebitingFailureHandling(DirectDebitingFailureHandlingEnum.getStackObj(value)));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addDirectDebitingFailureHandling",e);
		}
	}

	/**
	 *  Adding CostInformation AVP of type Grouped to the message.
	 */
	public CostInformationAvp addGroupedCostInformation() throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedCostInformation()");
			}
			return new CostInformationAvp(stackObj.addGroupedCostInformation());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGroupedCostInformation",e);
		}
	}

	/**
	 *  Adding FailedAVP AVP of type Grouped to the message.
	 */
	public FailedAVPAvp addGroupedFailedAVP() throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedFailedAVP()");
			}
			return new FailedAVPAvp(stackObj.addGroupedFailedAVP());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGroupedFailedAVP",e);
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
	 *  Adding RemainingBalance AVP of type Grouped to the message.
	 */
	public RemainingBalanceAvp addGroupedRemainingBalance() throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedRemainingBalance()");
			}
			return new RemainingBalanceAvp(stackObj.addGroupedRemainingBalance());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGroupedRemainingBalance",e);
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
	 *  Adding LowBalanceIndication AVP of type Enumerated to the message.
	 */
	public LowBalanceIndicationAvp addLowBalanceIndication(LowBalanceIndicationEnum value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addLowBalanceIndication()");
			}
			return new LowBalanceIndicationAvp(stackObj.addLowBalanceIndication(LowBalanceIndicationEnum.getStackObj(value)));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addLowBalanceIndication",e);
		}
	}

	//TODO
	//	/**
	//	 *  Adding RedirectHost AVP of type DiameterURI to the message.
	//	 */
	//	public RedirectHostAvp addRedirectHost(URI value) throws GyResourceException {
	//		try {
	//			if(logger.isDebugEnabled()){
	//				logger.debug("Inside addRedirectHost()");
	//			}
	//			return new RedirectHostAvp(stackObj.addRedirectHost(value));
	//		} catch (ValidationException e) {
	//			throw new GyResourceException("Exception in addRedirectHost",e);
	//		}
	//	}

	/**
	 *  Adding RedirectHostUsage AVP of type Enumerated to the message.
	 */
	public RedirectHostUsageAvp addRedirectHostUsage(RedirectHostUsageEnum value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addRedirectHostUsage()");
			}
			return new RedirectHostUsageAvp(stackObj.addRedirectHostUsage(RedirectHostUsageEnum.getStackObj(value)));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addRedirectHostUsage",e);
		}
	}

	/**
	 *  Adding RedirectMaxCacheTime AVP of type Unsigned32 to the message.
	 */
	public RedirectMaxCacheTimeAvp addRedirectMaxCacheTime(long value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addRedirectMaxCacheTime()");
			}
			return new RedirectMaxCacheTimeAvp(stackObj.addRedirectMaxCacheTime(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addRedirectMaxCacheTime",e);
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

//	/**
//	 *  This method returns the application id associated with this message.
//	 */
//	public long getApplicationId() {
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

	/**
	 *  Retrieving a single Enumerated value from CCSessionFailover AVPs.
	 */
	public int getCCSessionFailover() throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getCCSessionFailover()");
			}
			return stackObj.getCCSessionFailover();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getCCSessionFailover",e);
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

	/**
	 *  Retrieving a single Enumerated value from CreditControlFailureHandling AVPs.
	 */
	public int getCreditControlFailureHandling() throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getCreditControlFailureHandling()");
			}
			return stackObj.getCreditControlFailureHandling();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getCreditControlFailureHandling",e);
		}
	}

	/**
	 *  Retrieving a single Enumerated value from DirectDebitingFailureHandling AVPs.
	 */
	public int getDirectDebitingFailureHandling() throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getDirectDebitingFailureHandling()");
			}
			return stackObj.getDirectDebitingFailureHandling();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getDirectDebitingFailureHandling",e);
		}
	}

	/**
	 *  This method the returns the enum value corroesponding to CCRequestTypeAvp.
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
	 *  This method the returns the enum value corroesponding to CCSessionFailoverAvp.
	 */
	public CCSessionFailoverEnum getEnumCCSessionFailover() throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getEnumCCSessionFailover()");
			}
			return CCSessionFailoverEnum.getContainerObj(stackObj.getEnumCCSessionFailover());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getEnumCCSessionFailover",e);
		}
	}


	/**
	 *  This method the returns the enum value corroesponding to CreditControlFailureHandlingAvp.
	 */
	public CreditControlFailureHandlingEnum getEnumCreditControlFailureHandling() throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getEnumCreditControlFailureHandling()");
			}
			return CreditControlFailureHandlingEnum.getContainerObj(stackObj.getEnumCreditControlFailureHandling());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getEnumCreditControlFailureHandling",e);
		}
	}


	/**
	 *  This method the returns the enum value corroesponding to DirectDebitingFailureHandlingAvp.
	 */
	public DirectDebitingFailureHandlingEnum getEnumDirectDebitingFailureHandling() throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getEnumDirectDebitingFailureHandling()");
			}
			return DirectDebitingFailureHandlingEnum.getContainerObj(stackObj.getEnumDirectDebitingFailureHandling());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getEnumDirectDebitingFailureHandling",e);
		}
	}


	/**
	 *  This method the returns the enum value corroesponding to LowBalanceIndicationAvp.
	 */
	public LowBalanceIndicationEnum getEnumLowBalanceIndication() throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getEnumLowBalanceIndication()");
			}
			return LowBalanceIndicationEnum.getContainerObj(stackObj.getEnumLowBalanceIndication());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getEnumLowBalanceIndication",e);
		}
	}


	/**
	 *  This method the returns the enum value corroesponding to RedirectHostUsageAvp.
	 */
	public RedirectHostUsageEnum getEnumRedirectHostUsage() throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getEnumRedirectHostUsage()");
			}
			return RedirectHostUsageEnum.getContainerObj(stackObj.getEnumRedirectHostUsage());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getEnumRedirectHostUsage",e);
		}
	}


	/**
	 *  Retrieving a single Grouped value from CostInformation AVPs.
	 */
	public CostInformationAvp getGroupedCostInformation() throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedCostInformation()");
			}
			return new CostInformationAvp(stackObj.getGroupedCostInformation());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGroupedCostInformation",e);
		}
	}

	/**
	 *  Retrieving multiple Grouped values from FailedAVP AVPs.
	 */
	public FailedAVPAvp[] getGroupedFailedAVPs() throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedFailedAVPs()");
			}
			AvpFailedAVP[] stackAv= stackObj.getGroupedFailedAVPs();
			FailedAVPAvp[] contAvp= new FailedAVPAvp[stackAv.length];
			for(int i=0;i<stackAv.length;i++){
				contAvp[i]=new FailedAVPAvp(stackAv[i]);
			}
			return contAvp;
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGroupedFailedAVPs",e);
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
	 *  Retrieving a single Grouped value from RemainingBalance AVPs.
	 */
	public RemainingBalanceAvp getGroupedRemainingBalance() throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedRemainingBalance()");
			}
			return new RemainingBalanceAvp(stackObj.getGroupedRemainingBalance());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGroupedRemainingBalance",e);
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
	 *  Retrieving a single Enumerated value from LowBalanceIndication AVPs.
	 */
	public int getLowBalanceIndication() throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getLowBalanceIndication()");
			}
			return stackObj.getLowBalanceIndication();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getLowBalanceIndication",e);
		}
	}

//	/**
//	 *  Retrieving a single DiameterIdentity value from OriginHost AVPs.
//	 */
//	public java.lang.String getOriginHost() {
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


	//TODO
	//	/**
	//	 *  Retrieving multiple DiameterURI values from RedirectHost AVPs.
	//	 */
	//	public URI[] getRedirectHosts() throws GyResourceException {
	//		try {
	//			if(logger.isDebugEnabled()){
	//				logger.debug("Inside getRedirectHosts()");
	//			}
	//			return new URI[](stackObj.getRedirectHosts());
	//		} catch (ValidationException e) {
	//			throw new GyResourceException("Exception in getRedirectHosts",e);
	//		}
	//	}

	/**
	 *  Retrieving a single Enumerated value from RedirectHostUsage AVPs.
	 */
	public int getRedirectHostUsage() throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getRedirectHostUsage()");
			}
			return stackObj.getRedirectHostUsage();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getRedirectHostUsage",e);
		}
	}

	/**
	 *  Retrieving a single Unsigned32 value from RedirectMaxCacheTime AVPs.
	 */
	public long getRedirectMaxCacheTime() throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getRedirectMaxCacheTime()");
			}
			return stackObj.getRedirectMaxCacheTime();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getRedirectMaxCacheTime",e);
		}
	}

	/**
	 *  Retrieving a single Unsigned32 value from ResultCode AVPs.
	 */
	public long getResultCode() throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getResultCode()");
			}
			return stackObj.getResultCode();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getResultCode",e);
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
	
	@Override
	public AcctMultiSessionIdAvp addAcctMultiSessionId(long value, boolean mFlag)
			throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addAcctMultiSessionId()");
			}
			return new AcctMultiSessionIdAvp(stackObj.addAcctMultiSessionId(value, mFlag));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addAcctMultiSessionId",e);
		}
	}

	@Override
	public CCSubSessionIdAvp addCCSubSessionId(long value, boolean mFlag)
			throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addCCSubSessionId()");
			}
			return new CCSubSessionIdAvp(stackObj.addCCSubSessionId( value,mFlag));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addCCSubSessionId",e);
		}
	}

	@Override
	public CheckBalanceResultAvp addCheckBalanceResult(
			EnumCheckBalanceResult value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addCheckBalanceResult()");
			}
			return new CheckBalanceResultAvp(stackObj.addCheckBalanceResult(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addCheckBalanceResult",e);
		}
	}

	@Override
	public EventTimestampAvp addEventTimestamp(Date value)
			throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addEventTimestamp()");
			}
			return new EventTimestampAvp(stackObj.addEventTimestamp(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addEventTimestamp",e);
		}
	}

	@Override
	public FinalUnitIndicationAvp addGroupedFinalUnitIndication()
			throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedFinalUnitIndication()");
			}
			return new FinalUnitIndicationAvp(stackObj.addGroupedFinalUnitIndication());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGroupedFinalUnitIndication",e);
		}
	}

	@Override
	public GrantedServiceUnitAvp addGroupedGrantedServiceUnit()
			throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedServiceInformation()");
			}
			return new GrantedServiceUnitAvp(stackObj.addGroupedGrantedServiceUnit());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGroupedServiceInformation",e);
		}
	}

	@Override
	public OriginStateIdAvp addOriginStateId(long value)
			throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addOriginStateId()");
			}
			return new OriginStateIdAvp(stackObj.addOriginStateId(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addOriginStateId",e);
		}
	}

	@Override
	public UserNameAvp addUserName(String value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addUserName()");
			}
			return new UserNameAvp(stackObj.addUserName(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addUserName",e);
		}
	}

	@Override
	public ValidityTimeAvp addValidityTime(long value)
			throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addValidityTime()");
			}
			return new ValidityTimeAvp(stackObj.addValidityTime(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addValidityTime",e);
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
	public long getCheckBalanceResult() throws GyResourceException {
		// TODO Auto-generated method stub
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getCheckBalanceResult()");
			}
			return stackObj.getCheckBalanceResult();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getCheckBalanceResult",e);
		}
	}

	@Override
	public Date getEventTimestamp() throws GyResourceException {
		// TODO Auto-generated method stub
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getEventTimestamp()");
			}
			return stackObj.getEventTimestamp();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getEventTimestamp",e);
		}
	}

	@Override
	public FinalUnitIndicationAvp getGroupedFinalUnitIndication()
			throws GyResourceException {
		// TODO Auto-generated method stub
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedServiceInformation()");
			}
			return new FinalUnitIndicationAvp(stackObj.getGroupedFinalUnitIndication());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGroupedServiceInformation",e);
		}
	}

	@Override
	public GrantedServiceUnitAvp getGroupedGrantedServiceUnit()
			throws GyResourceException {
		// TODO Auto-generated method stub
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedServiceInformation()");
			}
			return new GrantedServiceUnitAvp(stackObj.getGroupedGrantedServiceUnit());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGroupedServiceInformation",e);
		}
	}

	@Override
	public long getOriginStateId() throws GyResourceException {
		// TODO Auto-generated method stub
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getOriginStateId()");
			}
			return stackObj.getOriginStateId();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getOriginStateId",e);
		}
	}

	@Override
	public String getUserName() throws GyResourceException {
		// TODO Auto-generated method stub
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
	public long getValidityTime() throws GyResourceException {
		// TODO Auto-generated method stub
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getValidityTime()");
			}
			return stackObj.getValidityTime();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getValidityTime",e);
		}
	}

	public void setRequest(CreditControlRequest request)
    {
    	if(logger.isDebugEnabled()){
			logger.debug("Inside setRequest()");
		}
    	this.gyRequest=request;
    }
	@Override
	public Request getRequest() {
		if(logger.isDebugEnabled()){
			logger.debug("Inside getRequest()");
		}
		return this.gyRequest;
	}

//	/**
//	 *  Retrieving a single UTF8String value from SessionId AVPs.
//	 * @throws GyResourceException 
//	 */
//	public java.lang.String getSessionId() throws GyResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getSessionId()");
//			}
//			return stackObj.getSessionId();
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in getSessionId",e);
//		}
//	}
}
