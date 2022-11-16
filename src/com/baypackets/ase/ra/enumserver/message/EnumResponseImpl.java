package com.baypackets.ase.ra.enumserver.message;

import org.apache.log4j.Logger;
import org.xbill.DNS.RRset;

import com.baypackets.ase.ra.enumserver.session.EnumResourceSession;
import com.baypackets.ase.resource.Request;

public class EnumResponseImpl extends EnumMessage implements EnumResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String data;
	private EnumRequest enumRequest;
	
	private RRset[] records=null;

	private Logger logger = Logger.getLogger(EnumResponseImpl.class);
	
	
	
	public EnumResponseImpl(EnumRequest enumRequest){
		if(logger.isDebugEnabled())
			logger.debug("Inside EnumResponseImpl(enumRequest) constructor ");
		this.enumRequest = enumRequest;
		setProtocolSession((EnumResourceSession)enumRequest.getSession());
	}



	public void setData(String data) {
		if(logger.isDebugEnabled())
    		logger.debug("in setData().");
		this.data = data;
	}

    public String getData(){
    	if(logger.isDebugEnabled())
    		logger.debug("in getData().");
    	return data;
    }

	@Override
	public Request getRequest() {
		return enumRequest;
	}




	public void setDNSRecords(RRset[] records) {
		// TODO Auto-generated method stub
		this.records=records;		
	}

	public RRset[] getRecords(){
		return records;
	}


}
