package com.baypackets.ase.ra.diameter.gy.avp;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.baypackets.ase.ra.diameter.gy.GyResourceException;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.gy.generated.avp.AvpServiceDataContainer;

public class ServiceDataContainerAvp extends AvpDiameterGrouped {

	private static Logger logger = Logger.getLogger(ServiceDataContainerAvp.class.getName());
	public static final long vendorId = 0L;

	private AvpServiceDataContainer stackObj;

	public ServiceDataContainerAvp(AvpServiceDataContainer stkObj){
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
	public BSID3GPP2Avp add3GPP2BSID(java.lang.String value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside add3GPP2BSID()");
			}
			return new BSID3GPP2Avp(stackObj.add3GPP2BSID(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in add3GPP2BSID",e);
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
	 *  Adding AccountingInputOctets AVP of type Unsigned64 to the message.
	 */
	public AccountingInputOctetsAvp addAccountingInputOctets(long value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addAccountingInputOctets()");
			}
			return new AccountingInputOctetsAvp(stackObj.addAccountingInputOctets(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addAccountingInputOctets",e);
		}
	}

	/**
	 *  Adding AccountingInputPackets AVP of type Unsigned64 to the message.
	 */
	public AccountingInputPacketsAvp addAccountingInputPackets(long value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addAccountingInputPackets()");
			}
			return new AccountingInputPacketsAvp(stackObj.addAccountingInputPackets(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addAccountingInputPackets",e);
		}
	}

	/**
	 *  Adding AccountingOutputOctets AVP of type Unsigned64 to the message.
	 */
	public AccountingOutputOctetsAvp addAccountingOutputOctets(long value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addAccountingOutputOctets()");
			}
			return new AccountingOutputOctetsAvp(stackObj.addAccountingOutputOctets(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addAccountingOutputOctets",e);
		}
	}

	/**
	 *  Adding AccountingOutputPackets AVP of type Unsigned64 to the message.
	 */
	public AccountingOutputPacketsAvp addAccountingOutputPackets(long value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addAccountingOutputPackets()");
			}
			return new AccountingOutputPacketsAvp(stackObj.addAccountingOutputPackets(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addAccountingOutputPackets",e);
		}
	}

	/**
	 *  Adding ChangeCondition AVP of type Integer32 to the message.
	 */
	public ChangeConditionAvp addChangeCondition(int value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addChangeCondition()");
			}
			return new ChangeConditionAvp(stackObj.addChangeCondition(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addChangeCondition",e);
		}
	}

	/**
	 *  Adding ChangeTime AVP of type Time to the message.
	 */
	public ChangeTimeAvp addChangeTime(java.util.Date value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addChangeTime()");
			}
			return new ChangeTimeAvp(stackObj.addChangeTime(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addChangeTime",e);
		}
	}

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
	 *  Adding AFCorrelationInformation AVP of type Grouped to the message.
	 */
	public AFCorrelationInformationAvp addGroupedAFCorrelationInformation( ) throws GyResourceException {
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
	 *  Adding QoSInformation AVP of type Grouped to the message.
	 */
	public QoSInformationAvp addGroupedQoSInformation( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedQoSInformation()");
			}
			return new QoSInformationAvp(stackObj.addGroupedQoSInformation());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGroupedQoSInformation",e);
		}
	}

	/**
	 *  Adding ServiceSpecificInfo AVP of type Grouped to the message.
	 */
	public ServiceSpecificInfoAvp addGroupedServiceSpecificInfo( ) throws GyResourceException {
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
	 *  Adding LocalSequenceNumber AVP of type Unsigned32 to the message.
	 */
	public LocalSequenceNumberAvp addLocalSequenceNumber(long value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addLocalSequenceNumber()");
			}
			return new LocalSequenceNumberAvp(stackObj.addLocalSequenceNumber(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addLocalSequenceNumber",e);
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
	 *  Adding TimeFirstUsage AVP of type Time to the message.
	 */
	public TimeFirstUsageAvp addTimeFirstUsage(java.util.Date value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addTimeFirstUsage()");
			}
			return new TimeFirstUsageAvp(stackObj.addTimeFirstUsage(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addTimeFirstUsage",e);
		}
	}

	/**
	 *  Adding TimeLastUsage AVP of type Time to the message.
	 */
	public TimeLastUsageAvp addTimeLastUsage(java.util.Date value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addTimeLastUsage()");
			}
			return new TimeLastUsageAvp(stackObj.addTimeLastUsage(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addTimeLastUsage",e);
		}
	}

	/**
	 *  Adding TimeUsage AVP of type Unsigned32 to the message.
	 */
	public TimeUsageAvp addTimeUsage(long value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addTimeUsage()");
			}
			return new TimeUsageAvp(stackObj.addTimeUsage(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addTimeUsage",e);
		}
	}

	/**
	 *  Retrieving a single UTF8String value from 3GPP2BSID AVPs.
	 */
	public java.lang.String get3GPP2BSID( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside get3GPP2BSID()");
			}
			return stackObj.get3GPP2BSID();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in get3GPP2BSID",e);
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
	 *  Retrieving a single Unsigned64 value from AccountingInputOctets AVPs.
	 */
	public long getAccountingInputOctets( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getAccountingInputOctets()");
			}
			return stackObj.getAccountingInputOctets();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getAccountingInputOctets",e);
		}
	}

	/**
	 *  Retrieving a single Unsigned64 value from AccountingInputPackets AVPs.
	 */
	public long getAccountingInputPackets( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getAccountingInputPackets()");
			}
			return stackObj.getAccountingInputPackets();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getAccountingInputPackets",e);
		}
	}

	/**
	 *  Retrieving a single Unsigned64 value from AccountingOutputOctets AVPs.
	 */
	public long getAccountingOutputOctets( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getAccountingOutputOctets()");
			}
			return stackObj.getAccountingOutputOctets();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getAccountingOutputOctets",e);
		}
	}

	/**
	 *  Retrieving a single Unsigned64 value from AccountingOutputPackets AVPs.
	 */
	public long getAccountingOutputPackets( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getAccountingOutputPackets()");
			}
			return stackObj.getAccountingOutputPackets();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getAccountingOutputPackets",e);
		}
	}

	/**
	 *  Retrieving multiple Integer32 values from ChangeCondition AVPs.
	 */
	public int[] getChangeConditions( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getChangeConditions()");
			}
			return stackObj.getChangeConditions();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getChangeConditions",e);
		}
	}

	/**
	 *  Retrieving a single Time value from ChangeTime AVPs.
	 */
	public java.util.Date getChangeTime( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getChangeTime()");
			}
			return stackObj.getChangeTime();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getChangeTime",e);
		}
	}

	/**
	 *  Retrieving a single UTF8String value from ChargingRuleBaseName AVPs.
	 */
	public java.lang.String getChargingRuleBaseName( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getChargingRuleBaseName()");
			}
			return stackObj.getChargingRuleBaseName();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getChargingRuleBaseName",e);
		}
	}

	/**
	 *  Retrieving a single Grouped value from AFCorrelationInformation AVPs.
	 */
	public AFCorrelationInformationAvp getGroupedAFCorrelationInformation( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedAFCorrelationInformation()");
			}
			return new AFCorrelationInformationAvp(stackObj.getGroupedAFCorrelationInformation());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGroupedAFCorrelationInformation",e);
		}
	}

	/**
	 *  Retrieving a single Grouped value from QoSInformation AVPs.
	 */
	public QoSInformationAvp getGroupedQoSInformation( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedQoSInformation()");
			}
			return new QoSInformationAvp(stackObj.getGroupedQoSInformation());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGroupedQoSInformation",e);
		}
	}

	/**
	 *  Retrieving a single Grouped value from ServiceSpecificInfo AVPs.
	 */
	public ServiceSpecificInfoAvp getGroupedServiceSpecificInfo( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedServiceSpecificInfo()");
			}
			return new ServiceSpecificInfoAvp(stackObj.getGroupedServiceSpecificInfo());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGroupedServiceSpecificInfo",e);
		}
	}

	/**
	 *  Retrieving a single Unsigned32 value from LocalSequenceNumber AVPs.
	 */
	public long getLocalSequenceNumber( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getLocalSequenceNumber()");
			}
			return stackObj.getLocalSequenceNumber();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getLocalSequenceNumber",e);
		}
	}

	/**
	 *  Retrieving a single Unsigned32 value from RatingGroup AVPs.
	 */
	public long getRatingGroup( ) throws GyResourceException {
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
	 *  Retrieving a single Unsigned32 value from ServiceIdentifier AVPs.
	 */
	public long getServiceIdentifier( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getServiceIdentifier()");
			}
			return stackObj.getServiceIdentifier();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getServiceIdentifier",e);
		}
	}

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
	 *  Retrieving a single Time value from TimeFirstUsage AVPs.
	 */
	public java.util.Date getTimeFirstUsage( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getTimeFirstUsage()");
			}
			return stackObj.getTimeFirstUsage();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getTimeFirstUsage",e);
		}
	}

	/**
	 *  Retrieving a single Time value from TimeLastUsage AVPs.
	 */
	public java.util.Date getTimeLastUsage( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getTimeLastUsage()");
			}
			return stackObj.getTimeLastUsage();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getTimeLastUsage",e);
		}
	}

	/**
	 *  Retrieving a single Unsigned32 value from TimeUsage AVPs.
	 */
	public long getTimeUsage( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getTimeUsage()");
			}
			return stackObj.getTimeUsage();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getTimeUsage",e);
		}
	}


}