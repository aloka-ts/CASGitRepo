package com.genband.isup.operations;

/**
 * This interface defines offset arrays and operation codes
 * @author vgoel
 *
 */
public interface ISUPConstants
{
	/**
	 * Array for storing offsets of mandatory fixed length parameters of ISUP messages.
	 * Currently, this array has capacity for 20 ISUP messages with each message having one or more fixed parameters offset.
	 * First dimension of array i.e., array[0] and array[1] refers to protocol. 0 - ITUT and 1 - ANSI
	 * Second dimension of array i.e., array[0][1]  refers to message type codes..like 1 refers to IAM, 2 - ACM and so on. 
	 * Third dimaension of an array i.e., array[0][1] contains an offset for messages corresponding to their indexes.. 
	 * like array[0][1][] corresponds to ITU-T IAM manadatory part offset as 0, 1, 2, 4, 5 and 6 and so on. 

	 * Last offset of every array i.e. array[0][], array[1][] etc. contains the value = offset of last parameter + its length.
	 * 
	 * Offset starts from 0.
	 * Offset for "Message Type" parameter is included in array. byte[0] of data will always contain Message Type parameter.
	 */
	int[][][] MAND_FIXED_PARAMS_OFFSET = 	{
			{   // Offset as per ITUT standard
				{},				 
				{0, 1, 2, 4, 5, 6},		//array[1][] for IAM
				{0, 1, 3},				//array[2][] for ACM
				{0, 1},					//array[3][] for CFN
				{0, 1},					//array[4][] for ANM
				{0, 1},					//array[5][] for REL
				{0, 1},					//array[6][] for RLC
				{0, 1, 2},				//array[7][] for CHG
				{0, 1, 2},				//array[8][] for CPG
				{0, 1, 2},				//array[9][] for SUS
				{0, 1, 2}				//array[10][] for RES
			},
			{ // Offset as per ANSI standard
				{},				 
				{0, 1, 2, 4, 5},		//array[1][] for IAM
				{0, 1, 3},				//array[2][] for ACM
				{0, 1},					//array[3][] for CFN
				{0, 1},					//array[4][] for ANM
				{0, 1},					//array[5][] for REL
				{0, 1},					//array[6][] for RLC
				{0, 1, 2},				//array[7][] for CHG
				{0, 1, 2},				//array[8][] for CPG
				{0, 1, 2},				//array[9][] for SUS
				{0, 1, 2}				//array[10][] for RES
			}
	};


	/**
	 * Array for storing offsets of mandatory variable length parameters of ISUP messages.
	 * Currently, this array has capacity for 20 ISUP messages with each message having one or more variable parameters offset.
	 * First dimension of array i.e., array[0] and array[1] refers to protocol. 0 - ITUT and 1 - ANSI
	 * Second  dimension of array i.e. array[0][0], array[0][1] indexes corresponds to message type codes.
	 * For ex.: array[0][1] contains offset for IAM as per ITUT, array[0][2] for ACM as per ITUT
	 * 
	 * Offset starts from 0. Offset = -1 indicates that variable parameter does not exist.
	 */
	int[][][] MAND_VAR_PARAMS_OFFSET =  {
			{ {},				 
				{6},				//array[1][] for IAM
				{-1},				//array[2][] for ACM
				{1},				//array[3][] for CFN
				{-1},				//array[4][] for ANM
				{1},				//array[5][] for REL
				{-1},				//array[6][] for RLC
				{2},				//array[7][] for CHG
				{-1},				//array[8][] for CPG
				{-1},				//array[9][] for SUS
				{-1}				//array[10][] for RES
			},
			{
				{},				 
				{5, 6},				//array[1][] for IAM
				{-1},				//array[2][] for ACM
				{1},				//array[3][] for CFN
				{-1},				//array[4][] for ANM
				{1},				//array[5][] for REL
				{-1},				//array[6][] for RLC
				{2},				//array[7][] for CHG
				{-1},				//array[8][] for CPG
				{-1},				//array[9][] for SUS
				{-1}				//array[10][] for RES

			}
	};

	/**
	 * Array for storing offsets of optional parameters of ISUP messages.
	 * Currently, this array stores one optional parameter offset for each message (since there can be only one opt param offset value).
	 * Index of array i.e. array[0], array[1] indexes corresponds to message type codes.
	 * For ex.: array[1] contains offset for IAM, array[2] for ACM.
	 * 
	 * Offset starts from 0. Offset = -1 indicates that optional parameter does not exist.
	 */
	int[] OPT_PARAMS_OFFSET =  {-1, 7, 3, 2, 1, 2, 1, 3, 2, 2, 2};				//array[1] for IAM, array[2] for ACM, array[3] for CFN, array[4] for ANM, array[5] for REL,
	//array[6] for RLC, array[7] for CHG, array[8] for CPG, array[9] for SUS , array[10] for RES



	//operation codes in decimal
	String OP_CODE_IAM = "1";
	String OP_CODE_ACM = "6";
	String OP_CODE_CFN = "47";
	String OP_CODE_ANM = "9";
	String OP_CODE_REL = "12";
	String OP_CODE_RLC = "16";
	String OP_CODE_CHG = "254";
	String OP_CODE_CPG = "44";
	String OP_CODE_SUS = "13";
	String OP_CODE_RES = "14";


	//internal op codes for storing in array
	int OP_CODE_INT_IAM = 1;
	int OP_CODE_INT_ACM = 2;
	int OP_CODE_INT_CFN = 3;
	int OP_CODE_INT_ANM = 4;
	int OP_CODE_INT_REL = 5;
	int OP_CODE_INT_RLC = 6;
	int OP_CODE_INT_CHG = 7;
	int OP_CODE_INT_CPG = 8;
	int OP_CODE_INT_SUS = 9;
	int OP_CODE_INT_RES = 10;

	//op params codes in decimal
	int CODE_ACCESS_TRANSPORT     = 3;
	int CODE_BW_CALL_IND          = 17;	
	int CODE_CAUSE_IND            = 18;
	int CODE_OPT_BW_CALL_IND      = 41;
	int CODE_PROP_DELAY_COUNTER   = 49;
	int CODE_SCF_ID               = 102;
	int CODE_CALLED_IN_NUMBER     = 111;
	int CODE_CALLING_PARTY_NUM    = 10;	// 10 as NTT Q763-1
	int CODE_GENERIC_DIGITS       = 193;
	int CODE_CARRIER_INFO_TRFR    = 241;
	int CODE_CHARGE_AREA_INFO     = 253;
	int CODE_ADDITIONAL_PARTY_CAT = 243;
	int CODE_DPC_INFO             = 235; // As per ITU-T, NTT
	int CODE_CHARGE_NUM           = 235; // As per ANSI
	int CODE_GENERIC_NUMBER       = 192;
	int CODE_CORRELATION_ID       = 101;
	int CODE_JTI                  = 239;
	int CODE_SERVICE_ACTIVATION   = 51;
	int CODE_JURISDICTION_INFO    = 196;

	int ISUP_ITUT = 0;
	int ISUP_ANSI = 1;

	int CODE_CONTRACTOR_NUMBER  = 249;
	int CODE_REDIRECTING_NUMBER = 11;
	int CODE_ORIGINAL_CALLED_NUMBER = 40;
	int CODE_REDIRECTION_INFO  = 19;
	int CODE_USER_TO_USER_INFO = 32;
	int CODE_CHARGING_INFO     = 251;
	int CODE_CHARGE_INFO_TYPE  = 250;
	int CODE_CHARGE_INFO_DELAY = 242;
	int CODE_USER_SERVICE_INFO=29;
	
}
