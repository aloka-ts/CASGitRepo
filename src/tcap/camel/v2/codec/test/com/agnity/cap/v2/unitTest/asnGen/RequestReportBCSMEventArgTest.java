package com.agnity.cap.v2.unitTest.asnGen;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.bn.CoderFactory;
import org.bn.IDecoder;

import asnGenerated.v2.BCSMEvent;
import asnGenerated.v2.EventTypeBCSM;
import asnGenerated.v2.MonitorMode;
import asnGenerated.v2.RequestReportBCSMEventArg;

import com.agnity.cap.v2.unitTest.expectedValues.RequestReportBCSMEventArgExpectedValues;

import junit.framework.TestCase;

public class RequestReportBCSMEventArgTest extends TestCase {
	
	private RequestReportBCSMEventArg rrBcsme=null;
	private RequestReportBCSMEventArgExpectedValues expectedValues;
	   
    @Override
    protected void setUp() throws Exception {
    	expectedValues = RequestReportBCSMEventArgExpectedValues.getObject();
    	expectedValues.setDefaultValues();
    	rrBcsme = (RequestReportBCSMEventArg) expectedValues.decode();
    	super.setUp();
    }
    
	public void testGetBcsmEvents() {
		Collection<BCSMEvent> eventsList =  (Collection<BCSMEvent>) rrBcsme.getBcsmEvents();
		ArrayList expectedBCSMEventList = expectedValues.getBCSMEvents();
		EventTypeBCSM.EnumType eventType = null;
		MonitorMode.EnumType monitorModeType = null;
		byte[] legSideID = null;
		boolean isEventTypeSame =false;
		boolean isModeTypeSame = false;
		boolean isLegSideIDSame = false;
		int j=0;
		for(int i=0;i<expectedBCSMEventList.size();i+=3){
		//for(BCSMEvent event: eventsList){
			BCSMEvent event = (BCSMEvent) eventsList.toArray()[j];
			eventType = event.getEventTypeBCSM().getValue();
			monitorModeType = event.getMonitorMode().getValue();
			if(event.getLegID().isReceivingSideIDSelected()){
				legSideID = event.getLegID().getReceivingSideID().getValue();
			}else{
				legSideID = event.getLegID().getSendingSideID().getValue();
			}
			j++;
			   isEventTypeSame = expectedBCSMEventList.get(i).equals(eventType);
			   isModeTypeSame = expectedBCSMEventList.get(i+1).equals(monitorModeType);
			   isLegSideIDSame = Arrays.equals((byte[])expectedBCSMEventList.get(i+2), legSideID); 
			   assertTrue(j+" BCSMEvent doesn't match.",isEventTypeSame&&isModeTypeSame&&isLegSideIDSame);
			 //  System.out.println("result"+(isEventTypeSame&&isModeTypeSame&&isLegSideIDSame));
			
		}
		
	}

	public void testGetExtensions() {
		if(expectedValues.extensions!=null){
			assertNotNull(rrBcsme.getExtensions());
		}else{
			assertNull(rrBcsme.getExtensions());
		}
	}

	public void testIsExtensionsPresent() {
		assertEquals(expectedValues.extensions!=null, rrBcsme.isExtensionsPresent());
	}
}
