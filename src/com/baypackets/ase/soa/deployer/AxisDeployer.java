package com.baypackets.ase.soa.deployer;

import com.baypackets.ase.spi.deployer.DeployableObject;
import com.baypackets.ase.deployer.ApplicationDeployer;

public class AxisDeployer extends ApplicationDeployer {

	public AxisDeployer() {
		super();
	}

	public String getDAOClassName() {
		return null;
	}
	
	public short getType(){
		return DeployableObject.TYPE_SOAP_SERVER;
	}
}
