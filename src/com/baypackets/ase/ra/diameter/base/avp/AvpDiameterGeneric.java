package com.baypackets.ase.ra.diameter.base.avp;

import java.nio.ByteBuffer;

import com.baypackets.ase.ra.diameter.common.enums.AvpFormatEnum;

import fr.marben.diameter.DiameterGenericAVP;
import fr.marben.diameter.DiameterOctetStringAVP;

public class AvpDiameterGeneric extends AvpDiameter {

	DiameterGenericAVP stackObj;

	public AvpDiameterGeneric(DiameterGenericAVP stkObj){
		super(stkObj);
		this.stackObj=stkObj;
	}

	@Override
	public String getAvpFormat() {
		return stackObj.getInstanceType();//getContainerObj(stackObj.get.getAvpFormat());
	}

	@Override
	public String getData() {
		return stackObj.toString();//.getData();	
	}
	
	public long getAVPCode() {
		return stackObj.getAVPCode();//.getData();	
	}

	

	public byte[] getRawGenericValue(){
		return stackObj.getValue();//RawOctetString();
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
