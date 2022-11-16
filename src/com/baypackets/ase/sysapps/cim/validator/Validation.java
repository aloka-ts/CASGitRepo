package com.baypackets.ase.sysapps.cim.validator;


import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.log4j.Logger;
import com.baypackets.ase.sysapps.cim.util.*;
import com.baypackets.ase.sysapps.cim.jaxb.Errors;
import com.baypackets.ase.sysapps.cim.jaxb.ServiceProfile;
import com.baypackets.ase.sysapps.cim.util.Constants;
import com.baypackets.ase.sysapps.cim.util.Configuration;



public class Validation {	
	private static Logger logger = Logger.getLogger(Validation.class.getName());
	private Errors errorList=new Errors();
	private static String aconyxUsernamePattern=Constants.PATTERN_ACONYX_USERNAME;
	
	static{
		try{
		Configuration config=Configuration.getInstance();
		String patternRegex=(String)config.getParamValue(Constants.PROP_CIM_PATTERN_ACONYX_USERNAME);
		// Compile Pattern to check syntax
		patternRegex=patternRegex.trim();
		Pattern.compile(patternRegex);
		aconyxUsernamePattern=patternRegex;
		}catch(PatternSyntaxException pEx){
			logger.error("[CIM] PatternSyntaxException in compiling regex property"+Constants.PROP_CIM_PATTERN_ACONYX_USERNAME+":"+pEx.toString());
		}catch(Exception e){
			logger.error("[CIM] Exception in loading property"+Constants.PROP_CIM_PATTERN_ACONYX_USERNAME+":"+e.toString());
		}
		if(logger.isInfoEnabled()){
			logger.info("[CIM] Expression for AconyxUserName Validation:"+aconyxUsernamePattern);
		}
	}
	public Errors getErrorList() {
		return errorList;
	}	
	
	
	public boolean validateUserName(ServiceProfile sr){	
		return validateAconyxUsername(sr.getUserName());
	}
	
	public boolean validateUserState(ServiceProfile sr){
		
		boolean isInvalidUserState=false;
		if (sr.getState()==null ) {
			Errors.Error error=new Errors.Error();
			error.setErrorCode(ErrorCodes.ERROR_006);
			error.setErrorDescription(ErrorCodes.ERROR_006_DESC);
			errorList.getError().add(error);
			isInvalidUserState=true;
		}


		if(sr.getState()!=null ){
			//Encrypted check
			if(! (sr.getState().equalsIgnoreCase(Constants.ENABLE)	|| sr.getState().equalsIgnoreCase(Constants.DISABLE))){
				Errors.Error error=new Errors.Error();
				error.setErrorCode(ErrorCodes.ERROR_005);
				error.setErrorDescription(ErrorCodes.ERROR_005_DESC);
				errorList.getError().add(error);
				isInvalidUserState=true;
			}
		}
		return isInvalidUserState;
	}
	
	public boolean validateAconyxUsername(String aconyxUserName){
		
		boolean isInvalidUserName=false;
		
		if(aconyxUserName == null || aconyxUserName.trim().length() == 0){
			Errors.Error error=new Errors.Error();
				error.setErrorCode(ErrorCodes.ERROR_004);
				error.setErrorDescription(ErrorCodes.ERROR_004_DESC);
				errorList.getError().add(error);
				isInvalidUserName=true;
		}
		
		else{
			if(aconyxUserName.length()>Constants.MAX_ACONYX_USERNAME_LENGTH){
				Errors.Error error=new Errors.Error();
				error.setErrorCode(ErrorCodes.ERROR_008);
				error.setErrorDescription(ErrorCodes.ERROR_008_DESC+Constants.MAX_ACONYX_USERNAME_LENGTH);
				errorList.getError().add(error);
				isInvalidUserName=true;
			}else{
				Pattern p = Pattern.compile(aconyxUsernamePattern);
				Matcher m = p.matcher(aconyxUserName);
				if (!m.matches()) {
					Errors.Error error=new Errors.Error();
					error.setErrorCode(ErrorCodes.ERROR_009);
					error.setErrorDescription(ErrorCodes.ERROR_009_DESC+aconyxUserName);
					errorList.getError().add(error);
					isInvalidUserName=true;
				}
			}
		}
		return isInvalidUserName;
	}
	
	public boolean validateUserState(String sr){
		
		boolean isInvalidUserState=false;
		if (sr==null ) {
			Errors.Error error=new Errors.Error();
			error.setErrorCode(ErrorCodes.ERROR_006);
			error.setErrorDescription(ErrorCodes.ERROR_006_DESC);
			errorList.getError().add(error);
			isInvalidUserState=true;
		}


		if(sr!=null ){
			//Encrypted check
			if(! (sr.equalsIgnoreCase(Constants.ENABLE)	|| sr.equalsIgnoreCase(Constants.DISABLE))){
				Errors.Error error=new Errors.Error();
				error.setErrorCode(ErrorCodes.ERROR_005);
				error.setErrorDescription(ErrorCodes.ERROR_005_DESC);
				errorList.getError().add(error);
				isInvalidUserState=true;
			}
		}
		return isInvalidUserState;
	}
}
