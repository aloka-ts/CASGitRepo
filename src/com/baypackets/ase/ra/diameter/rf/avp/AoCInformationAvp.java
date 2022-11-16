package com.baypackets.ase.ra.diameter.rf.avp;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.baypackets.ase.ra.diameter.rf.RfResourceException;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.rf.generated.avp.AvpAoCInformation;

public class AoCInformationAvp extends AvpDiameterGrouped {

	private static Logger logger = Logger.getLogger(AoCInformationAvp.class.getName());
	
	public static final String name = "ErrorReportingHost";
	public static final int code = 294;
	public static final long vendorId = 0L;

	private AvpAoCInformation stackObj;

	public AoCInformationAvp(AvpAoCInformation stkObj){
		super(stkObj);
		this.stackObj=stkObj;
	}

	public int getCode() {
		return stackObj.getCode();
	}

	public FlagRuleEnum getMRule() {
		return FlagRuleEnum.getContainerObj(stackObj.getMRule());
	}

	public String getName() {
		return stackObj.getName();
	}

	public FlagRuleEnum getPRule() {
		return FlagRuleEnum.getContainerObj(stackObj.getPRule());
	}

	public long getVendorId() {
		return stackObj.getVendorId();
	}

	public FlagRuleEnum getVRule() {
		return FlagRuleEnum.getContainerObj(stackObj.getVRule());
	}

	/**
	 * Adding AoCCostInformation AVP of type Grouped to the message.
	 * @return
	 * @throws RfResourceException  
	 */
	public AoCCostInformationAvp addGroupedAoCCostInformation() throws RfResourceException {
		try {
			return new AoCCostInformationAvp(stackObj.addGroupedAoCCostInformation());
		} catch (ValidationException e) {
			throw new RfResourceException(e);
		}
	}

	/**
	 * Adding TariffInformation AVP of type Grouped to the message.
	 * @return
	 */
	public TariffInformationAvp addGroupedTariffInformation() throws RfResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedTariffInformation()");
			}
			return	new TariffInformationAvp(stackObj.addGroupedTariffInformation());
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in addGroupedTariffInformation ",e);
		}
	}

	/**
	 * Retrieving a single Grouped value from AoCCostInformation AVPs.
	 * @return
	 */
	public AoCCostInformationAvp getGroupedAoCCostInformation() throws RfResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedAoCCostInformation()");
			}
			return	new AoCCostInformationAvp(stackObj.getGroupedAoCCostInformation());
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in getGroupedAoCCostInformation ",e);
		}
	}

	/**
	 * Retrieving a single Grouped value from TariffInformation AVPs.
	 * @return
	 */
	public TariffInformationAvp getGroupedTariffInformation() throws RfResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedTariffInformation()");
			}
			return	new TariffInformationAvp(stackObj.getGroupedTariffInformation());
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in getGroupedTariffInformation ",e);
		}	
	}

}