package com.agnity.map.test.datatypes;

import static org.junit.Assert.*;

import org.junit.Test;

import com.agnity.map.datatypes.ISDNAddressStringMap;
import com.agnity.map.enumdata.ExtentionMapEnum;
import com.agnity.map.enumdata.NatureOfAddressMapEnum;
import com.agnity.map.enumdata.NatureOfAddressIndicatorMapEnum;
import com.agnity.map.enumdata.NumberPlanMapEnum;
import com.agnity.map.util.Util;

public class ISDNAddressStringMapTest {

	@Test
	public void decodeValidIsdnAddr() {
		byte[] gsmscfaddr = new byte[]{(byte) 0x99, 0x12, 0x32, 0x43, 0x54, 0x65};
		ISDNAddressStringMap obj = null;
		Throwable e = null;
				
		try {
			obj = new ISDNAddressStringMap(gsmscfaddr);
		}
		catch(Exception ex){
			e = ex;
			System.out.println("error decoding "+ex);
		}
		System.out.println("ext = "+obj.getExtention());
		System.out.println("noa = "+obj.getNai());
		System.out.println("np = "+obj.getNumberPlan());
		System.out.println("addr = "+obj.getAddressDigits());
		
		assertNotNull(obj);
		assertNull(e);
		assertEquals(ExtentionMapEnum.NO_EXTENTION, obj.getExtention());
		assertEquals(NatureOfAddressMapEnum.INTERNATIONAL_NUMBER, obj.getNai());
		assertEquals(NumberPlanMapEnum.PRIVATE_NUMBERING_PLAN, obj.getNumberPlan());
		assertEquals("2123344556", obj.getAddressDigits());
	}

	@Test
	public void testValidEncode() {
		ISDNAddressStringMap obj = new ISDNAddressStringMap();
		obj.setAddressDigits("2123344556");
		obj.setExtention(ExtentionMapEnum.NO_EXTENTION);
		obj.setNatureOfNumber(NatureOfAddressMapEnum.INTERNATIONAL_NUMBER);
		obj.setNumberPlan(NumberPlanMapEnum.PRIVATE_NUMBERING_PLAN);
		
		byte[] gsmscfaddr = new byte[]{(byte) 0x99, 0x12, 0x32, 0x43, 0x54, 0x65};
		byte[]  data = null;
		Throwable e = null;
		try {
			data = obj.encode();
		}
		catch(Exception ex){
			e = ex;
			System.out.println("error decoding "+ex);
		}
		
		assertNotNull(data);
		assertNull(e);
		
		System.out.println("bytes = "+Util.formatBytes(data));
		assertArrayEquals(gsmscfaddr, data);
	}

}
