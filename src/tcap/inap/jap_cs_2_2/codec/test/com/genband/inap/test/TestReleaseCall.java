package com.genband.inap.test;

import junit.framework.TestCase;

import org.apache.log4j.PropertyConfigurator;

import com.genband.inap.datatypes.Cause;
import com.genband.inap.enumdata.CauseValEnum;
import com.genband.inap.enumdata.CodingStndEnum;
import com.genband.inap.enumdata.LocationEnum;
import com.genband.inap.exceptions.InvalidInputException;
import com.genband.inap.util.Util;

public class TestReleaseCall extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
		 PropertyConfigurator.configure("config" + System.getProperty("file.separator") + "log4j.properties");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testEncDecCauseVal(){
		
		Cause causeData = new Cause();
		byte[] data ;
		try {
			data = Cause.encodeCauseVal(LocationEnum.NETWORK_BEYOND_INTERWORKING_POINT, CodingStndEnum.ITUT_STANDARDIZED_CODING,CauseValEnum.Call_rejected);
			assertEquals("10001010", Util.conversiontoBinary(data[0]));
			assertEquals("10010101", Util.conversiontoBinary(data[1]));
			causeData = null ;
			causeData = Cause.decodeCauseVal(data);
		} catch (InvalidInputException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(LocationEnum.NETWORK_BEYOND_INTERWORKING_POINT, causeData.getLocEnum());
		assertEquals(CodingStndEnum.ITUT_STANDARDIZED_CODING, causeData.getCodingStndEnum());
		assertEquals(CauseValEnum.Call_rejected, causeData.getCauseValEnum());
	}
}
