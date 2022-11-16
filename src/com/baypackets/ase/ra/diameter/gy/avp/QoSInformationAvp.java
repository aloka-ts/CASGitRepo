package com.baypackets.ase.ra.diameter.gy.avp;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.baypackets.ase.ra.diameter.gy.GyResourceException;
import com.baypackets.ase.ra.diameter.gy.enums.QoSClassIdentifierEnum;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.gy.generated.avp.AvpQoSInformation;

public class QoSInformationAvp extends AvpDiameterGrouped {

	private static Logger logger = Logger.getLogger(QoSInformationAvp.class.getName());
	public static final long vendorId = 0L;

	private AvpQoSInformation stackObj;

	public QoSInformationAvp(AvpQoSInformation stkObj){
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
	 *  Adding APNAggregateMaxBitrateDL AVP of type Unsigned32 to the message.
	 */
	public APNAggregateMaxBitrateDLAvp addAPNAggregateMaxBitrateDL(long value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addAPNAggregateMaxBitrateDL()");
			}
			return new APNAggregateMaxBitrateDLAvp(stackObj.addAPNAggregateMaxBitrateDL(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addAPNAggregateMaxBitrateDL",e);
		}
	}

	/**
	 *  Adding APNAggregateMaxBitrateUL AVP of type Unsigned32 to the message.
	 */
	public APNAggregateMaxBitrateULAvp addAPNAggregateMaxBitrateUL(long value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addAPNAggregateMaxBitrateUL()");
			}
			return new APNAggregateMaxBitrateULAvp(stackObj.addAPNAggregateMaxBitrateUL(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addAPNAggregateMaxBitrateUL",e);
		}
	}

	/**
	 *  Adding BearerIdentifier AVP of type OctetString to the message.
	 */
	public BearerIdentifierAvp addBearerIdentifier(byte[] value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addBearerIdentifier()");
			}
			return new BearerIdentifierAvp(stackObj.addBearerIdentifier(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addBearerIdentifier",e);
		}
	}

	/**
	 *  Adding BearerIdentifier AVP of type OctetString to the message.
	 */
	public BearerIdentifierAvp addBearerIdentifier(java.lang.String value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addBearerIdentifier()");
			}
			return new BearerIdentifierAvp(stackObj.addBearerIdentifier(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addBearerIdentifier",e);
		}
	}

	/**
	 *  Adding AllocationRetentionPriority AVP of type Grouped to the message.
	 */
	public AllocationRetentionPriorityAvp addGroupedAllocationRetentionPriority( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedAllocationRetentionPriority()");
			}
			return new AllocationRetentionPriorityAvp(stackObj.addGroupedAllocationRetentionPriority());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGroupedAllocationRetentionPriority",e);
		}
	}

	/**
	 *  Adding GuaranteedBitrateDL AVP of type Unsigned32 to the message.
	 */
	public GuaranteedBitrateDLAvp addGuaranteedBitrateDL(long value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGuaranteedBitrateDL()");
			}
			return new GuaranteedBitrateDLAvp(stackObj.addGuaranteedBitrateDL(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGuaranteedBitrateDL",e);
		}
	}

	/**
	 *  Adding GuaranteedBitrateUL AVP of type Unsigned32 to the message.
	 */
	public GuaranteedBitrateULAvp addGuaranteedBitrateUL(long value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGuaranteedBitrateUL()");
			}
			return new GuaranteedBitrateULAvp(stackObj.addGuaranteedBitrateUL(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGuaranteedBitrateUL",e);
		}
	}

	/**
	 *  Adding MaxRequestedBandwidthDL AVP of type Unsigned32 to the message.
	 */
	public MaxRequestedBandwidthDLAvp addMaxRequestedBandwidthDL(long value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addMaxRequestedBandwidthDL()");
			}
			return new MaxRequestedBandwidthDLAvp(stackObj.addMaxRequestedBandwidthDL(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addMaxRequestedBandwidthDL",e);
		}
	}

	/**
	 *  Adding MaxRequestedBandwidthUL AVP of type Unsigned32 to the message.
	 */
	public MaxRequestedBandwidthULAvp addMaxRequestedBandwidthUL(long value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addMaxRequestedBandwidthUL()");
			}
			return new MaxRequestedBandwidthULAvp(stackObj.addMaxRequestedBandwidthUL(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addMaxRequestedBandwidthUL",e);
		}
	}

	/**
	 *  Adding QoSClassIdentifier AVP of type Enumerated to the message.
	 */
	public QoSClassIdentifierAvp addQoSClassIdentifier(QoSClassIdentifierEnum value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addQoSClassIdentifier()");
			}
			return new QoSClassIdentifierAvp(stackObj.addQoSClassIdentifier(QoSClassIdentifierEnum.getStackObj(value)));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addQoSClassIdentifier",e);
		}
	}

	/**
	 *  Retrieving a single Unsigned32 value from APNAggregateMaxBitrateDL AVPs.
	 */
	public long getAPNAggregateMaxBitrateDL( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getAPNAggregateMaxBitrateDL()");
			}
			return stackObj.getAPNAggregateMaxBitrateDL();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getAPNAggregateMaxBitrateDL",e);
		}
	}

	/**
	 *  Retrieving a single Unsigned32 value from APNAggregateMaxBitrateUL AVPs.
	 */
	public long getAPNAggregateMaxBitrateUL( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getAPNAggregateMaxBitrateUL()");
			}
			return stackObj.getAPNAggregateMaxBitrateUL();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getAPNAggregateMaxBitrateUL",e);
		}
	}

	/**
	 *  Retrieving a single OctetString value from BearerIdentifier AVPs.
	 */
	public java.lang.String getBearerIdentifier( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getBearerIdentifier()");
			}
			return stackObj.getBearerIdentifier();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getBearerIdentifier",e);
		}
	}

	/**
	 *  This method return the enum value corrosponding to QoSClassIdentifierAvp
	 */
	public QoSClassIdentifierEnum getEnumQoSClassIdentifier( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getEnumQoSClassIdentifier()");
			}
			return QoSClassIdentifierEnum.getContainerObj(stackObj.getEnumQoSClassIdentifier());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getEnumQoSClassIdentifier",e);
		}
	}

	/**
	 *  Retrieving a single Grouped value from AllocationRetentionPriority AVPs.
	 */
	public AllocationRetentionPriorityAvp getGroupedAllocationRetentionPriority( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedAllocationRetentionPriority()");
			}
			return new AllocationRetentionPriorityAvp(stackObj.getGroupedAllocationRetentionPriority());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGroupedAllocationRetentionPriority",e);
		}
	}

	/**
	 *  Retrieving a single Unsigned32 value from GuaranteedBitrateDL AVPs.
	 */
	public long getGuaranteedBitrateDL( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGuaranteedBitrateDL()");
			}
			return stackObj.getGuaranteedBitrateDL();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGuaranteedBitrateDL",e);
		}
	}

	/**
	 *  Retrieving a single Unsigned32 value from GuaranteedBitrateUL AVPs.
	 */
	public long getGuaranteedBitrateUL( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGuaranteedBitrateUL()");
			}
			return stackObj.getGuaranteedBitrateUL();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGuaranteedBitrateUL",e);
		}
	}

	/**
	 *  Retrieving a single Unsigned32 value from MaxRequestedBandwidthDL AVPs.
	 */
	public long getMaxRequestedBandwidthDL( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getMaxRequestedBandwidthDL()");
			}
			return stackObj.getMaxRequestedBandwidthDL();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getMaxRequestedBandwidthDL",e);
		}
	}

	/**
	 *  Retrieving a single Unsigned32 value from MaxRequestedBandwidthUL AVPs.
	 */
	public long getMaxRequestedBandwidthUL( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getMaxRequestedBandwidthUL()");
			}
			return stackObj.getMaxRequestedBandwidthUL();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getMaxRequestedBandwidthUL",e);
		}
	}

	/**
	 *  Retrieving a single Enumerated value from QoSClassIdentifier AVPs.
	 */
	public int getQoSClassIdentifier( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getQoSClassIdentifier()");
			}
			return stackObj.getQoSClassIdentifier();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getQoSClassIdentifier",e);
		}
	}

	/**
	 *  Retrieving a single OctetString value from BearerIdentifier AVPs.
	 */
	public byte[] getRawBearerIdentifier( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getRawBearerIdentifier()");
			}
			return stackObj.getRawBearerIdentifier();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getRawBearerIdentifier",e);
		}
	}

}