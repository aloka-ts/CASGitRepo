package com.genband.inap.datatypes;

import org.apache.log4j.Logger;

import com.genband.inap.enumdata.CalledPartyCatIndEnum;
import com.genband.inap.enumdata.CalledPartyStatusIndEnum;
import com.genband.inap.enumdata.ChargeIndicatorEnum;
import com.genband.inap.enumdata.EchoContDeviceIndEnum;
import com.genband.inap.enumdata.EndToEndInfoIndEnum;
import com.genband.inap.enumdata.EndToEndMethodIndEnum;
import com.genband.inap.enumdata.HoldingIndEnum;
import com.genband.inap.enumdata.ISDNAccessIndEnum;
import com.genband.inap.enumdata.ISDNUserPartIndEnum;
import com.genband.inap.enumdata.InterNwIndEnum;
import com.genband.inap.enumdata.SCCPMethodIndENum;
import com.genband.inap.exceptions.InvalidInputException;
import com.genband.inap.util.Util;

/**
 * Used for encoding and decoding of TtcBackwardCallIndicators
 * @author vgoel
 *
 */

public class TtcBwCallIndicators {
	
	/**
	 * see ChargeIndicatorEnum
	 */
	ChargeIndicatorEnum chargeIndicatorEnum;

	/**
	 * see CalledPartyStatusIndEnum
	 */
	CalledPartyStatusIndEnum calledPartyStatusIndEnum;
	
	/**
	 * see CalledPartyCatIndEnum
	 */
	CalledPartyCatIndEnum calledPartyCatIndEnum;
	
	/**
	 * see EndToEndMethodIndEnum
	 */
	EndToEndMethodIndEnum endMethodIndEnum;	
	
	/**
	 * see InterNwIndEnum
	 */	
	InterNwIndEnum interNwIndEnum;
	
	/**
	 * see EndToEndInfoIndEnum
	 */
	EndToEndInfoIndEnum endToEndInfoIndEnum;
	
	/**
	 * see ISDNUserPartIndEnum
	 */
	ISDNUserPartIndEnum isdnUserPartIndEnum;
	
	/**
	 * see HoldingIndEnum
	 */
	HoldingIndEnum holdingIndEnum;
	
	/**
	 * see ISDNAccessIndEnum
	 */
	ISDNAccessIndEnum isdnAccessIndEnum;
	
	/**
	 * @see EchoContDeviceIndEnum
	 */
	EchoContDeviceIndEnum echoContDeviceIndEnum;
	
	/**
	 * @see SCCPMethodIndENum
	 */
	SCCPMethodIndENum sccpMethodIndENum;
		
	
	private static Logger logger = Logger.getLogger(FwCallIndicators.class);

	/**
	 * This function will encode Ttc backward call indicator
	 * @param chargeIndicatorEnum
	 * @param calledPartyStatusIndEnum
	 * @param calledPartyCatIndEnum
	 * @param endMethodIndEnum
	 * @param interNwIndEnum
	 * @param endToEndInfoIndEnum
	 * @param isdnUserPartIndEnum
	 * @param holdingIndEnum
	 * @param isdnAccessIndEnum
	 * @param echoContDeviceIndEnum
	 * @param sccpMethodIndENum
	 * @return byte[]
	 * @throws InvalidInputException
	 */
	public static byte[] encodeTtcBwCallInd(ChargeIndicatorEnum chargeIndicatorEnum, CalledPartyStatusIndEnum calledPartyStatusIndEnum, CalledPartyCatIndEnum calledPartyCatIndEnum,
			EndToEndMethodIndEnum endMethodIndEnum, InterNwIndEnum interNwIndEnum, EndToEndInfoIndEnum endToEndInfoIndEnum, ISDNUserPartIndEnum isdnUserPartIndEnum, 
			HoldingIndEnum holdingIndEnum, ISDNAccessIndEnum isdnAccessIndEnum, EchoContDeviceIndEnum echoContDeviceIndEnum, 
			SCCPMethodIndENum sccpMethodIndENum) throws InvalidInputException
	{	
		if (logger.isInfoEnabled()) {
			logger.info("encodeTtcBwCallInd:Enter");
		}
		if(holdingIndEnum == null){
			logger.error("encodeTtcBwCallInd: InvalidInputException(holdingIndEnum is null )");
			throw new InvalidInputException("holdingIndEnum is null");
		}
		if(endMethodIndEnum == null){
			logger.error("encodeTtcBwCallInd: InvalidInputException(endMethodIndEnum is null )");
			throw new InvalidInputException("endMethodIndEnum is null");
		}
		if(interNwIndEnum == null){
			logger.error("encodeTtcBwCallInd: InvalidInputException(interNwIndEnum is null )");
			throw new InvalidInputException("interNwIndEnum is null");
		}
		if(endToEndInfoIndEnum == null){
			logger.error("encodeTtcBwCallInd: InvalidInputException(endToEndInfoIndEnum is null )");
			throw new InvalidInputException("endToEndInfoIndEnum is null");
		}
		if(isdnUserPartIndEnum == null){
			logger.error("encodeTtcBwCallInd: InvalidInputException(isdnUserPartIndEnum is null )");
			throw new InvalidInputException("isdnUserPartIndEnum is null");
		}
		if(isdnAccessIndEnum == null){
			logger.error("encodeTtcBwCallInd: InvalidInputException(isdnAccessIndEnum is null )");
			throw new InvalidInputException("isdnAccessIndEnum is null");
		}
		if(sccpMethodIndENum == null){
			logger.error("encodeTtcBwCallInd: InvalidInputException(sccpMethodIndENum is null )");
			throw new InvalidInputException("sccpMethodIndENum is null");
		}
		if(echoContDeviceIndEnum == null){
			logger.error("encodeTtcBwCallInd: InvalidInputException(echoContDeviceIndEnum is null )");
			throw new InvalidInputException("echoContDeviceIndEnum is null");
		}
		
		byte[] data = new byte[2];
		
		int chargeInd;
		if(chargeIndicatorEnum == null){
			chargeInd = 3; //spare
		}
		else
			chargeInd = chargeIndicatorEnum.getCode();
		
		int cpStatus;
		if(calledPartyStatusIndEnum == null){
			cpStatus = 3; //spare
		}
		else
			cpStatus = calledPartyStatusIndEnum.getCode();
		
		int cpCat;
		if(calledPartyCatIndEnum == null){
			cpCat = 3; //spare
		}
		else
			cpCat = calledPartyCatIndEnum.getCode();
		
		int holdingInd = holdingIndEnum.getCode();
		int echoControlInd = echoContDeviceIndEnum.getCode();
		int endToEndMethodInd = endMethodIndEnum.getCode();
		int interNwInd = interNwIndEnum.getCode();
		int endToEndInfoInd = endToEndInfoIndEnum.getCode();
		int isdnUserPartInd = isdnUserPartIndEnum.getCode();
		int isdnAccessInd = isdnAccessIndEnum.getCode();
		int sccpMethodInd = sccpMethodIndENum.getCode();
				
		data[0] = (byte) (endToEndMethodInd << 6 | cpCat << 4 | cpStatus << 2 | chargeInd);
		data[1] = (byte) (sccpMethodInd << 6 | echoControlInd << 5 | isdnAccessInd << 4 | holdingInd << 3 | isdnUserPartInd << 2 | endToEndInfoInd << 1 | interNwInd);
		
		if(logger.isDebugEnabled())
			logger.debug("encodeTtcBwCallInd:Encoded Ttc Backward Call Indicators: " + Util.formatBytes(data));
		if (logger.isInfoEnabled()) {
			logger.info("encodeTtcBwCallInd:Exit");
		}
		return data;
	}
	
	/**
	 * This function will decode ttc backward call indicator
	 * @param data
	 * @return decoded data String
	 * @throws InvalidInputException 
	 */
	public static TtcBwCallIndicators decodeTtcBwCallInd(byte[] data) throws InvalidInputException
	{
		if (logger.isInfoEnabled()) {
			logger.info("decodeTtcBwCallInd:Enter");
		}
		if(logger.isDebugEnabled())
			logger.debug("decodeTtcBwCallInd: Input--> data:" + Util.formatBytes(data));
		if(data == null){
			logger.error("decodeTtcBwCallInd: InvalidInputException(data is null)");
			throw new InvalidInputException("data is null");
		}
		
		TtcBwCallIndicators ttcBwCallInd = new TtcBwCallIndicators();
		
		ttcBwCallInd.chargeIndicatorEnum = ChargeIndicatorEnum.fromInt(data[0] & 0x3);
		ttcBwCallInd.calledPartyStatusIndEnum = CalledPartyStatusIndEnum.fromInt((data[0] >> 2 ) & 0x3);
		ttcBwCallInd.calledPartyCatIndEnum = CalledPartyCatIndEnum.fromInt((data[0] >> 4 ) & 0x3);
		ttcBwCallInd.endMethodIndEnum = EndToEndMethodIndEnum.fromInt((data[0] >> 6 ) & 0x3);		
		
		ttcBwCallInd.interNwIndEnum = InterNwIndEnum.fromInt(data[1] & 0x1);
		ttcBwCallInd.endToEndInfoIndEnum = EndToEndInfoIndEnum.fromInt((data[1] >> 1 ) & 0x1);
		ttcBwCallInd.isdnUserPartIndEnum = ISDNUserPartIndEnum.fromInt((data[1] >> 2 ) & 0x1);
		ttcBwCallInd.holdingIndEnum = HoldingIndEnum.fromInt((data[1] >> 3 ) & 0x1);
		ttcBwCallInd.isdnAccessIndEnum = ISDNAccessIndEnum.fromInt((data[1] >> 4 ) & 0x1);
		ttcBwCallInd.echoContDeviceIndEnum = EchoContDeviceIndEnum.fromInt((data[1] >> 5 ) & 0x1);
		ttcBwCallInd.sccpMethodIndENum = SCCPMethodIndENum.fromInt((data[1] >> 6 ) & 0x3);		
		
		if(logger.isDebugEnabled())
			logger.debug("decodeTtcBwCallInd: Output<--" + ttcBwCallInd.toString());
		if (logger.isInfoEnabled()) {
			logger.info("decodeTtcBwCallInd:Exit");
		}
		return ttcBwCallInd ;
	}
	

	public EndToEndMethodIndEnum getEndMethodIndEnum() {
		return endMethodIndEnum;
	}

	public void setEndMethodIndEnum(EndToEndMethodIndEnum endMethodIndEnum) {
		this.endMethodIndEnum = endMethodIndEnum;
	}

	public InterNwIndEnum getInterNwIndEnum() {
		return interNwIndEnum;
	}

	public void setInterNwIndEnum(InterNwIndEnum interNwIndEnum) {
		this.interNwIndEnum = interNwIndEnum;
	}

	public EndToEndInfoIndEnum getEndToEndInfoIndEnum() {
		return endToEndInfoIndEnum;
	}

	public void setEndToEndInfoIndEnum(EndToEndInfoIndEnum endToEndInfoIndEnum) {
		this.endToEndInfoIndEnum = endToEndInfoIndEnum;
	}

	public ISDNUserPartIndEnum getIsdnUserPartIndEnum() {
		return isdnUserPartIndEnum;
	}

	public void setIsdnUserPartIndEnum(ISDNUserPartIndEnum isdnUserPartIndEnum) {
		this.isdnUserPartIndEnum = isdnUserPartIndEnum;
	}

	public ISDNAccessIndEnum getIsdnAccessIndEnum() {
		return isdnAccessIndEnum;
	}

	public void setIsdnAccessIndEnum(ISDNAccessIndEnum isdnAccessIndEnum) {
		this.isdnAccessIndEnum = isdnAccessIndEnum;
	}

	public SCCPMethodIndENum getSccpMethodIndENum() {
		return sccpMethodIndENum;
	}

	public void setSccpMethodIndENum(SCCPMethodIndENum sccpMethodIndENum) {
		this.sccpMethodIndENum = sccpMethodIndENum;
	}	 
	
	public ChargeIndicatorEnum getChargeIndicatorEnum() {
		return chargeIndicatorEnum;
	}

	public void setChargeIndicatorEnum(ChargeIndicatorEnum chargeIndicatorEnum) {
		this.chargeIndicatorEnum = chargeIndicatorEnum;
	}

	public CalledPartyStatusIndEnum getCalledPartyStatusIndEnum() {
		return calledPartyStatusIndEnum;
	}

	public void setCalledPartyStatusIndEnum(
			CalledPartyStatusIndEnum calledPartyStatusIndEnum) {
		this.calledPartyStatusIndEnum = calledPartyStatusIndEnum;
	}

	public CalledPartyCatIndEnum getCalledPartyCatIndEnum() {
		return calledPartyCatIndEnum;
	}

	public void setCalledPartyCatIndEnum(CalledPartyCatIndEnum calledPartyCatIndEnum) {
		this.calledPartyCatIndEnum = calledPartyCatIndEnum;
	}

	public HoldingIndEnum getHoldingIndEnum() {
		return holdingIndEnum;
	}

	public void setHoldingIndEnum(HoldingIndEnum holdingIndEnum) {
		this.holdingIndEnum = holdingIndEnum;
	}

	public EchoContDeviceIndEnum getEchoContDeviceIndEnum() {
		return echoContDeviceIndEnum;
	}

	public void setEchoContDeviceIndEnum(EchoContDeviceIndEnum echoContDeviceIndEnum) {
		this.echoContDeviceIndEnum = echoContDeviceIndEnum;
	}
	
	public String toString(){
		
		String obj = "chargeIndicatorEnum:"+ chargeIndicatorEnum + " ,endMethodIndEnum:" + endMethodIndEnum + " ,interNwIndEnum:" + interNwIndEnum + 
		" ,endToEndInfoIndEnum:" + endToEndInfoIndEnum + " ,isdnUserPartIndEnum:" + isdnUserPartIndEnum + " ,calledPartyStatusIndEnum:" + calledPartyStatusIndEnum + 
		" ,isdnAccessIndEnum:" + isdnAccessIndEnum + " sccpMethodIndENum," + sccpMethodIndENum + " holdingIndEnum," + holdingIndEnum + 
		" echoContDeviceIndEnum," + echoContDeviceIndEnum + " calledPartyCatIndEnum," + calledPartyCatIndEnum ;
		return obj ;
	}

}
