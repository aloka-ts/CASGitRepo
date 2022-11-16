package com.genband.isup.test;

import junit.framework.TestCase;

import org.apache.log4j.PropertyConfigurator;

import com.genband.isup.datatypes.SusResIndicators;
import com.genband.isup.enumdata.SusResIndEnum;
import com.genband.isup.exceptions.InvalidInputException;

public class TestSUSRES extends TestCase  {

	protected void setUp() throws Exception {
		super.setUp();
		 PropertyConfigurator.configure("config" + System.getProperty("file.separator") + "log4j.properties");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testEncDecSUSRESInd(){
		
		SusResIndicators sr = null;
		byte[] b;
		try {
			b = SusResIndicators.encodeSusResInd(SusResIndEnum.NETWORK_INITIATED);
			
			sr = SusResIndicators.decodeSusResInd(b);
		} catch (InvalidInputException e) {
			assertFalse(true);
			e.printStackTrace();
		}
		
		assertEquals(SusResIndEnum.NETWORK_INITIATED, sr.getSusResIndicators());
	}
}
