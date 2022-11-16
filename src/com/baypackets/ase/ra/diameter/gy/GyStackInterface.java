package com.baypackets.ase.ra.diameter.gy;

import com.baypackets.ase.ra.diameter.gy.GyRequest;
import com.baypackets.ase.ra.diameter.gy.GyResourceException;
import com.baypackets.ase.ra.diameter.gy.GyResponse;
import com.baypackets.ase.spi.resource.ResourceContext;

public interface GyStackInterface {
	
	public void init(ResourceContext context) throws GyResourceException;
	
	public void init(String configFileName) throws GyResourceException;
	
	public void start() throws GyResourceException;
	
	public void stop() throws GyResourceException;
	
	public void handleRequest(GyRequest request) throws GyResourceException;
	
	public void handleResponse(GyResponse response) throws GyResourceException;

}

