package com.agnity.win.enumdata;

public enum SMSTeleServiceIdEnum {

	/*
	 * 
	 * SMSTeleServiceId 
	 * Value 	Meaning
	 * 0        Not Used
	 * 1        Reserved For Maintenance
	   4096 	AMPS Extended Protocol Enhanced Services[AMPS].
	   4097 	CDMA Cellular Paging Teleservice [CDMA].
	   4098 	CDMA Cellular Messaging Teleservice[CDMA].
	   4099 	CDMA Voice Mail Notification [CDMA].
	   32513    TDMA Cellular Messaging Teleservice
	 */

	NOT_USED(0), RESERVED_FOR_MAINTENANCE(1), AMPS_EXTENDED_PROTOCOL_ENHANCED_SERVICES(4096), CDMA_CELLULAR_PAGING_TELESERVICE(4097), CDMA_CELLULAR_MESSAGING_TELESERVICE(4098),
	CDMA_VOICE_MAIL_NOTIFICATION(4099),TDMA_CELLULAR_MESSAGING_TELESERVICE(32513);

	private int code;

	private SMSTeleServiceIdEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}

	public static SMSTeleServiceIdEnum fromInt(int num) {
		switch (num) {
		case 0: {
			return NOT_USED;
		}
		case 1: {
			return RESERVED_FOR_MAINTENANCE;
		}
		case 4096: {
			return AMPS_EXTENDED_PROTOCOL_ENHANCED_SERVICES;
		}
		case 4097: {
			return CDMA_CELLULAR_PAGING_TELESERVICE;
		}
		case 4098: {
			return CDMA_CELLULAR_MESSAGING_TELESERVICE;
		}
		case 4099: {
			return CDMA_VOICE_MAIL_NOTIFICATION;
		}
		case 32513: {
			return TDMA_CELLULAR_MESSAGING_TELESERVICE;
		}
		default: {
			return null;
		}
		}
	}

}
