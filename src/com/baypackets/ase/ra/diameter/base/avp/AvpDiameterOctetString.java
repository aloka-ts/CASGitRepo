package com.baypackets.ase.ra.diameter.base.avp;

import java.nio.ByteBuffer;

import com.baypackets.ase.ra.diameter.common.enums.AvpFormatEnum;

import fr.marben.diameter.DiameterOctetStringAVP;

public class AvpDiameterOctetString extends AvpDiameter {

	DiameterOctetStringAVP stackObj;

	public AvpDiameterOctetString(DiameterOctetStringAVP stkObj){
		super(stkObj);
		this.stackObj=stkObj;
	}

	@Override
	public String getAvpFormat() {
		return stackObj.getInstanceType();//getContainerObj(stackObj.get.getAvpFormat());
	}

	@Override
	public String getData() {
		return stackObj.getStringValue();//.getData();	
	}

	public java.lang.String getOctetString(){
		return stackObj.getStringValue();
	}

	public byte[] getRawOctetString(){
		return stackObj.getValue();//RawOctetString();
	}

	public java.lang.String toString(){
		return stackObj.toString();
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
