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


package com.agnity.map.datatypes;

import java.math.BigInteger;
import java.util.Arrays;

import org.apache.log4j.Logger;

import com.agnity.map.enumdata.LatitudeSignMapEnum;
import com.agnity.map.enumdata.TypeOfShapeMapEnum;
import com.agnity.map.enumdata.GeodeticLPRIEnum;
import com.agnity.map.enumdata.GeodeticScreeningIndicatorMapEnum;
import com.agnity.map.enumdata.ExtensionIndicatorEnumMap;
import com.agnity.map.exceptions.InvalidInputException;
import com.agnity.map.util.MapFunctions;
import com.agnity.map.util.Util;

/**
 * 
 * @author sanjay
 * 
 * Refers to Calling Geodetic Location defined in Q.763 (1999).
 * Only the description of an ellipsoid point with uncertainty circle
 * as specified in Q.763 (1999) is allowed to be used
 * The internal structure according to Q.763 (1999) is as follows:
 * 
 * Screening and presentation indicators                         1 octet
 * Type of shape (ellipsoid point with uncertainty circle)       1 octet
 * Degrees of Latitude                                           3 octet
 * Degrees of Longitude                                          3 octet
 * Uncertainty code                                              1 octet
 * Confidence                                                    1 octet
 *
 */

public class GeodeticInformationMap {


	private GeodeticLPRIEnum lpri; // Location Presentation Restricted Indicator
	private GeodeticScreeningIndicatorMapEnum screeningIndicator;
	private ExtensionIndicatorEnumMap extensionIndicator;
    private TypeOfShapeMapEnum typeOfShape;
    private byte[] degreesOfLatitude;
    private String degreesOfLongitude;
    private String uncertaintyCode;
    private String confidence;

    
    private static Logger logger = Logger.getLogger(GeographicalInformationMap.class);
    

    

	/**
     * decode GeodeticInformation byte array into non-asn object of GeodeticInformation
     * @param bytes
     * @return GeodeticInformation object
     * @throws InvalidInputException
     */
    public static GeodeticInformationMap decode(byte[] bytes) throws InvalidInputException{
        if(logger.isDebugEnabled()){
            logger.debug("in decode. GeodeticInformation byte array length::"+bytes.length);
            logger.debug("decoding geodetic info : "+Util.formatBytes(bytes));
        }
        System.out.println("decoding geodetic info : "+Util.formatBytes(bytes));
        
        GeodeticInformationMap gi = new GeodeticInformationMap();
        gi.lpri = GeodeticLPRIEnum.getValue((bytes[0]>>2) & 0x03);
        gi.screeningIndicator = GeodeticScreeningIndicatorMapEnum.getValue(bytes[0] & 0x03);
        gi.extensionIndicator = ExtensionIndicatorEnumMap.getValue((bytes[1]>>7)&0x01);
        gi.typeOfShape = TypeOfShapeMapEnum.getValue(bytes[1]& 0x7f);
        gi.degreesOfLatitude = new byte[]{ bytes[2], bytes[3], bytes[4]};
        gi.degreesOfLongitude = MapFunctions.byteArrayToDegOfLongitude(new byte[]{bytes[5],bytes[6],bytes[7]});
        gi.uncertaintyCode = String.valueOf(Integer.valueOf(bytes[8]&0xff));
        gi.confidence = String.valueOf(Integer.valueOf(bytes[9]&0xff));
        if(logger.isDebugEnabled()){
            logger.debug("GeodeticInformationMap non asn decoded successfully.");
        }
        return gi;
	    
    }
	
    /**
     * encode GeodeticInformationMap non asn into byte array
     * @param GeodeticInformationMap object
     * @return byte array of GeodeticInformationMap
     * @throws InvalidInputException
     */
    public static byte[] encode(GeodeticInformationMap gi) throws InvalidInputException{
        byte[] bytes = null;
        byte b0 = (byte)(((gi.lpri.getCode() << 2) | gi.screeningIndicator.getCode() ) & 0x0f);
        byte sh = (byte)gi.getTypeOfShape().getCode();
        sh = (byte) (sh & ((byte)(gi.getExtensionIndicator().getCode() << 7)));
        byte[] la = gi.getDegreesOfLatitude();
        byte[] lo = (new BigInteger(gi.getDegreesOfLongitude())).toByteArray();
        byte[] un = (new BigInteger(gi.getUncertaintyCode())).toByteArray();
        byte[] co = (new BigInteger(gi.getConfidence())).toByteArray();
        bytes = new byte[]{b0, sh, la[0], la[1], la[2], lo[0], lo[1], lo[2], un[0], co[0]};
        if(logger.isDebugEnabled()){
            logger.debug("GeodeticInformationMap non asn encoded successfully. byte array length:"+bytes.length);
        }
        return bytes;
    }

	/**
	 * @return the lpri
	 */
	public GeodeticLPRIEnum getLpri() {
		return lpri;
	}

	/**
	 * @return the screeningIndicator
	 */
	public GeodeticScreeningIndicatorMapEnum getScreeningIndicator() {
		return screeningIndicator;
	}

	/**
	 * @return the extensionIndicator
	 */
	public ExtensionIndicatorEnumMap getExtensionIndicator() {
		return extensionIndicator;
	}

	/**
	 * @return the typeOfShape
	 */
	public TypeOfShapeMapEnum getTypeOfShape() {
		return typeOfShape;
	}

	/**
	 * @return the degreesOfLatitude
	 */
	public byte[] getDegreesOfLatitude() {
		return degreesOfLatitude;
	}

	/**
	 * @return the degreesOfLongitude
	 */
	public String getDegreesOfLongitude() {
		return degreesOfLongitude;
	}

	/**
	 * @return the uncertaintyCode
	 */
	public String getUncertaintyCode() {
		return uncertaintyCode;
	}

	/**
	 * @return the confidence
	 */
	public String getConfidence() {
		return confidence;
	}

	/**
	 * @param lpri the lpri to set
	 */
	public void setLpri(GeodeticLPRIEnum lpri) {
		this.lpri = lpri;
	}

	/**
	 * @param screeningIndicator the screeningIndicator to set
	 */
	public void setScreeningIndicator(
			GeodeticScreeningIndicatorMapEnum screeningIndicator) {
		this.screeningIndicator = screeningIndicator;
	}

	/**
	 * @param extensionIndicator the extensionIndicator to set
	 */
	public void setExtensionIndicator(ExtensionIndicatorEnumMap extensionIndicator) {
		this.extensionIndicator = extensionIndicator;
	}

	/**
	 * @param typeOfShape the typeOfShape to set
	 */
	public void setTypeOfShape(TypeOfShapeMapEnum typeOfShape) {
		this.typeOfShape = typeOfShape;
	}

	/**
	 * @param degreesOfLatitude the degreesOfLatitude to set
	 */
	public void setDegreesOfLatitude(byte[] degreesOfLatitude) {
		this.degreesOfLatitude = degreesOfLatitude;
	}

	/**
	 * @param degreesOfLongitude the degreesOfLongitude to set
	 */
	public void setDegreesOfLongitude(String degreesOfLongitude) {
		this.degreesOfLongitude = degreesOfLongitude;
	}

	/**
	 * @param uncertaintyCode the uncertaintyCode to set
	 */
	public void setUncertaintyCode(String uncertaintyCode) {
		this.uncertaintyCode = uncertaintyCode;
	}

	/**
	 * @param confidence the confidence to set
	 */
	public void setConfidence(String confidence) {
		this.confidence = confidence;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "GeodeticInformationMap [lpri=" + lpri + ", screeningIndicator="
				+ screeningIndicator + ", extensionIndicator="
				+ extensionIndicator + ", typeOfShape=" + typeOfShape
				+ ", degreesOfLatitude=" + Util.formatBytes(degreesOfLatitude)
				+ ", degreesOfLongitude=" + degreesOfLongitude
				+ ", uncertaintyCode=" + uncertaintyCode + ", confidence="
				+ confidence + "]";
	}

}
