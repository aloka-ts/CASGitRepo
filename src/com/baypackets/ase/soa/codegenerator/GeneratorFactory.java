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
//      File:   GeneratorFactory.java
//
//      Desc:   This file acts as a factory for creating and returning an appropriate 
//		Code Generator
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Rajendra Singh				19/12/07        Initial Creation
//
//***********************************************************************************

package com.baypackets.ase.soa.codegenerator;

import com.baypackets.ase.soa.common.SoaConstants;

public class GeneratorFactory	{

	public static Generator getGenerator(int opType,int wsType)	{
		Generator generator = null;
		if(wsType == SoaConstants.WS_TYPE_SERVICE)	{
			switch(opType)	{
				case 1:
						generator = new DeployServiceGenerator();
						break;
				case 2:
						generator = new ProvisionServiceGenerator();
						break;
				case 3:
						generator = new UpgradeServiceGenerator();
						break;
				case 4:
						generator = new UpdateServiceGenerator();
						break;
			}
		}
		if(wsType == SoaConstants.WS_TYPE_APP)  {
			switch(opType)  {
                case 1:
                        generator = new DeployAppGenerator();
                        break;
				case 3:
                        generator = new UpgradeAppGenerator();
                        break;

			}
                                                                                                                   
        }

		return generator;
	}
}

