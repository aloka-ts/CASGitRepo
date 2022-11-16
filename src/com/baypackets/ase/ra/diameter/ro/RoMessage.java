package com.baypackets.ase.ra.diameter.ro;

import java.io.IOException;

import javax.servlet.sip.SipApplicationSession;

import org.apache.log4j.Logger;

import com.baypackets.ase.dispatcher.Destination;
import com.baypackets.ase.ra.diameter.ro.impl.RoResourceAdaptorFactory;
import com.baypackets.ase.ra.diameter.ro.impl.RoResourceAdaptorImpl;
import com.baypackets.ase.ra.diameter.ro.utils.Constants;
import com.baypackets.ase.resource.Message;
import com.baypackets.ase.resource.ResourceSession;
import com.baypackets.ase.spi.container.AbstractSasMessage;
import com.baypackets.ase.spi.container.SasMessageContext;
import com.baypackets.ase.spi.container.SasProtocolSession;
//import com.traffix.openblox.diameter.ro.generated.avp.*;

public class RoMessage extends AbstractSasMessage implements Message, Constants {

	private static Logger logger = Logger.getLogger(RoMessage.class);

	private int type;  
	private String method;
	private SasProtocolSession session;
	private Destination m_destination=  null;

	//protected ResourceContext context;

	public RoMessage() {
		super();
		logger.debug("Inside RoMessage() constructor ");
	}

	public RoMessage(int type) {
		this.type = type;
	}

	public String getMethod() {
		return this.method;
	}

	public String getProtocol() {
		return PROTOCOL;
	}

	public boolean isSecure() {
		return false;
	}

	public SasProtocolSession getProtocolSession() {
		return this.session;
	}

	public SasProtocolSession getProtocolSession(boolean create) {
		//if (create && this.context != null) {
		if (create && RoResourceAdaptorImpl.getResourceContext() != null) {
			try {
				//this.session = this.context.getSessionFactory().createSession();
				this.session = RoResourceAdaptorImpl.getResourceContext().getSessionFactory().createSession();
			} catch (Exception e) {
				logger.error("getProtocolSession(): " + e);
				this.session = null;
			}
		}
		return this.session;
	}

	public int getType() {
		return type;
	}

	public ResourceSession getSession() {
		return (ResourceSession)this.getProtocolSession();
	}

	public SipApplicationSession getApplicationSession() {
		SipApplicationSession appSession = null;
		if(this.getProtocolSession() != null){
			appSession = this.getProtocolSession().getApplicationSession();
		}
		return appSession;		
	}

	public void send() throws IOException {
		logger.debug("send() called.");
		SasMessageContext context = this.getMessageContext();
		if(context != null){
			context.sendMessage(this);
		} else {
			logger.info("Send to Sh resource adaptor directly.");
			try {
				RoResourceAdaptorFactory.getResourceAdaptor().sendMessage(this);
			} catch (Exception e) {
				logger.error("send(): " + e);
				throw new IOException(e.getMessage());
			}
		}
	}

	public void set(Object arg0) {
		// TODO Auto-generated method stub
		logger.info("set() is not supported.");

	}

	public Object get() {
		// TODO Auto-generated method stub
		logger.info("get() is not supported.");
		return null;
	}

	public void setProtocolSession(SasProtocolSession session) {
		this.session = session;		
	}
	/*	
	public ResourceContext getResourceContext() {
		return this.context;
	}

	public void setResourceContext(ResourceContext context) {
		this.context = context;
	}*/

	public void setDestination(Object destination)
	{
		if(m_destination==null)
			m_destination= new Destination();
		this.m_destination = (Destination)destination;
	}

	public Object getDestination()
	{
		return this.m_destination;
	}

	/**
	 * Sets the priority Message Flag for this message.
	 */
	public void setMessagePriority(boolean priority)        {
		priorityMsg = priority;
	}

	/**
	 * Returns the priority Message Flag for this message.
	 */
	public boolean getMessagePriority()     {
		return priorityMsg;
	}

//	public static AvpDiameter createContainerAvp(DiameterAVP stackAvp){
//		AvpDiameter containerObj=null;
//
//		if(stackAvp instanceof AvpAccessNetworkChargingIdentifierValue){
//			containerObj = new  AccessNetworkChargingIdentifierValueAvp((AvpAccessNetworkChargingIdentifierValue) stackAvp);
//		}else if(stackAvp instanceof AvpAccessNetworkInformation){
//			containerObj = new  AccessNetworkInformationAvp((AvpAccessNetworkInformation) stackAvp);
//		}else if(stackAvp instanceof AvpAccountExpiration){
//			containerObj = new  AccountExpirationAvp((AvpAccountExpiration) stackAvp);
//		}else if(stackAvp instanceof AvpAccountingInputOctets){
//			containerObj = new  AccountingInputOctetsAvp((AvpAccountingInputOctets) stackAvp);
//		}else if(stackAvp instanceof AvpAccountingInputPackets){
//			containerObj = new  AccountingInputPacketsAvp((AvpAccountingInputPackets) stackAvp);
//		}else if(stackAvp instanceof AvpAccountingOutputOctets){
//			containerObj = new  AccountingOutputOctetsAvp((AvpAccountingOutputOctets) stackAvp);
//		}else if(stackAvp instanceof AvpAccountingOutputPackets){
//			containerObj = new  AccountingOutputPacketsAvp((AvpAccountingOutputPackets) stackAvp);
//		}else if(stackAvp instanceof AvpAccountingRecordNumber){
//			containerObj = new  AccountingRecordNumberAvp((AvpAccountingRecordNumber) stackAvp);
//		}else if(stackAvp instanceof AvpAccountingRecordType){
//			containerObj = new  AccountingRecordTypeAvp((AvpAccountingRecordType) stackAvp);
//		}else if(stackAvp instanceof AvpAcctApplicationId){
//			containerObj = new  AcctApplicationIdAvp((AvpAcctApplicationId) stackAvp);
//		}else if(stackAvp instanceof AvpAcctInterimInterval){
//			containerObj = new  AcctInterimIntervalAvp((AvpAcctInterimInterval) stackAvp);
//		}else if(stackAvp instanceof AvpAccumulatedCost){
//			containerObj = new  AccumulatedCostAvp((AvpAccumulatedCost) stackAvp);
//		}else if(stackAvp instanceof AvpAdaptations){
//			containerObj = new  AdaptationsAvp((AvpAdaptations) stackAvp);
//		}else if(stackAvp instanceof AvpAdditionalContentInformation){
//			containerObj = new  AdditionalContentInformationAvp((AvpAdditionalContentInformation) stackAvp);
//		}else if(stackAvp instanceof AvpAdditionalTypeInformation){
//			containerObj = new  AdditionalTypeInformationAvp((AvpAdditionalTypeInformation) stackAvp);
//		}else if(stackAvp instanceof AvpAddressData){
//			containerObj = new  AddressDataAvp((AvpAddressData) stackAvp);
//		}else if(stackAvp instanceof AvpAddressDomain){
//			containerObj = new  AddressDomainAvp((AvpAddressDomain) stackAvp);
//		}else if(stackAvp instanceof AvpAddresseeType){
//			containerObj = new  AddresseeTypeAvp((AvpAddresseeType) stackAvp);
//		}else if(stackAvp instanceof AvpAddressType){
//			containerObj = new  AddressTypeAvp((AvpAddressType) stackAvp);
//		}else if(stackAvp instanceof AvpAFChargingIdentifier){
//			containerObj = new  AFChargingIdentifierAvp((AvpAFChargingIdentifier) stackAvp);
//		}else if(stackAvp instanceof AvpAFCorrelationInformation){
//			containerObj = new  AFCorrelationInformationAvp((AvpAFCorrelationInformation) stackAvp);
//		}else if(stackAvp instanceof AvpAllocationRetentionPriority){
//			containerObj = new  AllocationRetentionPriorityAvp((AvpAllocationRetentionPriority) stackAvp);
//		}else if(stackAvp instanceof AvpAlternateChargedPartyAddress){
//			containerObj = new  AlternateChargedPartyAddressAvp((AvpAlternateChargedPartyAddress) stackAvp);
//		}else if(stackAvp instanceof AvpAoCCostInformation){
//			containerObj = new  AoCCostInformationAvp((AvpAoCCostInformation) stackAvp);
//		}else if(stackAvp instanceof AvpAoCFormat){
//			containerObj = new  AoCFormatAvp((AvpAoCFormat) stackAvp);
//		}else if(stackAvp instanceof AvpAoCInformation){
//			containerObj = new  AoCInformationAvp((AvpAoCInformation) stackAvp);
//		}else if(stackAvp instanceof AvpAoCRequestType){
//			containerObj = new  AoCRequestTypeAvp((AvpAoCRequestType) stackAvp);
//		}else if(stackAvp instanceof AvpAoCService){
//			containerObj = new  AoCServiceAvp((AvpAoCService) stackAvp);
//		}else if(stackAvp instanceof AvpAoCServiceObligatoryType){
//			containerObj = new  AoCServiceObligatoryTypeAvp((AvpAoCServiceObligatoryType) stackAvp);
//		}else if(stackAvp instanceof AvpAoCServiceType){
//			containerObj = new  AoCServiceTypeAvp((AvpAoCServiceType) stackAvp);
//		}else if(stackAvp instanceof AvpAoCSubscriptionInformation){
//			containerObj = new  AoCSubscriptionInformationAvp((AvpAoCSubscriptionInformation) stackAvp);
//		}else if(stackAvp instanceof AvpAPNAggregateMaxBitrateDL){
//			containerObj = new  APNAggregateMaxBitrateDLAvp((AvpAPNAggregateMaxBitrateDL) stackAvp);
//		}else if(stackAvp instanceof AvpAPNAggregateMaxBitrateUL){
//			containerObj = new  APNAggregateMaxBitrateULAvp((AvpAPNAggregateMaxBitrateUL) stackAvp);
//		}else if(stackAvp instanceof AvpApplicationProvidedCalledPartyAddress){
//			containerObj = new  ApplicationProvidedCalledPartyAddressAvp((AvpApplicationProvidedCalledPartyAddress) stackAvp);
//		}else if(stackAvp instanceof AvpApplicationServer){
//			containerObj = new  ApplicationServerAvp((AvpApplicationServer) stackAvp);
//		}else if(stackAvp instanceof AvpApplicationServerInformation){
//			containerObj = new  ApplicationServerInformationAvp((AvpApplicationServerInformation) stackAvp);
//		}else if(stackAvp instanceof AvpApplicID){
//			containerObj = new  ApplicIDAvp((AvpApplicID) stackAvp);
//		}else if(stackAvp instanceof AvpAssociatedPartyAddress){
//			containerObj = new  AssociatedPartyAddressAvp((AvpAssociatedPartyAddress) stackAvp);
//		}else if(stackAvp instanceof AvpAssociatedURI){
//			containerObj = new  AssociatedURIAvp((AvpAssociatedURI) stackAvp);
//		}else if(stackAvp instanceof AvpAuthApplicationId){
//			containerObj = new  AuthApplicationIdAvp((AvpAuthApplicationId) stackAvp);
//		}else if(stackAvp instanceof AvpAuthorizedQoS){
//			containerObj = new  AuthorisedQoSAvp((AvpAuthorisedQoS) stackAvp);
//		}else if(stackAvp instanceof AvpAuxApplicInfo){
//			containerObj = new  AuxApplicInfoAvp((AvpAuxApplicInfo) stackAvp);
//		}else if(stackAvp instanceof AvpBaseTimeInterval){
//			containerObj = new  BaseTimeIntervalAvp((AvpBaseTimeInterval) stackAvp);
//		}else if(stackAvp instanceof AvpBearerIdentifier){
//			containerObj = new  BearerIdentifierAvp((AvpBearerIdentifier) stackAvp);
//		}else if(stackAvp instanceof AvpBearerService){
//			containerObj = new  BearerServiceAvp((AvpBearerService) stackAvp);
//		}else if(stackAvp instanceof Avp3GPP2BSID){
//			containerObj = new  BSID3GPP2Avp((Avp3GPP2BSID) stackAvp);
//		}else if(stackAvp instanceof AvpCalledAssertedIdentity){
//			containerObj = new  CalledAssertedIdentityAvp((AvpCalledAssertedIdentity) stackAvp);
//		}else if(stackAvp instanceof AvpCalledPartyAddress){
//			containerObj = new  CalledPartyAddressAvp((AvpCalledPartyAddress) stackAvp);
//		}else if(stackAvp instanceof AvpCalledStationId){
//			containerObj = new  CalledStationIdAvp((AvpCalledStationId) stackAvp);
//		}else if(stackAvp instanceof AvpCallingPartyAddress){
//			containerObj = new  CallingPartyAddressAvp((AvpCallingPartyAddress) stackAvp);
//		}else if(stackAvp instanceof AvpCarrierSelectRoutingInformation){
//			containerObj = new  CarrierSelectRoutingInformationAvp((AvpCarrierSelectRoutingInformation) stackAvp);
//		}else if(stackAvp instanceof AvpCauseCode){
//			containerObj = new  CauseCodeAvp((AvpCauseCode) stackAvp);
//		}else if(stackAvp instanceof AvpCCCorrelationId){
//			containerObj = new  CCCorrelationIdAvp((AvpCCCorrelationId) stackAvp);
//		}else if(stackAvp instanceof AvpCCInputOctets){
//			containerObj = new  CCInputOctetsAvp((AvpCCInputOctets) stackAvp);
//		}else if(stackAvp instanceof AvpCCMoney){
//			containerObj = new  CCMoneyAvp((AvpCCMoney) stackAvp);
//		}else if(stackAvp instanceof AvpCCOutputOctets){
//			containerObj = new  CCOutputOctetsAvp((AvpCCOutputOctets) stackAvp);
//		}else if(stackAvp instanceof AvpCCRequestNumber){
//			containerObj = new  CCRequestNumberAvp((AvpCCRequestNumber) stackAvp);
//		}else if(stackAvp instanceof AvpCCRequestType){
//			containerObj = new  CCRequestTypeAvp((AvpCCRequestType) stackAvp);
//		}else if(stackAvp instanceof AvpCCServiceSpecificUnits){
//			containerObj = new  CCServiceSpecificUnitsAvp((AvpCCServiceSpecificUnits) stackAvp);
//		}else if(stackAvp instanceof AvpCCSessionFailover){
//			containerObj = new  CCSessionFailoverAvp((AvpCCSessionFailover) stackAvp);
//		}else if(stackAvp instanceof AvpCCTime){
//			containerObj = new  CCTimeAvp((AvpCCTime) stackAvp);
//		}else if(stackAvp instanceof AvpCCTotalOctets){
//			containerObj = new  CCTotalOctetsAvp((AvpCCTotalOctets) stackAvp);
//		}else if(stackAvp instanceof AvpCCUnitType){
//			containerObj = new  CCUnitTypeAvp((AvpCCUnitType) stackAvp);
//		}else if(stackAvp instanceof AvpCGAddress){
//			containerObj = new  CGAddressAvp((AvpCGAddress) stackAvp);
//		}else if(stackAvp instanceof AvpChangeCondition){
//			containerObj = new  ChangeConditionAvp((AvpChangeCondition) stackAvp);
//		}else if(stackAvp instanceof AvpChangeTime){
//			containerObj = new  ChangeTimeAvp((AvpChangeTime) stackAvp);
//		}else if(stackAvp instanceof AvpChargedParty){
//			containerObj = new  ChargedPartyAvp((AvpChargedParty) stackAvp);
//		}else if(stackAvp instanceof Avp3GPPChargingCharacteristics){
//			containerObj = new  ChargingCharacteristics3GPPAvp((Avp3GPPChargingCharacteristics) stackAvp);
//		}else if(stackAvp instanceof AvpChargingCharacteristicsSelectionMode){
//			containerObj = new  ChargingCharacteristicsSelectionModeAvp((AvpChargingCharacteristicsSelectionMode) stackAvp);
//		}else if(stackAvp instanceof Avp3GPPChargingId){
//			containerObj = new  ChargingId3GPPAvp((Avp3GPPChargingId) stackAvp);
//		}else if(stackAvp instanceof AvpChargingRuleBaseName){
//			containerObj = new  ChargingRuleBaseNameAvp((AvpChargingRuleBaseName) stackAvp);
//		}else if(stackAvp instanceof AvpClassIdentifier){
//			containerObj = new  ClassIdentifierAvp((AvpClassIdentifier) stackAvp);
//		}else if(stackAvp instanceof AvpClientAddress){
//			containerObj = new  ClientAddressAvp((AvpClientAddress) stackAvp);
//		}else if(stackAvp instanceof AvpCNIPMulticastDistribution){
//			containerObj = new  CNIPMulticastDistributionAvp((AvpCNIPMulticastDistribution) stackAvp);
//		}else if(stackAvp instanceof AvpContentClass){
//			containerObj = new  ContentClassAvp((AvpContentClass) stackAvp);
//		}else if(stackAvp instanceof AvpContentDisposition){
//			containerObj = new  ContentDispositionAvp((AvpContentDisposition) stackAvp);
//		}else if(stackAvp instanceof AvpContentLength){
//			containerObj = new  ContentLengthAvp((AvpContentLength) stackAvp);
//		}else if(stackAvp instanceof AvpContentSize){
//			containerObj = new  ContentSizeAvp((AvpContentSize) stackAvp);
//		}else if(stackAvp instanceof AvpContentType){
//			containerObj = new  ContentTypeAvp((AvpContentType) stackAvp);
//		}else if(stackAvp instanceof AvpCostInformation){
//			containerObj = new  CostInformationAvp((AvpCostInformation) stackAvp);
//		}else if(stackAvp instanceof AvpCostUnit){
//			containerObj = new  CostUnitAvp((AvpCostUnit) stackAvp);
//		}else if(stackAvp instanceof AvpCreditControlFailureHandling){
//			containerObj = new  CreditControlFailureHandlingAvp((AvpCreditControlFailureHandling) stackAvp);
//		}else if(stackAvp instanceof AvpCSGAccessMode){
//			containerObj = new  CSGAccessModeAvp((AvpCSGAccessMode) stackAvp);
//		}else if(stackAvp instanceof AvpCSGId){
//			containerObj = new  CSGIdAvp((AvpCSGId) stackAvp);
//		}else if(stackAvp instanceof AvpCSGMembershipIndication){
//			containerObj = new  CSGMembershipIndicationAvp((AvpCSGMembershipIndication) stackAvp);
//		}else if(stackAvp instanceof AvpCUGInformation){
//			containerObj = new  CUGInformationAvp((AvpCUGInformation) stackAvp);
//		}else if(stackAvp instanceof AvpCurrencyCode){
//			containerObj = new  CurrencyCodeAvp((AvpCurrencyCode) stackAvp);
//		}else if(stackAvp instanceof AvpCurrentTariff){
//			containerObj = new  CurrentTariffAvp((AvpCurrentTariff) stackAvp);
//		}else if(stackAvp instanceof AvpDataCodingScheme){
//			containerObj = new  DataCodingSchemeAvp((AvpDataCodingScheme) stackAvp);
//		}else if(stackAvp instanceof AvpDCDInformation){
//			containerObj = new  DCDInformationAvp((AvpDCDInformation) stackAvp);
//		}else if(stackAvp instanceof AvpDeferredLocationEventType){
//			containerObj = new  DeferredLocationEventTypeAvp((AvpDeferredLocationEventType) stackAvp);
//		}else if(stackAvp instanceof AvpDeliveryReportRequested){
//			containerObj = new  DeliveryReportRequestedAvp((AvpDeliveryReportRequested) stackAvp);
//		}else if(stackAvp instanceof AvpDestinationHost){
//			containerObj = new  DestinationHostAvp((AvpDestinationHost) stackAvp);
//		}else if(stackAvp instanceof AvpDestinationInterface){
//			containerObj = new  DestinationInterfaceAvp((AvpDestinationInterface) stackAvp);
//		}else if(stackAvp instanceof AvpDestinationRealm){
//			containerObj = new  DestinationRealmAvp((AvpDestinationRealm) stackAvp);
//		}else if(stackAvp instanceof AvpDiagnostics){
//			containerObj = new  DiagnosticsAvp((AvpDiagnostics) stackAvp);
//		}else if(stackAvp instanceof AvpDirectDebitingFailureHandling){
//			containerObj = new  DirectDebitingFailureHandlingAvp((AvpDirectDebitingFailureHandling) stackAvp);
//		}else if(stackAvp instanceof AvpDomainName){
//			containerObj = new  DomainNameAvp((AvpDomainName) stackAvp);
//		}else if(stackAvp instanceof AvpDRMContent){
//			containerObj = new  DRMContentAvp((AvpDRMContent) stackAvp);
//		}else if(stackAvp instanceof AvpDynamicAddressFlag){
//			containerObj = new  DynamicAddressFlagAvp((AvpDynamicAddressFlag) stackAvp);
//		}else if(stackAvp instanceof AvpEarlyMediaDescription){
//			containerObj = new  EarlyMediaDescriptionAvp((AvpEarlyMediaDescription) stackAvp);
//		}else if(stackAvp instanceof AvpEnvelope){
//			containerObj = new  EnvelopeAvp((AvpEnvelope) stackAvp);
//		}else if(stackAvp instanceof AvpEnvelopeEndTime){
//			containerObj = new  EnvelopeEndTimeAvp((AvpEnvelopeEndTime) stackAvp);
//		}else if(stackAvp instanceof AvpEnvelopeReporting){
//			containerObj = new  EnvelopeReportingAvp((AvpEnvelopeReporting) stackAvp);
//		}else if(stackAvp instanceof AvpEnvelopeStartTime){
//			containerObj = new  EnvelopeStartTimeAvp((AvpEnvelopeStartTime) stackAvp);
//		}else if(stackAvp instanceof AvpErrorMessage){
//			containerObj = new  ErrorMessageAvp((AvpErrorMessage) stackAvp);
//		}else if(stackAvp instanceof AvpErrorReportingHost){
//			containerObj = new  ErrorReportingHostAvp((AvpErrorReportingHost) stackAvp);
//		}else if(stackAvp instanceof AvpEvent){
//			containerObj = new  EventAvp((AvpEvent) stackAvp);
//		}else if(stackAvp instanceof AvpEventChargingTimeStamp){
//			containerObj = new  EventChargingTimeStampAvp((AvpEventChargingTimeStamp) stackAvp);
//		}else if(stackAvp instanceof AvpEventTimestamp){
//			containerObj = new  EventTimestampAvp((AvpEventTimestamp) stackAvp);
//		}else if(stackAvp instanceof AvpEventType){
//			containerObj = new  EventTypeAvp((AvpEventType) stackAvp);
//		}else if(stackAvp instanceof AvpExpires){
//			containerObj = new  ExpiresAvp((AvpExpires) stackAvp);
//		}else if(stackAvp instanceof AvpExponent){
//			containerObj = new  ExponentAvp((AvpExponent) stackAvp);
//		}else if(stackAvp instanceof AvpFailedAVP){
//			containerObj = new  FailedAVPAvp((AvpFailedAVP) stackAvp);
//		}else if(stackAvp instanceof AvpFileRepairSupported){
//			containerObj = new  FileRepairSupportedAvp((AvpFileRepairSupported) stackAvp);
//		}else if(stackAvp instanceof AvpFilterId){
//			containerObj = new  FilterIdAvp((AvpFilterId) stackAvp);
//		}else if(stackAvp instanceof AvpFinalUnitAction){
//			containerObj = new  FinalUnitActionAvp((AvpFinalUnitAction) stackAvp);
//		}else if(stackAvp instanceof AvpFinalUnitIndication){
//			containerObj = new  FinalUnitIndicationAvp((AvpFinalUnitIndication) stackAvp);
//		}else if(stackAvp instanceof AvpFlowNumber){
//			containerObj = new  FlowNumberAvp((AvpFlowNumber) stackAvp);
//		}else if(stackAvp instanceof AvpFlows){
//			containerObj = new  FlowsAvp((AvpFlows) stackAvp);
//		}else if(stackAvp instanceof AvpGGSNAddress){
//			containerObj = new  GGSNAddressAvp((AvpGGSNAddress) stackAvp);
//		}else if(stackAvp instanceof Avp3GPPGGSNMCCMNC){
//			containerObj = new  GGSNMCCMNC3GPPAvp((Avp3GPPGGSNMCCMNC) stackAvp);
//		}else if(stackAvp instanceof AvpGrantedServiceUnit){
//			containerObj = new  GrantedServiceUnitAvp((AvpGrantedServiceUnit) stackAvp);
//		}else if(stackAvp instanceof AvpGSUPoolIdentifier){
//			containerObj = new  GSUPoolIdentifierAvp((AvpGSUPoolIdentifier) stackAvp);
//		}else if(stackAvp instanceof AvpGSUPoolReference){
//			containerObj = new  GSUPoolReferenceAvp((AvpGSUPoolReference) stackAvp);
//		}else if(stackAvp instanceof AvpGuaranteedBitrateDL){
//			containerObj = new  GuaranteedBitrateDLAvp((AvpGuaranteedBitrateDL) stackAvp);
//		}else if(stackAvp instanceof AvpGuaranteedBitrateUL){
//			containerObj = new  GuaranteedBitrateULAvp((AvpGuaranteedBitrateUL) stackAvp);
//		}else if(stackAvp instanceof AvpIMEI){
//			containerObj = new  IMEIAvp((AvpIMEI) stackAvp);
//		}else if(stackAvp instanceof AvpIMInformation){
//			containerObj = new  IMInformationAvp((AvpIMInformation) stackAvp);
//		}else if(stackAvp instanceof AvpIMSChargingIdentifier){
//			containerObj = new  IMSChargingIdentifierAvp((AvpIMSChargingIdentifier) stackAvp);
//		}else if(stackAvp instanceof AvpIMSCommunicationServiceIdentifier){
//			containerObj = new  IMSCommunicationServiceIdentifierAvp((AvpIMSCommunicationServiceIdentifier) stackAvp);
//		}else if(stackAvp instanceof Avp3GPPIMSI){
//			containerObj = new  IMSI3GPPAvp((Avp3GPPIMSI) stackAvp);
//		}else if(stackAvp instanceof Avp3GPPIMSIMCCMNC){
//			containerObj = new  IMSIMCCMNC3GPPAvp((Avp3GPPIMSIMCCMNC)stackAvp);
//		}else if(stackAvp instanceof AvpIMSInformation){
//			containerObj = new  IMSInformationAvp((AvpIMSInformation) stackAvp);
//		}else if(stackAvp instanceof AvpIMSIUnauthenticatedFlag){
//			containerObj = new  IMSIUnauthenticatedFlagAvp((AvpIMSIUnauthenticatedFlag) stackAvp);
//		}else if(stackAvp instanceof AvpIncomingTrunkGroupID){
//			containerObj = new  IncomingTrunkGroupIDAvp((AvpIncomingTrunkGroupID) stackAvp);
//		}else if(stackAvp instanceof AvpIncrementalCost){
//			containerObj = new  IncrementalCostAvp((AvpIncrementalCost) stackAvp);
//		}else if(stackAvp instanceof AvpInterfaceId){
//			containerObj = new  InterfaceIdAvp((AvpInterfaceId) stackAvp);
//		}else if(stackAvp instanceof AvpInterfacePort){
//			containerObj = new  InterfacePortAvp((AvpInterfacePort) stackAvp);
//		}else if(stackAvp instanceof AvpInterfaceText){
//			containerObj = new  InterfaceTextAvp((AvpInterfaceText) stackAvp);
//		}else if(stackAvp instanceof AvpInterfaceType){
//			containerObj = new  InterfaceTypeAvp((AvpInterfaceType) stackAvp);
//		}else if(stackAvp instanceof AvpInterOperatorIdentifier){
//			containerObj = new  InterOperatorIdentifierAvp((AvpInterOperatorIdentifier) stackAvp);
//		}else if(stackAvp instanceof AvpLCSAPN){
//			containerObj = new  LCSAPNAvp((AvpLCSAPN) stackAvp);
//		}else if(stackAvp instanceof AvpLCSClientDialedByMS){
//			containerObj = new  LCSClientDialedByMSAvp((AvpLCSClientDialedByMS) stackAvp);
//		}else if(stackAvp instanceof AvpLCSClientExternalID){
//			containerObj = new  LCSClientExternalIDAvp((AvpLCSClientExternalID) stackAvp);
//		}else if(stackAvp instanceof AvpLCSClientID){
//			containerObj = new  LCSClientIDAvp((AvpLCSClientID) stackAvp);
//		}else if(stackAvp instanceof AvpLCSClientName){
//			containerObj = new  LCSClientNameAvp((AvpLCSClientName) stackAvp);
//		}else if(stackAvp instanceof AvpLCSClientType){
//			containerObj = new  LCSClientTypeAvp((AvpLCSClientType) stackAvp);
//		}else if(stackAvp instanceof AvpLCSDataCodingScheme){
//			containerObj = new  LCSDataCodingSchemeAvp((AvpLCSDataCodingScheme) stackAvp);
//		}else if(stackAvp instanceof AvpLCSFormatIndicator){
//			containerObj = new  LCSFormatIndicatorAvp((AvpLCSFormatIndicator) stackAvp);
//		}else if(stackAvp instanceof AvpLCSInformation){
//			containerObj = new  LCSInformationAvp((AvpLCSInformation) stackAvp);
//		}else if(stackAvp instanceof AvpLCSNameString){
//			containerObj = new  LCSNameStringAvp((AvpLCSNameString) stackAvp);
//		}else if(stackAvp instanceof AvpLCSRequestorID){
//			containerObj = new  LCSRequestorIDAvp((AvpLCSRequestorID) stackAvp);
//		}else if(stackAvp instanceof AvpLCSRequestorIDString){
//			containerObj = new  LCSRequestorIDStringAvp((AvpLCSRequestorIDString) stackAvp);
//		}else if(stackAvp instanceof AvpLocalSequenceNumber){
//			containerObj = new  LocalSequenceNumberAvp((AvpLocalSequenceNumber) stackAvp);
//		}else if(stackAvp instanceof AvpLocationEstimate){
//			containerObj = new  LocationEstimateAvp((AvpLocationEstimate) stackAvp);
//		}else if(stackAvp instanceof AvpLocationEstimateType){
//			containerObj = new  LocationEstimateTypeAvp((AvpLocationEstimateType) stackAvp);
//		}else if(stackAvp instanceof AvpLocationInformation){
//			containerObj = new  LocationInformationAvp((AvpLocationInformation) stackAvp);
//		}else if(stackAvp instanceof AvpLocationType){
//			containerObj = new  LocationTypeAvp((AvpLocationType) stackAvp);
//		}else if(stackAvp instanceof AvpLowBalanceIndication){
//			containerObj = new  LowBalanceIndicationAvp((AvpLowBalanceIndication) stackAvp);
//		}else if(stackAvp instanceof AvpMandatoryCapability){
//			containerObj = new  MandatoryCapabilityAvp((AvpMandatoryCapability) stackAvp);
//		}else if(stackAvp instanceof AvpMaxRequestedBandwidthDL){
//			containerObj = new  MaxRequestedBandwidthDLAvp((AvpMaxRequestedBandwidthDL) stackAvp);
//		}else if(stackAvp instanceof AvpMaxRequestedBandwidthUL){
//			containerObj = new  MaxRequestedBandwidthULAvp((AvpMaxRequestedBandwidthUL) stackAvp);
//		}else if(stackAvp instanceof AvpMBMS2G3GIndicator){
//			containerObj = new  MBMS2G3GIndicatorAvp((AvpMBMS2G3GIndicator) stackAvp);
//		}else if(stackAvp instanceof AvpMBMSGWAddress){
//			containerObj = new  MBMSGWAddressAvp((AvpMBMSGWAddress) stackAvp);
//		}else if(stackAvp instanceof AvpMBMSInformation){
//			containerObj = new  MBMSInformationAvp((AvpMBMSInformation) stackAvp);
//		}else if(stackAvp instanceof AvpMBMSServiceArea){
//			containerObj = new  MBMSServiceAreaAvp((AvpMBMSServiceArea) stackAvp);
//		}else if(stackAvp instanceof AvpMBMSServiceType){
//			containerObj = new  MBMSServiceTypeAvp((AvpMBMSServiceType) stackAvp);
//		}else if(stackAvp instanceof AvpMBMSSessionIdentity){
//			containerObj = new  MBMSSessionIdentityAvp((AvpMBMSSessionIdentity) stackAvp);
//		}else if(stackAvp instanceof AvpMBMSUserServiceType){
//			containerObj = new  MBMSUserServiceTypeAvp((AvpMBMSUserServiceType) stackAvp);
//		}else if(stackAvp instanceof AvpMediaComponentNumber){
//			containerObj = new  MediaComponentNumberAvp((AvpMediaComponentNumber) stackAvp);
//		}else if(stackAvp instanceof AvpMediaInitiatorFlag){
//			containerObj = new  MediaInitiatorFlagAvp((AvpMediaInitiatorFlag) stackAvp);
//		}else if(stackAvp instanceof AvpMediaInitiatorParty){
//			containerObj = new  MediaInitiatorPartyAvp((AvpMediaInitiatorParty) stackAvp);
//		}else if(stackAvp instanceof Avp3GPP2MEID){
//			containerObj = new  MEID3GPP2Avp((Avp3GPP2MEID) stackAvp);
//		}else if(stackAvp instanceof AvpMessageBody){
//			containerObj = new  MessageBodyAvp((AvpMessageBody) stackAvp);
//		}else if(stackAvp instanceof AvpMessageClass){
//			containerObj = new  MessageClassAvp((AvpMessageClass) stackAvp);
//		}else if(stackAvp instanceof AvpMessageID){
//			containerObj = new  MessageIDAvp((AvpMessageID) stackAvp);
//		}else if(stackAvp instanceof AvpMessageSize){
//			containerObj = new  MessageSizeAvp((AvpMessageSize) stackAvp);
//		}else if(stackAvp instanceof AvpMessageType){
//			containerObj = new  MessageTypeAvp((AvpMessageType) stackAvp);
//		}else if(stackAvp instanceof AvpMMBoxStorageRequested){
//			containerObj = new  MMBoxStorageRequestedAvp((AvpMMBoxStorageRequested) stackAvp);
//		}else if(stackAvp instanceof AvpMMContentType){
//			containerObj = new  MMContentTypeAvp((AvpMMContentType) stackAvp);
//		}else if(stackAvp instanceof AvpMMSInformation){
//			containerObj = new  MMSInformationAvp((AvpMMSInformation) stackAvp);
//		}else if(stackAvp instanceof AvpMMTelInformation){
//			containerObj = new  MMTelInformationAvp((AvpMMTelInformation) stackAvp);
//		}else if(stackAvp instanceof AvpMSISDN){
//			containerObj = new  MSISDNAvp((AvpMSISDN) stackAvp);
//		}else if(stackAvp instanceof Avp3GPPMSTimeZone){
//			containerObj = new  MSTimeZone3GPPAvp((Avp3GPPMSTimeZone) stackAvp);
//		}else if(stackAvp instanceof AvpMultipleServicesCreditControl){
//			containerObj = new  MultipleServicesCreditControlAvp((AvpMultipleServicesCreditControl) stackAvp);
//		}else if(stackAvp instanceof AvpMultipleServicesIndicator){
//			containerObj = new  MultipleServicesIndicatorAvp((AvpMultipleServicesIndicator) stackAvp);
//		}else if(stackAvp instanceof AvpNextTariff){
//			containerObj = new  NextTariffAvp((AvpNextTariff) stackAvp);
//		}else if(stackAvp instanceof AvpNodeFunctionality){
//			containerObj = new  NodeFunctionalityAvp((AvpNodeFunctionality) stackAvp);
//		}else if(stackAvp instanceof AvpNodeId){
//			containerObj = new  NodeIdAvp((AvpNodeId) stackAvp);
//		}else if(stackAvp instanceof Avp3GPPNSAPI){
//			containerObj = new  NSAPI3GPPAvp((Avp3GPPNSAPI) stackAvp);
//		}else if(stackAvp instanceof AvpNumberOfDiversions){
//			containerObj = new  NumberOfDiversionsAvp((AvpNumberOfDiversions) stackAvp);
//		}else if(stackAvp instanceof AvpNumberOfMessagesSent){
//			containerObj = new  NumberOfMessagesSentAvp((AvpNumberOfMessagesSent) stackAvp);
//		}else if(stackAvp instanceof AvpNumberOfParticipants){
//			containerObj = new  NumberOfParticipantsAvp((AvpNumberOfParticipants) stackAvp);
//		}else if(stackAvp instanceof AvpNumberOfReceivedTalkBursts){
//			containerObj = new  NumberOfReceivedTalkBurstsAvp((AvpNumberOfReceivedTalkBursts) stackAvp);
//		}else if(stackAvp instanceof AvpNumberOfTalkBursts){
//			containerObj = new  NumberOfTalkBurstsAvp((AvpNumberOfTalkBursts) stackAvp);
//		}else if(stackAvp instanceof AvpNumberPortabilityRoutingInformation){
//			containerObj = new  NumberPortabilityRoutingInformationAvp((AvpNumberPortabilityRoutingInformation) stackAvp);
//		}else if(stackAvp instanceof AvpOfflineCharging){
//			containerObj = new  OfflineChargingAvp((AvpOfflineCharging) stackAvp);
//		}else if(stackAvp instanceof AvpOnlineChargingFlag){
//			containerObj = new  OnlineChargingFlagAvp((AvpOnlineChargingFlag) stackAvp);
//		}else if(stackAvp instanceof AvpOperatorName){
//			containerObj = new  OperatorNameAvp((AvpOperatorName) stackAvp);
//		}else if(stackAvp instanceof AvpOptionalCapability){
//			containerObj = new  OptionalCapabilityAvp((AvpOptionalCapability) stackAvp);
//		}else if(stackAvp instanceof AvpOriginatingIOI){
//			containerObj = new  OriginatingIOIAvp((AvpOriginatingIOI) stackAvp);
//		}else if(stackAvp instanceof AvpOriginatorAddress){
//			containerObj = new  OriginatorAddressAvp((AvpOriginatorAddress) stackAvp);
//		}else if(stackAvp instanceof AvpOriginator){
//			containerObj = new  OriginatorAvp((AvpOriginator) stackAvp);
//		}else if(stackAvp instanceof AvpOriginatorInterface){
//			containerObj = new  OriginatorInterfaceAvp((AvpOriginatorInterface) stackAvp);
//		}else if(stackAvp instanceof AvpOriginatorReceivedAddress){
//			containerObj = new  OriginatorReceivedAddressAvp((AvpOriginatorReceivedAddress) stackAvp);
//		}else if(stackAvp instanceof AvpOriginatorSCCPAddress){
//			containerObj = new  OriginatorSCCPAddressAvp((AvpOriginatorSCCPAddress) stackAvp);
//		}else if(stackAvp instanceof AvpOriginHost){
//			containerObj = new  OriginHostAvp((AvpOriginHost) stackAvp);
//		}else if(stackAvp instanceof AvpOriginRealm){
//			containerObj = new  OriginRealmAvp((AvpOriginRealm) stackAvp);
//		}else if(stackAvp instanceof AvpOriginStateId){
//			containerObj = new  OriginStateIdAvp((AvpOriginStateId) stackAvp);
//		}else if(stackAvp instanceof AvpOutgoingSessionId){
//			containerObj = new  OutgoingSessionIdAvp((AvpOutgoingSessionId) stackAvp);
//		}else if(stackAvp instanceof AvpOutgoingTrunkGroupID){
//			containerObj = new  OutgoingTrunkGroupIDAvp((AvpOutgoingTrunkGroupID) stackAvp);
//		}else if(stackAvp instanceof AvpParticipantAccessPriority){
//			containerObj = new  ParticipantAccessPriorityAvp((AvpParticipantAccessPriority) stackAvp);
//		}else if(stackAvp instanceof AvpParticipantActionType){
//			containerObj = new  ParticipantActionTypeAvp((AvpParticipantActionType) stackAvp);
//		}else if(stackAvp instanceof AvpParticipantGroup){
//			containerObj = new  ParticipantGroupAvp((AvpParticipantGroup) stackAvp);
//		}else if(stackAvp instanceof AvpParticipantsInvolved){
//			containerObj = new  ParticipantsInvolvedAvp((AvpParticipantsInvolved) stackAvp);
//		}else if(stackAvp instanceof AvpPDGAddress){
//			containerObj = new  PDGAddressAvp((AvpPDGAddress) stackAvp);
//		}else if(stackAvp instanceof AvpPDGChargingId){
//			containerObj = new  PDGChargingIdAvp((AvpPDGChargingId) stackAvp);
//		}else if(stackAvp instanceof AvpPDNConnectionID){
//			containerObj = new  PDNConnectionChargingIDAvp((AvpPDNConnectionChargingID) stackAvp);
//		}else if(stackAvp instanceof AvpPDPAddress){
//			containerObj = new  PDPAddressAvp((AvpPDPAddress) stackAvp);
//		}else if(stackAvp instanceof AvpPDPContextType){
//			containerObj = new  PDPContextTypeAvp((AvpPDPContextType) stackAvp);
//		}else if(stackAvp instanceof Avp3GPPPDPType){
//			containerObj = new  PDPType3GPPAvp((Avp3GPPPDPType) stackAvp);
//		}else if(stackAvp instanceof AvpPoCChangeCondition){
//			containerObj = new  PoCChangeConditionAvp((AvpPoCChangeCondition) stackAvp);
//		}else if(stackAvp instanceof AvpPoCChangeTime){
//			containerObj = new  PoCChangeTimeAvp((AvpPoCChangeTime) stackAvp);
//		}else if(stackAvp instanceof AvpPoCControllingAddress){
//			containerObj = new  PoCControllingAddressAvp((AvpPoCControllingAddress) stackAvp);
//		}else if(stackAvp instanceof AvpPoCEventType){
//			containerObj = new  PoCEventTypeAvp((AvpPoCEventType) stackAvp);
//		}else if(stackAvp instanceof AvpPoCGroupName){
//			containerObj = new  PoCGroupNameAvp((AvpPoCGroupName) stackAvp);
//		}else if(stackAvp instanceof AvpPoCInformation){
//			containerObj = new  PoCInformationAvp((AvpPoCInformation) stackAvp);
//		}else if(stackAvp instanceof AvpPoCServerRole){
//			containerObj = new  PoCServerRoleAvp((AvpPoCServerRole) stackAvp);
//		}else if(stackAvp instanceof AvpPoCSessionId){
//			containerObj = new  PoCSessionIdAvp((AvpPoCSessionId) stackAvp);
//		}else if(stackAvp instanceof AvpPoCSessionInitiationType){
//			containerObj = new  PoCSessionInitiationTypeAvp((AvpPoCSessionInitiationType) stackAvp);
//		}else if(stackAvp instanceof AvpPoCSessionType){
//			containerObj = new  PoCSessionTypeAvp((AvpPoCSessionType) stackAvp);
//		}else if(stackAvp instanceof AvpPoCUserRole){
//			containerObj = new  PoCUserRoleAvp((AvpPoCUserRole) stackAvp);
//		}else if(stackAvp instanceof AvpPoCUserRoleIDs){
//			containerObj = new  PoCUserRoleIDsAvp((AvpPoCUserRoleIDs) stackAvp);
//		}else if(stackAvp instanceof AvpPoCUserRoleInfoUnits){
//			containerObj = new  PoCUserRoleInfoUnitsAvp((AvpPoCUserRoleInfoUnits) stackAvp);
//		}else if(stackAvp instanceof AvpPositioningData){
//			containerObj = new  PositioningDataAvp((AvpPositioningData) stackAvp);
//		}else if(stackAvp instanceof AvpPreemptionCapability){
//			containerObj = new  PreemptionCapabilityAvp((AvpPreemptionCapability) stackAvp);
//		}else if(stackAvp instanceof AvpPreemptionVulnerability){
//			containerObj = new  PreemptionVulnerabilityAvp((AvpPreemptionVulnerability) stackAvp);
//		}else if(stackAvp instanceof AvpPreferredAoCCurrency){
//			containerObj = new  PreferredAoCCurrencyAvp((AvpPreferredAoCCurrency) stackAvp);
//		}else if(stackAvp instanceof AvpPriority){
//			containerObj = new  PriorityAvp((AvpPriority) stackAvp);
//		}else if(stackAvp instanceof AvpPriorityLevel){
//			containerObj = new  PriorityLevelAvp((AvpPriorityLevel) stackAvp);
//		}else if(stackAvp instanceof AvpProxyHost){
//			containerObj = new  ProxyHostAvp((AvpProxyHost) stackAvp);
//		}else if(stackAvp instanceof AvpProxyInfo){
//			containerObj = new  ProxyInfoAvp((AvpProxyInfo) stackAvp);
//		}else if(stackAvp instanceof AvpProxyState){
//			containerObj = new  ProxyStateAvp((AvpProxyState) stackAvp);
//		}else if(stackAvp instanceof AvpPSAppendFreeFormatData){
//			containerObj = new  PSAppendFreeFormatDataAvp((AvpPSAppendFreeFormatData) stackAvp);
//		}else if(stackAvp instanceof AvpPSFreeFormatData){
//			containerObj = new  PSFreeFormatDataAvp((AvpPSFreeFormatData) stackAvp);
//		}else if(stackAvp instanceof AvpPSFurnishChargingInformation){
//			containerObj = new  PSFurnishChargingInformationAvp((AvpPSFurnishChargingInformation) stackAvp);
//		}else if(stackAvp instanceof AvpPSInformation){
//			containerObj = new  PSInformationAvp((AvpPSInformation) stackAvp);
//		}else if(stackAvp instanceof AvpQoSClassIdentifier){
//			containerObj = new  QoSClassIdentifierAvp((AvpQoSClassIdentifier) stackAvp);
//		}else if(stackAvp instanceof AvpQoSInformation){
//			containerObj = new  QoSInformationAvp((AvpQoSInformation) stackAvp);
//		}else if(stackAvp instanceof AvpQuotaConsumptionTime){
//			containerObj = new  QuotaConsumptionTimeAvp((AvpQuotaConsumptionTime) stackAvp);
//		}else if(stackAvp instanceof AvpQuotaHoldingTime){
//			containerObj = new  QuotaHoldingTimeAvp((AvpQuotaHoldingTime) stackAvp);
//		}else if(stackAvp instanceof AvpRAI){
//			containerObj = new  RAIAvp((AvpRAI) stackAvp);
//		}else if(stackAvp instanceof AvpRateElement){
//			containerObj = new  RateElementAvp((AvpRateElement) stackAvp);
//		}else if(stackAvp instanceof AvpRatingGroup){
//			containerObj = new  RatingGroupAvp((AvpRatingGroup) stackAvp);
//		}else if(stackAvp instanceof Avp3GPPRATType){
//			containerObj = new  RATType3GPPAvp((Avp3GPPRATType) stackAvp);
//		}else if(stackAvp instanceof AvpReadReplyReportRequested){
//			containerObj = new  ReadReplyReportRequestedAvp((AvpReadReplyReportRequested) stackAvp);
//		}else if(stackAvp instanceof AvpRealTimeTariffInformation){
//			containerObj = new  RealTimeTariffInformationAvp((AvpRealTimeTariffInformation) stackAvp);
//		}else if(stackAvp instanceof AvpReasonCode){
//			containerObj = new  ReasonCodeAvp((AvpReasonCode) stackAvp);
//		}else if(stackAvp instanceof AvpReAuthRequestType){
//			containerObj = new  ReAuthRequestTypeAvp((AvpReAuthRequestType) stackAvp);
//		}else if(stackAvp instanceof AvpReceivedTalkBurstTime){
//			containerObj = new  ReceivedTalkBurstTimeAvp((AvpReceivedTalkBurstTime) stackAvp);
//		}else if(stackAvp instanceof AvpReceivedTalkBurstVolume){
//			containerObj = new  ReceivedTalkBurstVolumeAvp((AvpReceivedTalkBurstVolume) stackAvp);
//		}else if(stackAvp instanceof AvpRecipientAddress){
//			containerObj = new  RecipientAddressAvp((AvpRecipientAddress) stackAvp);
//		}else if(stackAvp instanceof AvpRecipientInfo){
//			containerObj = new  RecipientInfoAvp((AvpRecipientInfo) stackAvp);
//		}else if(stackAvp instanceof AvpRecipientReceivedAddress){
//			containerObj = new  RecipientReceivedAddressAvp((AvpRecipientReceivedAddress) stackAvp);
//		}else if(stackAvp instanceof AvpRecipientSCCPAddress){
//			containerObj = new  RecipientSCCPAddressAvp((AvpRecipientSCCPAddress) stackAvp);
//		}else if(stackAvp instanceof AvpRedirectAddressType){
//			containerObj = new  RedirectAddressTypeAvp((AvpRedirectAddressType) stackAvp);
//		}else if(stackAvp instanceof AvpRedirectHost){
//			containerObj = new  RedirectHostAvp((AvpRedirectHost) stackAvp);
//		}else if(stackAvp instanceof AvpRedirectHostUsage){
//			containerObj = new  RedirectHostUsageAvp((AvpRedirectHostUsage) stackAvp);
//		}else if(stackAvp instanceof AvpRedirectMaxCacheTime){
//			containerObj = new  RedirectMaxCacheTimeAvp((AvpRedirectMaxCacheTime) stackAvp);
//		}else if(stackAvp instanceof AvpRedirectServerAddress){
//			containerObj = new  RedirectServerAddressAvp((AvpRedirectServerAddress) stackAvp);
//		}else if(stackAvp instanceof AvpRedirectServer){
//			containerObj = new  RedirectServerAvp((AvpRedirectServer) stackAvp);
//		}else if(stackAvp instanceof AvpRefundInformation){
//			containerObj = new  RefundInformationAvp((AvpRefundInformation) stackAvp);
//		}else if(stackAvp instanceof AvpRemainingBalance){
//			containerObj = new  RemainingBalanceAvp((AvpRemainingBalance) stackAvp);
//		}else if(stackAvp instanceof AvpReplyApplicID){
//			containerObj = new  ReplyApplicIDAvp((AvpReplyApplicID) stackAvp);
//		}else if(stackAvp instanceof AvpReplyPathRequested){
//			containerObj = new  ReplyPathRequestedAvp((AvpReplyPathRequested) stackAvp);
//		}else if(stackAvp instanceof AvpReportingReason){
//			containerObj = new  ReportingReasonAvp((AvpReportingReason) stackAvp);
//		}else if(stackAvp instanceof AvpRequestedAction){
//			containerObj = new  RequestedActionAvp((AvpRequestedAction) stackAvp);
//		}else if(stackAvp instanceof AvpRequestedPartyAddress){
//			containerObj = new  RequestedPartyAddressAvp((AvpRequestedPartyAddress) stackAvp);
//		}else if(stackAvp instanceof AvpRequestedServiceUnit){
//			containerObj = new  RequestedServiceUnitAvp((AvpRequestedServiceUnit) stackAvp);
//		}else if(stackAvp instanceof AvpRequiredMBMSBearerCapabilities){
//			containerObj = new  RequiredMBMSBearerCapabilitiesAvp((AvpRequiredMBMSBearerCapabilities) stackAvp);
//		}else if(stackAvp instanceof AvpRestrictionFilterRule){
//			containerObj = new  RestrictionFilterRuleAvp((AvpRestrictionFilterRule) stackAvp);
//		}else if(stackAvp instanceof AvpResultCode){
//			containerObj = new  ResultCodeAvp((AvpResultCode) stackAvp);
//		}else if(stackAvp instanceof AvpRoleOfNode){
//			containerObj = new  RoleOfNodeAvp((AvpRoleOfNode) stackAvp);
//		}else if(stackAvp instanceof AvpRouteRecord){
//			containerObj = new  RouteRecordAvp((AvpRouteRecord) stackAvp);
//		}else if(stackAvp instanceof AvpScaleFactor){
//			containerObj = new  ScaleFactorAvp((AvpScaleFactor) stackAvp);
//		}else if(stackAvp instanceof AvpSDPAnswerTimestamp){
//			containerObj = new  SDPAnswerTimestampAvp((AvpSDPAnswerTimestamp) stackAvp);
//		}else if(stackAvp instanceof AvpSDPMediaComponent){
//			containerObj = new  SDPMediaComponentAvp((AvpSDPMediaComponent) stackAvp);
//		}else if(stackAvp instanceof AvpSDPMediaDescription){
//			containerObj = new  SDPMediaDescriptionAvp((AvpSDPMediaDescription) stackAvp);
//		}else if(stackAvp instanceof AvpSDPMediaName){
//			containerObj = new  SDPMediaNameAvp((AvpSDPMediaName) stackAvp);
//		}else if(stackAvp instanceof AvpSDPOfferTimestamp){
//			containerObj = new  SDPOfferTimestampAvp((AvpSDPOfferTimestamp) stackAvp);
//		}else if(stackAvp instanceof AvpSDPSessionDescription){
//			containerObj = new  SDPSessionDescriptionAvp((AvpSDPSessionDescription) stackAvp);
//		}else if(stackAvp instanceof AvpSDPTimeStamps){
//			containerObj = new  SDPTimeStampsAvp((AvpSDPTimeStamps) stackAvp);
//		}else if(stackAvp instanceof AvpSDPType){
//			containerObj = new  SDPTypeAvp((AvpSDPType) stackAvp);
//		}else if(stackAvp instanceof Avp3GPPSelectionMode){
//			containerObj = new  SelectionMode3GPPAvp((Avp3GPPSelectionMode) stackAvp);
//		}else if(stackAvp instanceof AvpServedPartyIPAddress){
//			containerObj = new  ServedPartyIPAddressAvp((AvpServedPartyIPAddress) stackAvp);
//		}else if(stackAvp instanceof AvpServerCapabilities){
//			containerObj = new  ServerCapabilitiesAvp((AvpServerCapabilities) stackAvp);
//		}else if(stackAvp instanceof AvpServerName){
//			containerObj = new  ServerNameAvp((AvpServerName) stackAvp);
//		}else if(stackAvp instanceof AvpServiceContextId){
//			containerObj = new  ServiceContextIdAvp((AvpServiceContextId) stackAvp);
//		}else if(stackAvp instanceof AvpServiceDataContainer){
//			containerObj = new  ServiceDataContainerAvp((AvpServiceDataContainer) stackAvp);
//		}else if(stackAvp instanceof AvpServiceGenericInformation){
//			containerObj = new  ServiceGenericInformationAvp((AvpServiceGenericInformation) stackAvp);
//		}else if(stackAvp instanceof AvpServiceId){
//			containerObj = new  ServiceIdAvp((AvpServiceId) stackAvp);
//		}else if(stackAvp instanceof AvpServiceIdentifier){
//			containerObj = new  ServiceIdentifierAvp((AvpServiceIdentifier) stackAvp);
//		}else if(stackAvp instanceof AvpServiceInformation){
//			containerObj = new  ServiceInformationAvp((AvpServiceInformation) stackAvp);
//		}else if(stackAvp instanceof AvpServiceMode){
//			containerObj = new  ServiceModeAvp((AvpServiceMode) stackAvp);
//		}else if(stackAvp instanceof AvpServiceSpecificData){
//			containerObj = new  ServiceSpecificDataAvp((AvpServiceSpecificData) stackAvp);
//		}else if(stackAvp instanceof AvpServiceSpecificInfo){
//			containerObj = new  ServiceSpecificInfoAvp((AvpServiceSpecificInfo) stackAvp);
//		}else if(stackAvp instanceof AvpServiceSpecificType){
//			containerObj = new  ServiceSpecificTypeAvp((AvpServiceSpecificType) stackAvp);
//		}else if(stackAvp instanceof AvpServiceType){
//			containerObj = new  ServiceTypeAvp((AvpServiceType) stackAvp);
//		}else if(stackAvp instanceof AvpServingNodeType){
//			containerObj = new  ServingNodeTypeAvp((AvpServingNodeType) stackAvp);
//		}else if(stackAvp instanceof AvpSessionId){
//			containerObj = new  SessionIdAvp((AvpSessionId) stackAvp);
//		}else if(stackAvp instanceof AvpSessionPriority){
//			containerObj = new  SessionPriorityAvp((AvpSessionPriority) stackAvp);
//		}else if(stackAvp instanceof Avp3GPPSessionStopIndicator){
//			containerObj = new  SessionStopIndicator3GPPAvp((Avp3GPPSessionStopIndicator) stackAvp);
//		}else if(stackAvp instanceof AvpSGSNAddress){
//			containerObj = new  SGSNAddressAvp((AvpSGSNAddress) stackAvp);
//		}else if(stackAvp instanceof Avp3GPPSGSNMCCMNC){
//			containerObj = new  SGSNMCCMNC3GPPAvp((Avp3GPPSGSNMCCMNC) stackAvp);
//		}else if(stackAvp instanceof AvpSGWChange){
//			containerObj = new  SGWChangeAvp((AvpSGWChange) stackAvp);
//		}else if(stackAvp instanceof AvpSIPMethod){
//			containerObj = new  SIPMethodAvp((AvpSIPMethod) stackAvp);
//		}else if(stackAvp instanceof AvpSIPRequestTimestamp){
//			containerObj = new  SIPRequestTimestampAvp((AvpSIPRequestTimestamp) stackAvp);
//		}else if(stackAvp instanceof AvpSIPRequestTimestampFraction){
//			containerObj = new  SIPRequestTimestampFractionAvp((AvpSIPRequestTimestampFraction) stackAvp);
//		}else if(stackAvp instanceof AvpSIPResponseTimestamp){
//			containerObj = new  SIPResponseTimestampAvp((AvpSIPResponseTimestamp) stackAvp);
//		}else if(stackAvp instanceof AvpSIPResponseTimestampFraction){
//			containerObj = new  SIPResponseTimestampFractionAvp((AvpSIPResponseTimestampFraction) stackAvp);
//		}else if(stackAvp instanceof AvpSMDischargeTime){
//			containerObj = new  SMDischargeTimeAvp((AvpSMDischargeTime) stackAvp);
//		}else if(stackAvp instanceof AvpSMMessageType){
//			containerObj = new  SMMessageTypeAvp((AvpSMMessageType) stackAvp);
//		}else if(stackAvp instanceof AvpSMProtocolID){
//			containerObj = new  SMProtocolIDAvp((AvpSMProtocolID) stackAvp);
//		}else if(stackAvp instanceof AvpSMSCAddress){
//			containerObj = new  SMSCAddressAvp((AvpSMSCAddress) stackAvp);
//		}else if(stackAvp instanceof AvpSMServiceType){
//			containerObj = new  SMServiceTypeAvp((AvpSMServiceType) stackAvp);
//		}else if(stackAvp instanceof AvpSMSInformation){
//			containerObj = new  SMSInformationAvp((AvpSMSInformation) stackAvp);
//		}else if(stackAvp instanceof AvpSMSNode){
//			containerObj = new  SMSNodeAvp((AvpSMSNode) stackAvp);
//		}else if(stackAvp instanceof AvpSMStatus){
//			containerObj = new  SMStatusAvp((AvpSMStatus) stackAvp);
//		}else if(stackAvp instanceof AvpSMUserDataHeader){
//			containerObj = new  SMUserDataHeaderAvp((AvpSMUserDataHeader) stackAvp);
//		}else if(stackAvp instanceof AvpSoftwareVersion){
//			containerObj = new  SoftwareVersionAvp((AvpSoftwareVersion) stackAvp);
//		}else if(stackAvp instanceof AvpStartTime){
//			containerObj = new  StartTimeAvp((AvpStartTime) stackAvp);
//		}else if(stackAvp instanceof AvpStopTime){
//			containerObj = new  StopTimeAvp((AvpStopTime) stackAvp);
//		}else if(stackAvp instanceof AvpSubmissionTime){
//			containerObj = new  SubmissionTimeAvp((AvpSubmissionTime) stackAvp);
//		}else if(stackAvp instanceof AvpSubscriberRole){
//			containerObj = new  SubscriberRoleAvp((AvpSubscriberRole) stackAvp);
//		}else if(stackAvp instanceof AvpSubscriptionId){
//			containerObj = new  SubscriptionIdAvp((AvpSubscriptionId) stackAvp);
//		}else if(stackAvp instanceof AvpSubscriptionIdData){
//			containerObj = new  SubscriptionIdDataAvp((AvpSubscriptionIdData) stackAvp);
//		}else if(stackAvp instanceof AvpSubscriptionIdType){
//			containerObj = new  SubscriptionIdTypeAvp((AvpSubscriptionIdType) stackAvp);
//		}else if(stackAvp instanceof AvpSupplementaryService){
//			containerObj = new  SupplementaryServiceAvp((AvpSupplementaryService) stackAvp);
//		}else if(stackAvp instanceof AvpTalkBurstExchange){
//			containerObj = new  TalkBurstExchangeAvp((AvpTalkBurstExchange) stackAvp);
//		}else if(stackAvp instanceof AvpTalkBurstTime){
//			containerObj = new  TalkBurstTimeAvp((AvpTalkBurstTime) stackAvp);
//		}else if(stackAvp instanceof AvpTalkBurstVolume){
//			containerObj = new  TalkBurstVolumeAvp((AvpTalkBurstVolume) stackAvp);
//		}else if(stackAvp instanceof AvpTariffChangeUsage){
//			containerObj = new  TariffChangeUsageAvp((AvpTariffChangeUsage) stackAvp);
//		}else if(stackAvp instanceof AvpTariffInformation){
//			containerObj = new  TariffInformationAvp((AvpTariffInformation) stackAvp);
//		}else if(stackAvp instanceof AvpTariffTimeChange){
//			containerObj = new  TariffTimeChangeAvp((AvpTariffTimeChange) stackAvp);
//		}else if(stackAvp instanceof AvpTariffXML){
//			containerObj = new  TariffXMLAvp((AvpTariffXML) stackAvp);
//		}else if(stackAvp instanceof AvpTerminalInformation){
//			containerObj = new  TerminalInformationAvp((AvpTerminalInformation) stackAvp);
//		}else if(stackAvp instanceof AvpTerminatingIOI){
//			containerObj = new  TerminatingIOIAvp((AvpTerminatingIOI) stackAvp);
//		}else if(stackAvp instanceof AvpTerminationCause){
//			containerObj = new  TerminationCauseAvp((AvpTerminationCause) stackAvp);
//		}else if(stackAvp instanceof AvpTimeFirstUsage){
//			containerObj = new  TimeFirstUsageAvp((AvpTimeFirstUsage) stackAvp);
//		}else if(stackAvp instanceof AvpTimeLastUsage){
//			containerObj = new  TimeLastUsageAvp((AvpTimeLastUsage) stackAvp);
//		}else if(stackAvp instanceof AvpTimeQuotaMechanism){
//			containerObj = new  TimeQuotaMechanismAvp((AvpTimeQuotaMechanism) stackAvp);
//		}else if(stackAvp instanceof AvpTimeQuotaThreshold){
//			containerObj = new  TimeQuotaThresholdAvp((AvpTimeQuotaThreshold) stackAvp);
//		}else if(stackAvp instanceof AvpTimeQuotaType){
//			containerObj = new  TimeQuotaTypeAvp((AvpTimeQuotaType) stackAvp);
//		}else if(stackAvp instanceof AvpTimeStamps){
//			containerObj = new  TimeStampsAvp((AvpTimeStamps) stackAvp);
//		}else if(stackAvp instanceof AvpTimeUsage){
//			containerObj = new  TimeUsageAvp((AvpTimeUsage) stackAvp);
//		}else if(stackAvp instanceof AvpTMGI){
//			containerObj = new  TMGIAvp((AvpTMGI) stackAvp);
//		}else if(stackAvp instanceof AvpTokenText){
//			containerObj = new  TokenTextAvp((AvpTokenText) stackAvp);
//		}else if(stackAvp instanceof AvpTrafficDataVolumes){
//			containerObj = new  TrafficDataVolumesAvp((AvpTrafficDataVolumes) stackAvp);
//		}else if(stackAvp instanceof AvpTrigger){
//			containerObj = new  TriggerAvp((AvpTrigger) stackAvp);
//		}else if(stackAvp instanceof AvpTriggerType){
//			containerObj = new  TriggerTypeAvp((AvpTriggerType) stackAvp);
//		}else if(stackAvp instanceof AvpTrunkGroupID){
//			containerObj = new  TrunkGroupIDAvp((AvpTrunkGroupID) stackAvp);
//		}else if(stackAvp instanceof AvpTypeNumber){
//			containerObj = new  TypeNumberAvp((AvpTypeNumber) stackAvp);
//		}else if(stackAvp instanceof AvpUnitCost){
//			containerObj = new  UnitCostAvp((AvpUnitCost) stackAvp);
//		}else if(stackAvp instanceof AvpUnitQuotaThreshold){
//			containerObj = new  UnitQuotaThresholdAvp((AvpUnitQuotaThreshold) stackAvp);
//		}else if(stackAvp instanceof AvpUnitValue){
//			containerObj = new  UnitValueAvp((AvpUnitValue) stackAvp);
//		}else if(stackAvp instanceof AvpUsedServiceUnit){
//			containerObj = new  UsedServiceUnitAvp((AvpUsedServiceUnit) stackAvp);
//		}else if(stackAvp instanceof AvpUserCSGInformation){
//			containerObj = new  UserCSGInformationAvp((AvpUserCSGInformation) stackAvp);
//		}else if(stackAvp instanceof AvpUserEquipmentInfo){
//			containerObj = new  UserEquipmentInfoAvp((AvpUserEquipmentInfo) stackAvp);
//		}else if(stackAvp instanceof AvpUserEquipmentInfoType){
//			containerObj = new  UserEquipmentInfoTypeAvp((AvpUserEquipmentInfoType) stackAvp);
//		}else if(stackAvp instanceof AvpUserEquipmentInfoValue){
//			containerObj = new  UserEquipmentInfoValueAvp((AvpUserEquipmentInfoValue) stackAvp);
//		}else if(stackAvp instanceof Avp3GPPUserLocationInfo){
//			containerObj = new  UserLocationInfo3GPPAvp((Avp3GPPUserLocationInfo) stackAvp);
//		}else if(stackAvp instanceof AvpUserName){
//			containerObj = new  UserNameAvp((AvpUserName) stackAvp);
//		}else if(stackAvp instanceof AvpUserParticipatingType){
//			containerObj = new  UserParticipatingTypeAvp((AvpUserParticipatingType) stackAvp);
//		}else if(stackAvp instanceof AvpUserSessionID){
//			containerObj = new  UserSessionIDAvp((AvpUserSessionID) stackAvp);
//		}else if(stackAvp instanceof AvpValidityTime){
//			containerObj = new  ValidityTimeAvp((AvpValidityTime) stackAvp);
//		}else if(stackAvp instanceof AvpValueDigits){
//			containerObj = new  ValueDigitsAvp((AvpValueDigits) stackAvp);
//		}else if(stackAvp instanceof AvpVASID){
//			containerObj = new  VASIDAvp((AvpVASID) stackAvp);
//		}else if(stackAvp instanceof AvpVASPID){
//			containerObj = new  VASPIDAvp((AvpVASPID) stackAvp);
//		}else if(stackAvp instanceof AvpVolumeQuotaThreshold){
//			containerObj = new  VolumeQuotaThresholdAvp((AvpVolumeQuotaThreshold) stackAvp);
//		}else if(stackAvp instanceof AvpWAGAddress){
//			containerObj = new  WAGAddressAvp((AvpWAGAddress) stackAvp);
//		}else if(stackAvp instanceof AvpWAGPLMNId){
//			containerObj = new  WAGPLMNIdAvp((AvpWAGPLMNId) stackAvp);
//		}else if(stackAvp instanceof AvpWLANInformation){
//			containerObj = new  WLANInformationAvp((AvpWLANInformation) stackAvp);
//		}else if(stackAvp instanceof AvpWLANRadioContainer){
//			containerObj = new  WLANRadioContainerAvp((AvpWLANRadioContainer) stackAvp);
//		}else if(stackAvp instanceof AvpWLANSessionId){
//			containerObj = new  WLANSessionIdAvp((AvpWLANSessionId) stackAvp);
//		}else if(stackAvp instanceof AvpWLANTechnology){
//			containerObj = new  WLANTechnologyAvp((AvpWLANTechnology) stackAvp);
//		}else if(stackAvp instanceof AvpWLANUELocalIPAddress){
//			containerObj = new  WLANUELocalIPAddressAvp((AvpWLANUELocalIPAddress) stackAvp);
//		}
//		return containerObj;
//	}
}
