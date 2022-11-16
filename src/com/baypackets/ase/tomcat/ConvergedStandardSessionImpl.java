/****

  Copyright (c) 2013 Agnity, Inc. All rights reserved.

  This is proprietary source code of Agnity, Inc. 
  Agnity, Inc. retains all intellectual property rights associated 
  with this source code. Use is subject to license terms.

  This source code contains trade secrets owned by Agnity, Inc.
  Confidentiality of this computer program must be maintained at 
  all times, unless explicitly authorized by Agnity, Inc.

 ****/

package com.baypackets.ase.tomcat;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.sip.ConvergedHttpSession;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipFactory;

import org.apache.catalina.Container;
import org.apache.catalina.Manager;
import org.apache.catalina.session.StandardSession;
import org.apache.log4j.Logger;

import com.baypackets.ase.container.AseApplicationSession;
import com.baypackets.ase.container.exceptions.AseInvocationFailedException;
import com.baypackets.ase.spi.container.SasApplicationSession;
import com.baypackets.ase.spi.container.SasMessage;
import com.baypackets.ase.spi.container.SasProtocolSession;
import com.baypackets.ase.spi.replication.ReplicationEvent;
import com.baypackets.ase.spi.replication.ReplicationListener;
import com.baypackets.ase.spi.replication.ReplicationSet;
import com.baypackets.ase.util.AseStrings;

/**
 * This class is wrapper class for Standard Session of Tomcat.
 * This is a converged session but will not replicated to other nodes by tomcat. 
 * This class is created to get call backs of session create activate and expire for
 * measeurement conters. 
 * 
 * @author Amit Baxi
 *
 */
public class ConvergedStandardSessionImpl extends StandardSession  
implements ConvergedHttpSession,SasProtocolSession{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(ConvergedStandardSessionImpl.class);
	protected int state;
	protected SasApplicationSession appSession = null;
	protected String appSessionId = null;
	protected String sessionId = null;
	protected String appId = null;
	protected int index;
	private boolean mFirstReplicationCompleted = false;
	
	public ConvergedStandardSessionImpl(Manager manager) {
		super(manager);
	}
	
	protected MergedContext getContext(){
		Container container = getManager().getContainer();
		return container instanceof MergedContext ? (MergedContext)container : null;
	}
	
	public String encodeURL(String url){
		if(logger.isDebugEnabled())
			logger.debug("Encoding URL :" + url);

		//Validate the inputs
		this.validateURL(url);
		if(logger.isDebugEnabled())
			logger.debug("Validated URL :" + url);

		//Remove any existing jsession-id in the URL
		url = this.removeJSessionId(url);
		if(logger.isDebugEnabled())
			logger.debug("URL with removed jsessionid :" + url);
		
		//Encode the URL with jsession-id
		url = toEncoded(url, getIdInternal());
		if(logger.isDebugEnabled())
			logger.debug("Encoded URL :" + url);
		
		return url;
	}

	public String encodeURL(String relativePath, String scheme) {
		//Validate the inputs
		if(relativePath == null)
			throw new IllegalArgumentException("Relative Path cannot be null");
		this.validateScheme(scheme);
		
		//Build the absolute URL
		String url = this.toAbsolute(relativePath, scheme);
		if(logger.isDebugEnabled())
			logger.debug("Absolute URL :" + url);
			
		return encodeURL(url);
	}

	@Override
	public SipApplicationSession getApplicationSession() {
		if(appSession != null)
			return appSession;
		
		MergedContext context = getContext();
		SipFactory factory = context.getSipFactory();
		SipApplicationSession tmp = factory.createApplicationSession();
		if(tmp instanceof AseApplicationSession){
			((AseApplicationSession)tmp).addProtocolSession(this);
		}
		
		return tmp;
	}
	
	protected boolean validateURL(String location){
		URL url = null;
		try {
			url = new URL(location);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException(e);
		}
	
		validateScheme(url.getProtocol());
		
		MergedContext context = getContext();
		if(context == null)
			throw new IllegalStateException("Not able to get the application associated with this session");
	
		String path = context.getPath();
		String file = url.getFile();
		if(path != null && file == null ) {
			throw new IllegalArgumentException("The URL does not contain the application path");
		}
		
		if(path != null && !file.startsWith(path) ){
			throw new IllegalArgumentException("The URL does not belong to the application :" + path);
		}
		
		return true;
	}
	
	protected boolean validateScheme(String scheme){
		if(scheme == null || !(scheme.toLowerCase().equals(AseStrings.PROTOCOL_HTTP) 
								|| scheme.toLowerCase().equals(AseStrings.PROTOCOL_HTTPS)))
			throw new IllegalArgumentException("Invalid scheme :" + scheme + 
					".Scheme needs to be either HTTP or HTTPS.");
		
		return true;
	}
	
	protected String removeJSessionId(String location){
		int index = location == null ? -1 : location.indexOf(";jsessionid=");
		if(index < 0)
			return location;
		
		StringBuffer buffer = new StringBuffer();
		buffer.append(location.substring(0, index));
		
		int nextIndex = -1;
		for(int i=index+1; i<location.length();i++){
			int ch = location.charAt(i);
			if(ch == ';' || ch == '?'){
				nextIndex = i;
				break;
			}
		}
		
		buffer.append(nextIndex < 0 ? "" : location.substring(nextIndex));
		
		return buffer.toString();
	}
	
	protected String toAbsolute(String relativePath, String scheme){
		MergedContext context = getContext();
		String path = context.getPath();
		StringBuffer buffer = new StringBuffer();
		buffer.append(scheme);
		buffer.append("://");
		buffer.append(context.getHttpServer());
		buffer.append(AseStrings.COLON);
		buffer.append(context.getHttpPort(scheme));
		
		buffer.append(path.startsWith(AseStrings.SLASH) ? AseStrings.BLANK_STRING : AseStrings.SLASH);
		buffer.append(path);
		buffer.append(relativePath.startsWith(AseStrings.SLASH) ? AseStrings.BLANK_STRING : AseStrings.SLASH);
		buffer.append(relativePath);
	
		return buffer.toString();
	}
	
	protected String toEncoded(String url, String sessionId) {

        if ((url == null) || (sessionId == null))
            return (url);

        String path = url;
        String query = "";
        String anchor = "";
        int question = url.indexOf(AseStrings.CHAR_QUESTION_MARK);
        if (question >= 0) {
            path = url.substring(0, question);
            query = url.substring(question);
        }
        int pound = path.indexOf(AseStrings.CHAR_HASH);
        if (pound >= 0) {
            anchor = path.substring(pound);
            path = path.substring(0, pound);
        }
        StringBuffer sb = new StringBuffer(path);
        if( sb.length() > 0 ) { // jsessionid can't be first.
            sb.append(";jsessionid=");
            sb.append(sessionId);
        }
        sb.append(anchor);
        sb.append(query);
        return (sb.toString());

    }
	 public void cleanup() {
	    	if(appSession != null){
	    		((AseApplicationSession)appSession).removeProtocolSession(this);
	    	}
	    }

		public String getHandler() {
			return null;
		}

		public String getProtocol() {
			return "HTTP";
		}

		public int getProtocolSessionState() {
			return state;
		}

		public ReplicationListener getReplicationListener() {
			return null;
		}

		public void handleMessage(SasMessage message)
				throws AseInvocationFailedException, ServletException {
		}

		public void sendReplicationEvent(ReplicationEvent event) {
		}

		public void setApplicationSession(SipApplicationSession session, int index) {
			this.appSession = (SasApplicationSession)session;
			this.appSessionId = ((SasApplicationSession)session).getAppSessionId();
			this.appId = this.appSession.getApplication().getId();
			this.index = index;
		}

		public void setHandler(String name) throws ServletException {
		}

		public void setProtocolSessionState(int i) {
			state = i;
		}

		public void activate(ReplicationSet parent) {
		}

		public String getReplicableId() {
			//this is not useful as we are not doing replication through sas but returning 
			//something instead of null.
			return appSessionId+"_123";
		}

		public boolean isModified() {
			return false;
		}

		public boolean isReadyForReplication() {
			return false;
		}
		
		@Override
		public void partialActivate(ReplicationSet parent) {
			// TODO Auto-generated method stub
			
		}

		public void readIncremental(ObjectInput in) throws IOException,
				ClassNotFoundException {
		}

		public void replicationCompleted() {
			replicationCompleted(false);
		}
		
		public void replicationCompleted(boolean noReplication) {
			
		}

		public void setReplicableId(String replicableId) {
		}

		public void writeIncremental(ObjectOutput out,int replicationType) throws IOException {
		}

		@Override
		public String getId() {
			
			if(this.sessionId != null) {
				return this.sessionId;
			}

			return super.getId();
		}

		@Override
		public String getIdInternal() {
			if(this.sessionId != null) {
				return this.sessionId;
			}
			return super.getIdInternal();
		}

		public void readExternal(ObjectInput in) throws IOException,
				ClassNotFoundException {
		 
		}

		public void writeExternal(ObjectOutput out) throws IOException {
			if (logger.isDebugEnabled()){
				logger.debug("setFirstReplicationCompleted(true); ");
			}
			this.setFirstReplicationCompleted(true);
		}
	
        @Override
        public boolean isFirstReplicationCompleted() {
			return mFirstReplicationCompleted;
        }

        @Override
        public void setFirstReplicationCompleted(boolean isFirstReplicationCompleted) {
			mFirstReplicationCompleted=isFirstReplicationCompleted;
        } 	

}
