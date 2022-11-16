/**
 * Filename: 	Constants.java
 *
 */
	
package com.baypackets.ase.ra.ro;

/**
 * This interface defines the various constant values that can be used
 * during online charging.
 *
 * @author Neeraj Jain
 */

public interface Constants 
{
	// configuration properties
	public static String STACK_PROVIDER = "stack_provider";
	public static String RO_CONFIG_FILE = "ro_config_file";
	
	// protocol 
	public static String PROTOCOL = "Ro";

	// Request Methods
	public static final String METHOD_FIRST_INTERROGATION			= "FIRST_INTERROGATION";
	public static final String METHOD_INTERMEDIATE_INTERROGATION	= "INTERMEDIATE_INTERROGATION";
	public static final String METHOD_FINAL_INTERROGATION			= "FINAL_INTERROGATION";
	public static final String METHOD_DIRECT_DEBITING				= "DIRECT_DEBITING";
	public static final String METHOD_REFUND_ACCOUNT				= "REFUND_ACCOUNT";
	public static final String METHOD_CHECK_BALANCE					= "CHECK_BALANCE";
	public static final String METHOD_PRICE_ENQUERY					= "PRICE_ENQUERY";

	// Request Type
	public static final int RO_FIRST_INTERROGATION			= 101;
	public static final int RO_INTERMEDIATE_INTERROGATION	= 102;
	public static final int RO_FINAL_INTERROGATION			= 103;
	public static final int RO_DIRECT_DEBITING				= 104;
	public static final int RO_REFUND_ACCOUNT				= 105;
	public static final int RO_CHECK_BALANCE				= 106;
	public static final int RO_PRICE_ENQUERY				= 107;

	///////////////////////////// Ro Message Fields Start ///////////////////////////////////
	
	// CC Request Type
	public static final short CCRT_INITIAL_REQUEST		= 1;
	public static final short CCRT_UPDATE_REQUEST		= 2;
	public static final short CCRT_TERMINATION_REQUEST	= 3;
	public static final short CCRT_EVENT_REQUEST		= 4;

	// CC Requested Action
	public static final short CCRA_DIRECT_DEBITING	= 0;
	public static final short CCRA_REFUND_ACCOUNT	= 1;
	public static final short CCRA_CHECK_BALANCE	= 2;
	public static final short CCRA_PRICE_ENQUERY	= 3;

	// Termination Cause
	public static final short TC_DIAMETER_LOGOUT				= 1;
	public static final short TC_DIAMETER_SERVICE_NOT_PROVIDED	= 2;
	public static final short TC_DIAMETER_BAD_ANSWER			= 3;
	public static final short TC_DIAMETER_ADMINISTRATIVE		= 4;
	public static final short TC_DIAMETER_LINK_BROKEN			= 5;
	public static final short TC_DIAMETER_AUTH_EXPIRED			= 6;
	public static final short TC_DIAMETER_USER_MOVED			= 7;
	public static final short TC_DIAMETER_SESSION_TIMEOUT		= 8;

	// Multiple Services Indicator
	public static final short MSI_MULTIPLE_SERVICES_NOT_SUPPORTED	= 0;
	public static final short MSI_MULTIPLE_SERVICES_SUPPORTED		= 1;

	// User Equipment Info Type
	public static final short UEIT_IMEISV			= 0;
	public static final short UEIT_MAC				= 1;
	public static final short UEIT_EUI64			= 2;
	public static final short UEIT_MODIFIED_EUI64	= 3;

	// Subscription Type
	public static final short ST_END_USER_E164		= 0;
	public static final short ST_END_USER_IMSI		= 1;
	public static final short ST_END_USER_SIP_URI	= 2;
	public static final short ST_END_USER_NAI		= 3;
	public static final short ST_END_USER_PRIVATE	= 4;

	// Reporting Reason
	public static final short RR_THRESHOLD					= 0;
	public static final short RR_QHT						= 1;
	public static final short RR_FINAL						= 2;
	public static final short RR_QUOTA_EXHAUSTED			= 3;
	public static final short RR_VALIDITY_TIME				= 4;
	public static final short RR_OTHER_QUOTA_TYPE			= 5;
	public static final short RR_RATING_CONDITION_CHANGE	= 6;
	public static final short RR_FORCED_REAUTHORISATION		= 7;
	public static final short RR_POOL_AXHAUSTED				= 8;

	// Tariff Change Usage
	public static final short TCU_UNIT_BEFORE_TARIFF_CHANGE	= 0;
	public static final short TCU_UNIT_AFTER_TARIFF_CHANGE	= 1;
	public static final short TCU_UNIT_INDETERMINATE		= 2;

	// Trigger Type
	public static final short TT_CHANGE_IN_SGSN_IP_ADDRESS						= 1;
	public static final short TT_CHANGE_IN_QOS									= 2;
	public static final short TT_CHANGE_IN_LOCATION								= 3;
	public static final short TT_CHANGE_IN_RAT									= 4;
	public static final short TT_CHANGEINQOS_TRAFFIC_CLASS						= 10;
	public static final short TT_CHANGEINQOS_RELIABILITY_CLASS					= 11;
	public static final short TT_CHANGEINQOS_DELAY_CLASS						= 12;
	public static final short TT_CHANGEINQOS_PEAK_THROUGHPUT					= 13;
	public static final short TT_CHANGEINQOS_PRECEDENCE_CLASS					= 14;
	public static final short TT_CHANGEINQOS_MEAN_THROUGHPUT					= 15;
	public static final short TT_CHANGEINQOS_MAXIMUM_BIT_RATE_FOR_UPLINK		= 16;
	public static final short TT_CHANGEINQOS_MAXIMUM_BIT_RATE_FOR_DOWNLINK		= 17;
	public static final short TT_CHANGEINQOS_RESIDUAL_BER						= 18;
	public static final short TT_CHANGEINQOS_SDU_ERROR_RATIO					= 19;
	public static final short TT_CHANGEINQOS_TRANSFER_DELAY						= 20;
	public static final short TT_CHANGEINQOS_TRAFFIC_HANDLING_PRIORITY			= 21;
	public static final short TT_CHANGEINQOS_GUARANTEED_BIT_RATE_FOR_UPLINK		= 22;
	public static final short TT_CHANGEINQOS_GUARANTEED_BIT_RATE_FOR_DOWNLINK	= 23;
	public static final short TT_CHANGEINLOCATION_MCC							= 30;
	public static final short TT_CHANGEINLOCATION_MNC							= 31;
	public static final short TT_CHANGEINLOCATION_RAC							= 32;
	public static final short TT_CHANGEINLOCATION_LAC							= 33;
	public static final short TT_CHANGEINLOCATION_CELL_ID						= 34;

	// CC Session Failover
	public static final short CCSF_FAILOVER_NOT_SUPPORTED	= 0;
	public static final short CCSF_FAILOVER_SUPPORTED		= 1;

	// Redirect Host Usage
	public static final short RHU_DONT_CACHE			= 0;
	public static final short RHU_ALL_SESSION			= 1;
	public static final short RHU_ALL_REALM				= 2;
	public static final short RHU_REALM_AND_APPLICATION	= 3;
	public static final short RHU_ALL_APPLICATION		= 4;
	public static final short RHU_ALL_HOST				= 5;
	public static final short RHU_ALL_USER				= 6;

	// Credit Control Failure Handling
	public static final short CCFH_TERMINATE			= 0;
	public static final short CCFH_CONTINUE				= 1;
	public static final short CCFH_RETRY_AND_TERMINATE	= 2;

	// Final Unit Action
	public static final short FUA_TERMINATE			= 0;
	public static final short FUA_REDIRECT			= 1;
	public static final short FUA_RESTRICT_ACCESS	= 2;

	// Redirect Address Type
	public static final short RAT_IPV4_ADDRESS	= 0;
	public static final short RAT_IPV6_ADDRESS	= 1;
	public static final short RAT_URL			= 2;
	public static final short RAT_SIP_URI		= 3;

	// Measurement Counters
	public static final String CCR_EVENT_COUNTER = "Credit Control Event Request Count";
	public static final String CCR_SESSION_COUNTER = "Credit Control Session Request Count";

	public static final String CCR_DIRECT_DEBIT_COUNTER = "Credit Control Direct Debit Request Count";
	public static final String CCR_ACCOUNT_REFUND_COUNTER = "Credit Control Account Refund Request Count";
	public static final String CCR_BALANCE_CHECK_COUNTER = "Credit Control Balance Check Request Count";
	public static final String CCR_PRICE_ENQUIRY_COUNTER = "Credit Control Price Enquiry Request Count";

	public static final String CCR_FIRST_INTEROGATION_COUNTER = "Credit Control First Interogation Request Count";
	public static final String CCR_INTERIM_INTEROGATION_COUNTER = "Credit Control Interim Interogation Request Count";
	public static final String CCR_FINAL_INTEROGATION_COUNTER = "Credit Control Final Interogation Request Count";

	public static final String CCA_EVENT_1XXX_COUNTER = "Credit Control 1xxx Event Answer Count";
	public static final String CCA_EVENT_2XXX_COUNTER = "Credit Control 2xxx Event Answer Count";
	public static final String CCA_EVENT_3XXX_COUNTER = "Credit Control 3xxx Event Answer Count";
	public static final String CCA_EVENT_4XXX_COUNTER = "Credit Control 4xxx Event Answer Count";
	public static final String CCA_EVENT_5XXX_COUNTER = "Credit Control 5xxx Event Answer Count";

	public static final String CCA_SESSION_1XXX_COUNTER = "Credit Control 1xxx Session Answer Count";
	public static final String CCA_SESSION_2XXX_COUNTER = "Credit Control 2xxx Session Answer Count";
	public static final String CCA_SESSION_3XXX_COUNTER = "Credit Control 3xxx Session Answer Count";
	public static final String CCA_SESSION_4XXX_COUNTER = "Credit Control 4xxx Session Answer Count";
	public static final String CCA_SESSION_5XXX_COUNTER = "Credit Control 5xxx Session Answer Count";

	// Error Counter
	public static final String CCR_SEND_ERROR = "Credit Control Request Send Error Count";
}
