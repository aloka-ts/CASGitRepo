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

public enum NotReachableReasonMapEnum { 

    MS_PERGED(0),IMSI_DETACHED(1), RESTRICTED_AREA(2), NOT_REGISTERED(3);

    private int code;
 
    private NotReachableReasonMapEnum(int code) {
        this.code = code;
    }


    public int getCode() {                                     
        return code;
    }

    public static NotReachableReasonMapEnum getValue(int tag){
        switch (tag) {
            case 0 : return MS_PERGED;
            case 1 : return IMSI_DETACHED;
            case 2 : return RESTRICTED_AREA;
            case 3 : return NOT_REGISTERED;
            default: return null;
        }
    }
}
