package com.baypackets.ase.jmxmanagement;

import java.net.URI;

import com.baypackets.ase.soa.exceptions.SoaException;

public interface SOAServiceProvisioningMBean {
	
	public boolean provisionService(String name, String version, URI uri) throws SoaException;

	public boolean updateService(String name, String version, URI uri) throws SoaException;

	public boolean removeService(String name);
	
	public java.util.Hashtable listProvisionedServices();

}
