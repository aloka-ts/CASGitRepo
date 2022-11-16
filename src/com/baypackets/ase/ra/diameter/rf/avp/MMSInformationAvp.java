package com.baypackets.ase.ra.diameter.rf.avp;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.baypackets.ase.ra.diameter.rf.RfResourceException;
import com.baypackets.ase.ra.diameter.rf.enums.AdaptationsEnum;
import com.baypackets.ase.ra.diameter.rf.enums.DeliveryReportRequestedEnum;
import com.baypackets.ase.ra.diameter.rf.enums.MessageTypeEnum;
import com.baypackets.ase.ra.diameter.rf.enums.PriorityEnum;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.rf.generated.avp.AvpMMSInformation;
import com.traffix.openblox.diameter.rf.generated.avp.AvpRecipientAddress;
import com.traffix.openblox.diameter.rf.generated.enums.EnumAdaptations;
import com.traffix.openblox.diameter.rf.generated.enums.EnumContentClass;
import com.traffix.openblox.diameter.rf.generated.enums.EnumDRMContent;
import com.traffix.openblox.diameter.rf.generated.enums.EnumDeliveryReportRequested;
import com.traffix.openblox.diameter.rf.generated.enums.EnumMMBoxStorageRequested;
import com.traffix.openblox.diameter.rf.generated.enums.EnumMessageType;
import com.traffix.openblox.diameter.rf.generated.enums.EnumPriority;
import com.traffix.openblox.diameter.rf.generated.enums.EnumReadReplyReportRequested;

public class MMSInformationAvp extends AvpDiameterGrouped {

	private static Logger logger = Logger.getLogger(MMSInformationAvp.class.getName());
	public static final long vendorId = 0L;

	private AvpMMSInformation stackObj;

	public MMSInformationAvp(AvpMMSInformation stkObj){
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
	 *  Adding Adaptations AVP of type Enumerated to the message.
	 */
	public AdaptationsAvp addAdaptations(EnumAdaptations value) throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addAdaptations()");
			}
			return new AdaptationsAvp(stackObj.addAdaptations(value));
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in addAdaptations",e);
		}
	}

	/**
	 *  Adding ApplicID AVP of type UTF8String to the message.
	 */
	public ApplicIDAvp addApplicID(java.lang.String value) throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addApplicID()");
			}
			return new ApplicIDAvp(stackObj.addApplicID(value));
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in addApplicID",e);
		}
	}

	/**
	 *  Adding AuxApplicInfo AVP of type UTF8String to the message.
	 */
	public AuxApplicInfoAvp addAuxApplicInfo(java.lang.String value) throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addAuxApplicInfo()");
			}
			return new AuxApplicInfoAvp(stackObj.addAuxApplicInfo(value));
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in addAuxApplicInfo",e);
		}
	}

	/**
	 *  Adding ContentClass AVP of type Enumerated to the message.
	 */
	public ContentClassAvp addContentClass(EnumContentClass value) throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addContentClass()");
			}
			return new ContentClassAvp(stackObj.addContentClass(value));
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in addContentClass",e);
		}
	}

	/**
	 *  Adding DeliveryReportRequested AVP of type Enumerated to the message.
	 */
	public DeliveryReportRequestedAvp addDeliveryReportRequested(EnumDeliveryReportRequested value) throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addDeliveryReportRequested()");
			}
			return new DeliveryReportRequestedAvp(stackObj.addDeliveryReportRequested(value));
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in addDeliveryReportRequested",e);
		}
	}

	/**
	 *  Adding DRMContent AVP of type Enumerated to the message.
	 */
	public DRMContentAvp addDRMContent(EnumDRMContent value) throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addDRMContent()");
			}
			return new DRMContentAvp(stackObj.addDRMContent(value));
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in addDRMContent",e);
		}
	}

	/**
	 *  Adding MessageClass AVP of type Grouped to the message.
	 */
	public MessageClassAvp addGroupedMessageClass() throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedMessageClass()");
			}
			return new MessageClassAvp(stackObj.addGroupedMessageClass());
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in addGroupedMessageClass",e);
		}
	}

	/**
	 *  Adding MMContentType AVP of type Grouped to the message.
	 */
	public MMContentTypeAvp addGroupedMMContentType() throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedMMContentType()");
			}
			return new MMContentTypeAvp(stackObj.addGroupedMMContentType());
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in addGroupedMMContentType",e);
		}
	}

	/**
	 *  Adding OriginatorAddress AVP of type Grouped to the message.
	 */
	public OriginatorAddressAvp addGroupedOriginatorAddress() throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedOriginatorAddress()");
			}
			return new OriginatorAddressAvp(stackObj.addGroupedOriginatorAddress());
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in addGroupedOriginatorAddress",e);
		}
	}

	//TODO
//	/**
//	 *  Adding RecipientAddress AVP of type Grouped to the message.
//	 */
//	public RecipientAddressAvp addGroupedRecipientAddress() throws RfResourceException { 
//
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside addGroupedRecipientAddress()");
//			}
//			return new RecipientAddressAvp(stackObj.addGroupedRecipientAddress());
//		} catch (ValidationException e) {
//			throw new RfResourceException("Exception in addGroupedRecipientAddress",e);
//		}
//	}

	/**
	 *  Adding MessageID AVP of type UTF8String to the message.
	 */
	public MessageIDAvp addMessageID(java.lang.String value) throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addMessageID()");
			}
			return new MessageIDAvp(stackObj.addMessageID(value));
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in addMessageID",e);
		}
	}

	/**
	 *  Adding MessageSize AVP of type Unsigned32 to the message.
	 */
	public MessageSizeAvp addMessageSize(long value) throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addMessageSize()");
			}
			return new MessageSizeAvp(stackObj.addMessageSize(value));
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in addMessageSize",e);
		}
	}

	/**
	 *  Adding MessageType AVP of type Enumerated to the message.
	 */
	public MessageTypeAvp addMessageType(EnumMessageType value) throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addMessageType()");
			}
			return new MessageTypeAvp(stackObj.addMessageType(value));
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in addMessageType",e);
		}
	}

	/**
	 *  Adding MMBoxStorageRequested AVP of type Enumerated to the message.
	 */
	public MMBoxStorageRequestedAvp addMMBoxStorageRequested(EnumMMBoxStorageRequested value) throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addMMBoxStorageRequested()");
			}
			return new MMBoxStorageRequestedAvp(stackObj.addMMBoxStorageRequested(value));
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in addMMBoxStorageRequested",e);
		}
	}

	/**
	 *  Adding Priority AVP of type Enumerated to the message.
	 */
	public PriorityAvp addPriority(EnumPriority value) throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addPriority()");
			}
			return new PriorityAvp(stackObj.addPriority(value));
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in addPriority",e);
		}
	}

	/**
	 *  Adding ReadReplyReportRequested AVP of type Enumerated to the message.
	 */
	public ReadReplyReportRequestedAvp addReadReplyReportRequested(EnumReadReplyReportRequested value) throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addReadReplyReportRequested()");
			}
			return new ReadReplyReportRequestedAvp(stackObj.addReadReplyReportRequested(value));
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in addReadReplyReportRequested",e);
		}
	}

	/**
	 *  Adding ReplyApplicID AVP of type UTF8String to the message.
	 */
	public ReplyApplicIDAvp addReplyApplicID(java.lang.String value) throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addReplyApplicID()");
			}
			return new ReplyApplicIDAvp(stackObj.addReplyApplicID(value));
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in addReplyApplicID",e);
		}
	}

	/**
	 *  Adding SubmissionTime AVP of type Time to the message.
	 */
	public SubmissionTimeAvp addSubmissionTime(java.util.Date value) throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addSubmissionTime()");
			}
			return new SubmissionTimeAvp(stackObj.addSubmissionTime(value));
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in addSubmissionTime",e);
		}
	}

	/**
	 *  Adding VASID AVP of type UTF8String to the message.
	 */
	public VASIDAvp addVASID(java.lang.String value) throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addVASID()");
			}
			return new VASIDAvp(stackObj.addVASID(value));
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in addVASID",e);
		}
	}

	/**
	 *  Adding VASPID AVP of type UTF8String to the message.
	 */
	public VASPIDAvp addVASPID(java.lang.String value) throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addVASPID()");
			}
			return new VASPIDAvp(stackObj.addVASPID(value));
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in addVASPID",e);
		}
	}

	/**
	 *  Retrieving a single Enumerated value from Adaptations AVPs.
	 */
	public int getAdaptations() throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getAdaptations()");
			}
			return stackObj.getAdaptations();
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in getAdaptations",e);
		}
	}

	/**
	 *  Retrieving a single UTF8String value from ApplicID AVPs.
	 */
	public java.lang.String getApplicID() throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getApplicID()");
			}
			return stackObj.getApplicID();
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in getApplicID",e);
		}
	}

	/**
	 *  Retrieving a single UTF8String value from AuxApplicInfo AVPs.
	 */
	public java.lang.String getAuxApplicInfo() throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getAuxApplicInfo()");
			}
			return stackObj.getAuxApplicInfo();
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in getAuxApplicInfo",e);
		}
	}

	/**
	 *  Retrieving a single Enumerated value from ContentClass AVPs.
	 */
	public int getContentClass() throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getContentClass()");
			}
			return stackObj.getContentClass();
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in getContentClass",e);
		}
	}

	/**
	 *  Retrieving a single Enumerated value from DeliveryReportRequested AVPs.
	 */
	public int getDeliveryReportRequested() throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getDeliveryReportRequested()");
			}
			return stackObj.getDeliveryReportRequested();
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in getDeliveryReportRequested",e);
		}
	}

	/**
	 *  Retrieving a single Enumerated value from DRMContent AVPs.
	 */
	public int getDRMContent() throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getDRMContent()");
			}
			return stackObj.getDRMContent();
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in getDRMContent",e);
		}
	}


	/**
	 *  EnumContentClass getEnumContentClass()
	 */
	public AdaptationsEnum getEnumAdaptations() throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getEnumAdaptations()");
			}
			return AdaptationsEnum.getContainerObj(stackObj.getEnumAdaptations());
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in getEnumAdaptations",e);
		}
	}



	/**
	 *  EnumDRMContent getEnumDRMContent()
	 */
	public DeliveryReportRequestedEnum getEnumDeliveryReportRequested() throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getEnumDeliveryReportRequested()");
			}
			return DeliveryReportRequestedEnum.getContainerObj(stackObj.getEnumDeliveryReportRequested());
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in getEnumDeliveryReportRequested",e);
		}
	}



	/**
	 *  EnumMMBoxStorageRequested getEnumMMBoxStorageRequested()
	 */
	public MessageTypeEnum getEnumMessageType() throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getEnumMessageType()");
			}
			return MessageTypeEnum.getContainerObj(stackObj.getEnumMessageType());
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in getEnumMessageType",e);
		}
	}



	/**
	 *  EnumReadReplyReportRequested getEnumReadReplyReportRequested()
	 */
	public PriorityEnum getEnumPriority() throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getEnumPriority()");
			}
			return PriorityEnum.getContainerObj(stackObj.getEnumPriority());
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in getEnumPriority",e);
		}
	}


	/**
	 *  Retrieving a single Grouped value from MessageClass AVPs.
	 */
	public MessageClassAvp getGroupedMessageClass() throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedMessageClass()");
			}
			return new MessageClassAvp(stackObj.getGroupedMessageClass());
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in getGroupedMessageClass",e);
		}
	}

	/**
	 *  Retrieving a single Grouped value from MMContentType AVPs.
	 */
	public MMContentTypeAvp getGroupedMMContentType() throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedMMContentType()");
			}
			return new MMContentTypeAvp(stackObj.getGroupedMMContentType());
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in getGroupedMMContentType",e);
		}
	}

	/**
	 *  Retrieving a single Grouped value from OriginatorAddress AVPs.
	 */
	public OriginatorAddressAvp getGroupedOriginatorAddress() throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedOriginatorAddress()");
			}
			return new OriginatorAddressAvp(stackObj.getGroupedOriginatorAddress());
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in getGroupedOriginatorAddress",e);
		}
	}
//
//	/**
//	 *  Retrieving multiple Grouped values from RecipientAddress AVPs.
//	 */
//	public RecipientAddressAvp[] getGroupedRecipientAddresss() throws RfResourceException { 
//
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getGroupedRecipientAddresss()");
//			}
//			AvpRecipientAddress[] stackAv= stackObj.getGroupedRecipientAddresss();
//			RecipientAddressAvp[] contAvp= new RecipientAddressAvp[stackAv.length];
//			for(int i=0;i<stackAv.length;i++){
//				contAvp[i]=new RecipientAddressAvp(stackAv[i]);
//			}
//			return contAvp;
//		} catch (ValidationException e) {
//			throw new RfResourceException("Exception in getGroupedRecipientAddresss",e);
//		}
//	}

	/**
	 *  Retrieving a single UTF8String value from MessageID AVPs.
	 */
	public java.lang.String getMessageID() throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getMessageID()");
			}
			return stackObj.getMessageID();
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in getMessageID",e);
		}
	}

	/**
	 *  Retrieving a single Unsigned32 value from MessageSize AVPs.
	 */
	public long getMessageSize() throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getMessageSize()");
			}
			return stackObj.getMessageSize();
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in getMessageSize",e);
		}
	}

	/**
	 *  Retrieving a single Enumerated value from MessageType AVPs.
	 */
	public int getMessageType() throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getMessageType()");
			}
			return stackObj.getMessageType();
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in getMessageType",e);
		}
	}

	/**
	 *  Retrieving a single Enumerated value from MMBoxStorageRequested AVPs.
	 */
	public int getMMBoxStorageRequested() throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getMMBoxStorageRequested()");
			}
			return stackObj.getMMBoxStorageRequested();
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in getMMBoxStorageRequested",e);
		}
	}

	/**
	 *  Retrieving a single Enumerated value from ReadReplyReportRequested AVPs.
	 */
	public int getReadReplyReportRequested() throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getReadReplyReportRequested()");
			}
			return stackObj.getReadReplyReportRequested();
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in getReadReplyReportRequested",e);
		}
	}

	/**
	 *  Retrieving a single UTF8String value from ReplyApplicID AVPs.
	 */
	public java.lang.String getReplyApplicID() throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getReplyApplicID()");
			}
			return stackObj.getReplyApplicID();
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in getReplyApplicID",e);
		}
	}

	/**
	 *  Retrieving a single Time value from SubmissionTime AVPs.
	 */
	public java.util.Date getSubmissionTime() throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getSubmissionTime()");
			}
			return stackObj.getSubmissionTime();
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in getSubmissionTime",e);
		}
	}

	/**
	 *  Retrieving a single UTF8String value from VASID AVPs.
	 */
	public java.lang.String getVASID() throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getVASID()");
			}
			return stackObj.getVASID();
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in getVASID",e);
		}
	}

	/**
	 *  Retrieving a single UTF8String value from VASPID AVPs.
	 */
	public java.lang.String getVASPID() throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getVASPID()");
			}
			return stackObj.getVASPID();
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in getVASPID",e);
		}
	}


}