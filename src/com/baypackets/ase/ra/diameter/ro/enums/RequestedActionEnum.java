package com.baypackets.ase.ra.diameter.ro.enums;


public enum RequestedActionEnum {
	DIRECT_DEBITING, REFUND_ACCOUNT,CHECK_BALANCE,PRICE_ENQUIRY;

	public static java.lang.String getName(int key) {
		return getName(key);
	}

	public static int getCode(RequestedActionEnum action) {
		int code=-1;
		switch(action){
		
		case DIRECT_DEBITING: 
			code= 0;
			break;
		case REFUND_ACCOUNT: 
			code= 1;
			break;
		case CHECK_BALANCE:
			code= 2;
			break;
		case PRICE_ENQUIRY:
			code=3;
			break;
		}
		return code;
	}
	
	
	public static RequestedActionEnum fromCode(int code) {
		RequestedActionEnum enumA=null;
		switch(code){
		
		case 0: 
			enumA= DIRECT_DEBITING;
			break;
		case 1: 
			enumA= REFUND_ACCOUNT;
			break;
		case 2:
			enumA= CHECK_BALANCE;
			break;
		case 3:
			enumA=PRICE_ENQUIRY;
			break;
		}
		return enumA;
	}
}
