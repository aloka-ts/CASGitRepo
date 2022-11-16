package com.genband.jain.protocol.ss7.tcap;

import jain.protocol.ss7.tcap.JainTcapListener;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.sip.ServletTimer;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.TimerListener;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.genband.tcap.provider.TcapProvider;
import com.genband.tcap.provider.TcapSession;

public class HeartBeatTimerListener implements TimerListener
{
       
	static private Logger logger = Logger.getLogger(HeartBeatTimerListener.class.getName());
	final String ListenerApp 		= "ListenerApp";
	
	public HeartBeatTimerListener() {}

        public void timeout(ServletTimer timer)
        {
        	if (logger.isDebugEnabled()) {
        		logger.log(Level.DEBUG, "timeout called") ;	
        	}
            SipApplicationSession appSession = timer.getApplicationSession();
            //Object obj = appSession.getAttribute("ORIGSUA");
            Object obj1 = appSession.getAttribute("AT");
            if (logger.isDebugEnabled()) {
            	logger.log(Level.DEBUG, "timeout called and AT:" + obj1) ;	
            }
            if (timer.getInfo() instanceof ATHandler){
            	ATHandler atHandler = (ATHandler)timer.getInfo();
            	if (logger.isDebugEnabled()) {
            		logger.log(Level.DEBUG, "timeout called for AT") ;
            	}
            	atHandler.timeout(timer);
            }else if (timer.getInfo() instanceof String && ((String)timer.getInfo()).equals("RSNTimeout")){
            	if (logger.isDebugEnabled()) {
            		logger.log(Level.DEBUG, "timeout called for RSN") ;
            	}
            	//JainTcapProviderImpl.getImpl().rsnHandler.timeout(timer);
            }else if (appSession.getAttribute("Tcap-Session") != null){
            	/*(timer.getInfo() instanceof TcapSession)*/
            	//After Serialization, TCAP Session is deserialized at two different location - one 
            	//at correlation map (Activate) and other as an atribute of App Session. Now the later one is not resurructed
            	//properly during activation which is why listenerApp Attribute is coming out to be null.
            	///Therefore need following change to ensure that correct TCAP Session is being picked.
            	TcapProvider provider = JainTcapProviderImpl.getImpl();
            	TcapSession ts = provider.getTcapSession(((Integer) appSession.getAttribute("Tcap-Session")));
            	if(ts==null){
            		logger.error("GOT TS for null for id:::"+appSession.getAttribute("Tcap-Session"));
            		//appSession.invalidate();
            		return;
            	}
            	try {
					ts.acquire();
					JainTcapListener jtl = (JainTcapListener) ts
							.getAttribute(ListenerApp);
					if (jtl instanceof TimerListener) {
						if (logger.isDebugEnabled()) {
							logger.log(Level.DEBUG,
									"Handing over timeout event to Listener ");
						}
						((TimerListener) jtl).timeout(timer);
					} else {
						logger.log(Level.ERROR,
								"Listener doesn't implements Timer Listener ");
					}
				} finally {
					ts.release();
				}
            }else if (timer.getInfo() instanceof TcapProviderGateway.TcapProviderListenerHandShake){
            	if (logger.isDebugEnabled()) {
            		logger.log(Level.DEBUG, "Handing over timeout event to Tcap Provider gateway for sending INFO ") ;
            	}
            	TcapProviderGateway.TcapProviderListenerHandShake tplHsk = (TcapProviderGateway.TcapProviderListenerHandShake)timer.getInfo();
            	try{
            		tplHsk.sendINFO(timer.getApplicationSession());	
            	}catch (UnsupportedEncodingException e){
            		logger.error("UnsupportedEncodingException while sending INFO message to INC",e);
            	}catch (IOException e) {
            		logger.error("IOException while sending INFO message to INC",e);
				}
            }else if (timer.getInfo() instanceof TcapProviderGateway ){
            	TcapProviderGateway tpg = (TcapProviderGateway)timer.getInfo();
            	tpg.timeout(timer);
            }else {
            	if (logger.isDebugEnabled()) {
            		logger.log(Level.DEBUG, "timeout called for " + timer.getId() + " INFO: " + timer.getInfo()) ;
            	}
            }
        }
}
