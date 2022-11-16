package com.baypackets.ase.jndi.ds.odg;

import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;

import org.apache.log4j.Logger;

import com.baypackets.ase.jndi.ds.BindingReference;
import com.baypackets.ase.jndi.ds.DataSourceUtil;

/**
 * This class implements javax.naming.spi.ObjectFactory interface
 * It recreates the DataSourceImpl object on the basis Reference
 */

public class ODGDataSourceObjectFactory implements ObjectFactory {

    static Logger logger = Logger.getLogger(ODGDataSourceObjectFactory.class);
    private ODGDataSourceWrapper ods = null;

    public ODGDataSourceObjectFactory() {
        if (logger.isDebugEnabled()) {
            logger.debug("Inside constructor of ODGDataSourceObjectFactory");
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
                    logger.info("Create new object of ODGDataSourceWrapper");
                }
                if (object instanceof Reference) {
                    Reference reference = (Reference) object;
                    if (reference.getClassName().equals(BindingReference.class.getName())) {
                        RefAddr addr = reference.get("dataSourceName");
                        if (addr != null) {
                            //Get configuration properties for datasource name
                            List<Properties> propList = DataSourceUtil.getProperties(bindName);
                            if (logger.isInfoEnabled()) {
                                logger.info("after getting properties from map");
                            }
                            //configure connection pool properties
                            for (Properties prop : propList) {
                                ods = new ODGDataSourceWrapper(bindName);
                                if (logger.isInfoEnabled()) {
                                    logger.info("Created new object of ODGDataSourceWrapper: " + ods);
                                }
                                ODGDataSourceWrapper.initialize(prop,cacheName);
                                if (logger.isInfoEnabled()) {
                                    logger.info("After configuring ODGDataSourceWrapper");
                                }
                                DataSourceUtil.addDataSource(bindName, ods);
                            }

                            ods = (ODGDataSourceWrapper) DataSourceUtil.getDataSource(bindName);
                            if (logger.isInfoEnabled()) {
                                logger.info("getObjectInstance: After adding ODGDataSourceWrapper to map");
                            }
                        }
                    }
                } else {
                    logger.error("getObjectInstance: Return null ods");
                    return null;
                }
            } else {
                if (logger.isInfoEnabled()) {
                    logger.info("getObjectInstance:Found ODGDataSourceWrapper in DataSourceUtil Map");
                }
                ods = (ODGDataSourceWrapper) DataSourceUtil.getDataSource(bindName);
            }
            if (logger.isInfoEnabled()) {
                logger.info("getObjectInstance:before returning ods: " + ods);
            }
            return ods;
        } catch (Exception exp) {
            logger.error("Exception in getObjectInstance: " + exp);
            DataSourceUtil.raiseFailAlarm(bindName);
            throw exp;
        }
    }
}

