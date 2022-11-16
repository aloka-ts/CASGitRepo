/*
 * Created on Aug 6, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.baypackets.ase.container.sip;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.sip.ServletTimer;
import javax.servlet.sip.SipApplicationSession;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;
import org.apache.log4j.Logger;

import com.baypackets.ase.common.AseEvent;
import com.baypackets.ase.common.AseMessage;
import com.baypackets.ase.container.AseIc;
import com.baypackets.ase.container.AseThreadData;
import com.baypackets.ase.control.ClusterManager;
import com.baypackets.ase.spi.replication.Replicable;
import com.baypackets.ase.spi.replication.ReplicationEvent;
import com.baypackets.ase.spi.replication.ReplicationSet;
import com.baypackets.ase.util.AseTimerService;
import com.baypackets.ase.util.Constants;


/**
 * @author Ravi
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

@DefaultSerializer(ExternalizableSerializer.class)
public class ServletTimerImpl implements ServletTimer, Replicable {
	
	private static final long serialVersionUID = -3471149982478251L;
    private static Logger _logger = Logger.getLogger(ServletTimerImpl.class);
            
	transient private ServletTimerTask timerTask = new ServletTimerTask();
	
	transient private SipApplicationSessionImpl appSession = null;
	private Serializable info = null;
	
	private String id = null;
	private long delay = 0;
	private long period = 0;
	private boolean isFixedDelay = false;
	private boolean isPersistent = false;
	private long absoluteTime = 0;
	private long nextAbsoluteTime = 0;
	private short state = Constants.STATE_TIMER_SCHEDULED;
	
	private String replicableId = null;
	private boolean _modified = true;
	private boolean _new = true;
	private boolean mFirstReplicationCompleted = false;
	
	transient private AseMessage message = null;
	
	// This is required to use Externalizable interface
	public ServletTimerImpl() {
	}

	public ServletTimerImpl(SipApplicationSession appSession, long delay, boolean isPersistent, Serializable info){
		this(appSession,delay,0,false,isPersistent,info);
	}
	
	public ServletTimerImpl(SipApplicationSession appSession, 
							long delay, long period, boolean isFixedDelay, boolean isPersistent, Serializable info){
		this.appSession = (SipApplicationSessionImpl)appSession;
		this.info = info;
		this.delay = delay;
		this.period = period;
		this.isFixedDelay = isFixedDelay;
		this.isPersistent = isPersistent;
		absoluteTime = System.currentTimeMillis() + delay;
		nextAbsoluteTime = absoluteTime;
		
		//This object is being created here, so as to avoid the
		//overhead of this object creation at the timer thread... 
		AseEvent event = new AseEvent(this.appSession, Constants.EVENT_SIP_TIMER,  this);
		this.message = new AseMessage(event, this.appSession);
		
		//Register this with the application session
		this.appSession.addTimer(this);

		this.id = appSession.getId()+"."+replicableId;
		
		if(this.isPersistent) {
			AseIc ic = this.appSession.getIc();
                
			if(ic == null) {
				if (_logger.isInfoEnabled()) {

					_logger.info("No InvocationContext object is associated with this app session, so this must be a timer failover recovery");
				}
			return;
			}
			if (_logger.isInfoEnabled()) {

			_logger.info("Dispatching Servlet timer creation event to InvocationContext for replication purposes");
			}
			ic.handleReplicationEvent(new ReplicationEvent(this, ReplicationEvent.TIMER_CREATION));
		}
		//This is required as while getting ID of timer during SAS failover from the timer object 
		//stored as an attribute of the session, null pointer exception is observed because of 
		//following code 
		/**
		 * public String getId() {
		 *     return (appSession.getId()+"."+replicableId);
		 *  }
		 */
	}
	
	long getDelay() {
		return delay;
	}

	long getPeriod() {
		return period;
	}

	boolean getFixedDelay() {
		return isFixedDelay;
	}

	boolean getPersistent() {
		return isPersistent;
	}

	long getRecoveryDelay() {
		
		int deltaFailOverDetectionTime=ClusterManager.getDeltaFailoverDetectionTime();
    	
    	long deltaFailOverDetectionTimeStamp=ClusterManager.getDeltaFailoverDetectionTimeStamp();
    	
    	long activationDelta=deltaFailOverDetectionTimeStamp!=0?System.currentTimeMillis()-deltaFailOverDetectionTimeStamp:0;
    	
		long delta = (absoluteTime - System.currentTimeMillis());

		if(_logger.isDebugEnabled()){
			_logger.error("For timer activationDelta:"+activationDelta+" deltaFailOverDetectionTime:"+deltaFailOverDetectionTime+" delta:"+delta );
		}
		if(0 < getPeriod()) {
            delta = delta % getPeriod();
        	if(delta < 0) {
				delta += getPeriod();
        	}
        }   
        else {
        	
        	if(delta>0){
        		return delta;
        	}else{
        		delta=(delta*-1)-activationDelta;
        		if(delta<deltaFailOverDetectionTime){
        			delta=2000;  
        		}else{
        			delta=0;// Must have fired on active system
        		}
        	}
        }   

		return delta;
	}

	/**
	 * Returns the application session associated with this timer.
	 */
	public SipApplicationSession getApplicationSession() {
		return appSession;
	}

	/**
	 * returns the info object associated with this timer.
	 */
	public Serializable getInfo() {
		return this.info;
	}
	
	/**
	 * returns the scheduled execution time for this timer.
	 */
	public long scheduledExecutionTime() {
		return this.timerTask.scheduledExecutionTime();
	}

	/**
	 * Cancel this timer. No more timer expiry will happen.
	 * ReplicationEvent TIMER_CANCEL will be raised
	 */
	public void cancel(){
		if (_logger.isInfoEnabled()) {

		_logger.info("ServletTImerImpl Cancel timer is::["+this.toString()+"]");
		}
		AseIc ic = this.appSession.getIc();
		boolean changeLock = AseThreadData.setIcLock(ic);

		try {
			// Cancel Timer
			this.appSession.cancelTimer(this);

			if(this.isPersistent) {
				try {
					ic.handleReplicationEvent(
						new ReplicationEvent(this, ReplicationEvent.TIMER_CANCELLATION));
				} catch(Exception e) {
					_logger.error("Exception in cancel of ServletTimerImpl", e);
				}
			}
		} finally {
			AseThreadData.resetIcLock(ic, changeLock);
		}
	}
	
	void cancel0() {
		this.timerTask.cancel();
		if (_logger.isDebugEnabled()) {

		_logger.debug("In cancel0 method of ServletTimerImpl : Nulling references");
		}
		this.appSession = null;
		this.message = null;
		this.info = null;
		this.timerTask = null;
	}
	
	/**
	 * Returns whether or not this timer is cancelled.
	 * @return
	 */
	public boolean isCancelled(){
		return (state == Constants.STATE_TIMER_CANCELLED);
	}
	
	public boolean isExpired(){
		return (state == Constants.STATE_TIMER_EXPIRED);
	}
	
	/**
	 * Returns the message object to be queued for this servlet timer.
	 */
	public AseMessage getMessage(){
		return this.message;
	}
	
	/**
	 * The ServletTimerTask class. 
	 */
	class ServletTimerTask extends TimerTask{
		
		public void run(){
			AseIc ic = null;
			if(_logger.isDebugEnabled()){
				_logger.debug("Inside run() of ServletTimerTask Delay :: " + delay + "  AppSessionId : " + appSession.getId());
			}
			
			try {
				if(appSession==null){
					_logger.warn("Servlet timer task without timer0,so this must be a timer failover recovery");
					return;
				}
				ic = ServletTimerImpl.this.appSession.getIc();
                
				if(ic == null) {
					if (_logger.isInfoEnabled()) {

						_logger.info("No InvocationContext object is associated with this app session, so this must be a timer failover recovery");
					}
					return;
				}

				if(_logger.isDebugEnabled()){
					_logger.debug("Inside run() of ServletTimerTask. Acquiring IC Lock");
				}
				
				
				String appName=ServletTimerImpl.this.appSession.getApplicationName();
				
//				if (appName != null && appName.startsWith("tcap-provider")) {
//					if (_logger.isDebugEnabled()) {
//						_logger.debug("Inside run() of ServletTimerTask. Not Acquiring IC Lock for tcap-provider timer proceeding");
//					}
//				} else {
					if(_logger.isDebugEnabled()){
						_logger.debug("Inside run() of ServletTimerTask. Acquiring IC Lock");
					}
					ic.acquire();
			//	}
				
				if(_logger.isDebugEnabled()){
					_logger.debug("Inside run() of ServletTimerTask. IC Lock successfully acquired");
				}
				
				if(appSession==null){
					_logger.warn("Servlet timer task without timer1,so this must be a timer failover recovery");
					try {
						ic.release();
					} catch(Throwable thr) {
						_logger.error("Error in releasing IC lock", thr);
					}
					return;
				}
				
				//Set the state to expired in case of single shot timer.
				if(period == 0){
					_modified = true;
					state = Constants.STATE_TIMER_EXPIRED;
				} else {		
					if(isFixedDelay == true) {
						nextAbsoluteTime = System.currentTimeMillis() + period;
					} else {
						nextAbsoluteTime = nextAbsoluteTime + period;
					}
				}
            
        		//Call timeout on the appsession
				appSession.timeout(ServletTimerImpl.this);
			} catch(Throwable thr) {
				_logger.error("Error in ServletTimer processing", thr);
			} finally {
				try {
					if(ic!=null && ic.isAcquiredByCurrentThread()){
						ic.release();
					}
				} catch(Throwable thr) {
					_logger.error("Error in releasing IC lock", thr);
				}
			}
		}
		
		public boolean cancel() {
			if (_logger.isInfoEnabled()) {

			_logger.info("ServletTImerTask Cancel timer Task");
			}
			_modified = true;
			state = Constants.STATE_TIMER_CANCELLED;
			return super.cancel();
		}
	}

	/**
	 * Returns the timer task associated with this servlet timer.
	 * @return
	 */
	public ServletTimerTask getTimerTask() {
		return timerTask;
	}
	
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("ServletTimer [replicableId=");
		buffer.append(this.replicableId);
		buffer.append(",Id=");
		buffer.append(this.id);
		buffer.append(",appSessionId=");
		buffer.append(this.appSession == null ? "NULL" : this.appSession.getAppSessionId());
		buffer.append(",period=");
		buffer.append(this.period);
		buffer.append(",delay=");
		buffer.append(this.delay);
		buffer.append(",fixedDelay=");
		buffer.append(this.isFixedDelay);
		buffer.append(",persistent=");
		buffer.append(this.isPersistent);
		buffer.append(",state=");
		buffer.append(this.state);
		buffer.append("]");
		return buffer.toString();
	}

	////////Replicable Interface Implementation - Starts ////////////////////

	public void partialActivate(ReplicationSet parents) {
		// NOOP
		_logger.error("ServletTimerImpl partialActivate Invoked...["+this.toString()+"]");
	}	
	public void activate(ReplicationSet parent) {
		//Check whether the parent is the Sip Application Session OR not
		if(!(parent instanceof SipApplicationSessionImpl)){
			_logger.error("Servlet Timer Activation FAILED since the parent is not Application Session :" + this);
			return;
		}
		
		//Check whether the application Session is NOT NULL or Not.
		this.appSession = (SipApplicationSessionImpl)parent;
		if(this.appSession == null){
			_logger.error("ServletTimer Activation failed as the Application Session is NULL :" + this);
			return;
		}
		
		//Check the state of the Timer Object....
		if(this.state != Constants.STATE_TIMER_SCHEDULED){
			_logger.error("Not activating the Servlet Timer. Since the state is not scheduled :" + this);
			return;
		}
		
		long recoveryDelay  = this.getRecoveryDelay();
		if(recoveryDelay == 0 && this.period == 0){
			_logger.error("Not activating the Servlet Timer. Since this timer would have already fired :" + this);
			return;
		}
		
		if(_logger.isDebugEnabled()){
			_logger.debug("Activating Servlet Timer : " + this);
			_logger.debug("Expiry time from here is (millisecs) : " + recoveryDelay);
		}
		this.timerTask = new ServletTimerTask();
		Timer timer = AseTimerService.instance().getTimer(appSession.getId()); 
	
		if(this.period > 0) {
			if(this.isFixedDelay) {
				timer.scheduleAtFixedRate(this.timerTask, recoveryDelay, this.period);
			} else {
				timer.schedule(this.timerTask, recoveryDelay,this.period);
			}
		} else {
			timer.schedule(this.timerTask, recoveryDelay);
		}
		
		//This object is being created here, so as to avoid the
		//overhead of this object creation at the timer thread... 
		AseEvent event = new AseEvent(this.appSession, Constants.EVENT_SIP_TIMER,  this);
		this.message = new AseMessage(event, this.appSession);
		
		if (_logger.isDebugEnabled()) {
    		_logger.debug("ServletTimerImpl activate()Adding timer to appsession  timer id::["+
    				this.getId()+"]  replicable id::["+this.getReplicableId()+"] appSessionId::["+
    				this.appSession.getId()+"]");
		}

		this.appSession.addTimer(this);
	}

	public String getReplicableId() {
		return this.replicableId;
	}

	public void setReplicableId(String replicableId) {
		this.replicableId = replicableId;
	}
	
	public boolean isModified() {
		return this._modified;
	}

	public boolean isNew() {
		return this._new;
	}

	public boolean isReadyForReplication() {
		if(_logger.isDebugEnabled()){
			_logger.debug("ServletTimerImpl isReadyForReplication : [" + this.isPersistent+"]");
		}
		
		return this.isPersistent;
	}

	public void readIncremental(ObjectInput in) throws IOException, ClassNotFoundException {
		if(_logger.isDebugEnabled()){
			_logger.debug("ServletTimerImpl readIncremental");
		}
		this.state = in.readShort();
		this.nextAbsoluteTime = in.readLong();
	}

	public void replicationCompleted(boolean noReplication) {
		if(_logger.isDebugEnabled()){
			_logger.debug("ServletTimerImpl replicationCompleted"+noReplication);
		}
		if (!noReplication) {
		this._modified = false;
		this._new = false;
	}
	}
	
	public void replicationCompleted() {
		if(_logger.isDebugEnabled()){
			_logger.debug("ServletTimerImpl replicationCompleted");
		}
		replicationCompleted(false);
	}

	public void writeIncremental(ObjectOutput out, int replicationType) throws IOException {
		if(_logger.isDebugEnabled()){
			_logger.debug("ServletTimerImpl writeIncremental");
		}
		out.writeShort(this.state);
		out.writeLong(this.nextAbsoluteTime);
	}
	////////Replicable Interface Implementation - Starts ////////////////////	

	////////Externalizable Interface Implementation - Starts ////////////////////
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		if(_logger.isDebugEnabled()){
			_logger.debug("ServletTimerImpl readExternal");
		}
		this.replicableId = (String)in.readObject();
		this.delay = in.readLong();
		this.period = in.readLong();
		this.isFixedDelay = in.readBoolean();
		this.isPersistent = in.readBoolean();
		this.absoluteTime = in.readLong();
		this.nextAbsoluteTime = in.readLong();
		this.state = in.readShort();
		this.info = (Serializable)in.readObject();
		this.id = (String)in.readObject();
	}

	public void writeExternal(ObjectOutput out) throws IOException {
		if(_logger.isDebugEnabled()){
			_logger.debug("ServletTimerImpl writeExternal");
		}
		
		if (_logger.isDebugEnabled()){
			_logger.debug("setFirstReplicationCompleted(true); ");
		}
		this.setFirstReplicationCompleted(true);

		out.writeObject(this.replicableId);
		out.writeLong(this.delay);
		out.writeLong(this.period);
		out.writeBoolean(this.isFixedDelay);
		out.writeBoolean(this.isPersistent);
		out.writeLong(this.absoluteTime);
		out.writeLong(this.nextAbsoluteTime);
		out.writeShort(this.state);
		out.writeObject(this.info);
		out.writeObject(this.id);
		
	}

	public String getId() {
		//This is required as while getting ID of timer during SAS failover from the timer object 
		//stored as an attribute of the session, null pointer exception is observed because of 
		//following code 
		//return (appSession.getId()+"."+replicableId);
		return this.id;
	}
	
	@Override
	public boolean isFirstReplicationCompleted() {
		return mFirstReplicationCompleted;
	}

	@Override
	public void setFirstReplicationCompleted(boolean isFirstReplicationCompleted) {
		mFirstReplicationCompleted=isFirstReplicationCompleted;
	}

	public long getTimeRemaining() {
		long delta = (nextAbsoluteTime - System.currentTimeMillis());
		return delta;
	}

	////////Externalizable Interface Implementation - Ends ////////////////////
	
}
