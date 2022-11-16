/*
 * Created on Oct 5, 2004
 *
 * Aug 25, 2005 - Added counter for loopback messages (application composition)
 */
package com.baypackets.ase.measurement;

import com.baypackets.ase.spi.measurement.MeasurementManager;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipConstants;

import org.apache.log4j.Logger;

/**
 * @author Ravi
 */
public class AseMeasurementUtil {

	private static Logger logger = Logger.getLogger(AseMeasurementUtil.class);


	private static final MeasurementManager measMgr = AseMeasurementManager.instance().getDefaultMeasurementManager();
	private static final MeasurementManager NSEPMeasMgr = AseMeasurementManager.instance().getNSEPMeasurementManager();

	public static final String NAME_ACTIVE_APP_SESSIONS = "Number of Active Application Sessions";
	public static  AseCounter counterActiveAppSessions = (AseCounter)measMgr.getMeasurementCounter(NAME_ACTIVE_APP_SESSIONS);
	
	public static final String NAME_TOTAL_APP_SESSIONS = "Total Number of Application Sessions";
	public static  AseCounter counterTotalAppSessions = (AseCounter)measMgr.getMeasurementCounter(NAME_TOTAL_APP_SESSIONS);
	
	public static final String NAME_TOTAL_TIMEOUT_APP_SESSIONS = "Total Number of Timedout Application Sessions";
	public static  AseCounter counterTotalTimeoutAppSessions = (AseCounter)measMgr.getMeasurementCounter(NAME_TOTAL_TIMEOUT_APP_SESSIONS);
	
	public static final String NAME_ACTIVE_SIP_SESSIONS = "Number of Active SIP Sessions";
	public static  AseCounter counterActiveSIPSessions = (AseCounter)measMgr.getMeasurementCounter(NAME_ACTIVE_SIP_SESSIONS);
	
	public static final String NAME_TOTAL_SIP_SESSIONS = "Total Number of SIP Sessions";
	public static  AseCounter counterTotalSIPSessions = (AseCounter)measMgr.getMeasurementCounter(NAME_TOTAL_SIP_SESSIONS);
	
	public static final String NAME_REJECTED_REQUESTS = "Number of Rejected Requests";
	public static  AseCounter counterRejectedRequests = (AseCounter)measMgr.getMeasurementCounter(NAME_REJECTED_REQUESTS);
	
	public static final String NAME_APP_NOT_FOUND = "Requests failed to trigger an application";
	public static  AseCounter counterAppNotFound = (AseCounter)measMgr.getMeasurementCounter(NAME_APP_NOT_FOUND);
	
	public static final String NAME_INVITE_IN = "Total Number of INVITEs IN";
	public static  AseCounter counterInvitesIn = (AseCounter)measMgr.getMeasurementCounter(NAME_INVITE_IN);
	
	public static final String NAME_INVITE_OUT = "Total Number of INVITEs OUT";
	public static  AseCounter counterInvitesOut = (AseCounter)measMgr.getMeasurementCounter(NAME_INVITE_OUT);
	
	public static final String NAME_ACK_IN = "Total Number of ACKs IN";
	public static AseCounter counterAcksIn = (AseCounter)measMgr.getMeasurementCounter(NAME_ACK_IN);
	
	public static final String NAME_ACK_OUT = "Total Number of ACKs OUT";
	public static AseCounter counterAcksOut = (AseCounter)measMgr.getMeasurementCounter(NAME_ACK_OUT);
	
	public static final String NAME_BYE_IN = "Total Number of BYEs IN";
	public static AseCounter counterByesIn = (AseCounter)measMgr.getMeasurementCounter(NAME_BYE_IN);
	
	public static final String NAME_BYE_OUT = "Total Number of BYEs OUT";
	public static AseCounter counterByesOut = (AseCounter)measMgr.getMeasurementCounter(NAME_BYE_OUT);
	
	public static final String NAME_CANCEL_IN = "Total Number of CANCELs IN";
	public static AseCounter counterCancelsIn = (AseCounter)measMgr.getMeasurementCounter(NAME_CANCEL_IN);
	
	public static final String NAME_CANCEL_OUT = "Total Number of CANCELs OUT";
	public static AseCounter counterCancelsOut = (AseCounter)measMgr.getMeasurementCounter(NAME_CANCEL_OUT);
	
	public static final String NAME_OPTION_IN = "Total Number of OPTIONs IN";
	public static AseCounter counterOptionsIn = (AseCounter)measMgr.getMeasurementCounter(NAME_OPTION_IN);
	
	public static final String NAME_OPTION_OUT = "Total Number of OPTIONs OUT";
	public static AseCounter counterOptionsOut = (AseCounter)measMgr.getMeasurementCounter(NAME_OPTION_OUT);
	
	public static final String NAME_REGISTER_IN = "Total Number of REGISTERs IN";
	public static AseCounter counterRegistersIn = (AseCounter)measMgr.getMeasurementCounter(NAME_REGISTER_IN);
	
	public static final String NAME_REGISTER_OUT = "Total Number of REGISTERs OUT";
	public static AseCounter counterRegistersOut = (AseCounter)measMgr.getMeasurementCounter(NAME_REGISTER_OUT);
	
	public static final String NAME_INFO_IN = "Total Number of INFOs IN";
	public static AseCounter counterInfosIn = (AseCounter)measMgr.getMeasurementCounter(NAME_INFO_IN);
	
	public static final String NAME_INFO_OUT = "Total Number of INFOs OUT";
	public static AseCounter counterInfosOut = (AseCounter)measMgr.getMeasurementCounter(NAME_INFO_OUT);
	
	public static final String NAME_PRACK_IN = "Total Number of PRACKs IN";
	public static AseCounter counterPracksIn = (AseCounter)measMgr.getMeasurementCounter(NAME_PRACK_IN);
	
	public static final String NAME_PRACK_OUT = "Total Number of PRACKs OUT";
	public static AseCounter counterPracksOut = (AseCounter)measMgr.getMeasurementCounter(NAME_PRACK_OUT);

	public static final String NAME_REFER_IN = "Total Number of REFERs IN";
	public static AseCounter counterRefersIn = (AseCounter)measMgr.getMeasurementCounter(NAME_REFER_IN);
	
	public static final String NAME_REFER_OUT = "Total Number of REFERs OUT";
	public static AseCounter counterRefersOut = (AseCounter)measMgr.getMeasurementCounter(NAME_REFER_OUT);
	
	public static final String NAME_NOTIFY_IN = "Total Number of NOTIFYs IN";
	public static AseCounter counterNotifysIn = (AseCounter)measMgr.getMeasurementCounter(NAME_NOTIFY_IN);
	
	public static final String NAME_NOTIFY_OUT = "Total Number of NOTIFYs OUT";
	public static AseCounter counterNotifysOut = (AseCounter)measMgr.getMeasurementCounter(NAME_NOTIFY_OUT);
	
	public static final String NAME_SUBSCRIBE_IN = "Total Number of SUBSCRIBEs IN";
	public static AseCounter counterSubscribesIn = (AseCounter)measMgr.getMeasurementCounter(NAME_SUBSCRIBE_IN);
	
	public static final String NAME_SUBSCRIBE_OUT = "Total Number of SUBSCRIBEs OUT";
	public static AseCounter counterSubscribesOut = (AseCounter)measMgr.getMeasurementCounter(NAME_SUBSCRIBE_OUT);
	
	public static final String NAME_UPDATE_IN = "Total Number of UPDATEs IN";
	public static AseCounter counterUpdatesIn = (AseCounter)measMgr.getMeasurementCounter(NAME_UPDATE_IN);
	
	public static final String NAME_UPDATE_OUT = "Total Number of UPDATEs OUT";
	public static AseCounter counterUpdatesOut = (AseCounter)measMgr.getMeasurementCounter(NAME_UPDATE_OUT);
	
	public static final String NAME_MESSAGE_IN = "Total Number of MESSAGEs IN";
	public static AseCounter counterMessagesIn = (AseCounter)measMgr.getMeasurementCounter(NAME_MESSAGE_IN);
	
	public static final String NAME_MESSAGE_OUT = "Total Number of MESSAGEs OUT";
	public static AseCounter counterMessagesOut = (AseCounter)measMgr.getMeasurementCounter(NAME_MESSAGE_OUT);
	
	public static final String NAME_PUBLISH_IN = "Total Number of PUBLISHs IN";
	public static AseCounter counterPublishsIn = (AseCounter)measMgr.getMeasurementCounter(NAME_PUBLISH_IN);
	
	public static final String NAME_PUBLISH_OUT = "Total Number of PUBLISHs OUT";
	public static AseCounter counterPublishsOut = (AseCounter)measMgr.getMeasurementCounter(NAME_PUBLISH_OUT);
	
	public static final String NAME_ACTIVE_TCAP_CALLS = "Number of Active Tcap Calls";
	public static AseCounter counterTcapActiveCalls = (AseCounter)measMgr.getMeasurementCounter(NAME_ACTIVE_TCAP_CALLS);
	
	public static final String NAME_TCAP_NOTIFY_RECEIVED = "Number of Tcap Notify Received";
	public static AseCounter counterTcapNotifyReceived = (AseCounter)measMgr.getMeasurementCounter(NAME_TCAP_NOTIFY_RECEIVED);
		
	public static final String NAME_REQUEST_IN = "Total Number of Requests IN";
	public static AseCounter counterRequestsIn = (AseCounter)measMgr.getMeasurementCounter(NAME_REQUEST_IN);
	
	public static final String NAME_REQUEST_OUT = "Total Number of Requests OUT";
	public static AseCounter counterRequestsOut = (AseCounter)measMgr.getMeasurementCounter(NAME_REQUEST_OUT);
	
	public static final String NAME_1XX_IN = "Total Number of 1xx Responses IN";
	public static AseCounter counter1xxIn = (AseCounter)measMgr.getMeasurementCounter(NAME_1XX_IN);
	
	public static final String NAME_1XX_OUT = "Total Number of 1xx Responses OUT";
	public static AseCounter counter1xxOut = (AseCounter)measMgr.getMeasurementCounter(NAME_1XX_OUT);
	
	public static final String NAME_2XX_IN =  "Total Number of 2xx Responses IN";
	public static AseCounter counter2xxIn = (AseCounter)measMgr.getMeasurementCounter(NAME_2XX_IN);
	
	public static final String NAME_2XX_OUT = "Total Number of 2xx Responses OUT";
	public static AseCounter counter2xxOut = (AseCounter)measMgr.getMeasurementCounter(NAME_2XX_OUT);
	
	public static final String NAME_3XX_IN = "Total Number of 3xx Responses IN";
	public static AseCounter counter3xxIn = (AseCounter)measMgr.getMeasurementCounter(NAME_3XX_IN);
	
	public static final String NAME_3XX_OUT = "Total Number of 3xx Responses OUT";
	public static AseCounter counter3xxOut = (AseCounter)measMgr.getMeasurementCounter(NAME_3XX_OUT);
	
	public static final String NAME_4XX_IN = "Total Number of 4xx Responses IN";
	public static AseCounter counter4xxIn = (AseCounter)measMgr.getMeasurementCounter(NAME_4XX_IN);
	
	public static final String NAME_4XX_OUT = "Total Number of 4xx Responses OUT";
	public static AseCounter counter4xxOut = (AseCounter)measMgr.getMeasurementCounter(NAME_4XX_OUT);
	
	public static final String NAME_5XX_IN = "Total Number of 5xx Responses IN";
	public static AseCounter counter5xxIn = (AseCounter)measMgr.getMeasurementCounter(NAME_5XX_IN);
	
	public static final String NAME_5XX_OUT = "Total Number of 5xx Responses OUT";
	public static AseCounter counter5xxOut = (AseCounter)measMgr.getMeasurementCounter(NAME_5XX_OUT);
	
	public static final String NAME_6XX_IN = "Total Number of 6xx Responses IN";
	public static AseCounter counter6xxIn = (AseCounter)measMgr.getMeasurementCounter(NAME_6XX_IN);
	
	public static final String NAME_6XX_OUT = "Total Number of 6xx Responses OUT";
	public static AseCounter counter6xxOut = (AseCounter)measMgr.getMeasurementCounter(NAME_6XX_OUT);
	
	public static final String NAME_RESPONSE_IN = "Total Number of Responses IN";
	public static AseCounter counterResponsesIn = (AseCounter)measMgr.getMeasurementCounter(NAME_RESPONSE_IN);
	
	public static final String NAME_RESPONSE_OUT = "Total Number of Responses OUT";
	public static AseCounter counterResponsesOut = (AseCounter)measMgr.getMeasurementCounter(NAME_RESPONSE_OUT);
	
	public static final String NAME_REPLICATED = "Total Number of calls replicated";
	public static AseCounter counterReplicated = (AseCounter)measMgr.getMeasurementCounter(NAME_REPLICATED);
	
	public static final String NAME_BEING_REPLICATED = "Number of calls currently replicated";
	public static AseCounter counterBeingReplicated = (AseCounter)measMgr.getMeasurementCounter(NAME_BEING_REPLICATED);
	
	public static final String NAME_CLEANEDUP = "Total Number of calls cleaned up";
	public static AseCounter counterCleanedUp = (AseCounter)measMgr.getMeasurementCounter(NAME_CLEANEDUP);
	
	public static final String NAME_ACTIVATED = "Total Number of calls activated";
	public static AseCounter counterActivated = (AseCounter)measMgr.getMeasurementCounter(NAME_ACTIVATED);

	public static final String NAME_ACKTIMEDOUT = "Total Number of ACK Timedout";
    public static AseCounter counterAckTimedout= (AseCounter)measMgr.getMeasurementCounter(NAME_ACKTIMEDOUT);

	public static final String NAME_PRACKTIMEDOUT = "Total Number of PRACK Timedout";
    public static AseCounter counterPrackTimedout= (AseCounter)measMgr.getMeasurementCounter(NAME_PRACKTIMEDOUT);
	
	public static final String NAME_SERIALIZATION_FAIL = "Total Number of Serialization Failures";
	public static AseCounter counterSerializationFail = (AseCounter)measMgr.getMeasurementCounter(NAME_SERIALIZATION_FAIL);
	
	public static final String NAME_DESERIALIZATION_FAIL = "Total Number of Deserialization Failures";
	public static AseCounter counterDeserializationFail = (AseCounter)measMgr.getMeasurementCounter(NAME_DESERIALIZATION_FAIL);
	
	public static final String NAME_APPSESSION_THRESHOLD = "30.10.101";
	public static AseCounter thresholdAppSession = (AseCounter)measMgr.getThresholdCounter(NAME_APPSESSION_THRESHOLD);
	
	public static final String NAME_SIPSESSION_THRESHOLD = "30.10.102";
	public static AseCounter thresholdSIPSession = (AseCounter)measMgr.getThresholdCounter(NAME_SIPSESSION_THRESHOLD);
	
	public static final String NAME_SERIALIZATION_FAIL_THRESHOLD = "30.10.103";
	public static AseCounter thresholdSerializationFail = (AseCounter)measMgr.getThresholdCounter(NAME_SERIALIZATION_FAIL_THRESHOLD);

	public static final String NAME_DESERIALIZATION_FAIL_THRESHOLD = "30.10.104";
	public static AseCounter thresholdDeserializationFail = (AseCounter)measMgr.getThresholdCounter(NAME_DESERIALIZATION_FAIL_THRESHOLD);
	
	// Http Related Measurement Counters
	public static final String NAME_ACTIVE_HTTP_SESSIONS = "Number of Active HTTP Sessions";
	public static AseCounter counterActiveHttpSessions = (AseCounter)measMgr.getMeasurementCounter(NAME_ACTIVE_HTTP_SESSIONS);
	
	public static final String NAME_TOTAL_HTTP_SESSIONS = "Total Number of HTTP Sessions";
	public static AseCounter counterTotalHttpSessions = (AseCounter)measMgr.getMeasurementCounter(NAME_TOTAL_HTTP_SESSIONS);
	
	public static final String NAME_HTTP_REQUEST_IN = "Total Number of HTTP Requests IN";
	public static AseCounter counterHttpRequestsIn = (AseCounter)measMgr.getMeasurementCounter(NAME_HTTP_REQUEST_IN);
	
	public static final String NAME_HTTP_GET_REQUEST_IN = "Total Number of HTTP GET Requests IN";
	public static AseCounter counterHttpGetRequestsIn = (AseCounter)measMgr.getMeasurementCounter(NAME_HTTP_GET_REQUEST_IN);
	
	public static final String NAME_HTTP_POST_REQUEST_IN = "Total Number of HTTP POST Requests IN";
	public static AseCounter counterHttpPostRequestsIn = (AseCounter)measMgr.getMeasurementCounter(NAME_HTTP_POST_REQUEST_IN);

	public static final String NAME_HTTP_SUCCESS_RESPONSE_OUT = "Total Number of HTTP Success Response OUT";
	public static AseCounter counterHttpSuccessResponsesOut = (AseCounter)measMgr.getMeasurementCounter(NAME_HTTP_SUCCESS_RESPONSE_OUT);
	
	public static final String NAME_HTTP_ERROR_RESPONSE_OUT = "Total Number of HTTP Error Response OUT";
	public static AseCounter counterHttpErrorResponsesOut = (AseCounter)measMgr.getMeasurementCounter(NAME_HTTP_ERROR_RESPONSE_OUT);
	
	//Peg Counts
	
	public static final String NAME_NEW_CALL_COUNT = "Number of New Calls per second";
	public static AseCounter counterNewCalls = (AseCounter)measMgr.getMeasurementCounter(NAME_NEW_CALL_COUNT);
	
	public static final String NAME_CALLS_CURRENTLY_IN_PROGRESS = "Number of Calls currently in Progress";
	public static AseCounter counterCallsCurrentlyInProgress = (AseCounter)measMgr.getMeasurementCounter(NAME_CALLS_CURRENTLY_IN_PROGRESS);
	
	public static final String NAME_TOTAL_CALLS_IN_PROGRESS = "Total Number of Calls in Progress";
	public static AseCounter counterTotalCallsInProgress = (AseCounter)measMgr.getMeasurementCounter(NAME_TOTAL_CALLS_IN_PROGRESS);
	
	public static final String NAME_AVERAGE_CALL_HOLD_TIME = "Average Call Hold Time (in seconds)";
	public static AseCounter counterAverageCallHoldTime = (AseCounter)measMgr.getMeasurementCounter(NAME_AVERAGE_CALL_HOLD_TIME);
	
	public static final String NAME_NETWORK_TRANSACTIONS_PER_SECOND = "Average Network Transactions Per Second";
	public static AseCounter counterNetworkTransactionsPerSec= (AseCounter)measMgr.getMeasurementCounter(NAME_NETWORK_TRANSACTIONS_PER_SECOND);
	
	public static final String NAME_AGGREGATED_TRANSACTIONS_PER_SECOND = "Average Aggregated Transactions Per Second";
	public static AseCounter counterAggregatedTransactionsPerSec = (AseCounter)measMgr.getMeasurementCounter(NAME_AGGREGATED_TRANSACTIONS_PER_SECOND);
	
	/**
	 * TCAP related counters
	 */
	public static final String NAME_TC_BEGINS_IN = "Number of TC-Begin IN";
	public static AseCounter counterTotalBeginsIn = (AseCounter) measMgr.getMeasurementCounter(NAME_TC_BEGINS_IN);
	
	public static final String NAME_TC_BEGINS_OUT = "Number of TC-Begin OUT";
	public static AseCounter counterTotalBeginsOut = (AseCounter) measMgr.getMeasurementCounter(NAME_TC_BEGINS_OUT);
	
	public static final String NAME_TC_UNIDIRECTIONAL_IN = "Number of TC-UniDirectional IN";
	public static AseCounter counterTotalUniDirIn = (AseCounter) measMgr.getMeasurementCounter(NAME_TC_UNIDIRECTIONAL_IN);
	
	public static final String NAME_TC_UNIDIRECTIONAL_OUT = "Number of TC-UniDirectional OUT";
	public static AseCounter counterTotalUniDirOut = (AseCounter) measMgr.getMeasurementCounter(NAME_TC_UNIDIRECTIONAL_OUT);
	
	public static final String NAME_TC_NOTICE_IN = "Number of TC-Notice IN";
	public static AseCounter counterTotalNoticeIn = (AseCounter) measMgr.getMeasurementCounter(NAME_TC_NOTICE_IN);
	
	public static final String NAME_TC_NOTICE_OUT = "Number of TC-Notice OUT";
	public static AseCounter counterTotalNoticeOut = (AseCounter) measMgr.getMeasurementCounter(NAME_TC_NOTICE_OUT);
	
	public static final String NAME_TC_UABORT_IN = "Number of TC-U-Abort IN";
	public static AseCounter counterTotalUAbortIn = (AseCounter) measMgr.getMeasurementCounter(NAME_TC_UABORT_IN);
	
	public static final String NAME_TC_UABORT_OUT = "Number of TC-U-Abort OUT";
	public static AseCounter counterTotalUAbortOut = (AseCounter) measMgr.getMeasurementCounter(NAME_TC_UABORT_OUT);
	
	public static final String NAME_TC_CONTINUE_IN = "Number of TC-Continue IN";
	public static AseCounter counterTotalContinueIn = (AseCounter) measMgr.getMeasurementCounter(NAME_TC_CONTINUE_IN);
	
	public static final String NAME_TC_CONTINUE_OUT = "Number of TC-Continue OUT";
	public static AseCounter counterTotalContinueOut = (AseCounter) measMgr.getMeasurementCounter(NAME_TC_CONTINUE_OUT);
	
	public static final String NAME_TC_END_IN = "Number of TC-End IN";
	public static AseCounter counterTotalEndIn = (AseCounter) measMgr.getMeasurementCounter(NAME_TC_END_IN);
	
	public static final String NAME_TC_END_OUT = "Number of TC-End OUT";
	public static AseCounter counterTotalEndOut = (AseCounter) measMgr.getMeasurementCounter(NAME_TC_END_OUT);
	
	public static final String NAME_TC_PABORT_IN = "Number of TC-P-Abort IN";
	public static AseCounter counterTotalPAbortIn = (AseCounter) measMgr.getMeasurementCounter(NAME_TC_PABORT_IN);
	
	public static final String NAME_TC_INVOKES_IN = "Number of TC-Invokes IN";
	public static AseCounter counterTotalInvokesIn = (AseCounter) measMgr.getMeasurementCounter(NAME_TC_INVOKES_IN);
	
	public static final String NAME_TC_INVOKES_OUT = "Number of TC-Invokes OUT";
	public static AseCounter counterTotalInvokesOut = (AseCounter) measMgr.getMeasurementCounter(NAME_TC_INVOKES_OUT);
	
	public static final String NAME_TC_RET_RESULT_IN = "Number of TC-ReturnResult IN";
	public static AseCounter counterTotalRetResultIn = (AseCounter) measMgr.getMeasurementCounter(NAME_TC_RET_RESULT_IN);
	
	public static final String NAME_TC_RET_RESULT_OUT = "Number of TC-ReturnResult OUT";
	public static AseCounter counterTotalRetResultOut = (AseCounter) measMgr.getMeasurementCounter(NAME_TC_RET_RESULT_OUT);
	
	public static final String NAME_TC_ERROR_IN = "Number of TC-Errors IN";
	public static AseCounter counterTotalErrorIn = (AseCounter) measMgr.getMeasurementCounter(NAME_TC_ERROR_IN);
	
	public static final String NAME_TC_ERROR_OUT = "Number of TC-Errors OUT";
	public static AseCounter counterTotalErrorOut = (AseCounter) measMgr.getMeasurementCounter(NAME_TC_ERROR_OUT);
	
	public static final String NAME_TC_REJECT_IN = "Number of TC-Reject IN";
	public static AseCounter counterTotalRejectIn = (AseCounter) measMgr.getMeasurementCounter(NAME_TC_REJECT_IN);
	
	public static final String NAME_TC_REJECT_OUT = "Number of TC-Reject OUT";
	public static AseCounter counterTotalRejectOut = (AseCounter) measMgr.getMeasurementCounter(NAME_TC_REJECT_OUT);
			
			
	public static void initDefaultCounters() {
		
		logger.error("initDefaultCounters entering");

		counterActiveAppSessions = (AseCounter) measMgr
				.getMeasurementCounter(NAME_ACTIVE_APP_SESSIONS);
		
		logger.error("initDefaultCounters " + counterActiveAppSessions);

		counterTotalAppSessions = (AseCounter) measMgr
				.getMeasurementCounter(NAME_TOTAL_APP_SESSIONS);

		counterTotalTimeoutAppSessions = (AseCounter) measMgr
				.getMeasurementCounter(NAME_TOTAL_TIMEOUT_APP_SESSIONS);

		counterActiveSIPSessions = (AseCounter) measMgr
				.getMeasurementCounter(NAME_ACTIVE_SIP_SESSIONS);

		counterTotalSIPSessions = (AseCounter) measMgr
				.getMeasurementCounter(NAME_TOTAL_SIP_SESSIONS);

		counterRejectedRequests = (AseCounter) measMgr
				.getMeasurementCounter(NAME_REJECTED_REQUESTS);

		counterAppNotFound = (AseCounter) measMgr
				.getMeasurementCounter(NAME_APP_NOT_FOUND);

		counterInvitesIn = (AseCounter) measMgr
				.getMeasurementCounter(NAME_INVITE_IN);

		counterInvitesOut = (AseCounter) measMgr
				.getMeasurementCounter(NAME_INVITE_OUT);

		counterAcksIn = (AseCounter) measMgr.getMeasurementCounter(NAME_ACK_IN);

		counterAcksOut = (AseCounter) measMgr
				.getMeasurementCounter(NAME_ACK_OUT);

		counterByesIn = (AseCounter) measMgr.getMeasurementCounter(NAME_BYE_IN);

		counterByesOut = (AseCounter) measMgr
				.getMeasurementCounter(NAME_BYE_OUT);

		counterCancelsIn = (AseCounter) measMgr
				.getMeasurementCounter(NAME_CANCEL_IN);

		counterCancelsOut = (AseCounter) measMgr
				.getMeasurementCounter(NAME_CANCEL_OUT);

		counterOptionsIn = (AseCounter) measMgr
				.getMeasurementCounter(NAME_OPTION_IN);

		counterOptionsOut = (AseCounter) measMgr
				.getMeasurementCounter(NAME_OPTION_OUT);

		counterRegistersIn = (AseCounter) measMgr
				.getMeasurementCounter(NAME_REGISTER_IN);

		counterRegistersOut = (AseCounter) measMgr
				.getMeasurementCounter(NAME_REGISTER_OUT);

		counterInfosIn = (AseCounter) measMgr
				.getMeasurementCounter(NAME_INFO_IN);

		counterInfosOut = (AseCounter) measMgr
				.getMeasurementCounter(NAME_INFO_OUT);

		counterPracksIn = (AseCounter) measMgr
				.getMeasurementCounter(NAME_PRACK_IN);

		counterPracksOut = (AseCounter) measMgr
				.getMeasurementCounter(NAME_PRACK_OUT);

		counterRefersIn = (AseCounter) measMgr
				.getMeasurementCounter(NAME_REFER_IN);

		counterRefersOut = (AseCounter) measMgr
				.getMeasurementCounter(NAME_REFER_OUT);

		counterNotifysIn = (AseCounter) measMgr
				.getMeasurementCounter(NAME_NOTIFY_IN);

		counterNotifysOut = (AseCounter) measMgr
				.getMeasurementCounter(NAME_NOTIFY_OUT);

		counterSubscribesIn = (AseCounter) measMgr
				.getMeasurementCounter(NAME_SUBSCRIBE_IN);

		counterSubscribesOut = (AseCounter) measMgr
				.getMeasurementCounter(NAME_SUBSCRIBE_OUT);

		counterUpdatesIn = (AseCounter) measMgr
				.getMeasurementCounter(NAME_UPDATE_IN);

		counterUpdatesOut = (AseCounter) measMgr
				.getMeasurementCounter(NAME_UPDATE_OUT);

		counterMessagesIn = (AseCounter) measMgr
				.getMeasurementCounter(NAME_MESSAGE_IN);

		counterMessagesOut = (AseCounter) measMgr
				.getMeasurementCounter(NAME_MESSAGE_OUT);

		counterPublishsIn = (AseCounter) measMgr
				.getMeasurementCounter(NAME_PUBLISH_IN);

		counterPublishsOut = (AseCounter) measMgr
				.getMeasurementCounter(NAME_PUBLISH_OUT);

		counterTcapActiveCalls = (AseCounter) measMgr
				.getMeasurementCounter(NAME_ACTIVE_TCAP_CALLS);

		counterTcapNotifyReceived = (AseCounter) measMgr
				.getMeasurementCounter(NAME_TCAP_NOTIFY_RECEIVED);

		counterRequestsIn = (AseCounter) measMgr
				.getMeasurementCounter(NAME_REQUEST_IN);

		counterRequestsOut = (AseCounter) measMgr
				.getMeasurementCounter(NAME_REQUEST_OUT);

		counter1xxIn = (AseCounter) measMgr.getMeasurementCounter(NAME_1XX_IN);

		counter1xxOut = (AseCounter) measMgr
				.getMeasurementCounter(NAME_1XX_OUT);

		counter2xxIn = (AseCounter) measMgr.getMeasurementCounter(NAME_2XX_IN);

		counter2xxOut = (AseCounter) measMgr
				.getMeasurementCounter(NAME_2XX_OUT);

		counter3xxIn = (AseCounter) measMgr.getMeasurementCounter(NAME_3XX_IN);

		counter3xxOut = (AseCounter) measMgr
				.getMeasurementCounter(NAME_3XX_OUT);

		counter4xxIn = (AseCounter) measMgr.getMeasurementCounter(NAME_4XX_IN);

		counter4xxOut = (AseCounter) measMgr
				.getMeasurementCounter(NAME_4XX_OUT);

		counter5xxIn = (AseCounter) measMgr.getMeasurementCounter(NAME_5XX_IN);

		counter5xxOut = (AseCounter) measMgr
				.getMeasurementCounter(NAME_5XX_OUT);

		counter6xxIn = (AseCounter) measMgr.getMeasurementCounter(NAME_6XX_IN);

		counter6xxOut = (AseCounter) measMgr
				.getMeasurementCounter(NAME_6XX_OUT);

		counterResponsesIn = (AseCounter) measMgr
				.getMeasurementCounter(NAME_RESPONSE_IN);

		counterResponsesOut = (AseCounter) measMgr
				.getMeasurementCounter(NAME_RESPONSE_OUT);

		counterReplicated = (AseCounter) measMgr
				.getMeasurementCounter(NAME_REPLICATED);

		counterBeingReplicated = (AseCounter) measMgr
				.getMeasurementCounter(NAME_BEING_REPLICATED);

		counterCleanedUp = (AseCounter) measMgr
				.getMeasurementCounter(NAME_CLEANEDUP);

		counterActivated = (AseCounter) measMgr
				.getMeasurementCounter(NAME_ACTIVATED);

		counterAckTimedout = (AseCounter) measMgr
				.getMeasurementCounter(NAME_ACKTIMEDOUT);

		counterPrackTimedout = (AseCounter) measMgr
				.getMeasurementCounter(NAME_PRACKTIMEDOUT);

		counterSerializationFail = (AseCounter) measMgr
				.getMeasurementCounter(NAME_SERIALIZATION_FAIL);

		counterDeserializationFail = (AseCounter) measMgr
				.getMeasurementCounter(NAME_DESERIALIZATION_FAIL);

		thresholdAppSession = (AseCounter) measMgr
				.getThresholdCounter(NAME_APPSESSION_THRESHOLD);

		thresholdSIPSession = (AseCounter) measMgr
				.getThresholdCounter(NAME_SIPSESSION_THRESHOLD);

		thresholdSerializationFail = (AseCounter) measMgr
				.getThresholdCounter(NAME_SERIALIZATION_FAIL_THRESHOLD);

		thresholdDeserializationFail = (AseCounter) measMgr
				.getThresholdCounter(NAME_DESERIALIZATION_FAIL_THRESHOLD);

		// Http Related Measurement Counters
		counterActiveHttpSessions = (AseCounter) measMgr
				.getMeasurementCounter(NAME_ACTIVE_HTTP_SESSIONS);

		counterTotalHttpSessions = (AseCounter) measMgr
				.getMeasurementCounter(NAME_TOTAL_HTTP_SESSIONS);

		counterHttpRequestsIn = (AseCounter) measMgr
				.getMeasurementCounter(NAME_HTTP_REQUEST_IN);

		counterHttpGetRequestsIn = (AseCounter) measMgr
				.getMeasurementCounter(NAME_HTTP_GET_REQUEST_IN);

		counterHttpPostRequestsIn = (AseCounter) measMgr
				.getMeasurementCounter(NAME_HTTP_POST_REQUEST_IN);

		counterHttpSuccessResponsesOut = (AseCounter) measMgr
				.getMeasurementCounter(NAME_HTTP_SUCCESS_RESPONSE_OUT);

		counterHttpErrorResponsesOut = (AseCounter) measMgr
				.getMeasurementCounter(NAME_HTTP_ERROR_RESPONSE_OUT);

		// Peg Counts

		counterNewCalls = (AseCounter) measMgr
				.getMeasurementCounter(NAME_NEW_CALL_COUNT);

		counterCallsCurrentlyInProgress = (AseCounter) measMgr
				.getMeasurementCounter(NAME_CALLS_CURRENTLY_IN_PROGRESS);

		counterTotalCallsInProgress = (AseCounter) measMgr
				.getMeasurementCounter(NAME_TOTAL_CALLS_IN_PROGRESS);

		counterAverageCallHoldTime = (AseCounter) measMgr
				.getMeasurementCounter(NAME_AVERAGE_CALL_HOLD_TIME);

		counterNetworkTransactionsPerSec = (AseCounter) measMgr
				.getMeasurementCounter(NAME_NETWORK_TRANSACTIONS_PER_SECOND);

		counterAggregatedTransactionsPerSec = (AseCounter) measMgr
				.getMeasurementCounter(NAME_AGGREGATED_TRANSACTIONS_PER_SECOND);

		/**
		 * TCAP related counters
		 */
		counterTotalBeginsIn = (AseCounter) measMgr
				.getMeasurementCounter(NAME_TC_BEGINS_IN);

		counterTotalBeginsOut = (AseCounter) measMgr
				.getMeasurementCounter(NAME_TC_BEGINS_OUT);

		counterTotalUniDirIn = (AseCounter) measMgr
				.getMeasurementCounter(NAME_TC_UNIDIRECTIONAL_IN);

		counterTotalUniDirOut = (AseCounter) measMgr
				.getMeasurementCounter(NAME_TC_UNIDIRECTIONAL_OUT);

		counterTotalNoticeIn = (AseCounter) measMgr
				.getMeasurementCounter(NAME_TC_NOTICE_IN);

		counterTotalNoticeOut = (AseCounter) measMgr
				.getMeasurementCounter(NAME_TC_NOTICE_OUT);

		counterTotalUAbortIn = (AseCounter) measMgr
				.getMeasurementCounter(NAME_TC_UABORT_IN);

		counterTotalUAbortOut = (AseCounter) measMgr
				.getMeasurementCounter(NAME_TC_UABORT_OUT);

		counterTotalContinueIn = (AseCounter) measMgr
				.getMeasurementCounter(NAME_TC_CONTINUE_IN);

		counterTotalContinueOut = (AseCounter) measMgr
				.getMeasurementCounter(NAME_TC_CONTINUE_OUT);

		counterTotalEndIn = (AseCounter) measMgr
				.getMeasurementCounter(NAME_TC_END_IN);

		counterTotalEndOut = (AseCounter) measMgr
				.getMeasurementCounter(NAME_TC_END_OUT);

		counterTotalPAbortIn = (AseCounter) measMgr
				.getMeasurementCounter(NAME_TC_PABORT_IN);

		counterTotalInvokesIn = (AseCounter) measMgr
				.getMeasurementCounter(NAME_TC_INVOKES_IN);

		counterTotalInvokesOut = (AseCounter) measMgr
				.getMeasurementCounter(NAME_TC_INVOKES_OUT);

		counterTotalRetResultIn = (AseCounter) measMgr
				.getMeasurementCounter(NAME_TC_RET_RESULT_IN);

		counterTotalRetResultOut = (AseCounter) measMgr
				.getMeasurementCounter(NAME_TC_RET_RESULT_OUT);

		counterTotalErrorIn = (AseCounter) measMgr
				.getMeasurementCounter(NAME_TC_ERROR_IN);

		counterTotalErrorOut = (AseCounter) measMgr
				.getMeasurementCounter(NAME_TC_ERROR_OUT);

		counterTotalRejectIn = (AseCounter) measMgr
				.getMeasurementCounter(NAME_TC_REJECT_IN);

		counterTotalRejectOut = (AseCounter) measMgr
				.getMeasurementCounter(NAME_TC_REJECT_OUT);

		logger.error("initDefaultCounters exit "); 
	}
	
	public static void incrementRequestIn(int method) {
		try {
			changeRequestIn(method, 1);
		} catch (Exception e) {
			logger.error(" incrementRequestIn thrown xeption  " + e);
		}
	}

	public static void decrementRequestIn(int method) {
		try {
			changeRequestIn(method, -1);
		} catch (Exception e) {
			logger.error(" decrementRequestIn thrown xeption  " + e);
		}
	}
	
	public static void changeRequestIn(int method, int value){
	
		if (logger.isDebugEnabled()) {
			logger.error(" changeRequestIn called...  method " + method + " value "
					+ value);
		}
		
				switch(method){
			case DsSipConstants.INVITE:
				counterInvitesIn.change(value);
				counterRequestsIn.change(value);
				break;
			case DsSipConstants.ACK:
				counterAcksIn.change(value);
				counterRequestsIn.change(value);
				break;
			case DsSipConstants.BYE:
				counterByesIn.change(value);
				counterRequestsIn.change(value);
				break;
			case DsSipConstants.CANCEL:
				counterCancelsIn.change(value);
				counterRequestsIn.change(value);
				break;
			case DsSipConstants.OPTIONS:
				counterOptionsIn.change(value);
				counterRequestsIn.change(value);
				break;
			case DsSipConstants.REGISTER:
				counterRegistersIn.change(value);
				counterRequestsIn.change(value);
				break;
			case DsSipConstants.INFO:
				counterInfosIn.change(value);
				counterRequestsIn.change(value);
				break;
			case DsSipConstants.PRACK:
				counterPracksIn.change(value);
				counterRequestsIn.change(value);
				break;
			case DsSipConstants.REFER:
				counterRefersIn.change(value);
				counterRequestsIn.change(value);
				break;
			case DsSipConstants.NOTIFY:
				counterNotifysIn.change(value);
				counterRequestsIn.change(value);
				break;
			case DsSipConstants.SUBSCRIBE:
				counterSubscribesIn.change(value);
				counterRequestsIn.change(value);
				break;
			case DsSipConstants.UPDATE:
				counterUpdatesIn.change(value);
				counterRequestsIn.change(value);
				break;
			case DsSipConstants.MESSAGE:
				counterMessagesIn.change(value);
				counterRequestsIn.change(value);
				break;
			case DsSipConstants.PUBLISH:
				counterPublishsIn.change(value);
				counterRequestsIn.change(value);
				break;
			default:
		}

	}

	public static void incrementRequestOut(int method) {
		try {
			changeRequestOut(method, 1);
		} catch (Exception e) {
			logger.error(" incrementRequestOut thrown xeption  " + e);
		}
	}

	public static void decrementRequestOut(int method) {
		try {
			changeRequestOut(method, -1);
		} catch (Exception e) {
			logger.error(" decrementRequestOut thrown xeption  " + e);
		}
	}
	
	public static void changeRequestOut(int method, int value){
		switch(method){
			case DsSipConstants.INVITE:
				counterInvitesOut.change(value);
				counterRequestsOut.change(value);
				break;
			case DsSipConstants.ACK:
				counterAcksOut.change(value);
				counterRequestsOut.change(value);
				break;
			case DsSipConstants.BYE:
				counterByesOut.change(value);
				counterRequestsOut.change(value);
				break;
			case DsSipConstants.CANCEL:
				counterCancelsOut.change(value);
				counterRequestsOut.change(value);
				break;
			case DsSipConstants.OPTIONS:
				counterOptionsOut.change(value);
				counterRequestsOut.change(value);
				break;
			case DsSipConstants.REGISTER:
				counterRegistersOut.change(value);
				counterRequestsOut.change(value);
				break;
			case DsSipConstants.INFO:
				counterInfosOut.change(value);
				counterRequestsOut.change(value);
				break;
			case DsSipConstants.PRACK:
				counterPracksOut.change(value);
				counterRequestsOut.change(value);
				break;
			case DsSipConstants.REFER:
				counterRefersOut.change(value);
				counterRequestsOut.change(value);
				break;
			case DsSipConstants.NOTIFY:
				counterNotifysOut.change(value);
				counterRequestsOut.change(value);
				break;
			case DsSipConstants.SUBSCRIBE:
				counterSubscribesOut.change(value);
				counterRequestsOut.change(value);
				break;
			case DsSipConstants.UPDATE:
				counterUpdatesOut.change(value);
				counterRequestsOut.change(value);
				break;
			case DsSipConstants.MESSAGE:
				counterMessagesOut.change(value);
				counterRequestsOut.change(value);
				break;
			case DsSipConstants.PUBLISH:
				counterPublishsOut.change(value);
				counterRequestsOut.change(value);
				break;
			default:
		}
	}
	
	public static void incrementResponseIn(int response) {
		try {
			changeResponseIn(response, 1);
		} catch (Exception e) {
			logger.error(" incrementResponseIn thrown xeption  " + e);
		}
	}

	public static void decrementResponseIn(int response) {
		try {
			changeResponseIn(response, -1);
		} catch (Exception e) {
			logger.error(" decrementResponseIn thrown xeption  " + e);
		}
	}
	
	public static void changeResponseIn(int response, int value){
		
		if(response >= 100 && response < 200){
			counter1xxIn.change(value);
			counterResponsesIn.change(value);
		}else if(response >= 200 && response < 300){
			counter2xxIn.change(value);
			counterResponsesIn.change(value);
		}else if(response >= 300 && response < 400){
			counter3xxIn.change(value);
			counterResponsesIn.change(value);
		}else if(response >= 400 && response < 500){
			counter4xxIn.change(value);
			counterResponsesIn.change(value);
		}else if(response >= 500 && response < 600){
			counter5xxIn.change(value);
			counterResponsesIn.change(value);
		}else if(response >= 600 && response < 700){
			counter6xxIn.change(value);
			counterResponsesIn.change(value);
		}
	}
	
	public static void incrementResponseOut(int response){
		changeResponseOut(response, 1);	
	}
	
	public static void decrementResponseOut(int response){
			changeResponseOut(response, -1);	
	}
	
	public static void changeResponseOut(int response, int value){
		
		if(response >= 100 && response < 200){
			counter1xxOut.change(value);
			counterResponsesOut.change(value);
		}else if(response >= 200 && response < 300){
			counter2xxOut.change(value);
			counterResponsesOut.change(value);
		}else if(response >= 300 && response < 400){
			counter3xxOut.change(value);
			counterResponsesOut.change(value);
		}else if(response >= 400 && response < 500){
			counter4xxOut.change(value);
			counterResponsesOut.change(value);
		}else if(response >= 500 && response < 600){
			counter5xxOut.change(value);
			counterResponsesOut.change(value);
		}else if(response >= 600 && response < 700){
			counter6xxOut.change(value);
			counterResponsesOut.change(value);
		}
	}


/*
	public static void incrementTimedout(int method){
        changeTimedout(method, 1);
    }

    public static void decrementTimedout(int method){
        changeTimedout(method, -1);
    }


    public static void changeTimedout(int method, int value){

		switch(method){
			case DsSipConstants.INVITE:
                break;
            case DsSipConstants.ACK:
                counterAckTimedout.change(value);
                break;
           case DsSipConstants.BYE:
                break;
            case DsSipConstants.CANCEL:
                break;
            case DsSipConstants.OPTIONS:
                break;
            case DsSipConstants.REGISTER:
                break;
            case DsSipConstants.INFO:
                break;
            case DsSipConstants.PRACK:
				counterAckTimedout.change(value);
                break;
            case DsSipConstants.REFER:
                break;
            case DsSipConstants.NOTIFY:
                break;
            case DsSipConstants.SUBSCRIBE:
                break;
            case DsSipConstants.UPDATE:
                break;
            default:
				
		}
	}
    */

/*********************** NSEP specific Counters. Changes starts ***************************************/
	public static String NAME_NSEP_INVITE_IN;
	public static String NAME_NSEP_INVITE_OUT;
	public static String NAME_NSEP_INVITE_QUEUED_BUT_DROPPED;
	public static String NAME_NSEP_UNSUCCESSFUL_SESSIONS;
	public static String NAME_NSEP_INVITE_DROPPED_DUE_TO_MC_CNGN;
	public static String NAME_NSEP_TOTAL_MESSAGES;
	public static String NAME_NSEP_INVITE_EXEMPT_CONGESTION_CTRL;
	public static String NAME_NSEP_INVITE_EXEMPT_NETWORK_MGMT_CTRL;

	public static AseCounter counterNSEPInvitesIn;
	public static AseCounter counterNSEPInvitesOut;
	public static AseCounter counterNSEPInvitesQueuedButDropped;
	public static AseCounter counterNSEPUnsuccessfulSessions;
	public static AseCounter counterNSEPInvitesRejected;
	public static AseCounter counterNSEPTotalMessages;
	public static AseCounter counterNSEPCongestionExemptedInvites;
	public static AseCounter counterNSEPNetworkExemptedInvites;


	public static void initNSEPCounters() {

		NAME_NSEP_INVITE_IN = "Priority INVITEs IN";
		NAME_NSEP_INVITE_OUT = "Priority INVITEs OUT";
		NAME_NSEP_INVITE_QUEUED_BUT_DROPPED = "Priority INVITEs Queued But Dropped";
		NAME_NSEP_UNSUCCESSFUL_SESSIONS = "Priority Unsuccessful Sessions";
		NAME_NSEP_INVITE_DROPPED_DUE_TO_MC_CNGN = "Priority INVITEs Dropped Due To M/C Cngn";
		NAME_NSEP_TOTAL_MESSAGES = "Priority SIP Messages";
		NAME_NSEP_INVITE_EXEMPT_CONGESTION_CTRL = "Priority INVITEs Exempted Frm Cngn Ctrl";
		NAME_NSEP_INVITE_EXEMPT_NETWORK_MGMT_CTRL = "Priority INVITEs Exempted Frm N/W Mgmt Ctrl";

		counterNSEPInvitesIn = (AseCounter)NSEPMeasMgr.getMeasurementCounter(NAME_NSEP_INVITE_IN);
		counterNSEPInvitesOut = (AseCounter)NSEPMeasMgr.getMeasurementCounter(NAME_NSEP_INVITE_OUT);
		counterNSEPInvitesQueuedButDropped = (AseCounter)NSEPMeasMgr.getMeasurementCounter(NAME_NSEP_INVITE_QUEUED_BUT_DROPPED);
		counterNSEPUnsuccessfulSessions = (AseCounter)NSEPMeasMgr.getMeasurementCounter(NAME_NSEP_UNSUCCESSFUL_SESSIONS);
		counterNSEPInvitesRejected = (AseCounter)NSEPMeasMgr.getMeasurementCounter(NAME_NSEP_INVITE_DROPPED_DUE_TO_MC_CNGN);
		counterNSEPTotalMessages = (AseCounter)NSEPMeasMgr.getMeasurementCounter(NAME_NSEP_TOTAL_MESSAGES);
		counterNSEPCongestionExemptedInvites = (AseCounter)NSEPMeasMgr.getMeasurementCounter(NAME_NSEP_INVITE_EXEMPT_CONGESTION_CTRL);
		counterNSEPNetworkExemptedInvites = (AseCounter)NSEPMeasMgr.getMeasurementCounter(NAME_NSEP_INVITE_EXEMPT_NETWORK_MGMT_CTRL);
		
	}
	
	/**
	 * Currently This method is called to count only INVITEs IN. In order to 
	 * count more requests call it from appropriate place and add more case: 
	 */
	public static void incrementPriorityRequestIn(int method) {
		switch(method) {
			case DsSipConstants.INVITE:
				counterNSEPInvitesIn.change(1);
				break;
			default:
		}
	}

	/**
	 * Currently This method is called to count only INVITEs OUT. In order to 
	 * count more requests call it from appropriate place and add more case: 
	 */
	public static void incrementPriorityRequestOut(int method) {
		switch(method) {
			case DsSipConstants.INVITE:
				counterNSEPInvitesOut.change(1);
				break;
			default:
		}
	}

	public static void incrementUnsuccessfulPrioritySessions() {
		counterNSEPUnsuccessfulSessions.change(1);
	}

	public static void incrementPriorityRejectedInvites() {
		counterNSEPInvitesRejected.change(1);
	}

	public static void incrementPriorityMessageCount() {
		counterNSEPTotalMessages.change(1);
	}

	public static void incrementCngnExemptedPriorityMessage(int method) {
		switch(method) {
			case DsSipConstants.INVITE:
				counterNSEPCongestionExemptedInvites.change(1);
				break;
			default:
				if(logger.isDebugEnabled())logger.debug("No counter for method "+method);
				break;
		}
	}


/*********************** NSEP specific Counters. Changes ends *****************************************/

	/////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////// LOOPBACK MESSAGE COUNTERS ////////////////////////////////
	//////////////////////////// (ADDED FOR APPLICATION COMPOSITION) ////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Following method initializes local counters, which are not defined in measure-config.xml
	 * and not exposed to EMS. These are accessible via Telnet interface only.
	 */
	public static void initLocalCounters() {
		
		logger.error("initLocalCounters ");
		counterLbInvites.setType(AseCounter.TYPE_EVENT);
		counterLbInvites.setServiceId(AseMeasurementManager.DEFAULT_SERVICE_ID);

		counterLbAcks.setType(AseCounter.TYPE_EVENT);
		counterLbAcks.setServiceId(AseMeasurementManager.DEFAULT_SERVICE_ID);

		counterLbByes.setType(AseCounter.TYPE_EVENT);
		counterLbByes.setServiceId(AseMeasurementManager.DEFAULT_SERVICE_ID);

		counterLbCancels.setType(AseCounter.TYPE_EVENT);
		counterLbCancels.setServiceId(AseMeasurementManager.DEFAULT_SERVICE_ID);

		counterLbOptions.setType(AseCounter.TYPE_EVENT);
		counterLbOptions.setServiceId(AseMeasurementManager.DEFAULT_SERVICE_ID);

		counterLbRegisters.setType(AseCounter.TYPE_EVENT);
		counterLbRegisters.setServiceId(AseMeasurementManager.DEFAULT_SERVICE_ID);

		counterLbInfos.setType(AseCounter.TYPE_EVENT);
		counterLbInfos.setServiceId(AseMeasurementManager.DEFAULT_SERVICE_ID);

		counterLbPracks.setType(AseCounter.TYPE_EVENT);
		counterLbPracks.setServiceId(AseMeasurementManager.DEFAULT_SERVICE_ID);

		counterLbRefers.setType(AseCounter.TYPE_EVENT);
		counterLbRefers.setServiceId(AseMeasurementManager.DEFAULT_SERVICE_ID);

		counterLbNotifys.setType(AseCounter.TYPE_EVENT);
		counterLbNotifys.setServiceId(AseMeasurementManager.DEFAULT_SERVICE_ID);

		counterLbSubscribes.setType(AseCounter.TYPE_EVENT);
		counterLbSubscribes.setServiceId(AseMeasurementManager.DEFAULT_SERVICE_ID);

		counterLbUpdates.setType(AseCounter.TYPE_EVENT);
		counterLbUpdates.setServiceId(AseMeasurementManager.DEFAULT_SERVICE_ID);

		counterLbMessages.setType(AseCounter.TYPE_EVENT);
		counterLbMessages.setServiceId(AseMeasurementManager.DEFAULT_SERVICE_ID);

		counterLbPublishs.setType(AseCounter.TYPE_EVENT);
		counterLbPublishs.setServiceId(AseMeasurementManager.DEFAULT_SERVICE_ID);

		counterLbRequests.setType(AseCounter.TYPE_EVENT);
		counterLbRequests.setServiceId(AseMeasurementManager.DEFAULT_SERVICE_ID);

		counterLb1xx.setType(AseCounter.TYPE_EVENT);
		counterLb1xx.setServiceId(AseMeasurementManager.DEFAULT_SERVICE_ID);

		counterLb2xx.setType(AseCounter.TYPE_EVENT);
		counterLb2xx.setServiceId(AseMeasurementManager.DEFAULT_SERVICE_ID);

		counterLb3xx.setType(AseCounter.TYPE_EVENT);
		counterLb3xx.setServiceId(AseMeasurementManager.DEFAULT_SERVICE_ID);

		counterLb4xx.setType(AseCounter.TYPE_EVENT);
		counterLb4xx.setServiceId(AseMeasurementManager.DEFAULT_SERVICE_ID);

		counterLb5xx.setType(AseCounter.TYPE_EVENT);
		counterLb5xx.setServiceId(AseMeasurementManager.DEFAULT_SERVICE_ID);

		counterLb6xx.setType(AseCounter.TYPE_EVENT);
		counterLb6xx.setServiceId(AseMeasurementManager.DEFAULT_SERVICE_ID);

		counterLbResponses.setType(AseCounter.TYPE_EVENT);
		counterLbResponses.setServiceId(AseMeasurementManager.DEFAULT_SERVICE_ID);

    	counterLbAckTimedout.setType(AseCounter.TYPE_EVENT);
    	counterLbAckTimedout.setServiceId(AseMeasurementManager.DEFAULT_SERVICE_ID);

    	counterLbPrackTimedout.setType(AseCounter.TYPE_EVENT);
    	counterLbPrackTimedout.setServiceId(AseMeasurementManager.DEFAULT_SERVICE_ID);
	}

	public static final String NAME_LB_INVITE = "Total Number of Loopback INVITEs";
	public static AseCounter counterLbInvites = AseMeasurementManager.instance().getCounter(NAME_LB_INVITE);
	
	public static final String NAME_LB_ACK = "Total Number of Loopback ACKs";
	public static AseCounter counterLbAcks = AseMeasurementManager.instance().getCounter(NAME_LB_ACK);
	
	public static final String NAME_LB_BYE = "Total Number of Loopback BYEs";
	public static AseCounter counterLbByes = AseMeasurementManager.instance().getCounter(NAME_LB_BYE);
	
	public static final String NAME_LB_CANCEL = "Total Number of Loopback CANCELs";
	public static AseCounter counterLbCancels = AseMeasurementManager.instance().getCounter(NAME_LB_CANCEL);
	
	public static final String NAME_LB_OPTION = "Total Number of Loopback OPTIONs";
	public static AseCounter counterLbOptions = AseMeasurementManager.instance().getCounter(NAME_LB_OPTION);
	
	public static final String NAME_LB_REGISTER = "Total Number of Loopback REGISTERs";
	public static AseCounter counterLbRegisters = AseMeasurementManager.instance().getCounter(NAME_LB_REGISTER);
	
	public static final String NAME_LB_INFO = "Total Number of Loopback INFOs";
	public static AseCounter counterLbInfos = AseMeasurementManager.instance().getCounter(NAME_LB_INFO);
	
	public static final String NAME_LB_PRACK = "Total Number of Loopback PRACKs";
	public static AseCounter counterLbPracks = AseMeasurementManager.instance().getCounter(NAME_LB_PRACK);
	
	public static final String NAME_LB_REFER = "Total Number of Loopback REFERs";
	public static AseCounter counterLbRefers = AseMeasurementManager.instance().getCounter(NAME_LB_REFER);
	
	public static final String NAME_LB_NOTIFY = "Total Number of Loopback NOTIFYs";
	public static AseCounter counterLbNotifys = AseMeasurementManager.instance().getCounter(NAME_LB_NOTIFY);
	
	public static final String NAME_LB_SUBSCRIBE = "Total Number of Loopback SUBSCRIBEs";
	public static AseCounter counterLbSubscribes = AseMeasurementManager.instance().getCounter(NAME_LB_SUBSCRIBE);
	
	public static final String NAME_LB_UPDATE = "Total Number of Loopback UPDATEs";
	public static AseCounter counterLbUpdates = AseMeasurementManager.instance().getCounter(NAME_LB_UPDATE);
	
	public static final String NAME_LB_MESSAGE = "Total Number of Loopback MESSAGEs";
	public static AseCounter counterLbMessages = AseMeasurementManager.instance().getCounter(NAME_LB_MESSAGE);
	
	public static final String NAME_LB_PUBLISH = "Total Number of Loopback PUBLISHs";
	public static AseCounter counterLbPublishs = AseMeasurementManager.instance().getCounter(NAME_LB_PUBLISH);
	
	public static final String NAME_LB_REQUEST = "Total Number of Loopback Requests";
	public static AseCounter counterLbRequests = AseMeasurementManager.instance().getCounter(NAME_LB_REQUEST);
	
	public static final String NAME_LB_1XX = "Total Number of Loopback 1xx Responses";
	public static AseCounter counterLb1xx = AseMeasurementManager.instance().getCounter(NAME_LB_1XX);
	
	public static final String NAME_LB_2XX =  "Total Number of Loopback 2xx Responses";
	public static AseCounter counterLb2xx = AseMeasurementManager.instance().getCounter(NAME_LB_2XX);
	
	public static final String NAME_LB_3XX = "Total Number of Loopback 3xx Responses";
	public static AseCounter counterLb3xx = AseMeasurementManager.instance().getCounter(NAME_LB_3XX);
	
	public static final String NAME_LB_4XX = "Total Number of Loopback 4xx Responses";
	public static AseCounter counterLb4xx = AseMeasurementManager.instance().getCounter(NAME_LB_4XX);
	
	public static final String NAME_LB_5XX = "Total Number of Loopback 5xx Responses";
	public static AseCounter counterLb5xx = AseMeasurementManager.instance().getCounter(NAME_LB_5XX);
	
	public static final String NAME_LB_6XX = "Total Number of Loopback 6xx Responses";
	public static AseCounter counterLb6xx = AseMeasurementManager.instance().getCounter(NAME_LB_6XX);
	
	public static final String NAME_LB_RESPONSE = "Total Number of Loopback Responses";
	public static AseCounter counterLbResponses = AseMeasurementManager.instance().getCounter(NAME_LB_RESPONSE);
	
	public static final String NAME_LB_ACKTIMEDOUT = "Total Number of Loopback ACK Timedout";
    public static AseCounter counterLbAckTimedout= AseMeasurementManager.instance().getCounter(NAME_LB_ACKTIMEDOUT);

	public static final String NAME_LB_PRACKTIMEDOUT = "Total Number of Loopback PRACK Timedout";
    public static AseCounter counterLbPrackTimedout= AseMeasurementManager.instance().getCounter(NAME_LB_PRACKTIMEDOUT);

	public static void incrementLbRequest(int method) {
		try {
			changeLbRequest(method, 1);
		} catch (Exception e) {
			logger.error(" incrementLbRequest thrown xeption  " + e);
		}
	}

	public static void decrementLbRequest(int method) {
		try {
			changeLbRequest(method, -1);
		} catch (Exception e) {
			logger.error(" decrementLbRequest thrown xeption  " + e);
		}

	}
	
	public static void changeLbRequest(int method, int value){
		switch(method){
			case DsSipConstants.INVITE:
				counterLbInvites.change(value);
				counterLbRequests.change(value);
				break;
			case DsSipConstants.ACK:
				counterLbAcks.change(value);
				counterLbRequests.change(value);
				break;
			case DsSipConstants.BYE:
				counterLbByes.change(value);
				counterLbRequests.change(value);
				break;
			case DsSipConstants.CANCEL:
				counterLbCancels.change(value);
				counterLbRequests.change(value);
				break;
			case DsSipConstants.OPTIONS:
				counterLbOptions.change(value);
				counterLbRequests.change(value);
				break;
			case DsSipConstants.REGISTER:
				counterLbRegisters.change(value);
				counterLbRequests.change(value);
				break;
			case DsSipConstants.INFO:
				counterLbInfos.change(value);
				counterLbRequests.change(value);
				break;
			case DsSipConstants.PRACK:
				counterLbPracks.change(value);
				counterLbRequests.change(value);
				break;
			case DsSipConstants.REFER:
				counterLbRefers.change(value);
				counterLbRequests.change(value);
				break;
			case DsSipConstants.NOTIFY:
				counterLbNotifys.change(value);
				counterLbRequests.change(value);
				break;
			case DsSipConstants.SUBSCRIBE:
				counterLbSubscribes.change(value);
				counterLbRequests.change(value);
				break;
			case DsSipConstants.UPDATE:
				counterLbUpdates.change(value);
				counterLbRequests.change(value);
				break;
			case DsSipConstants.MESSAGE:
				counterLbMessages.change(value);
				counterLbRequests.change(value);
				break;
			case DsSipConstants.PUBLISH:
				counterLbPublishs.change(value);
				counterLbRequests.change(value);
				break;
			default:
		}
	}

	public static void incrementLbResponse(int response){
		changeLbResponse(response, 1);	
	}
	
	public static void decrementLbResponse(int response){
			changeLbResponse(response, -1);	
	}
	
	public static void changeLbResponse(int response, int value){
		
		if(response >= 100 && response < 200){
			counterLb1xx.change(value);
			counterLbResponses.change(value);
		}else if(response >= 200 && response < 300){
			counterLb2xx.change(value);
			counterLbResponses.change(value);
		}else if(response >= 300 && response < 400){
			counterLb3xx.change(value);
			counterLbResponses.change(value);
		}else if(response >= 400 && response < 500){
			counterLb4xx.change(value);
			counterLbResponses.change(value);
		}else if(response >= 500 && response < 600){
			counterLb5xx.change(value);
			counterLbResponses.change(value);
		}else if(response >= 600 && response < 700){
			counterLb6xx.change(value);
			counterLbResponses.change(value);
		}
	}
}
