package com.baypackets.ase.spi.radius;


public interface RadiusService {

    public void doAccess(RadiusSession radiusSession)
	    throws RadiusServiceException;

    public void doAccounting(RadiusSession radiusSession)
	    throws RadiusServiceException;

}