package com.camel.test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import junit.framework.TestCase;
import org.apache.log4j.PropertyConfigurator;
import org.bn.CoderFactory;
import org.bn.IDecoder;

import asnGenerated.ApplyChargingReportArg;
import asnGenerated.PromptAndCollectUserInformationArg;

import com.camel.dataTypes.GenericDigitsDataType;
import com.camel.enumData.EncodingSchemeEnum;
import com.camel.exceptions.InvalidInputException;
import com.camel.util.Util;

	public class TestPlayAnnouncement extends TestCase {
	protected void setUp() throws Exception {
		super.setUp();
		 PropertyConfigurator.configure("config" + System.getProperty("file.separator") + "log4j.properties");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testEncDecenericDigits(){
		
		GenericDigitsDataType cp = new GenericDigitsDataType();
		byte[] b;
		try {
			b = GenericDigitsDataType.encodeGenericDigits(EncodingSchemeEnum.BCD_EVEN, "1234");
			cp = null ;
			cp = GenericDigitsDataType.decodeGenericDigits(b);
		} catch (InvalidInputException e) {
			e.printStackTrace();
		}
		
		assertEquals("1234", cp.getDigits());
		assertEquals(EncodingSchemeEnum.BCD_EVEN, cp.getEncodingSchemeEnum());
	}
	
	public void testDecodePromptCollect(){
		IDecoder decoder;
		try {
			decoder = CoderFactory.getInstance().newDecoder("BER");
			byte[] parms = {0x30, 0x22, (byte)0xa0 ,0x14 ,(byte)0xa0 ,0x12, (byte)0x80, 
					0x01, 0x01,(byte) 0x81 ,0x01 ,0x05, (byte)0x87, 0x01 ,0x00, (byte)0x88 ,0x01, (byte)0xff ,(byte)0x89,
					0x01 ,0x00 ,(byte)0x8a ,0x01 ,0x00, (byte)0x81, 0x01, (byte)0xff ,(byte)0xa2 ,0x07 ,(byte)0xa0 ,0x05 ,(byte)0xa0 ,0x03 ,(byte)0x80 ,0x01 ,0x05};
			InputStream ins = new ByteArrayInputStream(parms);
			PromptAndCollectUserInformationArg pcArg ;
			pcArg = decoder.decode(ins, PromptAndCollectUserInformationArg.class);
			System.out.println("DisconnectFromIPForbidden:"+pcArg.getDisconnectFromIPForbidden());
			System.out.println("getMinimumNbOfDigits:"+pcArg.getCollectedInfo().getCollectedDigits().getMinimumNbOfDigits());
		} catch (Exception e) {
			e.printStackTrace();
			assertFalse(true);
		}
	}
}
