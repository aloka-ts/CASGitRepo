package com.agnity.win.test;

import junit.framework.TestCase;

import com.agnity.win.datatypes.NonASNDigitsType;
import com.agnity.win.enumdata.EncodingSchemeEnum;
import com.agnity.win.enumdata.NatureOfNumAvailIndEnum;
import com.agnity.win.enumdata.NatureOfNumIndEnum;
import com.agnity.win.enumdata.NatureOfNumPresentationIndEnum;
import com.agnity.win.enumdata.NatureOfNumScreenIndEnum;
import com.agnity.win.enumdata.NumPlanEnum;
import com.agnity.win.enumdata.TypeOfDigitsEnum;
import com.agnity.win.exceptions.InvalidInputException;
import com.agnity.win.util.Util;

public class TestNonAsnDigitsType extends TestCase {

	/*
	 * Test encoding all possible values of Type Of Digits
	 */
	public void testEncodeTypeOfDigits() throws Exception{
		byte[] b = null;

		NatureOfNumIndEnum nonInd                 = NatureOfNumIndEnum.NATIONAL;
		NatureOfNumAvailIndEnum nonAvailInd       = NatureOfNumAvailIndEnum.NUM_AVAILABLE;
		NatureOfNumPresentationIndEnum nonPresInd = NatureOfNumPresentationIndEnum.PRES_ALLOW;
		NatureOfNumScreenIndEnum nonScreenInd     = NatureOfNumScreenIndEnum.USER_PROVIDED_SCREEN_FAILED;
		NumPlanEnum np        = NumPlanEnum.TEL_NP;
		EncodingSchemeEnum es = EncodingSchemeEnum.BCD;
		String digits         = "989145912";
		TypeOfDigitsEnum tod  = TypeOfDigitsEnum.DIALED_NUM;

		try {
			b = NonASNDigitsType.encodeDigits(digits, tod, nonInd, nonAvailInd, nonPresInd, nonScreenInd, np, es);
			assertEquals(b[0], 0x01);

			tod  = TypeOfDigitsEnum.CALLING_PTY_NUM;
			b = NonASNDigitsType.encodeDigits(digits, tod, nonInd, nonAvailInd, nonPresInd, nonScreenInd, np, es);
			assertEquals(b[0], 0x02);

			tod  = TypeOfDigitsEnum.CALLER_INTERACTION;
			b = NonASNDigitsType.encodeDigits(digits, tod, nonInd, nonAvailInd, nonPresInd, nonScreenInd, np, es);
			assertEquals(b[0], 0x03);

			tod  = TypeOfDigitsEnum.ROUTING_NUM;
			b = NonASNDigitsType.encodeDigits(digits, tod, nonInd, nonAvailInd, nonPresInd, nonScreenInd, np, es);
			assertEquals(b[0], 0x04);

			tod  = TypeOfDigitsEnum.BILLING_NUM;
			b = NonASNDigitsType.encodeDigits(digits, tod, nonInd, nonAvailInd, nonPresInd, nonScreenInd, np, es);
			assertEquals(b[0], 0x05);

			tod  = TypeOfDigitsEnum.DESTINATION_NUM;
			b = NonASNDigitsType.encodeDigits(digits, tod, nonInd, nonAvailInd, nonPresInd, nonScreenInd, np, es);
			assertEquals(b[0], 0x06);

			tod  = TypeOfDigitsEnum.LATA;
			b = NonASNDigitsType.encodeDigits(digits, tod, nonInd, nonAvailInd, nonPresInd, nonScreenInd, np, es);
			assertEquals(b[0], 0x07);

			tod  = TypeOfDigitsEnum.CARRIER;
			b = NonASNDigitsType.encodeDigits(digits, tod, nonInd, nonAvailInd, nonPresInd, nonScreenInd, np, es);
			assertEquals(b[0], 0x08);

			// Negative scenario
			tod  = TypeOfDigitsEnum.SPARE;
			b = NonASNDigitsType.encodeDigits(digits, tod, nonInd, nonAvailInd, nonPresInd, nonScreenInd, np, es);
			assertEquals(b[0], 0x00);

		}
		catch(InvalidInputException ex){
			assertFalse(true);
			ex.printStackTrace();
		}
		System.out.println("Encoded Digits: "+ Util.formatBytes(b));
	}

	/*
	 * Test encoding all possible values of Type Of Digits
	 */
	public void testDecodeTypeOfDigits() throws Exception{
		byte[] b = {(byte)0x02, (byte)0x03, (byte)0x21, (byte)0x05, 
				(byte)0x89, (byte)0x19, (byte)0x54, (byte)0x19,
				(byte)0x02 };

		try {
			NonASNDigitsType dig = NonASNDigitsType.decodeDigits(b);
			assertEquals(dig.getTypeOfDigits(), TypeOfDigitsEnum.CALLING_PTY_NUM);

			b[0] = 0x00;
			dig = NonASNDigitsType.decodeDigits(b);
			assertEquals(dig.getTypeOfDigits(), TypeOfDigitsEnum.SPARE);

			b[0] = 0x01;
			dig = NonASNDigitsType.decodeDigits(b);
			assertEquals(dig.getTypeOfDigits(), TypeOfDigitsEnum.DIALED_NUM);

			b[0] = 0x03;
			dig = NonASNDigitsType.decodeDigits(b);
			assertEquals(dig.getTypeOfDigits(), TypeOfDigitsEnum.CALLER_INTERACTION);

			b[0] = 0x04;
			dig = NonASNDigitsType.decodeDigits(b);
			assertEquals(dig.getTypeOfDigits(), TypeOfDigitsEnum.ROUTING_NUM);

			b[0] = 0x05;
			dig = NonASNDigitsType.decodeDigits(b);
			assertEquals(dig.getTypeOfDigits(), TypeOfDigitsEnum.BILLING_NUM);

			b[0] = 0x06;
			dig = NonASNDigitsType.decodeDigits(b);
			assertEquals(dig.getTypeOfDigits(), TypeOfDigitsEnum.DESTINATION_NUM);

			b[0] = 0x07;
			dig = NonASNDigitsType.decodeDigits(b);
			assertEquals(dig.getTypeOfDigits(), TypeOfDigitsEnum.LATA);

			b[0] = 0x08;
			dig = NonASNDigitsType.decodeDigits(b);
			assertEquals(dig.getTypeOfDigits(), TypeOfDigitsEnum.CARRIER);

		}
		catch(InvalidInputException ex){
			assertFalse(true);
			ex.printStackTrace();
		}
		System.out.println("Encoded Digits: "+ Util.formatBytes(b));
	}

	/*
	 * Test encoding all possible values of Nature Of Number
	 */
	public void testEncodeNatureofNum() throws Exception{
		byte[] b = null;

		NatureOfNumIndEnum nonInd                 = NatureOfNumIndEnum.NATIONAL;
		NatureOfNumPresentationIndEnum nonPresInd = NatureOfNumPresentationIndEnum.PRES_ALLOW;
		NatureOfNumAvailIndEnum nonAvailInd       = NatureOfNumAvailIndEnum.NUM_AVAILABLE;
		NatureOfNumScreenIndEnum nonScreenInd     = NatureOfNumScreenIndEnum.USER_PROVIDED_NOT_SCREENED;

		NumPlanEnum np        = NumPlanEnum.TEL_NP;
		EncodingSchemeEnum es = EncodingSchemeEnum.BCD;
		String digits         = "989145912";
		TypeOfDigitsEnum tod  = TypeOfDigitsEnum.DIALED_NUM;

		try {
			b = NonASNDigitsType.encodeDigits(digits, tod, nonInd, nonAvailInd, nonPresInd, nonScreenInd, np, es);
			assertEquals((b[1] & 0x01), 0x00);
			assertEquals((b[1] & 0x02), 0x00);
			assertEquals((b[1] & 0x04), 0x00);
			assertEquals((b[1] & 0x30), 0x00);	

			nonInd  = NatureOfNumIndEnum.INTERNATIONAL;
			b = NonASNDigitsType.encodeDigits(digits, tod, nonInd, nonAvailInd, nonPresInd, nonScreenInd, np, es);
			assertEquals((b[1] & 0x01), 0x01);
			assertEquals((b[1] & 0x02), 0x00);
			assertEquals((b[1] & 0x04), 0x00);
			assertEquals((b[1] & 0x30), 0x00);	

			nonPresInd  = NatureOfNumPresentationIndEnum.PRES_RESTRICTED;
			b = NonASNDigitsType.encodeDigits(digits, tod, nonInd, nonAvailInd, nonPresInd, nonScreenInd, np, es);
			assertEquals((b[1] & 0x01), 0x01);
			assertEquals((b[1] & 0x02), 0x02);
			assertEquals((b[1] & 0x04), 0x00);
			assertEquals((b[1] & 0x30), 0x00);	

			nonAvailInd  = NatureOfNumAvailIndEnum.NUM_UNAVAILABLE;
			b = NonASNDigitsType.encodeDigits(digits, tod, nonInd, nonAvailInd, nonPresInd, nonScreenInd, np, es);
			assertEquals((b[1] & 0x01), 0x01);
			assertEquals((b[1] & 0x02), 0x02);
			assertEquals((b[1] & 0x04), 0x04);
			assertEquals((b[1] & 0x30), 0x00);	

			nonScreenInd  = NatureOfNumScreenIndEnum.USER_PROVIDED_SCREEN_PASSED;
			b = NonASNDigitsType.encodeDigits(digits, tod, nonInd, nonAvailInd, nonPresInd, nonScreenInd, np, es);
			assertEquals((b[1] & 0x01), 0x01);
			assertEquals((b[1] & 0x02), 0x02);
			assertEquals((b[1] & 0x04), 0x04);
			assertEquals((b[1] & 0x30), 0x10);

			nonScreenInd  = NatureOfNumScreenIndEnum.USER_PROVIDED_SCREEN_FAILED;
			b = NonASNDigitsType.encodeDigits(digits, tod, nonInd, nonAvailInd, nonPresInd, nonScreenInd, np, es);
			assertEquals((b[1] & 0x01), 0x01);
			assertEquals((b[1] & 0x02), 0x02);
			assertEquals((b[1] & 0x04), 0x04);
			assertEquals((b[1] & 0x30), 0x20);
			
			nonScreenInd  = NatureOfNumScreenIndEnum.NETWORK_PROVIDED;
			b = NonASNDigitsType.encodeDigits(digits, tod, nonInd, nonAvailInd, nonPresInd, nonScreenInd, np, es);
			assertEquals((b[1] & 0x01), 0x01);
			assertEquals((b[1] & 0x02), 0x02);
			assertEquals((b[1] & 0x04), 0x04);
			assertEquals((b[1] & 0x30), 0x30);
			assertEquals(b[1], 0x37);
			
		}
		catch(InvalidInputException ex){
			assertFalse(true);
			ex.printStackTrace();
		}
		System.out.println("Encoded Digits: "+ Util.formatBytes(b));
	}

	/*
	 * Test encoding all possible values of Nature Of Number
	 */
	public void testDecodeNatureOfNumber() throws Exception{
		byte[] b = {(byte)0x02, (byte)0x00, (byte)0x21, (byte)0x05, 
				(byte)0x89, (byte)0x19, (byte)0x54, (byte)0x19,
				(byte)0x02 };

		try {
			NonASNDigitsType dig = NonASNDigitsType.decodeDigits(b);
			assertEquals(dig.getNatOfNumInd(), NatureOfNumIndEnum.NATIONAL);
			assertEquals(dig.getNatOfNumPresInd(), NatureOfNumPresentationIndEnum.PRES_ALLOW);
			assertEquals(dig.getNatOfNumAvlInd(), NatureOfNumAvailIndEnum.NUM_AVAILABLE);
			assertEquals(dig.getNatOfNumScrnInd(), NatureOfNumScreenIndEnum.USER_PROVIDED_NOT_SCREENED);

			b[1] = 0x01;
			dig = NonASNDigitsType.decodeDigits(b);
			assertEquals(dig.getNatOfNumInd(), NatureOfNumIndEnum.INTERNATIONAL);
			assertEquals(dig.getNatOfNumPresInd(), NatureOfNumPresentationIndEnum.PRES_ALLOW);
			assertEquals(dig.getNatOfNumAvlInd(), NatureOfNumAvailIndEnum.NUM_AVAILABLE);
			assertEquals(dig.getNatOfNumScrnInd(), NatureOfNumScreenIndEnum.USER_PROVIDED_NOT_SCREENED);

			b[1] = 0x02;
			dig = NonASNDigitsType.decodeDigits(b);
			assertEquals(dig.getNatOfNumInd(), NatureOfNumIndEnum.NATIONAL);
			assertEquals(dig.getNatOfNumPresInd(), NatureOfNumPresentationIndEnum.PRES_RESTRICTED);
			assertEquals(dig.getNatOfNumAvlInd(), NatureOfNumAvailIndEnum.NUM_AVAILABLE);
			assertEquals(dig.getNatOfNumScrnInd(), NatureOfNumScreenIndEnum.USER_PROVIDED_NOT_SCREENED);
			
			b[1] = 0x03;
			dig = NonASNDigitsType.decodeDigits(b);
			assertEquals(dig.getNatOfNumInd(), NatureOfNumIndEnum.INTERNATIONAL);
			assertEquals(dig.getNatOfNumPresInd(), NatureOfNumPresentationIndEnum.PRES_RESTRICTED);
			assertEquals(dig.getNatOfNumAvlInd(), NatureOfNumAvailIndEnum.NUM_AVAILABLE);
			assertEquals(dig.getNatOfNumScrnInd(), NatureOfNumScreenIndEnum.USER_PROVIDED_NOT_SCREENED);

			b[1] = 0x04;
			dig = NonASNDigitsType.decodeDigits(b);
			assertEquals(dig.getNatOfNumInd(), NatureOfNumIndEnum.NATIONAL);
			assertEquals(dig.getNatOfNumPresInd(), NatureOfNumPresentationIndEnum.PRES_ALLOW);
			assertEquals(dig.getNatOfNumAvlInd(), NatureOfNumAvailIndEnum.NUM_UNAVAILABLE);
			assertEquals(dig.getNatOfNumScrnInd(), NatureOfNumScreenIndEnum.USER_PROVIDED_NOT_SCREENED);		

			b[1] = 0x06;
			dig = NonASNDigitsType.decodeDigits(b);
			assertEquals(dig.getNatOfNumInd(), NatureOfNumIndEnum.NATIONAL);
			assertEquals(dig.getNatOfNumPresInd(), NatureOfNumPresentationIndEnum.PRES_RESTRICTED);
			assertEquals(dig.getNatOfNumAvlInd(), NatureOfNumAvailIndEnum.NUM_UNAVAILABLE);
			assertEquals(dig.getNatOfNumScrnInd(), NatureOfNumScreenIndEnum.USER_PROVIDED_NOT_SCREENED);
			

			b[1] = 0x07;
			dig = NonASNDigitsType.decodeDigits(b);
			assertEquals(dig.getNatOfNumInd(), NatureOfNumIndEnum.INTERNATIONAL);
			assertEquals(dig.getNatOfNumPresInd(), NatureOfNumPresentationIndEnum.PRES_RESTRICTED);
			assertEquals(dig.getNatOfNumAvlInd(), NatureOfNumAvailIndEnum.NUM_UNAVAILABLE);
			assertEquals(dig.getNatOfNumScrnInd(), NatureOfNumScreenIndEnum.USER_PROVIDED_NOT_SCREENED);

			b[1] = 0x17;
			dig = NonASNDigitsType.decodeDigits(b);
			assertEquals(dig.getNatOfNumInd(), NatureOfNumIndEnum.INTERNATIONAL);
			assertEquals(dig.getNatOfNumPresInd(), NatureOfNumPresentationIndEnum.PRES_RESTRICTED);
			assertEquals(dig.getNatOfNumAvlInd(), NatureOfNumAvailIndEnum.NUM_UNAVAILABLE);
			assertEquals(dig.getNatOfNumScrnInd(), NatureOfNumScreenIndEnum.USER_PROVIDED_SCREEN_PASSED);
			
			b[1] = 0x27;
			dig = NonASNDigitsType.decodeDigits(b);
			assertEquals(dig.getNatOfNumInd(), NatureOfNumIndEnum.INTERNATIONAL);
			assertEquals(dig.getNatOfNumPresInd(), NatureOfNumPresentationIndEnum.PRES_RESTRICTED);
			assertEquals(dig.getNatOfNumAvlInd(), NatureOfNumAvailIndEnum.NUM_UNAVAILABLE);
			assertEquals(dig.getNatOfNumScrnInd(), NatureOfNumScreenIndEnum.USER_PROVIDED_SCREEN_FAILED);
			
			b[1] = 0x37;
			dig = NonASNDigitsType.decodeDigits(b);
			assertEquals(dig.getNatOfNumInd(), NatureOfNumIndEnum.INTERNATIONAL);
			assertEquals(dig.getNatOfNumPresInd(), NatureOfNumPresentationIndEnum.PRES_RESTRICTED);
			assertEquals(dig.getNatOfNumAvlInd(), NatureOfNumAvailIndEnum.NUM_UNAVAILABLE);
			assertEquals(dig.getNatOfNumScrnInd(), NatureOfNumScreenIndEnum.NETWORK_PROVIDED);
			
			b[1] = 0x30;
			dig = NonASNDigitsType.decodeDigits(b);
			assertEquals(dig.getNatOfNumInd(), NatureOfNumIndEnum.NATIONAL);
			assertEquals(dig.getNatOfNumPresInd(), NatureOfNumPresentationIndEnum.PRES_ALLOW);
			assertEquals(dig.getNatOfNumAvlInd(), NatureOfNumAvailIndEnum.NUM_AVAILABLE);
			assertEquals(dig.getNatOfNumScrnInd(), NatureOfNumScreenIndEnum.NETWORK_PROVIDED);

		}
		catch(InvalidInputException ex){
			assertFalse(true);
			ex.printStackTrace();
		}
		System.out.println("Encoded Digits: "+ Util.formatBytes(b));
	}

	/*
	 * Test encoding all possible values of Numbering Plan
	 */
	public void testEncodeNumPlan() throws Exception{
		byte[] b = null;

		NatureOfNumIndEnum nonInd                 = NatureOfNumIndEnum.NATIONAL;
		NatureOfNumPresentationIndEnum nonPresInd = NatureOfNumPresentationIndEnum.PRES_ALLOW;
		NatureOfNumAvailIndEnum nonAvailInd       = NatureOfNumAvailIndEnum.NUM_AVAILABLE;
		NatureOfNumScreenIndEnum nonScreenInd     = NatureOfNumScreenIndEnum.USER_PROVIDED_NOT_SCREENED;
		
		NumPlanEnum np        = NumPlanEnum.ISDN_NP;
		EncodingSchemeEnum es = EncodingSchemeEnum.BCD;
		String digits         = "989145912";
		TypeOfDigitsEnum tod  = TypeOfDigitsEnum.DIALED_NUM;

		try {
			b = NonASNDigitsType.encodeDigits(digits, tod, nonInd, nonAvailInd, nonPresInd, nonScreenInd, np, es);
			assertEquals(((b[2] >> 4) & 0x0F), 0x01);

			np  = NumPlanEnum.TEL_NP;
			b = NonASNDigitsType.encodeDigits(digits, tod, nonInd, nonAvailInd, nonPresInd, nonScreenInd, np, es);
			assertEquals(((b[2] >> 4) & 0x0F), 0x02);

			np  = NumPlanEnum.DATA_NP;
			b = NonASNDigitsType.encodeDigits(digits, tod, nonInd, nonAvailInd, nonPresInd, nonScreenInd, np, es);
			assertEquals(((b[2] >> 4)& 0x0F), 0x03);

			np  = NumPlanEnum.TELEX_NP;
			b = NonASNDigitsType.encodeDigits(digits, tod, nonInd, nonAvailInd, nonPresInd, nonScreenInd, np, es);
			assertEquals(((b[2] >> 4) & 0x0F), 0x04);

			np  = NumPlanEnum.MARITIME_MOB_NP;
			b = NonASNDigitsType.encodeDigits(digits, tod, nonInd, nonAvailInd, nonPresInd, nonScreenInd, np, es);
			assertEquals(((b[2] >> 4) & 0x0F), 0x05);

			np  = NumPlanEnum.LAND_MOB_NP;
			b = NonASNDigitsType.encodeDigits(digits, tod, nonInd, nonAvailInd, nonPresInd, nonScreenInd, np, es);
			assertEquals(((b[2] >> 4) & 0x0F), 0x06);

			np  = NumPlanEnum.PRIVATE_NP;
			b = NonASNDigitsType.encodeDigits(digits, tod, nonInd, nonAvailInd, nonPresInd, nonScreenInd, np, es);
			assertEquals(((b[2] >> 4) & 0x0F), 0x07);

			np  = NumPlanEnum.ANSI_SS7_PC;
			b = NonASNDigitsType.encodeDigits(digits, tod, nonInd, nonAvailInd, nonPresInd, nonScreenInd, np, es);
			assertEquals(((b[2] >> 4) & 0x0F), 0x0D);

		/*	np  = NumPlanEnum.IP_ADDR;
			b = NonASNDigitsType.encodeDigits(digits, tod, nonInd, nonAvailInd, nonPresInd, nonScreenInd, np, es);
			assertEquals(((b[2] >> 4) & 0x0F), 0x0E);

			np  = NumPlanEnum.RESERVED;
			b = NonASNDigitsType.encodeDigits(digits, tod, nonInd, nonAvailInd, nonPresInd, nonScreenInd, np, es);
			assertEquals(((b[2] >> 4) & 0x0F), 0x0F);*/

		}
		catch(InvalidInputException ex){
			assertFalse(true);
			ex.printStackTrace();
		}
		System.out.println("Encoded Digits: "+ Util.formatBytes(b));
	}

	/*
	 * Test encoding all possible values of Numbering Plan
	 */
	public void testDecodeNumberingPlan() throws Exception{
		byte[] b = {(byte)0x02, (byte)0x03, (byte)0x01, (byte)0x05, 
				(byte)0x89, (byte)0x19, (byte)0x54, (byte)0x19,
				(byte)0x02 };

		try {
			NonASNDigitsType dig = NonASNDigitsType.decodeDigits(b);
			System.out.println("Nature Of num");
			assertEquals(dig.getNumberingPlan(), NumPlanEnum.SPARE);

			b[2] = 0x11;
			dig = NonASNDigitsType.decodeDigits(b);
			assertEquals(dig.getNumberingPlan(), NumPlanEnum.ISDN_NP);

			b[2] = 0x21;
			dig = NonASNDigitsType.decodeDigits(b);
			assertEquals(dig.getNumberingPlan(), NumPlanEnum.TEL_NP);

			b[2] = 0x31;
			dig = NonASNDigitsType.decodeDigits(b);
			assertEquals(dig.getNumberingPlan(), NumPlanEnum.DATA_NP);

			b[2] = 0x41;
			dig = NonASNDigitsType.decodeDigits(b);
			assertEquals(dig.getNumberingPlan(), NumPlanEnum.TELEX_NP);

			b[2] = 0x51;
			dig = NonASNDigitsType.decodeDigits(b);
			assertEquals(dig.getNumberingPlan(), NumPlanEnum.MARITIME_MOB_NP);

			b[2] = 0x61;
			dig = NonASNDigitsType.decodeDigits(b);
			assertEquals(dig.getNumberingPlan(), NumPlanEnum.LAND_MOB_NP);

			b[2] = 0x71;
			dig = NonASNDigitsType.decodeDigits(b);
			assertEquals(dig.getNumberingPlan(), NumPlanEnum.PRIVATE_NP);

			b[2] = (byte) 0xd1;
			dig = NonASNDigitsType.decodeDigits(b);
			assertEquals(dig.getNumberingPlan(), NumPlanEnum.ANSI_SS7_PC);

			b[2] = (byte) 0xe1;
			dig = NonASNDigitsType.decodeDigits(b);
			//assertEquals(dig.getNumberingPlan(), NumPlanEnum.IP_ADDR);

			b[2] = (byte) 0xf1;
			dig = NonASNDigitsType.decodeDigits(b);
			//assertEquals(dig.getNumberingPlan(), NumPlanEnum.RESERVED);	
		}
		catch(InvalidInputException ex){
			assertFalse(true);
			ex.printStackTrace();
		}
		System.out.println("Encoded Digits: "+ Util.formatBytes(b));
	}

	/*
	 * Test encoding all possible values of Encoding Scheme
	 */
	public void testEncodingScheme() throws Exception{
		byte[] b = null;


		NatureOfNumIndEnum nonInd                 = NatureOfNumIndEnum.NATIONAL;
		NatureOfNumPresentationIndEnum nonPresInd = NatureOfNumPresentationIndEnum.PRES_ALLOW;
		NatureOfNumAvailIndEnum nonAvailInd       = NatureOfNumAvailIndEnum.NUM_AVAILABLE;
		NatureOfNumScreenIndEnum nonScreenInd     = NatureOfNumScreenIndEnum.USER_PROVIDED_NOT_SCREENED;
		NumPlanEnum np        = NumPlanEnum.ISDN_NP;
		EncodingSchemeEnum es = EncodingSchemeEnum.SPARE;
		String digits         = "989145912";
		TypeOfDigitsEnum tod  = TypeOfDigitsEnum.DIALED_NUM;

		try {
			b = NonASNDigitsType.encodeDigits(digits, tod, nonInd, nonAvailInd, nonPresInd, nonScreenInd, np, es);
			assertEquals((b[2] & 0x0F), 0x00);

			es  = EncodingSchemeEnum.BCD;
			b = NonASNDigitsType.encodeDigits(digits, tod, nonInd, nonAvailInd, nonPresInd, nonScreenInd, np, es);
			assertEquals((b[2] & 0x0F), 0x01);

			es  = EncodingSchemeEnum.IA5;
			b = NonASNDigitsType.encodeDigits(digits, tod, nonInd, nonAvailInd, nonPresInd, nonScreenInd, np, es);
			assertEquals((b[2] & 0x0F), 0x02);

			es  = EncodingSchemeEnum.OCTET_STRING;
			b = NonASNDigitsType.encodeDigits(digits, tod, nonInd, nonAvailInd, nonPresInd, nonScreenInd, np, es);
			assertEquals((b[2] & 0x0F), 0x03);

		}
		catch(InvalidInputException ex){
			assertFalse(true);
			ex.printStackTrace();
		}
		System.out.println("Encoded Digits: "+ Util.formatBytes(b));
	}

	/*
	 * Test encoding all possible values of Encoding Scheme
	 */
	public void testDecodingEncodingScheme() throws Exception{
		byte[] b = {(byte)0x02, (byte)0x03, (byte)0x20, (byte)0x05, 
				(byte)0x89, (byte)0x19, (byte)0x54, (byte)0x19,
				(byte)0x02 };

		try {
			NonASNDigitsType dig = NonASNDigitsType.decodeDigits(b);
			assertEquals(dig.getEncoding(), EncodingSchemeEnum.SPARE);

			b[2] = 0x21;
			dig = NonASNDigitsType.decodeDigits(b);
			assertEquals(dig.getEncoding(), EncodingSchemeEnum.BCD);

			b[2] = 0x22;
			dig = NonASNDigitsType.decodeDigits(b);
			assertEquals(dig.getEncoding(), EncodingSchemeEnum.IA5);

			b[2] = 0x23;
			dig = NonASNDigitsType.decodeDigits(b);
			assertEquals(dig.getEncoding(), EncodingSchemeEnum.OCTET_STRING);	
		}
		catch(InvalidInputException ex){
			assertFalse(true);
			ex.printStackTrace();
		}
		System.out.println("Encoded Digits: "+ Util.formatBytes(b));
	}

	/*
	 * Testing decoding of Digits
	 */
	public void testDecodeDigitsOdd() throws Exception{
		byte[] b = {(byte)0x02, (byte)0x03, (byte)0x21, (byte)0x09, 
				(byte)0x89, (byte)0x19, (byte)0x54, (byte)0x19,
				(byte)0x02 };
		try {
			NonASNDigitsType dig = NonASNDigitsType.decodeDigits(b);
			assertEquals("Number of Digits matched", dig.getAddrSignal().length(), 9);
			assertEquals("Digits are maching", dig.getAddrSignal(), "989145912");
		}
		catch(InvalidInputException ex){
			assertFalse(true);
			ex.printStackTrace();
		}
	}

	/*
	 * Testing decoding of Digits
	 */
	public void testDecodeDigitsEven() throws Exception{
		byte[] b = {(byte)0x02, (byte)0x03, (byte)0x21, (byte)0x08, 
				(byte)0x89, (byte)0x19, (byte)0x54, (byte)0x19 };
		try {
			NonASNDigitsType dig = NonASNDigitsType.decodeDigits(b);
			assertEquals("Number of Digits matched", dig.getAddrSignal().length(), 8);
			assertEquals("Digits are maching", dig.getAddrSignal(), "98914591");
		}
		catch(InvalidInputException ex){
			assertFalse(true);
			ex.printStackTrace();
		}
	}

	/*
	 * Testing decoding of Digits having *
	 */
	public void testDecodeDigitsAsteriask() throws Exception{
		byte[] b = {(byte)0x02, (byte)0x03, (byte)0x21, (byte)0x08, 
				(byte)0x89, (byte)0x19, (byte)0x54, (byte)0xd1 };
		try {
			NonASNDigitsType dig = NonASNDigitsType.decodeDigits(b);
			System.out.println("Decode of ox89 0x19 0x54 0xd1:" + dig.getAddrSignal());
			assertEquals("Number of Digits matched", dig.getAddrSignal().length(), 8);
			assertEquals("Digits are maching", dig.getAddrSignal(), "9891451*");
		}
		catch(InvalidInputException ex){
			assertFalse(true);
			ex.printStackTrace();
		}
	}

	/*
	 * Testing decoding of Digits having H
	 */
	public void testDecodeDigitsHash() throws Exception{
		byte[] b = {(byte)0x02, (byte)0x03, (byte)0x21, (byte)0x08, 
				(byte)0x89, (byte)0x19, (byte)0x54, (byte)0xe1 };
		try {
			NonASNDigitsType dig = NonASNDigitsType.decodeDigits(b);
			System.out.println("Decode of ox89 0x19 0x54 0xe1:" + dig.getAddrSignal());
			assertEquals("Number of Digits matched", dig.getAddrSignal().length(), 8);
			assertEquals("Digits are maching", dig.getAddrSignal(), "9891451#");
		}
		catch(InvalidInputException ex){
			assertFalse(true);
			ex.printStackTrace();
		}
	}

	/*
	 * Testing encoding of Digits
	 */
	public void testEncodeDigitsOddAndEven() throws Exception{
		byte[] b = null;

		NatureOfNumIndEnum nonInd                 = NatureOfNumIndEnum.NATIONAL;
		NatureOfNumPresentationIndEnum nonPresInd = NatureOfNumPresentationIndEnum.PRES_ALLOW;
		NatureOfNumAvailIndEnum nonAvailInd       = NatureOfNumAvailIndEnum.NUM_AVAILABLE;
		NatureOfNumScreenIndEnum nonScreenInd     = NatureOfNumScreenIndEnum.USER_PROVIDED_NOT_SCREENED;
		NumPlanEnum np        = NumPlanEnum.ISDN_NP;
		EncodingSchemeEnum es = EncodingSchemeEnum.SPARE;
		String addrSig         = "989145912";
		TypeOfDigitsEnum tod  = TypeOfDigitsEnum.DIALED_NUM;

		try {
			b = NonASNDigitsType.encodeDigits(addrSig, tod, nonInd, nonAvailInd, nonPresInd, nonScreenInd, np, es);
			System.out.println("Encoding of [989145912]: "+ Util.formatBytes(b));

			assertEquals(b[3], (byte) 0x09);
			assertEquals(b[4], (byte) 0x89);
			assertEquals(b[5], (byte)0x19);
			assertEquals(b[6], (byte)0x54);
			assertEquals(b[7], (byte)0x19);
			assertEquals(b[8], (byte)0x02);

			addrSig = "98914591";
			b = NonASNDigitsType.encodeDigits(addrSig, tod, nonInd, nonAvailInd, nonPresInd, nonScreenInd, np, es);
			System.out.println("Encoding of [98914591]: "+ Util.formatBytes(b));

			assertEquals(b[3], (byte)0x08);
			assertEquals(b[4], (byte)0x89);
			assertEquals(b[5], (byte)0x19);
			assertEquals(b[6], (byte)0x54);
			assertEquals(b[7], (byte)0x19);

			addrSig = "";
			b = NonASNDigitsType.encodeDigits(addrSig, tod, nonInd, nonAvailInd, nonPresInd, nonScreenInd, np, es);
			System.out.println("Encoding of [null]: "+ Util.formatBytes(b));

			assertEquals(b[3], (byte)0x00);

			addrSig = "9891459#";
			b = NonASNDigitsType.encodeDigits(addrSig, tod, nonInd, nonAvailInd, nonPresInd, nonScreenInd, np, es);
			System.out.println("Encoding of [9891459#]: "+ Util.formatBytes(b));
			assertEquals(b[3], (byte)0x08);
			assertEquals(b[4], (byte)0x89);
			assertEquals(b[5], (byte)0x19);
			assertEquals(b[6], (byte)0x54);
			assertEquals(b[7], (byte)0xe9);

			addrSig = "9891459*";
			b = NonASNDigitsType.encodeDigits(addrSig, tod, nonInd, nonAvailInd, nonPresInd, nonScreenInd, np, es);

			assertEquals(b[3], (byte)0x08);
			assertEquals(b[4], (byte)0x89);
			assertEquals(b[5], (byte)0x19);
			assertEquals(b[6], (byte)0x54);
			assertEquals(b[7], (byte)0xd9);
			System.out.println("Encoding of [9891459*]: "+ Util.formatBytes(b));
		}
		catch(InvalidInputException ex){
			assertFalse(true);
			ex.printStackTrace();
		}
	}
}
