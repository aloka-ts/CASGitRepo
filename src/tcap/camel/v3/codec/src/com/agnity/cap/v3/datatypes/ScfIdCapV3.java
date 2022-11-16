/****
 * Copyright (coffee) 2013 Agnity, Inc. All rights reserved.
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



import com.agnity.cap.v3.datatypes.enumType.EncodingSchemeCapV3Enum;
import com.agnity.cap.v3.datatypes.enumType.TypeOfDigitsCapV3Enum;
import com.agnity.cap.v3.exceptions.InvalidInputException;
import com.agnity.cap.v3.util.CapFunctions;




public class ScfIdCapV3 {



	private EncodingSchemeCapV3Enum encodingSchema;
	private TypeOfDigitsCapV3Enum typeOfDigits;
	private String digit;



	public String getAddress() {
		return digit;
	}


	public void setAddress(String digit) {
		this.digit = digit;
	}


	public EncodingSchemeCapV3Enum getEncodingschema() {
		return encodingSchema;
	}


	public void setEncodingSchema(EncodingSchemeCapV3Enum encodingSchema) {
		this.encodingSchema = encodingSchema;
	}


	public TypeOfDigitsCapV3Enum getTypeOfDigits() {
		return typeOfDigits;
	}


	public void setTypeOfDigits(TypeOfDigitsCapV3Enum typeOfDigits) {
		this.typeOfDigits = typeOfDigits;
	}

	private String decodeScfID(byte[] bytes){
		StringBuilder number = new StringBuilder();
		int length = bytes.length;
		for(int i=1;i<length;i++){
			number.append(Integer.toHexString((bytes[i]&0x0f)));
			if((((bytes[i]>>4)&0x0F)==0) && (i==(length-1))){
				// log here for break 0 
				break; 
			}
			number.append(Integer.toHexString(((bytes[i]>>4)&0x0F)));
		}
		return number.toString();
	}


	public static ScfIdCapV3 decode(byte[] bytes) throws InvalidInputException {
		ScfIdCapV3 ScfID = new ScfIdCapV3();
		ScfID.encodingSchema = EncodingSchemeCapV3Enum.getValue((byte) (bytes[0])&0x1f);
		ScfID.typeOfDigits = TypeOfDigitsCapV3Enum.getValue((byte) (bytes[0]>>5)&0x07);
		ScfID.digit = ScfID.decodeScfID(bytes);

		return ScfID;
	}


	public static byte[] encode(ScfIdCapV3 ScfID) throws InvalidInputException
	{	
		int tag1=ScfID.encodingSchema.getCode();
		int tag2=ScfID.typeOfDigits.getCode();
		byte byte1 = (byte)((tag2<<5)+tag1);
		String tag3=ScfID.digit;
		if(tag3.length()%2!=0){
			tag3 = tag3+"0";
		}
		tag3 = CapFunctions.encodenNumber(tag3);
		byte[] digitnumber = CapFunctions.hexStringToByteArray(tag3);
		int totalByteLength = digitnumber.length+1;
		byte[] bytes = new byte[totalByteLength];
		bytes[0]=byte1;
		for(int i=0;i<(totalByteLength-1);i++){
			bytes[i+1] = digitnumber[i];
		}
		return bytes;
	}
	
	@Override
	public String toString() {
		String bl = "\n";
		return "encodingSchema="+this.encodingSchema+bl+
		"typeOfDigits="+this.typeOfDigits;
	}
	
	/******
	 ***
	 **
	For Unit Testing
	 *****
	 ****
	 ***
	 */

	public static void main(String[] args) throws InvalidInputException {

		ScfIdCapV3 decode= new ScfIdCapV3();
		ScfIdCapV3 decodeAG= new ScfIdCapV3();


		byte[] Values = new byte[]{(byte)0x63,(byte)0x79,(byte)0x52,(byte)0x97,(byte)0x30,(byte)0x96,(byte)0x20};

		decode=ScfIdCapV3.decode(Values);

		System.out.println("Digit:"+decode.decodeScfID(Values));
		System.out.println("EncodingSchemeCapV2Enum :" +decode.encodingSchema);
		System.out.println("TypeofDigitsCapV2Enum :" +decode.typeOfDigits);
		

		byte[] Values2 =ScfIdCapV3.encode(decode);
		
		decodeAG=ScfIdCapV3.decode(Values2);	

		System.out.println("Digit:"+decodeAG.decodeScfID(Values));
		System.out.println("EncodingSchemeCapV2Enum :" +decodeAG.encodingSchema);
		System.out.println("TypeOfDigitsCapV2Enum :" +decodeAG.typeOfDigits);

	}
}
