package com.baypackets.ase.ra.diameter.sh.utils;

public interface Constants {
	
	// Miscellaneous
	public static final int MAX_RETYR_COUNTER = 5;
	
	// configuration properties
	public static String STACK_PROVIDER = "stack_provider";
	public static String SH_CONFIG_FILE = "sh_config_file";

	public static String CMD_SH_STATS = "sh-stats";
	public static String CMD_PRINT_SH_UDR_STATS = "print-sh-udr-stats";


	// protocol 
	public static String PROTOCOL = "SH";
	public static String JAR_NAME = "sh-full.jar";
	
	//Sh message type
	public static final int UDR = 1;
	public static final int UDA = 2;
	public static final int PUR = 3;
	public static final int PUA = 4;
	public static final int SNR = 5;
	public static final int SNA = 6;
	public static final int PNR = 7;
	public static final int PNA = 8;
	
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
	
	// Counter names for client mode
	public static final String UDR_COUNTER_OUT = "Number of UDRs OUT";
	public static final String PUR_COUNTER_OUT = "Number of PURs OUT";
	public static final String SNR_COUNTER_OUT = "Number of SNRs OUT";
	public static final String PNR_COUNTER_IN = "Number of PNRs IN";
	public static final String UDA_1xxx_COUNTER_IN = "Number of 1xxx UDAs IN";
	public static final String UDA_2xxx_COUNTER_IN = "Number of 2xxx UDAs IN";
	public static final String UDA_3xxx_COUNTER_IN = "Number of 3xxx UDAs IN";
	public static final String UDA_4xxx_COUNTER_IN = "Number of 4xxx UDAs IN";
	public static final String UDA_5xxx_COUNTER_IN = "Number of 5xxx UDAs IN";
	public static final String PUA_1xxx_COUNTER_IN = "Number of 1xxx PUAs IN";
	public static final String PUA_2xxx_COUNTER_IN = "Number of 2xxx PUAs IN";
	public static final String PUA_3xxx_COUNTER_IN = "Number of 3xxx PUAs IN";
	public static final String PUA_4xxx_COUNTER_IN = "Number of 4xxx PUAs IN";
	public static final String PUA_5xxx_COUNTER_IN = "Number of 5xxx PUAs IN";
	public static final String SNA_1xxx_COUNTER_IN = "Number of 1xxx SNAs IN";
	public static final String SNA_2xxx_COUNTER_IN = "Number of 2xxx SNAs IN";
	public static final String SNA_3xxx_COUNTER_IN = "Number of 3xxx SNAs IN";
	public static final String SNA_4xxx_COUNTER_IN = "Number of 4xxx SNAs IN";
	public static final String SNA_5xxx_COUNTER_IN = "Number of 5xxx SNAs IN";
	public static final String PNA_1xxx_COUNTER_OUT = "Number of 1xxx PNAs OUT";
	public static final String PNA_2xxx_COUNTER_OUT = "Number of 2xxx PNAs OUT";
	public static final String PNA_3xxx_COUNTER_OUT = "Number of 3xxx PNAs OUT";
	public static final String PNA_4xxx_COUNTER_OUT = "Number of 4xxx PNAs OUT";
	public static final String PNA_5xxx_COUNTER_OUT = "Number of 5xxx PNAs OUT";
	public static final String UDR_ERROR_COUNTER_OUT = "Number of error count UDR OUT";
	public static final String PUR_ERROR_COUNTER_OUT = "Number of error count UDR OUT";
	public static final String SNR_ERROR_COUNTER_OUT = "Number of error count UDR OUT";
	public static final String PNR_ERROR_COUNTER_OUT = "Number of error count UDR OUT";

	// counter names for server mode
	public static final String UDR_COUNTER_IN = "Number of UDRs IN";
	public static final String PUR_COUNTER_IN = "Number of PURs IN";
	public static final String SNR_COUNTER_IN = "Number of SNRs IN";
	public static final String PNR_COUNTER_OUT = "Number of PNRs OUT";
	public static final String UDA_1xxx_COUNTER_OUT = "Number of 1xxx UDAs OUT";
	public static final String UDA_2xxx_COUNTER_OUT = "Number of 2xxx UDAs OUT";
	public static final String UDA_3xxx_COUNTER_OUT = "Number of 3xxx UDAs OUT";
	public static final String UDA_4xxx_COUNTER_OUT = "Number of 4xxx UDAs OUT";
	public static final String UDA_5xxx_COUNTER_OUT = "Number of 5xxx UDAs OUT";
	public static final String PUA_1xxx_COUNTER_OUT = "Number of 1xxx PUAs OUT";
	public static final String PUA_2xxx_COUNTER_OUT = "Number of 2xxx PUAs OUT";
	public static final String PUA_3xxx_COUNTER_OUT = "Number of 3xxx PUAs OUT";
	public static final String PUA_4xxx_COUNTER_OUT = "Number of 4xxx PUAs OUT";
	public static final String PUA_5xxx_COUNTER_OUT = "Number of 5xxx PUAs OUT";
	public static final String SNA_1xxx_COUNTER_OUT = "Number of 1xxx SNAs OUT";
	public static final String SNA_2xxx_COUNTER_OUT = "Number of 2xxx SNAs OUT";
	public static final String SNA_3xxx_COUNTER_OUT = "Number of 3xxx SNAs OUT";
	public static final String SNA_4xxx_COUNTER_OUT = "Number of 4xxx SNAs OUT";
	public static final String SNA_5xxx_COUNTER_OUT = "Number of 5xxx SNAs OUT";
	public static final String PNA_1xxx_COUNTER_IN = "Number of 1xxx PNAs IN";
	public static final String PNA_2xxx_COUNTER_IN = "Number of 2xxx PNAs IN";
	public static final String PNA_3xxx_COUNTER_IN = "Number of 3xxx PNAs IN";
	public static final String PNA_4xxx_COUNTER_IN = "Number of 4xxx PNAs IN";
	public static final String PNA_5xxx_COUNTER_IN = "Number of 5xxx PNAs IN";
	public static final String UDR_ERROR_COUNTER_IN = "Number of error count UDR IN";
	public static final String PUR_ERROR_COUNTER_IN = "Number of error count UDR IN";
	public static final String SNR_ERROR_COUNTER_IN = "Number of error count UDR IN";
	public static final String PNR_ERROR_COUNTER_IN = "Number of error count UDR IN";


	public static final String[] SERVER_COUNTERS={
			/*CCR_EVENT_COUNTER_IN,CCR_SESSION_COUNTER_IN,CCR_DIRECT_DEBIT_COUNTER_IN,CCR_ACCOUNT_REFUND_COUNTER_IN,
			CCR_BALANCE_CHECK_COUNTER_IN,CCR_PRICE_ENQUIRY_COUNTER_IN,CCR_FIRST_INTEROGATION_COUNTER_IN,CCR_INTERIM_INTEROGATION_COUNTER_IN,CCR_FINAL_INTEROGATION_COUNTER_IN,

			CCA_EVENT_1XXX_COUNTER_OUT,CCA_EVENT_2XXX_COUNTER_OUT,CCA_EVENT_3XXX_COUNTER_OUT,CCA_EVENT_4XXX_COUNTER_OUT,CCA_EVENT_5XXX_COUNTER_OUT,

			CCA_SESSION_1XXX_COUNTER_OUT,CCA_SESSION_2XXX_COUNTER_OUT,CCA_SESSION_3XXX_COUNTER_OUT,CCA_SESSION_4XXX_COUNTER_OUT,CCA_SESSION_5XXX_COUNTER_OUT
*/	// TO DO

	};

	public static final String[] CLIENT_COUNTERS={
	/*		CCR_EVENT_COUNTER_OUT,CCR_SESSION_COUNTER_OUT ,CCR_DIRECT_DEBIT_COUNTER_OUT ,CCR_ACCOUNT_REFUND_COUNTER_OUT,CCR_BALANCE_CHECK_COUNTER_OUT ,
			CCR_PRICE_ENQUIRY_COUNTER_OUT,CCR_FIRST_INTEROGATION_COUNTER_OUT,CCR_INTERIM_INTEROGATION_COUNTER_OUT,CCR_FINAL_INTEROGATION_COUNTER_OUT,

			CCA_EVENT_1XXX_COUNTER_IN,CCA_EVENT_2XXX_COUNTER_IN ,CCA_EVENT_3XXX_COUNTER_IN,CCA_EVENT_4XXX_COUNTER_IN ,CCA_EVENT_5XXX_COUNTER_IN ,

			CCA_SESSION_1XXX_COUNTER_IN ,CCA_SESSION_2XXX_COUNTER_IN,CCA_SESSION_3XXX_COUNTER_IN,CCA_SESSION_4XXX_COUNTER_IN,CCA_SESSION_5XXX_COUNTER_IN
*/	// TO DO

	};

}
