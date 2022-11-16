package com.baypackets.ase.ra.diameter.rf;


import com.baypackets.ase.resource.ResourceException;

public class RfResourceException extends ResourceException {
//public class RfResourceException extends Throwable {

	public RfResourceException() {
		super();
	}

	public RfResourceException(String message) {
		super(message);
	}

	public RfResourceException(String message, Throwable cause) {
		super(message, cause);
	}

	public RfResourceException(Throwable cause) {
		super(cause);
	}

}
