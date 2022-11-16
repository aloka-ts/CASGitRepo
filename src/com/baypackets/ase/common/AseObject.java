/*
 * Created on Aug 13, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.baypackets.ase.common;
import java.io.IOException;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.io.Externalizable;
import java.io.ObjectOutput;
import java.io.ObjectInput;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.baypackets.ase.util.AseStrings;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.util.Enumerator;
import com.baypackets.ase.util.StringManager;

import com.baypackets.ase.spi.replication.ReplicableMap;

/**
 * @author Ravi
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
@DefaultSerializer(ExternalizableSerializer.class)
public class AseObject implements Externalizable {

	private static Logger logger = Logger.getLogger(AseObject.class);	
	private static StringManager _strings = StringManager.getInstance(AseObject.class.getPackage());
	private static final long serialVersionUID = -3814634264647849791L;
	protected int state = Constants.STATE_VALID;
	protected ReplicableMap attributes = new ReplicableMap();
	
	protected AseObject(){
		//this needs to be done at the end of the super class constructor 
		//this.objectCreated();
	}

	public void writeExternal(ObjectOutput out) throws IOException {
        if (logger.isDebugEnabled()) {
            logger.debug("Entering AseObject writeExternal()");
        }

		try {
			out.writeInt(state);
		}
		catch (Exception e) {
            		logger.error("Exception in writeObject()" + e.toString(), e);
		}

        if (logger.isDebugEnabled()) {
            logger.debug("Leaving AseObject writeExternal()");
        }
	}

	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        if (logger.isDebugEnabled()) {
            logger.debug("Entering AseObject readExternal()");
        }

		try {
			state = in.readInt();
		}
		catch (Exception e) {
            logger.error("Exception in writeObject()" + e.toString(), e);
		}

        if (logger.isDebugEnabled()) {
            logger.debug("Leaving AseObject readExternal()");
        }
	}

	/**
	 * This method will be used to get attributes of AseObject but state will be checked only if checkState boolean is true.
	 * @param name attribute name
	 * @param checkState boolean to specify check state or not
	 * @return
	 */
	public Object getAttribute(String name,boolean checkState){
		if(checkState)
			this.checkValid();
		return this.attributes.get(name);
	}
	
	public Object getAttribute(String name){
		return getAttribute(name,true);
	}
	
	protected Iterator getAttributeNamesIterator(){
		this.checkValid();
		return this.attributes.keySet().iterator();
	}

	public Enumeration getAttributeNamesEnumeration() {
		this.checkValid();
		return new Enumerator(this.getAttributeNamesIterator());
	}

	public void removeAttribute(String name) {
		this.checkValid();
		Object value = this.attributes.remove(name);
		if(value != null){
			this.attributeRemoved(name, value);
		}	
	}
	
	public void setAttribute(String name, Object value) {
		
		if(value==null){
			return;
		}
		this.checkValid();

		if (value!=null &&!(value instanceof Serializable)) {
			String msg = name + " must be serializable";
			logger.error(msg);
			throw new IllegalArgumentException(msg);
		}
		boolean present = this.attributes.containsKey(name);
		this.attributes.put(name, value);
		if(present){
			this.attributeModified(name, value);
		}else{
			this.attributeAdded(name, value);
		}
	}
	
	public int getState() {
		return state;
	}

	public void setState(int i) {
		state = i;
	}

	public void checkValid(){
		if(this.state == Constants.STATE_INVALID || this.state == Constants.STATE_DESTROYED){
			throw new IllegalStateException(_strings.getString("AseBaseSession.invalidState"));
		}
	}
	
	public void cleanup(){
		
		//remove all the attributes.
		this.removeAllAttributes();
		
		//Update the state to session destroyed 
		this.state = Constants.STATE_DESTROYED;
		
		//Invoke the listener to notify the session is destroyed	
		this.objectDestroyed();
	}
	
	protected void removeAllAttributes(){
		//Remove all the attributes bound to this session.	
		Iterator iterator = this.attributes.entrySet().iterator();
		while(iterator.hasNext()){
			//Get the key and values from the iterator. 
			Map.Entry entry = (Map.Entry) iterator.next();
			String key = (String) entry.getKey();
			Object value = entry.getValue();
			
			//send notification for this attribute removal.
			this.attributeRemoved(key, value);
		}

		// Not calling remove() on individual elements as this method does normal
		// replication processing. Calling clear() instead afterwards which should not
		// harm as it is cleaningup anyway.
		this.attributes.clear();
	}

	public void attributeAdded(
		String name,
		Object value) {
		if(logger.isEnabledFor(Level.INFO))
			logger.info("Attribute added :"+name +"="+value);
	}

	public void attributeModified(
		String name,
		Object value) {
		if(logger.isEnabledFor(Level.INFO))
			logger.info("Attribute modified :"+name +"="+value);
	}

	public void attributeRemoved(
		String name,
		Object value) {
			if(logger.isEnabledFor(Level.INFO))
				logger.info("Attribute removed :"+name +AseStrings.EQUALS+value);
	}

	public void objectCreated() {
		if(logger.isEnabledFor(Level.INFO))
			logger.info("Session created :");
	}

	public void objectDestroyed() {
		if(logger.isEnabledFor(Level.INFO))
			logger.info("Session destroyed :");
	}

	public void objectExpired() {
		if(logger.isEnabledFor(Level.INFO))
			logger.info("Session expired :");
	}
	
	 public void objectReadyToInvalidate(){
		 if(logger.isEnabledFor(Level.INFO))
				logger.info("Session Ready To Invalidate :");
		 
	 }
}
