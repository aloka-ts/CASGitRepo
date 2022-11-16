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

public enum NatureOfAddressIndicatorMapEnum {
	SPARE(0), SUBCRIBER_NO(1), UNKNOWN(2), NATIONAL(3), INTERNATIONAL(4), NETWORK_NO(5),
	NATIONAL_NETWORK_ROUTE_NO(6), NETWORK_ROUTING_NO_NETWORK_SPECIFIC_NO_FORMAT(7); 

    int code;
    
    private NatureOfAddressIndicatorMapEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static NatureOfAddressIndicatorMapEnum getValue(int code) {
        switch(code) {
            case 0 : return SPARE;
            case 1 : return SUBCRIBER_NO;
            case 2 : return UNKNOWN;
            case 3 : return NATIONAL;
            case 4 : return INTERNATIONAL;
            case 5 : return NETWORK_NO;
            case 6 : return NATIONAL_NETWORK_ROUTE_NO;
            case 7 : return NETWORK_ROUTING_NO_NETWORK_SPECIFIC_NO_FORMAT;
            default: return null;
        }
    }
}

