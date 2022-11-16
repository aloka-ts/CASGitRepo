/****

  Copyright (c) 2013 Agnity, Inc. All rights reserved.

  This is proprietary source code of Agnity, Inc. 
  Agnity, Inc. retains all intellectual property rights associated 
  with this source code. Use is subject to license terms.

  This source code contains trade secrets owned by Agnity, Inc.
  Confidentiality of this computer program must be maintained at 
  all times, unless explicitly authorized by Agnity, Inc.

 ****/
package com.baypackets.ase.sipconnector;

import java.io.Serializable;
import java.util.HashMap;

import com.baypackets.ase.util.AseStrings;

/**
 * This class will be used to store sip signaling related information for a sip message.
 * Information will be stored 
 * 		<ul>
 * 		<li>12th Bit:- Direction : 1- INcoming, 0-Outgoing</li>  
 * 		<li>11th Bit:- SDP Present: 1- SDP , 0- No SDP</li>
 * 		<li>1-10 Bit:- Message type in binary format</li>	
 *  	</ul>
 * @author Amit Baxi
 *
 */
public class AseSipMessageInfo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 196036993987284362L;
	private static final short UNKNOWN_MESSAGE_CODE=99;
	private static final short MASK_INCOMING_DIR=2048;
	private static final short MASK_SDP=1024;
	private static final short MASK_MESSAGE_VALUE=1023;
	private static final HashMap<Short, String> CODE_TO_MESSAGE_MAP=new HashMap<Short,String>();
	private static final HashMap<String,Short> MESSAGE_TO_CODE_MAP=new HashMap<String,Short>();
	
	private short value=0;
	
	// Static map for method to code lookup
	static{
		MESSAGE_TO_CODE_MAP.put(AseStrings.INVITE, (short) 1);
		MESSAGE_TO_CODE_MAP.put(AseStrings.CANCEL, (short) 2);
		MESSAGE_TO_CODE_MAP.put(AseStrings.ACK, (short) 3);
		MESSAGE_TO_CODE_MAP.put(AseStrings.UPDATE, (short) 4);
		MESSAGE_TO_CODE_MAP.put(AseStrings.OPTIONS, (short) 5);
		MESSAGE_TO_CODE_MAP.put(AseStrings.PRACK, (short) 6);
		MESSAGE_TO_CODE_MAP.put(AseStrings.INFO, (short) 7);
		MESSAGE_TO_CODE_MAP.put(AseStrings.REFER, (short) 8);
		MESSAGE_TO_CODE_MAP.put(AseStrings.BYE, (short) 9);
		MESSAGE_TO_CODE_MAP.put(AseStrings.REGISTER, (short) 10);
		MESSAGE_TO_CODE_MAP.put(AseStrings.PUBLISH, (short) 11);
		MESSAGE_TO_CODE_MAP.put(AseStrings.SUBSCRIBE, (short) 12);
		MESSAGE_TO_CODE_MAP.put(AseStrings.NOTIFY, (short) 13);
		MESSAGE_TO_CODE_MAP.put(AseStrings.MESSAGE, (short) 14);
	}
	
	// Static map for code to method lookup
	static{
		CODE_TO_MESSAGE_MAP.put((short)1,AseStrings.INVITE.substring(0, 3));
		CODE_TO_MESSAGE_MAP.put((short)2,AseStrings.CANCEL.substring(0, 3));
		CODE_TO_MESSAGE_MAP.put((short)3,AseStrings.ACK.substring(0, 3));
		CODE_TO_MESSAGE_MAP.put((short)4,AseStrings.UPDATE.substring(0, 3));
		CODE_TO_MESSAGE_MAP.put((short)5,AseStrings.OPTIONS.substring(0, 3));
		CODE_TO_MESSAGE_MAP.put((short)6,AseStrings.PRACK.substring(0, 3));
		CODE_TO_MESSAGE_MAP.put((short)7,AseStrings.INFO.substring(0, 3));
		CODE_TO_MESSAGE_MAP.put((short)8,AseStrings.REFER.substring(0, 3));
		CODE_TO_MESSAGE_MAP.put((short)9,AseStrings.BYE.substring(0, 3));
		CODE_TO_MESSAGE_MAP.put((short)10,AseStrings.REGISTER.substring(0, 3));
		CODE_TO_MESSAGE_MAP.put((short)11,AseStrings.PUBLISH.substring(0, 3));
		CODE_TO_MESSAGE_MAP.put((short)12,AseStrings.SUBSCRIBE.substring(0, 3));
		CODE_TO_MESSAGE_MAP.put((short)13,AseStrings.NOTIFY.substring(0, 3));
		CODE_TO_MESSAGE_MAP.put((short)14,AseStrings.MESSAGE.substring(0, 3));
	}
	

	/**
	 * Constructs a object of AseSipMessageInfo for SipRequest
	 * @param requestMethod
	 * @param direction
	 * @param isSDP
	 */
	public AseSipMessageInfo(String requestMethod,String direction,boolean isSDP) {
		setMessageValue(requestMethod);
		if(direction.equals("incoming")){
			setIncomingBit();
		}
		if(isSDP){
			setSDPBit();
		}
	}
	
	/**
	 * Constructs a object of AseSipMessageInfo for SipResponse
	 * @param responseCode
	 * @param direction
	 * @param isSDP
	 */
	public AseSipMessageInfo(int responseCode,String direction,boolean isSDP) {
		setMessageValue(responseCode);
		if(direction.equals("incoming")){
			setIncomingBit();
		}
		if(isSDP){
			setSDPBit();
		}
	}
	
	
		//11th bit for SDP
		private void setSDPBit(){
			value=(short) (value | MASK_SDP);
		}

		// 12th bit for direction 
		private void setIncomingBit(){
			value=(short) (value | MASK_INCOMING_DIR);
		}

		// Message values stored in 1 to 10 bits
		
		private short getMessageValue() {
			return (short) (value & MASK_MESSAGE_VALUE);
		}
			
		
		private void setMessageValue(int message){
			value=(short) (value|message);
		}
		
		private void setMessageValue(String message){
			if(MESSAGE_TO_CODE_MAP.containsKey(message)){
				value=(short) (value|MESSAGE_TO_CODE_MAP.get(message));
			}else{
				if(message.matches("[1-6][0-9][0-9]")){
					value=(short) (value|Short.parseShort(message));
				}else{
					value=(short) (value|UNKNOWN_MESSAGE_CODE);
				}
			}
		} 
		
		
		@Override
		public String toString() {
			StringBuilder br=new StringBuilder();

			// Incoming or outgoing Flag
			br= ((value & MASK_INCOMING_DIR) > 1)  ?br.append("I-"):br.append("O-");
			
			// SDP Present in Message or Not S-Present, X-Not present
			br=((value & MASK_SDP)>1)?br.append("S-"):br.append("X-");
					
			// Message code conversion
			short messageCode=this.getMessageValue();
			if(messageCode < 100 && CODE_TO_MESSAGE_MAP.containsKey(messageCode)){
				br.append(CODE_TO_MESSAGE_MAP.get(messageCode));  
			}else{
				br.append(messageCode); 
			}
			return br.toString();
		}
}

