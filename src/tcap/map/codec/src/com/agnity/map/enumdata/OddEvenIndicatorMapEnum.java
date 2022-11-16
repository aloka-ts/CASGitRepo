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

public enum OddEvenIndicatorMapEnum {
   ODD(1),EVEN(0);
   
   private int code;
   
   private OddEvenIndicatorMapEnum(int i) {
	  this.code = i;
   }

   public int getCode() {
	return code;
  }
   
   public static OddEvenIndicatorMapEnum getValue(int code){
	   switch (code) {
		case 0: {return EVEN;}
		case 1: {return ODD;}
		default: {return null;}
		}
   }
}
