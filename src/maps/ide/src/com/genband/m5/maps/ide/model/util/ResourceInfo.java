package com.genband.m5.maps.ide.model.util;

import com.genband.m5.maps.common.CPFConstants;
import com.genband.m5.maps.common.CPFConstants.ResourceType;

public class ResourceInfo {

	private CPFConstants.ResourceType resourceType;
	
	private String resourceName;
	
	public CPFConstants.ResourceType getResourceType() {
		return resourceType;
	}
	public void setResourceType(CPFConstants.ResourceType resourceType) {
		this.resourceType = resourceType;
	}
	public String getResourceName() {
		return resourceName;
	}
	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}
}
