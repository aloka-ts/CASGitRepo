/**
 * ReplicatedMessageHolder.java
 * @auther Suresh Jangir
 */

package com.baypackets.ase.replication;

import java.io.Serializable;

import org.apache.log4j.Logger;

import com.baypackets.ase.sipconnector.AseSipServletMessage;
import com.baypackets.ase.container.AseApplicationSession;
import com.baypackets.ase.container.AseHost;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.util.Constants;

public class ReplicatedMessageHolder implements Serializable {
	private static Logger logger = Logger.getLogger(ReplicatedMessageHolder.class);
	private static final long serialVersionUID = 745182094324880647L;
	private String appSessionId;
	private int messageId;
	private transient AseSipServletMessage message;

	public ReplicatedMessageHolder () {
	}

	public ReplicatedMessageHolder (AseSipServletMessage message) {
		this.appSessionId = message.getApplicationSession().getId();
		this.message = message;
		this.messageId = message.assignMessageId();
		
	}

	public AseSipServletMessage resolve() {
		AseHost host = (AseHost)Registry.lookup(Constants.NAME_HOST);
		if(host == null) {
			logger.error("Not able to get the host reference");
			return null;
		}
		AseApplicationSession appSess = (AseApplicationSession)host.getApplicationSession(this.appSessionId);
		if(appSess ==null){
			logger.error("Return null,Appsession is null for ID::"+this.appSessionId);
			return null;
		}
		this.message = appSess.getSipServletMessage(this.messageId);
		return this.message;
	}

	public AseSipServletMessage getMessage() {
		if(this.message == null) {
			return resolve();
		}
		return this.message;
	}
}

 
	




