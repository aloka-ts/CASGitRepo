/*
 * Created on Mar 15, 2005
 *
 */
package com.baypackets.ase.control;

import org.apache.log4j.Logger;

/**
 * @author Dana
 * <p>This class is responsible for create AseRoleResolver object based on 
 * the cluster mode.
 * </p>
 */
public class RoleResolverFactory {
    private static Logger logger = Logger.getLogger(RoleResolverFactory.class);
    private static RoleResolverFactory instance = new RoleResolverFactory();
    private AseRoleResolver roleResolver = null;
	
    private RoleResolverFactory() {
    }
    
    public static RoleResolverFactory getInstance() {
    	return instance;
    }
    
    public AseRoleResolver getRoleResolver() {
    	if (this.roleResolver == null) {
    		//support simple mode only
    		this.roleResolver = new SimpleRoleResolver();
    		if (logger.isInfoEnabled()) {
    			logger.info("SimpleRoleResolver is created.");
    		}
    	}
    	return this.roleResolver;
    }
}