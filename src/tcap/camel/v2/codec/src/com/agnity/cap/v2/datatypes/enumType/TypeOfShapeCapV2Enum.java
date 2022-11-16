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
package com.agnity.cap.v2.datatypes.enumType;

/**
 * @ref: GSM 03.32
 * @author rnarayan
 * Bits	
   4 3 2 1	
   0 0 0 0	Ellipsoid Point
   0 0 0 1	Ellipsoid point with uncertainty Circle
   0 0 1 0	Ellipsoid point with uncertainty Ellipse
   0 1 0 1	Polygon
   other values	Reserved for future use

 */
public enum TypeOfShapeCapV2Enum {

	ELLIPSOID_POINT(0), ELLIPSOID_POINT_WITH_UNCERTAINTY_CIRCLE(1),
	ELLIPSOID_POINT_WITH_UNCERTAINTY_ELLIPSE(2),POLYGON(4);
	
	private int code;
	
	private TypeOfShapeCapV2Enum(int code){
		this.code=code;
	}
	
	public int getCode() {
		return code;
	}
	
	public static TypeOfShapeCapV2Enum getValue(int tag){
		switch (tag) {
		case 0: return ELLIPSOID_POINT;
		case 1: return ELLIPSOID_POINT_WITH_UNCERTAINTY_CIRCLE;
		case 2: return ELLIPSOID_POINT_WITH_UNCERTAINTY_ELLIPSE;
		case 4: return POLYGON;
		default: return null;
		}
	}
}
