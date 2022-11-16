/*

 * PACMemoryMap.java
 * @author Amit Baxi
 */
package com.baypackets.ase.sysapps.pac.cache;

import java.util.LinkedList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.baypackets.ase.sysapps.pac.dataobjects.ChannelDO;
import com.baypackets.ase.sysapps.pac.dataobjects.PresenceDO;
import com.baypackets.ase.sysapps.pac.receiver.PACSIPServlet;
import com.baypackets.ase.sysapps.pac.util.Configuration;

public class PACMemoryMap {

	private static ConcurrentHashMap<String, AconyxUsersDataMap> m_PACMemoryMap;
	private static ConcurrentHashMap<String,String> m_AconyxUsernameMap;
	private static PACMemoryMap m_instance = null;
	private final String KEY_SEPERATOR="|";
	private PACMemoryMap() {
		m_PACMemoryMap = new ConcurrentHashMap<String, AconyxUsersDataMap>();
		m_AconyxUsernameMap = new ConcurrentHashMap<String,String>();
	}
/**
 * This method will return instance of PACMemoryMap.This method should be used in methods.
 * Do not use at class level or in static block so that all maps will be loaded on first request.
 * @return
 */
	public static PACMemoryMap getInstance() {
		if (m_instance == null) {
			synchronized (PACMemoryMap.class) {
				if (m_instance == null) {
					m_instance = new PACMemoryMap();			
					PACSIPServlet.start();}
			}
		}
		return m_instance;
	}

	/**
	 * This method will reset PAC cache in case of failure during loading of PAC Memory map from DataBase.
	 */
	public static void reset(){
		if(m_instance!=null){
			m_PACMemoryMap.clear();
			m_AconyxUsernameMap.clear();
		}
		m_PACMemoryMap=null;
		m_AconyxUsernameMap=null;
		m_instance=null;
	}
	
	public AconyxUsersDataMap getAconyxUsersDataMap(String applicationID) {
		return m_PACMemoryMap.get(applicationID);
	}

	public void setAconyxUsersDataMap(String applicationID,
			AconyxUsersDataMap aconyxUsersData) {
		m_PACMemoryMap.put(applicationID, aconyxUsersData);
	}

	public UserChannelDataRow getChannelUserData(String applicationID,
			String aconyxUsername, int channelId, String channelUsername) {
		UserChannelDataRow channelUserData=null;
		if (applicationID != null && aconyxUsername != null && channelUsername != null) {
			AconyxUsersDataMap aconyxUsersDataMap = this.getAconyxUsersDataMap(applicationID);
			if (aconyxUsersDataMap != null) {
				ChannelUserDataMap channelUserDataMap = aconyxUsersDataMap.getChannelUsersDataMap(aconyxUsername);
				if (channelUserDataMap != null) {
					channelUserData = channelUserDataMap.getChannelUsersData(channelId + ""+ channelUsername);
					return channelUserData;
				}
			}
		}
		return channelUserData;
	}

	public void insertUpdateChannelUserData(String applicationID,
			String aconyxUsername, int channelId, String channelUsername,
			UserChannelDataRow channelUserData) {
		if (applicationID != null && aconyxUsername != null && channelUsername != null && channelUserData != null) {
			if (this.containsKey(applicationID)) {
				AconyxUsersDataMap aconyxUsersDataMap = this.getAconyxUsersDataMap(applicationID);
				if (aconyxUsersDataMap.containsKey(aconyxUsername)) {
					ChannelUserDataMap channelUserDataMap = aconyxUsersDataMap.getChannelUsersDataMap(aconyxUsername);
					channelUserDataMap.setChannelUsersData(channelId + ""+ channelUsername, channelUserData);
				} else {
					ChannelUserDataMap channelUserDataMap = new ChannelUserDataMap();
					channelUserDataMap.setChannelUsersData(channelId + ""+ channelUsername, channelUserData);
					aconyxUsersDataMap.setChannelUsersDataMap(aconyxUsername,channelUserDataMap);
				}

			} else {
				AconyxUsersDataMap aconyxUsersDataMap = new AconyxUsersDataMap();
				ChannelUserDataMap channelUserDataMap = new ChannelUserDataMap();
				channelUserDataMap.setChannelUsersData(channelId + ""+ channelUsername, channelUserData);
				aconyxUsersDataMap.setChannelUsersDataMap(aconyxUsername,channelUserDataMap);
				m_PACMemoryMap.put(applicationID, aconyxUsersDataMap);
			}
			this.addToAconyxUsernameMap(applicationID, aconyxUsername, channelId, channelUsername);
		}

	}

	public boolean deleteChannelUserData(String applicationID,
			String aconyxUsername, int channelId, String channelUsername) {
		boolean success=false;
		if (applicationID != null && aconyxUsername != null && channelUsername != null) {
			if (this.containsKey(applicationID)) {
				AconyxUsersDataMap aconyxUsersDataMap = this.getAconyxUsersDataMap(applicationID);
				if (aconyxUsersDataMap.containsKey(aconyxUsername)) {
					ChannelUserDataMap channelUserDataMap = aconyxUsersDataMap.getChannelUsersDataMap(aconyxUsername);
					if (channelUserDataMap.containsKey(channelId + ""+channelUsername)) {
						channelUserDataMap.remove(channelId + ""+channelUsername);
						if (channelUserDataMap.isEmpty())
							aconyxUsersDataMap.remove(aconyxUsername);
						if (aconyxUsersDataMap.isEmpty())
							this.remove(applicationID,false);
						success=true;
						this.removeFromAconyxUsernameMap(applicationID, aconyxUsername, channelId, channelUsername);
					}
				}
			}

		}
		return success;
	}

	public PresenceDO getPresence(String applicationID, String aconyxUsername,
			String channelName, String channelUsername) {
		PresenceDO presenceDO=null;
		if (applicationID != null && aconyxUsername != null
				&& channelName != null && channelUsername != null) {
			AconyxUsersDataMap aconyxUsersDataMap = this.getAconyxUsersDataMap(applicationID);
			if (aconyxUsersDataMap != null) {
				ChannelUserDataMap channelUserDataMap = aconyxUsersDataMap.getChannelUsersDataMap(aconyxUsername);
				int channelId = Configuration.getInstance().getChannelId(channelName);
				if (channelUserDataMap != null && channelUserDataMap.containsKey(channelId + ""+ channelUsername)) {
					UserChannelDataRow channelUserData = channelUserDataMap.getChannelUsersData(channelId + ""+ channelUsername);
					presenceDO = new PresenceDO();
					presenceDO.setApplicationId(applicationID);
					presenceDO.setAconyxUsername(aconyxUsername);
					presenceDO.setChannelUsername(channelUsername);
					presenceDO.setChannelName(channelName);
					presenceDO.setPassword(channelUserData.getPassword());
					presenceDO.setEncrypted(channelUserData.getEncrypted());
					presenceDO.setChannelURL(channelUserData.getChannelURL());
					presenceDO.setStatus(channelUserData.getStatus());
					presenceDO.setCustomLabel(channelUserData.getCustomLabel());
					return presenceDO;
				}
			}
		}
		return presenceDO;
	}

	public boolean containsKey(String applicationID) {
		return m_PACMemoryMap.containsKey(applicationID);
	}

	public void remove(String applicationID,boolean updateMap) {
		if (applicationID != null){
			m_PACMemoryMap.remove(applicationID);
			if(updateMap)
			this.removeFromAconyxUsernameMap(applicationID);
		}
	}

	public void deleteAconyxUserChannels(String applicationID, String aconyxUsername) {
		if (applicationID != null && aconyxUsername != null)
			if (this.containsAconyxUsername(applicationID, aconyxUsername)) {
				AconyxUsersDataMap aconyxUsersDataMap = this.getAconyxUsersDataMap(applicationID);
				if(aconyxUsersDataMap.containsKey(aconyxUsername)){
				aconyxUsersDataMap.remove(aconyxUsername);
				this.removeFromAconyxUsernameMap(applicationID, aconyxUsername);
				}
				if (aconyxUsersDataMap.isEmpty())
					this.remove(applicationID,false);
			}
	}

	public void deleteAllAconyxUserChannels(String aconyxUsername) {
		if (aconyxUsername != null) {
			Enumeration<String> appIds = this.keys();
			while (appIds.hasMoreElements()) {
				String applicationID = appIds.nextElement();
				AconyxUsersDataMap aconyxUsersDataMap = this.getAconyxUsersDataMap(applicationID);
				if(aconyxUsersDataMap.containsKey(aconyxUsername)){
				aconyxUsersDataMap.remove(aconyxUsername);
				this.removeFromAconyxUsernameMap(applicationID, aconyxUsername);
				}
				if (aconyxUsersDataMap.isEmpty())
					this.remove(applicationID,false);
			}
			
		}
	}

	public Enumeration<String> keys() {
		return m_PACMemoryMap.keys();
	}

	public Collection<AconyxUsersDataMap> getValues() {
		return m_PACMemoryMap.values();
	}

	public boolean isEmpty() {
		return m_PACMemoryMap.isEmpty();
	}

	public boolean containsChannelUserData(String applicationID,
			String aconyxUsername, int channelId, String channelUsername) {
		boolean contains=false;
		if (applicationID != null && aconyxUsername != null && channelUsername != null) {
			AconyxUsersDataMap aconyxUsersDataMap = this.getAconyxUsersDataMap(applicationID);
			if (aconyxUsersDataMap != null) {
				ChannelUserDataMap channelUserDataMap = aconyxUsersDataMap.getChannelUsersDataMap(aconyxUsername);
				if (channelUserDataMap != null) {
					if (channelUserDataMap.getChannelUsersData(channelId + ""+ channelUsername) != null)
						contains=true;
				}
			}
		}
		return contains;
	}

	public boolean containsApplicationID(String applicationId) {
		if (applicationId != null && this.containsKey(applicationId))
			return true;
		return false;
	}

	public boolean containsAconyxUsername(String applicationId,
			String aconyxUsername) {
		boolean contains=false;
		if (applicationId != null && aconyxUsername != null && this.containsKey(applicationId)) {
			AconyxUsersDataMap aconyxUsersDataMap = this.getAconyxUsersDataMap(applicationId);
			if (aconyxUsersDataMap != null && aconyxUsersDataMap.containsKey(aconyxUsername))
				contains=true;
		}
		return contains;
	}

	public LinkedList<PresenceDO> getAllChannelsPresence(String applicationId,
			String aconyxUsername) {
		LinkedList<PresenceDO> presenceDOList=null;
		if (applicationId != null && aconyxUsername != null && this.containsAconyxUsername(applicationId, aconyxUsername)) {
			presenceDOList = new LinkedList<PresenceDO>();
			Collection<UserChannelDataRow> data = this.getAconyxUsersDataMap(applicationId).getChannelUsersDataMap(aconyxUsername).getValues();
			if (data != null) {
				Iterator<UserChannelDataRow> it = data.iterator();
				while (it.hasNext()) {
					UserChannelDataRow channelUserData = it.next();
					PresenceDO presenceDO = new PresenceDO();
					presenceDO.setApplicationId(applicationId);
					presenceDO.setAconyxUsername(aconyxUsername);
					presenceDO.setChannelUsername(channelUserData.getChannelUsername());
					String channelName = Configuration.getInstance().getChannelMapDO(channelUserData.getChannelId()).getChannelName();
					presenceDO.setChannelName(channelName);
					presenceDO.setPassword(channelUserData.getPassword());
					presenceDO.setEncrypted(channelUserData.getEncrypted());
					presenceDO.setChannelURL(channelUserData.getChannelURL());
					presenceDO.setStatus(channelUserData.getStatus());
					presenceDO.setCustomLabel(channelUserData.getCustomLabel());
					presenceDOList.add(presenceDO);
				}
			}
		}
		return presenceDOList;
	}

	public LinkedList<ChannelDO> getAllChannelDO(String applicationId,
			String aconyxUsername) {
		LinkedList<ChannelDO> channelDOList=null;
		if (applicationId != null && aconyxUsername != null) {
			channelDOList = new LinkedList<ChannelDO>();
			Collection<UserChannelDataRow> data = this.getAconyxUsersDataMap(applicationId).getChannelUsersDataMap(aconyxUsername).getValues();
			if (data != null) {
				Iterator<UserChannelDataRow> it = data.iterator();
				while (it.hasNext()) {
					UserChannelDataRow channelUserData = it.next();
					String channelName = Configuration.getInstance().getChannelMapDO(channelUserData.getChannelId()).getChannelName();
					ChannelDO channelDO = new ChannelDO(applicationId,aconyxUsername, channelUserData.getChannelUsername(), channelUserData.getPassword(),
							channelUserData.getEncrypted(),channelName,channelUserData.getChannelURL());
					channelDOList.add(channelDO);
				}

			}			
		}
		return channelDOList;
	}

	public LinkedList<ChannelDO> getAllChannelDO(String applicationId) {
		LinkedList<ChannelDO> channelDOList=null;
		if (applicationId != null && this.containsKey(applicationId)) {
		channelDOList = new LinkedList<ChannelDO>();
			AconyxUsersDataMap aconyxUserMap = this.getAconyxUsersDataMap(applicationId);
			if (aconyxUserMap != null) {
				Enumeration<String> aconyxUsers = aconyxUserMap.keys();
				while (aconyxUsers.hasMoreElements()) {
					String aconyxUsername = aconyxUsers.nextElement();
					LinkedList<ChannelDO> list = this.getAllChannelDO(
							applicationId, aconyxUsername);
					if (list != null)
						channelDOList.addAll(list);
				}				
			}
		}
		return channelDOList;
	}
	
	public LinkedList<ChannelDO> getAllChannelDOForAconyxUser(String aconyxUsername) {
		LinkedList<ChannelDO> channelDOList=null;
		if (aconyxUsername != null ) {
				channelDOList=new LinkedList<ChannelDO>();
				Enumeration<String> appIds = this.keys();
				while(appIds.hasMoreElements())
				{
					String applicationId=appIds.nextElement();
					AconyxUsersDataMap acxMap=this.getAconyxUsersDataMap(applicationId);
					if(acxMap.containsKey(aconyxUsername)){
						ChannelUserDataMap channels=acxMap.getChannelUsersDataMap(aconyxUsername);
						if(channels!=null){
							Iterator<UserChannelDataRow> it = channels.getValues().iterator();
							while (it.hasNext()) {
								UserChannelDataRow channelUserData = it.next();
								String channelName = Configuration.getInstance().getChannelMapDO(channelUserData.getChannelId()).getChannelName();
								ChannelDO channelDO = new ChannelDO(applicationId,aconyxUsername, channelUserData.getChannelUsername(), channelUserData.getPassword(),
										channelUserData.getEncrypted(),channelName,channelUserData.getChannelURL());
								channelDOList.add(channelDO);
							}
						}
					}
				}
			}
		return channelDOList;
	}
	
	public void addToAconyxUsernameMap(String applicationID,
			String aconyxUsername, int channelId, String channelUsername) {
		if (applicationID != null && aconyxUsername != null && channelUsername != null) {
			String key=applicationID+KEY_SEPERATOR+channelId+KEY_SEPERATOR+channelUsername;
				m_AconyxUsernameMap.put(key, aconyxUsername);
			}			
	}
	
	public boolean removeFromAconyxUsernameMap(String applicationID,
			String aconyxUsername, int channelId, String channelUsername) {
		boolean success=false;
		if (applicationID != null && aconyxUsername != null && channelUsername != null) {
			String key=applicationID+KEY_SEPERATOR+channelId+KEY_SEPERATOR+channelUsername;
			if (m_AconyxUsernameMap.containsKey(key)) {
							m_AconyxUsernameMap.remove(key);
						success=true;
			}
		}
		return success;
	}
		
	public void removeFromAconyxUsernameMap(String applicationID,
			String aconyxUsername) {		
		if (applicationID != null && aconyxUsername != null) {
			for(String key:m_AconyxUsernameMap.keySet()){
				if(key.startsWith(applicationID+KEY_SEPERATOR)&& aconyxUsername.equals(m_AconyxUsernameMap.get(key)))
					m_AconyxUsernameMap.remove(key);
			}
		}		
	}
	
	public void removeFromAconyxUsernameMap(String applicationID) {
		if (applicationID != null) {
			for(String key:m_AconyxUsernameMap.keySet()){
				if(key.startsWith(applicationID+KEY_SEPERATOR))
					m_AconyxUsernameMap.remove(key);
			}
		}
	}
	public String getAconyxUsername(String applicationID, int channelId, String channelUsername){
		String aconyxUsername=null;
		if (applicationID != null && channelUsername != null ) 
			aconyxUsername=m_AconyxUsernameMap.get(applicationID+KEY_SEPERATOR+channelId+KEY_SEPERATOR+channelUsername);	
		return aconyxUsername;
	}
}
