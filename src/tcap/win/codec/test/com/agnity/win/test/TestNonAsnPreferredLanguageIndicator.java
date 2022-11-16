package com.agnity.win.test;

import java.util.LinkedList;

import junit.framework.TestCase;

import com.agnity.win.datatypes.NonASNPreferredLanguageIndicator;
import com.agnity.win.enumdata.PreferredLanguageEnum;
import com.agnity.win.exceptions.InvalidInputException;
import com.agnity.win.util.Util;

public class TestNonAsnPreferredLanguageIndicator extends TestCase {
	

	LinkedList  <PreferredLanguageEnum> preferredLanguage = new LinkedList<PreferredLanguageEnum> ();
	byte[] bytesToDecode = null;
	int langCodes[];
		
	
public void setUp() throws Exception {
		
		// Input values
	preferredLanguage.add(PreferredLanguageEnum.UNSPECIFIED);
	preferredLanguage.add(PreferredLanguageEnum.ENGLISH);
	preferredLanguage.add(PreferredLanguageEnum.FRENCH);
	preferredLanguage.add(PreferredLanguageEnum.SPANISH);
	preferredLanguage.add(PreferredLanguageEnum.GERMAN);
	preferredLanguage.add(PreferredLanguageEnum.PORTUGESE);
	
	    byte[] a = {(byte)0x00,(byte)0x01,(byte)0x02, (byte)0x03,(byte)0x04,(byte)0x05};
	    int[] c = {0,1,2,3,4,5};
	    bytesToDecode = a;
	    langCodes = c;
}
	/*
	 * Test encoding all possible values of BillingID
	 */
	public void testEncodePreferredLanguageIndicator() throws Exception{
		byte[] b = null;
		try {
			b = NonASNPreferredLanguageIndicator.encodePreferredLanguageIndicator(preferredLanguage);
		}
		catch(InvalidInputException ex){
			assertFalse(true);
			ex.printStackTrace();
		}
		System.out.println("Encoded NonASNPreferredLanguageIndicator: "+ Util.formatBytes(b));
	}

	/*
	 * Testing decoding of NonASNPreferredLanguageIndicator
	 */
	public void testDecodePreferredLanguageIndicator() throws Exception{
		try {
			NonASNPreferredLanguageIndicator pl = NonASNPreferredLanguageIndicator.decodePreferredLanguageIndicator(bytesToDecode);
			System.out.println("Decoded NonASNPreferredLanguageIndicator: "+ pl.toString());
			for(int i = 0; i<6; i++)
			{		
				assertEquals("NonASNPreferredLanguageIndicator is maching", pl.getPreferredLanguage().get(i).getCode(),langCodes[i]);
			}
			
		}
		catch(InvalidInputException ex){
			assertFalse(true);
			ex.printStackTrace();
		}
	}
}
