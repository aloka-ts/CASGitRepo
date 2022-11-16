package com.agnity.cap.v2.datatypes.enumType;

public enum  EncodingSchemeCapV2Enum {


	 BCD_EVEN (0),BCD_ODD  (1),IA5(2),BINARY(3); 
	
	private int code;
	
	private EncodingSchemeCapV2Enum(int i){
		this.code = i;
	}
	
	public int getCode() {
		return code;
	}
	
	public static EncodingSchemeCapV2Enum getValue(int code){
		switch (code) {
		case 0: return BCD_EVEN; 
		case 1: return BCD_ODD; 
		case 2: return IA5; 
		case 3: return BINARY;
		
		default: return null;
		}	
	}
}
