/**
 * 
 */
package com.genband.jain.protocol.ss7.tcap.router;


import org.bn.exceptions.EnumParamOutOfRangeException;

import jain.ASNParsingException;
import jain.CriticalityTypeException;
import jain.MandatoryParamMissingException;
import jain.ParameterOutOfRangeException;
import jain.protocol.ss7.SccpUserAddress;


/**
 * @author saneja
 *
 */
public interface TcapRoutingController {
	
	
	public TcapNextAppInfo getNextAppListener(byte[] initialRequest,SccpUserAddress origSUA) 
		throws  ASNParsingException, EnumParamOutOfRangeException, 
		MandatoryParamMissingException, ParameterOutOfRangeException, CriticalityTypeException;
	
	public TcapNextAppInfo getNextAppListener(String receivedDigits, SccpUserAddress sua);

	public TcapNextAppInfo getNextAppListener(byte[] parms,
			String opCode, String receivedDigits, SccpUserAddress sua) throws ASNParsingException, EnumParamOutOfRangeException, MandatoryParamMissingException, ParameterOutOfRangeException, CriticalityTypeException;



	public TcapNextAppInfo getNextAppListenerV2(byte[] initialRequest,
			String opCode,
			String receivedDigits,
			 String ssn,
			 int protocol,
			 int pointCode,
			 String serviceKey,
			 SccpUserAddress sua , int dialogueId) throws ASNParsingException, EnumParamOutOfRangeException, MandatoryParamMissingException, ParameterOutOfRangeException, CriticalityTypeException;

}
