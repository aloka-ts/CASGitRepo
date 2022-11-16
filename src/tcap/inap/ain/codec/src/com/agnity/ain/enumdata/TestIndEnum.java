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
public enum TestIndEnum 
{
	/**
	 * 0-presentation allowed
	 * 1-presentation restricted
	 * 2-spare
	 * 3-spare
	 */

	NOT_A_TEST_CALL(0), TEST_CALL(1);

	private int code;

	private TestIndEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}

	public static TestIndEnum fromInt(int num) {
		switch (num) {
		case 0: { return NOT_A_TEST_CALL; }
		case 1: { return TEST_CALL; }
		default: { return null; }
		}
	}
}
