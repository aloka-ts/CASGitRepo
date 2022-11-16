package com.baypackets.ase.ra.radius;

public class RadiusException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 15145161541L;
	
	public RadiusException(){
		super();
	}
	public RadiusException(String message) {
		super(message);
	}

	public RadiusException(String message, Throwable cause) {
		super(message, cause);
	}

	public RadiusException(Throwable cause) {
		super(cause);
	}
}