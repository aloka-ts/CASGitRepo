package com.agnity.win.test;

import junit.framework.TestCase;

import com.agnity.win.datatypes.NonASNBillingID;
import com.agnity.win.exceptions.InvalidInputException;
import com.agnity.win.util.Util;

public class TestNonAsnBillingID extends TestCase {
	
	// billingParameters[] = {Originating Market ID,Originating Switch No,Id no,Segment Counter}
 int billingParameters[] = null;
 byte[] bytesToDecode = null;
	
public void setUp() throws Exception {
		
		// Input values
	// Input set 1
	  /*  byte[] a = {(byte)0x00,(byte)0x12,(byte)0x05,(byte)0x00,(byte)0x00,  (byte)0x06, (byte)0x09 };
	    int[] c = {18,5,6,9};*/
	        
	 // Input set 2
	    byte[] a = {(byte)0x00,(byte)0x2a,(byte)0x02,(byte)0x00,(byte)0x00,  (byte)0x02, (byte)0x03 };
	    int[] c = {42,2,2,3};
	
	 // Input set 3
	   /* byte[] a = {(byte)0x01,(byte)0xc8,(byte)0x06,(byte)0x00,(byte)0x03,(byte)0x84, (byte)0x03 };
	    int[] c = {456,6,900,3};*/
	    
	    
	 // Input set 4
	  /* byte[] a = {(byte)0x01,(byte)0xfa,(byte)0x1a,(byte)0x00,(byte)0x06,(byte)0x22, (byte)0x07 };
	    int[] c = {506,26,1570,7};*/
	    
	    bytesToDecode = a;
	    billingParameters = c;
}
	
	/*
	 * Test encoding all possible values of BillingID
	 */
	public void testEncodeBillingID() throws Exception{
		byte[] b = null;

		try {
			b = NonASNBillingID.encodeBillingID((short)billingParameters[0], (short)billingParameters[1], billingParameters[2], (short)billingParameters[3]);
		}
		catch(InvalidInputException ex){
			assertFalse(true);
			ex.printStackTrace();
		}
		System.out.println("Encoded BillingID: "+ Util.formatBytes(b));
	}

	/*
	 * Testing decoding of BillingID
	 */
	public void testDecodeBillingID() throws Exception{
		try {
			NonASNBillingID bd = NonASNBillingID.decodeBillingID(bytesToDecode);
			System.out.println("Decoded BillingID: "+ bd.toString());
			assertEquals("OriginatingMarketID is maching", bd.getOriginatingMarketID(), billingParameters[0]);
			assertEquals("OriginatingSwitchNo is maching", bd.getOriginatingSwitchNo(), billingParameters[1]);
			assertEquals("IdNo is maching", bd.getIdNo(), billingParameters[2]);
			assertEquals("SegmentCounter is maching", bd.getSegmentCounter(), billingParameters[3]);
		}
		catch(InvalidInputException ex){
			assertFalse(true);
			ex.printStackTrace();
		}
	}
}
