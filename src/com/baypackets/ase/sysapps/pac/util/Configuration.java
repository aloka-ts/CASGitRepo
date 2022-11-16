/*
 * Configuration.java
 * @author Amit Baxi
 */

package com.baypackets.ase.sysapps.pac.util;
import com.baypackets.ase.sysapps.pac.dataobjects.ChannelMapDO;
import java.util.HashMap;


/** Singleton */

/** This class is a singleton class and it stores the initial parameters and values as obtained from ServletConfig.
*/

public class Configuration  
{
   
 	private static HashMap  m_configList;
	private static Configuration m_instance = null;
	
	@SuppressWarnings("unchecked")
	private Configuration() {
		m_configList = new HashMap();
	}

	public static Configuration getInstance() {
		if (m_instance == null ) {
			synchronized(Configuration.class){
				if (m_instance == null ) {
					m_instance = new Configuration();
				}	
			}
		}
		return m_instance;
	}

/* It gets the parameter value of the partcular parameter name
*/

	public Object getParamValue(String paramName) {
		return m_configList.get(paramName);
		
	}

/* It sets the parameter value of the parameter name
*/
	@SuppressWarnings("unchecked")
	public void setParamValue(String paramName,Object paramValue) {
		m_configList.put(paramName,paramValue);
	}
		
	@SuppressWarnings("unchecked")
	public int getChannelId(String channelName) {
		HashMap<String, Integer> channelIdMap = (HashMap<String, Integer>) (this.getParamValue(Constants.PROP_CHANNEL_ID_MAP));
		if (channelName != null && channelIdMap!=null) {			
			return channelIdMap.get(channelName);
		} else
			return -1;
	}
	
	
	@SuppressWarnings("unchecked")
	public ChannelMapDO getChannelMapDO(int channelId) {		
		ChannelMapDO channelMapDO=null;	
		HashMap<Integer, ChannelMapDO> channelIdMap = (HashMap <Integer, ChannelMapDO>) (this.getParamValue(Constants.PROP_ID_CHANNEL_MAP));
			if(channelIdMap!=null){
				channelMapDO=channelIdMap.get(channelId);
			}
			return channelMapDO;		
	}
	
	public int getChannelMode(String channelName){
		int channelmode=-1;
		ChannelMapDO dOObject=this.getChannelMapDO(this.getChannelId(channelName));
		if(dOObject!=null)
			channelmode=dOObject.getChannelMode();
		return channelmode;
	}
	
}

