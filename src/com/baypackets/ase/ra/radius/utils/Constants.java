package com.baypackets.ase.ra.radius.utils;

/**
 * This interface defines the various constant values that can be used in radius ra
 *
 * @author Amit Baxi
 */
  public interface Constants 
{
	  String PROP_RADIUS_SERVER_IP = "radius.server.ip";
	  String PROP_RADIUS_SHARED_SECRET = "radius.shared.secret";
	  String PROP_RADIUS_AUTH_PORT = "radius.auth.port";
	  String PROP_RADIUS_ACCOUNTING_PORT = "radius.accounting.port";
	  String PROP_RADIUS_SOCKET_TIMEOUT = "radius.socket.timeout";
	  String PROP_RADIUS_SESSION_TIMEOUT = "radius.session.timeout";
	  String PROP_RADIUS_IDLE_TIMEOUT = "radius.idle.timeout";
	  String PROP_RADIUS_SERVER_MAX_THREADS = "radius.server.maxthreads";
	  String PROP_RADIUS_DUPLICATE_INTERVAL = "radius.duplicateInterval";
	    
	// Miscellaneous
	      int MAX_RETYR_COUNTER = 5;
	
	// configuration properties
	    String STACK_PROVIDER = "stack_provider";
	    String RADIUS_CONFIG_FILE = "radius_config_file";
	
	// protocol 
	    String PROTOCOL = "RADIUS";
	    String JAR_NAME = "radius-full.jar";

	
	// Session state type
	      int PENDING = 0;		//Resource Session in Pending state
	      int ACTIVE = 1;		//Resource Session in Active State
	      int INACTIVE = -1;		//Resource Session in inactive state

	// Measurement Counters
	
	
	// RADIUS Accounting Request Type
	      short ACCOUNTING_START_COUNTER_REQUEST		= 1;
	      short ACCOUNTING_UPDATE_COUNTER_REQUEST		= 2;
	      short ACCOUNTING_STOP_COUNTER_REQUEST	= 3;

	// Measurement Counters
	//For Client mode
	      String RADIUS_ACCESS_REQUEST_COUNTER_OUT = "Radius Access Request Count OUT";
	      String RADIUS_ACCOUNTING_REQUEST_COUNTER_OUT = "Radius Accounting Request Count OUT";
	      
	      String RADIUS_ACCOUNTING_ON_REQUEST_COUNTER_OUT ="Radius Accounting On Request Count OUT";
	      String RADIUS_ACCOUNTING_OFF_REQUEST_COUNTER_OUT ="Radius Accounting Off Request Count OUT";
	      String RADIUS_ACCOUNTING_START_REQUEST_COUNTER_OUT ="Radius Accounting Start Request Count OUT";
	      String RADIUS_ACCOUNTING_UPDATE_REQUEST_COUNTER_OUT ="Radius Accounting Update Request Count OUT";
	      String RADIUS_ACCOUNTING_STOP_REQUEST_COUNTER_OUT = "Radius Accounting Stop Request Count OUT";

	      String RADIUS_ACCESS_ACCEPT_COUNTER_IN="Radius Access Accept Count IN";
	      String RADIUS_ACCESS_REJECT_COUNTER_IN="Radius Access Reject Count IN";
	      String RADIUS_ACCESS_CHALLANGE_COUNTER_IN="Radius Access Challange Count IN";
	
	      String RADIUS_ACCOUNTING_RESPONSE_COUNTER_IN="Radius Accounting Response Count IN";
	
	//Error Counter
	      String RADIUS_REQUEST_SEND_COUNTER_ERROR = "RADIUS Request Send Error Count";
	      String RADIUS_RESPONSE_SEND_COUNTER_ERROR = "RADIUS Response Send Error Count";
	      String RADIUS_REQUEST_FAILED_TO_TRIGGER_AN_APPLICATION="Radius Request Failed To Trigger An Application Count";
	//For Server mode
	
	      String RADIUS_ACCESS_REQUEST_COUNTER_IN = "Radius Access Request Count IN";
	      String RADIUS_ACCOUNTING_REQUEST_COUNTER_IN = "Radius Accounting Request Count IN";
	      
	      String RADIUS_ACCOUNTING_ON_REQUEST_COUNTER_IN ="Radius Accounting On Request Count IN";
	      String RADIUS_ACCOUNTING_OFF_REQUEST_COUNTER_IN ="Radius Accounting Off Request Count IN";
	      String RADIUS_ACCOUNTING_START_REQUEST_COUNTER_IN ="Radius Accounting Start Request Count IN";
	      String RADIUS_ACCOUNTING_UPDATE_REQUEST_COUNTER_IN ="Radius Accounting Update Request Count IN";
	      String RADIUS_ACCOUNTING_STOP_REQUEST_COUNTER_IN = "Radius Accounting Stop Request Count IN";
	      
	      String RADIUS_ACCESS_ACCEPT_COUNTER_OUT="Radius Access Accept Count OUT";
	      String RADIUS_ACCESS_REJECT_COUNTER_OUT="Radius Access Reject Count OUT";
	      String RADIUS_ACCESS_CHALLANGE_COUNTER_OUT="Radius Access Challange Count OUT";
	
	      String RADIUS_ACCOUNTING_RESPONSE_COUNTER_OUT="Radius Accounting Response Count OUT";
	      
	 /**
     * Packet type codes.
     */
	      int ACCESS_REQUEST      = 1;
	      int ACCESS_ACCEPT       = 2;
	      int ACCESS_REJECT       = 3;
	      int ACCOUNTING_REQUEST  = 4;
	      int ACCOUNTING_RESPONSE = 5;
	      int ACCOUNTING_STATUS   = 6;
	      int PASSWORD_REQUEST    = 7;
	      int PASSWORD_ACCEPT     = 8;
	      int PASSWORD_REJECT     = 9;
	      int ACCOUNTING_MESSAGE  = 10;
	      int ACCESS_CHALLENGE    = 11;
	      int STATUS_SERVER       = 12;
	      int STATUS_CLIENT       = 13;
	      int DISCONNECT_REQUEST  = 40;	// RFC 2882
	      int DISCONNECT_ACK      = 41;
	      int DISCONNECT_NAK      = 42;
	      int COA_REQUEST         = 43;
	      int COA_ACK             = 44;
	      int COA_NAK             = 45;
	      int STATUS_REQUEST      = 46;
	      int STATUS_ACCEPT       = 47;
	      int STATUS_REJECT       = 48;
	      int RESERVED            = 255;
	
	
	
	      String ACCESS_REQUEST_NAME="Access-Request";
	      String ACCESS_ACCEPT_NAME="Access-Accept";
	      String ACCESS_REJECT_NAME="Access-Reject";
	      String ACCOUNTING_REQUEST_NAME="Accounting-Request";
	      String ACCOUNTING_RESPONSE_NAME="Accounting-Response";
	      String ACCOUNTING_STATUS_NAME="Accounting-Status";
	      String PASSWORD_REQUEST_NAME="Password-Request";
	      String PASSWORD_ACCEPT_NAME="Password-Accept";
	      String PASSWORD_REJECT_NAME="Password-Reject";
	      String ACCOUNTING_MESSAGE_NAME="Accounting-Message";
	      String ACCESS_CHALLENGE_NAME="Access-Challenge";
	      String STATUS_SERVER_NAME="Status-Server";
	      String STATUS_CLIENT_NAME="Status-Client";
	      
	      // RFC 2882
	      String DISCONNECT_REQUEST_NAME="Disconnect-Request";	
	      String DISCONNECT_ACK_NAME="Disconnect-ACK";
	      String DISCONNECT_NAK_NAME="Disconnect-NAK";
	      String COA_REQUEST_NAME="CoA-Request";
	      String COA_ACK_NAME="CoA-ACK";
	      String COA_NAK_NAME="CoA-NAK";
	      String STATUS_REQUEST_NAME="Status-Request";
	      String STATUS_ACCEPT_NAME="Status-Accept";
	      String STATUS_REJECT_NAME="Status-Reject";
	      String RESERVED_NAME="Reserved";
}
