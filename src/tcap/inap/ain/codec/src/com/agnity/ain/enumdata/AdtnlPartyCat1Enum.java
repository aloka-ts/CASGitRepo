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
package com.agnity.ain.enumdata;
/**
 * Enum for Additional Party's Category 1
 * @author Mriganka
 *
 */
public enum AdtnlPartyCat1Enum {
	/**
	 * 0-spare
	 * 1-train public telephone
	 * 2-pink public telephone
	 */
	SPARE(0), TRAIN_PUBLIC_TELEPHONE(1), PINK_PUBLIC_TELEPHONE(2);
	private int code;
	private AdtnlPartyCat1Enum(int c) {
		code = c;
	}
	public int getCode() {
		return code;
	}
	public static AdtnlPartyCat1Enum fromInt(int num) {
		switch (num) {
		case 0: { return SPARE; }
		case 1: { return TRAIN_PUBLIC_TELEPHONE; }
		case 2: { return PINK_PUBLIC_TELEPHONE; }
		default: { return null; }
		}
	}
}
