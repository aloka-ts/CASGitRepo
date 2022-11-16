package com.baypackets.ase.resource;

import java.util.EventListener;

public interface ResourceListener extends EventListener {

	public void handleEvent(ResourceEvent event) throws ResourceException;
}
