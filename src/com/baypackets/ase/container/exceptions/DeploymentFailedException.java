/*
 * DeploymentFailedException.java
 *
 * Created on August 04, 2004, 5:12 PM
 */
package com.baypackets.ase.container.exceptions;


/**
 * This exception is thrown if an error occurs while deploying an application 
 * in the servlet engine.
 *
 * @see com.baypackets.ase.container.Deployer
 * @see com.baypackets.ase.container.AseContext
 *
 * @author Zoltan Medveczky
 */
public final class DeploymentFailedException extends Exception {
    
    public DeploymentFailedException(String message, Throwable cause) {
		super(message, cause);
	}

	public DeploymentFailedException(Throwable cause) {
		super(cause);
	}

	/**
     *
     *
     */
    public DeploymentFailedException() {
        super();
    }
    
    /**
     * 
     *
     */
    public DeploymentFailedException(String message) {
        super(message);
    }
    
    /**
     *
     *
     */
    public DeploymentFailedException(Exception e) {
        super(e.toString());
    }

}
