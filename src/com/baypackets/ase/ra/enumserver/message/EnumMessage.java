package com.baypackets.ase.ra.enumserver.message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.print.attribute.standard.Destination;
import javax.servlet.sip.SipApplicationSession;

import org.apache.log4j.Logger;

import com.baypackets.ase.cdr.CDRImpl;
import com.baypackets.ase.container.AseApplicationSession;
import com.baypackets.ase.container.AseContext;
import com.baypackets.ase.container.AseIc;
import com.baypackets.ase.container.sip.SipApplicationSessionImpl;
import com.baypackets.ase.ra.enumserver.EnumResourceAdaptor;
import com.baypackets.ase.resource.Message;
import com.baypackets.ase.resource.ResourceSession;
import com.baypackets.ase.sbb.CDR;
import com.baypackets.ase.spi.container.AbstractSasMessage;
import com.baypackets.ase.spi.container.SasMessageContext;
import com.baypackets.ase.spi.container.SasProtocolSession;
import com.baypackets.ase.util.Constants;

public class EnumMessage extends AbstractSasMessage implements Message{
	
	/**
	 * 
	 */
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The type. */
	private int type;
	
	private transient EnumResourceAdaptor enumResourceAdaptor;
	
	/** The Constant logger. */
	private static Logger logger = Logger.getLogger(EnumMessage.class);
	
	/** The session. */
	private SasProtocolSession session;
	
	/** The Constant PROTOCOL. */
	private String PROTOCOL="Enum";
	
	/** The message method. */
	private String method;
	
	private ArrayList<String> key;
    private ArrayList<String> value;
  
	private Destination m_destination=  null;
	
	private Message message=null;
	private int messageId;
	
	public int getMessageId() {
		return messageId;
	}

	public void setMessageId(int messageId) {
		this.messageId = messageId;
	}

	private Random random = new Random();
	public EnumMessage(){
		super();
		if(logger.isDebugEnabled())
			logger.debug("Inside EnumMessage() constructor ");
	}
	
	public EnumMessage(int type) {
		if(logger.isDebugEnabled())
			logger.debug("Inside EnumMessage(int) constructor ");
		this.type = type;
	}

	@Override
	public String getMethod() {
		return this.method;
	}

	@Override
	public String getProtocol() {
		return PROTOCOL;
	}

	@Override
	public SasProtocolSession getProtocolSession() {
		if(logger.isDebugEnabled())
			logger.debug("Inside EnumMessage() getProtocolSession " +session);
		return this.session;
	}

	@Override
	public SasProtocolSession getProtocolSession(boolean create) {
		//if (create && this.context != null) {
		if (this.session==null && create && enumResourceAdaptor.getResourceContext() != null) {
			try {
				this.session = enumResourceAdaptor.getResourceContext().getSessionFactory().createSession();
				this.session.setAttribute(CDR.class.getName(), getCDR());
			} catch (Exception e) {
				logger.error("getProtocolSession(): " + e);
				this.session = null;
			}
		}
		return this.session;
	}

	@Override
	public boolean isSecure() {
		return false;
	}

	@Override
	public void setMessagePriority(boolean arg0) {
		boolean isInfoEnabled=logger.isInfoEnabled();
		if(isInfoEnabled)
			logger.info("setMessagePriority() called.:::::Not Supported");
		
	}
    
	@Override
	public boolean getMessagePriority() {	
		return priorityMsg;
	}

	

	@Override
	public SipApplicationSession getApplicationSession() {
		SipApplicationSession appSession = null;
		if(this.getProtocolSession() != null){
			appSession = this.getProtocolSession().getApplicationSession();
		}
		return appSession;
	}
	
	
	/**
	 * Called by the "getAttribute" method to get the CDR object
	 * to be put into this session's attribute map.
	 */
	public  CDR getCDR() {
		
		if(logger.isDebugEnabled())
			logger.info("getCDR() called to get CDR ref");
		// Get the CDRContext of the app that this session is associated with...
		if(this.getApplicationSession()==null){
			if(logger.isDebugEnabled())
				logger.info("getCDR() called appsesion is currently null will add cdr ref later on");
			return null;
		}
		AseApplicationSession appSession = (AseApplicationSession)this.getApplicationSession();
		AseContext app = appSession.getContext();
		CDR cdr = app.getCDRContext(this.getSession().getId()).createCDR();

		// Populate the CDR with the initial values...
		//Marking it as default CDR
		cdr.set(CDR.DEFAULT_CDR,CDR.DEFAULT_CDR);
	//	cdr.set(CDR.CORRELATION_ID, appSession.getAttribute(Constants.CORRELATION_ID));
		cdr.set(CDR.SESSION_ID, this.getSession().getId());
///		cdr.set(CDR.ORIGINATING_NUMBER, ((SipURI)m_localParty.getURI()).getUser()); /coomenting for axtel it is coming as null for tcap
//		cdr.set(CDR.TERMINATING_NUMBER, ((SipURI)m_remoteParty.getURI()).getUser());
		cdr.set(CDR.CALL_START_TIMESTAMP, String.valueOf(this.getSession().getCreationTime()));
//		cdr.set(CDR.BILL_TO_NUMBER, ((SipURI)m_localParty.getURI()).getUser());

		// Special case for handling the default CDR implementation:
		// We associate the host and app name with the CDR here so that the 
		// CDRContext can be looked up and re-associated with the CDR after 
		// it has been replicated.
		// We also set a flag indicating if the CDR is associated with a
		// distributable app or not.  If it is distributable, only Serializable
		// attribute values will be allowed to be set on it.
		if (cdr instanceof CDRImpl) {
			((CDRImpl)cdr).setHostName(app.getParent().getName());
			((CDRImpl)cdr).setAppName(app.getName());
			((CDRImpl)cdr).setDistributable(app.isDistributable());
		}
		if(logger.isDebugEnabled())
			logger.info("getCDR() Leaving with ref "+cdr);
		return cdr;
	}


	@Override
	public ResourceSession getSession() {
		return (ResourceSession)this.getProtocolSession();
		}

	@Override
	public int getType() {
		return this.type;
	}
	
	public void setType(int type){
		this.type=type;
	}

	@Override
	public void send() throws IOException {
		boolean isDebugEnabled=logger.isDebugEnabled();
		if(isDebugEnabled)
			logger.debug("send() called.");
		SasMessageContext context = this.getMessageContext();
		if(context != null){
			context.sendMessage(this);
		} else {
			if(isDebugEnabled)
				logger.debug("Sending to resource adaptor directly.");
			try {
				enumResourceAdaptor.sendMessage(this);
			} catch (Exception e) {
				logger.error("send(): " + e);
				//ravi
				if(isDebugEnabled)
					logger.debug("EnumresourceAdaptor:"+enumResourceAdaptor);
				throw new IOException(e);
			}
		}
		
	}

	@Override
	public void set(Object arg0) {
		boolean isInfoEnabled=logger.isInfoEnabled();
		this.message=(Message)arg0;
		if(isInfoEnabled)
			logger.info("set() called.::::"+ message);
		
	}

	@Override
	public Message get() {
		boolean isInfoEnabled=logger.isInfoEnabled();
		if(isInfoEnabled)
			logger.info("get() called.:::::"+ this.message);
		return this.message;
	}
	
	@Override
	public Object getDestination() {
		boolean isInfoEnabled=logger.isInfoEnabled();
		if(isInfoEnabled)
			logger.info("getDestination() called.");
		return m_destination;
	}

	@Override
	public void setDestination(Object destn) {
		boolean isInfoEnabled=logger.isInfoEnabled();
		if(isInfoEnabled)
			logger.info("setDestination() called.");
		this.m_destination=(Destination)destn;
		
	}
	
	/**
	 * Sets the protocol session.
	 *
	 * @param session the new protocol session
	 */
	public void setProtocolSession(SasProtocolSession session) {
		this.session = session;	
		if(logger.isInfoEnabled())
			logger.info("setProtocolSession() called." + session);
		this.session.setAttribute(CDR.class.getName(), getCDR());
	}


	/**
	 * @param EnumResourceAdaptor the EnumResourceAdaptor to set
	 */
	public void setEnumResourceAdaptor(EnumResourceAdaptor enumResourceAdaptor) {
		if(logger.isDebugEnabled())
			logger.debug("(set)EnumresourceAdaptor:"+enumResourceAdaptor);
		this.enumResourceAdaptor = enumResourceAdaptor;
	}
	
	protected EnumResourceAdaptor getEnumResourceAdaptor(){
		return enumResourceAdaptor;
	}
	
	
	
	
	public int getWorkQueue() {
		boolean isDebugEnabled = logger.isDebugEnabled();
		if(isDebugEnabled){
			logger.debug("getWorkQueue() called.");
		}
		SipApplicationSessionImpl appSession = (SipApplicationSessionImpl) this.getApplicationSession(); 
		AseIc ic = null;
		if (appSession != null){
			ic = appSession.getIc();
			if (ic != null){
				if (isDebugEnabled) {
					logger.debug("getWorkQueue(): Returning value from IC: " + ic.getWorkQueue());
				}
				return ic.getWorkQueue();
			}
		}
		int value = this.random.nextInt();

		if (isDebugEnabled) {
			logger.debug("getWorkQueue(): Returning value: " + value);
		}
		return value;

	}

}
