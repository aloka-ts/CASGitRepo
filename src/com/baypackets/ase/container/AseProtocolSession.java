/*
 * Created on Aug 16, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.baypackets.ase.container;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Iterator;
import javax.servlet.ServletException;
import java.util.HashMap;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.baypackets.ase.container.exceptions.AseInvocationFailedException;
import com.baypackets.ase.replication.ReplicatedMessageHolder;
import com.baypackets.ase.ocm.TimeMeasurement;
import com.baypackets.ase.sipconnector.AseSipServletMessage;
import com.baypackets.ase.spi.container.AbstractProtocolSession;
import com.baypackets.ase.spi.container.SasMessage;
import com.baypackets.ase.spi.replication.ReplicationEvent;
import com.baypackets.ase.spi.replication.ReplicationSet;
import com.baypackets.ase.util.StringManager;


/**
 * @author Ravi
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

@DefaultSerializer(ExternalizableSerializer.class)
public abstract class AseProtocolSession extends AbstractProtocolSession {
	private static final long serialVersionUID = 384574942418488843L;
	private static Logger logger = Logger.getLogger(AseProtocolSession.class);
	private static StringManager _strings = StringManager.getInstance(AseProtocolSession.class.getPackage());
	private static final String CHAIN_INFO = "CHAIN_INFO".intern();
	
	private AseChainInfo chainInfo;
	//private HashMap attributes;

	 /**
	  * Constructor for Protocol Session. 
	  * @param id - Unique identifier for this session
	  */
	 public AseProtocolSession(String id) {
		 super(id);
         this.chainInfo = new AseChainInfo();
         this.chainInfo.setReplicableId(CHAIN_INFO);
         this.setReplicable(this.chainInfo);
	 }
	
	 public Object clone() throws CloneNotSupportedException {
        if (logger.isDebugEnabled()) {
            logger.debug("Entering AseProtocolSession clone()");
        }

		  AseProtocolSession clonedSession = (AseProtocolSession)(super.clone());
		  clonedSession.chainInfo = (AseChainInfo)this.chainInfo.clone();
		  clonedSession.chainInfo.setReplicableId(CHAIN_INFO);
		  clonedSession.setReplicable(clonedSession.chainInfo);
		  
        if (logger.isDebugEnabled()) {
            logger.debug("Leaving AseProtocolSession clone()");
        }
	 
		  return clonedSession;
	 }
	 

    public void sendReplicationEvent(ReplicationEvent event) {
    	if(this.getReplicationListener() != null){
			ArrayList list = new ArrayList(1);
			list.add(this.appSession.getApplication().getId());
			event.setAppNames(list);
    		this.getReplicationListener().handleReplicationEvent(event);
    	}
    }	

	public Object getAttribute(String name) {
		super.getAttribute(name);
		
		//Get the attribute from the map
        Object value = super.getAttribute(name); 
	
		//If the object is of type ReplicableAttributeHolder,
        //Get that object from the replication set.
        if (value instanceof SessionAttributeHolder) {
            String id = ((SessionAttributeHolder)value).id;
            value = getProtocolSession(id);
        }

        if (value instanceof ReplicatedMessageHolder) {
            value = ((ReplicatedMessageHolder)value).getMessage();
        }
        return value;
	}

	
	public void setAttribute(String name, Object attribute) {
		super.setAttribute( name, attribute );	
		
		//In case the attribute is AseProtocolSession and part of the same IC,
        //Replace it with a holder.
        if(attribute instanceof AbstractProtocolSession){
          if(appSession != null &&
              appSession == ((AbstractProtocolSession)attribute).getApplicationSession()){

              SessionAttributeHolder holder = new SessionAttributeHolder();
              holder.id = ((AbstractProtocolSession)attribute).getId();
              attribute = holder;
          }
        }
        if(attribute instanceof AseSipServletMessage )  {
			attribute = new ReplicatedMessageHolder((AseSipServletMessage)attribute);
        } 
		
		//Now add this attribute to the map.
        synchronized (attributes) {
            attributes.put(name, attribute);
            this.setModified(true);
		}
	}

	public void storeMessageAttr() {
		try {
			Iterator ic = attributes.keySet().iterator(); 
			while (ic.hasNext()) {
				Object value =attributes.get(ic.next());
				if(value instanceof ReplicatedMessageHolder) {
					//((AseSipServletMessage)value).assignMessageId();
					AseSipServletMessage msg = ((ReplicatedMessageHolder)value).getMessage();
					if(msg != null) {
						msg.storeMessageAttr();
					} else {
						logger.error("Due to replication data reduction, message not available.");
					}
				}
			}
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	public void activate(ReplicationSet parent) {
		super.activate(parent);
		try {
			Object[] ic = this.attributes.keySet().toArray();
			for(int i = 0; i < ic.length; ++i) {
				Object value = this.attributes.get(ic[i]);
				if(value instanceof ReplicatedMessageHolder) {
					ReplicatedMessageHolder holder = (ReplicatedMessageHolder)value;
					holder.resolve();
					//this.attributes.put(ic[i], holder.resolve());
				}
			}
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	
	 public void writeExternal(ObjectOutput out) throws IOException {
	     if (logger.isDebugEnabled()) {
	         logger.debug("Entering AseProtocolSession writeExternal()");
	     }
        super.writeExternal(out);
		
        if (logger.isDebugEnabled()) {
            logger.debug("Leaving AseProtocolSession writeExternal()");
        }
    }


    public void readExternal(ObjectInput in)  throws IOException, ClassNotFoundException{
         if (logger.isDebugEnabled()) {
             logger.debug("Entering AseProtocolSession readExternal()");
         }

		super.readExternal(in);
	    this.chainInfo = (AseChainInfo)this.getReplicable(CHAIN_INFO);
        	
		if (logger.isDebugEnabled()) {
			logger.debug("Leaving AseProtocolSession readExternal()");
		}
    }

	
	/**
	 * Returns the workqueue id for this session using the Invocation context
	 * @return
	 */
	int getWorkQueue(){
		int workQueue = -1;
		
		if(this.appSession != null){
			workQueue = ((AseApplicationSession)appSession).getIc().getWorkQueue();
		}
		return workQueue;
	}
	
	public void handleMessage(SasMessage message) throws AseInvocationFailedException, ServletException {
		if(message instanceof AseBaseRequest){
			this.handleRequest((AseBaseRequest)message, null);
		}else if(message instanceof AseBaseResponse){
			this.handleResponse(null, (AseBaseResponse)message);
		}
	}

	/**
	 * Handles the request. This method will be called from the AseHost 
	 * for handling the requests. If the protocol specific sessions want
	 * to do something specific, needs to over-ride this method and call super.handleRequest();
	 * @param request
	 * @param response
	 * @throws AseInvocationFailedException
	 */
	public void handleRequest(AseBaseRequest request, AseBaseResponse response) 
					throws AseInvocationFailedException, ServletException {
		
		if(logger.isEnabledFor(Level.INFO)){
			logger.info("HandleRequest called on protocol session");
		}

		//Check whether the app session is already associated or not.
		if(this.appSession == null){
			throw new AseInvocationFailedException(_strings.getString("AseProtocolSession.noAppSession"));
		}
		
		if (request instanceof TimeMeasurement) {
			setTimestamp((TimeMeasurement)request);
		}

		super.handleMessage(request);
	}
	
	/**
	 * Handles the response. This method will be called from the AseHost 
	 * for handling the responses. If the protocol specific sessions want
	 * to do something specific, needs to over-ride this method and call super.handleResponse();

	 * @param request
	 * @param response
	 * @throws AseInvocationFailedException
	 */	
	public void handleResponse(AseBaseRequest request, AseBaseResponse response)
					throws AseInvocationFailedException, ServletException {
		if(logger.isEnabledFor(Level.INFO)){
			logger.info("HandleResponse called on protocol session");
		}
		
		//Check whether the app session is already associated or not.
		if(this.appSession == null){
			throw new AseInvocationFailedException(_strings.getString("AseProtocolSession.noAppSession"));
		}
		
		if (request instanceof TimeMeasurement) {
			setTimestamp((TimeMeasurement)request);
		}
		
		super.handleMessage(response);
	}
	
	
	public AseChainInfo getChainInfo() {
		return chainInfo;
	}

	protected void setChainInfo(AseChainInfo info) {
		chainInfo = info;
		this.setModified(true);
	}
	
	/**
	 * Variables and metnods for response time baseed measurement 
	 */
	private ArrayList timestamps;
	
	public void setTimestamp(TimeMeasurement msg) {
		if (!msg.hasTimestamp()) {
			if (logger.isDebugEnabled()) {
				logger.debug("Timestamp is not recorded");
			}
			return;
		}
		((AseApplicationSession)appSession).setTimestamp(index, msg.getTimestamp());
	}
	
	public long getTimestamp(int msgIndex) {
		Long timestamp = (Long)timestamps.get(msgIndex);
		if (timestamp == null) return -1;
		return timestamp.longValue();
	}
	
	public String getApplicationName(){
		AseContext ctx = this.appSession != null ? ((AseApplicationSession)this.appSession).getContext() : null;
		return (ctx == null ? null : ctx.getName());
	}

	public void invalidate (){
		super.invalidate();
		AseApplicationSession as = (AseApplicationSession) appSession;
		if(as != null && as.getIc() != null){
			as.getIc().protocolSessionInvalidated(this);
		}
        }

	public void cleanup (){
		if(appSession != null){
			((AseApplicationSession)appSession).removeProtocolSession(this);
		}
        }
}
