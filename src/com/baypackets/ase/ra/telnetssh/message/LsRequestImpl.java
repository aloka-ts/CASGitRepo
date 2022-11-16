/**
 * 
 */
package com.baypackets.ase.ra.telnetssh.message;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.telnetssh.qm.QueueManagerImpl;
import com.baypackets.ase.ra.telnetssh.session.LsResourceSession;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.resource.Response;
import com.baypackets.ase.spi.replication.ReplicationEvent;
import com.baypackets.ase.util.UIDGenerator;

/**
 * The Class LsRequest.
 * extends LSMessage
 * Implements Request
 * Defines request to be 
 * Exchanged between RA and application
 *
 * @author saneja
 */
public class LsRequestImpl extends LsMessage implements
LsRequest {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 600000001L;

	/** The Constant logger. */
	private static final Logger logger=Logger.getLogger(LsRequestImpl.class);

	/** The ls command. */
	private String lsCommand;

	/** The ls id. */
	private int lsId;

	/** The request id. */
	private String requestId; 

	/** The status. */
	private int status=REQUEST_PENDING;
	
	/** number of times request has been attempted*/
	private int attempt=0;	

	/** The is replicated. */
	private boolean isReplicated = false ;

	/** This is to indicate this request must execute even session invalidate */
	private boolean isMustExecute=false;
	
	/**
	 * Sets the ls id.
	 *
	 * @param lsId the lsId to set
	 */
	public void setLsId(int lsId) {
		this.lsId = lsId;
	}

	/*
	 * (non-Javadoc)
	 * @see com.baypackets.ase.ra.telnetssh.message.LsRequest#getLsId()
	 */
	public int getLsId() {
		return lsId;
	}

	/**
	 * Sets the ls command.
	 *
	 * @param lsCommand the lsCommand to set
	 */
	public void setLsCommand(String lsCommand) {
		this.lsCommand = lsCommand;
	}

	/*
	 * (non-Javadoc)
	 * @see com.baypackets.ase.ra.telnetssh.message.LsRequest#getLsCommand()
	 */
	public String getLsCommand() {
		return lsCommand;
	}

	/**
	 * Sets the status.
	 *
	 * @param status the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}


	/*
	 * (non-Javadoc)
	 * @see com.baypackets.ase.ra.telnetssh.message.LsRequest#getStatus()
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * Sets the request id.
	 *
	 * @param requestId the requestId to set
	 */
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	/**
	 * Gets the request id.
	 *
	 * @return the requestId
	 */
	public String getRequestId() {
		return requestId;
	}

	/**
	 * Sets the replicated.
	 *
	 * @param isReplicated the isReplicated to set
	 */
	public void setReplicated(boolean isReplicated) {
		this.isReplicated = isReplicated;
	}

	/**
	 * Checks if is replicated.
	 *
	 * @return the isReplicated
	 */
	public boolean isReplicated() {
		return isReplicated;
	}

	//	public void isAlreadyReplicated(boolean isReplicated) {
	//		this.setReplicated(isReplicated);
	//	}

	/**
	 * Instantiates a new ls request.
	 *
	 * @param lsResourceSession the ls resource session
	 */
	public LsRequestImpl(LsResourceSession lsResourceSession){
		if(logger.isDebugEnabled())
			logger.debug("Inside LsRequest(LsResourceSession) constructor ");
		setProtocolSession(lsResourceSession);
		lsResourceSession.setRequest(this);
		String constString = "TELNET_SSH_REQUEST";
		requestId=constString+"_"+UIDGenerator.getInstance().get128BitUuid();
	}
	
	/**
	 * Instantiates a new ls request.
	 *
	 */
	public LsRequestImpl(){
		if(logger.isDebugEnabled())
			logger.debug("Inside LsRequest() constructor ");
		String constString = "TELNET_SSH_REQUEST";
		requestId=constString+"_"+UIDGenerator.getInstance().get128BitUuid();
	}



	/* (non-Javadoc)
	 * @see com.baypackets.ase.resource.Request#createResponse(int)
	 */
	@Override
	public Response createResponse(int type) throws ResourceException {
		boolean isDebugEnabled=logger.isDebugEnabled();
		if(isDebugEnabled)
			logger.debug("createResponse()");
		LsResponse response=new LsResponseImpl(this);
		((LsMessage) response).setLsResourceAdaptor(getLsResourceAdaptor());
		return response;
	}

	/*
	 * (non-Javadoc)
	 * @see com.baypackets.ase.ra.telnetssh.message.LsRequest#cancel()
	 */
	public boolean cancel() throws ResourceException{
		if(logger.isDebugEnabled()){
			logger.debug("Inside cancel() for request:"+this.getRequestId());
		}
		boolean result= QueueManagerImpl.getInstance().removeRequest(this);
		replicate(this);
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LsRequestImpl other = (LsRequestImpl) obj;
		if (requestId == null) {
			if (other.requestId != null)
				return false;
		} else if (!requestId.equals(other.requestId))
			return false;
		return true;
	}


	/**
	 * Replicate.
	 *
	 * @param request the request
	 */
	private void replicate(LsRequest request) {
		if(logger.isDebugEnabled())
			logger.debug("replicate called");
		ReplicationEvent event = new ReplicationEvent(request.getSession() , "RESOURCE");
		((LsResourceSession)request.getSession()).sendReplicationEvent(event);
	}

	/**
	 * @param incremnts the attempt
	 */
	public void incrementAttempt() {
		attempt++;
	}

	/**
	 * @return the attempt
	 */
	public int getAttempt() {
		return attempt;
	}

	@Override
	public void setMustExecute(boolean mustExecute) {
		this.isMustExecute=mustExecute;
	}
	
	public boolean isMustExecute(){
		return isMustExecute;
	}
}
