package com.baypackets.ase.ra.diameter.rf.stackif;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameter;
import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterVendorSpecificSet;
import com.baypackets.ase.ra.diameter.base.avp.BaseAvp;
import com.baypackets.ase.ra.diameter.common.enums.StandardEnum;
import com.baypackets.ase.ra.diameter.common.exception.ValidationRecord;
import com.baypackets.ase.ra.diameter.rf.RfMessage;
import com.baypackets.ase.ra.diameter.rf.RfResponse;
import com.baypackets.ase.ra.diameter.rf.avp.*;
import com.baypackets.ase.ra.diameter.rf.impl.RfResourceAdaptorFactory;
import com.baypackets.ase.ra.diameter.rf.impl.RfSession;
import com.baypackets.ase.ra.diameter.ro.RoResourceException;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.spi.container.SasMessageContext;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.coding.DiameterAnswer;
import com.traffix.openblox.diameter.coding.DiameterAvp;
import com.traffix.openblox.diameter.rf.generated.avp.*;

public abstract class RfAbstractResponse extends RfMessage implements RfResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(RfAbstractResponse.class);
	private static int count = 0;
	private int id;
	private boolean isReplicated = false ;
	// TODO need to add support for ShSession and StackSession.
	DiameterAnswer stackObj;
	private RfSession m_session;

	public RfAbstractResponse(int type) {
		super(type);
		System.out.println("Inside ShAbstractRequest constructor ");
	}

	public RfAbstractResponse(RfSession session){
		super();
		this.m_session=session;
	}

	public RfAbstractResponse(DiameterAnswer stkObj){
		super();
		this.stackObj=stkObj;
	}
	
	public int getId() {
		if (this.id == -1) {
			this.id = generateId();
		}
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	private static int generateId() {
		if (count >= Integer.MAX_VALUE) {
			count = 0;
		}
		return count++;

	}

	public void isAlreadyReplicated(boolean isReplicated) {
		this.isReplicated = isReplicated;
	}

	void setStackObject(DiameterAnswer stkObj){
		this.stackObj=stkObj;
	}

	DiameterAnswer getStackObject(){
		return this.stackObj;
	}

	/**
	 * Overridden from AbstractSasMessage to provide the index
	 * of the worker thread queue to enqueue this message in.  The
	 * value returned is a hash of the sessionId of stack object.
	 */
	public int getWorkQueue() {
		int index=-1;
		if(stackObj!=null){
			try {
				String sessionId=stackObj.getSessionId();
				if(sessionId!=null)
					index=sessionId.hashCode();
				if(logger.isDebugEnabled())
					logger.debug("getWorkQueue : hashcode of sessionId:" + index);
			} catch (ValidationException e) {
				logger.error("NULL sessionId for RfResponse so using index:"+index);
			}
		}else{
			logger.error("NULL stack object for RfResponse so using index:"+index);
		}
		return index;
	}
	
	public void send() throws IOException {
		logger.debug("RfAbstractResponse send() called.");
		SasMessageContext context = this.getMessageContext();
		if(context != null){
			context.sendMessage(this);
		} else {
			logger.info("Send to RF resource adaptor directly.");
			try {
				RfResourceAdaptorFactory.getResourceAdaptor().sendMessage(this);
			} catch (Exception e) {
				logger.error("send(): " ,e);
				throw new IOException(e.getMessage());
			}
		}
	}
	
	//	//
	//	//	public void setSubscriptionType(int subscriptionType)
	//	//			throws ShResourceException {
	//	//		throw new ShResourceException("Operation is not allowed");
	//	//	}
	//


	///////////////////////////////////////////////////////////////////////////////////
	///////// com.baypackets.ase.ra.sh.diameterbase.message.BaseMessage API starts ////
	///////////////////////////////////////////////////////////////////////////////////	

	//	public boolean equals(java.lang.Object obj){
	//		return false;
	//	}

	public long getApplicationId(){
		return stackObj.getApplicationId();
	}

	public byte[] getByteArray(){
		return stackObj.getByteArray();
	}

	public int getCommandCode(){
		return stackObj.getCommandCode();
	}

	public java.lang.String getDestinationHost(){
		String destHost=null;
		try {
			destHost=stackObj.getDestinationHost();
		} catch (ValidationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return destHost;
	}

	//	public Peer getDestinationPeer();

	/// TODO unable to resolve
	//	public int getHeaderLength(){
	//		
	//	}

	public long getHopIdentifier(){
		return stackObj.getHopByHopIdentifier();
	}

	public int getMessageLength(){
		return stackObj.getMessageLength();
	}

	public String getName(){
		return stackObj.getName();
	}

	/// TODO unable to resolve
	//	public int getOffset(){
	//		return stackObj.ge
	//	}

	//public Peer getOriginPeer();

	public String getSessionId(){
		String sessionId=null;
		try {
			sessionId=stackObj.getSessionId();
		} catch (ValidationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sessionId;
	}

	public StandardEnum getStandard(){
		return StandardEnum.getContainerObj(stackObj.getStandard());
	}

	//Should be implemented by the last class in hierarchy
	//	public void readExternal(java.io.ObjectInput input){
	//		
	//	}

	// unable to resolve
	//	public void resetIdentifier(){
	//		return stackObj.res
	//	}

	// unable to resolve
	//	public void resetIdentifier(long identifier){
	//		
	//	}

	//	public void setDestinationPeer(Peer destinationPeer);

	//	public void setOriginPeer(Peer originPeer);

	public void toXML(java.lang.StringBuilder builder){
		stackObj.toXML(builder);
	}

	public ValidationRecord validate(){
		return new ValidationRecordImpl(stackObj.validate());
	}

	///unable to resolve
	//	public void write(java.nio.ByteBuffer otherBuffer){
	//		stackObj.w
	//	}

	// unable to resolve
	//	public void writeExternal(java.io.ObjectOutput output){
	//		
	//	}


	///////////////////////////////////////////////////////////////////////////////////
	///////// com.baypackets.ase.ra.sh.diameterbase.message.BaseMessage API ends //////
	///////////////////////////////////////////////////////////////////////////////////	


	///////////////////////////////////////////////////////////////////////////////////
	// com.baypackets.ase.ra.sh.diameterbase.message.BaseDiameterMessage API starts ///
	///////////////////////////////////////////////////////////////////////////////////	

	//	public void addAvp(AvpDiameter avp){
	//		
	//	}

	/**
	 * Returns set of Application-Ids taken from the message avps
	 * @return
	 */
	//public java.util.Set<ApplicationId> getApplicationIdSet();

	public AvpDiameter getAvp(int avpCode){
		DiameterAvp stackAvp = stackObj.getAvp(avpCode);
		return createContainerAvp(stackAvp);
	}

	public AvpDiameter getAvp(int avpCode, long vendorId){
		DiameterAvp stackAvp = stackObj.getAvp(avpCode,vendorId);
		return createContainerAvp(stackAvp);
	}

	public java.util.List<? extends BaseAvp> getAvpList(){
		List containerList= new ArrayList<AvpDiameter>();
		List stackList=stackObj.getAvpList();
		Iterator<?> itr = stackList.iterator();
		while(itr.hasNext()){
			Object obj = itr.next();
			if(obj instanceof DiameterAvp){
				containerList.add(createContainerAvp((DiameterAvp)obj));
			}
		}
		return containerList;
	}

	public java.util.List<AvpDiameter> getAvpList(int avpCode){
		List containerList= new ArrayList<AvpDiameter>();
		List stackList=stackObj.getAvpList(avpCode);
		Iterator<?> itr = stackList.iterator();
		while(itr.hasNext()){
			Object obj = itr.next();
			if(obj instanceof DiameterAvp){
				containerList.add(createContainerAvp((DiameterAvp)obj));
			}
		}
		return containerList;
	}

	public java.util.List<AvpDiameter> getAvpList(int avpCode, long vendorId){
		List containerList= new ArrayList<AvpDiameter>();
		List stackList=stackObj.getAvpList(avpCode,vendorId);
		Iterator<?> itr = stackList.iterator();
		while(itr.hasNext()){
			Object obj = itr.next();
			if(obj instanceof DiameterAvp){
				containerList.add(createContainerAvp((DiameterAvp)obj));
			}
		}
		return containerList;
	}

	public java.util.List<AvpDiameter> getAvpList(long vendorId){
		List containerList= new ArrayList<AvpDiameter>();
		List stackList=stackObj.getAvpList(vendorId);
		Iterator<?> itr = stackList.iterator();
		while(itr.hasNext()){
			Object obj = itr.next();
			if(obj instanceof DiameterAvp){
				containerList.add(createContainerAvp((DiameterAvp)obj));
			}
		}
		return containerList;
	}

	public java.util.List<AvpDiameter> getAvps(){
		List containerList= new ArrayList<AvpDiameter>();
		List stackList=stackObj.getAvps();
		Iterator<?> itr = stackList.iterator();
		while(itr.hasNext()){
			Object obj = itr.next();
			if(obj instanceof DiameterAvp){
				containerList.add(createContainerAvp((DiameterAvp)obj));
			}
		}
		return containerList;
	}
	//public AvpSet getAvpSet();

	/**
	 * The End-to-End Identifier is an unsigned 32-bit integer field (in network byte order); 
	 * and is used to detect duplicate messages.
	 * @return
	 */
	public long getEndToEndIdentifier(){
		return stackObj.getEndToEndIdentifier();
	}

	/**
	 * The Hop-by-Hop Identifier is an unsigned 32-bit integer field (in network byte order);
	 * and aids in matching requests and replies.
	 * @return
	 */
	public long getHopByHopIdentifier(){
		return stackObj.getHopByHopIdentifier();
	}

	//public java.util.Set<InbandSecurityId> getInbandSecurityIdSet();

	public java.lang.String getOriginHost() throws ResourceException {
		try {
			return stackObj.getOriginHost();
		} catch (ValidationException e) {
			throw new ResourceException("Exception in getOriginHost ",e);
		}
	}

	public java.lang.String getOriginRealm() throws ResourceException {
		try {
			return stackObj.getOriginRealm();
		} catch (ValidationException e) {
			throw new ResourceException("Exception in getOriginRealm ",e);
		}
	}

	public java.util.List<AvpDiameter> getVendorIdAvps(long vendorId){
		// TODO need to write instanceof check for all the AVP classes.
		return null;
	}

	/**
	 * Adding a vendor specific avp to the message using the DiameterVendorSpecificAvpSet 
	 * object.
	 * @return
	 * @throws RoResourceException 
	 */
	public AvpDiameterVendorSpecificSet getVendorSpecificAvpSet(){
		if(logger.isDebugEnabled()){
			logger.debug("Inside getVendorSpecificAvpSet()");
		}
		return new AvpDiameterVendorSpecificSet(stackObj.getVendorSpecificAvpSet());
	}
	
	/**
	 * Adding a vendor specific avp to the message using the DiameterVendorSpecificAvpSet 
	 * object.
	 * @return
	 */
	//public DiameterVendorSpecificAvpSet getVendorSpecificAvpSet();

	/**
	 * Adding a vendor specific avp to the message using the DiameterVendorSpecificAvpSetNE object.
	 * @return
	 */
	//public DiameterVendorSpecificAvpSetNE getVendorSpecificAvpSetNe();

	public byte getVersion(){
		return stackObj.getVersion();
	}

	public boolean isError(){
		return stackObj.isError();
	}

	public boolean isProxiable(){
		return stackObj.isProxiable();
	}

	public boolean isRequest(){
		return stackObj.isRequest();
	}

	public boolean isReTransmitted(){
		return stackObj.isReTransmitted();
	}

	public void setReTransmitted(boolean value){
		stackObj.setReTransmitted(value);
	}

	/*
	public java.lang.String toString(){
		// TODO
		return null;
	}
	*/

	///////////////////////////////////////////////////////////////////////////////////
	// com.baypackets.ase.ra.sh.diameterbase.message.BaseDiameterMessage API ends /////
	///////////////////////////////////////////////////////////////////////////////////	


	///////////////////////////////////////////////////////////////////////////////////
	// com.baypackets.ase.ra.sh.diameterbase.message.BaseDiameterResponse API starts //
	///////////////////////////////////////////////////////////////////////////////////	

	//	public BaseDiameterRequest getRequest(){
	//		// TODO 
	//		return null;
	//	}

	public boolean isPerformFailover(){
		return stackObj.isPerformFailover();
	}

	public void setPerformFailover(boolean performFailover){
		stackObj.setPerformFailover(performFailover);
	}

	///////////////////////////////////////////////////////////////////////////////////
	// com.baypackets.ase.ra.sh.diameterbase.message.BaseDiameterResponse API ends  ////
	///////////////////////////////////////////////////////////////////////////////////

	public AvpDiameter createContainerAvp(DiameterAvp stackAvp){
		AvpDiameter containerObj=null;

		if(stackAvp instanceof AvpAccessNetworkChargingIdentifierValue){
			containerObj = new  AccessNetworkChargingIdentifierValueAvp((AvpAccessNetworkChargingIdentifierValue) stackAvp);
		}else if(stackAvp instanceof AvpAccessNetworkInformation){
			containerObj = new  AccessNetworkInformationAvp((AvpAccessNetworkInformation) stackAvp);
		}else if(stackAvp instanceof AvpAccountingApplicationId){
			containerObj = new  AccountingApplicationIdAvp((AvpAccountingApplicationId) stackAvp);
		}else if(stackAvp instanceof AvpAccountingInterimInterval){
			containerObj = new  AccountingInterimIntervalAvp((AvpAccountingInterimInterval) stackAvp);
		}else if(stackAvp instanceof AvpAccountingRecordNumber){
			containerObj = new  AccountingRecordNumberAvp((AvpAccountingRecordNumber) stackAvp);
		}else if(stackAvp instanceof AvpAccountingRecordType){
			containerObj = new  AccountingRecordTypeAvp((AvpAccountingRecordType) stackAvp);
		}else if(stackAvp instanceof AvpAccumulatedCost){
			containerObj = new  AccumulatedCostAvp((AvpAccumulatedCost) stackAvp);
		}else if(stackAvp instanceof AvpAdaptations){
			containerObj = new  AdaptationsAvp((AvpAdaptations) stackAvp);
		}else if(stackAvp instanceof AvpAdditionalContentInformation){
			containerObj = new  AdditionalContentInformationAvp((AvpAdditionalContentInformation) stackAvp);
		}else if(stackAvp instanceof AvpAdditionalTypeInformation){
			containerObj = new  AdditionalTypeInformationAvp((AvpAdditionalTypeInformation) stackAvp);
		}else if(stackAvp instanceof AvpAddressData){
			containerObj = new  AddressDataAvp((AvpAddressData) stackAvp);
		}else if(stackAvp instanceof AvpAddressDomain){
			containerObj = new  AddressDomainAvp((AvpAddressDomain) stackAvp);
		}else if(stackAvp instanceof AvpAddressType){
			containerObj = new  AddressTypeAvp((AvpAddressType) stackAvp);
		}else if(stackAvp instanceof AvpAlternateChargedPartyAddress){
			containerObj = new  AlternateChargedPartyAddressAvp((AvpAlternateChargedPartyAddress) stackAvp);
		}else if(stackAvp instanceof AvpAoCCostInformation){
			containerObj = new  AoCCostInformationAvp((AvpAoCCostInformation) stackAvp);
		}else if(stackAvp instanceof AvpAoCInformation){
			containerObj = new  AoCInformationAvp((AvpAoCInformation) stackAvp);
		}else if(stackAvp instanceof AvpApplicID){
			containerObj = new  ApplicIDAvp((AvpApplicID) stackAvp);
		}else if(stackAvp instanceof AvpApplicationProvidedCalledPartyAddress){
			containerObj = new  ApplicationProvidedCalledPartyAddressAvp((AvpApplicationProvidedCalledPartyAddress) stackAvp);
		}else if(stackAvp instanceof AvpApplicationServer){
			containerObj = new  ApplicationServerAvp((AvpApplicationServer) stackAvp);
		}else if(stackAvp instanceof AvpApplicationServerInformation){
			containerObj = new  ApplicationServerInformationAvp((AvpApplicationServerInformation) stackAvp);
		}else if(stackAvp instanceof AvpAssociatedURI){
			containerObj = new  AssociatedURIAvp((AvpAssociatedURI) stackAvp);
		}else if(stackAvp instanceof AvpAuthorizedQoS){
			containerObj = new  AuthorisedQoSAvp((AvpAuthorisedQoS) stackAvp);
		}else if(stackAvp instanceof AvpAuxApplicInfo){
			containerObj = new  AuxApplicInfoAvp((AvpAuxApplicInfo) stackAvp);
		}else if(stackAvp instanceof AvpBearerService){
			containerObj = new  BearerServiceAvp((AvpBearerService) stackAvp);
		}else if(stackAvp instanceof AvpCCUnitType){
			containerObj = new  CCUnitTypeAvp((AvpCCUnitType) stackAvp);
		}else if(stackAvp instanceof AvpCalledAssertedIdentity){
			containerObj = new  CalledAssertedIdentityAvp((AvpCalledAssertedIdentity) stackAvp);
		}else if(stackAvp instanceof AvpCalledPartyAddress){
			containerObj = new  CalledPartyAddressAvp((AvpCalledPartyAddress) stackAvp);
		}else if(stackAvp instanceof AvpCallingPartyAddress){
			containerObj = new  CallingPartyAddressAvp((AvpCallingPartyAddress) stackAvp);
		}else if(stackAvp instanceof AvpCarrierSelectRoutingInformation){
			containerObj = new  CarrierSelectRoutingInformationAvp((AvpCarrierSelectRoutingInformation) stackAvp);
		}else if(stackAvp instanceof AvpCauseCode){
			containerObj = new  CauseCodeAvp((AvpCauseCode) stackAvp);
		}else if(stackAvp instanceof Avp3GPPChargingId){
			containerObj = new  ChargingId3GPPAvp((Avp3GPPChargingId) stackAvp);
		}else if(stackAvp instanceof AvpClassIdentifier){
			containerObj = new  ClassIdentifierAvp((AvpClassIdentifier) stackAvp);
		}else if(stackAvp instanceof AvpContentClass){
			containerObj = new  ContentClassAvp((AvpContentClass) stackAvp);
		}else if(stackAvp instanceof AvpContentDisposition){
			containerObj = new  ContentDispositionAvp((AvpContentDisposition) stackAvp);
		}else if(stackAvp instanceof AvpContentID){
			containerObj = new  ContentIDAvp((AvpContentID) stackAvp);
		}else if(stackAvp instanceof AvpContentLength){
			containerObj = new  ContentLengthAvp((AvpContentLength) stackAvp);
		}else if(stackAvp instanceof AvpContentProviderID){
			containerObj = new  ContentProviderIDAvp((AvpContentProviderID) stackAvp);
		}else if(stackAvp instanceof AvpContentSize){
			containerObj = new  ContentSizeAvp((AvpContentSize) stackAvp);
		}else if(stackAvp instanceof AvpContentType){
			containerObj = new  ContentTypeAvp((AvpContentType) stackAvp);
		}else if(stackAvp instanceof AvpCurrencyCode){
			containerObj = new  CurrencyCodeAvp((AvpCurrencyCode) stackAvp);
		}else if(stackAvp instanceof AvpCurrentTariff){
			containerObj = new  CurrentTariffAvp((AvpCurrentTariff) stackAvp);
		}else if(stackAvp instanceof AvpDCDInformation){
			containerObj = new  DCDInformationAvp((AvpDCDInformation) stackAvp);
		}else if(stackAvp instanceof AvpDRMContent){
			containerObj = new  DRMContentAvp((AvpDRMContent) stackAvp);
		}else if(stackAvp instanceof AvpDeliveryReportRequested){
			containerObj = new  DeliveryReportRequestedAvp((AvpDeliveryReportRequested) stackAvp);
		}else if(stackAvp instanceof AvpErrorReportingHost){
			containerObj = new  ErrorReportingHostAvp((AvpErrorReportingHost) stackAvp);
		}else if(stackAvp instanceof AvpEvent){
			containerObj = new  EventAvp((AvpEvent) stackAvp);
		}else if(stackAvp instanceof AvpEventTimestamp){
			containerObj = new  EventTimestampAvp((AvpEventTimestamp) stackAvp);
		}else if(stackAvp instanceof AvpEventType){
			containerObj = new  EventTypeAvp((AvpEventType) stackAvp);
		}else if(stackAvp instanceof AvpExpires){
			containerObj = new  ExpiresAvp((AvpExpires) stackAvp);
		}else if(stackAvp instanceof AvpExponent){
			containerObj = new  ExponentAvp((AvpExponent) stackAvp);
		}else if(stackAvp instanceof AvpIMSChargingIdentifier){
			containerObj = new  IMSChargingIdentifierAvp((AvpIMSChargingIdentifier) stackAvp);
		}else if(stackAvp instanceof AvpIMSCommunicationServiceIdentifier){
			containerObj = new  IMSCommunicationServiceIdentifierAvp((AvpIMSCommunicationServiceIdentifier) stackAvp);
		}else if(stackAvp instanceof AvpIMSInformation){
			containerObj = new  IMSInformationAvp((AvpIMSInformation) stackAvp);
		}else if(stackAvp instanceof AvpIncomingTrunkGroupId){
			containerObj = new  IncomingTrunkGroupIdAvp((AvpIncomingTrunkGroupId) stackAvp);
		}else if(stackAvp instanceof AvpIncrementalCost){
			containerObj = new  IncrementalCostAvp((AvpIncrementalCost) stackAvp);
		}else if(stackAvp instanceof AvpInterOperatorIdentifier){
			containerObj = new  InterOperatorIdentifierAvp((AvpInterOperatorIdentifier) stackAvp);
		}else if(stackAvp instanceof AvpLCSInformation){
			containerObj = new  LCSInformationAvp((AvpLCSInformation) stackAvp);
		}else if(stackAvp instanceof AvpMMBoxStorageRequested){
			containerObj = new  MMBoxStorageRequestedAvp((AvpMMBoxStorageRequested) stackAvp);
		}else if(stackAvp instanceof AvpMMContentType){
			containerObj = new  MMContentTypeAvp((AvpMMContentType) stackAvp);
		}else if(stackAvp instanceof AvpMMSInformation){
			containerObj = new  MMSInformationAvp((AvpMMSInformation) stackAvp);
		}else if(stackAvp instanceof AvpMandatoryCapability){
			containerObj = new  MandatoryCapabilityAvp((AvpMandatoryCapability) stackAvp);
		}else if(stackAvp instanceof AvpMediaInitiatorFlag){
			containerObj = new  MediaInitiatorFlagAvp((AvpMediaInitiatorFlag) stackAvp);
		}else if(stackAvp instanceof AvpMediaInitiatorParty){
			containerObj = new  MediaInitiatorPartyAvp((AvpMediaInitiatorParty) stackAvp);
		}else if(stackAvp instanceof AvpMessageBody){
			containerObj = new  MessageBodyAvp((AvpMessageBody) stackAvp);
		}else if(stackAvp instanceof AvpMessageClass){
			containerObj = new  MessageClassAvp((AvpMessageClass) stackAvp);
		}else if(stackAvp instanceof AvpMessageID){
			containerObj = new  MessageIDAvp((AvpMessageID) stackAvp);
		}else if(stackAvp instanceof AvpMessageSize){
			containerObj = new  MessageSizeAvp((AvpMessageSize) stackAvp);
		}else if(stackAvp instanceof AvpMessageType){
			containerObj = new  MessageTypeAvp((AvpMessageType) stackAvp);
		}else if(stackAvp instanceof AvpNextTariff){
			containerObj = new  NextTariffAvp((AvpNextTariff) stackAvp);
		}else if(stackAvp instanceof AvpNodeFunctionality){
			containerObj = new  NodeFunctionalityAvp((AvpNodeFunctionality) stackAvp);
		}else if(stackAvp instanceof AvpNumberPortabilityRoutingInformation){
			containerObj = new  NumberPortabilityRoutingInformationAvp((AvpNumberPortabilityRoutingInformation) stackAvp);
		}else if(stackAvp instanceof AvpOnlineChargingFlag){
			containerObj = new  OnlineChargingFlagAvp((AvpOnlineChargingFlag) stackAvp);
		}else if(stackAvp instanceof AvpOptionalCapability){
			containerObj = new  OptionalCapabilityAvp((AvpOptionalCapability) stackAvp);
		}else if(stackAvp instanceof AvpOriginStateId){
			containerObj = new  OriginStateIdAvp((AvpOriginStateId) stackAvp);
		}else if(stackAvp instanceof AvpOriginatingIOI){
			containerObj = new  OriginatingIOIAvp((AvpOriginatingIOI) stackAvp);
		}else if(stackAvp instanceof AvpOriginatorAddress){
			containerObj = new  OriginatorAddressAvp((AvpOriginatorAddress) stackAvp);
		}else if(stackAvp instanceof AvpOriginator){
			containerObj = new  OriginatorAvp((AvpOriginator) stackAvp);
		}else if(stackAvp instanceof AvpOutgoingTrunkGroupId){
			containerObj = new  OutgoingTrunkGroupIdAvp((AvpOutgoingTrunkGroupId) stackAvp);
		}else if(stackAvp instanceof AvpPriority){
			containerObj = new  PriorityAvp((AvpPriority) stackAvp);
		}else if(stackAvp instanceof AvpProxyInfo){
			containerObj = new  ProxyInfoAvp((AvpProxyInfo) stackAvp);
		}else if(stackAvp instanceof AvpRateElement){
			containerObj = new  RateElementAvp((AvpRateElement) stackAvp);
		}else if(stackAvp instanceof AvpReadReplyReportRequested){
			containerObj = new  ReadReplyReportRequestedAvp((AvpReadReplyReportRequested) stackAvp);
		}else if(stackAvp instanceof AvpRealTimeTariffInformation){
			containerObj = new  RealTimeTariffInformationAvp((AvpRealTimeTariffInformation) stackAvp);
		}else if(stackAvp instanceof AvpReplyApplicID){
			containerObj = new  ReplyApplicIDAvp((AvpReplyApplicID) stackAvp);
		}else if(stackAvp instanceof AvpRequestedPartyAddress){
			containerObj = new  RequestedPartyAddressAvp((AvpRequestedPartyAddress) stackAvp);
		}else if(stackAvp instanceof AvpRoleOfNode){
			containerObj = new  RoleOfNodeAvp((AvpRoleOfNode) stackAvp);
		}else if(stackAvp instanceof AvpRouteRecord){
			containerObj = new  RouteRecordAvp((AvpRouteRecord) stackAvp);
		}else if(stackAvp instanceof AvpSDPMediaComponent){
			containerObj = new  SDPMediaComponentAvp((AvpSDPMediaComponent) stackAvp);
		}else if(stackAvp instanceof AvpSDPMediaDescription){
			containerObj = new  SDPMediaDescriptionAvp((AvpSDPMediaDescription) stackAvp);
		}else if(stackAvp instanceof AvpSDPMediaName){
			containerObj = new  SDPMediaNameAvp((AvpSDPMediaName) stackAvp);
		}else if(stackAvp instanceof AvpSDPSessionDescription){
			containerObj = new  SDPSessionDescriptionAvp((AvpSDPSessionDescription) stackAvp);
		}else if(stackAvp instanceof AvpSDPType){
			containerObj = new  SDPTypeAvp((AvpSDPType) stackAvp);
		}else if(stackAvp instanceof AvpSIPMethod){
			containerObj = new  SIPMethodAvp((AvpSIPMethod) stackAvp);
		}else if(stackAvp instanceof AvpSIPRequestTimestamp){
			containerObj = new  SIPRequestTimestampAvp((AvpSIPRequestTimestamp) stackAvp);
		}else if(stackAvp instanceof AvpSIPRequestTimestampFraction){
			containerObj = new  SIPRequestTimestampFractionAvp((AvpSIPRequestTimestampFraction) stackAvp);
		}else if(stackAvp instanceof AvpSIPResponseTimestamp){
			containerObj = new  SIPResponseTimestampAvp((AvpSIPResponseTimestamp) stackAvp);
		}else if(stackAvp instanceof AvpSIPResponseTimestampFraction){
			containerObj = new  SIPResponseTimestampFractionAvp((AvpSIPResponseTimestampFraction) stackAvp);
		}else if(stackAvp instanceof AvpScaleFactor){
			containerObj = new  ScaleFactorAvp((AvpScaleFactor) stackAvp);
		}else if(stackAvp instanceof AvpServedPartyIPAddress){
			containerObj = new  ServedPartyIPAddressAvp((AvpServedPartyIPAddress) stackAvp);
		}else if(stackAvp instanceof AvpServerCapabilities){
			containerObj = new  ServerCapabilitiesAvp((AvpServerCapabilities) stackAvp);
		}else if(stackAvp instanceof AvpServerName){
			containerObj = new  ServerNameAvp((AvpServerName) stackAvp);
		}else if(stackAvp instanceof AvpServiceContextId){
			containerObj = new  ServiceContextIdAvp((AvpServiceContextId) stackAvp);
		}else if(stackAvp instanceof AvpServiceId){
			containerObj = new  ServiceIdAvp((AvpServiceId) stackAvp);
		}else if(stackAvp instanceof AvpServiceInformation){
			containerObj = new  ServiceInformationAvp((AvpServiceInformation) stackAvp);
		}else if(stackAvp instanceof AvpServiceSpecificData){
			containerObj = new  ServiceSpecificDataAvp((AvpServiceSpecificData) stackAvp);
		}else if(stackAvp instanceof AvpServiceSpecificInfo){
			containerObj = new  ServiceSpecificInfoAvp((AvpServiceSpecificInfo) stackAvp);
		}else if(stackAvp instanceof AvpServiceSpecificType){
			containerObj = new  ServiceSpecificTypeAvp((AvpServiceSpecificType) stackAvp);
		}else if(stackAvp instanceof AvpSubmissionTime){
			containerObj = new  SubmissionTimeAvp((AvpSubmissionTime) stackAvp);
		}else if(stackAvp instanceof AvpTariffInformation){
			containerObj = new  TariffInformationAvp((AvpTariffInformation) stackAvp);
		}else if(stackAvp instanceof AvpTariffTimeChange){
			containerObj = new  TariffTimeChangeAvp((AvpTariffTimeChange) stackAvp);
		}else if(stackAvp instanceof AvpTariffXML){
			containerObj = new  TariffXMLAvp((AvpTariffXML) stackAvp);
		}else if(stackAvp instanceof AvpTerminatingIOI){
			containerObj = new  TerminatingIOIAvp((AvpTerminatingIOI) stackAvp);
		}else if(stackAvp instanceof AvpTimeStamps){
			containerObj = new  TimeStampsAvp((AvpTimeStamps) stackAvp);
		}else if(stackAvp instanceof AvpTokenText){
			containerObj = new  TokenTextAvp((AvpTokenText) stackAvp);
		}else if(stackAvp instanceof AvpTrunkGroupId){
			containerObj = new  TrunkGroupIdAvp((AvpTrunkGroupId) stackAvp);
		}else if(stackAvp instanceof AvpTypeNumber){
			containerObj = new  TypeNumberAvp((AvpTypeNumber) stackAvp);
		}else if(stackAvp instanceof AvpUnitCost){
			containerObj = new  UnitCostAvp((AvpUnitCost) stackAvp);
		}else if(stackAvp instanceof AvpUnitQuotaThreshold){
			containerObj = new  UnitQuotaThresholdAvp((AvpUnitQuotaThreshold) stackAvp);
		}else if(stackAvp instanceof AvpUnitValue){
			containerObj = new  UnitValueAvp((AvpUnitValue) stackAvp);
		}else if(stackAvp instanceof AvpUserName){
			containerObj = new  UserNameAvp((AvpUserName) stackAvp);
		}else if(stackAvp instanceof AvpUserSessionId){
			containerObj = new  UserSessionIdAvp((AvpUserSessionId) stackAvp);
		}else if(stackAvp instanceof AvpVASID){
			containerObj = new  VASIDAvp((AvpVASID) stackAvp);
		}else if(stackAvp instanceof AvpVASPID){
			containerObj = new  VASPIDAvp((AvpVASPID) stackAvp);
		}else if(stackAvp instanceof AvpValueDigits){
			containerObj = new  ValueDigitsAvp((AvpValueDigits) stackAvp);
		}
		return containerObj;
	}
}

