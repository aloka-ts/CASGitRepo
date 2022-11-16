//***********************************************************************************
//GENBAND, Inc. Confidential and Proprietary
//
//This work contains valuable confidential and proprietary
//information.
//Disclosure, use or reproduction without the written authorization of
//GENBAND, Inc. is prohibited. This unpublished work by GENBAND, Inc.
//is protected by laws of United States and other countries.
//If publication of work should occur the following notice shall
//apply:
//
//"Copyright 2007 GENBAND, Inc. All right reserved."
//***********************************************************************************


//***********************************************************************************
//
//      File:   ResourceReference.java
//
//      Desc:   This file contains set methods which are accessed by AppRuleSet.ResourceRefRule() 
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Somesh Kr. Srivastava           30/10/07        Initial Creation
//
//***********************************************************************************


package com.baypackets.ase.container;


/**
 * This class contains setters which are set by AppRuleSet.ResourceRefRule()
 * and getters are accessed by AseContext.bind()
 *
 * @author Somesh Kumar Srivastava
 */
public class ResourceReference {
	
	 private transient String _resRefName;
	 private transient String _resType;
	 private transient String _resAuth;
     private transient String _resSharingScope;
  
  
  /**
    * Sets the resourceRef name. This is specified by the "res-ref-name" tag
    * in the deployment descriptor.
    */
	public void setResourceRefName(String resRefName) {
		_resRefName = resRefName;
	}

  /**
    * @returns _resRefName -the resource reference name 
    * in the deployment descriptor.
    */
	public String getResourceRefName() {
		return _resRefName;
	}
	
	public void setResourceType(String resType) {
		_resType = resType;
    }

	public String getResourceType() {
		return _resType;
	}

	public void setResourceAuth(String resAuth) {
		_resAuth = resAuth;
	}

	public String getResourceAuth() {
		return _resAuth;
	}

	public void setResourceSharingScope(String resSharingScope) {
		_resSharingScope = resSharingScope;
	}

	public String getResourceSharingScope() {
		return _resSharingScope;
	}

}


