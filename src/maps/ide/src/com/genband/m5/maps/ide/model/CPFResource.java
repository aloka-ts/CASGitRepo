package com.genband.m5.maps.ide.model;

import com.genband.m5.maps.ide.model.util.ResourceInfo;


public class CPFResource {

	private CPFScreen cpfScreen;
	
	private ResourceInfo resourceInfo;
	
	private int operationId;
	
	private String className;
	
	public int getOperationId() {
		return operationId;
	}
	public void setOperationId(int operationId) {
		this.operationId = operationId;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public CPFResource (CPFScreen input) {
		cpfScreen = input;
	}
	public void setResourceInfo (ResourceInfo res) {
		resourceInfo = res;
	}
	public CPFScreen getCpfScreen() {
		return cpfScreen;
	}
	public ResourceInfo getResourceInfo() {
		return resourceInfo;
	}
	
}
