
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

public enum DomainTypeMapEnum {

    CS_DOMAIN(0), PS_DOMAIN(1);

    private int code;
  
    private DomainTypeMapEnum(int code) {
        this.code = code;
    }
    
    public int getCode() {
    	return code;
    }

    public static DomainTypeMapEnum getValue(int tag) {
        switch(tag) {
            case 0: return CS_DOMAIN;
            case 1: return PS_DOMAIN;
            default: return null;
        }
    }
}
