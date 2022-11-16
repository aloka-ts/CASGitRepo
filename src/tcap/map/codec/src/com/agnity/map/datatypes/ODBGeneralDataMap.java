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

package com.agnity.map.datatypes;

import java.util.Arrays;
import java.util.BitSet;

import org.apache.log4j.Logger;

import com.agnity.map.enumdata.ODBGeneralDataMapEnum;
import com.agnity.map.exceptions.InvalidInputException;


/**
 * 
 * @author sanjay
 *
 */

public class ODBGeneralDataMap {
	
	private static int MAX_BIT_STRING_SIZE = 32;
	
	BitSet odbData = new BitSet(MAX_BIT_STRING_SIZE);
	
	private static Logger logger = Logger.getLogger(ODBGeneralDataMap.class);
	
	/**
	 * Set ODB General Data Bit position to true
	 * @param bitIndex
	 */
	
	public void enableOdbGeneralDataAtIndex(ODBGeneralDataMapEnum bitIndex) {
		if(logger.isDebugEnabled()){
			logger.debug("Setting Index = "+bitIndex);
		}
		odbData.set(bitIndex.getCode());
	}

	/**
	 * Set ODB General Data Bi// The current values are sufficiently mapped in 1 byte
    	t position to false
	 * @param bitIndex
	 */
	
	public void disableOdbGeneralDataAtIndex(ODBGeneralDataMapEnum bitIndex) {
		if(logger.isDebugEnabled()){
			logger.debug("Disabling Index = "+bitIndex);
		}
		odbData.clear(bitIndex.getCode());
	}
	
	/**
	 * Get if a specific service specified by ODBGeneralDataMapEnum is 
	 * enabled or not
	 * @param bitIndex
	 * @return
	 */
	
	public boolean getOdbGeneralDataAtIndex(ODBGeneralDataMapEnum bitIndex){
		if(logger.isDebugEnabled()){
			logger.debug("Disabling Index = "+bitIndex);
		}
		
		return odbData.get(bitIndex.getCode());
	}
	
	/**
	 * 
	 * @return
	 */
	
	public static byte[] encode(ODBGeneralDataMap odbobj) 
			throws InvalidInputException {
		if(odbobj == null){
			logger.error("data to encode is null");
			throw new InvalidInputException("data to decode is null");
		}
		    	
		return toByteArr(odbobj.odbData);
	}
	
	/**
	 * Decode ODB General Data to ODBGeneralDataMap object 
	 * @param odbData
	 */
	
	public static ODBGeneralDataMap decode(byte[] odbData) 
		throws InvalidInputException {
		if(odbData == null) {
			logger.error("data to decode is null");
			throw new InvalidInputException("data to decode is null");
		}

	 	int max_size = MAX_BIT_STRING_SIZE/8;
		
	 	if(odbData.length > max_size) {
	 		logger.error("Invalid data length "+odbData.length+" expected max length is "+max_size +" bytes");
			throw new InvalidInputException("Invalid data length "+odbData.length+" expected max length is"+ max_size +" bytes");
		}
		
		if(logger.isDebugEnabled()){
            logger.debug("odbData array length "+ odbData.length);
		}
		
		ODBGeneralDataMap retObj = new ODBGeneralDataMap();
		retObj.odbData = ODBGeneralDataMap.fromByteArray(odbData);
		
		return retObj;
	}
	
    /**
     * This Method is used for converting BitSet to byte array
     * 
     * @return bytes as a byte array
     * 
     * @param BitSet object
     */
    public static byte[] toByteArr(BitSet bits) {
    		int numBytes = (int) Math.ceil((float)(bits.length()+7)/8);
    		if(numBytes == 0) numBytes++;
            byte[] bytes = new byte[numBytes];
            for (int i=0; i<bits.length(); i++) {
                    if (bits.get(i)) {
                            bytes[bytes.length-i/8-1] |= 0x80>>(i%8);
                    }
            }
            return bytes;
    }

        
    /**
     * This Method is used for converting byte array to BitSet
     * 
     * @param bytes as a byte array
     * 
     * @return BitSet object
     */
    public static BitSet fromByteArray(byte[] bytes) {
        BitSet bits = new BitSet();
        for (int i = 0; i < bytes.length * 8; i++) {
            if ((bytes[bytes.length - i / 8 - 1] & (0x80 >> (i % 8))) > 0) {
                bits.set(i);
            }
        }
        return bits;
    }
    
    public String toString() {
    	StringBuilder sb = new StringBuilder();
    	//sb.append(")"
    		//	+
    	sb.append("all og calls barred = ").append(getOdbGeneralDataAtIndex(ODBGeneralDataMapEnum.ALL_OG_CALLS_BARRED )).append("\n");
    	sb.append("intl og calls barred = ").append(getOdbGeneralDataAtIndex(ODBGeneralDataMapEnum.INTL_OG_CALLS_BARRED)).append("\n");
    	sb.append("intl og calls not to hplmn cntry barred = ").append(getOdbGeneralDataAtIndex(ODBGeneralDataMapEnum.INTL_OG_CALLS_NOT_TO_HPLMN_CNTRY_BARRED )).append("\n");
    	sb.append("interzonal OG CALLS barred = ").append(getOdbGeneralDataAtIndex(ODBGeneralDataMapEnum.INTERZONAL_OG_CALLS_BARRED)).append("\n");
    	sb.append("interzonal og calls not to hplmn cntry barred = ").append(getOdbGeneralDataAtIndex(ODBGeneralDataMapEnum.INTERZONAL_OG_CALLS_NOT_TO_HPLMN_CNTRY_Barred)).append("\n");
    	sb.append("interzonal og calls and intl og calls not to hplmn cntry barred = ").append(getOdbGeneralDataAtIndex(ODBGeneralDataMapEnum.INTERZONAL_OG_CALLS_AND_INTL_OG_CALLS_NOT_To_HPLMN_CNTRY_BARRED)).append("\n");
    	sb.append("premium rate info og calls barred = ").append(getOdbGeneralDataAtIndex(ODBGeneralDataMapEnum.PREMIUM_RATE_INFORMATION_OG_CALLS_BARRED )).append("\n");
    	sb.append("premium rate entertainment og calls barred = ").append(getOdbGeneralDataAtIndex(ODBGeneralDataMapEnum.PREMIUM_RATE_ENTERTAINMENT_OG_CALLS_BARRED )).append("\n");
    	sb.append("ss access barred = ").append(getOdbGeneralDataAtIndex(ODBGeneralDataMapEnum.SS_ACCESS_BARRED )).append("\n");
    	sb.append("all ect barred = ").append(getOdbGeneralDataAtIndex(ODBGeneralDataMapEnum.ALL_ECT_BARRED )).append("\n");
    	sb.append("chargeable ect barred = ").append(getOdbGeneralDataAtIndex(ODBGeneralDataMapEnum.CHARGEABLE_ECT_BARRED )).append("\n");
    	sb.append("intl ect barred = ").append(getOdbGeneralDataAtIndex(ODBGeneralDataMapEnum.INTL_ECT_BARRED )).append("\n");
    	sb.append("interzonal ect barred = ").append(getOdbGeneralDataAtIndex(ODBGeneralDataMapEnum.INTERZONAL_ECT_BARRED )).append("\n");
    	sb.append("dbly chargeable ect barred = ").append(getOdbGeneralDataAtIndex(ODBGeneralDataMapEnum.DBLY_CHARGEABLE_ECT_BARRED )).append("\n");
    	sb.append("multiple ect barred = ").append(getOdbGeneralDataAtIndex(ODBGeneralDataMapEnum.MULTIPLE_ECT_BARRED )).append("\n");
    	sb.append("all packed oriented serviced barred = ").append(getOdbGeneralDataAtIndex(ODBGeneralDataMapEnum.ALL_PACKET_ORIENTED_SERVICES_BARRED )).append("\n");
    	sb.append("roamer access to hplmn ap barred = ").append(getOdbGeneralDataAtIndex(ODBGeneralDataMapEnum.ROAMER_ACCESS_TO_HPLMN_AP_BARRED )).append("\n");
    	sb.append("roamer access to vplmn ap barred = ").append(getOdbGeneralDataAtIndex(ODBGeneralDataMapEnum.ROAMER_ACCESS_TO_VPLMN_AP_BARRED )).append("\n");
    	sb.append("roaming outside plmn og calls barred = ").append(getOdbGeneralDataAtIndex(ODBGeneralDataMapEnum.ROAMING_OUTSIDE_PLMN_OG_CALLS_BARRED )).append("\n");
    	sb.append("all ic calls barred = ").append(getOdbGeneralDataAtIndex(ODBGeneralDataMapEnum.ALL_IC_CALLS_BARRED )).append("\n");
    	sb.append("roaming outside plmn ic calls barred = ").append(getOdbGeneralDataAtIndex(ODBGeneralDataMapEnum.RAOMING_OUTSIDE_PLMN_IC_CALLS_BARRED )).append("\n");
    	sb.append("roaming outside plmn cntry ic calls barred = ").append(getOdbGeneralDataAtIndex(ODBGeneralDataMapEnum.ROAMING_OUTSIDE_PLMN_CNTRY_IC_CALLS_BARRED )).append("\n");
    	sb.append("roaming outside plmn barred = ").append(getOdbGeneralDataAtIndex(ODBGeneralDataMapEnum.ROAMING_OUTSIDE_PLMN_BARRED )).append("\n");
    	sb.append("roaming outside plmn country barred = ").append(getOdbGeneralDataAtIndex(ODBGeneralDataMapEnum.ROAMING_OUTSIDE_PLMN_COUNTRY_BARRED )).append("\n");
    	sb.append("registration all cf barred = ").append(getOdbGeneralDataAtIndex(ODBGeneralDataMapEnum.REGISTRATION_ALL_CF_BARRED )).append("\n");
    	sb.append("registraion cf not to hplmn barred = ").append(getOdbGeneralDataAtIndex(ODBGeneralDataMapEnum.REGISTRAION_CF_NOT_TO_HPLMN_BARRED )).append("\n");
    	sb.append("registration interzonal cf barred = ").append(getOdbGeneralDataAtIndex(ODBGeneralDataMapEnum.REGISTRATION_INTERZONAL_CF_BARRED )).append("\n");
    	sb.append("registration interzonal cf not to hplmn barred = ").append(getOdbGeneralDataAtIndex(ODBGeneralDataMapEnum.REGISTRAION_INTERZONAL_CF_NOT_TO_HPLMN_BARRED )).append("\n");
    	sb.append("registration international cf barred = ").append(getOdbGeneralDataAtIndex(ODBGeneralDataMapEnum.REGISTRATION_INTERNATIONAL_CF_BARRED  )).append("\n");

    	return sb.toString();
    }
}
