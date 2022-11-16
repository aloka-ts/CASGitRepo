package com.agnity.win.test;
import com.agnity.win.datatypes.NonASNCDMAServiceOption;
import com.agnity.win.exceptions.InvalidInputException;
import com.agnity.win.util.Util;

import junit.framework.TestCase;

public class TestNonAsnCDMAServiceOption extends TestCase {
	
	
 int CDMAServiceOptionVal[] = null;
 byte[][] bytesToDecode = null;
	
public void setUp() throws Exception {
		// Input values
	    byte[][] a = {{0x00,0x2a},{0x00,0x02},{0x00,0x1f},{0x00,0x03} };
	    int[] c = {42,2,31,3};
	    
	    bytesToDecode = a;
	    CDMAServiceOptionVal = c;
}
	
	/*
	 * Test encoding all possible values of CDMAServiceOption
	 */
	public void testEncodeCDMAServiceOption() throws Exception{
		byte[] b = null;

		try {
			for (int i=0;i<4;i++)
			{
			b = NonASNCDMAServiceOption.encodeCDMAServiceOption(CDMAServiceOptionVal[i]);
			System.out.println("Encoded CDMAServiceOption: "+ Util.formatBytes(b));
			}
		}
		catch(InvalidInputException ex){
			assertFalse(true);
			ex.printStackTrace();
		}
		
	}

	/*
	 * Testing decoding of CDMAServiceOption
	 */
	public void testDecodeCDMAServiceOption() throws Exception{
		try {
			for (int i =0; i<4;i++)
			{NonASNCDMAServiceOption cdmaSerOp = NonASNCDMAServiceOption.decodeCDMAServiceOption(bytesToDecode[i]);
			System.out.println("Decoded CDMAServiceOption: "+ cdmaSerOp.toString());
			assertEquals("CDMAServiceOptionVal is maching", cdmaSerOp.getCDMAServiceOption(), CDMAServiceOptionVal[i]);
			}
		}
		catch(InvalidInputException ex){
			assertFalse(true);
			ex.printStackTrace();
		}
	}

}

