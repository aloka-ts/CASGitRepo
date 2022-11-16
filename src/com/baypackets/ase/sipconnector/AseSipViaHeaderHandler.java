/**
 * AseSipViaHeaderHandler.java
 */

package com.baypackets.ase.sipconnector;

import java.util.Calendar;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.baypackets.ase.control.ClusterManager;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipConstants;
import com.dynamicsoft.DsLibs.DsSipObject.DsByteString;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipViaHeader;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipResponse;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipMessage;
import com.dynamicsoft.DsLibs.DsSipParser.DsSipParserException;
import com.dynamicsoft.DsLibs.DsSipParser.DsSipParserListenerException;

/**
 * Helper class to work on the VIA header
 */

class AseSipViaHeaderHandler {
	 
	 /**
	  * Add a VIA header to an outgoing request
	  * Called for PROXY operations
	  * Returns the Branch ID 
	  */

	 static DsByteString addViaHeader(AseSipServletRequest request,
									 DsByteString ipAddress,
									 int port,
									 int transportType,
									 boolean isLocalBranchId) {
	if (m_logger.isDebugEnabled()) 	m_logger.debug("Entering addViaHeader");

		
       	DsByteString branch = null;
       	//IPv6
       	ipAddress = new DsByteString(ClusterManager.adjustFIPFormat(ipAddress.toString()));
       	//
       	DsSipViaHeader via =
            	new DsSipViaHeader(ipAddress, port, transportType);
       	request.getDsRequest().addHeader(via, true, false);

		if(isLocalBranchId) {
			branch = new DsByteString(getNewBranchId());
			via.setBranch(branch);
		} else {
		  	request.getDsRequest().nextBranchId();
        	branch = via.getBranch();
		}

		if (m_logger.isDebugEnabled()) m_logger.debug("Leaving addViaHeader");
		return branch;
	 }

	static DsByteString removeTopViaHeader(AseSipServletResponse response) {
		if (m_logger.isDebugEnabled()) m_logger.debug("Entering removeTopViaHeader");

		DsSipResponse dsResp = response.getDsResponse();

		// Remove top Via header, if it matches to SAS
		DsSipViaHeader viaHdr = null;
		try {
			viaHdr = (DsSipViaHeader)dsResp.getHeaderValidate(DsSipConstants.VIA);
		} catch(DsSipParserException exp) {
			m_logger.error("Retrieving VIA", exp);
		} catch(DsSipParserListenerException exp) {
			m_logger.error("Retrieving VIA", exp);
		}

       	DsByteString branch = null;
		if(viaHdr != null) {
			String uriHost = viaHdr.getHost().toString();
			if(AseSipConnector.isMatchingAddress(uriHost)) {
				// if it matched an address, remove it from response
				if(m_logger.isDebugEnabled())
					m_logger.debug("Top Via removed for response " +
						"[call id = " + response.getCallId() + "]");
				dsResp.removeHeader(viaHdr);
        		branch = viaHdr.getBranch();
			}
		}

		if (m_logger.isDebugEnabled()) m_logger.debug("Leaving removeTopViaHeader");
		return branch;
	}

	static DsByteString getTopViaBranch(AseSipServletMessage message) {
	if (m_logger.isDebugEnabled())	m_logger.debug("Leaving getTopViaBranch");

		DsSipMessage dsMsg = message.getDsMessage();

		DsSipViaHeader via = null;
		try {
			via = (DsSipViaHeader)dsMsg.getHeaderValidate(DsSipConstants.VIA);
		} catch(DsSipParserException exp) {
			m_logger.error("Retrieving VIA", exp);
		} catch(DsSipParserListenerException exp) {
			m_logger.error("Retrieving VIA", exp);
		}

		if (m_logger.isDebugEnabled()) m_logger.debug("Leaving getTopViaBranch");
		return via.getBranch();
	}

	private static String getNewBranchId() {
		long bid;

		synchronized(AseSipViaHeaderHandler.class) {
			bid = m_bidCounter++;
		}

		return "z9hG4bK" + Calendar.getInstance().getTimeInMillis() + "*" + bid;
	}
	

	private static long m_bidCounter = 0;

	 /**
	  * The logger reference
	  */
	 transient private static Logger m_logger =
		  Logger.getLogger(AseSipViaHeaderHandler.class);
}

