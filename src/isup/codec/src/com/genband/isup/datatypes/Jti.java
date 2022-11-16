package com.genband.isup.datatypes;

import org.apache.log4j.Logger;


import com.genband.isup.enumdata.CalledLsSubIndEnum;
import com.genband.isup.enumdata.CalledMemberStatusIndEnum;
import com.genband.isup.enumdata.CalledMemberStatusInfoEnum;
import com.genband.isup.enumdata.IndLnpCheckEnum;
import com.genband.isup.enumdata.RepresentativeAreaCodeEnum;
import com.genband.isup.enumdata.TerminationBypassIndEnum;
import com.genband.isup.enumdata.TerminationIGSIndEnum;
import com.genband.isup.exceptions.InvalidInputException;
import com.genband.isup.util.Util;

/**
 * Used for encoding and decoding of CallingPartyNum
 * @author vgoel
 *
 */
public class Jti {
	
	/**
	 * @see RepresentativeAreaCodeEnum
	 */
	RepresentativeAreaCodeEnum repAreaCode;
	
	/**
	 * @see TerminationIGSIndEnum
	 */
	TerminationIGSIndEnum termIGSInd;
	
	/**
	 * @see TerminationBypassIndEnum
	 */
	TerminationBypassIndEnum termBypassInd;
	
	/**
	 * @see CalledLsSubIndEnum
	 */
	CalledLsSubIndEnum calledLsSubInd;
	
	/**
	 * @see IndLnpCheckEnum
	 */
	IndLnpCheckEnum indLnpChk;
	
	/**
	 * @see CalledMemberStatusIndEnum
	 */
	CalledMemberStatusIndEnum cldMemStsInd;
	
	/**
	 * @see CalledMemberStatusInfoEnum
	 */
	CalledMemberStatusInfoEnum cldMemStsInfo;
	
	public RepresentativeAreaCodeEnum getRepAreaCode() {
		return repAreaCode;
	}

	public void setRepAreaCode(RepresentativeAreaCodeEnum repAreaCode) {
		this.repAreaCode = repAreaCode;
	}

	public TerminationIGSIndEnum getTermIGSInd() {
		return termIGSInd;
	}

	public void setTermIGSInd(TerminationIGSIndEnum termIGSInd) {
		this.termIGSInd = termIGSInd;
	}

	public TerminationBypassIndEnum getTermBypassInd() {
		return termBypassInd;
	}

	public void setTermBypassInd(TerminationBypassIndEnum termBypassInd) {
		this.termBypassInd = termBypassInd;
	}

	public CalledLsSubIndEnum getCalledLsSubInd() {
		return calledLsSubInd;
	}

	public void setCalledLsSubInd(CalledLsSubIndEnum calledLsSubInd) {
		this.calledLsSubInd = calledLsSubInd;
	}

	public IndLnpCheckEnum getIndLnpChk() {
		return indLnpChk;
	}

	public void setIndLnpChk(IndLnpCheckEnum indLnpChk) {
		this.indLnpChk = indLnpChk;
	}

	public CalledMemberStatusIndEnum getCldMemStsInd() {
		return cldMemStsInd;
	}

	public void setCldMemStsInd(CalledMemberStatusIndEnum cldMemStsInd) {
		this.cldMemStsInd = cldMemStsInd;
	}

	public CalledMemberStatusInfoEnum getCldMemStsInfo() {
		return cldMemStsInfo;
	}

	public void setCldMemStsInfo(CalledMemberStatusInfoEnum cldMemStsInfo) {
		this.cldMemStsInfo = cldMemStsInfo;
	}
	
	private static Logger logger = Logger.getLogger(Jti.class);	 
	 
	/**
	 * This function will encode the JTI. It's length is of 5 octets. 
	 * Logical Channel Number (Octet 1), Logical Channel Group Number (octet 2)
	 * Logical Link Number (Octect 3) are always fixes as 0, so no input taken
	 * for encoding. 
	 * @param repAreaCode
	 * @param termIGSInd
	 * @param termBypassInd
	 * @param calledLsSubInd
	 * @param indLnpChk
	 * @param cldMemStsInd
	 * @param cldMemStsInfo
	 * @return encoded data byte[]
	 * @throws InvalidInputException 
	 */
	public static byte[] encodeJti(RepresentativeAreaCodeEnum repAreaCode, TerminationIGSIndEnum termIGSInd, TerminationBypassIndEnum termBypassInd, 
			CalledLsSubIndEnum calledLsSubInd, IndLnpCheckEnum indLnpChk , CalledMemberStatusIndEnum cldMemStsInd, CalledMemberStatusInfoEnum cldMemStsInfo) throws InvalidInputException {
	
		logger.info("encodeCalgParty:Enter");

		int seqLength = 5; // Length is fixed for 5 bytes
		int i = 0;
		byte[] myParms = new byte[seqLength];

		myParms[i++]= (byte)(0x0);
		myParms[i++]= (byte)(0x0);
		myParms[i++]= (byte)(0x0);
		
		int igsInd = 0;
		if(termIGSInd != null) {
			igsInd = termIGSInd.getCode();
		}
		
		int areaCode = 0;
		if(repAreaCode != null) {
			areaCode = repAreaCode.getCode();
		}
			
		int bypassInd = 0;
		if(termBypassInd != null) {
			bypassInd = termBypassInd.getCode();
		}
		
		int lsSubInd = 0;
		if (calledLsSubInd != null) {
			lsSubInd = calledLsSubInd.getCode();
		}
		
		int lnkChk =0;
		if (indLnpChk != null) {
			lnkChk = indLnpChk.getCode();
		}
		
		int memStsInd = 0;
		if(cldMemStsInd != null) {
			memStsInd = cldMemStsInd.getCode();
		}
		
		int memStsInfo =0;
		if(cldMemStsInfo != null) {
			memStsInfo = cldMemStsInfo.getCode();
		}
		
		myParms[i++]    = (byte)((lnkChk << 7) | (lsSubInd << 6) | (bypassInd << 5) | (igsInd << 4) | (areaCode << 3));
		myParms[i++]    = (byte)((memStsInd & 0x01) | ((memStsInfo & 0x0F) << 1));
		
		if(logger.isDebugEnabled())
			logger.debug("encodeJti: " + Util.formatBytes(myParms));
		logger.info("encodeJti:Exit");
		return myParms;
	}
	
	/**
	 * This function will decode Jti parameter
	 * @param data
	 * @return object of Jti
	 * @throws InvalidInputException
	 */
	public static Jti decodeJti(byte[] data) throws InvalidInputException{
		logger.info("Jti: Enter");
		if(logger.isDebugEnabled())
			logger.debug("decodeJti: Input--> data:" + Util.formatBytes(data));
		if(data == null){
			logger.error("decodeJti: InvalidInputException(data is null)");
			throw new InvalidInputException("data is null");
		}
		
		if(data.length != 5){
			logger.error("decodeJti: InvalidInputException(data length is not valid(5):)"+data.length);
			throw new InvalidInputException("data length is not valid(5)"+data.length);
		}

		Jti jti = new Jti();

		int value;
		value = ((data[3] >> 3) & 0x01);
		jti.repAreaCode = RepresentativeAreaCodeEnum.fromInt(value);
		
		value = ((data[3] >> 4) & 0x01);
		jti.termIGSInd = TerminationIGSIndEnum.fromInt(value);
		
		value = ((data[3] >> 5) & 0x01);
		jti.termBypassInd = TerminationBypassIndEnum.fromInt(value);
		
		value = ((data[3] >> 6) & 0x01);
		jti.calledLsSubInd = CalledLsSubIndEnum.fromInt(value);
		
		value = ((data[3] >> 7) & 0x01);
		jti.indLnpChk = IndLnpCheckEnum.fromInt(value);
		
		value = (data[4] & 0x01);
		jti.cldMemStsInd = CalledMemberStatusIndEnum.fromInt(value);
		
		value = ((data[4] >> 1) & 0x0F);
		jti.cldMemStsInfo = CalledMemberStatusInfoEnum.fromInt(value);
		
		if(logger.isDebugEnabled())
			logger.debug("Representative Area Code:" + jti.repAreaCode + " Termination IGS/GC Ind: " + jti.termIGSInd +
		             " Termination Bypass Ind:" + jti.termBypassInd + " Called LS Subscriber Ind:" + jti.calledLsSubInd +
		             " Indication of LNP Check: " + jti.indLnpChk + " Called Member Status Ind: " + jti.cldMemStsInd +
		             " Called Member Status Info:" + jti.cldMemStsInfo);
		logger.info("decodeJti:Exit");
		
		return jti ;
	}
	
	public String toString(){
		
		String obj = "Representative Area Code:" + repAreaCode + " \nTermination IGS/GC Ind: " + termIGSInd +
		             " \nTermination Bypass Ind:" + termBypassInd + " \nCalled LS Subscriber Ind:" + calledLsSubInd +
		             " \nIndication of LNP Check: " + indLnpChk + " \nCalled Member Status Ind: " + cldMemStsInd +
		             " \nCalled Member Status Info:" + cldMemStsInfo;
		return obj ;
	}
	
	

}
