package com.baypackets.ase.ra.http.message;

import java.util.List;
import java.util.Map;

import com.baypackets.ase.resource.Response;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;

@DefaultSerializer(ExternalizableSerializer.class)
public interface HttpResponse extends Response{
	
	 public static final int HTTP_OK = 200;
	   public static final int HTTP_CREATED = 201;
	   public static final int HTTP_ACCEPTED = 202;
	   public static final int HTTP_BAD_REQUEST = 400;
	   public static final int HTTP_UNAUTHORIZED = 401;
	   public static final int HTTP_NOT_FOUND = 404;
	   public static final int HTTP_BAD_METHOD = 405;
	   public static final int HTTP_SERVER_ERROR = 500;
	
	public String getData();
	public int getResponseCode();
	public Map<String, List<String>> getHeaderFields();
	
    
}
