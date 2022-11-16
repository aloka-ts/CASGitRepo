package com.agnity.inapitutcs2.datatypes.fci.mexico;

import java.io.Serializable;

import com.agnity.inapitutcs2.enumdata.fci.mexico.FCICallProgressStoppedIndicatorEnum;
import com.agnity.inapitutcs2.enumdata.fci.mexico.FCIServiceIndicationCodeEnum;
import com.agnity.inapitutcs2.exceptions.InvalidInputException;
import com.agnity.inapitutcs2.util.Util;

import org.apache.log4j.Logger;


/**
 * This Class will use for encode Billing Data.
 * This Mandatory parameter contains recording data necessary
 * for the SSP to create a per call Billing record.
 */
public class FCIBillingData implements Serializable{
    private static Logger logger = Logger.getLogger(FCIBillingData.class);

    private FCICallProgressStoppedIndicatorEnum fciCallProgressStoppedIndicator;
    private FCIServiceIndicationCodeEnum fciServiceIndicationCode;
    
    public int getFciServiceIndication() {
		return fciServiceIndication;
	}

	public void setFciServiceIndication(int fciServiceIndication) {
		this.fciServiceIndication = fciServiceIndication;
	}

	private int fciServiceIndication=-1;

    /**
     * This function will decode Billing Data. This Mandatory parameter contains recording data necessary
     * for the SSP to create a per call Billing record.Its contents are Call Progress Stooped Indicator,
     * Service Indicator Code
     * Billing Option Parameter
     * Documentation Type
     *
     * @return BillingData fciBillingData
     */
    FCIBillingData decodeBillingData(byte[] data, int len) throws InvalidInputException {
        if (logger.isInfoEnabled()) {
            logger.info("decodeBillingData:Enter");
        }

        if (logger.isDebugEnabled()) {
            logger.debug("decodeBillingData: Input--> data:" + Util.formatBytes(data));
        }

        if (data == null) {
            logger.error("decodeBillingData: InvalidInputException(data is null)");
            throw new InvalidInputException("data is null");
        }

        FCIBillingData billingData = new FCIBillingData();
        int callProgressbit = data[len++];
        billingData.fciCallProgressStoppedIndicator = FCICallProgressStoppedIndicatorEnum.fromInt(callProgressbit);

        if (logger.isInfoEnabled()) {
            logger.info("fciCallProgressStoppedIndicator:=>" + fciCallProgressStoppedIndicator);
        }
        int serviceIndicator = data[len++];
        billingData.fciServiceIndication=serviceIndicator;
        billingData.fciServiceIndicationCode = FCIServiceIndicationCodeEnum.fromInt(serviceIndicator);

        if (logger.isInfoEnabled()) {
            logger.info("fciServiceIndication:=>" + fciServiceIndication);
        }
        
        if (logger.isInfoEnabled()) {
            logger.info("fciServiceIndicationCode:=>" + fciServiceIndicationCode);
        }
        int billingOptionParameter = data[len++];
        if (logger.isInfoEnabled()) {
            logger.info("BillingOptionParameter:=>" + billingOptionParameter);
        }

        int documentationType = data[len++];
        if (logger.isInfoEnabled()) {
            logger.info("DocumentationType:=>" + documentationType);
        }
        return billingData;
    }

    /**
     * This function will encode Billing Data. This Mandatory parameter contains recording data necessary
     * for the SSP to create a per call Billing record.Its contents are Call Progress Stooped Indicator,
     * Service Indicator Code
     * Billing Option Parameter
     * Documentation Type
     *
     * @return encoded data byte[]
     */
    byte[] encodeBillingData() throws InvalidInputException {
        if (logger.isInfoEnabled()) {
            logger.info("encodeBiilingData:Enter");
        }

        byte[] data = new byte[4];
        if (fciCallProgressStoppedIndicator != null) {
            data[0] = (byte) fciCallProgressStoppedIndicator.getCode(); // Call Progress Stooped Indicator
        } else {
            throw new InvalidInputException("Call Progress Stopped Indicator is null. It is mandatory field");
        }
        if (fciServiceIndicationCode != null) {
            data[1] = (byte) fciServiceIndicationCode.getCode(); //  Service Indicator Code
        }else if (fciServiceIndication!=-1) {
        	
    	 if (logger.isInfoEnabled()) {
             logger.info("fciServiceIndication "+fciServiceIndication);
         }
            data[1] = (byte)fciServiceIndication; //  Service Indicator Code
        } else {
            throw new InvalidInputException("Service Indicator Code is null. It is mandatory field");
        }
        data[2] = 0x00; // Billing Option Parameter
        data[3] = 0x01; // Documentation Type
        if (logger.isInfoEnabled()) {
            logger.info("encodeBillingData:Exit");
        }
        return data;
    }

    public String toString() {
        return "Call Progress Stopped Indicator: =>" + fciCallProgressStoppedIndicator + ", " +
                "Service Indicator Code: =>" + fciServiceIndicationCode;
    }


    public FCIServiceIndicationCodeEnum getFciServiceIndicationCode() {
        return fciServiceIndicationCode;
    }

    public void setFciServiceIndicationCode(FCIServiceIndicationCodeEnum fciServiceIndicationCode) {
        this.fciServiceIndicationCode = fciServiceIndicationCode;
    }

    public FCICallProgressStoppedIndicatorEnum getFciCallProgressStoppedIndicator() {
        return fciCallProgressStoppedIndicator;
    }

    public void setFciCallProgressStoppedIndicator(FCICallProgressStoppedIndicatorEnum fciCallProgressStoppedIndicator) {
        this.fciCallProgressStoppedIndicator = fciCallProgressStoppedIndicator;
    }
}
