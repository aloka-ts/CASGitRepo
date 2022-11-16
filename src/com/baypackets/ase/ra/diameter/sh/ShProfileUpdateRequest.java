package com.baypackets.ase.ra.diameter.sh;

public interface ShProfileUpdateRequest extends ShRequest {
	/**
	 * This method returns a single OctetString value from UserData AVPs
	 * 
	 * @return The OctetString value in byte[].
	 * @throws ShResourceException - If parsing fails
	 */
	public byte[] getRawUserData() 
	throws ShResourceException;

	/**
	 * This method returns a single OctetString value from UserData AVPs
	 * 
	 * @return The OctetString value .
	 * @throws ShResourceException - If parsing fails
	 */
	public String getUserData() 
	throws ShResourceException;
}