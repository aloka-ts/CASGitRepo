package com.agnity.win.test;

import junit.framework.TestCase;

import com.agnity.win.datatypes.NonASNIMSIType;
import com.agnity.win.exceptions.InvalidInputException;
import com.agnity.win.util.Util;

public class TestNonAsnIMSIType extends TestCase {


	 String bcdCode = null;
	 byte[] bytesToDecode = null;
		
	public void setUp() throws Exception {
			
			// Input values
		    
		 // Input set 1
		  /* byte[] a = {(byte)0x43,(byte)0x67, (byte)0x01, (byte)0x89, (byte)0x67 };
		    String c = "3476109876";    
		     */
	    
		 // Input set 2  
		   byte[] a = {(byte)0x59,(byte)0x67, (byte)0x06, (byte)0x89, (byte)0x57 ,(byte)0x54, (byte)0x76, (byte)0xf8};
		    String c = "957660987545678f";
		    
		 // Input set 3  invalid input : data more than 8 bytes
		  /* byte[] a = {(byte)0x43,(byte)0x67 ,(byte)0x05 ,(byte)0x89 ,(byte)0x67 ,(byte)0x54 ,(byte)0x76 ,(byte)0x77 ,(byte)0x06 };
		    String c = "34765098764567776";
		*/
		    bytesToDecode = a;
		    bcdCode = c;
	}
		
	
	/*
	 * Test encoding all possible values of Type Of NonASNDigitsType
	 */
	public void testEncodeIMSIType() throws Exception{
		byte[] b = null;

		try {
			b = NonASNIMSIType.encodeIMSIType(bcdCode);
		}
		catch(InvalidInputException ex){
			assertFalse(true);
			ex.printStackTrace();
		}
		System.out.println("Encoded IMSIType: "+ Util.formatBytes(b));
	}

	/*
	 * Testing decoding of NonASNDigitsType
	 */
	public void testDecodeIMSIType() throws Exception{
		try {
			NonASNIMSIType dig = NonASNIMSIType.decodeIMSIType(bytesToDecode);
			System.out.println("Decoded IMSIType: "+ dig.toString());
			assertEquals("IMSIType are maching", dig.getIMSIType(), bcdCode);
		}
		catch(InvalidInputException ex){
			System.out.println(ex.getMessage());
			
			// comment next line for input set 2 as it is invalid input
			assertFalse(true);
			
			ex.printStackTrace();
		}
	}
}
