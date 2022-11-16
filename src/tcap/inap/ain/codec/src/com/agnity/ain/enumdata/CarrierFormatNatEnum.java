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
public enum CarrierFormatNatEnum {
	/**
	 * 0-No NOC Provided
	 * 1-Local
	 * 2-Intra LATA toll
	 * 3-Inter LATA
	 * 4-Local,Intra LATA toll and Inter LATA
	 * 5-Local and Intra LATA toll 	 
	 * 6-Intra LATA toll and Inter LATA 
	 */
	NO_NOC_PROVIDED(0),LOCAL(1),INTRA_LATA(2),INTER_LATA(3),LOCAL_INTER_INTRA_LATA(4),LOCAL_INTRA_LATA(5),INTER_INTRA_LATA(6);
	private int code;
	private CarrierFormatNatEnum(int c) {
		code = c;
	}
	public int getCode() {
		return code;
	}
	public static CarrierFormatNatEnum fromInt(int num) {
		switch (num) {
		case 0: { return 	NO_NOC_PROVIDED	; }
		case 1: { return 	LOCAL	; }
		case 2: { return 	INTRA_LATA	; }
		case 3: { return 	INTER_LATA	; }
		case 4: { return 	LOCAL_INTER_INTRA_LATA	; }
		case 5: { return 	LOCAL_INTRA_LATA	; }
		case 6: { return 	INTER_INTRA_LATA	; }
		default: { return null; }
		}
	}
}
