package com.genband.isup.enumdata;

/**
 * Enum for Location 
 * @author vgoel
 *
 */

public enum LocationEnum {

	/**
	* 0-user (U)
 	* 1-private network serving the local user (LPN)
 	*  2-public network serving the local user (LN)
 	*  3-transit network (TN)
 	*  4-public network serving the remote user (RLN)
 	*  5-private network serving the remote user (RPN)
 	*  6-spare
 	*  8-international network (INTL)
 	*  10-network beyond interworking point (BI)
 	*  12-reserved for national use
 	*  13-reserved for national use
 	*  14-reserved for national use
 	*  15-reserved for national use
	*/
	USER(0), PRIVATE_NETWORK_LOCAL_USER(1), PUBLIC_NETWORK_LOCAL_USER(2), TRANSIT_NETWORK(3), 
	PUBLIC_NETWORK_REMOTE_USER(4), PRIVATE_NETWORK_REMOTE_USER(5), SPARE(6), 
	INTERNATIONAL_NETWORK(8), NETWORK_BEYOND_INTERWORKING_POINT(10), RESERVED_NATIONAL_USE(12);
	
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
		case 6: { return 	SPARE	; }
		case 8: { return 	INTERNATIONAL_NETWORK	; }
		case 10: { return 	NETWORK_BEYOND_INTERWORKING_POINT	; }
		case 12: { return 	RESERVED_NATIONAL_USE	; }

		default: { return null; }
		}
	}
}
