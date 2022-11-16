/*
 * Constants.java
 *
 * Created on July 11, 2005
 */
package com.baypackets.ase.sbb.util;


/**
 * Provides an enumeration of public static constants.
 * 
 * @author Nishi
 */
public interface Constants {


	//public static final String ORIG_PRACK_FROM_PARTY_A ="PRACK_FROM_A";	
	public static final String SBB_LISTENER_CLASS = ".listenerClass";

	public static final String SPACE  =" ";	

	
	public static final String ATTRIBUTE_RSEQ_1XX_REL  ="RSEQ_1XX_REL";	
	public static final String ATTRIBUTE_CSEQ_1XX_REL  ="CSEQ_1XX_REL";	


	public static final int RESPONSE_1XX  = 1;
	public static final int RESPONSE_2XX  = 2;	
	public static final int RESPONSE_3XX  = 3;
	public static final int RESPONSE_4XX  = 4;	
	public static final int RESPONSE_5XX  = 5;
	
	// Constants for SIP Headers & METHOD
	public static final String METHOD_INVITE ="INVITE";	
	public static final String METHOD_ACK ="ACK";	
	public static final String METHOD_PRACK ="PRACK";	
	public static final String METHOD_CANCEL ="CANCEL";
	public static final String METHOD_BYE ="BYE";
	public static final String METHOD_INFO ="INFO";
	public static final String METHOD_UPDATE="UPDATE";
	public static final String METHOD_NOTIFY="NOTIFY";


	public static final String HDR_CSEQ ="CSeq";	
	public static final String HDR_RSEQ ="RSeq";	
	public static final String HDR_RACK ="RAck";
	public static final String HDR_REQUIRE = "Require";
	public static final String HDR_SUPPORTED = "Supported";
	public static final String VALUE_100REL = "100rel";
	public static final String HDR_REASON = "Reason";
	public static final String HDR_SESSION_EXPIRES = "Session-Expires";
	public static final String HDR_TIMER_SUPPORTED = "Supported";
	public static final String HDR_MIN_SE = "Min-SE";
	public static final String HDR_TO ="To";
	public static final String HDR_FROM ="From";
	public static final String HDR_CALL_ID ="Call-ID";
	public static final String HDR_ROUTE ="Route";
	public static final String HDR_VIA ="Via";
	public static final String HDR_RECORD_ROUTE ="Record-Route";
	public static final String HDR_CONTACT ="Contact";
	public static final String HDR_CONTENT_TYPE="Content-Type";
	public static final String HDR_CONTENT_LENGTH="Content-Length";
	public static final String HDR_ALLOW="Allow";
	public static final String HDR_CONTENT_DISPOSITION="Content-Disposition";
	
	public static final String DEFAULT_VALUE_ISUP_CONTENT_DISPOSITION = "signal;handling=required";
	public static final String DEFAULT_VALUE_SDP_CONTENT_DISPOSITION = "session; handling=required";
	
	// Constants for piggybacking original request
	public static final String PIGGY_BACK_ORIG_REQ = "ORIG_REQ";

	// Constants for dialog sate
	public static final String ATTRIBUTE_SESSION_STATE = "SESSION_STATE";
	public static final String ATTRIBUTE_DIALOG_STATE = "DIALOG_STATE";
	public static final String ATTRIBUTE_SUBSCRIPTION_STATE_SBB = "ATTRIBUTE_SUBSCRIPTION_STATE_SBB";
	public static final int STATE_INITIAL=0;
	public static final int STATE_UNDEFINED=0;
	public static final int STATE_EARLY=1;
	public static final int STATE_CONFIRMED=2;
	public static final int STATE_TERMINATED=3;


	// Constants for SDP 
	public static final String  SDP_ATTR ="a";
	public static final String  INACTIVE  ="inactive";
	public static final String  INACTIVE_IP  ="0.0.0.0";

	// Constants for RTP tunneling
	public static final int RTP_TUNNELING_ENABLED = 1;
	public static final int RTP_TUNNELING_DISABLED = 0;

	// Default 4xx response
	public static final int RESP_DEFAULT_4XX = 480;
	
	//service not available response
	public static final int RESP_IVR_NOT_AVAILABLE = 503;

	// Constants for Session Activaiton Listener
	public static final String ATTR_SESSION_ACT_LISTENER = "SessionActivationListener".intern();

	//Attribute name used to store the Media Server result Object into the req/resp.
        public static final String MS_RESULT = "RESULT".intern();

	public static final String ATTRIBUTE_INIT_REQUEST = "INIT_REQUEST";
	//Atrribute name used to store the initial request to determine the content type for Sending either SIP or SIP-T in case of disconnect
	public static final String ATTRIBUTE_INIT_REQUEST_FOR_CONTENT = "INIT_REQUEST_FOR_CONTENT";
	
	//Attribute name used to store iterator of unjoined participants
	public static final String ATTRIBUTE_UNJOINED_PARTICIPANTS = "UNJOINED_PARTICIPANTS";
	//Attribute name used to store iterator of joined participants
	public static final String ATTRIBUTE_JOINED_PARTICIPANTS = "JOINED_PARTICIPANTS";
	
	//Attribute name used to store iterator of unjoined streams
	public static final String ATTRIBUTE_UNJOINED_STREAMS = "UNJOINED_STREAMS";
	//Attribute name used to store iterator of modified streams
	public static final String ATTRIBUTE_MODIFIED_STREAMS = "MODIFIED_STREAMS";

	public static final String PRACK_UPDATE_FLOW = "PRACK_UPDATE_FLOW";
	public static final String RESPONSE_1XX_FROM_B = "RESPONSE_1XX_FROM_B";
	public static final String REQUEST_FROM_A = "REQUEST_FROM_A" ;
	// This is an attribute used in SCE, to tell whether no INVITE in the entire application
	// has to ever be sent without a SDP.
	public static final String NO_INVITE_WITHOUT_SDP = "NO_INVITE_WITHOUT_SDP";
	
	//attributes added for GroupedMSSBB
	public static final String ISUP_CONTENT_TYPE = "application/isup";
	public static final String SDP_CONTENT_TYPE = "application/sdp";
	public static final String SDP_MULTIPART = "multipart/";
	public static final String SDP_MULTIPART_MIXED = "multipart/mixed";
	
	//default release isup message
	public static final byte[] REL_ISUP_CONTENT = {(byte)0x0c, (byte)0x02, (byte)0x00, (byte)0x02, (byte)0x83, (byte)0xa9};
	
	//ISUP reason header value for cause code 41; change this if cause code in REL_ISUP_CONTENT is changed
	public static final String REASON_HDR_ISUP_VAL = "Q.850;cause=41";
	
	
	// Added for HPMS architecture support for Dialogic
	public static final String MS_NAME = "ms.name";
	
	// Added for Media Server flow support for SBTM-NGIN flows for SIP-T
		// the timeout required between 183 and 200 to party A. If it is null, no timeout is
		// done. Else, a timeout in milliseconds, equal to it's value is done.
		public static final String TIMEOUT_REQUIRED = "TIMEOUT_REQUIRED" ;
		
		// In a certain flows of the NGIN-SBB, an UPDATE is required to be sent to the
		// calling party before the 183 and 200 are sent. This attribute is used to
		// determine whether the update is required or not.
		public static final String UPDATE_NEEDED = "UPDATE_NEEDED" ;	
		
		//MIN-SE default value from ase.properties
		public static final String MIN_SE_TIMEOUT = "sip.default.min.se.timeout" ;
		public static final String MS_ENABLE_REFRESHER_UAC = "ms.enable.refresher.uac" ;
		
		//constants for session-expires timer
		public static final String SESSION_REFRESH_TIMER = "SESSION_REFRESH_TIMER";
		public static final String TIMER_FOR_MS ="SESSION_EXPIRY_FOR_MS";
		public static final String TIMER_FOR_A_PARTY ="SESSION_EXPIRY_FOR_A_PARTY";
		public static final String SESSION_EXPIRY_TIMER_FOR_MS="SESSION_EXPIRY_TIMER_FOR_MS";
		public static final String SESSION_EXPIRY_TIMER_FOR_A_PARTY ="SESSION_EXPIRY_TIMER_FOR_A_PARTY";
		public static final String SESSION_EXPIRED_OF = "SESSION_EXPIRED_OF";
		public static final String SESSION_EXPIRES_DEFAULT_ISUP_RELEASE_CAUSE ="session.expires.default.isup.release.cause";
		
		public static final String ORIG_REQUEST = "origReq";
		public static final String CPG_PLAY_SEND = "true";
		
		public static final String CONTROLLER_SDP_CONTENT = "CONTROLLER_SDP_CONTENT";
		public static final String CONTROLLER_SDP_CONTENT_TYPE = "CONTROLLER_SDP_CONTENT_TYPE";
		
		public static final String  IS_OTHER_LEG_TERMINATED= "isOtherLegTerminated";
		public static final String  IS_ACK_PENDING= "isAckPending";
		
		//UAT-745
		public static final String REQUEST_PENDING_TIMER = "request.pending.timer";
		public static final String TIMER_FOR_REQ_PEND = "TIMER_FOR_REQ_PEND";
		public static final String PENDING_REQUEST = "PENDING_REQUEST";

		public static final String NOTIFY_SESSION_EXPIRY_A_TOAPP ="notify.session.expiry.orig.toapp";
		public static final String  SBB_SERVLET_NAME = "SBBServlet";
        public static final String SEND_491 = "send_491";		
    	public static final String STOP_MS_OPER_AFTER_TIMER="STOP_MS_OPER_AFTER_IDT_TIMER";
		public static final String ATTRIBUTE_OPERATION_STOPPED = "ATTRIBUTE_OPERATION_STOPPED";
		public static final String MSML_TYPE="Msml".intern();
		
		public static final String DEFAULT = "default";
		
		public static final String IS_REL_RESP_SENT_ALREADY_SENT = "IS_REL_RESP_SENT_ALREADY_SENT";
}

