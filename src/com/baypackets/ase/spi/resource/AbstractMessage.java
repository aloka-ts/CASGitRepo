package com.baypackets.ase.spi.resource;

import java.io.IOException;

import javax.servlet.sip.SipApplicationSession;

import com.baypackets.ase.resource.Message;
import com.baypackets.ase.resource.ResourceSession;
import com.baypackets.ase.spi.container.AbstractSasMessage;
import com.baypackets.ase.spi.container.SasMessageContext;

import com.esotericsoftware.kryo.DefaultSerializer;
/**
 * This class provides the basic implementation of the interface.
 * <code>com.baypackets.ase.resource.Message</code>.
 * 
 * This class also extend the <code>com.baypackets.ase.spi.container.AbstractSasMessage</code>
 * class so that it can be dispatched to the appplication using the SAS container.
 * 
 * The resource adaptor implementations can extend this class to provide their
 * message implementation. Or they can also directly implement the above interfaces.
 * 
 * The functionalities implemented for each of the methods in this class 
 * are provided at the method description. 
 */
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;

@DefaultSerializer(ExternalizableSerializer.class)
public abstract class AbstractMessage extends AbstractSasMessage 
					implements Message{

	private Object content;
	private int type;
	
	/**
	 * Returns the Content of this message.
	 */
	public Object get() {
		return this.content;
	}

	/**
	 * Sets the content of this message.
	 */
	public void set(Object content) {
		this.content = content;
	}
	
	/**
	 * Returns the Application Session associated with this message.
	 * This method would return NULL, in case of Sessionless messages.
	 */
	public SipApplicationSession getApplicationSession() {
		SipApplicationSession appSession = null;
		if(this.getProtocolSession() != null){
			appSession = this.getProtocolSession().getApplicationSession();
		}
		return appSession;
	}

	/**
	 * Returns the ResourceSession object associated with this message.
	 * This method would return NULL in case of the Sessionless messages.
	 */
	public ResourceSession getSession() {
		return (ResourceSession) this.getProtocolSession();
	}

	/**
	 * Sends this message using the ResourceAdaptor object.
	 * This method will get the resource adaptor object using the getMessageSource() method.
	 * If the getMessageSource() method returns an Object other tahn the ResourceAdaptor,
	 * this method will not send out the message. 
	 * 
	 * So the sub-class implementations should take care of this method. 
	 */
	public void send() throws IOException {
		SasMessageContext context = this.getMessageContext();
		if(context != null){
			context.sendMessage(this);
		}
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	/**
         * Sets the priority Message Flag for this message.
         */
        public void setMessagePriority(boolean priority)	{
		priorityMsg = priority;
	}
                                                                                                                             
        /**
         * Returns the priority Message Flag for this message.
         */
        public boolean getMessagePriority()	{
		return priorityMsg;
	}

}
