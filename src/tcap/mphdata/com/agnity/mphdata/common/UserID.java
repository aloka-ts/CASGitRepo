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
package com.agnity.mphdata.common;

/**
 * This class is used for AIN user id received in info_collected and info_analyzed
 * @author reeta
 *
 */
public class UserID {
	
	
	public UserID(String dn,Integer facilityId, Integer trunkGroup,String aDSIcpeID){
		this.dn=dn;
		this.facilityId=facilityId;
		this.trunkGroup=trunkGroup;
		this.adsIcpeId=aDSIcpeID;
		
	}	
	public Integer getFacilityId() {
		return facilityId;
	}

	public String getDn() {
		return dn;
	}
	
	public Integer getTrunkGroup() {
		return trunkGroup;
	}
	
	public String getAdsIcpeId() {
		return adsIcpeId;
	}
	
	private Integer facilityId;
	private String dn;
	private Integer trunkGroup;
	private String adsIcpeId;
}
