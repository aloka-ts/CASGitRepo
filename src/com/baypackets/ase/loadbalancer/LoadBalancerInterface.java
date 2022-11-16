/*
 * Created on Mar 8, 2005
 *
 */
package com.baypackets.ase.loadbalancer;

import com.baypackets.ase.control.AsePartitionTable;


/**
 * @author Dana
 * <p>Defines a common interface to work with different load balancers
 * </p>
 */
public interface LoadBalancerInterface {
	public void initialize( ) throws LoadBalancerException;

	/**
	 * Get floating IP and activate it as necessary
	 * @return
	 * @throws LoadBalancerException
	 */
	public String getFIP() throws LoadBalancerException;

	/**
	 * Takeover the floating IP from the subsystem that has subsysId
	 * @param subsysId
	 * @param fip
	 * @return
	 * @throws LoadBalancerException
	 */
	public void takeoverFIP(int subsysId, String fip) throws LoadBalancerException;

	/**
	 * Release floating IP 
	 * @param fip
	 * @throws LoadBalancerException
	 */
	public void releaseFIP(String fip) throws LoadBalancerException;	

	/**
	 * Get Details of the all Floating IPs from the Load Balancer
	 */
	public String getFIPDetails() throws LoadBalancerException;

	/**
	 * Release fip related to the specified subsystem
	 */
	public void releaseFIP( int subSysId , String fip) throws LoadBalancerException;

	/**
	 * Activate the given FIP in the load balancer
	 */
	public void activateFIP(int subsysId, String fip) throws LoadBalancerException;

	/**
	 * Release the given FIP and free it up immediately, that is don't let it go to UNDEFINED LIST in LB
	 */
	public void releaseAndFreeFIP( int subsysId , String floatingIP, String timeStamp) throws LoadBalancerException;

	public void setPartitionTable(AsePartitionTable partitionTable);

	public AsePartitionTable getPartitionTable();
}
