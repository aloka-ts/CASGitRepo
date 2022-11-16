package com.baypackets.ase.ra.diameter.gy.avp;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.baypackets.ase.ra.diameter.gy.GyResourceException;
import com.baypackets.ase.ra.diameter.gy.enums.EnvelopeReportingEnum;
import com.baypackets.ase.ra.diameter.gy.enums.ReportingReasonEnum;
import com.baypackets.ase.ra.diameter.gy.enums.TariffChangeUsageEnum;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.gy.generated.avp.AvpMultipleServicesCreditControl;
import com.traffix.openblox.diameter.gy.generated.avp.AvpAFCorrelationInformation;
import com.traffix.openblox.diameter.gy.generated.avp.AvpEnvelope;
import com.traffix.openblox.diameter.gy.generated.avp.AvpGSUPoolReference;
import com.traffix.openblox.diameter.gy.generated.avp.AvpServiceSpecificInfo;
import com.traffix.openblox.diameter.gy.generated.avp.AvpUsedServiceUnit;

public class MultipleServicesCreditControlAvp extends AvpDiameterGrouped {

	private static Logger logger = Logger.getLogger(MultipleServicesCreditControlAvp.class.getName());
	public static final long vendorId = 0L;

	private AvpMultipleServicesCreditControl stackObj;

	public MultipleServicesCreditControlAvp(AvpMultipleServicesCreditControl stkObj){
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
	 *  Adding EnvelopeReporting AVP of type Enumerated to the message.
	 */
	public EnvelopeReportingAvp addEnvelopeReporting(EnvelopeReportingEnum value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addEnvelopeReporting()");
			}
			return new EnvelopeReportingAvp(stackObj.addEnvelopeReporting(EnvelopeReportingEnum.getStackObj(value)));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addEnvelopeReporting",e);
		}
	}

	/**
	 *  Adding AFCorrelationInformation AVP of type Grouped to the message.
	 */
	public AFCorrelationInformationAvp addGroupedAFCorrelationInformation() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedAFCorrelationInformation()");
			}
			return new AFCorrelationInformationAvp(stackObj.addGroupedAFCorrelationInformation());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGroupedAFCorrelationInformation",e);
		}
	}

	/**
	 *  Adding Envelope AVP of type Grouped to the message.
	 */
	public EnvelopeAvp addGroupedEnvelope() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedEnvelope()");
			}
			return new EnvelopeAvp(stackObj.addGroupedEnvelope());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGroupedEnvelope",e);
		}
	}

	/**
	 *  Adding FinalUnitIndication AVP of type Grouped to the message.
	 */
	public FinalUnitIndicationAvp addGroupedFinalUnitIndication() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedFinalUnitIndication()");
			}
			return new FinalUnitIndicationAvp(stackObj.addGroupedFinalUnitIndication());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGroupedFinalUnitIndication",e);
		}
	}

	/**
	 *  Adding GrantedServiceUnit AVP of type Grouped to the message.
	 */
	public GrantedServiceUnitAvp addGroupedGrantedServiceUnit() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedGrantedServiceUnit()");
			}
			return new GrantedServiceUnitAvp(stackObj.addGroupedGrantedServiceUnit());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGroupedGrantedServiceUnit",e);
		}
	}

	/**
	 *  Adding GSUPoolReference AVP of type Grouped to the message.
	 */
	public GSUPoolReferenceAvp addGroupedGSUPoolReference() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedGSUPoolReference()");
			}
			return new GSUPoolReferenceAvp(stackObj.addGroupedGSUPoolReference());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGroupedGSUPoolReference",e);
		}
	}

	/**
	 *  Adding PSFurnishChargingInformation AVP of type Grouped to the message.
	 */
	public PSFurnishChargingInformationAvp addGroupedPSFurnishChargingInformation() throws GyResourceException { 

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
//	public QoSInformationAvp addGroupedQoSInformation() throws GyResourceException { 
//
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
	 *  Adding RequestedServiceUnit AVP of type Grouped to the message.
	 */
	public RequestedServiceUnitAvp addGroupedRequestedServiceUnit() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedRequestedServiceUnit()");
			}
			return new RequestedServiceUnitAvp(stackObj.addGroupedRequestedServiceUnit());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGroupedRequestedServiceUnit",e);
		}
	}

	/**
	 *  Adding ServiceSpecificInfo AVP of type Grouped to the message.
	 */
	public ServiceSpecificInfoAvp addGroupedServiceSpecificInfo() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedServiceSpecificInfo()");
			}
			return new ServiceSpecificInfoAvp(stackObj.addGroupedServiceSpecificInfo());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGroupedServiceSpecificInfo",e);
		}
	}

	/**
	 *  Adding TimeQuotaMechanism AVP of type Grouped to the message.
	 */
	public TimeQuotaMechanismAvp addGroupedTimeQuotaMechanism() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedTimeQuotaMechanism()");
			}
			return new TimeQuotaMechanismAvp(stackObj.addGroupedTimeQuotaMechanism());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGroupedTimeQuotaMechanism",e);
		}
	}

	/**
	 *  Adding Trigger AVP of type Grouped to the message.
	 */
	public TriggerAvp addGroupedTrigger() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedTrigger()");
			}
			return new TriggerAvp(stackObj.addGroupedTrigger());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGroupedTrigger",e);
		}
	}

	/**
	 *  Adding UsedServiceUnit AVP of type Grouped to the message.
	 */
	public UsedServiceUnitAvp addGroupedUsedServiceUnit() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedUsedServiceUnit()");
			}
			return new UsedServiceUnitAvp(stackObj.addGroupedUsedServiceUnit());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGroupedUsedServiceUnit",e);
		}
	}

	/**
	 *  Adding QuotaConsumptionTime AVP of type Unsigned32 to the message.
	 */
	public QuotaConsumptionTimeAvp addQuotaConsumptionTime(long value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addQuotaConsumptionTime()");
			}
			return new QuotaConsumptionTimeAvp(stackObj.addQuotaConsumptionTime(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addQuotaConsumptionTime",e);
		}
	}

	/**
	 *  Adding QuotaHoldingTime AVP of type Unsigned32 to the message.
	 */
	public QuotaHoldingTimeAvp addQuotaHoldingTime(long value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addQuotaHoldingTime()");
			}
			return new QuotaHoldingTimeAvp(stackObj.addQuotaHoldingTime(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addQuotaHoldingTime",e);
		}
	}

	/**
	 *  Adding RatingGroup AVP of type Unsigned32 to the message.
	 */
	public RatingGroupAvp addRatingGroup(long value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addRatingGroup()");
			}
			return new RatingGroupAvp(stackObj.addRatingGroup(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addRatingGroup",e);
		}
	}

	/**
	 *  Adding RefundInformation AVP of type OctetString to the message.
	 */
	public RefundInformationAvp addRefundInformation(byte[] value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addRefundInformation()");
			}
			return new RefundInformationAvp(stackObj.addRefundInformation(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addRefundInformation",e);
		}
	}

	/**
	 *  Adding RefundInformation AVP of type OctetString to the message.
	 */
	public RefundInformationAvp addRefundInformation(java.lang.String value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addRefundInformation()");
			}
			return new RefundInformationAvp(stackObj.addRefundInformation(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addRefundInformation",e);
		}
	}

	/**
	 *  Adding ReportingReason AVP of type Enumerated to the message.
	 */
	public ReportingReasonAvp addReportingReason(ReportingReasonEnum value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addReportingReason()");
			}
			return new ReportingReasonAvp(stackObj.addReportingReason(ReportingReasonEnum.getStackObj(value)));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addReportingReason",e);
		}
	}

	/**
	 *  Adding ResultCode AVP of type Unsigned32 to the message.
	 */
	public ResultCodeAvp addResultCode(long value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addResultCode()");
			}
			return new ResultCodeAvp(stackObj.addResultCode(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addResultCode",e);
		}
	}

	/**
	 *  Adding ServiceIdentifier AVP of type Unsigned32 to the message.
	 */
	public ServiceIdentifierAvp addServiceIdentifier(long value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addServiceIdentifier()");
			}
			return new ServiceIdentifierAvp(stackObj.addServiceIdentifier(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addServiceIdentifier",e);
		}
	}

	/**
	 *  Adding TariffChangeUsage AVP of type Enumerated to the message.
	 */
//	public TariffChangeUsageAvp addTariffChangeUsage(TariffChangeUsageEnum value) throws GyResourceException { 
//
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside addTariffChangeUsage()");
//			}
//			return new TariffChangeUsageAvp(stackObj.addTariffChangeUsage(TariffChangeUsageEnum.getStackObj(value)));
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in addTariffChangeUsage",e);
//		}
//	}

	/**
	 *  Adding TimeQuotaThreshold AVP of type Unsigned32 to the message.
	 */
	public TimeQuotaThresholdAvp addTimeQuotaThreshold(long value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addTimeQuotaThreshold()");
			}
			return new TimeQuotaThresholdAvp(stackObj.addTimeQuotaThreshold(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addTimeQuotaThreshold",e);
		}
	}

	/**
	 *  Adding UnitQuotaThreshold AVP of type Unsigned32 to the message.
	 */
	public UnitQuotaThresholdAvp addUnitQuotaThreshold(long value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addUnitQuotaThreshold()");
			}
			return new UnitQuotaThresholdAvp(stackObj.addUnitQuotaThreshold(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addUnitQuotaThreshold",e);
		}
	}

	/**
	 *  Adding ValidityTime AVP of type Unsigned32 to the message.
	 */
	public ValidityTimeAvp addValidityTime(long value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addValidityTime()");
			}
			return new ValidityTimeAvp(stackObj.addValidityTime(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addValidityTime",e);
		}
	}

	/**
	 *  Adding VolumeQuotaThreshold AVP of type Unsigned32 to the message.
	 */
	public VolumeQuotaThresholdAvp addVolumeQuotaThreshold(long value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addVolumeQuotaThreshold()");
			}
			return new VolumeQuotaThresholdAvp(stackObj.addVolumeQuotaThreshold(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addVolumeQuotaThreshold",e);
		}
	}

	/**
	 *  Retrieves Enum of type EnvelopeReporting.
	 */
	public EnvelopeReportingEnum getEnumEnvelopeReporting() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getEnumEnvelopeReporting()");
			}
			return EnvelopeReportingEnum.getContainerObj(stackObj.getEnumEnvelopeReporting());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getEnumEnvelopeReporting",e);
		}
	}

	/**
	 *  Retrieves Enum of TariffChangeUsage type.
	 */
//	public TariffChangeUsageEnum getEnumTariffChangeUsage() throws GyResourceException { 
//
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getEnumTariffChangeUsage()");
//			}
//			return TariffChangeUsageEnum.getContainerObj(stackObj.getEnumTariffChangeUsage());
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in getEnumTariffChangeUsage",e);
//		}
//	}

	/**
	 *  Retrieving a single Enumerated value from EnvelopeReporting AVPs.
	 */
	public int getEnvelopeReporting() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getEnvelopeReporting()");
			}
			return stackObj.getEnvelopeReporting();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getEnvelopeReporting",e);
		}
	}

	/**
	 *  Retrieving multiple Grouped values from AFCorrelationInformation AVPs.
	 */
	public AFCorrelationInformationAvp[] getGroupedAFCorrelationInformations() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedAFCorrelationInformations()");
			}
			AvpAFCorrelationInformation[] stackAv= stackObj.getGroupedAFCorrelationInformations();
			AFCorrelationInformationAvp[] contAvp= new AFCorrelationInformationAvp[stackAv.length];
			for(int i=0;i<stackAv.length;i++){
				contAvp[i]=new AFCorrelationInformationAvp(stackAv[i]);
			}
			return contAvp;
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGroupedAFCorrelationInformations",e);
		}
	}

	/**
	 *  Retrieving multiple Grouped values from Envelope AVPs.
	 */
	public EnvelopeAvp[] getGroupedEnvelopes() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedEnvelopes()");
			}
			AvpEnvelope[] stackAv= stackObj.getGroupedEnvelopes();
			EnvelopeAvp[] contAvp= new EnvelopeAvp[stackAv.length];
			for(int i=0;i<stackAv.length;i++){
				contAvp[i]=new EnvelopeAvp(stackAv[i]);
			}
			return contAvp;
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGroupedEnvelopes",e);
		}
	}

	/**
	 *  Retrieving a single Grouped value from FinalUnitIndication AVPs.
	 */
	public FinalUnitIndicationAvp getGroupedFinalUnitIndication() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedFinalUnitIndication()");
			}
			return new FinalUnitIndicationAvp(stackObj.getGroupedFinalUnitIndication());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGroupedFinalUnitIndication",e);
		}
	}

	/**
	 *  Retrieving a single Grouped value from GrantedServiceUnit AVPs.
	 */
	public GrantedServiceUnitAvp getGroupedGrantedServiceUnit() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedGrantedServiceUnit()");
			}
			return new GrantedServiceUnitAvp(stackObj.getGroupedGrantedServiceUnit());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGroupedGrantedServiceUnit",e);
		}
	}

	/**
	 *  Retrieving multiple Grouped values from GSUPoolReference AVPs.
	 */
	public GSUPoolReferenceAvp[] getGroupedGSUPoolReferences() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedGSUPoolReferences()");
			}
			AvpGSUPoolReference[] stackAv= stackObj.getGroupedGSUPoolReferences();
			GSUPoolReferenceAvp[] contAvp= new GSUPoolReferenceAvp[stackAv.length];
			for(int i=0;i<stackAv.length;i++){
				contAvp[i]=new GSUPoolReferenceAvp(stackAv[i]);
			}
			return contAvp;
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGroupedGSUPoolReferences",e);
		}
	}

	/**
	 *  Retrieving a single Grouped value from PSFurnishChargingInformation AVPs.
	 */
	public PSFurnishChargingInformationAvp getGroupedPSFurnishChargingInformation() throws GyResourceException { 

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
//	public QoSInformationAvp getGroupedQoSInformation() throws GyResourceException { 
//
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getGroupedQoSInformation()");
//			}
//			return new QoSInformationAvp(stackObj.getGroupedQoSInformation());
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in getGroupedQoSInformation",e);
//		}
//	}

	/**
	 *  Retrieving a single Grouped value from RequestedServiceUnit AVPs.
	 */
	public RequestedServiceUnitAvp getGroupedRequestedServiceUnit() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedRequestedServiceUnit()");
			}
			return new RequestedServiceUnitAvp(stackObj.getGroupedRequestedServiceUnit());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGroupedRequestedServiceUnit",e);
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
	 *  Retrieving a single Grouped value from TimeQuotaMechanism AVPs.
	 */
	public TimeQuotaMechanismAvp getGroupedTimeQuotaMechanism() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedTimeQuotaMechanism()");
			}
			return new TimeQuotaMechanismAvp(stackObj.getGroupedTimeQuotaMechanism());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGroupedTimeQuotaMechanism",e);
		}
	}

	/**
	 *  Retrieving a single Grouped value from Trigger AVPs.
	 */
	public TriggerAvp getGroupedTrigger() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedTrigger()");
			}
			return new TriggerAvp(stackObj.getGroupedTrigger());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGroupedTrigger",e);
		}
	}

	/**
	 *  Retrieving multiple Grouped values from UsedServiceUnit AVPs.
	 */
	public UsedServiceUnitAvp[] getGroupedUsedServiceUnits() throws GyResourceException { 

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

	/**
	 *  Retrieving a single Unsigned32 value from QuotaConsumptionTime AVPs.
	 */
	public long getQuotaConsumptionTime() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getQuotaConsumptionTime()");
			}
			return stackObj.getQuotaConsumptionTime();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getQuotaConsumptionTime",e);
		}
	}

	/**
	 *  Retrieving a single Unsigned32 value from QuotaHoldingTime AVPs.
	 */
	public long getQuotaHoldingTime() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getQuotaHoldingTime()");
			}
			return stackObj.getQuotaHoldingTime();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getQuotaHoldingTime",e);
		}
	}

	/**
	 *  Retrieving a single Unsigned32 value from RatingGroup AVPs.
	 */
	public long getRatingGroup() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getRatingGroup()");
			}
			return stackObj.getRatingGroup();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getRatingGroup",e);
		}
	}

	/**
	 *  Retrieving a single OctetString value from RefundInformation AVPs.
	 */
	public byte[] getRawRefundInformation() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getRawRefundInformation()");
			}
			return stackObj.getRawRefundInformation();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getRawRefundInformation",e);
		}
	}

	/**
	 *  Retrieving a single OctetString value from RefundInformation AVPs.
	 */
	public java.lang.String getRefundInformation() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getRefundInformation()");
			}
			return stackObj.getRefundInformation();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getRefundInformation",e);
		}
	}

	/**
	 *  Retrieving multiple Enumerated values from ReportingReason AVPs.
	 */
	public int[] getReportingReasons() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getReportingReasons()");
			}
			return stackObj.getReportingReasons();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getReportingReasons",e);
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
	 *  Retrieving multiple Unsigned32 values from ServiceIdentifier AVPs.
	 */
	public long[] getServiceIdentifiers() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getServiceIdentifiers()");
			}
			return stackObj.getServiceIdentifiers();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getServiceIdentifiers",e);
		}
	}

	/**
	 *  Retrieving a single Enumerated value from TariffChangeUsage AVPs.
	 */
//	public int getTariffChangeUsage() throws GyResourceException { 
//
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getTariffChangeUsage()");
//			}
//			return stackObj.getTariffChangeUsage();
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in getTariffChangeUsage",e);
//		}
//	}

	/**
	 *  Retrieving a single Unsigned32 value from TimeQuotaThreshold AVPs.
	 */
	public long getTimeQuotaThreshold() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getTimeQuotaThreshold()");
			}
			return stackObj.getTimeQuotaThreshold();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getTimeQuotaThreshold",e);
		}
	}

	/**
	 *  Retrieving a single Unsigned32 value from UnitQuotaThreshold AVPs.
	 */
	public long getUnitQuotaThreshold() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getUnitQuotaThreshold()");
			}
			return stackObj.getUnitQuotaThreshold();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getUnitQuotaThreshold",e);
		}
	}

	/**
	 *  Retrieving a single Unsigned32 value from ValidityTime AVPs.
	 */
	public long getValidityTime() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getValidityTime()");
			}
			return stackObj.getValidityTime();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getValidityTime",e);
		}
	}

	/**
	 *  Retrieving a single Unsigned32 value from VolumeQuotaThreshold AVPs.
	 */
	public long getVolumeQuotaThreshold() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getVolumeQuotaThreshold()");
			}
			return stackObj.getVolumeQuotaThreshold();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getVolumeQuotaThreshold",e);
		}
	}

}