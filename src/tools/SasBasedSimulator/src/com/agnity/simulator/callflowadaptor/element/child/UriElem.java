package com.agnity.simulator.callflowadaptor.element.child;

import java.util.Map;

import com.agnity.simulator.callflowadaptor.element.Node;
import com.agnity.simulator.domainobjects.Variable;
import com.agnity.simulator.utils.Constants;


public class UriElem extends Node{
	
	private String uriPattern;
	private String userInfo;
	private String host;
	private String port;
	
	public UriElem() {
		super(Constants.URI, true);
	}

	/**
	 * @param uriPattern the uriPattern to set
	 */
	public void setUriPattern(String uriPattern) {
		this.uriPattern = uriPattern;
		
		String [] uri=uriPattern.split("@");
		userInfo= uri[0];
		String[] urlPart = uri[1].split(":"); 
		host = urlPart[0];
		port = urlPart[1];
		
	}

	/**
	 * @return the uriPattern
	 */
	public String getUri(Map<String,Variable> varMap) {
		StringBuilder uri=new StringBuilder();
		
		if(userInfo.startsWith("$")){
			String varName = userInfo.substring(1);
			Variable variable=varMap.get(varName);
			uri.append(variable.getVarValue());
		}else{		
			uri.append(userInfo);
		}
		uri.append("@");
		
		if(host.startsWith("$")){
			String varName = host.substring(1);
			Variable variable=varMap.get(varName);
			uri.append(variable.getVarValue());
		}else{		
			uri.append(host);
		}
		uri.append(":");
		if(port.startsWith("$")){
			String varName = port.substring(1);
			Variable variable=varMap.get(varName);
			uri.append(variable.getVarValue());
		}else{		
			uri.append(port);
		}
		
		return uri.toString();
	}
	
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(Constants.URI);
		builder.append("  ");
		
		builder.append(" uriPattern='");
		builder.append(uriPattern);
		builder.append("'");
		
	
		
		builder.append(super.toString());
		
		return builder.toString();
	}
	
}
