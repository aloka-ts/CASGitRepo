package com.genband.isup.test;

import junit.framework.TestCase;

import org.apache.log4j.PropertyConfigurator;

import com.genband.isup.datatypes.EventInfo;
import com.genband.isup.enumdata.EventIndEnum;
import com.genband.isup.enumdata.EventPrsntRestIndEnum;
import com.genband.isup.exceptions.InvalidInputException;

public class TestCFG extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
		 PropertyConfigurator.configure("config" + System.getProperty("file.separator") + "log4j.properties");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testEncDecEventInfo(){
		
		EventInfo ei = null;
		byte[] b;
		try {
			b = EventInfo.encodeEventInfo(EventIndEnum.CALL_FORWARD_REPLY, EventPrsntRestIndEnum.PRESENTATION_RESTRICTED);
			
			ei = EventInfo.decodeEventInfo(b);
		} catch (InvalidInputException e) {
			assertFalse(true);
			e.printStackTrace();
		}
		
		assertEquals(EventIndEnum.CALL_FORWARD_REPLY, ei.getEventIndEnum());
		assertEquals(EventPrsntRestIndEnum.PRESENTATION_RESTRICTED, ei.getEventPrsntRestIndEnum());
	}
}
