/****
  Copyright (c) 2013 Agnity, Inc. All rights reserved.
  
  This is proprietary source code of Agnity, Inc. 
  
  Agnity, Inc. retains all intellectual property rights associated 
  with this source code. Use is subject to license terms.
  
  This source code contains trade secrets owned by Agnity, Inc.
  Confidentiality of this computer program must be maintained at 
  all times, unless explicitly authorized by Agnity, Inc.
****/
package com.agnity.cap.v2.datatypes;

import java.util.Arrays;

import org.apache.log4j.Logger;

import com.agnity.cap.v2.datatypes.enumType.ExtentionCapV2Enum;
import com.agnity.cap.v2.datatypes.enumType.NumberingPlanIdentificationCapV2Enum;
import com.agnity.cap.v2.datatypes.enumType.TypeOfNumberCapV2Enum;
import com.agnity.cap.v2.exceptions.InvalidInputException;
import com.agnity.cap.v2.util.CapFunctions;
/**
 * ref - GSM 04.08 
 * @author rnarayan
 * CalledPartyBCDNumber non-asn decoding encoding
 */
public class CalledPartyBCDNumberCapV2 {

	private ExtentionCapV2Enum extention;
	private TypeOfNumberCapV2Enum typeOfNumber;
	private NumberingPlanIdentificationCapV2Enum numberingPlanIdentification;
	
	//this parameter is NUMBER DIGIT
	private String bcdDigits;
	private static Logger logger = Logger.getLogger(CalledPartyBCDNumberCapV2.class);

	/**
	 * @return ExtentionCapV2Enum
	 */
	public ExtentionCapV2Enum getExtention() {
		return extention;
	}

   /**
    * @return TypeOfNumberCapV2Enum
    */
	public TypeOfNumberCapV2Enum getTypeOfNumber() {
		return typeOfNumber;
	}

    /**
     * @return NumberingPlanIdentificationCapV2Enum
     */
	public NumberingPlanIdentificationCapV2Enum getNumberingPlanIdentification() {
		return numberingPlanIdentification;
	}

    /**
     * @return String bcdDigits
     */
	public String getBcdDigits() {
		return bcdDigits;
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
     * @param typeOfNumber
     */
	public void setTypeOfNumber(TypeOfNumberCapV2Enum typeOfNumber) {
		this.typeOfNumber = typeOfNumber;
	}

    /**
     * 
     * @param numberingPlanIdentification
     */
	public void setNumberingPlanIdentification(
			NumberingPlanIdentificationCapV2Enum numberingPlanIdentification) {
		this.numberingPlanIdentification = numberingPlanIdentification;
	}
    
	
	/**
	 * 
	 * @param bcdDigits
	 */
	public void setBcdDigits(String bcdDigits) {
		this.bcdDigits = bcdDigits;
	}


    /**
     * Return CalledPartyBCDNumberCapV2 object by decoding of byte array 
     * passed as arg.
     * @param byte array for CalledPartyBCDNumber
     * @return object of CalledPartyBCDNumberCapV2 with non-asn decoded values
     */
	public static CalledPartyBCDNumberCapV2 decode(byte[] bytes)  throws InvalidInputException {
		    CalledPartyBCDNumberCapV2 bcd = null;
			bcd = new CalledPartyBCDNumberCapV2();
			bcd.extention = ExtentionCapV2Enum.getValue((bytes[0]>>7)&0x01);
			bcd.typeOfNumber = TypeOfNumberCapV2Enum.getValue((bytes[0]&0x7F)>>4);
			bcd.numberingPlanIdentification = NumberingPlanIdentificationCapV2Enum.getValue((bytes[0]&0x0F));
			bcd.bcdDigits = bcd.decodeBCDDigits(bytes);
			
			if(logger.isDebugEnabled()){
				logger.debug("CalledPartyBCDNumber decoded successfully");
			}
		return bcd;
	}

	/**
	 * Return hex String format of bcd digits acc std. 
	 * @param bytes array
	 * @return hex String 
	 * 
* ³ Number digits (octets 4, etc.) ³
³ Bits Number digit value ³
³ 4 3 2 1 or    ³
³ 8 7 6 5       ³
³ 0 0 0 0     0 ³
³ 0 0 0 1     1 ³
³ 0 0 1 0     2 ³
³ 0 0 1 1     3 ³
³ 0 1 0 0     4 ³
³ 0 1 0 1     5 ³
³ 0 1 1 0     6 ³
³ 0 1 1 1     7 ³
³ 1 0 0 0     8 ³
³ 1 0 0 1     9 ³
³ ³
³ 1 0 1 0     * ³
³ 1 0 1 1     # ³
³ 1 1 0 0     a ³
³ 1 1 0 1     b ³
³ 1 1 1 0     c ³
³ 1 1 1 1     used as an endmark in the case of an odd ³
³ number of number digits ³
	 */
	private String decodeBCDDigits(byte[] bytes) {
		 String[] chars = new String[]{"*","#","a","b","c","f"};
		 StringBuilder number = new StringBuilder();
		    int length = bytes.length;
			for(int i=1;i<length;i++){
				
				if((int)(bytes[i]&0x0f)<10){
					 number.append(Integer.toHexString((bytes[i]&0x0f)));	
				}else{
					 number.append(chars[(int)(bytes[i]&0x0f)-10]);
				}
				
				//1 1 1 1     used as an endmark in the case of an odd ³
				//³ number of number digits ³
			    if((((bytes[i]>>4)&0x0F)==0x0F) && (i==(length-1))){
				     // log here for break 0	
				      break;	
				    }
			    
			    if((int)((bytes[i]>>4)&0x0F)<10){
			 	   number.append(Integer.toHexString(((bytes[i]>>4)&0x0F)));
			    }else{
			    	 number.append(chars[(int)((bytes[i]>>4)&0x0F)-10]);
			    }
			    
			}
			return number.toString();
	}
	
	
	/**
	 * return encode byte array for CalledPartyBCDNumber
	 * @param  object of CalledPartyBCDNumberCapV2
	 * @return  byte array 
	 */
	public static byte[] encode(CalledPartyBCDNumberCapV2 cpn)  throws InvalidInputException{
		byte[] bytes = null;
			int ext = cpn.extention.getCode();
			int ton = cpn.typeOfNumber.getCode();
			int numPlanIden = cpn.numberingPlanIdentification.getCode();
			String bcdDigits = cpn.bcdDigits;
			
			byte b0 = (byte)((((ext<<3)+ton)<<4)+numPlanIden);
			
			if(bcdDigits.length()%2!=0){
				bcdDigits = bcdDigits+"f";
			}
			
			bcdDigits = CapFunctions.encodenNumber(bcdDigits);
			byte[] bcdDigitsBytes = CapFunctions.hexStringToByteArray(bcdDigits);
			int totalLength = bcdDigitsBytes.length+1; 
			bytes = new byte[totalLength];
			bytes[0]=b0;
			for(int i=0;i<(totalLength-1);i++){
				bytes[i+1] = bcdDigitsBytes[i];
			}
			if(logger.isDebugEnabled()){
				logger.debug("CalledPartyBCDNumber encoded successfully");
				logger.debug("encoded CalledPartyBCDNumber byte array length::"+bytes.length);
			}
			
		
		return bytes;
	}
	
	@Override
	public String toString() {
		String bl = "\n";
		return "Extention "+ extention+bl+
		       "TypeOfNumber "+typeOfNumber+bl+
		       "NumberingPlanIdentification "+numberingPlanIdentification+bl+
		       "bcdDigits "+bcdDigits;
	}
	
	//for test
	public static void main(String[] args) throws InvalidInputException {
		CalledPartyBCDNumberCapV2 cpn = new CalledPartyBCDNumberCapV2();
		cpn.setExtention(ExtentionCapV2Enum.NO_EXTENTION);
		cpn.setTypeOfNumber(TypeOfNumberCapV2Enum.NATIONAL_NO);
		cpn.setNumberingPlanIdentification(NumberingPlanIdentificationCapV2Enum.ISDN_TELEPHONY_NUMBERING);
		cpn.setBcdDigits("9999111322");
		byte[] bytes = encode(cpn);
	    byte[] expectedBytes = CapFunctions.hexStringToByteArray("a19999113122fa");
	    System.out.println("result:: "+Arrays.equals(bytes, expectedBytes));
	    cpn = CalledPartyBCDNumberCapV2.decode(expectedBytes);
	    System.out.println(cpn);
	} 
	
}
