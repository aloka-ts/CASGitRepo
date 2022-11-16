package com.baypackets.ase.jndi.ds;

import com.baypackets.ase.common.Registry;
import com.baypackets.ase.util.Constants;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import oracle.jdbc.pool.OracleConnectionCacheManager;
import oracle.simplefan.*;
import org.apache.log4j.Logger;

import javax.sql.DataSource;
import java.util.Properties;

public class OracleRacEventListener {

    private static final String DATASOURCE_NAME = "APPDB";
    private static Logger logger = Logger.getLogger(OracleRacEventListener.class);
    private static int serviceDown = 0;
    private DataSource dataSource = null;
    private ConfigRepository configRep;
    private String connectionRefreshType;
    ;
    private String serviceNameForAppDb;

    public OracleRacEventListener() {
        this.configRep = (ConfigRepository) Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
        connectionRefreshType = this.configRep.getValue(Constants.ORACLE_RAC_EVENT_CONNECTION_REFRESH_TYPE);
        serviceNameForAppDb = this.configRep.getValue(Constants.SERVICE_NAME_FOR_APPDB);
        logger.error("Inside  OracleRacEventListener cons connectionRefreshType : " + connectionRefreshType);
        logger.error("Inside  OracleRacEventListener cons serviceNameForAppDb : " + serviceNameForAppDb);
    }

    public void listenEvents() {

        logger.debug("Inside  OracleRacEventListener listenEvents");
        Properties p = new Properties();
        p.put("serviceName", serviceNameForAppDb);

        FanSubscription sub = FanManager.getInstance().subscribe(p);

        logger.error("Inside  OracleRacEventListener listenEvents..(subscribed)..");

        sub.addListener(new FanEventListener() {
            public void handleEvent(ServiceDownEvent downEvent) {

                logger.error("Inside OracleRacEventListener ServiceDownEvent ! " + downEvent.getServiceName() + "  :  " + downEvent.getReason());

                if (downEvent.getServiceName().trim().equals(serviceNameForAppDb)) {

                    try {

                        OracleConnectionCacheManager connMgr =
                                OracleConnectionCacheManager.getConnectionCacheManagerInstance();
                        String stringCache[] = connMgr.getCacheNameList();

                        if (stringCache != null) {
                            for (int i = 0; i < stringCache.length; i++) {
                                logger.error("Inside OracleRacEventListener Service : cache list : " + stringCache[i]);
                            }
                        }

                        logger.error("Inside OracleRacEventListener Service : calling refresh.. ");
                        if (Integer.parseInt(connectionRefreshType) == 1) {
                            logger.error("Inside OracleRacEventListener Service : calling refresh for ALL ");
                            connMgr.refreshCache("APPDB_CACHE", OracleConnectionCacheManager.REFRESH_ALL_CONNECTIONS);
                        } else {
                            logger.error("Inside OracleRacEventListener Service : calling refresh for INVALID ");
                            connMgr.refreshCache("APPDB_CACHE", OracleConnectionCacheManager.REFRESH_INVALID_CONNECTIONS);
                        }


                        logger.error("Inside OracleRacEventListener Service : refresh completed.. ");
                    } catch (Exception e) {
                        logger.error("SQLException occured OracleRacEventListener.handleEvent method." + e);
                    } finally {
                        // for future consideration
                        logger.error("Inside OracleRacEventListener Service inside finally.");

                    }
                }
            }

            public void handleEvent(NodeDownEvent nodeDown) {

                logger.error("Inside OracleRacEventListener NodeDownEvent ! " + nodeDown.getNodeName());

                try {

                    OracleConnectionCacheManager connMgr =
                            OracleConnectionCacheManager.getConnectionCacheManagerInstance();
                    String stringCache[] = connMgr.getCacheNameList();

                    if (stringCache != null) {
                        for (int i = 0; i < stringCache.length; i++) {
                            logger.error("Inside OracleRacEventListener NodeDownEvent : cache list : " + stringCache[i]);
                        }
                    }

                    logger.error("Inside OracleRacEventListener NodeDownEvent : calling refresh.. ");
                    if (Integer.parseInt(connectionRefreshType) == 1) {
                        logger.error("Inside OracleRacEventListener Service : calling refresh for ALL ");
                        connMgr.refreshCache("APPDB_CACHE", OracleConnectionCacheManager.REFRESH_ALL_CONNECTIONS);
                    } else {
                        logger.error("Inside OracleRacEventListener Service : calling refresh for INVALID ");
                        connMgr.refreshCache("APPDB_CACHE", OracleConnectionCacheManager.REFRESH_INVALID_CONNECTIONS);
                    }


                    logger.error("Inside OracleRacEventListener NodeDownEvent : refresh completed.. ");
                } catch (Exception e) {
                    logger.error("SQLException occured OracleRacEventListener.handleEvent-NodeDownEvent method." + e);
                } finally {
                    // for future consideration
                    logger.error("Inside OracleRacEventListener NodeDownEvent inside finally.");

                }

            }

            public void handleEvent(LoadAdvisoryEvent arg0) {
                logger.error("Inside OracleRacEventListener LoadAdvisoryEvent ! ");

            }
        });
    }
}
