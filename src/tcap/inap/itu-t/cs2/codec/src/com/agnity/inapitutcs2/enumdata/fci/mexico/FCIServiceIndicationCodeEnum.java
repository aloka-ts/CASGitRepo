/**
 *
 */
package com.agnity.inapitutcs2.enumdata.fci.mexico;

/**
 * @author nisharma
 * Enum for Service Indicator Code
 */
public enum FCIServiceIndicationCodeEnum {

    /**
     * 0: No service recognized by SCP or switch based LD/ILD
     * 1: Advance free phone Call
     * 3: Basic free phone call
     * 6: VPN call
     * 13: Advanced LD/ILD
     * 14: Free phone Pay Phone Identification(Service id=14)
     * 15: Free phone Pay Phone Identification(Service id=15)
     */
    NO_SERVICE_RECOGNIZED(0),
    ADVANCED_FREE_PHONE(1),
    BASIC_FREE_PHONE(3),
    VPN_CALL(6),
    ADVANCED_LD_INTERNATIONAL_LD(13),
    FREE_PHONE_PAYPHONE_IND_14(14),
    FREE_PHONE_PAYPHONE_IND_15(15);

    private int code;

    FCIServiceIndicationCodeEnum(int code) {
        this.code = code;
    }

    public static FCIServiceIndicationCodeEnum fromInt(int num) {
        switch (num) {
            case 0: {return NO_SERVICE_RECOGNIZED;}
            case 1: {return ADVANCED_FREE_PHONE;}
            case 3: {return BASIC_FREE_PHONE;}
            case 6: {return VPN_CALL;}
            case 13: {return ADVANCED_LD_INTERNATIONAL_LD;}
            case 14: {return FREE_PHONE_PAYPHONE_IND_14;}
            case 15: {return FREE_PHONE_PAYPHONE_IND_15;}
            default:
                return null;
        }
    }

    public int getCode() {
        return code;
    }

}
