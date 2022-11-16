package com.baypackets.ase.ra.ro.impl;

import com.baypackets.ase.spi.resource.ResourceContext;
import com.baypackets.ase.ra.ro.RoRequest;

public interface RoStackInterface
{
	public void init(ResourceContext context) throws RoStackException;

	//public void init(String configFileName) throws RoStackException;

	public void start() throws RoStackException;

	public void stop() throws RoStackException;

	public void handleRequest(RoRequest request) throws RoStackException;
}
