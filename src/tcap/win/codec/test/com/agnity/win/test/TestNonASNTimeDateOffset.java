package com.agnity.win.test;

import junit.framework.TestCase;

import com.agnity.win.datatypes.NonASNTimeDateOffset;
import com.agnity.win.exceptions.InvalidInputException;
import com.agnity.win.util.Util;

public class TestNonASNTimeDateOffset extends TestCase {
	
	// offsetVal = {Originating Market ID,Originating Switch No,Id no,Segment Counter}
 int offsetVal = 0;
 byte[] bytesToDecode = null;
	
public void setUp() throws Exception {
		
		// Input values
	// Input set 1
	   /* byte[] a = {(byte)0xff,(byte)0xde};
	    int c = -34;*/
	        
	 // Input set 2
	   /* byte[] a = {(byte)0x00,(byte)0x0a };
	    int c = 10;*/
	
	 // Input set 3
	   byte[] a = {(byte)0xf5,(byte)0x42 };
	    int c = -2750;
	    
	    
	    bytesToDecode = a;
	    offsetVal = c;
}
	
	/*
	 * Test encoding possible values of TimeDateOffset
	 */
	public void testEncodeTimeDateOffset() throws Exception{
		byte[] b = null;

		try {
			b = NonASNTimeDateOffset.encodeTimeDateOffset(offsetVal);
		}
		catch(InvalidInputException ex){
			System.out.println("Exception : "+ex.getMessage());
			assertFalse(true);
			ex.printStackTrace();
		}
		System.out.println("Encoded TimeDateOffset: "+ Util.formatBytes(b));
	}

	/*
	 * Testing decoding of TimeDateOffset
	 */
	public void testDecodeTimeDateOffset() throws Exception{
		try {
			NonASNTimeDateOffset bd = NonASNTimeDateOffset.decodeTimeDateOffset(bytesToDecode);
			System.out.println("Decoded TimeDateOffset: "+ bd.toString());
			assertEquals("time Date Offset is maching", bd.gettimeDateOffsetVal(), offsetVal);
		}
		catch(InvalidInputException ex){
			System.out.println("Exception : "+ex.getMessage());
			assertFalse(true);
			ex.printStackTrace();
		}
	}
}
