/**
 * AseSipMaxForwardsHeaderHandler.java
 */

package com.baypackets.ase.sipconnector;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.dynamicsoft.DsLibs.DsSipObject.DsSipConstants;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipMessage;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipMaxForwardsHeader;

/**
 * Helper class to work on the Max-Forwards header
 */

class AseSipMaxForwardsHeaderHandler {
	 
	 /**
	  * Validate the max forwards header if present
	  */
	 static void validateMaxForwards(AseSipServletMessage message)
		  throws javax.servlet.sip.TooManyHopsException {
		  
		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering validateMaxForwards");
				
		  DsSipMessage dsMessage = message.getDsMessage();
		  
		  DsSipMaxForwardsHeader maxForwardsHdr = null;
		  try {
				maxForwardsHdr = (DsSipMaxForwardsHeader)
					 dsMessage.getHeaderValidate(DsSipConstants.MAX_FORWARDS);
		  }
		  catch(Exception e) {
            m_logger.error("Exception getting Max-forwards", e);
		  }

		  if (maxForwardsHdr != null) {
				int maxFwdVal = maxForwardsHdr.getMaxForwards();
				if (maxFwdVal < 1)
					 throw new javax.servlet.sip.TooManyHopsException();
		  }

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Leaving validateMaxForwards");
	 }

	 /**
	  * The logger reference
	  */
	 transient private static Logger m_logger =
		  Logger.getLogger(AseSipMaxForwardsHeaderHandler.class);
}

