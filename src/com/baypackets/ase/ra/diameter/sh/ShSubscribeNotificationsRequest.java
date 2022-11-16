package com.baypackets.ase.ra.diameter.sh;

import java.util.Date;

public interface ShSubscribeNotificationsRequest extends ShRequest {

	public int[] getDataReferences()
	throws ShResourceException;

	public String[] getDSAITags()
	throws ShResourceException;

	public Date getExpiryTime()
	throws ShResourceException;

	public int[] getIdentitySets()
	throws ShResourceException;

	public int getOneTimeNotification()
	throws ShResourceException;

	public int getSendDataIndication()
	throws ShResourceException;

	public int getSubsReqType()
	throws ShResourceException;
}
