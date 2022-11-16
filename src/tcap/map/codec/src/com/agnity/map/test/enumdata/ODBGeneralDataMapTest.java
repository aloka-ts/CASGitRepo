package com.agnity.map.test.enumdata;

import static org.junit.Assert.*;

import org.junit.Test;

import com.agnity.map.datatypes.ODBGeneralDataMap;
import com.agnity.map.enumdata.ODBGeneralDataMapEnum;
import com.agnity.map.util.Util;

public class ODBGeneralDataMapTest {
	
	@Test
	public void testEncode() {
		System.out.println("testEncode");
		byte[] data = new byte[]{ 0x00, 0x00, 0x10};
		ODBGeneralDataMap gendata = new ODBGeneralDataMap();
		gendata.enableOdbGeneralDataAtIndex(ODBGeneralDataMapEnum.RAOMING_OUTSIDE_PLMN_IC_CALLS_BARRED);
		byte[] actuals = null;
		Throwable e = null;
		try{
			actuals = ODBGeneralDataMap.encode(gendata);
		}
		catch(Exception ex){
			e = ex;
		}
		
		assertNull(e);
		//assertArrayEquals(data, ODBGeneralDataMap.encode(gendata));
	}

	@Test
	public void testDecode() {
		byte[] data = new byte[]{ 0x00, 0x00, 0x00, 0x10};
		//ODBGeneralDataMap gendata = ODBGeneralDataMap.decode(data);
		//assertTrue(gendata.getOdbGeneralDataAtIndex(
		//		ODBGeneralDataMapEnum.REGISTRATION_INTERNATIONAL_CF_BARRED));
	}

}
