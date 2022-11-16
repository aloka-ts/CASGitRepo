/**
 * 
 */
package com.baypackets.ase.replication;

import com.baypackets.ase.channel.PeerMessage;

/**
 * @author reeta
 *
 */
public interface AppDataMessageListener {
	
		public void handleDataMessage(PeerMessage msg);
	

}
