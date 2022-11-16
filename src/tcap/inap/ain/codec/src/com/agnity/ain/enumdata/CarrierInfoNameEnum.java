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
 * Enum for Carrier Information Name
 * @author Mriganka
 *
 */
public enum CarrierInfoNameEnum {
	/**
	 * 0-spare
	 * 1-reserved for national use
	 * 248-Donor SCP carrier information
	 * 249-Recepient SCP carrier information
	 * 250-SCP carrier information
	 * 251-OLEC Information
	 * 252-TLEC Information
	 * 253-Chosen inter-exchange carrier Information
	 * 254-Transit carrier Information
	 */
	SPARE(0), RESERVED(1), DONOR_SCP(248), RECEPIENT_SCP(249), SCP(250), OLEC(251), TLEC(252), CHOSEN_INTER_EXCHANGE(253), TRANSIT(254);
	private int code;
	private CarrierInfoNameEnum(int c) {
		code = c;
	}
	public int getCode() {
		return code;
	}
	public static CarrierInfoNameEnum fromInt(int num) {
		switch (num) {
			case 0: { return SPARE; }
			case 1: { return RESERVED; }
			case 248: { return DONOR_SCP; }
			case 249: { return RECEPIENT_SCP; }
			case 250: { return SCP; }
			case 251: { return OLEC; }
			case 252: { return TLEC; }
			case 253: { return CHOSEN_INTER_EXCHANGE; }
			case 254: { return TRANSIT; }
			default: { return null; }
		}
	}
}
