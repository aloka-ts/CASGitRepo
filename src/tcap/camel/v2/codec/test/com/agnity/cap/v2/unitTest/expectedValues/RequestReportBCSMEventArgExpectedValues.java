package com.agnity.cap.v2.unitTest.expectedValues;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

import org.bn.CoderFactory;
import org.bn.IDecoder;
import org.bn.annotations.ASN1EnumItem;

import asnGenerated.v2.EventTypeBCSM;
import asnGenerated.v2.MonitorMode;
import asnGenerated.v2.RequestReportBCSMEventArg;


public class RequestReportBCSMEventArgExpectedValues implements ExpectedValues {
	
	private static RequestReportBCSMEventArgExpectedValues object;
	
	
	public byte[]  requestReportBCSMEventArgBytes;
	public byte[]  extensions = null;
	private ArrayList BCSMEvents = new ArrayList();
	public boolean useDefaultValues = true;
	
	private RequestReportBCSMEventArgExpectedValues(){
		
	}
	
	public static RequestReportBCSMEventArgExpectedValues getObject(){
		if(object==null){
			object = new RequestReportBCSMEventArgExpectedValues();
		}
		object.BCSMEvents.clear();
		return object;
	}
	
	public void addBCSMEvent(EventTypeBCSM.EnumType eventType,MonitorMode.EnumType monitorModeType,byte[] legSideId){
		BCSMEvents.add(eventType);
		BCSMEvents.add(monitorModeType);
		BCSMEvents.add(legSideId);
	}
	
	public ArrayList getBCSMEvents(){
           return BCSMEvents;		
	}
	
	 public Object decode(){
			try {
				IDecoder decoder = CoderFactory.getInstance().newDecoder("BER");
			    ByteArrayInputStream bi = new ByteArrayInputStream(this.requestReportBCSMEventArgBytes);
			    return decoder.decode(bi,RequestReportBCSMEventArg.class);
			} catch (Exception e) {
				e.printStackTrace();
			}	
			return null;
		 }
	
	public boolean setDefaultValues(){
		if(!useDefaultValues){
			return false;
		}
		//hex values = 
		this.requestReportBCSMEventArgBytes = new byte[]{(byte)0x30,(byte)0x43,(byte)0xa0,(byte)0x41,
				                                         (byte)0x30,(byte)0x0b,(byte)0x80,(byte)0x01,
				                                         (byte)0x0a,(byte)0x81,(byte)0x01,(byte)0x01,
				                                         (byte)0xa2,(byte)0x03,(byte)0x80,(byte)0x01,
				                                         (byte)0x01,(byte)0x30,(byte)0x0b,(byte)0x80,
				                                         (byte)0x01,(byte)0x07,(byte)0x81,(byte)0x01,
				                                         (byte)0x00,(byte)0xa2,(byte)0x03,(byte)0x80,
				                                         (byte)0x01,(byte)0x02,(byte)0x30,(byte)0x0b,
				                                         (byte)0x80,(byte)0x01,(byte)0x06,(byte)0x81,
				                                         (byte)0x01,(byte)0x00,(byte)0xa2,(byte)0x03,
				                                         (byte)0x80,(byte)0x01,(byte)0x02,(byte)0x30,
				                                         (byte)0x0b,(byte)0x80,(byte)0x01,(byte)0x05,
				                                         (byte)0x81,(byte)0x01,(byte)0x00,(byte)0xa2,
				                                         (byte)0x03,(byte)0x80,(byte)0x01,(byte)0x02,
				                                         (byte)0x30,(byte)0x0b,(byte)0x80,(byte)0x01,
				                                         (byte)0x09,(byte)0x81,(byte)0x01,(byte)0x00,
				                                         (byte)0xa2,(byte)0x03,(byte)0x80,(byte)0x01,
				                                         (byte)0x01};
		this.addDefaultBCSMEvents();
		
      return true;
	}
	
	private void addDefaultBCSMEvents(){
		 EventTypeBCSM.EnumType eventType=null;
		 MonitorMode.EnumType monitorModeType=null;
		this.addBCSMEvent(eventType.oAbandon, monitorModeType.notifyAndContinue, new byte[]{(byte)0x01});
		this.addBCSMEvent(eventType.oAnswer, monitorModeType.interrupted, new byte[]{(byte)0x02});
		this.addBCSMEvent(eventType.oNoAnswer, monitorModeType.interrupted, new byte[]{(byte)0x02});
		this.addBCSMEvent(eventType.oCalledPartyBusy, monitorModeType.interrupted, new byte[]{(byte)0x02});
		this.addBCSMEvent(eventType.oDisconnect, monitorModeType.interrupted, new byte[]{(byte)0x01});
	}

}
