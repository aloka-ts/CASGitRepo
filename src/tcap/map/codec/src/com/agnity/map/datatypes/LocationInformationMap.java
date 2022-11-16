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

/**
 * 
 * @author sanjay
 *
 */
public class LocationInformationMap {
	
	private AgeOfLocationInformationMap ageOfLocation;
	private GeographicalInformationMap geoInfo;
	private ISDNAddressStringMap vlrNum;
	private LocationNumberMap locNum;
	private CellGidOrSaiOrLaiMap cgidOrSaiOrLai;
	private LSAIdentityMap selectedLsaId;
	private ISDNAddressStringMap mscNumber;
	private GeodeticInformationMap geodeticInfo;
	private LocationInformationEpsMap locInfoEps;
	private UserCsgInformationMap userCsgInfo;
	
	
	public AgeOfLocationInformationMap getAgeOfLocation() {
		return ageOfLocation;
	}
	
	public void setAgeOfLocation(AgeOfLocationInformationMap ageOfLocation) {
		this.ageOfLocation = ageOfLocation;
	}
	
	public GeographicalInformationMap getGeographicalInfo() {
		return geoInfo;
	}
	
	public void setGeographicalInfo(GeographicalInformationMap geoInfo) {
		this.geoInfo = geoInfo;
	}
	
	public ISDNAddressStringMap getVlrNum() {
		return vlrNum;
	}
	
	public void setVlrNum(ISDNAddressStringMap vlrNum) {
		this.vlrNum = vlrNum;
	}
	
	public LocationNumberMap getLocNum() {
		return locNum;
	}
	
	public void setLocNum(LocationNumberMap locNum) {
		this.locNum = locNum;
	}
	
	public CellGidOrSaiOrLaiMap getCgidOrSaiOrLai() {
		return cgidOrSaiOrLai;
	}
	
	public void setCgidOrSaiOrLai(CellGidOrSaiOrLaiMap cgidOrSaiOrLai) {
		this.cgidOrSaiOrLai = cgidOrSaiOrLai;
	}
	
	public LSAIdentityMap getSelectedLsaId() {
		return selectedLsaId;
	}
	
	public void setSelectedLsaId(LSAIdentityMap selectedLsaId) {
		this.selectedLsaId = selectedLsaId;
	}
	
	public ISDNAddressStringMap getMscNumber() {
		return mscNumber;
	}
	
	public void setMscNumber(ISDNAddressStringMap mscNumber) {
		this.mscNumber = mscNumber;
	}
	public GeodeticInformationMap getGeodeticInfo() {
		return geodeticInfo;
	}
	
	public void setGeodeticInfo(GeodeticInformationMap geodeticInfo) {
		this.geodeticInfo = geodeticInfo;
	}
	
	public LocationInformationEpsMap getLocInfoEps() {
		return locInfoEps;
	}
	
	public void setLocInfoEps(LocationInformationEpsMap locInfoEps) {
		this.locInfoEps = locInfoEps;
	}
	
	public UserCsgInformationMap getUserCsgInfo() {
		return userCsgInfo;
	}
	
	public void setUserCsgInfo(UserCsgInformationMap userCsgInfo) {
		this.userCsgInfo = userCsgInfo;
	}

	
	public String toString() {
		StringBuilder state = new StringBuilder();
		state.append("AgeOfLocationInformationMap = ").append(ageOfLocation).append("\n");
		state.append("GeographicalInformationMap = ").append(geoInfo).append("\n");
		state.append("VlrNumber = ").append(vlrNum).append("\n");
		state.append("LocationNumberMap = ").append(locNum).append("\n");
		state.append("CellGidOrSaiOrLaiMap = ").append(cgidOrSaiOrLai).append("\n");
		state.append("LSAIdentityMap = ").append(selectedLsaId).append("\n");
		state.append("mscNumber = ").append(mscNumber).append("\n");
		state.append("GeodeticInformationMap = ").append(geodeticInfo).append("\n");
		state.append("LocationInformationEpsMap = ").append(locInfoEps).append("\n");
		state.append("UserCsgInformationMap = ").append(userCsgInfo).append("\n");

		return state.toString();
	}

}
