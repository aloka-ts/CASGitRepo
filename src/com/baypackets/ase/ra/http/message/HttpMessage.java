package com.baypackets.ase.ra.http.message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.print.attribute.standard.Destination;
import javax.servlet.sip.SipApplicationSession;
import org.apache.log4j.Logger;

import com.baypackets.ase.container.AseIc;
import com.baypackets.ase.container.sip.SipApplicationSessionImpl;
import com.baypackets.ase.ra.http.HttpResourceAdaptor;
import com.baypackets.ase.resource.Message;
import com.baypackets.ase.resource.ResourceSession;
import com.baypackets.ase.spi.container.AbstractSasMessage;
import com.baypackets.ase.spi.container.SasMessage;
import com.baypackets.ase.spi.container.SasMessageContext;
import com.baypackets.ase.spi.container.SasProtocolSession;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;

@DefaultSerializer(ExternalizableSerializer.class)
public class HttpMessage extends AbstractSasMessage implements Message{
	
	/**
	 * 
	 */
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The type. */
	private int type;
	
	private transient HttpResourceAdaptor httpResourceAdaptor;
	
	/** The Constant logger. */
	private static Logger logger = Logger.getLogger(HttpMessage.class);
	
	/** The session. */
	private SasProtocolSession session;
	
	/** The Constant PROTOCOL. */
	private String PROTOCOL="HTTP";
	
	/** The message method. */
	private String method;
	
	private ArrayList<String> key;
    private ArrayList<String> value;
    private Map<String, ArrayList<String>> requestProperties;
	private Destination m_destination=  null;
	
	private Object message=null;
	
	private Random random = new Random();
	public HttpMessage(){
		super();
		requestProperties = new HashMap<String, ArrayList<String>>();
    	key = new ArrayList<String>();
    	value = new ArrayList<String>();
		if(logger.isDebugEnabled())
			logger.debug("Inside HttpMessage() constructor ");
	}
	
	public HttpMessage(int type) {
		if(logger.isDebugEnabled())
			logger.debug("Inside HttpMessage(int) constructor ");
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
		return this.session;
	}

	@Override
	public SasProtocolSession getProtocolSession(boolean create) {
		//if (create && this.context != null) {
		if (this.session==null && create && httpResourceAdaptor.getResourceContext() != null) {
			try {
				//this.session = this.context.getSessionFactory().createSession();
				this.session = httpResourceAdaptor.getResourceContext().getSessionFactory().createSession();
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
				httpResourceAdaptor.sendMessage(this);
			} catch (Exception e) {
				logger.error("send(): " + e);
				//ravi
				if(isDebugEnabled)
					logger.debug("httpresourceAdaptor:"+httpResourceAdaptor);
				throw new IOException(e);
			}
		}
		
	}

	@Override
	public void set(Object arg0) {
		boolean isInfoEnabled=logger.isInfoEnabled();
		this.message=arg0;
		if(isInfoEnabled)
			logger.info("set() called.::::"+ message);
		
	}

	@Override
	public Object get() {
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
	}


	/**
	 * @param EnumResourceAdaptor the HttpResourceAdaptor to set
	 */
	public void setHttpResourceAdaptor(HttpResourceAdaptor httpResourceAdaptor) {
		//ravi
		if(logger.isDebugEnabled())
			logger.debug("(set)httpresourceAdaptor:"+httpResourceAdaptor);
		this.httpResourceAdaptor = httpResourceAdaptor;
	}
	
	protected HttpResourceAdaptor getHttpResourceAdaptor(){
		return httpResourceAdaptor;
	}
	
	
	public void setHeader(String key, String value) {
		this.key.add(key);
		this.value.add(value);
	//   logger.error("ravi"+key+"and"+value);
		
	}
	
	public Map<String, ArrayList<String>> getRequestProperties(){
		
	     this.requestProperties.put("key", this.key);
	     this.requestProperties.put("value", this.value);
	 	 if(logger.isDebugEnabled()){
			logger.debug("request property added in map.");
		 }
		return this.requestProperties;
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
