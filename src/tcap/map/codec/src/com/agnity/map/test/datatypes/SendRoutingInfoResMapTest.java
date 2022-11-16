package com.agnity.map.test.datatypes;

import static org.junit.Assert.*;

import org.junit.Test;

import com.agnity.map.asngenerated.AnyTimeModificationRes;
import com.agnity.map.asngenerated.SendRoutingInfoRes;
import com.agnity.map.datatypes.AnyTimeModificationResMap;
import com.agnity.map.datatypes.SendRoutingInfoResMap;
import com.agnity.map.operations.MapOpCodes;
import com.agnity.map.operations.MapOperationsCoding;
import com.agnity.map.parser.ParseAsnToUserType;

public class SendRoutingInfoResMapTest {

	@Test
	public void testDecode() {
		
		byte[] sridata = new byte[] {
																			(byte)0xa3, (byte)0x81, (byte)0x8d,
				(byte)0x89, (byte)0x03, (byte)0x23, (byte)0x21, (byte)0x65, (byte)0xa7, (byte)0x58, (byte)0xa0,
				(byte)0x48, (byte)0x02, (byte)0x01, (byte)0x32, (byte)0x80, (byte)0x08, (byte)0x10, (byte)0x02, 
				(byte)0x00, (byte)0x02, (byte)0x00, (byte)0x00, (byte)0x04, (byte)0x04, (byte)0x81, (byte)0x04,
				(byte)0x80, (byte)0x33, (byte)0x22, (byte)0x11, (byte)0x82, (byte)0x05, (byte)0x03, (byte)0x13, 
			    (byte)0x23, (byte)0x23, (byte)0x23, (byte)0xa3, (byte)0x07, (byte)0x81, (byte)0x05, (byte)0xc2, 
			    (byte)0xfc, (byte)0x1c, (byte)0x00, (byte)0x15, (byte)0x86, (byte)0x04, (byte)0x80, (byte)0x02,
			    (byte)0x33, (byte)0x22, (byte)0xaa, (byte)0x1d, (byte)0x80, (byte)0x07, (byte)0xb1, (byte)0xc2, 
			    (byte)0x22, (byte)0x30, (byte)0x33, (byte)0x44, (byte)0x55, (byte)0x81, (byte)0x05, (byte)0xab,
			    (byte)0xbb, (byte)0xbb, (byte)0x11, (byte)0x22, (byte)0x83, (byte)0x08, (byte)0x10, (byte)0x87,
			    (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x4d, (byte)0x4d, (byte)0x86, (byte)0x01,
			    (byte)0x08, (byte)0xa3, (byte)0x07, (byte)0x83, (byte)0x05, (byte)0x80, (byte)0x33, (byte)0x44,
			    (byte)0x43, (byte)0xf2, (byte)0xa8, (byte)0x03, (byte)0x83, (byte)0x01, (byte)0x05, (byte)0x82,
			    (byte)0x04, (byte)0x80, (byte)0x33, (byte)0x44, (byte)0xf2, (byte)0x8c, (byte)0x03, (byte)0xa0,
			    (byte)0x54, (byte)0xf2, (byte)0x8d, (byte)0x01, (byte)0x05, (byte)0x8f, (byte)0x02, (byte)0x05,
			    (byte)0x20, (byte)0x90, (byte)0x02, (byte)0x00, (byte)0x08, (byte)0xb1, (byte)0x06, (byte)0x04,
			    (byte)0x04, (byte)0x80, (byte)0x11, (byte)0x33, (byte)0x22, (byte)0xb2, (byte)0x06, (byte)0x04,
			    (byte)0x01, (byte)0xa0, (byte)0x04, (byte)0x01, (byte)0x00, (byte)0xb3, (byte)0x03, (byte)0x82,
			    (byte)0x01, (byte)0x40, (byte)0x95, (byte)0x01, (byte)0x02
			    
		};
		
		SendRoutingInfoRes asnresp = null;

		Throwable e = null;
		try{
			asnresp = (SendRoutingInfoRes) MapOperationsCoding.
				decodeOperationsForOpCode(sridata, MapOpCodes.MAP_SEND_ROUTING_INFO, false);
		}
		catch(Exception ex){
			System.out.println("ex1 = "+ex);
			e = ex;
			ex.printStackTrace();
		}
		
		SendRoutingInfoResMap userobj = null;
		
		try{
			userobj = ParseAsnToUserType.decodeAsnToSriRes(asnresp);
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
