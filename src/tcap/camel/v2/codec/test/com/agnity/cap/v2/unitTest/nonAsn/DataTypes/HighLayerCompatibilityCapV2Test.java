package com.agnity.cap.v2.unitTest.nonAsn.DataTypes;

import java.util.Arrays;

import asnGenerated.v2.HighLayerCompatibility;
import asnGenerated.v2.InitialDPArg;

import com.agnity.cap.v2.datatypes.CallingPartyNumberCapV2;
import com.agnity.cap.v2.datatypes.HighLayerCompatibilityCapV2;
import com.agnity.cap.v2.exceptions.InvalidInputException;
import com.agnity.cap.v2.unitTest.expectedValues.InitialDPArgExpectedValues;

import junit.framework.TestCase;

public class HighLayerCompatibilityCapV2Test extends TestCase {

/*	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testGetCoadingStd() {
		fail("Not yet implemented");
	}

	public void testGetInterpretation() {
		fail("Not yet implemented");
	}

	public void testGetExtInd1() {
		fail("Not yet implemented");
	}

	public void testGetExtInd2() {
		fail("Not yet implemented");
	}

	public void testGetPreMethOfProtocolProfile() {
		fail("Not yet implemented");
	}

	public void testGetHighLayCharIden() {
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
		HighLayerCompatibility value = new HighLayerCompatibility();
		value.setValue(new byte[]{(byte)0x91,(byte)0x81});
		ida.setHighLayerCompatibility(value);
		byte[] bytesD = ida.getHighLayerCompatibility().getValue(); 
		HighLayerCompatibilityCapV2 obj = HighLayerCompatibilityCapV2.decode(bytesD);
		byte[] bytesI = HighLayerCompatibilityCapV2.encode(obj);
		//System.out.println("result::"+Arrays.equals(bytesI,bytesD ));
		assertTrue(Arrays.equals(bytesI,bytesD ));
	}
}
