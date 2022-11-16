package com.genband.jain.protocol.ss7.tcap;

import jain.protocol.ss7.IdNotAvailableException;
import jain.protocol.ss7.tcap.JainTcapListener;

import java.io.IOException;
import java.io.ObjectInput;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.servlet.sip.SipApplicationSession;

import org.apache.log4j.Logger;

import com.baypackets.ase.channel.AppInfo;
import com.baypackets.ase.spi.replication.Replicable;
import com.baypackets.ase.spi.replication.ReplicationEvent;
import com.baypackets.ase.spi.replication.SelectiveReplicationContext;
import com.baypackets.ase.util.AseObjectInputStream;
import com.genband.tcap.provider.TcapFactory;
import com.genband.tcap.provider.TcapSession;

public class TcapReplicationContext extends SelectiveReplicationContext implements TcapFactory {

	private transient ArrayList<AppInfo>	appNames	= new ArrayList<AppInfo>();
	private transient TcapSessionReplicator	parent		= null;
	private transient ClassLoader			loader;

	private transient int					counter		= 0;

	private static Logger					logger		= Logger
															.getLogger(TcapReplicationContext.class);
	
	private static final long serialVersionUID = 345184428432439439L;
	
	public TcapReplicationContext(String id, ClassLoader loader, TcapSessionReplicator parent) {
		super(id);
		this.loader = loader;
		this.parent = parent;
		
		//done as all tcap calls belong to same replication context;
		//if disable create is false full context is replicated at random interval
		//leading to duplicacy on stby sas
		super.setDisableCreate(false);//true for ss7 call issue for ocs
		
		//Added to enable hashing on replicable id instead of context id; 
		//done as in tcap calls context is shared;
		super.setUseRepId(true);
		
		AppInfo info = new AppInfo();
		info.setApplicationId(id);
		this.appNames.add(info);
		if (logger.isDebugEnabled()) {
			logger.debug("created TcapReplicationContext with id "+ id);
		}
	}

	public TcapSessionReplicator getParent() {
		return parent;
	}

	public Collection getAppInfo() {
		return appNames;
	}

	
	public void removeReplicable(String replicableId) {
		if(logger.isDebugEnabled()){
			logger.debug("removeReplicable(replicableid)::" + replicableId);
		}
		super.removeReplicable(replicableId);
	}
	
	
	@Override
	public void removeReplicable(String replicableId,boolean trackRemoval) {
		Replicable replicable = super.getReplicable(replicableId);

		if (!isActive()) {
			if(logger.isDebugEnabled()){
				logger.debug("removeReplicable id::"+replicableId);
			}
			
		}
		
		if(TcapSessionImpl.class.isInstance(replicable)){
			if(logger.isDebugEnabled()){
				logger.debug(replicableId+"::replicable is tcapssion" + replicableId);
			}
			this.getParent().removeTcapSession((TcapSessionImpl) replicable);
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("calling super removeReplicable for id::" + replicableId);
		}
		super.removeReplicable(replicableId,trackRemoval);
		
	}
	
	public void clear(){
		if(logger.isDebugEnabled()){
			logger.debug("clear");
		}
		super.clear();
	}
	
	public void setReplicable(Replicable replicable) {
		super.setReplicable(replicable,false);
		counter++;
		if (replicable instanceof TcapSessionImpl) {
			((TcapSessionImpl) replicable).setReplicationContext(this);
			parent.addTcapSession((TcapSessionImpl) replicable);
		}
		logger.debug("setReplicable isActive: " + isActive());
		if (!isActive()) {
			if (logger.isDebugEnabled()) {
				logger.debug("setReplicable calling partialActivate");
			}
			//will be called only in case of full replication
			replicable.partialActivate(this);
		}
	}

	public void readIncremental(ObjectInput in) throws IOException, ClassNotFoundException {
		if(logger.isDebugEnabled()){
			logger.debug("readIncremental in Tcapreplication Context");
		}
		if (in instanceof AseObjectInputStream) {
			((AseObjectInputStream) in).setClassLoader(loader);
		}
		super.readIncremental(in);
		if (in instanceof AseObjectInputStream) {
			((AseObjectInputStream) in).setClassLoader(null);
		}
	}

	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		if (in instanceof AseObjectInputStream) {
			((AseObjectInputStream) in).setClassLoader(loader);
		}
		super.readExternal(in);
		if (in instanceof AseObjectInputStream) {
			((AseObjectInputStream) in).setClassLoader(null);
		}
	}

	public void partialActivate() {
		//Overridden since partialActivate gets called during the setReplicable.
		if (logger.isDebugEnabled())
			logger.debug("partialActivate in tcapreplication context @@@ :"
							+ getAllTcapSessions().size());
		
		//no need to call partial activate in super as it will call 
		//iterations on all Tcapsession irrespective of replication happened or not
		//super.partialActivate();
		
		if (logger.isDebugEnabled()) {
			logger.debug("after calling partial activate of parent in  tcapreplication context @@@");
		}
		//after super partial activate
	}

	public void replicate(String replicableId, String eventId) {
		if (logger.isDebugEnabled()) {
			logger.debug("replicate @@@" + replicableId);
		}
		Replicable replicable = getReplicable(replicableId);
		if (replicable == null){
			
			if (logger.isDebugEnabled())
				logger.debug("replicable not found...returning "+ replicableId);
			return;
		}
		//saneja @ bug 10099[
		if (!(replicable.isReadyForReplication())) {
			if (logger.isDebugEnabled()) {
				logger
					.debug("Replicable(TcapSessionImpl not ready for replication. replicableId::["
									+ replicableId + "] replicable class::["
									+ replicable.getClass() + "]..so returning");
			}
			return;
		}
		//]closed saneja @bug 10099
		ReplicationEvent event = (eventId == null) ? null : new ReplicationEvent(this, eventId);

		if (logger.isDebugEnabled())
			logger.debug("calling SelectiveReplicationContext replicate @@@"+ replicableId);

		super.replicate(replicable, event);

	}

	//-------- TcapFactory Methods implementation ---------------
	@Override
	public TcapSession createTcapSession(JainTcapListener jtl,SipApplicationSession appSession) throws IdNotAvailableException {
		TcapSession ts = parent.createTcapSession(this);
		if(appSession!=null){
			ts.setAttribute(JainTcapProviderImpl.getImpl().APPLICATION_SESSION, appSession.getId());	
		}
			ts.setAttribute(JainTcapProviderImpl.getImpl().ListenerApp, jtl);
		return ts;
	}
	

	public Collection getAllTcapSessions() {
		return super.getAllReplicables();
	}

	public boolean hasApplication(String id) {
		if (logger.isDebugEnabled()) {
			logger.debug(id + "::hasApplication");
		}

		if (id == null) {
			return false;
		}

		Iterator<AppInfo> it = appNames != null ? appNames.iterator() : null;
		for (; it != null && it.hasNext();) {
			AppInfo tmp = (AppInfo) it.next();
			if (tmp.getApplicationId() != null && id.equals(tmp.getApplicationId())) {
				if (logger.isDebugEnabled()) {
					logger.debug(id + "::hasApplication matching app found");
				}
				return true;
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug(id + "::hasApplication NO matching app found AppList::"+appNames);
		}
		return false;
	}

	public String toString() {
		return this.printDebugInfo(null, false).toString();
	}

	public StringBuffer printDebugInfo(StringBuffer buffer, boolean all) {
		buffer = (buffer == null) ? new StringBuffer() : buffer;
		buffer.append("\nTcapReplicationContext [");
		buffer.append("id=");
		buffer.append(this.getId());
		buffer.append(", Active Count=");

		Collection temp = super.getAllReplicables();
		Iterator it = temp.iterator();
		buffer.append(temp.size());
		buffer.append(", Total Count=");
		buffer.append(counter);
		buffer.append("]");
		if (all) {
			buffer.append("\nActiveTcapSessions=(");
			for (int i = 0; it.hasNext(); i++) {
				buffer.append(i == 0 ? "" : ",");
				buffer.append(i != 0 && i % 50 == 0 ? "\n\t" : "");
				Replicable rep = (Replicable) it.next();
				buffer.append(rep.getReplicableId());
			}
			buffer.append(")");
		}
		return buffer;
	}
}
