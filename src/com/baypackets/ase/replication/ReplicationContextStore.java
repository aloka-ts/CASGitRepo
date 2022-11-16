/*
 * Created on Nov 3, 2004
 *
 */
package com.baypackets.ase.replication;

import com.baypackets.ase.channel.ReplicationContext;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.util.Constants;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * This class provides an implementation of Cache for the
 * ReplicationContexts.
 *
 * @author Ravi
 */
public class ReplicationContextStore {

    private static Logger _logger = Logger.getLogger(ReplicationContextStore.class);
    private static final int CONCURRENCY_DIVISION_FACTOR = 4;

    int concurrencyLevel = 16;

    public ReplicationContextStore() {
        ConfigRepository repository = (ConfigRepository) Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
        String strCount = repository.getValue(Constants.PROP_MT_CONTAINER_THREAD_POOL_SIZE);
        int threadPoolSize = Integer.parseInt(strCount);
        if (threadPoolSize > 100) {
            concurrencyLevel = threadPoolSize / CONCURRENCY_DIVISION_FACTOR;
            if (_logger.isEnabledFor(Level.DEBUG)) {
                _logger.debug("Setting the concurrency level as :" + concurrencyLevel + " since container thread pool size is: " + threadPoolSize);
            }
        }
    }

    //Map containing the mapping of Clusters and the Contexts
    final private Map<String, ConcurrentHashMap<String, ReplicationContext>> clusterContexts = new ConcurrentHashMap<String, ConcurrentHashMap<String, ReplicationContext>>();

    public Iterator findClusterIds() {
        return clusterContexts.entrySet().iterator();
    }

    public void addReplicationContext(ReplicationContext ctx) {
        if (_logger.isEnabledFor(Level.INFO)) {
            _logger.info("Adding replication context:: " + ctx.getId() + " into cache");
        }
        String clusterId = ctx.getClusterId();
        ConcurrentHashMap<String, ReplicationContext> contextIdsMap = this.clusterContexts.get(clusterId);
        if (null == contextIdsMap) {
            synchronized (this) {
                //Similar to double checked locking idiom
                if(null == contextIdsMap) {
                    contextIdsMap = new ConcurrentHashMap<String, ReplicationContext>(1024, 0.75f, concurrencyLevel);
                    this.clusterContexts.put(clusterId, contextIdsMap);
                }
            }
        }
        if (_logger.isEnabledFor(Level.INFO)) {
            if (contextIdsMap != null) {
                _logger.info("cluster " + clusterId + " has " + contextIdsMap.size() + " replication contexts");
            }
        }
        contextIdsMap.put(ctx.getId(), ctx);
    }

    public void removeReplicationContexts(ReplicationContext ctx) {
        if (_logger.isEnabledFor(Level.INFO)) {
            _logger.info("removing replication context:: " + ctx);
        }
        ConcurrentHashMap<String, ReplicationContext> contextIdsMap = null;
        if (ctx != null) {
            contextIdsMap = this.clusterContexts.get(ctx.getClusterId());
            if (contextIdsMap != null) {
                if (_logger.isEnabledFor(Level.DEBUG)) {
                    _logger.debug("context map size before removing context <<" + contextIdsMap.size() + ">>");
                }
                if (contextIdsMap.remove(ctx.getId()) == null) {
                	 if (_logger.isEnabledFor(Level.DEBUG)) {
                    _logger.debug("removeReplicationContexts(): Context in cluster-id map not found");
                	 }
                }
                if (_logger.isEnabledFor(Level.DEBUG)) {
                    _logger.debug("context map size after removing context <<" + contextIdsMap.size() + ">>");
                }
            } else {
                _logger.error("removeReplicationContexts(): Map for cluster-id not found");
                return;
            }
        } else {
            _logger.error("ctx cannot be null!");
            return;
        }
    }

    public ReplicationContext findReplicationContextByClusterAndId(String clusterId, String id) {
        ConcurrentHashMap<String, ReplicationContext> contextIdsMap = clusterContexts.get(clusterId);
        if (null == contextIdsMap) {
            return null;
        }
        return contextIdsMap.get(id);
    }

    public ReplicationContext findReplicationContextById(String id) {
        //Loop on all the clusters to find this context id
        Iterator iterator = this.findClusterIds();
        while (iterator.hasNext()) {
            Map.Entry<String, ReplicationContext> e = (Map.Entry<String, ReplicationContext>) iterator.next();
            ConcurrentHashMap<String, ReplicationContext> val = (ConcurrentHashMap) e.getValue();
            ReplicationContext ctxt = val.get(id);
            if (ctxt != null) {
                return ctxt;
            }
        }
        return null;
    }

    public Iterator findReplicationContextsByCluster(String clusterId) {
        ConcurrentHashMap<String, ReplicationContext> contextIdsMap = this.clusterContexts.get(clusterId);
        if (_logger.isEnabledFor(Level.INFO)) {
            if (contextIdsMap != null) {
                _logger.info("cluster, " + clusterId + " had " + contextIdsMap.size() + " replication contexts");
            }
        }
        if (contextIdsMap == null) {
            return null;
        }
        return (new HashSet<ReplicationContext>(contextIdsMap.values())).iterator();
    }

    public Iterator findReplicationContextIdsByCluster(String clusterId) {
        ConcurrentHashMap<String, ReplicationContext> contextIdsMap = this.clusterContexts.get(clusterId);
        if (_logger.isEnabledFor(Level.INFO)) {
            if (null != contextIdsMap) {
                _logger.info("cluster, " + clusterId + " had " + contextIdsMap.size() + " replication context ids");
            }
        }
        if (contextIdsMap == null) {
            return null;
        }
        return (new HashSet<String>(contextIdsMap.keySet())).iterator();
    }

    public Map getReplicationContextMap() {
        return this.clusterContexts;
    }
}
