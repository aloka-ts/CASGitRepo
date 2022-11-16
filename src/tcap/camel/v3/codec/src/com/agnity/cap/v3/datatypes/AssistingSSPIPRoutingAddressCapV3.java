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


import com.agnity.cap.v3.datatypes.enumType.AddressPresentationRestricatedIndicatorCapV2Enum;
import com.agnity.cap.v3.datatypes.enumType.NIIndicatorCapV3Enum;
import com.agnity.cap.v3.datatypes.enumType.NumberQualifierIndicatorCapV3Enum;
import com.agnity.cap.v3.datatypes.enumType.OddEvenIndicatorCapV3Enum;
import com.agnity.cap.v3.datatypes.enumType.NatureOfAddressIndicatorCapV3Enum;
import com.agnity.cap.v3.datatypes.enumType.NumberingOfPlanIndicatorCapV3Enum;
import com.agnity.cap.v3.datatypes.enumType.ScreeningIndicatorCapV3Enum;
import com.agnity.cap.v3.exceptions.InvalidInputException;
import com.agnity.cap.v3.util.CapFunctions;



public class AssistingSSPIPRoutingAddressCapV3 {


	private NumberQualifierIndicatorCapV3Enum numberQualifierIndicator;
	private OddEvenIndicatorCapV3Enum oddEvenIndicator;
	private NatureOfAddressIndicatorCapV3Enum natureOfAddressIndicator;
	private NIIndicatorCapV3Enum niIndicator;
	private NumberingOfPlanIndicatorCapV3Enum numberingPlanIndicator;
	private AddressPresentationRestricatedIndicatorCapV2Enum  addressPresentationRestrictedIndicator;
	private ScreeningIndicatorCapV3Enum screeningIndicator;
	private String genericNumber;


	public NumberQualifierIndicatorCapV3Enum getnumberQualifierIndicator() {
		return numberQualifierIndicator;
	}	
	public OddEvenIndicatorCapV3Enum getOddEvenIndicator() {
		return oddEvenIndicator;
	}

	public NatureOfAddressIndicatorCapV3Enum getNatureOfAddressIndicator() {
		return natureOfAddressIndicator;
	}

	public NIIndicatorCapV3Enum getnumberIncompleteIndicator() {
		return niIndicator;
	}

	public NumberingOfPlanIndicatorCapV3Enum getNumberingPlanIndicator() {
		return numberingPlanIndicator;
	}

	public AddressPresentationRestricatedIndicatorCapV2Enum getAddressPresentationRestrictedIndicator() {
		return addressPresentationRestrictedIndicator;
	}
	public ScreeningIndicatorCapV3Enum getscreeningIndicator() {
		return screeningIndicator;
	}

	public String getGenericNumber() {
		return genericNumber;
	}


	public void setNumberQualifierIndicatorCapV2Enum (NumberQualifierIndicatorCapV3Enum numberQualifierIndicator){
		this.numberQualifierIndicator=numberQualifierIndicator ;
	}		

	public void setOddEvenIndicator(OddEvenIndicatorCapV3Enum oddEvenIndicator) {
		this.oddEvenIndicator = oddEvenIndicator;
	}

	public void setNatureOfAddressIndicator(
			NatureOfAddressIndicatorCapV3Enum natureOfAddressIndicator) {
		this.natureOfAddressIndicator = natureOfAddressIndicator;
	}

	public void setniIndicator(NIIndicatorCapV3Enum niIndicator) {
		this.niIndicator = niIndicator;
	}

	public void setAddressPresentationRestrictedIndicator(
			AddressPresentationRestricatedIndicatorCapV2Enum  addressPresentationRestrictedIndicator) {
		this.addressPresentationRestrictedIndicator = addressPresentationRestrictedIndicator;
	}

	public void setNumberingPlanIndicator(
			NumberingOfPlanIndicatorCapV3Enum numberingPlanIndicator) {
		this.numberingPlanIndicator = numberingPlanIndicator;
	}
	public void setScreeningIndicator(
			ScreeningIndicatorCapV3Enum screeningIndicator) {
		this.screeningIndicator = screeningIndicator;
	}

	public void setGenericNumber(String GenericNumber) {
		this.genericNumber = GenericNumber;
	}

	private String decodeGenericNumber(byte[] bytes){
		StringBuilder number = new StringBuilder();
		int length = bytes.length;
		for(int i=3;i<length;i++){
			number.append(Integer.toHexString((bytes[i]&0x0f)));
			if((((bytes[i]>>4)&0x0F)==0) && (i==(length-1))){
				// log here for break 0 
				break; 
			}
			number.append(Integer.toHexString(((bytes[i]>>4)&0x0F)));
		}
		return number.toString();
	}


	public static AssistingSSPIPRoutingAddressCapV3 decode(byte[] bytes) throws InvalidInputException {
		AssistingSSPIPRoutingAddressCapV3 GNCapV2 = new AssistingSSPIPRoutingAddressCapV3();
		GNCapV2.numberQualifierIndicator = NumberQualifierIndicatorCapV3Enum.getValue((byte) (bytes[0])&0xff);
		GNCapV2.oddEvenIndicator = OddEvenIndicatorCapV3Enum.getValue((byte) (bytes[1]>>7)&0x01);
		GNCapV2.natureOfAddressIndicator = NatureOfAddressIndicatorCapV3Enum.getValue((byte) (bytes[1]&0x7F));
		GNCapV2.niIndicator = NIIndicatorCapV3Enum.getValue((byte)(bytes[2]>>7)&0x01);
		GNCapV2.numberingPlanIndicator = NumberingOfPlanIndicatorCapV3Enum.getValue((byte) ((bytes[2]&0x7F)>>4));	   
		GNCapV2.addressPresentationRestrictedIndicator = AddressPresentationRestricatedIndicatorCapV2Enum.getValue((byte) ((bytes[2]&0x0f)>>2));
		GNCapV2.screeningIndicator = ScreeningIndicatorCapV3Enum.getValue((byte) (bytes[2])&0x03);
		GNCapV2.genericNumber = GNCapV2.decodeGenericNumber(bytes);

		return GNCapV2;
	}


	public static byte[] encode(AssistingSSPIPRoutingAddressCapV3 GNCapV2) throws InvalidInputException{	
		byte byte1=(byte)GNCapV2.numberQualifierIndicator.getcode();
		int tag2=GNCapV2.oddEvenIndicator.getCode();
		int tag3=GNCapV2.natureOfAddressIndicator.getCode();
		byte byte2 = (byte)((tag2<<7)+tag3);
		int tag4=GNCapV2.niIndicator.getCode();
		int tag5= GNCapV2.numberingPlanIndicator.getCode();
		int tag6= GNCapV2.addressPresentationRestrictedIndicator.getCode();
		int tag7=  GNCapV2.screeningIndicator.getCode();
		byte byte3 = (byte)(((((((tag4<<3)+tag5)<<2)+tag6)<<2)+tag7));
		String tag8=GNCapV2.genericNumber;
		if(tag8.length()%2!=0){
			tag8 = tag8+"0";
		}
		tag8 = CapFunctions.encodenNumber(tag8);
		byte[] GenericNumBytes = CapFunctions.hexStringToByteArray(tag8);
		int totalByteLength = GenericNumBytes.length+3;
		byte[] bytes = new byte[totalByteLength];
		bytes[0]=byte1;
		bytes[1]=byte2;
		bytes[2]=byte3;
		for(int i=0;i<(totalByteLength-3);i++){
			bytes[i+3] = GenericNumBytes[i];
		}
		return bytes;
	}
	
	@Override
	public String toString() {
		String bl = "\n";
		return "numberQualifierIndicator="+this.numberQualifierIndicator+bl+
		"oddEvenIndicator="+this.oddEvenIndicator+bl+"natureOfAddressIndicator="+this.natureOfAddressIndicator+bl
		+"niIndicator+"+this.niIndicator+bl+"numberingPlanIndicator="+this.numberingPlanIndicator
		+bl+"addressPresentationRestrictedIndicator="+this.addressPresentationRestrictedIndicator+bl
		+"screeningIndicator="+this.screeningIndicator;
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

		AssistingSSPIPRoutingAddressCapV3 decode= new AssistingSSPIPRoutingAddressCapV3();
		AssistingSSPIPRoutingAddressCapV3 decodeAG= new AssistingSSPIPRoutingAddressCapV3();


		byte[] Values = new byte[]{(byte)0x06,(byte)0x04,(byte)0x13,(byte)0x79,(byte)0x52,(byte)0x97,(byte)0x30,(byte)0x96,(byte)0x20};

		decode=AssistingSSPIPRoutingAddressCapV3.decode(Values);

		System.out.println("GenericNumber:"+decode.decodeGenericNumber(Values));
		System.out.println("NumberQualifierIndicatorCapV2Enum :" +decode.numberQualifierIndicator);
		System.out.println("OddEvenIndicatorCapV2Enum :" +decode.oddEvenIndicator);
		System.out.println("NatureOfAddressIndicatorCapV2Enum :" +decode.natureOfAddressIndicator);
		System.out.println("NumberIncompleteIndicatorCapV2Enum :" +decode.niIndicator);
		System.out.println("NumberingOfPlanIndicatorCapV2Enum :" +decode.numberingPlanIndicator);
		System.out.println("AddressPresentationRestrictedIndicatorCapV2Enum :" +decode.addressPresentationRestrictedIndicator);
		System.out.println("ScreeningIndicatorCapV2Enum :" +decode.screeningIndicator);
		System.out.println(""+(byte)decode.numberQualifierIndicator.getcode());

		byte[] Values2 =AssistingSSPIPRoutingAddressCapV3.encode(decode);
		
		decodeAG=AssistingSSPIPRoutingAddressCapV3.decode(Values2);	

		System.out.println("GenericNumber:"+decodeAG.decodeGenericNumber(Values2));
		System.out.println("NumberQualifierIndicatorCapV2Enum :" +decodeAG.numberQualifierIndicator);
		System.out.println("OddEvenIndicatorCapV2Enum :" +decodeAG.oddEvenIndicator);
		System.out.println("NatureOfAddressIndicatorCapV2Enum :" +decodeAG.natureOfAddressIndicator);
		System.out.println("NumberIncompleteIndicatorCapV2Enum :" +decodeAG.niIndicator);
		System.out.println("NumberingOfPlanIndicatorCapV2Enum :" +decodeAG.numberingPlanIndicator);
		System.out.println("AddressPresentationRestrictedIndicatorCapV2Enum :" +decodeAG.addressPresentationRestrictedIndicator);
		System.out.println("ScreeningIndicatorCapV2Enum :" +decodeAG.screeningIndicator);
		System.out.println(""+(byte)decodeAG.numberQualifierIndicator.getcode());

	}
}
