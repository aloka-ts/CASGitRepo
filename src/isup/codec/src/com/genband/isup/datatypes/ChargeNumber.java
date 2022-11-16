package com.genband.isup.datatypes;

import org.apache.log4j.Logger;

import com.genband.isup.enumdata.NatureOfAddEnum;
import com.genband.isup.enumdata.NumPlanEnum;
import com.genband.isup.exceptions.InvalidInputException;
import com.genband.isup.util.Util;

/**
 * This class defines Charge Number data structure defined as per T1.113 section 3.10.
 * This method is defined in ANSI only in IAM. 
 * T
 * User service information parameter value:  0xeb
 * Minimum Length: 1
 * Maximum Length: 13
 * 
 *     -----------------------------------------------------------------------
 *     | 8       |    7   |    6     |    5  |    4  |    3  |    2  |    1  |
 *   1 | Odd/Even|                 Nature of address indicator               |
 *   2 | Spare   |       Numbering Plan      |       Reserved                |
 *   3 |      2nd address signal             |   1st address signal          |
 *     |                                                                     |
 *   n |    Filler (if necessary)            |    nt address signal          |
 *     -----------------------------------------------------------------------
 * 
 * odd/Even Indicator - 
 * 0 even number of address signals
 * 1 odd number of address signals
 * 
 * Nature of address indicator 
 * Bits
 * 7 6 5 4 3 2 1
 * 0 0 0 0 0 0 0          spare
 * 0 0 0 0 0 0 1         ANI of the calling party; subscriber number
 * 0 0 0 0 0 1 0         ANI not available or not provided
 * 0 0 0 0 0 1 1         ANI of the calling party; national number
 * 0 0 0 0 1 0 0         spare
 * 0 0 0 0 1 0 1         ANI of the called party; subscriber number
 * 0 0 0 0 1 1 0         ANI of the called party; no number present
 * 0 0 0 0 1 1 1         ANI of the called party; national number
 * 0 0 0 1 0 0 0 }
 * to } spare
 * 1 1 1 0 1 1 1 }
 * 1 1 1 1 0 0 0 }
 * to } reserved for network specific use
 * 1 1 1 1 1 1 0 }
 * 1 1 1 1 1 1 1 spare
 * 
 * Numbering plan indicator
 * 0 0 0 unknown (no interpretation)
 * 0 0 1 ISDN (Telephony) numbering plan (Recommendation E.164)
 * 0 1 0 spare (no interpretation)
 * 0 1 1 reserved (ITU-T: Data numbering plan)
 * 1 0 0 reserved (ITU-T: Telex numbering plan)
 * 1 0 1 Private numbering plan
 * 1 1 0 spare (no interpretation)
 * 1 1 1 spare (no interpretation)
 * 
 *  Address signal
 * 0 0 0 0 digit 0
 * 0 0 0 1 digit 1
 * 0 0 1 0 digit 2
 * 0 0 1 1 digit 3
 * 0 1 0 0 digit 4
 * 0 1 0 1 digit 5
 * 0 1 1 0 digit 6
 * 0 1 1 1 digit 7
 * 1 0 0 0 digit 8
 * 1 0 0 1 digit 9
 * 1 0 1 0 spare (no interpretation)
 * 1 0 1 1 code 11
 * 1 1 0 0 code 12
 * 1 1 0 1 spare (no interpretation)
 * 1 1 1 0 spare (no interpretation)
 * 1 1 1 1 ST (Reserved) (no interpretation)
 * 
 * @author rarya
 *
 */
public class ChargeNumber  extends AddressSignal {

	static public int USER_SERVICE_INFO_MIN_LENGTH = 2;
	static public int USER_SERVICE_INFO_MAX_KENGTH  = 11;

	private static Logger logger = Logger.getLogger(ChargeNumber.class);

	/**
	 * @see NatureOfAddEnum
	 */
	NatureOfAddEnum natureOfAdrs ; 

	/**
	 * @see NumPlanEnum
	 */
	NumPlanEnum numPlan ;

	/**
	 * This method encode Charge number as per Standard T1.113 for ANSI protocol. 
	 * @param addrSignal
	 * @param natureOfNumberEnum
	 * @param numberingPlanEnum
	 * @return
	 */
	public static byte[] encodeChargeNumber(String addrSignal, NatureOfAddEnum natureOfNumberEnum, 
			NumPlanEnum numberingPlanEnum) throws InvalidInputException {
		logger.info("encodeChargeNumber:Enter");

		byte[] bcdDigits = AddressSignal.encodeAdrsSignal(addrSignal);
		int seqLength = 2 + bcdDigits.length;

		int i = 0;
		byte[] myParms = new byte[seqLength];

		int natureOfAdrs;
		if(natureOfNumberEnum == null){
			logger.info("encodeChargeNumber:Assining default value spare of natureOfAdrs");
			natureOfAdrs = 0;
		}else {
			natureOfAdrs = natureOfNumberEnum.getCode();
		}

		int numberingPlan ;
		if(numberingPlanEnum == null){
			logger.info("encodeChargeNumber:Assining default value spare of numberingPlan");
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
		// Setting 2nd byte for numbering plan 
		myParms[i++] = (byte) ( (0 << 7) | (numberingPlan << 4));

		for (int j = 0; j < bcdDigits.length; j++) {
			myParms[i++] = bcdDigits[j];
		}

		logger.info("encodeChargeNum:Exit");
		return myParms;
	}

	/**
	 * @param data
	 * @return
	 * @throws InvalidInputException 
	 */
	public static ChargeNumber decodeChargeNumber(byte[] data) throws InvalidInputException {

		logger.info("decodeChargeNum:Enter");

		if(data == null){
			logger.error("decodeChargeNumber: InvalidInputException(data is null)");
			throw new InvalidInputException("data is null");
		}
		
		if (data.length < ChargeNumber.USER_SERVICE_INFO_MIN_LENGTH ||
					data.length > ChargeNumber.USER_SERVICE_INFO_MAX_KENGTH) {
			throw new InvalidInputException("Invalid data length, should > 2 and < 11 octet");
		}

		if(logger.isDebugEnabled())
			logger.debug("decodeChargeNumber: Input--> data:" + Util.formatBytes(data));

		ChargeNumber chrgNum = new ChargeNumber();

		int parity = (data[0] >> 7) & 0x1;
		int natureOfAdrs = data[0] & 0x7f ;
		chrgNum.natureOfAdrs = NatureOfAddEnum.fromInt(natureOfAdrs);

		if(data.length >= 1){
			int numberingPlan = (data[1] >> 4) & 0x7 ;
			chrgNum.numPlan = NumPlanEnum.fromInt(numberingPlan);
		}

		if(data.length > 2){
			chrgNum.addrSignal = AddressSignal.decodeAdrsSignal(data, 2 , parity);			
		}	

		if(logger.isDebugEnabled())
			logger.debug("decodeChargeNumber: Output<--" + chrgNum.toString());

		logger.info("encodeChargeNum:Exit");
		return chrgNum;
	}
	/**
	 * @return
	 */
	public NatureOfAddEnum getNatureOfAdrs() {
		return natureOfAdrs;
	}

	/**
	 * @param natureOfAdrs
	 */
	public void setNatureOfAdrs(NatureOfAddEnum natureOfAdrs) {
		this.natureOfAdrs = natureOfAdrs;
	}

	/**
	 * @return
	 */
	public NumPlanEnum getNumPlan() {
		return numPlan;
	}

	/**
	 * @param numPlan
	 */
	public void setNumPlan(NumPlanEnum numPlan) {
		this.numPlan = numPlan;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){

		String obj = "addrSignal:"+ addrSignal + " ,natureOfAdrs:"+ natureOfAdrs + " ,numPlan:" + numPlan ;
		return obj ;
	}

}
