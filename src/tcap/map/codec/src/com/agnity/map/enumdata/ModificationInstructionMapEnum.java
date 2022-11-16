package com.agnity.map.enumdata;

/**
 * @author sanjay
 *
 */

public enum ModificationInstructionMapEnum {

	DEACTIVATE(0), 
	ACTIVATE(1);
	
	private int code;

	private ModificationInstructionMapEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static ModificationInstructionMapEnum fromInt(int num) {
		switch (num) {
		case 0: { return 	DEACTIVATE	; }
		case 1: { return 	ACTIVATE	; }

		default: { return null; }
		}
	}
}
