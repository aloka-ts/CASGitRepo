package com.camel.test;

import junit.framework.TestCase;

import org.apache.log4j.PropertyConfigurator;

import com.camel.enumData.CalgPartyCatgEnum;
import com.camel.exceptions.InvalidInputException;
import com.camel.util.NonAsnArg;
import com.camel.util.Util;

public class TestNonAsnArg extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
		PropertyConfigurator.configure("config" + System.getProperty("file.separator") + "log4j.properties");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testEncDecCollectedDigits(){
		byte[] out = NonAsnArg.collectedDigitsEncoder("1234");
		String str = null;
		try {
			str = NonAsnArg.collectedDigitsDecoder(out);
		} catch (InvalidInputException e) {
			e.printStackTrace();
		}

		assertEquals("1234", str);		
	}

	public void testEncDecCalgPartyCatg(){
		byte[] out = NonAsnArg.encodeCalgPartyCatg(CalgPartyCatgEnum.OPRT_FRENCH);
		assertEquals("00000001" , Util.conversiontoBinary(out[0]));
		CalgPartyCatgEnum calgPartyCatgEnum = null;
		try {
			calgPartyCatgEnum = NonAsnArg.decodeCalgPartyCatg(out);
		} catch (InvalidInputException e) {
			e.printStackTrace();
		}
		assertEquals(CalgPartyCatgEnum.OPRT_FRENCH , calgPartyCatgEnum);
	}

	public void testEncDecTbcdString(){
		byte[] out = NonAsnArg.tbcdStringEncoder("*123");
		assertEquals("00011010" , Util.conversiontoBinary(out[0]));
		assertEquals("00110010" , Util.conversiontoBinary(out[1]));
		String str = null;
		str = NonAsnArg.TbcdStringDecoder(out, 0);
		assertEquals("*123", str);		
	}
}
