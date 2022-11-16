/*******************************************************************************
 *   Copyright (c) 2011 Agnity, Inc. All rights reserved.
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
package com.genband.isup.enumdata;

// This class defines Charge information Type as defined in TTC-JT-Qy63, section 3.105
// Octet format
// Octet -1 8	7	6	5	4	3	2	1				          
//The following codes are used in the Charge information type parameter field.		           
//  0 0 0 0 0 0 0 0										            
//          to            	reserved for national use						           
//  0 0 0 0 0 0 1 0 										           
//  0 0 0 0 0 0 1 1 	Application charging rate transfer				                       
//  0 0 0 0 0 1 0 0	 								                        
//    to .           	reserved for national use                                                       	                        *
//  1 0 0 0 0 0 0 0										           
//  1 0 0 0 0 0 0 1 									                        
//     to            spare								                        
//  1 1 1 1 1 1 0 1 									                        
//  1 1 1 1 1 1 1 0 Charging rate transfer						                        
//  1 1 1 1 1 1 1 1 spare	

public enum ChargeInfoTypeEnum {

	APP_CHARGING_RATE_TRANSFER(3), CHARGING_RATE_TRANSFER(254);

	private int code;

	private ChargeInfoTypeEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}

	public static ChargeInfoTypeEnum fromInt(int num) {
		switch (num) {
		case 3: { return APP_CHARGING_RATE_TRANSFER; }
		case 254: { return CHARGING_RATE_TRANSFER; }
		default: { return null; }
		}
	}
}