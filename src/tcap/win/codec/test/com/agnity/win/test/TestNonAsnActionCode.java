package com.agnity.win.test;

import java.util.LinkedList;

import junit.framework.TestCase;

import com.agnity.win.datatypes.NonASNActionCode;
import com.agnity.win.enumdata.ActionCodeEnum;
import com.agnity.win.exceptions.InvalidInputException;
import com.agnity.win.util.Util;

public class TestNonAsnActionCode extends TestCase {
	
	LinkedList  <ActionCodeEnum> actionCode = new LinkedList<ActionCodeEnum> ();
	byte[] bytesToDecode = null;
	int codes[];
		
	
public void setUp() throws Exception {
		
		// Input values
	actionCode.add(ActionCodeEnum.NOT_USED);
	actionCode.add(ActionCodeEnum.CONTINUE_PROCESSING);
	actionCode.add(ActionCodeEnum.DISCONNECT_CALL);
	actionCode.add(ActionCodeEnum.DISCONNECT_CALL_LEG);
	actionCode.add(ActionCodeEnum.CONF_CALLING_DROP_LAST_PARTY);
	actionCode.add(ActionCodeEnum.BRIDGE_CALL_LEG_TO_CONF_CALL);
	actionCode.add(ActionCodeEnum.DROP_CALL_LEG_BUSY_ROUTINGFAILURE);
	actionCode.add(ActionCodeEnum.DISCONNECT_ALL_CALL_LEG);
	actionCode.add(ActionCodeEnum.RELEASE_LEG_REDIRECT_SUBSCRIBER);
	actionCode.add(ActionCodeEnum.PRESENT_DISPLAY_TEXT_TO_CALLING_MS);
	
	    byte[] a = {(byte)0x00,(byte)0x01,(byte)0x02, (byte)0x03,(byte)0x04,(byte)0x05,(byte)0x06,(byte)0x07,(byte)0x14,(byte)0x18};
	    int[] c = {0,1,2,3,4,5,6,7,20,24};
	    bytesToDecode = a;
	    codes = c;
}
	
/*
 * Test encoding all possible values of Type Of ActionCodes
 */

	public void testEncodeActionCode() throws Exception{
		byte[] b = null;

		try {
			b = NonASNActionCode.encodeActionCode(actionCode);
		}
		catch(InvalidInputException ex){
			assertFalse(true);
			ex.printStackTrace();
		}
		System.out.println("Encoded NonASNActionCode: "+ Util.formatBytes(b));
		for(int i = 0; i<10; i++)
		{		
			assertEquals("NonASNActionCode is maching",bytesToDecode[i],b[i]);
		}
		
	}

	/*
	 * Testing decoding of Action Codes
	 */
	public void testDecodeActionCode() throws Exception{
		
		try {
			NonASNActionCode ac = NonASNActionCode.decodeActionCode(bytesToDecode);
			System.out.println("Decoded NonASNActionCode: "+ ac.toString());
			
	
			for(int i = 0; i<10; i++)
			{		System.out.println("NonASNActionCode: "+ ac.getActionCode().get(i));
				assertEquals("NonASNActionCode is maching", ac.getActionCode().get(i).getCode(),codes[i]);
			}
		
		}
		catch(InvalidInputException ex){
			assertFalse(true);
			ex.printStackTrace();
		}
	}
}
