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
 * Enum for Cause Value 
 * @author Mriganka
 *
 */
public enum CauseValEnum {
	/**
	 * 1-Unallocated Number
	 * 2-No route to the specific long-distance network
	 * 3-No route to destination
	 * 16-Normal call clearing
	 * 17-User busy
	 * 18-No User response	 * 19-No answer from user (user alerted)
	 * 21-Call rejected	 * 22-Destination subscriber number changed	 * 25-Caller_abandon	 
	 * 26-Non-selected user clearing
	 * 27-Destination out of order
	 * 28-Invalid number format (address incomplete)	 
	 * 31-Normal, unspecified
	 * 38-Network failure
	 * 41-Temporary failure
	 * 42-Switching equipment congestion
	 * 44-Requested circuit/channel not available
	 * 47-Resource unavailable, unspecified
	 * 57-Bearer Capability not authorized
	 * 58-Bearer Capability not available
	 * 63-Service or option not available,unspecified	 * 65-Bearer Capability not implemented
	 * 79-Service or Option not implemented, unspecified	 
	 * 95-Invalid message, unspecified
	 * 97-Message type undefined or unprovided
	 * 99-Reserved_unimplemented_accepted
	 * 100-Invalid information element contents
	 * 101-Reserved_unimplemented_dicarded
	 * 111-Protocol error, unspecified
	 * 127-Other interworking class
	 */
	Unallocated_number(1),No_route_spec_long_nw(2),No_route_destination(3),Normal_call_clearing(16),User_busy(17),No_user_response(18),No_answer_user(19),Call_rejected(21),	Destination_number_changed(22),Caller_abandon(25),Improper_caller_response(26),Destination_out_order(27),Address_incomplete(28),Normal_UNSPECIFIED(31),Network_failure(38),	Temporary_failure(41),Switching_equipment_congestion(42),Requested_channel_unavailable(44),Resource_unavailable(47),Bearer_capability_unauthorized(57),Bearer_capability_unavailable(58),	Service_not_available(63),Bearer_capability_unimplemented(65),Service_not_implemented(79),Invalid_message(95),UncompatibleOrUnimplemented_message(97),	Reserved_unimplemented_accepted(99),Invalid_information_element(100),Reserved_unimplemented_dicarded(101),Protocol_error(111),Interworking_unspecified(127),	UnSpecified(-1);
	private int code;
	private CauseValEnum(int c) {
		code = c;
	}
	public int getCode() {
		return code;
	}
	public static CauseValEnum fromInt(int num) {
		switch (num) {
			case 1: { return Unallocated_number; }			case 2: { return No_route_spec_long_nw; }
			case 3: { return No_route_destination; }
			case 16: { return Normal_call_clearing; }
			case 17: { return User_busy; }
			case 18: {return No_user_response;}
			case 19: { return No_answer_user; }
			case 21: { return Call_rejected; }
			case 22: { return Destination_number_changed; }
			case 25: { return Caller_abandon; }
			case 26: { return Improper_caller_response; }
			case 27: { return Destination_out_order; }
			case 28: { return Address_incomplete; }
			case 31: { return Normal_UNSPECIFIED; }
			case 38: { return Network_failure; }			
			case 41: { return Temporary_failure; }
			case 42: { return Switching_equipment_congestion; }
			case 44: { return Requested_channel_unavailable; }
			case 47: { return Resource_unavailable; }
			case 57: { return Bearer_capability_unauthorized; }
			case 58: { return Bearer_capability_unavailable; }
			case 63: { return Service_not_available; }
			case 65: { return Bearer_capability_unimplemented; }
			case 79: { return Service_not_implemented; }
			case 95: { return Invalid_message; }
			case 97: { return UncompatibleOrUnimplemented_message; }
			case 99: { return Reserved_unimplemented_accepted; }
			case 100: { return Invalid_information_element; }
			case 101: { return Reserved_unimplemented_dicarded; }
			case 111: { return Protocol_error; }
			case 127: { return Interworking_unspecified; }
			default: { return UnSpecified; }
		}
	}
}
