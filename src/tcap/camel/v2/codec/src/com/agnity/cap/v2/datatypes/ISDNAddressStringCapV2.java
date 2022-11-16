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

package com.agnity.cap.v2.datatypes;

import java.util.Arrays;

import org.apache.log4j.Logger;

import com.agnity.cap.v2.datatypes.enumType.ExtentionCapV2Enum;
import com.agnity.cap.v2.datatypes.enumType.NatureOfNumberCapV2Enum;
import com.agnity.cap.v2.datatypes.enumType.NumberPlanCapV2Enum;
import com.agnity.cap.v2.exceptions.InvalidInputException;
import com.agnity.cap.v2.util.CapFunctions;
/**
 * 
 * @author rnarayan
 * mscAddress non-asn decoding & encoding
 */
public class ISDNAddressStringCapV2 {

	 private ExtentionCapV2Enum extention;
	 private NatureOfNumberCapV2Enum natureOfNumber;
	 private NumberPlanCapV2Enum numberPlan;
	 private String addressDigits;
	 private int countryCode;
	 private static Logger logger = Logger.getLogger(ISDNAddressStringCapV2.class);
	 
	 /**
	  * 
	  * @return ExtentionCapV2Enum
	  */
	 public ExtentionCapV2Enum getExtention() {
		return extention;
	}

	/**
	 * 
	 * @return NatureOfNumberCapV2Enum
	 */
	public NatureOfNumberCapV2Enum getNatureOfNumber() {
		return natureOfNumber;
	}
    
	/**
	 * 
	 * @return NumberPlanCapV2Enum
	 */
	public NumberPlanCapV2Enum getNumberPlan() {
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
	public void setExtention(ExtentionCapV2Enum extention) {
		this.extention = extention;
	}

	/**
	 * 
	 * @param natureOfNumber
	 */
	public void setNatureOfNumber(NatureOfNumberCapV2Enum natureOfNumber) {
		this.natureOfNumber = natureOfNumber;
	}

	/**
	 * 
	 * @param numberPlan
	 */
	public void setNumberPlan(NumberPlanCapV2Enum numberPlan) {
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
	public static ISDNAddressStringCapV2 decode(byte[] bytes)  throws InvalidInputException{
		ISDNAddressStringCapV2 msc = null;
			 msc = new ISDNAddressStringCapV2();
			 msc.extention = ExtentionCapV2Enum.getValue((bytes[0]>>7)&0x01);
			 msc.natureOfNumber = NatureOfNumberCapV2Enum.getValue((bytes[0]&0x7F)>>4);
			 msc.numberPlan = NumberPlanCapV2Enum.getValue(bytes[0]&0x0F);
			 msc.addressDigits = CapFunctions.decodeNumber(bytes,1);
			 msc.countryCode = Integer.parseInt(CapFunctions.decodeNumber(new byte[]{bytes[0],bytes[1]},1));
			
			 if(logger.isDebugEnabled()){
				 logger.debug("mscAddress non-asn successfully decoded.");
			 }

		 return msc;
	}
	
	/**
	 * encode non-asn MscAddressCapV2 object into byte array of mscAddress
	 * @param ISDNAddressStringCapV2 object
	 * @return byteArray of mscAddress
	 */
	public static byte[] encode(ISDNAddressStringCapV2 msc)  throws InvalidInputException{
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
