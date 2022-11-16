package com.agnity.cap.v2.unitTest.nonAsn.DataTypes;

import java.util.Arrays;

import asnGenerated.v2.InitialDPArg;

import com.agnity.cap.v2.datatypes.CallingPartyCategoryCapV2;
import com.agnity.cap.v2.datatypes.LocationNumberCapV2;
import com.agnity.cap.v2.exceptions.InvalidInputException;
import com.agnity.cap.v2.unitTest.expectedValues.InitialDPArgExpectedValues;

import junit.framework.TestCase;

public class LocationNumberCapV2Test extends TestCase {

/*	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testGetOddEvenIndicator() {
		fail("Not yet implemented");
	}

	public void testGetNatureOfAddressIndicator() {
		fail("Not yet implemented");
	}

	public void testGetInnIndicator() {
		fail("Not yet implemented");
	}

	public void testGetNumberingPlanIndicator() {
		fail("Not yet implemented");
	}

	public void testGetAddPreResIndicator() {
		fail("Not yet implemented");
	}

	public void testGetScreeningIndicator() {
		fail("Not yet implemented");
	}

	public void testGetLocationNumber() {
		fail("Not yet implemented");
	}

	public void testDecodeParameters() {
		fail("Not yet implemented");
	}

	public void testEncode() {
		fail("Not yet implemented");
	}*/
	
	public void testDecodeEncode() throws InvalidInputException{
		InitialDPArgExpectedValues expt =  InitialDPArgExpectedValues.getObject();
		expt.setDefaultValues();
		InitialDPArg ida = (InitialDPArg) expt.decode();
		byte[] bytesD = ida.getLocationNumber().getValue(); 
		LocationNumberCapV2 obj = LocationNumberCapV2.decode(bytesD);
		byte[] bytesI = LocationNumberCapV2.encode(obj);
		//System.out.println("result::"+Arrays.equals(bytesI,bytesD ));
		assertTrue(Arrays.equals(bytesI,bytesD ));
	}

}
