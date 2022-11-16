package com.agnity.cap.v2.unitTest.nonAsn.DataTypes;

import java.util.Arrays;

import com.agnity.cap.v2.datatypes.CorrelationIdCapV2;
import com.agnity.cap.v2.exceptions.InvalidInputException;

import junit.framework.TestCase;

public class CorrelationIdCapV2Test extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testDecodeEncode() throws InvalidInputException {
		byte[] value = new byte[]{(byte)0x63,(byte)0x79,(byte)0x52,(byte)0x97,(byte)0x30,(byte)0x96,(byte)0x20};
	    byte[] exValue = CorrelationIdCapV2.encode(CorrelationIdCapV2.decode(value));
	    assertTrue(Arrays.equals(value,exValue));
	}
}
