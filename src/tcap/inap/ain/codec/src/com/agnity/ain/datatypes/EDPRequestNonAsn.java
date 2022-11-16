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

import org.apache.log4j.Logger;

/**
 * @author nishantsharma
 *
 *Used for encoding and decoding EDP Request
 */
public class EDPRequestNonAsn 
{
	public static final int O_CALLEDPARTY_BUSY=0;
	public static final int O_NOANSWER=1;
	private static final int O_TERM_SIZED=2;
	private static final int O_ANSWER=3;
	public static final int T_BUSY=4;
	public static final int T_NOANSWER=5;
	private static final int TERM_RESOURCEAVAILABLE=6;
	private static final int T_ANSWER=7;
	public static final int NETWORK_BUSY=8;
	public static final int O_SUSPENDED=9;
	public static final int O_DISCONNECT_CALLED=10;
	public static final int O_DISCONNECT=11;
	public static final int O_ABANDON=12;
	public static final int FEATURE_ACTIVATOR=13;
	public static final int SWITCH_HOOK_FLASH=14;
	public static final int SUCCSESS=15;
	public static final int T_DISCONNECT=16;
	public static final int TIMEOUT=17;
	public static final int ORIGNATION_ATTEMPT=18;
	public static final int O_DTMFENTERED=19;
	private static Logger logger = Logger.getLogger(EDPRequestNonAsn.class);
	BitSet edpBitSet=new BitSet(20);
	/**
	 * 
	 * This method encode the EDP Request related parameter
	 * 
	 * @return encoded byte array
	 */
	public  byte[] encodeEDPRequestNonAsn(){
		if(logger.isDebugEnabled()){
			logger.debug("edp Request enable bit length "+edpBitSet.length()+" String representation "+ edpBitSet.toString());
		}
		return toByteArr(edpBitSet);
	}
	/**
	 * this method used for enable bit on specific index
	 * 
	 * @param index
	 */
	public void enableEdp(int index){
		if(index!=2 && index!=3 && index!=6 && index!=7)
			edpBitSet.set(index);
	}
	/**
	 * this method used for disable bit on specific index
	 * 
	 * @param index
	 */
	public void disableEdp(int index){
		edpBitSet.clear(index);
	}
	
	/**
	 * this method used for seeing if bit is enable or not on specific index
	 * 
	 * @param index
	 * 
	 * @return Boolean
	 */
	public boolean isEnabled(int index){
		return edpBitSet.get(index);
	}
	/**
	 * 
	 * This method encode the EDP Notification related parameter
	 * 
	 * @return EDPRequestNonAsn object
	 */
	public EDPRequestNonAsn decodeEDPRequestNonAsn(byte[] edpByte){

		if(logger.isDebugEnabled()){
			logger.debug("edp Request enable bit length"+edpBitSet.length());
		}

		BitSet bit=fromByteArray(edpByte);
		for(int i=0;i<20;i++){
			if(bit.get(i)){
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
//	    byte[] bytes = new byte[bits.length()/8+1];
//	    for (int i=0; i<bits.length(); i++) {
//	        if (bits.get(i)) {
//	            bytes[bytes.length-i/8-1] |= 1<<(i%8);
//	        }
//	    }
//	    return bytes;
//	}
	

	public static byte[] toByteArr(BitSet bits) {
		byte[] bytes = new byte[3];
		for (int i=0, indx=0; i<bits.length(); i++) {
			if ( bits.get(i)) {       	
				bytes[i/8] |= 1<<(8-i%8-1);
			}
		}
		return bytes;
	}

}
