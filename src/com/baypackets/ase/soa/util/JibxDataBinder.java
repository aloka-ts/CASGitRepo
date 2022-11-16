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
//"Copyright 2008 GENBAND, Inc. All right reserved."
//***********************************************************************************


//***********************************************************************************
//
//      File:   JibxDataBinder.java
//
//      Desc:   This file defines wrapper class for XML to Java data binding using JiBX
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Rajendra Singh                  16/02/08        Initial Creation
//
//***********************************************************************************

package com.baypackets.ase.soa.util;

import java.util.Vector;
import java.io.File;

import org.jibx.binding.Compile;
import org.jibx.runtime.JiBXException;
import org.jibx.ws.wsdl.Jibx2Wsdl;
import org.jibx.xsd2jibx.*;

import org.apache.log4j.Logger;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.common.Registry;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.baypackets.ase.soa.common.SoaConstants;
import com.baypackets.ase.soa.codegenerator.exceptions.CodeGenerationFailedException;

public class JibxDataBinder	{

	private static Logger m_logger = Logger.getLogger(JibxDataBinder.class);

	/**
	* This method will use Jibx2wsdl tool to generate binding.xml, XSD and WSDL files.
	*/
	public void generateBindings(String baseUrl,String svcInterfaceClassName) throws CodeGenerationFailedException	{
		Vector cmdOptions = new Vector();
		String binDir = baseUrl + File.separatorChar + SoaConstants.JAVA_CLASS_DIR_NAME;
		String genDir = baseUrl + File.separatorChar + "gen";
        cmdOptions.add("-p");
        cmdOptions.add(binDir);
        cmdOptions.add("-t");
        cmdOptions.add(genDir);
        //cmdOptions.add("-v");
        cmdOptions.add("-x");
        cmdOptions.add(svcInterfaceClassName);
        int size = cmdOptions.size();
        String[] cmdLineArgs = new String[size];

        for (int i = 0; i < size; i++) {
            cmdLineArgs[i] = (String) cmdOptions.get(i);
        }
		if(m_logger.isDebugEnabled())	{
        	m_logger.debug("Invoking Jibx2Wsdl with options: " + cmdOptions);
		}
        
        try {
            org.jibx.ws.wsdl.Jibx2Wsdl.main(cmdLineArgs);
        } catch (Exception exp) {
			m_logger.error("Error in generating data bindings: " + exp);
            throw new CodeGenerationFailedException("Failed to generate data bindings: " +exp.getMessage());
                
        }
	}

	/**
	* This method will generate binding.xml and Service interface from XSD file
	* at outputDir location
	*/
	public void generateBindingsFromXsd(String outputDir,String xsdUrl) throws CodeGenerationFailedException	{
		Vector cmdOptions = new Vector();
		cmdOptions.add("-d");
        cmdOptions.add(outputDir);
        cmdOptions.add(xsdUrl);
		int size = cmdOptions.size();
        String[] cmdLineArgs = new String[size];

        for (int i = 0; i < size; i++) {
            cmdLineArgs[i] = (String) cmdOptions.get(i);
        }
		if(m_logger.isDebugEnabled())	{
        	m_logger.debug("Invoking XSD Generate.main() with options: " + cmdOptions);
		}
        try {

           Generate.main(cmdLineArgs); 
			if(m_logger.isDebugEnabled())	{
        		m_logger.debug("Successfully generated binding.xml from XSD file: ");
			}
        } catch (Exception e) {
			m_logger.error("Failed to generate bindings from xsd file"+e.getMessage(),e);
			throw new CodeGenerationFailedException("Failed to generate bindings from xsd file"); 	
        }catch(Throwable th)	{
			m_logger.error("Failed to generate bindings from xsd file",th);
            throw new CodeGenerationFailedException("Failed to generate bindings from xsd file"+th.getMessage());
		}
		if(m_logger.isDebugEnabled())   {
            m_logger.debug("Exiting froom generateBindingsFromXsd() method : ");
        }

    }

	/**
	* This method will use Jibx Binding compiler to compile generated bindings.
	* As part of this activity jibx binding compiler modifies existing data object classes
	*/
	public void compileBindings(String baseUrl,String jibxLibDir) throws CodeGenerationFailedException	{
		String binDir = baseUrl + File.separatorChar + SoaConstants.JAVA_CLASS_DIR_NAME;
		String bindingFile = baseUrl + File.separatorChar + "gen" + File.separatorChar + "binding.xml";
		ConfigRepository config = (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
        //String jarDirs = config.getValue(Constants.PROP_OTHER_JAR_DIRS);
		String jarDirs = Constants.ASE_HOME + File.separatorChar + "tools/jibx" ;
		
		String[] pathArr = new String[11];
		pathArr[0] = binDir;
		pathArr[1] = jarDirs + File.separatorChar +"bcel.jar"; 	
		pathArr[2] = jarDirs + File.separatorChar + "jibx-bind.jar"; 	
		pathArr[3] = jarDirs + File.separatorChar + "jibx-extras.jar"; 	
		pathArr[4] = jarDirs + File.separatorChar + "jibx-javatools.jar"; 	
		pathArr[5] = jarDirs + File.separatorChar + "jibx-run.jar"; 	
		pathArr[6] = jarDirs + File.separatorChar + "qdox-1.6.1.jar"; 	
		pathArr[7] = jarDirs + File.separatorChar + "stax-api.jar"; 	
		pathArr[8] = jarDirs + File.separatorChar + "wstx-asl.jar"; 	
		pathArr[9] = jarDirs + File.separatorChar + "xmlpull_1_1_4.jar"; 	
		pathArr[10] = jarDirs + File.separatorChar + "xpp3.jar"; 	
            
		String[] bindings = new String[]{bindingFile};
            
		try{    
            Compile compiler = new Compile();
         	if(m_logger.isDebugEnabled())	{                          
            	compiler.setVerbose(true);
			}
			//Set flag to stop validation of generated binding.xml
			//compiler.setSkipValidate(true);
            compiler.compile(pathArr, bindings);
                    
        } catch(JiBXException jEx) {
            m_logger.error("JiBXException in JiBX binding compilation: "+ jEx);
			//One or more <mapping> elements must be defined in <binding>
            m_logger.error("EAT THIS EXCEPTION SILENTLY: ");
			//throw new CodeGenerationFailedException("Binding compilation Failed: " +jEx.getMessage());
        }catch(Exception exp)	{
            m_logger.error("Exception in JiBX binding compilation: "+ exp);
			throw new CodeGenerationFailedException("Binding compilation Failed: " +exp.getMessage());
	  	}
	}

}
