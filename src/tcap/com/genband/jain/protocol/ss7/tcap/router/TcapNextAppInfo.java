/**
 * 
 */
package com.genband.jain.protocol.ss7.tcap.router;

/**
 * @author saneja
 *
 */
public class TcapNextAppInfo {
	
	
	private String nextAppInfo;
	
	private boolean isServiceKey;

	/**
	 * @param nextAppInfo
	 * @param isServiceKey
	 */
	public TcapNextAppInfo(String nextAppInfo, boolean isServiceKey) {
		super();
		this.setNextAppInfo(nextAppInfo);
		this.setServiceKey(isServiceKey);
	}
	/**
	 * @param nextAppInfo the nextAppInfo to set
	 */
	private void setNextAppInfo(String nextAppInfo) {
		this.nextAppInfo = nextAppInfo;
	}


	/**
	 * @return the nextAppInfo
	 */
	public String getNextAppInfo() {
		return nextAppInfo;
	}


	/**
	 * @param isServiceKey the isServiceKey to set
	 */
	private void setServiceKey(boolean isServiceKey) {
		this.isServiceKey = isServiceKey;
	}


	/**
	 * @return the isServiceKey
	 */
	public boolean isServiceKey() {
		return isServiceKey;
	}

}
