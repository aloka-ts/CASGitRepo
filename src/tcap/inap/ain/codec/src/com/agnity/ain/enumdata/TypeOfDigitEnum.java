package com.agnity.ain.enumdata;

public enum TypeOfDigitEnum {

	NOT_USED(0),
	CALLED(1),
	ANI_CALLING(2),
	CALLER_INTERATION(3),
	ROUTING_NUMBER(4),
	BILLING_NUMBER(5),
	DESTINATION_NUMBER(6),
	LATA(7),
	CARRIER_IDENTIFICATION(8);
	private int code;
	private TypeOfDigitEnum(int c){
		code = c;
	}
	public int getCode(){
		return code;
	}
	public static TypeOfDigitEnum fromInt(int num){
		switch (num){
		case 0: { return NOT_USED;}
		case 1: { return CALLED; }
 		case 2: { return ANI_CALLING; }
 		case 3: { return CALLER_INTERATION; }
 		case 4: { return ROUTING_NUMBER; }
 		case 5: { return BILLING_NUMBER; }
 		case 6: { return DESTINATION_NUMBER; }
		case 7: { return LATA; }
		case 8: { return CARRIER_IDENTIFICATION; }
		default: { return NOT_USED; }
		}
	}
	
}
