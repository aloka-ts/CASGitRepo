package com.genband.inap.datatypes;

import org.apache.log4j.Logger;

import com.genband.inap.enumdata.EndToEndInfoIndEnum;
import com.genband.inap.enumdata.EndToEndMethodIndEnum;
import com.genband.inap.enumdata.ISDNAccessIndEnum;
import com.genband.inap.enumdata.ISDNUserPartIndEnum;
import com.genband.inap.enumdata.ISDNUserPartPrefIndEnum;
import com.genband.inap.enumdata.InterNwIndEnum;
import com.genband.inap.enumdata.NatIntNatCallIndEnum;
import com.genband.inap.enumdata.SCCPMethodIndENum;
import com.genband.inap.exceptions.InvalidInputException;
import com.genband.inap.util.Util;

/**
 * Used for encoding and decoding of ForwardCallIndicators
 * @author vgoel
 *
 */

public class FwCallIndicators {
	
	NatIntNatCallIndEnum natIntNatCallIndEnum;
	
	EndToEndMethodIndEnum endMethodIndEnum;
	
	InterNwIndEnum interNwIndEnum;
	
	EndToEndInfoIndEnum endToEndInfoIndEnum;
	
	ISDNUserPartIndEnum isdnUserPartIndEnum;
	
	ISDNUserPartPrefIndEnum isdnUserPartPrefIndEnum;
	
	ISDNAccessIndEnum isdnAccessIndEnum;
	
	SCCPMethodIndENum sccpMethodIndENum;
		
	
	private static Logger logger = Logger.getLogger(FwCallIndicators.class);

	/**
	 * This function will encode forward call indicator
	 * @param natIntNatCallIndEnum
	 * @param endMethodIndEnum
	 * @param interNwIndEnum
	 * @param endToEndInfoIndEnum
	 * @param isdnUserPartIndEnum
	 * @param isdnUserPartPrefIndEnum
	 * @param isdnAccessIndEnum
	 * @param sccpMethodIndENum
	 * @return byte[]
	 * @throws InvalidInputException
	 */
	public static byte[] encodeFwCallInd(NatIntNatCallIndEnum natIntNatCallIndEnum, EndToEndMethodIndEnum endMethodIndEnum, InterNwIndEnum interNwIndEnum, 
			EndToEndInfoIndEnum endToEndInfoIndEnum, ISDNUserPartIndEnum isdnUserPartIndEnum , ISDNUserPartPrefIndEnum isdnUserPartPrefIndEnum, 
			ISDNAccessIndEnum isdnAccessIndEnum, SCCPMethodIndENum sccpMethodIndENum) throws InvalidInputException
	{	
		if (logger.isInfoEnabled()) {
			logger.info("encodeFwCallInd:Enter");
		}
		if(natIntNatCallIndEnum == null){
			logger.error("encodeFwCallInd: InvalidInputException(natIntNatCallIndEnum is null )");
			throw new InvalidInputException("natIntNatCallIndEnum is null");
		}
		if(endMethodIndEnum == null){
			logger.error("encodeFwCallInd: InvalidInputException(endMethodIndEnum is null )");
			throw new InvalidInputException("endMethodIndEnum is null");
		}
		if(interNwIndEnum == null){
			logger.error("encodeFwCallInd: InvalidInputException(interNwIndEnum is null )");
			throw new InvalidInputException("interNwIndEnum is null");
		}
		if(endToEndInfoIndEnum == null){
			logger.error("encodeFwCallInd: InvalidInputException(endToEndInfoIndEnum is null )");
			throw new InvalidInputException("endToEndInfoIndEnum is null");
		}
		if(isdnUserPartIndEnum == null){
			logger.error("encodeFwCallInd: InvalidInputException(isdnUserPartIndEnum is null )");
			throw new InvalidInputException("isdnUserPartIndEnum is null");
		}
		if(isdnAccessIndEnum == null){
			logger.error("encodeFwCallInd: InvalidInputException(isdnAccessIndEnum is null )");
			throw new InvalidInputException("isdnAccessIndEnum is null");
		}
		if(sccpMethodIndENum == null){
			logger.error("encodeFwCallInd: InvalidInputException(sccpMethodIndENum is null )");
			throw new InvalidInputException("sccpMethodIndENum is null");
		}
		
		byte[] data = new byte[2];		
		int natIntNatCallInd = natIntNatCallIndEnum.getCode();
		int endToEndMethodInd = endMethodIndEnum.getCode();
		int interNwInd = interNwIndEnum.getCode();
		int endToEndInfoInd = endToEndInfoIndEnum.getCode();
		int isdnUserPartInd = isdnUserPartIndEnum.getCode();
		int isdnUserPartPrefInd;
		if(isdnUserPartPrefIndEnum == null){
			isdnUserPartPrefInd = 3; //spare
		}
		else
			isdnUserPartPrefInd = isdnUserPartPrefIndEnum.getCode();
		int isdnAccessInd = isdnAccessIndEnum.getCode();
		int sccpMethodInd = sccpMethodIndENum.getCode();
				
		data[0] = (byte) (isdnUserPartPrefInd << 6 | isdnUserPartInd << 5 | endToEndInfoInd << 4 | interNwInd << 3| endToEndMethodInd << 1 | natIntNatCallInd);
		data[1] = (byte) (sccpMethodInd << 1 | isdnAccessInd);
		
		if(logger.isDebugEnabled())
			logger.debug("encodeFwCallInd:Encoded Calling Party: " + Util.formatBytes(data));
		if (logger.isInfoEnabled()) {
			logger.info("encodeFwCallInd:Exit");
		}
		return data;
	}
	
	/**
	 * This function will decode forward call indicator
	 * @param data
	 * @return decoded data String
	 * @throws InvalidInputException 
	 */
	public static FwCallIndicators decodeFwCallInd(byte[] data) throws InvalidInputException
	{
		if (logger.isInfoEnabled()) {
			logger.info("decodeFwCallInd:Enter");
		}
		if(logger.isDebugEnabled())
			logger.debug("decodeFwCallInd: Input--> data:" + Util.formatBytes(data));
		if(data == null){
			logger.error("decodeFwCallInd: InvalidInputException(data is null)");
			throw new InvalidInputException("data is null");
		}
		
		FwCallIndicators fwCallInd = new FwCallIndicators();
		
		fwCallInd.natIntNatCallIndEnum = NatIntNatCallIndEnum.fromInt(data[0] & 0x1);
		fwCallInd.endMethodIndEnum = EndToEndMethodIndEnum.fromInt((data[0] >> 1 ) & 0x3);
		fwCallInd.interNwIndEnum = InterNwIndEnum.fromInt((data[0] >> 3 ) & 0x1);
		fwCallInd.endToEndInfoIndEnum = EndToEndInfoIndEnum.fromInt((data[0] >> 4 ) & 0x1);
		fwCallInd.isdnUserPartIndEnum = ISDNUserPartIndEnum.fromInt((data[0] >> 5 ) & 0x1);
		fwCallInd.isdnUserPartPrefIndEnum = ISDNUserPartPrefIndEnum.fromInt((data[0] >> 6 ) & 0x3);
		fwCallInd.isdnAccessIndEnum = ISDNAccessIndEnum.fromInt(data[1] & 0x1);
		fwCallInd.sccpMethodIndENum = SCCPMethodIndENum.fromInt((data[1] >> 1 ) & 0x3);
		
		if(logger.isDebugEnabled())
			logger.debug("decodeFwCallInd: Output<--" + fwCallInd.toString());
		if (logger.isInfoEnabled()) {
			logger.info("decodeFwCallInd:Exit");
		}
		return fwCallInd ;
	}
	
	
	public NatIntNatCallIndEnum getNatIntNatCallIndEnum() {
		return natIntNatCallIndEnum;
	}

	public void setNatIntNatCallIndEnum(NatIntNatCallIndEnum natIntNatCallIndEnum) {
		this.natIntNatCallIndEnum = natIntNatCallIndEnum;
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

	public ISDNUserPartPrefIndEnum getIsdnUserPartPrefIndEnum() {
		return isdnUserPartPrefIndEnum;
	}

	public void setIsdnUserPartPrefIndEnum(
			ISDNUserPartPrefIndEnum isdnUserPartPrefIndEnum) {
		this.isdnUserPartPrefIndEnum = isdnUserPartPrefIndEnum;
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
	
	public String toString(){
		
		String obj = "natIntNatCallIndEnum:"+ natIntNatCallIndEnum + " ,endMethodIndEnum:" + endMethodIndEnum + " ,interNwIndEnum:" + interNwIndEnum + 
		" ,endToEndInfoIndEnum:" + endToEndInfoIndEnum + " ,isdnUserPartIndEnum:" + isdnUserPartIndEnum + " ,isdnUserPartPrefIndEnum:" + isdnUserPartPrefIndEnum + 
		" ,isdnAccessIndEnum:" + isdnAccessIndEnum + " sccpMethodIndENum," + sccpMethodIndENum;
		return obj ;
	}

}
