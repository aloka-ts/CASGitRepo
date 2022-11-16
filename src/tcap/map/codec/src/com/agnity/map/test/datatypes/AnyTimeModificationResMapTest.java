package com.agnity.map.test.datatypes;

import static org.junit.Assert.*;

import org.junit.Test;

import com.agnity.map.asngenerated.AnyTimeInterrogationRes;
import com.agnity.map.asngenerated.AnyTimeModificationRes;
import com.agnity.map.datatypes.AnyTimeInterrogationResMap;
import com.agnity.map.datatypes.AnyTimeModificationResMap;
import com.agnity.map.enumdata.ExtentionMapEnum;
import com.agnity.map.enumdata.NumberPlanMapEnum;
import com.agnity.map.operations.MapOpCodes;
import com.agnity.map.operations.MapOperationsCoding;
import com.agnity.map.parser.ParseAsnToUserType;

public class AnyTimeModificationResMapTest {

	@Test
	public void testDecode() {
		
		byte[] atmdata = new byte[] {
																		(byte)0x30, (byte)0x81, (byte)0x91, 
			(byte)0xa0, (byte)0x18, (byte)0xa1, (byte)0x16, (byte)0x80, (byte)0x01, (byte)0x43, (byte)0xa1,
			(byte)0x08, (byte)0x30, (byte)0x06, (byte)0x83, (byte)0x01, (byte)0xd6, (byte)0x84, (byte)0x01,
			(byte)0x05, (byte)0x82, (byte)0x04, (byte)0x33, (byte)0x33, (byte)0x34, (byte)0x31, (byte)0x83,
			(byte)0x01, (byte)0x04, (byte)0xa1, (byte)0x6d, (byte)0xa2, (byte)0x30, (byte)0xa0, (byte)0x2b,
			(byte)0x30, (byte)0x13, (byte)0x04, (byte)0x04, (byte)0x91, (byte)0x76, (byte)0x48, (byte)0x94,
			(byte)0x02, (byte)0x01, (byte)0x05, (byte)0x04, (byte)0x05, (byte)0x93, (byte)0x66, (byte)0x77,
			(byte)0x77, (byte)0x87, (byte)0x0a, (byte)0x01, (byte)0x01, (byte)0x30, (byte)0x14, (byte)0x04,
			(byte)0x05, (byte)0x99, (byte)0x45, (byte)0x45, (byte)0x45, (byte)0xf5, (byte)0x02, (byte)0x01,
			(byte)0x54, (byte)0x04, (byte)0x05, (byte)0xc8, (byte)0x33, (byte)0x45, (byte)0x45, (byte)0x25, 
			(byte)0x0a, (byte)0x01, (byte)0x00, (byte)0x81, (byte)0x01, (byte)0x02, (byte)0xa9, (byte)0x12, 
			(byte)0xa0, (byte)0x10, (byte)0x30, (byte)0x0e, (byte)0x80, (byte)0x01, (byte)0x0e, (byte)0x81,
			(byte)0x01, (byte)0x03, (byte)0x82, (byte)0x03, (byte)0x80, (byte)0x89, (byte)0x31, (byte)0x83,
			(byte)0x01, (byte)0x01, (byte)0xb0, (byte)0x0a, (byte)0x30, (byte)0x08, (byte)0x0a, (byte)0x01,
			(byte)0x02, (byte)0xa0, (byte)0x03, (byte)0x0a, (byte)0x01, (byte)0x00, (byte)0xb4, (byte)0x19,
			(byte)0xa0, (byte)0x14, (byte)0x30, (byte)0x12, (byte)0x04, (byte)0x04, (byte)0xe4, (byte)0x37,
			(byte)0x36, (byte)0xf2, (byte)0x02, (byte)0x01, (byte)0x23, (byte)0x04, (byte)0x04, (byte)0x98,
			(byte)0x21, (byte)0x93, (byte)0x80, (byte)0x0a, (byte)0x01, (byte)0x01, (byte)0x81, (byte)0x01,
			(byte)0x01, (byte)0xa3, (byte)0x06, (byte)0x30, (byte)0x04, (byte)0x03, (byte)0x02, (byte)0x00,
			(byte)0x10
		};
		
		AnyTimeModificationRes asnresp = null;

		Throwable e = null;
		try{
			asnresp = (AnyTimeModificationRes) MapOperationsCoding.
				decodeOperationsForOpCode(atmdata, MapOpCodes.MAP_ANY_TIME_MODIFICATION, false);
		}
		catch(Exception ex){
			System.out.println("ex1 = "+ex);
			e = ex;
			ex.printStackTrace();
		}
		
		AnyTimeModificationResMap userobj = null;
		
		try{
			userobj = ParseAsnToUserType.decodeAsnToAtmRes(asnresp);
		}
		catch(Exception ex){
			e = ex;
			System.out.println("ex2 = "+ex);
			e.printStackTrace();
		}
		
		assertNull(e);
		assertNotNull(userobj);
		System.out.println("=================================");
		System.out.println(userobj);
		System.out.println("=================================");
	}
}
