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
 * Enum for Location 
 * @author Mriganka
 *
 */
public enum LocationEnum {
	/**
	* 0-user (U)
 	* 1-private network serving the local user (LPN)
 	*  2-public network serving the local user (LN)
 	*  3-transit network (TN)	*  4-public network serving the remote user (RLN)
 	*  5-private network serving the remote user (RPN)	   	*  6-Local interface controlled by this signaling link	   	*  7-International network	   	*  10-Unknown
	*/
	USER(0), PRIVATE_NETWORK_LOCAL_USER(1), PUBLIC_NETWORK_LOCAL_USER(2), TRANSIT_NETWORK(3), 
	PUBLIC_NETWORK_REMOTE_USER(4), PRIVATE_NETWORK_REMOTE_USER(5), LOCAL_INTERFACE(6), 
	INTERNATIONAL_NETWORK(7),UNKNOWN(10);
	private int code;
	private LocationEnum(int c) {
		code = c;
	}
	
	public int getCode() {
		return code;
	}
	public static LocationEnum fromInt(int num) {
		switch (num) {
		case 0: { return 	USER	; }
		case 1: { return 	PRIVATE_NETWORK_LOCAL_USER	; }
		case 2: { return 	PUBLIC_NETWORK_LOCAL_USER	; }
		case 3: { return 	TRANSIT_NETWORK	; }
		case 4: { return 	PUBLIC_NETWORK_REMOTE_USER	; }
		case 5: { return 	PRIVATE_NETWORK_REMOTE_USER	; }
		case 6: { return 	LOCAL_INTERFACE	; }
		case 7: { return 	INTERNATIONAL_NETWORK	; }
		case 10: { return 	UNKNOWN	; }
		default: { return null; }
		}
	}
}
