package com.agnity.win.test;

import junit.framework.TestCase;

import com.agnity.win.datatypes.NonASNServingCellID;
import com.agnity.win.exceptions.InvalidInputException;
import com.agnity.win.util.Util;

public class TestNonAsnServingCellID extends TestCase {
	
 int servingCellID ;
 byte[] bytesToDecode = null;
	
	
public void setUp() throws Exception {
		
		// Input values
	// Input set 1
	  byte[] a = {(byte)0x01,(byte)0x02};
	  servingCellID= 258;
	        
	 // Input set 2
	   /* byte[] a = {(byte)0x00,(byte)0x1d};
	    servingCellID= 29;
	*/
	 // Input set 3
/*	  byte[] a = {(byte)0x02,(byte)0xf4 };
	   servingCellID= 756;*/
	    
	    
	 // Input set 4
	/*   byte[] a = {(byte)0x01,(byte)0xfa};
	   servingCellID = 506;*/
	    
	    bytesToDecode = a;
}

	/*
	 * Test encoding all possible values of servingCellID 
	 */
	public void testEncodeServingCellID () throws Exception{
		byte[] b = null;

		try {
			b = NonASNServingCellID.encodeServingCellID(servingCellID);
		}
		catch(InvalidInputException ex){
			assertFalse(true);
			ex.printStackTrace();
		}
		System.out.println("Encoded ServingCellID: "+ Util.formatBytes(b));
	}

	/*
	 * Testing decoding of ServingCellID
	 */
	public void testDecodeServingCellID() throws Exception{
		try {
			NonASNServingCellID scID = NonASNServingCellID.decodeServingCellID(bytesToDecode);
			System.out.println("Decoded ServingCellID: "+ scID.toString());
			assertEquals("ServingCellID is maching", scID.getServingCellID(), servingCellID);
		}
		catch(InvalidInputException ex){
			assertFalse(true);
			ex.printStackTrace();
		}
	}
}
