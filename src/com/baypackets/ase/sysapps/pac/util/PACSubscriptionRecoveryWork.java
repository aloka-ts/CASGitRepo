package com.baypackets.ase.sysapps.pac.util;

import org.apache.log4j.Logger;

import com.baypackets.ase.sysapps.pac.adaptors.PACAdaptor;
import com.baypackets.ase.sysapps.pac.adaptors.PACAdaptorFactory;
import com.baypackets.ase.sysapps.pac.cache.PACMemoryMap;
import com.baypackets.ase.sysapps.pac.dataobjects.ChannelDO;
import com.baypackets.ase.sysapps.pac.jaxb.Channel;

/**
 * 
 * This class is used to send subscribe request again on a channel asynchronously by PAC application 
 * using a separate thread when a subscribe request fails.
 *   
 */
public class PACSubscriptionRecoveryWork  implements Runnable{
	private static Logger logger = Logger.getLogger(PACSubscriptionWork.class);
	private ChannelDO channelDO;
	private int sleepBeforeRecovery=0;

	
	/**
	 * Constructor for Recovery Work.
	 * @param channelDO
	 */
	public PACSubscriptionRecoveryWork(ChannelDO channelDO,int sleepBeforeRecovery) {
		this.channelDO = channelDO;
		this.sleepBeforeRecovery=sleepBeforeRecovery;
	}
	
	@Override
	public void run() {
		if(logger.isDebugEnabled()){
			logger.debug("Inside run() method ....");
		}
		try {
			Thread.sleep(sleepBeforeRecovery);
		} catch (InterruptedException e) {
			logger.error("Exception in PACSubsciptionThread Class",e);
		}

		String channelName=channelDO.getChannelName();
		PACMemoryMap pacMemoryMap=PACMemoryMap.getInstance();
		int channelId=Configuration.getInstance().getChannelId(channelName);
		boolean isUserPresent=pacMemoryMap.containsChannelUserData(channelDO.getApplicationId(), channelDO.getAconyxUserName(),channelId,channelDO.getChannelUsername());
		// If user present in Memory Map subscribe for it again
		if(isUserPresent){
			PACAdaptor adaptor=PACAdaptorFactory.getInstance().getPACAdaptor(channelName);
			if(adaptor!=null){						
				adaptor.subscribeForUserPresence(channelDO.getApplicationId(), channelDO.getAconyxUserName(), new Channel(channelDO.getAconyxUserName(), channelDO.getChannelUsername(), channelDO.getPassword(), channelDO.getEncrypted(), channelName, channelDO.getChannelURL(), null));			
			}	
		}else{
			if(logger.isDebugEnabled()){
				logger.debug("Channel for user deleted from PAC Memory Map so not recovering it again:"+channelDO.getApplicationId()+":"+channelDO.getAconyxUserName()+":"+channelDO.getChannelUsername());
			}
		}
		if(logger.isDebugEnabled()){
			logger.debug("Exitting run() method ....");
		}
	}
	
	
}
