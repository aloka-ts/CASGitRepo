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

import com.agnity.cap.v3.datatypes.IPSSPCapabilitiesCapV3;
import com.agnity.cap.v3.exceptions.InvalidInputException;
import com.agnity.cap.v3.util.CapFunctions;

/**
 * REF: ETSI TS 101 046 V7.1.0 (2000-07)
 * @author rnarayan
 *
-- Octet 1 Standard Part for CAP V.2
-- Bit Value Meaning
-- 0 0 IPRoutingAddress not supported
-- 1 IPRoutingAddress supported
-- 1 0 VoiceBack not supported
-- 1 VoiceBack supported
-- 2 0 VoiceInformation not supported, via speech recognition
-- 1 VoiceInformation supported, via speech recognition
-- 3 0 VoiceInformation not supported, via voice recognition
-- 1 VoiceInformation supported, via voice recognition
-- 4 0 Generation of voice announcements from Text not supported
-- 1 Generation of voice announcements from Text supported
-- 5 - Reserved
-- 6 - Reserved
-- 7 0 End of standard part
-- 1 This value is reserved in CAP V.2
--
-- Octets 2 to 4 Bilateral Part: Network operator / equipment vendor specific
 */
public class IPSSPCapabilitiesCapV3 {
	
	private boolean isIPRoutingAddressSupported;
	private boolean isVoiceBackSupported; 
	private boolean isVoiceInformationSupportedViaSpeech;
    private boolean isVoiceInformationSupportedViaVoice;
    private boolean isGenerationOfVoiceAnnouncementsFromTextSupported;
    
    private byte[] bilateralPart;
    
    private static Logger logger = Logger.getLogger(IPSSPCapabilitiesCapV3.class);

    /**
     * 
     * @return boolean, show IPRoutingAddress supported or Not
     */
	public boolean isIPRoutingAddressSupported() {
		return isIPRoutingAddressSupported;
	}

	/**
	 * set IPRoutingAddress supported or Not
	 * @param isIPRoutingAddressSupported
	 */
	public void setIPRoutingAddressSupported(boolean isIPRoutingAddressSupported) {
		this.isIPRoutingAddressSupported = isIPRoutingAddressSupported;
	}

	/**
	 * VoiceBack supported or not
	 * @return boolean
	 */
	public boolean isVoiceBackSupported() {
		return isVoiceBackSupported;
	}

	/**
	 * set VoiceBack supported or not
	 * @param isVoiceBackSupported
	 */
	public void setVoiceBackSupported(boolean isVoiceBackSupported) {
		this.isVoiceBackSupported = isVoiceBackSupported;
	}

	/**
	 * VoiceInformation supported, via speech recognition or not
	 * @return boolean
	 */
	public boolean isVoiceInformationSupportedViaSpeech() {
		return isVoiceInformationSupportedViaSpeech;
	}

	/**
	 * set VoiceInformation supported, via speech recognition
	 * @param isVoiceInformationSupportedViaSpeech
	 */
	public void setVoiceInformationSupportedViaSpeech(boolean isVoiceInformationSupportedViaSpeech) {
		this.isVoiceInformationSupportedViaSpeech = isVoiceInformationSupportedViaSpeech;
	}

	/**
	 * VoiceInformation supported, via voice recognition or not
	 * @return boolean
	 */
	public boolean isVoiceInformationSupportedViaVoice() {
		return isVoiceInformationSupportedViaVoice;
	}

	/**
	 * set VoiceInformation supported, via voice recognition
	 * @param isVoiceInformationSupportedViaVoice
	 */
	public void setVoiceInformationSupportedViaVoice(
			boolean isVoiceInformationSupportedViaVoice) {
		this.isVoiceInformationSupportedViaVoice = isVoiceInformationSupportedViaVoice;
	}

	/**
	 * Generation of voice announcements from Text supported or not
	 * @return boolean 
	 */
	public boolean isGenerationOfVoiceAnnouncementsFromTextSupported() {
		return isGenerationOfVoiceAnnouncementsFromTextSupported;
	}

	/**
	 * set Generation of voice announcements from Text supported
	 * @param isGenerationOfVoiceAnnouncementsFromTextSupported
	 */
	public void setGenerationOfVoiceAnnouncementsFromTextSupported(
			boolean isGenerationOfVoiceAnnouncementsFromTextSupported) {
		this.isGenerationOfVoiceAnnouncementsFromTextSupported = isGenerationOfVoiceAnnouncementsFromTextSupported;
	}

	/**
	 * @return bilateral part of byte array
	 */
	public byte[] getBilateralPart() {
		return bilateralPart;
	}

	/**
	 * set bilateralPart
	 * @param bilateralPart
	 */
	public void setBilateralPart(byte[] bilateralPart) {
		this.bilateralPart = bilateralPart;
	}
    
	/**
	 * decode IPSSPCapabilities non asn parameters 
	 * @param byte array of IPSSPCapabilities
	 * @return IPSSPCapabilitiesCapV2 object
	 */
    public static IPSSPCapabilitiesCapV3 decode(byte[] bytes)  throws InvalidInputException{
    	
        IPSSPCapabilitiesCapV3 ips = null;
        if(bytes!=null){
            	ips = new IPSSPCapabilitiesCapV3();
            	
            	//standard part
            	byte std = bytes[0];
            	ips.isIPRoutingAddressSupported = parseToBoolean((std>>7)&0x01);
            	ips.isVoiceBackSupported = parseToBoolean((std>>6)&0x01);
            	ips.isVoiceInformationSupportedViaSpeech = parseToBoolean((std>>5)& 0x01);
            	ips.isVoiceInformationSupportedViaVoice = parseToBoolean((std>>4)& 0x01);
            	ips.isGenerationOfVoiceAnnouncementsFromTextSupported = parseToBoolean((std>>3)& 0x01);
                
            	//bilateral part
            	int bytesLength = bytes.length;
            	if(bytesLength>1){
            		ips.bilateralPart= new byte[bytesLength-1];
            		for(int i=1;i<bytesLength;i++){
            			ips.bilateralPart[i-1]= bytes[i];
            		}
            	}
            	if(logger.isDebugEnabled()){
            		logger.debug("IPSSPCapabilities byte array decoded successfully");
            	}
        	
        }else{
        	if(logger.isDebugEnabled()){
        		logger.debug("IPSSPCapabilitiesCapV2 decode byte array is null.");
        	}
        }
        
    	return ips;
    	
    }
    
    /**
     * parse integer 1/0 to boolean true / false
     * @param integer 1/0
     * @return boolean
     */
    private static boolean parseToBoolean(int i){
    	if(i==1){
    		return true;
    	}else if(i==0){
    		return false;
    	}else{
    		logger.error("it not 0 or 1 its "+i+"not valid number return false");
    		return false;
    	}
    }
    
    /**
     * parse boolean to integer 1/0
     * @param boolean true/false
     * @return 1/0
     */
    private static int parseToInt(boolean b){
    	if(b){
    		return 1;
    	}
    	return 0;
    }
    
    /**
     * encode IPSSPCapabilitiesCapV2 object to IPSSPCapabilities byte array
     * @param IPSSPCapabilitiesCapV3 object with valid parameters
     * @return IPSSPCapabilities byte array
     */
    public static byte[] encode(IPSSPCapabilitiesCapV3 ips)  throws InvalidInputException{
    	byte[] bytes= null;
    		byte b1 = (byte) ((parseToInt(ips.isIPRoutingAddressSupported)<<7)+
    				          (parseToInt(ips.isVoiceBackSupported)<<6)+
    				          (parseToInt(ips.isVoiceInformationSupportedViaSpeech)<<5)+
    				          (parseToInt(ips.isVoiceInformationSupportedViaVoice)<<4)+
    				          (parseToInt(ips.isGenerationOfVoiceAnnouncementsFromTextSupported)<<3)
    		                  );
    		if(ips.bilateralPart!=null){
    			int bytesLen = ips.bilateralPart.length+1;
    			bytes = new byte[bytesLen];
    			bytes[0]=b1;
    			for(int i=1;i<bytesLen;i++){
    				bytes[i] = ips.bilateralPart[i-1];
    			}
    		}else{
    		    bytes = new byte[]{b1};	
    		}
    		if(logger.isDebugEnabled()){
        		logger.debug("IPSSPCapabilitiesCapV2 object encoded successfully in to byte array.");
        	}

    	return bytes;
    }

    @Override
    public String toString() {
    	String bl="\n";
    	return"isIPRoutingAddressSupported "+this.isIPRoutingAddressSupported+bl+
    	"isVoiceBackSupported "+this.isVoiceBackSupported+bl+
    	"isVoiceInformationSupportedViaSpeech "+this.isVoiceInformationSupportedViaSpeech+bl+
        "isVoiceInformationSupportedViaVoice "+this.isVoiceInformationSupportedViaVoice+bl+ 
        "isGenerationOfVoiceAnnouncementsFromTextSupported "+this.isGenerationOfVoiceAnnouncementsFromTextSupported
        ;

    }
    
/*   public static void main(String[] args) {
		String byteArrayString = "f832150f11";
		byte[] bytes = CapV2Functions.hexStringToByteArray(byteArrayString);
		IPSSPCapabilitiesCapV2 ips = IPSSPCapabilitiesCapV2.decode(bytes);
		byte[] encodedBytes = IPSSPCapabilitiesCapV2.encode(ips);
		System.out.println(Arrays.equals(bytes, encodedBytes));
		System.out.println(ips.toString());
	}*/
}
