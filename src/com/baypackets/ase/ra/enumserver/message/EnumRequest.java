package com.baypackets.ase.ra.enumserver.message;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Map;

import com.baypackets.ase.resource.Request;

public interface EnumRequest extends Request {

	/** The Constant REQUEST_PENDING. */
	public static final short REQUEST_PENDING = 0;	

	/** The Constant REQUEST_ACTIVE. */
	public static final short REQUEST_ACTIVE = 1;	

	/** The Constant REQUEST_INACTIVE. */
	public static final short REQUEST_INACTIVE = 2;
	
	
	public byte[] getData();
	public void setData(byte[] data);
	
	/**
	 * Gets the status.
	 *
	 * @return the status
	 */
	public int getStatus();
	public String getkey();
	public String getAUS();
	public InetAddress getAddress() ;
	public int getPort();
}
