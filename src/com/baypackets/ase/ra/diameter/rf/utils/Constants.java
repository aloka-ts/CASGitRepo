package com.baypackets.ase.ra.diameter.rf.utils;

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
	public static String Rf_CONFIG_FILE = "rf_config_file";
	
	// protocol 
	public static String PROTOCOL = "RF";		//Offline Charging RF Client Stack
	public static String JAR_NAME = "rf-full.jar";
	//Data type
	public static final int IMS_PUBLIC_IDENTITY = 1;
	public static final int MSISDN = 2;
	public static final int CS_USER_STATE = 3;
	public static final int PS_USER_STATE = 4;
	public static final int IMS_USER_STATE = 5;
	public static final int CS_LOCATION = 6;
	public static final int PS_LOCATION = 7;
	public static final int REQUEST_URI_SPT = 8;
	public static final int SIP_METHOD_SPT = 9;
	public static final int SIP_HEADER_SPT = 10;
	public static final int SESSION_CASE_SPT = 11;
	public static final int SESSION_DESC_SPT = 12;

	// Request type
	public static final int EVENT_RECORD = 1;		//Event Based Charging Accounting-Record-Type
	public static final int START_RECORD = 2;		//Session Based Charging Accounting-Record-Type
	public static final int INTERIM_RECORD = 3;
	public static final int STOP_RECORD = 4;

	public static final int EVENT = 1;
	public static final int SESSION = 2;
	
	// Session state type
	public static final int PENDING = 0;		//Resource Session in Pending state
	public static final int ACTIVE = 1;		//Resource Session in Active State
	public static final int INACTIVE = -1;		//Resource Session in inactive state

	// Counters' name
	//client mode
	public static final String ACR_EVENT_COUNTER_OUT = "Number of Accounting Request in Event based offline charging OUT";
	public static final String ACR_SESSION_COUNTER_OUT = "Number of Accounting Request in SESSION based offline charging OUT";

	public static final String ACA_EVENT_1xxx_COUNTER_IN = "Number of 1xxx ACAs Event based charging IN";
	public static final String ACA_EVENT_2xxx_COUNTER_IN = "Number of 2xxx ACAs Event based charging IN";
	public static final String ACA_EVENT_3xxx_COUNTER_IN = "Number of 3xxx ACAs Event based charging IN";
	public static final String ACA_EVENT_4xxx_COUNTER_IN = "Number of 4xxx ACAs Event based charging IN";
	public static final String ACA_EVENT_5xxx_COUNTER_IN = "Number of 5xxx ACAs Event based charging IN";

	public static final String ACA_SESSION_1xxx_COUNTER_IN = "Number of 1xxx ACAs Session based charging IN";
	public static final String ACA_SESSION_2xxx_COUNTER_IN = "Number of 2xxx ACAs Session based charging IN";
	public static final String ACA_SESSION_3xxx_COUNTER_IN = "Number of 3xxx ACAs Session based charging IN";
	public static final String ACA_SESSION_4xxx_COUNTER_IN = "Number of 4xxx ACAs Session based charging IN";
	public static final String ACA_SESSION_5xxx_COUNTER_IN = "Number of 5xxx ACAs Session based charging IN";
	
	// server mode
	public static final String ACR_EVENT_COUNTER_IN = "Number of Accounting Request in Event based offline charging IN";
	public static final String ACR_SESSION_COUNTER_IN = "Number of Accounting Request in SESSION based offline charging IN";

	public static final String ACA_EVENT_1xxx_COUNTER_OUT = "Number of 1xxx ACAs Event based charging OUT";
	public static final String ACA_EVENT_2xxx_COUNTER_OUT = "Number of 2xxx ACAs Event based charging OUT";
	public static final String ACA_EVENT_3xxx_COUNTER_OUT = "Number of 3xxx ACAs Event based charging OUT";
	public static final String ACA_EVENT_4xxx_COUNTER_OUT = "Number of 4xxx ACAs Event based charging OUT";
	public static final String ACA_EVENT_5xxx_COUNTER_OUT = "Number of 5xxx ACAs Event based charging OUT";

	public static final String ACA_SESSION_1xxx_COUNTER_OUT = "Number of 1xxx ACAs Session based charging OUT";
	public static final String ACA_SESSION_2xxx_COUNTER_OUT = "Number of 2xxx ACAs Session based charging OUT";
	public static final String ACA_SESSION_3xxx_COUNTER_OUT = "Number of 3xxx ACAs Session based charging OUT";
	public static final String ACA_SESSION_4xxx_COUNTER_OUT = "Number of 4xxx ACAs Session based charging OUT";
	public static final String ACA_SESSION_5xxx_COUNTER_OUT = "Number of 5xxx ACAs Session based charging OUT";

}
