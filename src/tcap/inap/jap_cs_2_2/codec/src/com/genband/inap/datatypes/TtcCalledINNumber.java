package com.genband.inap.datatypes;

import org.apache.log4j.Logger;

import com.genband.inap.enumdata.AddPrsntRestEnum;
import com.genband.inap.enumdata.NumPlanEnum;
import com.genband.inap.enumdata.TTCNatureOfAddEnum;
import com.genband.inap.exceptions.InvalidInputException;
import com.genband.inap.util.Util;


/**
 * Used for encoding and decoding of TTCCalledINNumber
 * @author vgoel
 *
 */
public class TtcCalledINNumber extends AddressSignal { 
	
	/**
	 * @see TTCNatureOfAddEnum
	 */
	TTCNatureOfAddEnum natureOfAdrs; 
	/**
	 * @see NumPlanEnum
	 */
	NumPlanEnum numPlan; 
	/**
	 * @see AddPrsntRestEnum
	 */
	AddPrsntRestEnum adrsPresntRestd;
	
	private static Logger logger = Logger.getLogger(TtcCalledINNumber.class);	 
	 
	
	/**
	 * This function will encode the TTC Called IN Number. AddrsSignal is mandatory parameter.
	 * Other parameters will be assigned default values if not provided.
	 * @param addrSignal
	 * @param natureOfAdrsEnum
	 * @param numberingPlanEnum
	 * @param adrsPresntRestdEnum
	 * @return encoded data byte[]
	 * @throws InvalidInputException 
	 */
	public static byte[] encodeTtcCalledINNum(String addrSignal, TTCNatureOfAddEnum natureOfAdrsEnum, NumPlanEnum numberingPlanEnum, AddPrsntRestEnum adrsPresntRestdEnum) throws InvalidInputException {
		if (logger.isInfoEnabled()) {
			logger.info("encodeTtcCalledINNum:Enter");
		}
		byte[] bcdDigits = AddressSignal.encodeAdrsSignal(addrSignal);
		int seqLength = 2 + bcdDigits.length;
		int i = 0;
		byte[] myParms = new byte[seqLength];
		int natureOfAdrs;
		if(natureOfAdrsEnum == null){
			// Assigning default value -Spare
			natureOfAdrs = 0;
		}else {
			natureOfAdrs = natureOfAdrsEnum.getCode();
		}
		int numberingPlan ;
		if(numberingPlanEnum == null){
			// Assigning default value -Spare
			numberingPlan = 0 ;
		}else {
			numberingPlan = numberingPlanEnum.getCode();
		}
		int adrsPresntRestd ;
		if(adrsPresntRestdEnum == null){
			//Assigning default value -Spare
			adrsPresntRestd = 3;
		}else{
			adrsPresntRestd = adrsPresntRestdEnum.getCode();
		}	
		
		//If address presentation restricted indicator indicates
		//address not available(2), octets 3 to n are omitted, 1st byte are coded with
		//0's, and the subfield f) is coded with 11.
		if(adrsPresntRestd == 2){
			myParms[i++]= (byte)(0x0);
			myParms[i++]= (byte)((adrsPresntRestd << 2) | 0x03);
		}else {	
			// If even no. of Address signal then set 8th bit 0 otherwise 1
			if (addrSignal.length() % 2 == 0) {
				myParms[i++] = (byte) ((0 << 7) | natureOfAdrs);
			} else {
				myParms[i++] = (byte) ((1 << 7) | natureOfAdrs);
			}
			myParms[i++] = (byte)((numberingPlan << 4) | (adrsPresntRestd << 2));
		}
		
		for (int j = 0; j < bcdDigits.length; j++) {
			myParms[i++] = bcdDigits[j];
		}
		if(logger.isDebugEnabled())
			logger.debug("encodeTtcCalledINNum:Encoded Ttc Called IN Number: " + Util.formatBytes(myParms));
		if (logger.isInfoEnabled()) {
			logger.info("encodeTtcCalledINNum:Exit");
		}
		return myParms;
	}
	
	/**
	 * This function will decode TTC Called IN Number.
	 * @param data
	 * @return object of TtcCalledINNumber
	 * @throws InvalidInputException
	 */
	public static TtcCalledINNumber decodeTtcCalledINNum(byte[] data) throws InvalidInputException{
		if (logger.isInfoEnabled()) {
			logger.info("decodeTtcCalledINNum:Enter");
		}
		if(logger.isDebugEnabled())
			logger.debug("decodeTtcCalledINNum: Input--> data:" + Util.formatBytes(data));
		if(data == null){
			logger.error("decodeTtcCalledINNum: InvalidInputException(data is null)");
			throw new InvalidInputException("data is null");
		}
		
		TtcCalledINNumber ttcCalledINNum = new TtcCalledINNumber();
		int parity = (data[0] >> 7) & 0x1;
		int natureOfAdrs = data[0] & 0x7f ;
		ttcCalledINNum.natureOfAdrs = TTCNatureOfAddEnum.fromInt(natureOfAdrs);
		if(data.length >=1){
			int numberingPlan = (data[1] >> 4) & 0x7 ;
			ttcCalledINNum.numPlan = NumPlanEnum.fromInt(numberingPlan);
			int adrsPresntRestd = (data[1] >> 2) & 0x3 ;
			ttcCalledINNum.adrsPresntRestd = AddPrsntRestEnum.fromInt(adrsPresntRestd);
		}
		if(data.length > 2){
			ttcCalledINNum.addrSignal = AddressSignal.decodeAdrsSignal(data, 2 , parity);
		}		
		
		if(logger.isDebugEnabled())
			logger.debug("decodeTtcCalledINNum: Output<--" + ttcCalledINNum.toString());
		if (logger.isInfoEnabled()) {
			logger.info("decodeTtcCalledINNum:Exit");
		}
		return ttcCalledINNum ;
	}
	
	public TTCNatureOfAddEnum getNatureOfAdrs() {
		return natureOfAdrs;
	}

	public NumPlanEnum getNumPlan() {
		return numPlan;
	}

	public AddPrsntRestEnum getAdrsPresntRestd() {
		return adrsPresntRestd;
	}
	
	

	public void setNatureOfAdrs(TTCNatureOfAddEnum natureOfAdrs) {
		this.natureOfAdrs = natureOfAdrs;
	}

	public void setNumPlan(NumPlanEnum numPlan) {
		this.numPlan = numPlan;
	}

	public void setAdrsPresntRestd(AddPrsntRestEnum adrsPresntRestd) {
		this.adrsPresntRestd = adrsPresntRestd;
	}

	public String toString(){
		
		String obj = "addrSignal:"+ addrSignal + " ,natureOfAdrs:"+ natureOfAdrs + " ,numPlan:" + numPlan + " ,adrsPresntRestd:" + adrsPresntRestd ;
		return obj ;
	}

}
