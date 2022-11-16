/*******************************************************************************
 *   Copyright (c) 2014 Agnity, Inc. All rights reserved.
 *   
 *   This is proprietary source code of Agnity, Inc. 
 *   
 *   Agnity, Inc. retains all intellectual property rights associated 
 *   with this source code. Use is subject to license terms.
 *   
 *   This source code contains trade secrets owned by Agnity, Inc.
 *   Confidentiality of this computer program must be maintained at 
 *   all times, unless explicitly authorized by Agnity, Inc.
 *******************************************************************************/
/**
 * 
 */
package com.agnity.ain.enumdata;

/**
 * @author nishantsharma
 *
 */
public enum ACGControlTypeEnum {
	/**
	 * 0->  Normal Control
	 * 1->  Manual Individual control encountered
	 * 2->  Manual Global control encountered
	 * 3->  Both Manual Individual and Global control encountered
	 */
	NORMAL_CONTROL(0),MANUAL_INDIVIDUAL_CTRL(1),MANUAL_GLOBAL_CTRL(2),INDIVIDUAL_AND_GLOBAL(3);
	private int code;
	private ACGControlTypeEnum(int c) {
		code = c;
	}
	public int getCode() {
		return code;
	}

	public static ACGControlTypeEnum fromInt(int num) {
		switch (num) {
		case 0: { return NORMAL_CONTROL; }
		case 1: { return MANUAL_INDIVIDUAL_CTRL; }
		case 2: { return MANUAL_GLOBAL_CTRL; }
		case 3: { return INDIVIDUAL_AND_GLOBAL; }
		default: { return null; }
		}
	}
}

