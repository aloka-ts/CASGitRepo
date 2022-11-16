package com.agnity.win.test;

import java.util.LinkedList;

import junit.framework.TestCase;
import com.agnity.win.datatypes.NonASNAnnouncementCode;
import com.agnity.win.enumdata.ClassEnum;
import com.agnity.win.enumdata.StdAnnoucementEnum;
import com.agnity.win.enumdata.ToneEnum;
import com.agnity.win.exceptions.InvalidInputException;
import com.agnity.win.util.Util;

public class TestNonAsnAnnouncementCode extends TestCase {

	LinkedList <NonASNAnnouncementCode> annCodes = new LinkedList <NonASNAnnouncementCode>();
	
	byte[][] bytesToDecode = null;
	byte custom = 2;
	

		
	
public void setUp() throws Exception {
	NonASNAnnouncementCode ac1 = new NonASNAnnouncementCode();
	ac1.setClassEnum(ClassEnum.CONCURRENT);
	ac1.setStdAnnoucement(StdAnnoucementEnum.DENY1_PLUS);
	ac1.setTone(ToneEnum.BUSY_TONE);
	ac1.setCustAnnoucement(custom);

	NonASNAnnouncementCode ac2 = new NonASNAnnouncementCode();
	ac2.setClassEnum(ClassEnum.CONCURRENT);
	ac2.setStdAnnoucement(StdAnnoucementEnum.DENY_INCOMING_TO_ALL);
	ac2.setTone(ToneEnum.DENIALTONE_BURST);
	ac2.setCustAnnoucement((byte)0);
	
	NonASNAnnouncementCode ac3 = new NonASNAnnouncementCode();
	ac3.setClassEnum(ClassEnum.SEQUENTIAL);
	ac3.setStdAnnoucement(StdAnnoucementEnum.ENTER_PASSWORD_PROMPT);
	ac3.setTone(ToneEnum.CONGTONE_REORDERTONE);
	ac3.setCustAnnoucement((byte)0);
	
	
	NonASNAnnouncementCode ac4 = new NonASNAnnouncementCode();
	ac4.setClassEnum(ClassEnum.CONCURRENT);
	ac4.setStdAnnoucement(StdAnnoucementEnum.COURTESY_CALL_WARNING);
	ac4.setTone(ToneEnum.CONFIRM_TONE);
	ac4.setCustAnnoucement((byte)0);
	
	
	NonASNAnnouncementCode ac5 = new NonASNAnnouncementCode();
	ac5.setClassEnum(ClassEnum.CONCURRENT);
	ac5.setStdAnnoucement(StdAnnoucementEnum.ROAMER_ACCESS_SCREENING);
	ac5.setTone(ToneEnum.OFFHOOK_TONE);
	ac5.setCustAnnoucement((byte)5);
	
	annCodes.add(0, ac1);
	annCodes.add(1, ac2);
	annCodes.add(2, ac3);
	annCodes.add(3, ac4);
	annCodes.add(4, ac5);
	
	byte[][] bytes = {{0x04,0x00,0x15,0x02},{(byte)0xc4,0x00, 0x55,0x00},
	{0x03, 0x01,(byte)0x8a},{ 0x05, 0x00,0x79},{0x08, 0x00 ,0x56,0x05}};
	    bytesToDecode = bytes;

}	
	/*
	 * Test encoding all possible values of NonASNWINOperationCapability
	 */
	public void testEncodeNonAsnAnnouncementCode() throws Exception{
		byte[] b = null;
		try {
			int i =0;
			for (NonASNAnnouncementCode ac : annCodes)
			{
			b = NonASNAnnouncementCode.encodeAnnouncementCode(ac.getTone(), ac.getClassType(), ac.getStdAnnoucement(),ac.getCustAnnoucement());
			

		System.out.println("Encoded NonAsnAnnouncementCode: "+i+" :" + Util.formatBytes(b));

			assertEquals("AnnouncementCode's tone is maching",bytesToDecode[i][0],b[0]);
			assertEquals("AnnouncementCode's class is maching",bytesToDecode[i][1],b[1]);
			assertEquals("AnnouncementCode's std announcement is maching",bytesToDecode[i][2],b[2]);
			assertEquals("AnnouncementCode's custom announcement is maching",bytesToDecode[i][2],b[2]);
			i++;
	}
		}
		catch(InvalidInputException ex){
			assertFalse(true);
			ex.printStackTrace();
		}
	}

	/*
	 * Testing decoding of NonAsnAnnouncementCode
	 */
	public void testDecodeNonAsnAnnouncementList() throws Exception{
		try {
			for(int i=0;i<5;i++)
			{
			NonASNAnnouncementCode annCode = NonASNAnnouncementCode.decodeAnnouncementCode(bytesToDecode[i]);
			
			System.out.println("Decoded NonAsnAnnouncementCode: "+ annCode.toString());
	
			assertEquals("Class is maching",annCode.getClassType(),annCodes.get(i).getClassType());
			assertEquals("StdAnnoucement is maching",annCode.getStdAnnoucement(),annCodes.get(i).getStdAnnoucement());
			assertEquals("Tone is maching",annCode.getTone(),annCodes.get(i).getTone());
			assertEquals("Custom Annoucement is maching",annCode.getCustAnnoucement(),annCodes.get(i).getCustAnnoucement());
			}
		}
		catch(InvalidInputException ex){
			assertFalse(true);
			ex.printStackTrace();
		}
	}
	
}
