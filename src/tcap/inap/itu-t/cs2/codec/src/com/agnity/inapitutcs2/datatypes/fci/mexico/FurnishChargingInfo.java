package com.agnity.inapitutcs2.datatypes.fci.mexico;

import com.agnity.inapitutcs2.datatypes.GenericDigits;
import com.agnity.inapitutcs2.enumdata.EncodingSchemeEnum;
import com.agnity.inapitutcs2.enumdata.fci.mexico.FCIOptionalAttributesEnum;
import com.agnity.inapitutcs2.enumdata.fci.mexico.featureusagebit.ATFFeatureUsageCountEnum;
import com.agnity.inapitutcs2.enumdata.fci.mexico.featureusagebit.VPNFeatureUsageCountEnum;
import com.agnity.inapitutcs2.datatypes.fci.mexico.FurnishChargingMessage;
import com.agnity.inapitutcs2.exceptions.InvalidInputException;
import com.agnity.inapitutcs2.util.Util;

import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.Arrays;

import static com.agnity.inapitutcs2.enumdata.DigitCatEnum.RESERVED_ACCOUNT_CODE;

/**
 * Used for encoding of Furnish Charging Information octets
 * It is required at INAP messaging for Billing porpoises;
 *
 * @author nisharma
 */

public class FurnishChargingInfo extends FurnishChargingMessage implements Serializable{

	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(FurnishChargingInfo.class);

    boolean announcementUnitIndication;
    boolean alternateBillingNumberIndication;
    boolean accountCodeIndication;
    boolean dialedNumberIndication;
    boolean originatingNumberIndication;
    boolean destinationNumberIndication;
    boolean fubIndication;
    int overFlowBit;

    FCIBillingData fciBillingData = new FCIBillingData();
    int announcementUnits;
    String alternateBillingNumber;
    String accountOrDepartmentCode;
    String dialedNumber;
    String originatingNumber;
    String destinationNumber;

    int[] fubCountValue;
    
    public FurnishChargingInfo() {
    	setCode(FCI_TYPE1);
    }

    public int getAnnouncementUnits() {
        return announcementUnits;
    }

    /**
     * This function will set Announcement Unit .
     *
     * @param announcementUnits
     */
    public void setAnnouncementUnits(int announcementUnits) {
        this.announcementUnitIndication = true;
        this.announcementUnits = announcementUnits;
    }


    public int[] getFubCount() {
        return fubCountValue;
    }

    public void setFubCount(VPNFeatureUsageCountEnum featureUsageCountEnum, int value) {
        setFubCount(featureUsageCountEnum.getCode(), value);
    }

    /**
     * This function will set Usage count value corresponding to indexes.
     *
     * @param int index
     * @param int value
     */
    private void setFubCount(int index, int value) {
        fubIndication = true;
        //There are 31 counts defined for use , the value of each being held in a 2 bit field.

        //If any one count exceeds the maximum value of 3 then it will remain at value 3 and the overflow bit will be set.
        if (fubCountValue == null) {
            fubCountValue = new int[32];
        }

        if (index > 0 && index < 32) {
            fubCountValue[index] = value;
            if (value > 3) {
                fubCountValue[index] = 3;
                overFlowBit = 1;
            }
        }
    }

    public void setFubCount(ATFFeatureUsageCountEnum featureUsageCountEnum, int value) {
        setFubCount(featureUsageCountEnum.getCode(), value);
    }

    /**
     * This function will encode Furnish Charging Information.
     *
     * @return encoded data byte[]
     * @throws InvalidInputException
     */
    public byte[] encodeFurnishChargingInfo() throws InvalidInputException {
        if (logger.isInfoEnabled()) {
            logger.info("encodeFurnishChargingInfo:Enter");
        }
        byte[] myParams = new byte[this.getMaxFCILength()];
        int len = 0;

        // Encode Octet -1 (Indicator)
        myParams[len++] = this.encodeFciIndicator();
        if (logger.isInfoEnabled()) {
            logger.info("encodeFciIndicator:" + myParams[0]);
        }
        myParams[len++] = 0; // For spare

        byte[] bData = this.fciBillingData.encodeBillingData();
        System.arraycopy(bData, 0, myParams, len, bData.length);
        len += bData.length;

        // Announcement Unit
        if (announcementUnitIndication) {
            myParams[len++] = (byte) (announcementUnits & 0xFF);
        }

        if (alternateBillingNumberIndication) {
            if (logger.isInfoEnabled()) {
                logger.info("encoding start for Alternate Billing Number");
            }
            byte[] num = getEncodedGenericDigits(getGenericDigitsForInputString(alternateBillingNumber));
            myParams[len++] = (byte) num.length;
            System.arraycopy(num, 0, myParams, len, num.length);
            len += num.length;
            if (logger.isInfoEnabled()) {
                logger.info("encoding stop for Alternate Billing Number");
            }
        }

        if (accountCodeIndication) {
            if (logger.isInfoEnabled()) {
                logger.info("encoding start for Account Code");
            }
            byte[] num = getEncodedGenericDigits(getGenericDigitsForInputString(accountOrDepartmentCode));
            myParams[len++] = (byte) num.length;
            System.arraycopy(num, 0, myParams, len, num.length);
            len += num.length;
            if (logger.isInfoEnabled()) {
                logger.info("encoding stop for Account Code");
            }
        }

        if (dialedNumberIndication) {
            if (logger.isInfoEnabled()) {
                logger.info("encoding start for Dialed Number");
            }
            byte[] num = getEncodedGenericDigits(getGenericDigitsForInputString(dialedNumber));
            // copy byte array
            myParams[len++] = (byte) num.length;
            System.arraycopy(num, 0, myParams, len, num.length);
            len += num.length;
            if (logger.isInfoEnabled()) {
                logger.info("encoding stop for Dialed Number");
            }
        }

        if (originatingNumberIndication) {
            if (logger.isInfoEnabled()) {
                logger.info("encoding start for Originating Number");
            }
            byte[] num = getEncodedGenericDigits(getGenericDigitsForInputString(originatingNumber));
            // copy byte array
            myParams[len++] = (byte) num.length;
            System.arraycopy(num, 0, myParams, len, num.length);
            len += num.length;
            if (logger.isInfoEnabled()) {
                logger.info("encoding stop for Originating Number");
            }
        }

        if (destinationNumberIndication) {
            if (logger.isInfoEnabled()) {
                logger.info("encoding start for Destination Number");
            }
            byte[] num = getEncodedGenericDigits(getGenericDigitsForInputString(destinationNumber));
            // copy byte array
            myParams[len++] = (byte) num.length;
            System.arraycopy(num, 0, myParams, len, num.length);
            len += num.length;
            if (logger.isInfoEnabled()) {
                logger.info("encoding stop for Destination Number");
            }
        }

        if (fubIndication) {
            if (logger.isInfoEnabled()) {
                logger.info("encoding start for Feature Usage Count");
            }
            for (int j = 0, k = 0; j < 8; j++) {
                byte lc = 0;
                for (int l = 0; l < 4; l++) {
                    if (!((j == 0) && (l == 0))) {
                        byte lcCounter = (byte) (fubCountValue[k++] & 0x000000FF);
                        lcCounter = (byte) (lcCounter << (l * 2));
                        lc |= lcCounter;
                    } else {
                        lc |= (overFlowBit & 0x01);
                        k++;
                    }
                }
                myParams[len++] = lc;
            }
            if (logger.isInfoEnabled()) {
                logger.info("encoding stop for Feature Usage Count");
            }
        }
        if (logger.isInfoEnabled()) {
            logger.info("Full Byte Array:" + Util.formatBytes(myParams));
        }
        return myParams;
    }

    /**
     * This function will calculate Maximum length of FCI Parameters.
     *
     * @return integer value
     */
    private int getMaxFCILength() throws InvalidInputException {
        int retVal = 6;

        if (announcementUnitIndication) {
            retVal++;
        }

        if (alternateBillingNumberIndication) {
            retVal += getOctetLengthOfStringWithSelfLength(alternateBillingNumber);
        }

        if (accountCodeIndication) {
            retVal += getOctetLengthOfStringWithSelfLength(accountOrDepartmentCode);
        }

        if (dialedNumberIndication) {
            retVal += getOctetLengthOfStringWithSelfLength(dialedNumber);
        }

        if (originatingNumberIndication) {
            retVal += getOctetLengthOfStringWithSelfLength(originatingNumber);
        }

        if (destinationNumberIndication) {
            retVal += getOctetLengthOfStringWithSelfLength(destinationNumber);
        }

        if (fubIndication) {
            retVal += 8;
        }

        if (logger.isInfoEnabled()) {
            logger.info("getMaxFCILength: Total length to be encoded: )" + retVal);
        }
        return retVal;
    }

    /**
     * This function will encode Furnish Charging Information Indicators.
     *
     * @return encoded data byte
     */

    // This method is used to encode the indicators
    private byte encodeFciIndicator() {
        byte retVal = 0;

        if (announcementUnitIndication) {
            retVal |= 0x01;
        }
        if (alternateBillingNumberIndication) {
            retVal |= 0x02;
        }
        if (accountCodeIndication) {
            retVal |= 0x04;
        }
        if (dialedNumberIndication) {
            retVal |= 0x08;
        }
        if (originatingNumberIndication) {
            retVal |= 0x10;
        }
        if (destinationNumberIndication) {
            retVal |= 0x20;
        }
        if (fubIndication) {
            retVal |= 0x40;
        }
        return retVal;
    }

    private final byte[] getEncodedGenericDigits(GenericDigits genericDigits) throws InvalidInputException {
        return GenericDigits.encodeGenericDigits(genericDigits.encodingSchemeEnum,
                                                 genericDigits.digitCatEnum,
                                                 genericDigits.digits);
    }

    private final GenericDigits getGenericDigitsForInputString(String inputString) {
        GenericDigits genericDigits = new GenericDigits();
        genericDigits.setDigitCatEnum(RESERVED_ACCOUNT_CODE);
        genericDigits.setDigits(inputString);

        //If the length of inputString is odd, set the Encoding scheme is odd, else even.
        EncodingSchemeEnum encodingSchemeEnum;
        if (inputString.length() % 2 == 0) {
            encodingSchemeEnum = EncodingSchemeEnum.BCD_EVEN;
        } else {
            encodingSchemeEnum = EncodingSchemeEnum.BCD_ODD;
        }
        genericDigits.setEncodingSchemeEnum(encodingSchemeEnum);
        return genericDigits;
    }

    private final int getOctetLengthOfStringWithSelfLength(String inputString) {
        int retVal = 0;
        retVal += 1; // Length octet
        retVal += 1; // GenericDigits contains the first octet as encodingscheme and type of digits
        retVal += (inputString.length() + 1) / 2; //String encoded as nibble
        return retVal;
    }

    /**
     * This function will decode Furnish Charging Information.
     *
     * @param byte[] data
     * @throws InvalidInputException
     */
    public void decodeFurnishChargingInfo(byte[] data) throws InvalidInputException {
        if (logger.isInfoEnabled()) {
            logger.info("decodeFurnishChargingInfo:Enter");
        }
        if (logger.isDebugEnabled()) {
            logger.debug("decodeFurnishChargingInfo: Input--> data:" + Util.formatBytes(data));
        }
        if (data == null) {
            logger.error("decodeFurnishChargingInfo: InvalidInputException(data is null)");
            throw new InvalidInputException("data is null");
        }

        int len = 0;
        int fciIndicator = this.decodeFciIndicator(data);
        len++;//fciIndicators
        len++;//spare

        // Billing Data
        this.fciBillingData = this.fciBillingData.decodeBillingData(data, len);

        if (logger.isInfoEnabled()) {
            logger.info("Billing Data Parameter:=>" + fciBillingData);
        }

        len = len + 4;//billing data

        if (announcementUnitIndication) {
            int announcemenUnitData = data[len++];//announcement unit
            this.setAnnouncementUnits(announcemenUnitData);
            if (logger.isInfoEnabled()) {
                logger.info("announcemenUnit Data:=>" + announcemenUnitData);
            }
        }

        //decode AlternateBilling Number
        if (alternateBillingNumberIndication) {
            if (logger.isInfoEnabled()) {
                logger.info("decoding start for Alternate Billing Number");
            }
            if (len > 1) {
                int startPosition = len;
                int endPosition = len + data[len] + 1;
                if (logger.isInfoEnabled()) {
                    logger.info("length of Alternate Billing Number:=>" + data[len]);
                }
                byte[] alternateBillingNumberArray = Arrays.copyOfRange(data, startPosition + 1, endPosition);
                if (logger.isInfoEnabled()) {
                    logger.info(" Alternate Billing Number:=>" + Util.formatBytes(alternateBillingNumberArray));
                }
                GenericDigits alternateBillingNumber = GenericDigits.decodeGenericDigits(alternateBillingNumberArray);
                len = endPosition;
            }
            if (logger.isInfoEnabled()) {
                logger.info("decoding stop for Alternate Billing Number");
            }
        }

        //decode Account Code
        if (accountCodeIndication) {
            if (logger.isInfoEnabled()) {
                logger.info("decoding start for Account Code");
            }
            if (len > 1) {
                int startPosition = len;
                int endPosition = len + data[len] + 1;
                if (logger.isInfoEnabled()) {
                    logger.info("length of Account Code:=>" + data[len]);
                }
                byte[] accountCodeArray = Arrays.copyOfRange(data, startPosition + 1, endPosition);
                if (logger.isInfoEnabled()) {
                    logger.info("  Account Code:=>" + Util.formatBytes(accountCodeArray));
                }
                GenericDigits accountOrDepartmentCode = GenericDigits.decodeGenericDigits(accountCodeArray);
                len = endPosition;
            }
            if (logger.isInfoEnabled()) {
                logger.info("decoding stop for Account Code");
            }
        }

        //decode Dialed Number
        if (dialedNumberIndication) {
            if (logger.isInfoEnabled()) {
                logger.info("decoding start for Dialed Number");
            }
            if (len > 1) {
                int startPosition = len;
                int endPosition = len + data[len] + 1;
                if (logger.isInfoEnabled()) {
                    logger.info("length of dialedNumber:=>" + data[len]);
                }
                byte[] dialedNumArray = Arrays.copyOfRange(data, startPosition + 1, endPosition);
                if (logger.isInfoEnabled()) {
                    logger.info("  dialed Number:=>" + Util.formatBytes(dialedNumArray));
                }
                GenericDigits dialedNumber = GenericDigits.decodeGenericDigits(dialedNumArray);
                len = endPosition;
            }
            if (logger.isInfoEnabled()) {
                logger.info("decoding stop for Dialed Number");
            }
        }

        //decode Originating Number
        if (originatingNumberIndication) {
            if (logger.isInfoEnabled()) {
                logger.info("decoding start for Originating Number");
            }
            if (len > 1) {
                int startPosition = len;
                int endPosition = len + data[len] + 1;
                if (logger.isInfoEnabled()) {
                    logger.info("length of originating Number:=>" + data[len]);
                }
                byte[] originatingNumArray = Arrays.copyOfRange(data, startPosition + 1, endPosition);
                if (logger.isInfoEnabled()) {
                    logger.info(" originating Number:=>" + Util.formatBytes(originatingNumArray));
                }
                GenericDigits originatingNumber = GenericDigits.decodeGenericDigits(originatingNumArray);
                len = endPosition;
            }
            if (logger.isInfoEnabled()) {
                logger.info("decoding stop for Originating Number");
            }
        }

        // decode Destination Number
        if (destinationNumberIndication) {
            if (logger.isInfoEnabled()) {
                logger.info("decoding start for Destination Number");
            }
            if (len > 1) {
                int startPosition = len;
                int endPosition = len + data[len] + 1;
                if (logger.isInfoEnabled()) {
                    logger.info("length of Destination Number:=>" + data[len]);
                }
                byte[] destinationNumArray = Arrays.copyOfRange(data, startPosition + 1, endPosition);
                if (logger.isInfoEnabled()) {
                    logger.info("  Destination Number:=>" + Util.formatBytes(destinationNumArray));
                }
                GenericDigits destinationNumber = GenericDigits.decodeGenericDigits(destinationNumArray);
                len = endPosition;
            }
            if (logger.isInfoEnabled()) {
                logger.info("decoding stop for Destination Number");
            }
        }

        // decode FUB count
        if (fubIndication) {
            this.fubCountValue = new int[32];
            for (int i = 0, j = 0; i < 8; ++i) {
                byte loc = data[len++];
                if (i == 0) {
                    overFlowBit = loc & 0x01;
                }
                this.fubCountValue[j++] = (i == 0) ? 0x01 : ((loc) & 0x03);
                this.fubCountValue[j++] = ((loc >> 2) & 0x03);
                this.fubCountValue[j++] = ((loc >> 4) & 0x03);
                this.fubCountValue[j++] = ((loc >> 6) & 0x03);
            }
            String output = "";
            for (int i = 0; i < 32; ++i) {
                output += this.fubCountValue[i];
            }
            logger.info("FUB Count:" + output);
        }
    }

    private int decodeFciIndicator(byte[] data) {
        int fciIndication = 0;
        if ((data[0] & 0x01) == 1) {
            fciIndication = (data[0] & 0x01);
            if (logger.isInfoEnabled()) {
                logger.info("announcementUnitInd:=>" + FCIOptionalAttributesEnum.fromInt(fciIndication));
            }
            this.announcementUnitIndication = true;
        }

        if ((data[0] & 0x02) == 2) {
            fciIndication = (data[0] & 0x02);
            if (logger.isInfoEnabled()) {
                logger.info("alterBillingNumInd:=>" + FCIOptionalAttributesEnum.fromInt(fciIndication));
            }
            this.alternateBillingNumberIndication = true;
        }

        if ((data[0] & 0x04) == 4) {
            fciIndication = (data[0] & 0x04);
            if (logger.isInfoEnabled()) {
                logger.info("accountCodeInd:=>" + FCIOptionalAttributesEnum.fromInt(fciIndication));
            }
            this.accountCodeIndication = true;
        }

        if ((data[0] & 0x08) == 8) {
            fciIndication = (data[0] & 0x08);
            if (logger.isInfoEnabled()) {
                logger.info("dialedNumInd:=>" + FCIOptionalAttributesEnum.fromInt(fciIndication));
            }
            this.dialedNumberIndication = true;
        }

        if ((data[0] & 0x10) == 16) {
            fciIndication = (data[0] & 0x10);
            if (logger.isInfoEnabled()) {
                logger.info("originatingNumInd:=>" + FCIOptionalAttributesEnum.fromInt(fciIndication));
            }
            this.originatingNumberIndication = true;
        }

        if ((data[0] & 0x20) == 32) {
            fciIndication = (data[0] & 0x20);
            if (logger.isInfoEnabled()) {
                logger.info("destinationNumInd:=>" + FCIOptionalAttributesEnum.fromInt(fciIndication));
            }
            this.destinationNumberIndication = true;
        }

        if ((data[0] & 0x40) == 64) {
            fciIndication = (data[0] & 0x40);
            if (logger.isInfoEnabled()) {
                logger.info("FUBInd:=>" + FCIOptionalAttributesEnum.fromInt(fciIndication));
            }
            this.fubIndication = true;
        }
        return fciIndication;
    }

    public FCIBillingData getFciBillingData() {
        return fciBillingData;
    }

    public String getAlternateBillingNumber() {
        return alternateBillingNumber;
    }

    public void setAlternateBillingNumber(String alternateBillingNumber) {
        this.alternateBillingNumberIndication = true;
        this.alternateBillingNumber = alternateBillingNumber;
    }

    public String getAccountOrDepartmentCode() {
        return accountOrDepartmentCode;
    }

    public void setAccountOrDepartmentCode(String accountOrDepartmentCode) {
        this.accountCodeIndication = true;
        this.accountOrDepartmentCode = accountOrDepartmentCode;
    }

    public String getDialedNumber() {
        return dialedNumber;
    }

    public void setDialedNumber(String dialedNumber) {
        this.dialedNumberIndication = true;
        this.dialedNumber = dialedNumber;
    }

    public String getOriginatingNumber() {
        return originatingNumber;
    }

    public void setOriginatingNumber(String originatingNumber) {
        this.originatingNumberIndication = true;
        this.originatingNumber = originatingNumber;
    }

    public String getDestinationNumber() {
        return destinationNumber;
    }

    public void setDestinationNumber(String destinationNumber) {
        this.destinationNumberIndication = true;
        this.destinationNumber = destinationNumber;
    }
}
