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
//      File:   SoaProvisioner.java
//
//      Desc:   This interface includes the methods provided by the SOA Provisioner Module.
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Suresh Kr. Jangir               17/12/07        Initial Creation
//
//***********************************************************************************


package com.baypackets.ase.soa.provisioner;

import org.apache.log4j.Logger;
import java.net.URI;
import java.util.List;

import com.baypackets.ase.soa.exceptions.SoaException;


/**
 * SoaProvisioner is the abstraction for provisioning the remote services in the M5 SAS container.
 * It generates the code using the Code generator, creates the proxy and stubs and 
 * register them in the service map. 
 * After adding it in the Service Map the service becomes available to all the service 
 * or application deployed on the M5 SAS Container. 
 *
 * @author Suresh Kr. Jangir
 */

public interface SoaProvisioner {

						
	public void initialize();		

	public void start();		

	public void addRemoteService(String name, String version, URI uri) throws SoaException;

	public void updateRemoteService(String name, String version, URI uri) throws SoaException;

	public void removeRemoteService(String name);

	public List<AseRemoteService> listServices(); 

}

