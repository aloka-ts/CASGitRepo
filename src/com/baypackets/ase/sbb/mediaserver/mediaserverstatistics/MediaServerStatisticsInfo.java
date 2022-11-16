/*
 * @(#)MediaServerStatisticsInfo	
 * This class contains the MS statistics Information.
 * 
 */
package com.baypackets.ase.sbb.mediaserver.mediaserverstatistics;


public class MediaServerStatisticsInfo{
	
	private String announcementID ;
	private String announcementType;
	private int attempts;
	private int retries;
	private float duration;
	private String lastUpdated;
	
	/**
	 * Returns Announcement ID as a String
	 */
	public String getAnnouncementID() {
		return announcementID;
	}
	
	/**
	 * sets Announcement ID as a String
	 */
	public void setAnnouncementID(String announcementID) {
		this.announcementID = announcementID;
	}
	
	/**
	 * Returns announcement type  as a String
	 */
	public String getAnnouncementType() {
		return announcementType;
	}
	
	/**
	 * sets announcement type  as a String
	 */
	public void setAnnouncementType(String announcementType) {
		this.announcementType = announcementType;
	}
	
	/**
	 * Returns attempts as a int
	 */
	public int getAttempts() {
		return attempts;
	}
	
	/**
	 * sets attempts as a int
	 */
	public void setAttempts(int attempts) {
		this.attempts = attempts;
	}
	
	/**
	 * Returns retries as a int
	 */
	public int getRetries() {
		return retries;
	}
	
	/**
	 * sets retries as a int
	 */
	public void setRetries(int retries) {
		this.retries = retries;
	}
	
	/**
	 * Returns Duration as a Float
	 */
	public float getDuration() {
		return duration;
	}
	
	/**
	 * sets duration as a float
	 */
	public void setDuration(float duration) {
		this.duration = duration;
	}

	/**
	 * Returns LastUpdated Time as a String
	 */
	public String getLastUpdated() {
		return lastUpdated;
	}

	/**
	 * set LastUpdated Time as a String
	 */
	public void setLastUpdated(String lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
	
	
}