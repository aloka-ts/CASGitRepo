package com.genband.isup.datatypes;

import java.util.Iterator;
import java.util.LinkedList;

import org.apache.log4j.Logger;

import com.genband.isup.enumdata.AddPrsntRestEnum;
import com.genband.isup.enumdata.AdditionalPartyCatNameEnum;
import com.genband.isup.enumdata.MobileAdditionalPartyCat2Enum;
import com.genband.isup.enumdata.NatureOfAddEnum;
import com.genband.isup.enumdata.NumIncmpltEnum;
import com.genband.isup.enumdata.NumPlanEnum;
import com.genband.isup.enumdata.ScreeningIndEnum;
import com.genband.isup.exceptions.InvalidInputException;
import com.genband.isup.util.Util;

/**
 * Used for encoding and decoding of Additional Party's Category
 * @author rarya
 *
 */
public class AdditionalPartyCat{
	
	
	private static Logger logger = Logger.getLogger(AdditionalPartyCat.class);	 
	 
	
	/**
	 * This function will encode the additional party's category.
	 * This Field have list of Additional party's category name and 
	 * additional party's category information. 
	 * @param catNameList
	 * @return encoded data byte[]
	 * @throws InvalidInputException 
	 */
	public static byte[] encodeAdditionalPartyCategory(LinkedList <AdditionalPartyCatPair> catNameList) throws InvalidInputException {
	
		logger.info("encodeAdditionalPartyCategory:Enter");
				
		if(catNameList.isEmpty()) {
			logger.info("encodeAdditionalPartyCategory:Exit, Error");
			return null;
		}
		// Allocate length = number of fields in category list * 2
		int i =0;
		byte[] myParms = new byte[catNameList.size()*2];		
		Iterator it = catNameList.iterator();
		
		while(it.hasNext()){
			AdditionalPartyCatPair catPair = (AdditionalPartyCatPair) it.next();
			myParms[i++] = (byte) catPair.getCategoryName().getCode();
			myParms[i++] = (byte) catPair.getCategoryField();
		}

		if(logger.isDebugEnabled())
			logger.debug("encodeAdditionalPartyCategory: Additional Party's Category" + Util.formatBytes(myParms));
		logger.info("encodeAdditionalPartyCategory:Exit");
		return myParms;
	}
	
	/**
	 * This function will decode Additional Party's category.
	 * @param data
	 * @return List of AdditionalPartyCatPair containing Additional Category Name and Category Info
	 * @throws InvalidInputException
	 */
	public static LinkedList<AdditionalPartyCatPair> decodeAdditionalPartyCategory(byte[] data) throws InvalidInputException{
		logger.info("decodeAdditionalPartyCategory: Enter");
		if(logger.isDebugEnabled())
			logger.debug("decodeAdditionalPartyCategory: Input--> data:" + Util.formatBytes(data));
		if(data == null){
			logger.error("decodeAdditionalPartyCategory: InvalidInputException(data is null)");
			throw new InvalidInputException("data is null");
		}
		LinkedList<AdditionalPartyCatPair> catPairList = new LinkedList<AdditionalPartyCatPair>();
		
		for(int offset=0; offset<data.length; offset=offset+2){
			AdditionalPartyCatPair catPair = new AdditionalPartyCatPair();
			int catName = data[offset]&0xFF;
			int catField = data[offset+1]&0xFF;
			catPair.setAdditionalPartyCatPair(catName, catField);
			catPairList.add(catPair);
		}
	
		logger.info("decodeAdditionalPartyCategory:Exit");
		
		return catPairList ;
	}
}
