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
//      File:   BaseProxy.java
//
//      Desc:   This file defines an interafce which will be implemented by all proxies.
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Rajendra Singh                  20/12/07        Initial Creation
//
//***********************************************************************************


package com.baypackets.ase.soa.codegenerator.proxy;


public interface BaseProxy extends Cloneable {

	String getInterface();
	void setURI(String uri);
	void setImpl(Object obj);
	Object getImpl();
	Object clone() throws CloneNotSupportedException;

}

