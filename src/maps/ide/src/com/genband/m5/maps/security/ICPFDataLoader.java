package com.genband.m5.maps.security;

import com.genband.m5.maps.common.entity.DeployedApp;

public interface ICPFDataLoader {

       public void uploadSecurityData();
	
	public void uploadSecurityData (String csv_url, DeployedApp app_id);
	
	public void uploadOrganizationData ();
}
