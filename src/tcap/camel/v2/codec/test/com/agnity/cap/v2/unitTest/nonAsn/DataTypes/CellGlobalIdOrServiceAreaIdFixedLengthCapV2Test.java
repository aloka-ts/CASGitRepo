package com.agnity.cap.v2.unitTest.nonAsn.DataTypes;


import java.util.Arrays;

import junit.framework.TestCase;

import org.junit.Before;

import com.agnity.cap.v2.datatypes.CellGlobalIdOrServiceAreaIdFixedLengthCapV2;
import com.agnity.cap.v2.exceptions.InvalidInputException;
import com.agnity.cap.v2.util.CapFunctions;

public class CellGlobalIdOrServiceAreaIdFixedLengthCapV2Test extends TestCase {

	@Before
	public void setUp() throws Exception {
	}
	
	public void testDecodeEncode() throws InvalidInputException{
		byte[] bytes = CapFunctions.hexStringToByteArray("34672189455690");
		byte[] exByte = CellGlobalIdOrServiceAreaIdFixedLengthCapV2.encode(CellGlobalIdOrServiceAreaIdFixedLengthCapV2.decode(bytes));
		
		assertTrue(Arrays.equals(bytes, exByte));
	}
	
}