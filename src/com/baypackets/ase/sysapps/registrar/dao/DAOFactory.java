package com.baypackets.ase.sysapps.registrar.dao;

import java.util.*;
import org.apache.log4j.Logger;

import com.baypackets.ase.sysapps.registrar.common.Configuration;


/** Singleton */

/** It provieds a factory for obtaining data access objects
*/

public class DAOFactory  
{
	private static final Logger logger = Logger.getLogger(DAOFactory.class);

	public static final String DAO_CLASS = "com.baypackets.ase.sysapps.registrar.dao.rdbms.BindingsDAOImpl";
   
	private static DAOFactory m_instance = null;

	private DAOFactory() {
	}

	public static synchronized DAOFactory getInstance() {

		if (m_instance == null ) {
			m_instance = new DAOFactory();
		}
		return m_instance;
	}


	public BindingsDAO getBindingsDAO() {
		
		/*
		 *	Deployment descriptor has name of supported DAO. So 
		 *  extract from Configuration which DAO needs to be created 
		 */
		Configuration config=Configuration.getInstance();

		String daoClass = config.getParamValue("DAO_CLASS");
		BindingsDAO dao = null;
		try {
			dao = (BindingsDAO)Class.forName(daoClass).newInstance();
		} catch(Exception exp) {
			logger.error("Could not initialize DAO class", exp);
		}

		return dao;	
	}


}

