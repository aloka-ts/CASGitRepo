package com.baypackets.ase.ra.diameter.base.avp;

import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;

import fr.marben.diameter.DiameterAVP;
//import com.traffix.openblox.core.coding.Avp;


public abstract class BaseAvpDefinition extends BaseAvp {

	// TODO
	// This class AvpDefinition is not present OoenBlox stack
	// jar package. Need to use this class so that AvpDiamter 
	// can extends this. Need to confirm from Traffix System

	public BaseAvpDefinition(DiameterAVP stkObj) {
		super(stkObj);
	}


	/**
	 * 	Derived classes should force AVP's definition
	 * @return
	 */
	abstract  boolean 	getMBit();

	/**
	 * 	Derived classes should force AVP's definition
	 * @return
	 */
	abstract  boolean 	getPBit();


	abstract  long 	getVendorId();

	/**
	 * @return
	 */
	abstract  boolean 	getVBit();

}
