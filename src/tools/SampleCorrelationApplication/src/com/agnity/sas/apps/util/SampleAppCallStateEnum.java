package com.agnity.sas.apps.util;

/**
 * This enum represents the possible Call State.
 * @author saneja
 *
 */
public enum SampleAppCallStateEnum {
	
	//BEGIN is recieved as message
	BEGIN_RECIEVED(0),
	//IDP is recieved as message
	ANALYZED_INFORMATION(1), 
	//after creating ETC component
	SENDING_ETC(2),
	//after sending ETC component
	ETC_SENT(3), 
	//on recieveing IAMmessage
	IAM_RECIEVED(4), 
	//before invoking ms SBB
	MEDIA_OPERATION(5),
	//after creating DFC component
	SENDING_DFC(6),
	//after creating CON component
	SENDING_CON(7),
	//DFC and CON sent
	DFC_CON_SENT(8), 
	//on completion of IVR interactions
	MEDIA_OPERATION_COMPLETED(9), 
	RCVD_REL(10), 
	RLC_SENT(11), 
	MEDIA_DISCONNECTED(12);
	
	
	
	

	
	
	private int code;

	private SampleAppCallStateEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
}
