package com.baypackets.ase.spi.resource;

import javax.servlet.ServletException;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.resource.ResourceSession;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.util.EvaluationVersion;
import com.baypackets.ase.spi.container.AbstractProtocolSession;
import com.baypackets.ase.spi.replication.ReplicationEvent;
import com.baypackets.ase.spi.container.SasApplicationSession;

public abstract class AbstractSession extends AbstractProtocolSession implements ResourceSession {
	private static final Logger logger = Logger.getLogger(AbstractSession.class);

	private static Object m_counterSyncObject = new Object();
	private static int m_resourceSessionCount = 0;
	private static final long serialVersionUID=-318970298488843L;
	public AbstractSession() {
		super();

		if (EvaluationVersion.FLAG) {
			synchronized (m_counterSyncObject) {
				if (m_resourceSessionCount >= Constants.EVAL_VERSION_MAX_RES_SESSION) {
					logger.error("SAS evaluation version resource session limit exceeded.");
					throw new IllegalStateException("Resource session limit exceeded!!!");
				}

				++m_resourceSessionCount;
			}
		}
	}

	public AbstractSession(String id) {
		super(id);

		if (EvaluationVersion.FLAG) {
			synchronized (m_counterSyncObject) {
				if (m_resourceSessionCount >= Constants.EVAL_VERSION_MAX_RES_SESSION) {
					logger.error("SAS evaluation version resource session limit exceeded.");
					throw new IllegalStateException("Resource session limit exceeded!!!");
				}

				++m_resourceSessionCount;
			}
		}
	}

	public String getMessageListener() {
		return this.getHandler();
	}

	public void setMessageListener(String name) throws ResourceException {
		try{
			this.setHandler(name);
		}catch(ServletException e){
			throw new ResourceException(e.getMessage(), e);
		}
	}

	public void invalidate () {
		if(logger.isDebugEnabled())
		logger.debug("Invalidating resource session");
		super.invalidate();

		if (EvaluationVersion.FLAG) {
			synchronized (m_counterSyncObject) {
				--m_resourceSessionCount;
			}
		}
	}
	
	public void cleanup (){
		if(logger.isDebugEnabled())
		logger.debug("Inside cleanup method of Abstarct Session");
		if(appSession != null){
			if(logger.isDebugEnabled())
			logger.debug("Inside cleanup method of Abstarct Session removing protocol session ");
			appSession.removeProtocolSession(this);
		}
		if(logger.isDebugEnabled())
		logger.debug("Exit: cleanup method of Abstarct Session");
        }
}
