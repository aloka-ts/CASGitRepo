/**
 *
 */
package com.agnity.inapitutcs2.enumdata.fci.mexico;

/**
 * @author nisharma
 * Enum for Optional Attributes of FCI
 */
public enum FCIOptionalAttributesEnum {

    /**
     * 1: Announcement Unit
     * 2: Alternate Billing Number
     * 4: Account Code Indication
     * 8: Dialed Number Indication
     * 16: Originating Number Indication
     * 32: Destination Number Indication
     * 64: FUB Indication
     */
    ANNC_IND(1),
    ALT_BILL_NUM_IND(2),
    ACC_CODE_IND(4),
    DIALLED_NUM_IND(8),
    ORIG_NUM_IND(16),
    DEST_NUM_IND(32),
    FEAT_USAGE_BASED_IND(64);

    private int code;

    /**
     * @param code
     */
    FCIOptionalAttributesEnum(int code) {
        this.code = code;
    }

    public static FCIOptionalAttributesEnum fromInt(int num) {
        switch (num) {
            case 1: {return ANNC_IND;}
            case 2: {return ALT_BILL_NUM_IND;}
            case 4: {return ACC_CODE_IND;}
            case 8: {return DIALLED_NUM_IND;}
            case 16: {return ORIG_NUM_IND;}
            case 32: {return DEST_NUM_IND;}
            case 64: {return FEAT_USAGE_BASED_IND;}
            default:
                return null;
        }
    }

    public int getCode() {
        return code;
    }

}
