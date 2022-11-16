package com.agnity.map.test.datatypes;

import static org.junit.Assert.*;

import org.junit.Test;

import com.agnity.map.datatypes.SupportedCamelPhasesMap;
import com.agnity.map.enumdata.SupportedCamelPhasesMapEnum;
import com.agnity.map.exceptions.InvalidInputException;
import com.agnity.map.util.Util;

public class SupportedCamelPhasesMapTest {

	@Test
	public void testDecodeValidData() {
		
		byte[] data = new byte[] {(byte) 0xc0};
		SupportedCamelPhasesMap obj = null;
		Throwable e = null;
		try{
			 obj = SupportedCamelPhasesMap.decode(data);
		}
		catch(InvalidInputException ex) {
			System.out.println("invalid input "+ex);
			e = ex;
		}
		
		assertNotNull(obj);
		assertNull(e);
		assertFalse(obj.getCamelPhase(SupportedCamelPhasesMapEnum.PHASE_3));
		assertTrue(obj.getCamelPhase(SupportedCamelPhasesMapEnum.PHASE_1));
		assertTrue(obj.getCamelPhase(SupportedCamelPhasesMapEnum.PHASE_2));
		assertFalse(obj.getCamelPhase(SupportedCamelPhasesMapEnum.PHASE_4));
	}

	@Test
	public void testDecodeNull() {
		byte[] data = null;
		SupportedCamelPhasesMap obj = null;
		Throwable e = null;
		try{
			 obj = SupportedCamelPhasesMap.decode(data);
		}
		catch(Exception ex) {
			e = ex;
		}
		assertTrue(e instanceof InvalidInputException);
	}
	
	@Test
	public void testDecodeInvalidData(){
		byte[] data = new byte[] {0x00};
		SupportedCamelPhasesMap obj = null;
		Throwable e = null;
		try{
			 obj = SupportedCamelPhasesMap.decode(data);
		}
		catch(Exception ex) {
			System.out.println("invalid input "+ex);
			e = ex;
		}
		
		assertNotNull(obj);
		assertNull(e);
		assertFalse(obj.getCamelPhase(SupportedCamelPhasesMapEnum.PHASE_1));
		assertFalse(obj.getCamelPhase(SupportedCamelPhasesMapEnum.PHASE_2));
		assertFalse(obj.getCamelPhase(SupportedCamelPhasesMapEnum.PHASE_3));
		assertFalse(obj.getCamelPhase(SupportedCamelPhasesMapEnum.PHASE_4));
	}
	
	@Test
	public void testEncodeNullObj() {
		SupportedCamelPhasesMap obj = null;
		byte[] encdata = null;
		Throwable e = null;
		try{
			encdata = SupportedCamelPhasesMap.encode(obj);
		}
		catch(Exception ex){
			System.out.println("encode exception  = "+ex);
			e = ex;
		}
		assertNull(obj);
		assertTrue( e instanceof InvalidInputException);
	}
	
	@Test
	public void testEncodeWithNothingSet(){
		SupportedCamelPhasesMap obj = new SupportedCamelPhasesMap();
		byte[] encdata = null;
		Throwable e = null;
		try{
			encdata = SupportedCamelPhasesMap.encode(obj);
			System.out.println("encdat = "+Util.formatBytes(encdata));

		}
		catch(Exception ex){
			System.out.println("encode exception  = "+ex);
			e = ex;
		}
		byte[] expected = new byte[] {0x00};
		assertNull(e);
		assertEquals(encdata.length, 1);
		assertArrayEquals(expected, encdata);
	}

	@Test
	public void testEncodeWithPhaseSet(){
		System.out.println("Testing encode");
		SupportedCamelPhasesMap obj = new SupportedCamelPhasesMap();
		obj.setCamelPhase(SupportedCamelPhasesMapEnum.PHASE_2, true);
		byte[] encdata = null;
		Throwable e = null;
		try{
			encdata = SupportedCamelPhasesMap.encode(obj);
			System.out.println("encoded data = "+Util.formatBytes(encdata));
		}
		catch(Exception ex){
			System.out.println("encode exception  = "+ex);
			e = ex;
		}
		
		byte[] expected = new byte[] {0x40};
		assertNull(e);
		assertEquals(encdata.length, 1);
		assertArrayEquals(expected, encdata);
	}

}
