package com.agnity.cap.v2.unitTest.nonAsn.DataTypes;

import java.util.Arrays;

import com.agnity.cap.v2.datatypes.IPSSPCapabilitiesCapV2;
import com.agnity.cap.v2.exceptions.InvalidInputException;
import com.agnity.cap.v2.util.CapFunctions;

import junit.framework.TestCase;

public class IPSSPCapabilitiesCapV2Test extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}
	
	public void testDecodeEncode() throws InvalidInputException {
		String byteArrayString = "f832150f11";
		byte[] bytes = CapFunctions.hexStringToByteArray(byteArrayString);
		IPSSPCapabilitiesCapV2 ips = IPSSPCapabilitiesCapV2.decode(bytes);
		byte[] encodedBytes = IPSSPCapabilitiesCapV2.encode(ips);
		assertTrue(Arrays.equals(bytes, encodedBytes));
	}

}
