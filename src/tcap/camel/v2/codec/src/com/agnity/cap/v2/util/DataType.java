/****
 * Copyright (c) 2013 Agnity, Inc. All rights reserved.
 * 
 * This is proprietary source code of Agnity, Inc.
 * 
 * Agnity, Inc. retains all intellectual property rights associated with this
 * source code. Use is subject to license terms.
 * 
 * This source code contains trade secrets owned by Agnity, Inc. Confidentiality
 * of this computer program must be maintained at all times, unless explicitly
 * authorized by Agnity, Inc.
 ****/
package com.agnity.cap.v2.util;

public class DataType {

	private static DataType dataType;
	
	public static final String ODD = "odd";
	public static final String EVEN = "even";
	
	public static final String SPARE = "spare";
	public static final String UNKNOWN = "unknown";
	public static final String SUBCRIBER_NUMBER = "subcriber";
	public static final String NATIONAL = "national";
	public static final String INTERNATIONAL = "international";
	
	public static final String COMPLETE = "complete";
	public static final String INCOMPLETE = "incomplete";
	
	public static final String PRESENTATION_ALLOWED="presentation allowed" ;
	public static final String PRESENTATION_RESTRICTED="presentation restricted";
	public static final String ADDRESS_NOT_AVAILABLE="address not available"; 
	public static final String RESERVED = "reserved for restriction by the network"; 
	
	public static final String SCREENING_RESERVED="reserved"; 
	public static final String USER_PROVIDED= "user provided, verified and passed"; 
	public static final String NETWORK_PROVIDED="network provided"; 
	
	
	public static final String INN_ALLOWED="routing to internal network number allowed"; 
	public static final String INN_NOT_ALLOWED="routing to internal network number not allowed";
	
	public static final String NUMBERING_PLAN_ISDN="ISDN (Telephony) numbering plan"; 
	public static final String NUMBERING_PLAN_DATA="Data numbering plan"; //(national use) 
	public static final String NUMBERING_PLAN_TELEX="Telex numbering plan";	//(national use) 
	public static final String NUMBERING_PLAN_RESERVED="reserved"; 	//(national use) 
	
	public static final String EXT_NEXT_OCTET = "information continues in the next octet";
	public static final String EXT_LAST_OCTET = "last octet";
	
	public static final String COADING_ITU_T = "ITU-T standardized coading"; 
	public static final String SPEECH = "speech"; 
	
	
	private DataType(){
		
	}
	
	public String getNatureOfAddress(byte tag){
		switch (tag) {
		case 0: return DataType.SPARE; 
		case 1: return DataType.SUBCRIBER_NUMBER; 
		case 2: return DataType.UNKNOWN; 
		case 3: return DataType.NATIONAL;
		case 4: return DataType.INTERNATIONAL; 
		default: return DataType.UNKNOWN;
		}	
	}
	
	public String getOddEvenIndicator(byte tag){
		switch (tag) {
		case 0: return DataType.EVEN; 
		case 1: return DataType.ODD; 
		default: return DataType.UNKNOWN;
		}
	}
	
	public String getNIIndicator(byte tag){
		switch (tag) {
		case 0: return DataType.COMPLETE; 
		case 1: return DataType.INCOMPLETE; 
		default: return DataType.UNKNOWN;
		}
	}
	
	public String getNumberingOfPlanIndicator(byte tag){
		switch (tag) {
		case 0: return DataType.SPARE; 
		case 1: return DataType.NUMBERING_PLAN_ISDN; 
		case 2: return DataType.SPARE; 
		case 3: return DataType.NUMBERING_PLAN_DATA;
		case 4: return DataType.NUMBERING_PLAN_TELEX; 
		case 5: return DataType.NUMBERING_PLAN_RESERVED; 
		case 6: return DataType.NUMBERING_PLAN_RESERVED;
		case 7: return DataType.SPARE;
		default: return DataType.UNKNOWN;
		}
	}
	
	public String getAddressPresentationRestricatedIndicator(byte tag){
		switch (tag) {
		case 0: return DataType.PRESENTATION_ALLOWED; 
		case 1: return DataType.PRESENTATION_RESTRICTED; 
		case 2: return DataType.ADDRESS_NOT_AVAILABLE; 
		case 3: return DataType.RESERVED; 
		default: return DataType.UNKNOWN;
		}
	}
	
	public String getScreeningIndicator(byte tag){
		switch (tag) {
		case 0: return DataType.SCREENING_RESERVED; 
		case 1: return DataType.USER_PROVIDED; 
		case 2: return DataType.SCREENING_RESERVED; 
		case 3: return DataType.NETWORK_PROVIDED; 
		default: return DataType.UNKNOWN;
		}
	}
	
	public String getNumber(byte[] bytes){
		StringBuilder callingPartyNumber = new StringBuilder();
		for(int i=2;i<bytes.length;i++){
		 	callingPartyNumber.append((bytes[i]&0x0f));
		 	callingPartyNumber.append(((bytes[i]>>4)&0x0f));
		}
		return callingPartyNumber.toString();
	}
	
	public String getINNIndicator(byte tag){
		switch (tag) {
		case 0: return DataType.INN_ALLOWED; 
		case 1: return DataType.INN_NOT_ALLOWED; 
		default: return DataType.UNKNOWN;
		}
	}
	
	public String getExtentionIndicator(byte tag){
		switch (tag) {
		case 0: return DataType.EXT_NEXT_OCTET; 
		case 1: return DataType.EXT_LAST_OCTET; 
		default: return DataType.UNKNOWN;
		}
	}
	
	//not complete
	public String getCoadingStandard(byte tag){
		switch (tag) {
		case 0: return DataType.COADING_ITU_T; 
		case 1: return DataType.UNKNOWN; 
		case 2: return DataType.UNKNOWN; 
		case 3: return DataType.UNKNOWN; 
		default: return DataType.UNKNOWN;
		}
	}
	
	//not complete
	public String getInformationTransferCapability(byte tag){
		switch (tag) {
		case 0: return DataType.SPEECH; 
		case 1: return DataType.UNKNOWN; 
		case 2: return DataType.UNKNOWN; 
		case 3: return DataType.UNKNOWN; 
		default: return DataType.UNKNOWN;
		}
	}
	
	
	public static DataType getObject(){
		if(dataType==null){
			dataType = new DataType(); 
		}
		return dataType;
	}
	
//	public static void main(String[] args) {
//		byte[] bytes = new byte[]{(byte)0x04,(byte)0x10,(byte)0x19,(byte)0x99,(byte)0x99,(byte)0x11,(byte)0x41,(byte)0x44};
//		System.out.println(getObject().getCallingPartyNumber(bytes));
//	}
	
}
