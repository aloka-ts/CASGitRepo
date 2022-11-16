package com.agnity.inapitutcs2.enumdata.fci.mexico.featureusagebit;

import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ankitsinghal on 25/10/16.
 */

public enum ATFFeatureUsageCountEnum {
    DAY_OF_WEEK(1),
    TIME_OF_DAY(2),
    CALL_ALLOCATOR(3),
    COUNTRY_CODE_ROUTING(4),
    GEOGRAPHIC_ROUTING(5),
    CALL_PROMPTER(6),
    EN_ROUTE_ANNOUNCEMENT(7),
    DAY_OF_YEAR_ROUTING(8),
    DIALED_NUMBER_DECISION(9),
    COURTESY_RESPONSE(10),
    NO_ANSWER_BUSY(11);

    private final static Map<Integer, ATFFeatureUsageCountEnum> map =
            new HashMap<Integer, ATFFeatureUsageCountEnum>(ATFFeatureUsageCountEnum.values().length, 1.0f);
    private static final Logger logger = Logger.getLogger(ATFFeatureUsageCountEnum.class);

    static {
        for (ATFFeatureUsageCountEnum c : ATFFeatureUsageCountEnum.values()) {
            map.put(c.code, c);
        }
    }

    int code;

    ATFFeatureUsageCountEnum(int cause) {
        this.code = cause;
    }

    public static ATFFeatureUsageCountEnum fromInt(int id) {
        ATFFeatureUsageCountEnum result = map.get(id);
        if (result == null) {
            logger.warn("No code code exists with code: " + id );
        }
        return result;
    }

    public int getCode() {
        return this.code;
    }
}
