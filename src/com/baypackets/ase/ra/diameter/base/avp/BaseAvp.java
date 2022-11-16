package com.baypackets.ase.ra.diameter.base.avp;

import java.nio.ByteBuffer;

import com.baypackets.ase.ra.diameter.common.enums.AvpFormatEnum;

import fr.marben.diameter.DiameterAVP;


public  abstract class BaseAvp {

	DiameterAVP stackObj;

	public BaseAvp(DiameterAVP stkObj) {
		this.stackObj=stkObj;
	}

	public boolean equals(Object obj){
		// TODO need to override this method
		return true;
	}

	public  abstract String getAvpFormat();

	public  abstract int getCode();

	public abstract String getData();

	public String getName(){
		return stackObj.getName();
	}

//	public byte[] getRaw(){
//		return stackObj.getRaw();
//	}
//
//	public boolean isValid(){
//		return stackObj.isValid();
//	}

	public abstract void toXML(StringBuilder stringbuilder, String s);

	public  abstract void write(ByteBuffer bytebuffer);
}