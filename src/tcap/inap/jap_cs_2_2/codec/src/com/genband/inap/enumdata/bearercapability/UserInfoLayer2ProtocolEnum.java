package com.genband.inap.enumdata.bearercapability;
/**
 * This enum represents User Information Layer 2 Protocol.
 * @author vgoel
 */
public enum UserInfoLayer2ProtocolEnum {
	
	/**
	 * 2- Recommendation Q.921
	 * 6- Recommendation X.25
	 * 12- LAN logical link control
	 */
	
	RECOMMEND_Q_921(2), RECOMMEND_X_25(6), LAN_LOGICAL_LINK_CONTROL(12);
	
	private int code;

	private UserInfoLayer2ProtocolEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static UserInfoLayer2ProtocolEnum fromInt(int num) {
		switch (num) {
		case 2: { return 	RECOMMEND_Q_921	; }
		case 6: { return 	RECOMMEND_X_25	; }
		case 12: { return 	LAN_LOGICAL_LINK_CONTROL	; }
		default: { return null; }
		}
	}
}
