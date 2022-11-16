package com.baypackets.ase.deployer;

import com.baypackets.ase.spi.deployer.DeployableObject;
import com.baypackets.ase.spi.deployer.Deployer;

public class DefaultDDHandler implements DDHandler{

	private Deployer deployer = null;
	
	public DefaultDDHandler() {
		super();
	}

	public Deployer getDeployer() {
		return deployer;
	}

	public void setDeployer(Deployer deployer) {
		this.deployer = deployer;
	}

	public void parse(DeploymentDescriptor dd, DeployableObject object)  throws Exception{
	}
}
