/**
 * AseSipContactHeaderHandler.java
 */

package com.baypackets.ase.sipconnector;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.dynamicsoft.DsLibs.DsSipObject.DsURI;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipContactHeader;

/**
 * Helper class to work on the CONTACT header
 */

class AseSipContactHeaderHandler {

	 static DsURI getURI(AseSipServletMessage sipMessage) {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering AseSipContactHeaderHandler getURI");

		  DsURI returnURI = null;
		  DsSipContactHeader cHeader = null;
		  try {
				cHeader = sipMessage.getDsMessage().getContactHeaderValidate();
				if (cHeader != null) {
					returnURI = cHeader.getURI();
				}
		  }
		  catch (Exception e) {
				m_logger.error("Exception retrieving CONTACT header URI", e);
		  }
		  
		  if (null == returnURI) {
				if (m_logger.isDebugEnabled())
					 m_logger.debug("Returning NULL DsURI");
		  }
		  
		  if (m_logger.isDebugEnabled())
				m_logger.debug("Leaving AseSipContactHeaderHandler getURI");
		  
		  return returnURI;
	 }
	 

	 /**
	  * The logger reference
	  */
	 transient private static Logger m_logger =
		  Logger.getLogger(AseSipContactHeaderHandler.class);
}
