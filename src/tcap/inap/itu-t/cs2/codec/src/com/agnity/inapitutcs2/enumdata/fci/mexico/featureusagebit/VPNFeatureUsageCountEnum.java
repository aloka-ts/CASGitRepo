package com.agnity.inapitutcs2.enumdata.fci.mexico.featureusagebit;

import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ankitsinghal on 25/10/16.
 */

public enum VPNFeatureUsageCountEnum {
    VPN_ATTENDANT_RESERVED(1),
    PRIVILIGE_OVERRIDE(2),
    VPN_ON_NET(3),
    VPN_FORCED_ON_NET(4),
    VPN_OFF_NET(5),
    REMOTE_ACCESS(6),
    CALL_FOLLOW_ME_UPDATE(7),
    CALL_FOLLOW_ME(8),
    CALL_SCREENING(9),
    GVNS_TERMINATION(10),
    GVNS_ORIGINATION(10),
    CRISIS_MANAGEMENT(11),
    OFFNET_OVERFLOW_EGRESS_BUSY(12),
    RESERVED_TYPE_A(13),
    RESERVED_TYPE_B(14),
    RESERVED_TYPE_C(15),
    RESERVED_TYPE_D(16),
    RESERVED_TYPE_N(17),
    UNREGISTERED_DIAL_AROUND(18),
    MISPROVISIONED_CUSTOMER(19);

    private final static Map<Integer, VPNFeatureUsageCountEnum> map =
            new HashMap<Integer, VPNFeatureUsageCountEnum>(VPNFeatureUsageCountEnum.values().length, 1.0f);
    private static final Logger logger = Logger.getLogger(VPNFeatureUsageCountEnum.class);

    static {
        for (VPNFeatureUsageCountEnum c : VPNFeatureUsageCountEnum.values()) {
            map.put(c.code, c);
        }
    }

    int code;

    VPNFeatureUsageCountEnum(int cause) {
        this.code = cause;
    }

    public static VPNFeatureUsageCountEnum fromInt(int id) {
        VPNFeatureUsageCountEnum result = map.get(id);
        if (result == null) {
            logger.warn("No code code exists with code: " + id );
        }
        return result;
    }

    public int getCode() {
        return this.code;
    }
}
