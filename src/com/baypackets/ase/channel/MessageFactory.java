/*
 * Created on Oct 26, 2004
 *
 */
package com.baypackets.ase.channel;

/**
 * @author Ravi
 */
public interface MessageFactory {
	
	public PeerMessage createMessage(short type, short mode) throws ChannelException;
}
