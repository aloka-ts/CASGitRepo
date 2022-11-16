package com.baypackets.ase.jndi.ds;

import com.baypackets.ase.util.AESEncryption;
import oracle.jdbc.pool.OracleConnectionCacheManager;
import oracle.jdbc.pool.OracleDataSource;
import org.apache.log4j.Logger;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

/**
 * This class implements javax.naming.spi.ObjectFactory interface
 * It recreates the DataSourceImpl object on the basis Reference
 */

public class OracleDataSourceObjectFactory implements ObjectFactory {

    private static final int DEFAULT_CACHE_SIZE = 50;
    static Logger logger = Logger.getLogger(OracleDataSourceObjectFactory.class);
    private OracleDataSource ods = null;

    public OracleDataSourceObjectFactory() {
        if (logger.isDebugEnabled()) {
            logger.debug("Inside constructor of OracleDataSourceObjectFactory");
        }
    }

    /**
     * This methos returns the object instance with same properties as determined by the object ( first argument)
     *
     * @param object it is the same reference as passed by getReference method of DataSourceImpl class
     * @param name   the context name
     * @param ctx    The context to which the name is bound
     * @param env    the environment of the context
     * @return it returns the recreated object
     */

    public Object getObjectInstance(Object object, Name name, Context ctx, Hashtable env) throws Exception {

        if (logger.isDebugEnabled()) {
            logger.debug("getObjectInstance method has been called");
        }
        String bindName = name.toString();
        String cacheName = bindName + "_CACHE";
        if (logger.isDebugEnabled()) {
            logger.debug("Lookup Name is: " + bindName);
        }
        try {
            if (DataSourceUtil.getDataSource(bindName) == null) {
                if (logger.isInfoEnabled()) {
                    logger.info("Create new object of OracleDataSource");
                }
                if (object instanceof Reference) {
                    Reference reference = (Reference) object;
                    if (reference.getClassName().equals(BindingReference.class.getName())) {
                        RefAddr addr = reference.get("dataSourceName");
                        if (addr != null) {
                            //saneja@bug7812 [
                            //commented below line use wrapper instead to handle alarms
                            //ods = new OracleDataSource();

                            //]closed saneja@bug7812
                            if (logger.isInfoEnabled()) {
                                logger.info("Created new object of OracleDataSource: " + ods);
                            }
                            //Get configuration properties for datasource name
                            List<Properties> propList = DataSourceUtil.getProperties(bindName);
                            if (logger.isInfoEnabled()) {
                                logger.info("after getting properties from map");
                            }
                            //configure connection pool properties
                            String addRacListener = null;
                            for (Properties prop : propList) {
                                addRacListener = prop.getProperty("add-rac-listener");
                                ods = new OracleDataSourceWrapper(bindName);
                                configureDataSource(ods, prop);
                                if (logger.isInfoEnabled()) {
                                    logger.info("After configuring OracleDataSource");
                                }
                                //Create cache using Oracle ConnectionCacheManager
                                createCache(ods, cacheName, prop);
                                if (logger.isInfoEnabled()) {
                                    logger.info("getObjectInstance: After invoking createCache() method");
                                }
                                DataSourceUtil.addDataSource(bindName, ods);
                            }

                            if (bindName.equals("APPDB") && Boolean.valueOf(addRacListener)) {
                                if (logger.isDebugEnabled()) {
                                    logger.error("About to create OracleRacEventListener.." + ods);
                                }
                                OracleRacEventListener eventListener = new OracleRacEventListener();
                                eventListener.listenEvents();
                            }

                            ods = (OracleDataSource) DataSourceUtil.getDataSource(bindName);
                            if (logger.isInfoEnabled()) {
                                logger.info("getObjectInstance: After adding OracleDataSource to map");
                            }
                        }
                    }
                } else {
                    logger.error("getObjectInstance: Return null ods");
                    return null;
                }
            } else {
                if (logger.isInfoEnabled()) {
                    logger.info("getObjectInstance:Found Datasource in DataSourceUtil Map");
                }
                ods = (OracleDataSource) DataSourceUtil.getDataSource(bindName);
            }
            if (logger.isInfoEnabled()) {
                logger.info("getObjectInstance:before returning ods: " + ods);
            }
            return ods;
        } catch (Exception exp) {
            logger.error("Exception in getObjectInstance: " + exp);
            //saneja@bug7812 [
            DataSourceUtil.raiseFailAlarm(bindName);
            //]closed saneja@bug7812
            throw exp;
        }
    }

    private void configureDataSource(OracleDataSource ds, Properties dsInfo) throws Exception {
        String url = dsInfo.getProperty("url");
        String user = dsInfo.getProperty("username");
        String password = dsInfo.getProperty("password");
        String onsconfig = dsInfo.getProperty("onsconfig");
        String cachesize = dsInfo.getProperty("cachesize");
        boolean encryptionPolicy = Boolean.valueOf(dsInfo.getProperty("encryption-policy"));
        if (logger.isInfoEnabled()) {
            logger.info("configureDataSource:url=> " + url);
            logger.info("configureDataSource:user=> " + user);
            logger.info("configureDataSource:password=> " + password);
            logger.info("configureDataSource:onsconfig=> " + onsconfig);
            logger.info("configureDataSource:cachesize=> " + cachesize);
            logger.info("configureDataSource:encryption-policy=> " + encryptionPolicy);
        }
        ds.setURL(url);
        ds.setUser(user);
        if (encryptionPolicy && password != null) {
            logger.info("Encryption Policy On . Decrypting Password ");
            password = AESEncryption.decrypt(password);
        }
        ds.setPassword(password);
        /* Enable cahcing */
        ds.setConnectionCachingEnabled(true);
        //ds.setConnectionCacheName(CACHE_NAME);
		/* Set ONS configuration for FCF in case of RAC support */
        if (onsconfig != null) {
            if (logger.isInfoEnabled()) {
                logger.info("Set FCF and ONS Configuration=> " + onsconfig);
            }
            ds.setFastConnectionFailoverEnabled(true);
            ds.setONSConfiguration(onsconfig);
        }

        if (cachesize != null && !cachesize.isEmpty()) {
            int size = DEFAULT_CACHE_SIZE;
            try {
                size = Integer.parseInt(cachesize);
            } catch (Exception ex) {}

            if (size > 0) {
                //ds.setImplicitCachingEnabled(true);
                ds.setExplicitCachingEnabled(true);
            }

        }


    }

    private void createCache(OracleDataSource ds, String cacheName, Properties prop) throws Exception {


        Properties p = new Properties();
        String minLimit = (String) prop.get("minsize");
        if (logger.isDebugEnabled()) {
            logger.debug("Value of minLimit: " + minLimit);
        }
        String maxLimit = (String) prop.get("maxsize");
        if (logger.isDebugEnabled()) {
            logger.debug("Value of maxLimit: " + maxLimit);
        }
        String initialLimit = (String) prop.get("initialsize");
        if (logger.isDebugEnabled()) {
            logger.debug("Value of initialLimit: " + initialLimit);
        }
        p.setProperty("MinLimit", minLimit);
        p.setProperty("MaxLimit", maxLimit);
        p.setProperty("InitialLimit", initialLimit);

        //saneja@bug7812[
        //setting validate to true::
        p.setProperty("ValidateConnection", "true");
        //] closed saneja@bug7812

        String cachesize = (String) prop.getProperty("cachesize");
        if (cachesize != null && !cachesize.isEmpty()) {
            p.setProperty("MaxStatementsLimit", cachesize);
        } else {
            p.setProperty("MaxStatementsLimit", Integer.toString(DEFAULT_CACHE_SIZE));
        }

        try {
			/* Initialize the Connection Cache */
            OracleConnectionCacheManager connMgr =
                    OracleConnectionCacheManager.getConnectionCacheManagerInstance();
			
			/* Create the cache by passing the cache name, data source and the
			 * cache properties
			 */
            if (logger.isDebugEnabled()) {
                logger.debug("value of cacheName: " + cacheName);
                logger.debug("OracleDataSource URL: " + ds.getURL());
                logger.debug("OracleDataSource USER: " + ds.getUser());
                logger.debug("Cache Properties MinLimit: " + p.getProperty("MinLimit"));
                logger.debug("Cache Properties MaxLimit: " + p.getProperty("MaxLimit"));
                logger.debug("Cache Properties InitialLimit: " + p.getProperty("InitialLimit"));
                logger.debug("Before Invoking createCache on OracleConnectionCacheManager");
            }
            connMgr.createCache(cacheName, ds, p);
            if (logger.isDebugEnabled()) {
                logger.debug("After Invoking createCache on OracleConnectionCacheManager");
            }
        } catch (SQLException ex) {
            logger.error("SQL Exception in createCache: SQLException is: " + ex);
            throw new Exception("SQL Error while Instantiating Connection Cache: " + ex.toString());
        } catch (Throwable e) {
            logger.error("Exception in createCache: " + e);
            throw new Exception("Exception : " + e.toString());
        }

    }


}

