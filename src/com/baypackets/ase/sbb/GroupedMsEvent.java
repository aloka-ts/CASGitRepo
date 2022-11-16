package com.baypackets.ase.sbb;

import com.baypackets.ase.sbb.MediaServer;
import com.baypackets.ase.sbb.SBBEvent;

/**
 * This event is generated by GroupedMSSBB to return name of Connected MediaServer
 */
public class GroupedMsEvent extends SBBEvent {

	/** name of connected media server */
	private MediaServer connectedMediaServer;

	public MediaServer getConnectedMediaServer() {
		return connectedMediaServer;
	}

	public void setConnectedMediaServer(MediaServer connectedMediaServer) {
		this.connectedMediaServer = connectedMediaServer;
	}

}
