package com.agnity.win.test;

import junit.framework.TestCase;

import com.agnity.win.datatypes.NonASNMSCID;
import com.agnity.win.exceptions.InvalidInputException;
import com.agnity.win.util.Util;

public class TestNonAsnMSCID extends TestCase {
	
	// MSCID Parameters[] = {Market ID, Switch No}
 int mscidParameters[] = null;
 byte[] bytesToDecode = null;
	
	
public void setUp() throws Exception {
		
		// Input values
	// Input set 1
	 /*  byte[] a = {(byte)0x01,(byte)0x02,(byte)0x05};
	    int[] c = {258,5};*/
	        
	 // Input set 2
	   /* byte[] a = {(byte)0x01,(byte)0x92,(byte)0x1d };
	    int[] c = {402,29};*/
	
	 // Input set 3
	/*   byte[] a = {(byte)0x02,(byte)0xf4,(byte)0x01 };
	    int[] c = {756,1};*/
	    
	    
	 // Input set 4
	   byte[] a = {(byte)0x01,(byte)0xfa,(byte)0x1a};
	    int[] c = {506,26};
	    
	    bytesToDecode = a;
	    mscidParameters = c;
}

	/*
	 * Test encoding all possible values of MSCID 
	 */
	public void testEncodeMSCID () throws Exception{
		byte[] b = null;

		try {
			b = NonASNMSCID.encodeMSCID((short)mscidParameters[0], mscidParameters[1]);
		}
		catch(InvalidInputException ex){
			assertFalse(true);
			ex.printStackTrace();
		}
		System.out.println("Encoded MSCID: "+ Util.formatBytes(b));
	}

	/*
	 * Testing decoding of MSCID
	 */
	public void testDecodeMSCID() throws Exception{
		try {
			NonASNMSCID bd = NonASNMSCID.decodeMSCID(bytesToDecode);
			System.out.println("Decoded MSCID: "+ bd.toString());
			assertEquals("MarketID is maching", bd.getMarketID(), mscidParameters[0]);
			assertEquals("SwitchNo is maching", bd.getSwitchNo(), mscidParameters[1]);
		}
		catch(InvalidInputException ex){
			assertFalse(true);
			ex.printStackTrace();
		}
	}
}
