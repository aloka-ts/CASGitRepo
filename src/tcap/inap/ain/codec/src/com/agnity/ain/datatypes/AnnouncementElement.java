/*******************************************************************************
* Copyright (c) 2020 Agnity, Inc. All rights reserved.
*
* This is proprietary source code of Agnity, Inc.
*
* Agnity, Inc. retains all intellectual property rights associated
* with this source code. Use is subject to license terms.
*
* This source code contains trade secrets owned by Agnity, Inc.
* Confidentiality of this computer program must be maintained at
* all times, unless explicitly authorized by Agnity, Inc.
*******************************************************************************/
package com.agnity.ain.datatypes;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.agnity.ain.exceptions.InvalidInputException;
import com.agnity.ain.util.Util;

/**
 * this class is use for encoding and decoding of
 * AnnounceElement as per the GR1129 standard.
 * 
 * @author stiwari
 *
 */
		/*AnnouncementBlock ::= SEQUENCE {
		[1] IMPLICIT UninterAnnounceBlock OPTIONAL,
		[2] IMPLICIT InterAnnounceBlock OPTIONAL,
		[3] IMPLICIT IPResourceMeasure OPTIONAL,
		[4] IMPLICIT IPStayOnLine OPTIONAL
		}
		
		
		|		H | G | F | E | D | C | B | A |
		|		Announcement ID (most significant)
		|		Announcement ID (least significant)
		|		NumberOfInfoDigits
		|		2nd Info Digit | 1st Info Digit
		|			*
		|			*
		|			*
		|			*
		|			*
		|			*
		|		Nth Info Digit | N-1st Info Digit
		*/

public class AnnouncementElement {
	private static final char hexcodes[] = { '0', '1', '2', '3', '4', '5', '6',

			'7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
	private int NumberOfInfoDigits;
	private int announcementId;
	private int infodigit;
	
				
	private static Logger logger = Logger.getLogger(AnnouncementElement.class);

	/**
	 * this method is use for encoding of AnnounceElement
	 * as per the standard GR1129.
	 * @param announcementId
	 * @param NumberOfInfoDigits
	 * @param infodigit
	 * @return
	 * @throws InvalidInputException
	 */
	public static byte[] encodeAnnouncementElement(int announcementId, int NumberOfInfoDigits, int infodigit) throws InvalidInputException{
		//defining length of the byte[]
		byte[] anounceElement = new byte[3+((NumberOfInfoDigits+1)/2)];
		int cursor =0;
		
		if(logger.isDebugEnabled())
		{
			logger.debug("Enter: encodeAnnouncementElement--> addrSignal:" + announcementId);
		}
		
		//Implementation 
		if(NumberOfInfoDigits >255 | NumberOfInfoDigits<0){
			logger.error("encodeAnnouncementElement: InvalidInputException(announcementId is null or blank)");
			throw new InvalidInputException("announcementId is null or blank");
		}
		


		/**
		*
		* encoding announcementId
		*
		*/
		String[] hexval = convertHexRepersentaion(Integer.toHexString(announcementId));
		if(hexval.length==1){
			logger.info("adding custom 1st byte as ::0x00");
			anounceElement[cursor++] =(byte)0x00;
		}
		for (int i = 0; i < hexval.length; i++) {
		anounceElement[cursor++] =(byte)(Long.decode(hexval[i]) & 0xFF);
		}

		/**
		* encoding numOfInfoDigit
		*/
		String[] hexValnumOfInfoDi= convertHexRepersentaion(Integer.toHexString(NumberOfInfoDigits));
		for (int i = 0; i < hexValnumOfInfoDi.length; i++) {
		anounceElement[cursor++] =(byte)(Long.decode(hexValnumOfInfoDi[i]) & 0xFF);
		}

		/**
		* encoding infoDigit
		*/
		if(NumberOfInfoDigits!=0){
			byte[] retArrDigit = convertIntoBCD(infodigit);

			for (int i = 0; i < retArrDigit.length; i++) {
				anounceElement[cursor++] =retArrDigit[i];
			}
		}
		
	
		if(logger.isDebugEnabled())
		{
			logger.debug("Exit: encodeAnnouncementElement<-- byte[]:" + Util.formatBytes(anounceElement));
		}
		
		return anounceElement;
	}
	
	/**
	 * method use for decoding of AnnounceElement
	 * as per the standard GR1129
	 * @param rxedAnnBuf
	 * @return
	 * @throws InvalidInputException 
	 */
	public static AnnouncementElement decodeAnnouncementElement(byte[] rxedAnnBuf) throws InvalidInputException{
		
		if(logger.isDebugEnabled())
		{
			logger.debug("Enter: decodeAnnouncementElement--> data:" + Util.formatBytes(rxedAnnBuf));
		}
		
		AnnouncementElement announcElement = new AnnouncementElement();
		//Implementation 
		
		//decoding announcement id
		byte [] announcementId = new byte[2];
		for (int i = 0; i < announcementId.length; i++) {
			announcementId[i] = rxedAnnBuf[i];
		}
		int annouId = (int)ByteBuffer.wrap(announcementId).getShort();
		announcElement.setAnnouncementId(annouId);
		
		//decoding NumberOfInfoDigits
		byte[] noOfDigi = new byte[1];
		noOfDigi[0] = rxedAnnBuf[2];
		int noOfDigit = (int)ByteBuffer.wrap(noOfDigi).get();
		announcElement.setNumberOfInfoDigits(noOfDigit);
		//decoding infoDigit
		
		/**
		 * first three byte for announce id and no of digit
		 */
		byte[] infoDigitbuf = new byte[rxedAnnBuf.length-3];
		for (int i = 3, j=0; i < rxedAnnBuf.length; i++,j++) {
			infoDigitbuf[j] =rxedAnnBuf[i];
		}
		
		String infoDigit = decodefrmBCD(infoDigitbuf);
		announcElement.setInfodigit(Integer.parseInt(infoDigit));
		
		if(logger.isDebugEnabled())
		{
			logger.debug("Exit: decodeAnnouncementElement-->" );
		}
		return announcElement;
		
	}

    /**
     * this method will convert inserted string into
     * BCD format.
     * @param infodigit
     * @return
     */
    
    private static byte[] convertIntoBCD(int digit){
    	String infodigit = Integer.toString(digit);
		int len = infodigit.length();
		if(len%2!=0){
			infodigit= 0+infodigit;
		}
		int size = ( len + 1) / 2;
		byte[] out = new byte[size];
		for (int i = 0, j = 0; i < len; i += 2, j++) 
		{
			byte b1 = (byte) (infodigit.charAt(i) - '0');
			byte b2 = 0;
			if ((i + 1) < len) 
			{
				b2 = (byte) (infodigit.charAt(i + 1) - '0');
			}
			out[j] = (byte) ((b2 << 4) | b1);
		}
		return out;
    }
    
    
    /**
     * method use to convert into hex format
     * e.g. input "FAE"
     * output= 0x0F 0xAE
     * @param input
     * @return
     */
    public static String[] convertHexRepersentaion(String input) {
    	if(input.length()%2!=0) {
    	input =0+input;
    	}
    	char []output =new char[5*(input.length()/2)];
    	int top = 0;
    	int temp = 0;
    	for (int i = 0; i < input.length()/2; i++) {
    	output[top++ ]= '0';

    	output[top++ ]= 'x';

    	output[top++ ]= input.charAt(temp++);

    	output[top++ ]= input.charAt(temp++);

    	output[top++ ]= ' ';
    	}
    	String tmpStr = new String(output);
    	String[] hexString = tmpStr.split(" ");
    	return hexString;
    	
    }
    
    /**
     * decode BCD representation.
     * @param data
     * @return
     * @throws InvalidInputException
     */
	public static String decodefrmBCD(byte[] data) throws InvalidInputException
	{
		if(logger.isDebugEnabled())
		{
			logger.debug("Enter: decodefrmBCD:Input--> data:" + Util.formatBytes(data));
		}
		if(data == null)
		{
			logger.error("decodeAdrsSignal: InvalidInputException(data is null)");
			throw new InvalidInputException("data is null");
		}
		int len = data.length ;
		char output[] = new char[2 * len];
		int top = 0;
		for (int i = 0; i < len; i++) 
		{
			output[top++] = hexcodes[data[i] & 0xf];
			output[top++] = hexcodes[(data[i] >> 4) & 0xf];
		}
		String tmpStr = new String(output);
		tmpStr = tmpStr.substring(0, tmpStr.length()) ;
		if(logger.isDebugEnabled())
		{
			logger.debug("Exit: decodefrmBCD:Output<-- adrssignal:" + tmpStr);
		}
		return tmpStr;
	}

	public int getNumberOfInfoDigits() {
		return NumberOfInfoDigits;
	}

	public void setNumberOfInfoDigits(int numberOfInfoDigits) {
		NumberOfInfoDigits = numberOfInfoDigits;
	}

	public int getAnnouncementId() {
		return announcementId;
	}

	public void setAnnouncementId(int announcementId) {
		this.announcementId = announcementId;
	}

	public int getInfodigit() {
		return infodigit;
	}

	public void setInfodigit(int infodigit) {
		this.infodigit = infodigit;
	} 
    
    
}
