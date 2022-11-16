package com.baypackets.clicktodial.util;

import java.util.Date;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipApplicationSessionEvent;
import javax.servlet.sip.SipApplicationSessionListener;
import org.apache.log4j.Logger;

/*******************************************************************************
 *   Copyright (c) 2014 Agnity, Inc. All rights reserved.
 *   
 *   This is proprietary source code of Agnity, Inc. 
 *   
 *   Agnity, Inc. retains all intellectual property rights associated 
 *   with this source code. Use is subject to license terms.
 *   
 *   This source code contains trade secrets owned by Agnity, Inc.
 *   Confidentiality of this computer program must be maintained at 
 *   all times, unless explicitly authorized by Agnity, Inc.
 *******************************************************************************/
public class CallStatePersister
implements SipApplicationSessionListener, Constants
{
	private static Logger _logger = Logger.getLogger(CallStatePersister.class);

	public void sessionExpired(SipApplicationSessionEvent event)
	{
		if (_logger.isDebugEnabled()) {
			_logger.debug("sessionDestroyed() called");
		}
		
		try
		{
			SipApplicationSession session = event.getApplicationSession();

			CallDAO dao = (CallDAO)session.getAttribute("CALL_DAO");

			if (dao == null) {
				return;
			}

			if (_logger.isDebugEnabled()) {
				_logger.debug("Persisting call state info to the backing store...");
			}

			Call call = new Call();
			call.setCallID(session.getId());
			call.setCallStartTime((Date)session.getAttribute("CALL_START_TIME"));
			call.setCallEndTime((Date)session.getAttribute("CALL_END_TIME"));

			dao.persist(call);
		} catch (Exception e) {
			String msg = "Error occurred while persisting call state info to the backing store: " + e.toString();
			_logger.error(msg, e);
			throw new RuntimeException(msg);
		}

		if (_logger.isDebugEnabled())
			_logger.debug("Successfully persisted call state to the backing store.");
	}

	public void sessionDestroyed(SipApplicationSessionEvent event)
	{
	}

	public void sessionCreated(SipApplicationSessionEvent sipApplicationSessionEvent)
	{
	}

	public void sessionReadyToInvalidate(SipApplicationSessionEvent arg0)
	{
	}
}

