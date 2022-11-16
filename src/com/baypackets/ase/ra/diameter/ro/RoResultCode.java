package com.baypackets.ase.ra.diameter.ro;

//TODO Waiting from traffix to provide this class in jar. 
// This class is missing in their jar
//import com.traffix.openblox.diameter.ro.ResultCodeRo;

public interface RoResultCode {

	/**
	 * The OCF denies the service request in order to terminate the service for
	 * which credit is requested. For example this error code is used to inform
	 * PDP Context has to be terminated in the CCR message or to inform blacklist 
	 * the rating group in the Multiple-Service-Credit-Control AVP.
	 */
	 
//	public static final long AUTHORIZATION_REJECTED = ResultCodeRo.AUTHORIZATION_REJECTED;
	
	 /**
	  * The OCF denies the service request due to service restrictions (e.g. 
	  * terminate rating group) or limitations related to the end-user, for 
	  * example the end-user's account could not cover the requested service.
	  */
	 
//	 public static final long END_USER_SERVICE_DENIED = ResultCodeRo.END_USER_SERVICE_DENIED;
	 
	 /**
	  * The OCF determines that the service can be granted to the end user 
	  * but no further credit control needed for the service (e.g. service 
	  * is free of charge or the PDP context is treated for offline charging). 
	  */
	 
//	 public static final long CREDIT_CONTROL_NOT_APPLICABLE = ResultCodeRo.CREDIT_CONTROL_NOT_APPLICABLE;
	 
	 /**
	  * The OCF denies the service request since the end- user's account could
	  *  not cover the requested service. If the CCR contained used-service-units
	  *  they are deducted, if possible. 
	  */
	 
//	 public static final long CREDIT_LIMIT_REACHED = ResultCodeRo.CREDIT_LIMIT_REACHED;
	 
	 /**
	  * The specified end user could not be found in the OCF. 
	  */
	 
//	 public static final long USER_UNKNOWN = ResultCodeRo.USER_UNKNOWN;
	 
	 
	 /**
	  * This error code is used to inform the CTF that the OCF cannot rate 
	  * the service request due to insufficient rating input, incorrect AVP 
	  * combination or due to an AVP or an AVP value that is not recognized 
	  * or supported in the rating. For Flow Based Charging this error code 
	  * is used if the Rating group is not recognized. The Failed-AVP AVP 
	  * MUST be included and contain a copy of the entire AVP(s) that could 
	  * not be processed successfully or an example of the missing AVP complete 
	  * with the Vendor-Id if applicable. The value field of the missing AVP 
	  * should be of correct minimum length and contain zeroes. 
	  */
//	 public static final long RATING_FAILED = ResultCodeRo.RATING_FAILED;
    
}