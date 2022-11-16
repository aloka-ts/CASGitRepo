package com.baypackets.ase.spi.deployer;

public interface DeploymentListener {

	public void stateChangeCompleted(DeployableObject target, short prevState, short requestedState);
	
	public void stateChangeFailed(DeployableObject target, short prevState, short requestedState);
}
