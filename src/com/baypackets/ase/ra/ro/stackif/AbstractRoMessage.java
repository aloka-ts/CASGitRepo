/**
 * Filename:	AbstractRoMessage.java
 * Created On:	04-Oct-2006
 */
package com.baypackets.ase.ra.ro.stackif;

import java.util.List;

import org.apache.log4j.Logger;

import com.condor.apncommon.DiameterBaseInfo;

import com.baypackets.ase.spi.resource.AbstractMessage;
import com.baypackets.ase.spi.container.SasProtocolSession;
import com.baypackets.ase.spi.resource.SessionFactory;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.ra.ro.*;
import com.baypackets.ase.ra.ro.impl.RoSession;
import com.baypackets.ase.ra.ro.impl.RoResourceAdaptorImpl;

abstract class AbstractRoMessage extends AbstractMessage implements RoMessage {

	private static final Logger logger = Logger.getLogger(AbstractRoMessage.class);

	protected boolean _readonly = false;

	private RoSession _session;
	private DiameterBaseInfo _diamBaseInfo;

	AbstractRoMessage(RoSession session, DiameterBaseInfo dbi, boolean readonly) {
		super();
		this._session = session;
		this._diamBaseInfo = dbi;
		this._readonly = readonly;
	}

	public String getSessionId() {
		if(this._diamBaseInfo != null) {
			return this._diamBaseInfo.getSessionId();
		} else {
			return null;
		}
	}

	public DiamIdent getOriginHost() {
		logger.error("Not supported by stack");
		return null;
	}

	public DiamIdent getOriginRealm() {
		logger.error("Not supported by stack");
		return null;
	}

	public List getProxyInfoList() {
		logger.error("Not supported by stack");
		return null;
	}

	public List getRouteRecordList() {
		logger.error("Not supported by stack");
		return null;
	}

	public void addProxyInfo(ProxyInfo proxyInfo) {
		if(this._readonly) {
			throw new IllegalStateException("Cannot modify this message");
		}

		logger.error("Not supported by stack");
	}

	public void addRouteRecord(DiamIdent routeRecord) {
		if(this._readonly) {
			throw new IllegalStateException("Cannot modify this message");
		}

		logger.error("Not supported by stack");
	}

	public SasProtocolSession getProtocolSession(boolean create) {
		if( (this._session == null) && create) {
			try {
				SessionFactory sf =
					RoResourceAdaptorImpl.getResourceContext().getSessionFactory();
				this._session = (RoSession)sf.createSession();
			} catch(ResourceException re) {
				logger.error("Creating protocol session", re);
			}
		}

		return this._session;
	}

	public SasProtocolSession getProtocolSession() {
		return this._session;
	}

	public boolean isSecure() {
		return false;
	}

	public String getProtocol() {
		return Constants.PROTOCOL;
	}
}
