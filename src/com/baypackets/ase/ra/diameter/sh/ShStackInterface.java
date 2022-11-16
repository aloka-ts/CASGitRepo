package com.baypackets.ase.ra.diameter.sh;

import com.baypackets.ase.spi.resource.ResourceContext;

public interface ShStackInterface {

	public void init(ResourceContext context) throws ShResourceException;

	public void init(String configFileName) throws ShResourceException;

	public void startStack() throws ShResourceException;

	public void stopStack() throws ShResourceException;

	public void handleRequest(ShRequest request) throws ShResourceException;

	public void handleResponse(ShResponse response) throws ShResourceException;

}
