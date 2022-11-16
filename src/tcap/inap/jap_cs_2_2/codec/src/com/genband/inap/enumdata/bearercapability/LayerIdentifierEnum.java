package com.genband.inap.enumdata.bearercapability;
/**
 * This enum represents Layer identification.
 * @author vgoel
 *
 */
public enum LayerIdentifierEnum {

	/**
	 *  1-Layer 1
	 *  2-Layer 2 
	 *  3-Layer 3 
	 */
	LAYER_1(1), LAYER_2(2), LAYER_3(3);
	 
	private int code;

	private LayerIdentifierEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static LayerIdentifierEnum fromInt(int num) {
		switch (num) {
		case 1: { return 	LAYER_1	; }
		case 2: { return 	LAYER_2	; }
		case 3: { return 	LAYER_3	; }
		default: { return null; }
		}
	}
}
