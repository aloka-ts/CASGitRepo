package com.agnity.map.enumdata;

public enum IstSupportIndicatorMapEnum {
	BASIC_IST_SUPPORTED(0),
	IST_COMMAND_SUPPORTED(1);
	
	private int code;
	
	private IstSupportIndicatorMapEnum(int code){
		this.code = code;
	}
	
	public int getCode() {
		return this.code;
	}
	
	public static IstSupportIndicatorMapEnum getValue(int tag){
		switch(tag) {
			case 0: return BASIC_IST_SUPPORTED;
			case 1: return IST_COMMAND_SUPPORTED;
			default: return null;
		}
	}
}
