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
public enum ResourceTypeEnum {

	/**
	 * 0: PLAY_ANNOUNCEMNETS
	 * 1: PLAY_ANNOUNCEMENTS_COLLECT Digits
	 * 2: TEXT_TO_SPEECH
	 * 3: TEXT_TO_SPEECH_COLLECT Digits
	 * 4: FLEX_PARAMETER_BLOCK
	 */

	PLAY_ANNOUNCEMNETS(0), PLAY_ANNOUNCEMENTS_COLLECT(1), TEXT_TO_SPEECH(2), TEXT_TO_SPEECH_COLLECT(3), 
	FLEX_PARAMETER_BLOCK(4);
	private int code;
	private ResourceTypeEnum(int c){
		code = c;
	}
	public int getCode(){
		return code;
	}
	public static ResourceTypeEnum fromInt(int num){
		switch (num){
			case 0: { return PLAY_ANNOUNCEMNETS; }
			case 1: { return PLAY_ANNOUNCEMENTS_COLLECT; }
			case 2: { return TEXT_TO_SPEECH; }
			case 3: { return TEXT_TO_SPEECH_COLLECT; }
			case 4: { return FLEX_PARAMETER_BLOCK; }
			default: { return null; }
		}
	}
}
