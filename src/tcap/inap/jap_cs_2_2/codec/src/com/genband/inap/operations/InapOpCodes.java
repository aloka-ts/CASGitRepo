package com.genband.inap.operations;

/**
 * 
 * This interface contains operation codes of all INAP messages.
 * 
 * @author vgoel
 * 
 */
public interface InapOpCodes {

	// decode
	String IDP = "0x00";
	String RESTART_NOTIFICATION = "0x01";
	String ENC = "0x1A"; // 26
	String ERB = "0x18"; // 24
	String ER = "0x60"; // 96

	// encode
	String CONNECT_TO_RESOURCE = "0x13"; // 19
	String RESTART_NOTIFICATION_ACK = "0x02";
	String CONNECT = "0x14"; // 20
	String RELEASE_CALL = "0x16"; // 22
	String RRBE = "0x17"; // 23
	String CONTINUE = "0x1F"; // 31
	String FCI = "0x22"; // 34
	String APPLY_CHARGING = "0x23"; // 35
	// String APPLY_CHARGING_REPORT = "35";
	String SCI = "0x2E"; // 46
	String SRR = "0x31"; // 49
	String CANCEL = "0x35"; // 53
	String DFC = "-0x1E"; // -30
	String ETC = "0x11"; // 17
	String RNCE = "0x19"; // 25
	String ACTIVITY_TEST = "0x37";
	//bug7995[
	String DFC_WITHOUT_ARGS = "0x12"; //18
	byte DFC_WITHOUT_ARGS_BYTE = 0x12;//18
	//]closed bug 7995

	byte IDP_BYTE = 0x00;
	byte RESTART_NOTIFICATION_BYTE = 0x01;
	byte ENC_BYTE = 0x1A; // 26
	byte ERB_BYTE = 0x18; // 24
	byte ER_BYTE = 0x60; // 96

	// encode
	byte CONNECT_TO_RESOURCE_BYTE = 0x13; // 19
	byte RESTART_NOTIFICATION_ACK_BYTE = 0x02;
	byte CONNECT_BYTE = 0x14; // 20
	byte RELEASE_CALL_BYTE = 0x16; // 22
	byte RRBE_BYTE = 0x17; // 23
	byte CONTINUE_BYTE = 0x1F; // 31
	byte FCI_BYTE = 0x22; // 34
	byte APPLY_CHARGING_BYTE = 0x23; // 35
	// byte APPLY_CHARGING_REPORT = 35;
	byte SCI_BYTE = 0x2E; // 46
	byte SRR_BYTE = 0x31; // 49
	byte CANCEL_BYTE = 0x35; // 53
	byte DFC_BYTE = -0x1E; // -30
	byte ETC_BYTE = 0x11; // 17
	byte RNCE_BYTE = 0x19; // 25
	byte ACTIVITY_TEST_BYTE = 0x37;
}
