package com.baypackets.ase.sysapps.cab.maps;

import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.log4j.Logger;
import com.baypackets.ase.sysapps.cab.dao.rdbms.CABDAOImpl;

public class CABDBMaps {

	private static ConcurrentHashMap<String, Long> m_contactViewIDMap=new ConcurrentHashMap<String, Long>();;
	private static ConcurrentHashMap<String, Long> m_addressBookGroupIDMap=new ConcurrentHashMap<String, Long>();
	private static Logger logger=Logger.getLogger(CABDBMaps.class);
	private static CABDBMaps m_instance = null;
	
	private CABDBMaps(){
		try{
			CABDAOImpl.loadDBData(m_contactViewIDMap,m_addressBookGroupIDMap);
		} catch (SQLException e) {
			logger.error("Exception in loading initial maps"+e.toString());
		} catch (Exception e) {
			logger.error("Exception in loading initial maps"+e.toString());
		}
	}
	public static CABDBMaps getInstance() {
		if (m_instance == null) {
			synchronized (CABDBMaps.class) {
				if (m_instance == null) {
					if(logger.isDebugEnabled())
						logger.debug("Inside getInstance() ...");
					m_instance = new CABDBMaps();
					if(logger.isDebugEnabled())
						logger.debug("Exiting getInstance() ...");
				}	
			}				
		}
		return m_instance;
	}
	
	public  ConcurrentHashMap<String, Long> getConactViewMap() {
		return m_contactViewIDMap;
	}
	
	public  ConcurrentHashMap<String, Long> getAddressGroupMap() {
		return m_addressBookGroupIDMap;
	}
	public void removeFromMaps(String aconyxUsername) {
		if(aconyxUsername!=null){
			if(logger.isDebugEnabled())
				logger.debug("Inside removeFromMaps() for aconysUser:"+aconyxUsername);
			for(String key:m_contactViewIDMap.keySet()){
				if(key.startsWith(aconyxUsername+"|")){
					m_contactViewIDMap.remove(key);
				}
			}
			for(String key:m_addressBookGroupIDMap.keySet()){
				if(key.startsWith(aconyxUsername+"|")){
					m_addressBookGroupIDMap.remove(key);
				}
			}
			if(logger.isDebugEnabled())
				logger.debug("Exiting removeFromMaps() ...");
		}
		
	}
	
	/**
	 * This method reloads map from database when standby machine becomes active
	 */
	public void reloadMaps(){
		try{
			if(logger.isDebugEnabled())
				logger.debug("Inside reloadMaps() ...");
			CABDAOImpl.loadDBData(m_contactViewIDMap,m_addressBookGroupIDMap);
			if(logger.isDebugEnabled())
				logger.debug("Exiting reloadMaps()....");
		} catch (SQLException e) {
			logger.error("Exception in reloading maps"+e.toString());
		} catch (Exception e) {
			logger.error("Exception in reloading maps"+e.toString());
		}
	}

}