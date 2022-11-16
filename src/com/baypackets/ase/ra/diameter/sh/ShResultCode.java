package com.baypackets.ase.ra.diameter.sh;

import org.apache.commons.lang3.StringUtils;

public class ShResultCode {

	public static final String DIAMETER_UNKNOWN_PEER = "DIAMETER_UNKNOWN_PEER";
	public static String DIAMETER_END_USER_SERVICE_DENIED = "DIAMETER_END_USER_SERVICE_DENIED";
	public static int DIAMETER_END_USER_SERVICE_DENIED_code = 4010;
	public static String DIAMETER_CREDIT_CONTROL_NOT_APPLICABLE = "DIAMETER_CREDIT_CONTROL_NOT_APPLICABLE";
	public static int DIAMETER_CREDIT_CONTROL_NOT_APPLICABLE_code = 4011;
	public static String DIAMETER_CREDIT_LIMIT_REACHED = "DIAMETER_CREDIT_LIMIT_REACHED";
	public static int DIAMETER_CREDIT_LIMIT_REACHED_code = 4012;
	public static String DIAMETER_USER_UNKNOWN = "DIAMETER_USER_UNKNOWN";
	public static int DIAMETER_USER_UNKNOWN_code = 5030;
	public static String DIAMETER_RATING_FAILED = "DIAMETER_RATING_FAILED";
	public static int DIAMETER_RATING_FAILED_code = 5031;

	//
	// static HashMap<Integer,String> codes= new HashMap<Integer,String>();
	// static HashMap<String,Integer> names= new HashMap<String,Integer>();
	//
	// static{
	// codes.put(DIAMETER_END_USER_SERVICE_DENIED_code,
	// DIAMETER_END_USER_SERVICE_DENIED);
	// codes.put(DIAMETER_CREDIT_CONTROL_NOT_APPLICABLE_code,
	// DIAMETER_CREDIT_CONTROL_NOT_APPLICABLE);
	// codes.put(DIAMETER_CREDIT_LIMIT_REACHED_code,
	// DIAMETER_CREDIT_LIMIT_REACHED);
	// codes.put(DIAMETER_USER_UNKNOWN_code, DIAMETER_USER_UNKNOWN);
	// codes.put(DIAMETER_RATING_FAILED_code, DIAMETER_RATING_FAILED);
	//
	//
	// names.put(DIAMETER_END_USER_SERVICE_DENIED,
	// DIAMETER_END_USER_SERVICE_DENIED_code);
	// names.put(DIAMETER_CREDIT_CONTROL_NOT_APPLICABLE,
	// DIAMETER_CREDIT_CONTROL_NOT_APPLICABLE_code);
	// names.put(DIAMETER_CREDIT_LIMIT_REACHED,
	// DIAMETER_CREDIT_LIMIT_REACHED_code);
	// names.put(DIAMETER_USER_UNKNOWN, DIAMETER_USER_UNKNOWN_code);
	// names.put(DIAMETER_RATING_FAILED, DIAMETER_RATING_FAILED_code);
	// }

	//
	// public static String getName(int code){
	// return codes.get(code);
	//
	// }
	//
	// public static int getCode(String name){
	// return names.get(name);
	// }

	static public String getReturnCode(int codeString) {
		String returnCode = "DIAMETER_SUCCESS"; // SUCCESS

		if (codeString != -1) {
			switch (codeString) {
			case 1001:
				returnCode = "DIAMETER_MULTI_ROUND_AUTH";
				break;
			case 2001:
				returnCode = "DIAMETER_SUCCESS";
				break;
			case 2002:
				returnCode = "DIAMETER_LIMITED_SUCCESS";
				break;
			case 3001:
				returnCode = "DIAMETER_COMMAND_UNSUPPORTED";
				break;
			case 3002:
				returnCode = "DIAMETER_UNABLE_TO_DELIVER";
				break;
			case 3003:
				returnCode = "DIAMETER_REALM_NOT_SERVED";
				break;
			case 3004:
				returnCode = "DIAMETER_TOO_BUSY";
				break;
			case 3005:
				returnCode = "DIAMETER_LOOP_DETECTED";
				break;
			case 3006:
				returnCode = "DIAMETER_REDIRECT_INDICATION";
				break;
			case 3007:
				returnCode = "DIAMETER_APPLICATION_UNSUPPORTED";
				break;
			case 3008:
				returnCode = "DIAMETER_INVALID_HDR_BITS";
				break;
			case 3009:
				returnCode = "DIAMETER_INVALID_AVP_BITS";
				break;
			case 3010:
				returnCode = "DIAMETER_UNKNOWN_PEER";
				break;
			case 4001:
				returnCode = "DIAMETER_AUTHENTICATION_REJECTED";
				break;
			case 4002:
				returnCode = "DIAMETER_OUT_OF_SPACE";
				break;
			case 4003:
				returnCode = "ELECTION_LOST";
				break;
			case 4010:
				returnCode = "DIAMETER_END_USER_SERVICE_DENIED";
				break;
			case 4011:
				returnCode = "DIAMETER_CREDIT_CONTROL_NOT_APPLICABLE";
				break;
			case 4012:
				returnCode = "DIAMETER_CREDIT_LIMIT_REACHED";
				break;
			case 5001:
				returnCode = "DIAMETER_AVP_UNSUPPORTED";
				break;
			case 5002:
				returnCode = "DIAMETER_UNKNOWN_SESSION_ID";
				break;
			case 5003:
				returnCode = "DIAMETER_AUTHORIZATION_REJECTED";
				break;
			case 5004:
				returnCode = "DIAMETER_INVALID_AVP_VALUE";
				break;
			case 5005:
				returnCode = "DIAMETER_MISSING_AVP";
				break;
			case 5006:
				returnCode = "DIAMETER_RESOURCES_EXCEEDED";
				break;
			case 5007:
				returnCode = "DIAMETER_CONTRADICTING_AVPS";
				break;
			case 5008:
				returnCode = "DIAMETER_AVP_NOT_ALLOWED";
				break;
			case 5009:
				returnCode = "DIAMETER_AVP_OCCURS_TOO_MANY_TIMES";
				break;
			case 5010:
				returnCode = "DIAMETER_NO_COMMON_APPLICATION";
				break;
			case 5011:
				returnCode = "DIAMETER_UNSUPPORTED_VERSION";
				break;
			case 5012:
				returnCode = "DIAMETER_UNABLE_TO_COMPLY";
				break;
			case 5013:
				returnCode = "DIAMETER_INVALID_BIT_IN_HEADER";
				break;
			case 5014:
				returnCode = "DIAMETER_INVALID_AVP_LENGTH";
				break;
			case 5015:
				returnCode = "DIAMETER_INVALID_MESSAGE_LENGTH";
				break;
			case 5016:
				returnCode = "DIAMETER_INVALID_AVP_BIT_COMBO";
				break;
			case 5017:
				returnCode = "DIAMETER_NO_COMMON_SECURITY";
				break;
			case 5030:
				returnCode = "DIAMETER_USER_UNKNOWN";
				break;
			case 5031:
				returnCode = "DIAMETER_RATING_FAILED";
				break;
			default:
				returnCode = "DIAMETER_SUCCESS";
			}
		}

		return returnCode;
	}

	/**
	 * @param codeString
	 * @return
	 */
	static public int getReturnCode(String codeString, boolean isExperimental) {
		int returnCode = -1; // SUCCESS

		if (StringUtils.isNotBlank(codeString)) {

			if (codeString != null && codeString != "") {
				if (isExperimental) {
					switch (codeString) {
					case "DIAMETER_SUCCESS":
						return returnCode = 2001;
					case "DIAMETER_ERROR_USER_UNKNOWN":
						return returnCode = 5001;
					}
				}
				switch (codeString) {
				case "DIAMETER_MULTI_ROUND_AUTH":
					returnCode = 1001;
					break;
				case "DIAMETER_SUCCESS":
					returnCode = 2001;
					break;
				case "DIAMETER_LIMITED_SUCCESS":
					returnCode = 2002;
					break;
				case "DIAMETER_COMMAND_UNSUPPORTED":
					returnCode = 3001;
					break;
				case "DIAMETER_UNABLE_TO_DELIVER":
					returnCode = 3002;
					break;
				case "DIAMETER_REALM_NOT_SERVED":
					returnCode = 3003;
					break;
				case "DIAMETER_TOO_BUSY":
					returnCode = 3004;
					break;
				case "DIAMETER_LOOP_DETECTED":
					returnCode = 3005;
					break;
				case "DIAMETER_REDIRECT_INDICATION":
					returnCode = 3006;
					break;
				case "DIAMETER_APPLICATION_UNSUPPORTED":
					returnCode = 3007;
					break;
				case "DIAMETER_INVALID_HDR_BITS":
					returnCode = 3008;
					break;
				case "DIAMETER_INVALID_AVP_BITS":
					returnCode = 3009;
					break;
				case "DIAMETER_UNKNOWN_PEER":
					returnCode = 3010;
					break;
				case "DIAMETER_AUTHENTICATION_REJECTED":
					returnCode = 4001;
					break;
				case "DIAMETER_OUT_OF_SPACE":
					returnCode = 4002;
					break;
				case "ELECTION_LOST":
					returnCode = 4003;
					break;
				case "DIAMETER_CREDIT_LIMIT_REACHED":
					returnCode = 4012;
					break;
				case "DIAMETER_AVP_UNSUPPORTED":
					returnCode = 5001;
					break;
				case "DIAMETER_UNKNOWN_SESSION_ID":
					returnCode = 5002;
					break;
				case "DIAMETER_AUTHORIZATION_REJECTED":
					returnCode = 5003;
					break;
				case "DIAMETER_INVALID_AVP_VALUE":
					returnCode = 5004;
					break;
				case "DIAMETER_MISSING_AVP":
					returnCode = 5005;
					break;
				case "DIAMETER_RESOURCES_EXCEEDED":
					returnCode = 5006;
					break;
				case "DIAMETER_CONTRADICTING_AVPS":
					returnCode = 5007;
					break;
				case "DIAMETER_AVP_NOT_ALLOWED":
					returnCode = 5008;
					break;
				case "DIAMETER_AVP_OCCURS_TOO_MANY_TIMES":
					returnCode = 5009;
					break;
				case "DIAMETER_NO_COMMON_APPLICATION":
					returnCode = 5010;
					break;
				case "DIAMETER_UNSUPPORTED_VERSION":
					returnCode = 5011;
					break;
				case "DIAMETER_UNABLE_TO_COMPLY":
					returnCode = 5012;
					break;
				case "DIAMETER_INVALID_BIT_IN_HEADER":
					returnCode = 5013;
					break;
				case "DIAMETER_INVALID_AVP_LENGTH":
					returnCode = 5014;
					break;
				case "DIAMETER_INVALID_MESSAGE_LENGTH":
					returnCode = 5015;
					break;
				case "DIAMETER_INVALID_AVP_BIT_COMBO":
					returnCode = 5016;
					break;
				case "DIAMETER_NO_COMMON_SECURITY":
					returnCode = 5017;
					break;
				case "DIAMETER_END_USER_SERVICE_DENIED":
					returnCode = 4010;
					break;
				case "DIAMETER_CREDIT_CONTROL_NOT_APPLICABLE":
					returnCode = 4011;
					break;
				case "DIAMETER_USER_UNKNOWN":
					returnCode = 5030;
					break;
				case "DIAMETER_RATING_FAILED":
					returnCode = 5031;
					break;
				case "DIAMETER_ERROR_USER_DATA_NOT_RECOGNIZED":
					returnCode = 5100;
					break;
				case "DIAMETER_ERROR_OPERATION_NOT_ALLOWED":
					returnCode = 5101;
					break;
				case "DIAMETER_ERROR_USER_DATA_CANNOT_BE_READ":
					returnCode = 5102;
					break;
				case "DIAMETER_ERROR_USER_DATA_CANNOT_BE_MODIFIED":
					returnCode = 5103;
					break;
				case "DIAMETER_ERROR_USER_DATA_CANNOT_BE_NOTIFIED":
					returnCode = 5104;
					break;
				case "DIAMETER_ERROR_TOO_MUCH_DATA":
					returnCode = 5008;
					break;
				case "DIAMETER_ERROR_TRANSPARENT_DATA_OUT_OF_SYNC":
					returnCode = 5105;
					break;
				case "DIAMETER_ERROR_FEATURE_UNSUPPORTED":
					returnCode = 5011;
					break;
				case "DIAMETER_ERROR_SUBS_DATA_ABSENT":
					returnCode = 5106;
					break;
				case "DIAMETER_ERROR_NO_SUBSCRIPTION_TO_DATA":
					returnCode = 5107;
					break;
				case "DIAMETER_ERROR_DSAI_NOT_AVAILABLE":
					returnCode = 5108;
					break;
				case "DIAMETER_ERROR_IDENTITIES_DONT_MATCH":
					returnCode = 5002;
					break;
				case "DIAMETER_USER_DATA_NOT_AVAILABLE":
					returnCode = 4100;
					break;
				case "DIAMETER_PRIOR_UPDATE_IN_PROGRESS":
					returnCode = 4101;
					break;
				default:
					returnCode = 2001;
				}
			}

		}

		return returnCode;
	}

}
