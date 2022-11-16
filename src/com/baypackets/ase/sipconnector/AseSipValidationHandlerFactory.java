/**
 * AseSipValidationHandlerFactory.java
 */

package com.baypackets.ase.sipconnector;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.dynamicsoft.DsLibs.DsSipObject.DsSipConstants;

/**
 * This class provides a factory for SIP validation handlers
 */

class AseSipValidationHandlerFactory {

	 /**
	  * Return the AseSipValidationHandler corresponding to the session role
	  * The role is the session role with constants defined AseSipConstants
	  */
	 static AseSipValidationHandler getHandler(AseSipSession session) {
		  if (AseSipSession.ROLE_PROXY == session.getRole())
				return proxyValidationHandler;
		  else
				return uaValidationHandler;
	 }
	 
	 private static AseSipValidationHandler uaValidationHandler =
		  new AseSipValidationHandler();
	 private static AseSipValidationHandler proxyValidationHandler = null;
}

