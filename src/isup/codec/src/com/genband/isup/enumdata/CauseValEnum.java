package com.genband.isup.enumdata;

/**
 * Enum for Cause Value 
 * @author vgoel
 *
 */

public enum CauseValEnum {

	/**
	 * 1-Unallocated Number
	 * 2-No route to the specific long-distance network
	 * 3-No route to destination
	 * 4-Transmit particular audible tone
	 * 5-Mis-dialling of trunk prefix
	 * 6-Channel unacceptable
	 * 7-Call awarded and being delivered in an established channel
	 * 8-Preemption
	 * 9-Preemption - circuit reserved for reuse
	 * 16-Normal call clearing
	 * 17-User busy
	 * 18-No User response
	 * 19-No answer from user (user alerted)
	 * 20-Subscriber absent
	 * 21-Call rejected
	 * 22-Destination subscriber number changed
	 * 23-Redirection to new destination
	 * 26-Non-selected user clearing
	 * 27-Destination out of order
	 * 28-Invalid number format (address incomplete)
	 * 29-Facility reject
	 * 30-Response to STATUS ENQUIRY [This cause is included in the STATUS message]
	 * 31-Normal, unspecified
	 * 34-No circuit/channel available
	 * 38-Network failure
	 * 39-Permanent frame mode connection out-of-service [This cause is included in a STATUS message]
	 * 40-Permanent frame mode connection operational [This cause is included in a STATUS message]
	 * 41-Temporary failure
	 * 42-Switching equipment congestion
	 * 43-Access information discarded
	 * 44-Requested circuit/channel not available
	 * 46-Precedence call blocked
	 * 47-Resource unavailable, unspecified
	 * 49-Quality of Service not available
	 * 50-Requested facility not contracted
	 * 53-Outgoing calls barred within CUG
	 * 55-Call termination inside CUG prohibited
	 * 57-Bearer Capability not authorized
	 * 58-Bearer Capability not available
	 * 62-Inconsistency in designated outgoing access information and subscriber class
	 * 63-Service or option not available,unspecified
	 * 65-Bearer Capability not implemented
	 * 66-Channel type not implemented
	 * 69-Unprovided facility requested
	 * 70-Only capability for transmitting control digital information available
	 * 79-Service or Option not implemented, unspecified
	 * 81-Invalid call reference value
	 * 82-Identified channel does not exist
	 * 83-A suspended call exists, but this call identity does not
	 * 84-Call identity in use
	 * 85-No call suspended
	 * 86-Call having the requested call identity has been cleared
	 * 87-User not CUG member
	 * 88-Incompatible destination
	 * 90-Non-existent CUG
	 * 91-Invalid long-distance network selected
	 * 95-Invalid message, unspecified
	 * 96-Mandatory information element is missing
	 * 97-Message type undefined or unprovided
	 * 98-Message not compatible with call state or message type non-existent or not implemented
	 * 99-Information element or parameter undefined or unprovided
	 * 100-Invalid information element contents
	 * 101-Message not compatible with call state
	 * 102-Recovery on timer expiry
	 * 103-Undefined or unprovided parameter passed
	 * 110-Message with unrecognized parameter discarded
	 * 111-Protocol error, unspecified
	 * 127-Other interworking class
	 */
	Unallocated_number(1), No_route_spec_long_nw(2), No_route_destination(3), 
	Transmit_spec_aud_tone(4), Misdialed_trunk_prefix(5), Channel_not_accepted(6), Call_delivered_established_channel(7),
	Preemption(8), Preemption_circuit_reserved_reuse(9), Normal_call_clearing(16), 
	User_busy(17), No_user_response(18),No_answer_user(19), Subscriber_absent(20), Call_rejected(21), 
	Destination_number_changed(22), Redirection_new_destination(23), User_not_selected(26), Destination_out_order(27), 
	Invalid_number_format(28), Facility_reject(29), Status_enquiry_response(30), Normal_UNSPECIFIED(31), No_circuit_available(34),
	Network_failure(38), Permanent_connection_outofservice(39), Permanent_connection_operational(40), Temporary_failure(41), Switching_equipment_congestion(42), 
	Access_information_discarded(43), Requested_channel_unavailable(44), Precedence_call_blocked(46), Resource_unavailable(47), 
	QoS_unavailable(49), Requested_facility_not_contracted(50), Outgoing_calls_barred_CUG(53),  
	Call_term_CUG_prohibited(55), Bearer_capability_unauthorized(57), Bearer_capability_unavailable(58), 
	Outgoing_access_info_inconsistence(62), Service_not_available(63), Bearer_capability_unimplemented(65), 
	Channel_type_unimplemented(66), Requested_facility_not_provided(69),
	Restricted_digital_info_bearer_capability_supported(70), Service_not_implemented(79), 
	Invalid_call_reference(81), Requested_channel_not_activated(82), CallId_not_matched_SuspendedCalls(83), 
	CallId_in_use(84), No_call_suspended(85), Call_with_id_already_cleared(86), User_not_cug_member(87), 
	Incompatible_destination(88), CUG_not_available(90), Invalid_long_distance_network(91), Invalid_message(95), 
	Mandatory_information_element_missing(96), Unprovided_message_type (97), UncompatibleOrUnimplemented_message(98), 
	Unprovided_parameter (99),Invalid_information_element(100),Uncompatible_message(101), Timer_expired(102),
	Unprovided_parameter_passed(103), Message_with_unrecognized_parameter(110), Protocol_error(111), 
	Interworking_unspecified(127), UnSpecified (-1) ;
		
	private int code;

	private CauseValEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static CauseValEnum fromInt(int num) {
		switch (num) {
			case 1: { return Unallocated_number; }
			case 2: { return No_route_spec_long_nw; }
			case 3: { return No_route_destination; }
			case 4: { return Transmit_spec_aud_tone; }
			case 5: { return Misdialed_trunk_prefix; }
			case 6: { return Channel_not_accepted; }
			case 7: { return Call_delivered_established_channel; }
			case 8: { return Preemption; }
			case 9: { return Preemption_circuit_reserved_reuse; }
			case 16: { return Normal_call_clearing; }
			case 17: { return User_busy; }
			case 18: {return No_user_response;}
			case 19: { return No_answer_user; }
			case 20: { return Subscriber_absent; }
			case 21: { return Call_rejected; }
			case 22: { return Destination_number_changed; }
			case 23: { return Redirection_new_destination; }
			case 26: { return User_not_selected; }
			case 27: { return Destination_out_order; }
			case 28: { return Invalid_number_format; }
			case 29: { return Facility_reject; }
			case 30: { return Status_enquiry_response; }
			case 31: { return Normal_UNSPECIFIED; }
			case 34: { return No_circuit_available; }
			case 38: { return Network_failure; }
			case 39: { return Permanent_connection_outofservice; }
			case 40: { return Permanent_connection_operational; }
			case 41: { return Temporary_failure; }
			case 42: { return Switching_equipment_congestion; }
			case 43: { return Access_information_discarded; }
			case 44: { return Requested_channel_unavailable; }
			case 46: { return Precedence_call_blocked; }
			case 47: { return Resource_unavailable; }
			case 49: { return QoS_unavailable; }
			case 50: { return Requested_facility_not_contracted; }
			case 53: { return Outgoing_calls_barred_CUG; }
			case 55: { return Call_term_CUG_prohibited; }
			case 57: { return Bearer_capability_unauthorized; }
			case 58: { return Bearer_capability_unavailable; }
			case 62: { return Outgoing_access_info_inconsistence; }
			case 63: { return Service_not_available; }
			case 65: { return Bearer_capability_unimplemented; }
			case 66: { return Channel_type_unimplemented; }
			case 69: { return Requested_facility_not_provided; }
			case 70: { return Restricted_digital_info_bearer_capability_supported; }
			case 79: { return Service_not_implemented; }
			case 81: { return Invalid_call_reference; }
			case 82: { return Requested_channel_not_activated; }
			case 83: { return CallId_not_matched_SuspendedCalls; }
			case 84: { return CallId_in_use; }
			case 85: { return No_call_suspended; }
			case 86: { return Call_with_id_already_cleared; }
			case 87: { return User_not_cug_member; }
			case 88: { return Incompatible_destination; }
			case 90: { return CUG_not_available; }
			case 91: { return Invalid_long_distance_network; }
			case 95: { return Invalid_message; }
			case 96: { return Mandatory_information_element_missing; }
			case 97: { return Unprovided_message_type; }
			case 98: { return UncompatibleOrUnimplemented_message; }
			case 99: { return Unprovided_parameter; }
			case 100: { return Invalid_information_element; }
			case 101: { return Uncompatible_message; }
			case 102: { return Timer_expired; }
			case 103: { return Unprovided_parameter_passed; }
			case 110: { return Message_with_unrecognized_parameter; }
			case 111: { return Protocol_error; }
			case 127: { return Interworking_unspecified; }
			default: { return UnSpecified; }
		}
	}
}


