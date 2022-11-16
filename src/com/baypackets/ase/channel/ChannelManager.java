package com.baypackets.ase.channel;


public interface ChannelManager
{
	public void peerDown (AseSubsystem subsystem);

	public void peerUp (AseSubsystem subsystem);

	public void messageDeliveryFailed 
		(PeerMessage msg, AseSubsystem[] subsystem);
	
	//below two API used to give indivdual channel status in case of multiple channels with subsystem
	public void channelUp (String address,AseSubsystem subsystem);

	public void channelDown (String address,AseSubsystem subsystem);
}
