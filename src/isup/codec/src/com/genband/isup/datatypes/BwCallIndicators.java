package com.genband.isup.datatypes;

import org.apache.log4j.Logger;

import com.genband.isup.enumdata.CalledPartyCatIndEnum;
import com.genband.isup.enumdata.CalledPartyStatusIndEnum;
import com.genband.isup.enumdata.ChargeIndEnum;
import com.genband.isup.enumdata.EchoContDeviceIndEnum;
import com.genband.isup.enumdata.EndToEndInfoIndEnum;
import com.genband.isup.enumdata.EndToEndMethodIndEnum;
import com.genband.isup.enumdata.HoldingIndEnum;
import com.genband.isup.enumdata.ISDNAccessIndEnum;
import com.genband.isup.enumdata.ISDNUserPartIndEnum;
import com.genband.isup.enumdata.InterNwIndEnum;
import com.genband.isup.enumdata.SCCPMethodIndENum;
import com.genband.isup.exceptions.InvalidInputException;
import com.genband.isup.util.Util;


/**
 * Used for encoding and decoding of BackwardCallIndicators
 * @author vgoel
 *
 */

public class BwCallIndicators {

	/**
	 * @see ChargeIndEnum
	 */
	ChargeIndEnum chargeIndEnum;
	
	/**
	 * @see CalledPartyStatusIndEnum
	 */
	CalledPartyStatusIndEnum calledPartyStatusIndEnum;
	
	/**
	 * @see CalledPartyCatIndEnum
	 */
	CalledPartyCatIndEnum calledPartyCatIndEnum;
		
	/**
	 * @see EndToEndMethodIndEnum
	 */
	EndToEndMethodIndEnum endToEndMethodIndEnum;
	
	/**
	 * @see InterNwIndEnum
	 */
	InterNwIndEnum interNwIndEnum;
	
	/**
	 * @see EndToEndInfoIndEnum
	 */
	EndToEndInfoIndEnum endToEndInfoIndEnum;
	
	/**
	 * @see ISDNUserPartIndEnum
	 */
	ISDNUserPartIndEnum isdnUserPartIndEnum;
	
	/**
	 * @see HoldingIndEnum
	 */
	HoldingIndEnum holdingIndEnum;
	
	/**
	 * @see ISDNAccessIndEnum
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
	
			
	private static Logger logger = Logger.getLogger(BwCallIndicators.class);

	/**
	 * This function will encode backward call indicator
	 * @param chargeIndEnum
	 * @param calledPartyStatusIndEnum
	 * @param calledPartyCatIndEnum
	 * @param endToEndMethodIndEnum
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
	public static byte[] encodeBwCallInd(ChargeIndEnum chargeIndEnum, CalledPartyStatusIndEnum calledPartyStatusIndEnum, CalledPartyCatIndEnum calledPartyCatIndEnum, 
			EndToEndMethodIndEnum endToEndMethodIndEnum, InterNwIndEnum interNwIndEnum, EndToEndInfoIndEnum endToEndInfoIndEnum, ISDNUserPartIndEnum isdnUserPartIndEnum,
			HoldingIndEnum holdingIndEnum, ISDNAccessIndEnum isdnAccessIndEnum, EchoContDeviceIndEnum echoContDeviceIndEnum, SCCPMethodIndENum sccpMethodIndENum) throws InvalidInputException
	{	
		logger.info("encodeBwCallInd:Enter");

		if(endToEndMethodIndEnum == null){
			logger.error("encodeBwCallInd: InvalidInputException(endToEndMethodIndEnum is null )");
			throw new InvalidInputException("endToEndMethodIndEnum is null");
		}
		if(interNwIndEnum == null){
			logger.error("encodeBwCallInd: InvalidInputException(interNwIndEnum is null )");
			throw new InvalidInputException("interNwIndEnum is null");
		}
		if(endToEndInfoIndEnum == null){
			logger.error("encodeBwCallInd: InvalidInputException(endToEndInfoIndEnum is null )");
			throw new InvalidInputException("endToEndInfoIndEnum is null");
		}
		if(holdingIndEnum == null){
			logger.error("encodeBwCallInd: InvalidInputException(holdingIndEnum is null )");
			throw new InvalidInputException("holdingIndEnum is null");
		}
		if(isdnUserPartIndEnum == null){
			logger.error("encodeBwCallInd: InvalidInputException(isdnUserPartIndEnum is null )");
			throw new InvalidInputException("isdnUserPartIndEnum is null");
		}
		if(isdnAccessIndEnum == null){
			logger.error("encodeBwCallInd: InvalidInputException(isdnAccessIndEnum is null )");
			throw new InvalidInputException("isdnAccessIndEnum is null");
		}
		if(echoContDeviceIndEnum == null){
			logger.error("encodeBwCallInd: InvalidInputException(echoContDeviceIndEnum is null )");
			throw new InvalidInputException("echoContDeviceIndEnum is null");
		}
		if(sccpMethodIndENum == null){
			logger.error("encodeBwCallInd: InvalidInputException(sccpMethodIndENum is null )");
			throw new InvalidInputException("sccpMethodIndENum is null");
		}
		
		byte[] data = new byte[2];		
		int endToEndMethodInd = endToEndMethodIndEnum.getCode();
		int interNwInd = interNwIndEnum.getCode();
		int endToEndInfoInd = endToEndInfoIndEnum.getCode();
		int isdnUserPartInd = isdnUserPartIndEnum.getCode();
		int isdnAccessInd = isdnAccessIndEnum.getCode();
		int sccpMethodInd = sccpMethodIndENum.getCode();
		int echoContDeviceInd = echoContDeviceIndEnum.getCode();
		int holdingInd = holdingIndEnum.getCode();
		int calledPartyStatusCat;
		int chargeInd;
		int calledPartyCatInd;
		if(chargeIndEnum == null)
			chargeInd = 3; //spare	
		else
			chargeInd = chargeIndEnum.getCode();
		if(calledPartyStatusIndEnum == null)
			calledPartyStatusCat = 3; //spare	
		else
			calledPartyStatusCat = calledPartyStatusIndEnum.getCode();
		if(calledPartyCatIndEnum == null)
			calledPartyCatInd = 3; //spare	
		else
			calledPartyCatInd = calledPartyCatIndEnum.getCode();
		
				
		data[0] = (byte) (endToEndMethodInd << 6 | calledPartyCatInd<< 4 | calledPartyStatusCat << 2 | chargeInd);
		data[1] = (byte) (sccpMethodInd << 6 | echoContDeviceInd<<5 | isdnAccessInd<<4 | holdingInd<<3 | isdnUserPartInd<<2 | endToEndInfoInd<<1 | interNwInd);
		
		if(logger.isDebugEnabled())
			logger.debug("encodeBwCallInd:Encoded BwCallInd: " + Util.formatBytes(data));
		logger.info("encodeBwCallInd:Exit");
		
		return data;
	}
	
	/**
	 * This function will decode backward call indicators
	 * @param data
	 * @return decoded data String
	 * @throws InvalidInputException 
	 */
	public static BwCallIndicators decodeBwCallInd(byte[] data) throws InvalidInputException
	{
		logger.info("decodeBwCallInd:Enter");
		if(logger.isDebugEnabled())
			logger.debug("decodeBwCallInd: Input--> data:" + Util.formatBytes(data));
		if(data == null){
			logger.error("decodeBwCallInd: InvalidInputException(data is null)");
			throw new InvalidInputException("data is null");
		}
		
		BwCallIndicators bwCallInd = new BwCallIndicators();
		
		bwCallInd.chargeIndEnum = ChargeIndEnum.fromInt(data[0] & 0x3);
		bwCallInd.calledPartyStatusIndEnum = CalledPartyStatusIndEnum.fromInt((data[0] >> 2 ) & 0x3);
		bwCallInd.calledPartyCatIndEnum = CalledPartyCatIndEnum.fromInt((data[0] >> 4 ) & 0x3);
		bwCallInd.endToEndMethodIndEnum = EndToEndMethodIndEnum.fromInt((data[0] >> 6 ) & 0x3);
		bwCallInd.interNwIndEnum = InterNwIndEnum.fromInt(data[1] & 0x1);
		bwCallInd.endToEndInfoIndEnum = EndToEndInfoIndEnum.fromInt((data[1] >> 1 ) & 0x1);
		bwCallInd.isdnUserPartIndEnum = ISDNUserPartIndEnum.fromInt((data[1] >> 2 ) & 0x1);
		bwCallInd.holdingIndEnum = HoldingIndEnum.fromInt((data[1] >> 3 ) & 0x1);
		bwCallInd.isdnAccessIndEnum = ISDNAccessIndEnum.fromInt((data[1] >> 4 ) & 0x1);
		bwCallInd.echoContDeviceIndEnum = EchoContDeviceIndEnum.fromInt((data[1] >> 5 ) & 0x1);
		bwCallInd.sccpMethodIndENum = SCCPMethodIndENum.fromInt((data[1] >> 6 ) & 0x3);
		
		if(logger.isDebugEnabled())
			logger.debug("decodeBwCallInd: Output<--" + bwCallInd.toString());
		logger.info("decodeBwCallInd:Exit");
		
		return bwCallInd ;
	}
	
	
	public ChargeIndEnum getChargeIndEnum() {
		return chargeIndEnum;
	}

	public void setChargeIndEnum(ChargeIndEnum chargeIndEnum) {
		this.chargeIndEnum = chargeIndEnum;
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

	public EndToEndMethodIndEnum getEndToEndMethodIndEnum() {
		return endToEndMethodIndEnum;
	}

	public void setEndToEndMethodIndEnum(EndToEndMethodIndEnum endToEndMethodIndEnum) {
		this.endToEndMethodIndEnum = endToEndMethodIndEnum;
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

	public HoldingIndEnum getHoldingIndEnum() {
		return holdingIndEnum;
	}

	public void setHoldingIndEnum(HoldingIndEnum holdingIndEnum) {
		this.holdingIndEnum = holdingIndEnum;
	}

	public ISDNAccessIndEnum getIsdnAccessIndEnum() {
		return isdnAccessIndEnum;
	}

	public void setIsdnAccessIndEnum(ISDNAccessIndEnum isdnAccessIndEnum) {
		this.isdnAccessIndEnum = isdnAccessIndEnum;
	}

	public EchoContDeviceIndEnum getEchoContDeviceIndEnum() {
		return echoContDeviceIndEnum;
	}

	public void setEchoContDeviceIndEnum(EchoContDeviceIndEnum echoContDeviceIndEnum) {
		this.echoContDeviceIndEnum = echoContDeviceIndEnum;
	}

	public SCCPMethodIndENum getSccpMethodIndENum() {
		return sccpMethodIndENum;
	}

	public void setSccpMethodIndENum(SCCPMethodIndENum sccpMethodIndENum) {
		this.sccpMethodIndENum = sccpMethodIndENum;
	}

	public String toString(){
		
		String obj = "chargeIndEnum:"+ chargeIndEnum + " ,calledPartyStatusIndEnum:" + calledPartyStatusIndEnum + " ,calledPartyCatIndEnum:" + calledPartyCatIndEnum + 
		" ,endToEndMethodIndEnum:" + endToEndMethodIndEnum + " ,interNwIndEnum:" + interNwIndEnum + " ,endToEndInfoIndEnum:" + endToEndInfoIndEnum + 
		" ,isdnUserPartIndEnum:" + isdnUserPartIndEnum + " ,holdingIndEnum" + holdingIndEnum + " ,isdnAccessIndEnum" + isdnAccessIndEnum + 
		" ,echoContDeviceIndEnum" + echoContDeviceIndEnum + " ,sccpMethodIndENum" + sccpMethodIndENum;
		return obj ;
	}
}
