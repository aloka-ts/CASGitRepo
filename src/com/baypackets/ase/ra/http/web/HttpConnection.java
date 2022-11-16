package com.baypackets.ase.ra.http.web;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

import org.apache.log4j.Logger;


public class HttpConnection{

	private Logger logger = Logger.getLogger(HttpConnection.class);
	//private HttpConnection connection=null;
	private HttpURLConnection urlConnection = null;
	//private String url =null;
	private String httpMethod=null;
	private URL url = null;
	private Map<String, ArrayList<String>> propertyList;
	
	public HttpConnection(){
	
		if(logger.isDebugEnabled())
			logger.debug("in HtttpConnection.");
		
	}

	
	public HttpURLConnection getUrlConnection() {
		try {
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod(httpMethod);
			this.setProperty();
		} catch (IOException e) {
			logger.error("Exception",e);

		}
		return urlConnection;
	}

	public void setUrlConnection(HttpURLConnection urlConnection) {
		this.urlConnection = urlConnection;
	}

	public String getUrl() {
		return url.toString();
	}

	public void setUrl(String url) {
		try {
			this.url = new URL(url);
		} catch (MalformedURLException e) {
			logger.error("exception", e);
		}
	}

	public String getHttpMethod() {
		return httpMethod;
	}

	public void setHttpMethod(String httpMethod) {
		this.httpMethod = httpMethod;
	}
	
	public void setPropertyList(Map<String, ArrayList<String>> list){
		this.propertyList=list;
	}
	
	public void setProperty(){
		if(propertyList.get("key").isEmpty()){
			if(logger.isDebugEnabled()){
				logger.debug("no request property added.");
			}	
			
			logger.error("no request prorperty");
		}else{
				   ArrayList<String> key = propertyList.get("key");
				   ArrayList<String> value = propertyList.get("value");
				   //logger.error("ravi"+propertyList.get("key")+"and"+propertyList.get("value"));
		           for(int i=0;i< key.size();i++){
		        	   urlConnection.setRequestProperty(key.get(i),value.get(i));
		           }
		           if(logger.isDebugEnabled()){
		        	   logger.debug("all properties set in request");
		           }
		
			}
	}
	
}