package com.agnity.inapitutcs2.enumdata;

/**
 * Enum for Transmission medium requirement
 * @author Mriganka
 *
 */


public enum TransmissionMedReqEnum {

	/**
	 * 0-speech
	 * 1-spare
	 * 2-64 kbit/s unrestricted
	 * 3-3.1 kHz audio
	 * 4-reserved for alternate speech (service 2)/64 kbit/s unrestricted (service 1)
	 * 5- reserved for alternate 64 kbit/s unrestricted (service 1)/speech (service 2)
	 * 6- 64 kbit/s preferred
	 * 7-2 ? 64 kbit/s unrestricted
	 * 8-384 kbit/s unrestricted
	 * 9-1536 kbit/s unrestricted
	 * 10-1920 kbit/s unrestricted
	 * 16-3 ? 64 kbit/s unrestricted
	 * 17-4 ? 64 kbit/s unrestricted
	 * 18-5 ? 64 kbit/s unrestricted
	 * 20-7 ? 64 kbit/s unrestricted
	 * 21-8 ? 64 kbit/s unrestricted
	 * 22-9 ? 64 kbit/s unrestricted
	 * 23-10 ? 64 kbit/s unrestricted
	 * 24-11 ? 64 kbit/s unrestricted
	 * 25-12 ? 64 kbit/s unrestricted
	 * 26-13 ? 64 kbit/s unrestricted
	 * 27-14 ? 64 kbit/s unrestricted
	 * 28-15 ? 64 kbit/s unrestricted
	 * 29-16 ? 64 kbit/s unrestricted
	 * 30-17 ? 64 kbit/s unrestricted
	 * 31-18 ? 64 kbit/s unrestricted
	 * 32-19 ? 64 kbit/s unrestricted
	 * 33-20 ? 64 kbit/s unrestricted
	 * 34-21 ? 64 kbit/s unrestricted
	 * 35-22 ? 64 kbit/s unrestricted
	 * 36-23 ? 64 kbit/s unrestricted
	 * 38-25 ? 64 kbit/s unrestricted
	 * 39-26 ? 64 kbit/s unrestricted
	 * 40-27 ? 64 kbit/s unrestricted
	 * 41-28 ? 64 kbit/s unrestricted
	 * 42-29 ? 64 kbit/s unrestricted
	 */
	
	SPEECH(0), SPARE(1), KBPS_64_UNRESTRICTED(2) , KHZ_3_1(3), RESERVED_SERVICE_1(4), RESERVED_SERVICE_2(5), KBPS_64_PREFERRED(6), KBPS_2_64(7), KBPS_384(8), 
	KBPS_1536(9), KBPS_1920(10), KBPS_3_64(16), KBPS_4_64(17), KBPS_5_64(18), KBPS_7_64(20), KBPS_8_64(21), KBPS_9_64(22), KBPS_10_64(23), KBPS_11_64(24), 
	KBPS_12_64(25), KBPS_13_64(26), KBPS_14_64(27), KBPS_15_64(28), KBPS_16_64(29), KBPS_17_64(30), KBPS_18_64(31), KBPS_19_64(32), KBPS_20_64(33), 
	KBPS_21_64(34), KBPS_22_64(35), KBPS_23_64(36), KBPS_25_64(38), KBPS_26_64(39), KBPS_27_64(40), KBPS_28_64(41), KBPS_29_64(42);
	
	private int code;

	private TransmissionMedReqEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static TransmissionMedReqEnum fromInt(int num) {
		switch (num) {
			case 0: { return SPEECH; }
			case 1: { return SPARE; }
			case 2: { return KBPS_64_UNRESTRICTED; }
			case 3: { return KHZ_3_1; }
			case 4: { return RESERVED_SERVICE_1; }
			case 5: { return RESERVED_SERVICE_2; }
			case 6: { return KBPS_64_PREFERRED; }
			case 7: { return KBPS_2_64; }
			case 8: { return KBPS_384; }
			case 9: { return KBPS_1536; }
			case 10: { return KBPS_1920; }
			case 16: { return KBPS_3_64; }
			case 17: { return KBPS_4_64; }
			case 18: { return KBPS_5_64; }
			case 20: { return KBPS_7_64; }
			case 21: { return KBPS_8_64; }
			case 22: { return KBPS_9_64; }
			case 23: { return KBPS_10_64; }
			case 24: { return KBPS_11_64; }
			case 25: { return KBPS_12_64; }
			case 26: { return KBPS_13_64; }
			case 27: { return KBPS_14_64; }
			case 28: { return KBPS_15_64; }
			case 29: { return KBPS_16_64; }
			case 30: { return KBPS_17_64; }
			case 31: { return KBPS_18_64; }
			case 32: { return KBPS_19_64; }
			case 33: { return KBPS_20_64; }
			case 34: { return KBPS_21_64; }
			case 35: { return KBPS_22_64; }
			case 36: { return KBPS_23_64; }
			case 38: { return KBPS_25_64; }
			case 39: { return KBPS_26_64; }
			case 40: { return KBPS_27_64; }
			case 41: { return KBPS_28_64; }
			case 42: { return KBPS_29_64; }
			default: { return null; }
		}
	}
	
}
