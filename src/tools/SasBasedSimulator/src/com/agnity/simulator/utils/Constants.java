package com.agnity.simulator.utils;

public interface Constants {

	//Other constants
	public static final int TASK_FREQ=1;
	public static final long DEFAULT_TCAP_SESSION_TIMEOUT = 300;
	public static final long DEFAULT_AT_TIMEOUT = 60;
	public static final byte ACTIVITY_TEST_OP_CODE = (byte)0x37; //55 in decimal
	public static final String ACTIVITY_TEST_RESULT= "0x37";
	
	public static final String APP_NAME = "Simulator";
	public static final String APP_VERSION = "1.0";	
	public static final String INC_MESSAGE_IDENTIFIER1 = "ingw";
	public static final String INC_MESSAGE_IDENTIFIER2 = "tcap";
	public static final String TCAP_CONTENT_TYPE = "application/tcap";
	public static final String ISUP_CONTENT_TYPE = "application/isup";
	public static final String SDP_CONTENT_TYPE = "application/sdp";
	public static final String MULTIPART_CONTENT_TYPE = "multipart/";

	public static final String INFO_MESSAGE = "INFO";
	public static final String RECIEVE_ACTION = "receive";
	public static final String SEND_ACTION = "send";
	public static final String NO_ACTION = "NoAction";
	public static final String NOV_INCREMENT = "increment";
	public static final String NOV_STATIC = "static";

	//XML values
	public static final String ISUP_FLOW_TYPE="ISUP"; 

	//INAP dialogs
	public static final String DIALOG_BEGIN ="Begin".toLowerCase();
	public static final String DIALOG_CONTINUE ="Continue".toLowerCase();
	public static final String DIALOG_END ="End".toLowerCase();
	public static final String DIALOG_U_ABORT = "UAbort".toLowerCase();
	public static final String DIALOG_P_ABORT = "PAbort".toLowerCase();
	public static final String DIALOG_UNIDIR = "Unidirectional".toLowerCase();
	//XML parse::::
	public static final String CALLFLOW = "CallFlow";
	//elements
	public static final String NODE = "Node";
	public static final String VAR = "Var";
	public static final String PROVCALL = "ProvCall";
	public static final String FIELD = "Field";
	public static final String SUBFIELD = "SubField";
	public static final String SET = "Set";
	public static final String HEADER = "Header";
	public static final String URI = "Uri";
	public static final String TO = "To";
	public static final String FROM = "From";
	public static final String BODY = "Body";
	public static final String VALIDATE = "Validate";

	//types for Node element
	public static final String START = "Start";
	public static final String IDP = "IDP";
	public static final String ETC = "ETC";
	public static final String CON = "CON";
	public static final String ENC = "ENC";
	public static final String ERB = "ERB";
	public static final String SCI = "SCI";
	public static final String RNCE = "RNCE";
	public static final String RRBE = "RRBE";
	public static final String U_ABORT = "U-Abort";
	public static final String TC_END = "Tc-End";
	public static final String TC_ERROR = "Tc-Error";
	public static final String TC_REJECT = "Tc-Reject";
	public static final String RELEASE_CALL = "ReleaseCall";
	public static final String P_ABORT = "P-Abort";
	public static final String DFC = "DFC";
	public static final String ER = "ER";

	public static final String INVITE = "INVITE";
	public static final String BYE = "BYE";	
	public static final String CANCEL = "CANCEL";
	public static final String ACK = "ACK";
	public static final String PRACK = "PRACK";
	public static final String UPDATE = "UPDATE";
	public static final String INFO = "INFO";

	public static final String PRACK_SUCCESS_RES_NODE = "PRACK_2XX_NODE";
	public static final String UPDATE_SUCCESS_RES_NODE = "UPDATE_2XX_NODE";
	public static final String INFO_SUCCESS_RES_NODE = "INFO_2XX_NODE";
	public static final String BYE_SUCCESS_RES_NODE = "BYE_2XX_NODE";
	public static final String INVITE_PROV_RES_NODE = "INVITE_1XX_NODE";
	public static final String INVITE_SUCCESS_RES_NODE = "INVITE_2XX_NODE";
	public static final String INVITE_REDIRECT_RES_NODE = "INVITE_3XX_NODE";
	public static final String CLIENT_ERROR_RES_NODE = "4XX_NODE";
	public static final String SERVER_ERROR_RES_NODE = "5XX_NODE";
	
	public static final String CPG = "CPG";
	public static final String ACM = "ACM";

	public static final String IF_NODE = "If";
	public static final String TIMER_NODE = "Timer";
	public static final String CleanUp_NODE = "Cleanup";
	
	//types for WIN NODE element
	public static final String ORIG_REQ_RET_RESULT = "orreqres";
	public static final String ORREQ = "ORREQ";
	public static final String ANLYZD = "ANLYZD";
	public static final String ANLYZD_RES = "ANLYZDRES";
	public static final String CONNRES= "CONNRES";
	public static final String SEIZERES= "SEIZERES";
	public static final String SEIZERESRESP= "SEIZERESRESP";
	public static final String SRFDIRECTIVE= "SRFDIRECTIVE";
	public static final String SRFDIRECTIVE_RET_RES= "srfdirectiveretres";
	public static final String OANSWER= "OANSWER";
	public static final String ONOANSWER= "ONOANSWER";
	public static final String ONOANSWERRES= "ONOANSWERRES";
	public static final String ODISCONNECT= "ODISCONNECT";
	public static final String ODISCONNECTRES= "ODISCONNECTRES";
	public static final String INSTRUCTIONREQ= "INSTRUCTIONREQ";
	public static final String INSTRUCTIONRES= "INSTRUCTIONRES";
	public static final String CALLCONTROLDIRREQ= "CALLCONTROLDIR";
	public static final String CALLCONTROLDIRRES= "CALLCONTROLDIRRES";
	public static final String  TANSWER= "TANSWER";
	public static final String  TNOANSWER= "TNOANSWER";
	public static final String  TNOANSWERRES= "TNOANSWERRES";
	public static final String  TDISCONNECT= "TDISCONNECT";
	public static final String  TDISCONNECTRES= "TDISCONNECTRES";
	public static final String  TBUSY= "TBUSY";
	public static final String  TBUSYRES= "TBUSYRES";
	public static final String  OCALLEDPARTYBUSY= "OCALLEDPARTYBUSY";
	public static final String  OCALLEDPARTYBUSYRES = "OCALLEDPARTYBUSYRES";
	
	
	//attributes for Node elemnt
	public static final String NODE_TYPE = "type";   
	public static final String PREV_NODE_ID = "prevNodeId";
	public static final String NODE_ID = "nodeId";
	public static final String ACTION = "action";
	public static final String SIP_LEG = "sipLeg";
	public static final String FLOW_TYPE = "flowType";
	public static final String IS_LAST_MESSAGE = "isLastMessage";
	public static final String DIALOG_TYPE = "as";
	public static final String TIMEOUT = "timeout";
	public static final String TIMER_EXP_NEXT_NODE_ID = "timerActionNode";
	public static final String MESSAGE = "message";
	public static final String SUPPORTS_RELIABLE = "reliable";
	public static final String COMMAND_TO_EXECUTE = "command";
	
	public static final String SIPNODE_SET_SDP="setsdp";
	public static final String SIPNODE_SET_LASTSDP="setlastsdp";
	public static final String SIPNODE_SET_INFOCONTENT="setinfocontent";
	public static final String SIPNODE_SET_LASTINFOCONTENT="setlastinfocontent";
	public static final String SIPNODE_SET_REINVITE="reinvite";

	//attributes for node element (type=if)
	public static final String COND_TYPE = "condType";
	public static final String COND_OPERATOR = "condOperator";
	public static final String COND_VALUE = "condValue";
	public static final String NEXT_NODE_ID = "nextNodeId";

	//attributes for var element
	public static final String VAR_ID = "Id";  
	public static final String VAR_VALUE = "value";  
	public static final String NOV = "nov";
	public static final String STEP = "step";
	
	//attributes for provcall element
	public static final String PROVCALL_ID = "cmmndId";
	public static final String PROVCALL_VALUE = "cmmndvalue";

	//attributes for Field elem
	public static final String FIELD_TYPE = "type";
	public static final String FIELD_VALUE = "value";

	//attributes for SubFiled  elem
	public static final String SUB_FIELD_TYPE = "type";
	public static final String SUB_FIELD_VALUE = "value";
	public static final String SUB_FIELD_IS_LIST = "isList";

	//attributes for Set elem
	public static final String SET_VAR_ID = "id"; 
	public static final String SET_FIELD_TYPE = "fieldType";
	public static final String START_INDX = "startIndx";
	public static final String END_INDX = "endIndx";

	//attributes for BODY  elem
	public static final String BODY_TYPE="type";
	public static final String SDP="sdp";
	
	
	//attributes for Header elem
	public static final String HEADER_VALUE = "value";
	public static final String HEADER_NAME = "name";
	
	//attributes for field elem
	public static final String VALIDATE_FIELD_NAME = "field";
	public static final String VALIDATE_FIELD_VALUE = "value";

	//100 reliable
	public static final String HDR_REQUIRE = "Require";
	public static final String HDR_SUPPORTED = "Supported";
	public static final String VALUE_100REL = "100rel";
	public static final String HDR_RSEQ ="RSeq";
	public static final String TCAP_SESSION_ATTRIBUTE="Tcap-Session";


	public static final int dialogueIdRSN = 10001;
	public static final int invokeIdRSN = 10002;
	public static final String INTIAL_INVITE = "InitialInvite";

}

