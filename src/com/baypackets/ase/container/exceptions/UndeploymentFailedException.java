/*
 * UndeploymentFailedException.java
 *
 * Created on August 04, 2004, 5:12 PM
 */
package com.baypackets.ase.container.exceptions;


/**
 * This exception is thrown if an error occurs while un-deploying an 
 * application from the servlet engine.
 *
 * @see com.baypackets.ase.container.Deployer
 * @see com.baypackets.ase.container.AseContext
 *
 * @author Zoltan Medveczky
 */
public final class UndeploymentFailedException extends Exception {
    
    /**
     *
     *
     */
    public UndeploymentFailedException() {
        super();
    }
    
    /**
     * 
     *
     */
    public UndeploymentFailedException(String message) {
        super(message);
    }
    
    /**
     *
     *
     */
    public UndeploymentFailedException(Exception e) {
        super(e.toString());
    }

}
