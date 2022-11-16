package com.genband.jain.protocol.ss7.tcap;

import jain.InvalidAddressException;
import jain.MandatoryParameterNotSetException;
import jain.ParameterNotSetException;
import jain.protocol.ss7.SccpUserAddress;
import jain.protocol.ss7.SignalingPointCode;
import jain.protocol.ss7.SubSystemAddress;
import jain.protocol.ss7.sccp.StateReqEvent;
import jain.protocol.ss7.tcap.ComponentReqEvent;
import jain.protocol.ss7.tcap.DialogueReqEvent;
import jain.protocol.ss7.tcap.JainTcapProvider;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;
import javax.servlet.sip.ServletParseException;
import javax.servlet.sip.ServletTimer;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipApplicationSessionActivationListener;
import javax.servlet.sip.SipApplicationSessionEvent;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipServlet;
import javax.servlet.sip.SipServletMessage;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSession;
import javax.servlet.sip.SipSessionActivationListener;
import javax.servlet.sip.SipSessionEvent;
import javax.servlet.sip.SipURI;
import javax.servlet.sip.TimerService;

import org.apache.log4j.Logger;

import com.baypackets.ase.common.Registry;
import com.baypackets.ase.container.AseApplicationSession;
import com.baypackets.ase.container.AseHost;
import com.baypackets.ase.spi.container.SasApplicationSession;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.util.AseStrings;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.baypackets.bayprocessor.slee.threadmonitor.MonitoredThread;
import com.genband.tcap.parser.ConfigurationMsgDataType;
import com.genband.tcap.parser.TagsConstant;
import com.genband.tcap.parser.TcapContentReaderException;
import com.genband.tcap.parser.TcapContentWriterException;
import com.genband.tcap.parser.TcapParser;
import com.genband.tcap.parser.TcapType;
import com.genband.tcap.provider.TcapSession;

public class TcapProviderGateway implements Serializable, SipSessionActivationListener,
				SipApplicationSessionActivationListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 838204052396697379L;
	public static final String	ingwParam	= "ingw-sessionid";
	public static final String	seqdlg		= "seq-dlg";
	
	static private String									heartBeatMethod					= AseStrings.OPTIONS;
	static private String									PDUContentMethod				= AseStrings.NOTIFY;
	static private String									sccpContentInformationMethod	= AseStrings.INFO;
	static private String									MimeContentType					= "application/tcap";

	static private TreeMap<String, TcapProviderGateway>		spcMap							= new TreeMap<String, TcapProviderGateway>();
	/* rebuilt on non-active instances */
	static private Map<String, TcapProviderGateway>		sasMap								= new ConcurrentHashMap<String, TcapProviderGateway>();
	static private Map<String, SipApplicationSession>	applicationSessionMap				= new ConcurrentHashMap<String, SipApplicationSession>();
	/* rebuilt on non-active instances */
	static private LinkedList<TcapProviderGateway>			tpgList							= new LinkedList<TcapProviderGateway>();
	static private String lockObj = new String("TCAP_SASMAP_LOCK");
	//ConcurrentHashMap<String, AseApplicationSession> appSessionMapForInapDlgId ; 

	transient private SipSession							iSession						= null;
	transient private SipApplicationSession					sas								= null;
	transient private String								sasId							= null;

	public String getSasId() {
		return sasId;
	}

	public void setSasId(String id) {
		this.sasId = id;
	}

	transient private JainTcapProviderImpl	ss;
	transient private ServletContext		sc						= null;
	transient private SipFactory			factory					= null;
//	transient private ServletTimer			connectionTimer			= null;
	transient private Timer			        connectionTimer			= null;
	transient private ServletTimer			longPollHBTimer			= null;
	transient private SipServletRequest		hb;
	transient private SipURI				sourceUri				= null;
	transient private SipURI				destinationUri			= null;
	//transient private boolean				tcapContentInDialogue	= false;
	//transient private boolean				sccpContentInDialogue	= true;
	transient private int					idleConnectionKeepAliveTimer = 2000;
	transient private int					deltaTime = 500;
	transient private int					failedConnectionRetryTimer =1000;
	transient private int					optionRetryCount = 15;
	transient private SipURI				notifyRoute				= null;
	transient private SipURI				inviteRoute				= null;
	transient volatile private long			aliveTime;
	transient volatile private long			timeDiffSinceActive;
	
	private final String IDLE_CONN_INTERVAL ="tcap.idle.conn.interval";
	private final String FAILED_CONN_RETRY_INTERVAL ="tcap.failed.conn.retry.interval";
	private final String OPTION_RETRY_COUNT ="tcap.conn.option.retry.count";
	
	private final String TCAP_HB_SESSION ="PRIORTY_SESSION";
	private final String IN_PRGRS_CTR_DISABLE="IN_PRGRS_CTR_DISABLE";

	private boolean							connected				= false;
	//private int								dialogue				= 0;
	private String							ingwId					= null;
	public LinkedList<String>				spcList					= new LinkedList<String>();
	private LinkedList<SccpUserAddress>		ssaList					= new LinkedList<SccpUserAddress>();

	public LinkedList<SccpUserAddress> getSSAList() {
		return ssaList;
	}

	static private Logger			logger			= Logger.getLogger(TcapProviderGateway.class
														.getName());
	static private INGatewayManager	ingwManager		= null;
	static private String			REQ_ATTR_NAME	= "EVENT_OBJ".intern();



	private TcapProviderGateway() {}

	static public void init(INGatewayManager ingwmgr) {
		ingwManager = ingwmgr;
	}

	static public TcapProviderGateway createTcapProviderGateway(JainTcapProviderImpl ss,
					INGateway ingw) throws ServletParseException {
		synchronized (lockObj) {
			TcapProviderGateway tpg = new TcapProviderGateway();
			tpg.create(ss, ingw);
			return tpg;
		}
	}

	public void doErrorResponse(SipServletResponse resp) throws IOException {

		if (resp.getRequest().getMethod().equals(AseStrings.NOTIFY)) {
			logger.error("error response for notify:\n" + resp);

			Object eventObj = resp.getRequest().getAttribute(REQ_ATTR_NAME);
			if (eventObj != null && eventObj instanceof DialogueReqEvent) {
				JainTcapProviderImpl jtpi = JainTcapProviderImpl.getImpl();
				jtpi.tcapMessageFailed((DialogueReqEvent) eventObj, true);
			}

			//always clean appsession here..
			// if there are any pending request in appsession 
			//SAS will not allow those messages to reach application 
			removeSipApplicationSession(resp.getApplicationSession(),false);
			return;
		}

		if (resp.getRequest().getMethod().equals(AseStrings.INVITE)) {
			logger.error("CONNECTION ERROR to INGw:\n" + resp.getRequest() + "  " + resp);
		} else {
			if (resp.getRequest().equals(hb)) {
				logger.error("HEART BEAT ERROR:\n" + hb + resp);
			} else {
				//this will be reavhed in case of info
				logger.error("Error response :\n" + resp);
			}
		}
		//this handling will be comon for all error reponses other than notify
		try{
			disconnect();
		}catch(Exception e){
			logger.error("xception while disconnecting ingw");
		}finally{
		   StartConnectionDelay();
		}
	}

	static public List<TcapProviderGateway> getAllInstances() {
		synchronized (lockObj) {
			/* hopefully no one is creating these things now */
			return tpgList;
		}
	}

	public void FTLink(SipApplicationSession sas, SipSession inviteSession) {
		synchronized (lockObj) {
			try {
				if (logger.isDebugEnabled()) {
					logger.debug("FTLink sas:" + sas);
				}
				this.sas = sas;
				this.sasId = sas.getId();
				this.iSession = inviteSession;
				sasMap.put(sas.getId(), this);
				create(JainTcapProviderImpl.getImpl(), this.ingwId);
				aliveTime = System.currentTimeMillis();
				if (isConnected()) {
					if (logger.isDebugEnabled()) {
						logger.debug("FTLink calling StartHeartBeat");
					}
					StartHeartBeat();
				} else {
					if (logger.isDebugEnabled()) {
						logger.debug("FTLink calling StartConnectionDelay ");
					}
					StartConnectionDelay();
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	static public void addInstance(TcapProviderGateway tpg, SipApplicationSession sas,
					SipSession inviteSession) {
		if (logger.isDebugEnabled()) {
			logger.debug("Inside addInstance for appsesison id::" + sas.getId());
		}
		synchronized (lockObj) {
			tpg.FTLink(sas, inviteSession);
		}
	}

	static public TcapProviderGateway getByAddress(SignalingPointCode spc) {
		synchronized (lockObj) {
			return spcMap.get(spc.toString());
		}
	}

	static public TcapProviderGateway getTcapProviderGateway(SipServletMessage ssm) {
		if (logger.isDebugEnabled()) {
			logger.debug("Inside getTcapProviderGateway for appsesison id::"
							+ ssm.getApplicationSession().getId());
		}
		TcapProviderGateway tpg = sasMap.get(ssm.getApplicationSession().getId());
		if(tpg == null) {
			synchronized (lockObj) {
				if (tpg == null) {
					if (ssm instanceof SipServletRequest) {
						SipServletRequest ssr = (SipServletRequest) ssm;
						SipURI requestURI = (SipURI) ssr.getRequestURI();
						String id = requestURI.getParameter(ingwParam);
						tpg = id == null ? null : sasMap.get(id);
						if (tpg != null) {
							if (logger.isDebugEnabled()) {
								logger
								.debug("getTcapProviderGateway -- found gateway using request parm "
										+ ingwParam + " " + id);
							}
						}
					}
				}
			}
			
		}
		return tpg;
	}

	public void setAddress(byte[] iContent) throws IOException {
		try {
			TcapType addressInformation = TcapParser.parseSCCPMgmtMsg(iContent, ss);
			ConfigurationMsgDataType tct = addressInformation.getConfigMsg();
			if (tct != null) {
				List<SccpUserAddress> localUsers = tct.getOrigSua();
				Iterator<SccpUserAddress> iter = localUsers.iterator();
				while (iter.hasNext()) {
					SccpUserAddress ssat = iter.next();
					SubSystemAddress ssa = ssat.getSubSystemAddress();
					if (logger.isDebugEnabled()) {
						logger.debug("Putting ssa.getSignalingPointCode().toString() into spcMap "+ssat);
					}
					spcMap.put(ssa.getSignalingPointCode().toString(), this);
					ssaList.add(ssat);
					spcList.add(ssa.getSignalingPointCode().toString());
					try {
						JainTcapProviderImpl.getImpl().registerUserAddress(ssat);
					} catch (InvalidAddressException iae) {
						logger.error("Fatal error: invalid configuration received.", iae);
					}
				}
			}
		}

		catch (TcapContentReaderException tcre) {
			throw new IOException("could not unmarshal content cause" + tcre.getMessage()
							+ "content: " + iContent);
		} catch (ParameterNotSetException pew) {
			throw new IOException("could not unmarshal content cause" + pew.getMessage()
							+ "content: " + iContent);
		}
	}

	public void sendStateInformation(StateReqEvent sre) throws MandatoryParameterNotSetException,
					IOException {
		byte[] baos = null;
		try {
			baos = TcapParser.encodeSCCPMgmtMsg(sre, TagsConstant.SSN_STATE_IND_MSG);
			sendPayload(baos, sccpContentInformationMethod, true, sre);
		} catch (ParameterNotSetException pew) {
			throw new IOException("could not unmarshal content cause" + pew.getMessage()
							+ "content: " + pew);
		}
	}

	public void sendTcapContent(DialogueReqEvent dre, List<ComponentReqEvent> components,JainTcapProvider provider, boolean relay)
					throws TcapContentWriterException, MandatoryParameterNotSetException {
		byte[] encodedBuffer = TcapParser.encode(dre, components,provider,relay);
		sendPayload(encodedBuffer, PDUContentMethod, false, dre);
	}

	public boolean isConnected() {
		return connected;
	}

	public void StartConnectionDelay() {
		cancelTimers();
		if (logger.isDebugEnabled()) {
			logger.debug("StartConnectionDelay:Enter");
		}
//		connectionTimer = ((TimerService) sc.getAttribute(SipServlet.TIMER_SERVICE)).createTimer(
//						sas, failedConnectionRetryTimer, false, this);
		
		    connectionTimer = new Timer();
	        TimerTask task = new HeartbeatTimerTask(this);
	         
	        connectionTimer.schedule(task, failedConnectionRetryTimer);
	}

	public void StartHeartBeat() {
		cancelTimers();
		if (connectionTimer == null) {
			if (logger.isDebugEnabled()) {
				logger
					.debug("StartHeartBeat starting connection and long poll timers, for INGwId: "
									+ ingwId);
			}
			if (timeDiffSinceActive > idleConnectionKeepAliveTimer / 2) {
//				connectionTimer = ((TimerService) sc.getAttribute(SipServlet.TIMER_SERVICE))
//					.createTimer(sas, idleConnectionKeepAliveTimer / 2, idleConnectionKeepAliveTimer,false, false, this);
				
				 connectionTimer = new Timer();
			        TimerTask task = new HeartbeatTimerTask(this);
			         
			        connectionTimer.schedule(task, idleConnectionKeepAliveTimer / 2, idleConnectionKeepAliveTimer);
				if (logger.isDebugEnabled()) {
					logger.debug("timeDiffSinceActive: " + timeDiffSinceActive);
				}
			} else {
//				connectionTimer = ((TimerService) sc.getAttribute(SipServlet.TIMER_SERVICE))
//					.createTimer(sas, idleConnectionKeepAliveTimer,idleConnectionKeepAliveTimer, false, false, this);
				
				 connectionTimer = new Timer();
			        TimerTask task = new HeartbeatTimerTask(this);
			         
			        connectionTimer.schedule(task, idleConnectionKeepAliveTimer, idleConnectionKeepAliveTimer);
			}
			//	connectionTimer = ((TimerService)sc.getAttribute(SipServlet.TIMER_SERVICE)).createTimer(sas, idleConnectionKeepAliveTimer, false, this);
			longPollHBTimer = ((TimerService) sc.getAttribute(SipServlet.TIMER_SERVICE))
				.createTimer(sas, (optionRetryCount * idleConnectionKeepAliveTimer),(optionRetryCount * idleConnectionKeepAliveTimer),false, false, this);
		}
		//logger.log(Level.ERROR, "StartHeartBeat called for INGwId: " + ingwId);
	}

	public String INGwId() {
		return ingwId;
	}

	public void disconnect() {
		if (logger.isDebugEnabled()) {
			logger.debug("disconnect:Enter..");
		}
		disconnect(true);
	}
	
	public void disconnect(boolean flagForAlarm) {
		if (logger.isDebugEnabled()) {
			logger.debug("disconnect(boolean flagForAlarm):Enter..");
		}
		
		Thread currentThread = Thread.currentThread();
		MonitoredThread mt = null;
		if (MonitoredThread.class.isInstance(currentThread)) {
			mt = (MonitoredThread) currentThread;
		}
		
		cancelTimers();
		hb = null;
		
		if(flagForAlarm){
		ingwManager.inGatewayDown(ingwId);
		}
		
		connected = false;
		Iterator<SccpUserAddress> iter = ssaList.iterator();
		while (iter.hasNext()) {
			JainTcapProviderImpl.getImpl().SccpUserAddressOutOfService(iter.next());
		}

		/* remove dialogues associated with this INGw */
		//Cleanup all the SIP Application Sessions pending with this Gateway.
		JainTcapProviderImpl jtpi = JainTcapProviderImpl.getImpl();
		ArrayList<String> temp = new ArrayList<String>(sasMap.keySet());
		for (String id : temp) {
			//updated timestamp as during load this thread times out in cleanup
			if (mt != null) {
				mt.updateTimeStamp();
			}
			SipApplicationSession tempAS = applicationSessionMap.get(id);
			
			//clean tcap sesison for which request is sent in sent
			if(tempAS != null && tempAS != sas && tempAS.isValid()){
				try{
					Object eventObj = tempAS.getAttribute(REQ_ATTR_NAME);
					if (eventObj != null && eventObj instanceof DialogueReqEvent) {
						jtpi.tcapMessageFailed((DialogueReqEvent) eventObj, true);
					}
					removeSipApplicationSession(tempAS,true);
				}catch(Exception e){
					logger.error("Exception occured in tcapMessageFailed() callback..",e);
				}
			}
		}

		//Cleanup all the TCAP Sessions that are still pending.

		if (jtpi.replicator != null) {
			List<TcapSessionImpl> tcapSessions = jtpi.replicator.getAllTcapSessions();
			if (logger.isDebugEnabled()) {
				logger.debug("TS found on disconnect::" + tcapSessions.size());
			}
			for (TcapSessionImpl ts : tcapSessions) {
				
				//updated timestamp as during load this thread times out in cleanup
				if (mt != null) {
					mt.updateTimeStamp();
				}
				
				jtpi.tcapMessageFailed(ts, true);

				if (logger.isDebugEnabled()) {
					logger.debug("TS fialed::" + ts.getDialogueId());
				}

				//fail safe invalidate appsesison for tcaps esison if present and valid::
				String appSessionId = (String) ts
					.getAttribute(JainTcapProviderImpl.getImpl().APPLICATION_SESSION);
				SipApplicationSession appSession = null;
				if (appSessionId != null) {
					if (logger.isDebugEnabled()) {
						logger.debug("Appsesion id found");
					}
					appSession = ts.getAppSession(appSessionId);
				}
				if (appSession != null && appSession.isValid()) {
					if (logger.isDebugEnabled()) {
						logger.debug("Invalidate appsesion");
					}
					//appSession.invalidate();
					((SasApplicationSession) appSession).setTimeout(1);
				}

			}
		}

	}

	class HeartbeatTimerTask extends TimerTask
	{
//	    public static int i = 0;
	    TcapProviderGateway gw=null;
	    
	    public HeartbeatTimerTask(TcapProviderGateway tpgw){
	    	gw=tpgw;
	    }
	    public void run()
	    {

			if (isConnected()) {
				if (hb == null) {
					if (logger.isDebugEnabled()) {
						logger.debug("HeartBeat Log: heartbeat is null");
					}
					if (isDeltaConnectionIdle()) {
						//cancelTimers();
						try {
							/* send heartBeatMethod keep-alive */
							//logger.error("HB conn idle");
							hb = iSession.createRequest(heartBeatMethod);
							//factory.createRequest(getSipApplicationSession(), heartBeatMethod, source(), destination());
							//hb.setRequestURI(destination());
							
							if (logger.isDebugEnabled()) {
								logger.debug("HeartBeat Log: Idle keep-alive:" + hb.toString());
								logger.debug("HeartBeat Log: Sending heartbeat");
							}
							hb.send();
						}

						catch (Exception e) {
							logger.error("HeartBeat Log: Fatal keep-alive error:\n"
											+ e.getMessage() + "\n" + hb, e);
							hb = null;
						}
					}

					if (logger.isDebugEnabled()) {
						logger.debug("HeartBeat Log: calling Startheartbeat method");
					}
					//StartHeartBeat();
				} else {
					if (logger.isDebugEnabled()) {
						logger.debug("HeartBeat Log: Starting Connection Timer");
					}
					//this.startConnectionTimer();
				}
			} else {
				cancelTimers();
				try {
					connect();
					if (logger.isDebugEnabled()) {
						logger
							.debug("HeartBeat Log: creating timer longPollHBTimer for 30*idleConnectionKeepAliveTimer ");
					}
					longPollHBTimer = ((TimerService) sc.getAttribute(SipServlet.TIMER_SERVICE))
						.createTimer(sas, 30 * idleConnectionKeepAliveTimer, false, gw);
				} catch (IOException ioe) {
					logger
						.error("HeartBeat Log: Fatal IOException error while trying to connect to INGw",
										ioe);
					StartConnectionDelay();
				}
			}
		
	    }
	}

	public void timeout(ServletTimer timer) {
		if (logger.isDebugEnabled()) {
			logger.debug("HeartBeat Log: timeout called for INGwId: " + ingwId);
		}
		if (timer == longPollHBTimer) {
			if (isLongConnectionIdle()) {
				if (logger.isDebugEnabled()) {
					logger.debug("HeartBeat Log: HEART BEAT ERROR long poll timeout\n" + hb);
				}
				logger.error("long poll expired conn idle restablish connection");
				
				disconnect();
				//setting invalidate when ready to false to ensure app session is not invalidated  during sipsesion cleanup 
				sas.setInvalidateWhenReady(false);
				//invalidate isession here as options timer has failed and its response need not be handled
				iSession.invalidate();
				logger.error("SAS-INC Heartbeat failure....Starting Again");
				StartConnectionDelay();
				
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug("HeartBeat Log: calling Startheartbeat method");
				}
				
				//StartHeartBeat();
			}
		}
//		} else {
//			if (isConnected()) {
//				if (hb == null) {
//					if (logger.isDebugEnabled()) {
//						logger.debug("HeartBeat Log: heartbeat is null");
//					}
//					if (isDeltaConnectionIdle()) {
//						//cancelTimers();
//						try {
//							/* send heartBeatMethod keep-alive */
//							//logger.error("HB conn idle");
//							hb = iSession.createRequest(heartBeatMethod);
//							//factory.createRequest(getSipApplicationSession(), heartBeatMethod, source(), destination());
//							//hb.setRequestURI(destination());
//							
//							if (logger.isDebugEnabled()) {
//								logger.debug("HeartBeat Log: Idle keep-alive:" + hb.toString());
//								logger.debug("HeartBeat Log: Sending heartbeat");
//							}
//							hb.send();
//						}
//
//						catch (Exception e) {
//							logger.error("HeartBeat Log: Fatal keep-alive error:\n"
//											+ e.getMessage() + "\n" + hb, e);
//							hb = null;
//						}
//					}
//
//					if (logger.isDebugEnabled()) {
//						logger.debug("HeartBeat Log: calling Startheartbeat method");
//					}
//					//StartHeartBeat();
//				} else {
//					if (logger.isDebugEnabled()) {
//						logger.debug("HeartBeat Log: Starting Connection Timer");
//					}
//					//this.startConnectionTimer();
//				}
//			} else {
//				cancelTimers();
//				try {
//					connect();
//					if (logger.isDebugEnabled()) {
//						logger
//							.debug("HeartBeat Log: creating timer longPollHBTimer for 30*idleConnectionKeepAliveTimer ");
//					}
//					longPollHBTimer = ((TimerService) sc.getAttribute(SipServlet.TIMER_SERVICE))
//						.createTimer(sas, 30 * idleConnectionKeepAliveTimer, false, this);
//				} catch (IOException ioe) {
//					logger
//						.error("HeartBeat Log: Fatal IOException error while trying to connect to INGw",
//										ioe);
//					StartConnectionDelay();
//				}
//			}
//		}
	}

	private void cancelTimers() {
		if (connectionTimer != null) {
			connectionTimer.cancel();
			connectionTimer = null;
		}
		if (longPollHBTimer != null) {
			longPollHBTimer.cancel();
			longPollHBTimer = null;
		}
	}

	public boolean isConnectionIdle() {

		timeDiffSinceActive = (System.currentTimeMillis() - aliveTime);
		return timeDiffSinceActive > idleConnectionKeepAliveTimer;
		
	}
	
	public boolean isLongConnectionIdle() {

		timeDiffSinceActive = (System.currentTimeMillis() - aliveTime);
		return timeDiffSinceActive > (optionRetryCount*idleConnectionKeepAliveTimer);

	}
	
	public boolean isDeltaConnectionIdle() {

		timeDiffSinceActive = (System.currentTimeMillis() - aliveTime);
		if (logger.isDebugEnabled()) {
			logger.debug("isDeltaConnectionIdle() timeDiffSinceActive :" + timeDiffSinceActive);
		}
		return ( (timeDiffSinceActive>20000) || (timeDiffSinceActive > (idleConnectionKeepAliveTimer-deltaTime)));

	}

	public void activeConnection(SipServletMessage ssm) {
		if (ssm instanceof SipServletResponse) {
			if (((SipServletResponse) ssm).getRequest().equals(hb))
				hb = null;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Connection active on INGw[" + ingwId + ", " + getSasId()
							+ "], SipApplicationSession: " + ssm.getApplicationSession().getId());
		}
		//		if (ssm.getApplicationSession() != sas)
		//			removeSipApplicationSession(ssm.getApplicationSession());

		// StartHeartBeat();

		aliveTime = System.currentTimeMillis();

		if (logger.isDebugEnabled()) {
			logger.debug("alivetime:" + aliveTime);
		}
	}

	public void connect() throws IOException {
		hb = null;
		//set invalidate when readty to true as it was et false during long HB timer expiry
		if(sas!=null){
			sas.setInvalidateWhenReady(true);
		}
		removeSipApplicationSession(sas,false);
		sas = getSipApplicationSession();
		sas.setAttribute(TCAP_HB_SESSION, "true");
		sas.setAttribute(IN_PRGRS_CTR_DISABLE, "true");
		sasId = sas.getId();
		SipServletRequest ssr = factory.createRequest(sas, AseStrings.INVITE, source(), destination());
		iSession = ssr.getSession();
		ssr.setAttribute(com.baypackets.ase.util.Constants.DISABLE_OUTBOUND_PROXY, "1");
		iSession.setAttribute(com.baypackets.ase.util.Constants.DISABLE_OUTBOUND_PROXY, "1");
		iSession.setAttribute(TcapProviderGateway.class.getName(), this);

		ssr.setRequestURI(destination());
		if (inviteRoute != null) {
			ssr.pushRoute(inviteRoute);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Sending invite");
		}
		ssr.send();
	}

	public void connected(SipServletResponse ssr) {
		/*
		 * TODO: need to tell INGw about all associated users
		 * NStateReqEvent sreq = new NStateReqEvent(this, userAddress.getSubSystemAddress(),
		 * SccpConstants.USER_IN_SERVICE);
		 * sendStateReqEvent(sreq);
		 */
		ingwManager.inGatewayUp(ingwId);
		connected = true;
	}

	public SipApplicationSession getSipApplicationSession() {
		synchronized (lockObj) {
			SipApplicationSession returnSas = factory.createApplicationSession();

			sasMap.put(returnSas.getId(), this);
			if (logger.isDebugEnabled()) {
				logger.debug("After adding sasMap size:" + sasMap.size());
				logger.debug("Inside getSipApplicationSession added appsesion::"
								+ returnSas.getId());
			}
			return returnSas;
		}
	}

	public void addSipApplicationSession(SipApplicationSession addSas) {
		//for debugging one issue
		if(addSas.getId() == null){
			logger.error("Appsesion with ID NULL::"+addSas.toString());
			logger.error("Appsesion with ID NULL::"+addSas);
			logger.error("Appsesion with ID NULL::"+((AseApplicationSession)addSas).getIc());
		}
		
		if (addSas != null && sasMap.get(addSas.getId())== null ) {
			synchronized (lockObj) {
				sasMap.put(addSas.getId(), this);
				if (logger.isDebugEnabled()) {
					logger.debug("After adding sasMap size:" + sasMap.size());
					logger.debug("Inside getSipApplicationSession added appsesion::"
									+ addSas.getId());
				}//end if asssas!=null
			}//end synchronized
		}//end if logger
		
		//for debugging one issue
		if(addSas.getId() == null){
			logger.error("Appsesion with ID NULL::"+addSas.toString());
			logger.error("Appsesion with ID NULL::"+addSas);
			logger.error("Appsesion with ID NULL::"+((AseApplicationSession)addSas).getIc());
		}
	}

	public void removeSipApplicationSession(SipApplicationSession removedSas, boolean otherSession) {
		synchronized (lockObj) {
			if (removedSas != null) {
				sasMap.remove(removedSas.getId());
				if (logger.isDebugEnabled()) {
					logger.debug("After removing sasMap size:" + sasMap.size());
					logger.debug("Inside removeSipApplicationSession deleting appsesion::"
									+ removedSas.getId());
				}
			}//end if not null
		}//@end synch
		//mark as for invalidate
		if(removedSas!=null){
			if(otherSession){
				((SasApplicationSession) removedSas).setTimeout(1);
			}else{
				removedSas.invalidate();
			}
		}
	}

	private void create(JainTcapProviderImpl ss, String ingwId) throws ServletParseException {
		INGateway ingw = ingwManager.getINGateway(ingwId);
		create(ss, ingw);
	}

	private void create(JainTcapProviderImpl ss, INGateway ingw) throws ServletParseException {
		if (logger.isDebugEnabled()) {
			logger.debug("create tcapGateway");
		}
		if (ingw == null)
			throw new ServletParseException("INGateway cannot be NULL");
		
		ConfigRepository configRep = (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
		
		this.ss = ss;
		sc = ss.getServletContext();
		ingwId = ingw.getId();
		factory = (SipFactory) sc.getAttribute(SipServlet.SIP_FACTORY);

		destinationUri = factory.createSipURI("TcapProvider", ingw.getHost());
		destinationUri.setPort(ingw.getPort());
		destinationUri.setTransportParam(sc.getInitParameter("ConnectionTransport"));

		String fromAddress = null;
		try {
			fromAddress = "sip:TcapListener@" + InetAddress.getLocalHost().getCanonicalHostName();
		} catch (Exception e) {
			logger.error("cannot find Inet address for host.", e);
		}
		sourceUri = (SipURI) factory.createURI(fromAddress);
		sourceUri.setTransportParam(sc.getInitParameter("ConnectionTransport"));
		
		String idlConnTime = configRep.getValue(IDLE_CONN_INTERVAL);
		String failedConnRetryinterval = configRep.getValue(FAILED_CONN_RETRY_INTERVAL);
		String failureDetectionMultFactor = configRep.getValue(OPTION_RETRY_COUNT);
		
		if(idlConnTime !=null && idlConnTime.matches("[1-9][0-9]*")){
			idleConnectionKeepAliveTimer = Integer.parseInt(idlConnTime);
		}
		
		if(failedConnRetryinterval !=null && failedConnRetryinterval.matches("[1-9][0-9]*")){
			failedConnectionRetryTimer = Integer.parseInt(failedConnRetryinterval);
		}
		
		if(failureDetectionMultFactor !=null && failureDetectionMultFactor.matches("[1-9][0-9]*")){
			optionRetryCount = Integer.parseInt(failureDetectionMultFactor);
		}
		
		
//		idleConnectionKeepAliveTimer = Integer.valueOf(sc
//			.getInitParameter("IdleConnectionKeepAliveTimer"));
//		failedConnectionRetryTimer = Integer.valueOf(sc
//			.getInitParameter("FailedConnectionRetryTimer"));

		String strNotifyRoute = sc.getInitParameter("notify.route");
		notifyRoute = (strNotifyRoute != null && strNotifyRoute.startsWith("sip:")) ? (SipURI) factory
			.createURI(strNotifyRoute) : null;
		String strInviteRoute = sc.getInitParameter("invite.route");
		inviteRoute = (strInviteRoute != null && strInviteRoute.startsWith("sip:")) ? (SipURI) factory
			.createURI(strInviteRoute) : null;

		tpgList.add(this);

		Iterator<String> spcs = spcList.iterator();
		for (; spcs.hasNext();) {
			spcMap.put(spcs.next(), this);
		}
	}

	private SipURI destination() {
		return (SipURI)destinationUri.clone();
	}

	private SipURI source() {
		SipURI encodedURI = (SipURI) sourceUri.clone();
		encodedURI.setParameter(ingwParam, sas.getId());
		return encodedURI;
	}

	private void sendPayload(byte[] baos, String byMethod, boolean inDialogue, Object eventObj)
					throws MandatoryParameterNotSetException {
		SipServletRequest ssr = null;
		SipApplicationSession appSession = null;
		JainTcapProviderImpl tcapProvder = JainTcapProviderImpl.getImpl();
		String tcSeq=null;
		String seqdlgValue=null;
		
		try {
			if (inDialogue) {
				if (iSession != null) { // iSession will be null in simulator mode
					//OPTIONS----INFO FIX
					//ssr = iSession.createRequest(byMethod);
					appSession = iSession.getApplicationSession();
				}
			}

			else {
				
				if(byMethod.equals(AseStrings.NOTIFY)){
					tcSeq=getTcSeqValue((DialogueReqEvent)eventObj,tcapProvder);
					DialogueReqEvent dlg = (DialogueReqEvent) eventObj;
					seqdlgValue=tcSeq + AseStrings.MINUS + dlg.getDialogueId();
				}
				
				if (tcapProvder.simulator == true) {
					ssr = getRequest(byMethod, tcapProvder, eventObj,seqdlgValue);
					SipURI encodedURI = (SipURI) destinationUri.clone();
					encodedURI.setParameter(ingwParam, sasId);
					ssr.setRequestURI(encodedURI);
				} else {
					ssr = getRequest(byMethod, tcapProvder, eventObj,seqdlgValue);
					//ssr = factory.createRequest(getSipApplicationSession(), byMethod, source(), destination());
					ssr.setAttribute(com.baypackets.ase.util.Constants.DISABLE_OUTBOUND_PROXY, "1");
					ssr.setRequestURI(destination());
				}
				
			}

			if (ssr != null) {
				if (byMethod.equals("NOTIFY")) {
					
					ssr.addHeader("Event", "tcap-event");
					ssr.addHeader("Subscription-State", "active");
					DialogueReqEvent dlg = (DialogueReqEvent) eventObj;
					String dlgId = Integer.toString(dlg.getDialogueId());
					
					ssr.addHeader(JainTcapProviderImpl.DIALOG_ID, dlgId);
					TcapSession tcapSession =null; // tcapProvder.getTcapSession(dlg.getDialogueId());
					
					//@reeta added for Ansi correlation of out going messages
					
					Integer tcCorrId=tcapProvder.getTCCorrelationId(dlg.getDialogueId());
					
					if(tcCorrId==null){
						tcapSession = tcapProvder.getTcapSession(dlg.getDialogueId());
					}else{
						tcapSession = tcapProvder.getTcapSession(tcCorrId.intValue());
						ssr.addHeader(JainTcapProviderImpl.getImpl().TC_CORR_ID_HEADER, tcCorrId.toString());
					}

					ssr.getRequestURI().setParameter(seqdlg,seqdlgValue);
					ssr.addHeader("TC-Seq", String.valueOf(tcSeq));
					
					if (notifyRoute != null) {
						ssr.pushRoute(notifyRoute);
					}
					appSession = ssr.getApplicationSession();

					//added to ensure appsesison is invalidated after last 200ok
					Integer pendingNotify = (Integer) appSession.getAttribute("PENDING_NOTIFY");
					if (pendingNotify != null) {
						pendingNotify++;
						appSession.setAttribute("PENDING_NOTIFY", pendingNotify);
					} else {
						appSession.setAttribute("PENDING_NOTIFY", new Integer(1));
					}

					//additional changes for new request sent if appsesison detail doesn't exist already
					//adds appsesion to inap dialogid map in container to resue appsesion for incoming message
					AseHost host = (AseHost) Registry.lookup(Constants.NAME_HOST);
					if (host.getAppSessionMapForInapDlgId().get(dlgId) == null) {
						//appSession.setAttribute(tcapProvder.NOTIFY_SESSION,ssr.getSession().getId());
						tcapSession.setAttribute(tcapProvder.APPLICATION_SESSION,
										appSession.getId());
						host.getAppSessionMapForInapDlgId().put(dlgId, appSession);
						appSession.setAttribute(JainTcapProviderImpl.DIALOG_ID, dlgId);
					}
				}

				if (eventObj != null) {
					ssr.setAttribute(REQ_ATTR_NAME, eventObj);
					ssr.getApplicationSession().setAttribute(REQ_ATTR_NAME, eventObj);
				}
				ssr.setContent(baos, MimeContentType);
				ssr.send();
				//OPTIONS----INFO FIX
				//This fix has a scenario where in there is a problem in following scenario:
				//In case of OPTIONS, timer get enqueued and after dequeue actual OPTIONS
				//request is made while in case of INFO, first request is made then it is 
				//enqueued so there is a possibility that larger cseq goes first, in order to 
				//handle this specific case we need to uniform the way these messages are 
				//enqueued. Therefore in case of INFO requests, timer will get enqueued and 
				//the timeout event will go to Heartbeat timer listener where it will call
				//tcap provider gateway to initiate the INFO send process.
				/*
				 * if (byMethod.equals(sccpContentInformationMethod)){
				 * logger.log(Level.DEBUG, "INFO Request, enqueing it to the App Session queue");
				 * AseMessage message = new AseMessage(ssr);
				 * ((AseApplicationSession) appSession).enqueMessage(message);
				 * } else{
				 * ssr.send();
				 * }
				 */
			} else if (byMethod.equals(sccpContentInformationMethod)) {
				TcapProviderListenerHandShake tlHsk = this.new TcapProviderListenerHandShake();
				tlHsk.setBaos(baos);
				tlHsk.setEventObj(eventObj);
				if (logger.isDebugEnabled()) {
					logger
						.debug("INFO Request, wrapping it in the timer and enqueing it to the App Session queue");
				}
				((TimerService) sc.getAttribute(SipServlet.TIMER_SERVICE)).createTimer(appSession,
								0, false, tlHsk);
			}

			ConfigRepository config = (ConfigRepository) Registry
				.lookup(Constants.NAME_CONFIG_REPOSITORY);
			String sysappEnable = (String) config.getValue(Constants.PROP_SYSAPP_ENABLE);
			if (sysappEnable != null && sysappEnable.trim().contains("cdr")) {
				if (byMethod.equals(AseStrings.NOTIFY)) {
					DialogueReqEvent dlg = (DialogueReqEvent) eventObj;
					int dlgId = dlg.getDialogueId();
					if (logger.isDebugEnabled()) {
						logger.debug("Obtain the CDR object for Dialog Id = " + dlgId);
					}
					TcapSession ts = null; //JainTcapProviderImpl.getImpl().getTcapSession(dlgId);
					
                    Integer tcCorrId=JainTcapProviderImpl.getImpl().getTCCorrelationId(dlgId);
					
					if(tcCorrId==null){
						ts = JainTcapProviderImpl.getImpl().getTcapSession(dlgId);
					}else{
						ts = JainTcapProviderImpl.getImpl().getTcapSession(tcCorrId.intValue());
					}
					
					if (ts == null) {
						logger.error("TcapSession is NULL for DialogueID = " + dlgId);
						throw new MandatoryParameterNotSetException("TcapSession Missing!!");
					} else {
						if (logger.isDebugEnabled()) {
							logger.debug("sendPayLoad()--> TcapSession found for Dialog Id = "
											+ dlgId);
						}

						if (ts.getAttribute(Constants.CDR_KEY) == null) {
							ts.setAttribute(Constants.CDR_KEY,
											ssr.getSession().getAttribute(
															Constants.CDR_KEY_FOR_TCAP));
							if (logger.isDebugEnabled()) {
								logger.debug("sendPayLoad()--> Attribute value set to ["
												+ ts.getAttribute(Constants.CDR_KEY) + "]");
							}
						}
					}
				}
			}
		}

		catch (UnsupportedEncodingException uee) {
			/*
			 * TODO: consider expanding the reference implementation to support IOException, so this
			 * can be thrown through to user
			 */
			logger.error("platform does not support content.", uee);
			throw new MandatoryParameterNotSetException("platform does not support content.", uee);
		}

		catch (IOException ioe) {
			/*
			 * TODO: consider expanding the reference implementation to support IOException, so this
			 * can be thrown through to user
			 */
			logger.error("platform encoutered IOException while sending to gateway "
							+ destinationUri + "\n" + ssr, ioe);
			throw new MandatoryParameterNotSetException(
							"platform encoutered IOException while sending to gateway "
											+ destinationUri + "\n" + ssr, ioe);
		}
	}

	private String getTcSeqValue(DialogueReqEvent eventObj,JainTcapProviderImpl tcapProvder) throws MandatoryParameterNotSetException {

		String tcSeq = null;
		int dialogueId=eventObj.getDialogueId();
		TcapSession tcapSession = tcapProvder.getTcapSession(dialogueId);
		int seq = tcapSession.incrementCounter();
		if (seq < 10)
			tcSeq = "00" + String.valueOf(seq);
		else if (seq >= 10 && seq < 100)
			tcSeq = "0" + String.valueOf(seq);
		else
			tcSeq = String.valueOf(seq);
		return tcSeq;
	}

	private SipServletRequest getRequest(String byMethod, JainTcapProviderImpl tcapProvder,
					Object eventObj,String seqdlgValue) {
		if (logger.isDebugEnabled()) {
			logger.debug("Inside getRequest");
		}
		SipServletRequest ssr = null;
		SipApplicationSession appSession = null;
		String appSessionId = null;
		TcapSession ts = null;
		DialogueReqEvent dre = (DialogueReqEvent) eventObj;
		try {
			if (byMethod.equals(AseStrings.NOTIFY)) {
				if (logger.isDebugEnabled()) {
					logger.debug("Inside getRequest  NOTIFY");
				}
				

				  Integer tcCorrId=tcapProvder.getTCCorrelationId(dre.getDialogueId());
					
					if(tcCorrId==null){
						ts = tcapProvder.getTcapSession(dre.getDialogueId());
					}else{
						ts =tcapProvder.getTcapSession(tcCorrId.intValue());
					}

				if (ts != null) {
					appSessionId = (String) ts.getAttribute(tcapProvder.APPLICATION_SESSION);
				}

				if (logger.isDebugEnabled()) {
					logger.debug("Inside getRequest NOTIFY got appsession id::" + appSessionId);
				}

				if (appSessionId != null) {
					appSession = ts.getAppSession(appSessionId);
				}//end if appsesion id = null;

				if (logger.isDebugEnabled()) {
					logger.debug("Inside getRequest NOTIFY got appsession::" + appSession);
				}
			}//end if req is notify
		} catch (MandatoryParameterNotSetException e) {
			//do nothing use deafult
		}

		if (appSession == null) {
			appSession = getSipApplicationSession();
			try {
				appSession.setAttribute(JainTcapProviderImpl.DIALOG_ID, dre.getDialogueId());
			} catch (MandatoryParameterNotSetException e) {
				
			}
			if (logger.isDebugEnabled()) {
				logger.debug("Inside getRequest appsesion is null creating new appsession::"
								+ appSession);
			}
		} else {
			//adding to appsession map
			addSipApplicationSession(appSession);
		}
		
		SipURI fromURI=(SipURI) sourceUri.clone();
		SipURI toURI=destination();
				
		if(seqdlgValue!=null){
			toURI.setParameter(seqdlg, seqdlgValue);
		}
		ssr = factory.createRequest(appSession, byMethod,fromURI,toURI);
		return ssr;
	}

	public void sessionDidActivate(SipSessionEvent se) {
		if (logger.isDebugEnabled()) {
			logger.debug("Session activated.tcapgatewayInstance:" + this.connected + "   "
							+ this.ssaList + " object :" + this);
		}
		SipApplicationSession sas = se.getSession().getApplicationSession();
		
		if (this.connected) {
			if (logger.isDebugEnabled()) {
				logger.debug("Session activated.tcapgatewayInstance: gateway is connected so setting sua status 1");
			}
			for (SccpUserAddress sua : this.ssaList) {
				sua.setSuaStatus(1);
			}
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("Session activated.tcapgatewayInstance: gateway is not connected so not setting sua status");
			}
		}
		TcapProviderGateway.addInstance(this, sas, se.getSession());
	}

	public void sessionWillPassivate(SipSessionEvent se) {}

	static public void sessionCreated(SipApplicationSessionEvent ev) {
		if (logger.isDebugEnabled()) {
			logger.debug("sessionCreated @@@" + ev.getApplicationSession().getId());
		}
		
		applicationSessionMap.put(ev.getApplicationSession().getId(),
							ev.getApplicationSession());
		
	}

	static public void sessionDestroyed(SipApplicationSessionEvent ev) {
		if (logger.isDebugEnabled()) {
			logger.debug("sessionDestroyed @@@" + ev.getApplicationSession().getId());
		}
		
		applicationSessionMap.remove(ev.getApplicationSession().getId());
		if (logger.isDebugEnabled()) {
			logger.debug("After removing applicationSessionMap size:"
					+ applicationSessionMap.size());

		}

		synchronized (lockObj) {
			sasMap.remove(ev.getApplicationSession().getId());
			if (logger.isDebugEnabled()) {
				logger.debug("After removing sasMap size:" + sasMap.size());
			}
		}
	}

	static public void sessionExpired(SipApplicationSessionEvent ev) {
		if (logger.isDebugEnabled()) {
			logger.debug("sessionExpired @@@" + ev.getApplicationSession().getId());
		}
		
		TcapSession ts = null;
		SipApplicationSession appsession = ev.getApplicationSession();
		String dlgId = (String) appsession.getAttribute(JainTcapProviderImpl.DIALOG_ID);

		if (dlgId != null) {
			if (logger.isDebugEnabled()) {
				logger.debug(dlgId + " App sessionDidActivate: appsesion with dlg id");
			}
			
			int dialogueId=Integer.parseInt(dlgId);
	//		ts = JainTcapProviderImpl.getImpl().getTcapSession(Integer.parseInt(dlgId));
			
			 Integer tcCorrId=JainTcapProviderImpl.getImpl().getTCCorrelationId(dialogueId);
				
				if(tcCorrId==null){
					ts = JainTcapProviderImpl.getImpl().getTcapSession(dialogueId);
				}else{
					ts =JainTcapProviderImpl.getImpl().getTcapSession(tcCorrId.intValue());
				}
		}

		//logger.error("sessionExpired dlgId::"+dlgId+" ts::"+ts);
		//again applied dlg ID check becuasethis behavior is only for tcap calls appsession
		if (dlgId != null && ts == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Retuning as TS no longer exists for appsession@@@" + ev.getApplicationSession().getId());
			}
			return;
		}
		
		synchronized (lockObj) {
			if (sasMap.get(ev.getApplicationSession().getId()) != null){
				if (logger.isDebugEnabled()) {
					logger.debug("Extending expiry timer for App session@@@" + ev.getApplicationSession().getId());
				}
				ev.getApplicationSession().setExpires(5);
			}
		}
	}

	@Override
	public void sessionWillPassivate(SipApplicationSessionEvent paramSipApplicationSessionEvent) {
		if (logger.isDebugEnabled()) {
			logger.debug("App sessionWillPassivate: "
							+ paramSipApplicationSessionEvent.getApplicationSession().getId());
		}

	}

	@Override
	public void sessionDidActivate(SipApplicationSessionEvent paramSipApplicationSessionEvent) {
		if (logger.isDebugEnabled()) {
			logger.debug("App sessionDidActivate: "
							+ paramSipApplicationSessionEvent.getApplicationSession().getId());
		}
		TcapSession ts = null;
		SccpUserAddress sua = null;
		SignalingPointCode spc = null;
		SipApplicationSession appsession = paramSipApplicationSessionEvent.getApplicationSession();
		String dlgId = (String) appsession.getAttribute(JainTcapProviderImpl.DIALOG_ID);

		if (dlgId != null ) {
			if (logger.isDebugEnabled()) {
				logger.debug(dlgId + " App sessionDidActivate: appsesion with dlg id");
			}
	//		ts = JainTcapProviderImpl.getImpl().getTcapSession(Integer.parseInt(dlgId));
			
			int dialogueId=Integer.parseInt(dlgId);
					
					 Integer tcCorrId=JainTcapProviderImpl.getImpl().getTCCorrelationId(dialogueId);
						
						if(tcCorrId==null){
							ts = JainTcapProviderImpl.getImpl().getTcapSession(dialogueId);
						}else{
							ts =JainTcapProviderImpl.getImpl().getTcapSession(tcCorrId.intValue());
						}
		}

		if (ts != null) {
			if (logger.isDebugEnabled()) {
				logger.debug(dlgId + " App sessionDidActivate: adding appsession");
			}
			sua = (SccpUserAddress) ts
				.getAttribute(JainTcapProviderImpl.getImpl().SccpUserAddressAttr);
			if (logger.isDebugEnabled()) {
				logger.debug("Inside got sua from ts::" + sua);
			}
		}

		if (sua != null) {
			try {

				spc = sua.getSubSystemAddress().getSignalingPointCode();

			} catch (ParameterNotSetException pnse) {
				logger
					.warn(dlgId + " ParameterNotSetException as spc not present in tcap sesion sua");
			}
		}

		if (spc != null && spcList.contains(spc.toString())) {
			if (logger.isDebugEnabled()) {
				logger.debug(dlgId + " TPC for spc:: ading appsesison");
			}
			addSipApplicationSession(appsession);
		}

	}
	
	private void startConnectionTimer() {
		if (connectionTimer != null) {
			connectionTimer.cancel();
			connectionTimer = null;
		}
		connectionTimer = new Timer();
		 TimerTask task = new HeartbeatTimerTask(this);
		if (timeDiffSinceActive > idleConnectionKeepAliveTimer / 2) {
//			connectionTimer = ((TimerService) sc.getAttribute(SipServlet.TIMER_SERVICE))
//				.createTimer(sas, idleConnectionKeepAliveTimer / 2, false, this);
//			
	        connectionTimer.schedule(task, idleConnectionKeepAliveTimer / 2);
			if (logger.isDebugEnabled()) {
				logger.debug("timeDiffSinceActive: " + timeDiffSinceActive);
			}
		} else {
			 connectionTimer.schedule(task, idleConnectionKeepAliveTimer);
//			connectionTimer = ((TimerService) sc.getAttribute(SipServlet.TIMER_SERVICE))
//				.createTimer(sas, idleConnectionKeepAliveTimer, false, this);
		}
	}

	public StringBuffer debugInfo(StringBuffer buffer, boolean all) {
		buffer = (buffer == null) ? new StringBuffer() : buffer;
		buffer.append("\nIN Gateway id=");
		buffer.append(ingwId);
		buffer.append(", Connected=");
		buffer.append(connected);
		buffer.append(",aliveTime=");
		buffer.append(aliveTime);
		buffer.append(",Pending Appln Sessions Size=");
		buffer.append(sasMap.size());
		return buffer;
	}

	public static StringBuffer printDebugInfo(StringBuffer buffer, boolean all) {
		buffer = (buffer == null) ? new StringBuffer() : buffer;
		for (TcapProviderGateway tpg : tpgList) {
			tpg.debugInfo(buffer, all);
		}
		return buffer;
	}

	public void setConnected(boolean flag) {
		connected = flag;
	}

	class TcapProviderListenerHandShake implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 4277786198873158846L;
		transient private byte[]	baos			= null;
		transient private Object	eventObj		= null;
		transient private boolean	infoHandShake	= false;

		public boolean isInfoHandShake() {
			return infoHandShake;
		}

		public void setInfoHandShake(boolean infoHandShake) {
			this.infoHandShake = infoHandShake;
		}

		public Object getEventObj() {
			return eventObj;
		}

		public void setEventObj(Object eventObj) {
			this.eventObj = eventObj;
		}

		public byte[] getBaos() {
			return baos;
		}

		public void setBaos(byte[] baos) {
			this.baos = baos;
		}

		public void sendINFO(SipApplicationSession appSession) throws UnsupportedEncodingException,
						IOException {
			SipServletRequest ssr = iSession.createRequest(sccpContentInformationMethod);
			if (this.getEventObj() != null) {
				ssr.setAttribute(REQ_ATTR_NAME, this.getEventObj());
				ssr.getApplicationSession().setAttribute(REQ_ATTR_NAME, this.getEventObj());
			}
			try {
				ssr.setContent(this.getBaos(), MimeContentType);
				ssr.send();
			} catch (UnsupportedEncodingException e) {
				logger.error("platform does not support content.", e);
				throw e;
			} catch (IOException e) {
				logger.error("platform does not support content.", e);
				throw e;
			}
		}
	}

}
