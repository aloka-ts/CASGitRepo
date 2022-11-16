/*
 * TestContainer.java
 *
 * Created on August 27, 2004
 */
package com.baypackets.ase.sipconnector.test;

import java.io.IOException;
import java.util.EventObject;
import java.util.Iterator;

import javax.servlet.sip.ServletParseException;
import javax.servlet.sip.SipSession;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.baypackets.ase.common.AseBaseConnector;
import com.baypackets.ase.common.AseContainer;
import com.baypackets.ase.common.AseEvent;
import com.baypackets.ase.common.AseEventListener;
import com.baypackets.ase.common.AseMessage;
import com.baypackets.ase.container.exceptions.AseInvocationFailedException;
import com.baypackets.ase.sipconnector.AseCannotCancelException;
import com.baypackets.ase.sipconnector.AseConnectorSipFactory;
import com.baypackets.ase.sipconnector.AseOutOfSequenceException;
import com.baypackets.ase.sipconnector.AseSessionInvalidException;
import com.baypackets.ase.sipconnector.AseSipConnector;
import com.baypackets.ase.sipconnector.AseSipConstants;
import com.baypackets.ase.sipconnector.AseSipServletRequest;
import com.baypackets.ase.sipconnector.AseSipServletResponse;
import com.baypackets.ase.sipconnector.AseSipSession;
import com.baypackets.ase.sipconnector.AseStrayMessageException;
import com.baypackets.ase.spi.container.SasMessage;
import com.baypackets.bayprocessor.agent.MComponentState;

/**
 * This class is used for UT of SIP connector as a test container.
 *
 * @author Neeraj Jain
 */
class TestContainer
	implements AseContainer {
    
	TestContainer(String endA, String endB) {
		m_endA = new String(endA);
		m_endB = new String(endB);
	}

	//////////////////////// testing util methods /////////////////////////////

	AseSipServletRequest create_invite() {
		if(m_l.isInfoEnabled()) m_l.info("create_invite():enter");

		AseSipServletRequest req = null;

		try {
			req = (AseSipServletRequest)m_factory.createRequest(	null,
																	"INVITE",
																	m_endA,
																	m_endB);
		} catch(ServletParseException exp) {
			m_l.error("create_invite()", exp);
		}

		if(m_l.isInfoEnabled()) m_l.info("create_invite():exit");
		return req;
	}

	AseSipServletRequest create_cancel(AseSipServletRequest origInvite) {
		if(m_l.isInfoEnabled()) m_l.info("create_cancel(origInvite):enter");

		AseSipServletRequest cancel = null;

		try {
			cancel = (AseSipServletRequest)origInvite.createCancel();
		} catch(IllegalStateException exp) {
			m_l.error("create_cancel(origInvite)", exp);
		}

		if(m_l.isInfoEnabled()) m_l.info("create_cancel(origInvite):exit");
		return cancel;
	}

	void send_request(AseSipServletRequest request) {
	if(m_l.isInfoEnabled())
	m_l.info("send_request(request):enter");

		try {
			request.send();
		} catch(IllegalStateException exp) {
			m_l.error("send_request(request)", exp);
		} catch(IOException exp) {
			m_l.error("send_request(request)", exp);
		}

		if(m_l.isInfoEnabled()) m_l.info("send_request(request):exit");
	}

	void respond_to_invite(AseSipServletRequest req, int respCode) {
		if(m_l.isInfoEnabled()) m_l.info("respond_to_invite(req, " + respCode + "):enter");

		try {
			req.createResponse(respCode).send();
		} catch(IllegalArgumentException exp) {
			m_l.error("respond_to_invite(req, " + respCode + ")", exp);
		} catch(IllegalStateException exp) {
			m_l.error("respond_to_invite(req, " + respCode + ")", exp);
		} catch(IOException exp) {
			m_l.error("respond_to_invite(req, " + respCode + ")", exp);
		}

		if(m_l.isInfoEnabled()) m_l.info("respond_to_invite(req, " + respCode + "):exit");
	}

	void send_bye(AseSipServletRequest origInvite) {
		if(m_l.isInfoEnabled()) m_l.info("send_bye(origInvite):enter");

		SipSession session = origInvite.getSession();

		try {
			session.createRequest("BYE").send();
		} catch(IllegalStateException exp) {
			m_l.error("send_bye(origInvite)", exp);
		} catch(IOException exp) {
			m_l.error("send_bye(origInvite)", exp);
		}

		if(m_l.isInfoEnabled()) m_l.info("send_bye(origInvite):exit");
	}

	void respond_to_bye(AseSipServletRequest req, int respCode) {
	if(m_l.isInfoEnabled()) 	m_l.info("respond_to_bye(req, " + respCode + "):enter");

		try {
			req.createResponse(respCode).send();
		} catch(IllegalArgumentException exp) {
			m_l.error("respond_to_bye(req, " + respCode + ")", exp);
		} catch(IllegalStateException exp) {
			m_l.error("respond_to_bye(req, " + respCode + ")", exp);
		} catch(IOException exp) {
			m_l.error("respond_to_bye(req, " + respCode + ")", exp);
		}

		if(m_l.isInfoEnabled()) m_l.info("respond_to_bye(req, " + respCode + "):exit");
	}

	void send_ack(AseSipServletResponse response) {
		if(m_l.isInfoEnabled()) m_l.info("send_ack(response):enter");

		try {
			response.createAck().send();
		} catch(IllegalStateException exp) {
			m_l.error("send_ack(response)", exp);
		} catch(IOException exp) {
			m_l.error("send_ack(response)", exp);
		}

		if(m_l.isInfoEnabled()) m_l.info("send_ack(response):exit");
	}

	void receive_initial_invite(AseSipServletRequest request) {
		if(m_l.isInfoEnabled()) m_l.info("receive_initial_invite(AseSipServletRequest):enter");

		// Create session for initial invite
		AseSipSession session = m_factory.createSession();
		request.setAseSipSession(session);
		try {
			session.recvRequest(request);
		} catch(AseStrayMessageException exp) {
		if(m_l.isEnabledFor(Level.ERROR))
			m_l.error("passing request [call id = " + request.getCallId() + "] to default handler", exp);
		} catch(AseCannotCancelException exp) {
			if(m_l.isEnabledFor(Level.ERROR))
				m_l.error("call id = " + request.getCallId(), exp);
		} catch(AseOutOfSequenceException exp) {
			if(m_l.isEnabledFor(Level.ERROR))
				m_l.error("call id = " + request.getCallId(), exp);
		} catch(IllegalStateException exp) {
			if(m_l.isEnabledFor(Level.ERROR))
				m_l.error("call id = " + request.getCallId(), exp);
		} catch(AseSessionInvalidException exp) {
			if(m_l.isEnabledFor(Level.ERROR))
				m_l.error("call id = " + request.getCallId(), exp);
		} catch (Exception exp) {
			if(m_l.isEnabledFor(Level.ERROR))
				m_l.error("call id = " + request.getCallId(), exp);
      }

	if(m_l.isInfoEnabled())
	m_l.info("receive_initial_invite(AseSipServletRequest):exit");
	}

	/////////////////////////// container methods /////////////////////////////

    public void handleMessage(AseMessage message) {
		AseSipServletRequest request = null;
		AseSipServletResponse response = null;
		AseEvent event = null;

		switch(message.getMessageType()) {
			case AseMessage.MESSAGE:
				if(message.getMessage() instanceof AseSipServletRequest ){
					request = (AseSipServletRequest)message.getMessage();
					m_l.info("Received " + request.getMethod());
					if(request.getMethod() == "INVITE") {
						receive_initial_invite(request);
						m_l.info("Sending 200 OK");
						respond_to_invite(request, 200);
					}
				}
			break;
		}

		switch(m_testnum) {
			case 1:
			break;
		}
	}
    
    public String getName() {
		return "TestContainer";
	}
    
    public AseContainer getParent() {
		return null;
	}
    
    public void setParent(AseContainer parent) {
	}
    
    public void addChild(AseContainer child) {
	}
    
    public AseContainer findChild(String name) {
		return null;
	}
    
    public AseContainer[] findChildren() {
		return null;
	}
    
    public boolean removeChild(AseContainer child) {
		return true;
	}
    
    public boolean removeChild(String name) {
		return true;
	}

   public void registerConnector(AseBaseConnector connector) {
   }
    
	public void processMessage(SasMessage message) 
							throws AseInvocationFailedException {
	}
    
    public void handleEvent(EventObject event, AseEventListener listener) {
	}
    
    public Iterator getConnectors() {
		return null;
	}
    
	private String m_endA = null;

	private String m_endB = null;

	private static int	m_testnum = 0;

	AseConnectorSipFactory m_factory = null;

	private static Logger m_l = Logger.getLogger(TestContainer.class.getName());

	////////////////////////////// MAIN method ////////////////////////////////

	public static void main(String[] args) {
		if(args.length != 3) {
			m_l.error("Usage : TestContainer from to test#");
			System.exit(-1);
		}

		System.getProperties().setProperty(	AseSipConstants.SIPCONN_THREADNUM,
											"1");

		if(m_l.isInfoEnabled())
			m_l.info("Creating container");
		AseContainer container = new TestContainer(args[0], args[1]);

		if(m_l.isInfoEnabled())
			m_l.info("Creating sipconn");
		AseSipConnector sipconn = new AseSipConnector(container);

		if(m_l.isInfoEnabled())
			m_l.info("loading sipconn");
		sipconn.changeState(new MComponentState(MComponentState.LOADED));

		if(m_l.isInfoEnabled())
			m_l.info("running sipconn");
		sipconn.changeState(new MComponentState(MComponentState.RUNNING));

		// Now run the given test
		((TestContainer)container).m_factory =
								(AseConnectorSipFactory)sipconn.getFactory();

		((TestContainer)container).m_endA = new String(args[0]);
		((TestContainer)container).m_endB = new String(args[1]);
		((TestContainer)container).m_testnum = Integer.parseInt(args[2]);

		switch(m_testnum) {
			case 1:
			break;

		}

		if(m_l.isInfoEnabled())
			m_l.info("exiting from main()");
	} // main
}
