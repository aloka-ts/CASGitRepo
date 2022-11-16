package com.baypackets.ase.ra.diameter.rf.stackif;


import org.apache.log4j.Logger;

import com.traffix.openblox.diameter.rf.generated.session.SessionListenerFactoryRfServer;
import com.traffix.openblox.diameter.rf.generated.session.SessionListenerRfServer;



public class SessionListenerFactoryRfServerImpl extends SessionListenerFactoryRfServer {
    static final Logger logger = Logger.getLogger(SessionListenerFactoryRfServerImpl.class);

    private SessionListenerRfServer listener;

    public SessionListenerFactoryRfServerImpl(RfStackServerInterfaceImpl shServerApplication){
        logger.debug("Creating SessionListenerFactoryRfServerImpl");
        listener = new SessionListenerRfServerImpl(shServerApplication);
    }

    public SessionListenerRfServer newInstance(){
        logger.debug("Creating new instance of SessionListenerRfServer");
        return listener;
    }
}