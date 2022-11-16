package com.camel.CAPMsg;

/**
 * This enum represents the possible Call State.
 * @author nkumar
 *
 */
public enum SasCapCallStateEnum {

	/* After calling connect api, state changed to CONNECT_IN_PROGRESS.This state represents connection in progress.*/
	CONNECT_IN_PROGRESS (0),
	/* After receiving SRR(play result) or RRSLT(PlayAndCollect Result) from network, state changed to USER_INTERACTION_COMPLETED. This state represents interaction has been completed with user.*/
	USER_INTERACTION_COMPLETED(1),
	/* In case of originating half of the call,When terminating party disconnect the call before establishing the call,state changed to O_BUSY.*/
	O_BUSY(2),
	/* In case of originating half of the call,When terminating party does not pick the call,state changed to O_NOANSWER.*/
	O_NOANSWER(3),
	/* When terminating party disconnect the call,state changed to O_DISCONNECT_LEG2.*/
	O_DISCONNECT_LEG2(4),
	/* In case of originating half of the call,After calling disconnectIvr api, state changed to IVR_DISCONNECTED. This state represents IVR has been disconnected.*/
	IVR_DISCONNECTED(5),
	/* After receiving IDP from network, state changed to ANALYZED_INFORMATION. */
	ANALYZED_INFORMATION(6),
	/* When terminating party pick up the call,state changed to CONNECTED.*/
	CONNECTED(7),
	/* When originating party disconnect the call,state changed to O_DISCONNECT_LEG1.*/
	O_DISCONNECT_LEG1(8),
	/* After calling play or playAndCollect api, state changed to USER_INTERACTION_IN_PROGRESS.This state represents user interaction in progress.*/
	USER_INTERACTION_IN_PROGRESS(9),
	/* After receiving ApplyCharging report from network, state changed to CONNECTED_ACHRPT. */
	CONNECTED_ACHRPT(10),
	/* In case of SIP IVR, After calling connectToIvr or media sbb connect api, state changed to IVR_CONNECT_PROGRESS. This state represents IVR connection in progress.*/
	IVR_CONNECT_PROGRESS(11),
	/* In case of SIP Ivr,After receiving the IVR connected event from media Sbb, state changed to IVR_CONNECTED. */
	IVR_CONNECTED(12),
	/* In case of terminating half of the call,When terminating party disconnect the call before establishing the call,state changed to T_BUSY.*/
	T_BUSY(13),
	/* In case of terminating half of the call,When terminating party does not pick the call,state changed to T_NOANSWER.*/
	T_NOANSWER(14),
	/* In case of terminating half of the call, When originating party disconnect the call,state changed to T_DISCONNECT_LEG1.*/
	T_DISCONNECT_LEG1(15),
	/* In case of terminating half of the call, When terminating party disconnect the call,state changed to T_DISCONNECT_LEG2.*/
	T_DISCONNECT_LEG2(16),
	/* In case of terminating half of the call, When originating party disconnect the call before establishing the call,state changed to T_ABANDON.*/
	T_ABANDON(17),
	/* If dialled number is not routable or some network failure then state changed to ROUTE_SELECT_FAILURE */
	ROUTE_SELECT_FAILURE(18),
	/* if some error or reject received from network then state changed to ERROR_STATE */
	ERROR_STATE(19),
	/* In case of originating half of the call, When originating party disconnect the call before establishing the call,state changed to O_ABANDON.*/
	O_ABANDON(20),
	/* After receiving CallInformationReport from network, state changed to CALL_INFO_RPT. */
	CALL_INFO_RPT(21);
	
	
	private int code;

	private SasCapCallStateEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
}
