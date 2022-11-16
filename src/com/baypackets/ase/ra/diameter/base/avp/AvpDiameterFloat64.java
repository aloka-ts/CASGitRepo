package com.baypackets.ase.ra.diameter.base.avp;

import java.nio.ByteBuffer;

import fr.marben.diameter.DiameterFloat64AVP;

public class AvpDiameterFloat64 extends AvpDiameter {

	DiameterFloat64AVP stackObj;

	public AvpDiameterFloat64(DiameterFloat64AVP stkObj){
		super(stkObj);
		this.stackObj=stkObj;
	}

	public String getAvpFormat(){
		return stackObj.getInstanceType();
	}
	/**
	 * This method returns the data associated with this AVP.
	 */
	public String getData() {
		return stackObj.toString();
	}
	
	public double getFloat64(){
		return stackObj.getValue();
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

	@Override
	public void toXML(StringBuilder stringbuilder, String s) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void write(ByteBuffer bytebuffer) {
		// TODO Auto-generated method stub
		
	}
}
