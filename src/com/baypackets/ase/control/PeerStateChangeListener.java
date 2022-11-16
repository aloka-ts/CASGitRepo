package com.baypackets.ase.control;

import java.util.EventListener;

public interface PeerStateChangeListener extends EventListener
{
	public int handleEvent (PeerStateChangeEvent psce) throws PeerStateChangeException;
}
