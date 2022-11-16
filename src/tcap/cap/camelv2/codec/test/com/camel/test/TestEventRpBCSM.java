package com.camel.test;

import org.apache.log4j.PropertyConfigurator;

import com.camel.dataTypes.CauseDataType;
import com.camel.enumData.CauseValEnum;
import com.camel.enumData.CodingStndEnum;
import com.camel.enumData.LocationEnum;
import com.camel.exceptions.InvalidInputException;
import com.camel.util.Util;

import junit.framework.TestCase;

public class TestEventRpBCSM extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
		 PropertyConfigurator.configure("config" + System.getProperty("file.separator") + "log4j.properties");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testEncDecCauseVal(){
		
		CauseDataType causeData = new CauseDataType();
		byte[] data ;
		try {
			data = CauseDataType.encodeCauseVal(LocationEnum.USER, CodingStndEnum.ITUT_STANDARDIZED_CODING,CauseValEnum.Normal_UNSPECIFIED);
			assertEquals("10000000", Util.conversiontoBinary(data[0]));
			assertEquals("10011111", Util.conversiontoBinary(data[1]));
			causeData = null ;
			causeData = CauseDataType.decodeCauseVal(data);
		} catch (InvalidInputException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(LocationEnum.USER, causeData.getLocEnum());
		assertEquals(CodingStndEnum.ITUT_STANDARDIZED_CODING, causeData.getCodingStndEnum());
		assertEquals(CauseValEnum.Normal_UNSPECIFIED, causeData.getCauseValEnum());
	}
}
