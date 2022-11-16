package com.baypackets.ase.ra.http.message;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.sip.SipApplicationSession;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.http.session.HttpResourceSession;
import com.baypackets.ase.resource.Request;
import com.baypackets.ase.resource.ResourceSession;


import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;

@DefaultSerializer(ExternalizableSerializer.class)
public class HttpResponseImpl extends HttpMessage implements HttpResponse {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String data;
	private HttpRequest httpRequest;
	private Map<String, List<String>> headerFields;
	private int responseCode;
	public int getResponseCode() {
		return responseCode;
	}



	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}


	private Logger logger = Logger.getLogger(HttpResponseImpl.class);
	
	
	
	public HttpResponseImpl(HttpRequest httpRequest){
		if(logger.isDebugEnabled())
			logger.debug("Inside HttpResponse(HttpRequest) constructor ");
		this.httpRequest = httpRequest;
		setProtocolSession((HttpResourceSession)httpRequest.getSession());
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
		return httpRequest;
	}



	
	public Map<String, List<String>> getHeaderFields() {
		// TODO Auto-generated method stub
		return headerFields;
	}
	
	
	public void setHeaderFields(Map<String, List<String>> list){
		this.headerFields=list;
	}


}
