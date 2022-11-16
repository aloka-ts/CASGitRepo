package com.agnity.simulator.callflowadaptor.element.child;

import java.util.Map;

import com.agnity.simulator.callflowadaptor.element.Node;
import com.agnity.simulator.domainobjects.Variable;
import com.agnity.simulator.utils.Constants;


public class ToElem extends Node{

	private String toPattern;
	private String userInfo;
	private String host;
	private String port;

	public ToElem() {
		super(Constants.TO, true);
	}

	/**
	 * @param toPattern the toPattern to set
	 */
	public void setToPattern(String toPattern) {
		this.toPattern = toPattern;
		String [] uri=toPattern.split("@");
		userInfo= uri[0];
		String[] urlPart = uri[1].split(":"); 
		host = urlPart[0];
		port = urlPart[1];
	}

	/**
	 * @return the toPattern
	 */
	public String getTo(Map<String,Variable> varMap) {
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
		builder.append(Constants.TO);
		builder.append("  ");
		
		builder.append(" TOPATTERN='");
		builder.append(toPattern);
		builder.append("'");
		
		builder.append(super.toString());
		
		return builder.toString();
	}
}
