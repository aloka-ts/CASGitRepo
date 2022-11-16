/**
 * 
 */
package com.baypackets.ase.ra.telnetssh.message;

import java.util.List;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.telnetssh.session.LsResourceSession;
import com.baypackets.ase.resource.Request;

/**
 * The Class LsResponse.
 * extends LSMessage
 * Implements Response
 * Defines response to be 
 * Exchanged between RA and application
 *
 * @author saneja
 */
public class LsResponseImpl extends LsMessage implements LsResponse {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 700000001L;
	
	/** The Constant logger. */
	private static final Logger logger=Logger.getLogger(LsResponseImpl.class);
	
	/** The m_ ls request. */
	private LsRequest m_LsRequest;
	
	/** The result code. */
	private int resultCode;
	
	/** The result data. */
	private List<String> resultData;
	
	/* (non-Javadoc)
	 * @see com.baypackets.ase.resource.Response#getRequest()
	 */
	@Override
	public Request getRequest() {
		return m_LsRequest;
	}
	
	/**
	 * Instantiates a new ls response.
	 *
	 * @param lsRequest the ls request
	 */
	public LsResponseImpl(LsRequest lsRequest){
		if(logger.isDebugEnabled())
			logger.debug("Inside LsResponse(LsRequest) constructor ");
		this.m_LsRequest=lsRequest;
		setProtocolSession((LsResourceSession)lsRequest.getSession());
	}


	/**
	 * Sets the result code.
	 *
	 * @param resultCode the resultCode to set
	 */
	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}


	/*
	 * (non-Javadoc)
	 * @see com.baypackets.ase.ra.telnetssh.message.LsResponse#getResultCode()
	 */
	public int getResultCode() {
		return resultCode;
	}


	/**
	 * Sets the result data.
	 *
	 * @param resultData the resultData to set
	 */
	public void setResultData(List<String> resultData) {
		this.resultData = resultData;
	}


	/*
	 * (non-Javadoc)
	 * @see com.baypackets.ase.ra.telnetssh.message.LsResponse#getResultData()
	 */
	public List<String> getResultData() {
		return resultData;
	}

}
