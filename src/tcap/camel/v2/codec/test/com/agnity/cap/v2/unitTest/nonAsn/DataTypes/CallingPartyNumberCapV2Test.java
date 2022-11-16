package com.agnity.cap.v2.unitTest.nonAsn.DataTypes;

import java.util.Arrays;

import org.junit.Test;

import asnGenerated.v2.InitialDPArg;

import com.agnity.cap.v2.datatypes.CallingPartyCategoryCapV2;
import com.agnity.cap.v2.datatypes.CallingPartyNumberCapV2;
import com.agnity.cap.v2.exceptions.InvalidInputException;
import com.agnity.cap.v2.unitTest.expectedValues.InitialDPArgExpectedValues;

import junit.framework.TestCase;

public class CallingPartyNumberCapV2Test extends TestCase {

/*	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testGetOddEvenIndicator() {
		fail("Not yet implemented");
	}

	public void testGetNatureOfAddressIndicator() {
		fail("Not yet implemented");
	}

	public void testGetNiIndicator() {
		fail("Not yet implemented");
	}

	public void testGetNumberingPlanIndicator() {
		fail("Not yet implemented");
	}

	public void testGetAddressPresentationRestricatedIndicator() {
		fail("Not yet implemented");
	}

	public void testGetScreeningIndicator() {
		fail("Not yet implemented");
	}

	public void testGetCallingPartyNumber() {
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
		byte[] bytesD = ida.getCallingPartyNumber().getValue(); 
		CallingPartyNumberCapV2 obj = CallingPartyNumberCapV2.decode(bytesD);
		byte[] bytesI = CallingPartyNumberCapV2.encode(obj);
		/*System.out.println("result::"+Arrays.equals(bytesI,bytesD ));
		for(int i=0;i< bytesI.length;i++){
			System.out.println(bytesI[i] +"=="+bytesD[i]);
		}*/
		assertTrue(Arrays.equals(bytesI,bytesD ));
	}

}
