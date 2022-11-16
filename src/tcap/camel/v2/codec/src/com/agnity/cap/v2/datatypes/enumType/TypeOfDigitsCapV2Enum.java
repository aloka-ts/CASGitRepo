package com.agnity.cap.v2.datatypes.enumType;

public enum TypeOfDigitsCapV2Enum {


	 ACCOUNT_CODE          (0),
     AUTHORISATION_CODE    (1),
     TRAVELLING_CLASS_MARK (2),
     GROUP_IDENTITY        (3);

	
	private int code;
	
	private TypeOfDigitsCapV2Enum(int i){
		this.code = i;
	}
	
	public int getCode() {
		return code;
	}
	
	public static TypeOfDigitsCapV2Enum getValue(int code){
		switch (code) {
		case 0: return ACCOUNT_CODE; 
		case 1: return AUTHORISATION_CODE; 
		case 2: return TRAVELLING_CLASS_MARK; 
		case 3: return GROUP_IDENTITY;
		
		default: return null;
		}	
	}
}
