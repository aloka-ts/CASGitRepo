package com.baypackets.ase.ra.diameter.gy.utils;

/**
 * This interface defines the various constant values that can be used
 * during offline charging.
 *
 * @author Neeraj Jadaun
 */
public interface Constants 
{
	// Miscellaneous
	public static final int MAX_RETYR_COUNTER = 5;
	
	// configuration properties
	public static String STACK_PROVIDER = "stack_provider";
	public static String GY_CONFIG_FILE = "gy_config_file";
	
	// protocol 
	public static String PROTOCOL = "Gy";
	public static String JAR_NAME = "gy-full.jar";
//	//Data type
//	public static final int IMS_PUBLIC_IDENTITY = 1;
//	public static final int MSISDN = 2;
//	public static final int CS_USER_STATE = 3;
//	public static final int PS_USER_STATE = 4;
//	public static final int IMS_USER_STATE = 5;
//	public static final int CS_LOCATION = 6;
//	public static final int PS_LOCATION = 7;
//	public static final int REQUEST_URI_SPT = 8;
//	public static final int SIP_METHOD_SPT = 9;
//	public static final int SIP_HEADER_SPT = 10;
//	public static final int SESSION_CASE_SPT = 11;
//	public static final int SESSION_DESC_SPT = 12;
//
//	// Request Type
//	public static final int RO_FIRST_INTERROGATION			= 101;
//	public static final int RO_INTERMEDIATE_INTERROGATION	= 102;
//	public static final int RO_FINAL_INTERROGATION			= 103;
//	public static final int RO_DIRECT_DEBITING				= 104;
//	public static final int RO_REFUND_ACCOUNT				= 105;
//	public static final int RO_CHECK_BALANCE				= 106;
//	public static final int RO_PRICE_ENQUERY				= 107;

	public static final int EVENT = 1;
	public static final int SESSION = 2;
	
	// Session state type
	public static final int PENDING = 0;		//Resource Session in Pending state
	public static final int ACTIVE = 1;		//Resource Session in Active State
	public static final int INACTIVE = -1;		//Resource Session in inactive state

	// Measurement Counters
	
	
	// CC Request Type
	public static final short INITIAL_REQUEST		= 1;
	public static final short UPDATE_REQUEST		= 2;
	public static final short TERMINATION_REQUEST	= 3;
	public static final short EVENT_REQUEST		= 4;

	// Measurement Counters
	//For Client mode
	public static final String CCR_EVENT_COUNTER_OUT = "Credit Control Event Request Count OUT";
	public static final String CCR_SESSION_COUNTER_OUT = "Credit Control Session Request Count OUT";// For Client 
	public static final String CCR_DIRECT_DEBIT_COUNTER_OUT ="Credit Control Direct Debit Request Count OUT";
	public static final String CCR_ACCOUNT_REFUND_COUNTER_OUT ="Credit Control Account Refund Request Count OUT";
	public static final String CCR_BALANCE_CHECK_COUNTER_OUT ="Credit Control Balance Check Request Count OUT";
	public static final String CCR_PRICE_ENQUIRY_COUNTER_OUT ="Credit Control Price Enquiry Request Count OUT";
	public static final String CCR_FIRST_INTEROGATION_COUNTER_OUT = "Credit Control First Interogation Request Count OUT";
	public static final String CCR_INTERIM_INTEROGATION_COUNTER_OUT = "Credit Control Interim Interogation Request Count OUT";
	public static final String CCR_FINAL_INTEROGATION_COUNTER_OUT = "Credit Control Final Interogation Request Count OUT";

	public static final String CCA_EVENT_1XXX_COUNTER_IN = "Credit Control 1xxx Event Answer Count IN";
	public static final String CCA_EVENT_2XXX_COUNTER_IN = "Credit Control 2xxx Event Answer Count IN";
	public static final String CCA_EVENT_3XXX_COUNTER_IN = "Credit Control 3xxx Event Answer Count IN";
	public static final String CCA_EVENT_4XXX_COUNTER_IN = "Credit Control 4xxx Event Answer Count IN";
	public static final String CCA_EVENT_5XXX_COUNTER_IN = "Credit Control 5xxx Event Answer Count IN";	
	public static final String CCA_SESSION_1XXX_COUNTER_IN = "Credit Control 1xxx Session Answer Count IN";
	public static final String CCA_SESSION_2XXX_COUNTER_IN = "Credit Control 2xxx Session Answer Count IN";
	public static final String CCA_SESSION_3XXX_COUNTER_IN = "Credit Control 3xxx Session Answer Count IN";
	public static final String CCA_SESSION_4XXX_COUNTER_IN = "Credit Control 4xxx Session Answer Count IN";
	public static final String CCA_SESSION_5XXX_COUNTER_IN = "Credit Control 5xxx Session Answer Count IN";
	//Error Counter
	public static final String CCR_SEND_ERROR = "Credit Control Request Send Error Count";
	
	//For Server mode
	public static final String CCR_EVENT_COUNTER_IN = "Credit Control Event Request Count IN";
	public static final String CCR_SESSION_COUNTER_IN = "Credit Control Session Request Count IN";
	public static final String CCR_DIRECT_DEBIT_COUNTER_IN ="Credit Control Direct Debit Request Count IN";
	public static final String CCR_ACCOUNT_REFUND_COUNTER_IN ="Credit Control Account Refund Request Count IN";
	public static final String CCR_BALANCE_CHECK_COUNTER_IN ="Credit Control Balance Check Request Count IN";
	public static final String CCR_PRICE_ENQUIRY_COUNTER_IN ="Credit Control Price Enquiry Request Count IN";
	public static final String CCR_FIRST_INTEROGATION_COUNTER_IN = "Credit Control First Interogation Request Count IN";
	public static final String CCR_INTERIM_INTEROGATION_COUNTER_IN = "Credit Control Interim Interogation Request Count IN";
	public static final String CCR_FINAL_INTEROGATION_COUNTER_IN = "Credit Control Final Interogation Request Count IN";
	
	public static final String CCA_EVENT_1XXX_COUNTER_OUT = "Credit Control 1xxx Event Answer Count OUT";
	public static final String CCA_EVENT_2XXX_COUNTER_OUT = "Credit Control 2xxx Event Answer Count OUT";
	public static final String CCA_EVENT_3XXX_COUNTER_OUT = "Credit Control 3xxx Event Answer Count OUT";
	public static final String CCA_EVENT_4XXX_COUNTER_OUT = "Credit Control 4xxx Event Answer Count OUT";
	public static final String CCA_EVENT_5XXX_COUNTER_OUT = "Credit Control 5xxx Event Answer Count OUT";	
	public static final String CCA_SESSION_1XXX_COUNTER_OUT = "Credit Control 1xxx Session Answer Count OUT";
	public static final String CCA_SESSION_2XXX_COUNTER_OUT = "Credit Control 2xxx Session Answer Count OUT";
	public static final String CCA_SESSION_3XXX_COUNTER_OUT = "Credit Control 3xxx Session Answer Count OUT";
	public static final String CCA_SESSION_4XXX_COUNTER_OUT = "Credit Control 4xxx Session Answer Count OUT";
	public static final String CCA_SESSION_5XXX_COUNTER_OUT = "Credit Control 5xxx Session Answer Count OUT";
	
}
