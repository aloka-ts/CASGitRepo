package com.agnity.win.enumdata;

public enum AccessDeniedReasonEnum {

	/*
	 * NonASNAccessDeniedReason (octet 1, bits A-H) Bits HG FE DC BA Value
	 * Meaning
	 * 
	 * 00 00 00 00 0 Not used.
	 * 
	 * 00 00 00 01 1 Unassigned directory number(the MS is not served by the
	 * accessed system).
	 * 
	 * 00 00 00 10 2 Inactive(the MS is not active in the accessed system and
	 * the HLR pointer to the MS?s VLR should be maintained). 00 00 00 11 3
	 * Busy(the MS is busy in the accessed system and cannot accept additional
	 * calls). 00 00 01 00 4 Termination Denied(terminations to this MS are not
	 * allowed). 00 00 01 01 5 No Page Response(the MS was paged by the accessed
	 * system but did not respond). 00 00 01 10 6 Unavailable (the MS is
	 * currently not available and the HLR pointer to the MS?s VLR should be
	 * maintained and the MS shall remain in the same state). 00 00 01 11 7
	 * Service Rejected by MS 00 00 10 00 8 Service Rejected by the system 00 00
	 * 10 01 9 Service Type Mismatch 00 00 10 10 10 Service Denied
	 */

	NOT_USED(0), UNASSIGNED_DIR_NO(1), INACTIVE(2), BUSY(3), TERMINATION_DENIED(
			4), NO_PAGE_RESPONSE(5), UNAVAILABLE(6), SERVICE_REJECTED_BY_MS(7), SERVICE_REJECTED_BY_SYSTEM(
			8), SERVICE_TYPE_MISMATCH(9), SERVICE_DENIED(10);

	private int code;

	private AccessDeniedReasonEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}

	public static AccessDeniedReasonEnum fromInt(int num) {
		switch (num) {
		case 0: {
			return NOT_USED;
		}
		case 1: {
			return UNASSIGNED_DIR_NO;
		}
		case 2: {
			return INACTIVE;
		}
		case 3: {
			return BUSY;
		}
		case 4: {
			return TERMINATION_DENIED;
		}
		case 5: {
			return NO_PAGE_RESPONSE;
		}
		case 6: {
			return UNAVAILABLE;
		}
		case 7: {
			return SERVICE_REJECTED_BY_MS;
		}
		case 8: {
			return SERVICE_REJECTED_BY_SYSTEM;
		}
		case 9: {
			return SERVICE_TYPE_MISMATCH;
		}
		case 10: {
			return SERVICE_DENIED;
		}
		default: {
			return null;
		}
		}
	}

}
