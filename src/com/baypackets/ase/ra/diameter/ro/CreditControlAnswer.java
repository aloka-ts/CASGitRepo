package com.baypackets.ase.ra.diameter.ro;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterFloat32;
import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterFloat64;
import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGeneric;
import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterInteger32;
import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterInteger64;
import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterOctetString;
import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterUnsigned32;
import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterUnsigned64;
import com.baypackets.ase.ra.diameter.common.exception.ValidationRecord;
import com.baypackets.ase.ra.diameter.ro.enums.CCRequestTypeEnum;
import com.baypackets.ase.resource.ResourceException;

import fr.marben.diameter.DiameterAVP;
import fr.marben.diameter.DiameterGroupedAVP;
import fr.marben.diameter._3gpp.ro.DiameterRoMessageFactory;

public interface CreditControlAnswer extends RoResponse {

	public int getCommandCode();

	public long getApplicationId();

	public String getName();

	//public Standard getStandard();

	public ValidationRecord validate();

	public String getSessionId();

	public String getResultCode() throws RoResourceException;

	public String getOriginHost() throws ResourceException;

	public String getOriginRealm() throws ResourceException;


	
	////////////////////////////////////////////////////////////////
	//// RO Method implementation starts ///////////////////////////
	////////////////////////////////////////////////////////////////

	/**
	 *  Adding AuthApplicationId AVP of type Unsigned32 to the message.
	 */
	public void addAuthApplicationId(long value) throws RoResourceException ;

	/**
	 *  Adding CCRequestNumber AVP of type Unsigned32 to the message.
	 */
	public void addCCRequestNumber(long value) throws RoResourceException ;

//	/**
//	 *  Adding CCRequestType AVP of type Enumerated to the message.
//	 */
//	public CCRequestTypeAvp addCCRequestType(CCRequestTypeEnum value) throws RoResourceException ;

//	/**
//	 *  Adding CCSessionFailover AVP of type Enumerated to the message.
//	 */
//	public CCSessionFailoverAvp addCCSessionFailover(CCSessionFailoverEnum value) throws RoResourceException ;

//	/**
//	 *  Adding CreditControlFailureHandling AVP of type Enumerated to the message.
//	 */
//	public CreditControlFailureHandlingAvp addCreditControlFailureHandling(CreditControlFailureHandlingEnum value) 
//	throws RoResourceException ;
//	/**
//	 *  Adding DirectDebitingFailureHandling AVP of type Enumerated to the message.
//	 */
//	public DiameterAVP addDirectDebitingFailureHandling(DirectDebitingFailureHandlingEnum value) 
//	throws RoResourceException ;

//	/**
//	 *  Adding CostInformation AVP of type Grouped to the message.
//	 */
//	public CostInformationAvp addGroupedCostInformation() throws RoResourceException ;
//
//	/**
//	 *  Adding FailedAVP AVP of type Grouped to the message.
//	 */
//	public FailedAVPAvp addGroupedFailedAVP() throws RoResourceException ;
//
//	/**
//	 *  Adding MultipleServicesCreditControl AVP of type Grouped to the message.
//	 */
//	public MultipleServicesCreditControlAvp addGroupedMultipleServicesCreditControl() throws RoResourceException ;
//
//	/**
//	 *  Adding ProxyInfo AVP of type Grouped to the message.
//	 */
//	public ProxyInfoAvp addGroupedProxyInfo() throws RoResourceException ;

//	/**
//	 *  Adding RemainingBalance AVP of type Grouped to the message.
//	 */
//	public RemainingBalanceAvp addGroupedRemainingBalance() throws RoResourceException ;
//	
//	/**
//	 *  Adding ServiceInformation AVP of type Grouped to the message.
//	 */
//	public ServiceInformationAvp addGroupedServiceInformation() throws RoResourceException ;
//
//	/**
//	 *  Adding LowBalanceIndication AVP of type Enumerated to the message.
//	 */
//	public LowBalanceIndicationAvp addLowBalanceIndication(LowBalanceIndicationEnum value) 
//	throws RoResourceException ;
//
//	//TODO
//	//	/**
//	//	 *  Adding RedirectHost AVP of type DiameterURI to the message.
//	//	 */
//	//	public RedirectHostAvp addRedirectHost(URI value) throws RoResourceException ;
//	
//	/**
//	 *  Adding RedirectHostUsage AVP of type Enumerated to the message.
//	 */
//	public RedirectHostUsageAvp addRedirectHostUsage(RedirectHostUsageEnum value) throws RoResourceException ;
//	
//	/**
//	 *  Adding RedirectMaxCacheTime AVP of type Unsigned32 to the message.
//	 */
//	public RedirectMaxCacheTimeAvp addRedirectMaxCacheTime(long value) throws RoResourceException ;
//
//	/**
//	 *  Adding RouteRecord AVP of type DiameterIdentity to the message.
//	 */
//	public RouteRecordAvp addRouteRecord(java.lang.String value) throws RoResourceException ;
//
//	/**
//	 *  This method returns the application id associated with this message.
//	 */
//	public long getApplicationId() ;

//	/**
//	 *  Retrieving a single Unsigned32 value from AuthApplicationId AVPs.
//	 */
//	public long getAuthApplicationId() throws RoResourceException ;
//
	/**
	 *  Retrieving a single Unsigned32 value from CCRequestNumber AVPs.
	 */
	public long getCCRequestNumber() throws RoResourceException ;

	/**
	 *  Retrieving a single Enumerated value from CCRequestType AVPs.
	 */
	public long getCCRequestType() throws RoResourceException ;
	
	/**
	 *  Retrieving a single Enumerated value from CCSessionFailover AVPs.
	 */
	public long getCCSessionFailover() throws RoResourceException ;

	/**
	 *  This method returns the command code associated with this message.
	 */
	//public int getCommandCode() ;
//
//	/**
//	 *  Retrieving a single Enumerated value from CreditControlFailureHandling AVPs.
//	 */
//	public int getCreditControlFailureHandling() throws RoResourceException ;
//	
//	/**
//	 *  Retrieving a single Enumerated value from DirectDebitingFailureHandling AVPs.
//	 */
//	public int getDirectDebitingFailureHandling() throws RoResourceException ;
//
//	/**
//	 *  This method the returns the enum value corroesponding to CCRequestTypeAvp.
//	 */
//	public CCRequestTypeEnum getEnumCCRequestType() throws RoResourceException ;
//
//	/**
//	 *  This method the returns the enum value corroesponding to CCSessionFailoverAvp.
//	 */
//	public CCSessionFailoverEnum getEnumCCSessionFailover() throws RoResourceException ;
//
//	/**
//	 *  This method the returns the enum value corroesponding to CreditControlFailureHandlingAvp.
//	 */
//	public CreditControlFailureHandlingEnum getEnumCreditControlFailureHandling() 
//	throws RoResourceException ;
//
//
//	/**
//	 *  This method the returns the enum value corroesponding to DirectDebitingFailureHandlingAvp.
//	 */
//	public DirectDebitingFailureHandlingEnum getEnumDirectDebitingFailureHandling() 
//	throws RoResourceException ;
//
//
//	/**
//	 *  This method the returns the enum value corroesponding to LowBalanceIndicationAvp.
//	 */
//	public LowBalanceIndicationEnum getEnumLowBalanceIndication() throws RoResourceException ;
//
//	/**
//	 *  This method the returns the enum value corroesponding to RedirectHostUsageAvp.
//	 */
//	public RedirectHostUsageEnum getEnumRedirectHostUsage() throws RoResourceException ;
//
//	/**
//	 *  Retrieving a single Grouped value from CostInformation AVPs.
//	 */
//	public CostInformationAvp getGroupedCostInformation() throws RoResourceException ;
//
//	/**
//	 *  Retrieving multiple Grouped values from FailedAVP AVPs.
//	 */
//	public FailedAVPAvp[] getGroupedFailedAVPs() throws RoResourceException ;
//
//	/**
//	 *  Retrieving multiple Grouped values from MultipleServicesCreditControl AVPs.
//	 */
//	public MultipleServicesCreditControlAvp[] getGroupedMultipleServicesCreditControls() 
//	throws RoResourceException ;

	/**
	 *  Retrieving multiple Grouped values from ProxyInfo AVPs.
	 */
	public ArrayList<DiameterAVP> getGroupedProxyInfos() throws RoResourceException ;

//	/**
//	 *  Retrieving a single Grouped value from RemainingBalance AVPs.
//	 */
//	public RemainingBalanceAvp getGroupedRemainingBalance() throws RoResourceException ;
//
//	/**
//	 *  Retrieving a single Grouped value from ServiceInformation AVPs.
//	 */
//	public ServiceInformationAvp getGroupedServiceInformation() throws RoResourceException ;
//
//	/**
//	 *  Retrieving a single Enumerated value from LowBalanceIndication AVPs.
//	 */
//	public int getLowBalanceIndication() throws RoResourceException ;
//
//	//	/**
//	//	 *  Retrieving multiple DiameterURI values from RedirectHost AVPs.
//	//	 */
//	//	public URI[] getRedirectHosts() throws RoResourceException ;
//
//	/**
//	 *  Retrieving a single Enumerated value from RedirectHostUsage AVPs.
//	 */
//	public int getRedirectHostUsage() throws RoResourceException ;
//
//	/**
//	 *  Retrieving a single Unsigned32 value from RedirectMaxCacheTime AVPs.
//	 */
//	public long getRedirectMaxCacheTime() throws RoResourceException ;

	/**
	 *  Retrieving multiple DiameterIdentity values from RouteRecord AVPs.
	 */
	public ArrayList<DiameterAVP> getRouteRecords() throws RoResourceException ;
	
	public static final int code = 271;
	public static final String name = "ACA";
	//public static final Standard standard;
	public static final long applicationId = 3L;
	void addCreditControlFailureHandling(String value)
			throws RoResourceException;

	void addDirectDebitingFailureHandling(String value)
			throws RoResourceException;

	void addCCRequestType(int type, String vendorName)
			throws RoResourceException;

	void addCCSessionFailover(int value)
			throws RoResourceException;

	public CCRequestTypeEnum getEnumCCRequestType();

	void addDiameterInteger32AVP(String name, int value,
			String vendorName);

	void addDiameterInteger64AVP(String name, long value,
			String vendorName);

	void addDiameterUnsigned32AVP(String name, long value,
			String vendorName);

	void addDiameterUnsigned64AVP(String name,
			BigInteger value, String vendorName);

	void addDiameterFloat32AVP(String name, String vendorName,
			float value);

	void addDiameterFloat64AVP(String name, String vendorName,
			double value);

	void addDiameterGenericAVP(long avpCode, long vendorId,
			byte[] value);

	void addDiameterOctetStringAVP(String name,
			String vendorName, byte[] value);

	void addDiameterOctetStringAVP(String name,
			String vendorName, String value);
	
	AvpDiameterGrouped addDiameterGroupedAVP(String avpName, String vendorName);

	void addDiameterAVPs(ArrayList<DiameterAVP> groupedAvps);

	DiameterRoMessageFactory getDiameterRoMessageFactory();

	void addMultipleServiceCreditControl(ArrayList<DiameterAVP> avp)
			throws RoResourceException;

	void addDiameterGroupedAVP(String avpName, String vendorName,
			List<DiameterAVP> groupAvps);

	void setDestinationHost(String host);

	void setServiceContextId(String contextId);

}