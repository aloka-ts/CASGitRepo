package com.agnity.map.test.datatypes;

import static org.junit.Assert.*;

import org.junit.Test;

import com.agnity.map.asngenerated.AnyTimeInterrogationRes;
import com.agnity.map.datatypes.AnyTimeInterrogationResMap;
import com.agnity.map.enumdata.ExtentionMapEnum;
import com.agnity.map.enumdata.NatureOfAddressIndicatorMapEnum;
import com.agnity.map.enumdata.NotReachableReasonMapEnum;
import com.agnity.map.enumdata.NumberPlanMapEnum;
import com.agnity.map.operations.MapOpCodes;
import com.agnity.map.operations.MapOperationsCoding;
import com.agnity.map.parser.ParseAsnToUserType;

public class AnyTimeInterrogationResMapTest {

	@Test
	public void decodeAtiResponseWithSubStateAssumedIdle() {
		/*
		byte[] atiresp = new byte[] {
			            (byte)0x30, (byte)0x82, (byte)0x01, (byte)0x0f, (byte)0x30, (byte)0x82, (byte)0x01,
			(byte)0x0b, (byte)0xA0, (byte)0x6E, (byte)0x02, (byte)0x01, (byte)0x32, (byte)0x80, (byte)0x08,
			(byte)0x10, (byte)0x7B, (byte)0x11, (byte)0x94, (byte)0x01, (byte)0xE1, (byte)0xBB, (byte)0x01,
			(byte)0x81, (byte)0x06, (byte)0x98, (byte)0x67, (byte)0x34, (byte)0x10, (byte)0x32, (byte)0x54,
			(byte)0x82, (byte)0x05, (byte)0x83, (byte)0x11, (byte)0x87, (byte)0x87, (byte)0x07, (byte)0xa3,
			(byte)0x09, (byte)0x80, (byte)0x07, (byte)0xf4, (byte)0xd2, (byte)0xa3, (byte)0xff, (byte)0xff,
			(byte)0x85, (byte)0x12, (byte)0x85, (byte)0x03, (byte)0x98, (byte)0x12, (byte)0x34, (byte)0x86,
			(byte)0x05, (byte)0xa6, (byte)0x23, (byte)0x98, (byte)0x27, (byte)0x23, (byte)0x87, (byte)0x09,
			(byte)0x00, (byte)0x01, (byte)0x76, (byte)0x43, (byte)0x05, (byte)0x3d, (byte)0x93, (byte)0x01, 
			(byte)0x01, (byte)0xaa, (byte)0x27, (byte)0x80, (byte)0x07, (byte)0xc1, (byte)0x92, (byte)0xb1,
			(byte)0x10, (byte)0x12, (byte)0x34, (byte)0x56, (byte)0x81, (byte)0x05, (byte)0xc1, (byte)0x25,
			(byte)0x57, (byte)0x33, (byte)0x33, (byte)0x83, (byte)0x08, (byte)0x10, (byte)0x8c, (byte)0x00,
			(byte)0x02, (byte)0x00, (byte)0x00, (byte)0x03, (byte)0x02, (byte)0x84, (byte)0x08, (byte)0x06, 
			(byte)0x01, (byte)0x12, (byte)0x00, (byte)0x00, (byte)0x02, (byte)0x01, (byte)0x00, (byte)0x86, 
			(byte)0x01, (byte)0x32, (byte)0xab, (byte)0x05, (byte)0x80, (byte)0x03, (byte)0x23, (byte)0x45,
			(byte)0x23, (byte)0xa1, (byte)0x03, (byte)0x0a, (byte)0x01, (byte)0x01, (byte)0xa3, (byte)0x3a,
			(byte)0xa0, (byte)0x07, (byte)0x81, (byte)0x05, (byte)0xc3, (byte)0x34, (byte)0xe2, (byte)0x38, 
			(byte)0xe4, (byte)0x81, (byte)0x06, (byte)0xc1, (byte)0xd2, (byte)0xf6, (byte)0xff, (byte)0xe6,
			(byte)0x78, (byte)0x82, (byte)0x08, (byte)0x10, (byte)0x78, (byte)0x00, (byte)0x02, (byte)0x00,
			(byte)0x00, (byte)0x38, (byte)0x07, (byte)0x83, (byte)0x05, (byte)0x91, (byte)0x34, (byte)0x62,
			(byte)0x97, (byte)0xf8, (byte)0x84, (byte)0x03, (byte)0x45, (byte)0x00, (byte)0x00, (byte)0x87,
			(byte)0x08, (byte)0x03, (byte)0x01, (byte)0xfe, (byte)0x00, (byte)0x0d, (byte)0x05, (byte)0x02,
			(byte)0x04, (byte)0x89, (byte)0x01, (byte)0x16, (byte)0xaa, (byte)0x04, (byte)0x80, (byte)0x02,
			(byte)0x54, (byte)0x54, (byte)0x85, (byte)0x08, (byte)0x54, (byte)0x63, (byte)0x86, (byte)0x87,
			(byte)0x43, (byte)0x12, (byte)0x23, (byte)0x32, (byte)0x86, (byte)0x03, (byte)0x08, (byte)0x44, 
			(byte)0x22, (byte)0xa8, (byte)0x14, (byte)0x80, (byte)0x01, (byte)0xf0, (byte)0x81, (byte)0x05, 
			(byte)0x76, (byte)0x54, (byte)0x54, (byte)0x54, (byte)0x34, (byte)0x82, (byte)0x05, (byte)0xb1,
			(byte)0x34, (byte)0x43, (byte)0x43, (byte)0xf3, (byte)0x83, (byte)0x01, (byte)0x04, (byte)0x89,
			(byte)0x01, (byte)0x01, (byte)0x8a, (byte)0x04, (byte)0x99, (byte)0x99, (byte)0x99, (byte)0x99,
			(byte)0x8b, (byte)0x01, (byte)0x02, (byte)0xad, (byte)0x27, (byte)0x80, (byte)0x07, (byte)0x33,
			(byte)0x45, (byte)0xc2, (byte)0x30, (byte)0x98, (byte)0x76, (byte)0x53, (byte)0x81, (byte)0x05,
			(byte)0x44, (byte)0x04, (byte)0x45, (byte)0x66, (byte)0x55, (byte)0x83, (byte)0x08, (byte)0x10,
			(byte)0x0c, (byte)0x00, (byte)0x03, (byte)0x00, (byte)0x00, (byte)0x03, (byte)0x2a, (byte)0x84,
			(byte)0x08, (byte)0x00, (byte)0x01, (byte)0x12, (byte)0x00, (byte)0x00, (byte)0x02, (byte)0x03,
			(byte)0x05, (byte)0x86, (byte)0x01, (byte)0x13
		};
		*/
		
		/*
		// Without LocationInfo and locatinInfoEPS
		byte[] atiresp = new byte[] {
				            (byte)0x30, (byte)0x3f, (byte)0x30, (byte)0x3d, (byte)0xa0, (byte)0x07, (byte)0x81,
				(byte)0x05, (byte)0xb1, (byte)0x01, (byte)0x23, (byte)0x89, (byte)0x27, (byte)0xa1, (byte)0x02,
				(byte)0x81, (byte)0x00, (byte)0x85, (byte)0x08, (byte)0x21, (byte)0x43, (byte)0x65, (byte)0x34,
				(byte)0x54, (byte)0x76, (byte)0x21, (byte)0x34, (byte)0x86, (byte)0x03, (byte)0x00, (byte)0x40, 
				(byte)0x01, (byte)0xa8, (byte)0x13, (byte)0x80, (byte)0x02, (byte)0x96, (byte)0x96, (byte)0x81, 
				(byte)0x05, (byte)0x23, (byte)0x12, (byte)0x52, (byte)0x54, (byte)0xf5, (byte)0x82, (byte)0x03,
				(byte)0x91, (byte)0x88, (byte)0x88, (byte)0x83, (byte)0x01, (byte)0x05, (byte)0x89, (byte)0x01,
				(byte)0x00, (byte)0x8a, (byte)0x04, (byte)0x99, (byte)0x99, (byte)0x99, (byte)0x99, (byte)0x8b,
				(byte)0x01, (byte)0x03
		}; */
		
		/** works 
		byte[] atiresp = new byte[] {
					(byte)0x30, (byte)0x44, (byte)0x30, (byte)0x42, (byte)0xa0, (byte)0x07, (byte)0x81,
		(byte)0x05, (byte)0xb1, (byte)0x01, (byte)0x23, (byte)0x89, (byte)0x27, (byte)0xa1, (byte)0x02,
		(byte)0x81, (byte)0x00, (byte)0xa3, (byte)0x03, (byte)0x89, (byte)0x01, (byte)0x07, (byte)0x85,
		(byte)0x08, (byte)0x21, (byte)0x43, (byte)0x65, (byte)0x34, (byte)0x54, (byte)0x76, (byte)0x21, 
		(byte)0x34, (byte)0x86, (byte)0x03, (byte)0x00, (byte)0x40, (byte)0x01, (byte)0xa8, (byte)0x13, 
		(byte)0x80, (byte)0x02, (byte)0x96, (byte)0x96, (byte)0x81, (byte)0x05, (byte)0x23, (byte)0x12,
		(byte)0x52, (byte)0x54, (byte)0xf5, (byte)0x82, (byte)0x03, (byte)0x91, (byte)0x88, (byte)0x88,
		(byte)0x83, (byte)0x01, (byte)0x05, (byte)0x89, (byte)0x01, (byte)0x00, (byte)0x8a, (byte)0x04,
		(byte)0x99, (byte)0x99, (byte)0x99, (byte)0x99, (byte)0x8b, (byte)0x01, (byte)0x03
		};
		*/
		byte[] atiresp = new byte[] {
					            									(byte)0x30, (byte)0x81, (byte)0xa8, 
		(byte)0x30, (byte)0x81, (byte)0xa5, (byte)0xa0, (byte)0x6a, (byte)0x02, (byte)0x02, (byte)0x43,
		(byte)0x23, (byte)0x80, (byte)0x08, (byte)0x10, (byte)0x0c, (byte)0x00, (byte)0x20, (byte)0x00,
		(byte)0x00, (byte)0x17, (byte)0x04, (byte)0x81, (byte)0x05, (byte)0xb1, (byte)0x01, (byte)0x23, 
		(byte)0x89, (byte)0x27, (byte)0x82, (byte)0x06, (byte)0x84, (byte)0x57, (byte)0x78, (byte)0x49, 
		(byte)0x33, (byte)0x03, (byte)0xa3, (byte)0x09, (byte)0x80, (byte)0x07, (byte)0xc2, (byte)0xb2,
		(byte)0xe1, (byte)0x0c, (byte)0x9f, (byte)0x0d, (byte)0x12, (byte)0x85, (byte)0x03, (byte)0x14,
		(byte)0xf1, (byte)0x13, (byte)0x86, (byte)0x04, (byte)0x99, (byte)0x54, (byte)0x23, (byte)0x43,
		(byte)0x87, (byte)0x0a, (byte)0x00, (byte)0x01, (byte)0x88, (byte)0x99, (byte)0x88, (byte)0x00,
		(byte)0x00, (byte)0x0c, (byte)0x08, (byte)0x09, (byte)0xaa, (byte)0x29, (byte)0x80, (byte)0x07,
		(byte)0x42, (byte)0x1c, (byte)0x1c, (byte)0xc0, (byte)0xab, (byte)0xce, (byte)0xff, (byte)0x81,
		(byte)0x05, (byte)0xa2, (byte)0x34, (byte)0x56, (byte)0xff, (byte)0xee, (byte)0x83, (byte)0x08,
		(byte)0x10, (byte)0x81, (byte)0x00, (byte)0x02, (byte)0x00, (byte)0x00, (byte)0x03, (byte)0x2d,
		(byte)0x84, (byte)0x0a, (byte)0x00, (byte)0x01, (byte)0x88, (byte)0x99, (byte)0x88, (byte)0x00, 
		(byte)0x00, (byte)0x02, (byte)0x01, (byte)0x0a, (byte)0x86, (byte)0x01, (byte)0x45, (byte)0xa1, 
		(byte)0x02, (byte)0x81,	(byte)0x00, (byte)0xa3, (byte)0x03, (byte)0x89, (byte)0x01, (byte)0x07,	
		(byte)0x85, (byte)0x08, (byte)0x21, (byte)0x43, (byte)0x65, (byte)0x34, (byte)0x54, (byte)0x76,	
		(byte)0x21, (byte)0x34, (byte)0x86, (byte)0x03, (byte)0x00, (byte)0x40, (byte)0x01, (byte)0xa8,	
		(byte)0x13, (byte)0x80, (byte)0x02, (byte)0x96, (byte)0x96, (byte)0x81, (byte)0x05, (byte)0x23,	
		(byte)0x12, (byte)0x52, (byte)0x54, (byte)0xf5, (byte)0x82, (byte)0x03, (byte)0x91, (byte)0x88,	
		(byte)0x88, (byte)0x83, (byte)0x01, (byte)0x05, (byte)0x89, (byte)0x01, (byte)0x00, (byte)0x8a,	
		(byte)0x04, (byte)0x99, (byte)0x99, (byte)0x99, (byte)0x99, (byte)0x8b, (byte)0x01, (byte)0x03
		};
	
		AnyTimeInterrogationRes asnresp = null;
		Throwable e = null;
		try{
			asnresp = (AnyTimeInterrogationRes) MapOperationsCoding.
				decodeOperationsForOpCode(atiresp, MapOpCodes.MAP_ANY_TIME_INTERROGATION, false);
		}
		catch(Exception ex){
			System.out.println("ex1 = "+ex);
			e = ex;
			ex.printStackTrace();
		}
		
		AnyTimeInterrogationResMap userobj = null;
		
		try{
			userobj = ParseAsnToUserType.decodeAsnToAtiRes(asnresp);
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
			
		assertEquals(userobj.getSubscriberInfo().getLocationInfo().getVlrNum().getNumberPlan(), 
				NumberPlanMapEnum.NATIONAL_NUMBERING_PLAN);
		assertEquals(userobj.getSubscriberInfo().getLocationInfo().getVlrNum().getExtention(), 
				ExtentionMapEnum.NO_EXTENTION);
		assertEquals(userobj.getSubscriberInfo().getLocationInfo().getVlrNum().getAddressDigits(),
				"1234567809");
		assertEquals(userobj.getSubscriberInfo().getSubscriberState().isAssumedIdle(), true );
		assertEquals(userobj.getSubscriberInfo().getSubscriberState().isCamelBusy(), false);
		assertEquals(userobj.getSubscriberInfo().getSubscriberState().isNotProvidedFromVLR(), false);
		
	}

	//@Test
	public void decodeAtiResponseWithSubStateNetDetNotReachable() {
		byte[] atiresp = new byte[] {
			(byte)0x30, (byte)0x11, (byte)0x30, (byte)0x0f, (byte)0xa0,
			(byte)0x08, (byte)0x81, (byte)0x06, (byte)0x98, (byte)0x21, (byte)0x43, (byte)0x65, (byte)0x87,
			(byte)0x90, (byte)0xA1, (byte)0x03, (byte)0x8A, (byte)0x01, (byte)0x03
		};
		
		AnyTimeInterrogationRes asnresp = null;
		Throwable e = null;
		try{
			asnresp = (AnyTimeInterrogationRes) MapOperationsCoding.
				decodeOperationsForOpCode(atiresp, MapOpCodes.MAP_ANY_TIME_INTERROGATION, false);
		}
		catch(Exception ex){
			System.out.println("ex1 = "+ex);
			e = ex;
		}
		
		AnyTimeInterrogationResMap userobj = null;
		
		try{
			userobj = ParseAsnToUserType.decodeAsnToAtiRes(asnresp);
			System.out.println("Ss TEST 2 = " + userobj.getSubscriberInfo().getSubscriberState());

		}
		catch(Exception ex){
			e = ex;
			System.out.println("ex2 = "+ex);
		}
		
		assertNull(e);
		assertNotNull(userobj);
		assertEquals(userobj.getSubscriberInfo().getLocationInfo().getVlrNum().getNumberPlan(), 
				NumberPlanMapEnum.NATIONAL_NUMBERING_PLAN);
		
		
		assertEquals(userobj.getSubscriberInfo().getLocationInfo().getVlrNum().getExtention(), 
				ExtentionMapEnum.NO_EXTENTION);
		
		assertEquals(userobj.getSubscriberInfo().getLocationInfo().getVlrNum().getAddressDigits(),
				"1234567809");
		

		assertFalse(userobj.getSubscriberInfo().getSubscriberState().isAssumedIdle());
		assertFalse(userobj.getSubscriberInfo().getSubscriberState().isCamelBusy());
		assertFalse(userobj.getSubscriberInfo().getSubscriberState().isNotProvidedFromVLR());
	}


	//@Test
	public void decodeAtiResponseWithSubStateCamelBusy() {
		byte[] atiresp = new byte[] {
			(byte)0x30, (byte)0x10, (byte)0x30, (byte)0x0e, (byte)0xa0,
			(byte)0x08, (byte)0x81, (byte)0x06, (byte)0x98, (byte)0x21, (byte)0x43, (byte)0x65, (byte)0x87,
			(byte)0x90, (byte)0xA1, (byte)0x02, (byte)0x81, (byte)0x00
		};
		
		AnyTimeInterrogationRes asnresp = null;
		Throwable e = null;
		try{
			asnresp = (AnyTimeInterrogationRes) MapOperationsCoding.
				decodeOperationsForOpCode(atiresp, MapOpCodes.MAP_ANY_TIME_INTERROGATION, false);
		}
		catch(Exception ex){
			System.out.println("ex1 = "+ex);
			e = ex;
		}
		
		AnyTimeInterrogationResMap userobj = null;
		
		try{
			userobj = ParseAsnToUserType.decodeAsnToAtiRes(asnresp);
			System.out.println("Ss TEST 3 = " + userobj.getSubscriberInfo().getSubscriberState());

		}
		catch(Exception ex){
			e = ex;
			System.out.println("ex2 = "+ex);
		}
		
		assertNull(e);
		assertNotNull(userobj);
		assertEquals(userobj.getSubscriberInfo().getLocationInfo().getVlrNum().getNumberPlan(), 
				NumberPlanMapEnum.NATIONAL_NUMBERING_PLAN);
		
		
		assertEquals(userobj.getSubscriberInfo().getLocationInfo().getVlrNum().getExtention(), 
				ExtentionMapEnum.NO_EXTENTION);
		
		assertEquals(userobj.getSubscriberInfo().getLocationInfo().getVlrNum().getAddressDigits(),
				"1234567809");
		
		assertFalse(userobj.getSubscriberInfo().getSubscriberState().isAssumedIdle());
		assertTrue(userobj.getSubscriberInfo().getSubscriberState().isCamelBusy());
		assertFalse(userobj.getSubscriberInfo().getSubscriberState().isNotProvidedFromVLR());
	}
}
