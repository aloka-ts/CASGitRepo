package com.baypackets.ase.ra.http.message;

import java.util.ArrayList;
import java.util.Map;

import com.baypackets.ase.resource.Request;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;

@DefaultSerializer(ExternalizableSerializer.class)
public interface HttpRequest extends Request {

	/** The Constant REQUEST_PENDING. */
	public static final short REQUEST_PENDING = 0;	

	/** The Constant REQUEST_ACTIVE. */
	public static final short REQUEST_ACTIVE = 1;	

	/** The Constant REQUEST_INACTIVE. */
	public static final short REQUEST_INACTIVE = 2;
	
	public String getURL();
	public String getHttpMethod();
	public byte[] getData();
	public void setData(byte[] data);
	public Map<String,String> getParams();
	public void setParams(Map<String,String> params);
	public void setContentType(String contentType);
	public String getContentType();
	public void cancel();
	public void setHeader(String key, String value);
	public Map<String, ArrayList<String>> getRequestProperties();
	
	/**
	 * Gets the status.
	 *
	 * @return the status
	 */
	public int getStatus();
}
