/*******************************************************************************
 *   Copyright (c) 2014 Agnity, Inc. All rights reserved.
 *   
 *   This is proprietary source code of Agnity, Inc. 
 *   
 *   Agnity, Inc. retains all intellectual property rights associated 
 *   with this source code. Use is subject to license terms.
 *   
 *   This source code contains trade secrets owned by Agnity, Inc.
 *   Confidentiality of this computer program must be maintained at 
 *   all times, unless explicitly authorized by Agnity, Inc.
 *******************************************************************************/

package com.agnity.map.datatypes;

/**
 * 
 * @author sanjay
 *
 */
public class LocationInformationEpsMap {
	private EUtranCellGlobalIdentityMap eUtranCgid;
	private TrackingAreaIdentityMap trackingAreaId;
	private GeographicalInformationMap geoInfo;
	private GeodeticInformationMap geodeticInfo;
	private AgeOfLocationInformationMap ageOfLoc;
	
	public LocationInformationEpsMap() {
	}
	
	public LocationInformationEpsMap(EUtranCellGlobalIdentityMap eUtranCgid,
			TrackingAreaIdentityMap trackingAreaId,
			GeographicalInformationMap geoInfo,
			GeodeticInformationMap geodeticInfo, 
			AgeOfLocationInformationMap ageOfLoc){
		
		this.eUtranCgid = eUtranCgid;
		this.trackingAreaId = trackingAreaId;
		this.geoInfo = geoInfo;
		this.geodeticInfo = geodeticInfo;
		this.ageOfLoc = ageOfLoc;
	}
	
	public void setEUtranCgid(EUtranCellGlobalIdentityMap eutranCgid) {
		this.eUtranCgid = eutranCgid;
	}
	
	public EUtranCellGlobalIdentityMap getEUtranCgid() {
		return this.eUtranCgid;
	}
	
	public void setTrackingAreaIdentity(TrackingAreaIdentityMap taid) {
		this.trackingAreaId = taid;
	}
	
	public TrackingAreaIdentityMap getTrackingAreaIdentity() {
		return this.trackingAreaId;
	}
	
	public void setGeographicalInformation(GeographicalInformationMap geoInfo) {
		this.geoInfo = geoInfo;
	}
	
	public GeographicalInformationMap getGeographicalInformation() {
		return this.geoInfo;
	}
	
	public void setGeodeticInformation(GeodeticInformationMap geodeticInfo) {
		this.geodeticInfo = geodeticInfo;
	}
	
	public GeodeticInformationMap getGeodeticInformation() {
		return this.geodeticInfo;
	}
	
	public void setAgeOfLocation(AgeOfLocationInformationMap ageOfLoc){
		this.ageOfLoc = ageOfLoc;
	}
	
	public AgeOfLocationInformationMap getAgeOfLocation() {
		return this.ageOfLoc;
	}
	
	public String toString() {
		StringBuilder state = new StringBuilder();
		state.append("E-UtranCgid = ").append(eUtranCgid).append("\n");
		state.append("Tracking Area Id = ").append(this.trackingAreaId).append("\n");
		state.append("Geographical Info = ").append(this.geoInfo).append("\n");
		state.append("Geodetic info = ").append(this.geodeticInfo).append("\n");
		state.append("Age Of Location = ").append(this.ageOfLoc).append("\n");
		
		return state.toString();
	}
}
