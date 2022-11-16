/*
 * Created on Aug 30, 2004
 *
 */
package com.baypackets.ase.container.sip;

import java.util.Iterator;

import javax.servlet.sip.SipFactory;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.baypackets.ase.common.AseBaseConnector;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.container.AseApplicationSession;
import com.baypackets.ase.container.AseContext;
import com.baypackets.ase.container.AseEngine;
import com.baypackets.ase.container.AseProtocolAdapter;
import com.baypackets.ase.sipconnector.AseConnectorSipFactory;
import com.baypackets.ase.util.Constants;

/**
 * @author Ravi
 */
public class SipProtocolAdapter implements AseProtocolAdapter {
	
	//private SipFactory factory = null;
   private static Logger _logger = Logger.getLogger(SipProtocolAdapter.class);
	
	/* (non-Javadoc)
	 * @see com.baypackets.ase.container.AseProtocolAdapter#createApplicationSession(com.baypackets.ase.container.AseContext)
	 */
	public AseApplicationSession createApplicationSession(AseContext context) {
		return new SipApplicationSessionImpl(context);
	}

	/* (non-Javadoc)
	 * @see com.baypackets.ase.container.AseProtocolAdapter#getConnector()
	 */
	public AseBaseConnector getConnector() {
		AseBaseConnector connector = null;
		AseEngine engine = (AseEngine)Registry.lookup(Constants.NAME_ENGINE);
		Iterator iterator = engine.getConnectors();
		for(;iterator != null && iterator.hasNext();){
			connector = (AseBaseConnector) iterator.next();
			if(connector.getProtocol().equals(Constants.PROTOCOL_SIP_2_0)){
				return connector;
			}
		}
		return null;
	}

	//BPUsa06771 == Need to create a factory instance for each application context. 
	//Changed this method from getFactory to createFactory....
	public Object createFactory(AseContext context){
		Object factory = null;
      		if (_logger.isInfoEnabled()) {
         		_logger.info("Entering SipProtocolAdapter.createFactory...");
      		}

		//Get the connector and create the SipFactory object
		AseBaseConnector connector = this.getConnector();
		if(connector != null){
			factory = new SipFactoryImpl((AseConnectorSipFactory)connector.getFactory(), context);
		}else{
      			if (_logger.isInfoEnabled()) {
               			_logger.info("Null connector instance");
      			}
		}	
		return factory;		
	}
}
