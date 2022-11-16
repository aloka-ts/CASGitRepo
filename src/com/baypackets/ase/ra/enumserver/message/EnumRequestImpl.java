package com.baypackets.ase.ra.enumserver.message;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Map;

import org.apache.log4j.Logger;

import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.resource.Response;

public class EnumRequestImpl extends EnumMessage implements EnumRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private byte[] data = null;

	private String key = null;

	private String aus = null; // application unique string
	
	private InetAddress address=null;

	private int port=-1;
	
	private transient DatagramSocket so;

	public void setSo(DatagramSocket socket) {
		this.so = socket;
	}

	public String getKey() {
		return key;
	}

	public String getAus() {
		return aus;
	}

	public byte[] getData() {
		return data;
	}
	
	public DatagramSocket getSo() {
		return so;
	}


	public void setData(byte[] data) {

		if (logger.isDebugEnabled())
			logger.debug("in setData(). " + data);
		this.data = data;
	}

	private Logger logger = Logger.getLogger(EnumRequestImpl.class);
	private int status = REQUEST_PENDING;

	@Override
	public Response createResponse(int arg0) throws ResourceException {
		if (logger.isDebugEnabled())
			logger.debug("in createResponce().");
		// EnumResponse enumResponse = new EnumResponseImpl(this);
		// ((EnumMessage)enumResponse).setHttpResourceAdaptor(getEnumResourceAdaptor());
		return null;// httpResponse;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Override
	public int getStatus() {
		return status;
	}

	public void setkey(String key) {
		// TODO Auto-generated method stub
		this.key = key;

	}

	public void setAUS(String aus) {
		// TODO Auto-generated method stub
		this.aus = aus;
	}

	public void setAddress(InetAddress address) {
		this.address=address;

	}

	public void setPort(int port) {
		this.port=port;

	}
	
	public InetAddress getAddress() {
		return address;
	}

	public int getPort() {
		return port;
	}

	@Override
	public String  getkey() {
		// TODO Auto-generated method stub
		return this.key;
	}

	@Override
	public String getAUS() {
		return this.aus;
		
	}


}
