package com.agnity.cap.v2.unitTest.nonAsn.DataTypes;

import java.util.Arrays;

import asnGenerated.v2.InitialDPArg;

import com.agnity.cap.v2.datatypes.CalledPartyNumberCapV2;
import com.agnity.cap.v2.datatypes.CallingPartyCategoryCapV2;
import com.agnity.cap.v2.exceptions.InvalidInputException;
import com.agnity.cap.v2.unitTest.expectedValues.InitialDPArgExpectedValues;

import junit.framework.TestCase;

public class CallingPartyCategoryCapV2Test extends TestCase {

/*	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testGetCallingPartyCategory() {
		fail("Not yet implemented");
	}

	public void testDecode() {
		fail("Not yet implemented");
	}

	public void testEncode() {
		fail("Not yet implemented");
	}*/
	
	public void testDecodeEncode() throws InvalidInputException  {
		InitialDPArgExpectedValues expt =  InitialDPArgExpectedValues.getObject();
		expt.setDefaultValues();
		InitialDPArg ida = (InitialDPArg) expt.decode();
		byte[] bytesD = ida.getCallingPartysCategory().getValue(); 
		CallingPartyCategoryCapV2 obj = CallingPartyCategoryCapV2.decode(bytesD);
		byte[] bytesI = CallingPartyCategoryCapV2.encode(obj);
		//System.out.println("result::"+Arrays.equals(bytesI,bytesD ));
		assertTrue(Arrays.equals(bytesI,bytesD));
	}
}
