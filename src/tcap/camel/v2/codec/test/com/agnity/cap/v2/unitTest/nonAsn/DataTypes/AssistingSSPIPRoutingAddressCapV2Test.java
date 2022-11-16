package com.agnity.cap.v2.unitTest.nonAsn.DataTypes;

import java.util.Arrays;

import com.agnity.cap.v2.datatypes.AssistingSSPIPRoutingAddressCapV2;
import com.agnity.cap.v2.exceptions.InvalidInputException;

import junit.framework.TestCase;

public class AssistingSSPIPRoutingAddressCapV2Test extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testDecodeEncode() throws InvalidInputException {
		byte[] value = new byte[]{(byte)0x06,(byte)0x04,(byte)0x13,(byte)0x79,(byte)0x52,(byte)0x97,(byte)0x30,(byte)0x96,(byte)0x20};
		byte[] exValue = AssistingSSPIPRoutingAddressCapV2.encode(AssistingSSPIPRoutingAddressCapV2.decode(value));
	    assertTrue(Arrays.equals(value, exValue));
	}
}
