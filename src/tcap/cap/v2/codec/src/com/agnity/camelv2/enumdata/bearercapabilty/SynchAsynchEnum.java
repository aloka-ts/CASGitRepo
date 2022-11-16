package com.agnity.camelv2.enumdata.bearercapabilty;
/**
 * This enum represents Synchronous/Asynchronous.
 * @author vgoel
 */
public enum SynchAsynchEnum {
	
	/**
	 * 0-Synchronous data
	 * 1-Asynchronous data
	 */
	SYNCH_DATA(0), ASYNCH_DATA(1);
	
	private int code;

	private SynchAsynchEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static SynchAsynchEnum fromInt(int num) {
		switch (num) {
		case 0: { return 	SYNCH_DATA	; }
		case 1: { return 	ASYNCH_DATA	; }
		default: { return null; }
		}
	}
}
