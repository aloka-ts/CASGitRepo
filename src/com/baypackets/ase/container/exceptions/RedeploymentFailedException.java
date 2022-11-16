/*
 * RedeploymentFailedException.java
 *
 * Created on April 01, 2006, 6:15 PM
 */
package com.baypackets.ase.container.exceptions;


/**
 * This exception is thrown if an error occurs while redeploying an application
 * in the servlet engine.
 *
 *
 * @author Neeraj Kumar Jadaun
 */
public final class RedeploymentFailedException extends Exception {

    public RedeploymentFailedException(String message, Throwable cause) {
                super(message, cause);
        }

        public RedeploymentFailedException(Throwable cause) {
                super(cause);
        }

        /**
     *
     *
     */
    public RedeploymentFailedException() {
        super();
    }

    /**
     *
     *
     */
    public RedeploymentFailedException(String message) {
        super(message);
    }

    /**
     *
     *
     */
    public RedeploymentFailedException(Exception e) {
        super(e.toString());
    }
}
