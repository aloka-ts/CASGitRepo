package com.agnity.cap.v3.datatypes.enumType;

public enum RedirectingIndicatorCapV3Enum {


	noredirection(0),
	callrerouted_nationaluse(1),callrerouted_all_redirectioninformationpresentationrestricted(2),
	call_diverted(3),calldiverted_allredirectioninformationpresentationrestricted(4),
	callrerouted_redirectionnumberpresentationrestricted(5),
	calldiversion_redirectionnumberpresentationrestricted(6),SPARE(7);
	
	private int code;
	private RedirectingIndicatorCapV3Enum(int code){
		this.code = code;
	}
	
	public int getCode() {
		return code;
	}
	
	public static RedirectingIndicatorCapV3Enum getValue(int tag) {
	     switch (tag) {
		case 0: return noredirection;
		case 1: return callrerouted_nationaluse;
		case 2: return callrerouted_all_redirectioninformationpresentationrestricted;
		case 3: return call_diverted;
		case 4: return calldiverted_allredirectioninformationpresentationrestricted;
		case 5: return callrerouted_redirectionnumberpresentationrestricted;
		case 6: return calldiversion_redirectionnumberpresentationrestricted;
		case 7: return SPARE;
		default: return noredirection;
		}

	}
	

}


