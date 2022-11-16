package com.agnity.cap.v2.unitTest.nonAsn.DataTypes;

import java.util.Arrays;

import com.agnity.cap.v2.datatypes.LAIFixedLengthCapV2;
import com.agnity.cap.v2.exceptions.InvalidInputException;
import com.agnity.cap.v2.util.CapFunctions;

import junit.framework.TestCase;

public class LAIFixedLengthCapV2Test extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}
	
	public void testDecodeEncode() throws InvalidInputException{
		byte[] bytes = CapFunctions.hexStringToByteArray("3467218945");
		byte[] exByte = LAIFixedLengthCapV2.encode(LAIFixedLengthCapV2.decode(bytes));
		assertTrue(Arrays.equals(bytes, exByte));
	}

}
