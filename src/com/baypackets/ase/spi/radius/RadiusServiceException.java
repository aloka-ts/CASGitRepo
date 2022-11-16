package com.baypackets.ase.spi.radius;

/**
 * An exception which occurs on radius service impl side.
 */
public class RadiusServiceException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a RadiusException with a message.
     * @param message
     *            error message
     */
    public RadiusServiceException(Throwable throwable) {
	super(throwable);
    }

}
