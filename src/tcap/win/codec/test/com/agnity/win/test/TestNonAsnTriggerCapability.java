package com.agnity.win.test;

import junit.framework.TestCase;

import com.agnity.win.datatypes.NonASNTriggerCapability;
import com.agnity.win.exceptions.InvalidInputException;
import com.agnity.win.util.Util;

public class TestNonAsnTriggerCapability extends TestCase {

	byte[] bytesToDecode = null;
	byte trigCapParam[] = null;
	int codes[][];
		
	
public void setUp() throws Exception {
		
		// All possible Input values

	// input set 1
	    byte[] a = {(byte)0x00,(byte)0x00,(byte)0x00};
	    byte[] c = {0,0,0,0,0,0,0,0,0,0,0};
	    
	 // input set 2
/*	    byte[] a = {(byte)0x0f,(byte)0x0f,(byte)0x07};
	    byte[] c = {1,1,1,1,1,1,1,1,1,1,1};*/
	    
	    bytesToDecode = a;
	    trigCapParam = c;
}	
	/*
	 * Test encoding all possible values of NonASNTriggerCapability
	 */
	public void testEncodeWINOperationCapability() throws Exception{
		byte[] b = null;
		try {
			b = NonASNTriggerCapability.encodeTriggerCapability(trigCapParam[0],trigCapParam[1],trigCapParam[2],trigCapParam[3],
					trigCapParam[4],trigCapParam[5],trigCapParam[6],trigCapParam[7],trigCapParam[8],trigCapParam[9],trigCapParam[10]);
		}
		catch(InvalidInputException ex){
			assertFalse(true);
			ex.printStackTrace();
		}
		System.out.println("Encoded NonASNTriggerCapability: "+ Util.formatBytes(b));
		for(int i = 0; i<bytesToDecode.length; i++)
		{		
			assertEquals("NonASNTriggerCapability is maching",b[i],bytesToDecode[i]);
		}
	}

	/*
	 * Testing decoding of NonASNTriggerCapability
	 */
	public void testDecodeWINOperationCapability() throws Exception{
		try {
			NonASNTriggerCapability winOpCap = NonASNTriggerCapability.decodeTriggerCapability(bytesToDecode);
			
			System.out.println("Decoded NonASNTriggerCapability: "+ winOpCap.toString());		
			assertEquals("Init_can_be_armed is maching",winOpCap.getInit_can_be_armed(),trigCapParam[0]);
			assertEquals("KDigit_can_be_armed is maching",winOpCap.getKDigit_can_be_armed(),trigCapParam[1]);
			assertEquals("All_can_be_armed is maching",winOpCap.getAll_can_be_armed(),trigCapParam[2]);
			assertEquals("Rvtc_can_be_armed is maching",winOpCap.getRvtc_can_be_armed(),trigCapParam[3]);
			assertEquals("Ct_can_be_armed is maching",winOpCap.getCt_can_be_armed(),trigCapParam[4]);
			assertEquals("Unrec_can_be_armed is maching",winOpCap.getUnrec_can_be_armed(),trigCapParam[5]);
			assertEquals("Pa_can_be_armed is maching",winOpCap.getPa_can_be_armed(),trigCapParam[6]);
			assertEquals("At_can_be_armed is maching",winOpCap.getAt_can_be_armed(),trigCapParam[7]);
			assertEquals("Tra_can_be_armed is maching",winOpCap.getTra_can_be_armed(),trigCapParam[8]);
			assertEquals("Tbusy_can_be_armed is maching",winOpCap.getTbusy_can_be_armed(),trigCapParam[9]);
			assertEquals("Tna_can_be_armed is maching",winOpCap.getTna_can_be_armed(),trigCapParam[10]);
		}
		catch(InvalidInputException ex){
			assertFalse(true);
			ex.printStackTrace();
		}
	}
}
