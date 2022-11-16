/**
 * Filename:	RoResourceException.java
 *
 */

package com.baypackets.ase.ra.ro;

import com.baypackets.ase.resource.ResourceException;


public class RoResourceException extends ResourceException {

	public RoResourceException() {
		super();
	}

	public RoResourceException(String message) {
		super(message);
	}

	public RoResourceException(String message, Throwable cause) {
		super(message, cause);
	}

	public RoResourceException(Throwable cause) {
		super(cause);
	}
}

