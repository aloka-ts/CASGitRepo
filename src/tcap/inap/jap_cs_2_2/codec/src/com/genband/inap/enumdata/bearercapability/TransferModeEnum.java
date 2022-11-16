package com.genband.inap.enumdata.bearercapability;
/**
 * This enum represents Transfer Mode.
 * @author vgoel
 */
public enum TransferModeEnum {
	
	/**
	 * 0-Circuit mode
	 * 2-Packet mode
	 */
	CIRCUIT_MODE(0), PACKET_MODE(2);
	
	private int code;

	private TransferModeEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static TransferModeEnum fromInt(int num) {
		switch (num) {
		case 0: { return 	CIRCUIT_MODE	; }
		case 2: { return 	PACKET_MODE	; }
		default: { return null; }
		}
	}
}
