/*
 * Created on Oct 28, 2004
 *
 */
package com.baypackets.ase.replication;

import com.baypackets.ase.channel.ChannelException;
import com.baypackets.ase.channel.MessageFactory;
import com.baypackets.ase.channel.PeerMessage;
import com.baypackets.ase.control.MessageTypes;
import com.baypackets.ase.router.acm.AppDataMessage;

/**
 * @author Ravi
 */
public class ReplicationMessageFactory implements MessageFactory {

	/* (non-Javadoc)
	 * @see com.baypackets.ase.channel.MessageFactory#createMessage(short, short)
	 */
	public PeerMessage createMessage(short type, short mode) throws ChannelException {
		PeerMessage msg = null;
		switch(type){
			case MessageTypes.REPLICATION_MESSAGE:
				msg = new ReplicationMessage(mode);
				break;
//			case MessageTypes.APP_DATA_SYNC_MESSAGE:
//				msg = new AppDataMessage(mode);
//				break;
			default:
		}
		return msg;
	}
	
	public void releaseMessage(PeerMessage peerMessage) throws Exception {
		peerMessage.cleanup();
	}
	
}
