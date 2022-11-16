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
 * Enum for Information discrimination indicator
 * @author Mriganka
 *
 */
public enum InfoDiscriminationIndiEnum {
	/**
	 * 0-MA Code
	 * 1-CA Code
	 * 2->127-spare	 
	 */
	MA_CODE(0), CA_CODE(1), SPARE(2);
	private int code;
	private InfoDiscriminationIndiEnum(int c) {
		code = c;
	}
	
	public int getCode() {
		return code;
	}
	public static InfoDiscriminationIndiEnum fromInt(int num) {
		switch (num) {
			case 0: { return MA_CODE; }
			case 1: { return CA_CODE; }			case 2: { return SPARE; }			default: { return null; }
		}
	}
}
