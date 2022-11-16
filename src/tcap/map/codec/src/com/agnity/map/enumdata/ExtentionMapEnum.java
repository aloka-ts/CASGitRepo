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


public enum ExtentionMapEnum {

    NO_EXTENTION(1);

    private int code;
    
    private ExtentionMapEnum(int code) {
        this.code=code;
    }

    public int getCode() {
      return code;
    }

    public static ExtentionMapEnum getValue(int tag) {
        switch(tag) {
            case 1 : return NO_EXTENTION;
            default: return null;
        }
    }
}


