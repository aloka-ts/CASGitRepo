package com.genband.isup.datatypes;

import org.apache.log4j.Logger;

import com.genband.isup.enumdata.AdditionalPartyCat1Enum;
import com.genband.isup.enumdata.AdditionalPartyCatNameEnum;
import com.genband.isup.enumdata.MobileAdditionalPartyCat1Enum;
import com.genband.isup.enumdata.MobileAdditionalPartyCat2Enum;

/**
 * Used for encoding and decoding of Additional Party's Category Pair
 * @author rarya
 *
 */
public class AdditionalPartyCatPair{
	
	/**
	 * @see AdditionalPartyCatNameEnum
	 */
	AdditionalPartyCatNameEnum categoryName ; 
	/**
	 * @see MobileAdditionalPartyCat2Enum
	 */
	MobileAdditionalPartyCat2Enum category2;
	/**
	 * @see MobileAdditionalPartyCat1Enum
	 */
	MobileAdditionalPartyCat1Enum category1;
	/**
	 * @see AdditionalPartyCat1Enum
	 */
	AdditionalPartyCat1Enum addCategory1;
	
	private int fieldId=0 ;
	
	private static Logger logger = Logger.getLogger(AdditionalPartyCat.class);	 
	 
	/**
	 * This function set Additional Party's Category Name and Additional Party Info.
	 * Additional Party Info MUST resemble with Adiitional Category Name.  
	 * No verification is provided by this API.
	 * @param catName
	 * @param catField
	 * @return 
	 */
	public void setAdditionalPartyCatPair(int catName, int catField) {
		categoryName = AdditionalPartyCatNameEnum.fromInt(catName);
		switch(catName){
		case 252:
			category2 = MobileAdditionalPartyCat2Enum.fromInt(catField);
			fieldId =2;
			break;
		case 253:
			category1 = MobileAdditionalPartyCat1Enum.fromInt(catField);	
			fieldId =1;
			break;
		case 254:
			addCategory1 = AdditionalPartyCat1Enum.fromInt(catField);
			fieldId =3;
		break;
		}
	}

	/**
	 * This function returns Additional Party's Category Name if already set
	 * else return null.
	 * @return AdditionalPartyCatNameEnum
	 */
	public AdditionalPartyCatNameEnum getCategoryName() {
		if(fieldId==0)
			return null;

		return categoryName;
	}

	/**
	 * This function returns Mobile Additional Party's Category 2 if set.
	 * else return null.
	 * @return MobileAdditionalPartyCat2Enum
	 */
	public MobileAdditionalPartyCat2Enum getMobileCategory2Field() {
		if(fieldId == 2) 
			return category2;

			return null;
	}

	/**
	 * This function returns Mobile Additional Party's Category 1 if set.
	 * else return null.
	 * @return MobileAdditionalPartyCat1Enum
	 */
	public MobileAdditionalPartyCat1Enum getMobileCategory1Field() {
		if(fieldId == 1) 
			return category1;

			return null;
	}

	/**
	 * This function returns PSTN additional Party's Category 1 if set
	 * else return null.
	 * @return AdditionalPartyCat1Enum
	 */
	public AdditionalPartyCat1Enum getPSTNCategoryField() {
		if(fieldId == 3) 
			return addCategory1;

			return null;
	}

	/**
	 * This function returns integer value of Additional Party's Category Field
	 * User shall make out based on category name. 
	 * @return int
	 */
	public int getCategoryField() {
		int retVal = 0;
		if(fieldId != 0) {
			if(categoryName == categoryName.MOB_ADD_PARTY_CAT_1){
				retVal = category1.getCode();
			}
			else if(categoryName == categoryName.MOB_ADD_PARTY_CAT_2){
				retVal = category2.getCode();
			}
			else if(categoryName == categoryName.PSTN_ADD_PARTY_CAT_1){
				retVal = addCategory1.getCode();
			}
		}
			return retVal;
	}

	/**
	 * This function is used to set MobileAdditionalPartyCat2Enum.
	 * Additional Party's Category name is set to MOB_ADD_PARTY_CAT_2.
	 * @param catField
	 */
	public void setCategoryField(MobileAdditionalPartyCat2Enum catField) {
		categoryName = categoryName.MOB_ADD_PARTY_CAT_2;
		category2 = catField;
		fieldId = 2;
	}

	/**
	 * This function is used to set MobileAdditionalPartyCat1Enum.
	 * Additional Party's Category name is set to MOB_ADD_PARTY_CAT_1.
	 * @param catField
	 */
	public void getCategoryField(MobileAdditionalPartyCat1Enum catField) {
		categoryName = categoryName.MOB_ADD_PARTY_CAT_1;
		category1 = catField;
		fieldId = 1;
	}

	/**
	 * This function is used to set AdditionalPartyCat1Enum.
	 * Additional Party's Category name is set to PSTN_ADD_PARTY_CAT_1.
	 * @param catField
	 */
	public void getCategoryField(AdditionalPartyCat1Enum catField) {
		categoryName = categoryName.PSTN_ADD_PARTY_CAT_1;
		addCategory1 = catField;
		fieldId = 3;
	}

	public String toString(){
		String obj="";
		if(fieldId == 0)
			return obj="Additional Party ategory Not Set";
		
		obj ="AdditionalParty'sCategoryName: "+categoryName;
		if(fieldId == 1)
			obj += " ,AdditionalParty'sCategory: " + category1;
		else if(fieldId == 2)
			obj += " ,AdditionalParty'sCategory: " + category2;
		else if(fieldId == 3)
			obj += " ,AdditionalParty'sCategory: " + addCategory1;
		return obj ;
	}

}
