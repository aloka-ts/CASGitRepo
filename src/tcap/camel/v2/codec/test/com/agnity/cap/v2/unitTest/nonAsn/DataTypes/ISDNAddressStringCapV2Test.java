package com.agnity.cap.v2.unitTest.nonAsn.DataTypes;

import java.util.Arrays;


import asnGenerated.v2.InitialDPArg;

import com.agnity.cap.v2.datatypes.ISDNAddressStringCapV2;
import com.agnity.cap.v2.datatypes.LocationNumberCapV2;
import com.agnity.cap.v2.exceptions.InvalidInputException;
import com.agnity.cap.v2.unitTest.expectedValues.InitialDPArgExpectedValues;

import junit.framework.TestCase;

public class ISDNAddressStringCapV2Test extends TestCase {

/*	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testGetExtention() {
		fail("Not yet implemented");
	}

	public void testGetNatureOfNumber() {
		fail("Not yet implemented");
	}

	public void testGetNumberPlan() {
		fail("Not yet implemented");
	}

	public void testGetAddressDigits() {
		fail("Not yet implemented");
	}

	public void testGetCountryCode() {
		fail("Not yet implemented");
	}

	public void testDecode() {
		fail("Not yet implemented");
	}

	public void testEncode() {
		fail("Not yet implemented");
	}*/

	public void testDecodeEncode() throws InvalidInputException{
		InitialDPArgExpectedValues expt =  InitialDPArgExpectedValues.getObject();
		expt.setDefaultValues();
		InitialDPArg ida = (InitialDPArg) expt.decode();
		byte[] bytesD = ida.getMscAddress().getValue().getValue(); 
		ISDNAddressStringCapV2 obj = ISDNAddressStringCapV2.decode(bytesD);
		byte[] bytesI = ISDNAddressStringCapV2.encode(obj);
		//System.out.println("result::"+Arrays.equals(bytesI,bytesD ));
		assertTrue(Arrays.equals(bytesI,bytesD ));
	}
}
