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

public enum NatureOfAddressMapEnum {
    /*
      3GPP TS 29.002 version 9.4.0 Release 9 - page 417
 
      bits 
      7 6 5  nature of address indicator 
      
      0 0 0  unknown
      0 0 1  international number
      0 1 0  national significant number
      0 1 1  network specific number
      1 0 0  subscriber number
      1 0 1  reserved
      1 1 0  abbreviated number
      1 1 1  reserved for extension
    */

    UNKNOWN(0), INTERNATIONAL_NUMBER(1), NATIONAL_SIGNIFICANT_NUMBER(2), NETWORK_SPECIFIC_NUMBER(3),
    SUBSCRIBER_NUMBER(4), RESERVED(5), ABBREVIATED_NUMBER(6), RESERVED_FOR_EXTENSION(7);

    int code;
    
    private NatureOfAddressMapEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static NatureOfAddressMapEnum getValue(int code) {
        switch(code) {
            case 0 : return UNKNOWN;
            case 1 : return INTERNATIONAL_NUMBER;
            case 2 : return NATIONAL_SIGNIFICANT_NUMBER;
            case 3 : return NETWORK_SPECIFIC_NUMBER;
            case 4 : return SUBSCRIBER_NUMBER;
            case 5 : return RESERVED;
            case 6 : return ABBREVIATED_NUMBER;
            case 7 : return RESERVED_FOR_EXTENSION;
            default: return null;
        }
    }
}

