package com.genband.isup.datatypes;

import org.apache.log4j.Logger;

import com.genband.isup.exceptions.InvalidInputException;

/**
 * This class provide encode, decode function for Jurisdiction Information parameter. 
 * This parameter is sent in ANSI protocol in IAM. The structure of Jurisdiction as defined in T1.113.3, Figure 21A
 * 
 *  |  8  |  7  |  6  |  5  |  4  |  3  |  2  |  1  |
 *  | 2nd Address Signal    | 1st Address signal    |
 *  | 4th Address signal    | 3rd Address signal    |
 *  | 6th Address signal    | 5th Address signal    |
 *  -------------------------------------------------
 *  
 *  It can have Minimum length of 1 Octet and Max of 3 Octets
 *  Above digits are encoded as per Address signal. 
 * @author rarya
 *
 */
public class JurisdictionInfo extends AddressSignal {

	private static Logger logger = Logger.getLogger(JurisdictionInfo.class);

	// Length is defined for octet
	static public int JURISDICTION_INFO_MIN_LENGTH  = 1;  
	static public int JURISDICTION_INFO_MAX_LENGTH  = 3;

	/**
	 * This method is used to encode JuridictionInfo parameter.
	 * @param addrSignal - digit string to encoded in BCD format
	 * @return
	 * @throws InvalidInputException 
	 */
	public static byte[] encodeJurisdictionInfo(String addrSignal) throws InvalidInputException {
		logger.info("encodeJurisdictionInfo: Enter");
		byte[] bcdDigits = AddressSignal.encodeAdrsSignal(addrSignal);
		logger.info("encodeJurisdictionInfo: Exit");
		return bcdDigits;
	}

	/**
	 * This method is used to decode Jurisdiction info parameter
	 * @param b
	 * @return
	 * @throws InvalidInputException 
	 */
	public static JurisdictionInfo decodeJurisdictionInfo(byte [] data) throws InvalidInputException {
		logger.info("decodeJurisdictionInfo: Enter");
		
		if(data == null){
			logger.error("decodeJurisdictionInfo: InvalidInputException(data is null)");
			throw new InvalidInputException("data is null");
		}
		
		if (data.length < JurisdictionInfo.JURISDICTION_INFO_MIN_LENGTH || 
				data.length > JurisdictionInfo.JURISDICTION_INFO_MAX_LENGTH) {
			logger.error("decodeJurisdictionInfo: InvalidInputException(Invalid Length of received data)");
			throw new InvalidInputException("Invalid length, should be > 1 or <= 3"); 
		}
		
		JurisdictionInfo ji = new JurisdictionInfo();
		int parity = (data.length%2);

		ji.addrSignal = AddressSignal.decodeAdrsSignal(data, 0, parity);

		logger.info("decodeJurisdictionInfo: Exit");
		return ji;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){

		String obj = "addrSignal:"+ addrSignal ;
		return obj ;
	}
}
