package com.baypackets.ase.ra.diameter.gy.avp;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.baypackets.ase.ra.diameter.gy.GyResourceException;
import com.baypackets.ase.ra.diameter.gy.enums.PoCChangeConditionEnum;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.gy.generated.avp.AvpTalkBurstExchange;

public class TalkBurstExchangeAvp extends AvpDiameterGrouped {

	private static Logger logger = Logger.getLogger(TalkBurstExchangeAvp.class.getName());
	public static final long vendorId = 0L;

	private AvpTalkBurstExchange stackObj;

	public TalkBurstExchangeAvp(AvpTalkBurstExchange stkObj){
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
	 *  Adding NumberOfParticipants AVP of type Unsigned32 to the message.
	 */
	public NumberOfParticipantsAvp addNumberOfParticipants(long value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addNumberOfParticipants()");
			}
			return new NumberOfParticipantsAvp(stackObj.addNumberOfParticipants(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addNumberOfParticipants",e);
		}
	}

	/**
	 *  Adding NumberOfReceivedTalkBursts AVP of type Unsigned32 to the message.
	 */
	public NumberOfReceivedTalkBurstsAvp addNumberOfReceivedTalkBursts(long value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addNumberOfReceivedTalkBursts()");
			}
			return new NumberOfReceivedTalkBurstsAvp(stackObj.addNumberOfReceivedTalkBursts(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addNumberOfReceivedTalkBursts",e);
		}
	}

	/**
	 *  Adding NumberOfTalkBursts AVP of type Unsigned32 to the message.
	 */
	public NumberOfTalkBurstsAvp addNumberOfTalkBursts(long value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addNumberOfTalkBursts()");
			}
			return new NumberOfTalkBurstsAvp(stackObj.addNumberOfTalkBursts(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addNumberOfTalkBursts",e);
		}
	}

	/**
	 *  Adding PoCChangeCondition AVP of type Enumerated to the message.
	 */
	public PoCChangeConditionAvp addPoCChangeCondition(PoCChangeConditionEnum value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addPoCChangeCondition()");
			}
			return new PoCChangeConditionAvp(stackObj.addPoCChangeCondition(PoCChangeConditionEnum.getStackObj(value)));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addPoCChangeCondition",e);
		}
	}

	/**
	 *  Adding PoCChangeTime AVP of type Time to the message.
	 */
	public PoCChangeTimeAvp addPoCChangeTime(java.util.Date value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addPoCChangeTime()");
			}
			return new PoCChangeTimeAvp(stackObj.addPoCChangeTime(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addPoCChangeTime",e);
		}
	}

	/**
	 *  Adding ReceivedTalkBurstTime AVP of type Unsigned32 to the message.
	 */
	public ReceivedTalkBurstTimeAvp addReceivedTalkBurstTime(long value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addReceivedTalkBurstTime()");
			}
			return new ReceivedTalkBurstTimeAvp(stackObj.addReceivedTalkBurstTime(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addReceivedTalkBurstTime",e);
		}
	}

	/**
	 *  Adding ReceivedTalkBurstVolume AVP of type Unsigned32 to the message.
	 */
	public ReceivedTalkBurstVolumeAvp addReceivedTalkBurstVolume(long value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addReceivedTalkBurstVolume()");
			}
			return new ReceivedTalkBurstVolumeAvp(stackObj.addReceivedTalkBurstVolume(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addReceivedTalkBurstVolume",e);
		}
	}

	/**
	 *  Adding TalkBurstTime AVP of type Unsigned32 to the message.
	 */
	public TalkBurstTimeAvp addTalkBurstTime(long value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addTalkBurstTime()");
			}
			return new TalkBurstTimeAvp(stackObj.addTalkBurstTime(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addTalkBurstTime",e);
		}
	}

	/**
	 *  Adding TalkBurstVolume AVP of type Unsigned32 to the message.
	 */
	public TalkBurstVolumeAvp addTalkBurstVolume(long value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addTalkBurstVolume()");
			}
			return new TalkBurstVolumeAvp(stackObj.addTalkBurstVolume(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addTalkBurstVolume",e);
		}
	}

	/**
	 *  This method returns the enum value corrosponding to PoCChangeConditionAvp.
	 */
	public PoCChangeConditionEnum getEnumPoCChangeCondition( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getEnumPoCChangeCondition()");
			}
			return PoCChangeConditionEnum.getContainerObj(stackObj.getEnumPoCChangeCondition());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getEnumPoCChangeCondition",e);
		}
	}

	/**
	 *  Retrieving a single Unsigned32 value from NumberOfParticipants AVPs.
	 */
	public long getNumberOfParticipants( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getNumberOfParticipants()");
			}
			return stackObj.getNumberOfParticipants();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getNumberOfParticipants",e);
		}
	}

	/**
	 *  Retrieving a single Unsigned32 value from NumberOfReceivedTalkBursts AVPs.
	 */
	public long getNumberOfReceivedTalkBursts( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getNumberOfReceivedTalkBursts()");
			}
			return stackObj.getNumberOfReceivedTalkBursts();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getNumberOfReceivedTalkBursts",e);
		}
	}

	/**
	 *  Retrieving a single Unsigned32 value from NumberOfTalkBursts AVPs.
	 */
	public long getNumberOfTalkBursts( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getNumberOfTalkBursts()");
			}
			return stackObj.getNumberOfTalkBursts();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getNumberOfTalkBursts",e);
		}
	}

	/**
	 *  Retrieving a single Enumerated value from PoCChangeCondition AVPs.
	 */
	public int getPoCChangeCondition( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getPoCChangeCondition()");
			}
			return stackObj.getPoCChangeCondition();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getPoCChangeCondition",e);
		}
	}

	/**
	 *  Retrieving a single Time value from PoCChangeTime AVPs.
	 */
	public java.util.Date getPoCChangeTime( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getPoCChangeTime()");
			}
			return stackObj.getPoCChangeTime();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getPoCChangeTime",e);
		}
	}

	/**
	 *  Retrieving a single Unsigned32 value from ReceivedTalkBurstTime AVPs.
	 */
	public long getReceivedTalkBurstTime( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getReceivedTalkBurstTime()");
			}
			return stackObj.getReceivedTalkBurstTime();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getReceivedTalkBurstTime",e);
		}
	}

	/**
	 *  Retrieving a single Unsigned32 value from ReceivedTalkBurstVolume AVPs.
	 */
	public long getReceivedTalkBurstVolume( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getReceivedTalkBurstVolume()");
			}
			return stackObj.getReceivedTalkBurstVolume();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getReceivedTalkBurstVolume",e);
		}
	}

	/**
	 *  Retrieving a single Unsigned32 value from TalkBurstTime AVPs.
	 */
	public long getTalkBurstTime( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getTalkBurstTime()");
			}
			return stackObj.getTalkBurstTime();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getTalkBurstTime",e);
		}
	}

	/**
	 *  Retrieving a single Unsigned32 value from TalkBurstVolume AVPs.
	 */
	public long getTalkBurstVolume( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getTalkBurstVolume()");
			}
			return stackObj.getTalkBurstVolume();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getTalkBurstVolume",e);
		}
	}

}