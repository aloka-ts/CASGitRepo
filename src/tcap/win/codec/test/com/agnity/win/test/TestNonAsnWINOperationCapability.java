package com.agnity.win.test;

import java.util.LinkedList;

import junit.framework.TestCase;

import com.agnity.win.datatypes.NonASNWINOperationCapability;
import com.agnity.win.enumdata.CCDIREnum;
import com.agnity.win.enumdata.CircuitSwitchedDataEnum;
import com.agnity.win.enumdata.ConnectResourceEnum;
import com.agnity.win.enumdata.PositionRequestEnum;
import com.agnity.win.exceptions.InvalidInputException;
import com.agnity.win.util.Util;

public class TestNonAsnWINOperationCapability extends TestCase {
	
	LinkedList  <CircuitSwitchedDataEnum> csd = new  LinkedList <CircuitSwitchedDataEnum> ();
	LinkedList  <CCDIREnum> ccdir = new LinkedList <CCDIREnum>  ();
	LinkedList  <ConnectResourceEnum> cr = new LinkedList <ConnectResourceEnum>();
	LinkedList  <PositionRequestEnum> pr = new LinkedList <PositionRequestEnum>();
	byte[] bytesToDecode = null;
	int codes[][];
		
	
public void setUp() throws Exception {
		
		// All possible Input values
	csd.add(CircuitSwitchedDataEnum.SENDER_CANNOT_SUPPORT_WIN_CS_DATASERVICES);
	csd.add(CircuitSwitchedDataEnum.SENDER_CAN_SUPPORT_WIN_CS_DATASERVICES);
	ccdir.add(CCDIREnum.SENDER_CANNOT_SUPPORT_CCD_OPERATIONS);
	ccdir.add(CCDIREnum.SENDER_CAN_SUPPORT_CCD_OPERATIONS);
	cr.add(ConnectResourceEnum.SENDER_CANT_SUPPORT_OPERATIONS);
	cr.add(ConnectResourceEnum.SENDER_CAN_SUPPORT_OPERATIONS);
	pr.add(PositionRequestEnum.SENDER_CANNOT_SUPPORT_POS_REQUEST_OPERATIONS);
	pr.add(PositionRequestEnum.SENDER_CAN_SUPPORT_POS_REQUEST_OPERATIONS);
	
	    byte[] a = {(byte)0x00,(byte)0x0f};
	    int[][] c = {{0,0,0,0},{1,1,1,1}};
	    bytesToDecode = a;
	    codes = c;
}	
	/*
	 * Test encoding all possible values of NonASNWINOperationCapability
	 */
	public void testEncodeWINOperationCapability() throws Exception{
		byte[] b = null;
		try {
			b = NonASNWINOperationCapability.encodeWINOperationCapability(csd,ccdir,pr,cr);
		}
		catch(InvalidInputException ex){
			assertFalse(true);
			ex.printStackTrace();
		}
		System.out.println("Encoded NonASNWINOperationCapability: "+ Util.formatBytes(b));
		for(int i = 0; i<bytesToDecode.length; i++)
		{		
			assertEquals("NonASNWINOperationCapability is maching",bytesToDecode[i],b[i]);
		}
	}

	/*
	 * Testing decoding of NonASNWINOperationCapability
	 */
	public void testDecodeWINOperationCapability() throws Exception{
		try {
			NonASNWINOperationCapability winOpCap = NonASNWINOperationCapability.decodeWINOperationCapability(bytesToDecode);
			
			System.out.println("Decoded NonASNWINOperationCapability: "+ winOpCap.toString());
			for(int i = 0; i<codes.length; i++)
			{		
			assertEquals("CircuitSwitchedData is maching",winOpCap.getCircuitSwitchedData().get(i).getCode(),codes[i][0]);
			assertEquals("ccdir is maching",winOpCap.getCCDIR().get(i).getCode(),codes[i][1]);
			assertEquals("PositionRequest is maching",winOpCap.getPositionRequest().get(i).getCode(),codes[i][2]);
			assertEquals("ConnectResource is maching",winOpCap.getConnectResource().get(i).getCode(),codes[i][3]);
			}
		}
		catch(InvalidInputException ex){
			assertFalse(true);
			ex.printStackTrace();
		}
	}
}
