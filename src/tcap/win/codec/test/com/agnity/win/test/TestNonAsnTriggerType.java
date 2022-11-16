package com.agnity.win.test;

import java.util.LinkedList;

import junit.framework.TestCase;

import com.agnity.win.datatypes.NonASNTriggerType;
import com.agnity.win.enumdata.TriggerTypeEnum;
import com.agnity.win.exceptions.InvalidInputException;
import com.agnity.win.util.Util;

public class TestNonAsnTriggerType extends TestCase {
	
	//TriggerTypeEnum trigType =null;
	LinkedList  <TriggerTypeEnum> trigType = new LinkedList<TriggerTypeEnum> ();
	byte[] bytesToDecode =null ;
	int[] codes=null;
	
	// initializes global test values 
	public void setUp() throws Exception {

		// All possible Input values
	trigType.add(TriggerTypeEnum.UNSPECIFIED);
	trigType.add(TriggerTypeEnum.ALL_CALLS);
	trigType.add(TriggerTypeEnum.DOUBLE_INTRODUCING_STARS);
	trigType.add(TriggerTypeEnum.SINGLE_INTRODUCING_STARS);
	trigType.add(TriggerTypeEnum.DOUBLE_INTRODUCING_POUND);
	trigType.add(TriggerTypeEnum.SINGLE_INTRODUCING_POUND);
	trigType.add(TriggerTypeEnum.REVERTIVE_CALL);
	trigType.add(TriggerTypeEnum.DIGIT_0);
	trigType.add(TriggerTypeEnum.DIGIT_1);
	trigType.add(TriggerTypeEnum.DIGIT_2);
	trigType.add(TriggerTypeEnum.DIGIT_3);
	trigType.add(TriggerTypeEnum.DIGIT_4);
	trigType.add(TriggerTypeEnum.DIGIT_5);
	trigType.add(TriggerTypeEnum.DIGIT_6);
	trigType.add(TriggerTypeEnum.DIGIT_7);
	trigType.add(TriggerTypeEnum.DIGIT_8);
	trigType.add(TriggerTypeEnum.DIGIT_9);
	trigType.add(TriggerTypeEnum.DIGIT_10);
	trigType.add(TriggerTypeEnum.DIGIT_11);
	trigType.add(TriggerTypeEnum.DIGIT_12);
	trigType.add(TriggerTypeEnum.DIGIT_13);
	trigType.add(TriggerTypeEnum.DIGIT_14);
	trigType.add(TriggerTypeEnum.DIGIT_15);
	trigType.add(TriggerTypeEnum.LOCAL_CALL);
	trigType.add(TriggerTypeEnum.INTRA_LATA_TOLL_CALL);
	trigType.add(TriggerTypeEnum.INTER_LATA_TOLL_CALL);
	trigType.add(TriggerTypeEnum.WORLD_ZONE_CALL);
	trigType.add(TriggerTypeEnum.INTERNATIONAL_CALL);
	trigType.add(TriggerTypeEnum.UNRECOGNIZED_NO);
	trigType.add(TriggerTypeEnum.PRIOR_AGREEMENT);
	trigType.add(TriggerTypeEnum.SPECIFIC_CALLED_PARTY_DIGIT_STRING);
	trigType.add(TriggerTypeEnum.MOBILE_TERMINATION);
	trigType.add(TriggerTypeEnum.ADVANCED_TERMINATION);
	trigType.add(TriggerTypeEnum.LOCATION);
	trigType.add(TriggerTypeEnum.LOCALLY_ALLOWED_SPECIFIC_DIGIT_STRING);
	trigType.add(TriggerTypeEnum.ORIGINATION_ATTEMPT_AUTHORIZED);
	trigType.add(TriggerTypeEnum.CALLING_ROUTING_ADDRESS_AVAILABLE);
	trigType.add(TriggerTypeEnum.INITIAL_TERMINATION);
	trigType.add(TriggerTypeEnum.CALLED_ROUTING_ADDRESS_AVAILABLE);
	trigType.add(TriggerTypeEnum.O_ANSWER);
	trigType.add(TriggerTypeEnum.O_DISCONNECT);
	trigType.add(TriggerTypeEnum.O_CALLED_PARTY_BUSY);
	trigType.add(TriggerTypeEnum.O_NO_ANSWER);
	trigType.add(TriggerTypeEnum.TERMINATING_RESOURCE_AVAILABLE);
	trigType.add(TriggerTypeEnum.T_BUSY);
	trigType.add(TriggerTypeEnum.T_NOANSWER);
	trigType.add(TriggerTypeEnum.T_NO_PAGE_RESPONSE);
	trigType.add(TriggerTypeEnum.T_UNROUTABLE);
	trigType.add(TriggerTypeEnum.T_ANSWER);
	trigType.add(TriggerTypeEnum.T_DISCONNECT);
		
	byte[] a = {(byte)0x00,(byte)0x01,(byte)0x02, (byte)0x03,(byte)0x05,(byte)0x06,(byte)0x07,(byte)0x08,(byte)0x09,
			  (byte)0x0a, (byte)0x0b, (byte)0x0c, (byte)0x0d, (byte)0x0e, (byte)0x0f, (byte)0x10, (byte)0x11, (byte)0x12, (byte)0x13,
			  (byte)0x14,(byte)0x15,(byte)0x16,(byte)0x17,(byte)0x18,(byte)0x19,(byte)0x1a,(byte)0x1b,(byte)0x1c,(byte)0x1d,
			  (byte)0x1e,(byte)0x1f,(byte)0x20,(byte)0x21,(byte)0x22,(byte)0x23,(byte)0x24,(byte)0x25,(byte)0x26,(byte)0x27,
			  (byte)0x28,(byte)0x29,(byte)0x2a,(byte)0x2b,(byte)0x40,(byte)0x41,(byte)0x42,(byte)0x43,(byte)0x44,(byte)0x45,(byte)0x46};
			  
	    int[] c = {0,1,2,3,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,
	    		36,37,38,39,40,41,42,43,64,65,66,67,68,69,70};
	    bytesToDecode = a;
	    codes = c;
	}
	
	/*
	 * Test encoding all possible values of NonASNTriggerType
	 */
	public void testEncodeTriggerType() throws Exception{
		byte[] b = null;

		
		for(int i = 0; i<trigType.size(); i++)
		{		
			
		try {
			b = NonASNTriggerType.encodeTriggerType(trigType.get(i));
		}
		catch(InvalidInputException ex){
			assertFalse(true);
			ex.printStackTrace();
		}
		System.out.println("Encoded NonASNTriggerType: "+ Util.formatBytes(b));
	
			assertEquals("NonASNTriggerType is maching",bytesToDecode[i],b[0]);
		}
	}

	/*
	 * Testing decoding of NonASNTriggerType
	 */
	public void testDecodeTriggerType() throws Exception{
		try {
			for(int i = 0; i<bytesToDecode.length; i++)
			{		
				byte b[] = new byte[1];
				b[0] = bytesToDecode[i];
			NonASNTriggerType trigTp= NonASNTriggerType.decodeTriggerType(b);
			System.out.println("Decoded NonASNTriggerType: "+ trigTp.toString());
			assertEquals("NonASNTriggerType are maching", trigTp.getTriggerTypeEnum().getCode(),codes[i]);
			}
			
		}
		catch(InvalidInputException ex){
			assertFalse(true);
			ex.printStackTrace();
		}
	}
}
