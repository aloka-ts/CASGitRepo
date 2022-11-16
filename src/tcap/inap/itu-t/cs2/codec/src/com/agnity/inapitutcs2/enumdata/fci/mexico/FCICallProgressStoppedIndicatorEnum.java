/**
 *
 */
package com.agnity.inapitutcs2.enumdata.fci.mexico;

import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * @author nisharma
 * Enum for Call Progress Stopped Information
 */
public enum FCICallProgressStoppedIndicatorEnum {

    /**
     * 0: No Indication returned from SCP(example: vacant code account)
     * 1: Call Progress not stopped(no SCP blockage)
     * 2: Call progress stopped - Feature terminates to announcement or tone
     * 3: Not Used
     * 4: Call progress stopped - Invalid code(Account ,Authorization, Pin)
     * 5: Call progress stopped - Invalid Destination Number
     * 6: Call progress stopped - No digits received (Destination ,Account ,Authorization, Pin)
     */
    NO_INDICATION(0),
    CALL_PROGRESS_NOT_STOPPED(1),
    CALL_PROGRESS_STOPPED_TERMINATE_ANN_TONE(2),
    NOT_USED(3),
    CALL_PROGRESS_STOPPED_INVALID_CODE(4),
    CALL_PROGRESS_STOPPED_INVALID_DESTINATION_NUMBER(5),
    CALL_PROGRESS_STOPPED_NO_DIGITS_RECEIVED(6);

    private final static Map<Integer, FCICallProgressStoppedIndicatorEnum> map =
            new HashMap<Integer, FCICallProgressStoppedIndicatorEnum>(FCICallProgressStoppedIndicatorEnum.values().length, 1.0f);
    private static final Logger logger = Logger.getLogger(FCICallProgressStoppedIndicatorEnum.class);

    static {
        for (FCICallProgressStoppedIndicatorEnum c : FCICallProgressStoppedIndicatorEnum.values()) {
            map.put(c.code, c);
        }
    }

    int code;

    FCICallProgressStoppedIndicatorEnum(int cause) {
        this.code = cause;
    }

    public static FCICallProgressStoppedIndicatorEnum fromInt(int id) {
        FCICallProgressStoppedIndicatorEnum result = map.get(id);
        if (result == null) {
            logger.warn("No code code exists with code: " + id );
        }
        return result;
    }

    public int getCode() {
        return this.code;
    }

}
