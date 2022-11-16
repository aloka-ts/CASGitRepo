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

public enum NatureOfNumberCapV2Enum {
   INTERNATIONAL_NUMBER(1);
   
   private int code;
   private NatureOfNumberCapV2Enum(int code){
	   this.code=code;
   }
   
   public int getCode() {
	return code;
}
   public static NatureOfNumberCapV2Enum getValue(int tag){
	    switch (tag) {
	       case 1:return INTERNATIONAL_NUMBER;
	       default:return null;
	    }
   }
	
}
