package com.agnity.simulator.callflowadaptor.element.child;

import java.util.Map;

import com.agnity.simulator.callflowadaptor.element.Node;
import com.agnity.simulator.domainobjects.Variable;
import com.agnity.simulator.utils.Constants;


public class FromElem extends Node{

	private String fromPattern;
	private String userInfo;
	private String host;
	private String port;

	public FromElem() {
		super(Constants.FROM, true);
	}

	/**
	 * @param toPattern the toPattern to set
	 */
	public void setFromPattern(String fromPattern) {
		this.fromPattern = fromPattern;
		String [] uri=fromPattern.split("@");
		userInfo= uri[0];
		String[] urlPart = uri[1].split(":"); 
		host = urlPart[0];
		port = urlPart[1];
	}

	/**
	 * @return the toPattern
	 */
	public String getFrom(Map<String,Variable> varMap) {
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
		builder.append(Constants.FROM);
		builder.append("  ");

		builder.append(" fromPattern='");
		builder.append(fromPattern);
		builder.append("'");

		builder.append(super.toString());

		return builder.toString();
	}

}
