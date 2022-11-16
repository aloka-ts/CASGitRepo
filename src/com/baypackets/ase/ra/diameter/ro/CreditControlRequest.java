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
import com.baypackets.ase.ra.diameter.ro.enums.RequestedActionEnum;
import com.baypackets.ase.resource.ResourceException;

import fr.marben.diameter.DiameterAVP;
import fr.marben.diameter._3gpp.ro.DiameterRoMessageFactory;

public interface CreditControlRequest extends RoRequest {

	//public static final int code = 271;
	public static final String name = "CCR";
	//public static final Standard standard;
	public static final long applicationId = 3L;

//	/**
//	 *  Adding AoCRequestType AVP of type Enumerated to the message.
//	 */
//	public AoCRequestTypeAvp addAoCRequestType(AoCRequestTypeEnum value) throws RoResourceException ;

	/**
	 *  Adding AuthApplicationId AVP of type Unsigned32 to the message.
	 */
//	public AuthApplicationIdAvp addAuthApplicationId(long value) throws RoResourceException ;

	/**
	 *  Adding CCRequestNumber AVP of type Unsigned32 to the message.
	 */
//	public AvpDiameterUnsigned32 addCCRequestNumber(long value) throws RoResourceException ;
//
//	/**
//	 *  Adding CCRequestType AVP of type Enumerated to the message.
//	 */
//	public AvpDiameterInteger32 addCCRequestType(CCRequestTypeEnum value) throws RoResourceException ;

//	/**
//	 *  Adding EventTimestamp AVP of type Time to the message.
//	 */
//	public EventTimestampAvp addEventTimestamp(java.util.Date value) throws RoResourceException ;
//
//	/**
//	 *  Adding MultipleServicesCreditControl AVP of type Grouped to the message.
//	 */
//	public MultipleServicesCreditControlAvp addGroupedMultipleServicesCreditControl() throws RoResourceException ;

//	/**
//	 *  Adding ProxyInfo AVP of type Grouped to the message.
//	 */
//	public ProxyInfoAvp addGroupedProxyInfo() throws RoResourceException ;

//	/**
//	 *  Adding ServiceInformation AVP of type Grouped to the message.
//	 */
//	public ServiceInformationAvp addGroupedServiceInformation() throws RoResourceException ;
//
//	/**
//	 *  Adding SubscriptionId AVP of type Grouped to the message.
//	 */
//	public SubscriptionIdAvp addGroupedSubscriptionId() throws RoResourceException ;
//
//	/**
//	 *  Adding UserEquipmentInfo AVP of type Grouped to the message.
//	 */
//	public UserEquipmentInfoAvp addGroupedUserEquipmentInfo(boolean mFlag) throws RoResourceException ;
//
//	/**
//	 *  Adding MultipleServicesIndicator AVP of type Enumerated to the message.
//	 */
//	public MultipleServicesIndicatorAvp addMultipleServicesIndicator(MultipleServicesIndicatorEnum value) throws RoResourceException ;

//	/**
//	 *  Adding OriginStateId AVP of type Unsigned32 to the message.
//	 */
//	public OriginStateIdAvp addOriginStateId(long value) throws RoResourceException ;

//	/**
//	 *  Adding RequestedAction AVP of type Enumerated to the message.
//	 */
//	public RequestedActionAvp addRequestedAction(int value) throws RoResourceException ;

	/**
	 *  Adding RouteRecord AVP of type DiameterIdentity to the message.
	 */
//	public RouteRecordAvp addRouteRecord(java.lang.String value) throws RoResourceException ;

//	/**
//	 *  Adding ServiceContextId AVP of type UTF8String to the message.
//	 */
//	public ServiceContextIdAvp addServiceContextId(java.lang.String value) throws RoResourceException ;

//	/**
//	 *  Adding TerminationCause AVP of type Enumerated to the message.
//	 */
//	public TerminationCauseAvp addTerminationCause(TerminationCauseEnum value) throws RoResourceException ;
//
//	/**
//	 *  Adding UserName AVP of type UTF8String to the message.
//	 */
//	public UserNameAvp addUserName(java.lang.String value) throws RoResourceException ;

	/**
	 *  Create an answer with a given result code.
	 */
	public CreditControlAnswer createAnswer(String resultCode) throws RoResourceException ;

	/**
	 *  Create an answer with a given vendor specific experimental result code.
	 */
	public CreditControlAnswer createAnswer(long vendorId, int experimentalResultCode) throws RoResourceException ;

	/**
	 *  Retrieving a single Enumerated value from AoCRequestType AVPs.
	 */
	public int getAoCRequestType() throws RoResourceException ;

	/**
	 *  This method returns the applicationId associated with this message.
	 */
	public long getApplicationId() ;
	
	/**
	 *  This method returns the time stamp associated with this message.
	 */
	public long getTimestamp() ;

	/**
	 *  Retrieving a single Unsigned32 value from AuthApplicationId AVPs.
	 */
	public long getAuthApplicationId() throws RoResourceException ;

	/**
	 *  Retrieving a single OctetString value from CCCorrelationId AVPs.
	 */
	public java.lang.String getCCCorrelationId() throws RoResourceException ;

	/**
	 *  Retrieving a single Unsigned32 value from CCRequestNumber AVPs.
	 */
	public long getCCRequestNumber() throws RoResourceException ;

	/**
	 *  Retrieving a single Enumerated value from CCRequestType AVPs.
	 */
	public long getCCRequestType() throws RoResourceException ;

	/**
	 *  This method returns the command code associated with this message.
	 */
	public int getCommandCode();


	/**
	 *  Retrieving a single DiameterIdentity value from DestinationHost AVPs.
	 */
	public java.lang.String getDestinationHost() ;

	/**
	 *  Retrieving a single DiameterIdentity value from DestinationRealm AVPs.
	 */
	public java.lang.String getDestinationRealm() throws ResourceException ;

//	/**
//	 *  This method returns the enum vlaue associated with AoCRequestType.
//	 */
//	public AoCRequestTypeEnum getEnumAoCRequestType() throws RoResourceException ;
//
//
//	/**
//	 *  This method returns the enum vlaue associated with CCRequestType.
//	 */
//	public CCRequestTypeEnum getEnumCCRequestType() throws RoResourceException ;

//
//	/**
//	 *  This method returns the enum vlaue associated with MultipleServicesIndicator.
//	 */
//	public MultipleServicesIndicatorEnum getEnumMultipleServicesIndicator() throws RoResourceException ;


//	/**
//	 *  This method returns the enum vlaue associated with RequestedAction.
//	 */
//	public RequestedActionEnum getEnumRequestedAction() throws RoResourceException ;
//
//
//	/**
//	 *  This method returns the enum vlaue associated with TerminationCause.
//	 */
//	public TerminationCauseEnum getEnumTerminationCause() throws RoResourceException ;
//
//
//	/**
//	 *  Retrieving a single Time value from EventTimestamp AVPs.
//	 */
//	public java.util.Date getEventTimestamp() throws RoResourceException ;
//
//	/**
//	 *  Retrieving multiple Grouped values from MultipleServicesCreditControl AVPs.
//	 */
//	public MultipleServicesCreditControlAvp[] getGroupedMultipleServicesCreditControls() throws RoResourceException ;

	/**
	 *  Retrieving multiple Grouped values from ProxyInfo AVPs.
	 */
	public ArrayList<DiameterAVP> getGroupedProxyInfos() throws RoResourceException ;

//	/**
//	 *  Retrieving a single Grouped value from ServiceInformation AVPs.
//	 */
//	public ServiceInformationAvp getGroupedServiceInformation() throws RoResourceException ;
//
//	/**
//	 *  Retrieving multiple Grouped values from SubscriptionId AVPs.
//	 */
//	public SubscriptionIdAvp[] getGroupedSubscriptionIds() throws RoResourceException ;
//
//	/**
//	 *  Retrieving a single Grouped value from UserEquipmentInfo AVPs.
//	 */
//	public UserEquipmentInfoAvp getGroupedUserEquipmentInfo() throws RoResourceException ;
//
//	/**
//	 *  Retrieving a single Enumerated value from MultipleServicesIndicator AVPs.
//	 */
//	public int getMultipleServicesIndicator() throws RoResourceException ;

	/**
	 *  This method returns the name associated with this message.
	 */
	public java.lang.String getName() ;


	/**
	 *  Retrieving a single DiameterIdentity value from OriginHost AVPs.
	 */
	public java.lang.String getOriginHost() throws ResourceException;;

	/**
	 *  Retrieving a single DiameterIdentity value from OriginRealm AVPs.
	 */
	public java.lang.String getOriginRealm() throws ResourceException; ;

//	/**
//	 *  Retrieving a single Unsigned32 value from OriginStateId AVPs.
//	 */
//	public long getOriginStateId() throws RoResourceException ;
//
//	/**
//	 *  Retrieving a single OctetString value from CCCorrelationId AVPs.
//	 */
//	public byte[] getRawCCCorrelationId() throws RoResourceException ;

	/**
	 *  Retrieving a single Enumerated value from RequestedAction AVPs.
	 */
	public long getRequestedAction() throws RoResourceException ;

	/**
	 *  Retrieving multiple DiameterIdentity values from RouteRecord AVPs.
	 */
	public ArrayList<DiameterAVP> getRouteRecords() throws RoResourceException ;

	/**
	 *  Retrieving a single UTF8String value from ServiceContextId AVPs.
	 */
	public java.lang.String getServiceContextId() throws RoResourceException ;

	/**
	 *  Retrieving a single UTF8String value from SessionId AVPs.
	 */
	public java.lang.String getSessionId() ;

	/**
	 *  This method returns the standard associated with this message.
	 */
	//public Standard getStandard() throws RoResourceException ;


//	/**
//	 *  Retrieving a single Enumerated value from TerminationCause AVPs.
//	 */
//	public int getTerminationCause() throws RoResourceException ;
//
//	/**
//	 *  Retrieving a single UTF8String value from UserName AVPs.
//	 */
//	public java.lang.String getUserName() throws RoResourceException ;

	/**
	 *  Runs content validation of the message.
	 */
	public ValidationRecord validate() ;

	void addCCCorrelationId(byte[] value, boolean mFlag,
			String vendorName) throws RoResourceException;

	void addCCCorrelationId(String value, boolean mFlag,
			String vendorName) throws RoResourceException;

	void addCCRequestNumber(long value)
			throws RoResourceException;

	void addCCRequestType(int type)
			throws RoResourceException;

	void addRequestedAction(int value)throws RoResourceException;

	void addServiceContextId(String value)
			throws RoResourceException;

	public CCRequestTypeEnum getEnumCCRequestType();
	
	public RequestedActionEnum getEnumRequestedAction();

	void addDiameterOctetStringAVP(String name,
			String vendorName, String value);

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

	AvpDiameterGrouped addDiameterGroupedAVP(String avpName, String vendorName);

	void addDiameterAVPs(ArrayList<DiameterAVP> groupedAvps);

	DiameterRoMessageFactory getDiameterRoMessageFactory();

	void addMultipleServiceCreditControl(ArrayList<DiameterAVP> avp)
			throws RoResourceException;

	void addDiameterGroupedAVP(String avpName, String vendorName,
			List<DiameterAVP> groupAvps);
	
	void setDestinationHost(String host);
	
//	void setEventTimeStamp(String timestamp);
	
	void setServiceContextId(String contextId);
	
//	void setUserName(String username);
//	
//	void setOriginStateId(String stateId);
	
}
