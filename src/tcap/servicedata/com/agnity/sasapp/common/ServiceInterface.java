package com.agnity.sasapp.common;

import java.util.Map;

public interface ServiceInterface {

	int	ORIG_CONNECTION_TYPE		= 0;
	int	IVR_CONNECTION_TYPE			= 1;
	int	TERM_CONNECTION_TYPE		= 2;
	int	TERM_IVR_CONNECTION_TYPE	= 3;
	int	DEFAULT_OPERATION_TYPE		= 0;
	int	LS_INVOKED_OPERATION_TYPE	= 1;
	int	LS_EXECUTED_OPERATION_TYPE	= 2;
	int	ANN_COPY_OPERATION_TYPE		= 3;
	int	RESYNC_CALL_OPERATION_TYPE	= 4;
	int HTTP_GET_OPERATION_TYPE		= 5;


	String	FLOATING_IP_STR			= "FLOATING_IP";
	String	PEER_CLUSTER_FIP_STR	= "PEER_CLUSTER_FIP";
	String	ANN_BASE_PATH_STR		= "ANN_BASE_PATH";
	String	HTTP_LISTENER_PORT_STR	= "HTTP_LISTENER_PORT";
	String	PEER_NODE_IP_STR		= "PEER_NODE_IP";
	
	// Parameter list for HTTP GET request
	String	VXMLPARAMETER			= "vxmlURL";
	String 	DESTINATIONPARAMETER	= "destNum";
	String	TFPARAMETER				= "tfNum";
	String 	NUMBERTYPE				= "numType";
	String 	SERVICE_NODE_IP			= "serviceNodeIP";

	// Parameter list for HTTP POST request
	String	AAI							= "aai";
	String	DIGITS_PRESSED				= "service";
	String	DIGITS_SPOKEN				= "voice";


	public static final String	TOLLFREE_TYPE			= "tfType";
	public static final String	DESTINATION_TYPE		= "destType";

	/*
	 * This method will be called by Servlet on initial event to hand-over the
	 * call to service (kept for older PH compatibility)
	 */

	Action[] startCall(CallData callData);

	/*
	 * This method will be called by Servlet on initial event to hand-over the
	 * call to service
	*/
	Action[] startCall(CallData callData,int connectiontype);

	/*
	 * This method will be called by Servlet on redirected call event to
	 * hand-over the
	 * call to service
	 */
	Action[] resumeCall(CallData callData);

	/*
	 * Callback to tell about successful announcement played
	 */
	Action[] announcementPlayed(CallData callData, int connectionType);

	/*
	 * Callback to tell about failure in playing announcement
	 */
	Action[] playAnnouncementFailed(CallData callData, int connectionType);

	/*
	 * Callback to tell about successful digits collection
	 */
	Action[] digitsCollected(CallData callData, String digits,
			int connectionType);

	/*
	 * Callback to tell about failure in play and collect
	 */
	Action[] playCollectFailed(CallData callData, int connectionType);

	/*
	 * This method will be called in case connection attempt failed
	 */
	Action[] connectionFailed(CallData callData, int connectionType);

	/*
	 * This method will be called in case connection attempt successful
	 */
	Action[] connectionConnected(CallData callData, int connectionType);

	/*
	 * This method will be called in case connection disconnected
	 */
	Action[] connectionDisconnected(CallData callData, int connectionType);
	
	/*
	 * This method will be called in 200 OK of BYE is received on TERM connection 
	 */
	Action[] connectionDisconnectedSuccess(CallData callData, int connectionType);

	/*
	 * This method will be called for generic error handling to inform service
	 * about requested operation failed
	 */
	Action[] operationFailed(CallData callData, int resultType);

	/*
	 * This method will be called when the call is cleared by the switch
	 */
	Action[] callDropped(CallData callData);
	
	/*
	 * This method will be called when FT happens
	 */
	Action[] notifyFailover(CallData callData, boolean isCallToBeRecovered);

	/*
	 * This method will be called when the announcement is successfully recorded
	 * by IVR
	 */
	Action[] announcementRecorded(CallData callData, String recordingLength,
			int connectionType);

	/*
	 * This method will be called when the there is error in announcement
	 * recording.
	 */
	Action[] recordingFailed(CallData callData, int connectionType);

	/*
	 * This method will be called for Telnet/ssh RA handling to inform service
	 * about requested operation success
	 */
	Action[] operationSuccessful(CallData callData, int resultType);
	
	/*
	 * This method will be called for HTTP POST handling to inform service
	 * about requested operation 
	 */
	String infoReceived(String resetCallCounterXML);

	/*
	 * This method will be called for HTTP GET handling to ask service
	 * to generate report
	 */
	String generateReport(Map<String, String> paramMap);

	/*
	 * This method will be called for HTTP GET handling to ask service
	 * to return the vxml file from the location mentioned in the URL.
	 */
	String retrieveVXML(CallData callData);
	
	/*
	 * This method shall be called by the Protocol Handler when the Service CDR
	 * has to be written. Service shall return a string containing a properly
	 * formatted CDR.
	 */
	String getServiceCdr(CallData callData);

	/*
	 * This method shall be called by the Protocol Handler when the Service CDR 
	 * (for call queuing feature)  
	 * has to be initially written. Service shall return a string containing a properly
	 * formatted CDR.
	 */
	String getServiceInitialCdr(CallData callData);
	
	/**
	 * Win authenticate operATION ON rECIEVING orreq
	 * fROM msc
	 * @param callData
	 * @return
	 */
	
	
	Action[] authenticate(CallData callData,int connectionType);

	/**
	 * Win authenticate operATION ON rECIEVING analyze 
	 * FROM msc
	 * @param callData
	 * @return
	 */
	Action[] analyze(CallData callData,int connectionType);
	
	/**
	 * Win TIMER EVENT EXPIRATION FOR LOW BALANCE 
	 * FROM msc
	 * @param callData
	 * @return
	 */

	Action[] timerEvent(CallData callData);
	
	
	/**
	 * Win LOW BALANCE NOTIFICATION CONFIRMATION
	 * FROM msc
	 * @param callData
	 * @return
	 */
	Action[] confirmation(CallData callData);

	 // Added by Supriya for SMS flow
	/**
	 * PH informs service to analyze PPC SMS for MS Origination
	 * FROM mc
	 * @param callData
	 * @return
	 */
	Action[] smsOriginated(CallData callData, int termConnectionType);

	/**
	 * PH informs service to analyze PPC SMS for MS Termination
	 * FROM mc
	 * @param callData
	 * @return
	 */
	Action[] smsTerminated(CallData callData, int termConnectionType);


	/**
	 * PH informs service about sms failure 
	 * FROM mc
	 * @param callData
	 * @return
	 */
	Action[] smsFailed(CallData callData, int connectionType);
}
