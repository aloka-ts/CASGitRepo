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
package com.agnity.ain.enumdata;
/**
 * Enum for RoutingIndicator
 * @author Mriganka
 *
 */
public enum RoutingIndicatorEnum {
	/**
	 * 0-Routing based on GT
	 * 1-Routing based on PC & SSN
	 */
	ROUTING_GT(0), ROUTING_PC_SSN(1);
	private int code;
	private RoutingIndicatorEnum(int c) {		code = c;
	}
	public int getCode() {
		return code;
	}
	public static RoutingIndicatorEnum fromInt(int num) {
		switch (num) {
			case 0: { return ROUTING_GT; }
			case 1: { return ROUTING_PC_SSN; }
			default: { return null; }
		}
	}
}
