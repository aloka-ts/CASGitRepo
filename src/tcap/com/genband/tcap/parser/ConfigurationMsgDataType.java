package com.genband.tcap.parser;

import jain.protocol.ss7.SccpUserAddress;

import java.util.LinkedList;
import java.util.List;

/**
 *	Configuration message (In SIP INVITE)
 *  <CONFIGURATION><LENGTH><VALUE><ORIG_SUA><LENGTH><VALUE><ORIG_SUA><LENGTH><VALUE>

 */
public class ConfigurationMsgDataType {

	List<SccpUserAddress> origSua = new LinkedList<SccpUserAddress>();

	public List<SccpUserAddress> getOrigSua() {
		return origSua;
	}

	public void setOrigSua(List<SccpUserAddress> origSua) {
		this.origSua = origSua;
	}

	@Override
	public String toString() {
		return "ConfigurationMsgDataType [origSua=" + origSua + "]";
	}
	
	
}
