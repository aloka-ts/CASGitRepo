package com.baypackets.ase.sysapps.pac.receiver;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.sip.ServletTimer;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipApplicationSessionActivationListener;
import javax.servlet.sip.SipApplicationSessionEvent;
import javax.servlet.sip.SipApplicationSessionListener;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipServlet;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSessionsUtil;
import javax.servlet.sip.SipURI;
import javax.servlet.sip.TimerListener;
import javax.servlet.sip.TimerService;

import org.apache.log4j.Logger;

import com.baypackets.ase.common.Registry;
import com.baypackets.ase.control.AseRoles;
import com.baypackets.ase.control.ClusterManager;
import com.baypackets.ase.control.PartitionInfo;
import com.baypackets.ase.control.RoleChangeListener;
import com.baypackets.ase.sysapps.pac.adaptors.SIPPACAdaptor;
import com.baypackets.ase.sysapps.pac.cache.PACMemoryMap;
import com.baypackets.ase.sysapps.pac.cache.UserChannelDataRow;
import com.baypackets.ase.sysapps.pac.channel.sip.RegInfoHandler;
import com.baypackets.ase.sysapps.pac.channel.sip.SipPresence;
import com.baypackets.ase.sysapps.pac.dao.rdbms.PACDAOImpl;
import com.baypackets.ase.sysapps.pac.dataobjects.ChannelDO;
import com.baypackets.ase.sysapps.pac.jaxb.Channel;
import com.baypackets.ase.sysapps.pac.jaxb.ChannelPresence;
import com.baypackets.ase.sysapps.pac.jaxb.UpdatePresenceRequest;
import com.baypackets.ase.sysapps.pac.manager.PACManager;
import com.baypackets.ase.sysapps.pac.util.Configuration;
import com.baypackets.ase.sysapps.pac.util.Constants;
import com.baypackets.ase.sysapps.pac.util.PACSessionRecoveryTask;
import com.baypackets.ase.sysapps.pac.util.PACSubscriptionRecoveryWork;
import com.baypackets.ase.util.AseStrings;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;

public class PACSIPServlet extends SipServlet implements SipApplicationSessionListener,SipApplicationSessionActivationListener,TimerListener,RoleChangeListener,Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6433823342913676738L;
	private static Logger logger = Logger.getLogger(PACSIPServlet.class.getName());
	private static SipFactory factory;
	private static SipSessionsUtil util;
	public static String SIP_AOR_STATE_ACTIVE="active";
	public static String SIP_AOR_STATE_INIT="init";
	public static String SIP_AOR_STATE_TERMINATED="terminated";
	private static TimerService timerService = null;
	PACManager pacManager=PACManager.getInstance();
	private static final String timerInfo="PAC Application timer";
	private static final String subscribeTimerInfo="PAC Subscribe Timer";
	//bolean indicates that SAS on which PAC Application running is active
	private static boolean isActiveSAS=false;
	private static boolean isSipReplicationEnabled=true;
	private static boolean subscribeForOnlyActiveUsers=true;
	//timerDelayTime in minutes
	private static int timerRestartTime=Constants.DEFAULT_PAC_APPSESSION_TIMER_RESTART_TIME;
	//Interval added to ensure that the Subscription timer run by the PAC Service should expire before the timer run by the Registrar 
	private static int subscriptionTimerAdjustInterval=Constants.DEFAULT_PAC_SUBSCRIPTION_EXPIRES_ADJUST_INTERVAL;
	SIPPACAdaptor adaptor;
	public static SipFactory getSipFactory() {
		return factory;
	}
	/**
	 * This method will get appSessions by given appSessionId using SipSessionUtil object.
	 * @param appSessionId
	 */
	public static SipApplicationSession getApplicationSessionById(String appSessionId){
		SipApplicationSession appsession=null;
		if(appSessionId!=null){
			appsession=util.getApplicationSessionById(appSessionId);
		}
		if(logger.isDebugEnabled())
			logger.debug("AppSession loaded from container: "+appsession);
		return appsession;
	}

	public void init() throws ServletException {
		if(logger.isDebugEnabled()) {
			logger.debug("[PAC] init method called on PACSIPServlet.");
		}
		ServletContext ctx=this.getServletContext();
		factory = (SipFactory)ctx.getAttribute(SIP_FACTORY);
		util=(SipSessionsUtil)ctx.getAttribute("javax.servlet.sip.SipSessionsUtil");
		timerService=(TimerService)ctx.getAttribute("javax.servlet.sip.TimerService");
		adaptor=SIPPACAdaptor.getInstance();
		ClusterManager clusterManager = (ClusterManager)Registry.lookup(com.baypackets.ase.util.Constants.NAME_CLUSTER_MGR);
        clusterManager.registerRoleChangeListener(this,com.baypackets.ase.util.Constants.RCL_SYSAPPS_PRIORITY);
		String time=(String)Configuration.getInstance().getParamValue(Constants.PROP_PAC_APPSESSION_TIMER_RESTART_TIME);
		String sipSessionReplicationFlag=(String)Configuration.getInstance().getParamValue(Constants.PROP_PAC_SIP_SESSION_REPLICATION_ENABLED);
		String subscribeForActiveUsersFlag=(String)Configuration.getInstance().getParamValue(Constants.PROP_PAC_SUBSCRIBE_FOR_ONLY_ACTIVE_USERS);
		if(sipSessionReplicationFlag!=null &&  sipSessionReplicationFlag.trim().length()!=0){
			isSipReplicationEnabled=AseStrings.FALSE_SMALL.equalsIgnoreCase(sipSessionReplicationFlag.trim())?false:true;
		}
		if(subscribeForActiveUsersFlag!=null &&  subscribeForActiveUsersFlag.trim().length()!=0){
			subscribeForOnlyActiveUsers=AseStrings.FALSE_SMALL.equalsIgnoreCase(subscribeForActiveUsersFlag.trim())?false:true;
		}
		if(time!=null && time.trim().length()!=0){
			try{
				timerRestartTime=Integer.valueOf(time);
				timerRestartTime=(timerRestartTime>0)?timerRestartTime:Constants.MIN_PAC_APPSESSION_TIMER_RESTART_TIME;
			}catch (Exception e) {
				logger.error("Incorrect value given for property"+Constants.PROP_PAC_APPSESSION_TIMER_RESTART_TIME);
				timerRestartTime=Constants.DEFAULT_PAC_APPSESSION_TIMER_RESTART_TIME;
			}
		}
		
		String adjustTime=(String)Configuration.getInstance().getParamValue(Constants.PROP_SIP_SUBSCRIPTION_EXPIRES_ADJUST_INTERVAL);
		if(adjustTime!=null && adjustTime.trim().length()!=0){
			try{
				subscriptionTimerAdjustInterval=Integer.valueOf(adjustTime);
				subscriptionTimerAdjustInterval=(subscriptionTimerAdjustInterval>=30 || subscriptionTimerAdjustInterval<=90)?subscriptionTimerAdjustInterval:Constants.DEFAULT_PAC_SUBSCRIPTION_EXPIRES_ADJUST_INTERVAL;
			}catch (Exception e) {
				logger.error("Incorrect value given for property"+Constants.PROP_SIP_SUBSCRIPTION_EXPIRES_ADJUST_INTERVAL);
				subscriptionTimerAdjustInterval=Constants.DEFAULT_PAC_SUBSCRIPTION_EXPIRES_ADJUST_INTERVAL;
			}
		}
		
		ConfigRepository config=(ConfigRepository)Registry.lookup(com.baypackets.ase.util.Constants.NAME_CONFIG_REPOSITORY);
		   
		String currRole = config.getValue(com.baypackets.ase.util.Constants.OID_CURRENT_ROLE);   
	        if((currRole == null) || currRole.equalsIgnoreCase("Active")) {
	        	createAppSession();
	        }
	   if(logger.isDebugEnabled()){     
		logger.debug("[PAC] init() exitting timerRestartTime:"+timerRestartTime);
	   }
	}

	public void doNotify(SipServletRequest request) {
		if(logger.isDebugEnabled()) {
			logger.debug("[PAC] doNotify method called on PACSIPServlet.");
		}
		try {
			request.createResponse(200).send();
		} catch (IOException e1) {
			logger.error("IOException in doNotify()....", e1);
		}
		SipApplicationSession applicationSession=request.getApplicationSession();
		String readyToInvalidate=(String)applicationSession.getAttribute(Constants.ATTRIB_SESSION_STATE);
		if(readyToInvalidate!=null && Constants.STATE_READY_TO_INVALIDATE.equals(readyToInvalidate) ){
			if(logger.isDebugEnabled()){
				logger.debug("Invalidating appsession......"+applicationSession);
			}
			applicationSession.invalidate();
			
			return;
		}
		RegInfoHandler handler = new RegInfoHandler();
		List<SipPresence> sipPresences=null;
		try {
			sipPresences=handler.parseRegInfoXML((String)request.getContent());
		} catch (UnsupportedEncodingException e) {
			logger.error("UnsupportedEncodingException in doNotify()....", e);
		} catch (IOException e) {
			logger.error("IOException in doNotify()....", e);
		}
		String applicationId=(String)applicationSession.getAttribute(Constants.ATTRIB_APPLICATIONID);
		String aconyxUsername=(String)applicationSession.getAttribute(Constants.ATTRIB_ACONYXUSERNAME);

		if(applicationId!=null && aconyxUsername!=null && sipPresences!=null){
			for (int i = 0; i < sipPresences.size(); i++) {
				SipPresence reg=sipPresences.get(i);
				String state=reg.getState();
				String channelUsername=reg.getAddressOfRecord();
				int channelId=Configuration.getInstance().getChannelId(SIPPACAdaptor.SIP_CHANNEL);
				PACMemoryMap pacMemoryMap=PACMemoryMap.getInstance();
				UserChannelDataRow ucRow=pacMemoryMap.getChannelUserData(applicationId, aconyxUsername, channelId, channelUsername);
				if(ucRow!=null){
					if (state.equals(SIP_AOR_STATE_ACTIVE)||state.equals(SIP_AOR_STATE_INIT)) {
						UpdatePresenceRequest prequest=new UpdatePresenceRequest();
						prequest.setApplicationId(applicationId);
						List<ChannelPresence> presenceList=new LinkedList<ChannelPresence>();
						ChannelPresence uChannel=new ChannelPresence();
						uChannel.setAconyxUsername(aconyxUsername);
						uChannel.setChannelUsername(channelUsername);
						uChannel.setChannelName(SIPPACAdaptor.SIP_CHANNEL);
						if(state.equals(SIP_AOR_STATE_ACTIVE))
							uChannel.setStatus(Constants.PRESENCE_STATUS_AVAILABLE);
						else
							uChannel.setStatus(Constants.PRESENCE_STATUS_NOT_AVAILABLE);	
						presenceList.add(uChannel);
						prequest.setChannelPresence(presenceList);						
						pacManager.updatePresence(prequest,false);//Need to check in response.
						if(state.equals(SIP_AOR_STATE_INIT) && subscribeForOnlyActiveUsers){
							Channel channel=new Channel();
							channel.setChannelName(SIPPACAdaptor.SIP_CHANNEL);
							channel.setChannelUsername(channelUsername);
							channel.setChannelURL(ucRow.getChannelURL());
							adaptor.endSubscriptionForChannel(applicationId, aconyxUsername, channel);
						}
					}else if (state.equals(SIP_AOR_STATE_TERMINATED)) {
						
						// Cancel subscription timer
						cancelTimer(applicationSession, Constants.ATTRIB_PAC_SUBSCRIPTION_TIMER_ID);
						
						Channel channel=new Channel();
						channel.setChannelName(SIPPACAdaptor.SIP_CHANNEL);
						channel.setChannelUsername(channelUsername);
						channel.setChannelURL(ucRow.getChannelURL());
						//channel.setPassowrd(ucRow.getPassword());
						//channel.setEncrypted(ucRow.getEncrypted());
						
						//Removing SipAppSession no need to send SUBSCRIBE with expires 0 in fetchUserPresence() method.
						
						adaptor.removeSIPAppSession(applicationId + aconyxUsername + channelUsername);
						if(applicationSession.isValid()){
							if(logger.isDebugEnabled()){
								logger.debug("Invalidating appsession due to state'terminated'......"+applicationSession);
							}
							applicationSession.invalidate();							
						}else{
							if(logger.isDebugEnabled()){
								logger.debug("Appsession already invalidated'......"+applicationSession);
							}
						}
						synchronized(channelUsername.intern()) {
							if(!adaptor.isChannelWorking(applicationId, aconyxUsername, channelUsername) ){
								adaptor.subscribeForUserPresence(applicationId, aconyxUsername, channel);
							}else{
								logger.debug("User " + channelUsername + " already subscribed"  );
							}
						}
					}
				}
			}

		}
	}

	public void doErrorResponse(SipServletResponse resp) {
		if(logger.isDebugEnabled())
			logger.debug("Inside doErrorResponse()......");
		
		SipApplicationSession appSession=resp.getApplicationSession();
		String readyToInvalidate=(String)appSession.getAttribute(Constants.ATTRIB_SESSION_STATE);
	
		if(readyToInvalidate!=null && Constants.STATE_READY_TO_INVALIDATE.equals(readyToInvalidate) ){
			if(logger.isInfoEnabled())	
				logger.info("Already removed appsession...");
			appSession.invalidate();
		} else {
			if(Constants.SUBSCRIBE.equals(resp.getMethod())){
				try{
					String applicationId=(String)appSession.getAttribute(Constants.ATTRIB_APPLICATIONID);
					String aconyxUsername=(String)appSession.getAttribute(Constants.ATTRIB_ACONYXUSERNAME);
					String channelUsername=(String)appSession.getAttribute(Constants.ATTRIB_CHANNELUSERNAME);
					SipURI reqURI = (SipURI) appSession.getAttribute(Constants.ATTRIB_SUBSCRIBE_URI);
					if(logger.isDebugEnabled())
						logger.debug("Going to remove appsession from Hashtable of adaptor....");
					adaptor.removeSIPAppSession(applicationId + aconyxUsername + channelUsername);
					if(appSession.isValid()){
						if(logger.isDebugEnabled())	
							logger.debug("Invalidating appsession due error response received......"+appSession.getId());
						appSession.invalidate();							
					}
					ChannelDO channelDo=new ChannelDO(applicationId, aconyxUsername, channelUsername, "", "", SIPPACAdaptor.SIP_CHANNEL, reqURI.toString());
					if(logger.isDebugEnabled())
						logger.debug("Going to submit recovey work for:"+channelUsername);

					PACSubscriptionRecoveryWork work=new PACSubscriptionRecoveryWork(channelDo,5000);
					PACManager.pacExecutorService.submit(work);

				}catch(Exception e){
					logger.error("Exception in doErrorResponse()",e);
				}
			}else{
				if(logger.isDebugEnabled())
					logger.debug("doErrorResponse() called for request method:"+resp.getMethod());
			}
		}
		
		if(logger.isDebugEnabled())
			logger.debug("doErrorResponse()......exit");	
	}

	public void doSuccessResponse(SipServletResponse resp) {
		if(logger.isDebugEnabled()){
			logger.debug("doSuccessResponse()......enter");
		}	
		if(resp.getMethod().equals(Constants.SUBSCRIBE)){
			SipApplicationSession appSession=resp.getApplicationSession(); 
			String readyToInvalidate=(String)appSession.getAttribute(Constants.ATTRIB_SESSION_STATE);
			if(readyToInvalidate!=null && Constants.STATE_READY_TO_INVALIDATE.equals(readyToInvalidate) ){
				if(logger.isDebugEnabled()){
					logger.debug("Subscribe with expires");
				}
				return;
			}
			int duration=0;
			try{
				String expires=resp.getHeader("Expires");
				duration=Integer.valueOf(expires);
			}catch(Exception e){
				duration=Constants.DEFAULT_PAC_SUBSCRIPTION_EXPIRES;
			}
			if(duration>0){
				if(logger.isDebugEnabled()){
					logger.debug("Starting subscription timer for duration(secs):"+(duration+subscriptionTimerAdjustInterval));
				}
				ServletTimer subcriptionTimer=timerService.createTimer(appSession, (duration+subscriptionTimerAdjustInterval)*1000, true,subscribeTimerInfo);
				if(logger.isDebugEnabled()){
					logger.debug("Subscription timer id:"+subcriptionTimer.getId());
				}
				appSession.setAttribute(Constants.ATTRIB_PAC_SUBSCRIPTION_TIMER_ID, subcriptionTimer.getId());
			}
		}

		if(logger.isDebugEnabled()){
			logger.debug("doSuccessResponse()......exit");
		}	
	}
	
	@Override
	public void sessionDidActivate(SipApplicationSessionEvent event) {
		SipApplicationSession appsession = event.getApplicationSession();

		if (appsession.getAttribute(Constants.ATTRIB_SUBSCRIPTION_SESSION) != null) {
			
			String applicationId=(String)appsession.getAttribute(Constants.ATTRIB_APPLICATIONID);
			String aconyxUsername=(String)appsession.getAttribute(Constants.ATTRIB_ACONYXUSERNAME);
			String channelUsername=(String)appsession.getAttribute(Constants.ATTRIB_CHANNELUSERNAME);
			SipURI reqURI = (SipURI) appsession.getAttribute(Constants.ATTRIB_SUBSCRIBE_URI);
			String key=applicationId + aconyxUsername + channelUsername;	
			synchronized(channelUsername.intern()) {
				if(!adaptor.isChannelWorking(applicationId, aconyxUsername, channelUsername)){
					adaptor.addSIPAppSession(key,appsession.getId());
				}else{
					logger.error(" Inside sessionDidActivate Channel already working for : " + channelUsername);
					if(appsession.isValid()){
						logger.error("Invalidating App Session : " + appsession);
						appsession.invalidate();
					}
					return;
				}
			}	
			// Extra checks for timer
			String timerId=(String)appsession.getAttribute(Constants.ATTRIB_PAC_SUBSCRIPTION_TIMER_ID);
			ServletTimer timer=appsession.getTimer(timerId);
			
			if(timer==null || timer.getTimeRemaining()<=0){
				logger.error("Subscription timer not found or zero for :"+applicationId+"::"+aconyxUsername+"::"+channelUsername+"");			
				if(timer!=null)
					logger.error("Subscription timer time: "+timer.getTimeRemaining());
				
				Channel channel=new Channel();
				channel.setChannelName(SIPPACAdaptor.SIP_CHANNEL);
				channel.setChannelUsername(channelUsername);
				channel.setChannelURL(reqURI.toString());
				//adaptor.endSubscriptionForChannel(applicationId, aconyxUsername, channel);
				adaptor.subscribeForUserPresence(applicationId, aconyxUsername, channel);
			}else if(logger.isDebugEnabled()){
				logger.debug("Subscription found for :"+applicationId+"::"+aconyxUsername+"::"+channelUsername+"");
			}
			
		}
		else {
			if (appsession.getAttribute(Constants.ATTRIB_PAC_APP_SESSION) != null) {
					logger.error("Inside sessionDidActivate() for PAC Application Session");
				try{
					if (appsession.isValid())
						appsession.invalidate();
				}catch(Exception e){
					logger.error("Exception in invalidating app session",e);
				}
				createAppSession();
				start();
				Timer timer=new Timer();
				logger.error("Scheduling PACSessionRecoveryTask for recovery of session after FT.");
				timer.schedule(new PACSessionRecoveryTask(), 120000);
				
			}
		}
	}
	
	private void createAppSession() {
		SipApplicationSession applicationSession = factory.createApplicationSession();
		if(logger.isDebugEnabled()){
			logger.debug("PAC SIP Application Session created is:"+ applicationSession);
		}
		applicationSession.setAttribute(Constants.ATTRIB_PAC_APP_SESSION,Constants.ATTRIB_PAC_APP_SESSION);
		applicationSession.setInvalidateWhenReady(false);
		logger.info("Creating timer for PAC SIP Application Session");
		timerService.createTimer(applicationSession,timerRestartTime*60*1000, true, timerInfo);
	}
	
	@Override
	public void sessionWillPassivate(SipApplicationSessionEvent arg0) {
		if(logger.isDebugEnabled()){
			logger.info("Inside sessionWillPassivate()......");
		}
	}
	@Override
	public void timeout(ServletTimer timer) {
		if(logger.isDebugEnabled()){
			logger.info("Inside timeout()......");
		}
		if(isActiveSAS()&& timerInfo.equals(timer.getInfo())){
			SipApplicationSession appsession=timer.getApplicationSession();
				if(appsession.getAttribute(Constants.ATTRIB_PAC_APP_SESSION)!=null){
					appsession.setExpires(timerRestartTime+1);
					logger.info("Creating timer for PAC SIP Application Session");
					timerService.createTimer(appsession, timerRestartTime*60*1000, true,timerInfo);
				}
			}
		else if(subscribeTimerInfo.equals(timer.getInfo())){
			SipApplicationSession appsession=timer.getApplicationSession();
			String applicationId=(String)appsession.getAttribute(Constants.ATTRIB_APPLICATIONID);
			String aconyxUsername=(String)appsession.getAttribute(Constants.ATTRIB_ACONYXUSERNAME);
			String channelUsername=(String)appsession.getAttribute(Constants.ATTRIB_CHANNELUSERNAME);
			SipURI reqURI = (SipURI) appsession.getAttribute(Constants.ATTRIB_SUBSCRIBE_URI);
			/*appsession.removeAttribute(Constants.ATTRIB_SUBSCRIPTION_SESSION);
			appsession.setAttribute(Constants.ATTRIB_SESSION_STATE,Constants.STATE_READY_TO_INVALIDATE);
			adaptor.removeSIPAppSession(applicationId + aconyxUsername + channelUsername);
			try{
				if(appsession.isValid()){
					logger.debug("Invalidating appsession due to state'terminated'......"+appsession);
					appsession.invalidate();							
				}else{
					logger.debug("Appsession already invalidated'......"+appsession);
				}
			}catch(Exception e){
				logger.error("Exception occured in app session invalidation",e);
			}*/
			
			Channel channel=new Channel();
			channel.setChannelName(SIPPACAdaptor.SIP_CHANNEL);
			channel.setChannelUsername(channelUsername);
			channel.setChannelURL(reqURI.toString());
			synchronized(channelUsername.intern()) {
				adaptor.subscribeForUserPresence(applicationId, aconyxUsername, channel);
			}
		}
	}
	/**
	 * @param isActiveSAS the isActiveSAS to set
	 */
	public static void setActiveSAS(boolean isActiveSAS) {
		PACSIPServlet.isActiveSAS = isActiveSAS;
	}
	/**
	 * @return the isActiveSAS
	 */
	public static boolean isActiveSAS() {
		return isActiveSAS;
	}
	/**
	 * This method will load all required map from database and create a SipApplicationSession 
	 * with timer for this PAC application.
	 */
	public static synchronized void start(){
		if(logger.isDebugEnabled()){
			logger.debug("Inside start().....");
		}
		if (!isActiveSAS() && PACManager.PAC_CACHE_STATE.equals(Constants.STATE_INIT)) {
			setActiveSAS(true);
			PACManager.PAC_CACHE_STATE=Constants.STATE_LOADING;
			logger.error("loading PACMemoryMap from DB....");
			long startTime=System.currentTimeMillis();
			PACDAOImpl dao = new PACDAOImpl();
			boolean isLoaded=false;
			try {
				isLoaded=dao.loadPACMemoryMapFromDB();				
			} catch (Exception e) {
				logger.error("Exception while loading PACMemoryMap from DB", e);
			}
			if(isLoaded){
				logger.error("loaded PACMemoryMap from DB in "+(System.currentTimeMillis()-startTime)+"ms");
				PACManager.PAC_CACHE_STATE=Constants.STATE_LOADED;
			}else{
				logger.error("Unable to load PACMemoryMap from DB");
				reset();
			}
		}
	}
	
	/**
	 * This method will reset PAC cache in case of failure during loading of PAC Memory map from DataBase.
	 */
	private static void reset(){
		PACMemoryMap.reset();
		setActiveSAS(false);
		PACManager.PAC_CACHE_STATE=Constants.STATE_INIT;
	}
	
	@Override
	public void roleChanged(String clusterId, PartitionInfo pInfo) {
		logger.error("Inside roleChanged() with role:"+AseRoles.getString(pInfo.getRole()));
		if(pInfo.getRole()==AseRoles.ACTIVE && isSipReplicationEnabled){			
				PACManager.SUBSCRIBE_FOR_PRESENCE_ON_LOAD=false;
		}
	}
	
	/**
     * This method cancel a timer specified by timerName and removes it from
     * appSession.
     * 
     * @param appSession
     *            the application session used for timer.
     * @param timerName
     *            name of timer to be canceled.
     */
    public static void cancelTimer(SipApplicationSession appSession, String timerName) {
	if (logger.isDebugEnabled()) {
	    logger.debug("[PAC] In cancelTimer for timer:" + timerName);
	}
	if (timerName == null) {
	    return;
	}
	try {
	    String timerId = (String) appSession.getAttribute(timerName);
	    if (timerId != null) {
		ServletTimer sipTimer = appSession.getTimer(timerId);
		if (sipTimer != null) {
		    sipTimer.cancel();
		    if (logger.isDebugEnabled()) {
			logger.debug("[PAC] timer cancelled");
		    }
		} else {
		    if (logger.isDebugEnabled()) {
			logger.debug("[PAC] timer not found");
		    }
		}

	    } else {
		if (logger.isDebugEnabled()) {
		    logger.debug("[PAC] timer id not present");
		}
	    }
	} catch (Exception e) {
	    logger.error("[PAC] Unable to cancel timer due to exception: ", e);
	}
	appSession.removeAttribute(timerName);
	if (logger.isDebugEnabled()) {
		logger.debug("[PAC] Exitting cancelTimer for timer:" + timerName);
	}
    }
	@Override
	public void sessionCreated(SipApplicationSessionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void sessionDestroyed(SipApplicationSessionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void sessionExpired(SipApplicationSessionEvent event) {

		SipApplicationSession applicationSession=event.getApplicationSession();
		if (applicationSession.getAttribute(Constants.ATTRIB_PAC_APP_SESSION) != null) {
			if(logger.isDebugEnabled()){
				logger.debug("Inside sessionExpired for PAC Application Session");
			}
			applicationSession.setExpires(timerRestartTime);
			if(logger.isDebugEnabled()){
				logger.debug("Extended session expiry to:"+timerRestartTime);
			}
		}
		else if (applicationSession.getAttribute(Constants.ATTRIB_SUBSCRIPTION_SESSION) != null) {
			if(logger.isDebugEnabled()){
				logger.debug("Inside sessionExpired for PAC Subscription Application Session");
			}
			applicationSession.setExpires(5);
			if(logger.isDebugEnabled()){
				logger.debug("Extended subscription session expiry to:"+5);
			}
		}

	}
	@Override
	public void sessionReadyToInvalidate(SipApplicationSessionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
