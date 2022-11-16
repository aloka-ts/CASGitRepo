/****
 * Copyright (c) 2013 Agnity, Inc. All rights reserved.
 * 
 * This is proprietary source code of Agnity, Inc.
 * 
 * Agnity, Inc. retains all intellectual property rights associated with this
 * source code. Use is subject to license terms.
 * 
 * This source code contains trade secrets owned by Agnity, Inc. Confidentiality
 * of this computer program must be maintained at all times, unless explicitly
 * authorized by Agnity, Inc.
 ****/
package com.agnity.cap.v3.datatypes;


import org.apache.log4j.Logger;

import com.agnity.cap.v3.datatypes.enumType.TypeOfShapeCapV3Enum;

import com.agnity.cap.v3.exceptions.InvalidInputException;
/**
 * @ref:ETSI TS 100 974 V7.15.0 (2004-03) 
 * @author rnarayan
 *  Refers to geographical Information defined in GSM 03.32. 
    Only the description of an ellipsoid point with uncertainty circle 
    as specified in GSM 03.32 is allowed to be used 
	The internal structure according to GSM 03.32 is as follows: 
	**********
	Type of shape (ellipsoid point with uncertainty circle) 1 octet 
	Degrees of Latitude                                     3 octets 
	Degrees of Longitude                                    3 octets 
	Uncertainty code                                        1 octet 
 */
public class GeographicalInformationCapV3 {
	
  private TypeOfShapeCapV3Enum typeOfShape;
  private byte[] degreesOfLatitude;
  private byte[] degreesOfLongitude;
  private byte[] uncertaintyCode;
  private static Logger logger = Logger.getLogger(GeographicalInformationCapV3.class);
  
   /**
    * @return TypeOfShapeCapV2Enum object
    */
	public TypeOfShapeCapV3Enum getTypeOfShape() {
		return typeOfShape;
	}

	/**
	 * set Type of shape
	 * @param typeOfShape
	 */
	public void setTypeOfShape(TypeOfShapeCapV3Enum typeOfShape) {
		this.typeOfShape = typeOfShape;
	}

	/**
	 * 
	 * @return byte array of Degrees of Latitude   
	 */
	public byte[] getDegreesOfLatitude() {
		return degreesOfLatitude;
	}

	/**
	 * set Degrees of Latitude   
	 * @param degreesOfLatitude
	 */
	public void setDegreesOfLatitude(byte[] degreesOfLatitude) {
		this.degreesOfLatitude = degreesOfLatitude;
	}

	/**
	 * 
	 * @return byte array of Degrees of Longitude
	 */
	public byte[] getDegreesOfLongitude() {
		return degreesOfLongitude;
	}

	/**
	 * set Degrees of Longitude
	 * @param degreesOfLongitude
	 */
	public void setDegreesOfLongitude(byte[] degreesOfLongitude) {
		this.degreesOfLongitude = degreesOfLongitude;
	}

	/**
	 * 
	 * @return byte array of Uncertainty code
	 */
	public byte[] getUncertaintyCode() {
		return uncertaintyCode;
	}

	/**
	 * set Uncertainty code
	 * @param uncertaintyCode
	 */
	public void setUncertaintyCode(byte[] uncertaintyCode) {
		this.uncertaintyCode = uncertaintyCode;
	}
	
	/**
	 * decode GeographicalInformation byte array into non-asn object of GeographicalInformationCapV2
	 * @param bytes
	 * @return GeographicalInformationCapV2 object
	 * @throws InvalidInputException
	 */
	public static GeographicalInformationCapV3 decode(byte[] bytes) throws InvalidInputException{
	    if(logger.isDebugEnabled()){
	    	logger.debug("in decode. GeographicalInformation byte array length::"+bytes.length);
	    }
		GeographicalInformationCapV3 gi = new GeographicalInformationCapV3();
		gi.typeOfShape = TypeOfShapeCapV3Enum.getValue((bytes[0]>>4)& 0x0f);
		gi.degreesOfLatitude = new byte[]{bytes[1],bytes[2],bytes[3]};
		gi.degreesOfLongitude =  new byte[]{bytes[4],bytes[5],bytes[6]};
		gi.uncertaintyCode = new byte[]{bytes[7]};
		if(logger.isDebugEnabled()){
			logger.debug("GeographicalInformation non asn decoded successfully.");
		}
		return gi;
		
	}
	
	/**
	 * encode GeographicalInformation non asn into byte array
	 * @param GeographicalInformationCapV2 object
	 * @return byte array of GeographicalInformation
	 * @throws InvalidInputException
	 */
	public static byte[] encode(GeographicalInformationCapV3 gi) throws InvalidInputException{
		byte[] bytes = null;
		byte b0 = (byte)((gi.typeOfShape.getCode()<<4) & 0xff);
		byte[] la = gi.degreesOfLatitude;
		byte[] lo = gi.degreesOfLongitude;
		byte[] un = gi.uncertaintyCode;
		bytes = new byte[]{b0,la[0],la[1],la[2],lo[0],lo[1],lo[2],un[0]};
		if(logger.isDebugEnabled()){
			logger.debug("GeographicalInformation non asn encoded successfully. byte array length:"+bytes.length);
		}
		return bytes;
	}
	
	//test
	/*public static void main(String[] args)throws InvalidInputException {
		byte[] bytes = CapV2Functions.hexStringToByteArray("204a5682f463910a");
		byte[] exByte = encode(decode(bytes));
		System.out.println(Arrays.equals(bytes, exByte));
		
	}*/
	
	  
	  
}
