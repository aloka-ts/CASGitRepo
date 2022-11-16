package com.agnity.cap.v2.unitTest.nonAsn.DataTypes;


import java.util.Arrays;

import junit.framework.TestCase;

import org.junit.Before;

import com.agnity.cap.v2.datatypes.NaOliInfoCapV2;
import com.agnity.cap.v2.exceptions.InvalidInputException;
import com.agnity.cap.v2.util.CapFunctions;

public class NaOliInfoCapV2Test extends TestCase {

	@Before
	public void setUp() throws Exception {
	}

	public void testDecodeEncode() throws InvalidInputException{
		byte[] bytes = CapFunctions.hexStringToByteArray("3D");
		byte[] exByte = NaOliInfoCapV2.encode(NaOliInfoCapV2.decode(bytes));
		assertTrue(Arrays.equals(bytes, exByte));
		
		byte[] bytes1 = CapFunctions.hexStringToByteArray("3E");
		byte[] exByte1 = NaOliInfoCapV2.encode(NaOliInfoCapV2.decode(bytes1));
		assertTrue(Arrays.equals(bytes1, exByte1));
		
		byte[] bytes2 = CapFunctions.hexStringToByteArray("3F");
		byte[] exByte2 = NaOliInfoCapV2.encode(NaOliInfoCapV2.decode(bytes2));
		assertTrue(Arrays.equals(bytes2, exByte2));
	}
}
