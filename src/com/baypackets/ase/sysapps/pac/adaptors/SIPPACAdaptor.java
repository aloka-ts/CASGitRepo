/*
 * SIPPACAdaptor.java
 * @author Amit Baxi
 */
package com.baypackets.ase.sysapps.pac.adaptors;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import javax.servlet.sip.ServletParseException;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipURI;
import javax.servlet.sip.URI;
import javax.servlet.sip.SipSession;

import com.baypackets.ase.sysapps.pac.dao.PACDAO;
import com.baypackets.ase.sysapps.pac.dao.rdbms.PACDAOImpl;
import com.baypackets.ase.sysapps.pac.jaxb.Channel;
import com.baypackets.ase.sysapps.pac.jaxb.ChannelPresence;
import com.baypackets.ase.sysapps.pac.receiver.PACSIPServlet;
import com.baypackets.ase.sysapps.pac.util.Configuration;
import com.baypackets.ase.sysapps.pac.util.Constants;
/**
 * This adaptor will fetch presence from SIP channel.
 * 
 */
public class SIPPACAdaptor implements PACAdaptor, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5255475326510957443L;
	private static Logger logger = Logger.getLogger(SIPPACAdaptor.class.getName());
	private SipFactory factory=null;
	public static final String SIP_CHANNEL = "SIP";
	private static SIPPACAdaptor m_SipAdaptor=null;
	private String pacSIPUri=Constants.DEFAULT_PAC_SIP_URI;
	private int expires=Constants.DEFAULT_PAC_SUBSCRIPTION_EXPIRES;
	private int appSessionExpires=(Constants.DEFAULT_PAC_SUBSCRIPTION_EXPIRES+60)/60;//Default appSession Expires in minutes (61 Minutes)
	/**This table stores SIP Application Sessions with Application Id Aconyx Username and Channel Username combination as key. */
	private static ConcurrentHashMap <String,String> m_SIPAppSessionMap;
		private SIPPACAdaptor(){
			factory=PACSIPServlet.getSipFactory();
			m_SIPAppSessionMap=new ConcurrentHashMap<String,String>();
			initialize();
	}
	/**
	 * This method initializes PAC SIP URI and expiration time for SUBSCRIBE request from configuration map populated by pac.properties.
	 * 	
	 */
	public void initialize(){
		logger.debug("Initializing SIP Adaptor for Configuration map loaded by pac.properties file");
		Configuration config=Configuration.getInstance();
		String uri=(String)config.getParamValue(Constants.PROP_PAC_SIP_URI);
		if(uri!=null && uri.trim().length()!=0){
			URI sipUri;
			try {
				sipUri = factory.createURI(uri);
				if(sipUri.isSipURI())
					pacSIPUri=uri;
			} catch (ServletParseException e) {
				logger.error("Incorrect value given for property"+Constants.PROP_PAC_SIP_URI);
				pacSIPUri=Constants.DEFAULT_PAC_SIP_URI;
			}			
		}
		String str_expires=(String)config.getParamValue(Constants.PROP_SIP_SUBSCRIPTION_EXPIRES);
		if(str_expires!=null && str_expires.trim().length()!=0){
			try{
				expires=Integer.valueOf(str_expires);
				if(expires>60)
					appSessionExpires=(expires+60)/60; //Converting in Minutes
			}catch (Exception e) {
				logger.error("Incorrect value given for property"+Constants.PROP_SIP_SUBSCRIPTION_EXPIRES);
				expires=Constants.DEFAULT_PAC_SUBSCRIPTION_EXPIRES;
			}
		}
		logger.debug("Initialized SIP Adaptor PAC SIP URI: "+pacSIPUri+" Expires: "+expires+"seconds ApplicationSession Expires: "+appSessionExpires+"min");
	}
	
	/**
	 * This method returns instance of this adaptor.
	 * 
	 */
	
	public static SIPPACAdaptor getInstance(){
		if(m_SipAdaptor==null){
			synchronized (SIPPACAdaptor.class) {
				if(m_SipAdaptor==null){
					m_SipAdaptor=new SIPPACAdaptor();	
				}
			}	
		}
		return m_SipAdaptor;
	} 
	
	/**
	 * This method will send a SUBSCRIBE request to the registrar for SIP presence.
	 * @param applicationId Identifier of Application. 
	 * @param aconyxUsername Aconyx Username.
	 * @param channel Channel information.	
	 */
	
	@Override
	public void subscribeForUserPresence(String applicationId, String aconyxUsername,Channel channel) {
		logger.debug("Inside fetchUserPresence()..for : "+ channel.getChannelUsername() + ":"+ channel.getChannelName());
		String channelURL = channel.getChannelURL();
		String channelUsername = channel.getChannelUsername();
		if (channelURL != null) {
			SipServletRequest request;
			String key=applicationId + aconyxUsername + channelUsername;
			try {	
				this.endSubscriptionForChannel(applicationId,aconyxUsername,channel);					
				SipApplicationSession applicationSession = factory.createApplicationSession();
				applicationSession.setInvalidateWhenReady(false);
				logger.debug("SIP Application Session created is:"+applicationSession);
				applicationSession.setAttribute(Constants.ATTRIB_SUBSCRIPTION_SESSION, Constants.ATTRIB_SUBSCRIPTION_SESSION);
				applicationSession.setAttribute(Constants.ATTRIB_APPLICATIONID,applicationId);
				applicationSession.setAttribute(Constants.ATTRIB_ACONYXUSERNAME, aconyxUsername);
				applicationSession.setAttribute(Constants.ATTRIB_CHANNELUSERNAME, channelUsername);
				request = factory.createRequest(applicationSession,Constants.SUBSCRIBE, this.pacSIPUri, channelUsername);
				SipURI reqURI = (SipURI) factory.createURI(channelURL);
				applicationSession.setAttribute(Constants.ATTRIB_SUBSCRIBE_URI,reqURI);
				applicationSession.setAttribute(Constants.ATTRIB_SUBSCRIBE_SESSION_ID,request.getSession().getId());
				request.getSession().setInvalidateWhenReady(false);
				request.setRequestURI(reqURI);
				request.setHeader("Event", "reg");
				request.setExpires(this.expires);
				applicationSession.setExpires(this.appSessionExpires);
				this.addSIPAppSession(key,applicationSession.getId());
				request.send();
				logger.debug("Exit fetchUserPresence()..for : "+ channel.getChannelUsername() + ":" + channelUsername);
			} catch (ServletParseException e1) {
				logger.error("ServletParseException in fetchUserPresence()"+ e1.toString());
			} catch (IOException e) {
				logger.error("IOException in fetchUserPresence()"+ e.toString());
			}
		} else {
			logger.debug("Registrar URI is not modified so exitting....");
		}
	}

	/**
	 * This method will send a SUBSCRIBE request with zero expires to the registrar for end subscription.
	 * @param applicationId Identifier of Application. 
	 * @param aconyxUsername Aconyx Username.
	 * @param channel Channel information.	
	 * */
	@Override
	public void endSubscriptionForChannel(String applicationId,String aconyxUsername,Channel channel) {
		logger.debug("Inside endSubscriptionForChannel()....");
		String channelUsername=channel.getChannelUsername();
		String key = applicationId + aconyxUsername + channelUsername;
		SipApplicationSession applicationSession = this.getSIPAppSession(key);
		if (applicationSession != null) {
				logger.debug("SIP Application Session found is:"+applicationSession);
					this.removeSIPAppSession(key);
					// If channel is deleted then cancel timer from here
					PACSIPServlet.cancelTimer(applicationSession, Constants.ATTRIB_PAC_SUBSCRIPTION_TIMER_ID);
					applicationSession.removeAttribute(Constants.ATTRIB_SUBSCRIPTION_SESSION);
					applicationSession.setAttribute(Constants.ATTRIB_SESSION_STATE,Constants.STATE_READY_TO_INVALIDATE);
					String subSessionId=(String)applicationSession.getAttribute(Constants.ATTRIB_SUBSCRIBE_SESSION_ID);
					
			try {
				    SipSession subSession=applicationSession.getSipSession(subSessionId);
					if(subSession!=null){
						SipServletRequest request = subSession.createRequest("SUBSCRIBE");
						SipURI reqURI = (SipURI) applicationSession.getAttribute(Constants.ATTRIB_SUBSCRIBE_URI);
						request.setRequestURI(reqURI);
						request.setHeader("Event", "reg");
						request.setExpires(0);
						request.send();
					}else{
						if(logger.isDebugEnabled())
							logger.debug("SIP Session for subscrie not found so not sending subscribe request");
					}
				} catch (IOException e) {
					logger.error("IOException in endSubscriptionForChannel()" + e.toString());
				}
				
		}		
		else{
			logger.debug("No SIP Application Session found for key:"+key);
		}
		logger.debug("Exitting endSubscriptionForChannel()....");
	}
	
	/**
	 * This method returns SIP Application Session from HashTable specified by key.
	 * @param key 
	 * @return
	 */
	public SipApplicationSession getSIPAppSession(String key) {
		SipApplicationSession appSession=null;
		if(key!=null && this.m_SIPAppSessionMap.containsKey(key)){
			String appSessionId=m_SIPAppSessionMap.get(key);
			if(appSessionId!=null)
				appSession=PACSIPServlet.getApplicationSessionById(appSessionId);
		}
		return appSession;
	}
	/**
	 * This method removes SIP Application Session from HashTable specified by key.
	 * @param key
	 * @return
	 */
	public void removeSIPAppSession(String key) {
		if (key != null) {
			logger.debug("Inside removeSIPAppSession() with key" + key);
			m_SIPAppSessionMap.remove(key);
		}
	}
	
	/**
	 * This method adds SIP Application Session in HashTable specified by key.
	 * @param key Map Key
	 * @param appSession SIPApplicationSession object
	 * 
	 */	
	public void addSIPAppSession(String key,String appSessionId) {
		logger.debug("Inside addSIPAppSession() with key" + key+ " SIPAppSession: " + appSessionId);
		if (key != null && appSessionId != null) {
			m_SIPAppSessionMap.put(key, appSessionId);
		}
	}
	
	@Override
	public void startPolling(Channel uc) {
		// TODO Auto-generated method stub		
	}

	@Override
	public void configure() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stopPolling(Channel uc) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public List<ChannelPresence> fetchUserPresence(String applicationId,
			String username, String password,
			List<Channel> channelList) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean isChannelWorking(String applicationId,
			String aconyxUsername, String channelUsername) {
		String key=applicationId + aconyxUsername + channelUsername;
		return m_SIPAppSessionMap.containsKey(key);		
	}

	
	
}

