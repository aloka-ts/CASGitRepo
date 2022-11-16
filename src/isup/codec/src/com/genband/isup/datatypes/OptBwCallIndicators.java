package com.genband.isup.datatypes;

import org.apache.log4j.Logger;

import com.genband.isup.enumdata.CallDiversionIndEnum;
import com.genband.isup.enumdata.InbandInfoIndEnum;
import com.genband.isup.enumdata.MLPPUserIndEnum;
import com.genband.isup.enumdata.SimpleSegmentationIndEnum;
import com.genband.isup.exceptions.InvalidInputException;
import com.genband.isup.util.Util;


/**
 * Used for encoding and decoding of BackwardCallIndicators
 * @author vgoel
 *
 */

public class OptBwCallIndicators {

	/**
	 * @see InbandInfoIndEnum
	 */
	InbandInfoIndEnum inbandInfoIndEnum;
	
	/**
	 * @see CallDiversionIndEnum
	 */
	CallDiversionIndEnum callDiversionIndEnum;
	
	/**
	 * @see SimpleSegmentationIndEnum
	 */
	SimpleSegmentationIndEnum simpleSegmentationIndEnum;
		
	/**
	 * @see MLPPUserIndEnum
	 */
	MLPPUserIndEnum mlppUserIndEnum;	
			
	private static Logger logger = Logger.getLogger(OptBwCallIndicators.class);

	/**
	 * This function will encode optional backward call indicator
	 * @param inbandInfoIndEnum
	 * @param callDiversionIndEnum
	 * @param simpleSegmentationIndEnum
	 * @param mlppUserIndEnum
	 * @return byte[]
	 * @throws InvalidInputException
	 */
	public static byte[] encodeOptBwCallInd(InbandInfoIndEnum inbandInfoIndEnum, CallDiversionIndEnum callDiversionIndEnum, SimpleSegmentationIndEnum simpleSegmentationIndEnum, 
			MLPPUserIndEnum mlppUserIndEnum) throws InvalidInputException
	{	
		logger.info("encodeOptBwCallInd:Enter");

		if(inbandInfoIndEnum == null){
			logger.error("encodeOptBwCallInd: InvalidInputException(inbandInfoIndEnum is null )");
			throw new InvalidInputException("inbandInfoIndEnum is null");
		}
		if(callDiversionIndEnum == null){
			logger.error("encodeOptBwCallInd: InvalidInputException(callDiversionIndEnum is null )");
			throw new InvalidInputException("callDiversionIndEnum is null");
		}
		if(simpleSegmentationIndEnum == null){
			logger.error("encodeOptBwCallInd: InvalidInputException(simpleSegmentationIndEnum is null )");
			throw new InvalidInputException("simpleSegmentationIndEnum is null");
		}
		if(mlppUserIndEnum == null){
			logger.error("encodeOptBwCallInd: InvalidInputException(mlppUserIndEnum is null )");
			throw new InvalidInputException("mlppUserIndEnum is null");
		}
		
		byte[] data = new byte[1];	
				
		data[0] = (byte) (mlppUserIndEnum.getCode() << 3 | simpleSegmentationIndEnum.getCode() << 2 | callDiversionIndEnum.getCode() << 1 | inbandInfoIndEnum.getCode());		
		
		if(logger.isDebugEnabled())
			logger.debug("encodeOptBwCallInd:Encoded OptBwCallInd: " + Util.formatBytes(data));
		logger.info("encodeOptBwCallInd:Exit");
		
		return data;
	}
	
	/**
	 * This function will decode optional backward call indicators
	 * @param data
	 * @return decoded data String
	 * @throws InvalidInputException 
	 */
	public static OptBwCallIndicators decodeOptBwCallInd(byte[] data) throws InvalidInputException
	{
		logger.info("decodeOptBwCallInd:Enter");
		if(logger.isDebugEnabled())
			logger.debug("decodeOptBwCallInd: Input--> data:" + Util.formatBytes(data));
		if(data == null){
			logger.error("decodeOptBwCallInd: InvalidInputException(data is null)");
			throw new InvalidInputException("data is null");
		}
		
		OptBwCallIndicators bwCallInd = new OptBwCallIndicators();
		
		bwCallInd.inbandInfoIndEnum = InbandInfoIndEnum.fromInt(data[0] & 0x1);
		bwCallInd.callDiversionIndEnum = CallDiversionIndEnum.fromInt((data[0] >> 1 ) & 0x1);
		bwCallInd.simpleSegmentationIndEnum = SimpleSegmentationIndEnum.fromInt((data[0] >> 2 ) & 0x1);
		bwCallInd.mlppUserIndEnum = MLPPUserIndEnum.fromInt((data[0] >> 3 ) & 0x1);
		
		if(logger.isDebugEnabled())
			logger.debug("decodeOptBwCallInd: Output<--" + bwCallInd.toString());
		logger.info("decodeOptBwCallInd:Exit");
		
		return bwCallInd ;
	}
	

	public InbandInfoIndEnum getInbandInfoIndEnum() {
		return inbandInfoIndEnum;
	}

	public void setInbandInfoIndEnum(InbandInfoIndEnum inbandInfoIndEnum) {
		this.inbandInfoIndEnum = inbandInfoIndEnum;
	}

	public CallDiversionIndEnum getCallDiversionIndEnum() {
		return callDiversionIndEnum;
	}

	public void setCallDiversionIndEnum(CallDiversionIndEnum callDiversionIndEnum) {
		this.callDiversionIndEnum = callDiversionIndEnum;
	}

	public SimpleSegmentationIndEnum getSimpleSegmentationIndEnum() {
		return simpleSegmentationIndEnum;
	}

	public void setSimpleSegmentationIndEnum(
			SimpleSegmentationIndEnum simpleSegmentationIndEnum) {
		this.simpleSegmentationIndEnum = simpleSegmentationIndEnum;
	}

	public MLPPUserIndEnum getMlppUserIndEnum() {
		return mlppUserIndEnum;
	}

	public void setMlppUserIndEnum(MLPPUserIndEnum mlppUserIndEnum) {
		this.mlppUserIndEnum = mlppUserIndEnum;
	}

	public String toString(){
		
		String obj = "inbandInfoIndEnum:"+ inbandInfoIndEnum + " ,callDiversionIndEnum:" + callDiversionIndEnum
		+ " ,simpleSegmentationIndEnum:" + simpleSegmentationIndEnum + " ,mlppUserIndEnum:" + mlppUserIndEnum;
		return obj ;
	}
}
