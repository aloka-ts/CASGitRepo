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
//      File:   ProvisionerDAOFactory.java
//
//      Desc:   This Factory class creates the instance of the Provisioner DAO.
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Suresh Kr. Jangir               18/12/07        Initial Creation
//
//***********************************************************************************


package com.baypackets.ase.soa.provisioner;

import org.apache.log4j.Logger;


public class ProvisionerDAOFactory {
	
	private static Logger m_logger = Logger.getLogger(ProvisionerDAOFactory.class);
	
	private static ProvisionerDAOFactory m_provisionerFactory = null;


	private ProvisionerDAOFactory() {
		// made it private for Singleton Pattern
	}

	public static synchronized ProvisionerDAOFactory getInstance() {
		if(null == m_provisionerFactory) {
			m_provisionerFactory = new ProvisionerDAOFactory();
		} 
		return m_provisionerFactory;
	}


	public SoaProvisionerDAO getSoaProvisionerDAO(int type) {
		SoaProvisionerDAO dao = null;
		if(type == SoaProvisionerDAO.FILE_BASED) {
			if(m_logger.isDebugEnabled()) {
				m_logger.debug("Creating new instance of FileProvisionerDAO");
			}
			dao = new FileProvisionerDAO();
		}
		return dao;
	}

}
