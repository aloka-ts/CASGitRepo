package com.baypackets.ase.replication;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.baypackets.ase.channel.ReplicationContext;
//import com.baypackets.ase.control.DataChannelSyncMessageListener;

public class ReplicationContextStoreManager  {//extends JGroupsChannelProvider 
	    private static Logger m_logger = Logger.getLogger(ReplicationContextStoreManager.class);
	    private ReplicationContextStore _dataChannelStore = new ReplicationContextStore();
	    
	    private ReplicationMessageFactory  messageFactory=null;
	    
	    
	   private  static ReplicationContextStoreManager storeManager=new ReplicationContextStoreManager();
	    
	    
	    public static ReplicationContextStoreManager getInstance(){
	    	return storeManager;
	    }

	    public void createReplicationContext(ReplicationContext ctx) {
	        _dataChannelStore.addReplicationContext(ctx);
	    }

	    public Iterator findClusterIds() {
	        return _dataChannelStore.findClusterIds();
	    }

	    public Iterator findReplicationContextByCluster(String clusterId) {
	        return _dataChannelStore.findReplicationContextsByCluster(clusterId);
	    }

	    public Iterator findReplicationContextIdsByCluster(String clusterId) {
	        return _dataChannelStore.findReplicationContextIdsByCluster(clusterId);
	    }

	    public ReplicationContext findReplicationContextById(String id) {
	        return _dataChannelStore.findReplicationContextById(id);
	    }

	    public ReplicationContext findReplicationContextByClusterAndId(String clusterId, String id) {
	        return _dataChannelStore.findReplicationContextByClusterAndId(clusterId, id);
	    }

	    public void removeReplicationContext(ReplicationContext ctxt) {
	        _dataChannelStore.removeReplicationContexts(ctxt);
	    }

	    public Map getReplicationContextMap() {
	        return _dataChannelStore.getReplicationContextMap();
	    }

//	    public void registerDataChannelSyncMsgListener(DataChannelSyncMessageListener listener) {
//	        if (m_logger.isInfoEnabled()) {
//	            m_logger.info("registerDataChannelSyncMsgListener called." + listener);
//	        }
//
//	    }
	    
	    public void registerAppDataMsgListener(AppDataMessageListener listener) {
	        if (m_logger.isInfoEnabled()) {
	            m_logger.info("registerAppDataMsgListener called." + listener);
	        }
	    }
	    
	   public List<AppDataMessageListener> getAppDataMsgListeners(){
		   return null;
	    	
	    }

	public void setMessageFactory(
			ReplicationMessageFactory replicationMessageFactory) {
		// TODO Auto-generated method stub
		
		messageFactory=replicationMessageFactory;
		
	}
	
	public ReplicationMessageFactory getMessageFactory(){
		return messageFactory;
	}

}
