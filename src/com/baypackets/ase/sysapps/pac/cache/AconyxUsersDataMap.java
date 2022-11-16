/*
 * AconyxUsersDataMap.java
 * @author Amit Baxi
 */
package com.baypackets.ase.sysapps.pac.cache;

import java.util.Collection;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

public class AconyxUsersDataMap {
	private ConcurrentHashMap<String, ChannelUserDataMap> m_AconyxUesrsDataMap;

	public AconyxUsersDataMap() {
		m_AconyxUesrsDataMap = new ConcurrentHashMap<String, ChannelUserDataMap>();
	}

	protected ChannelUserDataMap getChannelUsersDataMap(String aconyxUsername) {
		return m_AconyxUesrsDataMap.get(aconyxUsername);

	}

	protected void setChannelUsersDataMap(String aconyxUsername,
			ChannelUserDataMap channelUsersData) {
		m_AconyxUesrsDataMap.put(aconyxUsername, channelUsersData);
	}

	protected boolean containsKey(String aconyxUsername) {
		return m_AconyxUesrsDataMap.containsKey(aconyxUsername);
	}

	protected void remove(String aconyxUsername) {
		m_AconyxUesrsDataMap.remove(aconyxUsername);
	}

	protected Enumeration<String> keys() {
		return m_AconyxUesrsDataMap.keys();
	}

	protected Collection<ChannelUserDataMap> getValues() {
		return m_AconyxUesrsDataMap.values();
	}

	protected boolean isEmpty() {
		return m_AconyxUesrsDataMap.isEmpty();
	}
}
