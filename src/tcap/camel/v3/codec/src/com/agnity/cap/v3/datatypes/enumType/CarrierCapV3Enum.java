package com.agnity.cap.v3.datatypes.enumType;

public enum CarrierCapV3Enum {

	
	NoIndication(0), Selected_carrier_identification_code_subscribed_and_not_input_by_calling_party(1),
	Selected_carrier_identification_code_subscribed_input_by_calling_party(2),
	Selected_carrier_identification_code_no_indicationofwhether_inputbycallingparty(3),
	Selected_carrier_identification_code_notpresubscribed_and_inputbycallingparty(4);
	
	private int code;
	private CarrierCapV3Enum(int code){
		this.code = code;
	}
	
	public int getCode() {
		return code;
	}
	
	public static CarrierCapV3Enum getValue(int tag) {
	     switch (tag) {
		case 0: return NoIndication;
		case 1: return Selected_carrier_identification_code_subscribed_and_not_input_by_calling_party;
		case 2: return Selected_carrier_identification_code_subscribed_input_by_calling_party;
		case 3: return Selected_carrier_identification_code_no_indicationofwhether_inputbycallingparty;
		case 4: return Selected_carrier_identification_code_notpresubscribed_and_inputbycallingparty;
				
		default: return null;
		}

	}
	

}
