package com.agnity.inapitutcs2.datatypes;

import com.agnity.inapitutcs2.enumdata.ErrorRejectEnum;
import com.agnity.inapitutcs2.util.Util;

/**
 * This class have parameters for error or reject operations.
 * Type will be identified by the enum.
 * @author Mriganka
 *
 */
public class ErrorRejectTypeArg
{
	/**
	 * @see ErrorRejectEnum
	 */
	ErrorRejectEnum errorRejectEnum;
	
	byte[] errorCode;
	
	int errorType;
	
	int rejectProblem;
	
	int rejectProblemType;

	public ErrorRejectEnum getErrorRejectEnum() {
		return errorRejectEnum;
	}

	public void setErrorRejectEnum(ErrorRejectEnum errorRejectEnum) {
		this.errorRejectEnum = errorRejectEnum;
	}

	public byte[] getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(byte[] errorCode) {
		this.errorCode = errorCode;
	}

	public int getErrorType() {
		return errorType;
	}

	public void setErrorType(int errorType) {
		this.errorType = errorType;
	}

	public int getRejectProblem() {
		return rejectProblem;
	}

	public void setRejectProblem(int rejectProblem) {
		this.rejectProblem = rejectProblem;
	}

	public int getRejectProblemType() {
		return rejectProblemType;
	}

	public void setRejectProblemType(int rejectProblemType) {
		this.rejectProblemType = rejectProblemType;
	}
	
	public String toString(){
		return "errorRejectEnum:" + errorRejectEnum + " ,errorCode:" + Util.formatBytes(errorCode) + " ,errorType:" + errorType + 
		" ,rejectProblem:" + rejectProblem + " ,rejectProblemType:" + rejectProblemType;
	}

}
