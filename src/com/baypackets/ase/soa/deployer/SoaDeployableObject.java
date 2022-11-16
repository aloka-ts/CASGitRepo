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
//      File:   SoaDeployableObject.java
//
//      Desc:   This class extends AbstractDeployableObject class. 
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Suresh Kr. Jangir               17/12/07        Initial Creation
//
//***********************************************************************************


package com.baypackets.ase.soa.deployer;

import com.baypackets.ase.common.exceptions.StartupFailedException;
import com.baypackets.ase.container.AseApplicationSession;
import com.baypackets.ase.container.exceptions.ActivationFailedException;
import com.baypackets.ase.container.exceptions.DeactivationFailedException;
import com.baypackets.ase.deployer.AbstractDeployableObject;
import com.baypackets.ase.spi.container.SasApplication;
import com.baypackets.ase.spi.container.SasMessage;
import com.baypackets.ase.spi.deployer.DeployableObject;



public class SoaDeployableObject extends AbstractDeployableObject {
	
	private static final long serialVersionUID = -4071455728543333637L;
	
    public static final short NO_ERROR = 0;
    public static final short DEPLOY_FAILED = 1;
    public static final short START_FAILED = 2;
    public static final short ACTIVATE_FAILED = 3;
	private short _errorStatus;
    
	public SoaDeployableObject() {
		super();
		super.setType(DeployableObject.TYPE_PURE_SOA);
	}

	public void processMessage(SasMessage message) {
		//NUll impl
	}

	public String toString() {
		return this.getDisplayInfo();		
	}

	public void start() throws StartupFailedException {
		try {
			super.start();
		}catch (Exception e) {
			throw new StartupFailedException(e.toString());
		}
	}

	public void activate() throws ActivationFailedException {
		try {
			 super.activate();
		}catch (Exception e) {
			this.setState(SasApplication.STATE_ERROR);
			this.setErrorStatus(ACTIVATE_FAILED);
			throw new ActivationFailedException(e.toString());
		}
	}
		
	public void deactivate() throws DeactivationFailedException {
		try {
			 super.deactivate();
		}catch (Exception e) {
			this.setState(SasApplication.STATE_ERROR);
			throw new DeactivationFailedException(e.toString());
		}
	}

    public short getErrorStatus() {
        return _errorStatus;
	}

    /**
     * Sets the error status of the application.
     */
    public void setErrorStatus(short errorStatus) {
        _errorStatus = errorStatus;
    }
    
	public AseApplicationSession createApplicationSession(String protocol, String sessionId) {
		// no need to implement as this was added in the DeploableObject class only 
		// to create RA application router
		return null;
	}
}




