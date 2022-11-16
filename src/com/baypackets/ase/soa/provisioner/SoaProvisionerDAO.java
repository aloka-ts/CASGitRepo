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
//      File:   SoaProvisionerDAO.java
//
//      Desc:   This interface includes all the method required by SOA Provisioner DAO.
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Suresh Kr. Jangir               18/12/07        Initial Creation
//
//***********************************************************************************


package com.baypackets.ase.soa.provisioner;

import java.util.List;
import com.baypackets.ase.container.exceptions.FinderException;
import com.baypackets.ase.container.exceptions.PersistenceException;


public interface SoaProvisionerDAO {

	public static final int FILE_BASED = 1;
	
	public List<AseRemoteService> loadRemoteServices() throws FinderException;

	public void persistRemoteService(AseRemoteService service) throws PersistenceException;

	public void removeRemoteService(String service);

}
	 
	



