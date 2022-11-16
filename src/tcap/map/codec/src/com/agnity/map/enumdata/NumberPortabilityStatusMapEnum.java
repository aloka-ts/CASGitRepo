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

public enum NumberPortabilityStatusMapEnum  { 

    /*
      notKnownToBePorted (0), 
      ownNumberPortedOut (1), 
      foreignNumberPortedToForeignNetwork (2), 
      ownNumberNotPortedOut (4), 
      foreignNumberPortedIn (5)

    */
    NOT_KNOWN_TO_BE_PORTED(0), OWN_NUMBERPORTED_OUT(1), FOREIGN_NUMBER_PORTED_TO_FOREIGN_NETWORK(2),
    OWN_NUMBER_NOT_PORTED_OUT(4), FOREIGN_NUMBER_PORTED_IN (5);

    private int code;
 
    private NumberPortabilityStatusMapEnum (int code) {
        this.code = code;
    }


    public int getCode() {                                     
        return code;
    }

    public static NumberPortabilityStatusMapEnum getValue(int tag){
        switch (tag) {
            case 0 : return NOT_KNOWN_TO_BE_PORTED;
            case 1 : return OWN_NUMBERPORTED_OUT;
            case 2 : return FOREIGN_NUMBER_PORTED_TO_FOREIGN_NETWORK;
            case 4 : return OWN_NUMBER_NOT_PORTED_OUT;
            case 5 : return FOREIGN_NUMBER_PORTED_IN;
            default: return null;
        }
    }
}
