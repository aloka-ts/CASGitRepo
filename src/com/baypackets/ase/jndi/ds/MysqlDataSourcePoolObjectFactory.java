package com.baypackets.ase.jndi.ds;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.log4j.Logger;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

/**
 * @author Purnima
 */

/**
 * This class implements javax.naming.spi.ObjectFactory interface It recreates
 * the DataSourceImpl object on the basis Reference for Mysql DB
 */

public class MysqlDataSourcePoolObjectFactory implements ObjectFactory {

    static Logger logger = Logger.getLogger(MysqlDataSourcePoolObjectFactory.class);
    private MySqlDataSourceWrapper mysqlPoolDs = null;

    public MysqlDataSourcePoolObjectFactory() {
        logger.debug("Inside constructor of MysqlDataSourcePoolObjectFactory");
    }

    /**
     * This method returns the object instance with same properties as
     * determined by the object ( first argument)
     *
     * @param object it is the same reference as passed by getReference method of
     *               DataSourceImpl class
     * @param name   the context name
     * @param ctx    The context to which the name is bound
     * @param env    the environment of the context
     * @return it returns the recreated object
     */

    public Object getObjectInstance(Object object, Name name, Context ctx,
                                    Hashtable env) throws Exception {

        logger.debug("getObjectInstance method has been called");
        String bindName = name.toString();
        logger.debug("Lookup Name is: " + bindName);

        try {
            if (DataSourceUtil.getDataSource(bindName) == null) {
                logger.info("Create new object of PooledMysqlDataSource");
                if (object instanceof Reference) {
                    Reference reference = (Reference) object;
                    if (reference.getClassName().equals(
                            BindingReference.class.getName())) {
                        RefAddr addr = reference.get("dataSourceName");
                        if (addr != null) {
                            // Get configuration properties for datasource name
                            List<Properties> propList = DataSourceUtil.getProperties(bindName);
                            logger.info("after getting properties from map");

                            mysqlPoolDs = new MySqlDataSourceWrapper();
                            for (Properties prop : propList) {
                                try {
                                    ComboPooledDataSource comboDs = mysqlPoolDs.configureDataSource(prop, true);
                                    mysqlPoolDs.setDataSource(comboDs);
                                    logger.info("After configuring MysqlDataSource");
                                    break;// Configure First data source by bind name
                                } catch (Exception e) {
                                    logger.error("Exception occured for configure datasource:" + bindName + ":" + e.getMessage(), e);
                                }
                            }
                            mysqlPoolDs.setPropList(propList);
                            logger.info("After configuring MysqlDataSource");
                            DataSourceUtil.addDataSource(bindName, mysqlPoolDs);
                            logger.info("getObjectInstance: After adding MysqlDataSource to map");
                        }
                    }
                } else {
                    logger.error("getObjectInstance: Return null ods");
                    return null;
                }
            } else {
                logger.info("getObjectInstance:Found Datasource in DataSourceUtil Map");
                mysqlPoolDs = (MySqlDataSourceWrapper) DataSourceUtil.getDataSource(bindName);
            }
            logger.info("getObjectInstance:before returning ods: " + mysqlPoolDs);
            return mysqlPoolDs;
        } catch (Exception exp) {
            logger.error("Exception in getObjectInstance: " + exp);
            throw exp;
        }
    }
}
