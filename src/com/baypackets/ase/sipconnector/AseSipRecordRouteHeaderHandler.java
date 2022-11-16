/**
 * AseSipRecordRouteHeaderHandler.java
 */

package com.baypackets.ase.sipconnector;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import javax.servlet.sip.SipURI;

import com.dynamicsoft.DsLibs.DsSipObject.DsSipConstants;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipMessage;
import com.dynamicsoft.DsLibs.DsSipObject.DsURI;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipURL;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipRecordRouteHeader;
import com.dynamicsoft.DsLibs.DsSipParser.DsSipParserException;

/**
 * Helper class to work on the RECORD ROUTE header
 */

class AseSipRecordRouteHeaderHandler {

	 static void addRecordRoute(AseSipServletRequest request, SipURI uri) {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering addRecordRoute");
		  
		  DsURI rrUri = null;
		  try {
		  		//BpInd 18588
				rrUri = new DsSipURL(uri.toString());
				if(uri.isSecure())
				{
					((DsSipURL)rrUri).setSecure(true);
				}
		  }
		  catch (DsSipParserException e) {
				m_logger.error("Exception creating Record-Route URI", e);
				return;
		  }

		  DsSipRecordRouteHeader rrHdr = new DsSipRecordRouteHeader(rrUri);
		  request.getDsRequest().addHeader(rrHdr, true, false);

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Leaving addRecordRoute");
	 }

	 static void addRecordRoute(AseSipServletRequest request, SipURI uri,
										 AseSipSession session) {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering addRecordRoute");
		  
		  if (AseSipSession.ROLE_PROXY != session.getRole()) {
				if (m_logger.isDebugEnabled())
					 m_logger.debug("Not a PROXY session. NOOP" +
										 session.getLogId());
				return;
		  }
		  
		  DsURI rrUri = null;
		  try {
				rrUri = new DsSipURL(uri.toString());
		  }
		  catch (DsSipParserException e) {
				m_logger.error("Exception creating Record-Route URI", e);
				return;
		  }

		  DsSipRecordRouteHeader rrHdr = new DsSipRecordRouteHeader(rrUri);
		  request.getDsRequest().addHeader(rrHdr, true, false);

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Leaving addRecordRoute");
	 }

	 /**
	  * The logger reference
	  */
	 transient private static Logger m_logger =
		  Logger.getLogger(AseSipRecordRouteHeaderHandler.class);
}
