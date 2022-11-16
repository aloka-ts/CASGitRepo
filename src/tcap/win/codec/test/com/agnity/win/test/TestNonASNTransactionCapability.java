package com.agnity.win.test;

import java.util.LinkedList;

import junit.framework.TestCase;

import com.agnity.win.datatypes.NonASNTransactionCapability;
import com.agnity.win.enumdata.AnnouncementsEnum;
import com.agnity.win.enumdata.BusyDetectionEnum;
import com.agnity.win.enumdata.SubscriberPINInterceptEnum;
import com.agnity.win.enumdata.BusyDetectionEnum;
import com.agnity.win.enumdata.RemoteUserInteractionEnum;
import com.agnity.win.enumdata.MultipleTerminationsEnum;
import com.agnity.win.enumdata.TerminationListEnum;
import com.agnity.win.enumdata.ProfileEnum;
import com.agnity.win.enumdata.RemoteUserInteractionEnum;
import com.agnity.win.enumdata.SubscriberPINInterceptEnum;
import com.agnity.win.enumdata.TerminationListEnum;
import com.agnity.win.exceptions.InvalidInputException;
import com.agnity.win.util.Util;

public class TestNonASNTransactionCapability extends TestCase {
	

	LinkedList  <BusyDetectionEnum> bd = new  LinkedList <BusyDetectionEnum> ();
	LinkedList  <SubscriberPINInterceptEnum> spi = new LinkedList <SubscriberPINInterceptEnum>  ();
	LinkedList  <RemoteUserInteractionEnum> rui = new LinkedList <RemoteUserInteractionEnum>();
	LinkedList  <TerminationListEnum> tl = new LinkedList <TerminationListEnum>();
	LinkedList  <AnnouncementsEnum> an = new LinkedList <AnnouncementsEnum>();
	LinkedList  <ProfileEnum> pr = new LinkedList <ProfileEnum>();
	LinkedList  <MultipleTerminationsEnum> mt = new LinkedList <MultipleTerminationsEnum>();
	
	byte[][] bytesToDecode = null;
	int codes[][];
		
	
public void setUp() throws Exception {
		
		// possible Input values
	bd.add(BusyDetectionEnum.SYSTEM_CAN_DETECT_BUSY_CONDITION);
	bd.add(BusyDetectionEnum.SYSTEM_CANNOT_DETECT_BUSY_CONDITION);
	spi.add(SubscriberPINInterceptEnum.SYSTEM_CAN_SUPPORT_LOCAL_SPINI_OP);
	spi.add(SubscriberPINInterceptEnum.SYSTEM_CANNOT_SUPPORT_LOCAL_SPINI_OP);
	rui.add(RemoteUserInteractionEnum.SYSTEM_CAN_INTERACT_WITH_USER);
	rui.add(RemoteUserInteractionEnum.SYSTEM_CANNOT_INTERACT_WITH_USER);
	an.add(AnnouncementsEnum.SYSTEM_CAN_SUPPORT_ANNOUNCEMENTLIST_PARAM);
	an.add(AnnouncementsEnum.SYSTEM_CANNOT_SUPPORT_ANNOUNCEMENTLIST_PARAM);
	tl.add(TerminationListEnum.SYSTEM_CAN_SUPPORT_TERMINATIONLIST_PARAM);
	tl.add(TerminationListEnum.SYSTEM_CANNOT_SUPPORT_TERMINATIONLIST_PARAM);
	pr.add(ProfileEnum.SYSTEM_CAN_SUPPORT_IS41C_PROFILE_PARAMS);
	pr.add(ProfileEnum.SYSTEM_CANNOT_SUPPORT_IS41C_PROFILE_PARAMS);
	mt.add(MultipleTerminationsEnum.SYSTEM_CANNOT_ACCEPT_TERMINATION);
	mt.add(MultipleTerminationsEnum.SYSTEM_SUPPORTS_2_CALLLEG);
	
	    byte[][] a = {{(byte)0x1f,(byte)0x10},{(byte)0x00,(byte)0x02}};
	    int[][] c = {{1,1,1,1,1,1,0},{0,0,0,0,0,0,2}};
	    bytesToDecode = a;
	    codes = c;
}	
	/*
	 * Test encoding all possible values of NonASNTransactionCapability
	 */
	public void testEncodeTransactionCapability() throws Exception{
		byte[] b = null;
		for(int i=0;i<bytesToDecode.length;i++)
		{
		try {
			b = NonASNTransactionCapability.encodeTransactionCapability(pr.get(i),bd.get(i),an.get(i),rui.get(i),
			spi.get(i),mt.get(i),tl.get(i));
		}
		catch(InvalidInputException ex){
			System.out.println("Exception: "+ ex.getMessage());
			assertFalse(true);
			ex.printStackTrace();
		}
		System.out.println("Encoded NonASNTransactionCapability: "+ Util.formatBytes(b));
		
			assertEquals("NonASNTransactionCapability is maching",bytesToDecode[i][0],b[0]);
			assertEquals("NonASNTransactionCapability is maching",bytesToDecode[i][1],b[1]);
		}
	}

	/*
	 * Testing decoding of NonASNTransactionCapability
	 */
	public void testDecodeTransactionCapability() throws Exception{
		try {
			for(int i = 0; i<codes.length; i++)
			{	
			NonASNTransactionCapability winOpCap = NonASNTransactionCapability.decodeTransactionCapability(bytesToDecode[i]);
			System.out.println("Decoded NonASNTransactionCapability: "+ winOpCap.toString());
			assertEquals("BusyDetectionEnum is maching",winOpCap.getBusyDetection().getCode(),codes[i][0]);
			assertEquals("SubscriberPINInterceptEnum is maching",winOpCap.getSubscriberPINIntercept().getCode(),codes[i][1]);
			assertEquals("RemoteUserInteractionEnum is maching",winOpCap.getRemoteUserInteraction().getCode(),codes[i][2]);
			assertEquals("AnnouncementsEnum is maching",winOpCap.getAnnouncements().getCode(),codes[i][3]);
			}
		}
		catch(InvalidInputException ex){
			assertFalse(true);
			ex.printStackTrace();
		}
	}
}
