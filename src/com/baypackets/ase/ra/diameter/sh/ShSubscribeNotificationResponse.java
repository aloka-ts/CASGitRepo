package com.baypackets.ase.ra.diameter.sh;

import java.util.Date;

public interface ShSubscribeNotificationResponse extends ShResponse {


	public byte[] getRawUserData()
	throws ShResourceException;

	public String getUserData();

	public String getWildcardedIMPU()
	throws ShResourceException;

	public String getWildcardedPSI()
	throws ShResourceException;


	public Date getExpiryTime()
	throws ShResourceException;

}
