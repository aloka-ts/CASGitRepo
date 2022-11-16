/*
 * Created on Aug 17, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.baypackets.ase.container;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EventObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipApplicationSessionActivationListener;
import javax.servlet.sip.SipApplicationSessionAttributeListener;
import javax.servlet.sip.SipApplicationSessionBindingEvent;
import javax.servlet.sip.SipApplicationSessionBindingListener;
import javax.servlet.sip.SipApplicationSessionEvent;

import com.baypackets.ase.util.*;
import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.io.KryoObjectInput;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.baypackets.ase.common.AseEvent;
import com.baypackets.ase.common.AseEventListener;
import com.baypackets.ase.common.AseMessage;
import com.baypackets.ase.common.AseObject;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.container.sip.SipApplicationSessionImpl;
import com.baypackets.ase.deployer.DeployerImpl;
import com.baypackets.ase.dispatcher.Destination;
import com.baypackets.ase.measurement.AseMeasurementUtil;
import com.baypackets.ase.monitor.CallStatistics;
import com.baypackets.ase.monitor.CallStatsHolder;
import com.baypackets.ase.monitor.CallStatsProcessor;
import com.baypackets.ase.monitor.CallStatsHolder.CallState;
import com.baypackets.ase.ocm.OverloadControlManager;
import com.baypackets.ase.ocm.TimeMeasurementRule;
import com.baypackets.ase.replication.ReplicatedMessageHolder;
import com.baypackets.ase.replication.SpecialReplicationActivator;
import com.baypackets.ase.sipconnector.AseSipConnector;
import com.baypackets.ase.sipconnector.AseSipServletMessage;
import com.baypackets.ase.sipconnector.AseSipServletRequest;
import com.baypackets.ase.sipconnector.AseSipServletResponse;
import com.baypackets.ase.sipconnector.AseSipSession;
import com.baypackets.ase.sipconnector.AseSipSessionState;
import com.baypackets.ase.spi.container.AbstractProtocolSession;
import com.baypackets.ase.spi.container.SasApplication;
import com.baypackets.ase.spi.container.SasApplicationSession;
import com.baypackets.ase.spi.container.SasProtocolSession;
import com.baypackets.ase.spi.deployer.DeployerFactory;
import com.baypackets.ase.spi.replication.Replicable;
import com.baypackets.ase.spi.replication.ReplicableMap;
import com.baypackets.ase.spi.replication.Replicables;
import com.baypackets.ase.spi.replication.ReplicationListener;
import com.baypackets.ase.spi.replication.ReplicationSet;


/**
 * @author Ravi
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
@DefaultSerializer(ExternalizableSerializer.class)
public abstract class AseApplicationSession extends AseObject 
	implements SasApplicationSession, AseEventListener, Replicable, ReplicationSet {
	
	public static final String ENCODE_KEY = "sas-sessionid";
	private static final long serialVersionUID = -3814634264647849792L;
	private static Logger logger = Logger.getLogger(AseApplicationSession.class);
	private static StringManager _strings = StringManager.getInstance(AseObject.class.getPackage());
	private static OverloadControlManager ocmManager = (OverloadControlManager)Registry.lookup(Constants.NAME_OC_MANAGER);
	private static int ocmId = ocmManager.getParameterId(OverloadControlManager.APP_SESSION_COUNT);
	
	private static final String ATTRIBUTE_MAP = "APP_SESSION_ATTRIBUTE_MAP".intern();
	private static final String MESSAGE_MAP = "MESSAGE_MAP".intern();
	
	private static ReplicationUtilityListener tcapAppListener;
	
	private static AseSipConnector m_sipConnector = (AseSipConnector) Registry.lookup("SIP.Connector");
	private static String lastOctet_fip = null;
	
	private String id = null;
	private long createdTime;
	private long lastAccessedTime;
  // Used to store the Extented Expiry Time Duration
	private int timerExpiry = 0;
	private int timeout = Constants.DEFAULT_SESSION_TIMEOUT;
	private boolean isInAppSessionMap = false;
	protected ArrayList sessions = new ArrayList();
	
	protected String contextName = null;
	protected AseContext ctx = null;
	protected AseIc ic = null;
	
	protected Replicables replicables = new Replicables();

	private boolean   m_isReadyForReplication = true;
	private boolean   m_isModified = true;
	private boolean	  m_isNew = true;
	private boolean   m_initialPriorityStatus = false;
	private boolean   m_priorityStatus = false; 
	
	private int nSession = 0;

	private int m_MessageId;
	private ReplicableMap message_map;
	// Map of ArrayList of timestamps. Protocol session's index is the map key 
	// and message's index is ArrayList's index.
	private Map timestamps;
	private static final String INVITE = "INVITE";
	private static final String SUBSCRIBE = "SUBSCRIBE";
	
	private CallStatistics callStatistics;
	
	//BpInd 17365
  	// Need to replicate Destination Info on Standby SAS
	private Destination m_destination = new Destination();
	

	//map storing callid's with ACK stored
	Set<String> callIdSet = new HashSet<String>();
	
	private AtomicInteger activatedSipSessionCnt = new AtomicInteger(0);
	
	private static String hostNameForCDR = "127.0.0.1";
	
	static
	{
		int index = m_sipConnector.getIPAddress().lastIndexOf('.');
		lastOctet_fip = m_sipConnector.getIPAddress().substring(index+1);
		lastOctet_fip += "$";
		if (logger.isDebugEnabled()) {
			logger.debug("AseApplicationSession: lastOctet_fip="+lastOctet_fip);
		}
		try {
			hostNameForCDR = InetAddress.getLocalHost().getHostAddress();
			if(logger.isDebugEnabled()){
				logger.debug("Setting the hostname for CDR as: " + hostNameForCDR);
			}
		} catch (UnknownHostException uhe){
			logger.error("Received unknown host exception while getting local host address.. Setting loopback");
		}
	}

	protected int timerValue = 0;
	private boolean mFirstReplicationCompleted=false;

	/**
	 * Constructor for the AseApplication session.
	 * @param ctx - the Application context
	 */
	public AseApplicationSession(AseContext ctx) {
		this.createdTime = System.currentTimeMillis();
		this.lastAccessedTime = System.currentTimeMillis();
    	this.timerExpiry = 0;
		//this.id = lastOctet_fip + UIDGenerator.getInstance().get128BitUuid();
		this.id = lastOctet_fip + TimeBasedUUIDGenerator.getUUID();
		
		//logger.debug("AseApplicationSession: SSSid="+this.id);
		this.ctx = ctx;
		this.contextName = ctx.getName();
		this.setTimeout(ctx.getAppSessionTimeout());
		
		//Cretate the attribute map.
		this.attributes = new ReplicableMap(ATTRIBUTE_MAP, true);
		this.setReplicable((Replicable)this.attributes);

		// In the case of replication, timerValue will be reassigned a value
		// at activation time
		this.timerValue = this.getTimeout()*60;
		
		if (EvaluationVersion.FLAG) {
			if (AseMeasurementUtil.counterActiveAppSessions.getCount() >= Constants.EVAL_VERSION_MAX_APP_SESSION) {
				logger.error("SAS evaluation version active app session limit exceeded.");
				throw new IllegalStateException("Max application session limit exceeded");
			}
		}

		//Increment the counter for this appsession.
		AseMeasurementUtil.counterActiveAppSessions.increment();
		AseMeasurementUtil.counterTotalAppSessions.increment();
		AseMeasurementUtil.thresholdAppSession.increment();

		//Increment for the overload control. 
		//This is moved to place from where this constructor is called
		// ocmManager.increase(ocmId);
		
		// create the Hashmap for storing Messages
		message_map= new ReplicableMap(MESSAGE_MAP,false);
		this.setReplicable(this.message_map);
		
		if(CallStatsProcessor.getInstance().isTrafficMonitoringEnabled()){
			callStatistics = new CallStatistics();
		}
	}
	

	/**
	 * JSR 289.42
	 * Constructor for the AseApplication session.Session id will be same as sessionId passed
	 * @param ctx - the Application context
	 */
	public AseApplicationSession(AseContext ctx,String sessionId) {
		this.createdTime = System.currentTimeMillis();
		this.lastAccessedTime = System.currentTimeMillis();
    this.timerExpiry = 0;
		this.id = sessionId;
		
		//logger.debug("AseApplicationSession: SSSid="+this.id);
		this.ctx = ctx;
		this.contextName = ctx.getName();
		this.setTimeout(ctx.getAppSessionTimeout());
		
		//Cretate the attribute map.
		this.attributes = new ReplicableMap(ATTRIBUTE_MAP, true);
		this.setReplicable((Replicable)this.attributes);

		// In the case of replication, timerValue will be reassigned a value
		// at activation time
		this.timerValue = this.getTimeout()*60;
		
		if (EvaluationVersion.FLAG) {
			if (AseMeasurementUtil.counterActiveAppSessions.getCount() >= Constants.EVAL_VERSION_MAX_APP_SESSION) {
				logger.error("SAS evaluation version active app session limit exceeded.");
				throw new IllegalStateException("Max application session limit exceeded");
			}
		}

		//Increment the counter for this appsession.
		AseMeasurementUtil.counterActiveAppSessions.increment();
		AseMeasurementUtil.counterTotalAppSessions.increment();
		AseMeasurementUtil.thresholdAppSession.increment();

		//Increment for the overload control. 
		//This is moved to place from where this constructor is called
		// ocmManager.increase(ocmId);
		
		// create the Hashmap for storing Messages
		message_map= new ReplicableMap(MESSAGE_MAP,false);
		this.setReplicable(this.message_map);

		if(CallStatsProcessor.getInstance().isTrafficMonitoringEnabled()){
			callStatistics = new CallStatistics();
		}
	}


	public boolean getInitialPriorityStatus() {
		return this.m_initialPriorityStatus;
	}

	public void setInitialPriorityStatus(boolean b) {
		this.m_initialPriorityStatus = b;
	}
	
	public AseApplicationSession() {
	}

	public synchronized int generateMessageId() {
		return ++this.m_MessageId;
	}


	public String getApplicationName() {
		return this.ctx.getObjectName();
	}

	public void addSipServletMessage(int key, Object value) {
		if(logger.isDebugEnabled()) {
			logger.debug("In addSipServletMessage msg-id = " + key);	
		}
		if(value instanceof AseSipServletRequest){
			AseSipServletRequest req = ((AseSipServletRequest)value);
			if ( req.getMethod().equals(INVITE) && 
							!req.isInitial()){
				
				if (logger.isDebugEnabled()) {
					logger
						.debug("Non initial INVITE transaction; Not adding to MAP; msg-id=" + key);
				}
				return;
			}else if(!req.getMethod().equals(INVITE) && !req.getMethod().equals(SUBSCRIBE)){
				//This else is added to not to add non-invite requests
				if (logger.isDebugEnabled()) {
					logger
						.debug("Non INVITE/SUBSCRIBE transaction; Not adding to MAP; msg-id=" + key);
				}
				return;
			}else if ((req.getMethod().equals(INVITE) || req.getMethod().equals(SUBSCRIBE)) && req.isInitial()){
				if (logger.isDebugEnabled()) {
					logger.debug("Initial INVITE/SUBSCRIBE request, so adding it and also storing the message id " + key);
				}
				((AseSipSession)req.getSession()).setInitialRequestMessageId(key);
			}
			
		}else if(value instanceof AseSipServletResponse){
			AseSipServletResponse resp = ((AseSipServletResponse)value);
			if( (resp.getMethod().equals(INVITE)||resp.getMethod().equals(SUBSCRIBE)) && 
					(resp.getRequest() == null || !resp.getRequest().isInitial() ) 
					&& resp.getStatus() != 200){

				if (logger.isDebugEnabled()) {
					logger.debug("Ignore responses; msg-id="			
									+ key);
				}
				return;					
			}else if(!resp.getMethod().equals(INVITE) && !resp.getMethod().equals(SUBSCRIBE)){
				if (logger.isDebugEnabled()) {
					logger.debug("Ignore NON Invite/Subscribe responses; msg-id="			
									+ key);
				}
				return;					
			}
		}//end if request
        this.message_map.put(new Integer(key), value);
    }

	public AseSipServletMessage getSipServletMessage(int messId) {
		if(logger.isDebugEnabled()) {
			logger.debug("In getSipServletMessage msg-id = " + messId);	
		}
        return (AseSipServletMessage)this.message_map.get(new Integer(messId));
    }

    public String getReplicableId() {
        return this.getAppSessionId();
    }
  
    public void setReplicableId(String id){
		this.setId(id);
    }
    
    public boolean isReadyForReplication() {
        return m_isReadyForReplication;
    }

    public void setReadyForReplication(boolean ready) {
        m_isReadyForReplication = ready;
    }

   public boolean isModified(){
      return this.m_isModified || this.replicables.isModified();
   }

   public void setModified(boolean reqd){
      this.m_isModified = reqd;
   }
   
   public boolean isNew(){
      return this.m_isNew;
   }
   
   public void replicationCompleted(){
	   replicationCompleted(false);
   }
   
   public void replicationCompleted(boolean noReplication){
		if (!noReplication) {
			this.m_isModified = false;
			this.m_isNew = false;
		}
   	  this.replicables.replicationCompleted(noReplication);
   }

   public void readIncremental(ObjectInput in) throws IOException, ClassNotFoundException {
		if(logger.isDebugEnabled()) {
        	logger.debug("Entering AseApplicationSession readIncremental() for context ");
       	}
	   this.state = in.readInt();
	   //saneja done to support replication of updated timeout in FT
	   this.timeout = in.readInt();
	   //]
	   this.timerExpiry = in.readInt();
	   this.lastAccessedTime = in.readLong();
       m_priorityStatus = in.readBoolean();
           
//	   if (in instanceof AseObjectInputStream) {
//			((AseObjectInputStream)in).setClassLoader(this.ctx.getClassLoader());
//		}
//		
//	   this.replicables.readIncremental(in);
		try {
			//Restore the Attributes MAP.
			this.attributes = (ReplicableMap) this.getReplicable(ATTRIBUTE_MAP);
			this.message_map = (ReplicableMap) this.getReplicable(MESSAGE_MAP);
			this.m_MessageId = in.readInt();
			this.contextName = (String)(in.readObject());
			
			if(logger.isDebugEnabled()) {
	        	logger.debug("AseApplicationSession readIncremental() contextName is " +contextName);
	       	}
			//AseHost tmpHost = null;
			// Now populate the AseCOntext object
			if (contextName!=null) {
				AseEngine engine = (AseEngine)Registry.lookup(Constants.NAME_ENGINE);
				AseHost tmpHost = (AseHost)(engine.findChild(Constants.NAME_HOST));
				this.ctx = (AseContext)(tmpHost.findChild(contextName));

				if (logger.isDebugEnabled()) {
					logger.debug("AseContext set for the AppSession Id [" + id + "]" +" contextName "+contextName);
				}
			}
			
			if(ctx == null) {
				logger.error(" readIncremental() No matching Context found: returning");
				return;
			}
			
			//moved this down as context needs to be updated first then we need to class loader for service chining case e.e.g
			  if (in instanceof AseObjectInputStream) {
					((AseObjectInputStream)in).setClassLoader(this.ctx.getClassLoader());
				}
				
			   this.replicables.readIncremental(in);
			
			   super.readExternal(in);
		}
		catch (Exception e) {
        	logger.error("Exception in readIncremental()" + e.toString(),e);
		}
		
		if (in instanceof AseObjectInputStream) {
			((AseObjectInputStream)in).setClassLoader(null);
		}
		
		if(logger.isDebugEnabled()) {
        	logger.debug("Leaving AseApplicationSession readIncremental()");
       	}
   }

   public void writeIncremental(ObjectOutput out, int replicationType) throws IOException {
		if(logger.isDebugEnabled()) {
        		logger.debug("Entering AseApplicationSession writeIncremental() For context "+contextName);
		}

		this.storeMessageAttr();
        out.writeInt(this.state);
        
        //saneja done to support update of timeout in FT
        out.writeInt(timeout);
        //]
        out.writeInt(this.timerExpiry);
        out.writeLong(lastAccessedTime);
		out.writeBoolean(m_priorityStatus);
	//	this.replicables.writeIncremental(out, replicationType);
		out.writeInt(this.m_MessageId);
		out.writeObject(contextName);
		
		this.replicables.writeIncremental(out, replicationType);
		try {
			super.writeExternal(out);
		}
		catch (Exception e) {
        	logger.error("Exception in writeIncremental()" + e.toString(),e);
		}

		if (logger.isDebugEnabled()) {
			logger.debug("SIZE of message Map in writeInc"+ message_map.size());	
        	logger.debug("Leaving AseApplicationSession writeIncremental()");
        }
   }

	public void partialActivate(ReplicationSet parent) {
		if (logger.isDebugEnabled()) {
			logger.debug("Entering AseApplicationSession partialActivate(). for context "+ contextName);
		}
		if(parent instanceof AseIc){
  		((AseIc)parent).addApplicationSession(this);
		}
		
		String priortySess = (String)this.getAttribute(Constants.PRIORTY_SESSION);
		if(priortySess!=null){
			if (logger.isDebugEnabled()) {
				logger.debug("Adding priorty session");
			}
			SpecialReplicationActivator.getInstance().addReplicationContext(ic);
		}
		
		//to synch appsession counters and avoid memory leak in case of FT
		String dlgId = (String)this.getAttribute(Constants.DIALOGUE_ID);
		if(dlgId != null){
			if(logger.isDebugEnabled()) {
				logger.debug("dlgId is:" + dlgId);
			}
			//changed map type from string ase application session to string sip app session to make it usable in service.
			ConcurrentHashMap< String, SipApplicationSession> map = 
				(ConcurrentHashMap<String, SipApplicationSession>)this.ctx.getAttribute("Tcap_DlgID_AppSessionMap");
			if(logger.isDebugEnabled()) {
					logger.debug("Dialog id not found in map");
			}
			map.put(dlgId,this);
			
			if(logger.isDebugEnabled()) {
				logger.debug("Add dlgId to map:" + dlgId);	
			}
		}

		
		if(isInAppSessionMap == true){
			this.getContext().addApplicationSession(this);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Leaving AseApplicationSession partialActivate().");
		}
	}

	public void activate(ReplicationSet parent) {
		if (logger.isDebugEnabled()) {
			logger.debug("Entering AseApplicationSession activate() For context name -->"+contextName);
		}
		
		if(contextName.startsWith("tcap-provider")){
			if(!SpecialReplicationActivator.getInstance().isActivated()){
				if (logger.isDebugEnabled()) {
					logger.debug("special contexts are not yet active activate them as rolechanged is called before partialactivate ");
				}
			SpecialReplicationActivator.getInstance().activateContexts();
			}
		}

        if(parent != null) {
				
        	Iterator iter = this.getSessions("SIP");
        	while (iter.hasNext()){
        		AseSipSession session = (AseSipSession) iter.next();
        		session.getLocalCSeqNumber(50);
        	}
        	//Activate the Child objects...
        	this.replicables.activate(this);

			long currentTime = System.currentTimeMillis();
			int idleTimeSecs = (int) ((currentTime - this.lastAccessedTime)/1000);
			if(idleTimeSecs<0) {
				this.setState(Constants.STATE_INVALIDATING);
			}
			this.timerValue = idleTimeSecs;
			AseTimerClock.getInstance().add(this);
        	
        	//Increment the counter for this appsession.
        	AseMeasurementUtil.counterActiveAppSessions.increment();
        	AseMeasurementUtil.counterTotalAppSessions.increment();
        	AseMeasurementUtil.thresholdAppSession.increment();
        	
        	//do not increment inPrgrs ctr for HB APpsession
        	String hbSession = (String) this.getAttribute("IN_PRGRS_CTR_DISABLE");

        	//Increment for the overload control.
			if (hbSession == null) {
				if (this.getInitialPriorityStatus()) {
					ocmManager.increaseNSEP(ocmId, true);
				} else {
					ocmManager.increase(ocmId, true);
				}
			} else {
				if (this.getInitialPriorityStatus()) {
					ocmManager.increaseNSEP(ocmId, false);
				} else {
					ocmManager.increase(ocmId, false);
				}
			}
        	
			try {
				Iterator itor = message_map.keySet().iterator();
				while(itor.hasNext()) {
					Object value = message_map.get(itor.next());
					if(value instanceof AseSipServletMessage) {
                        try {
                            if (logger.isDebugEnabled())
                                  logger.debug("Going to activate  Messages "+value);
                            ((AseSipServletMessage)value).activate();
                        } catch(Exception exp) {
                            logger.error(exp.getMessage(),exp);
                        }
                    }
                }

			} catch (Exception ex) {
				logger.error(ex.getMessage(), ex);
			}	
			
			try {
				Object[] ic = attributes.keySet().toArray();
				for	(int i=0; i<ic.length; i++) {
					Object value = attributes.get(ic[i]);
					if(value instanceof ReplicatedMessageHolder) {
						ReplicatedMessageHolder holder = (ReplicatedMessageHolder)value;
						holder.resolve();
						//attributes.put(ic[i],holder.resolve());
					}
				}
			} catch (Exception ex) {
				logger.error(ex.getMessage(), ex);
			}	
			
			if (logger.isDebugEnabled()) {
				logger.debug("APPSession-Correlation replication-Checking corrID");
			}
			//saneja@bug 7084 correlation replication[
			Integer correlationId=(Integer) this.getAttribute("Correlation-ID");
			if(correlationId!=null){
				if (logger.isDebugEnabled()) {
					logger.debug("APPSession-Correlation replication-ADDING Application SESSION FOR CORRELATION ID::["+correlationId+"]");
				}
				AseContext aseContext=this.getContext();
				if (logger.isDebugEnabled()) {
					logger.debug("APPSession-Correlation replication-Found APpContext::["+aseContext+"]");
				}
				Map<Integer,Object> corrMap=(Map) aseContext.getAttribute("Correlation-Map");
				corrMap.put(correlationId, this);
				if (logger.isDebugEnabled()) {
					logger.debug("APPSession-Correlation replication-Added tCApsession in correlation map");
				}
			}			
			//]saneja@bug 7084 correlation replication)
			
			// Notify SipApplicationSessionActivationListeners of this session's activation
	        genActivationEvent();
	        
		} else {
            logger.error("activate() failed as IC is NULL");
        }

        if (logger.isDebugEnabled()) {
        	logger.debug("Leaving AseApplicationSession activate()");
        }
    }

	private void storeMessageAttr() {
		try {
			Iterator ic = attributes.keySet().iterator();
			while (ic.hasNext()) {
				Object value = attributes.get(ic.next());
				if(value instanceof ReplicatedMessageHolder) {
					//((AseSipServletMessage)value).assignMessageId();
					AseSipServletMessage msg = ((ReplicatedMessageHolder)value).getMessage();
					if(msg != null) {
						msg.storeMessageAttr();
					} else {
						logger.error("Due to replication data reduction, message not available.");
					}
				}
			}
			ic = this.sessions.iterator();
			while(ic.hasNext()) {
				// BPInd19894 [
				Object obj=ic.next();
				if(obj instanceof AseProtocolSession){
					((AseProtocolSession)obj).storeMessageAttr();
				} // ]
			}
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
																																																							

   	public void writeExternal(ObjectOutput out) throws IOException {
		if(logger.isDebugEnabled()) {
        	logger.debug("Entering AseApplicationSession writeExternal() For" + contextName);
       	}
		
		// Notify SipApplicationSessionActivationListeners before serializing
		
		if (logger.isDebugEnabled()){
			logger.debug("setFirstReplicationCompleted(true) ");
		}
		setFirstReplicationCompleted(true);

		genPassivationEvent();
    	
		this.storeMessageAttr();

		try {
			if (logger.isDebugEnabled()) {
				logger.debug("SIZE of Mesage Map in writeExternal="+message_map.size());
			}
			out.writeObject(id);
			out.writeLong(createdTime);
			out.writeInt(timeout);
			out.writeBoolean(isInAppSessionMap);
			out.writeBoolean(m_initialPriorityStatus);
			out.writeBoolean(m_priorityStatus);
			out.writeObject(contextName);
			out.writeObject(m_destination);
			out.writeInt(this.state);
			out.writeInt(this.timerExpiry);
			out.writeLong(this.lastAccessedTime);
			out.writeObject(this.replicables);
			//This is replicated to avoid the scenario of having same message id for 
			//two messages after FT, especially after the change of recreating ORIG_REQUEST
			//based on the intial request message id
			out.writeInt(this.m_MessageId);
			
			super.writeExternal(out);
		}
		catch (Exception e) {
        	logger.error("Exception in writeObject()" + e.toString(),e);
		}

		
		if (logger.isDebugEnabled()) {
        	logger.debug("Leaving AseApplicationSession writeExternal()");
        }
	}

	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException{
		if (logger.isDebugEnabled()) {
			logger.debug("Entering AseApplicationSession readExternal() for context "+contextName);
		}

		id = (String)(in.readObject());
		createdTime = in.readLong();
		timeout = in.readInt();
		isInAppSessionMap = in.readBoolean();
		m_initialPriorityStatus = in.readBoolean();
		m_priorityStatus = in.readBoolean();
		contextName = (String)(in.readObject());
		m_destination = (Destination)(in.readObject());
		
		AseHost tmpHost = null;
		// Now populate the AseCOntext object
		if (null == ctx) {
			AseEngine engine = (AseEngine)Registry.lookup(Constants.NAME_ENGINE);
			tmpHost = (AseHost)(engine.findChild(Constants.NAME_HOST));
			this.ctx = (AseContext)(tmpHost.findChild(contextName));

			if (logger.isDebugEnabled()) {
				logger.debug("AseContext set for the AppSession Id [" + id + "]" +" contextName "+ contextName);
			}
		}
		if(ctx == null) {
			logger.error(" readExternal() No matching Context found: returning");
			return;
		}
		
		this.state = in.readInt();
		this.timerExpiry = in.readInt();
		this.lastAccessedTime = in.readLong();

		if (in instanceof AseObjectInputStream) {
			((AseObjectInputStream)in).setClassLoader(this.ctx.getClassLoader());
			this.replicables = (Replicables)in.readObject();
			((AseObjectInputStream)in).setClassLoader(null);
		} else if(in instanceof KryoObjectInput) {
			ClassLoader previousCl = ((KryoObjectInput)in).getKryoObject().getClassLoader();
			((KryoObjectInput)in).getKryoObject().setClassLoader(this.ctx.getClassLoader());
			this.replicables = (Replicables)in.readObject();
			((KryoObjectInput)in).getKryoObject().setClassLoader(previousCl);
		} else{
			this.replicables = (Replicables)in.readObject();
		}	
		this.m_MessageId = in.readInt();
		super.readExternal(in);

		//Restore the Attributes MAP.
		this.attributes = (ReplicableMap) this.getReplicable(ATTRIBUTE_MAP);
		this.message_map = (ReplicableMap) this.getReplicable(MESSAGE_MAP);
		if(logger.isDebugEnabled()) {
			logger.debug("SIZE of Mesage Map in readExternal="+message_map.size());
			logger.debug("Application Session Replicables :::" + this.replicables);
			logger.debug("Application Session attribute map :::" + this.attributes); 
			logger.debug("Leaving AseApplicationSession readExternal()");
		}
	}

	/**
	 * 
	 * @return iterator of all the protocol sessions
	 */
	public Iterator getSessions() {
		this.checkValid();
		return this.sessions.iterator();
	}

	/**
	 * 
	 * @param protocol - the name of the protocol
	 * @return iterator of all the protocol session that implement the specified protocol
	 */
	public Iterator getSessions(String protocol) {
		this.checkValid();
		return new ProtocolSessionIterator(protocol, this.sessions);
	}
	
	public AseProtocolSession getSession(String id){
		if(id == null){
			return null;
		}
		AseProtocolSession session = null;
		for(int i=0; i<this.sessions.size();i++){
			AseProtocolSession temp = (AseProtocolSession)this.sessions.get(i);
			if(temp != null && temp.getId().equals(id)){
				session = temp;
				break;
			}
		}
		return session;
	}
	
	public void incrementActivatedSipSessions(){
		activatedSipSessionCnt.incrementAndGet();
	}
	
	public void decrementActivatedSipSessions(){
		activatedSipSessionCnt.decrementAndGet();
	}

	/**
	 * @param name - The name of the attribute whose value is to be returned.
	 * @return The value of the specified attribute or NULL if no value was set.
	 */
	public Object getAttribute(String name) {
		if (Constants.ATTRIBUTE_SERVLET_CONTEXT.equals(name)) {
			return this.getContext().getServletContext();
		}
		
		//BUG6765 (LIVE SBB UPGRADE)
		if (Constants.SBB_FACTORY.equals(name)) {
			return ((SipApplicationSessionImpl)this).getSbbFactory();
		}
		
	
		// BPUsa07541 : [
		if (Constants.CORRELATION_ID.equals(name)) {
			Object value = super.getAttribute(name,false);
			
			if (value == null) {
				if (logger.isDebugEnabled()) {
					logger.debug("getAttribute(): Generating correlation ID for all CDR objects associated with this app session...");
				}

				try {
					//super.setAttribute(name, value = System.currentTimeMillis() + InetAddress.getLocalHost().getHostAddress() + Thread.currentThread().getName());
					super.setAttribute(name, value = System.currentTimeMillis() + hostNameForCDR + Thread.currentThread().getName());
				} catch (Exception e) {
					String msg = "Error occurred while generating correlation ID for CDR objects.";
					logger.error(msg, e);
					throw new RuntimeException(msg);
				}
			}
			return value;
		}  //]
	
		Object value = super.getAttribute(name);

		if(value instanceof AppSessionAttributeHolder) {
			String id = ((AppSessionAttributeHolder)value).id;
			AseHost host = (AseHost)Registry.lookup(Constants.NAME_HOST);
			if(host == null) {
				logger.error("Not able to get the host reference");
				return null;
			}
			AseApplicationSession appSess = (AseApplicationSession)host.getApplicationSession(id);
			value = appSess;
		}

        if (value instanceof AbstractProtocolSession.SessionAttributeHolder) {
            String id = ((AbstractProtocolSession.SessionAttributeHolder)value).id;
            value = this.getSession(id);
        } 

		
        if (value instanceof ReplicatedMessageHolder) {
            value = ((ReplicatedMessageHolder)value).getMessage();
        }
		
		return value;
	}

	/**
	 * @param name - The name of the attribute to set.
	 * @param value - The value to set for the specified attribute.
	 */
	public void setAttribute(String name, Object value) {
		if (logger.isDebugEnabled()) {
			logger.debug("Entering setAttribute with arg(name, value) = "+name+"---value"+value);
		}
		
		if (Constants.ATTRIBUTE_SERVLET_CONTEXT.equals(name)) {
			throw new IllegalArgumentException("App session attribute with name, " + Constants.ATTRIBUTE_SERVLET_CONTEXT + " is reserved.  It's value cannot be set!");
		}

		// BPUsa07541 : [
		if (Constants.CORRELATION_ID.equals(name)) {
			throw new IllegalArgumentException("App session attribute with name, " + Constants.CORRELATION_ID + " is reserved.  It's value cannot be set!");
		}
		// ]
		
		if(value instanceof AseApplicationSession) {
			if (logger.isDebugEnabled()) {
				logger.debug("value is instance of AseApplicationSession");
			}
			AppSessionAttributeHolder holder = new AppSessionAttributeHolder();
			holder.id = ((AseApplicationSession)value).getId(); 
			value = holder;
		}
		
		if(value instanceof AbstractProtocolSession){
			if (logger.isDebugEnabled()) {
				logger.debug("value is instance of AseProtocolSession");
			}
            AbstractProtocolSession.SessionAttributeHolder holder = new AbstractProtocolSession.SessionAttributeHolder();
            holder.id = ((AbstractProtocolSession)value).getId();
            value = holder;
        }	

		if(value instanceof AseSipServletMessage )  {
			value = new ReplicatedMessageHolder((AseSipServletMessage)value);
        }
		
		Object preValue = super.getAttribute(name); 
		super.setAttribute(name, value);
		
		 // Generate appropriate events
		  if(null != preValue) {
				genAttributeReplacedEvent(name);
		  		genValueUnboundEvent(name, preValue);
		  } else {
				genAttributeAddedEvent(name);
		  }
		  genValueBoundEvent(name, value);
	}

	 /**
   * Remove an attribute from the attributes map.
   * Generate attributeRemovedEvent and valueUnboundEvent
   */
	 public void removeAttribute(String name) {
		  if (logger.isDebugEnabled()) {
			  logger.debug("Entering AseApplicationSession.removeAttribute. " +
								 "Attribute Name = [" + name + "]" + id);
		  }
		  Object value = super.getAttribute(name); 
		  super.removeAttribute(name);
		  // generate events
		  if (null != value) {
				genAttributeRemovedEvent(name);
				genValueUnboundEvent(name, value);
		  }
		  if (logger.isDebugEnabled()) {
			  logger.debug("Leaving AseApplicationSession.removeAttribute. " +
								 "Attribute Name = [" + name + "]" + id);
		  }
	 }
		
		  
	

	
	 
	/**
	 * 
	 * @return list of all attribute names
	 */	
	public Iterator getAttributeNames(){
		this.checkValid();
		return this.getAttributeNamesIterator();
	}
	
	/**
	 * Invalidates the application session
 	 */
	public void invalidate(){
		if(logger.isDebugEnabled()) {
			logger.debug("Entering AseApplicationSession invalidate "+this);
		}

		boolean changeLock = AseThreadData.setIcLock(this.ic);

		try {
			if(this.getState() == Constants.STATE_INVALID || 
				this.getState() == Constants.STATE_DESTROYED){
				logger.error("object is not in valid state: Returning");
				return;
			}
			
			String priortySess = (String)this.getAttribute(Constants.PRIORTY_SESSION);
			if(priortySess!=null){
				if (logger.isDebugEnabled()) {
					logger.debug("removing priorty session");
				}
				SpecialReplicationActivator.getInstance().removeReplicationContext(ic);
			}
			
			//@nitin for sbtm
			String dlgId = (String)this.getAttribute(Constants.DIALOGUE_ID);
			if(dlgId != null){
				if(logger.isDebugEnabled()) {
					logger.debug("dlgId is:" + dlgId);
				}
				//changed map type from string aseapplication session to string sipappsession to make it usable in service.
				ConcurrentHashMap< String, SipApplicationSession> map = 
					(ConcurrentHashMap<String, SipApplicationSession>)this.ctx.getAttribute("Tcap_DlgID_AppSessionMap");
				map.remove(dlgId);
				if(logger.isDebugEnabled()) {
					logger.debug("removed dlgId from map:" + dlgId);	
				}
			}
			
			String counterDecremented = (String) this.getAttribute(Constants.CALL_CNTR_DEC);
			// Set timerValue used by timer to -2.
			this.timerValue = -2;
		
			//Checks the state of the call for calculating call stats before invalidating
			if(CallStatsProcessor.getInstance().isTrafficMonitoringEnabled()){
				checkCallState(this);
			}
			//Iterate through all the protocol session and invalidate them
			SasProtocolSession[] tmp = new SasProtocolSession[this.sessions.size()];
			tmp = (SasProtocolSession[]) this.sessions.toArray(tmp);
			boolean isConfirmed = true;
			for(int i=0; i<tmp.length;i++){
				if(tmp[i] == null) {
					continue;
				}


				if( tmp[i] instanceof AseSipSession) {
					if ( ((AseSipSession)tmp[i]).getSessionState() == AseSipSessionState.STATE_CONFIRMED) {
						if(isConfirmed == true) {
							if(logger.isDebugEnabled()) {
								logger.debug("AppSession contains Active dialogs");
								logger.debug("The ApplicationSession is = "+ this.toString());
							}
							isConfirmed = false;
						}
						logger.info("Active SipSession is = "+ ((AseSipSession)tmp[i]).toString());
					}
				}
				tmp[i].invalidate();
	        }
			
			Iterator iter = super.getAttributeNamesIterator();
			while (iter.hasNext()) {
				String attrName = (String)(iter.next());
				genAttributeRemovedEvent(attrName);
				genValueUnboundEvent(attrName, super.getAttribute(attrName));
			
			}
			//Set the state as INVALID.
			this.setState(Constants.STATE_INVALID);
		
			//Notify the IC that, the appsession is invalidated.
			this.ic.appSessionInvalidated(this);

			// Remove ourselves from the AseContext map  and host level map
			// (if it was added to appsessionMap in AseHost)
			//ctx.removeApplicationSession(this); Moved to cleanup
			
			//If for this app session active call counter and active calls in progress
			//have already been decremented then no need to do it again. 
			if (counterDecremented == null){
				//Decrement for the overload control.
				if(getInitialPriorityStatus()) {
					ocmManager.decreaseNSEP(ocmId);
				} else if(m_destination.getServletName()!=null){ //SBTM decrement only if it is a call appsession not a timer or other one
					ocmManager.decrease(ocmId);	
				}
				AseMeasurementUtil.counterActiveAppSessions.decrement();
			}else{
				if(logger.isDebugEnabled()) {
					logger.debug("Active Call Counter already decremented ");
				}
			}

		} finally {
			if(logger.isDebugEnabled()) {
				logger.debug("Going to release the lock: App Session = "+this);
			}
			AseThreadData.resetIcLock(this.ic, changeLock);
		}
	}


	/**
	 * Does a complete cleanup of the object and marks it as destroyed.
	 */
	public void cleanup(){

		if(logger.isDebugEnabled()) {
			logger.debug("Entering cleanup() of "+this);
		}
		
		// Invalidate if already active
		if((this.ic != null) && this.ic.isActive()) {
			if(this.getState() == Constants.STATE_VALID){
				this.invalidate();
			}
		}
		
		//Clear the protocolsession list..
		SasProtocolSession[] tmp = new SasProtocolSession[this.sessions.size()];
		tmp = (SasProtocolSession[]) this.sessions.toArray(tmp);
		for(int i=0; i<tmp.length;i++){
			if(tmp[i] != null)
				tmp[i].cleanup();
		}
		this.sessions.clear();
		
		
		//invalidated Sip sessions, adjustctr if any is false ley incremented
		for(; activatedSipSessionCnt.intValue()>0;){
			AseMeasurementUtil.counterActiveSIPSessions.decrement();
			activatedSipSessionCnt.decrementAndGet();
		}
		
		//@saneja Dlg Id cleanup moved form invalidate to cleanup to ensure cleanup on FT
		//added all call state check to avoid illegal state  exception if this method is invoked through invalidate();
		//below logic will be invoked on stby SAS as replicator call cleanup without changing state of appsession 
		if (this.getState() != Constants.STATE_INVALID
						&& this.getState() != Constants.STATE_DESTROYED) {
			
			String priortySess = (String)this.getAttribute(Constants.PRIORTY_SESSION);
			if(priortySess!=null){
				if (logger.isDebugEnabled()) {
					logger.debug("removing priorty session2");
				}
				SpecialReplicationActivator.getInstance().removeReplicationContext(ic);
			}
			
			String dlgId = (String) this.getAttribute(Constants.DIALOGUE_ID);

			if (dlgId != null) {
				if (logger.isDebugEnabled()) {
					logger.debug("dlgId is:" + dlgId);
				}
				//changed map type from string aseapplication session to string sipappsession to make it usable in service.
				ConcurrentHashMap<String, SipApplicationSession> map = (ConcurrentHashMap<String, SipApplicationSession>) this.ctx
					.getAttribute("Tcap_DlgID_AppSessionMap");
				map.remove(dlgId);
				if (logger.isDebugEnabled()) {
					logger.debug("removed dlgId from map:" + dlgId);
				}
				//fetch tcap listenere if null
				if(tcapAppListener ==null){
					try {
						Class clazz = Class.forName("com.genband.jain.protocol.ss7.tcap.JainTcapProviderImpl");
						tcapAppListener= (ReplicationUtilityListener) clazz.getMethod("getImpl", null).invoke(clazz, null);
					} catch (Throwable t) {
						logger.error(id+"::"+dlgId+"::Exception Unable to get Tcap App Listener for cleanup",t);
					}
				}
				//calling AppsessionCleaned if null
				if(tcapAppListener!=null){
					if (logger.isDebugEnabled()) {
						logger.debug(id+"::"+dlgId+"::Try cleaning dlgId from map");
					}
					try {
						tcapAppListener.appSessionCleaned(dlgId);
					} catch (Throwable t) {
						logger.error(id+"::"+dlgId+"::Exception unable to process AppsessionCleaned",t);
					}
				}else{
					logger.error(id+"::"+dlgId+"::tcapAppListener is nulll");
				}
			}//@End IF dlg id check


		}//@End IF appsession state check

		// BPInd18810 Moved this from invalidate	
		ctx.removeApplicationSession(this);
        
       	// Undeploy the application that this session belongs to if the
       	// app was upgraded and the app session count has reached zero.
		if (ctx.getAppSessionCount() == 0) {
           	if (logger.isDebugEnabled()) {
               	logger.debug("invalidate(): Undeploying application, " + ctx.getName());
           	}
           	DeployerFactory deployerFactory = (DeployerFactory)
           					Registry.lookup(DeployerFactory.class.getName());
           	try {
           		DeployerImpl deployer = (DeployerImpl)deployerFactory.getDeployer(ctx.getType());
               	deployer.checkExpectedState(ctx.getId(), false);
           	} catch (Exception e) {
               	logger.error("Undeploying application: " + ctx.getName(), e);
           	}
        } 

		//Call the cleanup on super class.
		super.cleanup();
		if(logger.isDebugEnabled()) {
			logger.debug("Exiting cleanup() of "+this);
		}
	}
	
	/**
	 *	Set the last accessed time for this session.
	 *  This method also extends the expiry timer for the new duration 
	 *  @param lastAccessed
	 */
	public void setLastAccessedTime(long lastAccessed){
		this.checkValid();
		this.lastAccessedTime = lastAccessed;
	}

	/**
	 * @return the creation time for this session
	 */
	public long getCreationTime() {
		return this.createdTime;
	}

	/**
	 * @return the id of this session
	 */
	public String getId() {
		if(isInAppSessionMap == false){
			isInAppSessionMap = true;
			AseHost host = ((AseHost)(this.getContext()).getParent());
			host.addApplicationSession(this);
		}

		return this.id;
	}

	public String getAppSessionId() {
		return this.id;
	}
	
	/**
	 * @return the lastAccessed time for this session
	 */
	public long getLastAccessedTime() {
		return this.lastAccessedTime;
	}

	/**
	 * @return the timeout value (in minutes) for this session
	 */
	public int getTimeout() {
		return this.timeout;
	}

	/**
	 * Sets the Id for this session 
	 * @param id
	 */
	protected void setId(String id){
		this.checkValid();
		this.id = id;
		this.setModified(true);
	}
	
	/**
	 * Sets the timeout value for this session
	 * @param timeout
	 */
	public void setTimeout(int timeout){
		this.checkValid();
		this.timeout = timeout;
		this.setModified(true);
	}
	
	/**
	 * Extend the life of the session by the provided time minutes
	 * @param deltaMinutes
	 * @return
	 */
	public int setExpires(int deltaMinutes) {
		//Check the state and the arguments.
		this.checkValid();
		if(deltaMinutes < 1){
			throw new IllegalArgumentException("Value < 1");
		}
		// Change Siddharth
		if (this.ctx.getState() != SasApplication.STATE_ACTIVE) {
			if (logger.isDebugEnabled()) {
				logger
					.debug("Got an external request for extending the lifetime of this session. Ignoring it since the application is not in ACTIVE state.");
			}
			return 0;
		} else {
			timerValue = deltaMinutes * 60;
		}
		timerExpiry += deltaMinutes * 60;
		//return this.resetTimer(deltaMinutes*60, false);
		return deltaMinutes;
	}
	
	/**
	 * Returns the application context for this session
	 * @return
	 */
	public AseContext getContext() {
		return ctx;
	}
	
	/**
	 * Sets the application context for this session
	 * @param ctx
	 */
	public void setContext(AseContext ctx){
		this.ctx = ctx;
		this.contextName=ctx.getName();
		
		if (logger.isDebugEnabled()) {
           	logger.debug("setContext , " + ctx.getName());
       	}
		this.setModified(true);
	}

	/**
	 * Returns the invocation context for this app session
	 * @return
	 */
	public AseIc getIc() {
		return ic;
	}

	/**
	 * Sets the invocation context for this app session
	 * @param ic
	 */
	public void setIc(AseIc ic) {
		this.ic = ic;
		this.setModified(true);
	}
	
	/**
	 * Add a new protocol session with the application session
	 * @param protocolSession
	 */
	public void addProtocolSession(SasProtocolSession protocolSession){
		this.checkValid();
		if(protocolSession != null && this.sessions.indexOf(protocolSession) == -1){
			this.sessions.add(protocolSession);
			protocolSession.setApplicationSession((SipApplicationSession)this, nSession++);

			ic.setReplicable(protocolSession);
			
			this.ctx.addProtocolSession(protocolSession);
		}
	}

	public void removeProtocolSession(SasProtocolSession session){
		if(session == null || !sessions.contains(session))
			return;

		if(logger.isDebugEnabled()){
			logger.debug("Removing the protocol session:" + session);
		}
		ctx.removeProtocolSession(session);
		ic.removeReplicable(session.getReplicableId());
		sessions.remove(session);	
	}
	
	public SasApplication getApplication() {
		return this.ctx;
	}
	
	public ReplicationListener getReplicationListener(){
		return this.ic;
	}
	

	/**
	 * Iterator class that matches the protocol and returns the matching protocol
	 */
	class ProtocolSessionIterator extends AbstractAseIterator{
		
		public ProtocolSessionIterator(String criteria, ArrayList list){
			super(criteria, list);
		}
		
		protected boolean match(Object obj, Object criteria){
			boolean matched = false;
			if(obj != null && obj instanceof SasProtocolSession){
				SasProtocolSession protoSession = (SasProtocolSession) obj;
				matched = protoSession.getProtocol().equalsIgnoreCase(""+criteria);
			}
			return matched;
		}
	}
	
	/**
	 * Enqueues this message into the engine's thread pool
	 * @param message
	 */
	public void enqueMessage(AseMessage message){
		//Enqueue the message into the workers's pool.
		if(logger.isEnabledFor(Level.INFO)){
			logger.info("Enquing a asynchronus message with type :"+message.getMessageType());
		}
		AseEngine engine = (AseEngine)Registry.lookup(Constants.NAME_ENGINE);
		message.setWorkQueue(this.getIc().getWorkQueue());
		engine.handleMessage(message);
	}

	/**
	 * Handles the session expiry by calling the objectExpired method.
	 * This method is invoked in the context of the worker thread
	 * IC lock is taken by SipApplicationSession sub-class.
	 */
	public void handleEvent(EventObject eventObject) {
		AseEvent event = (AseEvent)eventObject;

		int type = event.getType();
		switch (type){
			case Constants.EVENT_APPLICATION_SESSION_EXPIRED:
				if(logger.isInfoEnabled()) {
					logger.info("Got the timer expired event for AppSession :"+this.id);
				}

				this.objectExpired();
			break;

			default:
		}
	}

	public int adjustTimer( int value )
	{

		if(logger.isDebugEnabled()) {
			logger.debug("Entering adjustTimer(): App Session = "+this);
		}

		if(( this.getState() == Constants.STATE_INVALID ) || 
			( this.getState() == Constants.STATE_DESTROYED)) {
			return -2;
		}

/*
		timerValue = -1;

		if(timerValue<=0) {
			long currentTime = System.currentTimeMillis();
			int idleTimeSecs = (int) ((currentTime - this.lastAccessedTime)/1000);
			if( idleTimeSecs < this.timeout*60 ) {
				timerValue += this.timeout*60 - idleTimeSecs;
			}
			
		}
*/

    timerValue = -1;
		long currentTime = System.currentTimeMillis();
		int idleTimeSecs = (int) ((currentTime - this.lastAccessedTime)/1000);
		if( idleTimeSecs < this.timeout*60 ) {
			timerValue += this.timeout*60 - idleTimeSecs;
		}

    if (timerExpiry > 0) {
      timerValue += timerExpiry + this.timeout*60 - idleTimeSecs;
    }

    if (logger.isDebugEnabled()) {
		logger.debug(" Leaving timerValue=" + timerValue);
    }
		if(timerValue <= 0) {
			return -1;
		}

		return timerValue;
	}

	/**
	 * Variables and methods for response time based overload control
	 */	
	public void setTimestamp(int sessionIndex, long timestamp) {
		if (timestamps == null) {
			timestamps = new HashMap();
		}

		Integer key = new Integer(sessionIndex);
		ArrayList sessionTimestamps = null;
		if (timestamps.containsKey(key)) {
			sessionTimestamps = (ArrayList)timestamps.get(key);
		} else {
			sessionTimestamps = new ArrayList();
			timestamps.put(key, sessionTimestamps);
		}
		sessionTimestamps.add(new Long(timestamp));
	}
	
	public Long getTimestamp(int sessionIndex, int msgIndex) {
		if (timestamps == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("No timestamp found for this application session.");
			}
			return null;
		}
		
		ArrayList sessionTimestamps = (ArrayList)timestamps.get(new Integer(sessionIndex));
		if (sessionTimestamps == null || sessionTimestamps.size() <= msgIndex) {
			if (logger.isDebugEnabled()) {
				logger.debug("No timestamp found for session " + sessionIndex +
						" message " + msgIndex);
			}
			return null;
		}
		return (Long)sessionTimestamps.get(msgIndex);
	}
	
	public Double getResponseTimeRatio() {
		double responseTimeRatio = 0;
		boolean found = false;
		ArrayList rules = ctx.getTimeMeasurementRules();
		
		if (rules == null) {
			return null;
		}
		
		// use the first matched rule
		for (int i = 0; i < rules.size(); i++) {
			TimeMeasurementRule rule = (TimeMeasurementRule)rules.get(i);
			TimeMeasurementRule.Rule beginingRule = rule.getBeginingRule();
			TimeMeasurementRule.Rule endingRule = rule.getEndingRule();
			
			// get begining timestamp
			Long beginingTime = getTimestamp(beginingRule.getSessionIndex(), beginingRule.getMsgIndex());
			if (beginingTime == null) continue;
			
			// get ending timestamp
			Long endingTime = getTimestamp(endingRule.getSessionIndex(), endingRule.getMsgIndex());
			if (endingTime == null) continue;
			
			// calculate ratio of current response time and target response time
			double responseTime = endingTime.longValue() - beginingTime.longValue();
			double targetTime = rule.getTargetTime();
			responseTimeRatio = responseTime/targetTime;
			found = true;
			break;
		}
		if (!found) return null;
		return new Double(responseTimeRatio);		
	}
	
	//////////// ReplicableCollection Interface Implementation starts //////////////
	public void clear() {
		this.replicables.clear();
	}

	public Collection getAllReplicables() {
		return this.replicables.getAllReplicables();
	}

	public Replicable getReplicable(String id) {
		return this.replicables.getReplicable(id);
	}

	public void removeReplicable(String id) {
		this.replicables.removeReplicable(id);
	}

	public void setReplicable(Replicable replicable) {
		this.replicables.setReplicable(replicable);
	}
	////////////ReplicableCollection Interface Implementation ends //////////////
	
	/**
	 * toString method
	 */
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("ApplicationSession (");
		buffer.append("id=");
		buffer.append(this.id);
		buffer.append(",appName=");
		buffer.append(this.contextName);
		buffer.append(",state=");
		buffer.append(this.getState());
		buffer.append(",create timestamp=");
		buffer.append(this.createdTime);
		buffer.append(",timeout=");
		buffer.append(this.timeout);
		buffer.append(",timerExpiry=");
		buffer.append(this.timerExpiry);
		buffer.append(",timerValue=");
		buffer.append(this.timerValue);
		Object serviceInfo=attributes.get(Constants.ATTRIBUTE_SERVICE_INFO);
		if(serviceInfo!=null){
			buffer.append(",serviceInfo=");
			buffer.append(serviceInfo.toString());
		}
		buffer.append(")");
		return buffer.toString();
	}

public void setDestination(Destination d)
	{
		this.m_destination = d;
	}

	public Destination getDestination()
	{
		return this.m_destination;
	}

	/**
	* This method is used to set priority status of appsession
	*/
	public void setPriorityStatus(boolean priority)	{
		if(logger.isDebugEnabled()){
			logger.info("Setting ApplicationSession status to :"+priority);
		}
		m_priorityStatus = priority;
	}

	/**
	* This method is used to return priority status of appsession
	*/
	public boolean getPriorityStatus()	{
		if(logger.isDebugEnabled()){
			logger.info("Returning ApplicationSession status as :"+m_priorityStatus);
		}
		return m_priorityStatus;
	}
	
	
	public static class AppSessionAttributeHolder implements Externalizable{
	  public  String id;
		
      public AppSessionAttributeHolder(){
      }

	   public void readExternal(ObjectInput in)
		   throws IOException, ClassNotFoundException {
		   this.id = (String)in.readObject();
	   }

	   public void writeExternal(ObjectOutput out) throws IOException {
         if (logger.isDebugEnabled()) {
             logger.debug("Inside SessionAttributeHolder writeExternal()");
         }
		   out.writeObject(this.id);
	   }
	}
	
	/**
     * Dispatches a SipApplicationSessionEvent to all registered 
     * SipApplicationSessionActivationListener to notify them of this 
     * SipApplicationSession's activation.
     */
    private void genActivationEvent() {
        if (logger.isDebugEnabled()) {
            logger.debug("Notifying all SipApplicationSessionActivationListener of " +
            		"the SipApplicationSession's activation.");
        }
        Iterator listeners = this.getContext().getListeners(
        		SipApplicationSessionActivationListener.class).iterator();
        
        if (null == listeners || !listeners.hasNext()) {
			if (logger.isDebugEnabled()) {
				logger.debug("SipApplicationSessionActivationListener not registered");
				logger.debug("Leaving AseApplicationSession.genActivationEvent");
			}
			return;
		}
        SipApplicationSessionEvent event = new SipApplicationSessionEvent(this);

        for ( ; listeners != null && listeners.hasNext(); ){
        	SipApplicationSessionActivationListener listener =
				(SipApplicationSessionActivationListener)listeners.next();
			try {
				listener.sessionDidActivate(event);
			} catch (Throwable th) {
				logger.error(th.getMessage(), th);
			}
		}
        if (logger.isDebugEnabled()) {
            logger.debug("Successfully notified all SipApplicationSessionActivationListener");
        }
        /*Iterator names = this.getAttributeNamesIterator();
        if (names != null) {        
            while (names.hasNext()) {
                Object obj = this.getAttribute((String)names.next());                        
                
                if (obj instanceof SipApplicationSessionActivationListener) {
                    if (event == null) {
                        event = new SipApplicationSessionEvent(this);
                    }
                    try { 
	                    ((SipApplicationSessionActivationListener)obj).sessionDidActivate(event);
	                    break;
					} catch (Throwable th) {
						logger.error(th.getMessage(), th);
					}
                }
            }
        }
        if (event == null) {
            if (logger.isDebugEnabled()) {
                logger.debug("No SipApplicationSessionActivationListener are currently " +
                		"registered with SipApplicationSession.");
            }
        } else if (logger.isDebugEnabled()) {
            logger.debug("Successfully notified all SipApplicationSessionActivationListener");
        }*/
    }
	 
    
    /**
     * Dispatches a SipApplicationSessionEvent to all registered 
     * SipApplicationSessionActivationListener to notify them of this 
     * SipApplicationSession's passivation.
     */
    private void genPassivationEvent() {
        if (logger.isDebugEnabled()) {
            logger.debug("Notifying all SipApplicationSessionActivationListener of" +
            		" the SipApplicationSession's passivation.");
        }
        Iterator listeners = this.getContext().getListeners(
        		SipApplicationSessionActivationListener.class).iterator();
        
        if (null == listeners || !listeners.hasNext()) {
			if (logger.isDebugEnabled()) {
				logger.debug("SipApplicationSessionActivationListener not registered");
				logger.debug("Leaving AseApplicationSession.genPassivationEvent");
			}
			return;
		}
        SipApplicationSessionEvent event = new SipApplicationSessionEvent(this);

        for ( ; listeners != null && listeners.hasNext(); ){
        	SipApplicationSessionActivationListener listener =
				(SipApplicationSessionActivationListener)listeners.next();
			try {
				listener.sessionWillPassivate(event);
			} catch (Throwable th) {
				logger.error(th.getMessage(), th);
			}
		}
        if (logger.isDebugEnabled()) {
            logger.debug("Successfully notified all SipApplicationSessionActivationListener");
        }
        
        
        
        
        
        /*SipApplicationSessionEvent event = null;
        Iterator names = this.getAttributeNamesIterator();
        if (names != null) {        
            while (names.hasNext()) {
                Object obj = this.getAttribute((String)names.next());                        
                if (obj instanceof SipApplicationSessionActivationListener) {
                    if (event == null) {
                        event = new SipApplicationSessionEvent(this);
                    }
                   	try { 
	                    ((SipApplicationSessionActivationListener)obj).sessionWillPassivate(event);
					} catch (Throwable th) {
						logger.error(th.getMessage(), th);
					}
                }
            }
        }
        if (event == null) {
            if (logger.isDebugEnabled()) {
                logger.debug("No SipApplicationSessionActivationListener are currently " +
                		"registered with SipApplicationSession.");
            }
        } else if (logger.isDebugEnabled()) {
            logger.debug("Successfully notified all SipApplicationSessionActivationListener");
        }*/
    }

    
	 /**
	  * Generate a SipApplicationSessionBindingEvent and invoke the attributeAdded
	  * callback on the SipApplicationSessionAttributeListeners.
	  * If no listener is registered then this method return false
	  */
	 private boolean genAttributeAddedEvent(String attributeName) {
		  if (logger.isDebugEnabled()) {
				logger.debug("Entering AseSipApplicationSession.genAttributeAddedEvent" +
								 id);
		  }
		  // Check if the SipApplicationSessionAttributeListener is registered
		  // If not then return false
		  // If there are no listeners registered then getListeners method
		  // will return null as opposed to an empty iterator
		  Iterator listeners = this.getContext().getListeners(SipApplicationSessionAttributeListener.class).iterator();
		  if (null == listeners || !listeners.hasNext()) {
				if (logger.isDebugEnabled()) {
					 logger.debug("SipApplicationSessionAttributeListeners not registered");
					 logger.debug("Leaving SipApplicationSessionAttributeListeners");
				}
				return false;
		  }
		  // Create a SipApplicationSessionBindingEvent and invoke listener's
		  // attributeAdded callback
		  SipApplicationSessionBindingEvent event =
				new SipApplicationSessionBindingEvent(this, attributeName);
		  
		  for ( ; listeners != null && listeners.hasNext(); ){
				SipApplicationSessionAttributeListener listener =
					 (SipApplicationSessionAttributeListener)listeners.next();
				try {
					listener.attributeAdded(event);
				} catch (Throwable th) {
					logger.error(th.getMessage(), th);
				}
		  }
		  if (logger.isDebugEnabled()) {
				logger.debug("Leaving AseSipSession.genAttributeAddedEvent" +
								 id);
		  }
		  return true;
	 }
	 
	 /**
	  * Generate a SipApplicationSessionBindingEvent and invoke the attributeRemoved
	  * callback on the SipApplicationSessionAttributeListeners.
	  * If no listener is registered this return false
	  */
	 private boolean genAttributeRemovedEvent(String attributeName) {
		  if (logger.isDebugEnabled()) {
				logger.debug("Entering AseApplicationSession.genAttributeRemovedEvent" +
								 id);
		  }
		  
		  // Check if the SipSessionAttributeListener is registered
		  // If not then return false
		  // If there are no listeners registered the getListeners method
		  // will return null as opposed to an empty iterator
		  Iterator listeners = this.getContext().getListeners(SipApplicationSessionAttributeListener.class).iterator();
		  if (null == listeners || !listeners.hasNext()) {
				if (logger.isDebugEnabled()) {
					 logger.debug("SipApplicationSessionAttributeListeners not registered");
					 logger.debug("Leaving AseApplicationSession.genAttributeRemovedEvent");
				}
				return false;
		  }
		  // Create a SipSessionBindingEvent and invoke listener's
		  // attributeRemoved callback
		  SipApplicationSessionBindingEvent event =
				new SipApplicationSessionBindingEvent(this, attributeName);
		  
		  for ( ; listeners != null && listeners.hasNext(); ){
			  SipApplicationSessionAttributeListener listener =
					 (SipApplicationSessionAttributeListener)listeners.next();
				try {
					listener.attributeRemoved(event);
				} catch (Throwable th) {
					logger.error(th.getMessage(), th);
				}
		  }
		  if (logger.isDebugEnabled()) {
				logger.debug("Leaving AseApplicationSession.genAttributeRemovedEvent" +id);
		  }
		  return true;
	 }

	 /**
	  * Generate a SipApplicationSessionBindingEvent and invoke the attributeRemoved
	  * callback on the SipApplicationSessionAttributeListeners.
	  * If no listener is registered this return false
	  */
	 private boolean genAttributeReplacedEvent(String attributeName) {
		  if (logger.isDebugEnabled()) {
				logger.debug("Entering AseApplicationSession.genAttributeReplacedEvent"+ id);
		  }
		  // Check if the SipSessionAttributeListener is registered
		  // If not then return false
		  // If there are no listeners registered the getListeners method
		  // will return null as opposed to an empty iterator
		  Iterator listeners = this.getContext().getListeners(SipApplicationSessionAttributeListener.class).iterator();
		  if (null == listeners || !listeners.hasNext()) {
				if (logger.isDebugEnabled()) {
					 logger.debug("SipApplicationSessionAttributeListeners not registered");
					 logger.debug("Leaving AseApplicationSession.genAttributeReplacedEvent");
				}
				return false;
		  }
		  // Create a SipSessionBindingEvent and invoke listener's
		  // attributeReplaced callback
		  SipApplicationSessionBindingEvent event =
				new SipApplicationSessionBindingEvent(this, attributeName);
		  
		  for ( ; listeners != null && listeners.hasNext(); ){
			  SipApplicationSessionAttributeListener listener =
					 (SipApplicationSessionAttributeListener)listeners.next();
				try {
					listener.attributeReplaced(event);
				} catch (Throwable th) {
					logger.error(th.getMessage(), th);
				}
		  }
		  if (logger.isDebugEnabled()) {
				logger.debug("Leaving AseApplicationSession.genAttributeReplacedEvent" +id);
		  }
		  return true;
	 }

	 /**
	  * Generate a SipApplicationSessionBindingEvent and invoke the valueBound
	  * callback on the SipApplicationSessionBindingListeners.
	  * If no listener is registered this return false
	  */
	 private boolean genValueBoundEvent(String attributeName, Object attributeObject) {
		if(logger.isDebugEnabled()) {
			logger.debug("Entering AseApplicationSession.genValueBoundEvent" + id);
		}
		
		if(attributeObject instanceof SipApplicationSessionBindingListener) {
			SipApplicationSessionBindingEvent event = new SipApplicationSessionBindingEvent(this, attributeName);
			((SipApplicationSessionBindingListener)attributeObject).valueBound(event);
		}

		if(logger.isDebugEnabled()) {
			logger.debug("Leaving AseApplicationSession.genValueBoundEvent" +id);
		}
		return true;
	 }

	 /**
	  * Generate a SipApplicationSessionBindingEvent and invoke the valueUnbound
	  * callback on the SipApplicationSessionBindingListeners.
	  * If no listener is registered this return false
	  */
	 private boolean genValueUnboundEvent(String attributeName, Object attributeObject) {
		if(logger.isDebugEnabled()) {
			logger.debug("Entering AseApplicationSession.genValueUnboundEvent" +id);
		}

		if(attributeObject instanceof SipApplicationSessionBindingListener) {
			SipApplicationSessionBindingEvent event = new SipApplicationSessionBindingEvent(this, attributeName);
			((SipApplicationSessionBindingListener)attributeObject).valueUnbound(event);
		}
		  
		if(logger.isDebugEnabled()) {
			logger.debug("Leaving AseSipSession.genValueUnboundEvent" +id);
		}
		return true;
	 }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AseApplicationSession other = (AseApplicationSession) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public CallStatistics getCallStatistics(){
		return callStatistics;
	}

	/*
	 * Checks the state of the call for calculating call stats before invalidating
	 */
	private void checkCallState(AseApplicationSession aseApplicationSession) {
		
		if(logger.isDebugEnabled()){
			logger.debug("AseApplicationSession : checkCallState");
		}
		CallStatistics callStatistics = aseApplicationSession.getCallStatistics();
		if(callStatistics != null){
			CallState callState = callStatistics.getCallState();
			if(callState == CallState.CONNECTED ||
					callState == CallState.IN_PROGRESS){
				CallStatsProcessor.getInstance().reportInProgressCall(false,aseApplicationSession);
				/* LEV-1535
				 * This line is commented because we are considering only those calls for calculating 
				 * callholdtime for which BYE is received. Call Hold Time for stuck calls will not be
				 * considered. 
				 */
				//CallStatsProcessor.getInstance().reportCallHoldTime(false,aseApplicationSession);
				callStatistics.setCallCompleted(true);
			}
		
			callStatistics.setCallState(CallState.END);
		}
	}
	
	@Override
	public boolean isFirstReplicationCompleted() {
		return mFirstReplicationCompleted;
	}

	@Override
	public void setFirstReplicationCompleted(boolean isFirstReplicationCompleted) {
		mFirstReplicationCompleted = isFirstReplicationCompleted;
	}
	
}
