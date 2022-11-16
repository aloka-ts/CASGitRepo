/*
 * Created on Mar 8, 2005
 *
 */
package com.baypackets.ase.loadbalancer;

import org.apache.log4j.Logger;

import com.baypackets.ase.common.Registry;
import com.baypackets.ase.util.AseStrings;
import com.baypackets.ase.util.AseUtils;
import com.baypackets.ase.util.Constants;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.baypackets.ase.control.AsePartitionTable;
import com.baypackets.ase.control.ClusterManager;

/**
 * @author Dana
 * <p>
 * </p>
 */
public class DefaultLoadBalancerInterface implements LoadBalancerInterface {
	private static Logger logger = Logger.getLogger(LoadBalancerFactory.class);

	private int selfId;
	private String sipFip;
	private String httpFip;

	public DefaultLoadBalancerInterface(int selfId) {
		this.selfId = selfId;
	}

	public String getFIP() throws LoadBalancerException {
		/*
		ConfigRepository repository = (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
		String value = repository.getValue(Constants.OID_DESIGNATED_ROLE);
		if(value.equalsIgnoreCase("ACTIVE"))    {
			return sipFip;
		}else   {
			return null;
		}
		 */
		return sipFip;
	}

	public void takeoverFIP(int subsysId, String fip) 
			throws LoadBalancerException {
		this.sipFip = fip;
	}

	public void releaseFIP(String fip) throws LoadBalancerException {
		//
	}

	public void releaseFIP(int subSysId , String fip) throws LoadBalancerException {
		//
	}

	public void initialize() throws LoadBalancerException {
		ConfigRepository repository = (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
		String value = repository.getValue(Constants.OID_SIP_FLOATING_IP);
		if (value == null || value.equals("") || value.trim().equals("")) {
			throw new LoadBalancerException("SIP IP Cannot be NULL");
		} 

		sipFip = AseUtils.getIPAddressList(value,false);
		logger.debug("SIP FIP :: " +  sipFip);

		this.sipFip = ClusterManager.adjustFIPFormat(this.sipFip);
	}

	public String getFIPDetails() throws LoadBalancerException
	{
		//
		return null;
	}

	public void activateFIP(int subsysId, String fip) throws LoadBalancerException
	{
		;//
	}

	public void releaseAndFreeFIP( int subsysId , String floatingIP, String timeStamp) throws LoadBalancerException
	{
		;//
	}

	@Override
	public void setPartitionTable(AsePartitionTable partitionTable) {
		// TODO Auto-generated method stub

	}

	@Override
	public AsePartitionTable getPartitionTable() {
		// TODO Auto-generated method stub
		return null;
	}

}
