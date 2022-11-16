/*******************************************************************************
 *   Copyright (c) 2014 Agnity, Inc. All rights reserved.
 *   
 *   This is proprietary source code of Agnity, Inc. 
 *   
 *   Agnity, Inc. retains all intellectual property rights associated 
 *   with this source code. Use is subject to license terms.
 *   
 *   This source code contains trade secrets owned by Agnity, Inc.
 *   Confidentiality of this computer program must be maintained at 
 *   all times, unless explicitly authorized by Agnity, Inc.
 *******************************************************************************/
/**
 * 
 */
package com.agnity.ain.datatypes;

import org.apache.log4j.Logger;

import com.agnity.ain.enumdata.ACGControlTypeEnum;
import com.agnity.ain.enumdata.ACGEncounteredEnum;
import com.agnity.ain.enumdata.ACGSmsControlIndEnum;
import com.agnity.ain.enumdata.ACGSpcControlIndEnum;
import com.agnity.ain.exceptions.InvalidInputException;

/**
 * @author nishantsharma
 *
 */
public class ACGEncounteredNonAsn 
{
	private static Logger logger = Logger.getLogger(ACGEncounteredNonAsn.class);

	private ACGEncounteredEnum acgEncounteredEnum;
	private ACGControlTypeEnum acgControlTypeEnum;
	private ACGSmsControlIndEnum acgSmsControlIndEnum;
	private ACGSpcControlIndEnum acgSpcControlIndEnum;

	/**
	 * This function will encode ACGEncountered 
	 * 
	 * ACGEncountered parameter is used in InfoCollected and InfoAnalyzed .
	 */
	public byte [] encodeACGEncounteredNonAsn(){
		byte [] data =new byte[1];
		int acgEncountered;
		int acgControlType;
		int acgSmsControlInd;
		int acgSpcControlInd;
		if(this.acgEncounteredEnum==null){
			if (logger.isInfoEnabled()){
				logger.info("encodeACGEncounteredNonAsn:Assining default value spare of numToOutpulse");
			}
			acgEncountered = 0;
		}
		else{
			acgEncountered=this.acgEncounteredEnum.getCode();
		}

		if(this.acgControlTypeEnum==null){
			if (logger.isInfoEnabled()){
				logger.info("encodeACGEncounteredNonAsn:Assining default value spare of numToOutpulse");
			}
			acgControlType = 0;
		}
		else{
			acgControlType=this.acgControlTypeEnum.getCode();
		}
		if(this.acgSmsControlIndEnum==null){
			if (logger.isInfoEnabled()){
				logger.info("encodeACGEncounteredNonAsn:Assining default value spare of numToOutpulse");
			}
			acgSmsControlInd = 0;
		}
		else{
			acgSmsControlInd=this.acgSmsControlIndEnum.getCode();
		}
		if(this.acgSpcControlIndEnum==null){
			if (logger.isInfoEnabled()){
				logger.info("encodeACGEncounteredNonAsn:Assining default value spare of numToOutpulse");
			}
			acgSpcControlInd = 0;
		}
		else{
			acgSpcControlInd=this.acgSpcControlIndEnum.getCode();
		}
		data[1]=(byte) (acgSpcControlInd << 7 |acgSmsControlInd << 6|acgControlType << 4|acgEncountered);
		return data;

	}
	/**

	 * This function will decode ACGEncountered.
	 * 
	 * ACGEncountered parameter is used in InfoCollected and InfoAnalyzed .

	 * @param data byte array

	 * @return object of ACGEncounteredNonAsn DataType

	 * @throws InvalidInputException 

	 */
	public ACGEncounteredNonAsn decodeACGEncounteredNonAsn(byte [] data) throws InvalidInputException{
		if (logger.isInfoEnabled()) {
			logger.info("decodeAinDigits:Enter");
		}
		if(data == null){
			logger.error("InvalidInputException(data is null)");
			throw new InvalidInputException("data is null");
		}
		int acgScpControl=(data[1] >> 7) & 0x1;
		this.acgSpcControlIndEnum=ACGSpcControlIndEnum.fromInt(acgScpControl);
		int acgSmsControl=(data[1] >> 6) & 0x1;
		this.acgSmsControlIndEnum=ACGSmsControlIndEnum.fromInt(acgSmsControl);
		int acgControlType=(data[1] >> 4) & 0x3;
		this.acgControlTypeEnum=ACGControlTypeEnum.fromInt(acgControlType);
		int acgEncountered=(data[1]) & 0xF;
		this.acgEncounteredEnum=ACGEncounteredEnum.fromInt(acgEncountered);
		return this;

	}
	public ACGEncounteredEnum getAcgEncounteredEnum() {
		return acgEncounteredEnum;
	}

	public void setAcgEncounteredEnum(ACGEncounteredEnum acgEncounteredEnum) {
		this.acgEncounteredEnum = acgEncounteredEnum;
	}

	public ACGControlTypeEnum getAcgControlTypeEnum() {
		return acgControlTypeEnum;
	}

	public void setAcgControlTypeEnum(ACGControlTypeEnum acgControlTypeEnum) {
		this.acgControlTypeEnum = acgControlTypeEnum;
	}

	public ACGSmsControlIndEnum getAcgSmsControlIndEnum() {
		return acgSmsControlIndEnum;
	}

	public void setAcgSmsControlIndEnum(ACGSmsControlIndEnum acgSmsControlIndEnum) {
		this.acgSmsControlIndEnum = acgSmsControlIndEnum;
	}

	public ACGSpcControlIndEnum getAcgSpcControlIndEnum() {
		return acgSpcControlIndEnum;
	}

	public void setAcgSpcControlIndEnum(ACGSpcControlIndEnum acgSpcControlIndEnum) {
		this.acgSpcControlIndEnum = acgSpcControlIndEnum;
	}

}
