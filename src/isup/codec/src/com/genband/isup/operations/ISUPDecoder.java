package com.genband.isup.operations;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * This class contains methods for decoding and parsing of ISUP messages
 * @author vgoel
 *
 */
public class ISUPDecoder
{
	private static Logger logger = Logger.getLogger(ISUPDecoder.class);
	
	/**
	 * This methods decode mandatory fixed parameters of ISUP messages based on the offset info.
	 * This message will return list of parsed byte[] buffer
	 * @param opBuffer
	 * @param offsetInfo
	 * @return LinkedList<byte[]>
	 * @throws Exception
	 */
	public static LinkedList<byte[]> decodeFixedParams(byte[] opBuffer, int[] offsetInfo)
	{
		logger.info("decodeFixedParams:Enter");
		
		LinkedList<byte[]> outList = new LinkedList<byte[]>();
		for(int i=0; i<offsetInfo.length-1; i++)
		{
			int bytesLength = offsetInfo[i+1]-offsetInfo[i];		//length is difference in consecutive offsets
			byte[] paramBytes = new byte[bytesLength];
			for(int j=offsetInfo[i], k=0; j<offsetInfo[i+1]; j++, k++){
				paramBytes[k] = opBuffer[j];
			}
			
			outList.add(paramBytes);
		}
		
		logger.info("decodeFixedParams:Exit");
		return outList;
	}
	
	/**
	 * This methods decode mandatory variable length parameters of ISUP messages based on the offset info.
	 * This message will return list of parsed byte[] buffer
	 * @param opBuffer
	 * @param offsetInfo
	 * @return LinkedList<byte[]>
	 */
	public static LinkedList<byte[]> decodeVariableParams(byte[] opBuffer, int[] offsetInfo)
	{
		logger.info("decodeVariableParams:Enter");
		
		LinkedList<byte[]> outList = new LinkedList<byte[]>();
		for(int i=0; i<offsetInfo.length; i++)
		{
			if(offsetInfo[i] != -1){
				int varOffset = (opBuffer[offsetInfo[i]]<0 ? 256+opBuffer[offsetInfo[i]] : opBuffer[offsetInfo[i]]);	// if offset is more than 127 (8 bits)
				int varLengthIndex = offsetInfo[i] + varOffset;		//length index is (value at index mentioned by offset + offset)
				int bytesLength = (opBuffer[varLengthIndex]<0 ? 256+opBuffer[varLengthIndex] : opBuffer[varLengthIndex]);			// if length is more than 127 (8 bits)
				byte[] paramBytes = new byte[bytesLength];
				for(int j=varLengthIndex+1, k=0; j<=varLengthIndex+bytesLength; j++, k++){
					paramBytes[k] = opBuffer[j];
				}
				
				outList.add(paramBytes);
			}
		}
		
		logger.info("decodeVariableParams:Exit");
		return outList;
	}
	
	/**
	 * This methods decodes IAM optional parameters of ISUP messages based on the offset info.
	 * This message will change in the incoming method object
	 * @param opBuffer
	 * @param offset
	 * @param iam
	 */
	/*public static void decodeIAMOptParams(byte[] opBuffer, int offset, IAMMessage iam)
	{
		logger.info("decodeIAMOptParams:Enter");

		if(offset != -1){
			int optOffset = (opBuffer[offset]<0 ? 256+opBuffer[offset] : opBuffer[offset]);	// if offset is more than 127 (8 bits)
			int optTypeIndex = offset + optOffset;
			
			if(optOffset != 0)
				while(true)
				{
					int optType = opBuffer[optTypeIndex];		//opt type is value at (value at index mentioned by offset + offset)
					int optLengthIndex = optTypeIndex+1;		
					int bytesLength = (opBuffer[optLengthIndex]<0 ? 256+opBuffer[optLengthIndex] : opBuffer[optLengthIndex]);			// if length is more than 127 (8 bits)
					
					byte[] paramBytes = new byte[bytesLength];
					for(int j=optLengthIndex+1, k=0; j<=optLengthIndex+bytesLength; j++, k++){						
						paramBytes[k] = opBuffer[j];
					}
					
					if(optType == ISUPConstants.CODE_CALLING_PARTY_NUM){
						logger.info("decodeIAMOptParams:decoding calling party num");				
						iam.setCallingPartyNumber(paramBytes);
					}
					else if(optType == ISUPConstants.CODE_PROP_DELAY_COUNTER){
						logger.info("decodeIAMOptParams:decoding propagation delay counter");				
						iam.setPropagationDelayCounter(paramBytes);
					}
					else if(opType == ISUPConstants.CODE_ADDITIONAL_PARTY_CAT){
						logger.info("decodeIAMOptParams:decoding Additional Party's Category ");				
						iam.setAdditionalPartyCat(paramBytes);
					}
					
					//calculate new optIndex
					if(opBuffer[optLengthIndex+bytesLength+1] == 0){		//0x00 is end of optional params
						break;
					}
					else{
						optTypeIndex = optLengthIndex+bytesLength+1;
					}		
				}
		}
		logger.info("decodeIAMOptParams:Exit");
	}	*/
	
	
	/**
	 * This methods decodes optional parameters of ISUP messages based on the offset info.
	 * @param opBuffer
	 * @param offset
	 * @return map
	 */
	public static Map<Integer, byte[]> decodeOptParams(byte[] opBuffer, int offset)
	{
		logger.info("decodeOptParams:Enter");

		Map<Integer, byte[]> optMap = new HashMap<Integer, byte[]>();
		if(offset != -1){
			int optOffset = (opBuffer[offset]<0 ? 256+opBuffer[offset] : opBuffer[offset]);	// if offset is more than 127 (8 bits)
			int optTypeIndex = offset + optOffset;
			
			if(optOffset != 0)
				while(true)
				{
					int optType = opBuffer[optTypeIndex] & 0xff;		//opt type is value at (value at index mentioned by offset + offset)
					int optLengthIndex = optTypeIndex+1;		
					int bytesLength = (opBuffer[optLengthIndex]<0 ? 256+opBuffer[optLengthIndex] : opBuffer[optLengthIndex]);			// if length is more than 127 (8 bits)
					
					byte[] paramBytes = new byte[bytesLength];
					for(int j=optLengthIndex+1, k=0; j<=optLengthIndex+bytesLength; j++, k++){						
						paramBytes[k] = opBuffer[j];
					}
					
					optMap.put(optType, paramBytes);				
					
					//calculate new optIndex
					if(opBuffer[optLengthIndex+bytesLength+1] == 0){		//0x00 is end of optional params
						break;
					}
					else{
						optTypeIndex = optLengthIndex+bytesLength+1;
					}		
				}
		}
		logger.info("decodeOptParams:Exit");
		
		return optMap;
	}
	
}
