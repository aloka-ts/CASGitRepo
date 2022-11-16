package com.baypackets.ase.spi.deployer;

import java.util.Iterator;

public interface DeployerFactory {
	
	public Deployer getDeployer(int type);

	 public Iterator getAllDeployer() ;
}
