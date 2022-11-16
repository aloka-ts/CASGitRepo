package com.genband.m5.maps.ide.model;

import java.util.Map;

import com.genband.m5.maps.common.CPFConstants;

public class WebServiceInfo implements java.io.Serializable{
	
	public WebServiceInfo() {
		//TODO: Look for an Entity by qualified name as passed
	}

	private String webServiceName;

	private Map<CPFConstants.OperationType, String> webMethodsMap;
	
	private String targetNamespace;
	
	private Map<CPFConstants.OperationType, String[]> webParams; 
	
	private Map<CPFConstants.OperationType, String> webResults;

	public Map<CPFConstants.OperationType, String> getWebResults() {
		return webResults;
	}

	public void setWebResults(Map<CPFConstants.OperationType, String> webResults) {
		this.webResults = webResults;
	}

	public Map<CPFConstants.OperationType, String[]> getWebParams() {
		return webParams;
	}

	public void setWebParams(Map<CPFConstants.OperationType, String[]> webParams) {
		this.webParams = webParams;
	}

	public String getWebServiceName() {
		return webServiceName;
	}

	public void setWebServiceName(String webServiceName) {
		this.webServiceName = webServiceName;
	}

	public Map<CPFConstants.OperationType, String> getWebMethodsMap() {
		return webMethodsMap;
	}

	public void setWebMethodsMap(
			Map<CPFConstants.OperationType, String> webMethodsMap) {
		this.webMethodsMap = webMethodsMap;
	}

	public String getTargetNamespace() {
		return targetNamespace;
	}

	public void setTargetNamespace(String targetNamespace) {
		this.targetNamespace = targetNamespace;
	}
	
	
}
