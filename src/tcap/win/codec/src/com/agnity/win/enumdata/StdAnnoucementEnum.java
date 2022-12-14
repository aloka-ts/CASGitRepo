package com.agnity.win.enumdata;

public enum StdAnnoucementEnum {
	
	NONE(0) , UNAUTHORIZEDUSER(1),INVALIDESN(2),UNAUTHORIZEDMOBILE(3),SUSPENDED_ORIGINATION(4),ORIGINATION_DENIED(5),
	SERVICEAREA_DENIAL(6),PARTIAL_DIAL(16),REQUIREL_PLUS(17),REQUIREL_PLUS_NPA(18),REQUIREL_0_PLUS(19),REQUIREL_0_PLUS_NPA(20),
	DENY1_PLUS(21),UNSUPPORTED_10_PLUS(22),DENY_10PLUS(23),UNSUPPORTED_10_XXX(24),DENY_10XXX(25),DENY_10XXX_LOCALLY(26),REQUIRE_10_PLUS(27),
	REQUIRE_NPA(28),DENY_TOLL_ORIGINATION(29),DENY_INTERNATIONAL_ORIGINATION(30),DENY0_MINUS(31),DENY_NUMBER(48),ALTERNATE_OP_SERVICE(49),
	NOCKT_ALLCKTBUSY_FACPROBLEM(64),OVERLOAD(65),INTERNAL_OFFICE_FAILURE(66),NO_WINK_RECIEVED(67),INTER_OFC_LINK_FAILURE(68),VACANT(69),
	INVALID_PREFIX_OR_ACCESSCODE(70),OTHER_DIALING_IRREGULARITY(71),VACANT_OR_DISCONNECT_NUMBER(80),DENY_TERMINATION(81),
	SUSPENDED_TERMINATION(82) ,CHANGED_NUMBER(83) ,INACCESSIBLE_SUBSCRIBER(84) , DENY_INCOMING_TO_ALL(85) ,ROAMER_ACCESS_SCREENING(86) ,REFUSE_CALL(87) ,
	REDIRECT_CALL(88) , NO_PAGE_RESPONSE(89) , NO_ANSWER(90) , ROAMER_INTERCEPT(96) , GENERAL_INFO(97) ,UNREC_FEATURE_CODE(112) ,UNAUTH_FEATURE_CODE(113) ,
	RESTICTED_FEATURE_CODE(114) ,INVALID_MODIFIER_DIGITS(115) ,SUCCESS_FEATURE_REGISTERATION(116) ,SUCCESS_FEATURE_DEREGISTERATION(117) ,SUCCESS_FEATURE_ACTIVATION(118) ,
	SUCCESS_FEATURE_DEACTIVATION(119) ,INVALID_FWD_TO_NUMBER(120) ,COURTESY_CALL_WARNING(121) ,ENTER_PIN_SEND_PROMPT(128) ,ENTER_PIN_PROMPT(129) ,
	REENTER_PIN_SEND_PROMPT(130) ,REENTER_PIN_PROMPT(131) ,ENTER_OLD_PIN_SEND_PROMPT(132) ,ENTER_OLD_PIN_PROMPT(133) ,
	ENTER_NEW_PIN_SEND_PROMPT(134) ,ENTER_NEW_PIN_PROMPT(135) ,REENTER_NEW_PIN_SEND_PROMPT(136) ,REENTER_NEW_PIN_PROMPT(137) ,
	ENTER_PASSWORD_PROMPT(138) ,ENTER_DIR_NUMBER_PROMPT(139) ,REENTER_DIR_NUMBER_PROMPT(140) ,ENTER_FEATURE_CODE_PROMPT(141) ,;
	
	private int code;
	
	private StdAnnoucementEnum(int c) {
		code = c;
	}
	public int getCode() {
		return code;
	}
	public static StdAnnoucementEnum fromInt(int num) {
		switch (num) {
		case 0: {return NONE;}
		case 1: {return UNAUTHORIZEDUSER;}
		case 2: {return INVALIDESN;}
		case 3: {return UNAUTHORIZEDMOBILE;}
		case 4: {return SUSPENDED_ORIGINATION;}
		case 5: {return ORIGINATION_DENIED;}
		case 6: {return SERVICEAREA_DENIAL;}
		case 16: {return PARTIAL_DIAL;}
		case 17: {return REQUIREL_PLUS;}
		case 18: {return REQUIREL_PLUS_NPA;}
		case 19: {return REQUIREL_0_PLUS;}
		case 20: {return REQUIREL_0_PLUS_NPA;}
		case 21: {return DENY1_PLUS;}
		case 22: {return UNSUPPORTED_10_PLUS;}
		case 23: {return DENY_10PLUS;}
		case 24: {return UNSUPPORTED_10_XXX;}
		case 25: {return DENY_10XXX;}
		case 26: {return DENY_10XXX_LOCALLY;}
		case 27: {return REQUIRE_10_PLUS;}
		case 28: {return REQUIRE_NPA;}
		case 29: {return DENY_TOLL_ORIGINATION;}
		case 30: {return DENY_INTERNATIONAL_ORIGINATION;}
		case 31: {return DENY0_MINUS;}
		case 48: {return DENY_NUMBER;}
		case 49: {return ALTERNATE_OP_SERVICE;}
		case 64: {return NOCKT_ALLCKTBUSY_FACPROBLEM;}
		case 65: {return OVERLOAD;}
		case 67: {return NO_WINK_RECIEVED;}
		case 66: {return INTERNAL_OFFICE_FAILURE;}
		case 68: {return INTER_OFC_LINK_FAILURE;}
		case 69: {return VACANT;}
		case 70: {return INVALID_PREFIX_OR_ACCESSCODE;}
		case 71: {return OTHER_DIALING_IRREGULARITY;}
		case 80: {return VACANT_OR_DISCONNECT_NUMBER;}
		case 81: {return DENY_TERMINATION;}
		case 82: {return SUSPENDED_TERMINATION ;}
		case 83: {return CHANGED_NUMBER ;}
		case 84: {return  INACCESSIBLE_SUBSCRIBER;}
		case 85: {return DENY_INCOMING_TO_ALL ;}
		case 86: {return ROAMER_ACCESS_SCREENING ;}
		case 87: {return REFUSE_CALL ;}
		case 88: {return REDIRECT_CALL ;}
		case 89: {return NO_PAGE_RESPONSE ;}
		case 90: {return NO_ANSWER ;}
		case 96: {return ROAMER_INTERCEPT ;}
		case 97: {return GENERAL_INFO ;}
		case 112: {return UNREC_FEATURE_CODE ;}
		case 113: {return UNAUTH_FEATURE_CODE ;}
		case 114: {return RESTICTED_FEATURE_CODE ;}
		case 115: {return INVALID_MODIFIER_DIGITS ;}
		case 116: {return SUCCESS_FEATURE_REGISTERATION ;}
		case 118: {return SUCCESS_FEATURE_ACTIVATION ;}
		case 119: {return SUCCESS_FEATURE_DEACTIVATION ;}
		case 120: {return INVALID_FWD_TO_NUMBER ;}
		case 121: {return COURTESY_CALL_WARNING ;}
		case 128: {return ENTER_PIN_SEND_PROMPT ;}
		case 129: {return ENTER_PIN_PROMPT ;}
		case 130: {return REENTER_PIN_SEND_PROMPT ;}
		case 131: {return REENTER_PIN_PROMPT ;}
		case 132: {return ENTER_OLD_PIN_SEND_PROMPT ;}
		case 133: {return ENTER_OLD_PIN_PROMPT ;}
		case 134: {return ENTER_NEW_PIN_SEND_PROMPT ;}
		case 135: {return ENTER_NEW_PIN_PROMPT ;}
		case 136: {return REENTER_NEW_PIN_SEND_PROMPT ;}
		case 137: {return REENTER_NEW_PIN_PROMPT ;}
		case 138: {return ENTER_PASSWORD_PROMPT ;}
		case 139: {return ENTER_DIR_NUMBER_PROMPT ;}
		case 140: {return REENTER_DIR_NUMBER_PROMPT ;}
		case 141: {return ENTER_FEATURE_CODE_PROMPT ;}
		
		
		default: {
			return null;
		}
		}
	}

}
