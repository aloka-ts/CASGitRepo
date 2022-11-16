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

import com.agnity.ain.enumdata.CallTreatIndicatorEnum;
import com.agnity.ain.enumdata.NumToOutpulseEnum;
import com.agnity.ain.exceptions.InvalidInputException;

/**
 * @author nishantsharma
 *
 */
public class TrunkGroup extends AddressSignal
{
	private static Logger logger = Logger.getLogger(TrunkGroup.class);
	
	private CallTreatIndicatorEnum callTreatIndicator;
	
	private NumToOutpulseEnum numToOutpulse;
	
	private int sfg;
	
	String routeIndex;
	
	/**
	 * This method encode the Alternate trunk group
	 * This parameter is used in Analyzed Route Operation
	 * @param callTreatIndicator
	 * @param numToOutpulse
	 * @param sfg
	 * @param routeIndex
	 * @return a byte array
	 * @throws InvalidInputException
	 */
	public static byte[] encodeAlternateTrunkGrp(CallTreatIndicatorEnum callTreatIndicator ,NumToOutpulseEnum numToOutpulse,int sfg,String routeIndex) throws InvalidInputException
	{	
		int i=0;
		int noOutPluse;
		int callTrtInd;
		if(numToOutpulse==null){
			if (logger.isInfoEnabled()) 
			{
				logger.info("encodeAlternateTrunkGrp:Assining default value spare of numToOutpulse");
			}
			noOutPluse = 0;
		}
		else{
			noOutPluse=numToOutpulse.getCode();
		}
		if(callTreatIndicator==null){
			if (logger.isInfoEnabled()){
				logger.info("encodeAlternateTrunkGrp:Assining default value spare of numToOutpulse");
			}
			callTrtInd = 0;
		}
		else{
			callTrtInd=callTreatIndicator.getCode();
		}
		byte[] routeIndexByte=AddressSignal.encodeAdrsSignal(routeIndex);
		int seqLength=routeIndexByte.length+1;
		byte[] alternateTrunkByte=new byte[seqLength];
		alternateTrunkByte[i++]=(byte) ((noOutPluse << 7) | (sfg << 6) | (callTrtInd));
		for (int j = 0; j < routeIndexByte.length; j++) {
			alternateTrunkByte[i++] = routeIndexByte[j];
		}
		return alternateTrunkByte;
		
	}
	/**
	 * This method decode the Alternate trunk group
	 * @param data
	 * @return	AlternateTrunkGrp object
	 * @throws InvalidInputException
	 */
	public static TrunkGroup decodeAlternateTrunkGrp(byte[] data) throws InvalidInputException{
		
		TrunkGroup alternateTrunkGrp=new TrunkGroup();
		int numToOutpulse=data[0]>>7 & 0x1;
		alternateTrunkGrp.numToOutpulse=NumToOutpulseEnum.fromInt(numToOutpulse);
		alternateTrunkGrp.sfg=data[0]>>6 & 0x1;
		int callTreatIndicator=data[0] & 0x3F;
		alternateTrunkGrp.callTreatIndicator=CallTreatIndicatorEnum.fromInt(callTreatIndicator);
		alternateTrunkGrp.routeIndex=AddressSignal.decodeAdrsSignal(data, 1, 0);
		return alternateTrunkGrp;
	}
	
	public int getSfg() {
		return sfg;
	}

	public void setSfg(int sfg) {
		this.sfg = sfg;
	}

	public String getRouteIndex() {
		return routeIndex;
	}

	public void setRouteIndex(String routeIndex) {
		this.routeIndex = routeIndex;
	}

	public CallTreatIndicatorEnum getCallTreatIndicator() {
		return callTreatIndicator;
	}

	public void setCallTreatIndicator(CallTreatIndicatorEnum callTreatIndicator) {
		this.callTreatIndicator = callTreatIndicator;
	}

	public NumToOutpulseEnum getNumToOutpulse() {
		return numToOutpulse;
	}

	public void setNumToOutpulse(NumToOutpulseEnum numToOutpulse) {
		this.numToOutpulse = numToOutpulse;
	}
	
	
}
