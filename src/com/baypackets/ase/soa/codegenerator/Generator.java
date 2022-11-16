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
//      File:   Generator.java
//
//      Desc:   This file defines an interface to be implemented by all concrete
//			code generators.
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Rajendra Singh				19/12/07        Initial Creation
//
//***********************************************************************************

package com.baypackets.ase.soa.codegenerator;

import java.util.Map;
import com.baypackets.ase.soa.codegenerator.exceptions.CodeGenerationFailedException;

public interface Generator	{

	Map generate(String baseUrl,String wsdl,String wsName,String soaContextName,
					ClassLoader cl) throws CodeGenerationFailedException;

}


