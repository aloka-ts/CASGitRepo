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

import com.agnity.cap.v3.datatypes.enumType.CarrierCapV3Enum; 
import com.agnity.cap.v3.exceptions.InvalidInputException;


public class CarrierCapV3 {

	private CarrierCapV3Enum carriercapv3enum;
	private static Logger logger = Logger.getLogger(CarrierCapV3.class);
	
	/**
	 * 
	 * @return CarrierCapV3Enum
	 */
	public CarrierCapV3Enum getCarriercapv3enum() {
		return carriercapv3enum;
	}

	/**
	 * 
	 * @param carriercapv3enum
	 */
	public void setCarriercapv3enum(CarrierCapV3Enum carriercapv3enum) {
		this.carriercapv3enum = carriercapv3enum;
	}
	
	/**
	 * decode byte array CarrierCap
	 * @param byte array CarrierCap
	 * @return CarrierCapV3 object
	 * @throws InvalidInputException
	 */
	public static CarrierCapV3 decode(byte[] bytes) throws InvalidInputException{
		CarrierCapV3 carrier = new CarrierCapV3();
		carrier.carriercapv3enum = CarrierCapV3Enum.getValue((byte)(bytes[0]));		
		return carrier;
	}

	/**
	 * encode into byte array of CarrierCap
	 * @param carrier
	 * @return byte array
	 * @throws InvalidInputException
	 */
	public static byte[] encode(CarrierCapV3 carrier) throws InvalidInputException
	{	
		byte byte1=(byte)carrier.carriercapv3enum.getCode();
		byte[] bytes = new byte[1];
		bytes[0]=byte1;
		return bytes;
	}
	
	@Override
	public String toString() {
		return "carriercapv3enum="+this.carriercapv3enum;
	}
	
	/******
	 ***
	 **
	For Unit Testing
	 * @throws InvalidInputException 
	 *****
	 ****
	 ***
	 */

	public static void main(String[] args) throws InvalidInputException {

		CarrierCapV3 decode= new CarrierCapV3();
		CarrierCapV3 decodeAG= new CarrierCapV3();


		byte[] Values = new byte[]{(byte)0x03};

		decode=CarrierCapV3.decode(Values);

		System.out.println("Carrier Type:"+decode.carriercapv3enum);
		

		byte[] Values2 =CarrierCapV3.encode(decode);
		
		decodeAG=CarrierCapV3.decode(Values2);	

		System.out.println("Carrier Type:"+decodeAG.carriercapv3enum);
		
	}

	
}
