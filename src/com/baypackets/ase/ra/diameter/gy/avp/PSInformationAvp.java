package com.baypackets.ase.ra.diameter.gy.avp;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.baypackets.ase.ra.diameter.gy.GyResourceException;
import com.baypackets.ase.ra.diameter.gy.enums.ChargingCharacteristicsSelectionModeEnum;
import com.baypackets.ase.ra.diameter.gy.enums.DynamicAddressFlagEnum;
import com.baypackets.ase.ra.diameter.gy.enums.IMSIUnauthenticatedFlagEnum;
import com.baypackets.ase.ra.diameter.gy.enums.PDPContextTypeEnum;
import com.baypackets.ase.ra.diameter.gy.enums.PDPType3GPPEnum;
import com.baypackets.ase.ra.diameter.gy.enums.SGWChangeEnum;
import com.baypackets.ase.ra.diameter.gy.enums.ServingNodeTypeEnum;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.gy.generated.avp.AvpPSInformation;
import com.traffix.openblox.diameter.gy.generated.avp.AvpServiceDataContainer;
import com.traffix.openblox.diameter.gy.generated.avp.AvpTrafficDataVolumes;

public class PSInformationAvp extends AvpDiameterGrouped {

	private static Logger logger = Logger.getLogger(PSInformationAvp.class.getName());
	public static final long vendorId = 0L;

	private AvpPSInformation stackObj;

	public PSInformationAvp(AvpPSInformation stkObj){
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
	 *  Adding 3GPP2BSID AVP of type UTF8String to the message.
	 */
//	public BSID3GPP2Avp add3GPP2BSID(java.lang.String value) throws GyResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside add3GPP2BSID()");
//			}
//			return new BSID3GPP2Avp(stackObj.add3GPP2BSID(value));
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in add3GPP2BSID",e);
//		}
//	}

	/**
	 *  Adding 3GPPChargingCharacteristics AVP of type UTF8String to the message.
	 */
	public ChargingCharacteristics3GPPAvp add3GPPChargingCharacteristics(java.lang.String value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside add3GPPChargingCharacteristics()");
			}
			return new ChargingCharacteristics3GPPAvp(stackObj.add3GPPChargingCharacteristics(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in add3GPPChargingCharacteristics",e);
		}
	}

	/**
	 *  Adding 3GPPChargingId AVP of type OctetString to the message.
	 */
	public ChargingId3GPPAvp add3GPPChargingId(byte[] value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside add3GPPChargingId()");
			}
			return new ChargingId3GPPAvp(stackObj.add3GPPChargingId(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in add3GPPChargingId",e);
		}
	}

	/**
	 *  Adding 3GPPChargingId AVP of type OctetString to the message.
	 */
	public ChargingId3GPPAvp add3GPPChargingId(java.lang.String value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside add3GPPChargingId()");
			}
			return new ChargingId3GPPAvp(stackObj.add3GPPChargingId(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in add3GPPChargingId",e);
		}
	}

	/**
	 *  Adding 3GPPGGSNMCCMNC AVP of type UTF8String to the message.
	 */
	public GGSNMCCMNC3GPPAvp add3GPPGGSNMCCMNC(java.lang.String value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside add3GPPGGSNMCCMNC()");
			}
			return new GGSNMCCMNC3GPPAvp(stackObj.add3GPPGGSNMCCMNC(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in add3GPPGGSNMCCMNC",e);
		}
	}

	/**
	 *  Adding 3GPPIMSIMCCMNC AVP of type UTF8String to the message.
	 */
	public IMSIMCCMNC3GPPAvp add3GPPIMSIMCCMNC(java.lang.String value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside add3GPPIMSIMCCMNC()");
			}
			return new IMSIMCCMNC3GPPAvp(stackObj.add3GPPIMSIMCCMNC(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in add3GPPIMSIMCCMNC",e);
		}
	}

	/**
	 *  Adding 3GPPMSTimeZone AVP of type OctetString to the message.
	 */
	public MSTimeZone3GPPAvp add3GPPMSTimeZone(byte[] value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside add3GPPMSTimeZone()");
			}
			return new MSTimeZone3GPPAvp(stackObj.add3GPPMSTimeZone(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in add3GPPMSTimeZone",e);
		}
	}

	/**
	 *  Adding 3GPPMSTimeZone AVP of type OctetString to the message.
	 */
	public MSTimeZone3GPPAvp add3GPPMSTimeZone(java.lang.String value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside add3GPPMSTimeZone()");
			}
			return new MSTimeZone3GPPAvp(stackObj.add3GPPMSTimeZone(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in add3GPPMSTimeZone",e);
		}
	}

	/**
	 *  Adding 3GPPNSAPI AVP of type UTF8String to the message.
	 */
	public NSAPI3GPPAvp add3GPPNSAPI(java.lang.String value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside add3GPPNSAPI()");
			}
			return new NSAPI3GPPAvp(stackObj.add3GPPNSAPI(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in add3GPPNSAPI",e);
		}
	}

	/**
	 *  Adding 3GPPPDPType AVP of type Enumerated to the message.
	 */
	public PDPType3GPPAvp add3GPPPDPType(PDPType3GPPEnum value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside add3GPPPDPType()");
			}
			return new PDPType3GPPAvp(stackObj.add3GPPPDPType(PDPType3GPPEnum.getStackObj(value)));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in add3GPPPDPType",e);
		}
	}

	/**
	 *  Adding 3GPPRATType AVP of type OctetString to the message.
	 */
	public RATType3GPPAvp add3GPPRATType(byte[] value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside add3GPPRATType()");
			}
			return new RATType3GPPAvp(stackObj.add3GPPRATType(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in add3GPPRATType",e);
		}
	}

	/**
	 *  Adding 3GPPRATType AVP of type OctetString to the message.
	 */
	public RATType3GPPAvp add3GPPRATType(java.lang.String value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside add3GPPRATType()");
			}
			return new RATType3GPPAvp(stackObj.add3GPPRATType(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in add3GPPRATType",e);
		}
	}

	/**
	 *  Adding 3GPPSelectionMode AVP of type UTF8String to the message.
	 */
	public SelectionMode3GPPAvp add3GPPSelectionMode(java.lang.String value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside add3GPPSelectionMode()");
			}
			return new SelectionMode3GPPAvp(stackObj.add3GPPSelectionMode(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in add3GPPSelectionMode",e);
		}
	}

	/**
	 *  Adding 3GPPSessionStopIndicator AVP of type UTF8String to the message.
	 */
	public SessionStopIndicator3GPPAvp add3GPPSessionStopIndicator(java.lang.String value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside add3GPPSessionStopIndicator()");
			}
			return new SessionStopIndicator3GPPAvp(stackObj.add3GPPSessionStopIndicator(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in add3GPPSessionStopIndicator",e);
		}
	}

	/**
	 *  Adding 3GPPSGSNMCCMNC AVP of type UTF8String to the message.
	 */
	public SGSNMCCMNC3GPPAvp add3GPPSGSNMCCMNC(java.lang.String value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside add3GPPSGSNMCCMNC()");
			}
			return new SGSNMCCMNC3GPPAvp(stackObj.add3GPPSGSNMCCMNC(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in add3GPPSGSNMCCMNC",e);
		}
	}

	/**
	 *  Adding 3GPPUserLocationInfo AVP of type OctetString to the message.
	 */
	public UserLocationInfo3GPPAvp add3GPPUserLocationInfo(byte[] value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside add3GPPUserLocationInfo()");
			}
			return new UserLocationInfo3GPPAvp(stackObj.add3GPPUserLocationInfo(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in add3GPPUserLocationInfo",e);
		}
	}

	/**
	 *  Adding 3GPPUserLocationInfo AVP of type OctetString to the message.
	 */
	public UserLocationInfo3GPPAvp add3GPPUserLocationInfo(java.lang.String value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside add3GPPUserLocationInfo()");
			}
			return new UserLocationInfo3GPPAvp(stackObj.add3GPPUserLocationInfo(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in add3GPPUserLocationInfo",e);
		}
	}

	/**
	 *  Adding CalledStationId AVP of type UTF8String to the message.
	 */
	public CalledStationIdAvp addCalledStationId(java.lang.String value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addCalledStationId()");
			}
			return new CalledStationIdAvp(stackObj.addCalledStationId(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addCalledStationId",e);
		}
	}

	/**
	 *  Adding CGAddress AVP of type Address to the message.
	 */
	public CGAddressAvp addCGAddress(byte[] value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addCGAddress()");
			}
			return new CGAddressAvp(stackObj.addCGAddress(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addCGAddress",e);
		}
	}

	/**
	 *  Adding CGAddress AVP of type Address to the message.
	 */
	public CGAddressAvp addCGAddress(java.net.InetAddress value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addCGAddress()");
			}
			return new CGAddressAvp(stackObj.addCGAddress(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addCGAddress",e);
		}
	}

	/**
	 *  Adding ChangeCondition AVP of type Integer32 to the message.
	 */
//	public ChangeConditionAvp addChangeCondition(int value) throws GyResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside addChangeCondition()");
//			}
//			return new ChangeConditionAvp(stackObj.addChangeCondition(value));
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in addChangeCondition",e);
//		}
//	}

	/**
	 *  Adding ChargingCharacteristicsSelectionMode AVP of type Enumerated to the message.
	 */
//	public ChargingCharacteristicsSelectionModeAvp addChargingCharacteristicsSelectionMode(ChargingCharacteristicsSelectionModeEnum value) throws GyResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside addChargingCharacteristicsSelectionMode()");
//			}
//			return new ChargingCharacteristicsSelectionModeAvp(stackObj.addChargingCharacteristicsSelectionMode(ChargingCharacteristicsSelectionModeEnum.getStackObj(value)));
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in addChargingCharacteristicsSelectionMode",e);
//		}
//	}

	/**
	 *  Adding ChargingRuleBaseName AVP of type UTF8String to the message.
	 */
	public ChargingRuleBaseNameAvp addChargingRuleBaseName(java.lang.String value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addChargingRuleBaseName()");
			}
			return new ChargingRuleBaseNameAvp(stackObj.addChargingRuleBaseName(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addChargingRuleBaseName",e);
		}
	}

	/**
	 *  Adding Diagnostics AVP of type Integer32 to the message.
	 */
//	public DiagnosticsAvp addDiagnostics(int value) throws GyResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside addDiagnostics()");
//			}
//			return new DiagnosticsAvp(stackObj.addDiagnostics(value));
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in addDiagnostics",e);
//		}
//	}

	/**
	 *  Adding DynamicAddressFlag AVP of type Enumerated to the message.
	 */
//	public DynamicAddressFlagAvp addDynamicAddressFlag(DynamicAddressFlagEnum value) throws GyResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside addDynamicAddressFlag()");
//			}
//			return new DynamicAddressFlagAvp(stackObj.addDynamicAddressFlag(DynamicAddressFlagEnum.getStackObj(value)));
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in addDynamicAddressFlag",e);
//		}
//	}

	/**
	 *  Adding GGSNAddress AVP of type Address to the message.
	 */
	public GGSNAddressAvp addGGSNAddress(byte[] value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGGSNAddress()");
			}
			return new GGSNAddressAvp(stackObj.addGGSNAddress(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGGSNAddress",e);
		}
	}

	/**
	 *  Adding GGSNAddress AVP of type Address to the message.
	 */
	public GGSNAddressAvp addGGSNAddress(java.net.InetAddress value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGGSNAddress()");
			}
			return new GGSNAddressAvp(stackObj.addGGSNAddress(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGGSNAddress",e);
		}
	}

	/**
	 *  Adding OfflineCharging AVP of type Grouped to the message.
	 */
	public OfflineChargingAvp addGroupedOfflineCharging( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedOfflineCharging()");
			}
			return new OfflineChargingAvp(stackObj.addGroupedOfflineCharging());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGroupedOfflineCharging",e);
		}
	}

	/**
	 *  Adding PSFurnishChargingInformation AVP of type Grouped to the message.
	 */
	public PSFurnishChargingInformationAvp addGroupedPSFurnishChargingInformation( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedPSFurnishChargingInformation()");
			}
			return new PSFurnishChargingInformationAvp(stackObj.addGroupedPSFurnishChargingInformation());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGroupedPSFurnishChargingInformation",e);
		}
	}

	/**
	 *  Adding QoSInformation AVP of type Grouped to the message.
	 */
//	public QoSInformationAvp addGroupedQoSInformation( ) throws GyResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside addGroupedQoSInformation()");
//			}
//			return new QoSInformationAvp(stackObj.addGroupedQoSInformation());
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in addGroupedQoSInformation",e);
//		}
//	}

	/**
	 *  Adding ServiceDataContainer AVP of type Grouped to the message.
	 */
//	public ServiceDataContainerAvp addGroupedServiceDataContainer( ) throws GyResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside addGroupedServiceDataContainer()");
//			}
//			return new ServiceDataContainerAvp(stackObj.addGroupedServiceDataContainer());
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in addGroupedServiceDataContainer",e);
//		}
//	}

	/**
	 *  Adding TerminalInformation AVP of type Grouped to the message.
	 */
//	public TerminalInformationAvp addGroupedTerminalInformation( ) throws GyResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside addGroupedTerminalInformation()");
//			}
//			return new TerminalInformationAvp(stackObj.addGroupedTerminalInformation());
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in addGroupedTerminalInformation",e);
//		}
//	}

	/**
	 *  Adding TrafficDataVolumes AVP of type Grouped to the message.
	 */
//	public TrafficDataVolumesAvp addGroupedTrafficDataVolumes( ) throws GyResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside addGroupedTrafficDataVolumes()");
//			}
//			return new TrafficDataVolumesAvp(stackObj.addGroupedTrafficDataVolumes());
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in addGroupedTrafficDataVolumes",e);
//		}
//	}

	/**
	 *  Adding UserCSGInformation AVP of type Grouped to the message.
	 */
//	public UserCSGInformationAvp addGroupedUserCSGInformation( ) throws GyResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside addGroupedUserCSGInformation()");
//			}
//			return new UserCSGInformationAvp(stackObj.addGroupedUserCSGInformation());
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in addGroupedUserCSGInformation",e);
//		}
//	}

	/**
	 *  Adding UserEquipmentInfo AVP of type Grouped to the message.
//	 */
//	public UserEquipmentInfoAvp addGroupedUserEquipmentInfo(boolean mFlag) throws GyResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside addGroupedUserEquipmentInfo()");
//			}
//			return new UserEquipmentInfoAvp(stackObj.addGroupedUserEquipmentInfo(mFlag));
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in addGroupedUserEquipmentInfo",e);
//		}
//	}

	/**
	 *  Adding IMSIUnauthenticatedFlag AVP of type Enumerated to the message.
	 */
//	public IMSIUnauthenticatedFlagAvp addIMSIUnauthenticatedFlag(IMSIUnauthenticatedFlagEnum value) throws GyResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside addIMSIUnauthenticatedFlag()");
//			}
//			return new IMSIUnauthenticatedFlagAvp(stackObj.addIMSIUnauthenticatedFlag(IMSIUnauthenticatedFlagEnum.getStackObj(value)));
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in addIMSIUnauthenticatedFlag",e);
//		}
//	}

	/**
	 *  Adding NodeId AVP of type UTF8String to the message.
	 */
//	public NodeIdAvp addNodeId(java.lang.String value) throws GyResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside addNodeId()");
//			}
//			return new NodeIdAvp(stackObj.addNodeId(value));
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in addNodeId",e);
//		}
//	}

	/**
	 *  Adding PDNConnectionID AVP of type Unsigned32 to the message.
	 */
//	public PDNConnectionIDAvp addPDNConnectionID(long value) throws GyResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside addPDNConnectionID()");
//			}
//			return new PDNConnectionIDAvp(stackObj.addPDNConnectionID(value));
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in addPDNConnectionID",e);
//		}
//	}

	/**
	 *  Adding PDPAddress AVP of type Address to the message.
	 */
	public PDPAddressAvp addPDPAddress(byte[] value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addPDPAddress()");
			}
			return new PDPAddressAvp(stackObj.addPDPAddress(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addPDPAddress",e);
		}
	}

	/**
	 *  Adding PDPAddress AVP of type Address to the message.
	 */
	public PDPAddressAvp addPDPAddress(java.net.InetAddress value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addPDPAddress()");
			}
			return new PDPAddressAvp(stackObj.addPDPAddress(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addPDPAddress",e);
		}
	}

	/**
	 *  Adding PDPContextType AVP of type Enumerated to the message.
	 */
	public PDPContextTypeAvp addPDPContextType(PDPContextTypeEnum value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addPDPContextType()");
			}
			return new PDPContextTypeAvp(stackObj.addPDPContextType(PDPContextTypeEnum.getStackObj(value)));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addPDPContextType",e);
		}
	}

	/**
	 *  Adding ServingNodeType AVP of type Enumerated to the message.
	 */
//	public ServingNodeTypeAvp addServingNodeType(ServingNodeTypeEnum value) throws GyResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside addServingNodeType()");
//			}
//			return new ServingNodeTypeAvp(stackObj.addServingNodeType(ServingNodeTypeEnum.getStackObj(value)));
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in addServingNodeType",e);
//		}
//	}

	/**
	 *  Adding SGSNAddress AVP of type Address to the message.
	 */
	public SGSNAddressAvp addSGSNAddress(byte[] value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addSGSNAddress()");
			}
			return new SGSNAddressAvp(stackObj.addSGSNAddress(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addSGSNAddress",e);
		}
	}

	/**
	 *  Adding SGSNAddress AVP of type Address to the message.
	 */
	public SGSNAddressAvp addSGSNAddress(java.net.InetAddress value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addSGSNAddress()");
			}
			return new SGSNAddressAvp(stackObj.addSGSNAddress(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addSGSNAddress",e);
		}
	}

	/**
	 *  Adding SGWChange AVP of type Enumerated to the message.
	 */
//	public SGWChangeAvp addSGWChange(SGWChangeEnum value) throws GyResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside addSGWChange()");
//			}
//			return new SGWChangeAvp(stackObj.addSGWChange(SGWChangeEnum.getStackObj(value)));
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in addSGWChange",e);
//		}
//	}

	/**
	 *  Adding StartTime AVP of type Time to the message.
	 */
//	public StartTimeAvp addStartTime(java.util.Date value) throws GyResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside addStartTime()");
//			}
//			return new StartTimeAvp(stackObj.addStartTime(value));
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in addStartTime",e);
//		}
//	}

	/**
	 *  Adding StopTime AVP of type Time to the message.
	 */
//	public StopTimeAvp addStopTime(java.util.Date value) throws GyResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside addStopTime()");
//			}
//			return new StopTimeAvp(stackObj.addStopTime(value));
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in addStopTime",e);
//		}
//	}

	/**
	 *  Retrieving a single UTF8String value from 3GPP2BSID AVPs.
	 */
//	public java.lang.String get3GPP2BSID( ) throws GyResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside get3GPP2BSID()");
//			}
//			return stackObj.get3GPP2BSID();
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in get3GPP2BSID",e);
//		}
//	}

	/**
	 *  Retrieving a single UTF8String value from 3GPPChargingCharacteristics AVPs.
	 */
	public java.lang.String get3GPPChargingCharacteristics( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside get3GPPChargingCharacteristics()");
			}
			return stackObj.get3GPPChargingCharacteristics();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in get3GPPChargingCharacteristics",e);
		}
	}

	/**
	 *  Retrieving a single OctetString value from 3GPPChargingId AVPs.
	 */
	public java.lang.String get3GPPChargingId( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside get3GPPChargingId()");
			}
			return stackObj.get3GPPChargingId();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in get3GPPChargingId",e);
		}
	}

	/**
	 *  Retrieving a single UTF8String value from 3GPPGGSNMCCMNC AVPs.
	 */
	public java.lang.String get3GPPGGSNMCCMNC( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside get3GPPGGSNMCCMNC()");
			}
			return stackObj.get3GPPGGSNMCCMNC();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in get3GPPGGSNMCCMNC",e);
		}
	}

	/**
	 *  Retrieving a single UTF8String value from 3GPPIMSIMCCMNC AVPs.
	 */
	public java.lang.String get3GPPIMSIMCCMNC( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside get3GPPIMSIMCCMNC()");
			}
			return stackObj.get3GPPIMSIMCCMNC();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in get3GPPIMSIMCCMNC",e);
		}
	}

	/**
	 *  Retrieving a single OctetString value from 3GPPMSTimeZone AVPs.
	 */
	public java.lang.String get3GPPMSTimeZone( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside get3GPPMSTimeZone()");
			}
			return stackObj.get3GPPMSTimeZone();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in get3GPPMSTimeZone",e);
		}
	}

	/**
	 *  Retrieving a single UTF8String value from 3GPPNSAPI AVPs.
	 */
	public java.lang.String get3GPPNSAPI( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside get3GPPNSAPI()");
			}
			return stackObj.get3GPPNSAPI();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in get3GPPNSAPI",e);
		}
	}

	/**
	 *  Retrieving a single Enumerated value from 3GPPPDPType AVPs.
	 */
	public int get3GPPPDPType( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside get3GPPPDPType()");
			}
			return stackObj.get3GPPPDPType();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in get3GPPPDPType",e);
		}
	}

	/**
	 *  Retrieving a single OctetString value from 3GPPRATType AVPs.
	 */
	public java.lang.String get3GPPRATType( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside get3GPPRATType()");
			}
			return stackObj.get3GPPRATType();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in get3GPPRATType",e);
		}
	}

	/**
	 *  Retrieving a single UTF8String value from 3GPPSelectionMode AVPs.
	 */
	public java.lang.String get3GPPSelectionMode( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside get3GPPSelectionMode()");
			}
			return stackObj.get3GPPSelectionMode();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in get3GPPSelectionMode",e);
		}
	}

	/**
	 *  Retrieving a single UTF8String value from 3GPPSessionStopIndicator AVPs.
	 */
	public java.lang.String get3GPPSessionStopIndicator( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside get3GPPSessionStopIndicator()");
			}
			return stackObj.get3GPPSessionStopIndicator();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in get3GPPSessionStopIndicator",e);
		}
	}

	/**
	 *  Retrieving a single UTF8String value from 3GPPSGSNMCCMNC AVPs.
	 */
	public java.lang.String get3GPPSGSNMCCMNC( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside get3GPPSGSNMCCMNC()");
			}
			return stackObj.get3GPPSGSNMCCMNC();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in get3GPPSGSNMCCMNC",e);
		}
	}

	/**
	 *  Retrieving a single OctetString value from 3GPPUserLocationInfo AVPs.
	 */
	public java.lang.String get3GPPUserLocationInfo( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside get3GPPUserLocationInfo()");
			}
			return stackObj.get3GPPUserLocationInfo();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in get3GPPUserLocationInfo",e);
		}
	}

	/**
	 *  Retrieving a single UTF8String value from CalledStationId AVPs.
	 */
	public java.lang.String getCalledStationId( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getCalledStationId()");
			}
			return stackObj.getCalledStationId();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getCalledStationId",e);
		}
	}

	/**
	 *  Retrieving a single Address value from CGAddress AVPs.
	 */
	public java.net.InetAddress getCGAddress( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getCGAddress()");
			}
			return stackObj.getCGAddress();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getCGAddress",e);
		}
	}

	/**
	 *  Retrieving a single Integer32 value from ChangeCondition AVPs.
	 */
//	public int getChangeCondition( ) throws GyResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getChangeCondition()");
//			}
//			return stackObj.getChangeCondition();
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in getChangeCondition",e);
//		}
//	}

	/**
	 *  Retrieving a single Enumerated value from ChargingCharacteristicsSelectionMode AVPs.
	 */
//	public int getChargingCharacteristicsSelectionMode( ) throws GyResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getChargingCharacteristicsSelectionMode()");
//			}
//			return stackObj.getChargingCharacteristicsSelectionMode();
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in getChargingCharacteristicsSelectionMode",e);
//		}
//	}

	/**
	 *  Retrieving multiple UTF8String values from ChargingRuleBaseName AVPs.
	 */
	public java.lang.String[] getChargingRuleBaseNames( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getChargingRuleBaseNames()");
			}
			return stackObj.getChargingRuleBaseNames();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getChargingRuleBaseNames",e);
		}
	}

	/**
	 *  Retrieving a single Integer32 value from Diagnostics AVPs.
	 */
//	public int getDiagnostics( ) throws GyResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getDiagnostics()");
//			}
//			return stackObj.getDiagnostics();
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in getDiagnostics",e);
//		}
//	}
//
//	/**
//	 *  Retrieving a single Enumerated value from DynamicAddressFlag AVPs.
//	 */
//	public int getDynamicAddressFlag( ) throws GyResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getDynamicAddressFlag()");
//			}
//			return stackObj.getDynamicAddressFlag();
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in getDynamicAddressFlag",e);
//		}
//	}

	/**
	 *  This method retunrs the enum corrosponding to PDPType3GPPAvp.
	 */
	public PDPType3GPPEnum getEnum3GPPPDPType( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getEnum3GPPPDPType()");
			}
			return PDPType3GPPEnum.getContainerObj(stackObj.getEnum3GPPPDPType());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getEnum3GPPPDPType",e);
		}
	}

	/**
	 *  This method retunrs the enum corrosponding to ChargingCharacteristicsSelectionModeAvp.
	 */
//	public ChargingCharacteristicsSelectionModeEnum getEnumChargingCharacteristicsSelectionMode( ) throws GyResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getEnumChargingCharacteristicsSelectionMode()");
//			}
//			return ChargingCharacteristicsSelectionModeEnum.getContainerObj(stackObj.getEnumChargingCharacteristicsSelectionMode());
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in getEnumChargingCharacteristicsSelectionMode",e);
//		}
//	}
//
//	/**
//	 *  This method retunrs the enum corrosponding to DynamicAddressFlagAvp.
//	 */
//	public DynamicAddressFlagEnum getEnumDynamicAddressFlag( ) throws GyResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getEnumDynamicAddressFlag()");
//			}
//			return DynamicAddressFlagEnum.getContainerObj(stackObj.getEnumDynamicAddressFlag());
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in getEnumDynamicAddressFlag",e);
//		}
//	}
//
//	/**
//	 *  This method retunrs the enum corrosponding to IMSIUnauthenticatedFlagAvp.
//	 */
//	public IMSIUnauthenticatedFlagEnum getEnumIMSIUnauthenticatedFlag( ) throws GyResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getEnumIMSIUnauthenticatedFlag()");
//			}
//			return IMSIUnauthenticatedFlagEnum.getContainerObj(stackObj.getEnumIMSIUnauthenticatedFlag());
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in getEnumIMSIUnauthenticatedFlag",e);
//		}
//	}

	/**
	 *  This method retunrs the enum corrosponding to PDPContextTypeAvp.
	 */
	public PDPContextTypeEnum getEnumPDPContextType( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getEnumPDPContextType()");
			}
			return PDPContextTypeEnum.getContainerObj(stackObj.getEnumPDPContextType());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getEnumPDPContextType",e);
		}
	}

	/**
	 *  This method retunrs the enum corrosponding to ServingNodeTypeAvp.
	 */
//	public ServingNodeTypeEnum getEnumServingNodeType( ) throws GyResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getEnumServingNodeType()");
//			}
//			return ServingNodeTypeEnum.getContainerObj(stackObj.getEnumServingNodeType());
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in getEnumServingNodeType",e);
//		}
//	}
//
//	/**
//	 *  This method retunrs the enum corrosponding to SGWChangeAvp.
//	 */
//	public SGWChangeEnum getEnumSGWChange( ) throws GyResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getEnumSGWChange()");
//			}
//			return SGWChangeEnum.getContainerObj(stackObj.getEnumSGWChange());
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in getEnumSGWChange",e);
//		}
//	}

	/**
	 *  Retrieving a single Address value from GGSNAddress AVPs.
	 */
	public java.net.InetAddress getGGSNAddress( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGGSNAddress()");
			}
			return stackObj.getGGSNAddress();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGGSNAddress",e);
		}
	}

	/**
	 *  Retrieving a single Grouped value from OfflineCharging AVPs.
	 */
	public OfflineChargingAvp getGroupedOfflineCharging( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedOfflineCharging()");
			}
			return new OfflineChargingAvp(stackObj.getGroupedOfflineCharging());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGroupedOfflineCharging",e);
		}
	}

	/**
	 *  Retrieving a single Grouped value from PSFurnishChargingInformation AVPs.
	 */
	public PSFurnishChargingInformationAvp getGroupedPSFurnishChargingInformation( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedPSFurnishChargingInformation()");
			}
			return new PSFurnishChargingInformationAvp(stackObj.getGroupedPSFurnishChargingInformation());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGroupedPSFurnishChargingInformation",e);
		}
	}

	/**
	 *  Retrieving a single Grouped value from QoSInformation AVPs.
	 */
//	public QoSInformationAvp getGroupedQoSInformation( ) throws GyResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getGroupedQoSInformation()");
//			}
//			return new QoSInformationAvp(stackObj.getGroupedQoSInformation());
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in getGroupedQoSInformation",e);
//		}
//	}
//
//	/**
//	 *  Retrieving multiple Grouped values from ServiceDataContainer AVPs.
//	 */
//	public ServiceDataContainerAvp[] getGroupedServiceDataContainers( ) throws GyResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getGroupedServiceDataContainers()");
//			}
//			AvpServiceDataContainer[] stackAv= stackObj.getGroupedServiceDataContainers();
//			ServiceDataContainerAvp[] contAvp= new ServiceDataContainerAvp[stackAv.length];
//			for(int i=0;i<stackAv.length;i++){
//				contAvp[i]=new ServiceDataContainerAvp(stackAv[i]);
//			}
//			return contAvp;
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in getGroupedServiceDataContainers",e);
//		}
//	}
//
//	/**
//	 *  Retrieving a single Grouped value from TerminalInformation AVPs.
//	 */
//	public TerminalInformationAvp getGroupedTerminalInformation( ) throws GyResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getGroupedTerminalInformation()");
//			}
//			return new TerminalInformationAvp(stackObj.getGroupedTerminalInformation());
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in getGroupedTerminalInformation",e);
//		}
//	}
//
//	/**
//	 *  Retrieving multiple Grouped values from TrafficDataVolumes AVPs.
//	 */
//	public TrafficDataVolumesAvp[] getGroupedTrafficDataVolumess( ) throws GyResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getGroupedTrafficDataVolumess()");
//			}
//			AvpTrafficDataVolumes[] stackAv= stackObj.getGroupedTrafficDataVolumess();
//			TrafficDataVolumesAvp[] contAvp= new TrafficDataVolumesAvp[stackAv.length];
//			for(int i=0;i<stackAv.length;i++){
//				contAvp[i]=new TrafficDataVolumesAvp(stackAv[i]);
//			}
//			return contAvp;
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in getGroupedTrafficDataVolumess",e);
//		}
//	}
//
//	/**
//	 *  Retrieving a single Grouped value from UserCSGInformation AVPs.
//	 */
//	public UserCSGInformationAvp getGroupedUserCSGInformation( ) throws GyResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getGroupedUserCSGInformation()");
//			}
//			return new UserCSGInformationAvp(stackObj.getGroupedUserCSGInformation());
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in getGroupedUserCSGInformation",e);
//		}
//	}
//
//	/**
//	 *  Retrieving a single Grouped value from UserEquipmentInfo AVPs.
//	 */
//	public UserEquipmentInfoAvp getGroupedUserEquipmentInfo( ) throws GyResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getGroupedUserEquipmentInfo()");
//			}
//			return new UserEquipmentInfoAvp(stackObj.getGroupedUserEquipmentInfo());
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in getGroupedUserEquipmentInfo",e);
//		}
//	}
//
//	/**
//	 *  Retrieving a single Enumerated value from IMSIUnauthenticatedFlag AVPs.
//	 */
//	public int getIMSIUnauthenticatedFlag( ) throws GyResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getIMSIUnauthenticatedFlag()");
//			}
//			return stackObj.getIMSIUnauthenticatedFlag();
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in getIMSIUnauthenticatedFlag",e);
//		}
//	}
//
//	/**
//	 *  Retrieving a single UTF8String value from NodeId AVPs.
//	 */
//	public java.lang.String getNodeId( ) throws GyResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getNodeId()");
//			}
//			return stackObj.getNodeId();
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in getNodeId",e);
//		}
//	}
//
//	/**
//	 *  Retrieving a single Unsigned32 value from PDNConnectionID AVPs.
//	 */
//	public long getPDNConnectionID( ) throws GyResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getPDNConnectionID()");
//			}
//			return stackObj.getPDNConnectionID();
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in getPDNConnectionID",e);
//		}
//	}

	/**
	 *  Retrieving a single Address value from PDPAddress AVPs.
	 */
	public java.net.InetAddress getPDPAddress( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getPDPAddress()");
			}
			return stackObj.getPDPAddress();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getPDPAddress",e);
		}
	}

	/**
	 *  Retrieving a single Enumerated value from PDPContextType AVPs.
	 */
	public int getPDPContextType( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getPDPContextType()");
			}
			return stackObj.getPDPContextType();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getPDPContextType",e);
		}
	}

	/**
	 *  Retrieving a single OctetString value from 3GPPChargingId AVPs.
	 */
	public byte[] getRaw3GPPChargingId( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getRaw3GPPChargingId()");
			}
			return stackObj.getRaw3GPPChargingId();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getRaw3GPPChargingId",e);
		}
	}

	/**
	 *  Retrieving a single OctetString value from 3GPPMSTimeZone AVPs.
	 */
	public byte[] getRaw3GPPMSTimeZone( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getRaw3GPPMSTimeZone()");
			}
			return stackObj.getRaw3GPPMSTimeZone();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getRaw3GPPMSTimeZone",e);
		}
	}

	/**
	 *  Retrieving a single OctetString value from 3GPPRATType AVPs.
	 */
	public byte[] getRaw3GPPRATType( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getRaw3GPPRATType()");
			}
			return stackObj.getRaw3GPPRATType();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getRaw3GPPRATType",e);
		}
	}

	/**
	 *  Retrieving a single OctetString value from 3GPPUserLocationInfo AVPs.
	 */
	public byte[] getRaw3GPPUserLocationInfo( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getRaw3GPPUserLocationInfo()");
			}
			return stackObj.getRaw3GPPUserLocationInfo();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getRaw3GPPUserLocationInfo",e);
		}
	}

	/**
	 *  Retrieving a single Address value from CGAddress AVPs.
	 */
	public byte[] getRawCGAddress( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getRawCGAddress()");
			}
			return stackObj.getRawCGAddress();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getRawCGAddress",e);
		}
	}

	/**
	 *  Retrieving a single Address value from GGSNAddress AVPs.
	 */
	public byte[] getRawGGSNAddress( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getRawGGSNAddress()");
			}
			return stackObj.getRawGGSNAddress();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getRawGGSNAddress",e);
		}
	}

	/**
	 *  Retrieving a single Address value from PDPAddress AVPs.
	 */
	public byte[] getRawPDPAddress( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getRawPDPAddress()");
			}
			return stackObj.getRawPDPAddress();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getRawPDPAddress",e);
		}
	}

	/**
	 *  Retrieving a single Address value from SGSNAddress AVPs.
	 */
	public byte[] getRawSGSNAddress( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getRawSGSNAddress()");
			}
			return stackObj.getRawSGSNAddress();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getRawSGSNAddress",e);
		}
	}

	/**
	 *  Retrieving a single Enumerated value from ServingNodeType AVPs.
	 */
//	public int getServingNodeType( ) throws GyResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getServingNodeType()");
//			}
//			return stackObj.getServingNodeType();
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in getServingNodeType",e);
//		}
//	}

	/**
	 *  Retrieving a single Address value from SGSNAddress AVPs.
	 */
	public java.net.InetAddress getSGSNAddress( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getSGSNAddress()");
			}
			return stackObj.getSGSNAddress();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getSGSNAddress",e);
		}
	}

	/**
	 *  Retrieving a single Enumerated value from SGWChange AVPs.
	 */
//	public int getSGWChange( ) throws GyResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getSGWChange()");
//			}
//			return stackObj.getSGWChange();
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in getSGWChange",e);
//		}
//	}
//
//	/**
//	 *  Retrieving a single Time value from StartTime AVPs.
//	 */
//	public java.util.Date getStartTime( ) throws GyResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getStartTime()");
//			}
//			return stackObj.getStartTime();
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in getStartTime",e);
//		}
//	}
//
//	/**
//	 *  Retrieving a single Time value from StopTime AVPs.
//	 */
//	public java.util.Date getStopTime( ) throws GyResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getStopTime()");
//			}
//			return stackObj.getStopTime();
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in getStopTime",e);
//		}
//	}

}