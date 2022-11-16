package com.agnity.win.test;

import junit.framework.TestCase;

import com.agnity.win.datatypes.NonASNLocationAreaID;
import com.agnity.win.exceptions.InvalidInputException;
import com.agnity.win.util.Util;

public class TestNonAsnLocationAreaID extends TestCase {
	
 int locationAreaID ;
 byte[] bytesToDecode = null;
	
	
public void setUp() throws Exception {
		
		// Input values
	// Input set 1
	  byte[] a = {(byte)0x01,(byte)0x02};
	  locationAreaID= 258;
	        
	 // Input set 2
	   /* byte[] a = {(byte)0x00,(byte)0x1d};
	    locationAreaID= 29;
	*/
	 // Input set 3
/*	  byte[] a = {(byte)0x02,(byte)0xf4 };
	   locationAreaID= 756;*/
	    
	    
	 // Input set 4
	/*   byte[] a = {(byte)0x01,(byte)0xfa};
	   locationAreaID = 506;*/
	    
	    bytesToDecode = a;
}

	/*
	 * Test encoding all possible values of LocationAreaID 
	 */
	public void testEncodeLocationAreaID () throws Exception{
		byte[] b = null;

		try {
			b = NonASNLocationAreaID.encodeLocationAreaID(locationAreaID);
		}
		catch(InvalidInputException ex){
			assertFalse(true);
			ex.printStackTrace();
		}
		System.out.println("Encoded LocationAreaID: "+ Util.formatBytes(b));
	}

	/*
	 * Testing decoding of LocationAreaID
	 */
	public void testDecodeLocationAreaID() throws Exception{
		try {
			NonASNLocationAreaID scID = NonASNLocationAreaID.decodeLocationAreaID(bytesToDecode);
			System.out.println("Decoded LocationAreaID: "+ scID.toString());
			assertEquals("LocationAreaID is maching", scID.getLocationAreaID(), locationAreaID);
		}
		catch(InvalidInputException ex){
			assertFalse(true);
			ex.printStackTrace();
		}
	}
}
