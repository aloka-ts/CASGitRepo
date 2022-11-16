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
//      File:   UpgradeAppGenerator.java
//
//      Desc:   This file defines code generator for application upgrade operation
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Rajendra Singh                  04/01/08        Initial Creation
//
//***********************************************************************************
                                                                                                                        
                                                                                                                        
package com.baypackets.ase.soa.codegenerator;
                                                                                                                        
import java.util.Map;
import java.util.Hashtable;
import java.util.Iterator;
import com.baypackets.ase.soa.common.SoaConstants;
import com.baypackets.ase.soa.codegenerator.exceptions.CodeGenerationFailedException;

public class UpgradeAppGenerator implements Generator  {

	public Map generate(String baseUrl,String wsdl,String wsName,
						String soaContextName,ClassLoader cl)
						throws CodeGenerationFailedException	{

		Map map = null;
        Generator deployAppGenerator =
						GeneratorFactory.getGenerator(SoaConstants.OPERATION_DEPLOY,
          												SoaConstants.WS_TYPE_APP);
        if(deployAppGenerator != null)   {
            map = deployAppGenerator.generate(baseUrl,wsdl,wsName,soaContextName,cl);
        }
        return map;
	} 

}
