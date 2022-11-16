package com.baypackets.ase.teststubs;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.baypackets.ase.common.AseBaseConnector;
import com.baypackets.ase.common.AseContainer;
import com.baypackets.ase.common.AseMessage;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.spi.container.SasProtocolSession;
import com.baypackets.ase.util.Constants;
import com.baypackets.bayprocessor.agent.MComponentState;
import com.baypackets.bayprocessor.agent.OperationType;
import com.baypackets.bayprocessor.slee.common.Pair;


public class DummySipConnector
	extends AseBaseConnector 

{

  public DummySipConnector() {
    m_l.log(Level.INFO, "Going to initialize self");
    initialize((AseContainer)Registry.lookup(Constants.NAME_ENGINE));
  }


	public void handleMessage(AseMessage message) {
		m_l.info("handleMessage called "+message);
	}


	public java.lang.String getProtocol() {
		m_l.log(Level.ALL,"getProtocol()");
		return "SIP/2.0";
	}

	public java.lang.Object getFactory() {
		m_l.log(Level.ALL,"getFactory()");
		return null;
	}

	/**
	 * This method is notification from container about addition of given
	 * SIP session. It adds the session to dialog manager.
	 *
	 * @param session protocol session to be added
	 */
	public void addSession(SasProtocolSession session) {
		m_l.log(Level.INFO,"addSession(AseProtocolSession) "+  session);

		m_l.log(Level.ALL,"addSession(AseProtocolSession):exit");
	}

	/**
	 * This method is notification from container about termination of given
	 * SIP session. It removes the session from dialog manager.
	 *
	 * @param session protocol session to be removed
	 */
	public void removeSession(SasProtocolSession session) {
		m_l.log(Level.INFO,"removeSession(AseProtocolSession) "+  session);

		m_l.log(Level.ALL,"removeSession(AseProtocolSession):exit");
	}

	/**
	 * Creates SIP protocol connector specific dispatcher object
	 * thread pool and initializes super class.
	 *
	 * @param container reference to container instance
	 */
	protected void initialize(AseContainer	container) {
		super.initialize(container);
                this.container = container;
		m_l.log(Level.ALL,"initialize(AseContainer):enter");
		if (m_l.isInfoEnabled()) 
		m_l.log(Level.INFO, "Going to initialize super class");

	}

        public void sendToContainer (AseMessage msg) 
        {
          container.handleMessage(msg);          
        }

	public void changeState(MComponentState componentState){}

	public void updateConfiguration(Pair[]	configData, OperationType opType){}



	private static Logger m_l = Logger.getLogger(DummySipConnector.class.getName());
        private AseContainer container;

	/////////////////////////////////// UT code ///////////////////////////////

	public static void main(String[] args) {

		DummySipConnector sipconn = new DummySipConnector();

		// crete an AseMessage with AseEvent
		if (m_l.isInfoEnabled()) 
		m_l.log(Level.INFO, "Creating test ASE message with ase event");
		//AseEvent event = new AseEvent(1, new AseEventSCListener(), "TestNeerajEvent");

	}
}
