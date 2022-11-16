package com.baypackets.ase.ra.diameter.base.avp;

import java.nio.ByteBuffer;

import fr.marben.diameter.DiameterInteger32AVP;

public class AvpDiameterInteger32 extends AvpDiameter {

	DiameterInteger32AVP stackObj;

	public AvpDiameterInteger32(DiameterInteger32AVP stkObj){
		super(stkObj);
		this.stackObj=stkObj;
	}

	@Override
	public String getAvpFormat() {
		return stackObj.getInstanceType();//AvpFormatEnum.getContainerObj(stackObj.getAvpFormat());
	}

	@Override
	public String getData() {
		return stackObj.toString();
	}
	
	 public int getInteger32(){
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
