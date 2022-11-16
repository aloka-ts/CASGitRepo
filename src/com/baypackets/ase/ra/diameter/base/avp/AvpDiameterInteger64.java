package com.baypackets.ase.ra.diameter.base.avp;

import java.nio.ByteBuffer;

import com.baypackets.ase.ra.diameter.common.enums.AvpFormatEnum;
import com.traffix.openblox.diameter.coding.DiameterInteger64Avp;

import fr.marben.diameter.DiameterInteger64AVP;

public class AvpDiameterInteger64 extends AvpDiameter {

	DiameterInteger64AVP stackObj;

	public AvpDiameterInteger64(DiameterInteger64AVP stkObj){
		super(stkObj);
		this.stackObj=stkObj;
	}

	@Override
	public String getAvpFormat() {
		return stackObj.getInstanceType();
	}

	@Override
	public String getData() {
		return stackObj.toString();
	}
	
	 public long getInteger64(){
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
