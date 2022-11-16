/**
 * 
 */
package com.baypackets.ase.ra.telnetssh.ls;

/**
 * @author saneja
 *
 */
public enum CommandStatus {
	
	SEND_SUCCESS(0),
	SEND_FAIL(1),
	SEND_ERROR(2);
	
	private int code;

	private CommandStatus(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}

}
