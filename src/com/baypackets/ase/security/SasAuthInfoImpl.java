package com.baypackets.ase.security;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.sip.AuthInfo;

import org.apache.log4j.Logger;

public class SasAuthInfoImpl implements AuthInfo {
	
	private static Logger logger =Logger.getLogger(SasAuthInfoImpl.class.getName());
	private List<SasAuthInfoBean> authInfoBeansList = new ArrayList<SasAuthInfoBean>();

	//Bug ID : 5638
	@Override
	public void addAuthInfo(int statusCode, String realm, String userName, String password) {
		if (logger.isDebugEnabled()) 
			logger.debug(" Entering in addAuthInfo of SasAuthInfoImpl..");
		try{
		SasAuthInfoBean authInfoBean = new SasAuthInfoBean(statusCode,realm, userName, password);
		authInfoBeansList.add(authInfoBean);
		}catch (Exception e) {
			logger.error(e.getMessage());
		}
		if (logger.isDebugEnabled()) 
			logger.debug(" Leaving in addAuthInfo of SasAuthInfoImpl..");
	}
	
	//Bug ID : 5638
	public Iterator<SasAuthInfoBean> getAuthInfo()
	{
		if (logger.isDebugEnabled()) 
			logger.debug("Entering in getAuthInfo of SasAuthInfoImpl..");
		return authInfoBeansList.iterator();
	}
	
}