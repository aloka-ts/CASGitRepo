/*
 * Created on Mar 18, 2005
 *
 */
package com.baypackets.ase.control;

import org.apache.log4j.Logger;

/**
 * @author Dana
 * <p>This class is responsible for create AsePartition object based on 
 * the configuration.
 * </p>
 */
public class PartitionFactory {
    private static Logger logger = Logger.getLogger(RoleResolverFactory.class);
    private static PartitionFactory instance = new PartitionFactory();
	
    private PartitionFactory() {
    }
    
    public static PartitionFactory getInstance() {
    	return instance;
    }
    
    public AsePartition createPartition() {
    	return new SimplePartition();
    }
}