package com.genband.isup.enumdata;

/**
 * Enum for Member Status Indication for DPC Info Parameter
 * @author rarya
 *
 */
public enum MemberStatusIndEnum {

	/**
	 * Values as per 1416 SBTM RA - Requirement to have screening feature (V14)
	 * 0-pseudo user
	 * 1-member
	 * 2-member (Stop Service)
	 * 3-pseudo subscriber (stop service)
	 * 4-pseudo subscriber (out of service)
	 * 5-non member
	 * 6-Spare
	 * 7-member1
	 * 8-member1 (Stop Service)
	 */
	PSEUDO_USER(0), MEMBER(1), MEMBER_STOP_SRV(2), PSEUDO_SUBSCRIBER_STOP_SRV(3),
	PSEUDO_SUBSCRIBER_OUT_OF_SRV(4), NON_MEMBER(5), SPARE(6), MEMBER1(7),
	MEMBER1_STOP_SRV(8);
	
	private int code;

	private MemberStatusIndEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static MemberStatusIndEnum fromInt(int num) {
		switch (num) {
			case 0: { return PSEUDO_USER; }
			case 1: { return MEMBER; }
			case 2: { return MEMBER_STOP_SRV; }
			case 3: { return PSEUDO_SUBSCRIBER_STOP_SRV; }
			case 4: { return PSEUDO_SUBSCRIBER_OUT_OF_SRV; }
			case 5: { return NON_MEMBER; }
			case 6: { return SPARE; }
			case 7: { return MEMBER1; }
			case 8: { return MEMBER1_STOP_SRV; }
			default: { return null; }
		}
	}
}
