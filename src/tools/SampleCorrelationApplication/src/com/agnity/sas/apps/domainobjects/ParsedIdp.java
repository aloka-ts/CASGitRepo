package com.agnity.sas.apps.domainobjects;

import com.genband.inap.datatypes.CallingPartyNum;

public class ParsedIdp {
	
	

	public ParsedIdp(int srvKey, CallingPartyNum clpNum, String callingParty, String ttcContarctorNum,
			String ttcCalledINNum) {
		this.srvKey=srvKey;
		this.callingParty=callingParty;
		this.ttcContarctorNum=ttcContarctorNum;
		this.ttcCalledINNum=ttcCalledINNum;
		this.clpNum=clpNum;
	}

	/**
	 * @return the srvKey
	 */
	public int getSrvKey() {
		return srvKey;
	}
	/**
	 * @param srvKey the srvKey to set
	 */
	public void setSrvKey(int srvKey) {
		this.srvKey = srvKey;
	}
	/**
	 * @return the callingParty
	 */
	public String getCallingParty() {
		return callingParty;
	}
	/**
	 * @param callingParty the callingParty to set
	 */
	public void setCallingParty(String callingParty) {
		this.callingParty = callingParty;
	}
	/**
	 * @return the ttcContarctorNum
	 */
	public String getTtcContarctorNum() {
		return ttcContarctorNum;
	}
	/**
	 * @param ttcContarctorNum the ttcContarctorNum to set
	 */
	public void setTtcContarctorNum(String ttcContarctorNum) {
		this.ttcContarctorNum = ttcContarctorNum;
	}
	/**
	 * @return the ttcCalledINNum
	 */
	public String getTtcCalledINNum() {
		return ttcCalledINNum;
	}
	/**
	 * @param ttcCalledINNum the ttcCalledINNum to set
	 */
	public void setTtcCalledINNum(String ttcCalledINNum) {
		this.ttcCalledINNum = ttcCalledINNum;
	}

	/**
	 * @param clpNum the clpNum to set
	 */
	public void setClpNum(CallingPartyNum clpNum) {
		this.clpNum = clpNum;
	}

	/**
	 * @return the clpNum
	 */
	public CallingPartyNum getClpNum() {
		return clpNum;
	}

	private int srvKey;
	private String callingParty;
	private String ttcContarctorNum;
	private String ttcCalledINNum;
	private CallingPartyNum clpNum;
}
