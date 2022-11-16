package com.agnity.win.datatypes;

import org.apache.log4j.Logger;

import com.agnity.win.exceptions.InvalidInputException;
import com.agnity.win.util.Util;

/*
 * This class provides encode and decode methods for OriginationTriggers.
 * The OriginationTriggers (ORIGTRIG) parameter defines the origination 
 * trigger points that are currently active for the subscriber
 */
public class NonAsnOriginationTriggers {

	private static Logger logger = Logger.getLogger(NonAsnOriginationTriggers.class);

	boolean isAllOriginationSet;
	boolean isLocalSet;
	boolean isIntraLataTollSet;
	boolean isInterLataTollSet;
	boolean isInternaltionalSet;
	boolean isWorldZoneSet;
	boolean isUnrecognizedNumSet;
	boolean isRevertiveCallSet;
	boolean isStarSet;
	boolean isDoubleStarSet;
	boolean isPoundSet;
	boolean isDoublePoundSet;
	boolean isPriorAgreementSet;
	boolean isNoDigitsSet;
	boolean isOneDigitSet;
	boolean isTwoDigitSet;
	boolean isThreeDigitSet;
	boolean isFourDigitSet;
	boolean isFiveDigitSet;
	boolean isSixDigitSet;
	boolean isSevenDigitSet;
	boolean isEightDigitSet;
	boolean isNineDigitSet;
	boolean isTenDigitSet;
	boolean isElevenDigitSet;
	boolean isTwelveDigitSet;
	boolean isThirteenDigitSet;
	boolean isFourteenDigitSet;
	boolean isFifteenDigitSet;

	/**
	 * This function will decode OriginationTriggers as per specification TIA-EIA-41-D
	 * section 6.5.2.90
	 * @param data
	 * @return decoded Digits
	 * @throws InvalidInputException 
	 */
	public static NonAsnOriginationTriggers decodeOriginationTriggers(byte[] data) throws InvalidInputException {
		if(logger.isDebugEnabled())
			logger.debug("NonAsnOriginationTriggers: Input--> data:" + Util.formatBytes(data));

		if(data == null){
			logger.error("NonAsnOriginationTriggers: InvalidInputException(Input data(bytes) not present or null)");
			throw new InvalidInputException("Input data(bytes) not present or null");
		}

		NonAsnOriginationTriggers ot = new NonAsnOriginationTriggers();

		if(data.length > 1)
			decodeFirstOctet(data[0], ot);

		if(data.length > 2)
			decodeSecondOctet(data[1], ot);

		if(data.length > 3)
			decodeThirdOctet(data[2], ot);

		if(data.length > 4)
			decodeFourthOctet(data[3], ot);

		if(logger.isDebugEnabled())
			logger.debug("decodeOriginationTriggers: :" + ot);

		return ot;
	}

	/**
	 * This function will encode Origination Triggers as per specification TIA-EIA-41-D
	 * section 6.5.2.90
	 * @param mc   - manufacturerCode
	 * @param sn   - SerialNum
	 * @return byte[] of encoded ECN
	 * @throws InvalidInputException 
	 */
	public static byte[] encodeOriginationTriggers(NonAsnOriginationTriggers ot) throws InvalidInputException{
		if(logger.isDebugEnabled())
			logger.info("encodeOriginationTrigger:" + ot );

		byte[] myParams = new byte[4];

		myParams[0] = encodeFirstOctet(ot);
		myParams[1] = encodeSecondOctet(ot);
		myParams[2] = encodeThirdOctet(ot);
		myParams[3] = encodeFourthOctet(ot);

		if(logger.isDebugEnabled())
			logger.debug("encodeOriginationTriggers: Output--> data:" + Util.formatBytes(myParams));

		return myParams;
	}

	/**
	 * This function will decode First Octet 
	 * @param val - first octet
	 * @param ot  - NonAsnOriginationTriggers to be filled in
	 * @return void
	 * @throws InvalidInputException 
	 */
	private static void decodeFirstOctet(byte val, NonAsnOriginationTriggers ot){
		if(logger.isDebugEnabled())
			logger.debug("decodeFirstOctet: Input--> data:" + val);

		if((val&0x01)== 1)
			ot.isAllOriginationSet=true;

		if((val&0x02) == 1)
			ot.isLocalSet = true;

		if((val&0x04) == 1)
			ot.isIntraLataTollSet = true; 

		if((val&0x08) == 1) 
			ot.isInterLataTollSet = true;

		if((val&0x10) == 1)
			ot.isInternaltionalSet = true;

		if((val&0x20) == 1)
			ot.isWorldZoneSet = true;

		if((val&0x40) == 1)
			ot.isUnrecognizedNumSet = true;

		if((val&0x80) == 1)
			ot.isRevertiveCallSet = true;
	}

	/**
	 * This function will decode Second Octet 
	 * @param val - second octet
	 * @param ot  - NonAsnOriginationTriggers to be filled in
	 * @return void
	 * @throws InvalidInputException 
	 */
	private static void decodeSecondOctet(byte val, NonAsnOriginationTriggers ot){

		if(logger.isDebugEnabled())
			logger.debug("decodeSecondOctet: Input--> data:" + val);

		if((val&0x01)== 1)
			ot.isStarSet=true;

		if((val&0x02) == 1)
			ot.isDoubleStarSet = true;

		if((val&0x04) == 1)
			ot.isPoundSet = true; 

		if((val&0x08) == 1) 
			ot.isDoublePoundSet = true;

		if((val&0x10) == 1)
			ot.isPriorAgreementSet = true;

	}

	/**
	 * This function will decode Third Octet 
	 * @param val - Third octet
	 * @param ot  - NonAsnOriginationTriggers to be filled in
	 * @return void
	 * @throws InvalidInputException 
	 */
	private static void decodeThirdOctet(byte val, NonAsnOriginationTriggers ot){

		if(logger.isDebugEnabled())
			logger.debug("decodeThirdOctet: Input--> data:" + val);

		if((val&0x01)== 1)
			ot.isNoDigitsSet=true;

		if((val&0x02) == 1)
			ot.isOneDigitSet = true;

		if((val&0x04) == 1)
			ot.isTwoDigitSet = true; 

		if((val&0x08) == 1) 
			ot.isThreeDigitSet = true;

		if((val&0x10) == 1)
			ot.isFourDigitSet = true;

		if((val&0x20) == 1)
			ot.isFiveDigitSet = true;

		if((val&0x40) == 1)
			ot.isSixDigitSet = true;

		if((val&0x80) == 1)
			ot.isSevenDigitSet = true;

	}

	/**
	 * This function will decode Fourth Octet 
	 * @param val - Fourth octet
	 * @param ot  - NonAsnOriginationTriggers to be filled in
	 * @return void
	 * @throws InvalidInputException 
	 */
	private static void decodeFourthOctet(byte val, NonAsnOriginationTriggers ot){

		if(logger.isDebugEnabled())
			logger.debug("decodeFourthOctet: Input--> data:" + val);

		if((val&0x01)== 1)
			ot.isEightDigitSet=true;

		if((val&0x02) == 1)
			ot.isNineDigitSet = true;

		if((val&0x04) == 1)
			ot.isTenDigitSet = true; 

		if((val&0x08) == 1) 
			ot.isElevenDigitSet = true;

		if((val&0x10) == 1)
			ot.isTwelveDigitSet = true;

		if((val&0x20) == 1)
			ot.isThirteenDigitSet = true;

		if((val&0x40) == 1)
			ot.isFourteenDigitSet = true;

		if((val&0x80) == 1)
			ot.isFifteenDigitSet = true;

	}

	/**
	 * This function will encode First Octet 
	 * @param ot  - NonAsnOriginationTriggers to be filled in
	 * @return byte
	 * @throws InvalidInputException 
	 */
	private static byte encodeFirstOctet(NonAsnOriginationTriggers ot){
		if(logger.isDebugEnabled())
			logger.debug("encodeFirstOctet: Input--> data:" + ot);

		byte val = 0;

		if(ot.isAllOriginationSet)
			val |= 0x01;

		if(ot.isLocalSet)
			val |= 0x02;

		if(ot.isIntraLataTollSet)
			val |= 0x04;

		if(ot.isInterLataTollSet)
			val |= 0x08;

		if(ot.isInternaltionalSet)
			val |= 0x10;

		if(ot.isWorldZoneSet)
			val |= 0x20;

		if(ot.isUnrecognizedNumSet)
			val |= 0x40;

		if(ot.isRevertiveCallSet)
			val |= 0x80;

		return val;
	}

	/**
	 * This function will encode Second Octet 
	 * @param ot  - NonAsnOriginationTriggers to be filled in
	 * @return byte
	 * @throws InvalidInputException 
	 */
	private static byte encodeSecondOctet(NonAsnOriginationTriggers ot){

		if(logger.isDebugEnabled())
			logger.debug("encodeSecondOctet: Input--> data:" + ot);

		byte val = 0;
		if(ot.isStarSet)
			val |= 0x01;

		if(ot.isDoubleStarSet)
			val |= 0x02;

		if(ot.isPoundSet)
			val |= 0x04;

		if(ot.isDoublePoundSet)
			val |= 0x08;

		if(ot.isPriorAgreementSet)
			val |= 0x10;

		return val;
	}

	/**
	 * This function will encode Third Octet 
	 * @param ot  - NonAsnOriginationTriggers to be filled in
	 * @return byte
	 * @throws InvalidInputException 
	 */
	private static byte encodeThirdOctet(NonAsnOriginationTriggers ot){

		if(logger.isDebugEnabled())
			logger.debug("encodeThirdOctet: Input--> data:" + ot);

		byte val =0;
		if(ot.isNoDigitsSet)
			val |= 0x01;

		if(ot.isOneDigitSet)
			val |= 0x02;

		if(ot.isTwoDigitSet)
			val |= 0x04;

		if(ot.isThreeDigitSet)
			val |= 0x08;

		if(ot.isFourDigitSet)
			val |= 0x10;

		if(ot.isFiveDigitSet)
			val |= 0x20;

		if(ot.isSixDigitSet)
			val |=0x40;

		if(ot.isSevenDigitSet)
			val |= 0x80;

		return val;
	}

	/**
	 * This function will encode Fourth Octet 
	 * @param ot  - NonAsnOriginationTriggers to be filled in
	 * @return byte
	 * @throws InvalidInputException 
	 */
	private static byte encodeFourthOctet(NonAsnOriginationTriggers ot){

		if(logger.isDebugEnabled())
			logger.debug("encodeFourthOctet: Input--> data:" + ot);

		byte val =0;
		if(ot.isEightDigitSet)
			val |= 0x01;

		if(ot.isNineDigitSet)
			val |=0x20;

		if(ot.isTenDigitSet)
			val |= 0x40;

		if(ot.isElevenDigitSet)
			val |= 0x08;

		if(ot.isTwelveDigitSet)
			val |= 0x10;

		if(ot.isThirteenDigitSet)
			val |= 0x20;

		if(ot.isFourteenDigitSet)
			val |= 0x40;

		if(ot.isFifteenDigitSet)
			val |= 0x80;

		return val;

	}

	public boolean isAllOriginationSet() {
		return isAllOriginationSet;
	}

	public void setAllOriginationSet(boolean isAllOriginationSet) {
		this.isAllOriginationSet = isAllOriginationSet;
	}

	public boolean isLocalSet() {
		return isLocalSet;
	}

	public void setLocalSet(boolean isLocalSet) {
		this.isLocalSet = isLocalSet;
	}

	public boolean isIntraLataTollSet() {
		return isIntraLataTollSet;
	}

	public void setIntraLataTollSet(boolean isIntraLataTollSet) {
		this.isIntraLataTollSet = isIntraLataTollSet;
	}

	public boolean isInterLataTollSet() {
		return isInterLataTollSet;
	}

	public void setInterLataTollSet(boolean isInterLataTollSet) {
		this.isInterLataTollSet = isInterLataTollSet;
	}

	public boolean isInternaltionalSet() {
		return isInternaltionalSet;
	}

	public void setInternaltionalSet(boolean isInternaltionalSet) {
		this.isInternaltionalSet = isInternaltionalSet;
	}

	public boolean isWorldZoneSet() {
		return isWorldZoneSet;
	}

	public void setWorldZoneSet(boolean isWorldZoneSet) {
		this.isWorldZoneSet = isWorldZoneSet;
	}

	public boolean isUnrecognizedNumSet() {
		return isUnrecognizedNumSet;
	}

	public void setUnrecognizedNumSet(boolean isUnrecognizedNumSet) {
		this.isUnrecognizedNumSet = isUnrecognizedNumSet;
	}

	public boolean isRevertiveCallSet() {
		return isRevertiveCallSet;
	}

	public void setRevertiveCallSet(boolean isRevertiveCallSet) {
		this.isRevertiveCallSet = isRevertiveCallSet;
	}

	public boolean isStarSet() {
		return isStarSet;
	}

	public void setStarSet(boolean isStarSet) {
		this.isStarSet = isStarSet;
	}

	public boolean isDoubleStarSet() {
		return isDoubleStarSet;
	}

	public void setDoubleStarSet(boolean isDoubleStarSet) {
		this.isDoubleStarSet = isDoubleStarSet;
	}

	public boolean isPoundSet() {
		return isPoundSet;
	}

	public void setPoundSet(boolean isPoundSet) {
		this.isPoundSet = isPoundSet;
	}

	public boolean isDoublePoundSet() {
		return isDoublePoundSet;
	}

	public void setDoublePoundSet(boolean isDoublePoundSet) {
		this.isDoublePoundSet = isDoublePoundSet;
	}

	public boolean isPriorAgreementSet() {
		return isPriorAgreementSet;
	}

	public void setPriorAgreementSet(boolean isPriorAgreementSet) {
		this.isPriorAgreementSet = isPriorAgreementSet;
	}

	public boolean isNoDigitsSet() {
		return isNoDigitsSet;
	}

	public void setNoDigitsSet(boolean isNoDigitsSet) {
		this.isNoDigitsSet = isNoDigitsSet;
	}

	public boolean isOneDigitSet() {
		return isOneDigitSet;
	}

	public void setOneDigitSet(boolean isOneDigitSet) {
		this.isOneDigitSet = isOneDigitSet;
	}

	public boolean isTwoDigitSet() {
		return isTwoDigitSet;
	}

	public void setTwoDigitSet(boolean isTwoDigitSet) {
		this.isTwoDigitSet = isTwoDigitSet;
	}

	public boolean isThreeDigitSet() {
		return isThreeDigitSet;
	}

	public void setThreeDigitSet(boolean isThreeDigitSet) {
		this.isThreeDigitSet = isThreeDigitSet;
	}

	public boolean isFourDigitSet() {
		return isFourDigitSet;
	}

	public void setFourDigitSet(boolean isFourDigitSet) {
		this.isFourDigitSet = isFourDigitSet;
	}

	public boolean isFiveDigitSet() {
		return isFiveDigitSet;
	}

	public void setFiveDigitSet(boolean isFiveDigitSet) {
		this.isFiveDigitSet = isFiveDigitSet;
	}

	public boolean isSixDigitSet() {
		return isSixDigitSet;
	}

	public void setSixDigitSet(boolean isSixDigitSet) {
		this.isSixDigitSet = isSixDigitSet;
	}

	public boolean isSevenDigitSet() {
		return isSevenDigitSet;
	}

	public void setSevenDigitSet(boolean isSevenDigitSet) {
		this.isSevenDigitSet = isSevenDigitSet;
	}

	public boolean isEightDigitSet() {
		return isEightDigitSet;
	}

	public void setEightDigitSet(boolean isEightDigitSet) {
		this.isEightDigitSet = isEightDigitSet;
	}

	public boolean isNineDigitSet() {
		return isNineDigitSet;
	}

	public void setNineDigitSet(boolean isNineDigitSet) {
		this.isNineDigitSet = isNineDigitSet;
	}

	public boolean isTenDigitSet() {
		return isTenDigitSet;
	}

	public void setTenDigitSet(boolean isTenDigitSet) {
		this.isTenDigitSet = isTenDigitSet;
	}

	public boolean isElevenDigitSet() {
		return isElevenDigitSet;
	}

	public void setElevenDigitSet(boolean isElevenDigitSet) {
		this.isElevenDigitSet = isElevenDigitSet;
	}

	public boolean isTwelveDigitSet() {
		return isTwelveDigitSet;
	}

	public void setTwelveDigitSet(boolean isTwelveDigitSet) {
		this.isTwelveDigitSet = isTwelveDigitSet;
	}

	public boolean isThirteenDigitSet() {
		return isThirteenDigitSet;
	}

	public void setThirteenDigitSet(boolean isThirteenDigitSet) {
		this.isThirteenDigitSet = isThirteenDigitSet;
	}

	public boolean isFourteenDigitSet() {
		return isFourteenDigitSet;
	}

	public void setFourteenDigitSet(boolean isFourteenDigitSet) {
		this.isFourteenDigitSet = isFourteenDigitSet;
	}

	public boolean isFifteenDigitSet() {
		return isFifteenDigitSet;
	}

	public void setFifteenDigitSet(boolean isFifteenDigitSet) {
		this.isFifteenDigitSet = isFifteenDigitSet;
	}

	public String toString() {
		String obj = "isAllOriginationSet" + isAllOriginationSet +
		"isLocalSet" + isLocalSet + "isIntraLataTollSet" + isIntraLataTollSet + 
		"isInterLataTollSet" + isInterLataTollSet + "isInternaltionalSet" + isInternaltionalSet +
		"isWorldZoneSet" + isWorldZoneSet + "isUnrecognizedNumSet" + isUnrecognizedNumSet +
		"isRevertiveCallSet" + isRevertiveCallSet +"isStarSet" + isStarSet +
		"isDoubleStarSet" + isDoubleStarSet + "isPoundSet" + isPoundSet +
		"isDoublePoundSet" + isDoublePoundSet+ "isPriorAgreementSet" + isPriorAgreementSet +
		"isNoDigitsSet" + isNoDigitsSet + "isOneDigitSet" + isOneDigitSet +
		"isTwoDigitSet" + isTwoDigitSet + "isThreeDigitSet" + isThreeDigitSet +
		"isFourDigitSet" + isFourDigitSet + "isFiveDigitSet" + isFiveDigitSet+ 
		"isSixDigitSet" + isSixDigitSet + "isSevenDigitSet" + isSevenDigitSet+ 
		"isEightDigitSet" + isEightDigitSet+  "isNineDigitSet" + isNineDigitSet+ 
		"isTenDigitSet" + isTenDigitSet +  "isElevenDigitSet" +isElevenDigitSet+
		"isTwelveDigitSet" + isTwelveDigitSet+ "isThirteenDigitSet" + isThirteenDigitSet+ 
		"isFourteenDigitSet" + isFourteenDigitSet+ "isFifteenDigitSet" +isFifteenDigitSet;

		return obj;	
	}
}
