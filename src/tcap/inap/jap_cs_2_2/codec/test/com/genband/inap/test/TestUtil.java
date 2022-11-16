package com.genband.inap.test;

import org.apache.log4j.PropertyConfigurator;
import junit.framework.TestCase;

import com.genband.inap.util.Util;

public class TestUtil extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
		PropertyConfigurator.configure("config" + System.getProperty("file.separator") + "log4j.properties");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testFormatBytes(){
		byte[] data = {(byte)0x30 };
		String str = Util.formatBytes(data);
		assertEquals("0x30" , str);
		
		byte[] data1 = {(byte)0xFF };
		str = Util.formatBytes(data1);
		assertEquals("0xff" , str);
	}
}
