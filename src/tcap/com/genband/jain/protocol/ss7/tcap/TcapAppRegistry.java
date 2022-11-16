package com.genband.jain.protocol.ss7.tcap;

import jain.protocol.ss7.tcap.JainTcapListener;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class have methods to maintain all registry maps.
 * @author nkumar
 *
 */
public class TcapAppRegistry {
	
	
	private static TcapAppRegistry _tcapAppRegister = null;
	
	/* built on active/non-active providers */
	private Map<String, List<JainTcapListener>> listeners = new ConcurrentHashMap<String, List<JainTcapListener> >();
	
	/* built on active/non-active providers */
	private Map<String, JainTcapListener> listenersForSrvKey = new ConcurrentHashMap<String, JainTcapListener >();
	
	/* built on active/non-active providers */
	private Map<String, JainTcapListener> listenersForAppName = new ConcurrentHashMap<String, JainTcapListener >();
	
	/* built on active/non-active providers */
	private Map<String, String> activeVersionForAppName = new ConcurrentHashMap<String, String >();
	
	/* built to set state of listener providers */
	private Map<JainTcapListener, Boolean> tcapListenerState = new ConcurrentHashMap<JainTcapListener, Boolean >();
	
	private TcapAppRegistry(){
		
	}
	
	/**
	 * This method will return the singleton instance.
	 * @return TcapAppRegistry - reference of the Class
	 */
	public synchronized static TcapAppRegistry getInstance(){
		
		if(_tcapAppRegister == null) {
			_tcapAppRegister= new TcapAppRegistry();
		}
		
		return _tcapAppRegister ;
	}
	/**
	 * This method will return the JainTcapListener reference for the service key.
	 * @param srvKey
	 * @return
	 */
	public JainTcapListener getListenerForSrvKey(String srvKey, boolean ignoreState){
		JainTcapListener listener = listenersForSrvKey.get(srvKey);
		if(ignoreState ||  (listener!= null && getTcapListenerState(listener)) ){
			return listener;
		}else{
			return null;
		}
	}
	
	/**
	 * This method will return the JainTcapListener reference for the application name.
	 * @param appName
	 * @return
	 */
	public JainTcapListener getListenerForAppName(String appName, boolean ignoreState){
		JainTcapListener listener =  listenersForAppName.get(appName);
		if(ignoreState ||  (listener!= null && getTcapListenerState(listener)) ){
			return listener;
		}else{
			return null;
		}
		
	}
	
	/**
	 * This method will return the List of JainTcapListener reference for the SCCP User address(SUA).
	 * @param sua
	 * @return
	 */
	public List<JainTcapListener> getListenerForSUA(String sua){
		return listeners.get(sua);
		
	}
	
	public Collection<List<JainTcapListener>> getListenersForAllSUA(){
		
		return listeners.values();
	}

	/**
	 * This method will return the Version for the application deployed with given name.
	 * @param appname
	 * @return
	 */
	public String getActiveVersionForAppName(String appname){
		return activeVersionForAppName.get(appname);
	}
	
	/**
	 * This methos will populate the listenersForSrvKey map.
	 * @param srvKey
	 * @param listener
	 */
	public void addListenerForSrvKey(String srvKey, JainTcapListener listener){
		listenersForSrvKey.put(srvKey, listener);
	}
	
	/**
	 * This methos will populate the listenersForAppName map.
	 * @param appName
	 * @param listener
	 */
	public void addListenerForAppName(String appName, JainTcapListener listener){
		listenersForAppName.put(appName, listener);
	}
	
	/**
	 * This methos will populate the listener map.
	 * @param sua
	 * @param listener
	 */
	public void addListenerForSUA(String sua, List<JainTcapListener> listener){
		listeners.put(sua, listener);
	}
	
	/**
	 * This method will populate the activeVersionForAppName map.
	 * @param appName
	 * @param version
	 */
	public void addActiveVersionForAppName(String appName, String version){
		activeVersionForAppName.put(appName, version);
	}
	
	/**
	 * This methos will remove the listener from the listeners map.
	 * @param sua
	 *
	 */
	public void removeListenerForSUA(String sua){
		listeners.remove(sua);
	}
	
	/**
	 * This methos will remove the listener from the listenersForAppName map.
	 * @param appName
	 *
	 */
	public void removeListenerForAppName(String appName){
		listenersForAppName.remove(appName);
	}
	
	/**
	 * This methos will remove the listener from the listenersForSrvKey map.
	 * @param srvKey
	 *
	 */
	public void removeListenerForSrvKey(String srvKey){
		listenersForSrvKey.remove(srvKey);
	}
	
	/**
	 * This method will remove the version from the activeVersionForAppName map.
	 * @param appName
	 *
	 */
	public void removeActiveVersionForAppName(String appName){
		activeVersionForAppName.remove(appName);
	}

	/**
	 * @param 
	 */
	public void setTcapListenerState(JainTcapListener tcapListener, boolean state) {
		tcapListenerState.put(tcapListener, state);
	}

	/**
	 * @return the tcapListenerState for listener
	 */
	public boolean getTcapListenerState(JainTcapListener tcapListener) {
		return tcapListenerState.get(tcapListener);
	}	
}