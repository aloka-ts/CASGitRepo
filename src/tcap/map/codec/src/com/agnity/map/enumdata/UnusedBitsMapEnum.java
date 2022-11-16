package com.agnity.map.enumdata;

public enum UnusedBitsMapEnum {
	NO_UNUSED_BIT(0),
	LAST_1_BIT_UNUSED(1),
	LAST_2_BIT_UNUSED(2),
	LAST_3_BIT_UNUSED(3),
	LAST_4_BIT_UNUSED(4),
	LAST_5_BIT_UNUSED(5),
	LAST_6_BIT_UNUSED(6),
	LAST_7_BIT_UNUSED(7);
	
	int code;
	
	private UnusedBitsMapEnum(int code) {
		this.code = code;
	}
	
	public int getcode() {
		return this.code;
	}
	
	public static UnusedBitsMapEnum getValue(int tag) {
		switch(tag) {
			case 0: return NO_UNUSED_BIT;
			case 1: return LAST_1_BIT_UNUSED;
			case 2: return LAST_2_BIT_UNUSED;
			case 3: return LAST_3_BIT_UNUSED;
			case 4: return LAST_4_BIT_UNUSED;
			case 5: return LAST_5_BIT_UNUSED;
			case 6: return LAST_6_BIT_UNUSED;
			case 7: return LAST_7_BIT_UNUSED;
			default: return null;
		}
	}
}
