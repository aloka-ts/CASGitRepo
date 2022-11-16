package com.genband.isup.datatypes;



import org.apache.log4j.Logger;

import com.genband.isup.enumdata.FeatureCodeEnum;
import com.genband.isup.exceptions.InvalidInputException;
import com.genband.isup.util.Util;
/**
 * Used for encoding and decoding of Service Activation
 * @author pgandhi
 *
 */
public class ServiceActivation{
	
	private static Logger logger = Logger.getLogger(TtcChargeAreaInfo.class);
	
	FeatureCodeEnum featureCodeEnum;
	
	public FeatureCodeEnum getFeatureCodeEnum() {
		return featureCodeEnum;
	}

	public void setFeatureCodeEnum(FeatureCodeEnum featureCodeEnum) {
		this.featureCodeEnum = featureCodeEnum;
	}

	public static byte[] encodeServiceActivation(FeatureCodeEnum featureCodeEnum) throws InvalidInputException{
		if(logger.isDebugEnabled())
			logger.debug("encodeServiceActivation:Enter");
		byte[] myParms = new byte[1];
		myParms[0] = (byte) ((0 << 7) | featureCodeEnum.getCode());
		if(logger.isDebugEnabled()){
			logger.debug("encodeServiceActivation:Encoded Service Activation: " + Util.formatBytes(myParms));
			logger.debug("encodeServiceActivation:Exit");
		}
		return myParms;
	}
	
	public static ServiceActivation decodeServiceActivation(byte[] data) throws InvalidInputException{
		
		if(logger.isDebugEnabled()){
			logger.debug("decodeServiceActivation:Enter");
			logger.debug("decodeServiceActivation: Input--> data:" + Util.formatBytes(data));
		}
		if(data == null){
			logger.error("decodeServiceActivation: InvalidInputException(data is null)");
			throw new InvalidInputException("data is null");
		}
		ServiceActivation svcAct = new ServiceActivation();
		int featureCode = data[0] & 0x7f ;
		svcAct.featureCodeEnum = FeatureCodeEnum.fromInt(featureCode);
		return svcAct;
	}
}