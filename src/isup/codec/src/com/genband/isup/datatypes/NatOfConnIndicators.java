package com.genband.isup.datatypes;

import org.apache.log4j.Logger;

import com.genband.isup.enumdata.ContCheckIndEnum;
import com.genband.isup.enumdata.EchoContDeviceIndEnum;
import com.genband.isup.enumdata.SatelliteIndEnum;
import com.genband.isup.exceptions.InvalidInputException;
import com.genband.isup.util.Util;

/**
 * Used for encoding and decoding of NatureOfConnectionIndicators
 * @author vgoel
 *
 */

public class NatOfConnIndicators {

	/**
	 * @see SatelliteIndEnum
	 */
	SatelliteIndEnum satelliteIndEnum;
	
	/**
	 * @see ContCheckIndEnum
	 */
	ContCheckIndEnum contCheckIndEnum;
	
	/**
	 * @see EchoContDeviceIndEnum
	 */
	EchoContDeviceIndEnum echoContDeviceIndEnum;
	
	private static Logger logger = Logger.getLogger(NatOfConnIndicators.class);

	public SatelliteIndEnum getSatelliteIndEnum() {
		return satelliteIndEnum;
	}

	public void setSatelliteIndEnum(SatelliteIndEnum satelliteIndEnum) {
		this.satelliteIndEnum = satelliteIndEnum;
	}

	public ContCheckIndEnum getContCheckIndEnum() {
		return contCheckIndEnum;
	}

	public void setContCheckIndEnum(ContCheckIndEnum contCheckIndEnum) {
		this.contCheckIndEnum = contCheckIndEnum;
	}

	public EchoContDeviceIndEnum getEchoContDeviceIndEnum() {
		return echoContDeviceIndEnum;
	}

	public void setEchoContDeviceIndEnum(EchoContDeviceIndEnum echoContDeviceIndEnum) {
		this.echoContDeviceIndEnum = echoContDeviceIndEnum;
	}

	
	/**
	 * This function will encode nature of connection indicators
	 * @param satelliteIndEnum
	 * @param contCheckIndEnum
	 * @param echoContDeviceIndEnum
	 * @return byte[]
	 * @throws InvalidInputException
	 */
	public static byte[] encodeConnIndicators(SatelliteIndEnum satelliteIndEnum, ContCheckIndEnum contCheckIndEnum, EchoContDeviceIndEnum echoContDeviceIndEnum) throws InvalidInputException
	{
		logger.info("encodeConnIndicators:Enter");		
		
		if(echoContDeviceIndEnum == null){
			logger.error("encodeConnIndicators: InvalidInputException(echoContDeviceIndEnum is null )");
			throw new InvalidInputException("echoContDeviceIndEnum is null");
		}
		int satelliteInd;
		int contCheckInd;
		int echoContDeviceInd = echoContDeviceIndEnum.getCode();
		
		if(satelliteIndEnum == null)
			satelliteInd = 3;		
		else
			satelliteInd = satelliteIndEnum.getCode();
		
		if(contCheckIndEnum == null)
			contCheckInd = 3;
		else
			contCheckInd = contCheckIndEnum.getCode();
		
		byte[] data = new byte[1];

		data[0] = (byte) (echoContDeviceInd << 4 | contCheckInd << 2 | satelliteInd);
		
		if(logger.isDebugEnabled())
			logger.debug("encodeConnIndicators:Encoded Nature Of Connection Indicators : "+ Util.formatBytes(data));
		logger.info("encodeConnIndicators:Exit");
		
		return data;
	}
	
	
	public static NatOfConnIndicators decodeConnIndicators(byte[] data) throws InvalidInputException
	{
		logger.info("NatOfConnIndicators:Enter");
		if(logger.isDebugEnabled())
			logger.debug("NatOfConnIndicators: Input--> data:" + Util.formatBytes(data));
		if(data == null){
			logger.error("NatOfConnIndicators: InvalidInputException(data is null)");
			throw new InvalidInputException("data is null");
		}
		
		NatOfConnIndicators connInd = new NatOfConnIndicators();
		
		connInd.satelliteIndEnum = SatelliteIndEnum.fromInt(data[0] & 0x03);
		connInd.contCheckIndEnum = ContCheckIndEnum.fromInt((data[0] >> 2) & 0x03);
		connInd.echoContDeviceIndEnum = EchoContDeviceIndEnum.fromInt((data[0] >> 4) & 0x01);
		
		if(logger.isDebugEnabled())
			logger.debug("decodeFwCallInd: Output<--" + connInd.toString());
		logger.info("decodeFwCallInd:Exit");
		
		return connInd ;
		
	}
	
	@Override
	public String toString() {
		return "satelliteIndEnum:"+ satelliteIndEnum + " ,contCheckIndEnum:"+ contCheckIndEnum + " ,echoContDeviceIndEnum:" + echoContDeviceIndEnum;
	}	
	
	
}
