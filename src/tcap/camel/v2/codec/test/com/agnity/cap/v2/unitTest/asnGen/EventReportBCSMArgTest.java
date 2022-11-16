package com.agnity.cap.v2.unitTest.asnGen;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Array;
import java.util.Arrays;

import org.bn.CoderFactory;
import org.bn.IDecoder;



import asnGenerated.v2.EventReportBCSMArg;

import com.agnity.cap.v2.unitTest.expectedValues.EventReportBCSMArgExpectedValues;

import junit.framework.TestCase;

public class EventReportBCSMArgTest extends TestCase {
	
	private EventReportBCSMArg erBcsm;
	private EventReportBCSMArgExpectedValues expectedValues;
		
	@Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		expectedValues = EventReportBCSMArgExpectedValues.getObject();
		expectedValues.setDefaultValues();
		erBcsm = expectedValues.decode();
		super.setUp();
	}
	
	public void testGetEventTypeBCSM() {
		assertEquals(expectedValues.eventTypeBCSM, erBcsm.getEventTypeBCSM().getValue());
	}

	public void testGetEventSpecificInformationBCSM() {
		if(expectedValues.eventSpecificInformationBCSM!=null){
			assertNotNull(erBcsm.getEventSpecificInformationBCSM());
		}else{
			assertNull(erBcsm.getEventSpecificInformationBCSM());
		}
	}

	public void testIsEventSpecificInformationBCSMPresent() {
		  assertEquals(expectedValues.eventSpecificInformationBCSM!=null, erBcsm.isEventSpecificInformationBCSMPresent());
	}

	public void testGetLegID() {
		 assertTrue(Arrays.equals(expectedValues.legID, erBcsm.getLegID().getReceivingSideID().getValue()));
	}

	public void testIsLegIDPresent() {
		 assertEquals(expectedValues.legID!=null, erBcsm.isLegIDPresent());
	}

	public void testGetMiscCallInfo() {
		 assertEquals(expectedValues.miscCallInfo, erBcsm.getMiscCallInfo().getMessageType().getValue());
	}

	public void testGetExtensions() {
		if(expectedValues.extensions!=null){
			assertNotNull(erBcsm.getExtensions());
		}else{
			assertNull(erBcsm.getExtensions());
		}
	}

	public void testIsExtensionsPresent() {
		assertEquals(expectedValues.extensions!=null, erBcsm.isExtensionsPresent());
	}

}
