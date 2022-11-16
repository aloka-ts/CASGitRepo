package com.baypackets.ase.ra.http.message;


import java.util.Map;

import org.apache.log4j.Logger;

import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.resource.Response;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;

@DefaultSerializer(ExternalizableSerializer.class)
public class HttpRequestImpl extends HttpMessage implements HttpRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String url = null;
	private String httpMethod = null;
	private byte[] data=null;
	private String contentType=null;
	private Map<String ,String> params=null;
	
	
	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		
		if(logger.isDebugEnabled())
			logger.debug("in setData(). " +data);
		this.data = data;
	}

	private Logger logger = Logger.getLogger(HttpRequestImpl.class);
	private int status = REQUEST_PENDING;
   
    
    
    
	@Override
	public Response createResponse(int arg0) throws ResourceException {
		if(logger.isDebugEnabled())
			logger.debug("in createResponce().");
		HttpResponse httpResponse = new HttpResponseImpl(this);
	    ((HttpMessage)httpResponse).setHttpResourceAdaptor(getHttpResourceAdaptor());
		return httpResponse;
	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getURL() {
		return url;
	}
	
	public void setURL(String url){
		this.url=url;
	}

	public void setHttpMethod(String httpMethod){
		this.httpMethod=httpMethod;
	}

	@Override
	public String getHttpMethod() {
		return httpMethod;
	}
	
	public void setStatus(int status) {
		this.status = status;
	}

	@Override
	public int getStatus() {
		return status;
	}

	@Override
	public Map<String, String> getParams() {
		// TODO Auto-generated method stub
		return params;
	}

	@Override
	public void setParams(Map<String, String> params) {
		this.params=params;
		
	}
	


	

}
