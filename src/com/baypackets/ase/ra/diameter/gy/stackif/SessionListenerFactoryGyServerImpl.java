package com.baypackets.ase.ra.diameter.gy.stackif;
import org.apache.log4j.Logger;

import com.traffix.openblox.diameter.gy.generated.session.SessionListenerFactoryGyServer;
import com.traffix.openblox.diameter.gy.generated.session.SessionListenerGyServer;



public class SessionListenerFactoryGyServerImpl extends SessionListenerFactoryGyServer {
    static final Logger logger = Logger.getLogger(SessionListenerFactoryGyServerImpl.class);

    private SessionListenerGyServer listener;

    public SessionListenerFactoryGyServerImpl(GyStackServerInterfaceImpl roServerApplication){
        logger.debug("Creating SessionListenerFactoryGyServerImpl");
        listener = new SessionListenerGyServerImpl(roServerApplication);
    }

    public SessionListenerGyServer newInstance(){
        logger.debug("Creating new instance of SessionListenerRoServer");
        return listener;
    }
}