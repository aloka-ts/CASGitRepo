package com.baypackets.ase.ra.diameter.common.utils;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.common.enums.TransportProtocolEnum;
import com.baypackets.ase.resource.ResourceException;

//public class URI extends FilterRule {
public class URI {

	private static Logger logger = Logger.getLogger(URI.class.getName());
	public static final long vendorId = 0L;

	private com.traffix.openblox.core.utils.URI stackObj;

	public URI(com.traffix.openblox.core.utils.URI stkObj){
		//super(stkObj);
		this.stackObj=stkObj;
	}

	public int compareTo(java.lang.Object obj) {
		return stackObj.compareTo(obj);
	}

	public boolean equals(java.lang.Object obj) {
		return stackObj.equals(obj);
	}

	public java.net.InetAddress getAddress() {
		return stackObj.getAddress();
	}

	public java.lang.String getFQDN() {
		return stackObj.getFQDN();
	}

	public java.lang.String getHost() {
		return stackObj.getHost();
	}

	public java.lang.String getPath() {
		return stackObj.getPath();
	}

	public int getPort() {
		return stackObj.getPort();
	}

	public java.lang.String getProtocolParam() {
		return stackObj.getProtocolParam();
	}

	public Scheme getScheme() {
		return Scheme.getContainerObj(stackObj.getScheme());
	}

	public TransportProtocolEnum getTransportProtocol() {
		return TransportProtocolEnum.getContainerObj(stackObj.getTransportProtocol());
	}

	public boolean isSecure() {
		return stackObj.isSecure();
	}

	public void setEncrypted(boolean encrypted) {
		stackObj.setEncrypted(encrypted);
	}
	
	public com.traffix.openblox.core.utils.URI getStackObject(){
		return this.stackObj;
	}

}