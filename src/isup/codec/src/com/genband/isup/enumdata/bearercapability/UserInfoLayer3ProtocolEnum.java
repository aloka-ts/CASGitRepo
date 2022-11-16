package com.genband.isup.enumdata.bearercapability;
/**
 * This enum represents User Information Layer 3 Protocol.
 * @author vgoel
 */
public enum UserInfoLayer3ProtocolEnum {
	
	/**
	 * 2- Recommendation Q.931
	 * 6- Recommendation X.25, packet layer
	 * 11- ISO?IEC TR 9577
	 */
	
	RECOMMEND_Q_931(2), RECOMMEND_X_25_PACKET_LAYER(6), ISO_IEC_TR_9577(12);
	
	private int code;

	private UserInfoLayer3ProtocolEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static UserInfoLayer3ProtocolEnum fromInt(int num) {
		switch (num) {
		case 2: { return 	RECOMMEND_Q_931	; }
		case 6: { return 	RECOMMEND_X_25_PACKET_LAYER	; }
		case 11: { return 	ISO_IEC_TR_9577	; }
		default: { return null; }
		}
	}
}
