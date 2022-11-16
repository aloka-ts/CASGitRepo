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
package com.agnity.cap.v2.datatypes;



import com.agnity.cap.v2.datatypes.enumType.EncodingSchemeCapV2Enum;
import com.agnity.cap.v2.datatypes.enumType.TypeOfDigitsCapV2Enum;
import com.agnity.cap.v2.exceptions.InvalidInputException;
import com.agnity.cap.v2.util.CapFunctions;




public class ScfIdCapV2 {



	private EncodingSchemeCapV2Enum encodingSchema;
	private TypeOfDigitsCapV2Enum typeOfDigits;
	private String digit;



	public String getAddress() {
		return digit;
	}


	public void setAddress(String digit) {
		this.digit = digit;
	}


	public EncodingSchemeCapV2Enum getEncodingschema() {
		return encodingSchema;
	}


	public void setEncodingSchema(EncodingSchemeCapV2Enum encodingSchema) {
		this.encodingSchema = encodingSchema;
	}


	public TypeOfDigitsCapV2Enum getTypeOfDigits() {
		return typeOfDigits;
	}


	public void setTypeOfDigits(TypeOfDigitsCapV2Enum typeOfDigits) {
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


	public static ScfIdCapV2 decode(byte[] bytes) throws InvalidInputException {
		ScfIdCapV2 ScfID = new ScfIdCapV2();
		ScfID.encodingSchema = EncodingSchemeCapV2Enum.getValue((byte) (bytes[0])&0x1f);
		ScfID.typeOfDigits = TypeOfDigitsCapV2Enum.getValue((byte) (bytes[0]>>5)&0x07);
		ScfID.digit = ScfID.decodeScfID(bytes);

		return ScfID;
	}


	public static byte[] encode(ScfIdCapV2 ScfID) throws InvalidInputException
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

		ScfIdCapV2 decode= new ScfIdCapV2();
		ScfIdCapV2 decodeAG= new ScfIdCapV2();


		byte[] Values = new byte[]{(byte)0x63,(byte)0x79,(byte)0x52,(byte)0x97,(byte)0x30,(byte)0x96,(byte)0x20};

		decode=ScfIdCapV2.decode(Values);

		System.out.println("Digit:"+decode.decodeScfID(Values));
		System.out.println("EncodingSchemeCapV2Enum :" +decode.encodingSchema);
		System.out.println("TypeofDigitsCapV2Enum :" +decode.typeOfDigits);
		

		byte[] Values2 =ScfIdCapV2.encode(decode);
		
		decodeAG=ScfIdCapV2.decode(Values2);	

		System.out.println("Digit:"+decodeAG.decodeScfID(Values));
		System.out.println("EncodingSchemeCapV2Enum :" +decodeAG.encodingSchema);
		System.out.println("TypeOfDigitsCapV2Enum :" +decodeAG.typeOfDigits);

	}
}
