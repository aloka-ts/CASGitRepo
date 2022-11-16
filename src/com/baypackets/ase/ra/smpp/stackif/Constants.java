/*******************************************************************************
 *   Copyright (c) Agnity, Inc. All rights reserved.
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


/***********************************************************************************
//
//      File:   Constants.java
//
//      Desc:   This class defines all the constant value to be used by smpp RA.
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Prashant Kumar              23/01/08        Initial Creation
//
/***********************************************************************************/

package com.baypackets.ase.ra.smpp.stackif;

public class Constants extends org.smpp.Data {
	
	// SmscPooling thread's pooling interval(in seconds)
	public static final int POOLING_INTERVAL=60;

	// Smpp requests type
	public static final int BIND_TRANSMITTER_REQ=101;
	public static final int BIND_RECEIVER_REQ=102;
	public static final int BIND_TRANSCEIVER_REQ=103;
	public static final int UNBIND_REQ=104;
	public static final int OUTBIND_REQ=105;
	public static final int ENQUIRE_LINK_REQ=106;
	public static final int ALERT_NOTIFICATION=107;
	public static final int GENERIC_NACK=108;

	public static final int SUBMIT_SM_REQ=109;
	public static final int SUBMIT_SM_MULTI_REQ=110;
	public static final int DATA_SM_REQ=111;
	public static final int DELIVER_SM_REQ=112;
	public static final int QUERY_SM_REQ=113;
	public static final int CANCEL_SM_REQ=114;
	public static final int REPLACE_SM_REQ=115;


	// Smpp responses type
	public static final int BIND_TRANSMITTER_RES=201;
	public static final int BIND_RECEIVER_RES=202;
	public static final int BIND_TRANSCEIVER_RES=203;
	public static final int UNBIND_RES=204;
	public static final int REPLACE_SM_RES=205;
	public static final int ENQUIRE_LINK_RES=206;

	public static final int SUBMIT_SM_RES=207;
	public static final int SUBMIT_SM_MULTI_RES=208;
	public static final int DATA_SM_RES=209;
	public static final int DELIVER_SM_RES=210;
	public static final int QUERY_SM_RES=211;
	public static final int CANCEL_SM_RES=212;
	
	public static final int SMPP_REQ_OUT_COUNT=213;
	public static final int SMPP_RES_OUT_COUNT=214;
	
	public static final int SMPP_REQ_IN_COUNT=215;
	public static final int SMPP_RES_IN_COUNT=216;

	public static final String INTERFACE_VERSION="52";
	public static final String PROTOCOL="SMPP";
	
	//States of SMSC primary or secondary
	public static final String STATUS_ACTIVE="STATUS_ACTIVE";
	public static final String STATUS_DOWN="STATUS_DOWN";

	public static final String BIND_TX_REQ_COUNTER ="Bind Transmitter Request Count";
	public static final String BIND_TX_RES_COUNTER ="Bind Transmitter Response Count";
	public static final String BIND_RX_REQ_COUNTER ="Bind Receiver Request Count";
	public static final String BIND_RX_RES_COUNTER ="Bind Receiver Response Count";
	public static final String BIND_TRX_REQ_COUNTER ="Bind Transceiver Request Count";
	public static final String BIND_TRX_RES_COUNTER ="Bind Transceiver Response Count";
	public static final String OUTBIND_REQ_COUNTER ="Outbind Request Count";
	public static final String UNBIND_REQ_COUNTER ="Unbind Request Count";
	public static final String UNBIND_RES_COUNTER ="Unbind Response Count";
	public static final String SMPP_REQ_OUT_COUNTER ="SMPP Request OUT Count";
	public static final String SMPP_RES_OUT_COUNTER ="SMPP Response OUT Count";
	public static final String SMPP_REQ_IN_COUNTER ="SMPP Request IN Count";
	public static final String SMPP_RES_IN_COUNTER ="SMPP Response IN Count";
	public static final String SUBMIT_REQ_COUNTER ="SUBMIT_SM Request Count";
	public static final String SUBMIT_RES_COUNTER ="SUBMIT_SM Response Count";
	public static final String SUBMITMULTI_REQ_COUNTER ="SUBMITMULTI_SM Request Count";
	public static final String SUBMITMULTI_RES_COUNTER ="SUBMITMULTI_SM Response Count";
	public static final String DATA_REQ_COUNTER ="DATA_SM Request Count";
	public static final String DATA_RES_COUNTER ="DATA_SM Response Count";
	public static final String DELIVER_REQ_COUNTER ="DELIVER_SM Request Count";
	public static final String DELIVER_RES_COUNTER ="DELIVER_SM Response Count";
	public static final String QUERY_REQ_COUNTER ="QUERY_SM Request Count";
	public static final String QUERY_RES_COUNTER ="QUERY_SM Response Count";
	public static final String REPLACE_REQ_COUNTER ="REPLACE_SM Request Count";
	public static final String REPLACE_RES_COUNTER ="REPLACE_SM Response Count";
	public static final String CANCEL_REQ_COUNTER ="CANCEL_SM Request Count";
	public static final String CANCEL_RES_COUNTER ="CANCEL_SM Response Count";
	public static final String ENQUIRELINK_REQ_COUNTER ="ENQUIRE_LINK Request Count";
	public static final String ENQUIRELINK_RES_COUNTER ="ENQUIRE_LINK Response Count";
	public static final String ALERT_NOTIFICATION_REQ_COUNTER ="ALERT_NOTIFICATION Request Count";
	public static final String GENERIC_NACK_REQ_COUNTER ="GENERIC_NACK Requst Count";
	public static final String SMPP_REQ_SEND_ERROR ="SMPP req send error";
	public static final String SMPP_RES_SEND_ERROR ="SMPP res send error";

	// Constants related to bind mode
	public static final String BIND_TX = "BIND_TX";
	public static final String BIND_RX = "BIND_RX";
	public static final String BIND_TRX = "BIND_TRX";

	/*
	// Optional parameters name
	static short OPT_PAR_ADD_STAT_INFO = super.OPT_PAR_ADD_STAT_INFO;
	static int OPT_PAR_ADD_STAT_INFO_MAX =super.OPT_PAR_ADD_STAT_INFO_MAX;
	static int OPT_PAR_ADD_STAT_INFO_MIN =super.OPT_PAR_ADD_STAT_INFO_MIN;
	static short OPT_PAR_ALERT_ON_MSG_DELIVERY =super.OPT_PAR_ALERT_ON_MSG_DELIVERY;
	static short OPT_PAR_CALLBACK_NUM =super.OPT_PAR_CALLBACK_NUM;
	static short OPT_PAR_CALLBACK_NUM_ATAG =super.;
	static int OPT_PAR_CALLBACK_NUM_ATAG_MAX =super.;
	static int OPT_PAR_CALLBACK_NUM_ATAG_MIN =super.;
	static int OPT_PAR_CALLBACK_NUM_MAX =super.;
	static int OPT_PAR_CALLBACK_NUM_MIN =super.;
	static short OPT_PAR_CALLBACK_NUM_PRES_IND=super. ;
	static short OPT_PAR_DEL_FAIL_RSN =super.;
	static short OPT_PAR_DEST_MSC_ADDR =super.;
	static int OPT_PAR_DEST_MSC_ADDR_MAX =super.;
	static int OPT_PAR_DEST_MSC_ADDR_MIN =super.;
	static short OPT_PAR_DEST_SUBADDR =super.;
	static int OPT_PAR_DEST_SUBADDR_MAX =super.;
	static int OPT_PAR_DEST_SUBADDR_MIN =super.;
	static short OPT_PAR_DISPLAY_TIME =super.;
	static short OPT_PAR_DPF_RES =super.;
	static short OPT_PAR_DST_ADDR_SUBUNIT ;
	static short OPT_PAR_DST_BEAR_TYPE =super.;
	static short OPT_PAR_DST_NW_TYPE =super.;
	static short OPT_PAR_DST_PORT =super.;
	static short OPT_PAR_DST_TELE_ID=super. 
	static short OPT_PAR_ITS_REPLY_TYPE =super.;
	static short OPT_PAR_ITS_SESSION_INFO =super.;
	static short OPT_PAR_LANG_IND =super.;
	static short OPT_PAR_MORE_MSGS =super.;
	static short OPT_PAR_MS_AVAIL_STAT =super.;
	static short OPT_PAR_MS_VALIDITY =super.;
	static short OPT_PAR_MSG_PAYLOAD =super.;
	static int OPT_PAR_MSG_PAYLOAD_MAX =super.;
	static int OPT_PAR_MSG_PAYLOAD_MIN =super.;
	static short OPT_PAR_MSG_STATE =super.;
	static short OPT_PAR_MSG_WAIT =super.;
	static short OPT_PAR_NUM_MSGS =super.;
	static short OPT_PAR_NW_ERR_CODE =super.;
	static int OPT_PAR_NW_ERR_CODE_MAX =super.;
	static int OPT_PAR_NW_ERR_CODE_MIN =super.;
	static short OPT_PAR_ORIG_MSC_ADDR =super.;
	static int OPT_PAR_ORIG_MSC_ADDR_MAX =super.;
	static int OPT_PAR_ORIG_MSC_ADDR_MIN =super.;
	static short OPT_PAR_PAYLOAD_TYPE =super.;
	static short OPT_PAR_PRIV_IND =super.;
	static short OPT_PAR_QOS_TIME_TO_LIVE =super.;
	static int OPT_PAR_QOS_TIME_TO_LIVE_MAX =super.;
	static int OPT_PAR_QOS_TIME_TO_LIVE_MIN =super.;
	static short OPT_PAR_RECP_MSG_ID=super. 
	static int OPT_PAR_RECP_MSG_ID_MAX =super.;
	static int OPT_PAR_RECP_MSG_ID_MIN =super.;
	static short OPT_PAR_SAR_MSG_REF_NUM =super.;
	static short OPT_PAR_SAR_SEG_SNUM =super.;
	static short OPT_PAR_SAR_TOT_SEG =super.;
	static short OPT_PAR_SC_IF_VER =super.;
	static short OPT_PAR_SET_DPF =super.;
	static short OPT_PAR_SMS_SIGNAL =super.;
	static short OPT_PAR_SRC_ADDR_SUBUNIT =super.;
	static short OPT_PAR_SRC_BEAR_TYPE =super.;
	static short OPT_PAR_SRC_NW_TYPE=super. 
	static short OPT_PAR_SRC_PORT =super.;
	static short OPT_PAR_SRC_SUBADDR =super.;
	static int OPT_PAR_SRC_SUBADDR_MAX =super.;
	static int OPT_PAR_SRC_SUBADDR_MIN =super.;
	static short OPT_PAR_SRC_TELE_ID =super.;
	static int OPT_PAR_UNUSED =super.;
	static short OPT_PAR_USER_MSG_REF=super. ;
	static short OPT_PAR_USER_RESP_CODE =super.;
	static short OPT_PAR_USSD_SER_OP =super.;
	*/

	// optional parameters
	// Privacy Indicator
	/*
public static final short OPT_PAR_PRIV_IND = 0x0202;

// Source Subaddress
public static final short OPT_PAR_SRC_SUBADDR = 0x0202;
// Destination Subaddress
public static final short OPT_PAR_DEST_SUBADDR = 0x0203;

// User Message Reference
public static final short OPT_PAR_USER_MSG_REF = 0x0204;

// User Response Code
public static final short OPT_PAR_USER_RESP_CODE = 0x0205;

// Language Indicator
public static final short OPT_PAR_LANG_IND = 0x020D;

// Source Port
public static final short OPT_PAR_SRC_PORT = 0x020A;

// Destination Port
public static final short OPT_PAR_DST_PORT = 0x020B;

// Concat Msg Ref Num
public static final short OPT_PAR_SAR_MSG_REF_NUM = 0x020C;

// Concat Total Segments
public static final short OPT_PAR_SAR_TOT_SEG = 0x020E;

// Concat Segment Seqnums
public static final short OPT_PAR_SAR_SEG_SNUM = 0x020F;

// SC Interface Version
public static final short OPT_PAR_SC_IF_VER = 0x0210;

// Display Time
public static final short OPT_PAR_DISPLAY_TIME = 0x1201;

// Validity Information
public static final short OPT_PAR_MS_VALIDITY = 0x1204;

// DPF Result
public static final short OPT_PAR_DPF_RES = 0x0420;

// Set DPF
public static final short OPT_PAR_SET_DPF = 0x0421;

// MS Availability Status
public static final short OPT_PAR_MS_AVAIL_STAT = 0x0422;

// Network Error Code
public static final short OPT_PAR_NW_ERR_CODE = 0x0423;
// public static final int OPT_PAR_NW_ERR_CODE_MIN = 3;
// public static final int OPT_PAR_NW_ERR_CODE_MAX = 3;

// Extended Short Message has no size limit

// Delivery Failure Reason
public static final short OPT_PAR_DEL_FAIL_RSN = 0x0425;

// More Messages to Follow
public static final short OPT_PAR_MORE_MSGS = 0x0426;

// Message State
public static final short OPT_PAR_MSG_STATE = 0x0427;

// Callback Number
//public static final short OPT_PAR_CALLBACK_NUM = 0x0381;
//public static final int OPT_PAR_CALLBACK_NUM_MIN = 4;
//public static final int OPT_PAR_CALLBACK_NUM_MAX = 19;

// Callback Number Presentation  Indicator
//public static final short OPT_PAR_CALLBACK_NUM_PRES_IND = 0x0302;

//// Callback Number Alphanumeric Tag
//public static final short OPT_PAR_CALLBACK_NUM_ATAG = 0x0303;
//public static final int OPT_PAR_CALLBACK_NUM_ATAG_MIN = 1;
//public static final int OPT_PAR_CALLBACK_NUM_ATAG_MAX = 65;

// Number of messages in Mailbox
public static final short OPT_PAR_NUM_MSGS = 0x0304;

// SMS Received Alert
public static final short OPT_PAR_SMS_SIGNAL = 0x1203;

// Message Delivery Alert
public static final short OPT_PAR_ALERT_ON_MSG_DELIVERY = 0x130C;

// ITS Reply Type
public static final short OPT_PAR_ITS_REPLY_TYPE = 0x1380;

// ITS Session Info
public static final short OPT_PAR_ITS_SESSION_INFO = 0x1383;

// USSD Service Op
public static final short OPT_PAR_USSD_SER_OP = 0x0501;
*/


}
