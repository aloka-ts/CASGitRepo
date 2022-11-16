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
//      File:   FileProvisionerDAO.java
//
//      Desc:   This Factory class creates the instance of the Provisioner DAO.
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Suresh Kr. Jangir               18/12/07        Initial Creation
//
//***********************************************************************************


package com.baypackets.ase.soa.provisioner;

import java.io.*;

import java.net.URI;
import java.util.*;

import org.apache.log4j.Logger;

import com.baypackets.ase.util.AseStrings;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.container.exceptions.FinderException;
import com.baypackets.ase.container.exceptions.PersistenceException;


public class FileProvisionerDAO implements SoaProvisionerDAO {

	private static String fileName = "soaRemoteServices.properties";
	private static Logger m_logger = Logger.getLogger(FileProvisionerDAO.class);


	public List<AseRemoteService> loadRemoteServices() throws FinderException {
			
		List<AseRemoteService> list = new ArrayList<AseRemoteService>();
		try {	
		
			File file = new File(Constants.ASE_HOME+"/apps/db/"+fileName);
			if(! file.exists()) {
				if(m_logger.isDebugEnabled()) {
					m_logger.debug("File not present: Not initializing any remote Service");
				}
				return list;
			}
			FileInputStream fstream = new FileInputStream(Constants.ASE_HOME+"/apps/db/"+fileName);
			DataInputStream dataIn = new DataInputStream(fstream);
			BufferedReader reader = new BufferedReader(new InputStreamReader(dataIn));
			String strLine = null;
			while((strLine = reader.readLine()) != null) {
				AseRemoteService service = new AseRemoteService();
				StringTokenizer tokens = new StringTokenizer(strLine, AseStrings.COMMA);
				service.setServiceName(tokens.nextToken());
				service.setVersion(tokens.nextToken());
				service.setWsdlUri(new URI(tokens.nextToken()));
				list.add(service);
			}
		} catch(Exception e) {
			m_logger.error("Unable to load persistant information",e);
			throw new FinderException("Unable to load persistant information");
		}
		return list;
	}

	public void persistRemoteService(AseRemoteService service) throws PersistenceException {
	
		PrintWriter writer = null;	
		try {
			File dbDir = new File(Constants.ASE_HOME+"/apps", "db");

			 // Create the database directory if it doesn't already exist.
			if (!dbDir.exists()) {
				dbDir.mkdirs();
			}

			File contextFile = new File(dbDir,fileName);
			
			if (!contextFile.exists()) {
				contextFile.createNewFile();
			}
			
			//delete the service Information if alreadt there.
			this.removeServiceInfo(contextFile,service.getServiceName());

			//Write the Service Information 
			writer = new PrintWriter(new FileWriter(contextFile,true));
			StringBuffer buffer = new StringBuffer();
			buffer.append(service.getServiceName());
			buffer.append(AseStrings.COMMA);
			buffer.append(service.getVersion());
			buffer.append(AseStrings.COMMA);
			buffer.append(service.getWsdlUri());
			writer.println(buffer.toString());
			writer.close();
		} catch(Exception exp) {
			m_logger.error("Unable to persist Remote Service Information", exp);
			throw new PersistenceException("Unable to persist Remote Service Information");
		} finally {
			if(writer != null) {
				writer.close();
			}
		}
	}

	public void removeRemoteService(String service) {
		if(m_logger.isDebugEnabled()) {
			m_logger.debug("Entering removeRemoteService(): Service = "+service);
		}
		try {
			this.removeServiceInfo(new File(Constants.ASE_HOME+"/apps/db", fileName), service);
		} catch (Exception exp) {
			m_logger.error("Unable to remove Remote Service Information", exp);
		}

	}

	private void removeServiceInfo(File file, String serviceName) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			File tempFile = new File(file.getAbsolutePath()+".temp");
			PrintWriter out = new PrintWriter(new FileWriter(tempFile));
			String line = null;
			
			while((line = br.readLine()) != null) {
				StringTokenizer tokens = new StringTokenizer(line, AseStrings.COMMA);
				if(m_logger.isDebugEnabled()) {
				m_logger.debug("TESTSURESH: name ="+serviceName);
				m_logger.debug("TESTSURESH: tokens ="+tokens);
				}
				if(serviceName.equals(tokens.nextToken())) {
					continue;
				} else {
					out.println(line);
					out.flush();
				}
			}
			out.close();
			br.close();

			if(! file.delete()) {
				m_logger.error("Unable to remove file");
				return;
			}

			// rename the backup file 
			if(! tempFile.renameTo(file)) {
				m_logger.error("Unable to rename the temp file");
				return;
			}
		} catch(Exception e) {
			m_logger.error("Unable to remove service Info",e);
		}
	}
		
		
}
	 
	



