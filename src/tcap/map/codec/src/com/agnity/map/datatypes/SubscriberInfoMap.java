/****
 * Copyright (c) 2013 Agnity, Inc. All rights reserved.
 * 
 * This is proprietary source code of Agnity, Inc.
 * 
 * Agnity, Inc. retains all intellectual property rights associated with this
 * source code. Use is subject to license terms.
 * 
 * This source code contains trade secrets owned by Agnity, Inc. Confidentiality
 * of this computer program must be maintained at all times, unless explicitly
 * authorized by Agnity, Inc.
 ****/


package com.agnity.map.datatypes;

import com.agnity.map.enumdata.ImsVoiceOverPSSessionsIndMapEnum;
import com.agnity.map.enumdata.UsedRATTypeMapEnum;
import com.agnity.map.datatypes.LocationInformationMap;
import com.agnity.map.datatypes.SubscriberStateMap;
import com.agnity.map.datatypes.LocationInformationGprsMap;
import com.agnity.map.datatypes.ImeiMap;
import com.agnity.map.datatypes.MnpInfoResMap;
import com.agnity.map.datatypes.TimeMap;
import com.agnity.map.datatypes.LocationInformationEpsMap;
import com.agnity.map.datatypes.PsSubscriberStateMap;

/**
 * 
 * @author sanjay
 *
 */
public class SubscriberInfoMap {
	
	private LocationInformationMap locationInfo;
	private SubscriberStateMap subscriberState;
	private LocationInformationGprsMap locationInformationGPRS;
	private PsSubscriberStateMap psSubscriberState; // TODO
	private ImeiMap  imei;
	private MsClassMark2Map msClassmark2;
	//private GprsMsClassMap gprsMsClass;  // TODO
	private MnpInfoResMap mnpInfo;
	private ImsVoiceOverPSSessionsIndMapEnum imsVoPsSessionIndication;
	private TimeMap lastUeActivityTime; 
	private UsedRATTypeMapEnum lastRatType;
	//private PsSubscriberStateMap epsSubscriberState; TODO
	private LocationInformationEpsMap locationInfoEps;
	
	/**
	 * @return the lastUeActivityTime
	 */
	public TimeMap getLastUeActivityTime() {
		return lastUeActivityTime;
	}

	/**
	 * @param lastUeActivityTime the lastUeActivityTime to set
	 */
	public void setLastUeActivityTime(TimeMap lastUeActivityTime) {
		this.lastUeActivityTime = lastUeActivityTime;
	}

	public SubscriberInfoMap() {
	}

	/**
	 * @return the locationInfo
	 */
	public LocationInformationMap getLocationInfo() {
		return locationInfo;
	}
	
	

	/**
	 * @param locationInfo the locationInfo to set
	 */
	public void setLocationInfo(LocationInformationMap locationInfo) {
		this.locationInfo = locationInfo;
	}

	/**
	 * @return the subscriberState
	 */
	public SubscriberStateMap getSubscriberState() {
		return subscriberState;
	}

	/**
	 * @param subscriberState the subscriberState to set
	 */
	public void setSubscriberState(SubscriberStateMap subscriberState) {
		this.subscriberState = subscriberState;
	}

	/**
	 * @return the locationInformationGPRS
	 */
	public LocationInformationGprsMap getLocationInformationGPRS() {
		return locationInformationGPRS;
	}

	/**
	 * @param locationInformationGPRS the locationInformationGPRS to set
	 */
	public void setLocationInformationGPRS(
			LocationInformationGprsMap locationInformationGPRS) {
		this.locationInformationGPRS = locationInformationGPRS;
	}


	public PsSubscriberStateMap getPsSubscriberState() {
		return psSubscriberState;
	}

	public void setPsSubscriberState(PsSubscriberStateMap psSubscriberState) {
		this.psSubscriberState = psSubscriberState;
	}
	
	
	/**
	 * @return the imei
	 */
	public ImeiMap getImei() {
		return imei;
	}

	/**
	 * @param imei the imei to set
	 */
	public void setImei(ImeiMap imei) {
		this.imei = imei;
	}

	/**
	 * @return the msClassmark2
	 */
	public MsClassMark2Map getMsClassmark2() {
		return msClassmark2;
	}

	/**
	 * @param msClassmark2 the msClassmark2 to set
	 */
	public void setMsClassmark2(MsClassMark2Map msClassmark2) {
		this.msClassmark2 = msClassmark2;
	}

	/**
	 * @return the mnpInfo
	 */
	public MnpInfoResMap getMnpInfo() {
		return mnpInfo;
	}

	/**
	 * @param mnpInfo the mnpInfo to set
	 */
	public void setMnpInfo(MnpInfoResMap mnpInfo) {
		this.mnpInfo = mnpInfo;
	}

	/**
	 * @return the imsVoPsSessionIndication
	 */
	public ImsVoiceOverPSSessionsIndMapEnum getImsVoPsSessionIndication() {
		return imsVoPsSessionIndication;
	}

	/**
	 * @param imsVoPsSessionIndication the imsVoPsSessionIndication to set
	 */
	public void setImsVoPsSessionIndication(
			ImsVoiceOverPSSessionsIndMapEnum imsVoPsSessionIndication) {
		this.imsVoPsSessionIndication = imsVoPsSessionIndication;
	}

	/**
	 * @return the lastRatType
	 */
	public UsedRATTypeMapEnum getLastRatType() {
		return lastRatType;
	}

	/**
	 * @param lastRatType the lastRatType to set
	 */
	public void setLastRatType(UsedRATTypeMapEnum lastRatType) {
		this.lastRatType = lastRatType;
	}

	/**
	 * @return the locationInfoEps
	 */
	public LocationInformationEpsMap getLocationInfoEps() {
		return locationInfoEps;
	}

	/**
	 * @param locationInfoEps the locationInfoEps to set
	 */
	public void setLocationInfoEps(LocationInformationEpsMap locationInfoEps) {
		this.locationInfoEps = locationInfoEps;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SubscriberInfoMap [locationInfo=" + locationInfo
				+ ", subscriberState=" + subscriberState
				+ ", psSubscriberState=" + psSubscriberState
				+ ", locationInformationGPRS=" + locationInformationGPRS
				+ ", imei=" + imei + ", msClassmark2=" + msClassmark2
				+ ", mnpInfo=" + mnpInfo + ", imsVoPsSessionIndication="
				+ imsVoPsSessionIndication + ", lastUeActivityTime="
				+ lastUeActivityTime + ", lastRatType=" + lastRatType
				+ ", locationInfoEps=" + locationInfoEps + "]";
	}

}
