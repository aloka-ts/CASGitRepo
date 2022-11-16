package com.agnity.win.datatypes;

import org.apache.log4j.Logger;
import com.agnity.win.asngenerated.ElectronicSerialNumber;
import com.agnity.win.exceptions.InvalidInputException;
import com.agnity.win.util.Util;


/*
 * This class provides encode and decode methods for Electronic Serial Number.
 * ESN parameter is used to indicate the unique 32-bits electronic serial number 
 * for an MS
 * Length                         - 4 Octets
 * First Octet                    - Manufacturer's Code
 * Second, Third and Fourth Octet - Serial Number
 */

public class NonAsnElectronicSerialNum {
	
	private static Logger logger = Logger.getLogger(NonAsnElectronicSerialNum.class);
	
	/*
	 * Manufacturer's Code
	 */
	byte manufacturerCode;
	
	/*
	 * Serial Number
	 */
	int serialNum;
	
	/**
	 * This function will decode ECN as per specification TIA-EIA-41-D
	 * section 6.5.2.63
	 * @param data
	 * @return decoded Digits
	 * @throws InvalidInputException 
	 */
	public static NonAsnElectronicSerialNum decodeEcn(byte[] data) throws InvalidInputException {
		if(logger.isDebugEnabled())
			logger.debug("decodeEcn: Input--> data:" + Util.formatBytes(data));

		if(data == null){
			logger.error("decodeEcn: InvalidInputException(Input data(bytes) not present or null)");
			throw new InvalidInputException("Input data(bytes) not present or null");
		}
		
		if(data.length != 4){
			logger.error("decodeEcn: InvalidInputException(data length is not equal to 4)");
			throw new InvalidInputException("ECN Length not equal to 4");
		}
		
		NonAsnElectronicSerialNum ecn = new NonAsnElectronicSerialNum();
		
		ecn.manufacturerCode = data[0];
		ecn.serialNum        = (data[3] << 16 | data[2] << 8 | data[1]);
		
		if(logger.isDebugEnabled())
			logger.debug("decodeEcn: manufacturerCode:" + ecn.manufacturerCode + " SerialNum:"+ecn.serialNum);
		
		return ecn;
	}
	
	/**
	 * This function will encode ECN as per specification TIA-EIA-41-D
	 * section 6.5.2.63
	 * @param mc   - manufacturerCode
	 * @param sn   - SerialNum
	 * @return byte[] of encoded ECN
	 * @throws InvalidInputException 
	 */
	public static byte[] encodeEcn(byte mc, int sn) throws InvalidInputException{
		if(logger.isDebugEnabled())
			logger.info("encodeEcn: manufacturerCode:" + mc + " SerialNum:"+ sn);
		
		byte[] myParams = new byte[4];
		
		myParams[0] = mc;
		myParams[1] = (byte) ((sn >> 16) & 0xFF);
		myParams[2] = (byte) ((sn >> 8) & 0xFF);
		myParams[3] = (byte) (sn & 0xFF);
		
		if(logger.isDebugEnabled())
			logger.debug("encodeEcn: Output--> data:" + Util.formatBytes(myParams));
		
		return myParams;
	}
	
	/**
	 * This function will encode Non ASN ElectronicSerialNumber to ASNElectronicSerial Number object
	 * @param nonASNElectronic Serial Number
	 * @return Electronic Serial Number
	 * @throws InvalidInputException
	 */
	public static ElectronicSerialNumber encodeEcn(NonAsnElectronicSerialNum nonASNElectronicSerialNum)
			throws InvalidInputException {
		
		logger.info("Before encode ElectronicSerial Number : nonASN to ASN");
		ElectronicSerialNumber ecn = new ElectronicSerialNumber();
		ecn.setValue(encodeEcn(nonASNElectronicSerialNum.getManufacturerCode(),nonASNElectronicSerialNum.getSerialNum()));
		logger.info("After encode ElectronicSerial Number : nonASN to ASN");
		return ecn;
	}

	public byte getManufacturerCode() {
		return manufacturerCode;
	}

	public void setManufacturerCode(byte manufacturerCode) {
		this.manufacturerCode = manufacturerCode;
	}

	public int getSerialNum() {
		return serialNum;
	}

	public void setSerialNum(int serialNum) {
		this.serialNum = serialNum;
	}
	
	public String toString() {
		String obj = "Manufacturer'sCode: " + manufacturerCode + " ,SerialNumber: " + serialNum;	
		return obj;	
	}
		
}
