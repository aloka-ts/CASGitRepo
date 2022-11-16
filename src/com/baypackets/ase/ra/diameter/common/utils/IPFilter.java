package com.baypackets.ase.ra.diameter.common.utils;

import org.apache.log4j.Logger;

import com.baypackets.ase.resource.ResourceException;
import com.traffix.openblox.diameter.coding.DiameterAddressVSAvp;

//public class IPFilter extends FilterRule {
public class IPFilter {

	private static Logger logger = Logger.getLogger(IPFilter.class.getName());
	public static final long vendorId = 0L;

	private com.traffix.openblox.core.utils.IPFilter stackObj;

	public IPFilter(com.traffix.openblox.core.utils.IPFilter stkObj){
		//super(stkObj);
		this.stackObj=stkObj;
	}

	/**
	 *  This method returns the Icmp type.
	 */
	public java.lang.String getIcmpTypes( ) throws ResourceException {
		if(logger.isDebugEnabled()){
			logger.debug("Inside getIcmpTypes()");
		}
		return stackObj.getIcmpTypes();
	}

	/**
	 *  This method returns the IP Options.
	 */
	public java.lang.String getIpOptions( ) throws ResourceException {
		if(logger.isDebugEnabled()){
			logger.debug("Inside getIpOptions()");
		}
		return stackObj.getIpOptions();
	}


	/**
	 *  This method returns the options
	 */
	public java.lang.String getOptions( ) throws ResourceException {
		if(logger.isDebugEnabled()){
			logger.debug("Inside getOptions()");
		}
		return stackObj.getOptions();
	}


	/**
	 *  This method returns the protocol.
	 */
	public java.lang.String getProtocol( ) throws ResourceException {
		if(logger.isDebugEnabled()){
			logger.debug("Inside getProtocol()");
		}
		return stackObj.getProtocol();
	}


	/**
	 *  This method returns the TCP flag.
	 */
	public java.lang.String getTcpFlags( ) throws ResourceException {
		if(logger.isDebugEnabled()){
			logger.debug("Inside getTcpFlags()");
		}
		return stackObj.getTcpFlags();
	}


	/**
	 *  This method returns the TCP options.
	 */
	public java.lang.String getTcpOptions( ) throws ResourceException {
		if(logger.isDebugEnabled()){
			logger.debug("Inside getTcpOptions()");
		}
		return stackObj.getTcpOptions();
	}


	/**
	 *  This method returns if filter is established.
	 */
	public boolean isEstablished( ) throws ResourceException {
		if(logger.isDebugEnabled()){
			logger.debug("Inside isEstablished()");
		}
		return stackObj.isEstablished();
	}


	/**
	 *  This method returns if it is fragmented.
	 */
	public boolean isFragment( ) throws ResourceException {
		if(logger.isDebugEnabled()){
			logger.debug("Inside isFragment()");
		}
		return stackObj.isFragment();
	}
	
	public com.traffix.openblox.core.utils.IPFilter getStackObject(){
		return this.stackObj;
	}

}