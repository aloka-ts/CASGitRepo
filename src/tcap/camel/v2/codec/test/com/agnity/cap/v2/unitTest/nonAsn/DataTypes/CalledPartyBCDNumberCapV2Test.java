package com.agnity.cap.v2.unitTest.nonAsn.DataTypes;

import java.util.Arrays;


import asnGenerated.v2.InitialDPArg;

import com.agnity.cap.v2.datatypes.CalledPartyBCDNumberCapV2;
import com.agnity.cap.v2.exceptions.InvalidInputException;
import com.agnity.cap.v2.unitTest.expectedValues.InitialDPArgExpectedValues;

import junit.framework.TestCase;

public class CalledPartyBCDNumberCapV2Test extends TestCase {

/*	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testGetExtention() {
		fail("Not yet implemented");
	}

	public void testGetTypeOfNumber() {
		fail("Not yet implemented");
	}

	public void testGetNumberingPlanIdentification() {
		fail("Not yet implemented");
	}

	public void testGetBcdDigits() {
		fail("Not yet implemented");
	}

	public void testDecode() {
		fail("Not yet implemented");
	}

	public void testEncode() {
		fail("Not yet implemented");
	}*/
	
	public void testDecodeEncode() throws InvalidInputException {
		InitialDPArgExpectedValues expt =  InitialDPArgExpectedValues.getObject();
		expt.setDefaultValues();
		InitialDPArg ida = (InitialDPArg) expt.decode();
		byte[] calledPartyByte = ida.getCalledPartyBCDNumber().getValue(); 
		CalledPartyBCDNumberCapV2 calledObj = CalledPartyBCDNumberCapV2.decode(calledPartyByte);
		byte[] bytes = CalledPartyBCDNumberCapV2.encode(calledObj);
		//System.out.println("result::"+Arrays.equals(calledPartyByte, bytes));
		assertTrue(Arrays.equals(calledPartyByte, bytes));
	}

}
