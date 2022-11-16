package com.agnity.cap.v2.unitTest.nonAsn.DataTypes;

import java.util.Arrays;

import com.agnity.cap.v2.datatypes.OriginalCalledPartyIdCapV2;
import com.agnity.cap.v2.exceptions.InvalidInputException;
import com.agnity.cap.v2.util.CapFunctions;

import junit.framework.TestCase;

public class OriginalCalledPartyIdCapV2Test extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testDecodeEncode() throws InvalidInputException {
		String byteStr = "864c58617014";
		byte[] bytes = CapFunctions.hexStringToByteArray(byteStr);
		byte[] exBytes = OriginalCalledPartyIdCapV2.encode(OriginalCalledPartyIdCapV2.decode(bytes));
		assertTrue(Arrays.equals(bytes, exBytes));
	}
}
