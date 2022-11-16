/*******************************************************************************
 *   Copyright (c) 2014 Agnity, Inc. All rights reserved.
 *   
 *   This is proprietary source code of Agnity, Inc. 
 *   
 *   Agnity, Inc. retains all intellectual property rights associated 
 *   with this source code. Use is subject to license terms.
 *   
 *   This source code contains trade secrets owned by Agnity, Inc.
 *   Confidentiality of this computer program must be maintained at 
 *   all times, unless explicitly authorized by Agnity, Inc.
 *******************************************************************************/

package com.agnity.ain.operations;

/**
 * 
 * This interface contains operation codes of all AIN messages.
 * 
 * @author Mriganka
 * 
 */
public interface AinOpCodes {

	// decode
	String IDP = "0x00";
	String RESTART_NOTIFICATION = "0x01";
	String ENC = "0x1A"; // 26
	String ERB = "0x18"; // 24
	String ER = "0x60"; // 96
	
	//ain operations SSP-->SCP
	String FAILURE_OUTCOME          ="0x64 0x04";
	String INFO_ANALYZED            ="0x64 0x03";
	String INFO_COLLECTED           ="0x64 0x02";
	String NETWORK_BUSY             ="0x64 0x17";
	String O_ANSWER                 ="0x64 0x0B";
	String O_NO_ANSWER              ="0x64 0x09";
	String O_CALLED_PARTY_BUSY      ="0x64 0x07";
	String O_DISCONNECT             ="0x64 0x1A";
	String O_TERM_SIZED             ="0x64 0x0C";
	String RESOURCE_CLEAR           ="0x66 0x02";
	String O_ABANDON                ="0x64 0x0E";
	String TERMINATION_ATTEMPT      ="0x64 0x05";
	String AUTHORIZED_TERMINATION   ="0x65 0x02";
	String FORWARD_CALL             ="0x6A 0x01";
	String SEND_NOTIFICATION        ="0x67 0x05";
	String TERMINATION_NOTIFICATION ="0x67 0x04";
	String ORIGINATION_ATTEMPT      ="0x64 0x18";
	String CLOSE                    ="0x6E 0x01";	
	String ANALYZE_ROUTE            ="0x65 0x01";
	String CONTINUE                 ="0x65 0x0D";
	String CONNECT_TO_RESOURCE      ="0X66 0x05";
	String SEND_TO_RESOURCE         ="0x66 0x01";
	String REQUEST_REPORT_BCSM_EVENT="0x6D 0x01";
	String ACG                      ="0x69 0x01";
	String DISCONNECT               ="0x65 0x03";
	String CALL_INFO_FROM_RESOURCE  ="0x66 0x04";
	//LIDB_QUERY_TYPE operation code
	String LIDB_Query = "0x81 0x01";
	String CONNECTION_CONTROL ="0x04 0x01"; 
	String PROVIDE_INSTRUCTION = "0x83 0x01";
	String AC_QUERY = "0xFE 0x01";

	String ARC_QUERY = "0x01 0x01";
	String ISVM_QUERY="0x0A 0x01";
	String ISVM_QUERY_WITH_REPLY="0x8A 0x01";
	
	int FAILURE_OUTCOME_BYTE= 25604;
	int INFO_ANALYZED_BYTE= 25603;
	int INFO_COLLECTED_BYTE= 25602;
	int NETWORK_BUSY_BYTE=25623;
	int O_ANSWER_BYTE=25611;
	int O_CALLED_PARTY_BUSY_BYTE=25607;
	int O_DISCONNECT_BYTE=25626;
	int O_TERM_SIZED_BYTE=25612;
	int RESOURCE_CLEAR_BYTE=26114;
	int O_NO_ANSWER_BYTE=25609;
	int O_ABANDON_BYTE=25614;
	int TERMINATION_ATTEMPT_BYTE=25605;
	int AUTHORIZED_TERMINATION_BYTE=25858;
	int FORWARD_CALL_BYTE=27137;
	int SEND_NOTIFICATION_BYTE     = 26373;
	int TERMINATION_NOTIFICATION_BYTE=26372;
	int ORIGINATION_ATTEMPT_BYTE   = 25624;
	int CLOSE_BYTE                 = 28161;
	int ANALYZE_ROUTE_BYTE         = 25857;
	int CONTINUE_AIN_BYTE          = 25869;
	int CONNECT_TO_RESOURCE_AIN_BYTE  =26117;
	int REQUEST_REPORT_BCSM_EVENT_BYTE=27905;
	int SEND_TO_RESOURCE_BYTE      = 26113;
	int ACG_BYTE                   = 26881;
	int DISCONNECT_BYTE            = 25859;
	int CALL_INFO_FROM_RESOURCE_BYTE =26116;
	
    // LIDB_QUERY_TYPE operation code.
	int LIDB_QUERY_BYTE = -32511; //-32511 is 2's complement of 33025;
	// encode
	int GN_ACG_BYTE = 1793;
	int CONNECTION_CONTROL_BYTE = 1025;
	int PROVIDE_INSTRUCTION_BYTE=-31999;
    int AC_QUERY_BYTE = -511; // -511 id 2's complement of 65025;
    int ARC_QUERY_BYTE = 257;//0x01 0x01 
    int ISVM_QUERY_BYTE=2561;//0x0A 0x01
    int ISVM_QUERY_WITH_REPLY_BYTE=-30207;//0x8A 0x01
	//String CONNECT_TO_RESOURCE = "0x13"; // 19

	String RESTART_NOTIFICATION_ACK = "0x02";

	String CONNECT = "0x14"; // 20

	String RELEASE_CALL = "0x16"; // 22

	String RRBE = "0x17"; // 23

	//String CONTINUE = "0x1F"; // 31

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
	
	//ain Operations SCP-->SSP

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

