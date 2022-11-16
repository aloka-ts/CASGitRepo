/**
 * Created on Dec 13, 2004
 */
package com.baypackets.ase.sipconnector;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;

import com.dynamicsoft.DsLibs.DsSipObject.DsSipRouteFixInterface;
import com.dynamicsoft.DsLibs.DsSipObject.DsByteString;
import com.dynamicsoft.DsLibs.DsSipObject.DsURI;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipURL;

/**
 * The <code>AseSipRouteFixImpl</code> class implements
 * <code>DsSipRouteFixInterface</code> interface. It receives a URI from stack
 * and identifies it, if this was the same URI as sent in Record-Route header.
 *
 * @author Neeraj Jain
 */
class AseSipRouteFixImpl
	implements DsSipRouteFixInterface {

	/**
	 * Constructor. Instantiates logger and sets attribute values.
	 *
	 * @param uri Record-Route URI
	 */
	AseSipRouteFixImpl(DsSipURL uri) {
		m_l.log(Level.ALL, "AseSipRouteFixImpl(List) called");
		m_recordRouteURI = uri;
		m_sasParam = new DsByteString(AseSipConstants.RR_URI_PARAM);
	}

	/**
	 * This method is called to recognize if given uri matches with the
	 * Record-Route URI actually inserted in one of the proxied requests
	 *
	 * @param uri URI tobe recognized
	 *
	 * @return true, if uri matched and false, if uri did not match
	 */
	public boolean recognize(DsURI uri) {
		if(m_l.isDebugEnabled())
			m_l.debug("recognize(DsURI) called with URI = " + uri.toString());
		if(uri instanceof DsSipURL) {
			DsSipURL sipUrl = (DsSipURL)uri;

			// Match host-addr
			if(!sipUrl.getHost().equals(m_recordRouteURI.getHost()))
				return false;

			// Match port no.
			if(sipUrl.getPort() != m_recordRouteURI.getPort())
				return false;

			// Match presence of lr param
			if(sipUrl.hasLRParam() != m_recordRouteURI.hasLRParam())
				return false;

			// Match presence of SAS record-route param
			if(sipUrl.hasParameter(m_sasParam)
				!= m_recordRouteURI.hasParameter(m_sasParam))
				return false;

			if(m_l.isDebugEnabled()) m_l.debug("RR-URI matched.");
			return true;
		}

		if(m_l.isDebugEnabled()) m_l.debug("URI not an instance of DsSipURL");
		return false;
	}

	////////////////////////// private attributes /////////////////////////////

	private DsSipURL m_recordRouteURI = null;
	private static DsByteString m_sasParam;
	private static Logger m_l = Logger.getLogger(
										AseSipRouteFixImpl.class.getName());
}
