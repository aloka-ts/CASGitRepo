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
public class LocationInformationGprsMap {
	private CellGidOrSaiOrLaiMap cgidOrSaiOrLai;
	//private RAIdentityMap raIdenity; TODO
	private GeographicalInformationMap geoInformation;
	private ISDNAddressStringMap sgsnNumber;
	private LSAIdentityMap selectedLSAIdentity;
	//private boolean saiPresent;
	private GeodeticInformationMap geodeticInformation;
	private boolean currentLocationRetrieved;
	private AgeOfLocationInformationMap ageOfLocation;
	private UserCsgInformationMap userCSGInformation;
	
	public LocationInformationGprsMap() {
	}

	/**
	 * @return the cgidOrSaiOrLai
	 */
	public CellGidOrSaiOrLaiMap getCgidOrSaiOrLai() {
		return cgidOrSaiOrLai;
	}

	/**
	 * @param cgidOrSaiOrLai the cgidOrSaiOrLai to set
	 */
	public void setCgidOrSaiOrLai(CellGidOrSaiOrLaiMap cgidOrSaiOrLai) {
		this.cgidOrSaiOrLai = cgidOrSaiOrLai;
	}

	/**
	 * @return the geoInformation
	 */
	public GeographicalInformationMap getGeoInformation() {
		return geoInformation;
	}

	/**
	 * @param geoInformation the geoInformation to set
	 */
	public void setGeoInformation(GeographicalInformationMap geoInformation) {
		this.geoInformation = geoInformation;
	}

	/**
	 * @return the sgsnNumber
	 */
	public ISDNAddressStringMap getSgsnNumber() {
		return sgsnNumber;
	}

	/**
	 * @param sgsnNumber the sgsnNumber to set
	 */
	public void setSgsnNumber(ISDNAddressStringMap sgsnNumber) {
		this.sgsnNumber = sgsnNumber;
	}

	/**
	 * @return the selectedLSAIdentity
	 */
	public LSAIdentityMap getSelectedLSAIdentity() {
		return selectedLSAIdentity;
	}

	/**
	 * @param selectedLSAIdentity the selectedLSAIdentity to set
	 */
	public void setSelectedLSAIdentity(LSAIdentityMap selectedLSAIdentity) {
		this.selectedLSAIdentity = selectedLSAIdentity;
	}

	/**
	 * @return the saiPresent
	 */
	//public boolean isSaiPresent() {
	//	return saiPresent;
	//}

	/**
	 * @param saiPresent the saiPresent to set
	 */
	//public void setSaiPresent(boolean saiPresent) {
	//	this.saiPresent = saiPresent;
	//}

	/**
	 * @return the geodeticInformation
	 */
	public GeodeticInformationMap getGeodeticInformation() {
		return geodeticInformation;
	}

	/**
	 * @param geodeticInformation the geodeticInformation to set
	 */
	public void setGeodeticInformation(GeodeticInformationMap geodeticInformation) {
		this.geodeticInformation = geodeticInformation;
	}

	/**
	 * @return the currentLocationRetrieved
	 */
	public boolean isCurrentLocationRetrieved() {
		return currentLocationRetrieved;
	}

	/**
	 * @param currentLocationRetrieved the currentLocationRetrieved to set
	 */
	public void setCurrentLocationRetrieved(boolean currentLocationRetrieved) {
		this.currentLocationRetrieved = currentLocationRetrieved;
	}

	/**
	 * @return the ageOfLocation
	 */
	public AgeOfLocationInformationMap getAgeOfLocation() {
		return ageOfLocation;
	}

	/**
	 * @param ageOfLocation the ageOfLocation to set
	 */
	public void setAgeOfLocation(AgeOfLocationInformationMap ageOfLocation) {
		this.ageOfLocation = ageOfLocation;
	}

	/**
	 * @return the userCSGInformation
	 */
	public UserCsgInformationMap getUserCSGInformation() {
		return userCSGInformation;
	}

	/**
	 * @param userCSGInformation the userCSGInformation to set
	 */
	public void setUserCSGInformation(UserCsgInformationMap userCSGInformation) {
		this.userCSGInformation = userCSGInformation;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "LocationInformationGprsMap [cgidOrSaiOrLai=" + cgidOrSaiOrLai
				+ ", geoInformation=" + geoInformation + ", sgsnNumber="
				+ sgsnNumber + ", selectedLSAIdentity=" + selectedLSAIdentity
				//+ ", saiPresent=" + saiPresent + ", geodeticInformation="
				+ geodeticInformation + ", currentLocationRetrieved="
				+ currentLocationRetrieved + ", ageOfLocation=" + ageOfLocation
				+ ", userCSGInformation=" + userCSGInformation + "]";
	}
}
