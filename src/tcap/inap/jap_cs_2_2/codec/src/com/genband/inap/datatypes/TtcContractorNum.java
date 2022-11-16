package com.genband.inap.datatypes;

import org.apache.log4j.Logger;

import com.genband.inap.enumdata.NatureOfAddEnum;
import com.genband.inap.enumdata.NumPlanEnum;
import com.genband.inap.exceptions.InvalidInputException;
import com.genband.inap.util.Util;


/**
 * Used for encoding and decoding of TTCContractorNumber
 * @author vgoel
 *
 */
public class TtcContractorNum extends AddressSignal{

	private static Logger logger = Logger.getLogger(TtcContractorNum.class);	 
	
	/**
	 * @see NatureOfAddEnum
	 */
	NatureOfAddEnum natureOfAdrs ; 
	/**
	 * @see NumPlanEnum
	 */
	NumPlanEnum numPlan ;
	
	/**
	 * This function will encode TTC contractor number. AddrsSignal is mandatory parameter.
	 * Other parameters will be assigned default values if not provided.
	 * NatureOfAdrsEnum: spare,NumPlanEnum: spare
	 * @param addrSignal
	 * @param natureOfNumberEnum
	 * @param numberingPlanEnum
	 * @return encoded data byte[]
	 * @throws InvalidInputException 
	 */
	public static byte[] encodeTtcContractorNum(String addrSignal, NatureOfAddEnum natureOfNumberEnum, NumPlanEnum numberingPlanEnum) throws InvalidInputException {
		if (logger.isInfoEnabled()) {
			logger.info("encodeTtcContractorNum:Enter");
		}
		byte[] bcdDigits = AddressSignal.encodeAdrsSignal(addrSignal);
		int seqLength = 2 + bcdDigits.length;

		int i = 0;
		byte[] myParms = new byte[seqLength];
		
		int natureOfAdrs;
		if(natureOfNumberEnum == null){
			if (logger.isInfoEnabled()) {
				logger.info("encodeTtcContractorNum:Assining default value spare of natureOfAdrs");
			}
			natureOfAdrs = 0;
		}else {
			natureOfAdrs = natureOfNumberEnum.getCode();
		}
		int numberingPlan ;
		if(numberingPlanEnum == null){
			if (logger.isInfoEnabled()) {
				logger.info("encodeTtcContractorNum:Assining default value spare of numberingPlan");
			}
			numberingPlan = 0 ;
		}else {
			numberingPlan = numberingPlanEnum.getCode();
		}
		
		// If even no. then set 8th bit 0 otherwise 1
		if (addrSignal.length() % 2 == 0) {
			myParms[i++] = (byte) ((0 << 7) | natureOfAdrs);
		} else {
			myParms[i++] = (byte) ((1 << 7) | natureOfAdrs);
		}
		// Setting 2nd byte for numbering plan and Internal Network Number indicator (INN ind.)
		myParms[i++] = (byte) (numberingPlan << 4);
		
		for (int j = 0; j < bcdDigits.length; j++) {
			myParms[i++] = bcdDigits[j];
		}
		if(logger.isDebugEnabled())
			logger.debug("encodeTtcContractorNum:Encoded TTC Contractor number: "+ Util.formatBytes(myParms));
		if (logger.isInfoEnabled()) {
			logger.info("encodeTtcContractorNum:Exit");
		}
		return myParms;
	}
	
	/**
	 * This function will decode TTC Contractor Number.
	 * @param data
	 * @return decoded data String
	 * @throws InvalidInputException 
	 */
	public static TtcContractorNum decodeTtcContractorNum(byte[] data) throws InvalidInputException{
		if (logger.isInfoEnabled()) {
			logger.info("decodeTtcContractorNum:Enter");
		}
		if(logger.isDebugEnabled())
			logger.debug("decodeTtcContractorNum: Input--> data:" + Util.formatBytes(data));
		if(data == null){
			logger.error("decodeTtcContractorNum: InvalidInputException(data is null)");
			throw new InvalidInputException("data is null");
		}
		
		TtcContractorNum ttcContNum = new TtcContractorNum();
		int parity = (data[0] >> 7) & 0x1;
		int natureOfAdrs = data[0] & 0x7f ;
		ttcContNum.natureOfAdrs = NatureOfAddEnum.fromInt(natureOfAdrs);
		if(data.length >= 1){
			int numberingPlan = (data[1] >> 4) & 0x7 ;
			ttcContNum.numPlan = NumPlanEnum.fromInt(numberingPlan);
		}
		if(data.length > 2){
			ttcContNum.addrSignal = AddressSignal.decodeAdrsSignal(data, 2 , parity);			
		}	
		if(logger.isDebugEnabled())
			logger.debug("decodeTtcContractorNum: Output<--" + ttcContNum.toString());
		if (logger.isInfoEnabled()) {
			logger.info("decodeTtcContractorNum:Exit");
		}
		return ttcContNum ;
	}
	
	public String toString(){
		
		String obj = "addrSignal:"+ addrSignal + " ,natureOfAdrs:"+ natureOfAdrs + " ,numPlan:" + numPlan ;
		return obj ;
	}

	public NatureOfAddEnum getNatureOfAdrs() {
		return natureOfAdrs;
	}

	public void setNatureOfAdrs(NatureOfAddEnum natureOfAdrs) {
		this.natureOfAdrs = natureOfAdrs;
	}

	public NumPlanEnum getNumPlan() {
		return numPlan;
	}

	public void setNumPlan(NumPlanEnum numPlan) {
		this.numPlan = numPlan;
	}

}
