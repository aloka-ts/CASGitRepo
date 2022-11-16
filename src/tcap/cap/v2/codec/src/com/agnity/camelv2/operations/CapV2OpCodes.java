package com.agnity.camelv2.operations;

/**
 * 
 * This interface contains operation codes of all INAP messages.
 * 
 * @author saneja
 * 
 */
public interface CapV2OpCodes {

	// For upcoming messages from network -String representation of hexadecimal codes
	String IDP = "0x00" ;
	String EVENT_REPORT_BCSM = "0x18" ;
	String APPLY_CHARGING_REPORT = "0x24" ;
	String PROMPT_COLLECT_USER_INFO = "0x30" ;
	String SPECIALIZED_RSOURCE_RPRT = "0x31" ;
	String CALL_INFORMATION_RPT = "0x2C" ;
	String ASSIST_REQ_INST = "0x10" ;
	String ENTITY_RELEASED = "0x60" ;

	// OpCode for messages send to network
	String REQUEST_REPORT = "0x17" ;
	String CONNECT = "0x14" ;
	String RELEASE_CALL = "0x16" ;
	String CONTINUE_WITH_ARG = "0x58" ;
	String APPLY_CHARGING = "0x23" ;
	String RESET_TIMER = "0x21" ;
	String PROMPT_COLLECT = "0x30" ;
	String PLAY_ANNOUNCEMENT = "0x2F" ;
	String CONNECT_TO_RESOURCE = "0x13" ;
	String DISCONNECT_FORWARD_CONNECTION_WITH_ARGS = "0x56" ;
	String CALL_INFORMATION_REQUEST = "0x2D" ;
	String ESTABLISH_TEMP_CONNECTION = "0x11" ;
	String FURNISH_CHARGING_INFORMATION = "0x22" ;
	//No encodeing reqd:
	String DISCONNECT_FORWARD_CONNECTION_WITHOUT_ARGS = "0x12" ;
	String CONTINUE_WITHOUT_ARG = "0x1F" ;
	String ACTIVITY_TEST = "0x37" ;
	String CANCEL = "0x35";
		
	
	
	byte IDP_BYTE = (byte)0x00 ;
	byte EVENT_REPORT_BCSM_BYTE = (byte)0x18 ;
	byte APPLY_CHARGING_REPORT_BYTE = (byte)0x24 ;
	byte PROMPT_COLLECT_USER_INFO_BYTE = (byte)0x30 ;
	byte SPECIALIZED_RSOURCE_RPRT_BYTE = (byte)0x31 ;
	byte CALL_INFORMATION_RPT_BYTE = (byte)0x2C ;
	byte ASSIST_REQ_INST_BYTE = (byte)0x10 ;
	byte ENTITY_RELEASED_BYTE = (byte)0x60 ;
	
	// OpCode for messages send to network
	byte REQUEST_REPORT_BYTE = (byte)0x17 ;
	byte CONNECT_BYTE = (byte)0x14 ;
	byte RELEASE_CALL_BYTE = (byte)0x16 ;
	byte CONTINUE_WITHOUT_ARG_BYTE = (byte)0x1F ;
	byte CONTINUE_WITH_ARG_BYTE = (byte)0x58 ;
	byte ACTIVITY_TEST_BYTE = (byte)0x37 ;
	byte APPLY_CHARGING_BYTE = (byte)0x23 ;
	byte PROMPT_COLLECT_BYTE = (byte)0x30 ;
	byte RESET_TIMER_BYTE = (byte)0x21 ;
	byte PLAY_ANNOUNCEMENT_BYTE = (byte)0x2F ;
	byte CONNECT_TO_RESOURCE_BYTE = (byte)0x13 ;
	byte DISCONNECT_FORWARD_CONNECTION_WITHOUT_ARGS_BYTE = (byte)0x12 ;
	byte DISCONNECT_FORWARD_CONNECTION_WITH_ARGS_BYTE = (byte)0x56 ;
	byte CALL_INFORMATION_REQUEST_BYTE = (byte)0x2D ;
	byte ESTABLISH_TEMP_CONNECTION_BYTE = (byte)0x11 ;
	byte FURNISH_CHARGING_INFORMATION_BYTE = (byte)0x22 ;
	byte SCI_BYTE = (byte)0x46 ;
	byte RRBE_BYTE = (byte)0x23 ;
	byte CANCEL_BYTE = (byte)0x35;
	
	// Internal Data encoding type
	String CAMEL_BILL_CHARGE_CHAR = "0x99";
	String CAMEL_CALL_RESULT      = "0x98";
}
