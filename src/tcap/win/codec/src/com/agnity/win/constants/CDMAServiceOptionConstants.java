package com.agnity.win.constants;

// These constant values are obtained from the following link
//http://anonsvn.wireshark.org/wireshark/trunk-1.0/asn1/ansi_map/packet-ansi_map-template.c
public interface CDMAServiceOptionConstants {

		 final int NULL = 0;
		 final int BASIC_VARIABLE_RATE_VOICE_SVC = 1 ;
		 final int MOBILE_STATION_LOOPBACK_8KBPS = 2;
		 final int ENHANCED_VARIABLE_RATE_VOICE_SVC = 3;
		 final int ASYNCH_DATA_SVC_9_DOT_6_KBPS = 4;
		 final int GROUP_3_FACSIMILE_9_DOT_6KBPS = 5;
		 final int SHORT_MESSAGE_SVCS = 6;
		 final int PKT_DATA_SVC_INTRNT_OR_ISO_PROTOCOL_STCK = 7;
		 final int PKT_DATA_SVC_CDPD_PROTOCOL_STACK = 8;
		 final int MOBILE_STATION_LOOPBACK_13KB = 9;
		 final int STU3_TRANSPARENT_SVC = 10;
		 final int STU3_NON_TRANSPARENT_SVC = 11;
		 final int ASYNCH_DATA_SVC = 12;
		 final int GROUP_3_FACSIMILE = 13;
		 final int SHORT_MSG_SVCS_RATESET_2 = 14;
		 final int PKT_DATA_SVC_INTRNT_OR_ISO_PROTOCOL_STACK_14_DOT_4_KBPS = 15;
		 final int PKT_DATA_SVC_CDPD_PROTOCOL_STACK_14_DOT_4_KBPS = 16;
		 final int HIGH_RATE_VOICE_SVC_13_KBPS = 17;
		 final int OVER_THE_AIR_PARAM_ADMIN_RATE_SET_1 = 18;
		 final int OVER_THE_AIR_PARAM_ADMIN_RATE_SET_2 = 19;
		 final int GROUP_3_ANALOG_FACSIMILE_RATE_SET_1 = 20;
		 final int GROUP_3_ANALOG_FACSIMILE_RATE_SET_2 = 21;
		 final int HIGH_SPEED_PKT_DATA_SVC_INTRNT_OR_ISO_RS1_FWD_RS1_REVERSE= 22;
		 final int HIGH_SPEED_PKT_DATA_SVC_INTRNT_OR_ISO_RS1_FWD_RS2_REVERSE = 23;
		 final int HIGH_SPEED_PKT_DATA_SVC_INTRNT_OR_ISO_RS2_FWD_RS1_REVERSE= 24;
		 final int HIGH_SPEED_PKT_DATA_SVC_INTRNT_OR_ISO_RS2_FWD_RS2_REVERSE = 25;
		 final int HIGH_SPEED_PKT_DATA_SVC_CDPD_PROTOCOL_STACK_RS1_FWD_RS1_REVERSE = 26;
		 final int HIGH_SPEED_PKT_DATA_SVC_CDPD_PROTOCOL_STACK_RS1_FWD_RS2_REVERSE = 27;
		 final int HIGH_SPEED_PKT_DATA_SVC_CDPD_PROTOCOL_STACK_RS2_FWD_RS1_REVERSE = 28;
		 final int HIGH_SPEED_PKT_DATA_SVC_CDPD_PROTOCOL_STACK_RS2_FWD_RS2_REVERSE = 29;
		 final int SUPP_CHANNEL_LOOPBACK_TEST_FOR_RATE_SET_1 = 30;
		 final int SUPP_CHANNEL_LOOPBACK_TEST_FOR_RATE_SET_2 = 31;
		 final int TEST_DATA_SVC_OPTION = 32;
		 final int CDMA2000_HIGH_SPEED_PKT_DATA_SVC_INTRNT_ISO_PROTOCOL_STACK_1 = 33;
		 final int CDMA2000_HIGH_SPEED_PKT_DATA_SVC_ISO_PROTOCOL_STACK = 34;
		 final int LOCATION_SVCS_RATE_SET_1_9DOT6_KBPS =35;
		 final int LOCATION_SVCS_RATE_SET_1_14DOT4_KBPS = 36;
		 final int ISDN_INTERWORKING_SVC = 37;
		 final int GSM_VOICE = 38;
		 final int GSM_CIRCUIT_DATA = 39;
		 final int GSM_PKT_DATA = 40;
		 final int GSM_SHORT_MSG_SVC = 41;
		 final int NONE_RESERVED_FOR_MCMAP_STD_SVC_OPTIONS = 42;
		 final int MARKOV_SVC_OPTION = 43;
		 final int LOOPBACK_SVC_OPTION = 44;
		 final int SELECTABLE_MODE_VOCODER = 2;
		 final int CIRCUIT_VIDEO_CONF_32KBPS = 2;
		 final int KBPS_CIRCUIT_VIDEO_CONF_64KBPS = 2;
		 final int HRPD_ACCOUNTING_RECORDS_ID = 2;
		 final int LLA_ROHC_HEADER_REMOVAL = 2;
		 final int LLA_ROHC_HEADER_COMPRESSION = 2;
		 final int SRC_CONTROLLED_VMR_WB_SPEECH_CODEC_RATE_SET_2 = 62;
		 final int SRC_CONTROLLED_VMR_WB_SPEECH_CODEC_RATE_SET_1 = 63;
		 final int HRPD_AUXILIARY_PKT_DATA_SVC_INSTANCE = 64;
		 final int CDMA2000_GPRS_INTER_WORKING = 65;
		 final int CDMA2000_HIGH_SPEED_PKT_DATA_SVC_INTRNT_ISO_PROTOCOL_STACK_2 = 66;
		 final int HRPD_PKT_DATA_IP_SVC_WHR_HIGHER_LAYER_PROTOCOL_IP_OR_ROHC = 67;
		 final int ENHANCED_VAR_RATE_VOICE_SVC_EVRCB = 68;
		 final int HRPD_PKT_DATA_SVC = 69;
		 final int ENHANCED_VAR_RATE_VOICE_SVC_EVRCWB = 70;
		 final int NONE_RESERVED_FOR_STD_SVC_OPTIONS = 4099;
		 final int ASYNCH_DATA_SVC_REV1 = 4100;
		 final int GROUP_3_FACSIMILE_REV1 = 4101;
		 final int RESERVED_FOR_STD_SVC_OPTION = 4102;
		 final int PKT_DATA_SVC_INTRNT_ISO_REV1 = 4103;
		 final int PKT_DATA_SVC_CDPD_REV1 = 4104;
		 final int RESERVED_FOR_STD_SVC_OPTIONS = 32767;
		 final int QCELP = 32768;
		 final int PROPRIETARY_QUALCOMM_INCORPORATED = 32771;
		 final int PROPRIETARY_OKI_TELECOM = 32775;
		 final int PROPRIETARY_LUCENT_TECH = 32779;
		 final int NOKIA = 32783;
		 final int NORTEL_NTWK = 32787;
		 final int SONY_ELECTRONICS_INC = 32791;
		 final int MOTOROLA = 32795;
		 final int QUALCOMM_INCORPORATED1 = 32799;
		 final int QUALCOMM_INCORPORATED2 = 32803;
		 final int QUALCOMM_INCORPORATED3 = 32807;
		 final int QUALCOMM_INCORPORATED4 = 32811;
		 final int LUCENT_TECH = 32815;
		 final int DENSO_INTERNATIONAL1 = 32819;
		 final int MOTOROLA2 = 32823;
		 final int DENSO_INTERNATIONAL2 = 32827;
		 final int DENSO_INTERNATIONAL3 = 32831;
		 final int DENSO_INTERNATIONAL4 = 32835;
		 final int NEC_AMERICA = 32839;
		 final int SAMSUNG_ELECTRNICS = 32843;
		 final int TEXAS_INSTRUMENTS_INC = 32847;
		 final int TOSHIBA_CORPORATION = 32851;
		 final int LG_ELECTRONICS_INC = 32855;
		 final int VIA_TELECOM_INC = 32859;
		 
		
	}

	
	 
	

