/**
 * AseSipFromHeaderHandler.java
 */

package com.baypackets.ase.sipconnector;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.dynamicsoft.DsLibs.DsSipObject.DsURI;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipFromHeader;

/**
 * Helper class to work on the FROM header
 */

class AseSipFromHeaderHandler {

	 static DsURI getURI(AseSipServletMessage sipMessage) {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering AseSipFromHeaderHandler getURI");

		  DsURI returnURI = null;
		  DsSipFromHeader fHeader = null;
		  try {
				fHeader = sipMessage.getDsMessage().getFromHeaderValidate();
				returnURI = fHeader.getURI();
		  }
		  catch (Exception e) {
				m_logger.error("Exception retrieving FROM header URI", e);
		  }
		  
		  if (null == returnURI) {
				if (m_logger.isDebugEnabled())
					 m_logger.debug("Returning NULL DsURI");
		  }
		  
		  if (m_logger.isDebugEnabled())
				m_logger.debug("Leaving AseSipFromHeaderHandler getURI");
		  
		  return returnURI;
	 }
	 

	 /**
	  * The logger reference
	  */
	 transient private static Logger m_logger =
		  Logger.getLogger(AseSipFromHeaderHandler.class);
}
