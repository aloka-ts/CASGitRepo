/*******************************************************************************
 *   Copyright (c) 2014 Agnity, Inc. All rights reserved.
 *   
 *   This is proprietary source code of Agnity, Inc. 
 *   
 *   Agnity, Inc. retains all intellectual property rights associated 
 *   with this source code. Use is subject to license terms.
 *   
 *   This source code contains trade secrets owned by Agnity, Inc.
 *   Confidentiality of this computer program must be maintained at 
 *   all times, unless explicitly authorized by Agnity, Inc.
 *******************************************************************************/
/**
 * 
 */
package com.agnity.ain.datatypes;

import java.util.BitSet;
import java.util.Map;

import org.apache.log4j.Logger;


/**
 * @author nishantsharma
 *
 */
@SuppressWarnings("unused")
public class EDPNotificationNonAsn 
{

	private static final int O_CALLEDPARTY_BUSY=0;
	private static final int O_NOANSWER=1;
	public static final int O_TERM_SIZED=2;
	public static final int O_ANSWER=3;
	private static final int T_BUSY=4;
	private static final int T_NOANSWER=5;
	public static final int TERM_RESOURCEAVAILABLE=6;
	public static final int T_ANSWER=7;
	private static final int NETWORK_BUSY=8;
	private static final int O_SUSPENDED=9;
	private static final int O_DISCONNECT_CALLED=10;
	private static final int O_DISCONNECT=11;
	private static final int O_ABANDON=12;
	private static final int FEATURE_ACTIVATOR=13;
	private static final int SWITCH_HOOK_FLASH=14;
	private static final int SUCCSESS=15;
	private static final int T_DISCONNECT=16;
	private static final int TIMEOUT=17;
	public static final int ORIGNATION_ATTEMPT=18;
	private static final int O_DTMFENTERED=19;
	private static Logger logger = Logger.getLogger(EDPNotificationNonAsn.class);

	private BitSet edpBitSet=new BitSet(20);
	/**
	 * This encode method return encoded bit set after encoding for EDP Notifictaion
	 * 
	 * @return a byte array
	 */
	public  byte[] encodeEDPNotificationNonAsn(){		
		if(logger.isDebugEnabled()){
			logger.debug("edp enable bit length"+edpBitSet.length());
		}
		return toByteArr(edpBitSet);

	}

	/**
	 *  This Method is for enabling the bit on specified index
	 *  
	 * @param index
	 */
	public void enableEdp(int index){
		if(index==2 ||index==3 || index==18)
			edpBitSet.set(index);
	}

	/**
	 *  This Method is for disabling the bit on specified index
	 *  
	 * @param index
	 */
	public void disableEdp(int index){
		edpBitSet.clear(index);
	}

	/**
	 *  This Method is for checking the bit is set or not 
	 *  on specified index
	 *  
	 * @param index
	 */
	public boolean isEnabled(int index){
		return edpBitSet.get(index);
	}

	/**
	 * This decode method is used for decoding and displaying enable bits in byte  
	 * 
	 * @return a EDPNotificationNonAsn decoded object
	 * 
	 * @param a byte array as edpByte
	 */
	public EDPNotificationNonAsn decodeEDPNotificationNonAsn(byte[] edpByte){

		if(logger.isDebugEnabled()){
			logger.debug("edp notification enable bit length"+edpBitSet.cardinality());
		}

		BitSet bit=fromByteArray(edpByte);
		for(int i=0;i<20;i++)
		{
			if(bit.get(i))
			{
				if(logger.isDebugEnabled()){
					switch(i){
					case 19:
						logger.debug("enable parameter at inedx"+","+i +"O_CALLEDPARTY_BUSY");
						break;
					case 18:
						logger.debug("enable parameter at inedx"+","+i +" "+"O_NOANSWER");
						break;
					case 17:
						logger.debug("enable parameter at inedx"+","+i +" "+"O_TERM_SIZED");
						break;
					case 16:
						logger.debug("enable parameter at inedx"+","+i +" "+"O_ANSWER");
						break;
					case 15:
						logger.debug("enable parameter at inedx"+","+i +" "+"T_BUSY");
						break;
					case 14:
						logger.debug("enable parameter at inedx"+","+i +"T_NOANSWER");
						break;
					case 13:
						logger.debug("enable parameter at inedx"+","+i +" "+"TERM_RESOURCEAVAILABLE");
						break;
					case 12:
						logger.debug("enable parameter at inedx"+","+i +" "+"T_ANSWER");
						break;
					case 11:
						logger.debug("enable parameter at inedx"+","+i +" "+"NETWORK_BUSY");
						break;
					case 10:
						logger.debug("enable parameter at inedx"+","+i +" "+"O_SUSPENDED");
						break;
					case 9:
						logger.debug("enable parameter at inedx"+","+i +" "+"O_DISCONNECT_CALLED");
						break;
					case 8:
						logger.debug("enable parameter at inedx"+","+i +" "+"O_DISCONNECT");
						break;
					case 7:
						logger.debug("enable parameter at inedx"+","+i +" "+"O_ABANDON");
						break;
					case 6:
						logger.debug("enable parameter at inedx"+","+i +"FEATURE_ACTIVATOR");
						break;
					case 5:
						logger.debug("enable parameter at inedx"+","+i +" "+"SWITCH_HOOK_FLASH");
						break;
					case 4:
						logger.debug("enable parameter at inedx"+","+i +" "+"SUCCSESS");
						break;
					case 3:
						logger.debug("enable parameter at inedx"+","+i +" "+"T_DISCONNECT");
						break;
					case 2:
						logger.debug("enable parameter at inedx"+","+i +" "+"TIMEOUT");
						break;
					case 1:
						logger.debug("enable parameter at inedx"+","+i +" "+"ORIGNATION_ATTEMPT");
						break;
					case 0:
						logger.debug("enable parameter at inedx"+","+i +" "+"O_DTMFENTERED");
						break;
					}
				}
			}
		}
		return this;

	}
	/**
	 * This Method is used for converting byte array to BitSet
	 * 
	 * @param bytes as a byte array
	 * 
	 * @return BitSet object
	 */
	public static BitSet fromByteArray(byte[] bytes) {
		BitSet bits = new BitSet();
		for (int i = 0; i < bytes.length * 8; i++) {
			if ((bytes[bytes.length - i / 8 - 1] & (1 << (i % 8))) > 0) {
				bits.set(i);
			}
		}
		return bits;
	}
	/**
	 * This Method is used for converting BitSet to byte array
	 * 
	 * @return bytes as a byte array
	 * 
	 * @param BitSet object
	 */
//	public static byte[] toByteArr(BitSet bits) {
//		byte[] bytes = new byte[bits.length()/8+1];
//		for (int i=0; i<bits.length(); i++) {
//			if (bits.get(i)) {
//				bytes[bytes.length-i/8-1] |= 1<<(i%8);
//			}
//		}
//		return bytes;
//	}

	public static byte[] toByteArr(BitSet bits) {
		byte[] bytes = new byte[3];

		for (int i=0; i<bits.length(); i++) {

			if ( bits.get(i)) {       	
				bytes[i/8] |= 1<<(8-i%8-1);
			}
		}
		return bytes;
	}

}
