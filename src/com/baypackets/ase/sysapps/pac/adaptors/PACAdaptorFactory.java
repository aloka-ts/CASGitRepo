/*
 * PACAdaptorFactory.java
 * @author Amit Baxi
 */
package com.baypackets.ase.sysapps.pac.adaptors;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.baypackets.ase.sysapps.pac.util.Configuration;
import com.baypackets.ase.sysapps.pac.util.Constants;

/**
 * PACAdaptorFactory will be used to get instance of PACAdaptor for channels. 
 * 
 */
public class PACAdaptorFactory {
	private static final Logger logger = Logger.getLogger(PACAdaptorFactory.class);
	private static PACAdaptorFactory m_factory=null;
	private List<String> restChannelList;
	private PACAdaptorFactory(){
		Configuration configuration=Configuration.getInstance();
		restChannelList=new LinkedList<String>();
		String restPACAdaptorChannels=(String)configuration.getParamValue(Constants.PROP_PAC_REST_ADAPTOR_CHANNELS);
		if(restPACAdaptorChannels!=null && !restPACAdaptorChannels.isEmpty()){
			String channels[]=restPACAdaptorChannels.split(",");
			for(String channelName:channels){
				channelName=channelName.trim();
				restChannelList.add(channelName);
			}
		}
	}
	
	public static PACAdaptorFactory getInstance(){
		if(m_factory==null)
		{
			synchronized (PACAdaptorFactory.class) {
				if(m_factory==null){
					m_factory=new PACAdaptorFactory();	
				}
			}
		}
		return m_factory;
	} 
	
	public PACAdaptor getPACAdaptor(String channelName){
		PACAdaptor pacAdaptor=null;
		if(channelName!=null){
			if (channelName.equals(SIPPACAdaptor.SIP_CHANNEL)) {
				pacAdaptor=SIPPACAdaptor.getInstance();
			}else if (this.restChannelList.contains(channelName)) {
				pacAdaptor=RESTPACAdaptor.getInstance();
			} else {
				logger.error("No adaptor found for given Channel Name so returning PACAdaptor as null");
			}	
		}
		else
			logger.error("Channel Name is null so returning PACAdaptor as null");
		return pacAdaptor;
	}

}
