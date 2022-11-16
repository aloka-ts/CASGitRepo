/**
 * Enum maps conncetion method code to connection methods
 */
package com.baypackets.ase.ra.telnetssh.configmanager;


/**
 * @author saneja
 *
 */
public enum ConnectionMethodEnum {
	TELNET(1),SSH(2);
	
	private ConnectionMethodEnum(int i){
		this.code=i;
	}
	private int code;

	public int getCode() {
		return code;
	}
	
	public static ConnectionMethodEnum fromInt(int num) {
		ConnectionMethodEnum connMethod=TELNET;
		switch (num) {
			case 1: { 
				connMethod= TELNET; 
				break;
			}
			case 2: { 
				connMethod= SSH;
				break;
			}
		}//@End Switch
		return connMethod;
	}
}

