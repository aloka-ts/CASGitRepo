package com.baypackets.ase.ra.diameter.base.avp;

import java.math.BigInteger;
import java.nio.ByteBuffer;

import com.baypackets.ase.ra.diameter.common.enums.AvpFormatEnum;

import fr.marben.diameter.DiameterUnsigned64AVP;

public class AvpDiameterUnsigned64 extends AvpDiameter {

	DiameterUnsigned64AVP stackObj;

	public AvpDiameterUnsigned64(DiameterUnsigned64AVP stkObj){
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

	public BigInteger getUnsigned64(){
		return stackObj.getValue();
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
