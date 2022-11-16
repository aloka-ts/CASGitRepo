package com.agnity.map.test.datatypes;

import static org.junit.Assert.*;

import org.junit.Test;

import com.agnity.map.datatypes.AllowedServicesMap;
import com.agnity.map.enumdata.AllowedServicesMapEnum;
import com.agnity.map.exceptions.InvalidInputException;

public class AllowedServicesMapTest {

	@Test
	public void testDecodeValidData() {

		byte[] data = new byte[] {0x01};
		AllowedServicesMap obj = null;
		Throwable e = null;
		try{
			 obj = AllowedServicesMap.decode(data);
		}
		catch(InvalidInputException ex) {
			System.out.println("invalid input "+ex);
			e = ex;
		}
		
		assertNotNull(obj);
		assertNull(e);
		assertTrue(obj.getAllowedService(AllowedServicesMapEnum.FIRST_SERVICE_ALLOWED));
		assertFalse(obj.getAllowedService(AllowedServicesMapEnum.SECOND_SERVICE_ALLOWED));
	}

	@Test
	public void testDecodeNullData() {
		byte[] data = null;
		AllowedServicesMap obj = null;
		Throwable e = null;
		try{
			 obj = AllowedServicesMap.decode(data);
		}
		catch(InvalidInputException ex) {
			System.out.println("invalid input "+ex);
			e = ex;
		}
		
		assertNull(obj);
		assertNotNull(e);
		assertTrue(e instanceof InvalidInputException);
	}

}
