package com.baypackets.ase.ra.diameter.gy;
import com.baypackets.ase.ra.diameter.common.exception.ValidationRecord;
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
import com.baypackets.ase.resource.ResourceException;
import com.traffix.openblox.diameter.gy.generated.event.MessageCCA;

public interface CreditControlRequest extends GyRequest {

	//public static final int code = 271;
	public static final String name = "CCR";
	//public static final Standard standard;
	public static final long applicationId = 3L;

	/**
	 *  Adding AoCRequestType AVP of type Enumerated to the message.
	 */
	public AoCRequestTypeAvp addAoCRequestType(AoCRequestTypeEnum value) throws GyResourceException ;

	/**
	 *  Adding AuthApplicationId AVP of type Unsigned32 to the message.
	 */
	public AuthApplicationIdAvp addAuthApplicationId(long value) throws GyResourceException ;

	/**
	 *  Adding CCCorrelationId AVP of type OctetString to the message.
	 */
	public CCCorrelationIdAvp addCCCorrelationId(byte[] value, boolean mFlag) throws GyResourceException ;

	/**
	 *  Adding CCCorrelationId AVP of type OctetString to the message.
	 */
	public CCCorrelationIdAvp addCCCorrelationId(java.lang.String value, boolean mFlag) throws GyResourceException ;

	/**
	 *  Adding CCRequestNumber AVP of type Unsigned32 to the message.
	 */
	public CCRequestNumberAvp addCCRequestNumber(long value) throws GyResourceException ;

	/**
	 *  Adding CCRequestType AVP of type Enumerated to the message.
	 */
	public CCRequestTypeAvp addCCRequestType(CCRequestTypeEnum value) throws GyResourceException ;

	/**
	 *  Adding EventTimestamp AVP of type Time to the message.
	 */
	public EventTimestampAvp addEventTimestamp(java.util.Date value) throws GyResourceException ;

	/**
	 *  Adding MultipleServicesCreditControl AVP of type Grouped to the message.
	 */
	public MultipleServicesCreditControlAvp addGroupedMultipleServicesCreditControl() throws GyResourceException ;

	/**
	 *  Adding ProxyInfo AVP of type Grouped to the message.
	 */
	public ProxyInfoAvp addGroupedProxyInfo() throws GyResourceException ;

	/**
	 *  Adding ServiceInformation AVP of type Grouped to the message.
	 */
	public ServiceInformationAvp addGroupedServiceInformation() throws GyResourceException ;

	/**
	 *  Adding SubscriptionId AVP of type Grouped to the message.
	 */
	public SubscriptionIdAvp addGroupedSubscriptionId() throws GyResourceException ;

	/**
	 *  Adding UserEquipmentInfo AVP of type Grouped to the message.
	 */
	public UserEquipmentInfoAvp addGroupedUserEquipmentInfo(boolean mFlag) throws GyResourceException ;

	/**
	 *  Adding MultipleServicesIndicator AVP of type Enumerated to the message.
	 */
	public MultipleServicesIndicatorAvp addMultipleServicesIndicator(MultipleServicesIndicatorEnum value) throws GyResourceException ;

	/**
	 *  Adding OriginStateId AVP of type Unsigned32 to the message.
	 */
	public OriginStateIdAvp addOriginStateId(long value) throws GyResourceException ;

	/**
	 *  Adding RequestedAction AVP of type Enumerated to the message.
	 */
	public RequestedActionAvp addRequestedAction(RequestedActionEnum value) throws GyResourceException ;

	/**
	 *  Adding RouteRecord AVP of type DiameterIdentity to the message.
	 */
	public RouteRecordAvp addRouteRecord(java.lang.String value) throws GyResourceException ;

	/**
	 *  Adding ServiceContextId AVP of type UTF8String to the message.
	 */
	public ServiceContextIdAvp addServiceContextId(java.lang.String value) throws GyResourceException ;

	/**
	 *  Adding TerminationCause AVP of type Enumerated to the message.
	 */
	public TerminationCauseAvp addTerminationCause(TerminationCauseEnum value) throws GyResourceException ;

	/**
	 *  Adding UserName AVP of type UTF8String to the message.
	 */
	public UserNameAvp addUserName(java.lang.String value) throws GyResourceException ;

	/**
	 *  Create an answer with a given result code.
	 */
	public CreditControlAnswer createAnswer(long resultCode) throws GyResourceException ;

	/**
	 *  Create an answer with a given vendor specific experimental result code.
	 */
	public CreditControlAnswer createAnswer(long vendorId, long experimentalResultCode) throws GyResourceException ;

	/**
	 *  Retrieving a single Enumerated value from AoCRequestType AVPs.
	 */
	public int getAoCRequestType() throws GyResourceException ;

	/**
	 *  This method returns the applicationId associated with this message.
	 */
	public long getApplicationId() ;

	/**
	 *  Retrieving a single Unsigned32 value from AuthApplicationId AVPs.
	 */
	public long getAuthApplicationId() throws GyResourceException ;

	/**
	 *  Retrieving a single OctetString value from CCCorrelationId AVPs.
	 */
	public java.lang.String getCCCorrelationId() throws GyResourceException ;

	/**
	 *  Retrieving a single Unsigned32 value from CCRequestNumber AVPs.
	 */
	public long getCCRequestNumber() throws GyResourceException ;

	/**
	 *  Retrieving a single Enumerated value from CCRequestType AVPs.
	 */
	public int getCCRequestType() throws GyResourceException ;

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

	/**
	 *  This method returns the enum vlaue associated with AoCRequestType.
	 */
	public AoCRequestTypeEnum getEnumAoCRequestType() throws GyResourceException ;


	/**
	 *  This method returns the enum vlaue associated with CCRequestType.
	 */
	public CCRequestTypeEnum getEnumCCRequestType() throws GyResourceException ;


	/**
	 *  This method returns the enum vlaue associated with MultipleServicesIndicator.
	 */
	public MultipleServicesIndicatorEnum getEnumMultipleServicesIndicator() throws GyResourceException ;


	/**
	 *  This method returns the enum vlaue associated with RequestedAction.
	 */
	public RequestedActionEnum getEnumRequestedAction() throws GyResourceException ;


	/**
	 *  This method returns the enum vlaue associated with TerminationCause.
	 */
	public TerminationCauseEnum getEnumTerminationCause() throws GyResourceException ;


	/**
	 *  Retrieving a single Time value from EventTimestamp AVPs.
	 */
	public java.util.Date getEventTimestamp() throws GyResourceException ;

	/**
	 *  Retrieving multiple Grouped values from MultipleServicesCreditControl AVPs.
	 */
	public MultipleServicesCreditControlAvp[] getGroupedMultipleServicesCreditControls() throws GyResourceException ;

	/**
	 *  Retrieving multiple Grouped values from ProxyInfo AVPs.
	 */
	public ProxyInfoAvp[] getGroupedProxyInfos() throws GyResourceException ;

	/**
	 *  Retrieving a single Grouped value from ServiceInformation AVPs.
	 */
	public ServiceInformationAvp getGroupedServiceInformation() throws GyResourceException ;

	/**
	 *  Retrieving multiple Grouped values from SubscriptionId AVPs.
	 */
	public SubscriptionIdAvp[] getGroupedSubscriptionIds() throws GyResourceException ;

	/**
	 *  Retrieving a single Grouped value from UserEquipmentInfo AVPs.
	 */
	public UserEquipmentInfoAvp getGroupedUserEquipmentInfo() throws GyResourceException ;

	/**
	 *  Retrieving a single Enumerated value from MultipleServicesIndicator AVPs.
	 */
	public int getMultipleServicesIndicator() throws GyResourceException ;

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

	/**
	 *  Retrieving a single Unsigned32 value from OriginStateId AVPs.
	 */
	public long getOriginStateId() throws GyResourceException ;

	/**
	 *  Retrieving a single OctetString value from CCCorrelationId AVPs.
	 */
	public byte[] getRawCCCorrelationId() throws GyResourceException ;

	/**
	 *  Retrieving a single Enumerated value from RequestedAction AVPs.
	 */
	public int getRequestedAction() throws GyResourceException ;

	/**
	 *  Retrieving multiple DiameterIdentity values from RouteRecord AVPs.
	 */
	public java.lang.String[] getRouteRecords() throws GyResourceException ;

	/**
	 *  Retrieving a single UTF8String value from ServiceContextId AVPs.
	 */
	public java.lang.String getServiceContextId() throws GyResourceException ;

	/**
	 *  Retrieving a single UTF8String value from SessionId AVPs.
	 */
	public java.lang.String getSessionId() ;

	/**
	 *  This method returns the standard associated with this message.
	 */
	//public Standard getStandard() throws GyResourceException ;


	/**
	 *  Retrieving a single Enumerated value from TerminationCause AVPs.
	 */
	public int getTerminationCause() throws GyResourceException ;

	/**
	 *  Retrieving a single UTF8String value from UserName AVPs.
	 */
	public java.lang.String getUserName() throws GyResourceException ;

	/**
	 *  Runs content validation of the message.
	 */
	public ValidationRecord validate() ;
	
	public AcctMultiSessionIdAvp addAcctMultiSessionId(long value, boolean mFlag) throws GyResourceException ;
	
	public CCSubSessionIdAvp addCCSubSessionId(long value, boolean mFlag)  throws GyResourceException ;
	
	public ServiceParameterInfoAvp addGroupedServiceParameterInfo(boolean mFlag)  throws GyResourceException ;
	
	public  UsedServiceUnitAvp addGroupedUsedServiceUnit() throws GyResourceException ;
	
	public  ServiceIdentifierAvp addServiceIdentifier(long value) throws GyResourceException ;
	
	public long getAcctMultiSessionId() throws GyResourceException ;
	
	public long getCCSubSessionId()  throws GyResourceException ;
	
	public ServiceParameterInfoAvp[] getGroupedServiceParameterInfos()  throws GyResourceException ;
		
	public  UsedServiceUnitAvp[] getGroupedUsedServiceUnits()  throws GyResourceException ;
	
	public long getServiceIdentifier()  throws GyResourceException ;
		
}