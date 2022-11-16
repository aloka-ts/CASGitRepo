package com.agnity.cap.v2.unitTest.nonAsn.DataTypes;

import java.util.Arrays;

import com.agnity.cap.v2.datatypes.LegTypeCapV2;
import com.agnity.cap.v2.exceptions.InvalidInputException;
import com.agnity.cap.v2.util.CapFunctions;

import junit.framework.TestCase;

public class LegTypeCapV2Test extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}
	
	public void testDecodeEncode() throws InvalidInputException{
		byte[] bytes = CapFunctions.hexStringToByteArray("01");
		byte[] exByte = LegTypeCapV2.encode(LegTypeCapV2.decode(bytes));
		assertTrue(Arrays.equals(bytes, exByte));
		
		byte[] bytes1 = CapFunctions.hexStringToByteArray("02");
		byte[] exByte1 = LegTypeCapV2.encode(LegTypeCapV2.decode(bytes1));
		assertTrue(Arrays.equals(bytes1, exByte1));
	}

}
