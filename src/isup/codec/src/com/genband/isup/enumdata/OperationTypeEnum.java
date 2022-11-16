package com.genband.isup.enumdata;

/**
 * Enum for Operation Type
 * @author vgoel
 *
 */

public enum OperationTypeEnum {

	/**
	 * 0-spare
	 * 4-notification of estimated amount
	 * 6-Immediate Charge Command
	 * 7-time moment charging with an alert tone
	 * 8-time moment charging without an alert tone
	 * 31-charging permission
	 */
	SPARE(0), NOTIFICATION_ESTIMATED_AMOUNT(4), IMMEDIATE_CHARGE_COMMAND(6), TIME_MOMENT_WITH_ALERT_TONE(7), TIME_MOMENT_WITHOUT_ALERT_TONE(8),
	CHARGING_PERMISSION(31);
	
	private int code;

	private OperationTypeEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static OperationTypeEnum fromInt(int num) {
		switch (num) {
			case 0: { return SPARE; }
			case 4: { return NOTIFICATION_ESTIMATED_AMOUNT; }
			case 6: { return IMMEDIATE_CHARGE_COMMAND; }
			case 7: { return TIME_MOMENT_WITH_ALERT_TONE; }
			case 8: { return TIME_MOMENT_WITHOUT_ALERT_TONE; }
			case 31: { return CHARGING_PERMISSION; }
			default: { return null; }
		}
	}
}
