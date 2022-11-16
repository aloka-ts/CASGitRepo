package com.agnity.win.test;

import java.util.LinkedList;

import junit.framework.TestCase;

import com.agnity.win.datatypes.NonASNSpecializedResource;
import com.agnity.win.enumdata.ResourceTypeEnum;
import com.agnity.win.exceptions.InvalidInputException;
import com.agnity.win.util.Util;

public class TestNonAsnSpecializedResource extends TestCase {
	
	
	LinkedList  <ResourceTypeEnum> resType = new LinkedList<ResourceTypeEnum> ();
	byte[] bytesToDecode = null;
	int resTypesValue[];
		
	
public void setUp() throws Exception {
		
		// Input values
	resType.add(ResourceTypeEnum.NOT_USED);
	resType.add(ResourceTypeEnum.DTMF_TONE_DETECTOR);
	resType.add(ResourceTypeEnum.AUTO_SPEECH_RECOG_INDEPENDENT_DIGITS);
	resType.add(ResourceTypeEnum.AUTO_SPEECH_RECOG_INDEPENDENT_SPEAKER_UI_V1);

	byte[] a = {(byte)0x00,(byte)0x01,(byte)0x02, (byte)0x03};
	    int[] c = {0,1,2,3};
	    bytesToDecode = a;
	    resTypesValue = c;
}
	/*
	 * Test encoding all possible values of BillingID
	 */
	public void testEncodeSpecializedResource() throws Exception{
		byte[] b = null;

		try {
			b = NonASNSpecializedResource.encodeResourceType(resType);
			
		}
		catch(InvalidInputException ex){
			assertFalse(true);
			ex.printStackTrace();
		}
		System.out.println("Encoded NonASNSpecializedResource: "+ Util.formatBytes(b));
		for(int i = 0; i<3; i++)
		{
		assertEquals("Decoded byte is maching", b[i],bytesToDecode[i]);
		}
	}

	/*
	 * Testing decoding of NonASNSpecializedResource
	 */
	public void testDecodeSpecializedResource() throws Exception{
		try {
			NonASNSpecializedResource sr = NonASNSpecializedResource.decodeResourceType(bytesToDecode);
			System.out.println("Decoded NonASNSpecializedResource: "+ sr.toString());
			for(int i = 0; i<3; i++)
			{
			assertEquals("NonASNSpecializedResource is maching", sr.getResourceType().get(i).getCode(),resTypesValue[i]);
			}
			}
		catch(InvalidInputException ex){
			assertFalse(true);
			ex.printStackTrace();
		}
	}
}
