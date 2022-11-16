package com.baypackets.ase.ra.diameter.base.avp;

import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;

import fr.marben.diameter.DiameterAVP;

public abstract class AvpDiameter extends BaseAvpDefinition {

	DiameterAVP stackObj;
	
	public AvpDiameter(DiameterAVP stkObj) {
		super(stkObj);
		this.stackObj=stkObj;
	}

	public int getCode(){
		return (int) stackObj.getCode();
	}
	
	public String getName(){
		return stackObj.getName();
	}

	@Override
	boolean getMBit() {
		// TODO Auto-generated method stub
		return stackObj.getMBit();
	}

	@Override
	boolean getPBit() {
		// TODO Auto-generated method stub
		return stackObj.getPBit();
	}

	@Override
	boolean getVBit() {
		// TODO Auto-generated method stub
		return stackObj.getVBit();
	}

//	public int getHeaderLength() {
//		return stackObj.get.getHeaderLength();
//	}
//
//	public boolean isEncrypted() {
//		return stackObj.isEncrypted();
//	}
//
//	public boolean isMandatory() {
//		return stackObj.isMandatory();
//	}

	public boolean isVendorId() {
		return stackObj.getVBit();
	}

//	public void setEncrypted(boolean flag) throws ValidationException {
//		try {
//			stackObj.setEncrypted(flag);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			throw new ValidationException(e);
//		}
//	}

//	public void setMandatory(boolean flag) {
//		stackObj.setMandatory(flag);
//	}
//
//	public void toXML(StringBuilder stringbuilder, String s) {
//		stackObj.toXML(stringbuilder, s);
//	}
//
//	public void write(ByteBuffer bytebuffer) {
//		stackObj.write(bytebuffer);
//	}

//	/// AvpDefinitation API
//
	/**
	 * 	Derived classes should force AVP's definition
	 * @return
	 */
	public FlagRuleEnum getMRule(){
		return null;
	}

	/**
	 * 	Derived classes should force AVP's definition
	 * @return
	 */
	public FlagRuleEnum getPRule(){
		return null;//FlagRuleEnum.getContainerObj(stackObj.getPRule());
	}


	public long getVendorId(){
		return stackObj.getVendorId();
	}

	/**
	 * @return
	 */
	public FlagRuleEnum getVRule(){
		return null;
	}
}
