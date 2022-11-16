package com.agnity.cap.v2.unitTest.expectedValues;

import java.io.ByteArrayInputStream;

import org.bn.CoderFactory;
import org.bn.IDecoder;

import asnGenerated.v2.EventReportBCSMArg;
import asnGenerated.v2.EventTypeBCSM;
import asnGenerated.v2.MiscCallInfo.MessageTypeEnumType;


public class EventReportBCSMArgExpectedValues implements ExpectedValues {
	
	public byte[] EventReportBCSMArgByteValue = null;
	public EventTypeBCSM.EnumType eventTypeBCSM = null;
	public byte[] eventSpecificInformationBCSM = null;
	public byte[] legID = null;
	public MessageTypeEnumType.EnumType miscCallInfo = null;
	public byte[]  extensions = null;
	private static EventReportBCSMArgExpectedValues erBCSMexpectedValue= null;
	public boolean useDefaultValues = true;
	
	private EventReportBCSMArgExpectedValues(){
		
	}
	
	public EventReportBCSMArg decode(){
		try {
			IDecoder decoder = CoderFactory.getInstance().newDecoder("BER");
		    ByteArrayInputStream bi = new ByteArrayInputStream(this.EventReportBCSMArgByteValue);
		    return (EventReportBCSMArg)decoder.decode(bi,EventReportBCSMArg.class);
		} catch (Exception e) {
			e.printStackTrace();
		}	
		return null;
	 }
	
	public static EventReportBCSMArgExpectedValues getObject(){
		if(erBCSMexpectedValue==null){
			erBCSMexpectedValue = new EventReportBCSMArgExpectedValues();
		}
		return erBCSMexpectedValue;
	}
	

	public boolean setDefaultValues() {
		if(!useDefaultValues){
			return false;
		}
		//hex value=300d800107a303810102a403800100
		this.EventReportBCSMArgByteValue = new byte[]{(byte)0x30,(byte)0x0d,(byte)0x80,(byte)0x01,
				                                      (byte)0x07,(byte)0xa3,(byte)0x03,(byte)0x81,
				                                      (byte)0x01,(byte)0x02,(byte)0xa4,(byte)0x03,
				                                      (byte)0x80,(byte)0x01,(byte)0x00};
		
		this.eventTypeBCSM = eventTypeBCSM.oAnswer;
		this.legID = new byte[]{(byte)0x02};
		this.miscCallInfo = miscCallInfo.request;
		
		
		return true;
	}

}
