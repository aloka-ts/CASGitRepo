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

package com.agnity.cap.v3.datatypes;

import java.util.Arrays;

import org.apache.log4j.Logger;

import com.agnity.cap.v3.datatypes.enumType.ExtentionCapV3Enum;
import com.agnity.cap.v3.datatypes.enumType.NatureOfNumberCapV3Enum;
import com.agnity.cap.v3.datatypes.enumType.NumberPlanCapV3Enum;
import com.agnity.cap.v3.exceptions.InvalidInputException;
import com.agnity.cap.v3.util.CapFunctions;
/**
 * 
 * @author rnarayan
 * mscAddress non-asn decoding & encoding
 */
public class ISDNAddressStringCapV3 {

	 private ExtentionCapV3Enum extention;
	 private NatureOfNumberCapV3Enum natureOfNumber;
	 private NumberPlanCapV3Enum numberPlan;
	 private String addressDigits;
	 private int countryCode;
	 private static Logger logger = Logger.getLogger(ISDNAddressStringCapV3.class);
	 
	 /**
	  * 
	  * @return ExtentionCapV2Enum
	  */
	 public ExtentionCapV3Enum getExtention() {
		return extention;
	}

	/**
	 * 
	 * @return NatureOfNumberCapV2Enum
	 */
	public NatureOfNumberCapV3Enum getNatureOfNumber() {
		return natureOfNumber;
	}
    
	/**
	 * 
	 * @return NumberPlanCapV2Enum
	 */
	public NumberPlanCapV3Enum getNumberPlan() {
		return numberPlan;
	}

	/**
	 * 
	 * @return decode hex string of addressDigits
	 */
	public String getAddressDigits() {
		return addressDigits;
	}

	/**
	 * 
	 * @return countryCode
	 */
	public int getCountryCode() {
		return countryCode;
	}
	
	/**
	 * 
	 * @param extention
	 */
	public void setExtention(ExtentionCapV3Enum extention) {
		this.extention = extention;
	}

	/**
	 * 
	 * @param natureOfNumber
	 */
	public void setNatureOfNumber(NatureOfNumberCapV3Enum natureOfNumber) {
		this.natureOfNumber = natureOfNumber;
	}

	/**
	 * 
	 * @param numberPlan
	 */
	public void setNumberPlan(NumberPlanCapV3Enum numberPlan) {
		this.numberPlan = numberPlan;
	}

	/**
	 * 
	 * @param addressDigits
	 */
	public void setAddressDigits(String addressDigits) {
		this.addressDigits = addressDigits;
	}

   
	/**
	 * decode mscAddress byte array into non-asn object of MscAddressCapV2
	 * @param byte array of mscAddress
	 * @return MscAddressCapV2 object
	 */
	public static ISDNAddressStringCapV3 decode(byte[] bytes)  throws InvalidInputException{
		ISDNAddressStringCapV3 msc = null;
			 msc = new ISDNAddressStringCapV3();
			 msc.extention = ExtentionCapV3Enum.getValue((bytes[0]>>7)&0x01);
			 msc.natureOfNumber = NatureOfNumberCapV3Enum.getValue((bytes[0]&0x7F)>>4);
			 msc.numberPlan = NumberPlanCapV3Enum.getValue(bytes[0]&0x0F);
			 msc.addressDigits = CapFunctions.decodeNumber(bytes,1);
			 msc.countryCode = Integer.parseInt(CapFunctions.decodeNumber(new byte[]{bytes[0],bytes[1]},1));
			
			 if(logger.isDebugEnabled()){
				 logger.debug("mscAddress non-asn successfully decoded.");
			 }

		 return msc;
	}
	
	/**
	 * encode non-asn MscAddressCapV2 object into byte array of mscAddress
	 * @param ISDNAddressStringCapV3 object
	 * @return byteArray of mscAddress
	 */
	public static byte[] encode(ISDNAddressStringCapV3 msc)  throws InvalidInputException{
		byte[] bytes = null;
			int ext = msc.extention.getCode();
			int natOfNum = msc.natureOfNumber.getCode();
			int numPlan = msc.numberPlan.getCode();
			String adddigits = msc.addressDigits; 
			
			byte b0 = (byte)((((ext<<3)+natOfNum)<<4)+numPlan);
			
			adddigits = CapFunctions.encodenNumber(adddigits);
			byte[] adddigitsBytes = CapFunctions.hexStringToByteArray(adddigits);
			int totalLength = adddigitsBytes.length+1; 
			bytes = new byte[totalLength];
			bytes[0]=b0;
			for(int i=0;i<(totalLength-1);i++){
				bytes[i+1] = adddigitsBytes[i];
			}
			
			if(logger.isDebugEnabled()){
				logger.debug("mscAddress byte array encoded successfully");
				logger.debug("mscAddress byte array encoded length::"+bytes.length);
			}
			
		return bytes;
	}
	
	@Override
	public String toString() {
		String bl = "\n";
		return  "Extention "+ extention+bl+
		        "NatureOfNumber "+ natureOfNumber+bl+
		        "NumberPlan "+ numberPlan+bl+
		       "addressDigits "+addressDigits+bl+
		       "countryCode "+countryCode;
	}
	/*public static void main(String[] args) {
		MscAddressCapV2 msc = new MscAddressCapV2();
		msc.setAddressDigits("0123456789");
		msc.setExtention(ExtentionCapV2Enum.NO_EXTENTION);
		msc.setNatureOfNumber(NatureOfNumberCapV2Enum.INTERNATIONAL_NUMBER);
		msc.setNumberPlan(NumberPlanCapV2Enum.ISDN_TELEPHONY_NUMBERING);
		
		byte[] bytes = encode(msc);
		byte[] expectedBytes = CapV2Functions.hexStringToByteArray("911032547698");
		
		System.out.println(Arrays.equals(bytes,expectedBytes));
		
		MscAddressCapV2 msc2 = decode(bytes);
		System.out.println(msc.addressDigits.equals(msc2.addressDigits));
		System.out.println(msc.countryCode+" "+(msc2.countryCode));
		System.out.println(msc.natureOfNumber.equals(msc2.natureOfNumber));
		System.out.println(msc.numberPlan.equals(msc2.numberPlan));
		
		System.out.println(msc2.toString());
	}*/
	 
}
