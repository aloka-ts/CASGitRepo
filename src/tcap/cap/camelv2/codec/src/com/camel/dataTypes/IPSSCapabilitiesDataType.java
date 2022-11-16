package com.camel.dataTypes;

import org.apache.log4j.Logger;

import com.camel.exceptions.InvalidInputException;
import com.camel.util.Util;

public class IPSSCapabilitiesDataType {
	
	public boolean ipRouting ;
	public boolean voiceBack ;
	public boolean voiceInforamtionViaSpeech ;
	public boolean voiceInformationViavoice ;
	public boolean voiceAnnouncments;
	
	private static Logger logger = Logger.getLogger(IPSSCapabilitiesDataType.class);	 
	
	/**
	 * This function will encode IPSSCapabilities.
	 * @param ipssDataType
	 * @return encoded data of IPSSCapabilitiesDataType
	 */
	public static byte[] encodeIpSS(IPSSCapabilitiesDataType ipssDataType){
	
		logger.info("encodeIpSS:Enter");
		byte[] myParams = new byte[1];
		myParams[0] = 0;
		
		if(ipssDataType.voiceAnnouncments){
			myParams[0] = (byte)((1 << 4) & 16);			
		}
		if(ipssDataType.voiceInformationViavoice){
			myParams[0] = (byte)((1<<3) | myParams[0]);
		}
		if(ipssDataType.voiceInforamtionViaSpeech){
			myParams[0] = (byte)((1<<2) | myParams[0]);
		}
		if(ipssDataType.voiceBack){
			myParams[0] = (byte)((1<<1) | myParams[0]);
		}
		if(ipssDataType.ipRouting){
			myParams[0] = (byte)((1) | myParams[0]);
		}
		if(logger.isDebugEnabled())
		logger.debug("encodeIpSS: Encoded IpSS" + Util.formatBytes(myParams));
		
		logger.info("encodeIpSS:Exit");
		return myParams ;
	
	}
	
	/**
	 * This function will decode IPSSCapabilities.
	 * @param myParams
	 * @return object of IPSSCapabilitiesDataType
	 * @throws InvalidInputException
	 */
	public static IPSSCapabilitiesDataType decodeIpSS(byte[] myParams) throws InvalidInputException{
		
		logger.info("decodeIpSS:Enter");
		if(myParams == null){
			logger.error("decodeIpSS: InvalidInputException(data is null)");
			throw new InvalidInputException("data is null");
		}
		IPSSCapabilitiesDataType ipCb = new IPSSCapabilitiesDataType();
		
		byte b1 = (byte)(myParams[0] & 1);
		if( b1 == 1){
			ipCb.ipRouting = true;
		}
		byte b2 = (byte)((myParams[0]>> 1) & 1);
		if( b2 == 1){
			ipCb.voiceBack = true;
		}
		byte b3 = (byte)((myParams[0]>> 2) & 1);
		if( b3 == 1){
			ipCb.voiceInforamtionViaSpeech = true;
		}
		byte b4 = (byte)((myParams[0]>> 3) & 1);
		if( b4 == 1){
			ipCb.voiceInformationViavoice = true;
		}
		byte b5 = (byte)((myParams[0]>> 4) & 1);
		if( b5 == 1){
			ipCb.voiceAnnouncments = true;
		}
		logger.debug("decodeIpSS:Output<--");
		logger.debug(ipCb.toString());
		logger.info("decodeIpSS:Exit");
		return ipCb ;
	}
	
	public String toString(){
		String obj = "ipRouting:" + ipRouting + " voiceBack:" + voiceBack + " voiceInforamtionViaSpeech:" + voiceInforamtionViaSpeech +
						" voiceInformationViavoice:" + voiceInformationViavoice + " voiceAnnouncments:" + voiceAnnouncments ;
		return obj;
	}
}
