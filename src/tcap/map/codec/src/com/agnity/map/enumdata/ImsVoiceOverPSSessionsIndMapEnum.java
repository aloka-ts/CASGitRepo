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

public enum ImsVoiceOverPSSessionsIndMapEnum  { 

    /*
     imsVoiceOverPS-SessionsNotSupported(0),
     imsVoiceOverPS-SessionsSupported (1)
    */
    IMS_VOICE_OVER_PS_SESSIONS_NOT_SUPPORTED(0),
    IMS_VOICE_OVER_PS_SESSIONS_SUPPORTED(1);

    private int code;
 
    private ImsVoiceOverPSSessionsIndMapEnum (int code) {
        this.code = code;
    }


    public int getCode() {                                     
        return code;
    }

    public static ImsVoiceOverPSSessionsIndMapEnum getValue(int tag){
        switch (tag) {
            case 0 : return IMS_VOICE_OVER_PS_SESSIONS_NOT_SUPPORTED;
            case 1 : return IMS_VOICE_OVER_PS_SESSIONS_SUPPORTED;
            default: return null;
        }
    }
}
