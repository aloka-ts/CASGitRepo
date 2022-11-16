/**
 * @author K Kameswara Rao
 *
 */

package com.baypackets.ase.sysapps.registrar.common;

import javax.servlet.sip.SipSessionActivationListener;
import javax.servlet.sip.SipSessionEvent;
import org.apache.log4j.*;
import javax.servlet.sip.SipSession;
import javax.servlet.sip.SipApplicationSession;
import com.baypackets.ase.sysapps.registrar.common.Notifier;
import java.io.Serializable;


public class Listener implements SipSessionActivationListener,Serializable
{
	
	private static final long serialVersionUID = 156426486484L;
	
	private static Logger logger = Logger.getLogger(Listener.class);
	
	private String aor="";


	public String getAor()
	{
		return aor;
	}

	public void setAor(String a)
	{
		aor=a;
	}

	public static void init()
	{
		;
	}

	/**
	 * Activates all the passive objects ,that are now in passive mode
	 * @param SipSession
	 */
	public void sessionDidActivate(SipSessionEvent sipEvent)
	{
		if(logger.isDebugEnabled())	
			logger.debug("Session activated");
		SipSession sipS = sipEvent.getSession();
		SipApplicationSession sipApp = sipS.getApplicationSession();
		String sessionType=(String)sipApp.getAttribute(Constants.SUBSCRIPTION_SESSION_TYPE);
		if(sessionType!=null){
			String aorKey = (String) sipS.getAttribute("targetAOR");
			String appId = sipApp.getId();
			if(sessionType.equals(Constants.SUBSCRIPTION_TYPE_REGINFO)){
				if(Notifier.getAorToId(aorKey) == null){
					Notifier.insertAorToId(aorKey,appId);  //activating the aor->id list table
					Notifier.insertIdToSession(sipApp );
				}else{
					if(sipApp.isValid()){
						logger.error("Invalidating App Session for : " + aorKey + sipApp);
						sipApp.invalidate();
					}
					//activating the id->session table
				}
			}else{
				PresenceNotifier.insertAorToId(aorKey,appId);
				PresenceNotifier.insertIdToSession(sipApp );
			}
		}
		if(logger.isDebugEnabled())	
			logger.debug("session activation complete");
	}

	
	/**
	 * Empty method
	 * @param SipSession
	 */
	public void sessionWillPassivate(SipSessionEvent sipEvent)
	{
		;
	}

}
