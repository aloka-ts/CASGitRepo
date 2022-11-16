package com.camel.CAPMsg;

import asnGenerated.RequestedInformationType;

import com.camel.dataTypes.CauseDataType;


/**
 * This class have parameters for RequestedInformation
 * in the CallInformationReportArg.
 * @author nkumar
 *
 */
public class SasCapReqInfoDataType {

	/** This field have type of RequestedInfo */
	RequestedInformationType reqInfoType ;
	
	/** This parameter have value corresponding to  callAttemptElapsedTime in the reqInfoType */
	Integer callAttemptElapsedTime ;
	
	/** The datetime will be in format yyyymmddhhmmss and value  corresponding to  callStopTime in the reqIbfoType */
	String dateTime ;
	
	/** This parameter have value corresponding to  callConnectedElapsedTime in the reqInfoType */
	Integer callConnectedElapsedTimeValue ;
	
	/** This parameter have value corresponding to  releaseCause in the reqInfoType */
	CauseDataType cause ;

	public boolean isCallAttemptElapsedTimePresent(){
		return callAttemptElapsedTime != null ;
	}
	
	public boolean isDateTimePresent(){
		return dateTime != null ;
	}
	
	public boolean isCallConnectedElapsedTimeValuePresent(){
		return callConnectedElapsedTimeValue != null ;
	}
	
	public boolean isCausePresent(){
		return cause != null ;
	}
	
	public RequestedInformationType getReqInfoType() {
		return reqInfoType;
	}

	public void setReqInfoType(RequestedInformationType reqInfoType) {
		this.reqInfoType = reqInfoType;
	}

	public Integer getCallAttemptElapsedTime() {
		return callAttemptElapsedTime;
	}

	public void setCallAttemptElapsedTime(Integer callAttemptElapsedTime) {
		this.callAttemptElapsedTime = callAttemptElapsedTime;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public Integer getCallConnectedElapsedTimeValue() {
		return callConnectedElapsedTimeValue;
	}

	public void setCallConnectedElapsedTimeValue(
			Integer callConnectedElapsedTimeValue) {
		this.callConnectedElapsedTimeValue = callConnectedElapsedTimeValue;
	}

	public CauseDataType getCause() {
		return cause;
	}

	public void setCause(CauseDataType cause) {
		this.cause = cause;
	}
	
	
	
}
