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

public enum NumberPlanMapEnum { 

    /*
      As per ASN.1 documented in ETSI TS 129 002 V9.4.0 (2011-01)
      
      bits 
      4 3 2 1: numbering plan indicator

      0 0 0 0 unknown
      0 0 0 1 ISDN/Telephony Numbering Plan (Rec ITU-T E.164)
      0 0 1 0 spare
      0 0 1 1 data numbering plan (ITU-T Rec X.121)
      0 1 0 0 telex numbering plan (ITU-T Rec F.69)
      0 1 0 1 spare
      0 1 1 0 land mobile numbering plan (ITU-T Rec E.212)
      0 1 1 1 spare
      1 0 0 0 national numbering plan
      1 0 0 1 private numbering plan
      1 1 1 1 reserved for extension
    */
    UNKNOWN(0),ISDN_TELEPHONY_NUMBERING(1), SPARE_2(2), DATA_NUMBERING_PLAN(3),TELEX_NUMBERING_PLAN(4),
    SPARE_5(5), LAND_MOBILE_NUMBERING_PLAN(6), SPARE_7(7), NATIONAL_NUMBERING_PLAN(8), PRIVATE_NUMBERING_PLAN(9),
    RESERVED_FOR_EXTENSION(15);               

    private int code;
 
    private NumberPlanMapEnum(int code) {
        this.code = code;
    }


    public int getCode() {                                     
        return code;
    }

    public static NumberPlanMapEnum getValue(int tag){
        switch (tag) {
            case 0 : return UNKNOWN;
            case 1 : return ISDN_TELEPHONY_NUMBERING;
            case 2 : return SPARE_2;
            case 3 : return DATA_NUMBERING_PLAN;
            case 4 : return TELEX_NUMBERING_PLAN;
            case 5 : return SPARE_5;
            case 6 : return LAND_MOBILE_NUMBERING_PLAN;
            case 7 : return SPARE_7;
            case 8 : return NATIONAL_NUMBERING_PLAN;
            case 9 : return PRIVATE_NUMBERING_PLAN;
            case 15: return RESERVED_FOR_EXTENSION;
            default: return null;
        }
    }
}
