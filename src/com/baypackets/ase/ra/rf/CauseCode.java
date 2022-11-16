package com.baypackets.ase.ra.rf;

import java.io.Serializable;
import org.apache.log4j.Logger;

/**
 * This class defines the Cause-Code AVP that is part of an accounting request
 * according to 3GPP TS 32.299 V6.5.0
 *
 * @author Prashant Kumar
 *
 */

public class CauseCode implements Serializable  
{

	public static int END_OF_SUBSCRIBE_DIALOG = com.condor.chargingcommon.CauseCode.END_OF_SUBSCRIBE_DIALOG;
	public static int END_REGISTER_DIALOG = com.condor.chargingcommon.CauseCode.END_REGISTER_DIALOG ;
	public static int INTERNAL_ERROR = com.condor.chargingcommon.CauseCode.INTERNAL_ERROR ;
	public static int NORMAL_END_OF_SESSION = com.condor.chargingcommon.CauseCode.NORMAL_END_OF_SESSION ;
	public static int SUCESSFUL_TRANSACTION = com.condor.chargingcommon.CauseCode.SUCESSFUL_TRANSACTION ;
	public static int UNSPECIFIED_ERROR = com.condor.chargingcommon.CauseCode.UNSPECIFIED_ERROR ;
	public static int UNSUCESSFUL_SESSION_SETUP = com.condor.chargingcommon.CauseCode.UNSUCESSFUL_SESSION_SETUP ;
	

	public CauseCode()
	{

	}		
	
}
