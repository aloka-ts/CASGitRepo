package com.agnity.cap.v2.unitTest.nonAsn.DataTypes;

import java.util.Arrays;

import com.agnity.cap.v2.datatypes.RedirectionInformationCapV2;
import com.agnity.cap.v2.exceptions.InvalidInputException;

import junit.framework.TestCase;

public class RedirectionInformationCapV2Test extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}
	
	public void testDecodeEncode() throws InvalidInputException {
		byte[] value= new byte[]{(byte)0x04,(byte)0x00};
		byte[] exvalue = RedirectionInformationCapV2.encode(RedirectionInformationCapV2.decode(value));
	    assertTrue(Arrays.equals(value, exvalue));
	}

}
