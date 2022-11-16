package com.genband.tcap.provider;

import jain.protocol.ss7.tcap.JainTcapListener;
import jain.protocol.ss7.tcap.TimeOutEvent;

/**
 * Extension to the JainTcapListener interface.
 * 
 * Provides the following extensions to the JainTcapListener interface.
 * <li> Add one more api for sending some event on timeout </li>
 */

public interface TcapListener extends JainTcapListener{
	
    public void processTimeOutEvent(TimeOutEvent timeOutEvent);
    
    public String getInviteSessionId();
    
    public String getName();
    
    public String getVersion();
    
    public String getDisplayName() ;
    
    /**
     * this method is used by Jain tcap app to give call back  to service in case of FT
     * @param tcapSession
     */
    public void processTcapSessionActivationEvent(TcapSession tcapSession);
}
