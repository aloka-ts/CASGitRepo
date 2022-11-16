package com.camel.enumData;

/**
 * This enum represent  type of alerting level
 * for Alerting Pattern.
 * @author nkumar
 *
 */
public enum TypeOfAlertLevelEnum {

	/**
	 * 0-alertingLevel-0 
	 * 1-alertingLevel-1
	 * 2-alertingLevel-2 
	 * 3-reserved
	 */
	ALERT_LEVEL0(0), ALERT_LEVEL1(1), ALERT_LEVEL2(2) ;
	
	private int code;

	private TypeOfAlertLevelEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static TypeOfAlertLevelEnum fromInt(int num) {
		switch (num) {
			case 0: { return ALERT_LEVEL0; }
			case 1: { return ALERT_LEVEL1; }
			case 2: { return ALERT_LEVEL2; }
			default: { return null; }
		}
	}
}
