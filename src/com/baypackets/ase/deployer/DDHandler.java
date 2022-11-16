package com.baypackets.ase.deployer;

import com.baypackets.ase.spi.deployer.DeployableObject;
import com.baypackets.ase.spi.deployer.Deployer;

public interface DDHandler {
	
	public void setDeployer(Deployer deployer);
	
	public void parse(DeploymentDescriptor dd, DeployableObject object)
					throws Exception;
}
