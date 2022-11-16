package com.baypackets.ase.ra.diameter.gy.avp;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.baypackets.ase.ra.diameter.gy.GyResourceException;
import com.baypackets.ase.ra.diameter.gy.enums.OnlineChargingFlagEnum;
import com.baypackets.ase.ra.diameter.gy.enums.RoleOfNodeEnum;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.gy.generated.avp.AvpIMSInformation;
import com.traffix.openblox.diameter.gy.generated.avp.AvpApplicationServerInformation;
import com.traffix.openblox.diameter.gy.generated.avp.AvpInterOperatorIdentifier;
import com.traffix.openblox.diameter.gy.generated.avp.AvpMessageBody;
import com.traffix.openblox.diameter.gy.generated.avp.AvpSDPMediaComponent;
import com.traffix.openblox.diameter.gy.generated.avp.AvpServiceSpecificInfo;
import com.traffix.openblox.diameter.gy.generated.enums.EnumNodeFunctionality;
import com.traffix.openblox.diameter.gy.generated.enums.EnumOnlineChargingFlag;
import com.traffix.openblox.diameter.gy.generated.enums.EnumRoleOfNode;

public class IMSInformationAvp extends AvpDiameterGrouped {

	private static Logger logger = Logger.getLogger(IMSInformationAvp.class.getName());
	public static final long vendorId = 0L;

	private AvpIMSInformation stackObj;

	public IMSInformationAvp(AvpIMSInformation stkObj){
		super(stkObj);
		this.stackObj=stkObj;
	}

	/**
	 * This method returns the standard code for this AVP.
	 */
	public int getCode() {
		return stackObj.getCode();
	}

	/**
	 * The standard rule for the M (mandatory) AVP header flag
	 */
	public FlagRuleEnum getMRule() {
		return FlagRuleEnum.getContainerObj(stackObj.getMRule());
	}

	/**
	 * This method returns the name of this AVP.
	 */
	public String getName() {
		return stackObj.getName();
	}

	/**
	 * The standard rule for the P (end-to-end encryption) AVP header flag
	 */
	public FlagRuleEnum getPRule() {
		return FlagRuleEnum.getContainerObj(stackObj.getPRule());
	}

	/**
	 * This method returns the vendor ID associated with this AVP.
	 */
	public long getVendorId() {
		return stackObj.getVendorId();
	}

	/**
	 * The standard rule for the V (vendor) AVP header flag
	 */
	public FlagRuleEnum getVRule() {
		return FlagRuleEnum.getContainerObj(stackObj.getVRule());
	}



	/**
	 * Adding AccessNetworkInformation AVP of type OctetString to the message.
	 * @param value
	 * @return
	 * @throws GyResourceException 
	 */
	public AccessNetworkInformationAvp	addAccessNetworkInformation(byte[] value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addAccessNetworkInformation()");
			}
			return new AccessNetworkInformationAvp(stackObj.addAccessNetworkInformation(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addAccessNetworkInformation ",e);
		}
	}

	/**
	 * Adding AccessNetworkInformation AVP of type OctetString to the message.
	 * @param value
	 * @return
	 */
	public AccessNetworkInformationAvp	addAccessNetworkInformation(java.lang.String value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addAccessNetworkInformation()");
			}
			return new AccessNetworkInformationAvp(stackObj.addAccessNetworkInformation(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addAccessNetworkInformation ",e);
		}
	}

	/**
	 * Adding AlternateChargedPartyAddress AVP of type UTF8String to the message.
	 * @param value
	 * @return
	 */
	public AlternateChargedPartyAddressAvp 	addAlternateChargedPartyAddress(java.lang.String value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addAlternateChargedPartyAddress()");
			}
			return new AlternateChargedPartyAddressAvp(stackObj.addAlternateChargedPartyAddress(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addAlternateChargedPartyAddress ",e);
		}
	}

	/**
	 * Adding AssociatedURI AVP of type UTF8String to the message.
	 * @param value
	 * @return
	 */
	public AssociatedURIAvp 	addAssociatedURI(java.lang.String value)  throws GyResourceException{
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addAssociatedURI()");
			}
			return new AssociatedURIAvp(stackObj.addAssociatedURI(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addAssociatedURI ",e);
		}	
	}

	/**
	 * Adding BearerService AVP of type OctetString to the message.
	 * @param value
	 * @return
	 */
	public BearerServiceAvp 	addBearerService(byte[] value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addBearerService()");
			}
			return new BearerServiceAvp(stackObj.addBearerService(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addBearerService ",e);
		}
	}

	/**
	 * Adding BearerService AVP of type OctetString to the message.
	 * @param value
	 * @return
	 */
	public BearerServiceAvp 	addBearerService(java.lang.String value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addBearerService()");
			}
			return new BearerServiceAvp(stackObj.addBearerService(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addBearerService ",e);
		}
	}

	/**
	 * Adding CalledAssertedIdentity AVP of type UTF8String to the message.
	 * @param value
	 * @return
	 */
	public CalledAssertedIdentityAvp 	addCalledAssertedIdentity(java.lang.String value)  throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addCalledAssertedIdentity()");
			}
			return new CalledAssertedIdentityAvp(stackObj.addCalledAssertedIdentity(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addCalledAssertedIdentity ",e);
		}
	}

	/**
	 * Adding CalledPartyAddress AVP of type UTF8String to the message.
	 * @param value
	 * @return
	 */
	public CalledPartyAddressAvp 	addCalledPartyAddress(java.lang.String value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addCalledPartyAddress()");
			}
			return new CalledPartyAddressAvp(stackObj.addCalledPartyAddress(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addCalledPartyAddress ",e);
		}
	}

	/**
	 * Adding CallingPartyAddress AVP of type UTF8String to the message.
	 * @param value
	 * @return
	 */
	public CallingPartyAddressAvp 	addCallingPartyAddress(java.lang.String value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addCallingPartyAddress()");
			}
			return new CallingPartyAddressAvp(stackObj.addCallingPartyAddress(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addCallingPartyAddress ",e);
		}
	}

	/**
	 * Adding CarrierSelectRoutingInformation AVP of type UTF8String to the message.
	 * @param value
	 * @return
	 */
	public CarrierSelectRoutingInformationAvp 	addCarrierSelectRoutingInformation(java.lang.String value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addCarrierSelectRoutingInformation()");
			}
			return new CarrierSelectRoutingInformationAvp(stackObj.addCarrierSelectRoutingInformation(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addCarrierSelectRoutingInformation ",e);
		}
	}

	/**
	 * Adding CauseCode AVP of type Integer32 to the message.
	 * @param value
	 * @return
	 */
	public CauseCodeAvp 	addCauseCode(int value)  throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addCauseCode()");
			}
			return new CauseCodeAvp(stackObj.addCauseCode(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addCauseCode ",e);
		}
	}

	/**
	 * Adding ApplicationServerInformation AVP of type Grouped to the message.
	 * @return
	 */
	public ApplicationServerInformationAvp 	addGroupedApplicationServerInformation() throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedApplicationServerInformation()");
			}
			return new ApplicationServerInformationAvp(stackObj.addGroupedApplicationServerInformation());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGroupedApplicationServerInformation ",e);
		}
	}

	//	/**
	//	 * Adding EarlyMediaDescription AVP of type Grouped to the message.
	//	 * @return
	//	 */
	//	EarlyMediaDescriptionAvp 	addGroupedEarlyMediaDescription() {
	//		
	//	}

	/**
	 * Adding EventType AVP of type Grouped to the message.
	 * @return
	 */
	public EventTypeAvp 	addGroupedEventType() throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedEventType()");
			}
			return new EventTypeAvp(stackObj.addGroupedEventType());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGroupedEventType ",e);
		}
	}

	/**
	 * Adding InterOperatorIdentifier AVP of type Grouped to the message.
	 * @return
	 */
	public InterOperatorIdentifierAvp 	addGroupedInterOperatorIdentifier() throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedInterOperatorIdentifier()");
			}
			return new InterOperatorIdentifierAvp(stackObj.addGroupedInterOperatorIdentifier());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGroupedInterOperatorIdentifier ",e);
		}
	}

	/**
	 * Adding MessageBody AVP of type Grouped to the message.
	 * @return
	 */
	public MessageBodyAvp 	addGroupedMessageBody() throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedMessageBody()");
			}
			return new MessageBodyAvp(stackObj.addGroupedMessageBody());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGroupedMessageBody ",e);
		}
	}

//	/**
//	 * Adding RealTimeTariffInformation AVP of type Grouped to the message.
//	 * @return
//	 */
//	public RealTimeTariffInformationAvp addGroupedRealTimeTariffInformation() throws GyResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside addGroupedRealTimeTariffInformation()");
//			}
//			return new RealTimeTariffInformationAvp(stackObj.addGroupedRealTimeTariffInformation());
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in addGroupedRealTimeTariffInformation ",e);
//		}
//	}

	/**
	 * Adding SDPMediaComponent AVP of type Grouped to the message.
	 * @return
	 */
	public SDPMediaComponentAvp 	addGroupedSDPMediaComponent() throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedSDPMediaComponent()");
			}
			return new SDPMediaComponentAvp(stackObj.addGroupedSDPMediaComponent());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGroupedSDPMediaComponent ",e);
		}
	}

	/**
	 * Adding ServerCapabilities AVP of type Grouped to the message.
	 * @return
	 */
	public ServerCapabilitiesAvp 	addGroupedServerCapabilities() throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedServerCapabilities()");
			}
			return new ServerCapabilitiesAvp(stackObj.addGroupedServerCapabilities());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGroupedServerCapabilities ",e);
		}
	}

	/**
	 * Adding ServiceSpecificInfo AVP of type Grouped to the message.
	 * @return
	 */
	public ServiceSpecificInfoAvp addGroupedServiceSpecificInfo() throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedServiceSpecificInfo()");
			}
			return new ServiceSpecificInfoAvp(stackObj.addGroupedServiceSpecificInfo());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGroupedServiceSpecificInfo ",e);
		}
	}

	/**
	 * Adding TimeStamps AVP of type Grouped to the message.
	 * @return
	 */
	public TimeStampsAvp addGroupedTimeStamps() throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedTimeStamps()");
			}
			return new TimeStampsAvp(stackObj.addGroupedTimeStamps());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGroupedTimeStamps ",e);
		}
	}

	/**
	 * Adding TrunkGroupId AVP of type Grouped to the message.
	 * @return
	 */
	public TrunkGroupIDAvp addGroupedTrunkGroupId() throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedTrunkGroupId()");
			}
			return new TrunkGroupIDAvp(stackObj.addGroupedTrunkGroupID());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGroupedTrunkGroupId ",e);
		}
	}

	/**
	 * Adding IMSChargingIdentifier AVP of type UTF8String to the message.
	 * @param value
	 * @return
	 */
	public IMSChargingIdentifierAvp addIMSChargingIdentifier(java.lang.String value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addIMSChargingIdentifier()");
			}
			return new IMSChargingIdentifierAvp(stackObj.addIMSChargingIdentifier(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addIMSChargingIdentifier ",e);
		}
	}

	/**
	 * Adding IMSCommunicationServiceIdentifier AVP of type UTF8String to the message.
	 * @param value
	 * @return
	 */
	public IMSCommunicationServiceIdentifierAvp 	addIMSCommunicationServiceIdentifier(java.lang.String value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addIMSCommunicationServiceIdentifier()");
			}
			return new IMSCommunicationServiceIdentifierAvp(stackObj.addIMSCommunicationServiceIdentifier(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addIMSCommunicationServiceIdentifier ",e);
		}
	}

	/**
	 * Adding NodeFunctionality AVP of type Enumerated to the message.
	 * @param value
	 * @return
	 */
	public NodeFunctionalityAvp 	addNodeFunctionality(EnumNodeFunctionality value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addNodeFunctionality()");
			}
			return new NodeFunctionalityAvp(stackObj.addNodeFunctionality(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addNodeFunctionality ",e);
		}
	}

	/**
	 * Adding NumberPortabilityRoutingInformation AVP of type UTF8String to the message.
	 * @param value
	 * @return
	 */
	public NumberPortabilityRoutingInformationAvp 	addNumberPortabilityRoutingInformation(java.lang.String value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addNumberPortabilityRoutingInformation()");
			}
			return new NumberPortabilityRoutingInformationAvp(stackObj.addNumberPortabilityRoutingInformation(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addNumberPortabilityRoutingInformation ",e);
		}
	}

	/**
	 * Adding OnlineChargingFlag AVP of type Enumerated to the message.
	 * @param value
	 * @return
	 */
//	public OnlineChargingFlagAvp 	addOnlineChargingFlag(EnumOnlineChargingFlag value) throws GyResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside addOnlineChargingFlag()");
//			}
//			return new OnlineChargingFlagAvp(stackObj.addOnlineChargingFlag(value));
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in addOnlineChargingFlag ",e);
//		}
//	}

	/**
	 * Adding RequestedPartyAddress AVP of type UTF8String to the message.
	 * @param value
	 * @return
	 */
	public RequestedPartyAddressAvp addRequestedPartyAddress(java.lang.String value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addRequestedPartyAddress()");
			}
			return new RequestedPartyAddressAvp(stackObj.addRequestedPartyAddress(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addRequestedPartyAddress ",e);
		}
	}

	/**
	 * Adding RoleOfNode AVP of type Enumerated to the message.
	 * @param value
	 * @return
	 */
	public RoleOfNodeAvp 	addRoleOfNode(EnumRoleOfNode value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addRoleOfNode()");
			}
			return new RoleOfNodeAvp(stackObj.addRoleOfNode(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addRoleOfNode ",e);
		}
	}

	/**
	 * Adding SDPSessionDescription AVP of type UTF8String to the message.
	 * @param value
	 * @return
	 */
	SDPSessionDescriptionAvp addSDPSessionDescription(java.lang.String value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addSDPSessionDescription()");
			}
			return new SDPSessionDescriptionAvp(stackObj.addSDPSessionDescription(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addSDPSessionDescription ",e);
		}
	}

	/**
	 * Adding ServedPartyIPAddress AVP of type Address to the message.
	 * @param value
	 * @return
	 */
	ServedPartyIPAddressAvp 	addServedPartyIPAddress(byte[] value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addServedPartyIPAddress()");
			}
			return new ServedPartyIPAddressAvp(stackObj.addServedPartyIPAddress(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addServedPartyIPAddress ",e);
		}
	}

	/**
	 * Adding ServedPartyIPAddress AVP of type Address to the message.
	 * @param value
	 * @return
	 */
	ServedPartyIPAddressAvp 	addServedPartyIPAddress(java.net.InetAddress value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addServedPartyIPAddress()");
			}
			return new ServedPartyIPAddressAvp(stackObj.addServedPartyIPAddress(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addServedPartyIPAddress ",e);
		}
	}

	/**
	 * Adding ServiceId AVP of type UTF8String to the message.
	 * @param value
	 * @return
	 */
	public ServiceIdAvp 	addServiceId(java.lang.String value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addServiceId()");
			}
			return new ServiceIdAvp(stackObj.addServiceId(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addServiceId ",e);
		}
	}

	/**
	 * Adding UserSessionId AVP of type UTF8String to the message.
	 * @param value
	 * @return
	 * @throws GyResourceException 
	 */
	public UserSessionIDAvp addUserSessionId(java.lang.String value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addUserSessionId()");
			}
			return new UserSessionIDAvp(stackObj.addUserSessionID(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addUserSessionId ",e);
		}
	}

	/**
	 *  Retrieving a single OctetString value from AccessNetworkInformation AVPs.
	 */
	public java.lang.String getAccessNetworkInformation() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getAccessNetworkInformation()");
			}
			return stackObj.getAccessNetworkInformation();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getAccessNetworkInformation",e);
		}
	}

	/**
	 *  Retrieving a single UTF8String value from AlternateChargedPartyAddress AVPs.
	 */
	public java.lang.String getAlternateChargedPartyAddress() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getAlternateChargedPartyAddress()");
			}
			return stackObj.getAlternateChargedPartyAddress();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getAlternateChargedPartyAddress",e);
		}
	}

	/**
	 *  Retrieving multiple UTF8String values from AssociatedURI AVPs.
	 */
	public java.lang.String[] getAssociatedURIs() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getAssociatedURIs()");
			}
			return stackObj.getAssociatedURIs();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getAssociatedURIs",e);
		}
	}

	/**
	 *  Retrieving a single OctetString value from BearerService AVPs.
	 */
	public java.lang.String getBearerService() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getBearerService()");
			}
			return stackObj.getBearerService();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getBearerService",e);
		}
	}

	/**
	 *  Retrieving multiple UTF8String values from CalledAssertedIdentity AVPs.
	 */
	public java.lang.String[] getCalledAssertedIdentitys() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getCalledAssertedIdentitys()");
			}
			return stackObj.getCalledAssertedIdentitys();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getCalledAssertedIdentitys",e);
		}
	}

	/**
	 *  Retrieving a single UTF8String value from CalledPartyAddress AVPs.
	 */
	public java.lang.String getCalledPartyAddress() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getCalledPartyAddress()");
			}
			return stackObj.getCalledPartyAddress();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getCalledPartyAddress",e);
		}
	}

	/**
	 *  Retrieving multiple UTF8String values from CallingPartyAddress AVPs.
	 */
	public java.lang.String[] getCallingPartyAddresss() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getCallingPartyAddresss()");
			}
			return stackObj.getCallingPartyAddresss();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getCallingPartyAddresss",e);
		}
	}

	/**
	 *  Retrieving a single UTF8String value from CarrierSelectRoutingInformation AVPs.
	 */
	public java.lang.String getCarrierSelectRoutingInformation() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getCarrierSelectRoutingInformation()");
			}
			return stackObj.getCarrierSelectRoutingInformation();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getCarrierSelectRoutingInformation",e);
		}
	}

	/**
	 *  Retrieving a single Integer32 value from CauseCode AVPs.
	 */
	public int getCauseCode() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getCauseCode()");
			}
			return stackObj.getCauseCode();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getCauseCode",e);
		}
	}

	//	/**
	//	 *  this method returns the Enum value of NodeFunctionality.
	//	 */
	//	public NodeFunctionalityEnum getEnumNodeFunctionality() throws GyResourceException { 
	//		try {
	//			if(logger.isDebugEnabled()){
	//				logger.debug("Inside getEnumNodeFunctionality()");
	//			}
	//			return	NodeFunctionalityEnum.getContainerObj(stackObj.getEnumNodeFunctionality());
	//		} catch (ValidationException e) {
	//			throw new GyResourceException("Exception in getEnumNodeFunctionality ",e);
	//		}
	//	}



	/**
	 *  this method returns the Enum value of OnlineChargingFlag.
	 */
//	public OnlineChargingFlagEnum getEnumOnlineChargingFlag() throws GyResourceException { 
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getEnumOnlineChargingFlag()");
//			}
//			return	OnlineChargingFlagEnum.getContainerObj(stackObj.getEnumOnlineChargingFlag());
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in getEnumOnlineChargingFlag ",e);
//		}
//	}



	/**
	 *  this method returns the Enum value of RoleOfNode.
	 */
	public RoleOfNodeEnum getEnumRoleOfNode() throws GyResourceException { 
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getEnumRoleOfNode()");
			}
			return	RoleOfNodeEnum.getContainerObj(stackObj.getEnumRoleOfNode());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in OnlineChargingFlagEnum ",e);
		}
	}



	/**
	 *  Retrieving multiple Grouped values from ApplicationServerInformation AVPs.
	 */
	public ApplicationServerInformationAvp[] getGroupedApplicationServerInformations() throws GyResourceException { 
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedApplicationServerInformations()");
			}
			AvpApplicationServerInformation[] stackAv= stackObj.getGroupedApplicationServerInformations();
			ApplicationServerInformationAvp[] contAvp= new ApplicationServerInformationAvp[stackAv.length];
			for(int i=0;i<stackAv.length;i++){
				contAvp[i]=new ApplicationServerInformationAvp(stackAv[i]);
			}
			return contAvp;
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGroupedApplicationServerInformations",e);
		}
	}
	//
	//	/**
	//	 *  Retrieving multiple Grouped values from EarlyMediaDescription AVPs.
	//	 */
	//	public EarlyMediaDescriptionAvp[] getGroupedEarlyMediaDescriptions() throws GyResourceException { 
	//		try {
	//			if(logger.isDebugEnabled()){
	//				logger.debug("Inside getGroupedEarlyMediaDescriptions()");
	//			}
	//			AvpEarlyMediaDescription[] stackAv= stackObj.getGroupedEarlyMediaDescriptions();
	//			EarlyMediaDescriptionAvp[] contAvp= new EarlyMediaDescriptionAvp[stackAv.length];
	//			for(int i=0;i<stackAv.length;i++){
	//				contAvp[i]=new EarlyMediaDescriptionAvp(stackAv[i]);
	//			}
	//			return contAvp;
	//		} catch (ValidationException e) {
	//			throw new GyResourceException("Exception in getGroupedEarlyMediaDescriptions",e);
	//		}
	//	}

	/**
	 *  Retrieving a single Grouped value from EventType AVPs.
	 */
	public EventTypeAvp getGroupedEventType() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedEventType()");
			}
			return new EventTypeAvp(stackObj.getGroupedEventType());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGroupedEventType",e);
		}
	}

	/**
	 *  Retrieving multiple Grouped values from InterOperatorIdentifier AVPs.
	 */
	public InterOperatorIdentifierAvp[] getGroupedInterOperatorIdentifiers() throws GyResourceException { 
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedInterOperatorIdentifiers()");
			}
			AvpInterOperatorIdentifier[] stackAv= stackObj.getGroupedInterOperatorIdentifiers();
			InterOperatorIdentifierAvp[] contAvp= new InterOperatorIdentifierAvp[stackAv.length];
			for(int i=0;i<stackAv.length;i++){
				contAvp[i]=new InterOperatorIdentifierAvp(stackAv[i]);
			}
			return contAvp;
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGroupedInterOperatorIdentifiers",e);
		}
	}

	/**
	 *  Retrieving multiple Grouped values from MessageBody AVPs.
	 */
	public MessageBodyAvp[] getGroupedMessageBodys() throws GyResourceException { 
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedMessageBodys()");
			}
			AvpMessageBody[] stackAv= stackObj.getGroupedMessageBodys();
			MessageBodyAvp[] contAvp= new MessageBodyAvp[stackAv.length];
			for(int i=0;i<stackAv.length;i++){
				contAvp[i]=new MessageBodyAvp(stackAv[i]);
			}
			return contAvp;
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGroupedMessageBodys",e);
		}
	}

	/**
	 *  Retrieving a single Grouped value from RealTimeTariffInformation AVPs.
	 */
//	public RealTimeTariffInformationAvp getGroupedRealTimeTariffInformation() throws GyResourceException { 
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getGroupedRealTimeTariffInformation()");
//			}
//			return new RealTimeTariffInformationAvp(stackObj.getGroupedRealTimeTariffInformation());
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in getGroupedRealTimeTariffInformation",e);
//		}
//	}

	/**
	 *  Retrieving multiple Grouped values from SDPMediaComponent AVPs.
	 */
	public SDPMediaComponentAvp[] getGroupedSDPMediaComponents() throws GyResourceException { 
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedSDPMediaComponents()");
			}
			AvpSDPMediaComponent[] stackAv= stackObj.getGroupedSDPMediaComponents();
			SDPMediaComponentAvp[] contAvp= new SDPMediaComponentAvp[stackAv.length];
			for(int i=0;i<stackAv.length;i++){
				contAvp[i]=new SDPMediaComponentAvp(stackAv[i]);
			}
			return contAvp;
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGroupedSDPMediaComponents",e);
		}
	}

	/**
	 *  Retrieving a single Grouped value from ServerCapabilities AVPs.
	 */
	public ServerCapabilitiesAvp getGroupedServerCapabilities() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedServerCapabilities()");
			}
			return new ServerCapabilitiesAvp(stackObj.getGroupedServerCapabilities());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGroupedServerCapabilities",e);
		}
	}

	/**
	 *  Retrieving multiple Grouped values from ServiceSpecificInfo AVPs.
	 */
	public ServiceSpecificInfoAvp[] getGroupedServiceSpecificInfos() throws GyResourceException { 
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedServiceSpecificInfos()");
			}
			AvpServiceSpecificInfo[] stackAv= stackObj.getGroupedServiceSpecificInfos();
			ServiceSpecificInfoAvp[] contAvp= new ServiceSpecificInfoAvp[stackAv.length];
			for(int i=0;i<stackAv.length;i++){
				contAvp[i]=new ServiceSpecificInfoAvp(stackAv[i]);
			}
			return contAvp;
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGroupedServiceSpecificInfos",e);
		}
	}

	/**
	 *  Retrieving a single Grouped value from TimeStamps AVPs.
	 */
	public TimeStampsAvp getGroupedTimeStamps() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedTimeStamps()");
			}
			return new TimeStampsAvp(stackObj.getGroupedTimeStamps());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGroupedTimeStamps",e);
		}
	}

	/**
	 *  Retrieving a single Grouped value from TrunkGroupId AVPs.
	 */
	public TrunkGroupIDAvp getGroupedTrunkGroupId() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedTrunkGroupId()");
			}
			return new TrunkGroupIDAvp(stackObj.getGroupedTrunkGroupID());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGroupedTrunkGroupId",e);
		}
	}

	/**
	 *  Retrieving a single UTF8String value from IMSChargingIdentifier AVPs.
	 */
	public java.lang.String getIMSChargingIdentifier() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getIMSChargingIdentifier()");
			}
			return stackObj.getIMSChargingIdentifier();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getIMSChargingIdentifier",e);
		}
	}

	/**
	 *  Retrieving a single UTF8String value from IMSCommunicationServiceIdentifier AVPs.
	 */
	public java.lang.String getIMSCommunicationServiceIdentifier() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getIMSCommunicationServiceIdentifier()");
			}
			return stackObj.getIMSCommunicationServiceIdentifier();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getIMSCommunicationServiceIdentifier",e);
		}
	}

	/**
	 *  Retrieving a single Enumerated value from NodeFunctionality AVPs.
	 */
	public int getNodeFunctionality() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getNodeFunctionality()");
			}
			return stackObj.getNodeFunctionality();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getNodeFunctionality",e);
		}
	}

	/**
	 *  Retrieving a single UTF8String value from NumberPortabilityRoutingInformation AVPs.
	 */
	public java.lang.String getNumberPortabilityRoutingInformation() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getNumberPortabilityRoutingInformation()");
			}
			return stackObj.getNumberPortabilityRoutingInformation();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getNumberPortabilityRoutingInformation",e);
		}
	}

	/**
	 *  Retrieving a single Enumerated value from OnlineChargingFlag AVPs.
	 */
//	public int getOnlineChargingFlag() throws GyResourceException { 
//
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getOnlineChargingFlag()");
//			}
//			return stackObj.getOnlineChargingFlag();
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in getOnlineChargingFlag",e);
//		}
//	}

	/**
	 *  Retrieving a single OctetString value from AccessNetworkInformation AVPs.
	 */
	public byte[] getRawAccessNetworkInformation() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getRawAccessNetworkInformation()");
			}
			return stackObj.getRawAccessNetworkInformation();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getRawAccessNetworkInformation",e);
		}
	}

	/**
	 *  Retrieving a single OctetString value from BearerService AVPs.
	 */
	public byte[] getRawBearerService() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getRawBearerService()");
			}
			return stackObj.getRawBearerService();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getRawBearerService",e);
		}
	}

	/**
	 *  Retrieving a single Address value from ServedPartyIPAddress AVPs.
	 */
	public byte[] getRawServedPartyIPAddress() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getRawServedPartyIPAddress()");
			}
			return stackObj.getRawServedPartyIPAddress();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getRawServedPartyIPAddress",e);
		}
	}

	/**
	 *  Retrieving a single UTF8String value from RequestedPartyAddress AVPs.
	 */
	public java.lang.String getRequestedPartyAddress() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getRequestedPartyAddress()");
			}
			return stackObj.getRequestedPartyAddress();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getRequestedPartyAddress",e);
		}
	}

	/**
	 *  Retrieving a single Enumerated value from RoleOfNode AVPs.
	 */
	public int getRoleOfNode() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getRoleOfNode()");
			}
			return stackObj.getRoleOfNode();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getRoleOfNode",e);
		}
	}

	/**
	 *  Retrieving multiple UTF8String values from SDPSessionDescription AVPs.
	 */
	public java.lang.String[] getSDPSessionDescriptions() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getSDPSessionDescriptions()");
			}
			return stackObj.getSDPSessionDescriptions();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getSDPSessionDescriptions",e);
		}
	}

	/**
	 *  Retrieving a single Address value from ServedPartyIPAddress AVPs.
	 */
	public java.net.InetAddress getServedPartyIPAddress() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getServedPartyIPAddress()");
			}
			return stackObj.getServedPartyIPAddress();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getServedPartyIPAddress",e);
		}
	}

	/**
	 *  Retrieving a single UTF8String value from ServiceId AVPs.
	 */
	public java.lang.String getServiceId() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getServiceId()");
			}
			return stackObj.getServiceId();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getServiceId",e);
		}
	}

	/**
	 *  Retrieving a single UTF8String value from UserSessionId AVPs.
	 */
	public java.lang.String getUserSessionId() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getUserSessionId()");
			}
			return stackObj.getUserSessionID();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getUserSessionId",e);
		}
	}


}