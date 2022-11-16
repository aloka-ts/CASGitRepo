package com.baypackets.ase.ra.diameter.gy.avp;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.baypackets.ase.ra.diameter.gy.GyResourceException;
import com.baypackets.ase.ra.diameter.gy.enums.ReplyPathRequestedEnum;
import com.baypackets.ase.ra.diameter.gy.enums.SMMessageTypeEnum;
import com.baypackets.ase.ra.diameter.gy.enums.SMSNodeEnum;
import com.baypackets.ase.ra.diameter.gy.enums.SMServiceTypeEnum;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.gy.generated.avp.AvpSMSInformation;
import com.traffix.openblox.diameter.gy.generated.avp.AvpRecipientInfo;

public class SMSInformationAvp extends AvpDiameterGrouped {

	private static Logger logger = Logger.getLogger(SMSInformationAvp.class.getName());
	public static final long vendorId = 0L;

	private AvpSMSInformation stackObj;

	public SMSInformationAvp(AvpSMSInformation stkObj){
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
	 *  Adding ClientAddress AVP of type Address to the message.
	 */
	public ClientAddressAvp addClientAddress(byte[] value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addClientAddress()");
			}
			return new ClientAddressAvp(stackObj.addClientAddress(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addClientAddress",e);
		}
	}

	/**
	 *  Adding ClientAddress AVP of type Address to the message.
	 */
	public ClientAddressAvp addClientAddress(java.net.InetAddress value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addClientAddress()");
			}
			return new ClientAddressAvp(stackObj.addClientAddress(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addClientAddress",e);
		}
	}

	/**
	 *  Adding DataCodingScheme AVP of type Integer32 to the message.
	 */
	public DataCodingSchemeAvp addDataCodingScheme(int value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addDataCodingScheme()");
			}
			return new DataCodingSchemeAvp(stackObj.addDataCodingScheme(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addDataCodingScheme",e);
		}
	}

	/**
	 *  Adding OriginatorInterface AVP of type Grouped to the message.
	 */
	public OriginatorInterfaceAvp addGroupedOriginatorInterface( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedOriginatorInterface()");
			}
			return new OriginatorInterfaceAvp(stackObj.addGroupedOriginatorInterface());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGroupedOriginatorInterface",e);
		}
	}

	/**
	 *  Adding OriginatorReceivedAddress AVP of type Grouped to the message.
	 */
//	public OriginatorReceivedAddressAvp addGroupedOriginatorReceivedAddress( ) throws GyResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside addGroupedOriginatorReceivedAddress()");
//			}
//			return new OriginatorReceivedAddressAvp(stackObj.addGroupedOriginatorReceivedAddress());
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in addGroupedOriginatorReceivedAddress",e);
//		}
//	}
//
//	/**
//	 *  Adding RecipientInfo AVP of type Grouped to the message.
//	 */
//	public RecipientInfoAvp addGroupedRecipientInfo( ) throws GyResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside addGroupedRecipientInfo()");
//			}
//			return new RecipientInfoAvp(stackObj.addGroupedRecipientInfo());
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in addGroupedRecipientInfo",e);
//		}
//	}

	/**
	 *  Adding NumberOfMessagesSent AVP of type Unsigned32 to the message.
	 */
	public NumberOfMessagesSentAvp addNumberOfMessagesSent(long value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addNumberOfMessagesSent()");
			}
			return new NumberOfMessagesSentAvp(stackObj.addNumberOfMessagesSent(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addNumberOfMessagesSent",e);
		}
	}

	/**
	 *  Adding OriginatorSCCPAddress AVP of type Address to the message.
	 */
	public OriginatorSCCPAddressAvp addOriginatorSCCPAddress(byte[] value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addOriginatorSCCPAddress()");
			}
			return new OriginatorSCCPAddressAvp(stackObj.addOriginatorSCCPAddress(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addOriginatorSCCPAddress",e);
		}
	}

	/**
	 *  Adding OriginatorSCCPAddress AVP of type Address to the message.
	 */
	public OriginatorSCCPAddressAvp addOriginatorSCCPAddress(java.net.InetAddress value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addOriginatorSCCPAddress()");
			}
			return new OriginatorSCCPAddressAvp(stackObj.addOriginatorSCCPAddress(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addOriginatorSCCPAddress",e);
		}
	}

	/**
	 *  Adding ReplyPathRequested AVP of type Enumerated to the message.
	 */
	public ReplyPathRequestedAvp addReplyPathRequested(ReplyPathRequestedEnum value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addReplyPathRequested()");
			}
			return new ReplyPathRequestedAvp(stackObj.addReplyPathRequested(ReplyPathRequestedEnum.getStackObj(value)));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addReplyPathRequested",e);
		}
	}

	/**
	 *  Adding SMDischargeTime AVP of type Time to the message.
	 */
	public SMDischargeTimeAvp addSMDischargeTime(java.util.Date value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addSMDischargeTime()");
			}
			return new SMDischargeTimeAvp(stackObj.addSMDischargeTime(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addSMDischargeTime",e);
		}
	}

	/**
	 *  Adding SMMessageType AVP of type Enumerated to the message.
	 */
	public SMMessageTypeAvp addSMMessageType(SMMessageTypeEnum value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addSMMessageType()");
			}
			return new SMMessageTypeAvp(stackObj.addSMMessageType(SMMessageTypeEnum.getStackObj(value)));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addSMMessageType",e);
		}
	}

	/**
	 *  Adding SMProtocolID AVP of type OctetString to the message.
	 */
	public SMProtocolIDAvp addSMProtocolID(byte[] value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addSMProtocolID()");
			}
			return new SMProtocolIDAvp(stackObj.addSMProtocolID(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addSMProtocolID",e);
		}
	}

	/**
	 *  Adding SMProtocolID AVP of type OctetString to the message.
	 */
	public SMProtocolIDAvp addSMProtocolID(java.lang.String value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addSMProtocolID()");
			}
			return new SMProtocolIDAvp(stackObj.addSMProtocolID(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addSMProtocolID",e);
		}
	}

	/**
	 *  Adding SMSCAddress AVP of type Address to the message.
	 */
	public SMSCAddressAvp addSMSCAddress(byte[] value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addSMSCAddress()");
			}
			return new SMSCAddressAvp(stackObj.addSMSCAddress(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addSMSCAddress",e);
		}
	}

	/**
	 *  Adding SMSCAddress AVP of type Address to the message.
	 */
	public SMSCAddressAvp addSMSCAddress(java.net.InetAddress value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addSMSCAddress()");
			}
			return new SMSCAddressAvp(stackObj.addSMSCAddress(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addSMSCAddress",e);
		}
	}

	/**
	 *  Adding SMServiceType AVP of type Enumerated to the message.
	 */
//	public SMServiceTypeAvp addSMServiceType(SMServiceTypeEnum value) throws GyResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside addSMServiceType()");
//			}
//			return new SMServiceTypeAvp(stackObj.addSMServiceType(SMServiceTypeEnum.getStackObj(value)));
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in addSMServiceType",e);
//		}
//	}

	/**
	 *  Adding SMSNode AVP of type Enumerated to the message.
	 */
	public SMSNodeAvp addSMSNode(SMSNodeEnum value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addSMSNode()");
			}
			return new SMSNodeAvp(stackObj.addSMSNode(SMSNodeEnum.getStackObj(value)));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addSMSNode",e);
		}
	}

	/**
	 *  Adding SMStatus AVP of type OctetString to the message.
	 */
	public SMStatusAvp addSMStatus(byte[] value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addSMStatus()");
			}
			return new SMStatusAvp(stackObj.addSMStatus(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addSMStatus",e);
		}
	}

	/**
	 *  Adding SMStatus AVP of type OctetString to the message.
	 */
	public SMStatusAvp addSMStatus(java.lang.String value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addSMStatus()");
			}
			return new SMStatusAvp(stackObj.addSMStatus(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addSMStatus",e);
		}
	}

	/**
	 *  Adding SMUserDataHeader AVP of type OctetString to the message.
	 */
	public SMUserDataHeaderAvp addSMUserDataHeader(byte[] value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addSMUserDataHeader()");
			}
			return new SMUserDataHeaderAvp(stackObj.addSMUserDataHeader(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addSMUserDataHeader",e);
		}
	}

	/**
	 *  Adding SMUserDataHeader AVP of type OctetString to the message.
	 */
	public SMUserDataHeaderAvp addSMUserDataHeader(java.lang.String value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addSMUserDataHeader()");
			}
			return new SMUserDataHeaderAvp(stackObj.addSMUserDataHeader(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addSMUserDataHeader",e);
		}
	}

	/**
	 *  Retrieving a single Address value from ClientAddress AVPs.
	 */
	public java.net.InetAddress getClientAddress( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getClientAddress()");
			}
			return stackObj.getClientAddress();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getClientAddress",e);
		}
	}

	/**
	 *  This method returns the enum value corrosponding to ReplyPathRequestedAvp.
	 */
	public ReplyPathRequestedEnum getEnumReplyPathRequested( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getEnumReplyPathRequested()");
			}
			return ReplyPathRequestedEnum.getContainerObj(stackObj.getEnumReplyPathRequested());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getEnumReplyPathRequested",e);
		}
	}

	/**
	 *  This method returns the enum value corrosponding to SMMessageTypeAvp.
	 */
	public SMMessageTypeEnum getEnumSMMessageType( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getEnumSMMessageType()");
			}
			return SMMessageTypeEnum.getContainerObj(stackObj.getEnumSMMessageType());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getEnumSMMessageType",e);
		}
	}

	/**
	 *  This method returns the enum value corrosponding to SMServiceTypeAvp.
	 */
//	public SMServiceTypeEnum getEnumSMServiceType( ) throws GyResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getEnumSMServiceType()");
//			}
//			return SMServiceTypeEnum.getContainerObj(stackObj.getEnumSMServiceType());
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in getEnumSMServiceType",e);
//		}
//	}

	/**
	 *  This method returns the enum value corrosponding to SMSNodeAvp.
	 */
	public SMSNodeEnum getEnumSMSNode( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getEnumSMSNode()");
			}
			return SMSNodeEnum.getContainerObj(stackObj.getEnumSMSNode());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getEnumSMSNode",e);
		}
	}

	/**
	 *  Retrieving a single Grouped value from OriginatorInterface AVPs.
	 */
	public OriginatorInterfaceAvp getGroupedOriginatorInterface( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedOriginatorInterface()");
			}
			return new OriginatorInterfaceAvp(stackObj.getGroupedOriginatorInterface());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGroupedOriginatorInterface",e);
		}
	}

	/**
	 *  Retrieving a single Grouped value from OriginatorReceivedAddress AVPs.
	 */
//	public OriginatorReceivedAddressAvp getGroupedOriginatorReceivedAddress( ) throws GyResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getGroupedOriginatorReceivedAddress()");
//			}
//			return new OriginatorReceivedAddressAvp(stackObj.getGroupedOriginatorReceivedAddress());
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in getGroupedOriginatorReceivedAddress",e);
//		}
//	}
//
//	/**
//	 *  Retrieving multiple Grouped values from RecipientInfo AVPs.
//	 */
//	public RecipientInfoAvp[] getGroupedRecipientInfos( ) throws GyResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getGroupedRecipientInfos()");
//			}
//			AvpRecipientInfo[] stackAv= stackObj.getGroupedRecipientInfos();
//			RecipientInfoAvp[] contAvp= new RecipientInfoAvp[stackAv.length];
//			for(int i=0;i<stackAv.length;i++){
//				contAvp[i]=new RecipientInfoAvp(stackAv[i]);
//			}
//			return contAvp;
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in getGroupedRecipientInfos",e);
//		}
//	}

	/**
	 *  Retrieving a single Unsigned32 value from NumberOfMessagesSent AVPs.
	 */
	public long getNumberOfMessagesSent( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getNumberOfMessagesSent()");
			}
			return stackObj.getNumberOfMessagesSent();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getNumberOfMessagesSent",e);
		}
	}

	/**
	 *  Retrieving a single Address value from OriginatorSCCPAddress AVPs.
	 */
	public java.net.InetAddress getOriginatorSCCPAddress( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getOriginatorSCCPAddress()");
			}
			return stackObj.getOriginatorSCCPAddress();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getOriginatorSCCPAddress",e);
		}
	}

	/**
	 *  Retrieving a single Address value from ClientAddress AVPs.
	 */
	public byte[] getRawClientAddress( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getRawClientAddress()");
			}
			return stackObj.getRawClientAddress();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getRawClientAddress",e);
		}
	}

	/**
	 *  Retrieving a single Address value from OriginatorSCCPAddress AVPs.
	 */
	public byte[] getRawOriginatorSCCPAddress( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getRawOriginatorSCCPAddress()");
			}
			return stackObj.getRawOriginatorSCCPAddress();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getRawOriginatorSCCPAddress",e);
		}
	}

	/**
	 *  Retrieving a single OctetString value from SMProtocolID AVPs.
	 */
	public byte[] getRawSMProtocolID( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getRawSMProtocolID()");
			}
			return stackObj.getRawSMProtocolID();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getRawSMProtocolID",e);
		}
	}

	/**
	 *  Retrieving a single Address value from SMSCAddress AVPs.
	 */
	public byte[] getRawSMSCAddress( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getRawSMSCAddress()");
			}
			return stackObj.getRawSMSCAddress();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getRawSMSCAddress",e);
		}
	}

	/**
	 *  Retrieving a single OctetString value from SMStatus AVPs.
	 */
	public byte[] getRawSMStatus( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getRawSMStatus()");
			}
			return stackObj.getRawSMStatus();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getRawSMStatus",e);
		}
	}

	/**
	 *  Retrieving a single OctetString value from SMUserDataHeader AVPs.
	 */
	public byte[] getRawSMUserDataHeader( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getRawSMUserDataHeader()");
			}
			return stackObj.getRawSMUserDataHeader();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getRawSMUserDataHeader",e);
		}
	}

	/**
	 *  Retrieving a single Enumerated value from ReplyPathRequested AVPs.
	 */
	public int getReplyPathRequested( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getReplyPathRequested()");
			}
			return stackObj.getReplyPathRequested();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getReplyPathRequested",e);
		}
	}

	/**
	 *  Retrieving a single Time value from SMDischargeTime AVPs.
	 */
	public java.util.Date getSMDischargeTime( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getSMDischargeTime()");
			}
			return stackObj.getSMDischargeTime();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getSMDischargeTime",e);
		}
	}

	/**
	 *  Retrieving a single Enumerated value from SMMessageType AVPs.
	 */
	public int getSMMessageType( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getSMMessageType()");
			}
			return stackObj.getSMMessageType();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getSMMessageType",e);
		}
	}

	/**
	 *  Retrieving a single OctetString value from SMProtocolID AVPs.
	 */
	public java.lang.String getSMProtocolID( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getSMProtocolID()");
			}
			return stackObj.getSMProtocolID();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getSMProtocolID",e);
		}
	}

	/**
	 *  Retrieving a single Address value from SMSCAddress AVPs.
	 */
	public java.net.InetAddress getSMSCAddress( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getSMSCAddress()");
			}
			return stackObj.getSMSCAddress();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getSMSCAddress",e);
		}
	}

	/**
	 *  Retrieving a single Enumerated value from SMServiceType AVPs.
	 */
//	public int getSMServiceType( ) throws GyResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getSMServiceType()");
//			}
//			return stackObj.getSMServiceType();
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in getSMServiceType",e);
//		}
//	}

	/**
	 *  Retrieving a single Enumerated value from SMSNode AVPs.
	 */
	public int getSMSNode( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getSMSNode()");
			}
			return stackObj.getSMSNode();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getSMSNode",e);
		}
	}

	/**
	 *  Retrieving a single OctetString value from SMStatus AVPs.
	 */
	public java.lang.String getSMStatus( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getSMStatus()");
			}
			return stackObj.getSMStatus();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getSMStatus",e);
		}
	}

	/**
	 *  Retrieving a single OctetString value from SMUserDataHeader AVPs.
	 */
	public java.lang.String getSMUserDataHeader( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getSMUserDataHeader()");
			}
			return stackObj.getSMUserDataHeader();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getSMUserDataHeader",e);
		}
	}

}