/****
 * Copyright (c) 2013 Agnity, Inc. All rights reserved.
 * 
 * This is proprietary source code of Agnity, Inc.
 * 
 * Agnity, Inc. retains all intellectual property rights associated with this
 * source code. Use is subject to license terms.
 * 
 * This source code contains trade secrets owned by Agnity, Inc. Confidentiality
 * of this computer program must be maintained at all times, unless explicitly
 * authorized by Agnity, Inc.
 ****/
package com.agnity.cap.v2.datatypes.enumType;
/**
 * 
 * @author rnarayan
 *
 */
public enum AssignorAssigneeCapV2Enum {

	/**
	 * 0-Message originator is default assignee
	 * 1-Message originator is assignor only
	 */
	DEFAULT_ASSIGNEE(0), ASSIGNOR_ONLY(1);
	
	private int code;

	private AssignorAssigneeCapV2Enum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static AssignorAssigneeCapV2Enum getValue(int num) {
		switch (num) {
		case 0: { return 	DEFAULT_ASSIGNEE	; }
		case 1: { return 	ASSIGNOR_ONLY	; }
		default: { return null; }
		}
	}
}
