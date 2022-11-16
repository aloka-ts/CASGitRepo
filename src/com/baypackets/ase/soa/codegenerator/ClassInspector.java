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
//      File:   ClassInspector.java
//
//      Desc:   This file extracts all meta information of a java class using reflection.
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Rajendra Singh              27/12/07        Initial Creation
//
//***********************************************************************************
                                                                                                     
                                                                                                     
package com.baypackets.ase.soa.codegenerator;

import java.util.*;
import java.lang.reflect.*;
import org.apache.log4j.Logger;

import com.baypackets.ase.util.AseStrings;

public final class ClassInspector  {
	private static Logger logger = Logger.getLogger(ClassInspector.class);
	private String packageName = "";
	private String className = ""; 
	private Method[] methods = null;
	private Class c = null;

	public ClassInspector()	{
	
	}

	public void parse(Class cls) throws Exception	{
		c = cls;
		Package pkg = c.getPackage();
		if(pkg != null)	{
			packageName = pkg.getName();
		}
		className = c.getName();
		if(pkg != null)	{
			className = className.substring(className.lastIndexOf(AseStrings.CHAR_DOT)+1);
		}


		methods = c.getDeclaredMethods();	

	}

	public String getPackageName()	{
		return packageName;
	}

	public String getClassName()	{
		return className;
	}

	public String getAbsoluteClassName()	{
	if (logger.isDebugEnabled()) {
		logger.debug("Class c: " +c);
		logger.debug("Class Name: " +c.getName());
	}
		return c.getName();
	}

	public Method[] getMethods()	{
		return methods;
	}

}
