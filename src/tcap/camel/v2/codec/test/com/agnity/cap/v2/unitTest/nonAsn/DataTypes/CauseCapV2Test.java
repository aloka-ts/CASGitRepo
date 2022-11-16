package com.agnity.cap.v2.unitTest.nonAsn.DataTypes;

import java.util.Arrays;

import com.agnity.cap.v2.datatypes.CauseCapV2;
import com.agnity.cap.v2.exceptions.InvalidInputException;

import junit.framework.TestCase;

public class CauseCapV2Test extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testDecodeEncode() throws InvalidInputException {
		byte[] value = new byte[]{(byte)0x80,(byte)0x90};
		byte[] exValue = CauseCapV2.encode(CauseCapV2.decode(value));
		assertTrue(Arrays.equals(value, exValue));
	}
}
