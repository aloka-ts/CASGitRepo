/*
 * PACAdaptor.java
 * @author Amit Baxi
 */
package com.baypackets.ase.sysapps.pac.adaptors;
import java.util.List;

import com.baypackets.ase.sysapps.pac.jaxb.Channel;
import com.baypackets.ase.sysapps.pac.jaxb.ChannelPresence;;
/**
 * This PACAdaptor interface will be implemented by various channel specific adaptors.
 */
public interface PACAdaptor {
	
	/**
	 * This method will be used to get presence form external channels.
	 * @param applicationId
	 * @param aconyxUsername
	 * @param channel 
	 */
	public void subscribeForUserPresence(String applicationId, String aconyxUsername, Channel channel);
	
	/**
	 * This method will be used to get on demand presence form external channels.
	 * @param applicationId - Application Identifier for request
	 * @param username - Username for authorization on external channel 
	 * @param password - Password for authorization on external channel
	 * @param channelList - List of Channels for which presence is required.
	 * @return null if channelList is null or zero sized or there is connection problem with external channel.
	 */
	List<ChannelPresence> fetchUserPresence(String applicationId,String username, String password,List<Channel> channelList);
	
	public void endSubscriptionForChannel(String applicationId, String aconyxUsername, Channel uc);
	
	public void startPolling(Channel uc);
	
	public void stopPolling(Channel uc);
	
	public void configure();
	
	/**
	 * This method will be used to check weather a channel for PAC user is working or not.
	 * @param applicationId
	 * @param aconyxUsername
	 * @param channelUsername
	 * @return
	 */
	public boolean isChannelWorking(String applicationId, String aconyxUsername,String channelUsername);
}
