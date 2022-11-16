package com.baypackets.ase.ra.radius;

import com.baypackets.ase.resource.ResourceException;

public class RadiusResourceException extends ResourceException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public RadiusResourceException() {
		super();
	}

	public RadiusResourceException(String message) {
		super(message);
	}

	public RadiusResourceException(String message, Throwable cause) {
		super(message, cause);
	}

	public RadiusResourceException(Throwable cause) {
		super(cause);
	}
}
