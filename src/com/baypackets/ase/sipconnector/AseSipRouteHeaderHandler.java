/**
 * AseSipRouteHeaderHandler.java
 */

package com.baypackets.ase.sipconnector;

import javax.servlet.sip.Address;
import javax.servlet.sip.ServletParseException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.dynamicsoft.DsLibs.DsSipObject.DsSipConstants;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipMessage;
import com.dynamicsoft.DsLibs.DsSipObject.DsURI;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipURL;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipRouteHeader;

/**
 * Helper class to work on the ROUTE header
 */

class AseSipRouteHeaderHandler {

	/**
	 * strip the TOPmost ROUTE header if it points to ASE Invoked when an
	 * Initial request is received by the container
	 */
	static void stripTopSelfRoute(AseSipServletMessage sipMessage) {

		if (m_logger.isDebugEnabled())
			m_logger.debug("Entering AseSipRouteHeaderHandler "
					+ "stripTopSelfRoute");

		DsSipRouteHeader routeHdr = null;
		DsSipMessage dsMessage = sipMessage.getDsMessage();

		try {
			routeHdr = (DsSipRouteHeader) dsMessage
					.getHeaderValidate(DsSipConstants.ROUTE);
		} catch (Exception e) {
			m_logger.error("Could not parse Route Header", e);
		}

		// If ROUTE header is present and its host matches the ASE
		// Host then remove the header.
		if (routeHdr != null) {
			DsURI routeUri = routeHdr.getURI();

			if ((routeUri != null) && (routeUri instanceof DsSipURL)) {
				String uriHost = ((DsSipURL) routeUri).getHost().toString();

			
				if (AseSipConnector.isMatchingAddress(uriHost)) {

					if (m_logger.isDebugEnabled())
						m_logger.debug("Top route removed from the message");
					//if ip or fqdn in route header is matching container's own ip than remove this header
					dsMessage.removeHeader(routeHdr);
					try {
						if (sipMessage instanceof AseSipServletRequest) {
							//prepare popped route
							AseSipServletRequest request = (AseSipServletRequest) sipMessage;
							DsSipURL sipURL = (DsSipURL) routeUri;
							String sipURI = sipURL.getName().toString() + ":"
									+ (sipURL.getUser()==null?"":sipURL.getUser().toString()+"@") // Bug 6267
									+ sipURL.getHost().toString() + ":"
									+ sipURL.getPort();
							Address poppedRoute = new AseAddressImpl(sipURI);
							//set popped route in request
							request.setPoppedRoute(poppedRoute);
							request.setInitialPoppedRoute(poppedRoute);

							if (m_logger.isDebugEnabled()) {
								m_logger.debug("Popped route header value is :"
										+ request.getPoppedRoute());
							}
						}
					} catch (ServletParseException e) {
						m_logger.error("Could not parse Route Header", e);
					}
				} else {
					if (m_logger.isDebugEnabled())
						m_logger.debug("Top route does not match host "+uriHost);
				}
			}
		}

		if (m_logger.isDebugEnabled())
			m_logger.debug("Leaving AseSipRouteHeaderHandler "
					+ "stripTopSelfRoute");
	}

	/**
	 * strip the TOPmost ROUTE header if it points to ASE Invoked when a
	 * subsequent request is sent out by the contianer Stripping is done only if
	 * role is PROXY
	 */
	static void stripTopSelfRoute(AseSipServletRequest request,
			AseSipSession session) {

		if (m_logger.isDebugEnabled())
			m_logger
					.debug("Entering AseSipRouteHeaderHandler stripTopSelfRoute ");

		if (AseSipSession.ROLE_PROXY != session.getRole()) {
			if (m_logger.isDebugEnabled()) {
				m_logger.debug("Not a PROXY session. NOOP");
				m_logger.debug("Leaving stripTopSelfRoute ");
			}
			return;
		}

		DsSipRouteHeader routeHdr = null;
		DsSipMessage dsMessage = request.getDsMessage();

		try {
			routeHdr = (DsSipRouteHeader) dsMessage
					.getHeaderValidate(DsSipConstants.ROUTE);
		} catch (Exception e) {
			m_logger.error("Could not parse Route Header", e);
		}

		// If ROUTE header is present and its host matches the ASE
		// Host then remove the header.
		if (routeHdr != null) {
			DsURI routeUri = routeHdr.getURI();

			if ((routeUri != null) && (routeUri instanceof DsSipURL)) {
				String uriHost = ((DsSipURL) routeUri).getHost().toString();

				//if route header is matchig container's own ip or fqdn remove it
				if (AseSipConnector.isMatchingAddress(uriHost)) {

					if (m_logger.isDebugEnabled())
						m_logger.debug("Top route removed from the message");

					dsMessage.removeHeader(routeHdr);
					try {
						//prepare popped route
							DsSipURL sipURL = (DsSipURL) routeUri;
							String sipURI = sipURL.getName().toString() + ":"
									+ (sipURL.getUser()==null?"":sipURL.getUser().toString()+"@") // Bug 6267
									+ sipURL.getHost().toString() + ":"
									+ sipURL.getPort();
							Address poppedRoute = new AseAddressImpl(sipURI);
							//set popped route in request
							request.setPoppedRoute(poppedRoute);

							if (m_logger.isDebugEnabled()) {
								m_logger.debug("Popped route header in subsequent request is :"
										+ request.getPoppedRoute());
							}
					} catch (ServletParseException e) {
						m_logger.error("Could not parse Route Header", e);
					}

				} else {
					if (m_logger.isDebugEnabled())
						m_logger.debug("Top route does not match");
				}
			}
		}

		if (m_logger.isDebugEnabled())
			m_logger.debug("Leaving AseSipRouteHeaderHandler "
					+ "stripTopSelfRoute");
	}

	/**
	 * Extracts top Route header from message and returns it.
	 */
	static DsSipRouteHeader getTopRoute(AseSipServletMessage message) {
		if (m_logger.isDebugEnabled())
			m_logger
					.debug("Entering AseSipRouteHeaderHandler " + "getTopRoute");

		DsSipRouteHeader routeHdr = null;
		DsSipMessage dsMessage = message.getDsMessage();

		try {
			routeHdr = (DsSipRouteHeader) dsMessage
					.getHeaderValidate(DsSipConstants.ROUTE);
		} catch (Exception e) {
			m_logger.error("getTopRoute: parsing Route Header", e);
		}

		if (m_logger.isDebugEnabled())
			m_logger.debug("Leaving AseSipRouteHeaderHandler " + "getTopRoute");
		return routeHdr;
	}

	/**
	 * The logger reference
	 */
	transient private static Logger m_logger = Logger
			.getLogger(AseSipRouteHeaderHandler.class);
}
