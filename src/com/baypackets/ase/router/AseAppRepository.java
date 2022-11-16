package com.baypackets.ase.router;

import java.util.List;
import java.util.Map;

import com.baypackets.ase.ari.AseSipApplicationRouterInfo;

public interface AseAppRepository{
	
	public Map<String,List<AseSipApplicationRouterInfo>> loadAppDetails() ;
	
	public AseSipApplicationRouterInfo loadAppDetails(String name) ;
	
	
}