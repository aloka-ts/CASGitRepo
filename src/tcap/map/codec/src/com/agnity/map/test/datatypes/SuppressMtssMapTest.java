package com.agnity.map.test.datatypes;

import static org.junit.Assert.*;

import org.junit.Test;

import com.agnity.map.datatypes.SuppressMtssMap;
import com.agnity.map.enumdata.SuppressMtssMapEnum;
import com.agnity.map.exceptions.InvalidInputException;

public class SuppressMtssMapTest {

	@Test
	public void testDecodeValidData() {
		
		byte[] data = new byte[] {0x03};
		SuppressMtssMap obj = null;
		try{
			 obj = SuppressMtssMap.decode(data);
		}
		catch(InvalidInputException ex) {
			System.out.println("invalid input "+ex);
		}
		
		assertNotNull(obj);
		assertTrue(obj.getSuppressMtss(SuppressMtssMapEnum.SUPPRESS_CUG));
		assertTrue(obj.getSuppressMtss(SuppressMtssMapEnum.SUPPRESS_CCBS));
	}
	

	@Test
	public void testDecodeInvalidData() {
		byte[] data = new byte[]{0x08};
		SuppressMtssMap obj = null;
		Throwable e = null;
		
		try{
			 obj = SuppressMtssMap.decode(data);
		}
		catch(Exception ex) {
			System.out.println("invalid input "+ex);
			e = ex;
		}
		assertNotNull(obj);
		assertFalse(obj.getSuppressMtss(SuppressMtssMapEnum.SUPPRESS_CCBS));
		assertFalse(obj.getSuppressMtss(SuppressMtssMapEnum.SUPPRESS_CUG));
	}

	@Test
	public void testDecodeNull() {
		byte[] data = new byte[]{0x08};
		SuppressMtssMap obj = null;
		Throwable e = null;
		
		try{
			 obj = SuppressMtssMap.decode(data);
		}
		catch(Exception ex) {
			System.out.println("invalid input "+ex);
			e = ex;
		}
		assertNotNull(obj);
		assertFalse(obj.getSuppressMtss(SuppressMtssMapEnum.SUPPRESS_CCBS));
		assertFalse(obj.getSuppressMtss(SuppressMtssMapEnum.SUPPRESS_CUG));
	}
	
	

	@Test
	public void testEncodeNullData() {
		SuppressMtssMap obj = null;
		byte[] data = null;
		Throwable e = null;
		
		try{
			 data = SuppressMtssMap.encode(obj);
		}
		catch(Exception ex) {
			System.out.println("invalid input "+ex);
			e = ex;
		}
		
		assertNull(data);
		assertTrue(e instanceof InvalidInputException);
	}
	

	@Test
	public void testEncodeValidObjNotSet() {
		SuppressMtssMap obj = new SuppressMtssMap();
		byte[] data = null;
		Throwable e = null;
		
		try{
			 data = SuppressMtssMap.encode(obj);
		}
		catch(Exception ex) {
			System.out.println("invalid input "+ex);
			e = ex;
		}

		byte[] expected = new byte[] {0x00};
		assertNull(e);
		assertNotNull(data);
		assertArrayEquals(expected, data);
		assertFalse(obj.getSuppressMtss(SuppressMtssMapEnum.SUPPRESS_CCBS));
		assertFalse(obj.getSuppressMtss(SuppressMtssMapEnum.SUPPRESS_CUG));
	}
	
	@Test
	public void testEncodeValidObj() {
		SuppressMtssMap obj = new SuppressMtssMap();
		obj.setSuppressMtss(SuppressMtssMapEnum.SUPPRESS_CUG, true);
		byte[] data = null;
		Throwable e = null;
		
		try{
			 data = SuppressMtssMap.encode(obj);
		}
		catch(Exception ex) {
			System.out.println("invalid input "+ex);
			e = ex;
		}

		byte[] expected = new byte[] {0x01};
		assertNull(e);
		assertNotNull(data);
		assertArrayEquals(expected, data);
	}
	

}
