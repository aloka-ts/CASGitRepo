package com.baypackets.ase.ra.diameter.gy;

import com.baypackets.ase.ra.diameter.common.exception.ValidationRecord;
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
import com.baypackets.ase.resource.ResourceException;
import com.traffix.openblox.diameter.gy.generated.avp.AvpAcctMultiSessionId;
import com.traffix.openblox.diameter.gy.generated.enums.EnumCheckBalanceResult;

public interface CreditControlAnswer extends GyResponse {

	public int getCommandCode();

	public long getApplicationId();

	public String getName();

	//public Standard getStandard();

	public ValidationRecord validate();

	public String getSessionId();

	public long getResultCode() throws GyResourceException;

	public String getOriginHost() throws ResourceException;

	public String getOriginRealm() throws ResourceException;


	
	////////////////////////////////////////////////////////////////
	//// RO Method implementation starts ///////////////////////////
	////////////////////////////////////////////////////////////////

	/**
	 *  Adding AuthApplicationId AVP of type Unsigned32 to the message.
	 */
	public AuthApplicationIdAvp addAuthApplicationId(long value) throws GyResourceException ;

	/**
	 *  Adding CCRequestNumber AVP of type Unsigned32 to the message.
	 */
	public CCRequestNumberAvp addCCRequestNumber(long value) throws GyResourceException ;

	/**
	 *  Adding CCRequestType AVP of type Enumerated to the message.
	 */
	public CCRequestTypeAvp addCCRequestType(CCRequestTypeEnum value) throws GyResourceException ;

	/**
	 *  Adding CCSessionFailover AVP of type Enumerated to the message.
	 */
	public CCSessionFailoverAvp addCCSessionFailover(CCSessionFailoverEnum value) throws GyResourceException ;

	/**
	 *  Adding CreditControlFailureHandling AVP of type Enumerated to the message.
	 */
	public CreditControlFailureHandlingAvp addCreditControlFailureHandling(CreditControlFailureHandlingEnum value) 
	throws GyResourceException ;
	/**
	 *  Adding DirectDebitingFailureHandling AVP of type Enumerated to the message.
	 */
	public DirectDebitingFailureHandlingAvp addDirectDebitingFailureHandling(DirectDebitingFailureHandlingEnum value) 
	throws GyResourceException ;

	/**
	 *  Adding CostInformation AVP of type Grouped to the message.
	 */
	public CostInformationAvp addGroupedCostInformation() throws GyResourceException ;

	/**
	 *  Adding FailedAVP AVP of type Grouped to the message.
	 */
	public FailedAVPAvp addGroupedFailedAVP() throws GyResourceException ;

	/**
	 *  Adding MultipleServicesCreditControl AVP of type Grouped to the message.
	 */
	public MultipleServicesCreditControlAvp addGroupedMultipleServicesCreditControl() throws GyResourceException ;

	/**
	 *  Adding ProxyInfo AVP of type Grouped to the message.
	 */
	public ProxyInfoAvp addGroupedProxyInfo() throws GyResourceException ;

	/**
	 *  Adding RemainingBalance AVP of type Grouped to the message.
	 */
	public RemainingBalanceAvp addGroupedRemainingBalance() throws GyResourceException ;
	
	/**
	 *  Adding ServiceInformation AVP of type Grouped to the message.
	 */
	public ServiceInformationAvp addGroupedServiceInformation() throws GyResourceException ;

	/**
	 *  Adding LowBalanceIndication AVP of type Enumerated to the message.
	 */
	public LowBalanceIndicationAvp addLowBalanceIndication(LowBalanceIndicationEnum value) 
	throws GyResourceException ;

	//TODO
	//	/**
	//	 *  Adding RedirectHost AVP of type DiameterURI to the message.
	//	 */
	//	public RedirectHostAvp addRedirectHost(URI value) throws GyResourceException ;
	
	/**
	 *  Adding RedirectHostUsage AVP of type Enumerated to the message.
	 */
	public RedirectHostUsageAvp addRedirectHostUsage(RedirectHostUsageEnum value) throws GyResourceException ;
	
	/**
	 *  Adding RedirectMaxCacheTime AVP of type Unsigned32 to the message.
	 */
	public RedirectMaxCacheTimeAvp addRedirectMaxCacheTime(long value) throws GyResourceException ;

	/**
	 *  Adding RouteRecord AVP of type DiameterIdentity to the message.
	 */
	public RouteRecordAvp addRouteRecord(java.lang.String value) throws GyResourceException ;

//	/**
//	 *  This method returns the application id associated with this message.
//	 */
//	public long getApplicationId() ;

	/**
	 *  Retrieving a single Unsigned32 value from AuthApplicationId AVPs.
	 */
	public long getAuthApplicationId() throws GyResourceException ;

	/**
	 *  Retrieving a single Unsigned32 value from CCRequestNumber AVPs.
	 */
	public long getCCRequestNumber() throws GyResourceException ;

	/**
	 *  Retrieving a single Enumerated value from CCRequestType AVPs.
	 */
	public int getCCRequestType() throws GyResourceException ;
	
	/**
	 *  Retrieving a single Enumerated value from CCSessionFailover AVPs.
	 */
	public int getCCSessionFailover() throws GyResourceException ;

//	/**
//	 *  This method returns the command code associated with this message.
//	 */
//	public int getCommandCode() ;

	/**
	 *  Retrieving a single Enumerated value from CreditControlFailureHandling AVPs.
	 */
	public int getCreditControlFailureHandling() throws GyResourceException ;
	
	/**
	 *  Retrieving a single Enumerated value from DirectDebitingFailureHandling AVPs.
	 */
	public int getDirectDebitingFailureHandling() throws GyResourceException ;

	/**
	 *  This method the returns the enum value corroesponding to CCRequestTypeAvp.
	 */
	public CCRequestTypeEnum getEnumCCRequestType() throws GyResourceException ;

	/**
	 *  This method the returns the enum value corroesponding to CCSessionFailoverAvp.
	 */
	public CCSessionFailoverEnum getEnumCCSessionFailover() throws GyResourceException ;

	/**
	 *  This method the returns the enum value corroesponding to CreditControlFailureHandlingAvp.
	 */
	public CreditControlFailureHandlingEnum getEnumCreditControlFailureHandling() 
	throws GyResourceException ;


	/**
	 *  This method the returns the enum value corroesponding to DirectDebitingFailureHandlingAvp.
	 */
	public DirectDebitingFailureHandlingEnum getEnumDirectDebitingFailureHandling() 
	throws GyResourceException ;


	/**
	 *  This method the returns the enum value corroesponding to LowBalanceIndicationAvp.
	 */
	public LowBalanceIndicationEnum getEnumLowBalanceIndication() throws GyResourceException ;

	/**
	 *  This method the returns the enum value corroesponding to RedirectHostUsageAvp.
	 */
	public RedirectHostUsageEnum getEnumRedirectHostUsage() throws GyResourceException ;

	/**
	 *  Retrieving a single Grouped value from CostInformation AVPs.
	 */
	public CostInformationAvp getGroupedCostInformation() throws GyResourceException ;

	/**
	 *  Retrieving multiple Grouped values from FailedAVP AVPs.
	 */
	public FailedAVPAvp[] getGroupedFailedAVPs() throws GyResourceException ;

	/**
	 *  Retrieving multiple Grouped values from MultipleServicesCreditControl AVPs.
	 */
	public MultipleServicesCreditControlAvp[] getGroupedMultipleServicesCreditControls() 
	throws GyResourceException ;

	/**
	 *  Retrieving multiple Grouped values from ProxyInfo AVPs.
	 */
	public ProxyInfoAvp[] getGroupedProxyInfos() throws GyResourceException ;

	/**
	 *  Retrieving a single Grouped value from RemainingBalance AVPs.
	 */
	public RemainingBalanceAvp getGroupedRemainingBalance() throws GyResourceException ;

	/**
	 *  Retrieving a single Grouped value from ServiceInformation AVPs.
	 */
	public ServiceInformationAvp getGroupedServiceInformation() throws GyResourceException ;

	/**
	 *  Retrieving a single Enumerated value from LowBalanceIndication AVPs.
	 */
	public int getLowBalanceIndication() throws GyResourceException ;

	//	/**
	//	 *  Retrieving multiple DiameterURI values from RedirectHost AVPs.
	//	 */
	//	public URI[] getRedirectHosts() throws GyResourceException ;

	/**
	 *  Retrieving a single Enumerated value from RedirectHostUsage AVPs.
	 */
	public int getRedirectHostUsage() throws GyResourceException ;

	/**
	 *  Retrieving a single Unsigned32 value from RedirectMaxCacheTime AVPs.
	 */
	public long getRedirectMaxCacheTime() throws GyResourceException ;

	/**
	 *  Retrieving multiple DiameterIdentity values from RouteRecord AVPs.
	 */
	public java.lang.String[] getRouteRecords() throws GyResourceException ;
	
	
	public  AcctMultiSessionIdAvp addAcctMultiSessionId(long value, boolean mFlag)  throws GyResourceException ;
	
	public  CCSubSessionIdAvp addCCSubSessionId(long value, boolean mFlag) throws GyResourceException ;
	
	public  CheckBalanceResultAvp addCheckBalanceResult(EnumCheckBalanceResult value) throws GyResourceException ;
	
	public  EventTimestampAvp addEventTimestamp(java.util.Date value)  throws GyResourceException ;
	
	public  FinalUnitIndicationAvp addGroupedFinalUnitIndication() throws GyResourceException ;
	
	public  GrantedServiceUnitAvp addGroupedGrantedServiceUnit() throws GyResourceException ;
	
	public  OriginStateIdAvp addOriginStateId(long value)  throws GyResourceException ;
	
	public  UserNameAvp	addUserName(java.lang.String value)  throws GyResourceException ;
	
	public  ValidityTimeAvp addValidityTime(long value) throws GyResourceException ;
	
	public long getAcctMultiSessionId() throws GyResourceException ;
	
	public long getCCSubSessionId() throws GyResourceException ;
	
	public long getCheckBalanceResult() throws GyResourceException ;
	
	public java.util.Date getEventTimestamp() throws GyResourceException ;
	
	public  FinalUnitIndicationAvp getGroupedFinalUnitIndication() throws GyResourceException ;
	
	public  GrantedServiceUnitAvp getGroupedGrantedServiceUnit() throws GyResourceException ;
	
	public long getOriginStateId() throws GyResourceException ;
	
	public String getUserName() throws GyResourceException ;
	
	public long getValidityTime() throws GyResourceException ;
	
	
	public static final int code = 271;
	public static final String name = "ACA";
	//public static final Standard standard;
	public static final long applicationId = 3L;

}