package com.agnity.win.test;

import java.util.LinkedList;

import junit.framework.TestCase;

import com.agnity.win.datatypes.NonASNDmhServiceId;
import com.agnity.win.util.Util;

public class TestNonAsnDmhServiceId extends TestCase {

	/*
	 * Encoding Single 
	 */
	public void testEncodeSingleDmhServiceId() throws Exception {
		byte[] b = null;
		
		LinkedList<Short> mktIdList    = new LinkedList<Short>();
		LinkedList<Byte>  mktSegIdList = new LinkedList<Byte>();
		LinkedList<Short> svcIdList    = new LinkedList<Short>();

		mktIdList.add((short)60);
		mktSegIdList.add((byte)0x12);
		svcIdList.add((short)100);
		
		b = NonASNDmhServiceId.encodeDmhServiceId(mktIdList, mktSegIdList, svcIdList);

		assertEquals(b.length, 5);
		assertEquals(b[0], (byte)0x00);
		assertEquals(b[1], (byte)0x3C);
		assertEquals(b[2], (byte)0x12);
		assertEquals(b[3], (byte)0x00);
		assertEquals(b[4], (byte)0x64);
		System.out.println("TestNonAsnDmhServiceId1: "+Util.formatBytes(b));
		
		// value spanning 2 octets
		mktIdList.clear();
		mktSegIdList.clear();
		svcIdList.clear();
		
		mktIdList.add((short)4661);
		mktSegIdList.add((byte)0x12);
		svcIdList.add((short)65530);
		
		b = NonASNDmhServiceId.encodeDmhServiceId(mktIdList, mktSegIdList, svcIdList);
		assertEquals(b.length, 5);
		assertEquals(b[0], (byte)0x12);
		assertEquals(b[1], (byte)0x35);
		assertEquals(b[2], (byte)0x12);
		assertEquals(b[3], (byte)0xFF);
		assertEquals(b[4], (byte)0xFA);
		System.out.println("TestNonAsnDmhServiceId2: "+Util.formatBytes(b));
		
		// invalid values
		mktIdList.clear();
		mktSegIdList.clear();
		svcIdList.clear();
		
		mktIdList.add((short)4661);
		mktIdList.add((short)2661);
		mktSegIdList.add((byte)0x12);
		svcIdList.add((short)65530);
		
		try {
			b = NonASNDmhServiceId.encodeDmhServiceId(mktIdList, mktSegIdList, svcIdList);
		}
		catch(Exception e){
			assertFalse(false);
		}
		
		// out of range value
		mktIdList.clear();
		mktSegIdList.clear();
		svcIdList.clear();
		
		mktIdList.add((short)65536);
		mktSegIdList.add((byte)0xff);
		svcIdList.add((short)65530);
		
		try {
			b = NonASNDmhServiceId.encodeDmhServiceId(mktIdList, mktSegIdList, svcIdList);
			System.out.println("TestNonAsnDmhServiceId3: "+Util.formatBytes(b));
		}
		catch(Exception e){
			assertFalse(false);
		}
	}
	
	public void testEncodeMultipleDmhServiceId() throws Exception {
		byte[] b = null;
		
		LinkedList<Short> mktIdList    = new LinkedList<Short>();
		LinkedList<Byte>  mktSegIdList = new LinkedList<Byte>();
		LinkedList<Short> svcIdList    = new LinkedList<Short>();

		mktIdList.add((short)60);
		mktSegIdList.add((byte)0x12);
		svcIdList.add((short)100);
		
		b = NonASNDmhServiceId.encodeDmhServiceId(mktIdList, mktSegIdList, svcIdList);

		assertEquals(b.length, 5);
		assertEquals(b[0], (byte)0x00);
		assertEquals(b[1], (byte)0x3C);
		assertEquals(b[2], (byte)0x12);
		assertEquals(b[3], (byte)0x00);
		assertEquals(b[4], (byte)0x64);
		System.out.println("TestNonAsnDmhServiceId1: "+Util.formatBytes(b));
	}
}
