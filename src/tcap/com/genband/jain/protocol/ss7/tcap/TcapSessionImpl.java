package com.genband.jain.protocol.ss7.tcap;

import jain.protocol.ss7.IdNotAvailableException;
import jain.protocol.ss7.SccpUserAddress;
import jain.protocol.ss7.tcap.ComponentIndEvent;
import jain.protocol.ss7.tcap.ComponentReqEvent;
import jain.protocol.ss7.tcap.JainTcapListener;
import jain.protocol.ss7.tcap.SS7MessageInfo;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.ServletContext;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipServlet;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipSessionsUtil;

import org.apache.log4j.Logger;

import com.baypackets.ase.common.Registry;
import com.baypackets.ase.common.exceptions.AseLockException;
import com.baypackets.ase.measurement.AseMeasurementUtil;
import com.baypackets.ase.spi.container.SasApplicationSession;
import com.baypackets.ase.spi.replication.Replicable;
import com.baypackets.ase.spi.replication.ReplicationEvent;
import com.baypackets.ase.spi.replication.ReplicationSet;
import com.baypackets.ase.util.Constants;
import com.baypackets.bayprocessor.slee.common.BaseContext;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.genband.tcap.provider.TcapListener;
import com.genband.tcap.provider.TcapSession;
import com.baypackets.ase.ocm.OverloadControlManager;
import com.baypackets.ase.sipconnector.AseEvictingQueue;

public class TcapSessionImpl implements TcapSession, Replicable {

	private static Logger						logger		= Logger
																.getLogger(TcapSessionImpl.class);
	private static OverloadControlManager ocmManager = (OverloadControlManager)Registry.lookup(Constants.NAME_OC_MANAGER);
	private static int ocmId = ocmManager.getParameterId(OverloadControlManager.APP_SESSION_COUNT);
	private int									id;
	private int                                 outgoingDialogueId;
	private HashMap<String, Object>				attributes	= new HashMap<String, Object>();
	private static ConfigRepository				_configRepository;
	private static int							offset;
	private int									counter		= offset;
	private transient boolean					_new		= true;
	private transient boolean					_modified	= false;
	private transient boolean					_closed		= false;
	private transient boolean					isWriteExternal		= false;
	private transient TcapReplicationContext	ctxt		= null;
	transient private ReentrantLock icLock = new ReentrantLock();
	private int tcCorrId;
	private transient boolean activated = false;
	private String callId = null;
	
	private static String _CLOSED = "_CLOSED";

    private static short ss7SignalingInfoListSize=12;
	private static boolean isreplicationEnabled=true;
	
	private boolean mFirstReplicationCompleted = false;

	//Contains the Timestamp of latest message received or send.
	private long mesFirstTimeStamp;
	private long mesLastTimeStamp;
		
	private AseEvictingQueue<SS7MessageInfo> m_ss7SignalingInfoList=new AseEvictingQueue<SS7MessageInfo>(ss7SignalingInfoListSize);
	
	
	/**
	 * added serial version uid to support upgrade
	 */
	private static final long	serialVersionUID	= -1234567898765464L;

	static {
		_configRepository = (ConfigRepository) Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
		offset = Integer.valueOf(_configRepository.getValue(Constants.OFFSET_TCAP_NOTIFY_COUNTER));
      String replEnabled=BaseContext.getConfigRepository().getValue(Constants.PROP_REPLICATION_ENABLED);
		
		if("0".equals(replEnabled)){
			isreplicationEnabled=false;
		}
		//offset = Integer.valueOf(1);
	}

	public TcapSessionImpl() {
		if (logger.isDebugEnabled()) {
			logger.debug("::create TcapSessionImpl()");
		}
		//incremnt counters of activate calls on active for FT
		
	}

	TcapSessionImpl(int id) {
		if (logger.isDebugEnabled()) {
			logger.debug(id+"::create TcapSessionImpl(int id)");
		}
		this.id = id;
		this.activated= true;
		//incremnt counters of active calls
		AseMeasurementUtil.counterTcapActiveCalls.increment();
	}

	public int incrementCounter() {
		return counter++;
	}

	public TcapReplicationContext getReplicationContext() {
		return ctxt;
	}

	public void setReplicationContext(TcapReplicationContext ctxt) {
		this.ctxt = ctxt;
	}

	//=========== TcapSession Interface implementation ====
	public Object getAttribute(String name) {
		if (logger.isDebugEnabled()) {
			logger.debug(getDialogueId()+"::getAttribute(String name)::"+name);
		}
		return attributes.get(name);
		
	}

	public int getDialogueId() {
		return id;
	}
	
	protected int setDialogueId(int id) {
		return this.id=id;
	}
	
     public int getOutgoingDialogueId() {
		return outgoingDialogueId;
	}

	public void setOutgoingDialogueId(int outgoingDialogueId) {
		this.outgoingDialogueId = outgoingDialogueId;
	}

	public int getTcCorrelationId(){
    	 return tcCorrId;
     }
	
	public void setTcCorrelationId(int tcCorrelationId)
	{
		tcCorrId=tcCorrelationId;
	}

	public void setAttribute(String name, Object value) {
		if (logger.isDebugEnabled()) {
			logger.debug(getDialogueId()+"::setAttribute(name,value)::"+name+"  ::"+value);
		}
		attributes.put(name, value);
		_modified = true;
	}

	public Iterator<String> getAttributeNames() {
		return attributes.keySet().iterator();
	}

	public void removeAttributes() {
		if (logger.isDebugEnabled()) {
			logger.debug(getDialogueId()+"::removeAttributes()");
		}
		attributes.clear();
	}

	public Object removeAttribute(String name) {
		//saneja @ bug 10099[
		if (logger.isDebugEnabled()) {
			logger.debug(getDialogueId()+"::removeAttribute(name)::"+name);
		}
		Object removed = attributes.remove(name);
		if (removed != null) {
			_modified = true;
		}
		return removed;
		//] closed saneja @ bug 10099
	}

	public void invalidate() throws IdNotAvailableException {

		if (logger.isDebugEnabled()) {
			logger.debug(getDialogueId() + "::invalidate tcapsession " + id);
		}
		try {
			acquire();
			// Marking itself to be closed
			if (_closed) {
				logger.error("Invalidate() Already invalidated..." + id);
				return;
			}
			setClosed(true);

			if (this.ctxt == null) {
				logger.error("Invalidate() Not able to find the Replication Context..."
						+ id);
				return;
			}
			String appSessionId = (String) this
					.getAttribute(JainTcapProviderImpl.getImpl().APPLICATION_SESSION);

			this.ctxt.getParent().closeTcapSession(this);

			if (logger.isDebugEnabled()) {
				logger.debug("marking the appSession for dlgId to close");
			}
			// TcapProviderGateway tpg =
			// TcapProviderGateway.getTcapProviderGateway(req);
			SipApplicationSession applicationSession = getAppSession(appSessionId);

			if (applicationSession != null) {
				if (this.getAttribute(Constants.FOR_HANDOFF) != null){
					//UAT-1079: In case of handoff scenarios, active calls were counted as twice
					//and INAP call was not decremented when TC_END for handoff had been sent as
					//its tcap session and app session was not invalidated at that time.
					//ATF invalidates app session when INVITE comes into picture, but at that time
					//we are just marking app session's attribute to closed, but not invalidating it
					//so this change is to decrement the active calls by one for handoff as handoff
					//call is already ended. The attribute is set to the application session
					//so that when this app session will get decremented then 
					if (logger.isDebugEnabled()) {
						logger.debug("Handoff tcap session getting invalidated, thus decrementing " +
								"the acitve calls ");
					}
					AseMeasurementUtil.counterActiveAppSessions.decrement();
					ocmManager.decrease(ocmId);
					applicationSession.setAttribute(Constants.CALL_CNTR_DEC, "true");
				}else{
					applicationSession.setAttribute("CLOSED", "true");
					if (logger.isDebugEnabled()) {
						logger.debug("obj1 appsession: " + applicationSession);
					}
				}
			}

			// Always decrement tcap call counters on invalidate
			AseMeasurementUtil.counterTcapActiveCalls.decrement();
		} finally {
			release();
		}
		// release dialogue id form invocation MAP if part of same.
		JainTcapProviderImpl.getImpl().releaseDialogueId(id);

	}

	public void replicate() {
		if (logger.isDebugEnabled()) {
			logger.debug(getDialogueId()+"::TcapSessionImpl replicate called" + id);
		}
		if (this.ctxt == null) {
			logger.error(getDialogueId()+"::Replicate() Not able to find the Replication Context..." + id);
			return;
		}
		this.ctxt.replicate(this, null);
	}

	//============== Replicables Interface Implementation ========
		
	public void activate(){
		if (logger.isDebugEnabled()) {
			logger.debug(getDialogueId() + "::TcapSessionImpl activate() on use called " + id);
		}
		activate(null);
	}
	
	public void activate(ReplicationSet set) {
		if (logger.isDebugEnabled()) {
			logger.debug(getDialogueId() + "::TcapSessionImpl activate called " + id);
		}

		if (activated) {
			return;
		}
		try {
			acquire();
			if (activated ) {
				release();
				return;
			}
			
			String appSessionId = (String) this
					.getAttribute(JainTcapProviderImpl.getImpl().APPLICATION_SESSION);
			SipApplicationSession appSession = null;
			if (appSessionId != null) {
				if (logger.isDebugEnabled()) {
					logger.debug(getDialogueId() + "::sleep for sometime to amke sure appsession is active " + id);
				}
				Thread.currentThread().sleep(500); //let the appsession get activated if not yet activated
				appSession = getAppSession(appSessionId);
			}
			if(appSession == null){
				logger.error(id+"::Bulkreplication after cleanup; Appsesison is null for id::"+appSessionId);
				//FIXME: this is a workaround; hope to get better solution in replication
				cleanup();
				release();
				return;
			}
			if(_closed){
				logger.error(id+"::Ts is closed; Appsesison for id::"+appSessionId+" is::"+appSession);
				
				cleanup();
				
				// invalidate appsession if valid
				if (appSession != null && appSession.isValid()) {
					try {
						//using timeout to avoid possible deadlock
						((SasApplicationSession) appSession).setTimeout(1);
					} catch (Exception e) {
						logger.error("Activate ::Error invalidating AS for closed TS"+getDialogueId());
					}
				}
				
				
				release();
				return;
			}

			TcapAppRegistry appRegistry = TcapAppRegistry.getInstance();

			if (logger.isDebugEnabled()) {
				logger.debug(getDialogueId() + "::TcapSessionImpl appregistry"
						+ appRegistry);
			}

			// increment self seq by 100 in case of FT
			// This increment is done as for assist cases after media operation
			// when first inap message is sent Tcap session is not replicated;
			// to be safe for future flows incrementing by 100
			counter += 100;

			// incremnt counters of active calls
			AseMeasurementUtil.counterTcapActiveCalls.increment();

			JainTcapListener jtl = (JainTcapListener) getAttribute("ListenerApp");
			if (jtl == null) {
				if (logger.isDebugEnabled()) {
					logger.debug(getDialogueId()
							+ "::TcapSessionImpl jtl is null");
				}
				Object srvKey = getAttribute("ServiceKey");
				if (srvKey != null) {
					jtl = appRegistry.getListenerForSrvKey((String) srvKey,
							true);
				} else {
					Object appName = getAttribute("AppName");
					if (appName != null)
						jtl = appRegistry.getListenerForAppName(
								(String) appName, true);
				}

				// set listenerApp as it was null
				if (jtl != null) {
					if (logger.isDebugEnabled()) {
						logger.debug(getDialogueId()
								+ "::TcapsessionImpl Activate setting the  ListenerApp in tcapsession: "
								+ jtl);
					}
					setAttribute("ListenerApp", jtl);
				}
			}

			
			int protocol = JainTcapProviderImpl.getImpl().getProtocol();
			
			if (protocol == JainTcapProviderImpl.PROTOCOL_ITU) {
				Object srvKey = getAttribute("ServiceKey");
				if (srvKey != null) {
					jtl = appRegistry.getListenerForSrvKey((String) srvKey,
							true);
				} else {
					Object appName = getAttribute("AppName");
					if (appName != null) {
						jtl = appRegistry.getListenerForAppName(
								(String) appName, true);
					}
				}
			} else if (protocol == JainTcapProviderImpl.PROTOCOL_ANSI) {
				SccpUserAddress sua  = (SccpUserAddress) getAttribute(JainTcapProviderImpl.getImpl().SccpUserAddressAttr);
				if(sua!=null){
					jtl = JainTcapProviderImpl.getImpl().getListenerForSua(sua);
				}
			if (jtl != null) {
				// give call back to service for specific handling
				if (logger.isDebugEnabled()) {
					logger.debug(getDialogueId() + "::Invoke Listener");
				}
				// done to ensure app session invalidated properly after FT
				if (appSession != null && appSession.isValid()) {
					appSession.removeAttribute("PENDING_NOTIFY");
					
				}
				((TcapListener) jtl).processTcapSessionActivationEvent(this);
				
				//cleaning stale calls object
				if (appSession != null && appSession.isValid()) {
					if("true".equalsIgnoreCase( ( (String)(appSession.getAttribute("CLOSED"))) ) ){
						try {
							invalidate();
						} catch (IdNotAvailableException e) {
							if (logger.isDebugEnabled()) {
								logger.debug(
										getDialogueId()
												+ "::TcapSessionImpl error invaliding TcapSession",
										e);
							}
						}
						try{
							//using timeout to avoid possible deadlock
							((SasApplicationSession) appSession).setTimeout(1);
						}catch (Exception e) {
							if (logger.isDebugEnabled()) {
								logger.debug(getDialogueId()
												+ "::TcapSessionImpl error invaliding Appsession",
										e);
							}
						}//end try catch
					}//endi  f closed
				}//end as valid

			} else {
				logger.warn(getDialogueId() + "Listener not found after FT");
				// invalidate tcap session
				try {
					invalidate();
				} catch (IdNotAvailableException e) {
					if (logger.isDebugEnabled()) {
						logger.debug(
								getDialogueId()
										+ "::TcapSessionImpl error invaliding TcapSession",
								e);
					}
					cleanup();
				}

				// invalidate appsession if valid
				if (appSession != null && appSession.isValid()) {
					//using timeout to avoid possible deadlock
					((SasApplicationSession) appSession).setTimeout(1);
				}
			}
	         }
		} catch(Exception e){
			logger.error(getDialogueId()+"Got errro in activation of TSSession",e);
		}finally {
			activated = true;
			release();
		}

		if (logger.isDebugEnabled()) {
			logger.debug(getDialogueId() + "::Leave TcapSessionImpl activate");
		}
	}
	

	public String getReplicableId() {
		return "" + id;
	}

	public boolean isModified() {
		return _modified;
	}

	public boolean isNew() {
		return _new;
	}

	public boolean isReadyForReplication() {
		boolean b = _modified || _new;
		if (logger.isDebugEnabled()) {
			logger.debug("TcapSessionImpl isReadyForReplication called @@@" + b);
		}
		return b;
	}

	/**
	 * for INAP calls this will be called only on full replication
	 */
	public void partialActivate(ReplicationSet set) {
		if (logger.isDebugEnabled()) {
			logger.debug(getDialogueId() + "::TcapSessionImpl partialActivate called::" + id);
		}
		
		//this will be done through remove replicable wil be removed in next release.
		
	}
	
	
	public void readIncremental(ObjectInput in) throws IOException, ClassNotFoundException {
		
		if (logger.isDebugEnabled()) {
			logger.debug(getDialogueId()+"TcapSessionImpl readIncremental called" + id);
		}
		this.attributes = (HashMap<String, Object>) in.readObject();
		
		if (logger.isDebugEnabled()) {
			logger.debug(getDialogueId()+"TcapSessionImpl attrbutes read are" + this.attributes);
		}
		if(JainTcapProviderImpl.getImpl().isSS7MsgInfoEnabled()){
			this.callId = in.readUTF();
			this.mesFirstTimeStamp = in.readLong();
			this.mesLastTimeStamp  = in.readLong();
			this.m_ss7SignalingInfoList = (AseEvictingQueue<SS7MessageInfo>) in.readObject();
		}
		counter = in.readInt();
		//done to support live upgrade
		_closed = (attributes.get(_CLOSED)!=null);
	}

	public void replicationCompleted() {
		if (logger.isDebugEnabled()) {
			logger.debug("TcapSessionImpl replicationCompleted");
		}
		replicationCompleted(false);

		//] closed saneja @ bug 10099
	}
	
	public void replicationCompleted(boolean noReplication) {
		if (logger.isDebugEnabled()) {
			logger.debug(getDialogueId()+"::TcapSessionImpl replicationCompleted::"+noReplication);
		}
		boolean locked = false;
		if (!noReplication && isWriteExternal) {
			
			locked = tryLock();
			if (locked) {
				_new = false;
				// saneja @ bug 10099[
				_modified = true;
				release();
			}
		}

		//] closed saneja @ bug 10099
	}

	private void setClosed(boolean val) {
		_closed = val;
	}

	public boolean isClosed() {
		return _closed;
	}

	public void setReplicableId(String id) {}

	public void writeIncremental(ObjectOutput out, int replicationType) throws IOException {
		if (logger.isDebugEnabled()) {
			logger.debug(getDialogueId()+"TcapSessionImpl writeIncremental called" + id);
		}
		if(!isreplicationEnabled){
			return;
		}
		try{
			acquire();
			if(_closed){
				attributes.put(_CLOSED, _closed);
			}
			Object obj = attributes.get("ListenerApp");
			
			if (logger.isDebugEnabled()) {
				logger.debug(getDialogueId()+"TcapSessionImpl attrbutes  are" + this.attributes);
			}
			
			
			if (obj != null) {
				JainTcapListener jtl = (JainTcapListener) attributes.get("ListenerApp");
				attributes.remove("ListenerApp");
				out.writeObject(attributes);
				attributes.put("ListenerApp", jtl);
				
				if (logger.isDebugEnabled()) {
					logger.debug(getDialogueId()+"TcapSessionImpl write listener attrbutes  are" + jtl);
				}
			} else {
				out.writeObject(attributes);
			}
			if(JainTcapProviderImpl.getImpl().isSS7MsgInfoEnabled()){
				
				out.writeUTF(callId);
				out.writeLong(mesFirstTimeStamp);
				out.writeLong(mesLastTimeStamp);
				out.writeObject(m_ss7SignalingInfoList);
			}
			out.writeInt(counter);
			//done to support Live upgrade
			//out.writeBoolean(_closed);
		}finally{
			release();
		}
		if (logger.isDebugEnabled()) {
			logger.debug(getDialogueId()+"TcapSessionImpl writeIncremental leave");
		}
	}


	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		if (logger.isDebugEnabled()) {
			logger.debug("TcapSessionImpl readExternal called " +id);
		}
		if(!isreplicationEnabled){
			return;
		}
		id = in.readInt();
		readIncremental(in);
		
	}

	public void writeExternal(ObjectOutput out) throws IOException {
		if (logger.isDebugEnabled()) {
			logger.debug(getDialogueId()+"TcapSessionImpl writeExternal  "+ id);
		}
		if(!isreplicationEnabled){
			return;
		}
				try{
		                   
					if (logger.isDebugEnabled()){
						logger.debug("setFirstReplicationCompleted(true); ");
					}
					this.setFirstReplicationCompleted(true);
		 
					acquire();
					out.writeInt(id);
					writeIncremental(out, ReplicationEvent.TYPE_REGULAR);
					isWriteExternal = true;
				}finally{			
					release();
				}
			
		if (logger.isDebugEnabled()) {
			logger.debug(getDialogueId()+"TcapSessionImpl writeExternal leave");
		}
	}

	public SipApplicationSession getAppSession(String id) {
		if(logger.isDebugEnabled()){
			logger.debug("Inside get getAppSession for id::"+id);
		}
		SipSessionsUtil sipSessionsUtil = (SipSessionsUtil) ((SipServlet) JainTcapProviderImpl
			.getImpl()).getServletContext().getAttribute("javax.servlet.sip.SipSessionsUtil");
		return sipSessionsUtil.getApplicationSessionById(id);
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("TcapSession [id=");
		buffer.append(id);
		buffer.append(",closed =");
		buffer.append(_closed);
		buffer.append(",activated =");
		buffer.append(activated);
		if(JainTcapProviderImpl.getImpl().isSS7MsgInfoEnabled()){
			buffer.append(", Call Id =");
			buffer.append(callId);
			buffer.append(", First Timestamp =");
			buffer.append(new Date(mesFirstTimeStamp));
			buffer.append(", Last Timestamp =");
			buffer.append(new Date(mesLastTimeStamp));
			buffer.append(", Signaling Info = [");
			buffer.append(m_ss7SignalingInfoList.toString());
			buffer.append("]");
		}
		buffer.append(",attributes=");
		buffer.append(attributes);
		buffer.append("]");
		return buffer.toString();
	}

	//This API is needed to clean the Correlation Map in case of FT. This is introduced as 
	//in sessionDidActivate service doesn't get the Servlet Context as it was getting replicated
	//as part of App Session attribute
	public void cleanCorrMap(Integer correlationId) {
		JainTcapListener jtl = (JainTcapListener) this.getAttribute("ListenerApp");
		TcapAppRegistry appRegistry = TcapAppRegistry.getInstance();
		if (jtl == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("TcapSessionImpl jtl is null");
			}
			Object srvKey = this.getAttribute("ServiceKey");
			if (srvKey != null) {
				jtl = appRegistry.getListenerForSrvKey((String) srvKey,true);
			} else {
				Object appName = this.getAttribute("AppName");
				if (appName != null)
					jtl = appRegistry.getListenerForAppName((String) appName, true);
			}
		}
		ServletContext servletContext = null;
		if (jtl != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Setting the  ListenerApp in tcapsession: " + jtl);
			}
			this.setAttribute("ListenerApp", jtl);
			servletContext = TcapSessionReplicator.getApplicationContext(jtl.getClass()
				.getClassLoader());
			if (logger.isDebugEnabled()) {
				logger.debug("TCAP-Correlation replication-Found APpContext::[" + servletContext + "]");
			}
			Map<Integer, Object> corrMap = (Map) servletContext.getAttribute("Correlation-Map");
			corrMap.remove(correlationId);
			if (logger.isDebugEnabled()) {
				logger.debug("Correlation Map Cleaned: " + jtl);
			}
		} else {
			logger.error("Listener App is null");
		}
	}
	
	@Override
	public void acquire() {
		try {
			icLock.lockInterruptibly();
		} catch (InterruptedException e) {
			logger.error(getDialogueId()+"Error locking session on dialogID:"+e.getMessage());
			logger.warn(e);
		}finally{
			release();
		}
	}
	
	private boolean  tryLock() {
		boolean  locked = false;
		try {
			locked =icLock.tryLock(50, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			logger.error(getDialogueId()+"Error trying lock session on dialogID:"+e.getMessage());
			logger.warn(e);
		}
		return locked;
	}

	@Override
	public void release() {
		try {

			if (icLock.isHeldByCurrentThread()) {
				icLock.unlock();
			} else {
				if (logger.isInfoEnabled()) {
					logger.info("lock not held by current thread no need to unlock:");
				}
			}

		} catch (IllegalMonitorStateException e) {
			logger.error(getDialogueId()
					+ "Error Releasing lock session on dialogID:"
					+ e.getMessage());
			logger.warn(e);
		}
	}

	@Override
	public boolean isActive() {
		return activated;
	}
	
	@Override
	public boolean isFirstReplicationCompleted() {
		return mFirstReplicationCompleted;
	}

	@Override
	public void setFirstReplicationCompleted(boolean isFirstReplicationCompleted) {
		mFirstReplicationCompleted=isFirstReplicationCompleted;
	}
         
	@Override
	//not taking lock as it can cause deadlock during FT<AS+TS here; TS+AS in activate>
	public void cleanup() {
		setClosed(true);
		if(this.ctxt!=null){
			try {
				this.ctxt.removeReplicable(Integer.toString(id), false);
			} catch (Exception e) {
				logger.error(id+"::Error cleaning TS",e);
			}
		}
		
	}
	
	@Override
	public void updateIndEventPrintInfo(SipServletRequest req,
			List<ComponentIndEvent> list, int primitiveType) {
		
		if (logger.isDebugEnabled()) {
			logger.debug("updateIndEventPrintInfo called :");
		}
		
		if(req!=null && this.callId == null){
			this.callId  = req.getCallId();
		}
		
		if(this.mesFirstTimeStamp == 0){
			this.mesFirstTimeStamp = System.currentTimeMillis();
		}else{
			this.mesLastTimeStamp = System.currentTimeMillis();
		}
		
		SS7MessageInfo ss7MessageInfo = new SS7MessageInfo(list,primitiveType, Constants.DIRECTION_INCOMING);
		m_ss7SignalingInfoList.add(ss7MessageInfo);
	}

	@Override
	public void updateReqEventPrintInfo(SipServletRequest req,
			List<ComponentReqEvent> list,  int primitiveType) {
		
		if (logger.isDebugEnabled()) {
			logger.debug("updateReqEventPrintInfo called :");	
		}
		
		this.mesLastTimeStamp =  System.currentTimeMillis();
		SS7MessageInfo ss7MessageInfo = new SS7MessageInfo(list, primitiveType, Constants.DIRECTION_OUTGOING);
		m_ss7SignalingInfoList.add(ss7MessageInfo);
	}
}
