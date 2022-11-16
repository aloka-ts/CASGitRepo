/*
 * PACSubscriptionThread.java
 * @author Amit Baxi
 */
package com.baypackets.ase.sysapps.pac.util;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import com.baypackets.ase.sysapps.pac.adaptors.PACAdaptor;
import com.baypackets.ase.sysapps.pac.adaptors.PACAdaptorFactory;
import com.baypackets.ase.sysapps.pac.dataobjects.ChannelDO;
import com.baypackets.ase.sysapps.pac.jaxb.Channel;

/**
 * 
 * This class is used to send subscribe request on a channel asynchronously by PAC application 
 * using a separate thread.
 * It requires list of channelDo and operation for them as constructor argument.
 * Number of max subscription requests and subscriptionDelay  are initialized from pac.prperties.
 *   
 */
public class PACSubscriptionWork implements Runnable{	
	private static Logger logger = Logger.getLogger(PACSubscriptionWork.class);
	private List<ChannelDO> channelDoList;
	private Operation operation;
	private static int maxSubscriptionRequests=20;//Default value is 20
	private static long subscriptionDelay=1000;//Default value is 1000 ms
	private static boolean isInitialized=false;
	//Enum to define operation for channelDo list
	public enum Operation{
		SUBSCRIBE,UNSBSCRIBE;
	}	
	/**
	 * @param channelDoList
	 * @param operation
	 */
	public PACSubscriptionWork(List<ChannelDO> channelDoList,
			Operation operation) {
		this.channelDoList = channelDoList;
		this.operation = operation;
		if(!isInitialized)
				initialize();
	}
	
	/**
	 * This method is used to initialize() subscriptionDelay and 
	 * maxSubscriptionRequests from pac.properties
	 */
	private void initialize(){
		Configuration config=Configuration.getInstance();
		String str_maxSubscriptions=(String)config.getParamValue(Constants.PROP_PAC_MAX_SUBSCRIPTION_REQUESTS);
		if(str_maxSubscriptions!=null && str_maxSubscriptions.trim().length()!=0){
			try{
				maxSubscriptionRequests=Integer.valueOf(str_maxSubscriptions);
			}catch (Exception e) {
				logger.error("Incorrect value given for property"+Constants.PROP_PAC_MAX_SUBSCRIPTION_REQUESTS);
				maxSubscriptionRequests=20;
			}
		}
		String str_delay=(String)config.getParamValue(Constants.PROP_PAC_SUBSCRIPTION_DELAY);
		if(str_delay!=null && str_delay.trim().length()!=0){
			try{
				subscriptionDelay=Long.valueOf(str_delay);
			}catch (Exception e) {
				logger.error("Incorrect value given for property"+Constants.PROP_PAC_SUBSCRIPTION_DELAY);
				subscriptionDelay=1000;
			}
		}
                    isInitialized=true;
	}
	
	@Override
	public void run(){
		if(this.channelDoList!=null && this.operation!=null){
			Iterator<ChannelDO> iterator=this.channelDoList.iterator();
			int counter=0;
			if(this.operation.equals(Operation.SUBSCRIBE)){
				while(iterator.hasNext())
				{
					ChannelDO channelDO=iterator.next();
					String channelName=channelDO.getChannelName();
					PACAdaptor adaptor=PACAdaptorFactory.getInstance().getPACAdaptor(channelName);
					if(adaptor!=null){						
						adaptor.subscribeForUserPresence(channelDO.getApplicationId(), channelDO.getAconyxUserName(), new Channel(channelDO.getAconyxUserName(), channelDO.getChannelUsername(), channelDO.getPassword(), channelDO.getEncrypted(), channelName, channelDO.getChannelURL(), null));
						counter++;
					}	
					iterator.remove();
					if(counter==maxSubscriptionRequests){
						counter=0;
						try {
							Thread.sleep(subscriptionDelay);
						} catch (InterruptedException e) {
							logger.error("Exception in PACSubsciptionThread Class",e);
						}

					}
				}
			}
			else if(this.operation.equals(Operation.UNSBSCRIBE)){
				while(iterator.hasNext())
				{
					ChannelDO channelDO=iterator.next();
					String channelName=channelDO.getChannelName();
					PACAdaptor adaptor=PACAdaptorFactory.getInstance().getPACAdaptor(channelName);
					if(adaptor!=null){
						adaptor.endSubscriptionForChannel(channelDO.getApplicationId(), channelDO.getAconyxUserName(), new Channel(channelDO.getAconyxUserName(), channelDO.getChannelUsername(), channelDO.getPassword(), channelDO.getEncrypted(), channelName, channelDO.getChannelURL(), null));	
						counter++;
					}
					iterator.remove();
					if(counter==maxSubscriptionRequests){
						counter=0;
						try {
							Thread.sleep(subscriptionDelay);
						} catch (InterruptedException e) {
							logger.error("Exception in PACSubsciptionThread Class",e);
						}

					}
				}
			}			

		}		
	}
	
}
