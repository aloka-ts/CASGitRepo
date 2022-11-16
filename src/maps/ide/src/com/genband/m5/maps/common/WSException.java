package com.genband.m5.maps.common;

import javax.xml.ws.WebFault;

@WebFault(name="faultDetail", targetNamespace="...")
public class WSException extends Exception {

	private static final long serialVersionUID = -8599116444622523343L;
	public WSException () {
		super ();
	}
	public WSException (String msg) {
		super(msg);
	}
	public WSException (String msg, Throwable t) {
		super(msg, t);	
	}
}

