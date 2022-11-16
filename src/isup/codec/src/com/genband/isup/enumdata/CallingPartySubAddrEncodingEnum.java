package com.genband.isup.enumdata;

/**
 * Enum for CallingPartySubAddrEncodingEnum for NSAP type sub address
 * @author reeta
 *
 */

public enum CallingPartySubAddrEncodingEnum {

        /**
         *  0-no indication
         *  1-no charge 
         *  2-charge 
         *  3-spare 
         */

        DECIMAL(0), BCD(1), IA5(2);

        private int code;

        private CallingPartySubAddrEncodingEnum(int c) {
                code = c;
        }

        public int getCode() {
                return code;
        }

        public static CallingPartySubAddrEncodingEnum fromInt(int num) {
                switch (num) {
                case 0: { return        DECIMAL ; }
                case 1: { return        BCD     ; }
                case 2: { return        IA5     ; }
                default: { return null; }
                }
        }
}
