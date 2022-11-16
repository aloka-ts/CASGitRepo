/*
 * implemnt TimerListener and overrided  timeout for no response.
 * @author sumit
 */

package com.baypackets.ase.sbb.timer;



import java.io.IOException;
import java.util.Iterator;

import javax.servlet.sip.ServletTimer;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipSession;
import javax.servlet.sip.TimerListener;
import javax.servlet.sip.SipSession.State;
import javax.servlet.sip.annotation.SipListener;

import org.apache.log4j.Logger;

import com.baypackets.ase.sbb.MediaServerException;
import com.baypackets.ase.sbb.b2b.ConnectHandler;
import com.baypackets.ase.sbb.b2b.NetworkMessageHandler;
import com.baypackets.ase.sbb.b2b.OneWayDialoutHandler;
import com.baypackets.ase.sbb.conf.ConferenceConnectHandler;
import com.baypackets.ase.sbb.impl.BasicSBBOperation;
import com.baypackets.ase.sbb.impl.SBBImpl;
import com.baypackets.ase.sbb.impl.SBBOperationContext;
import com.baypackets.ase.sbb.mediaserver.GroupedMsSessionControllerImpl;
import com.baypackets.ase.sbb.mediaserver.MsSessionControllerImpl;
import com.baypackets.ase.sbb.timer.TimerInfo;
import com.baypackets.ase.sbb.util.Constants;
import com.baypackets.ase.util.AseTimerInfo;

@SipListener
public class TimerListenerImpl implements TimerListener {
	static final Logger logger= Logger.getLogger(TimerListenerImpl.class);

	/*
	 * (non-Javadoc)
	 * @see javax.servlet.sip.TimerListener#timeout(javax.servlet.sip.ServletTimer)
	 */
	private SBBImpl sbb;
	
	public void timeout(ServletTimer arg0) {
		
		boolean isDebug = logger.isDebugEnabled();
		
		if (isDebug){
			logger.debug("Inside TimerListenerImpl timeout checking which timer got timedout...");
		}
		SipApplicationSession appSession = arg0.getApplicationSession();
		Object infoObj = arg0.getInfo();
		// ----changes------
		if (infoObj != null
				&& infoObj.getClass().isAssignableFrom(AseTimerInfo.class)) {
			AseTimerInfo aseTimerInfo = (AseTimerInfo) infoObj;
			String sbbName = aseTimerInfo.getSbbName();
			if (sbbName != null) {
				sbb = (SBBImpl) appSession.getAttribute(sbbName);
				infoObj =  sbb.getServletTimer(arg0.getId());
			}
			
		}
		// ----------------
		
		if (isDebug){
			logger.debug("Inside TimerListenerImpl the timer info object is ..."+infoObj);
		}
		
		TimerInfo info = null;
		
		if (infoObj instanceof TimerInfo) {
			info = (TimerInfo) infoObj;
			// -check for session expires timeout----------------
			if (info.getMsg().equals(Constants.TIMER_FOR_MS)
					|| info.getMsg().equals(Constants.TIMER_FOR_A_PARTY)
					|| info.getMsg().equals(Constants.TIMER_FOR_REQ_PEND)) {
				
				String timerType = info.getMsg();
                logger.error("Timer "+timerType+" has expired.");
				Object obj = info.getObject();
				if(obj.equals(Constants.SESSION_REFRESH_TIMER)){
					NetworkMessageHandler handler = new NetworkMessageHandler();
					handler.setOperationContext(sbb);
					sbb.addSBBOperation(handler);
					handler.postSessionExpiry(timerType);
				}else if(obj instanceof ConnectHandler ){
					((ConnectHandler) obj).postSessionExpiry(timerType);
				}else if(obj instanceof NetworkMessageHandler){
					((NetworkMessageHandler) obj).postSessionExpiry(timerType);
				}else if(obj instanceof OneWayDialoutHandler){
					((OneWayDialoutHandler) obj).postSessionExpiry(timerType);
				}
				if(obj instanceof ConferenceConnectHandler){
					((ConferenceConnectHandler) obj).postSessionExpiry(timerType);
				}
				
			}
			else if(info.getMsg().equals(Constants.TIMEOUT_REQUIRED)){
				if(isDebug)
					logger.debug("NO answer Timer invocation");
				((ConnectHandler) info.getObject()).postTimerProcessing();
				// info.getConnectHandler().postTimerProcessing();
			} else if (info.getMsg().equals(
					Constants.STOP_MS_OPER_AFTER_TIMER)) {
				if (isDebug) {
					logger.debug("Need to stop ms operation now timer expired STOP_MS_OPER_AFTER_IDT_TIMER");
				}
				try {
					((MsSessionControllerImpl) info.getObject())
							.stopMediaOperations();
				} catch (IllegalStateException e) {
					logger.error(" eception raised while stoping ms operation after idt timer timeout"
							+ e);
				} catch (MediaServerException e) {
					logger.error(" eception raised while stoping ms operation after idt timer timeout"
							+ e);
				}

			} else {
				if (isDebug)
					logger.debug("Not a NO answer Timer invocation,Return");
			}
			//-----------
			/*if(!info.getMsg().equals(Constants.TIMEOUT_REQUIRED)){
				if(isDebug)
					logger.debug("Not a NO answer Timer invocation,Return");
				return;
			}*/
		}else{
			if(isDebug)
				logger.debug("Unknown Timer invocation,Return");
			return;
		}
		if(appSession==null){
			if(isDebug)
				logger.debug("Dnt knw how Timer invocation Happened?");
			return;
		}
		//info.getConnectHandler().postTimerProcessing();
		if(isDebug)
			logger.debug("Leaving TimerListenerImpl timeout");
	}
}
