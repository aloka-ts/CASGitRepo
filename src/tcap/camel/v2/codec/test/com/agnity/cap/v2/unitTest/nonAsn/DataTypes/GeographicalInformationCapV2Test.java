package com.agnity.cap.v2.unitTest.nonAsn.DataTypes;


import java.util.Arrays;

import junit.framework.TestCase;

import org.junit.Before;

import com.agnity.cap.v2.datatypes.GeographicalInformationCapV2;
import com.agnity.cap.v2.exceptions.InvalidInputException;
import com.agnity.cap.v2.util.CapFunctions;

public class GeographicalInformationCapV2Test extends TestCase{

	@Before
	public void setUp() throws Exception {
		
	}

	public void testDecodeEncode() throws InvalidInputException{
		byte[] bytes = CapFunctions.hexStringToByteArray("204a5682f463910a");
		byte[] exByte = GeographicalInformationCapV2.encode(GeographicalInformationCapV2.decode(bytes));
		assertTrue(Arrays.equals(bytes, exByte));
	}
	
}
