package com.agnity.map.test.datatypes;

import static org.junit.Assert.*;

import org.junit.Test;

import com.agnity.map.asngenerated.AnyTimeSubscriptionInterrogationRes;
import com.agnity.map.datatypes.AnyTimeSubscriptionInterrogationResMap;
import com.agnity.map.operations.MapOpCodes;
import com.agnity.map.operations.MapOperationsCoding;
import com.agnity.map.parser.ParseAsnToUserType;

public class AnyTimeSubscriptionInterrogationResMapTest {

	@Test
	public void testDecode() {
		byte[] input = new byte[]{
		                                                            (byte)0x30, (byte)0x81, (byte)0x93, 
		(byte)0xa1, (byte)0x28, (byte)0x30, (byte)0x26, (byte)0x30, (byte)0x24, (byte)0x83, (byte)0x01, 
		(byte)0x21, (byte)0x84, (byte)0x01, (byte)0x0a, (byte)0x85, (byte)0x05, (byte)0xc6, (byte)0x54, 
		(byte)0x56, (byte)0x78, (byte)0xf8, (byte)0x88,	(byte)0x08, (byte)0x00, (byte)0x00, (byte)0x78, 
		(byte)0x96, (byte)0x78, (byte)0x09, (byte)0x09,	(byte)0x09, (byte)0x86, (byte)0x01, (byte)0xec, 
		(byte)0x87, (byte)0x01, (byte)0x07, (byte)0x8a,	(byte)0x05, (byte)0xb8, (byte)0x78, (byte)0x78, 
		(byte)0x78, (byte)0xf6, (byte)0xa2, (byte)0x13,	(byte)0x30, (byte)0x08, (byte)0x30, (byte)0x06, 
		(byte)0x82, (byte)0x01, (byte)0x13, (byte)0x84,	(byte)0x01, (byte)0x04, (byte)0x12, (byte)0x04, 
		(byte)0x39, (byte)0x38, (byte)0x33, (byte)0x34,	(byte)0x02, (byte)0x01, (byte)0x04, (byte)0xa3, 
		(byte)0x0a, (byte)0x30, (byte)0x08,	(byte)0x03, (byte)0x02, (byte)0x00, (byte)0x31, (byte)0x03,	
		(byte)0x02, (byte)0x04, (byte)0x20, (byte)0xa4, (byte)0x1d, (byte)0xb4, (byte)0x1b, (byte)0xa0, 
		(byte)0x14, (byte)0x30, (byte)0x12, (byte)0x04, (byte)0x04, (byte)0xa1, (byte)0x03,	(byte)0x65, 
		(byte)0x78, (byte)0x02, (byte)0x01, (byte)0x01, (byte)0x04, (byte)0x04, (byte)0x96, (byte)0x54,	
		(byte)0x78, (byte)0x46, (byte)0x0a,	(byte)0x01, (byte)0x01, (byte)0x81, (byte)0x01,	(byte)0x10,
		(byte)0x84, (byte)0x00, (byte)0x85, (byte)0x02, (byte)0x06, (byte)0xc0, (byte)0x86,	(byte)0x02, 
		(byte)0x06, (byte)0x20, (byte)0x88, (byte)0x02, (byte)0x00, (byte)0x40, (byte)0x89, (byte)0x02, 
		(byte)0x00, (byte)0x10, (byte)0xaa, (byte)0x17, (byte)0x30, (byte)0x0d, (byte)0x04, (byte)0x03, 
		(byte)0x99, (byte)0x78, (byte)0x78, (byte)0xa0, (byte)0x06, (byte)0x82, (byte)0x01, (byte)0x10,
		(byte)0x83, (byte)0x01, (byte)0xd1, (byte)0x30, (byte)0x06, (byte)0x04, (byte)0x04, (byte)0x93, 
		(byte)0x54, (byte)0x16, (byte)0x32
		};
	
		Throwable e = null;
		// Get 
		AnyTimeSubscriptionInterrogationRes asnobj = null;
		try {
			asnobj = (AnyTimeSubscriptionInterrogationRes) MapOperationsCoding
				.decodeOperationsForOpCode(input, MapOpCodes.MAP_ANY_TIME_SUBSCRIPTION_INTERROGATION, false);
		}
		catch(Exception ex){
			e = ex;
			System.out.println("Error decoding binary to asn object");
			ex.printStackTrace();
		}
		
		
		
		//Get the user object
		AnyTimeSubscriptionInterrogationResMap userobj = null;
		
		try {
			userobj = ParseAsnToUserType.decodeAsnToAtsiRes(asnobj);
		}
		catch(Exception ex) {
			e = ex;
			System.out.println("Error decoding asn to atsi user object");
			ex.printStackTrace();
		}
		System.out.println("User object = "+userobj);
		assertNull(e);
		assertNotNull(userobj);
	}
}
