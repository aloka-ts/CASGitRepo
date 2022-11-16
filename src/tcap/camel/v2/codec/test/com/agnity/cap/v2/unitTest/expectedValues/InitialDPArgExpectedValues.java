package com.agnity.cap.v2.unitTest.expectedValues;

import java.io.ByteArrayInputStream;

import org.bn.CoderFactory;
import org.bn.IDecoder;

import asnGenerated.v2.EventTypeBCSM;
import asnGenerated.v2.InitialDPArg;





public class InitialDPArgExpectedValues implements ExpectedValues{
	
	private static InitialDPArgExpectedValues object;
	
	public byte[] initialDPArg = null;

	public int serviceKey = 0;

	public byte[] calledPartyNumber = null;

	public byte[] callingPartyNumber = null;

	public byte[] callingPartysCategory = null;

	public byte[] iPSSPCapabilities = null;

	public byte[] locationNumber = null;

	public byte[] originalCalledPartyID = null;

	public byte[] extensions = null;

	public byte[] highLayerCompatibility = null;

	public byte[] additionalCallingPartyNumber = null;

	public byte[] bearerCapability = null;

	public EventTypeBCSM.EnumType eventTypeBCSM = null;

	public byte[] redirectingPartyID = null;

	public byte[] redirectionInformation = null;

	public byte[] iMSI = null;

	public byte[] subscriberState = null;

	public byte[] locationInformation = null;

	public byte[] ext_basicServiceCode = null;

	public byte[] callReferenceNumber = null;

	public byte[] mscAddress = null;

	public byte[] calledPartyBCDNumber = null;

	public byte[] timeAndTimezone = null;

	public byte[] gsm_ForwardingPending = null;

	public byte[] initialDPArgExtension = null;
	
	public boolean useDefaultValue = true;
	
	private InitialDPArgExpectedValues(){
		
	}
	
	public static InitialDPArgExpectedValues getObject(){
		if(object==null){
			object = new InitialDPArgExpectedValues();
		}
		return object;
	}
	
	 public Object decode(){
		  IDecoder decoder=null;
		try {
			decoder = CoderFactory.getInstance().newDecoder("BER");
		    ByteArrayInputStream bi = new ByteArrayInputStream(this.initialDPArg);
		    return decoder.decode(bi,InitialDPArg.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	 }
	
	public boolean setDefaultValues(){
		if(!useDefaultValue){
			return false;
		}
		//hex value=307780016e8308041019999911414485010a8801008a0704101032547698bb0580038090a39c01029f320864001200141044f4bf34160201008106911032547698a309800764f0f000010000bf35038301119f360700010b010008009f37069110325476989f3806a199991131229f39080211301222201522
		this.initialDPArg = new byte[]{(byte)0x30,(byte)0x77,(byte)0x80,(byte)0x01,(byte)0x6e,
				                       (byte)0x83,(byte)0x08,(byte)0x04,(byte)0x10,(byte)0x19,
				                       (byte)0x99,(byte)0x99,(byte)0x11,(byte)0x41,(byte)0x44,
				                       (byte)0x85,(byte)0x01,(byte)0x0a,(byte)0x88,(byte)0x01,
				                       (byte)0x00,(byte)0x8a,(byte)0x07,(byte)0x04,(byte)0x10,
				                       (byte)0x10,(byte)0x32,(byte)0x54,(byte)0x76,(byte)0x98,
				                       (byte)0xbb,(byte)0x05,(byte)0x80,(byte)0x03,(byte)0x80,
				                       (byte)0x90,(byte)0xa3,(byte)0x9c,(byte)0x01,(byte)0x02,
				                       (byte)0x9f,(byte)0x32,(byte)0x08,(byte)0x64,(byte)0x00,
				                       (byte)0x12,(byte)0x00,(byte)0x14,(byte)0x10,(byte)0x44,
				                       (byte)0xf4,(byte)0xbf,(byte)0x34,(byte)0x16,(byte)0x02,
				                       (byte)0x01,(byte)0x00,(byte)0x81,(byte)0x06,(byte)0x91,
				                       (byte)0x10,(byte)0x32,(byte)0x54,(byte)0x76,(byte)0x98,
				                       (byte)0xa3,(byte)0x09,(byte)0x80,(byte)0x07,(byte)0x64,
				                       (byte)0xf0,(byte)0xf0,(byte)0x00,(byte)0x01,(byte)0x00,
				                       (byte)0x00,(byte)0xbf,(byte)0x35,(byte)0x03,(byte)0x83,
				                       (byte)0x01,(byte)0x11,(byte)0x9f,(byte)0x36,(byte)0x07,
				                       (byte)0x00,(byte)0x01,(byte)0x0b,(byte)0x01,(byte)0x00,
				                       (byte)0x08,(byte)0x00,(byte)0x9f,(byte)0x37,(byte)0x06,
				                       (byte)0x91,(byte)0x10,(byte)0x32,(byte)0x54,(byte)0x76,
				                       (byte)0x98,(byte)0x9f,(byte)0x38,(byte)0x06,(byte)0xa1,
				                       (byte)0x99,(byte)0x99,(byte)0x11,(byte)0x31,(byte)0x22,
				                       (byte)0x9f,(byte)0x39,(byte)0x08,(byte)0x02,(byte)0x11,
				                       (byte)0x30,(byte)0x12,(byte)0x22,(byte)0x20,(byte)0x15,
				                       (byte)0x22};
		//hex value=6e
		this.serviceKey =110;
	    //hex value=null
	    this.calledPartyNumber = null;
	    //hex value=0410199999114144
	    this.callingPartyNumber = new byte[]{(byte)0x04,(byte)0x10,(byte)0x19,(byte)0x99,(byte)0x99,(byte)0x11,(byte)0x41,(byte)0x44}; 
	    //hex value=0a
	    this.callingPartysCategory =  new byte[]{(byte)0x0a};
	    //hex value=00
	    this.iPSSPCapabilities = new byte[]{(byte)0x00};
	    //hex value=04101032547698
	    this.locationNumber = new byte[]{(byte)0x04,(byte)0x10,(byte)0x10,(byte)0x32,(byte)0x54,(byte)0x76,(byte)0x98};
		  //hex value=null
	    this.originalCalledPartyID = null;
		  //hex value=null
	    this.extensions = null;
		  //hex value=null
	    this.highLayerCompatibility = null;
		  //hex value=null
	    this.additionalCallingPartyNumber = null; 
		  //hex value=8090a3
	    this.bearerCapability = new byte[]{(byte)0x80,(byte)0x90,(byte)0xa3};
		  //hex value=02
	    this.eventTypeBCSM = this.eventTypeBCSM.collectedInfo;
		  //hex value=null;
	    this.redirectingPartyID = null;
		  //hex value=64001200141044f4
	    this.iMSI = new byte[]{(byte)0x64,(byte)0x00,(byte)0x12,(byte)0x00,(byte)0x14,(byte)0x10,(byte)0x44,(byte)0xf4};
		  //hex value=null
	    this.subscriberState =null; 
		  //hex value=0201008106911032547698a309800764f0f000010000
	    this.locationInformation = new byte[]{(byte)0x02,(byte)0x01,(byte)0x00,(byte)0x81,(byte)0x06,(byte)0x91,(byte)0x10,(byte)0x32,(byte)0x54,(byte)0x76,(byte)0x98,(byte)0xa3,(byte)0x09,(byte)0x80,(byte)0x07,(byte)0x64,(byte)0xf0,(byte)0xf0,(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x00};
		  //hex value=11
	    this.ext_basicServiceCode = new byte[]{(byte)0x11};
		  //hex value=00010b01000800
	    this.callReferenceNumber = new byte[]{(byte)0x00,(byte)0x01,(byte)0x0b,(byte)0x01,(byte)0x00,(byte)0x08,(byte)0x00};
		  //hex value=911032547698
	    this.mscAddress = new byte[]{(byte)0x91,(byte)0x10,(byte)0x32,(byte)0x54,(byte)0x76,(byte)0x98};
		  //hex value=a19999113122
	    this.calledPartyBCDNumber=new byte[]{(byte)0xa1,(byte)0x99,(byte)0x99,(byte)0x11,(byte)0x31,(byte)0x22};
		  //hex value=0211301222201522
	    this.timeAndTimezone = new byte[]{(byte)0x02,(byte)0x11,(byte)0x30,(byte)0x12,(byte)0x22,(byte)0x20,(byte)0x15,(byte)0x22};
		  //hex value=null
	    this.gsm_ForwardingPending = null;
		  //hex value=null
	    this.initialDPArgExtension = null;	    
	    
	    return true;
	    }
	
}
