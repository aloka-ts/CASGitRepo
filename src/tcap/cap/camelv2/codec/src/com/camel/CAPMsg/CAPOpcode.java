package com.camel.CAPMsg;

/**
 * This interface contains operation codes of 
 * all CAP messages.
 * @author nkumar
 *
 */
public interface CAPOpcode {
	
	// For upcoming messages from network -String representation of hexadecimal codes
	String IDP = "0x00" ;
	String EVENT_REPORT_BCSM = "0x18" ;
	String APPLY_CHARGING_REPORT = "0x24" ;
	String PROMPT_COLLECT_USER_INFO = "0x30" ;
	String SPECIALIZED_RSOURCE_RPRT = "0x31" ;
	String CALL_INFORMATION_RPT = "0x2C" ;
	String ASSIST_REQ_INST = "0x10" ;
	String ACTIVITY_TEST_RESULT = "0x37" ;
	String ENTITY_RELEASED = "0x60" ;
	
	// OpCode for messages send to network
	byte REQUEST_REPORT = (byte)0x17 ;
	byte CONNECT = (byte)0x14 ;
	byte RELEASE_CALL = (byte)0x16 ;
	byte CONTINUE = (byte)0x1F ;
	byte ACTIVITY_TEST = (byte)0x37 ;
	byte CONTINUE_ARG = (byte)0x58 ;
	byte APPLY_CHARGING = (byte)0x23 ;
	byte PROMPT_COLLECT = (byte)0x30 ;
	byte RESET_TIMER = (byte)0x21 ;
	byte PLAY_ANNOUNCEMENT = (byte)0x2F ;
	byte CONNECT_TO_RESOURCE = (byte)0x13 ;
	byte DISCONNECT_FORWARD_CONNECTION = (byte)0x12 ;
	byte DISCONNECT_FORWARD_CONNECTION_ARG = (byte)0x56 ;
	byte SPECIALIZED_RSOURCE_REPORT = (byte)0x31 ;
	byte IDP_CODE = (byte)0x00 ;
	byte CALL_INFORMATION_REQUEST = (byte)0x2D ;
	byte ESTABLISH_TEMP_CONNECTION = (byte)0x11 ;
	byte FURNISH_CHARGING_INFORMATION = (byte)0x22 ;
	
	//Mapping of Param Identifier value to Hex Value
	//TODO a0
	byte PARAMETERTYPE_SINGLE = (byte)0x04 ;
	byte PARAMETERTYPE_SEQUENCE = (byte)0x30 ;
	byte PARAMETERTYPE_SET = (byte)0x31 ;
	
	// Class type of CAP operations
	int RRBCSM_CLASS = 2 ;
	int CONNECT_CLASS = 2 ;
	int CONTINUE_CLASS = 4 ;
	int APLLY_CHARGING_CLASS = 2 ;
	int RELEASE_CALL_CLASS = 4;
	int PROMPT_COLLECT_CLASS = 1 ;
	int PLAY_ANNOUNCEMENT_CLASS = 2 ;
	int CONNECT_TO_RESOURCE_CLASS = 2 ;
	int DISCONNECT_FORWARD_CONNECTION_CLASS = 2 ;
	int SPECIALIZED_RSOURCE_REPORT_CLASS = 4 ;
	int CALL_INFORMATION_REQ_CLASS = 2 ;
	int CALL_INFORMATION_RPT_CLASS = 4 ;
	int CLASS_ONE = 1 ;
	int CLASS_TWO = 2 ;
	int CLASS_THREE = 3 ;
	int CLASS_FOUR = 4 ;
}
