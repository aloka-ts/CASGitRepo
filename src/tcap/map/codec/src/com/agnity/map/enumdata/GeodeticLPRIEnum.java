/****
 * Copyright (c) 2013 Agnity, Inc. All rights reserved.
 * 
 * This is proprietary source code of Agnity, Inc.
 * 
 * Agnity, Inc. retains all intellectual property rights associated with this
 * source code. Use is subject to license terms.
 * 
 * This source code contains trade secrets owned by Agnity, Inc. Confidentiality
 * of this computer program must be maintained at all times, unless explicitly
 * authorized by Agnity, Inc.
 ****/

package com.agnity.map.enumdata;

/**
 * @author sanjay
 * 
 * Refers to Calling Geodetic Location defined in Q.763 (1999).
 *  Only the description of an ellipsoid point with uncertainty circle
 * as specified in Q.763 (1999) is allowed to be used
 * 
 * 0 0 presentation allowed
 * 0 1 presentation restricted
 * 1 0 location not available (Note)
 * 1 1 spare 
 *
 */
public enum GeodeticLPRIEnum {
	
	PRESENTATION_ALLOWED(0), PRESENTATION_RESTRICTED(1), LOCATION_NOT_AVAILABLE(2),
	SPARE(3);
	
	private int code;
	
	private GeodeticLPRIEnum(int code){
		this.code=code;
	}
	
	public int getCode() {
		return code;
	}
	
	public static GeodeticLPRIEnum getValue(int tag){
		switch (tag) {
		case 0: return PRESENTATION_ALLOWED;
		case 1: return PRESENTATION_RESTRICTED;
		case 2: return LOCATION_NOT_AVAILABLE;
		case 3: return SPARE;
		default: return null;
		}
	}
	
}
