package com.agnity.cap.v2.unitTest.expectedValues;

import java.io.ByteArrayInputStream;

import org.bn.CoderFactory;
import org.bn.IDecoder;

import asnGenerated.v2.ReleaseCallArg;




public class ReleaseCallArgExpectedValues implements ExpectedValues{

	public byte[] releaseCallArgByteValue;
	private static ReleaseCallArgExpectedValues object;
	
	public byte[] value;
	
	private ReleaseCallArgExpectedValues(){
		
	}
	public Object decode() {

		  IDecoder decoder=null;
		try {
			decoder = CoderFactory.getInstance().newDecoder("BER");
		    ByteArrayInputStream bi = new ByteArrayInputStream(this.releaseCallArgByteValue);
		    return decoder.decode(bi,ReleaseCallArg.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static ReleaseCallArgExpectedValues getObject(){
		if(object==null){
			object = new ReleaseCallArgExpectedValues();
		}
		return object;
	}
	
	@Override
	public boolean setDefaultValues() {
		 this.releaseCallArgByteValue=new byte[]{(byte)0x04,(byte)0x02,(byte)0x80,(byte)0x90};
		 this.value= new byte[]{(byte)0x80,(byte)0x90};
		return false;
	}

}
