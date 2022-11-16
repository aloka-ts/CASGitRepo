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

import com.agnity.cap.v3.datatypes.enumType.CodingStandardCapV3Enum;
import com.agnity.cap.v3.datatypes.enumType.ExtentionIndicatorCapV3Enum;
import com.agnity.cap.v3.datatypes.enumType.HighLayerCharacteristicsIdentCapV3Enum;
import com.agnity.cap.v3.datatypes.enumType.InterpretationCapV3Enum;
import com.agnity.cap.v3.datatypes.enumType.PresentationMethodOfProtocolProfileCapV3Enum;
import com.agnity.cap.v3.exceptions.InvalidInputException;
import com.agnity.cap.v3.util.CapFunctions;
/**
 * ref: T-REC-Q.931-4.5.17
 * @author rnarayan
 * HighLayercompatibility non-asn decoding & encoading
 */
public class HighLayerCompatibilityCapV3 {
    
	private ExtentionIndicatorCapV3Enum extInd1;
	private CodingStandardCapV3Enum coadingStd;
	private InterpretationCapV3Enum interpretation;
	private PresentationMethodOfProtocolProfileCapV3Enum preMethOfProtocolProfile;
	private ExtentionIndicatorCapV3Enum extInd2;
	private HighLayerCharacteristicsIdentCapV3Enum highLayCharIden;
	private static Logger logger = Logger.getLogger(HighLayerCompatibilityCapV3.class);
	
	/**
	 * 
	 * @return CoadingStandardCapV2Enum
	 */
	public CodingStandardCapV3Enum getCoadingStd() {
		return coadingStd;
	}
	
	/**
	 * 
	 * @param coadingStd
	 */
	public void setCoadingStd(CodingStandardCapV3Enum coadingStd) {
		this.coadingStd = coadingStd;
	}
	
	/**
	 * 
	 * @return InterpretationCapV2Enum
	 */
	public InterpretationCapV3Enum getInterpretation() {
		return interpretation;
	}
	
	/**
	 * 
	 * @param interpretation
	 */
	public void setInterpretation(InterpretationCapV3Enum interpretation) {
		this.interpretation = interpretation;
	}
	
	/**
	 * 
	 * @return ExtentionIndicatorCapV2Enum
	 */
	public ExtentionIndicatorCapV3Enum getExtInd1() {
		return extInd1;
	}
	
	/**
	 * 
	 * @param extInd1
	 */
	public void setExtInd1(ExtentionIndicatorCapV3Enum extInd1) {
		this.extInd1 = extInd1;
	}
	
	/**
	 * 
	 * @return ExtentionIndicatorCapV2Enum
	 */
	public ExtentionIndicatorCapV3Enum getExtInd2() {
		return extInd2;
	}
	
	/**
	 * 
	 * @param extInd2
	 */
	public void setExtInd2(ExtentionIndicatorCapV3Enum extInd2) {
		this.extInd2 = extInd2;
	}
	
	/**
	 * 
	 * @return PresentationMethodOfProtocolProfileCapV2Enum
	 */
	public PresentationMethodOfProtocolProfileCapV3Enum getPreMethOfProtocolProfile() {
		return preMethOfProtocolProfile;
	}
	
	/**
	 * 
	 * @param preMethOfProtocolProfile
	 */
	public void setPreMethOfProtocolProfile(
			PresentationMethodOfProtocolProfileCapV3Enum preMethOfProtocolProfile) {
		this.preMethOfProtocolProfile = preMethOfProtocolProfile;
	}
	
	/**
	 * 
	 * @return HighLayerCharacteristicsIdentCapV2Enum
	 */
	public HighLayerCharacteristicsIdentCapV3Enum getHighLayCharIden() {
		return highLayCharIden;
	}
	
	/**
	 * 
	 * @param highLayCharIden
	 */
	public void setHighLayCharIden(
			HighLayerCharacteristicsIdentCapV3Enum highLayCharIden) {
		this.highLayCharIden = highLayCharIden;
	}
	
	/**
	 * decode non-asn and return HighLayerCompatibilityCapV2 object with 
	 * non-asn parameters
	 * @param byte array of HighLayerCompatibility
	 * @return HighLayerCompatibilityCapV2 object
	 */
	public static HighLayerCompatibilityCapV3 decode(byte[] bytes)  throws InvalidInputException{
		HighLayerCompatibilityCapV3 high = null;
			high = new HighLayerCompatibilityCapV3();
			high.extInd1 = ExtentionIndicatorCapV3Enum.getValue((bytes[0]>>7)&0x01);
			high.coadingStd = CodingStandardCapV3Enum.getValue(((bytes[0]&0x7f)>>5)&0x03);
			high.interpretation = InterpretationCapV3Enum.getValue(((bytes[0]&0x1f)>>2)&0x07);
			high.preMethOfProtocolProfile = PresentationMethodOfProtocolProfileCapV3Enum.getValue((bytes[0]&0x03)&0x03);
			high.extInd2 = ExtentionIndicatorCapV3Enum.getValue((bytes[1]>>7)&0x01);
			high.highLayCharIden = HighLayerCharacteristicsIdentCapV3Enum.getValue(bytes[1]&0x07f);
	         
			if(logger.isDebugEnabled()){
				logger.debug("HighLayerCompatibility non-asn decoded successfully");
			}
    		   
		return high;
	}
	
	/**
	 * encode HighLayerCompatibilityCapV2 non-asn parameters into HighLayerCompatibility
	 * byte array
	 * @param HighLayerCompatibilityCapV3 object with non-asn values
	 * @return byte array 
	 */
	public static byte[] encode(HighLayerCompatibilityCapV3 high)  throws InvalidInputException{
		byte[] bytes = null;
			int extInd1 = high.extInd1.getCode();
			int coadingStd = high.coadingStd.getCode();
			int inter = high.interpretation.getCode();
			int preMeth = high.preMethOfProtocolProfile.getCode();
			int extInd2 = high.extInd2.getCode();
			int highLayCharIden = high.highLayCharIden.getCode();
			
			byte b0 = (byte)((((((extInd1<<2)+coadingStd)<<3)+inter)<<2)+preMeth);
			byte b1 = (byte)((extInd2<<7)+highLayCharIden);
			bytes = new byte[]{b0,b1};
			 
			if(logger.isDebugEnabled()){
				logger.debug("HighLayerCompatibility byte array encoded successfully");
				logger.debug("encoded HighLayerCompatibility byte array length::"+bytes.length);
			 }
		
		return bytes;
	}
	
	@Override
	public String toString() {
		String bl = "\n";
		return "ExtentionIndicator " +extInd1+bl+
		       "CodingStandard " +coadingStd+bl+
		       "InterpretationCapV2Enum "+interpretation+bl+
		        "PresentationMethodOfProtocolProfile "+ preMethOfProtocolProfile+bl+
	            "ExtentionIndicator "+extInd2+bl+
		        "HighLayerCharacteristicsIdent "+ highLayCharIden;
	}
	/*public static void main(String[] args) {
		HighLayerCompatibilityCapV2 hi = new HighLayerCompatibilityCapV2();
		hi.setCoadingStd(CodingStandardCapV2Enum.ITU_T_COADING);
		hi.setExtInd1(ExtentionIndicatorCapV2Enum.LAST_OCTET);
		hi.setExtInd2(ExtentionIndicatorCapV2Enum.LAST_OCTET);
		hi.setHighLayCharIden(HighLayerCharacteristicsIdentCapV2Enum.TELEPHONY);
		hi.setInterpretation(InterpretationCapV2Enum.FIRST_HIGH_LAYER_CHAR_IDEN);
		hi.setPreMethOfProtocolProfile(PresentationMethodOfProtocolProfileCapV2Enum.HIGH_LAYER_PROTOCOL_PROFILE);
		
		byte[] bytes = encode(hi);
		byte[] expectedBytes = CapV2Functions.hexStringToByteArray("9181");
		
		System.out.println(Arrays.equals(bytes,expectedBytes));
		
		HighLayerCompatibilityCapV2 h2 = decode(bytes);
		System.out.println(h2.getCoadingStd());
		System.out.println(h2.getExtInd1());
		System.out.println(h2.getExtInd2());
        System.out.println(h2.getHighLayCharIden());
        System.out.println(h2.getInterpretation());
        System.out.println(h2.getPreMethOfProtocolProfile());
		
		System.out.println(h2);
        
        
	}*/
}
