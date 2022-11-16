package com.baypackets.ase.ra.ro.impl;

import com.baypackets.ase.ra.ro.RoResponse;
import com.baypackets.ase.ra.ro.RoResourceEvent;
import com.baypackets.ase.resource.ResourceException;

public interface RoResourceAdaptor {
	public void deliverResponse(RoResponse resp) throws ResourceException;

	public void deliverEvent(RoResourceEvent event) throws ResourceException;
}

