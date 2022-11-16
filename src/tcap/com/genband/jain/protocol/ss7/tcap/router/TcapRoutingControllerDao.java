package com.genband.jain.protocol.ss7.tcap.router;

import javax.naming.NamingException;

public interface TcapRoutingControllerDao {
	
	/**
	 * finds application name for SIP-T call.
	 * @param serviceKey
	 * @param originatingNumber
	 * @param terminatingNumber
	 * @return appname as String
	 * @throws Throwable
	 */
	public String findApplicationName(int serviceKey,String originatingNumber,String terminatingNumber) throws Throwable;
	
	
	public  void init(String procedureName) throws NamingException;
}
