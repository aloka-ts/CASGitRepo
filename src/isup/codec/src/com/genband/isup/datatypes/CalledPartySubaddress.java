package com.genband.isup.datatypes;

import org.apache.log4j.Logger;

import com.genband.isup.enumdata.TypeOfSubaddress;
import com.genband.isup.exceptions.InvalidInputException;
import com.genband.isup.util.Util;

/**
 * This class have parameters for Called Party Subaddress. 
 * @author vgoel
 *
 */
public class CalledPartySubaddress {

	private static final char hexcodes[] = { '0', '1', '2', '3', '4', '5', '6',
		'7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
	
	/**
	 * @see TypeOfSubaddress
	 */
	TypeOfSubaddress typeOfSubaddress;

	String subAddInfo;
	
	private static Logger logger = Logger.getLogger(CalledPartySubaddress.class);
	
	
	public String getSubAddInfo() {
		return subAddInfo;
	}

	public void setSubAddInfo(String subAddInfo) {
		this.subAddInfo = subAddInfo;
	}	
	
	public TypeOfSubaddress getTypeOfSubaddress() {
		return typeOfSubaddress;
	}

	public void setTypeOfSubaddress(TypeOfSubaddress typeOfSubaddress) {
		this.typeOfSubaddress = typeOfSubaddress;
	}

	
	/**
	 * This function will encode Called Party Subaddress
	 * @param typeOfSubaddress
	 * @param subAddInfo
	 * @return byte[]
	 * @throws InvalidInputException
	 */
	public static byte[] encodeCalledPartySubaddress(TypeOfSubaddress typeOfSubaddress, String subAddInfo) throws InvalidInputException
	{	
		logger.info("encodeCalledPartySubaddress:Enter");
		
		byte[] data = null;
		
		if(typeOfSubaddress.getCode() == 2) {	//user specified
			//saneja revert for SN-UAT 85;bug 11780
			byte[] bcdDigits = AddressSignal.encodeAdrsSignal(subAddInfo);
			//byte[] bcdDigits = encodeSubAddInfoForUserSpecified(subAddInfo);
			data = new byte[3+bcdDigits.length];		//3 is length before subadd info
			data[0] = (byte)(0x71);	//Called party subaddress identifier
			data[1] = (byte)(bcdDigits.length+1);
			data[2] = (byte)(1<<7 | typeOfSubaddress.getCode()<<4 | (subAddInfo.length()%2==0?0:1)<<3);
			for (int j=0,i=3; j < bcdDigits.length; j++,i++) {
				data[i] = bcdDigits[j];
			}
		}
		
		if(typeOfSubaddress.getCode() == 0) {	//NSAP
			/*byte[] bcdDigits = AddressSignal.encodeAdrsSignal(subAddInfo);
			data = new byte[3+bcdDigits.length + 1];		//3 is length before subadd info and 1 for AFI
			data[0] = (byte)(0x71);	//Called party subaddress identifier
			data[1] = (byte)(bcdDigits.length+2);
			data[2] = (byte)(1<<7 | typeOfSubaddress.getCode()<<4);
			data[3] = (byte)(0x48);	//bcd encoded (as per rfc 4715)
			for (int j=0,i=4; j < bcdDigits.length; j++,i++) {
				data[i] = bcdDigits[j];
			}*/
			
			//as per IA5
			data = new byte[3+subAddInfo.length() + 1];		//3 is length before subadd info and 1 for AFI
			data[0] = (byte)(0x71);	//Called party subaddress identifier
			data[1] = (byte)(subAddInfo.length()+2);
			data[2] = (byte)(1<<7 | typeOfSubaddress.getCode()<<4);
			data[3] = (byte)(0x50);	//IA5 encoded (as per rfc 4715)
			for (int j=0,i=4; j<subAddInfo.length(); j++,i++) {
				data[i] = (byte)subAddInfo.charAt(j);
			}
		}
		
		if(logger.isDebugEnabled())
			logger.debug("encodeCalledPartySubaddress:Encoded Called Party Subaddress: " + Util.formatBytes(data));
		logger.info("encodeCalledPartySubaddress:Exit");
		
		return data;
	}
	
	
	/**
	 * This function will decode Called Party Subaddress
	 * @param data
	 * @return decoded data String
	 * @throws InvalidInputException 
	 */
	public static CalledPartySubaddress decodeCalledPartySubaddress(byte[] data) throws InvalidInputException
	{
		logger.info("decodeCalledPartySubaddress:Enter");
		if(logger.isDebugEnabled())
			logger.debug("decodeCalledPartySubaddress: Input--> data:" + Util.formatBytes(data));
		if(data == null){
			logger.error("decodeCalledPartySubaddress: InvalidInputException(data is null)");
			throw new InvalidInputException("data is null");
		}
		
		CalledPartySubaddress cpSubAdd = new CalledPartySubaddress();		
		cpSubAdd.typeOfSubaddress = TypeOfSubaddress.fromInt((data[2] >> 4) & 0x7);
		if(cpSubAdd.typeOfSubaddress.getCode() == 0) {	//NSAP
			if(data[3] == 72)	//bcd encoded
				cpSubAdd.subAddInfo = AddressSignal.decodeAdrsSignal(data, 4 , 0);
			if(data[3] == 80){	//IA5 encoded
				cpSubAdd.subAddInfo = "";
				for(int i=4; i<data.length; i++)
					cpSubAdd.subAddInfo += (char)data[i];
			}
			
		}
		else if(cpSubAdd.typeOfSubaddress.getCode() == 2) {	//user specific
			//saneja revert for SN-UAT 85;bug 11780
			cpSubAdd.subAddInfo = AddressSignal.decodeAdrsSignal(data, 3 , (data[2] >> 3) & 0x1);
			//cpSubAdd.subAddInfo = decodeSubAddInfoForUserSpecified(data, 3 , (data[2] >> 3) & 0x1);
		}
		
		if(logger.isDebugEnabled())
			logger.debug("decodeCalledPartySubaddress: Output<--" + cpSubAdd.toString());
		logger.info("decodeCalledPartySubaddress:Exit");
		
		return cpSubAdd ;
	}
	/**
	 * This function will encode address signal.
	 * @param subAddInfo
	 * @return encoded data byte[]
	 * @throws InvalidInputException 
	 * @author sumit
	 */
	private static byte[] encodeSubAddInfoForUserSpecified(String subAddInfo) throws InvalidInputException{
		logger.info("encodeSubAddInfoForUserSpecified:Enter");
		logger.debug("encodeSubAddInfoForUserSpecified:Input--> addrSignal:" + subAddInfo);
		if(subAddInfo == null || subAddInfo.equals(" ")){
			logger.error("encodeSubAddInfoForUserSpecified: InvalidInputException(AddressSignal is null or blank)");
			throw new InvalidInputException("encodeSubAddInfoForUserSpecified is null or blank");
		}
		int len = subAddInfo.length();
		int size = ( len + 1) / 2;
		byte[] out = new byte[size];

		for (int i = 0, j = 0; i < len; i += 2, j++) {
			byte b1 = (byte) (subAddInfo.charAt(i) - '0');
			byte b2 = 0;
			if ((i + 1) < len) {
				b2 = (byte) (subAddInfo.charAt(i + 1) - '0');
			}

			out[j] = (byte) ((b1 << 4) | b2);
		}
		if(logger.isDebugEnabled())
			logger.debug("encodeSubAddInfoForUserSpecified:Output<-- byte[]:" + Util.formatBytes(out));
		logger.info("encodeSubAddInfoForUserSpecified:Exit");
		return out;
	}
	
	/**
	 * This function will decode the address signal.
	 * @param data
	 * @param offset
	 * @param parity
	 * @return decoded data String
	 * @throws InvalidInputException 
	 * @author sumit
	 */
	public static String decodeSubAddInfoForUserSpecified(byte[] data , int offset, int parity) throws InvalidInputException{
		logger.info("decodeSubAddInfoForUserSpecified:Enter");
		if(logger.isDebugEnabled())
			logger.debug("decodeSubAddInfoForUserSpecified:Input--> data:" + Util.formatBytes(data)+ " ,offset:"+ offset + " ,parity"+ parity);
		if(data == null){
			logger.error("decodeSubAddInfoForUserSpecified: InvalidInputException(data is null)");
			throw new InvalidInputException("data is null");
		}
		int len = data.length ;
		char output[] = new char[2 * (len - offset)];
		int top = 0;

		for (int i = offset; i < len; i++) {
			output[top++] = hexcodes[(data[i] >> 4) & 0xf];
			output[top++] = hexcodes[data[i] & 0xf];
		}
		String tmpStr = new String(output);
		tmpStr = tmpStr.substring(0, tmpStr.length()- parity) ;
		
		if(logger.isDebugEnabled())
			logger.debug("decodeSubAddInfoForUserSpecified:Output<-- adrssignal:" + tmpStr);
		logger.info("decodeSubAddInfoForUserSpecified:Exit");
		return tmpStr;
	}
	
	public String toString(){
		
		String obj = "typeOfSubaddress:"+ typeOfSubaddress + " ,subAddInfo:" + subAddInfo;
		return obj ;
	}	
}
