package com.agnity.win.enumdata;

public enum AnnouncementsEnum {

	/* ANNOUNCEMENTS 
	 * DecimalValue   Meaning 
	 *         0       SYSTEM CANNOT SUPPORT ANNOUNCEMENT LIST PARAMETER
	 *         1       SYSTEM CAN SUPPORT ANNOUNCEMENT LIST PARAMETER
	 */
	SYSTEM_CANNOT_SUPPORT_ANNOUNCEMENTLIST_PARAM(0), SYSTEM_CAN_SUPPORT_ANNOUNCEMENTLIST_PARAM(
			1);

	private int code;

	private AnnouncementsEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}

	public static AnnouncementsEnum fromInt(int num) {
		switch (num) {
		case 0: {
			return SYSTEM_CANNOT_SUPPORT_ANNOUNCEMENTLIST_PARAM;
		}
		case 1: {
			return SYSTEM_CAN_SUPPORT_ANNOUNCEMENTLIST_PARAM;
		}
		default: {
			return null;
		}
		}
	}

}
