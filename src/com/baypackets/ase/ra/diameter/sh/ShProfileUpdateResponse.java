package com.baypackets.ase.ra.diameter.sh;



public interface ShProfileUpdateResponse extends ShResponse {

	public String getWildcardedIMPU()
	throws ShResourceException;

	public String getWildcardedPSI()
	throws ShResourceException;
}
