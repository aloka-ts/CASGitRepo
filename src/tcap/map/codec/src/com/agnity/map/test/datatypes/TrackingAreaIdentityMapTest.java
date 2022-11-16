package com.agnity.map.test.datatypes;

import static org.junit.Assert.*;

import org.junit.Test;

import com.agnity.map.datatypes.TrackingAreaIdentityMap;
import com.agnity.map.exceptions.InvalidInputException;
import com.agnity.map.util.Util;

public class TrackingAreaIdentityMapTest {
	
	@Test 
	public void testDecode() {
		byte[] data = new byte[] {0x01, 0x02, 0x03, 0x04};
		Throwable e = null;
		try{
			TrackingAreaIdentityMap decobj = TrackingAreaIdentityMap.decode(data);
		}
		catch(Exception ex){
			System.out.println("Exception while decoding = "+ex);
			e =ex;
		}
		
		assertTrue(e instanceof InvalidInputException);
	}
	
	@Test
	public void testInValidMccWithLen2(){
		TrackingAreaIdentityMap obj = new TrackingAreaIdentityMap();
		obj.setMobCountryCode("01");;
		obj.setMobNetworkCode("891");
		Throwable e = null;
		try{
			TrackingAreaIdentityMap.encode(obj);
		}
		catch(Exception ex){
			e = ex;
			System.out.println("Exception  = "+ex);
		}
		assertTrue(e instanceof InvalidInputException);
	}
	
	@Test
	public void testInValidMccWithLen4(){
		TrackingAreaIdentityMap obj = new TrackingAreaIdentityMap();
		obj.setMobCountryCode("0132");;
		obj.setMobNetworkCode("891");
		Throwable e = null;
		try{
			TrackingAreaIdentityMap.encode(obj);
		}
		catch(InvalidInputException ex){
			e = ex;
			System.out.println("Exception  = "+ex);
		}
		assertTrue(e instanceof InvalidInputException);
	}
	
	
	@Test
	public void testValidMccWithTwoDigitMNC() {
		TrackingAreaIdentityMap obj = new TrackingAreaIdentityMap();
		obj.setMobCountryCode("091");;
		obj.setMobNetworkCode("89");
		
		// Tracking in hex
		byte[] tac = new byte[] {0x01, 0x02};
		obj.setTac(tac);
		byte[] encdata = null;
		try {
			encdata = TrackingAreaIdentityMap.encode(obj);
		}
		catch(InvalidInputException ex){
			assertFalse("Failed to encode the object "+obj+ex, false);
		}
		
		System.out.println("encode data = "+Util.formatBytes(encdata));
		
		TrackingAreaIdentityMap decobj = null;
		try{
			decobj = TrackingAreaIdentityMap.decode(encdata);
		}
		catch(InvalidInputException ex){
			assertFalse("Failed to decode the data " + ex, false);
		}
		
		assertNotNull(decobj);
		System.out.println("mcc = "+ decobj.getMobCountryCode());
		System.out.println("mnc = "+ decobj.getMobNetworkCode());
		System.out.println("tac = "+ Util.formatBytes(decobj.getTac()));

		assertEquals(obj.getMobCountryCode(), decobj.getMobCountryCode());
		assertEquals(obj.getMobNetworkCode(), decobj.getMobNetworkCode());
		assertArrayEquals(tac, decobj.getTac());
	}
	
	@Test
	public void testValidMccWithThreeDigitMNC(){
		TrackingAreaIdentityMap obj = new TrackingAreaIdentityMap();
		obj.setMobCountryCode("011");;
		obj.setMobNetworkCode("891");
		
		// Tracking in hex
		byte[] tac = new byte[] {0x01, 0x02};
		obj.setTac(tac);
		byte[] encdata = null;
		try {
			encdata = TrackingAreaIdentityMap.encode(obj);
		}
		catch(InvalidInputException ex){
			assertFalse("Failed to encode the object "+obj+ex, false);
		}
		
		System.out.println("encode data = "+Util.formatBytes(encdata));
		
		TrackingAreaIdentityMap decobj = null;
		try{
			decobj = TrackingAreaIdentityMap.decode(encdata);
		}
		catch(InvalidInputException ex){
			assertFalse("Failed to decode the data " + ex, false);
		}
		
		assertNotNull(decobj);
		System.out.println("mcc = "+ decobj.getMobCountryCode());
		System.out.println("mnc = "+ decobj.getMobNetworkCode());
		System.out.println("tac = "+ Util.formatBytes(decobj.getTac()));

		assertEquals(obj.getMobCountryCode(), decobj.getMobCountryCode());
		assertEquals(obj.getMobNetworkCode(), decobj.getMobNetworkCode());
		assertArrayEquals(tac, decobj.getTac());		
	}
}
