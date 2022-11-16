package com.baypackets.ase.ra.diameter.base.avp;

import java.nio.ByteBuffer;

import fr.marben.diameter.DiameterUnsigned32AVP;


public class AvpDiameterUnsigned32 extends AvpDiameter {

	DiameterUnsigned32AVP stackObj;

	public AvpDiameterUnsigned32(DiameterUnsigned32AVP stkObj){
		super(stkObj);
		this.stackObj=stkObj;
	}

	@Override
	public String getAvpFormat() {
		return stackObj.getInstanceType();
	}

	@Override
	public String getData() {
		return stackObj.getName();
	}

	public long getUnsigned32(){
		return stackObj.getValue();
	}

	public java.lang.String toString(){
		return stackObj.toString();
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
