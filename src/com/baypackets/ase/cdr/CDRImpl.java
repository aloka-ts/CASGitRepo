/*
 * Created on Jun 20, 2005
 *
 */
package com.baypackets.ase.cdr;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;

import com.baypackets.ase.sbb.CDR;
import com.baypackets.ase.sbb.CDRWriteFailedException;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.container.AseEngine;
import com.baypackets.ase.container.AseHost;
import com.baypackets.ase.container.AseContext;

/**
 * Implementation of the CDR interface.
 */
public class CDRImpl implements CDR, Serializable {
	private static final long serialVersionUID = 3322772378251L;
	private transient CDRContext context = null;
	private boolean distributable = false;
	private String hostName;
	private String appName;
	private HashMap values = new HashMap();
	private int writeCount;
	
	/**
	 * 
	 */
	public CDRImpl(CDRContext ctx) {
		super();
		this.context = ctx;
	}

	/**
	 * Gets the value for the specified CDR field
	 */
	public Object get(String field) {
		return this.values.get(field);
	}

	/**
	 * Sets the value for the specified CDR field.
	 */
	public void set(String field, Object value) {
		if (this.distributable && !(value instanceof java.io.Serializable)) {
			throw new IllegalArgumentException("Value set for CDR attribute, " + field + " is not Serializable!");
		}
		this.values.put(field, value);
	}

	/* (non-Javadoc)
	 * @see com.baypackets.ase.ext.cdr.CDR#write()
	 */
	public void write() throws CDRWriteFailedException {
		this.getContext().writeCDR(this);
	}
	
	public String toString(){
		return this.getContext().formatCDR(this);
	}
	/**
	 * @return the CDRContext object associated with this CDR.
	 */
	public CDRContext getContext() {
		if (this.context == null) {
			// Perform a lookup of the CDRContext if it is NULL.
			AseEngine engine = (AseEngine)Registry.lookup(Constants.NAME_ENGINE);
			AseHost host = (AseHost)engine.findChild(this.hostName);
			AseContext app = (AseContext)host.findChild(this.appName);
			this.context = app.getCDRContext(SESSION_ID);
		}
		return this.context;
	}

	/**
	 * @return the number of times this CDR was written to file.
	 */
	public int getWriteCount() {
		return writeCount;
	}

	/**
	 * @param count Number of times written to the File. 
	 */
	public void setWriteCount(int count) {
		writeCount = count;
	}
	
	/**
	 * Returns the iterator of all the CDR fields set on this CDR object.
	 */
	public Iterator getFields() {
		return this.values.keySet().iterator();
	}

	/**
	 * Sets the ID of the AseHost object that this CDR is associated with.
	 */
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	/**
	 * Sets the ID of the app that this CDR is associated with.
	 */
	public void setAppName(String appName) {
		this.appName = appName;
	}

	public void setDistributable(boolean distributable) {
		this.distributable = distributable;
	}

	//sumit@sbtm new method for CDR writing [
	/* (non-Javadoc)
	 * @see com.baypackets.ase.sbb.CDR#write(java.lang.String[])
	 */
	@Override
	public void write(String[] cdr) throws CDRWriteFailedException {
		this.getContext().writeCDR(cdr, this);
	}
	//]sumit@sbtm new method for CDR writing
}
