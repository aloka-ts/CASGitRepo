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
package com.agnity.map.operations;

/**
 * 
 * This interface contains operation codes of all AIN messages.^M
 * 
 * @author Sanjay
 * 
 */
public interface MapOpCodes {

    
	
    // opcode of messages sent to HLR
    String MAP_ANY_TIME_INTERROGATION = "0x47"; // 71 CODE local in ASN.1
    String MAP_ANY_TIME_SUBSCRIPTION_INTERROGATION = "0x3E"; // 62
    String MAP_ANY_TIME_MODIFICATION = "0x41"; // 65
    String MAP_NOTE_SUBSCRIBER_DATA_MODIFIED = "0x05"; // 5
    String MAP_SEND_ROUTING_INFO = "0x16"; // 22
    String MAP_SS_INVOCATION_NOTIFICATION = "0x48";

    byte MAP_ANY_TIME_SUBSCRIPTION_INTERROGATION_BYTE = (byte) 0x3E;
    byte MAP_ANY_TIME_MODIFICATION_BYTE = (byte) 0x41;
    byte MAP_NOTE_SUBSCRIBER_DATA_MODIFIED_BYTE = (byte) 0x05;
    byte MAP_SEND_ROUTING_INFO_BYTE = (byte) 0x16;
    byte MAP_ANY_TIME_INTERROGATION_BYTE = (byte) 0x47;
    byte MAP_SS_INVOCATION_NOTIFICATION_BYTE = (byte)0x48;
}

