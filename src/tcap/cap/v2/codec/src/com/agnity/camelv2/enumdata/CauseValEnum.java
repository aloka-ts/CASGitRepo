package com.agnity.camelv2.enumdata;

/**
 * This enum represents Cause values.
 * @author nkumar
 *
 */
public enum CauseValEnum {

	/**
	 * 3-No route to destination
	 * 16-Normal call clearing
	 * 17-User busy
	 * 19-No answer from user (user alerted)
	 * 21-Call rejected
	 * 23-Redirection to new destination
	 * 27-Destination out of order
	 * 28-Invalid number format (address incomplete)
	 * 31-Normal, unspecified
	 * 34-No circuit/channel available
	 * 41-Temporary failure
	 * 42-Switching equipment congestion
	 * 47-Resource unavailable, unspecified
	 * 63-Service or option not available,unspecified
	 * 95-Invalid message, unspecified
	 * 96-Mandatory information element is missing
	 * 111-Protocol error, unspecified
	 */
	UNKNOWN(0), No_route_destination(3), Normal_call_clearing(16), User_busy(17), No_answer_user(19), Call_rejected(21), 
	
	Redirection_new_destination(23), Destination_out_order(27), Invalid_number_format(28), Normal_UNSPECIFIED(31), No_circuit_available(34), 
	
	Temporary_failure(41), Switching_equipment_congestion(42), Resource_unavailable(47), Service_not_available(63), Invalid_message(95), 
	
	Mandatory_information_element_missing(96), Protocol_error(111) ;
		
	private int code;

	private CauseValEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static CauseValEnum fromInt(int num) {
		switch (num) {
			case 0: { return UNKNOWN; }
			case 3: { return No_route_destination; }
			case 16: { return Normal_call_clearing; }
			case 17: { return User_busy; }
			case 19: { return No_answer_user; }
			case 21: { return Call_rejected; }
			case 23: { return Redirection_new_destination; }
			case 27: { return Destination_out_order; }
			case 28: { return Invalid_number_format; }
			case 31: { return Normal_UNSPECIFIED; }
			case 34: { return No_circuit_available; }
			case 41: { return Temporary_failure; }
			case 42: { return Switching_equipment_congestion; }
			case 47: { return Resource_unavailable; }
			case 63: { return Service_not_available; }
			case 95: { return Invalid_message; }
			case 96: { return Mandatory_information_element_missing; }
			case 111: { return Protocol_error; }
			default: { return null; }
		}
	}
}
