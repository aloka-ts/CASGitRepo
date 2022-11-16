/*
 * Created on Mar 8, 2005
 *
 */
package com.baypackets.ase.loadbalancer;

import org.apache.log4j.Logger;

import com.baypackets.ase.common.Registry;
import com.baypackets.ase.util.AseStrings;
import com.baypackets.ase.util.Constants;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;

/**
 * @author Dana
 * <p>
 * </p>
 */
public class LoadBalancerFactory {
    private static Logger logger = Logger.getLogger(LoadBalancerFactory.class);
    private static LoadBalancerFactory instance = new LoadBalancerFactory();
    private static LoadBalancerInterface loadbalancerInterface = null;
    
    private LoadBalancerFactory() {
    }
	
    public static LoadBalancerFactory getInstance() {
    	return instance;
    }
    
    /**
     * Return an instance of BpLoadBalancerInterface if EMS is installed
     * and BayPackets' Load Balancer FIP and port are available. Otherwise, 
     * an instance of DefaultLoadBalancerInerface is returned.
     * 
     * @return LoadBalancerInterface
     */
    public LoadBalancerInterface getLoadBalancerInterface(int subsystemId) {
    	if (loadbalancerInterface == null) {
			try {
	    		ConfigRepository repository = (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
				String value = repository.getValue(Constants.OID_LOADBALANCER_FIP);
				if (value == null || value.equals(AseStrings.BLANK_STRING) || value.equals("0.0.0.0")) {
		    		loadbalancerInterface = new DefaultLoadBalancerInterface(subsystemId);
		    		((DefaultLoadBalancerInterface)loadbalancerInterface).initialize();
				} else {
					String fip = value;
					value = repository.getValue(Constants.OID_LOADBALANCER_PORT);
					int port = Integer.parseInt(value);
					loadbalancerInterface = new BpLoadBalancerInterface(subsystemId, fip, port);
				}
			} catch (Exception ex){
				loadbalancerInterface = null;
				logger.error(ex);
			}
    	}
		return loadbalancerInterface;
    }
    
}
