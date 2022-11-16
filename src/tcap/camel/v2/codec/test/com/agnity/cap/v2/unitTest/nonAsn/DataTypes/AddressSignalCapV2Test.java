package com.agnity.cap.v2.unitTest.nonAsn.DataTypes;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.agnity.cap.v2.datatypes.AddressSignalCapV2;
import com.agnity.cap.v2.exceptions.InvalidInputException;

public class AddressSignalCapV2Test {

	@Before
	public void setUp() throws Exception {
		
	}

	/*@Test
	public void testEncode() {
		fail("Not yet implemented");
	}

	@Test
	public void testDecode() {
		fail("Not yet implemented");
	}*/
	
	@Test
	public void testEncodeDecode() throws InvalidInputException {
		String addressSignal = "12987597463922";
		byte[] bytes = AddressSignalCapV2.encode(addressSignal);
		String addSigStr = AddressSignalCapV2.decode(bytes, 0);
		byte[] bytes2 = AddressSignalCapV2.encode(addSigStr);
		
		assertArrayEquals(bytes, bytes2);
		assertEquals(addressSignal, addSigStr);
		
	}

}
