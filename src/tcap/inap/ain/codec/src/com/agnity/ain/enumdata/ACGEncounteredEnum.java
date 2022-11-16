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
public enum ACGEncounteredEnum 
{
	/**
	 * 1->  1 Digit Control
	 * 2->  2 Digit Control
	 * 3->  3 Digit Control
	 * 4->  4 Digit Control
	 * 5->  5 Digit Control
	 * 6->  6 Digit Control
	 * 7->  7 Digit Control
	 * 8->  8 Digit Control
	 * 9->  9 Digit Control
	 * 10->  10 Digit Control
	 */
	Digit_1_Control(1),Digit_2_Control(2),Digit_3_Control(3),Digit_4_Control(4),Digit_5_Control(5),Digit_6_Control(6),Digit_7_Control(7),
	Digit_8_Control(8),Digit_9_Control(9),Digit_10_Control(10);
	private int code;
	private ACGEncounteredEnum(int c) {
		code = c;
	}
	public int getCode() {
		return code;
	}

	public static ACGEncounteredEnum fromInt(int num) {
		switch (num) {
		case 1: { return Digit_1_Control; }
		case 2: { return Digit_2_Control; }
		case 3: { return Digit_3_Control; }
		case 4: { return Digit_4_Control; }
		case 5: { return Digit_5_Control; }
		case 6: { return Digit_6_Control; }
		case 7: { return Digit_7_Control; }
		case 8: { return Digit_8_Control; }
		case 9: { return Digit_9_Control; }
		case 10: { return Digit_10_Control; }
		default: { return null; }
		}
	}
}
