package com.agnity.inapitutcs2.enumdata;

/**
 * Enum for Error or Reject Operation 
 * @author Mriganka
 *
 */

public enum ErrorRejectEnum
{
	/**
	 * 0-Error
	 * 1-Reject
	 */
	ERROR(0), REJECT(1);
	
	private int code;

	private ErrorRejectEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static ErrorRejectEnum fromInt(int num) {
		switch (num) {
			case 0: { return ERROR; }
			case 1: { return REJECT; }
			default: { return null; }
		}
	}
}
