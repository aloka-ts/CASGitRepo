/*
 * ChannelUserDataMap.java
 * @author Amit Baxi
 */
package com.baypackets.ase.sysapps.pac.cache;

import java.util.Collection;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

public class ChannelUserDataMap {
	private ConcurrentHashMap<String, UserChannelDataRow> m_ChannelUserDataMap;

	public ChannelUserDataMap() {
		m_ChannelUserDataMap = new ConcurrentHashMap<String, UserChannelDataRow>();
	}

	protected UserChannelDataRow getChannelUsersData(String p_Key) {
		return m_ChannelUserDataMap.get(p_Key);

	}

	protected void setChannelUsersData(String key,
			UserChannelDataRow channelUsersData) {
		m_ChannelUserDataMap.put(key, channelUsersData);
	}

	protected boolean containsKey(String p_Key) {
		return m_ChannelUserDataMap.containsKey(p_Key);
	}

	protected void remove(String p_Key) {
		m_ChannelUserDataMap.remove(p_Key);
	}

	protected Enumeration<String> keys() {
		return m_ChannelUserDataMap.keys();
	}

	protected Collection<UserChannelDataRow> getValues() {
		return m_ChannelUserDataMap.values();
	}

	protected boolean isEmpty() {
		return m_ChannelUserDataMap.isEmpty();
	}
}
