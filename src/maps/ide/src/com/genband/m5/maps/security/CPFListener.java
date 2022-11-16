package com.genband.m5.maps.security;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;


public class CPFListener implements ServletContextListener {

	private static final Logger logger = Logger
			.getLogger("com.genband.m5.maps.security");

	
	public void contextInitialized(ServletContextEvent event) {
		try {
			logger.info("servlet context initialized");
			Context ctx = new InitialContext ();
			logger.info("Context inside listener is : " + ctx.toString());
			Object o = ctx.lookup ("maps/LocalCPFDataLoader");
			logger.info ("Lookup successful? - " + (o != null));
			logger.debug ("Class of o is - " + o.getClass().getName());
			ICPFDataLoader loader = (ICPFDataLoader) o;
			//loader.uploadOrganizationData();
			loader.uploadSecurityData();
			logger.info("done upload of data");
		} catch (NamingException e) {
			logger.info("Got Exception in listener class ");
			e.printStackTrace();
		}
	}

	public void contextDestroyed(ServletContextEvent event) {
		logger.debug("servlet context destroyed");
	}

}
