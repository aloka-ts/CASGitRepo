package com.baypackets.ase.ra.diameter.gy.avp;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.baypackets.ase.ra.diameter.gy.GyResourceException;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.gy.generated.avp.AvpTrafficDataVolumes;

public class TrafficDataVolumesAvp extends AvpDiameterGrouped {

	private static Logger logger = Logger.getLogger(TrafficDataVolumesAvp.class.getName());
	public static final long vendorId = 0L;

	private AvpTrafficDataVolumes stackObj;

	public TrafficDataVolumesAvp(AvpTrafficDataVolumes stkObj){
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
	 *  Retrieving a single Integer32 value from ChangeCondition AVPs.
	 */
	public int getChangeCondition( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getChangeCondition()");
			}
			return stackObj.getChangeCondition();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getChangeCondition",e);
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

}