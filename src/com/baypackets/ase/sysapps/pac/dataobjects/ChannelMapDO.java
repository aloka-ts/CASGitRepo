package com.baypackets.ase.sysapps.pac.dataobjects;

import java.io.Serializable;
public class ChannelMapDO implements Serializable {	
	private static final long serialVersionUID = 1L;
	private String channelName;
	private int channelMode;
	private String priority;
	/**
	 * @param priority the priority to set
	 */
	public void setPriority(String priority) {
		this.priority = priority;
	}
	/**
	 * @param channelName
	 * @param channelMode
	 * @param priority
	 */
	public ChannelMapDO(String channelName, int channelMode, String priority) {
		this.channelName = channelName;
		this.channelMode = channelMode;
		this.priority = priority;
	}
	/**
	 * @return the priority
	 */
	public String getPriority() {
		return priority;
	}
	/**
	 * @param channelMode the channelMode to set
	 */
	public void setChannelMode(int channelMode) {
		this.channelMode = channelMode;
	}
	/**
	 * @return the channelMode
	 */
	public int getChannelMode() {
		return channelMode;
	}
	/**
	 * @param channelName the channelName to set
	 */
	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}
	/**
	 * @return the channelName
	 */
	public String getChannelName() {
		return channelName;
	}
	
}
