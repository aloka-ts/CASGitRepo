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
//      File:   CodeGenerationFailedException.java
//
//      Desc:   This file defines an exception to be thrown by code generator in case of any error
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Rajendra Singh                  18/12/07        Initial Creation
//
//***********************************************************************************



package com.baypackets.ase.soa.codegenerator.exceptions;

public class CodeGenerationFailedException extends Exception 	{

	public CodeGenerationFailedException()	{
		super();
	}

	public CodeGenerationFailedException(String message)  {
          super(message);
     }

}

