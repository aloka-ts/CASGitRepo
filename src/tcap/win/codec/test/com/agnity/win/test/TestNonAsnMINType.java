package com.agnity.win.test;

import junit.framework.TestCase;

import com.agnity.win.datatypes.NonASNMINType;
import com.agnity.win.exceptions.InvalidInputException;
import com.agnity.win.util.Util;

public class TestNonAsnMINType extends TestCase {

	 String bcdCode = null;
	 String bcdCode_invalid = null;
	 byte[] bytesToDecode = null;
	 byte[] bytesToDecode_invalid = null;
		
	public void setUp() throws Exception {
			
			// Input values
		    
		 // Input set 1
		  byte[] a = {(byte)0x43,(byte)0x67, (byte)0x01, (byte)0x89, (byte)0x67 };
		    String c = "3476109876";

		 // Input set 2
		   byte[] a1 = {(byte)0x43,(byte)0x67, (byte)0x0f, (byte)0x89, (byte)0x67 };
		    String c1 = "3476a09876";
		    
		    bytesToDecode = a;
		    bcdCode = c;
		    bytesToDecode_invalid = a1;
		    bcdCode_invalid = c1;
	}
		
	
	/*
	 * Test encoding all possible values of Type Of NonASNDigitsType
	 */
	public void testEncodeMINType() throws Exception{
		byte[] b = null;

		try {
			b = NonASNMINType.encodeMINType(bcdCode);
		}
		catch(InvalidInputException ex){
			assertFalse(true);
			ex.printStackTrace();
		}
		System.out.println("Encoded NonASNMINType: "+ Util.formatBytes(b));
	}

	/*
	 * Testing decoding of NonASNDigitsType
	 */
	public void testDecodeMINType() throws Exception{
		try {
			NonASNMINType dig = NonASNMINType.decodeMINType(bytesToDecode);
			System.out.println("Decoded NonASNDigitsType: "+ dig.toString());
			assertEquals("NonASNDigitsType are maching", dig.getMINType(), bcdCode);
		}
		catch(InvalidInputException ex){
			System.out.println(ex.getMessage());
			
			// uncomment next line for input set 2 as it is invalid input
			assertFalse(true);
			
			ex.printStackTrace();
		}
	}
	
	/*
	 * Testing decoding of NonASNDigitsType
	 */
	public void testDecodeMINType_invalid() throws Exception{
		try {
			NonASNMINType dig = NonASNMINType.decodeMINType(bytesToDecode_invalid);
			System.out.println("Decoded NonASNDigitsType: "+ dig.toString());
			assertEquals("NonASNDigitsType are maching", dig.getMINType(), bcdCode_invalid);
		}
		catch(InvalidInputException ex){
			System.out.println(ex.getMessage());
			
			// uncomment next line for input set 2 as it is invalid input
			assertFalse(true);
			
			ex.printStackTrace();
		}
	}
}
