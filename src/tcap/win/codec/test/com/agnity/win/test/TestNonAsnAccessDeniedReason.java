package com.agnity.win.test;

import junit.framework.TestCase;

import com.agnity.win.datatypes.NonASNAccessDeniedReason;
import com.agnity.win.enumdata.AccessDeniedReasonEnum;
import com.agnity.win.exceptions.InvalidInputException;
import com.agnity.win.util.Util;

public class TestNonAsnAccessDeniedReason extends TestCase {

	
	AccessDeniedReasonEnum adr =null;
	byte[] b =null ;
	int code;
	
	// initializes global test values 
	public void setUp() throws Exception {
		
		// Input set 1
		adr = AccessDeniedReasonEnum.INACTIVE;
	    byte[] a = { (byte) 0x02 };
	    code = 2;

		
		// Input set 2
	   /* adr = AccessDeniedReasonEnum.NOT_USED;
	    byte[] a = { (byte) 0x00 };
	    code = 0;*/
	
		// Input set 3
	  /*  adr = AccessDeniedReasonEnum.UNASSIGNED_DIR_NO;
	    byte[] a = { (byte) 0x01 };
	    code = 1;*/
	    
		// Input set 4
	  /* adr = AccessDeniedReasonEnum.BUSY;
	    byte[] a = { (byte) 0x03 };
	    code =3;*/
		
		// Input set 5
	   /* 
	    adr = AccessDeniedReasonEnum.TERMINATION_DENIED;
	    byte[] a = { (byte) 0x04 };
	    code = 4;*/
	    
		
		// Input set 6
	  /*  adr = AccessDeniedReasonEnum.NO_PAGE_RESPONSE;
	    byte[] a = { (byte) 0x05 };
	    code = 5;*/
	    
		// Input set 7
	   /* adr = AccessDeniedReasonEnum.SERVICE_REJECTED_BY_MS;
	    byte[] a = { (byte) 0x07 };
	    code = 7;*/
	    
		// Input set 8
	/*    adr = AccessDeniedReasonEnum.SERVICE_REJECTED_BY_SYSTEM;
	    byte[] a = { (byte) 0x08 };
	    code = 8;
	    */
	    
		// Input set 9
	  /*  adr = AccessDeniedReasonEnum.SERVICE_TYPE_MISMATCH;
	    byte[] a = { (byte) 0x09 };
	    code = 9;
	    */
	    
		// Input set 10
	   /* adr = AccessDeniedReasonEnum.SERVICE_DENIED;
	    byte[] a = { (byte) 0x0a };
	    code = 10;*/
	    
	    
		// Input set 11
	   /* adr = AccessDeniedReasonEnum.UNAVAILABLE;
	    byte[] a = { (byte) 0x0b };
	    code = 11;*/
	    
	b=a;
	}
	
	/*
	 * Test encoding all possible values of NonASNAccessDeniedReason
	 */
	public void testEncodeAccessDeniedReason() throws Exception {
		byte[] b = null;
		try {
			b = NonASNAccessDeniedReason.encodeAccessDeniedReason(adr);
		} catch (InvalidInputException ex) {
			assertFalse(true);
			ex.printStackTrace();
		}
		System.out
				.println("Encoded NonASNAccessDeniedReason: " + Util.formatBytes(b));

	}

	/*
	 * Testing decoding of NonASNAccessDeniedReason
	 */
	public void testDecodeAccessDeniedReason() throws Exception {
		try {
			NonASNAccessDeniedReason adr = NonASNAccessDeniedReason
					.decodeAccessDeniedReason(b);
			assertEquals("NonASNAccessDeniedReason are maching", adr
					.getAccessDeniedReasonEnum().getCode(), code);
		} catch (InvalidInputException ex) {
			assertFalse(true);
			ex.printStackTrace();
		}
	}

}
