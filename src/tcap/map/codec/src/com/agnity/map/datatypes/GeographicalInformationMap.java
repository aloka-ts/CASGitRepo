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
import com.agnity.map.exceptions.InvalidInputException;
import com.agnity.map.util.MapFunctions;
import com.genband.tcap.parser.Util;
/**
 * @ref:ETSI TS 100 974 V7.15.0 (2004-03) 
 * @author sanjay
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
public class GeographicalInformationMap {
	
    private TypeOfShapeMapEnum typeOfShape;
    private String degreesOfLatitudeHigh;
    private String degreesOfLatitudeLow;
    private String degreesOfLongitude;
    private String uncertaintyCode;
    private LatitudeSignMapEnum signOfLatitude;
    /**
	 * @return the signOfLatitude
	 */
	public LatitudeSignMapEnum getSignOfLatitude() {
		return signOfLatitude;
	}

	/**
	 * @param signOfLatitude the signOfLatitude to set
	 */
	public void setSignOfLatitude(LatitudeSignMapEnum signOfLatitude) {
		this.signOfLatitude = signOfLatitude;
	}

	private static Logger logger = Logger.getLogger(GeographicalInformationMap.class);
  
    /**
     * @return TypeOfShapeMapEnum object
     */
    public TypeOfShapeMapEnum getTypeOfShape() {
        return typeOfShape;
    }

    /**
     * set Type of shape
     * @param typeOfShape
     */
    public void setTypeOfShape(TypeOfShapeMapEnum typeOfShape) {
        this.typeOfShape = typeOfShape;
    }


    /**
     * 
     * @return byte array of Degrees of Longitude
     */
    public String getDegreesOfLongitude() {
        return degreesOfLongitude;
    }

    /**
    * set Degrees of Longitude
    * @param degreesOfLongitude
    */
    public void setDegreesOfLongitude(String degreesOfLongitude) {
        this.degreesOfLongitude = degreesOfLongitude;
    }

    /**
     * 
     * @return byte array of Uncertainty code
     */
    public String getUncertaintyCode() {
        return uncertaintyCode;
    }

    /**
     * set Uncertainty code
     * @param uncertaintyCode
     */
    public void setUncertaintyCode(String uncertaintyCode) {
        this.uncertaintyCode = uncertaintyCode;
    }
	
    /**
     * decode GeographicalInformation byte array into non-asn object of GeographicalInformationMap
     * @param bytes
     * @return GeographicalInformationMap object
     * @throws InvalidInputException
     */
    public static GeographicalInformationMap decode(byte[] bytes) throws InvalidInputException{
        if(logger.isDebugEnabled()){
            logger.debug("in decode. GeographicalInformation byte array length::"+bytes.length);
        }
        GeographicalInformationMap gi = new GeographicalInformationMap();
        gi.typeOfShape = TypeOfShapeMapEnum.getValue((bytes[0]>>4)& 0x0f);
        gi.signOfLatitude = LatitudeSignMapEnum.getValue( (bytes[1]&0x80)>>7);
       	gi.degreesOfLatitudeHigh = MapFunctions.byteArrayToDegOfLatHigh(new byte[] {bytes[1]});
       	gi.degreesOfLatitudeLow = MapFunctions.byteArrayToDegOfLatLow(new byte[]{ bytes[2], bytes[3]});

       	gi.degreesOfLongitude = MapFunctions.byteArrayToDegOfLongitude(new byte[] {bytes[4], bytes[5], bytes[6]});

        gi.uncertaintyCode = String.valueOf(Integer.valueOf(bytes[7]&0xff));
        if(logger.isDebugEnabled()){
            logger.debug("GeographicalInformation non asn decoded successfully.");
        }
        return gi;
	    
    }
	
    /**
     * encode GeographicalInformation non asn into byte array
     * @param GeographicalInformationMap object
     * @return byte array of GeographicalInformation
     * @throws InvalidInputException
     */
    public static byte[] encode(GeographicalInformationMap gi) throws InvalidInputException{
        byte[] bytes = null;
        byte b0 = (byte)((gi.typeOfShape.getCode()<<4) & 0xff);
        byte[] lahigh = (new BigInteger(gi.degreesOfLatitudeHigh)).toByteArray();
        byte[] lalow = (new BigInteger(gi.degreesOfLatitudeLow)).toByteArray();
        byte[] lo = (new BigInteger(gi.degreesOfLongitude)).toByteArray();
        byte[] un = (new BigInteger(gi.uncertaintyCode)).toByteArray();
        bytes = new byte[]{b0,lahigh[0],lalow[0],lalow[1],lo[0],lo[1],lo[2],un[0]};
        if(logger.isDebugEnabled()){
            logger.debug("GeographicalInformation non asn encoded successfully. byte array length:"+bytes.length);
        }
        return bytes;
    }

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "GeographicalInformationMap [typeOfShape=" + typeOfShape
				+ ", degreesOfLatitudeHigh=" + degreesOfLatitudeHigh
				+ ", degreesOfLatitudeLow=" + degreesOfLatitudeLow
				+ ", degreesOfLongitude=" + degreesOfLongitude
				+ ", uncertaintyCode=" + uncertaintyCode + ", signOfLatitude="
				+ signOfLatitude + "]";
	}

}
