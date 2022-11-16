package com.genband.isup.enumdata.bearercapability;
/**
 * This enum represents Assignor/Assignee.
 * @author vgoel
 */
public enum AssignorAssigneeEnum {
	
	/**
	 * 0-Message originator is default assignee
	 * 1-Message originator is assignor only
	 */
	DEFAULT_ASSIGNEE(0), ASSIGNOR_ONLY(1);
	
	private int code;

	private AssignorAssigneeEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static AssignorAssigneeEnum fromInt(int num) {
		switch (num) {
		case 0: { return 	DEFAULT_ASSIGNEE	; }
		case 1: { return 	ASSIGNOR_ONLY	; }
		default: { return null; }
		}
	}
}
